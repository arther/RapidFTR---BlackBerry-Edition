package com.rapidftr.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;

import com.rapidftr.datastore.ChildrenRecordStore;
import com.rapidftr.model.Child;
import com.rapidftr.net.ConnectionFactory;
import com.rapidftr.net.HttpGateway;
import com.rapidftr.net.RequestCallBack;
import com.rapidftr.utilities.HttpUtility;
import com.sun.me.web.request.Arg;
import com.sun.me.web.request.Request;
import com.sun.me.web.request.RequestListener;
import com.sun.me.web.request.Response;

public class ChildPhotoUpdateListener implements RequestListener {
	private static final String FILE_STORE_HOME_USER = "file:///store/home/user";
	private RequestCallBack requestCallBack;
	private int total;
	private boolean hasError;
	private ChildrenRecordStore childrenStore;

	public ChildPhotoUpdateListener(RequestCallBack requestCallBack, int total,
			boolean currentSyncStatus, ChildrenRecordStore childrenStore) {
		this.requestCallBack = requestCallBack;
		this.total = total;
		hasError = currentSyncStatus;
		this.childrenStore = childrenStore;
	}

	private boolean isValidResponse(Response response) {
		return (response.getException() == null)
				&& (response.getCode() == HttpConnection.HTTP_OK || response
						.getCode() == HttpConnection.HTTP_CREATED);
	}

	public String getPhotoPath(Arg[] headers){
		int noOfHeaders = headers.length;
		for(int index = 0 ; index < noOfHeaders ; index++){
			if((headers[index].getKey()).equals("Photo_id")){
				return headers[index].getValue();
			}
		}
		return null;
	}
	public void done(Object context, Response response) throws Exception {
		Child child = (Child) (((Hashtable) context)
				.get(ChildSyncService.CHILD_TO_SYNC));
		requestCallBack.updateProgressMessage(((Hashtable) context).get(
				ChildSyncService.PROCESS_STATE).toString());
		String imagePath = getPhotoPath(response.getHeaders());
		if (isValidResponse(response)) {
			try {
				byte[] data = response.getResult().getData();
				savePhoto(imagePath, data);
				if(imagePath.equals(child.getPhotoKey())){
				child.setPhotoKeyWithoutUpdate(getStorePath() + "/pictures/"
						+ child.getField("current_photo_key") + ".jpg");
				}
			} catch (Exception e) {
				e.printStackTrace();
				child.syncFailed(e.getMessage());
				hasError = true;
			}
		} else {
			hasError = true;
			child.syncFailed(response.getErrorMessage());
		}

		childrenStore.addOrUpdate(child);
		checkIfDone();
	}
	
	private void savePhoto(String imageLocation, byte[] data)
			throws IOException {
			imageLocation = getStorePath() + "/pictures/" + imageLocation + ".jpg";
		synchronized (Connector.class) {
			FileConnection fc = (FileConnection) Connector.open(imageLocation);
			if (!fc.exists()) {
				fc.create();
			}// create the file if it doesn't exist
			fc.setWritable(true);
			OutputStream outStream = fc.openOutputStream();
			outStream.write(data);
			outStream.close();
			fc.close();
		}
		
	}

	private synchronized void checkIfDone() {
		total--;
		if (total == 0) {
			if (hasError) {
				requestCallBack.onProcessFail("Errors have occurred");
			} else {
				requestCallBack.onProcessSuccess();
			}
		}
	}

	private String getStorePath() {
		String storePath = "";
		try {
			String sdCardPath = "file:///SDCard/Blackberry";
			FileConnection fc = (FileConnection) Connector.open(sdCardPath);
			if (fc.exists())
				storePath = sdCardPath;
			else
				storePath = FILE_STORE_HOME_USER;
		} catch (IOException ex) {
			storePath = FILE_STORE_HOME_USER;
		}
		return storePath;
	}

	public void readProgress(Object context, int bytes, int total) {
	}

	public void writeProgress(Object context, int bytes, int total) {
	}

}
