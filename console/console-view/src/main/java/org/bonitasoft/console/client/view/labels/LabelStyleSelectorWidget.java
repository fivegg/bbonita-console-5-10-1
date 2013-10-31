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

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.StyleSelectionListener;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.view.I18NPopupPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This widget allows a user to select a css style using some preview mechanism to associate to a {@link PersistedLabelModel}.
 * 
 * @author Nicolas Chabanoles
 */
public class LabelStyleSelectorWidget extends I18NPopupPanel {

	protected static final String[] editableCSSClassName = new String[] { "label_red_editable", "label_dark_red_editable", "label_green_editable", "label_dark_green_editable", "label_blue_editable",
			"label_dark_blue_editable", LabelModel.DEFAULT_EDITABLE_CSS, "label_dark_grey_editable" };
	protected static final String[] readonlyCSSClassName = new String[] { "label_red_readonly", "label_dark_red_readonly", "label_green_readonly", "label_dark_green_readonly", "label_blue_readonly",
			"label_dark_blue_readonly", LabelModel.DEFAULT_READONLY_CSS, "label_dark_grey_readonly" };
	protected static final String[] previewCSSClassName = new String[] { "label_red_preview", "label_dark_red_preview", "label_green_preview", "label_dark_green_preview", "label_blue_preview",
			"label_dark_blue_preview", LabelModel.DEFAULT_PREVIEW_CSS, "label_dark_grey_preview" };

	protected static final String CONTAINER_CSS_SUFFIX = "_container";
	protected static final String B_CHARACTER = "b";
	protected static final String TITLE_LABEL_KEY = constants.selectLabelStyle();
	protected StyleSelectionListener myStyleSelectionListener;
	protected LabelDataSource myLabelDataSource;

	protected TextBox myNewLabelName = new TextBox();
	protected final DialogBox myCreateDialogBox;
	protected LabelModel myLabelModel;
	protected final Label myErrorMessage;

	private class StyleClickHandler implements ClickHandler {

		LabelModel myLabel;
		String myEditableCSSStyle;
		String myPreviewCSSStyle;
		String myReadOnlyCSSStyle;

		/**
		 * Default constructor.
		 * 
		 * @param aLabel
		 * @param aEditableCSSStyle
		 * @param aPreviewCSSStyle
		 * @param aReadOnlyCSSStyle
		 */
		public StyleClickHandler(LabelModel aLabel, String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle) {
			super();
			this.myLabel = aLabel;
			this.myEditableCSSStyle = aEditableCSSStyle;
			this.myPreviewCSSStyle = aPreviewCSSStyle;
			this.myReadOnlyCSSStyle = aReadOnlyCSSStyle;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
		public void onClick(ClickEvent aArg0) {
			myLabelDataSource.updateLabelCSSStyle(myLabel, myEditableCSSStyle, myPreviewCSSStyle, myReadOnlyCSSStyle);
			myStyleSelectionListener.notifySelectionChange(myEditableCSSStyle, myPreviewCSSStyle, myReadOnlyCSSStyle);
		}
	}

	/**
	 * 
	 * Default constructor.
	 * 
	 * @param aLabelDataSource
	 * @param aLabelModel
	 */
	public LabelStyleSelectorWidget(final LabelDataSource aLabelDataSource, final LabelModel aLabelModel) {
		super();
		myLabelDataSource = aLabelDataSource;
		myLabelModel = aLabelModel;
		myErrorMessage = new Label();
		myErrorMessage.setStylePrimaryName(CSSClassManager.VALIDATION_ERROR_MESSAGE);
		myCreateDialogBox = createDialogBox();
		FlexTable theOuterPanel = new FlexTable();
		Label theLabel;
		DecoratorPanel theContainer;
		theLabel = new Label(TITLE_LABEL_KEY);
		theOuterPanel.setWidget(0, 0, theLabel);
		theOuterPanel.getFlexCellFormatter().setColSpan(0, 0, 4);
		//

		if (editableCSSClassName.length != readonlyCSSClassName.length || editableCSSClassName.length != previewCSSClassName.length) {
			Window.alert("Invalid list of CSS style definitions in class LabelStyleSelectorWidget!");
		}

		int theRow = 1;
		int theCol = 0;
		for (int i = 0; i < editableCSSClassName.length; i++) {
			theContainer = new DecoratorPanel();
			theLabel = new Label(B_CHARACTER);
			theContainer.add(theLabel);
			theContainer.setStylePrimaryName(previewCSSClassName[i] + CONTAINER_CSS_SUFFIX);
			theLabel.setStyleName(previewCSSClassName[i]);
			theLabel.addClickHandler(new StyleClickHandler(aLabelModel, editableCSSClassName[i], previewCSSClassName[i], readonlyCSSClassName[i]));
			theOuterPanel.setWidget(theRow, theCol, theContainer);
			// Go to a new line every 4 choices.
			if (theCol == 3) {
				theRow++;
				theCol = 0;
			} else {
				theCol++;
			}
		}

		// Add a separator.
		theRow++;
		theOuterPanel.setHTML(theRow, 0, "<HR>");
		theOuterPanel.getFlexCellFormatter().setColSpan(theRow, 0, 4);

		// Add a menu.
		theRow++;
		Label theRenameLink = new Label(constants.renameLabel());
		theRenameLink.setStylePrimaryName(CSSClassManager.LINK_LABEL);
		theOuterPanel.setWidget(theRow, 0, theRenameLink);
		theOuterPanel.getFlexCellFormatter().setColSpan(theRow, 0, 4);

		theRenameLink.addClickHandler(new ClickHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google .gwt.event.dom.client.ClickEvent)
			 */
			public void onClick(final ClickEvent aClickEvent) {
				myCreateDialogBox.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
					public void setPosition(int anOffsetWidth, int anOffsetHeight) {
						int left = ((Window.getClientWidth() / 2) - (anOffsetWidth / 2));
						int top = aClickEvent.getNativeEvent().getClientY() - (anOffsetHeight / 2);
						myCreateDialogBox.setPopupPosition(left, top);
					}
				});
				myNewLabelName.setText(myLabelModel.getUUID().getValue());
				myNewLabelName.setFocus(true);
				LabelStyleSelectorWidget.this.hide();
			}
		});

		this.add(theOuterPanel);
		// Set the auto hide feature.
		setAutoHideEnabled(true);
	}

	/**
	 * 
	 * @param aListener
	 */
	public void setStyleSelectionListener(StyleSelectionListener aListener) {
		myStyleSelectionListener = aListener;

	}

	private DialogBox createDialogBox() {
		// Create a dialog box and set the caption text
		final DialogBox theDialogBox = new DialogBox();
		theDialogBox.setText(constants.renameLabelWindowTitle());

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		theDialogBox.setWidget(dialogContents);

		myNewLabelName.setMaxLength(20);
		myNewLabelName.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent anEvent) {
				char theChar = anEvent.getCharCode();
				if (KeyCodes.KEY_ENTER == theChar) {
					renameLabel();
				}

			}
		});
		HorizontalPanel theButtonPanel = new HorizontalPanel();
		Button theOkButton = new Button(constants.okButton(), new ClickHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google .gwt.event.dom.client.ClickEvent)
			 */
			public void onClick(ClickEvent aArg0) {
				renameLabel();
			}
		});
		Button theCancelButton = new Button(constants.cancelButton(), new ClickHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google .gwt.event.dom.client.ClickEvent)
			 */
			public void onClick(ClickEvent aArg0) {
				// Clean the form.
				myNewLabelName.setText("");
				myCreateDialogBox.hide();
			}
		});
		theButtonPanel.add(theOkButton);
		theButtonPanel.add(theCancelButton);

		// Layout widgets
		HorizontalPanel theForm = new HorizontalPanel();
		HTML label = new HTML(constants.newLabelWindowInputLabel());

		theForm.add(label);
		theForm.add(myNewLabelName);
		

		// theForm.setCellHorizontalAlignment(theNewLabelName, HasHorizontalAlignment.ALIGN_RIGHT);

		dialogContents.add(theForm);
		dialogContents.add(myErrorMessage);
		myErrorMessage.setVisible(false);
		dialogContents.add(theButtonPanel);
		dialogContents.setCellHorizontalAlignment(theButtonPanel, HasHorizontalAlignment.ALIGN_RIGHT);

		// Return the dialog box
		return theDialogBox;
	}

	private void renameLabel() {
		if (myNewLabelName.getValue() == null || myNewLabelName.getValue().length() == 0) {
			myErrorMessage.setText(messages.emptyLabelNameNotSupported());
			myErrorMessage.setVisible(true);
		} else if (myLabelDataSource.getLabel(myNewLabelName.getValue()) != null) {
			myErrorMessage.setText(messages.duplicateLabelName());
			myErrorMessage.setVisible(true);
		} else {
			myLabelDataSource.renameLabel(myLabelModel, myNewLabelName.getValue(), new AsyncHandler<Void>() {
				public void handleFailure(Throwable aT) {
					myErrorMessage.setText(messages.unableToUpdateLabel());
					myErrorMessage.setVisible(true);
				}

				public void handleSuccess(Void aResult) {
					myNewLabelName.setText("");
					myCreateDialogBox.hide();
					if (myErrorMessage.isVisible()) {
						myErrorMessage.setVisible(false);
					}
				}
			});
		}
	}
}
