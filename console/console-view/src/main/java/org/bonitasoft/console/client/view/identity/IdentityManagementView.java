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

import java.util.ArrayList;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.identity.UserMetadataDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Anthony Birembaut, Nicolas Chabanoles
 * 
 */
public class IdentityManagementView extends BonitaPanel {

  protected final FlowPanel myOuterPanel;
  protected final DecoratedTabPanel myInnerPanel;
  protected final UsersListWidget myUsersListTab;
  protected final RolesListWidget myRolesListTab;
  protected final GroupsListWidget myGroupsListTab;
  protected final UserMetadataListWidget myUserMetadataListTab;
  protected final ArrayList<BonitaFilteredDataSource<? extends BonitaUUID, ? extends Item, ? extends ItemFilter>> myDatasources;
  

  /**
   * Default constructor.
   * 
   * @param aUserSelection
   * @param aUserDataSource
   */
  public IdentityManagementView(MessageDataSource aMessageDataSource, UserDataSource aUserDataSource, GroupDataSource aGroupDataSource, RoleDataSource aRoleDataSource, UserMetadataDataSource aUserMetadataDataSource) {
    super();
    myOuterPanel = new FlowPanel();

    myUsersListTab = initUsersList(aMessageDataSource, aUserDataSource, aRoleDataSource, aGroupDataSource, aUserMetadataDataSource);
    myRolesListTab = initRolesList(aMessageDataSource, aRoleDataSource);
    myGroupsListTab = initGroupsList(aMessageDataSource, aGroupDataSource);
    myUserMetadataListTab = initUserMetadatasList(aMessageDataSource, aUserMetadataDataSource);
    myInnerPanel = new DecoratedTabPanel();
    myInnerPanel.add(myUsersListTab, constants.usersTabName());
    myInnerPanel.add(myRolesListTab, constants.rolesTabName());
    myInnerPanel.add(myGroupsListTab, constants.groupsTabName());
    myInnerPanel.add(myUserMetadataListTab, constants.userMetadataTabName());
    myOuterPanel.add(myInnerPanel);

    // Keep order of datasources
    myDatasources = new ArrayList<BonitaFilteredDataSource<?, ?, ?>>();
    myDatasources.add(aUserDataSource);
    myDatasources.add(aRoleDataSource);
    myDatasources.add(aGroupDataSource);
    myDatasources.add(aUserMetadataDataSource);

    myOuterPanel.setStylePrimaryName("identity_management_outer_panel");
    myInnerPanel.setStylePrimaryName("identity_management_tab_panel");
    myInnerPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {

      public void onBeforeSelection(BeforeSelectionEvent<Integer> aEvent) {
        try {
          if (aEvent != null && aEvent.getItem() >= 0 && aEvent.getItem() < myDatasources.size()) {
            myDatasources.get(aEvent.getItem()).reload();
          }
        } catch (Exception e) {
          GWT.log("Error while trying to reload datasource on tab selection", e);
        }

      }
    });
    myInnerPanel.selectTab(0);
    this.initWidget(myOuterPanel);
  }

  /**
   * @param aMessageDataSource
   * @param aUserMetadataDataSource
   * @return
   */
  protected UserMetadataListWidget initUserMetadatasList(MessageDataSource aMessageDataSource, UserMetadataDataSource aUserMetadataDataSource) {
    return new UserMetadataListWidget(aMessageDataSource, aUserMetadataDataSource);
  }

  /**
   * @param aMessageDataSource
   * @param aGroupDataSource
   * @return
   */
  protected GroupsListWidget initGroupsList(MessageDataSource aMessageDataSource, GroupDataSource aGroupDataSource) {
    return new GroupsListWidget(aMessageDataSource, aGroupDataSource);
  }

  /**
   * @param aMessageDataSource
   * @param aRoleDataSource
   * @return
   */
  protected RolesListWidget initRolesList(MessageDataSource aMessageDataSource, RoleDataSource aRoleDataSource) {
    return new RolesListWidget(aMessageDataSource, aRoleDataSource);
  }

  /**
   * @param aUserMetadataDataSource 
   * @param aGroupDataSource 
   * @param aUserDataSource 
   * @param aMessageDataSource 
   * @return
   */
  protected UsersListWidget initUsersList(MessageDataSource aMessageDataSource, UserDataSource aUserDataSource,RoleDataSource aRoleDataSource, GroupDataSource aGroupDataSource, UserMetadataDataSource aUserMetadataDataSource) {
    return new UsersListWidget(aMessageDataSource, aUserDataSource, aRoleDataSource, aGroupDataSource, aUserMetadataDataSource);
  }

  @Override
  public String getLocationLabel() {
    return constants.usersManagement();
  }
}
