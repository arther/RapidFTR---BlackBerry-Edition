package com.rapidftr.screens;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

import com.rapidftr.controllers.ViewChildPhotoGalleryController;
import com.rapidftr.model.Child;
import com.rapidftr.screens.internal.CustomScreen;
import com.rapidftr.utilities.ImageHelper;

public class ViewChildPhotoGalleryScreen extends CustomScreen {

	private LabelField childName;
	private Child curChild;
	private int headerHeight = 0;
	private SeparatorField separatorField;
	private static final String FILE_STORE_HOME_USER = "file:///store/home/user";

	BitmapField[] imageFields;
	int imageWidth = 80;
	int imageHeight = 80;
	int imageMargin = 10;
	String focusedImageName;

	private String[] imageLocations;

	public void setChild(Child child) {
		this.curChild = child;
		clearFields();
		childName = new LabelField(child.getField("name"));
		add(childName);
		separatorField = new SeparatorField();
		add(separatorField);
	}

	protected void onUiEngineAttached(boolean attached) {
		super.onUiEngineAttached(attached);
		setHeaderHeight();
		prepareImageFields();
	}

	private void setHeaderHeight() {
		int newHeaderHeight = separatorField.getTop()
				+ separatorField.getHeight();
		if (headerHeight != newHeaderHeight)
			headerHeight = newHeaderHeight;
	}

	private void setImageLocations() {

		try {
			imageLocations = curChild.getImageLocations();
			if (imageLocations == null) {
				imageLocations = new String[1];
				imageLocations[0] = curChild.getPrimaryImageName();
			}
		} catch (Exception ex) {
		}
	}

	private void prepareImageFields() {
		setImageLocations();
		if (imageLocations != null) {
			imageFields = new BitmapField[imageLocations.length];
			for (int i = 0; i < imageLocations.length; i++) {
				imageFields[i] = getBitmapField(getStorePath() + "/pictures/"
						+ imageLocations[i] + ".jpg");
				imageFields[i].setMargin(imageMargin, imageMargin, imageMargin,
						imageMargin);
				setFocusListenerToImage(imageFields[i]);
				add(imageFields[i]);
			}
		}else{
			Dialog.alert("Record does not have an image");
		}
	}

	private BitmapField getBitmapField(String imageLocation) {
		return new BitmapField(new ImageHelper().getImage(imageLocation),
				BitmapField.FOCUSABLE) {
			protected boolean navigationClick(int status, int time) {
				((ViewChildPhotoGalleryController) controller)
						.viewSelectedPhoto(getStorePath() + "/pictures/"
								+ imageLocations[getFocusedImageIndex()]
								+ ".jpg");
				return true;
			}
		};
	}

	private void setFocusListenerToImage(BitmapField bitmapField) {
		bitmapField.setFocusListener(new FocusChangeListener() {
			public void focusChanged(Field field, int eventType) {
				if (eventType == FOCUS_GAINED) {
					Border blueBorder = BorderFactory.createSimpleBorder(
							new XYEdges(2, 2, 2, 2), new XYEdges(Color.BLUE,
									Color.BLUE, Color.BLUE, Color.BLUE),
							Border.STYLE_SOLID);
					field.setBorder(blueBorder);
				} else if (eventType == FOCUS_LOST) {
					field.setBorder(null);
				}
			}
		});

	}

	private int getFocusedImageIndex() {
		if (imageFields == null)
			return -1;
		for (int i = 0; i < imageFields.length; i++) {
			if (imageFields[i].isFocus()) {
				return i;
			}
		}
		return -1;
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
}
