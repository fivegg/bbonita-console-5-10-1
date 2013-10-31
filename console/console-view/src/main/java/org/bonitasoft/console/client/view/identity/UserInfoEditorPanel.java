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

import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.Focusable;
import org.bonitasoft.console.client.view.HasValidator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserInfoEditorPanel extends BonitaPanel implements HasValidator, Focusable, ModelChangeListener {

    /**
     * the max length of the username and password
     */
    private static final int MAX_LOGIN_SIZE = 50;

    protected final UserDataSource myUserDataSource;
    protected User myUser = null;

    protected final FlexTable myOuterPanel;
    protected final HTML myErrorMessageLabel = new HTML();
    protected final Label myUsernameLabel = new Label(constants.usernameLabel());
    protected final TextBox myUsernameTextBox = new TextBox();
    protected final Label myPasswordLabel = new Label(constants.passwordLabel());
    protected final PasswordTextBox myPasswordTextBox = new PasswordTextBox();
    protected final Label myPasswordConfirmLabel = new Label(constants.passwordConfirmLabel());
    protected final PasswordTextBox myPasswordConfirmTextBox = new PasswordTextBox();
    protected final Label myFirstNameLabel = new Label(constants.firstNameLabel());
    protected final TextBox myFirstNameTextBox = new TextBox();
    protected final Label myLastNameLabel = new Label(constants.lastNameLabel());
    protected final TextBox myLastNameTextBox = new TextBox();
    protected final Label myTitleLabel = new Label(constants.titleLabel());
    protected final TextBox myTitleTextBox = new TextBox();
    protected final Label myJobTitleLabel = new Label(constants.jobTitleLabel());
    protected final TextBox myJobTitleTextBox = new TextBox();
    protected final Label myManagerLabel = new Label(constants.managerLabel());
    protected final UserViewer myManagerUserViewer;
    protected final Label myDelegateLabel = new Label(constants.delegateLabel());
    protected final UserViewer myDelegateUserViewer;

    protected UserUUID myDelegateUserUUID;
    protected UserUUID myManagerUserUUID;

    public UserInfoEditorPanel(final UserDataSource userDataSource) {
        super();
        myUserDataSource = userDataSource;

        myDelegateUserViewer = new UserViewer(myUserDataSource, myDelegateUserUUID, true);
        myManagerUserViewer = new UserViewer(myUserDataSource, myManagerUserUUID, true);
        myDelegateUserViewer.addModelChangeListener(UserViewer.USER_PROPERTY, this);
        myManagerUserViewer.addModelChangeListener(UserViewer.USER_PROPERTY, this);

        myOuterPanel = buildContent();

        myOuterPanel.setStylePrimaryName("bos_user_info_editor");
        initWidget(myOuterPanel);
    }

    protected FlexTable buildContent() {
        FlexTable thePanel = new FlexTable();
        thePanel.setWidget(0, 1, myErrorMessageLabel);
        thePanel.getFlexCellFormatter().setStyleName(0, 1, "identity_form_mandatory");
        thePanel.getFlexCellFormatter().setColSpan(0, 1, 2);

        thePanel.setWidget(1, 0, myUsernameLabel);
        thePanel.getFlexCellFormatter().setStyleName(1, 0, "bos_user_editor_identity_form_label");
        myUsernameTextBox.setStyleName("identity_form_input");
        thePanel.setWidget(1, 1, myUsernameTextBox);
        thePanel.setWidget(1, 2, new Label(constants.mandatorySymbol()));

        thePanel.setWidget(2, 0, myPasswordLabel);
        thePanel.getFlexCellFormatter().setStyleName(2, 0, "bos_user_editor_identity_form_label");
        myPasswordTextBox.setStyleName("identity_form_input");
        thePanel.setWidget(2, 1, myPasswordTextBox);
        thePanel.setWidget(2, 2, new Label(constants.mandatorySymbol()));

        thePanel.setWidget(3, 0, myPasswordConfirmLabel);
        thePanel.getFlexCellFormatter().setStyleName(3, 0, "bos_user_editor_identity_form_label");
        myPasswordConfirmTextBox.setStyleName("identity_form_input");
        thePanel.setWidget(3, 1, myPasswordConfirmTextBox);
        thePanel.setWidget(3, 2, new Label(constants.mandatorySymbol()));

        thePanel.setWidget(4, 0, myFirstNameLabel);
        thePanel.getFlexCellFormatter().setStyleName(4, 0, "bos_user_editor_identity_form_label");
        myFirstNameTextBox.setStyleName("identity_form_input");
        thePanel.setWidget(4, 1, myFirstNameTextBox);

        thePanel.setWidget(5, 0, myLastNameLabel);
        thePanel.getFlexCellFormatter().setStyleName(5, 0, "bos_user_editor_identity_form_label");
        myLastNameTextBox.setStyleName("identity_form_input");
        thePanel.setWidget(5, 1, myLastNameTextBox);

        thePanel.setWidget(6, 0, myTitleLabel);
        thePanel.getFlexCellFormatter().setStyleName(6, 0, "bos_user_editor_identity_form_label");
        myTitleTextBox.setStyleName("identity_form_input");
        thePanel.setWidget(6, 1, myTitleTextBox);

        thePanel.setWidget(7, 0, myJobTitleLabel);
        thePanel.getFlexCellFormatter().setStyleName(7, 0, "bos_user_editor_identity_form_label");
        myJobTitleTextBox.setStyleName("identity_form_input");
        thePanel.setWidget(7, 1, myJobTitleTextBox);

        thePanel.setWidget(8, 0, myManagerLabel);
        thePanel.getFlexCellFormatter().setStyleName(8, 0, "bos_user_editor_identity_form_label");
        myJobTitleTextBox.setStyleName("identity_form_input");
        thePanel.setWidget(8, 1, myManagerUserViewer);

        thePanel.setWidget(9, 0, myDelegateLabel);
        thePanel.getFlexCellFormatter().setStyleName(9, 0, "bos_user_editor_identity_form_label");
        myJobTitleTextBox.setStyleName("identity_form_input");
        thePanel.setWidget(9, 1, myDelegateUserViewer);

        return thePanel;
    }

    public void setFocus() {
        myUsernameTextBox.setFocus(true);
    }

    public boolean validate() {
        StringBuilder errorMessages = new StringBuilder();
        if (myUsernameTextBox.getValue() == null || myUsernameTextBox.getValue().length() == 0) {
            errorMessages.append(patterns.mandatoryFieldLabel(constants.username()));
        }
        if (myUsernameTextBox.getValue().length() > MAX_LOGIN_SIZE) {
            if (errorMessages.length() > 0) {
                errorMessages.append("<br/>");
            }
            errorMessages.append(patterns.maxUsernameSizeWarn(MAX_LOGIN_SIZE));
        }
        if (myPasswordTextBox.getValue() == null || myPasswordTextBox.getValue().length() == 0) {
            if (errorMessages.length() > 0) {
                errorMessages.append("<br/>");
            }
            errorMessages.append(patterns.mandatoryFieldLabel(constants.password()));
        }
        if (myPasswordTextBox.getValue().length() > MAX_LOGIN_SIZE) {
            if (errorMessages.length() > 0) {
                errorMessages.append("<br/>");
            }
            errorMessages.append(patterns.maxPasswordSizeWarn(MAX_LOGIN_SIZE));
        }
        if (myPasswordTextBox.getValue().contains(":")) {
            if (errorMessages.length() > 0) {
                errorMessages.append("<br/>");
            }
            errorMessages.append(constants.passwordCannotContainColonCharacter());
        }
        if (!myPasswordTextBox.getValue().equals(myPasswordConfirmTextBox.getValue())) {
            if (errorMessages.length() > 0) {
                errorMessages.append("<br/>");
            }
            errorMessages.append(constants.wrongPasswordConfirm());
        }
        if (myUser != null && myManagerUserUUID != null) {
            if (myUser.getUUID().equals(myManagerUserUUID)) {
                if (errorMessages.length() > 0) {
                    errorMessages.append("<br/>");
                }
                errorMessages.append(constants.usersCannotBeTheirOwnManager());
            }
        }
        if (myUser != null && myDelegateUserUUID != null) {
            if (myUser.getUUID().equals(myDelegateUserUUID)) {
                if (errorMessages.length() > 0) {
                    errorMessages.append("<br/>");
                }
                errorMessages.append(constants.usersCannotBeTheirOwnDelegate());
            }
        }
        setErrorMessage(errorMessages.toString());
        return errorMessages.length() == 0;
    }

    public void update(UserUUID anItem) {
        if (anItem != null) {
            myUser = myUserDataSource.getItem(anItem);
        } else {
            myUser = null;
        }

        if (myUser == null) {
            myUsernameTextBox.setValue("");
            myUsernameTextBox.setEnabled(true);
            myPasswordTextBox.setValue("");
            myPasswordConfirmTextBox.setValue("");
            myFirstNameTextBox.setValue("");
            myLastNameTextBox.setValue("");
            myTitleTextBox.setValue("");
            myJobTitleTextBox.setValue("");
            myManagerUserViewer.setUser(null);
            myDelegateUserViewer.setUser(null);
            myManagerUserUUID = null;
            myDelegateUserUUID = null;
        } else {
            myUsernameTextBox.setValue(myUser.getUsername());
            myUsernameTextBox.setEnabled(false);
            myPasswordTextBox.setValue(myUser.getPassword());
            myPasswordConfirmTextBox.setValue(myUser.getPassword());
            myFirstNameTextBox.setValue(myUser.getFirstName());
            myLastNameTextBox.setValue(myUser.getLastName());
            myTitleTextBox.setValue(myUser.getTitle());
            myJobTitleTextBox.setValue(myUser.getJobTitle());
            myManagerUserViewer.setUser(myUser.getManagerUuid());
            myDelegateUserViewer.setUser(myUser.getDelegateUuid());
            myManagerUserUUID = myUser.getManagerUuid();
            myDelegateUserUUID = myUser.getDelegateUuid();
        }
    }

    /**
     * Create a new user with its personal info. Other fields are set to null;
     * 
     * @return
     */
    public User getUserInfo() {
        User theResult = new User();
        // keep old values (useful for values not edited in this view)
        theResult.updateItem(myUser);

        // update new values
        theResult.setDelegateUuid(myDelegateUserUUID);
        theResult.setFirstName(myFirstNameTextBox.getValue());
        theResult.setJobTitle(myJobTitleTextBox.getValue());
        theResult.setLastName(myLastNameTextBox.getValue());
        theResult.setManagerUuid(myManagerUserUUID);
        theResult.setPassword(myPasswordTextBox.getValue());
        theResult.setTitle(myTitleTextBox.getValue());
        theResult.setUsername(myUsernameTextBox.getValue());

        return theResult;
    }

    /**
     * @param anErrorMessage
     */
    public void setErrorMessage(String anErrorMessage) {
        myErrorMessageLabel.setHTML(anErrorMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
     * .bonitasoft.console.client.events.ModelChangeEvent)
     */
    public void modelChange(ModelChangeEvent aEvt) {
        if (UserViewer.USER_PROPERTY.equals(aEvt.getPropertyName())) {
            final User theNewValue = (User) aEvt.getNewValue();
            if (myDelegateUserViewer != null && aEvt.getSource().equals(myDelegateUserViewer)) {
                if (theNewValue != null) {
                    myDelegateUserUUID = theNewValue.getUUID();
                } else {
                    myDelegateUserUUID = null;
                }
            } else if (myManagerUserViewer != null && aEvt.getSource().equals(myManagerUserViewer)) {
                if (theNewValue != null) {
                    myManagerUserUUID = theNewValue.getUUID();
                } else {
                    myManagerUserUUID = null;
                }
            }
        }
    }

}
