package com.rapidftr.controllers;

import com.rapidftr.controllers.internal.Controller;
import com.rapidftr.controllers.internal.Dispatcher;
import com.rapidftr.model.Child;
import com.rapidftr.screens.ChildPhotoScreen;
import com.rapidftr.screens.ViewSelectedPhotoScreen;
import com.rapidftr.screens.internal.CustomScreen;
import com.rapidftr.screens.internal.UiStack;

public class ViewSelectedPhotoController extends Controller {
	public ViewSelectedPhotoController(CustomScreen screen, UiStack uiStack,
			Dispatcher dispatcher) {
		super(screen, uiStack, dispatcher);
	}

	public void viewSelectedPhoto(String imageLocation) {
		getSelectedPhotoScreen().setImageLocation(imageLocation);
		show();
	}

	private ViewSelectedPhotoScreen getSelectedPhotoScreen() {
		return (ViewSelectedPhotoScreen) currentScreen;
	}
}
