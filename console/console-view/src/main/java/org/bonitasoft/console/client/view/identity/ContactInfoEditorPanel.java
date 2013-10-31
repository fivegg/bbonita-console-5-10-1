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
import java.util.Map;

import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.Focusable;
import org.bonitasoft.console.client.view.HasValidator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ContactInfoEditorPanel extends BonitaPanel implements HasValidator, Focusable {

  public enum ContactType {
    PERSONAL, PROFESSIONAL;
  }

  protected final UserDataSource myUserDataSource;
  protected User myUser = null;
  protected final ContactType myContactType;
  protected Map<String, String> myContactInfos;

  protected FlexTable myOuterPanel = new FlexTable();
  protected HTML errorMessageLabel = new HTML();
  protected final Label myAddressLabel = new Label(constants.address());
  protected final TextBox myAddressTextBox = new TextBox();
  protected final Label myBuildingLabel = new Label(constants.building());
  protected final TextBox myBuildingTextBox = new TextBox();
  protected final Label myCityLabel = new Label(constants.city());
  protected final TextBox myCityTextBox = new TextBox();
  protected final Label myCountryLabel = new Label(constants.country());
  protected final TextBox myCountryTextBox = new TextBox();
  protected final Label myEmailLabel = new Label(constants.email());
  protected final TextBox myEmailTextBox = new TextBox();
  protected final Label myFaxLabel = new Label(constants.fax());
  protected final TextBox myFaxTextBox = new TextBox();
  protected final Label myMobileLabel = new Label(constants.mobile());
  protected final TextBox myMobileTextBox = new TextBox();
  protected final Label myPhoneLabel = new Label(constants.phone());
  protected final TextBox myPhoneTextBox = new TextBox();
  protected final Label myRoomLabel = new Label(constants.room());
  protected final TextBox myRoomTextBox = new TextBox();
  protected final Label myStateLabel = new Label(constants.state());
  protected final TextBox myStateTextBox = new TextBox();
  protected final Label myWebsiteLabel = new Label(constants.website());
  protected final TextBox myWebsiteTextBox = new TextBox();
  protected final Label myZipLabel = new Label(constants.zip());
  protected final TextBox myZipTextBox = new TextBox();
  

  public ContactInfoEditorPanel(final UserDataSource aUserDataSource, final ContactType aContactType) {
    super();
    myUserDataSource = aUserDataSource;
    myContactType = aContactType;
    
    myOuterPanel = buildContent();
    
    initWidget(myOuterPanel);
  }

  private FlexTable buildContent() {
    FlexTable thePanel = new FlexTable();
    thePanel.setWidget(0, 1, errorMessageLabel);
    thePanel.getFlexCellFormatter().setStyleName(0, 1, "identity_form_mandatory");
    thePanel.getFlexCellFormatter().setColSpan(0, 1, 2);

    thePanel.setWidget(1, 0, myEmailLabel);
    thePanel.getFlexCellFormatter().setStyleName(1, 0, "bos_user_editor_identity_form_label");
    myEmailTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(1, 1, myEmailTextBox);
//    thePanel.setWidget(1, 2, new Label(constants.mandatorySymbol()));

    thePanel.setWidget(2, 0, myPhoneLabel);
    thePanel.getFlexCellFormatter().setStyleName(2, 0, "bos_user_editor_identity_form_label");
    myPhoneTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(2, 1, myPhoneTextBox);

    thePanel.setWidget(3, 0, myMobileLabel);
    thePanel.getFlexCellFormatter().setStyleName(3, 0, "bos_user_editor_identity_form_label");
    myMobileTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(3, 1, myMobileTextBox);

    thePanel.setWidget(4, 0, myFaxLabel);
    thePanel.getFlexCellFormatter().setStyleName(4, 0, "bos_user_editor_identity_form_label");
    myFaxTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(4, 1, myFaxTextBox);

    thePanel.setWidget(5, 0, myWebsiteLabel);
    thePanel.getFlexCellFormatter().setStyleName(5, 0, "bos_user_editor_identity_form_label");
    myWebsiteTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(5, 1, myWebsiteTextBox);

    thePanel.setWidget(6, 0, myRoomLabel);
    thePanel.getFlexCellFormatter().setStyleName(6, 0, "bos_user_editor_identity_form_label");
    myRoomTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(6, 1, myRoomTextBox);
    
    thePanel.setWidget(7, 0, myBuildingLabel);
    thePanel.getFlexCellFormatter().setStyleName(7, 0, "bos_user_editor_identity_form_label");
    myBuildingTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(7, 1, myBuildingTextBox);

    thePanel.setWidget(8, 0, myAddressLabel);
    thePanel.getFlexCellFormatter().setStyleName(8, 0, "bos_user_editor_identity_form_label");
    myAddressTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(8, 1, myAddressTextBox);

    thePanel.setWidget(9, 0, myCityLabel);
    thePanel.getFlexCellFormatter().setStyleName(9, 0, "bos_user_editor_identity_form_label");
    myCityTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(9, 1, myCityTextBox);
    
    thePanel.setWidget(10, 0, myZipLabel);
    thePanel.getFlexCellFormatter().setStyleName(10, 0, "bos_user_editor_identity_form_label");
    myZipTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(10, 1, myZipTextBox);
    
    thePanel.setWidget(11, 0, myStateLabel);
    thePanel.getFlexCellFormatter().setStyleName(11, 0, "bos_user_editor_identity_form_label");
    myStateTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(11, 1, myStateTextBox);

    thePanel.setWidget(12, 0, myCountryLabel);
    thePanel.getFlexCellFormatter().setStyleName(12, 0, "bos_user_editor_identity_form_label");
    myCountryTextBox.setStyleName("identity_form_input");
    thePanel.setWidget(12, 1, myCountryTextBox);
    
    return thePanel;
  }

  public void setFocus() {
    myEmailTextBox.setFocus(true);
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
    if (myUser != null) {
      switch (myContactType) {
      case PERSONAL:
        myContactInfos = myUser.getPersonalContactInfo();
        break;
      case PROFESSIONAL:
        myContactInfos = myUser.getProfessionalContactInfo();
        break;
      }
      if(myContactInfos == null) {
        myContactInfos = new HashMap<String, String>();
      }
    } else {
      myContactInfos = new HashMap<String, String>();
    }
    
    if (myUser == null) {
      myAddressTextBox.setValue("");
      myBuildingTextBox.setValue("");
      myCityTextBox.setValue("");
      myCountryTextBox.setValue("");
      myEmailTextBox.setValue("");
      myFaxTextBox.setValue("");
      myMobileTextBox.setValue("");
      myPhoneTextBox.setValue("");
      myRoomTextBox.setValue("");
      myStateTextBox.setValue("");
      myWebsiteTextBox.setValue("");
      myZipTextBox.setValue("");
    } else {
      myAddressTextBox.setValue(myContactInfos.get(User.ADDRESS_KEY));
      myBuildingTextBox.setValue(myContactInfos.get(User.BUILDING_KEY));
      myCityTextBox.setValue(myContactInfos.get(User.CITY_KEY));
      myCountryTextBox.setValue(myContactInfos.get(User.COUNTRY_KEY));
      myEmailTextBox.setValue(myContactInfos.get(User.EMAIL_KEY));
      myFaxTextBox.setValue(myContactInfos.get(User.FAX_NUMBER_KEY));
      myMobileTextBox.setValue(myContactInfos.get(User.MOBILE_NUMBER_KEY));
      myPhoneTextBox.setValue(myContactInfos.get(User.PHONE_NUMBER_KEY));
      myRoomTextBox.setValue(myContactInfos.get(User.ROOM_KEY));
      myStateTextBox.setValue(myContactInfos.get(User.STATE_KEY));
      myWebsiteTextBox.setValue(myContactInfos.get(User.WEBSITE_KEY));
      myZipTextBox.setValue(myContactInfos.get(User.ZIPCODE_KEY));
    }
  }

  public Map<String, String> getContactInfo() {
    HashMap<String, String> theResult = new HashMap<String, String>();
    theResult.put(User.ADDRESS_KEY, myAddressTextBox.getValue());
    theResult.put(User.BUILDING_KEY, myBuildingTextBox.getValue());
    theResult.put(User.CITY_KEY, myCityTextBox.getValue());
    theResult.put(User.COUNTRY_KEY, myCountryTextBox.getValue());
    theResult.put(User.EMAIL_KEY, myEmailTextBox.getValue());
    theResult.put(User.FAX_NUMBER_KEY, myFaxTextBox.getValue());
    theResult.put(User.MOBILE_NUMBER_KEY, myMobileTextBox.getValue());
    theResult.put(User.PHONE_NUMBER_KEY, myPhoneTextBox.getValue());
    theResult.put(User.ROOM_KEY, myRoomTextBox.getValue());
    theResult.put(User.STATE_KEY, myStateTextBox.getValue());
    theResult.put(User.WEBSITE_KEY, myWebsiteTextBox.getValue());
    theResult.put(User.ZIPCODE_KEY, myZipTextBox.getValue());
    return theResult;
  }

}
