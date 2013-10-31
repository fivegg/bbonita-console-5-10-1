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

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.identity.UserMetadataDataSource;
import org.bonitasoft.console.client.users.UserFilter;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.ItemFilterEditor;
import org.bonitasoft.console.client.view.ItemSelectionWidget;
import org.bonitasoft.console.client.view.SimpleFilterEditor;
import org.bonitasoft.console.client.view.SimpleSelectorWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UsersListWidget extends AbstractItemList<UserUUID, User, UserFilter> {

  private static final String USER_LIST_YOU_STYLE = "user_list_you";
  protected CustomDialogBox addUpdateUserDialogBox;
  protected ConfirmationDialogbox confirmationDialogbox;
  protected final HashMap<UserUUID, ItemSelectionWidget<UserUUID>> myItemSelectionWidgets = new HashMap<UserUUID, ItemSelectionWidget<UserUUID>>();
  protected final HashMap<BonitaUUID, Widget> myItemFirstNameWidgets = new HashMap<BonitaUUID, Widget>();
  protected final HashMap<BonitaUUID, Widget> myItemLastNameWidgets = new HashMap<BonitaUUID, Widget>();
  protected final HashMap<BonitaUUID, Widget> myItemUsernameWidgets = new HashMap<BonitaUUID, Widget>();

  protected final RoleDataSource myRoleDataSource;
  protected final GroupDataSource myGroupDataSource;
  protected final UserMetadataDataSource myUserMetadataDataSource;
  protected UserUUID myCurrentlyUpdatedUserUUID;
  
  /**
   * Default constructor.
   * 
   * @param aRoleDataSource
   */
  public UsersListWidget(MessageDataSource aMessageDataSource, UserDataSource aUserDataSource, RoleDataSource aRoleDataSource, GroupDataSource aGroupDataSource,
      final UserMetadataDataSource aUserMetadataDataSource) {
    super(aMessageDataSource, aUserDataSource.getItemSelection(), aUserDataSource, 20, 20, 5);
    myBonitaDataSource.addModelChangeListener(UserDataSource.ITEM_LIST_PROPERTY, this);
    myBonitaDataSource.addModelChangeListener(UserDataSource.ITEM_UPDATED_PROPERTY, this);
    myRoleDataSource = aRoleDataSource;
    myGroupDataSource = aGroupDataSource;
    myUserMetadataDataSource = aUserMetadataDataSource;
    newerButtonTop.setHTML(constants.previousPageLinkLabel());
    olderButtonTop.setHTML(constants.nextPageLinkLabel());
    newerButtonBottom.setHTML(constants.previousPageLinkLabel());
    olderButtonBottom.setHTML(constants.nextPageLinkLabel());

    initView();
    createWidgetsForItemsAndDisplay();
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
        addUser();
      }

    });
    
    myVisibleItems = myBonitaDataSource.getVisibleItems();
    theTopMenu.addItem(constants.delete(), new Command() {
      public void execute() {
          //you can not delete your own account.
          final List<UserUUID> selectList =  myItemSelection.getSelectedItems();
          Boolean hasUser = false;
		  for (UserUUID theBonitaUUID : selectList) {
		      final User theItem = ((UserDataSource) myBonitaDataSource).getItem(theBonitaUUID);
    		  final String selectedName = theItem.getUsername();
    		  final String userName = new UserUUID(BonitaConsole.userProfile.getUsername()).getValue();
    		  if (selectedName.equals(userName)){
    		      hasUser = true;
    		      myMessageDataSource.addWarningMessage(messages.deleteYourAccountWarn());
        		  break;
        	  } 
    	  }
		  //add a ConfirmationDialogbox when you delete a user.
		  if(!hasUser){
		      if (myItemSelection.getSize() > 0) {
    		      confirmationDialogbox = new ConfirmationDialogbox(constants.deleteUsers(), patterns.deleteUsersWarn(selectList.size()), constants.okButton(), constants.cancelButton());
    		      confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                    public void onClose(CloseEvent<PopupPanel> event) {                   
                        if(confirmationDialogbox.getConfirmation()){
                            deleteSelectedUsers();
                        }
                    }} );
		      } else {
		          if (myMessageDataSource != null) {
		              myMessageDataSource.addWarningMessage(messages.noUserSelected());
		          }
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

    // Create the Selector Widget
    theFirstCell.add(new SimpleSelectorWidget<UserUUID, User, UserFilter>(myBonitaDataSource));
    theFirstCell.add(theTopMenu);
    theFirstCell.add(theRefreshLink);

    return theFirstCell;
  }

  protected void deleteSelectedUsers() {
      myBonitaDataSource.deleteItems(myItemSelection.getSelectedItems(), new AsyncHandler<ItemUpdates<User>>() {
        public void handleFailure(Throwable anT) {
          myItemSelection.clearSelection();
        }
    
        public void handleSuccess(ItemUpdates<User> anResult) {
          myItemSelection.clearSelection();
        }
      });
  }

  protected void addUser() {
    History.newItem(ViewToken.UserEditor.name());
  }

  @Override
  protected void createWidgetsForItemsAndDisplay() {
    myVisibleItems = myBonitaDataSource.getVisibleItems();
    if (myVisibleItems != null) {
      hideLoading();
      myInnerTable.removeStyleName(LOADING_STYLE);
      for (UserUUID theUserUUID : myVisibleItems) {
        if (!myItemTableRow.containsKey(theUserUUID)) {
          // theItem.addModelChangeListener(CaseItem.LABELS_PROPERTY, this);
          createWidgetsForItem(theUserUUID);
        }
      }
      // Update the UI.
      update(myVisibleItems);
    } else {
      displayLoading();
      myInnerTable.addStyleName(LOADING_STYLE);
    }
  }

  protected void createWidgetsForItem(UserUUID anItem) {
    User theItem = myBonitaDataSource.getItem(anItem);
    myItemSelectionWidgets.put(anItem, new ItemSelectionWidget<UserUUID>(myItemSelection, anItem));
    myItemUsernameWidgets.put(anItem, new Label(theItem.getUsername()));
    myItemFirstNameWidgets.put(anItem, new Label(theItem.getFirstName()));
    myItemLastNameWidgets.put(anItem, new Label(theItem.getLastName()));
  }
  
  protected void updateWidgetsForItem(UserUUID anItem) {
      User theItem = myBonitaDataSource.getItem(anItem);
      ((Label)myItemUsernameWidgets.get(anItem)).setText(theItem.getUsername());
      ((Label)myItemFirstNameWidgets.get(anItem)).setText(theItem.getFirstName());
      ((Label)myItemLastNameWidgets.get(anItem)).setText(theItem.getLastName());
  }

  public void notifyItemClicked(UserUUID anItem, final ClickEvent anEvent) {
    Cell theCell = myInnerTable.getCellForEvent(anEvent);
    if (theCell!=null && theCell.getCellIndex() > 0 && anItem != null) {
      History.newItem(ViewToken.UserEditor+"/"+anItem);
    }

  }

  @Override
  protected void update(List<UserUUID> anItemList) {

    updateListSize(anItemList);

    if (myTopNavBar != null && !myTopNavBar.isAttached()) {
      // Create the navigation row (Top).
      myInnerTable.setWidget(0, 0, myTopNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(0, 0, myColumnNumber);
    }

    // Add the column titles
    myInnerTable.setWidget(1, 2, new Label(constants.username()));
    myInnerTable.setWidget(1, 3, new Label(constants.firstName()));
    myInnerTable.setWidget(1, 4, new Label(constants.lastName()));

    // Set CSS style.
    myInnerTable.getRowFormatter().setStylePrimaryName(1, ITEM_LIST_CONTENT_ROW_TITLE_STYLE);
    myInnerTable.getColumnFormatter().setStyleName(1, USER_LIST_YOU_STYLE);

    fillInContentRow(anItemList);

    if (myBottomNavBar != null && !myBottomNavBar.isAttached()) {
      // Create the navigation row (Bottom).
      int theBottomNavBarPosition = myInnerTable.getRowCount();
      myInnerTable.setWidget(theBottomNavBarPosition, 0, myBottomNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(theBottomNavBarPosition, 0, myColumnNumber);
    }
  }

  protected void fillInContentRow(List<UserUUID> anItemList) {
    int theRowOffset = 2;
    int nbItemDisplayed = 0;
    int theCurrentRowIndex = theRowOffset + nbItemDisplayed;
    for (UserUUID theBonitaUUID : anItemList) {
      theCurrentRowIndex = theRowOffset + nbItemDisplayed;

      User theItem = ((UserDataSource) myBonitaDataSource).getItem(theBonitaUUID);

      Image theCurrentUserIcon = new Image(PICTURE_PLACE_HOLDER);
      if (new UserUUID(BonitaConsole.userProfile.getUsername()).getValue().equals(theItem.getUsername())) {
        theCurrentUserIcon.setStyleName(CSSClassManager.SOLO);
        theCurrentUserIcon.setTitle(constants.you());
      }

      // Add a new row to the table, then set each of its columns.
      // layout widgets
      myInnerTable.setWidget(theCurrentRowIndex, 0, myItemSelectionWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 1, theCurrentUserIcon);
      myInnerTable.setWidget(theCurrentRowIndex, 2, myItemUsernameWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 3, myItemFirstNameWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 4, myItemLastNameWidgets.get(theItem.getUUID()));

      // Set CSS style.
      if (theCurrentRowIndex % 2 == 0) {
        myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);
        myInnerTable.getRowFormatter().addStyleName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE + EVEN_STYLE_SUFFIX);
      } else {
        myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);
      }
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
    if (SimpleFilterEditor.FILTER_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
      final String theSearchPattern = ((UserFilter) anEvent.getNewValue()).getSearchPattern();
      myBonitaDataSource.getItemFilter().setSearchPattern(theSearchPattern);
      myBonitaDataSource.reload();
    } else if (UserDataSource.ITEM_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
      createWidgetsForItemsAndDisplay();
    } else if (UserDataSource.ITEM_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
      BonitaUUID theItemUUID = (BonitaUUID)anEvent.getSource();
      if(myItemSelectionWidgets.containsKey(theItemUUID)) {
        User theItem = (User)anEvent.getNewValue();
        updateWidgetsForItem(theItem.getUUID());
      }
    }
    else {
      // The event may come from a subscription made by my super class.
      super.modelChange(anEvent);
    }
  }

  @Override
  protected ItemFilterEditor<UserFilter> buildFilterEditor() {
    SimpleFilterEditor<UserFilter> theEditor = new SimpleFilterEditor<UserFilter>(myMessageDataSource, myBonitaDataSource.getItemFilter(), constants.filterUsersToolTip());
    theEditor.addModelChangeListener(SimpleFilterEditor.FILTER_UPDATED_PROPERTY, this);
    return theEditor;
  }
  
}
