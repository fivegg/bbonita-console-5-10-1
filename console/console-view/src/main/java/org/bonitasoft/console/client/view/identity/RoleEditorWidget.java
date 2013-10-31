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

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.identity.Role;
import org.bonitasoft.console.client.identity.exceptions.RoleAlreadyExistsException;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Anthony Birembaut, Nicolas Chabanoles
 *
 */
public class RoleEditorWidget extends BonitaPanel {

    protected HTML errorMessageLabel = new HTML();
    protected Label roleNameLabel = new Label(constants.roleNameLabel());
    protected TextBox roleNameTextBox = new TextBox();
    protected Label roleLabelLabel = new Label(constants.roleLabelLabel());
    protected TextBox roleLabelTextBox = new TextBox();
    protected Label roleDescriptionLabel = new Label(constants.roleDescriptionLabel());
    protected TextArea roleDescriptionTextBox = new TextArea();
    protected Button saveButton = new Button(constants.save());
    protected Button cancelButton = new Button(constants.cancelButton());
    
    protected Role role = null;
    protected RoleDataSource roleDataSource;
    protected ClickHandler myCreateOrUpdateHandler;
    protected final FlexTable myOuterPanel;
    
    public RoleEditorWidget(RoleDataSource roleDataSource, BonitaUUID roleUUID) {
        super();
        this.roleDataSource = roleDataSource;
        if (roleUUID != null) {
            this.role = roleDataSource.getItem(roleUUID);
        }
        myOuterPanel = new FlexTable();
        myOuterPanel.setStylePrimaryName("bos_role_editor");
        
        if (this.role == null) {
            roleNameTextBox.setValue("");
            roleLabelTextBox.setValue("");
            roleDescriptionTextBox.setValue("");
        } else {
            roleNameTextBox.setValue(role.getName());
            roleLabelTextBox.setValue(role.getLabel());
            roleDescriptionTextBox.setValue(role.getDescription());
        }
        myOuterPanel.setWidget(0, 1, errorMessageLabel);
        myOuterPanel.getFlexCellFormatter().setStyleName(0, 1, "identity_form_mandatory");
        myOuterPanel.getFlexCellFormatter().setColSpan(0, 1, 2);
        
        myOuterPanel.setWidget(1, 0, roleNameLabel);
        myOuterPanel.getFlexCellFormatter().setStyleName(1, 0, "identity_form_label");
        roleNameTextBox.setStyleName("identity_form_input");
        myOuterPanel.setWidget(1, 1, roleNameTextBox);
        myOuterPanel.setWidget(1, 2, new Label(constants.mandatorySymbol()));
        
        myOuterPanel.setWidget(2, 0, roleLabelLabel);
        myOuterPanel.getFlexCellFormatter().setStyleName(2, 0, "identity_form_label");
        roleLabelTextBox.setStyleName("identity_form_input");
        myOuterPanel.setWidget(2, 1, roleLabelTextBox);
        myOuterPanel.setWidget(2, 2, new Label(constants.mandatorySymbol()));
        
        myOuterPanel.setWidget(3, 0, roleDescriptionLabel);
        myOuterPanel.getFlexCellFormatter().setStyleName(3, 0, "identity_form_label");
        myOuterPanel.setWidget(3, 1, roleDescriptionTextBox);
        roleDescriptionTextBox.setStyleName("identity_role_description");
        
        FlowPanel buttonPanel = new FlowPanel();
        saveButton.setStyleName("identity_form_button");
        buttonPanel.add(saveButton);
        saveButton.addClickHandler(new ClickHandler() {
            
            public void onClick(ClickEvent event) {
                if (role == null) {
                    create();
                } else {
                    update();
                }
            }
        });
        cancelButton.setStyleName("identity_form_button");
        buttonPanel.add(cancelButton);
        buttonPanel.setStyleName("identity_form_button_group");
        myOuterPanel.setWidget(4, 0, buttonPanel);
        myOuterPanel.getFlexCellFormatter().setColSpan(4, 0, 3);
        initWidget(myOuterPanel);
    }
    
    public void setFocus() {
        roleNameTextBox.setFocus(true);
    }
    
    /**
     * Add a click handler on the cancel button
     * @return HandlerRegistration
     */
    public HandlerRegistration addCancelClickHandler(ClickHandler clickHandler){
        return cancelButton.addClickHandler(clickHandler);
    }
    
    /**
     * Add a click handler on the save button
     */
    public void addSaveClickHandler(ClickHandler clickHandler){
        myCreateOrUpdateHandler = clickHandler;
    }
    
    protected boolean validate() {
        StringBuilder errorMessages = new StringBuilder();
        if (roleNameTextBox.getValue() == null || roleNameTextBox.getValue().length() == 0) {
            errorMessages.append(patterns.mandatoryFieldLabel(constants.roleName()));
        }
        if (roleLabelTextBox.getValue() == null || roleLabelTextBox.getValue().length() == 0) {
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
            Role newRole = new Role(null, roleNameTextBox.getValue(), roleLabelTextBox.getValue(), roleDescriptionTextBox.getValue());
            roleDataSource.addItem(newRole, new AsyncHandler<ItemUpdates<Role>>() {
    
                public void handleFailure(Throwable t) {
                    if (t instanceof RoleAlreadyExistsException) {
                        errorMessageLabel.setText(messages.roleAlreadyExists(roleNameTextBox.getValue()));
                    }
                }
    
                public void handleSuccess(ItemUpdates<Role> result) {
                    if (myCreateOrUpdateHandler != null) {
                        myCreateOrUpdateHandler.onClick(null);
                    }
                }
            });
        }
    }
    
    protected void update() {
        if (validate()) {
            Role updatedRole = new Role(null, roleNameTextBox.getValue(), roleLabelTextBox.getValue(), roleDescriptionTextBox.getValue());
            roleDataSource.updateItem(role.getUUID(), updatedRole, new AsyncHandler<Role>() {
    
                public void handleFailure(Throwable t) {
                    if (t instanceof RoleAlreadyExistsException) {
                        errorMessageLabel.setText(messages.roleAlreadyExists(roleNameTextBox.getValue()));
                    }
                }
    
                public void handleSuccess(Role result) {
                    if (myCreateOrUpdateHandler != null) {
                        myCreateOrUpdateHandler.onClick(null);
                    }
                }
            });
        }
    }
}
