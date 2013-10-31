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
package org.bonitasoft.console.client.view.categories;

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.ItemFilterEditor;
import org.bonitasoft.console.client.view.ItemSelectionWidget;
import org.bonitasoft.console.client.view.SimpleFilterEditor;
import org.bonitasoft.console.client.view.SimpleSelectorWidget;
import org.bonitasoft.console.client.view.identity.ConfirmationDialogbox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoriesListWidget extends AbstractItemList<CategoryUUID, Category, SimpleFilter> {

  protected static final String CONTAINER_SUFFIX_STYLE = "_container";
  protected CustomDialogBox addUpdateItemDialogBox;
  protected ConfirmationDialogbox confirmationDialogbox;
  protected final HashMap<CategoryUUID, ItemSelectionWidget<CategoryUUID>> myItemSelectionWidgets = new HashMap<CategoryUUID, ItemSelectionWidget<CategoryUUID>>();
  protected final HashMap<CategoryUUID, DecoratorPanel> myItemColorWidgets = new HashMap<CategoryUUID, DecoratorPanel>();
  protected final HashMap<CategoryUUID, Label> myItemNameWidgets = new HashMap<CategoryUUID, Label>();

  /**
   * Default constructor.
   * 
   * @param aRoleDataSource
   */
  public CategoriesListWidget(MessageDataSource aMessageDataSource, CategoryDataSource aCategoryDataSource) {
    super(aMessageDataSource, aCategoryDataSource.getItemSelection(), aCategoryDataSource, 20, 20, 3);
    myBonitaDataSource.addModelChangeListener(CategoryDataSource.ITEM_LIST_PROPERTY, this);
    newerButtonTop.setHTML(constants.previousPageLinkLabel());
    olderButtonTop.setHTML(constants.nextPageLinkLabel());
    newerButtonBottom.setHTML(constants.previousPageLinkLabel());
    olderButtonBottom.setHTML(constants.nextPageLinkLabel());

    initView();
    createWidgetsForItemsAndDisplay();
    addUpdateItemDialogBox = new CustomDialogBox(false, true);

  }

  @Override
  protected FlowPanel buildBottomNavBar() {
    return buildTopNavBar();

  }

  @Override
  protected FlowPanel buildTopNavBar() {
    final FlowPanel theFirstCell = new FlowPanel();
    final CustomMenuBar theTopMenu = new CustomMenuBar();
    theTopMenu.addItem(constants.add(), new Command() {
      public void execute() {
        addItem();
      }

    });

    theTopMenu.addItem(constants.delete(), new Command() {
      public void execute() {
          //add a ConfirmationDialogbox when you delete a Categorie.
          if (myItemSelection.getSize() > 0) {
             confirmationDialogbox = new ConfirmationDialogbox(constants.deleteCategoriesDialogbox(), patterns.deleteCategoriesWarn( myItemSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
              confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                  public void onClose(CloseEvent<PopupPanel> event) {
                      if(confirmationDialogbox.getConfirmation()){
                          deleteSelectedItems();
                      }
                  }} );
          } else {
              if (myMessageDataSource != null) {
                  myMessageDataSource.addWarningMessage(messages.noCategorySelected());
              }
          }   
      }

    });

    Label theRefreshLink = new Label(constants.refresh());
    theRefreshLink.setStylePrimaryName(CSSClassManager.LINK_LABEL);
    theRefreshLink.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        myBonitaDataSource.reload();

      }
    });

    theFirstCell.add(new SimpleSelectorWidget<CategoryUUID, Category, SimpleFilter>(myBonitaDataSource));
    theFirstCell.add(theTopMenu);
    theFirstCell.add(theRefreshLink);

    return theFirstCell;
  }

  protected void deleteSelectedItems() {
    if (myItemSelection.getSize() > 0) {
      myBonitaDataSource.deleteItems(myItemSelection.getSelectedItems(), new AsyncHandler<ItemUpdates<Category>>() {
        public void handleFailure(Throwable anT) {
          myItemSelection.clearSelection();
        }

        public void handleSuccess(ItemUpdates<Category> anResult) {
          myItemSelection.clearSelection();
        }
      });
    } else {
      myMessageDataSource.addWarningMessage(messages.noCategorySelected());
    }
  }

  protected void updateItem(final CategoryUUID anItem) {
    if (anItem != null) {
      final CategoryEditorPanel theCategoryEditorPanel = new CategoryEditorPanel((CategoryDataSource) myBonitaDataSource, anItem);
      theCategoryEditorPanel.addCancelClickHandler(new ClickHandler() {

        public void onClick(ClickEvent event) {
          addUpdateItemDialogBox.hide();
        }
      });
      theCategoryEditorPanel.addSaveClickHandler(new ClickHandler() {

        public void onClick(ClickEvent event) {
          addUpdateItemDialogBox.hide();
          Integer theItemRow = myItemTableRow.get(anItem);
          if (theItemRow != null) {
            Category theItem = myBonitaDataSource.getItem(anItem);
            myInnerTable.setWidget(theItemRow, 0, myItemSelectionWidgets.get(theItem.getUUID()));
            myInnerTable.setWidget(theItemRow, 1, myItemColorWidgets.get(theItem.getUUID()));
            myInnerTable.setWidget(theItemRow, 2, myItemNameWidgets.get(theItem.getUUID()));
          }
        }
      });
      addUpdateItemDialogBox.clear();
      addUpdateItemDialogBox.add(theCategoryEditorPanel);
      addUpdateItemDialogBox.setText(constants.categoryUpdate());
      addUpdateItemDialogBox.center();
      theCategoryEditorPanel.setFocus();
    }
  }

  protected void addItem() {
    final CategoryEditorPanel theCategoryEditorPanel = new CategoryEditorPanel((CategoryDataSource) myBonitaDataSource, null);
    theCategoryEditorPanel.addCancelClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        addUpdateItemDialogBox.hide();
      }
    });
    theCategoryEditorPanel.addSaveClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        addUpdateItemDialogBox.hide();
      }
    });
    addUpdateItemDialogBox.clear();
    addUpdateItemDialogBox.add(theCategoryEditorPanel);
    addUpdateItemDialogBox.setText(constants.categoryCreation());
    addUpdateItemDialogBox.center();
    theCategoryEditorPanel.setFocus();
  }

  @Override
  protected void createWidgetsForItemsAndDisplay() {
    // Do not create object. Use HTML instead.
    myVisibleItems = ((CategoryDataSource) myBonitaDataSource).getVisibleItems();
    if (myVisibleItems != null) {
      hideLoading();
      myInnerTable.removeStyleName(LOADING_STYLE);
      for (CategoryUUID theItemUUID : myVisibleItems) {
        if (!myItemTableRow.containsKey(theItemUUID)) {
          createWidgetsForItem(theItemUUID);
        }
      }
      // Update the UI.
      update(myVisibleItems);
    } else {
      displayLoading();
      myInnerTable.addStyleName(LOADING_STYLE);
    }

  }

  protected void createWidgetsForItem(CategoryUUID anItem) {
    final Category theItem = myBonitaDataSource.getItem(anItem);
    final DecoratorPanel theColorPickerContainer = new DecoratorPanel();
    final Image theColorPicker = new Image(PICTURE_PLACE_HOLDER);
    theColorPickerContainer.add(theColorPicker);

    String theStyleName;
    if (theItem.getPreviewCSSStyleName() == null) {
      theStyleName = LabelModel.DEFAULT_PREVIEW_CSS;
    } else {
      theStyleName = theItem.getPreviewCSSStyleName();
    }
    theColorPicker.setStyleName(theStyleName);
    theColorPickerContainer.setStylePrimaryName(theStyleName + CONTAINER_SUFFIX_STYLE);
    theItem.addModelChangeListener(Category.CSS_CLASS_NAME_PROPERTY, new ModelChangeListener() {

      public void modelChange(ModelChangeEvent aEvt) {
        final String theStyleName = ((Category) aEvt.getSource()).getPreviewCSSStyleName();
        theColorPicker.setStyleName(theStyleName);
        theColorPickerContainer.setStylePrimaryName(theStyleName + CONTAINER_SUFFIX_STYLE);
      }
    });
    theItem.addModelChangeListener(Category.NAME_PROPERTY, new ModelChangeListener() {

      public void modelChange(ModelChangeEvent aEvt) {
        final Category theCategory = (Category) aEvt.getSource();
        Label theLabel = myItemNameWidgets.get(theCategory.getUUID());
        if (theLabel != null) {
          theLabel.setText(theCategory.getName());
        }
      }
    });

    myItemSelectionWidgets.put(anItem, new ItemSelectionWidget<CategoryUUID>(myItemSelection, anItem));
    myItemColorWidgets.put(anItem, theColorPickerContainer);
    myItemNameWidgets.put(anItem, new Label(theItem.getName()));
  }

  public void notifyItemClicked(CategoryUUID anItem, final ClickEvent anEvent) {
    Cell theCell = myInnerTable.getCellForEvent(anEvent);
    if (theCell.getCellIndex() > 0 && anItem != null) {
      updateItem(anItem);
    }

  }

  @Override
  protected void update(List<CategoryUUID> anItemList) {

    updateListSize(anItemList);

    if (myTopNavBar != null && !myTopNavBar.isAttached()) {
      // Create the navigation row (Top).
      myInnerTable.setWidget(0, 0, myTopNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(0, 0, myColumnNumber);
    }

    // Add the column titles
    myInnerTable.setWidget(1, 1, new Label(constants.categoryColor()));
    myInnerTable.getColumnFormatter().setStyleName(1, "bos_category_color_column");
    myInnerTable.setWidget(1, 2, new Label(constants.name()));
    myInnerTable.getColumnFormatter().setStyleName(2, "bos_category_name_column");
    
    // Set CSS style.
    myInnerTable.getRowFormatter().setStylePrimaryName(1, ITEM_LIST_CONTENT_ROW_TITLE_STYLE);

    fillInContentRow(anItemList);

    if (myBottomNavBar != null && !myBottomNavBar.isAttached()) {
      // Create the navigation row (Bottom).
      int theBottomNavBarPosition = myInnerTable.getRowCount();
      myInnerTable.setWidget(theBottomNavBarPosition, 0, myBottomNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(theBottomNavBarPosition, 0, myColumnNumber);
    }
  }

  protected void fillInContentRow(List<CategoryUUID> anItemList) {

    int theRowOffset = 2;
    int nbItemDisplayed = 0;
    int theCurrentRowIndex = theRowOffset + nbItemDisplayed;
    Category theItem;
    for (CategoryUUID theItemUUID : anItemList) {
      theCurrentRowIndex = theRowOffset + nbItemDisplayed;

      theItem = myBonitaDataSource.getItem(theItemUUID);
      // Add a new row to the table, then set each of its columns.
      // layout widgets
      myInnerTable.setWidget(theCurrentRowIndex, 0, myItemSelectionWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 1, myItemColorWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 2, myItemNameWidgets.get(theItem.getUUID()));

      // Set CSS style.
      myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);
      myInnerTable.getRowFormatter().getElement(theCurrentRowIndex).setTitle(getContentRowTooltip());
      // Keep link between the user and the row.
      linkItemWithRow(theItem.getUUID(), theCurrentRowIndex);

      nbItemDisplayed++;
    }

    fillWithEmptyRows(theRowOffset, theRowOffset + nbItemDisplayed, myColumnNumber);

  }

  protected String getContentRowTooltip() {
    return constants.clickToEdit();
  }

  @Override
  public void modelChange(ModelChangeEvent anEvent) {
    // The event may come from a subscription made by my super class.
    super.modelChange(anEvent);
    if (CategoryDataSource.ITEM_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
      createWidgetsForItemsAndDisplay();
    }
    if (SimpleFilterEditor.FILTER_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
      final String theSearchPattern = ((SimpleFilter) anEvent.getNewValue()).getSearchPattern();
      myBonitaDataSource.getItemFilter().setSearchPattern(theSearchPattern);
      myBonitaDataSource.reload();
    }
  }

  @Override
  protected ItemFilterEditor<SimpleFilter> buildFilterEditor() {
//    SimpleFilterEditor<SimpleFilter> theEditor = new SimpleFilterEditor<SimpleFilter>(myMessageDataSource, myBonitaDataSource.getItemFilter(), constants.filterCategoriesToolTip());
//    theEditor.addModelChangeListener(SimpleFilterEditor.FILTER_UPDATED_PROPERTY, this);
//    return theEditor;
    return null;
  }
  
  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.view.BonitaPanel#getLocationLabel()
   */
  @Override
  public String getLocationLabel() {
    return constants.categoriesTabName();
  }
}


