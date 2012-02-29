package com.rapidftr.services;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.ui.component.BitmapField;

import com.rapidftr.datastore.ChildrenRecordStore;
import com.rapidftr.model.Child;
import com.rapidftr.net.HttpService;
import com.rapidftr.net.RequestCallBack;
import com.rapidftr.utilities.HttpUtility;
import com.sun.me.web.request.Arg;

public class ChildPhotoUpdater {

	private HttpService service;
	private ChildrenRecordStore recordStore;

	public ChildPhotoUpdater(HttpService service,
			ChildrenRecordStore recordStore) {
		this.service = service;
		this.recordStore = recordStore;
	}

	// private String[] fetchImagesLocation(String imagesLocation) {
	// String fileNames[];
	// BitmapField[] mBmpFields;
	// imagesLocation = imagesLocation.replace('[', ' ');
	// imagesLocation = imagesLocation.replace(']', ' ');
	// imagesLocation = imagesLocation.replace('"', ' ');
	// imagesLocation = imagesLocation.trim();
	//
	// fileNames = split(imagesLocation, " , ");
	// for (int i = 0; i < fileNames.length; i++) {
	// fileNames[i] = fileNames[i].trim();
	// }
	// return fileNames;
	// }
	//
	// public static String[] split(String strString, String strDelimiter) {
	// String[] strArray;
	// int iOccurrences = 0;
	// int iIndexOfInnerString = 0;
	// int iIndexOfDelimiter = 0;
	// int iCounter = 0;
	//
	// // Check for null input strings.
	// if (strString == null) {
	// throw new IllegalArgumentException("Input string cannot be null.");
	// }
	// // Check for null or empty delimiter strings.
	// if (strDelimiter.length() <= 0 || strDelimiter == null) {
	// throw new IllegalArgumentException(
	// "Delimeter cannot be null or empty.");
	// }
	//
	// // strString must be in this format: (without {} )
	// // "{str[0]}{delimiter}str[1]}{delimiter} ...
	// // {str[n-1]}{delimiter}{str[n]}{delimiter}"
	//
	// // If strString begins with delimiter then remove it in order
	// // to comply with the desired format.
	//
	// if (strString.startsWith(strDelimiter)) {
	// strString = strString.substring(strDelimiter.length());
	// }
	//
	// // If strString does not end with the delimiter then add it
	// // to the string in order to comply with the desired format.
	// if (!strString.endsWith(strDelimiter)) {
	// strString += strDelimiter;
	// }
	//
	// // Count occurrences of the delimiter in the string.
	// // Occurrences should be the same amount of inner strings.
	// while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
	// iIndexOfInnerString)) != -1) {
	// iOccurrences += 1;
	// iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
	// }
	//
	// // Declare the array with the correct size.
	// strArray = new String[iOccurrences];
	//
	// // Reset the indices.
	// iIndexOfInnerString = 0;
	// iIndexOfDelimiter = 0;
	//
	// // Walk across the string again and this time add the
	// // strings to the array.
	// while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
	// iIndexOfInnerString)) != -1) {
	//
	// // Add string to array.
	// strArray[iCounter] = strString.substring(iIndexOfInnerString,
	// iIndexOfDelimiter);
	//
	// // Increment the index to the next character after
	// // the next delimiter.
	// iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
	//
	// // Inc the counter.
	// iCounter += 1;
	// }
	//
	// return strArray;
	// }

	public void doUpdates(Vector childrenRequiringPhotoUpdate,
			RequestCallBack requestCallBack, boolean currentSyncStatus) {
		int total = childrenRequiringPhotoUpdate.size();
		Enumeration items = childrenRequiringPhotoUpdate.elements();
		int index = 0;
		ChildPhotoUpdateListener listener = new ChildPhotoUpdateListener(
				requestCallBack, total, currentSyncStatus, recordStore);
		while (items.hasMoreElements()) {
			Child child = (Child) items.nextElement();
			index++;
			Hashtable context = new Hashtable();
			context.put(ChildSyncService.PROCESS_STATE, "Updating photo ["
					+ index + "/" + total + "]");
			context.put(ChildSyncService.CHILD_TO_SYNC, child);

			Arg[] httpArgs = new Arg[1];
			httpArgs[0] = HttpUtility.HEADER_CONTENT_TYPE_IMAGE;
			// String imageLocation = child.getField("photo_keys");
			String imagePath[] = child.getImageLocations();
			for (int i = 0; i < imagePath.length; i++) {
				context.put("imagePath", imagePath[i]);
				service.get("children/" + child.getField("_id") + "/photo/"
						+ imagePath[i], null, httpArgs, listener, context);
			}
		}
	}
}