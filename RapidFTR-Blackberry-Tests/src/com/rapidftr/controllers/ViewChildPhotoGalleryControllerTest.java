package com.rapidftr.controllers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rapidftr.controllers.internal.Dispatcher;
import com.rapidftr.model.Child;
import com.rapidftr.model.ChildFactory;
import com.rapidftr.screens.ViewChildPhotoGalleryScreen;
import com.rapidftr.screens.ViewChildScreen;
import com.rapidftr.screens.internal.UiStack;

public class ViewChildPhotoGalleryControllerTest {

	@Mock
	private ViewChildPhotoGalleryScreen screen;
	@Mock
	private UiStack uiStack;
	@Mock
	private Dispatcher dispatcher;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldInvokeChildPhotoGalleryScreen() {
		ViewChildPhotoGalleryController controller = new ViewChildPhotoGalleryController(
				screen, uiStack, dispatcher);
		Child child = ChildFactory.newChild();
		controller.viewChildPhotoGallery(child);
	}
	
	@Test
	public void shouldGetChildPhotoGalleryScreen(){
		ViewChildPhotoGalleryController controller = new ViewChildPhotoGalleryController(
				screen, uiStack, dispatcher);
	//	assertNotNull(controller.getChildPhotoGalleryScreen());
	}

}
