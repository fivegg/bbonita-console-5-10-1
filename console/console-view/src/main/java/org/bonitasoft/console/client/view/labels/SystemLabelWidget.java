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
package org.bonitasoft.console.client.view.labels;

import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class SystemLabelWidget extends BonitaPanel {

	private static final String SYSTEM_LABEL_WIDGET_PLACEHOLDER = "system_label_widget_placeholder";
	private static final String NOT_SELECTED_STYLE = "system_label_widget";
	private static final String SELECTED_STYLE = "system_label_widget_selected";
	public static final String SYSTEM_LABEL_STYLE_PREFIX = "system_label_";

	private LabelModel myLabelModel;
	private final Label myLabelName = new Label();

	// Create the layout.
	private final DecoratorPanel myOuterPanel = new DecoratorPanel();
	private final HorizontalPanel myInnerPanel = new HorizontalPanel();

	// private LabelDataSource myLabelDataSource;

	/**
	 * 
	 * Default constructor.
	 * 
	 * @param aLabel
	 * @param selected
	 */
	public SystemLabelWidget(LabelDataSource aLabelDataSource, LabelModel aLabel, boolean selected) {
		super();
		myLabelModel = aLabel;
		// myLabelDataSource = aLabelDataSource;

		// Associate a css class name.
		if (selected) {
			myOuterPanel.setStyleName(SELECTED_STYLE);
		} else {
			myOuterPanel.setStyleName(NOT_SELECTED_STYLE);
		}

		// Create the handler for the dynamic part of the widget.
		ClickHandler theClickHandler = new ClickHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
			 */
			public void onClick(ClickEvent aArg0) {
				History.newItem(ViewToken.CaseList + "/lab:" + myLabelModel.getUUID().getValue());
			}
		};

		// Create the widgets.
		Image theLabelIcon = new Image(PICTURE_PLACE_HOLDER);
		theLabelIcon.setStyleName(myLabelModel.getIconCSSStyle());

		// Set the click handlers.
		myLabelName.addClickHandler(theClickHandler);
		theLabelIcon.addClickHandler(theClickHandler);

		// Use an empty place holder image to be aligned with custom labels
		Image theEmptyPicture = new Image(PICTURE_PLACE_HOLDER);
		theEmptyPicture.setStyleName(SYSTEM_LABEL_WIDGET_PLACEHOLDER);
		// Finally layout widgets.
		myInnerPanel.add(theEmptyPicture);
		myInnerPanel.add(myLabelName);
		myInnerPanel.add(theLabelIcon);

		// Wrap the inner panel into a decorated panel to allow rounded corner design creation.
		myOuterPanel.setWidget(myInnerPanel);
		this.initWidget(myOuterPanel);

		// Set text to display.
		String theLocalizedLabelName = LocaleUtil.translate(myLabelModel.getUUID());
		if(theLocalizedLabelName.length()> 20){
			theLocalizedLabelName = theLocalizedLabelName.substring(0, 20) + "...";
		}
		myLabelName.setText(theLocalizedLabelName);
		myLabelName.setTitle(LocaleUtil.getLinkTitle(myLabelModel.getUUID()));
	}

	/**
	 * Get the CSS style given to this widget by default.<br>
	 * Useful if you want to reset the widget.
	 * 
	 * @return
	 */
	public static String getDefaultStyleName() {
		return NOT_SELECTED_STYLE;
	}

	/**
	 * Defines whether the label is selected or not.
	 * 
	 * @param isSelected
	 */
	public void setSelected(boolean isSelected) {
		// Associate a css class name.
		if (isSelected) {
			myOuterPanel.setStyleName(SELECTED_STYLE);
		} else {
			myOuterPanel.setStyleName(NOT_SELECTED_STYLE);
		}
	}
}
