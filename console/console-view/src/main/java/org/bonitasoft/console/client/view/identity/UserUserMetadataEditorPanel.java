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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.identity.UserMetadataItem;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.identity.UserMetadataDataSource;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.Focusable;
import org.bonitasoft.console.client.view.HasValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserUserMetadataEditorPanel extends BonitaPanel implements HasValidator, Focusable {

  protected final UserDataSource myUserDataSource;
  protected final UserMetadataDataSource myUserMetadataDataSource;
  protected User myUser = null;

  protected final Panel myOuterPanel;
  protected HTML errorMessageLabel = new HTML();
  protected Map<String, String> myUserMetadatas;
  protected final Set<UserMetadataItem> myAllMetadatas;
  protected final HashMap<UserMetadataItem, TextBox> myAllMetadatasTextBoxes;

  public UserUserMetadataEditorPanel(final UserDataSource aUserDataSource, final UserMetadataDataSource aUserMetadataDataSource) {
    super();
    myUserDataSource = aUserDataSource;
    myUserMetadataDataSource = aUserMetadataDataSource;
    myUserMetadatas = new HashMap<String, String>();
    myAllMetadatas = new HashSet<UserMetadataItem>();
    myAllMetadatasTextBoxes = new HashMap<UserMetadataItem, TextBox>();

    myOuterPanel = new FlowPanel();

    
    initWidget(myOuterPanel);
  }

  private Panel buildContent() {
    final int theNumberOfRows;
    if(myAllMetadatas.size() > 0 ) {
      theNumberOfRows = myAllMetadatas.size();
    } else {
      theNumberOfRows = 1;
    }
    Grid theResult = new Grid(theNumberOfRows, 2);
    theResult.setWidth("100%");
    int theRow = 0;

    for (final UserMetadataItem theMetadata : myAllMetadatas) {
      final TextBox theTextBox;
      theTextBox = new TextBox();
      theTextBox.setStyleName("identity_form_input");
      theTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {

        public void onValueChange(ValueChangeEvent<String> aEvent) {
          myUserMetadatas.put(theMetadata.getName(), theTextBox.getValue());

        }
      });
      myAllMetadatasTextBoxes.put(theMetadata, theTextBox);
      theResult.setWidget(theRow, 0, new Label(theMetadata.getLabelOrName()));
      theResult.getCellFormatter().setStyleName(theRow, 0, "bos_user_editor_identity_form_label");
      theResult.setWidget(theRow, 1, myAllMetadatasTextBoxes.get(theMetadata));
      theRow++;
    }

    if (theRow == 0) {
      theResult.setWidget(theRow, 0, new Label(constants.noMetadataDefined()));
    }

    return theResult;
  }

  public void setFocus() {
    // Nothing to do here.
  }

  public boolean validate() {
    return true;
  }

  public void update(UserUUID anItem) {
    if (anItem != null) {
      myUser = myUserDataSource.getItem(anItem);
    } else {
      myUser = null;
    }
    
    myAllMetadatas.clear();
    myAllMetadatasTextBoxes.clear();
    myOuterPanel.clear();
    myUserMetadatas.clear();
    
    myUserMetadataDataSource.getAllItems(new AsyncHandler<List<UserMetadataItem>>() {
      public void handleFailure(Throwable aT) {
        GWT.log("Error while listing all metadata.", aT);
        myOuterPanel.add(buildContent());
      }

      public void handleSuccess(List<UserMetadataItem> result) {
        if (result != null) {
          myAllMetadatas.addAll(result);
        }
        myOuterPanel.add(buildContent());
        updateContent();
      };
    });
    
    
    
  }

  protected void updateContent() {
    if (myUser == null) {
      for (TextBox theTextBox : myAllMetadatasTextBoxes.values()) {
        theTextBox.setValue(null);
      }
    } else {
      // Store locally the user metadata for future update.
      if (myUser.getMetadatas() != null) {
        myUserMetadatas = new HashMap<String, String>(myUser.getMetadatas());
      }
      String theMetadataValueForUser;
      for (Entry<UserMetadataItem, TextBox> theEntry : myAllMetadatasTextBoxes.entrySet()) {
        theMetadataValueForUser = myUserMetadatas.get(theEntry.getKey().getName());
        theEntry.getValue().setValue(theMetadataValueForUser);
      }
    }
  }

  /**
   * Create a new user with its personal info. Other fields are set to null;
   * 
   * @return
   */
  public Map<String, String> getUserMetadata() {
    Map<String, String> theResult = new HashMap<String, String>(myUserMetadatas);
    return theResult;
  }

  /**
   * @param anErrorMessage
   */
  public void setErrorMessage(String anErrorMessage) {
    errorMessageLabel.setHTML(anErrorMessage);
  }

}
