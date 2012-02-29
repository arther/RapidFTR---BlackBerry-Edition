package com.rapidftr.gallery;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.component.BitmapField;

import com.rapidftr.controls.ScrollableImageField;
import com.rapidftr.screens.internal.CustomScreen;
import com.rapidftr.utilities.ImageHelper;

public class PhotoGallery extends CustomScreen {


	

	public PhotoGallery() {
		// super(VERTICAL_SCROLL | VERTICAL_SCROLLBAR);
		// prepareBmpFields();
	}

	public void prepareBmpFields() {
		BitmapField[] mBmpFields;
		int mImgWidth = 80;
		int mImgHeight = 80;
		int mImgMargin = 10;
		String fileNames[] = { "default.jpg", "default.jpg", "default.jpg",
				"default.jpg" };
		mBmpFields = new BitmapField[fileNames.length];
		for (int i = 0; i < fileNames.length; i++) {
			EncodedImage image = EncodedImage.getEncodedImageResource("res/"
					+ fileNames[i]);
			image = sizeImage(image, mImgWidth, mImgHeight);
			mBmpFields[i] = new BitmapField(image.getBitmap(),
					BitmapField.FOCUSABLE);
			mBmpFields[i].setMargin(mImgMargin, mImgMargin, mImgMargin,
					mImgMargin);
			add(mBmpFields[i]);
		}
	}

	public EncodedImage sizeImage(EncodedImage image, int width, int height) {
		EncodedImage result = null;

		int currentWidthFixed32 = Fixed32.toFP(image.getWidth());
		int currentHeightFixed32 = Fixed32.toFP(image.getHeight());

		int requiredWidthFixed32 = Fixed32.toFP(width);
		int requiredHeightFixed32 = Fixed32.toFP(height);

		int scaleXFixed32 = Fixed32.div(currentWidthFixed32,
				requiredWidthFixed32);
		int scaleYFixed32 = Fixed32.div(currentHeightFixed32,
				requiredHeightFixed32);

		result = image.scaleImage32(scaleXFixed32, scaleYFixed32);
		return result;
	}

	protected void onUiEngineAttached(boolean attached) {
		super.onUiEngineAttached(attached);
		// setHeaderHeight();
		prepareBmpFields();
	}
}
