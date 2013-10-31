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
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.identity.Group;
import org.bonitasoft.console.client.identity.exceptions.GroupAlreadyExistsException;
import org.bonitasoft.console.client.identity.exceptions.UserAlreadyExistsException;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
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
public class GroupEditorWidget extends BonitaPanel implements ModelChangeListener {

  protected HTML errorMessageLabel = new HTML();
  protected Label myParentLabel = new Label(constants.groupParentLabel());
  protected final GroupViewer myParentGroupViewer;
  protected Label myNameLabel = new Label(constants.groupPathElement());
  protected TextBox myGroupNameTextBox = new TextBox();
  protected Label myLabelLabel = new Label(constants.groupLabelLabel());
  protected TextBox myLabelTextBox = new TextBox();
  protected Label myDecriptionLabel = new Label(constants.groupDescription());
  protected TextArea myDescriptionTextBox = new TextArea();

  protected Group myParentGroup;

  protected Button saveButton = new Button(constants.save());
  protected Button cancelButton = new Button(constants.cancelButton());

  protected Group myGroup = null;
  protected GroupDataSource myGroupDataSource;
  protected AsyncHandler<Void> myCreateOrUpdateHandler;

  protected final FlexTable myOuterPanel = new FlexTable();
  protected final BonitaUUID myGroupUUID;

  public GroupEditorWidget(final GroupDataSource aGroupDataSource, final BonitaUUID aGroupUUID) {
    super();
    myGroupDataSource = aGroupDataSource;
    myGroupUUID = aGroupUUID;
    myOuterPanel.setStylePrimaryName("bos_group_editor");
    if (myGroupUUID != null) {
      myGroup = myGroupDataSource.getItem(aGroupUUID);
    }

    if (myGroup == null) {
      myGroupNameTextBox.setValue("");
      myLabelTextBox.setValue("");
      myDescriptionTextBox.setValue("");
      myParentGroupViewer = new GroupViewer(myGroupDataSource, null, true);
      myParentGroup = null;
    } else {
      myGroupNameTextBox.setValue(myGroup.getName());
      myLabelTextBox.setValue(myGroup.getLabel());
      myDescriptionTextBox.setValue(myGroup.getDescription());
      if (myGroup.getParentGroup() != null) {
        myParentGroupViewer = new GroupViewer(myGroupDataSource, myGroup.getParentGroup().getUUID(), true);
        myParentGroup = myGroup.getParentGroup();
      } else {
        myParentGroupViewer = new GroupViewer(myGroupDataSource, null, true);
        myParentGroup = null;
      }
    }

    myParentGroupViewer.addModelChangeListener(GroupViewer.GROUP_PROPERTY, this);

    final int theParentRow = 0;
    final int theNameRow = 1;
    final int theLabelRow = 2;
    final int theDescriptionRow = 3;
    final int theErrorMessageRow = 4;
    
    myOuterPanel.setWidget(theParentRow, 0, myParentLabel);
    myOuterPanel.getFlexCellFormatter().setStyleName(theParentRow, 0, "identity_form_label");
    myOuterPanel.setWidget(theParentRow, 1, myParentGroupViewer);

    myOuterPanel.setWidget(theNameRow, 0, myNameLabel);
    myOuterPanel.getFlexCellFormatter().setStyleName(theNameRow, 0, "identity_form_label");
    myGroupNameTextBox.setStyleName("identity_form_input");
    myOuterPanel.setWidget(theNameRow, 1, myGroupNameTextBox);
    myOuterPanel.setHTML(theNameRow, 2, constants.mandatorySymbol());

    myOuterPanel.setWidget(theLabelRow, 0, myLabelLabel);
    myOuterPanel.getFlexCellFormatter().setStyleName(theLabelRow, 0, "identity_form_label");
    myLabelTextBox.setStyleName("identity_form_input");
    myOuterPanel.setWidget(theLabelRow, 1, myLabelTextBox);
    myOuterPanel.setHTML(theLabelRow, 2, constants.mandatorySymbol());

    myOuterPanel.setWidget(theDescriptionRow, 0, myDecriptionLabel);
    myOuterPanel.getFlexCellFormatter().setStyleName(theDescriptionRow, 0, "identity_form_label");
    myDescriptionTextBox.setStyleName("identity_group_description");
    myOuterPanel.setWidget(theDescriptionRow, 1, myDescriptionTextBox);

    myOuterPanel.setWidget(theErrorMessageRow, 1, errorMessageLabel);
    myOuterPanel.getFlexCellFormatter().setStyleName(theErrorMessageRow, 1, "identity_form_mandatory");
    myOuterPanel.getFlexCellFormatter().setColSpan(theErrorMessageRow, 1, 2);
    
    FlowPanel buttonPanel = new FlowPanel();
    saveButton.setStyleName("identity_form_button");
    buttonPanel.add(saveButton);
    saveButton.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        if (myGroup == null) {
          create();
        } else {
          update();
        }
      }
    });
    cancelButton.setStyleName("identity_form_button");
    buttonPanel.add(cancelButton);
    buttonPanel.setStyleName("identity_form_button_group");
    myOuterPanel.setWidget(8, 0, buttonPanel);
    myOuterPanel.getFlexCellFormatter().setColSpan(8, 0, 3);

    initWidget(myOuterPanel);
  }

  private String getGroupPath(){
	  
	    String theGroupPath = "";
	    if(myParentGroupViewer!=null){
	    	theGroupPath = Group.buildGroupPath(myParentGroupViewer.getMyCurrentItem());
	    }
	    theGroupPath = theGroupPath + Group.PATH_SEPARATOR + myGroupNameTextBox.getValue();
	    
	    return theGroupPath;
  }
  
  public void setFocus() {
    myGroupNameTextBox.setFocus(true);
  }

  /**
   * Add a click handler on the cancel button
   * 
   * @return HandlerRegistration
   */
  public HandlerRegistration addCancelClickHandler(ClickHandler clickHandler) {
    return cancelButton.addClickHandler(clickHandler);
  }

  protected boolean validate() {
    StringBuilder errorMessages = new StringBuilder();
    if (myGroupNameTextBox.getValue() == null || myGroupNameTextBox.getValue().length() == 0) {
      errorMessages.append(patterns.mandatoryFieldLabel(constants.username()));
    }
    if (myLabelTextBox.getValue() == null || myLabelTextBox.getValue().length() == 0) {
        if(errorMessages.length() > 0){
            errorMessages.append("<br/>");
        }
        errorMessages.append(patterns.mandatoryFieldLabel(constants.groupLabelLabel()));
      }
    if (isParentAChildOfMine()) {
      errorMessages.append(constants.groupChildOfMine());
    }
    errorMessageLabel.setHTML(errorMessages.toString());
    return errorMessages.length() == 0;
  }

  private boolean isParentAChildOfMine() {
    if (myParentGroup == null || myGroupUUID == null) {
      return false;
    }

    boolean loopDetected = false;
    BonitaUUID theCurrentGroupUUID;
    Group theCurrentGroup;
    theCurrentGroup = myParentGroup;
    while (theCurrentGroup != null && !loopDetected) {
      theCurrentGroupUUID = theCurrentGroup.getUUID();
      if (theCurrentGroupUUID.equals(myGroupUUID)) {
        loopDetected = true;
      }
      theCurrentGroup = theCurrentGroup.getParentGroup();
    }

    return loopDetected;
  }

  protected void create() {

    if (validate()) {

      final Group theNewItem = new Group(null, myGroupNameTextBox.getValue(), myLabelTextBox.getValue(), myDescriptionTextBox.getValue());
      theNewItem.setParentGroup(myParentGroup);

      myGroupDataSource.addItem(theNewItem, new AsyncHandler<ItemUpdates<Group>>() {

        public void handleFailure(Throwable t) {
          if (t instanceof GroupAlreadyExistsException) {
            errorMessageLabel.setText(messages.groupAlreadyExists(getGroupPath()));
          }
        }

        public void handleSuccess(ItemUpdates<Group> result) {
          if (myCreateOrUpdateHandler != null) {
            myCreateOrUpdateHandler.handleSuccess(null);
          }
        }
      });
    }
  }

  protected void update() {
    if (validate()) {
      final Group theUpdatedItem = new Group(myGroupUUID.getValue(), myGroupNameTextBox.getValue(), myLabelTextBox.getValue(), myDescriptionTextBox.getValue());
      theUpdatedItem.setParentGroup(myParentGroup);

      myGroupDataSource.updateItem(theUpdatedItem.getUUID(), theUpdatedItem, new AsyncHandler<Group>() {

        public void handleFailure(Throwable t) {
          if (t instanceof GroupAlreadyExistsException) {
            errorMessageLabel.setText(messages.groupAlreadyExists(getGroupPath()));
          }
        }

        public void handleSuccess(final Group aResult) {
          if (myCreateOrUpdateHandler != null) {
            myCreateOrUpdateHandler.handleSuccess(null);
          }
        }
      });
    }
  }

  /**
   * @param asyncHandler
   */
  public void addSaveHandler(AsyncHandler<Void> asyncHandler) {
    myCreateOrUpdateHandler = asyncHandler;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
   * .bonitasoft.console.client.events.ModelChangeEvent)
   */
  public void modelChange(ModelChangeEvent aEvt) {
    if (GroupViewer.GROUP_PROPERTY.equals(aEvt.getPropertyName())) {
      final Group theNewValue = (Group) aEvt.getNewValue();
      if (myParentGroupViewer != null && aEvt.getSource().equals(myParentGroupViewer)) {
        if (theNewValue != null) {
          myParentGroup = theNewValue;
        } else {
          myParentGroup = null;
        }
      }
    }

  }

}
