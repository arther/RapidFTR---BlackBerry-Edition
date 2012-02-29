package com.rapidftr.controllers;

import com.rapidftr.controllers.internal.Controller;
import com.rapidftr.controllers.internal.Dispatcher;
import com.rapidftr.model.Child;
import com.rapidftr.screens.ViewChildPhotoGalleryScreen;
import com.rapidftr.screens.internal.CustomScreen;
import com.rapidftr.screens.internal.UiStack;

public class ViewChildPhotoGalleryController extends Controller{

	public ViewChildPhotoGalleryController(CustomScreen screen,
			UiStack uiStack, Dispatcher dispatcher) {
		super(screen, uiStack, dispatcher);
	}

	public void viewChildPhotoGallery(Child child){
		getChildPhotoGalleryScreen().setChild(child);
        show();
	}

	private ViewChildPhotoGalleryScreen getChildPhotoGalleryScreen() {
		return (ViewChildPhotoGalleryScreen) currentScreen;
	}

	public void viewSelectedPhoto(String imageLocation) {
		dispatcher.viewSelectedPhoto(imageLocation);
	}
}
