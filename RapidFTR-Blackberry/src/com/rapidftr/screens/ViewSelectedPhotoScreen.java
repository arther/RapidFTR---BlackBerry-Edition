package com.rapidftr.screens;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;

import com.rapidftr.controls.ScrollableImageField;
import com.rapidftr.model.Child;
import com.rapidftr.screens.internal.CustomScreen;
import com.rapidftr.utilities.ImageHelper;

public class ViewSelectedPhotoScreen extends CustomScreen {
	private String imageLocation;
	private SeparatorField separatorField;
	private int headerHeight = 0;

	public void setImageLocation(String imageLocation) {
		this.imageLocation = imageLocation;
		clearFields();
		separatorField = new SeparatorField();
		add(separatorField);
	}

	protected void onUiEngineAttached(boolean attached) {
		super.onUiEngineAttached(attached);
		setHeaderHeight();
		add(new ScrollableImageField(new ImageHelper().getScaledImage(
				imageLocation, Display.getWidth(),
				Display.getHeight() - headerHeight), this.headerHeight));
	}

	private void setHeaderHeight() {
		int newHeaderHeight = separatorField.getTop()
				+ separatorField.getHeight();
		if (headerHeight != newHeaderHeight)
			headerHeight = newHeaderHeight;
	}
}
