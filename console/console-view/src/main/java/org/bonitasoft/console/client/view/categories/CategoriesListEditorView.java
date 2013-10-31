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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.events.AddItemHandler;
import org.bonitasoft.console.client.events.HasAddHandler;
import org.bonitasoft.console.client.events.HasRemoveHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.RemoveItemsHandler;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.I18NComposite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoriesListEditorView extends I18NComposite implements ModelChangeListener, HasAddHandler<Category>, HasRemoveHandler<Category> {

  protected static final int MIN_ROW_COUNT = 10;
  protected static final String ITEM_LIST_EMPTY_ROW_STYLE = "item_list_empty_row";
  protected static final String CONTAINER_SUFFIX_STYLE = "_container";
  private Grid myItemList;
  protected final Set<String> myExistingItems;
  protected final List<Category> myItems;
  protected final Map<Integer, Category> myRowItem = new HashMap<Integer, Category>();
  protected final Set<Integer> myItemRowSelection;

  protected final CategoryFinderPanel myCategoryFinder;
  protected CustomDialogBox myCategorySearchPopup;
  protected Category myCategoryToAdd;

  protected final FlexTable myOuterPanel;
  protected final CategoryDataSource myCategoryDataSource;
  private ArrayList<AddItemHandler<Category>> myAddHandlers;
  private ArrayList<RemoveItemsHandler<Category>> myRemoveHandlers;
  private Label myErrorMessage;

  /**
   * Default constructor.
   */
  public CategoriesListEditorView(final CategoryDataSource aCategoryDataSource) {
    myCategoryDataSource = aCategoryDataSource;

    myItems = new ArrayList<Category>();
    myExistingItems = new HashSet<String>();
    myCategoryFinder = new CategoryFinderPanel(myCategoryDataSource, false);

    myOuterPanel = new FlexTable();

    myItemRowSelection = new HashSet<Integer>();

    buildContent();

    myOuterPanel.setStylePrimaryName("bos_category_list_editor");
    initWidget(myOuterPanel);
  }

  protected void buildContent() {

    myItemList = new Grid(1, 3);
    myItemList.setWidth("100%");
    myItemList.setStylePrimaryName("item_list");
    myItemList.setWidget(0, 0, buildSelectAllSelector());
    myItemList.setHTML(0, 1, constants.categoryColor());
    myItemList.setHTML(0, 2, constants.name());
    myItemList.getRowFormatter().setStylePrimaryName(0, "item_list_content_row_title");

    final CustomMenuBar theActionButtons = new CustomMenuBar();
    theActionButtons.addItem(constants.add(), new Command() {

      public void execute() {
        if (myCategorySearchPopup == null) {
          myCategorySearchPopup = buildCategorySearchPopup();
        }
        myCategorySearchPopup.center();
      }
    });

    theActionButtons.addItem(constants.delete(), new Command() {

      public void execute() {
        removeSelectedItems();
      }
    });

    myOuterPanel.setWidget(0, 0, myItemList);
    myOuterPanel.setWidget(1, 0, theActionButtons);

    myErrorMessage = new Label();
    myErrorMessage.setStyleName(CSSClassManager.VALIDATION_ERROR_MESSAGE);
    myOuterPanel.setWidget(3, 0, myErrorMessage);
  }

  protected CustomDialogBox buildCategorySearchPopup() {
    final CustomDialogBox theResult = new CustomDialogBox(false, true);
    myCategoryFinder.addModelChangeListener(CategoryFinderPanel.ITEM_LIST_PROPERTY, this);
    myCategoryFinder.addModelChangeListener(CategoryFinderPanel.CANCEL_PROPERTY, this);
    theResult.add(myCategoryFinder);
    theResult.setText(constants.search());
    return theResult;
  }

  protected void addItemToList() {
    if (myCategoryToAdd != null) {
      final String theCategoryId = myCategoryToAdd.getUUID().getValue();
      boolean alreadyInList = (myExistingItems.contains(theCategoryId));

      if (!alreadyInList) {
        if (myAddHandlers != null) {
          for (AddItemHandler<Category> theHandler : myAddHandlers) {
            theHandler.addItemRequested(myCategoryToAdd);
          }
        }
      }
    }
  }

  private Widget buildSelectAllSelector() {
    final FlowPanel theWrapper = new FlowPanel();
    final CheckBox theSelectAllCheckBox = new CheckBox();
    theWrapper.add(theSelectAllCheckBox);
    theSelectAllCheckBox.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        if (aEvent.getSource() instanceof CheckBox) {
          CheckBox theCheckBox = (CheckBox) aEvent.getSource();
          selectAllRows(theCheckBox.getValue());
          aEvent.stopPropagation();
        }
      }
    });
    final CustomMenuBar theSelector = new CustomMenuBar();
    theSelector.addItem(theWrapper, new Command() {
      public void execute() {
        boolean theNewValue = !theSelectAllCheckBox.getValue();
        theSelectAllCheckBox.setValue(theNewValue, true);
        selectAllRows(theNewValue);
      }
    });
    return theSelector;
  }

  protected void selectAllRows(Boolean aValue) {
    for (int i = 1; i < myItemList.getRowCount(); i++) {
      Widget theWidget = myItemList.getWidget(i, 0);
      if (theWidget instanceof CheckBox) {
        ((CheckBox) theWidget).setValue(aValue, true);
      }
    }
  }

  protected void removeSelectedItems() {
    final Collection<Category> theItemsToRemove = new HashSet<Category>();
    int theRowIndex = myItemList.getRowCount() - 1;
    for (int i = theRowIndex; i >= 1; i--) {
      Widget theWidget = myItemList.getWidget(i, 0);
      if (theWidget instanceof CheckBox) {
        if (((CheckBox) theWidget).getValue()) {
          if (myRowItem.get(i) != null) {
            theItemsToRemove.add(myRowItem.get(i));
          }
        }
      }
    }
    for (RemoveItemsHandler<Category> theHandler : myRemoveHandlers) {
      theHandler.removeItemsRequested(theItemsToRemove);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
   * .bonitasoft.console.client.events.ModelChangeEvent)
   */
  public void modelChange(ModelChangeEvent anEvt) {
    if (CategoryFinderPanel.ITEM_LIST_PROPERTY.equals(anEvt.getPropertyName())) {
      final List<Category> theCategories = myCategoryFinder.getItems();
      if (theCategories != null && !theCategories.isEmpty()) {
        myCategoryToAdd = theCategories.get(0);
        myCategorySearchPopup.hide();
        addItemToList();
      }
    }else if (CategoryFinderPanel.CANCEL_PROPERTY.equals(anEvt.getPropertyName())) {
      myCategorySearchPopup.hide();
    } 
  }

  public void addAddHandler(AddItemHandler<Category> aHandler) {
    if (aHandler != null) {
      if (myAddHandlers == null) {
        myAddHandlers = new ArrayList<AddItemHandler<Category>>();
      }
      if (!myAddHandlers.contains(aHandler)) {
        myAddHandlers.add(aHandler);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.HasRemoveHandler#addRemoveHandler(
   * org.bonitasoft.console.client.events.RemoveItemsHandler)
   */
  public void addRemoveHandler(RemoveItemsHandler<Category> aHandler) {
    if (aHandler != null) {
      if (myRemoveHandlers == null) {
        myRemoveHandlers = new ArrayList<RemoveItemsHandler<Category>>();
      }
      if (!myRemoveHandlers.contains(aHandler)) {
        myRemoveHandlers.add(aHandler);
      }
    }

  }

  /**
   * @param aResult
   */
  public void setItems(List<Category> anItemList) {
    myItems.clear();
    myExistingItems.clear();
    if (anItemList != null) {
      myItems.addAll(anItemList);
      String theCategoryId;
      for (Category theItem : anItemList) {
        theCategoryId = theItem.getUUID().getValue();
        myExistingItems.add(theCategoryId);
      }
    }
    int theNbOfRow = 1;
    if (myItems != null) {
      theNbOfRow += myItems.size();
    }
    if (theNbOfRow < MIN_ROW_COUNT) {
      theNbOfRow = MIN_ROW_COUNT;
    }
    myItemList.resize(theNbOfRow, 3);
    int theCurrentRow = 1;
    if (myItems != null && !myItems.isEmpty()) {
      for (Category theItem : myItems) {
        final DecoratorPanel theColorPickerContainer = new DecoratorPanel();
        final Image theColorPicker = new Image(PICTURE_PLACE_HOLDER);
        theColorPickerContainer.add(theColorPicker);
        
        String theStyleName;
        if(theItem.getPreviewCSSStyleName()==null){
          theStyleName = LabelModel.DEFAULT_PREVIEW_CSS;
        } else {
          theStyleName = theItem.getPreviewCSSStyleName();
        }
        theColorPicker.setStyleName(theStyleName);
        theColorPickerContainer.setStylePrimaryName(theStyleName + CONTAINER_SUFFIX_STYLE);
        
        myItemList.setWidget(theCurrentRow, 0, buildItemSelector(theCurrentRow));
        myItemList.setWidget(theCurrentRow, 1, theColorPickerContainer);
        myItemList.setWidget(theCurrentRow, 2, new Label(theItem.getName()));
        myItemList.getRowFormatter().setStylePrimaryName(theCurrentRow, "item_list_content_row");
        // keep mapping between row and membership
        myRowItem.put(theCurrentRow, theItem);
        theCurrentRow++;
      }
    }
    for (; theCurrentRow < MIN_ROW_COUNT; theCurrentRow++) {
      myItemList.clearCell(theCurrentRow, 0);
      myItemList.clearCell(theCurrentRow, 1);
      myItemList.clearCell(theCurrentRow, 2);
      myItemList.getRowFormatter().setStylePrimaryName(theCurrentRow, ITEM_LIST_EMPTY_ROW_STYLE);
    }
  }

  private Widget buildItemSelector(final int row) {
    final CheckBox theSelectItemCheckBox = new CheckBox();
    theSelectItemCheckBox.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        if (aEvent.getSource() instanceof CheckBox) {
          CheckBox theCheckBox = (CheckBox) aEvent.getSource();
          if (theCheckBox.getValue()) {
            myItemRowSelection.add(row);
          } else {
            myItemRowSelection.remove(row);
          }
        }
      }
    });
    return theSelectItemCheckBox;
  }
}
