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
package org.bonitasoft.console.client.identity;

import java.util.Collection;
import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.users.UserFilter;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Anthony Birembaut, Nicolas Chabanoles
 * 
 */
public interface IdentityServiceAsync {

  /*
   * Configuration.
   */
  void getIdentityConfiguration(final AsyncCallback<IdentityConfiguration> aCallback);

  void updateConfiguration(IdentityConfiguration aNewConfiguration, final AsyncCallback<Void> aCallback);

  /*
   * Users
   */
  void getUsers(UserFilter aFilter, AsyncCallback<ItemUpdates<User>> callback);

  void getUsers(List<UserUUID> anItemSelection, UserFilter aFilter, AsyncCallback<List<User>> aCallback);

  void getUser(UserUUID anItemUUID, UserFilter aFilter, AsyncCallback<User> aCallback);

  void addUser(User user, final UserFilter anItemFilter, AsyncCallback<ItemUpdates<User>> callback);

  void updateUser(UserUUID aUserUuid, User user, AsyncCallback<User> callback);

  void removeUsers(Collection<UserUUID> anItemSelection, UserFilter aFilter, AsyncCallback<ItemUpdates<User>> aCallback);

  /*
   * Groups.
   */
  void getAllGroups(final GroupFilter anItemFilter, final AsyncCallback<ItemUpdates<Group>> aCallback);

  void getGroups(List<BonitaUUID> anItemSelection, GroupFilter aFilter, AsyncCallback<List<Group>> aCallback);

  void getGroup(BonitaUUID anItemUUID, GroupFilter aFilter, AsyncCallback<Group> aCallback);

  void addGroup(final Group aGroup, final GroupFilter anItemFilter, final AsyncCallback<ItemUpdates<Group>> aCallback);

  void updateGroup(final BonitaUUID aGroupUUID, final Group aGroup, final AsyncCallback<Group> aCallback);

  void removeGroups(final Collection<BonitaUUID> anItemsSelection, final GroupFilter anItemFilter, final AsyncCallback<ItemUpdates<Group>> aCallback);

  /*
   * Roles.
   */
  void getAllRoles(SimpleFilter anItemFilter, AsyncCallback<ItemUpdates<Role>> aCallback);

  void getRoles(List<BonitaUUID> anItemSelection, SimpleFilter aFilter, AsyncCallback<List<Role>> aCallback);

  void getRole(BonitaUUID anItemUUID, SimpleFilter aFilter, AsyncCallback<Role> aCallback);

  void addRole(Role role, final SimpleFilter anItemFilter, AsyncCallback<ItemUpdates<Role>> callback);

  void updateRole(BonitaUUID aRoleUUID, Role role, AsyncCallback<Role> callback);

  void removeRoles(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter, AsyncCallback<ItemUpdates<Role>> aCallback);

  /*
   * User Metadatas.
   */
  void addUserMetadata(UserMetadataItem anItem, SimpleFilter aFilter, AsyncCallback<ItemUpdates<UserMetadataItem>> aCallback);

  void removeUserMetadatas(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter, AsyncCallback<ItemUpdates<UserMetadataItem>> aCallback);

  void getAllUserMetadatas(SimpleFilter anItemFilter, AsyncCallback<ItemUpdates<UserMetadataItem>> aCallback);

  void getUserMetadata(BonitaUUID anItemUUID, SimpleFilter aFilter, AsyncCallback<UserMetadataItem> aCallback);

  void getUserMetadatas(List<BonitaUUID> anItemSelection, SimpleFilter aFilter, AsyncCallback<List<UserMetadataItem>> aCallback);

  void updateUserMetadata(BonitaUUID anItemId, UserMetadataItem anItem, AsyncCallback<UserMetadataItem> aCallback);

  void getAllUserMetadatas(AsyncCallback<List<UserMetadataItem>> aCallback);

  /*
   * Memberships.
   */
  void getMembershipItem(BonitaUUID anItemUUID, SimpleFilter aFilter, AsyncCallback<MembershipItem> aCallback);

  void getMembershipItems(List<BonitaUUID> anItemSelection, SimpleFilter aFilter, AsyncCallback<List<MembershipItem>> aCallback);

}
