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

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.identity.exceptions.UserAlreadyExistsException;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.identity.UserMetadataDataSource;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.Focusable;
import org.bonitasoft.console.client.view.HasValidator;
import org.bonitasoft.console.client.view.identity.ContactInfoEditorPanel.ContactType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Zhang Qixiang
 * 
 */
public class UserEditor extends BonitaPanel {

    /* Back to link */
    protected Label myBackToLabel = new Label();
    protected User myUser = null;
    protected UserDataSource userDataSource;
    protected RoleDataSource myRoleDataSource;
    protected AsyncHandler<Void> myCreateOrUpdateHandler;
    protected DecoratedTabPanel myTabPanel = new DecoratedTabPanel();
    protected FlowPanel myOuterPanel = new FlowPanel();
    protected ConfirmationDialogbox confirmationDialogbox;
    protected final ContactInfoEditorPanel myUserProfessionalContactEditor;
    protected final ContactInfoEditorPanel myUserPersonalContactEditor;
    protected final UserMembershipsEditorPanel myUserMembershipEditor;
    protected final UserUserMetadataEditorPanel myUserMetadataEditor;
    protected final UserInfoEditorPanel myUserEditor;
    protected final GroupDataSource myGroupDataSource;

    protected Button mySaveButton = new Button(constants.save());
    protected Button myCancelButton = new Button(constants.cancelButton());

    public UserEditor(final MessageDataSource aMessageDataSource, final UserDataSource userDataSource, final RoleDataSource aRoleDataSource, final GroupDataSource aGroupDataSource, final UserMetadataDataSource aUserMetadataDataSource) {
        super();
        this.userDataSource = userDataSource;
        this.myRoleDataSource = aRoleDataSource;
        this.myGroupDataSource = aGroupDataSource;

        myUserEditor = new UserInfoEditorPanel(userDataSource);
        myUserMembershipEditor = new UserMembershipsEditorPanel(aMessageDataSource, userDataSource, myGroupDataSource, myRoleDataSource);
        myUserProfessionalContactEditor = new ContactInfoEditorPanel(userDataSource, ContactType.PROFESSIONAL);
        myUserPersonalContactEditor = new ContactInfoEditorPanel(userDataSource, ContactType.PERSONAL);
        myUserMetadataEditor = new UserUserMetadataEditorPanel(userDataSource, aUserMetadataDataSource);

        // Create the action panel.
        FlowPanel buttonPanel = new FlowPanel();
        mySaveButton.setStyleName("identity_form_button");
        buttonPanel.add(mySaveButton);
        mySaveButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (myUser == null) {
                    create();
                } else {
                    if (myUser != null) {
                        myUserMembershipEditor.getRestoreAddRowItem().clear();
                        myUserMembershipEditor.setRestoreAddMembership(false);
                        myUserMembershipEditor.getRestoreDeleteRowItem().clear();
                        myUserMembershipEditor.setRestoreDeleteMembership(false);
                    }
                    update();
                }
            }
        });
        myCancelButton.setStyleName("identity_form_button");
        buttonPanel.add(myCancelButton);
        buttonPanel.setStyleName("identity_form_button_group");
        myCancelButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                //add a ConfirmationDialogbox when you delete a user.
                confirmationDialogbox = new ConfirmationDialogbox(constants.cancelUserEditionDialogbox(), constants.cancelUserEditionWarn(), constants.okButton(), constants.cancelButton());
                confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                    public void onClose(CloseEvent<PopupPanel> event) {                   
                        if(confirmationDialogbox.getConfirmation()){
                            if (myUser != null) {
                                final String restoreUernameString = myUser.getUsername();
                                myUserMembershipEditor.setRestoreUsername(restoreUernameString);
                                // restore the membership for UserMemberships
                                if (myUserMembershipEditor.getRestoreAddRowItem().isEmpty()) {
                                    myUserMembershipEditor.setRestoreAddMembership(false);
                                } else {
                                    myUserMembershipEditor.setRestoreAddMembership(true);
                                }
                                if (myUserMembershipEditor.getRestoreDeleteRowItem().isEmpty()) {
                                    myUserMembershipEditor.setRestoreDeleteMembership(false);
                                } else {
                                    myUserMembershipEditor.setRestoreDeleteMembership(true);
                                }
                            }
                            redirectToUsersList();
                            myUserEditor.setErrorMessage(null);
                        }
                    }
                });
            }
        });

        // layout panels
        myTabPanel.add(myUserEditor, constants.generalUserEditorTab());
        myTabPanel.add(myUserMembershipEditor, constants.memberOfUserEditorTab());
        myTabPanel.add(myUserProfessionalContactEditor, constants.professionalContactUserEditorTab());
        myTabPanel.add(myUserPersonalContactEditor, constants.personalContactUserEditorTab());
        myTabPanel.add(myUserMetadataEditor, constants.metadataUserEditorTab());
        myTabPanel.selectTab(0);
        myTabPanel.setStylePrimaryName("bos_user_editor_tab_panel");

        // layout label
        myBackToLabel.setStyleName(CSSClassManager.LINK_LABEL);
        myBackToLabel.setText(patterns.backToDestination(constants.User));
        myBackToLabel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent aArg0) {
                redirectToUsersList();
                myUserEditor.setErrorMessage(null);
            }
        });

        myOuterPanel.add(myBackToLabel);
        myOuterPanel.add(myTabPanel);
        myOuterPanel.add(buttonPanel);
        initWidget(myOuterPanel);
    }

    protected boolean validate() {
        Widget theWidget;
        for (int i = 0; i < myTabPanel.getWidgetCount(); i++) {
            theWidget = myTabPanel.getWidget(i);
            if (theWidget instanceof HasValidator) {
                if (!((HasValidator) theWidget).validate()) {
                    myTabPanel.selectTab(i);
                    if (theWidget instanceof Focusable) {
                        ((Focusable) theWidget).setFocus();
                    }
                    return false;
                }
            }
        }
        return true;
    }

    protected void create() {

        if (validate()) {
            final User newUser = myUserEditor.getUserInfo();
            newUser.setMembership(myUserMembershipEditor.getUserMemberships());
            newUser.setPersonnalContactInfo(myUserPersonalContactEditor.getContactInfo());
            newUser.setProfessionalContactInfo(myUserProfessionalContactEditor.getContactInfo());
            newUser.setMetadata(myUserMetadataEditor.getUserMetadata());

            userDataSource.addItem(newUser, new AsyncHandler<ItemUpdates<User>>() {

                public void handleFailure(Throwable t) {
                    if (t instanceof UserAlreadyExistsException) {
                        final int theTabIndex = myTabPanel.getWidgetIndex(myUserEditor);
                        if (theTabIndex > -1) {
                            myTabPanel.selectTab(theTabIndex);
                            myUserEditor.setErrorMessage(messages.userAlreadyExists(newUser.getUsername()));
                            myUserEditor.setFocus();
                        }
                    }
                }

                public void handleSuccess(ItemUpdates<User> result) {
                    myUserEditor.setErrorMessage(null);
                    if (myCreateOrUpdateHandler != null) {
                        myCreateOrUpdateHandler.handleSuccess(null);
                    }
                    ArrayList<User> list = result.getItems();
                    redirectToUsersList();
                }
            });
        }
    }

    protected void update() {
        if (validate()) {
            final User updatedUser = myUserEditor.getUserInfo();
            updatedUser.setMembership(myUserMembershipEditor.getUserMemberships());
            updatedUser.setPersonnalContactInfo(myUserPersonalContactEditor.getContactInfo());
            updatedUser.setProfessionalContactInfo(myUserProfessionalContactEditor.getContactInfo());
            updatedUser.setMetadata(myUserMetadataEditor.getUserMetadata());

            userDataSource.updateItem(myUser.getUUID(), updatedUser, new AsyncHandler<User>() {

                public void handleFailure(Throwable t) {
                    if (t instanceof UserAlreadyExistsException) {
                        final int theTabIndex = myTabPanel.getWidgetIndex(myUserEditor);
                        if (theTabIndex > -1) {
                            myTabPanel.selectTab(theTabIndex);
                            myUserEditor.setErrorMessage(messages.userAlreadyExists(updatedUser.getUsername()));
                            myUserEditor.setFocus();
                        }
                    }
                }

                public void handleSuccess(final User aUser) {
                    myUserEditor.setErrorMessage(null);
                    if (myCreateOrUpdateHandler != null) {
                        myCreateOrUpdateHandler.handleSuccess(null);
                    }
                    redirectToUsersList();
                    myUser=null;
                }
            });
        }
    }

    private void redirectToUsersList() {
        History.newItem(ViewToken.UsersManagement.name());
    }

    public void setUser(final UserUUID aUserUUID) {
        if (aUserUUID != null) {
            this.myUser = userDataSource.getItem(aUserUUID);
        } else {
            this.myUser = null;
        }
        myUserEditor.update(aUserUUID);
        myUserMembershipEditor.update(aUserUUID);
        myUserMetadataEditor.update(aUserUUID);
        myUserPersonalContactEditor.update(aUserUUID);
        myUserProfessionalContactEditor.update(aUserUUID);
    }

    public void setItem(final User aUser) {
        if (aUser != null) {
            this.setUser(aUser.getUUID());
        } else {
            this.setUser(null);
        }
        
        
    }
}
