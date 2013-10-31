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
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.identity.exceptions.GroupAlreadyExistsException;
import org.bonitasoft.console.client.identity.exceptions.GroupNotFoundException;
import org.bonitasoft.console.client.identity.exceptions.MembershipNotFoundException;
import org.bonitasoft.console.client.identity.exceptions.RoleAlreadyExistsException;
import org.bonitasoft.console.client.identity.exceptions.RoleNotFoundException;
import org.bonitasoft.console.client.identity.exceptions.UserAlreadyExistsException;
import org.bonitasoft.console.client.identity.exceptions.UserMetadataAlreadyExistsException;
import org.bonitasoft.console.client.identity.exceptions.UserMetadataNotFoundException;
import org.bonitasoft.console.client.identity.exceptions.UserNotFoundException;
import org.bonitasoft.console.client.users.UserFilter;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author Anthony Birembaut, Nicolas Chabanoles
 * 
 */
public interface IdentityService extends RemoteService {

  /*
   * Configuration.
   */

  /**
   * Update the configuration
   * 
   * @param aNewConfiguration
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  void updateConfiguration(IdentityConfiguration aNewConfiguration) throws ConsoleException, SessionTimeOutException;

  /**
   * Read the configuration from the engine.
   * 
   * @return
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  IdentityConfiguration getIdentityConfiguration() throws ConsoleException, SessionTimeOutException;

  /*
   * Users
   */
  /**
   * Retrieve all the users
   * 
   * @param aFilter
   *          the filter to apply
   * @return a {@link List} of {@link User}
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  ItemUpdates<User> getUsers(UserFilter aFilter) throws ConsoleException, SessionTimeOutException;

  List<User> getUsers(List<UserUUID> anItemSelection, UserFilter aFilter) throws UserNotFoundException, ConsoleException, SessionTimeOutException;

  User getUser(UserUUID anItemUUID, UserFilter aFilter) throws UserNotFoundException, ConsoleException, SessionTimeOutException;

  /**
   * Create a new a user
   * 
   * @param user
   *          the user to create
   * @return the {@link User} created
   * @throws UserAlreadyExistsException
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  ItemUpdates<User> addUser(User user, final UserFilter anItemFilter) throws UserAlreadyExistsException, ConsoleException, SessionTimeOutException;

  /**
   * Update a user.
   * 
   * @param aUserUuid
   * @param user
   * @return
   * @throws UserNotFoundException
   * @throws UserAlreadyExistsException
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  User updateUser(UserUUID aUserUuid, User user) throws UserNotFoundException, UserAlreadyExistsException, UserMetadataNotFoundException, RoleNotFoundException, GroupNotFoundException,
      MembershipNotFoundException, ConsoleException, SessionTimeOutException;

  /**
   * Permanently remove users
   * 
   * @param aUserNameCollection
   * @throws UserNotFoundException
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  ItemUpdates<User> removeUsers(Collection<UserUUID> anItemSelection, UserFilter aFilter) throws UserNotFoundException, ConsoleException, SessionTimeOutException;

  /*
   * Groups.
   */

  /**
   * List groups that match the given filter.
   * 
   * @param anItemFilter
   * @param aCallback
   */
  ItemUpdates<Group> getAllGroups(final GroupFilter anItemFilter) throws ConsoleException, SessionTimeOutException;

  List<Group> getGroups(List<BonitaUUID> anItemSelection, GroupFilter aFilter) throws GroupNotFoundException, ConsoleException, SessionTimeOutException;

  Group getGroup(BonitaUUID anItemUUID, GroupFilter aFilter) throws GroupNotFoundException, ConsoleException, SessionTimeOutException;

  /**
   * Create a new group.
   * 
   * @param aGroup
   * @param anItemFilter
   * @param aCallback
   * @throws GroupNotFoundException
   * @throws GroupAlreadyExistsException
   */
  ItemUpdates<Group> addGroup(final Group aGroup, final GroupFilter anItemFilter) throws ConsoleException, SessionTimeOutException, GroupAlreadyExistsException, GroupNotFoundException;

  /**
   * Update the given group definition.
   * 
   * @param aGroupName
   * @param aGroup
   * @param aCallback
   */
  Group updateGroup(final BonitaUUID aGroupUUID, final Group aGroup) throws GroupNotFoundException, ConsoleException, SessionTimeOutException, GroupAlreadyExistsException;

  /**
   * Remove the given groups.
   * 
   * @param anItemsSelection
   * @param anItemFilter
   * @param aCallback
   */
  ItemUpdates<Group> removeGroups(final Collection<BonitaUUID> anItemsSelection, final GroupFilter anItemFilter) throws GroupNotFoundException, ConsoleException, SessionTimeOutException;

  /*
   * Roles.
   */

  /**
   * List roles matching the given filter.
   * 
   * @param anItemFilter
   * @return
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  ItemUpdates<Role> getAllRoles(SimpleFilter anItemFilter) throws ConsoleException, SessionTimeOutException;

  /**
   * List the roles identified by their ID.
   */
  List<Role> getRoles(List<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws RoleNotFoundException, ConsoleException, SessionTimeOutException;

  /***
   * Get a Role identified by its ID.
   * 
   * @param anItemUUID
   * @param aFilter
   * @return
   * @throws RoleNotFoundException
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  Role getRole(BonitaUUID anItemUUID, SimpleFilter aFilter) throws RoleNotFoundException, ConsoleException, SessionTimeOutException;

  /**
   * Create a new role
   * 
   * @param role
   *          the role to create
   * @return the {@link Role} created
   * @throws RoleAlreadyExistsException
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  ItemUpdates<Role> addRole(Role role, final SimpleFilter anItemFilter) throws RoleAlreadyExistsException, ConsoleException, SessionTimeOutException;

  /**
   * Update a role
   * 
   * @param role
   *          the new role properties
   * @return the updated {@link Role}
   * @throws RoleNotFoundException
   * @throws RoleAlreadyExistsException
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  Role updateRole(BonitaUUID aRoleUUID, Role role) throws RoleNotFoundException, RoleAlreadyExistsException, ConsoleException, SessionTimeOutException;

  /**
   * 
   * @param anItemSelection
   * @param anItemFilter
   * @return
   * @throws RoleNotFoundException
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  ItemUpdates<Role> removeRoles(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter) throws RoleNotFoundException, ConsoleException, SessionTimeOutException;

  /*
   * User Metadatas.
   */
  ItemUpdates<UserMetadataItem> addUserMetadata(UserMetadataItem anItem, SimpleFilter aFilter) throws UserMetadataAlreadyExistsException, ConsoleException, ConsoleSecurityException,
      SessionTimeOutException;

  ItemUpdates<UserMetadataItem> removeUserMetadatas(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter) throws UserMetadataNotFoundException, ConsoleException,
      ConsoleSecurityException, SessionTimeOutException;

  ItemUpdates<UserMetadataItem> getAllUserMetadatas(SimpleFilter anItemFilter) throws ConsoleException, ConsoleSecurityException, SessionTimeOutException;

  List<UserMetadataItem> getAllUserMetadatas() throws ConsoleException, ConsoleSecurityException, SessionTimeOutException;

  UserMetadataItem getUserMetadata(BonitaUUID anItemUUID, SimpleFilter aFilter) throws UserMetadataNotFoundException, ConsoleException, ConsoleSecurityException, SessionTimeOutException;

  List<UserMetadataItem> getUserMetadatas(List<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws UserMetadataNotFoundException, ConsoleException, ConsoleSecurityException,
      SessionTimeOutException;

  UserMetadataItem updateUserMetadata(BonitaUUID anItemId, UserMetadataItem anItem) throws UserMetadataNotFoundException, UserMetadataAlreadyExistsException, ConsoleException,
      ConsoleSecurityException, SessionTimeOutException;

  /*
   * Memberships.
   */
  MembershipItem getMembershipItem(BonitaUUID anItemUUID, SimpleFilter aFilter) throws MembershipNotFoundException, ConsoleException, ConsoleSecurityException, SessionTimeOutException;

  List<MembershipItem> getMembershipItems(List<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws MembershipNotFoundException, ConsoleException, ConsoleSecurityException,
      SessionTimeOutException;
}
