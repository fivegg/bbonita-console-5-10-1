package org.bonitasoft.console.client.view;

import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.i18n.ConsoleMessages;
import org.bonitasoft.console.client.i18n.ConsolePatterns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.PopupPanel;

public abstract class I18NPopupPanel extends PopupPanel {

	protected static final ConsoleConstants constants = (ConsoleConstants) GWT.create(ConsoleConstants.class);
	protected static final ConsoleMessages messages = (ConsoleMessages) GWT.create(ConsoleMessages.class);
	protected static final ConsolePatterns patterns = (ConsolePatterns) GWT.create(ConsolePatterns.class);
	protected static final String PICTURE_PLACE_HOLDER = ConsoleConstants.PICTURE_PLACEHOLDER;

	public I18NPopupPanel() {
		super();
	}

}