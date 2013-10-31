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
package org.bonitasoft.console.client.view.identity;

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.identity.Role;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.ItemFilterEditor;
import org.bonitasoft.console.client.view.ItemSelectionWidget;
import org.bonitasoft.console.client.view.RoleSelectorWidget;
import org.bonitasoft.console.client.view.SimpleFilterEditor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Anthony Birembaut, Nicolas Chabanoles
 * 
 */
public class RolesListWidget extends AbstractItemList<BonitaUUID, Role, SimpleFilter> {

  protected CustomDialogBox addUpdateRoleDialogBox;
  protected ConfirmationDialogbox confirmationDialogbox;
  protected final HashMap<BonitaUUID, ItemSelectionWidget<BonitaUUID>> myItemSelectionWidgets = new HashMap<BonitaUUID, ItemSelectionWidget<BonitaUUID>>();
  protected final HashMap<BonitaUUID, Label> myItemNameWidgets = new HashMap<BonitaUUID, Label>();
  protected final HashMap<BonitaUUID, Label> myItemLabelWidgets = new HashMap<BonitaUUID, Label>();
  protected final HashMap<BonitaUUID, Label> myItemDescriptionWidgets = new HashMap<BonitaUUID, Label>();

  /**
   * Default constructor.
   * 
   * @param aRoleDataSource
   * @param aRoleSelection
   * @param aMessageDataSource
   */
  public RolesListWidget(MessageDataSource aMessageDataSource, RoleDataSource aRoleDataSource) {
    super(aMessageDataSource, aRoleDataSource.getItemSelection(), aRoleDataSource, 20, 20, 4);
    myBonitaDataSource.addModelChangeListener(RoleDataSource.ITEM_LIST_PROPERTY, this);
    myBonitaDataSource.addModelChangeListener(RoleDataSource.ITEM_UPDATED_PROPERTY, this);
    initView();
    createWidgetsForItemsAndDisplay();
    addUpdateRoleDialogBox = new CustomDialogBox(false, true);
  }

  @Override
  protected FlowPanel buildBottomNavBar() {

    // Build exactly the same panel as for the top.
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
          //add a ConfirmationDialogbox when you delete Roles.
          if (myItemSelection.getSize() > 0) {
             confirmationDialogbox = new ConfirmationDialogbox(constants.deleteRolesDialogbox(), patterns.deleteRolesWarn( myItemSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
              confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                  public void onClose(CloseEvent<PopupPanel> event) {
                      if(confirmationDialogbox.getConfirmation()){
                          deleteSelectedItems();
                      }
                  }} );
          } else {
              if (myMessageDataSource != null) {
                  myMessageDataSource.addWarningMessage(messages.noRoleSelected());
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

    theFirstCell.add(new RoleSelectorWidget((RoleDataSource) myBonitaDataSource));
    theFirstCell.add(theTopMenu);
    theFirstCell.add(theRefreshLink);

    return theFirstCell;
  }

  protected void deleteSelectedItems() {
    if (myItemSelection.getSize() > 0) {
      ((RoleDataSource) myBonitaDataSource).deleteItems(myItemSelection.getSelectedItems(), new AsyncHandler<ItemUpdates<Role>>() {
        public void handleFailure(Throwable anT) {
          myItemSelection.clearSelection();
        }

        public void handleSuccess(ItemUpdates<Role> anResult) {
          myItemSelection.clearSelection();
        }
      });
    } else {
      myMessageDataSource.addWarningMessage(messages.noRoleSelected());
    }

  }

  protected void updateRole(final BonitaUUID anItem) {
    if (anItem != null) {
      final RoleEditorWidget roleEditorPanel = new RoleEditorWidget((RoleDataSource) myBonitaDataSource, anItem);
      roleEditorPanel.addCancelClickHandler(new ClickHandler() {

        public void onClick(ClickEvent event) {
          addUpdateRoleDialogBox.hide();
        }
      });
      roleEditorPanel.addSaveClickHandler(new ClickHandler() {

        public void onClick(ClickEvent event) {
          addUpdateRoleDialogBox.hide();
          Integer itemRow = myItemTableRow.get(anItem);
          if (itemRow != null) {
            Role theItem = myBonitaDataSource.getItem(anItem);
            myInnerTable.setWidget(itemRow, 0, myItemSelectionWidgets.get(theItem.getUUID()));
            myInnerTable.setWidget(itemRow, 1, new Label(theItem.getName()));
            myInnerTable.setWidget(itemRow, 2, new Label(theItem.getLabel()));
            myInnerTable.setWidget(itemRow, 3, new Label(theItem.getDescription()));
          }
        }
      });
      addUpdateRoleDialogBox.clear();
      addUpdateRoleDialogBox.add(roleEditorPanel);
      addUpdateRoleDialogBox.setText(constants.roleUpdate());
      addUpdateRoleDialogBox.center();
      roleEditorPanel.setFocus();
    }
  }

  protected void addItem() {
    final RoleEditorWidget roleEditorPanel = new RoleEditorWidget((RoleDataSource) myBonitaDataSource, null);
    roleEditorPanel.addCancelClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        addUpdateRoleDialogBox.hide();
      }
    });
    roleEditorPanel.addSaveClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        addUpdateRoleDialogBox.hide();
      }
    });
    addUpdateRoleDialogBox.clear();
    addUpdateRoleDialogBox.add(roleEditorPanel);
    addUpdateRoleDialogBox.setText(constants.roleCreation());
    addUpdateRoleDialogBox.center();
    roleEditorPanel.setFocus();
  }

  @Override
  protected void createWidgetsForItemsAndDisplay() {
    myVisibleItems = myBonitaDataSource.getVisibleItems();
    if (myVisibleItems != null) {
      hideLoading();
      myInnerTable.removeStyleName(LOADING_STYLE);
      for (BonitaUUID theItemUUID : myVisibleItems) {
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

  protected void createWidgetsForItem(BonitaUUID anItem) {
    Role theItem = myBonitaDataSource.getItem(anItem);
    myItemSelectionWidgets.put(anItem, new ItemSelectionWidget<BonitaUUID>(myItemSelection, anItem));
    myItemNameWidgets.put(anItem, new Label(theItem.getName()));
    myItemLabelWidgets.put(anItem, new Label(theItem.getLabel()));
    myItemDescriptionWidgets.put(anItem, new Label(theItem.getDescription()));
  }
  
  protected void updateWidgetsForItem(Role anItem) {
      myItemNameWidgets.get(anItem.getUUID()).setText(anItem.getName());
      myItemLabelWidgets.get(anItem.getUUID()).setText(anItem.getLabel());
      myItemDescriptionWidgets.get(anItem.getUUID()).setText(anItem.getDescription());
  }

  public void notifyItemClicked(BonitaUUID anItem, final ClickEvent anEvent) {
    Cell theCell = myInnerTable.getCellForEvent(anEvent);
    if (theCell!=null && theCell.getCellIndex() > 0 && anItem != null) {
        History.newItem(ViewToken.RoleEditor.name() + ConsoleConstants.TOKEN_SEPARATOR + anItem.getValue());
    }
  }

  @Override
  protected void update(List<BonitaUUID> anItemList) {

    updateListSize(anItemList);

    if (myTopNavBar != null && !myTopNavBar.isAttached()) {
      // Create the navigation row (Top).
      myInnerTable.setWidget(0, 0, myTopNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(0, 0, 4);
    }

    // Add the column titles
    myInnerTable.setWidget(1, 1, new Label(constants.roleName()));
    myInnerTable.setWidget(1, 2, new Label(constants.roleLabel()));
    myInnerTable.setWidget(1, 3, new Label(constants.roleDescription()));

    // Set CSS style.
    myInnerTable.getRowFormatter().setStylePrimaryName(1, ITEM_LIST_CONTENT_ROW_TITLE_STYLE);

    fillInContentRows(anItemList);

    if (myBottomNavBar != null && !myBottomNavBar.isAttached()) {
      // Create the navigation row (Bottom).
      int theBottomNavBarPosition = myInnerTable.getRowCount();
      myInnerTable.setWidget(theBottomNavBarPosition, 0, myBottomNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(theBottomNavBarPosition, 0, 4);
    }

  }

  protected void fillInContentRows(List<BonitaUUID> aAnItemList) {
    int theRowOffset = 2;
    int nbItemDisplayed = 0;
    int theCurrentRowIndex = theRowOffset + nbItemDisplayed;
    for (BonitaUUID theBonitaUUID : aAnItemList) {

      theCurrentRowIndex = theRowOffset + nbItemDisplayed;

      Role theItem = myBonitaDataSource.getItem(theBonitaUUID);

      // Add a new row to the table, then set each of its columns.
      // layout widgets
      myInnerTable.setWidget(theCurrentRowIndex, 0, myItemSelectionWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 1, myItemNameWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 2, myItemLabelWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 3, myItemDescriptionWidgets.get(theItem.getUUID()));

      // Set CSS style.
      if (theCurrentRowIndex % 2 == 0) {
        myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);
        myInnerTable.getRowFormatter().addStyleName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE + EVEN_STYLE_SUFFIX);
      } else {
        myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);
      }
      myInnerTable.getRowFormatter().getElement(theCurrentRowIndex).setTitle(getContentRowTooltip());
      // Keep link between the process and the row.
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
    if (RoleDataSource.ITEM_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
      createWidgetsForItemsAndDisplay();
    } else if (SimpleFilterEditor.FILTER_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
      final String theSearchPattern = ((SimpleFilter) anEvent.getNewValue()).getSearchPattern();
      myBonitaDataSource.getItemFilter().setSearchPattern(theSearchPattern);
      myBonitaDataSource.reload();
    } else if(RoleDataSource.ITEM_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
        BonitaUUID theItemUUID = (BonitaUUID)anEvent.getSource();
        if(myItemSelectionWidgets.containsKey(theItemUUID)) {
          Role theItem = (Role)anEvent.getNewValue();
          updateWidgetsForItem(theItem);
        }
      }
  }

  @Override
  protected ItemFilterEditor<SimpleFilter> buildFilterEditor() {
    SimpleFilterEditor<SimpleFilter> theEditor = new SimpleFilterEditor<SimpleFilter>(myMessageDataSource, myBonitaDataSource.getItemFilter(), constants.filterRolesToolTip());
    theEditor.addModelChangeListener(SimpleFilterEditor.FILTER_UPDATED_PROPERTY, this);
    return theEditor;
  }
}
