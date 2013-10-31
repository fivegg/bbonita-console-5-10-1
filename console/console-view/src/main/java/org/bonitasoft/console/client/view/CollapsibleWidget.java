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

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class CollapsibleWidget extends BonitaPanel {

	private DisclosurePanel myOuterPanel;
	private HideablePanel myWidget;

	private class CollapsibleWidgetHeader extends Composite {

		public CollapsibleWidgetHeader(String aHeader) {
			Label theTitle = new Label(aHeader);
			Image theExpandCollapseImagePlaceHolder = new Image(PICTURE_PLACE_HOLDER);
			theExpandCollapseImagePlaceHolder.setStyleName("collapsible_widget_action_icon");
			FlowPanel theOuterPanel = new FlowPanel();
			theOuterPanel.add(theExpandCollapseImagePlaceHolder);
			theOuterPanel.add(theTitle);
			this.initWidget(theOuterPanel);
		}
	}

	/**
	 * Default constructor.
	 * @param aHeader
	 * @param aWidget
	 */
	public CollapsibleWidget(String aHeader, HideablePanel aWidget) {
		this(aHeader, aWidget, false);
	}

	/**
	 * Default constructor.
	 * @param aHeader
	 * @param aWidget
	 * @param isOpen
	 *            the default state of the widget. True to see the panel, false to collapse it.
	 */
	public CollapsibleWidget(String aHeader, HideablePanel aWidget, boolean isOpen) {
		super();
		myOuterPanel = new DisclosurePanel();
		myOuterPanel.setHeader(new CollapsibleWidgetHeader(aHeader));
		myOuterPanel.setOpen(isOpen);
		myWidget = aWidget;
		myOuterPanel.setContent(myWidget);
		myOuterPanel.setAnimationEnabled(true);

		this.initWidget(myOuterPanel);

		// this must be done after the initWidget
		this.setStylePrimaryName("collapsible_widget");
		myOuterPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			public void onOpen(OpenEvent<DisclosurePanel> aEvent) {
				myWidget.showPanel();

			}
		});
		myOuterPanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			public void onClose(CloseEvent<DisclosurePanel> aEvent) {
				myWidget.hidePanel();
			}
		});
	}
}
