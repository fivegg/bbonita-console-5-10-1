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

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.identity.UserMetadataItem;
import org.bonitasoft.console.client.identity.exceptions.RoleAlreadyExistsException;
import org.bonitasoft.console.client.identity.exceptions.UserMetadataAlreadyExistsException;
import org.bonitasoft.console.client.model.identity.UserMetadataDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Nicolas Chabanoles
 *
 */
public class UserMetadataEditorPanel extends BonitaPanel {

    protected HTML errorMessageLabel = new HTML();
    protected Label roleNameLabel = new Label(constants.userMetadataNameLabel());
    protected TextBox myItemNameTextBox = new TextBox();
    protected Label roleLabelLabel = new Label(constants.userMetadataLabelLabel());
    protected TextBox myItemLabelTextBox = new TextBox();
    protected Button saveButton = new Button(constants.save());
    protected Button cancelButton = new Button(constants.cancelButton());
    
    protected UserMetadataItem myItem = null;
    protected UserMetadataDataSource myBonitaDataSource;
    protected AsyncHandler<Void> myCreateOrUpdateHandler;
    
    public UserMetadataEditorPanel(UserMetadataDataSource aDataSource, BonitaUUID anItemUUID) {
        super();
        myBonitaDataSource = aDataSource;
        if (anItemUUID != null) {
          myItem = myBonitaDataSource.getItem(anItemUUID);
        }
        FlowPanel myOuterPanel = new FlowPanel();
        FlexTable myRoleEditor = new FlexTable();
        
        if (myItem == null) {
            myItemNameTextBox.setValue("");
            myItemLabelTextBox.setValue("");
        } else {
            myItemNameTextBox.setValue(myItem.getName());
            myItemLabelTextBox.setValue(myItem.getLabel());
        }
        myRoleEditor.setWidget(0, 1, errorMessageLabel);
        myRoleEditor.getFlexCellFormatter().setStyleName(0, 1, "identity_form_mandatory");
        myRoleEditor.getFlexCellFormatter().setColSpan(0, 1, 2);
        
        myRoleEditor.setWidget(1, 0, roleNameLabel);
        myRoleEditor.getFlexCellFormatter().setStyleName(1, 0, "identity_form_label");
        myItemNameTextBox.setStyleName("identity_form_input");
        myRoleEditor.setWidget(1, 1, myItemNameTextBox);
        myRoleEditor.setWidget(1, 2, new Label(constants.mandatorySymbol()));
        
        myRoleEditor.setWidget(2, 0, roleLabelLabel);
        myRoleEditor.getFlexCellFormatter().setStyleName(2, 0, "identity_form_label");
        myItemLabelTextBox.setStyleName("identity_form_input");
        myRoleEditor.setWidget(2, 1, myItemLabelTextBox);
        myRoleEditor.setWidget(2, 2, new Label(constants.mandatorySymbol()));
        
        FlowPanel buttonPanel = new FlowPanel();
        saveButton.setStyleName("identity_form_button");
        buttonPanel.add(saveButton);
        saveButton.addClickHandler(new ClickHandler() {
            
            public void onClick(ClickEvent event) {
                if (myItem == null) {
                    create();
                } else {
                    update();
                }
            }
        });
        cancelButton.setStyleName("identity_form_button");
        buttonPanel.add(cancelButton);
        buttonPanel.setStyleName("identity_form_button_group");
        myRoleEditor.setWidget(4, 0, buttonPanel);
        myRoleEditor.getFlexCellFormatter().setColSpan(4, 0, 3);
        myOuterPanel.add(myRoleEditor);
        initWidget(myOuterPanel);
    }
    
    public void setFocus() {
        myItemNameTextBox.setFocus(true);
    }
    
    /**
     * Add a click handler on the cancel button
     * @return HandlerRegistration
     */
    public HandlerRegistration addCancelClickHandler(ClickHandler clickHandler){
        return cancelButton.addClickHandler(clickHandler);
    }
    
    public void addSaveHandler(AsyncHandler<Void> asyncHandler) {
      myCreateOrUpdateHandler = asyncHandler;
    }
    
    protected boolean validate() {
        StringBuilder errorMessages = new StringBuilder();
        if (myItemNameTextBox.getValue() == null || myItemNameTextBox.getValue().length() == 0) {
            errorMessages.append(patterns.mandatoryFieldLabel(constants.roleName()));
        }
        if (myItemLabelTextBox.getValue() == null || myItemLabelTextBox.getValue().length() == 0) {
            if (errorMessages.length() > 0) {
                errorMessages.append("<br/>");
            }
            errorMessages.append(patterns.mandatoryFieldLabel(constants.roleLabel()));
        }
        errorMessageLabel.setHTML(errorMessages.toString());
        return errorMessages.length() == 0;
    }
    
    protected void create() {
        if (validate()) {
            UserMetadataItem theNewItem = new UserMetadataItem(null, myItemNameTextBox.getValue(), myItemLabelTextBox.getValue());
            myBonitaDataSource.addItem(theNewItem, new AsyncHandler<ItemUpdates<UserMetadataItem>>() {
    
                public void handleFailure(Throwable t) {
                    if (t instanceof UserMetadataAlreadyExistsException) {
                        errorMessageLabel.setText(messages.metadataAlreadyExists(myItemNameTextBox.getValue()));
                    }
                }
    
                public void handleSuccess(ItemUpdates<UserMetadataItem> result) {
                    if (myCreateOrUpdateHandler != null) {
                        myCreateOrUpdateHandler.handleSuccess(null);
                    }
                }
            });
        }
    }
    
    protected void update() {
        if (validate()) {
          UserMetadataItem theUpdatedItem = new UserMetadataItem(null, myItemNameTextBox.getValue(), myItemLabelTextBox.getValue());
            myBonitaDataSource.updateItem(myItem.getUUID(), theUpdatedItem, new AsyncHandler<UserMetadataItem>() {
    
                public void handleFailure(Throwable t) {
                    if (t instanceof RoleAlreadyExistsException) {
                        errorMessageLabel.setText(messages.roleAlreadyExists(myItemNameTextBox.getValue()));
                    }
                }
    
                public void handleSuccess(UserMetadataItem result) {
                    if (myCreateOrUpdateHandler != null) {
                        myCreateOrUpdateHandler.handleSuccess(null);
                    }
                }
            });
        }
    }
}
