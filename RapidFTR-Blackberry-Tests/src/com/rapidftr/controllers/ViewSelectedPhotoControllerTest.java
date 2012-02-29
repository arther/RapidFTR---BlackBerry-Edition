package com.rapidftr.controllers;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rapidftr.controllers.internal.Dispatcher;
import com.rapidftr.screens.ViewChildPhotoGalleryScreen;
import com.rapidftr.screens.internal.UiStack;

public class ViewSelectedPhotoControllerTest {

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

}
