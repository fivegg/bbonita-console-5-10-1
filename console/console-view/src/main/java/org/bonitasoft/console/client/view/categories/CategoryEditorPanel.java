/**
 * Copyright (C) 2010 BonitaSoft S.A.
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
package org.bonitasoft.console.client.view.categories;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.categories.exceptions.CategoryAlreadyExistsException;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.StyleSelectionListener;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CSSStyleSelectorWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoryEditorPanel extends BonitaPanel {

  protected HTML myErrorMessageLabel = new HTML();
  protected Label categoryNameLabel = new Label(constants.categoryNameLabel());
  protected TextBox myCategoryNameTextBox = new TextBox();
  protected DecoratorPanel myColorPickerContainer;
  protected Image myColorPicker;
  protected CSSStyleSelectorWidget myCssStyleSelectorWidget;
  protected String myPreviewCSSStyle;
  protected String myCSSStyle;

  protected Button mySaveButton = new Button(constants.save());
  protected Button myCancelButton = new Button(constants.cancelButton());

  protected Category myCategory = null;
  protected CategoryDataSource myCategoryDataSource;
  protected ClickHandler myCreateOrUpdateHandler;

  public CategoryEditorPanel(CategoryDataSource categoryDataSource, CategoryUUID categoryUUID) {
    super();
    this.myCategoryDataSource = categoryDataSource;
    if (categoryUUID != null) {
      this.myCategory = categoryDataSource.getItem(categoryUUID);
    }
    FlowPanel myOuterPanel = new FlowPanel();
    FlexTable myCategoryEditor = new FlexTable();

    if (this.myCategory == null) {
      myCategoryNameTextBox.setValue("");
      myCategoryNameTextBox.setEnabled(true);
      myPreviewCSSStyle = LabelModel.DEFAULT_PREVIEW_CSS;
      myCSSStyle = LabelModel.DEFAULT_READONLY_CSS;
    } else {
      myCategoryNameTextBox.setValue(myCategory.getName());
      myCategoryNameTextBox.setEnabled(false);
      if(myCategory.getPreviewCSSStyleName()==null) {
        myPreviewCSSStyle = LabelModel.DEFAULT_PREVIEW_CSS;
      } else {
        myPreviewCSSStyle = myCategory.getPreviewCSSStyleName();
      }
      
      if(myCategory.getCSSStyleName() ==null) {
        myCSSStyle = LabelModel.DEFAULT_READONLY_CSS;
      } else {
        myCSSStyle = myCategory.getCSSStyleName();
      }
    }

    myColorPicker = new Image(PICTURE_PLACE_HOLDER);
    myColorPicker.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent aEvent) {
        if (myCssStyleSelectorWidget == null) {
          myCssStyleSelectorWidget = new CSSStyleSelectorWidget();
          myCssStyleSelectorWidget.setStyleSelectionListener(new StyleSelectionListener() {

            public void notifySelectionChange(String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle) {
              myPreviewCSSStyle = aPreviewCSSStyle;
              myCSSStyle = aReadOnlyCSSStyle;
              updateColorPickerStyle();
           // hide the popup
              myCssStyleSelectorWidget.hide();
            }

          });
        }
        // Reposition the popup relative to the button
        Widget source = (Widget) aEvent.getSource();
        int left = source.getAbsoluteLeft();
        int top = source.getAbsoluteTop() + getOffsetHeight();
        myCssStyleSelectorWidget.setPopupPosition(left, top);

        // Show the popup
        myCssStyleSelectorWidget.show();
      }
    });
    myColorPickerContainer = new DecoratorPanel();
    myColorPickerContainer.add(myColorPicker);
    updateColorPickerStyle();

    int theRow = 0;
    myCategoryEditor.setWidget(theRow, 0, myErrorMessageLabel);
    myCategoryEditor.getFlexCellFormatter().setColSpan(theRow, 0, 3);

    theRow++;
    myCategoryEditor.setWidget(theRow, 0, categoryNameLabel);
    myCategoryEditor.getFlexCellFormatter().setStyleName(theRow, 0, "identity_form_label");
    myCategoryNameTextBox.setStyleName("category_dialog_input");
    myCategoryEditor.setWidget(theRow, 1, myCategoryNameTextBox);
    myCategoryEditor.setHTML(theRow, 2, constants.mandatorySymbol());

    theRow++;
    myCategoryEditor.setHTML(theRow, 0, constants.categoryColor());
    myCategoryEditor.getFlexCellFormatter().setStyleName(theRow, 0, "identity_form_label");
    myCategoryNameTextBox.setStyleName("category_dialog_input");
    myCategoryEditor.setWidget(theRow, 1, myColorPickerContainer);

    FlowPanel buttonPanel = new FlowPanel();
    mySaveButton.setStyleName("identity_form_button");
    buttonPanel.add(mySaveButton);
    mySaveButton.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        if (myCategory == null) {
          create();
        } else {
          update();
        }
      }
    });
    myCancelButton.setStyleName("identity_form_button");
    buttonPanel.add(myCancelButton);
    buttonPanel.setStyleName("identity_form_button_group");
    myCategoryEditor.setWidget(4, 0, buttonPanel);
    myCategoryEditor.getFlexCellFormatter().setColSpan(4, 0, 3);
    myOuterPanel.add(myCategoryEditor);
    initWidget(myOuterPanel);
  }

  protected void updateColorPickerStyle() {
    myColorPicker.setStyleName(myPreviewCSSStyle);
    myColorPickerContainer.setStylePrimaryName(myPreviewCSSStyle + "_container");
  }

  public void setFocus() {
    if(myCategory==null) {
      myCategoryNameTextBox.setFocus(true);
    }
  }

  /**
   * Add a click handler on the cancel button
   * 
   * @return HandlerRegistration
   */
  public HandlerRegistration addCancelClickHandler(ClickHandler clickHandler) {
    return myCancelButton.addClickHandler(clickHandler);
  }

  /**
   * Add a click handler on the save button
   */
  public void addSaveClickHandler(ClickHandler clickHandler) {
    myCreateOrUpdateHandler = clickHandler;
  }

  protected boolean validate() {
    StringBuilder errorMessages = new StringBuilder();
    if (myCategoryNameTextBox.getValue() == null || myCategoryNameTextBox.getValue().length() == 0) {
      errorMessages.append(patterns.mandatoryFieldLabel(constants.categoryNameLabel()));
    }
    myErrorMessageLabel.setHTML(errorMessages.toString());
    return errorMessages.length() == 0;
  }

  protected void create() {
    if (validate()) {
      Category newCategory = new Category(null, removeLastSpace(myCategoryNameTextBox.getValue()));
      newCategory.setCSSStyleName(myCSSStyle);
      newCategory.setPreviewCSSStyleName(myPreviewCSSStyle);
      myCategoryDataSource.addItem(newCategory, new AsyncHandler<ItemUpdates<Category>>() {

        public void handleFailure(Throwable t) {
          if (t instanceof CategoryAlreadyExistsException) {
            myErrorMessageLabel.setText(messages.categoryAlreadyExists(removeLastSpace(myCategoryNameTextBox.getValue())));
          }
        }

        public void handleSuccess(ItemUpdates<Category> result) {
          if (myCreateOrUpdateHandler != null) {
            myCreateOrUpdateHandler.onClick(null);
          }
        }
      });
    }
  }

  protected void update() {
    if (validate()) {
      Category updatedCategory = new Category(null, myCategoryNameTextBox.getValue());
      updatedCategory.setCSSStyleName(myCSSStyle);
      updatedCategory.setPreviewCSSStyleName(myPreviewCSSStyle);
      myCategoryDataSource.updateItem(myCategory.getUUID(), updatedCategory, new AsyncHandler<Category>() {

        public void handleFailure(Throwable t) {
          if (t instanceof CategoryAlreadyExistsException) {
            myErrorMessageLabel.setText(messages.categoryAlreadyExists(myCategoryNameTextBox.getValue()));
          }
        }

        public void handleSuccess(Category result) {
          if (myCreateOrUpdateHandler != null) {
            myCreateOrUpdateHandler.onClick(null);
          }
        }
      });
    }
  }
  
  private String removeLastSpace(String value) {
	  if(value.endsWith(" ")){
		  value = value.substring(0, value.lastIndexOf(" "));
		  return removeLastSpace(value);
      }
	  return value;
  }
  
}
