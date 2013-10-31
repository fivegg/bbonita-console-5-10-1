/**
 * Copyright (C) 2009 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.console.client.view;

import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.BonitaMessage;
import org.bonitasoft.console.client.model.BonitaMessage.BonitaMessageSeverity;
import org.bonitasoft.console.client.model.MessageDataSource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class MessageWidget extends BonitaPanel implements ModelChangeListener {

	private MessageDataSource myMessageDataSource;

	private DecoratorPanel myOuterPanel = new DecoratorPanel();
	private Label myMessage = new Label();
	private Label myCloseAction = new Label("X");

	private FlowPanel myMessagePanel = new FlowPanel();

	private Timer myTimer;

	/**
	 * Default constructor.
	 * 
	 * @param aDataSource
	 */
	public MessageWidget(MessageDataSource aDataSource) {
		super();
		myMessageDataSource = aDataSource;
		myMessageDataSource.addModelChangeListener(MessageDataSource.MESSAGE_PROPERTY, this);

		myOuterPanel.setStylePrimaryName("message_widget");
		myMessage.setStylePrimaryName("message_widget_content");
		myCloseAction.setStylePrimaryName("message_widget_action");
		myCloseAction.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent aEvent) {
				hideMessage();

			}

		});

		myMessagePanel.add(myMessage);
		myMessagePanel.add(myCloseAction);
		myOuterPanel.add(myMessagePanel);
		initWidget(myOuterPanel);
		hideMessage();
		myTimer = new Timer() {
			@Override
			public void run() {
				hideMessage();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.ModelChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void modelChange(ModelChangeEvent anEvent) {
		if (MessageDataSource.MESSAGE_PROPERTY.equals(anEvent.getPropertyName())) {
			BonitaMessage theMessageToDisplay = (BonitaMessage) anEvent.getNewValue();
			update(theMessageToDisplay);
		}

	}

	/*
	 * Update the UI.
	 */
	private void update(BonitaMessage aMessageToDisplay) {
		// Cancel previous timer.
		myTimer.cancel();
		// Update the text displayed.
		myMessage.setText(aMessageToDisplay.getMessage());
		// Remove previously set style name.
		myOuterPanel.removeStyleDependentName(BonitaMessageSeverity.info.name());
		myOuterPanel.removeStyleDependentName(BonitaMessageSeverity.warn.name());
		myOuterPanel.removeStyleDependentName(BonitaMessageSeverity.error.name());
		// Add the current style name to use.
		myOuterPanel.addStyleDependentName(aMessageToDisplay.getSeverity().name());
		// Make the message visible.
		myOuterPanel.setVisible(true);

		// Schedule a timer to automatically hide the message.
		myTimer.schedule(constants.messageDisplayTime());
	}

	/**
	 * Hide the message.
	 */
	private void hideMessage() {
		myOuterPanel.setVisible(false);
	}
}
