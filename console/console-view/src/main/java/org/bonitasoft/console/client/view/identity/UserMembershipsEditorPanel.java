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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.identity.Group;
import org.bonitasoft.console.client.identity.MembershipItem;
import org.bonitasoft.console.client.identity.Role;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.Focusable;
import org.bonitasoft.console.client.view.HasValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserMembershipsEditorPanel extends BonitaPanel implements HasValidator, Focusable, ModelChangeListener {

    protected final UserDataSource myUserDataSource;
    protected User myUser = null;

    protected Panel myOuterPanel = new FlexTable();
    protected HTML errorMessageLabel = new HTML();
    private Grid myMembershipsList;
    protected final Set<Integer> myMembershipRowSelection;
    protected Set<MembershipItem> myUserMemberships;
    protected Map<Integer, MembershipItem> myRowItem = new HashMap<Integer, MembershipItem>();
    protected final GroupViewer myGroupViewerPanel;
    protected final RoleViewer myRoleViewerPanel;
    protected Group myGroupToAdd;
    protected Role myRoleToAdd;
    protected final Label myAddErrorValidationMessage;
    protected CustomDialogBox myAddMembershipPopup;
    protected String restoreUsername = null;
    protected Set<MembershipItem> restoreAddRowItem = new HashSet<MembershipItem>();
    protected Set<MembershipItem> restoreDeleteRowItem = new HashSet<MembershipItem>();
    protected boolean isRestoreAddMembership = false;
    protected boolean isRestoreDeleteMembership = false;
    protected MessageDataSource myMessageDataSource;
    
    public UserMembershipsEditorPanel(final MessageDataSource aMessageDataSource, final UserDataSource userDataSource, final GroupDataSource aGroupDataSource, final RoleDataSource aRoleDataSource) {
        super();
        myMessageDataSource = aMessageDataSource;
        myUserDataSource = userDataSource;
        myMembershipRowSelection = new HashSet<Integer>();

        myGroupViewerPanel = new GroupViewer(aGroupDataSource, null, true);
        myRoleViewerPanel = new RoleViewer(aRoleDataSource, null, true);

        myGroupViewerPanel.addModelChangeListener(GroupViewer.GROUP_PROPERTY, this);
        myRoleViewerPanel.addModelChangeListener(RoleViewer.ROLE_PROPERTY, this);

        myAddErrorValidationMessage = new Label();

        myOuterPanel = buildContent();
        
        initWidget(myOuterPanel);
    }

    private Panel buildContent() {
        FlowPanel theResult = new FlowPanel();
        ScrollPanel theScrollPanel = new ScrollPanel();

        myMembershipsList = new Grid(1, 3);
        myMembershipsList.setWidth("100%");
        myMembershipsList.setStylePrimaryName("item_list");
        myMembershipsList.setWidget(0, 0, buildSelectAllSelector());
        myMembershipsList.setHTML(0, 1, constants.groupPath());
        myMembershipsList.setHTML(0, 2, constants.membershipRoleName());
        myMembershipsList.getRowFormatter().setStylePrimaryName(0, "item_list_content_row_title");

        theScrollPanel.setWidget(myMembershipsList);

        final CustomMenuBar theActionButtons = new CustomMenuBar();
        theActionButtons.addItem(constants.add(), new Command() {

            public void execute() {
                showAddMembershipPopup();
            }
        });

        theActionButtons.addItem(constants.delete(), new Command() {

            public void execute() {
                removeSelectedItems();
            }
        });

        theResult.add(theScrollPanel);
        theResult.add(theActionButtons);

        return theResult;
    }

    protected void showAddMembershipPopup() {
        if(myAddMembershipPopup == null) {
            buildAddMembershipPopup();
        }
        myAddMembershipPopup.center();
    }

    protected void buildAddMembershipPopup() {
        myAddMembershipPopup = new CustomDialogBox(false, true);
        myAddMembershipPopup.addStyleName("bos_user_add_membership");
        myAddMembershipPopup.setText(constants.addMembershipGroupPanelCaption());
        final FlowPanel thePopupContent = new FlowPanel();
        final Grid theAddPanel = new Grid(2, 2);
        theAddPanel.setStylePrimaryName(CSSClassManager.GROUP_PANEL_CONTENT);
        theAddPanel.setWidget(0, 0, new Label(constants.chooseAGroup()));
        theAddPanel.setWidget(0, 1, myGroupViewerPanel);
        theAddPanel.setWidget(1, 0, new Label(constants.chooseARole()));
        theAddPanel.setWidget(1, 1, myRoleViewerPanel);
        
        final FlowPanel theActionsWrapper = new FlowPanel();
        theActionsWrapper.setStylePrimaryName("bos_action_wrapper");
        final CustomMenuBar theAddButton = new CustomMenuBar();
        theAddButton.addItem(constants.add(), new Command() {

            public void execute() {
                if (addItem()) {
                    myAddMembershipPopup.hide();
                }
            }
        });

        final CustomMenuBar theCancelButton = new CustomMenuBar();
        theCancelButton.addItem(constants.cancel(), new Command() {

            public void execute() {
                myGroupViewerPanel.setItem(null);
                myRoleViewerPanel.setItem(null);
                myAddMembershipPopup.hide();
                myGroupToAdd = null;
                myRoleToAdd = null;
            }
        });
        
        theActionsWrapper.add(theAddButton);
        theActionsWrapper.add(theCancelButton);
        thePopupContent.add(myAddErrorValidationMessage);
        thePopupContent.add(theAddPanel);
        thePopupContent.add(theActionsWrapper);
        
        myAddMembershipPopup.add(thePopupContent);
    }

    protected boolean addItem() {
        int addRestoreRowItem = 1;
        Boolean isExists = false;
        MembershipItem theMembershipToAdd;
        if (myGroupToAdd != null && myRoleToAdd != null) {
            if (myUser != null) {
                theMembershipToAdd = new MembershipItem(myUser.getUUID(), myGroupToAdd, myRoleToAdd);
            } else {
                theMembershipToAdd = new MembershipItem(null, myGroupToAdd, myRoleToAdd);
            }
            for (MembershipItem theUserMembershipItem : myUserMemberships) {
                if (theUserMembershipItem.getGroup().equals(myGroupToAdd) && theUserMembershipItem.getRole().equals(myRoleToAdd)) {
                    isExists = true;
                }
            }
            if (isExists) {
                myAddMembershipPopup.hide();
                myMessageDataSource.addWarningMessage(messages.alreadyAddedTheMembership());
                myGroupViewerPanel.setItem(null);
                myRoleViewerPanel.setItem(null);
                setErrorMessage(null);
                myGroupToAdd = null;
                myRoleToAdd = null;
            } else {
                if (myUserMemberships.add(theMembershipToAdd)) {
                    if (myUser != null) {
                        myUser.getMembership().add(theMembershipToAdd);
                    }
                    restoreAddRowItem.add(theMembershipToAdd);
                    addRestoreRowItem++;
                    updateMembershipsList();
                    myGroupViewerPanel.setItem(null);
                    myRoleViewerPanel.setItem(null);
                    setErrorMessage(null);
                    myGroupToAdd = null;
                    myRoleToAdd = null;
                    return true;
                } else {
                    myMessageDataSource.addWarningMessage(messages.alreadyAddedTheMembership());
                }
            }
        }
        return false;
    }

    protected void removeSelectedItems() {
        int theRowIndex = myMembershipsList.getRowCount() - 1;
        int addRestoreRowItem = 1;
        Widget theWidget;
        MembershipItem theMembershipToRemove;
        MembershipItem theMembershipToReload;
        MembershipItem theMembershipToRestoreItem;
        if(myUser!=null){    
            myUser.getMembership().clear();
        }
        restoreDeleteRowItem.clear();
        for (int i = theRowIndex; i >= 1; i--) {
            theWidget = myMembershipsList.getWidget(i, 0);
            if (theWidget instanceof CheckBox) {
                if (((CheckBox) theWidget).getValue()) {
                    theMembershipToRestoreItem = new MembershipItem(myRowItem.get(i).getUUID(), myRowItem.get(i).getGroup(), myRowItem.get(i).getRole());
                    restoreDeleteRowItem.add(theMembershipToRestoreItem);
                    addRestoreRowItem++;
                    myMembershipsList.removeRow(i);
                    // remove mapping between row and membership
                    theMembershipToRemove = myRowItem.remove(i);
                    if (theMembershipToRemove != null) {
                        myUserMemberships.remove(theMembershipToRemove);
                    } else {
                        GWT.log("Trying to remove a membership that is not identified.", new NullPointerException());
                    }
                } else {
                    theMembershipToReload = myRowItem.get(i);
                    if (theMembershipToReload != null) {
                        myUser.getMembership().add(theMembershipToReload);
                    }
                }
            }
        }
    }
    
    /**
     * This fuction restore the add membershipif you cancel the operation
     */
    public void restoreAddUserMemberships() {
        for (MembershipItem theMembership : restoreAddRowItem) {
            myUserMemberships.remove(theMembership);
        }
        restoreAddRowItem.clear();
    }

    /**
     * This fuction restore the delete the membership if you cancel the operation
     */
    public void restoreDeleteUserMemberships() {
        for (MembershipItem theMembership : restoreDeleteRowItem) {
            myUserMemberships.add(theMembership);
        }
        restoreDeleteRowItem.clear();
    }

    /**
     * add the memberships to a new user.
     */
    public void saveMembershipsToNewUser() {
        for (MembershipItem theMembership : restoreAddRowItem) {
            MembershipItem theMembershipToNewUserItem = new MembershipItem(myUser.getUUID(), theMembership.getGroup(), theMembership.getRole());
            myUser.getMembership().add(theMembershipToNewUserItem);
        }
        restoreDeleteRowItem.clear();
    }
    
    /**
     * @param row
     */
    private Widget buildItemSelector(final int row) {
        final CheckBox theSelectItemCheckBox = new CheckBox();
        theSelectItemCheckBox.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent aEvent) {
                if (aEvent.getSource() instanceof CheckBox) {
                    CheckBox theCheckBox = (CheckBox) aEvent.getSource();
                    if (theCheckBox.getValue()) {
                        myMembershipRowSelection.add(row);
                    } else {
                        myMembershipRowSelection.remove(row);
                    }
                }
            }
        });
        return theSelectItemCheckBox;
    }

    private Widget buildSelectAllSelector() {
        final CheckBox theSelectAllCheckBox = new CheckBox();
        theSelectAllCheckBox.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent aEvent) {
                if (aEvent.getSource() instanceof CheckBox) {
                    CheckBox theCheckBox = (CheckBox) aEvent.getSource();
                    selectAllRows(theCheckBox.getValue());
                }

            }
        });
        return theSelectAllCheckBox;
    }

    /**
     * @param aValue
     */
    protected void selectAllRows(Boolean aValue) {
        for (int i = 1; i < myMembershipsList.getRowCount(); i++) {
            Widget theWidget = myMembershipsList.getWidget(i, 0);
            if (theWidget instanceof CheckBox) {
                ((CheckBox) theWidget).setValue(aValue, true);
            }
        }

    }

    public void setFocus() {
        // Nothing to do here.
    }

    public boolean validate() {
        return true;
    }
    
    /**
     * @param anItem
     */
    public void update(UserUUID anItem) {
        if (anItem != null) {
            myUser = myUserDataSource.getItem(anItem);
        } else {
            myUser = null;
        }
        // Reset the user memberships
        myUserMemberships = new HashSet<MembershipItem>();

        if (myUser == null) {
            myMembershipsList.resize(1, 3);
        } else {
            // Store locally the user memberships for future update.
            if (myUser.getMembership() != null) {
                myUserMemberships.addAll(myUser.getMembership());
            }
            if (myUser.getUsername().equals(restoreUsername)) {
                if (isRestoreAddMembership) {
                    restoreAddUserMemberships();
                    isRestoreAddMembership = false;
                }
                if (isRestoreDeleteMembership) {
                    restoreDeleteUserMemberships();
                    isRestoreDeleteMembership = false;
                }
            } else {
                restoreAddRowItem.clear();
                restoreDeleteRowItem.clear();
            }
            updateMembershipsList();
        }
    }

    private void updateMembershipsList() {
        int theNbOfRow = 1;
        if (myUserMemberships != null) {
            theNbOfRow += myUserMemberships.size();
        }
        myMembershipsList.resize(theNbOfRow, 3);
        if (myUserMemberships != null && !myUserMemberships.isEmpty()) {
            int theCurrentRow = 1;
            for (MembershipItem theMembership : myUserMemberships) {
                myMembershipsList.setWidget(theCurrentRow, 0, buildItemSelector(theCurrentRow));
                myMembershipsList.setHTML(theCurrentRow, 1, Group.buildGroupPath(theMembership.getGroup()));
                myMembershipsList.setWidget(theCurrentRow, 2, new Label(theMembership.getRole().getLabel()));
                myMembershipsList.getRowFormatter().setStylePrimaryName(theCurrentRow, "item_list_content_row");
                // keep mapping between row and membership
                myRowItem.put(theCurrentRow, theMembership);
                theCurrentRow++;
            }
        }

    }

    /**
     * Create a new user with its personal info. Other fields are set to null;
     * 
     * @return
     */
    public Set<MembershipItem> getUserMemberships() {
        final Set<MembershipItem> theResult = new HashSet<MembershipItem>(myUserMemberships);
        return theResult;
    }

    /**
     * @param anErrorMessage
     */
    public void setErrorMessage(String anErrorMessage) {
        errorMessageLabel.setHTML(anErrorMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
     * .bonitasoft.console.client.events.ModelChangeEvent)
     */
    public void modelChange(ModelChangeEvent aEvt) {
        if (RoleViewer.ROLE_PROPERTY.equals(aEvt.getPropertyName())) {
            final Role theNewValue = (Role) aEvt.getNewValue();
            myRoleToAdd = theNewValue;
        } else if (GroupViewer.GROUP_PROPERTY.equals(aEvt.getPropertyName())) {
            final Group theNewValue = (Group) aEvt.getNewValue();
            myGroupToAdd = theNewValue;
        }
    }
    
    /**
     * @param myUser the myUser to set
     */
    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }
        
    /**
     * @param restoreUsername the restoreUsername to set
     */
    public void setRestoreUsername(String restoreUsername) {
        this.restoreUsername = restoreUsername;
    }
    
    /**
     * @return the restoreAddRowItem
     */
    public Set<MembershipItem> getRestoreAddRowItem() {
        return restoreAddRowItem;
    }
    
    /**
     * @return the restoreDeleteRowItem
     */
    public Set<MembershipItem> getRestoreDeleteRowItem() {
        return restoreDeleteRowItem;
    }
    
    /**
     * @param isRestoreAddMembership the isRestoreAddMembership to set
     */
    public void setRestoreAddMembership(boolean isRestoreAddMembership) {
        this.isRestoreAddMembership = isRestoreAddMembership;
    }

    /**
     * @param isRestoreDeleteMembership the isRestoreDeleteMembership to set
     */
    public void setRestoreDeleteMembership(boolean isRestoreDeleteMembership) {
        this.isRestoreDeleteMembership = isRestoreDeleteMembership;
    }

}
