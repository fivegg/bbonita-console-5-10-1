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
package org.bonitasoft.console.client.view.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.events.AddItemHandler;
import org.bonitasoft.console.client.events.HasAddHandler;
import org.bonitasoft.console.client.events.HasRemoveHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.RemoveItemsHandler;
import org.bonitasoft.console.client.identity.Role;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.I18NComposite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class RolesListEditorView extends I18NComposite implements ModelChangeListener, HasAddHandler<Role>, HasRemoveHandler<Role> {

  protected static final int MIN_ROW_COUNT = 10;
  protected static final String ITEM_LIST_EMPTY_ROW_STYLE = "item_list_empty_row";
  
  private Grid myItemList;
  protected final Set<String> myExistingItems;
  protected final List<Role> myItems;
  protected final Map<Integer, Role> myRowItem = new HashMap<Integer, Role>();
  protected final Set<Integer> myItemRowSelection;
  
  protected final RoleFinderPanel myRoleFinder;
  protected CustomDialogBox myRoleSearchPopup;
  protected Role myRoleToAdd;


  protected final FlexTable myOuterPanel;
  protected final RoleDataSource myRoleDataSource;
  private ArrayList<AddItemHandler<Role>> myAddHandlers;
  private ArrayList<RemoveItemsHandler<Role>> myRemoveHandlers;
  private Label myErrorMessage;

  /**
   * Default constructor.
   */
  public RolesListEditorView(final RoleDataSource aRoleDataSource) {
    myRoleDataSource = aRoleDataSource;

    myItems = new ArrayList<Role>();
    myExistingItems = new HashSet<String>();
    myRoleFinder = new RoleFinderPanel(false);
    
    myOuterPanel = new FlexTable();


    myItemRowSelection = new HashSet<Integer>();

    buildContent();

    myOuterPanel.setStylePrimaryName("bos_role_list_editor");
    initWidget(myOuterPanel);
  }

  protected void buildContent() {

    myItemList = new Grid(1, 3);
    myItemList.setWidth("100%");
    myItemList.setStylePrimaryName("item_list");
    myItemList.setWidget(0, 0, buildSelectAllSelector());
    myItemList.setHTML(0, 1, constants.roleNameLabel());
    myItemList.setHTML(0, 2, constants.roleDescriptionLabel());
    myItemList.getColumnFormatter().setStyleName(0, "item_selector");
    myItemList.getRowFormatter().setStylePrimaryName(0, "item_list_content_row_title");
    
    final CustomMenuBar theActionButtons = new CustomMenuBar();
    theActionButtons.addItem(constants.add(), new Command() {

      public void execute() {
        if (myRoleSearchPopup == null) {
          myRoleSearchPopup = buildRoleSearchPopup();
        }
        myRoleSearchPopup.center();
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

  protected CustomDialogBox buildRoleSearchPopup() {
    final CustomDialogBox theResult = new CustomDialogBox(false, true);
    myRoleFinder.addModelChangeListener(RoleFinderPanel.ITEM_LIST_PROPERTY, this);
    myRoleFinder.addModelChangeListener(RoleFinderPanel.CANCEL_PROPERTY, this);
    theResult.add(myRoleFinder);
    theResult.setText(constants.searchForARole());
    return theResult;
  }

  protected void addItemToList() {
    if (myRoleToAdd != null ) {
      final String theRoleId = myRoleToAdd.getUUID().getValue();
      boolean alreadyInList = (myExistingItems.contains(theRoleId));

      if (!alreadyInList) {
        if (myAddHandlers != null) {
          for (AddItemHandler<Role> theHandler : myAddHandlers) {
            theHandler.addItemRequested(myRoleToAdd);
          }
        }
      }
    }
  }

  private Widget buildSelectAllSelector() {
    final CheckBox theSelectAllCheckBox = new CheckBox();
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
    theSelector.addItem(theSelectAllCheckBox, new Command() {
      public void execute() {
        boolean theNewValue = !theSelectAllCheckBox.getValue();
        theSelectAllCheckBox.setValue(theNewValue,true);
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
    final Collection<Role> theItemsToRemove = new HashSet<Role>();
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
    for (RemoveItemsHandler<Role> theHandler : myRemoveHandlers) {
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
    if (RoleFinderPanel.ITEM_LIST_PROPERTY.equals(anEvt.getPropertyName())) {
      final List<Role> theRoles = myRoleFinder.getItems();
      if(theRoles != null && !theRoles.isEmpty()) {
        myRoleToAdd = theRoles.get(0);
        myRoleSearchPopup.hide();
        addItemToList();
      }
    } else if (RoleFinderPanel.CANCEL_PROPERTY.equals(anEvt.getPropertyName())) {
      myRoleSearchPopup.hide();
    } 
  }

  public void addAddHandler(AddItemHandler<Role> aHandler) {
    if (aHandler != null) {
      if (myAddHandlers == null) {
        myAddHandlers = new ArrayList<AddItemHandler<Role>>();
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
  public void addRemoveHandler(RemoveItemsHandler<Role> aHandler) {
    if (aHandler != null) {
      if (myRemoveHandlers == null) {
        myRemoveHandlers = new ArrayList<RemoveItemsHandler<Role>>();
      }
      if (!myRemoveHandlers.contains(aHandler)) {
        myRemoveHandlers.add(aHandler);
      }
    }

  }

  /**
   * @param aResult
   */
  public void setItems(List<Role> anItemList) {
    myItems.clear();
    myExistingItems.clear();
    if (anItemList != null) {
      myItems.addAll(anItemList);
      String theRoleId;
      for (Role theItem : anItemList) {
        theRoleId = theItem.getUUID().getValue();
          myExistingItems.add(theRoleId);
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
      for (Role theItem : myItems) {
        myItemList.setWidget(theCurrentRow, 0, buildItemSelector(theCurrentRow));
        myItemList.setWidget(theCurrentRow, 1, new Label(theItem.getLabel()));
        myItemList.setWidget(theCurrentRow, 2, new Label(theItem.getDescription()));
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
