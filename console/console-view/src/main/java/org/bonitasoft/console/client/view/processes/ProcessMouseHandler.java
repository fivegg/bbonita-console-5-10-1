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
package org.bonitasoft.console.client.view.processes;

import org.bonitasoft.console.client.processes.BonitaProcess;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * @revision Anthony Birembaut updated the tooltip to display the label, version and description
 *
 */
public class ProcessMouseHandler implements MouseOverHandler, MouseOutHandler {


		private final BonitaProcess myProcessDefinition;
		private final PopupPanel myPopup = new PopupPanel(true);

		/**
		 * Default constructor.
		 * 
		 * @param aProcessDefinition
		 * @param aHeader 
		 */
		public ProcessMouseHandler(BonitaProcess aProcessDefinition, Widget aHeader) {
			myProcessDefinition = aProcessDefinition;
			// Create the tooltip
			FlexTable theOuterPanel = new FlexTable();
			theOuterPanel.setWidget(0, 0, aHeader);
			Label theHearderLabel = new Label(myProcessDefinition.getDisplayName() + " - " + myProcessDefinition.getVersion());
			
			//Label theDescriptionLabel = new Label(theSB.toString());
			Label theDescriptionLabel = new Label(myProcessDefinition.getProcessDescription());
			theDescriptionLabel.setStyleName("process_description");
			// layout widgets
			theOuterPanel.setWidget(0, 1, theHearderLabel);
			theOuterPanel.setWidget(1, 1, theDescriptionLabel);
			myPopup.add(theOuterPanel);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google
		 * .gwt.event.dom.client.MouseOutEvent)
		 */
		public void onMouseOut(MouseOutEvent aArg0) {
			myPopup.hide();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.
		 * google.gwt.event.dom.client.MouseOverEvent)
		 */
		public void onMouseOver(MouseOverEvent anEvent) {
			// Reposition the popup relative to the button
			Widget source = (Widget) anEvent.getSource();
			int left = source.getAbsoluteLeft() + source.getOffsetWidth() + 1;
			int top = source.getAbsoluteTop();
			myPopup.setPopupPosition(left, top);

			// Show the popup
			myPopup.show();
		}
	}
