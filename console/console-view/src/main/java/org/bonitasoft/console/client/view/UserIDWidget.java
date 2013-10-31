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
package org.bonitasoft.console.client.view;

import org.bonitasoft.console.client.users.UserProfile;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserIDWidget extends BonitaPanel {

  protected final FlowPanel myOuterPanel = new FlowPanel();
  protected final UserProfile myUserProfile;

  /**
   * Default constructor.
   * @param aUserProfile
   */
  public UserIDWidget(UserProfile aUserProfile) {
    super();
    myUserProfile = aUserProfile;
    // set the css style name
    myOuterPanel.setStyleName("bos_user_id");
    myOuterPanel.add(buildUserIdentityCard());

    this.initWidget(myOuterPanel);
  }

  private Widget buildUserIdentityCard() {
    Label theUserIdentity = new Label(myUserProfile.getUsername());
    theUserIdentity.setStylePrimaryName("identif-1");
    return theUserIdentity;
  }

}
