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
package org.bonitasoft.console.server.identity;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.identity.Group;
import org.bonitasoft.console.client.identity.GroupFilter;
import org.bonitasoft.console.client.identity.IdentityConfiguration;
import org.bonitasoft.console.client.identity.IdentityService;
import org.bonitasoft.console.client.identity.MembershipItem;
import org.bonitasoft.console.client.identity.Role;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.identity.UserMetadataItem;
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
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.server.login.SessionManager;
import org.bonitasoft.console.server.users.UserDataStore;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Anthony Birembaut, Nicolas Chabanoles
 * 
 */
public class IdentityServlet extends RemoteServiceServlet implements IdentityService {

  /**
   * UID
   */
  private static final long serialVersionUID = 5230161755229778494L;

  private static final Logger LOGGER = Logger.getLogger(IdentityServlet.class.getName());

  private static final UserDataStore userDataStore = UserDataStore.getInstance();

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#addRole(org.bonitasoft
   * .console.client.identity.Role)
   */
  public ItemUpdates<Role> addRole(Role aRole, SimpleFilter anItemFilter) throws RoleAlreadyExistsException, ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = SessionManager.login(theRequest);
    try {
      return IdentityDataStore.getInstance().addRole(aRole, anItemFilter);
    } catch (RoleAlreadyExistsException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, "a role named " + aRole.getName() + " already exists");
      }
      throw e;
    } catch (Throwable e) {
      String errorMessage = "Error while adding role " + aRole.getName();
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, errorMessage, e);
      }
      throw new ConsoleException(errorMessage, e);
    } finally {
      SessionManager.logout(theLoginContext);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#addUser(org.bonitasoft
   * .console.client.identity.User)
   */
  public ItemUpdates<User> addUser(User aUser, UserFilter aFilter) throws UserAlreadyExistsException, ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = SessionManager.login(theRequest);
    try {
      return IdentityDataStore.getInstance().addUser(aUser, aFilter);

    } catch (org.ow2.bonita.facade.exception.UserAlreadyExistsException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, "a user with username " + aUser.getUsername() + " already exists");
      }
      throw new UserAlreadyExistsException();
    } catch (Throwable e) {
      String errorMessage = "Error while adding user " + aUser.getUsername();
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, errorMessage, e);
      }
      throw new ConsoleException(errorMessage, e);
    } finally {
      SessionManager.logout(theLoginContext);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.identity.IdentityService#getUsers()
   */
  public ItemUpdates<User> getUsers(UserFilter aFilter) throws ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = SessionManager.login(theRequest);
    try {
      return IdentityDataStore.getInstance().getUsers(aFilter);
    } catch (Throwable e) {
      String errorMessage = "Error while retrieving the users";
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, errorMessage, e);
      }
      throw new ConsoleException(errorMessage, e);
    } finally {
      SessionManager.logout(theLoginContext);
    }
  }

  public Role updateRole(BonitaUUID aRoleUUID, Role aRole) throws RoleNotFoundException, RoleAlreadyExistsException, ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = SessionManager.login(theRequest);
    try {
      return IdentityDataStore.getInstance().updateRole(aRoleUUID, aRole);
    } catch (RoleAlreadyExistsException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, "a role named " + aRole.getName() + " already exists");
      }
      throw e;
    } catch (RoleNotFoundException e) {
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, "role " + aRoleUUID.getValue() + " unknown.");
      }
      throw e;
    } catch (Throwable e) {
      String errorMessage = "Error while updating role " + aRoleUUID.getValue();
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, errorMessage, e);
      }
      throw new ConsoleException(errorMessage, e);
    } finally {
      SessionManager.logout(theLoginContext);
    }
  }

  public User updateUser(UserUUID aUserUUID, User aUser) throws UserNotFoundException, UserAlreadyExistsException, ConsoleException, SessionTimeOutException, UserMetadataNotFoundException,
      RoleNotFoundException, GroupNotFoundException, MembershipNotFoundException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = SessionManager.login(theRequest);
    try {
      return IdentityDataStore.getInstance().updateUser(aUserUUID, aUser);

    } catch (UserAlreadyExistsException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, "a user with username " + aUser.getUsername() + " already exists");
      }
      throw e;
    } catch (UserNotFoundException e) {
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, "user " + aUserUUID.getValue() + " unknown.");
      }
      throw e;
    } catch (UserMetadataNotFoundException e) {
      e.printStackTrace();
      throw e;
    } catch (RoleNotFoundException e) {
      e.printStackTrace();
      throw e;
    } catch (GroupNotFoundException e) {
      e.printStackTrace();
      throw e;
    } catch (MembershipNotFoundException e) {
      e.printStackTrace();
      throw e;
    } catch (Throwable e) {
      String errorMessage = "Error while updating user " + aUserUUID.getValue();
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, errorMessage, e);
      }
      throw new ConsoleException(errorMessage, e);
    } finally {
      SessionManager.logout(theLoginContext);
    }
  }

  public ItemUpdates<User> removeUsers(Collection<UserUUID> anItemSelection, UserFilter aFilter) throws UserNotFoundException, ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = SessionManager.login(theRequest);
    ItemUpdates<User> theResult;
    try {
      theResult = IdentityDataStore.getInstance().deleteUsers(anItemSelection, aFilter);
    } catch (UserNotFoundException e) {
      e.printStackTrace();
      throw e;
    } catch (Throwable e) {
      String errorMessage = "Error while removing users.";
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, errorMessage, e);
      }
      throw new ConsoleException(errorMessage, e);
    } finally {
      SessionManager.logout(theLoginContext);
    }
    return theResult;

  }

  public IdentityConfiguration getIdentityConfiguration() throws ConsoleException, SessionTimeOutException {

    LoginContext theLoginContext = null;
    try {
      HttpServletRequest theRequest = this.getThreadLocalRequest();
      theLoginContext = SessionManager.login(theRequest);
      return userDataStore.getIdentityConfiguration();
    } catch (SessionTimeOutException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ConsoleException("Unable to get the reporting configuration.", e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
  }

  public void updateConfiguration(IdentityConfiguration aNewConfiguration) throws ConsoleException, SessionTimeOutException {
    LoginContext theLoginContext = null;
    try {
      HttpServletRequest theRequest = this.getThreadLocalRequest();
      theLoginContext = SessionManager.login(theRequest);
      userDataStore.updateConfiguration(aNewConfiguration);

    } catch (SessionTimeOutException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
      throw e;
    } catch (Throwable t) {
      String theErrorMessage = "Unable to update the configuration.";
      LOGGER.log(Level.SEVERE, theErrorMessage, t);
      throw new ConsoleException(theErrorMessage, t);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
  }

  public ItemUpdates<Group> addGroup(final Group aGroup, final GroupFilter anItemFilter) throws ConsoleException, SessionTimeOutException, GroupAlreadyExistsException, GroupNotFoundException {

    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<Group> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " adding new group of users: " + aGroup.getName() + "'.");
      }
      theResult = IdentityDataStore.getInstance().addGroup(theUserProfile, aGroup, anItemFilter);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, aGroup.getName() + " added successfully.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (GroupAlreadyExistsException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (GroupNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  public ItemUpdates<Group> getAllGroups(final GroupFilter anItemFilter) throws ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<Group> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);

      theResult = IdentityDataStore.getInstance().getAllGroups(theUserProfile, anItemFilter);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, (theResult == null ? 0 : theResult.getNbOfItems()) + " group(s) found.'");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;

  }

  public ItemUpdates<Group> removeGroups(final Collection<BonitaUUID> anItemsSelection, final GroupFilter anItemFilter) throws ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<Group> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " removing " + anItemsSelection.size() + "groups of users.");
      }
      theResult = IdentityDataStore.getInstance().removeGroups(theUserProfile, anItemsSelection, anItemFilter);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, anItemsSelection.size() + " groups were removed successfully.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;

  }

  public Group updateGroup(final BonitaUUID aGroupUuid, final Group aGroup) throws ConsoleException, SessionTimeOutException, GroupAlreadyExistsException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    Group theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " updating group of users: " + aGroup.getName() + ".");
      }
      theResult = IdentityDataStore.getInstance().updateGroup(theUserProfile, aGroupUuid, aGroup);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, aGroup.getName() + " updated successfully.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (GroupAlreadyExistsException e) {
        LOGGER.severe(e.getMessage());
        throw e;
      } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;

  }

  public ItemUpdates<Role> deleteRoles(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter) throws SessionTimeOutException, ConsoleException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<Role> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " deleting " + anItemSelection.size() + "user roles.");
      }
      theResult = IdentityDataStore.getInstance().deleteRoles(anItemSelection, anItemFilter);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Roles deleted successfully.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  public ItemUpdates<Role> getAllRoles(SimpleFilter anItemFilter) throws SessionTimeOutException, ConsoleException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<Role> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " listing roles.");
      }
      theResult = IdentityDataStore.getInstance().getAllRoles(anItemFilter);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theResult.getNbOfItems() + " roles found.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  public Role getRole(BonitaUUID anItemUUID, SimpleFilter aFilter) throws SessionTimeOutException, ConsoleException, RoleNotFoundException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    Role theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);

      theResult = IdentityDataStore.getInstance().getRole(theUserProfile, anItemUUID, aFilter);

    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (RoleNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  public List<Role> getRoles(List<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws RoleNotFoundException, SessionTimeOutException, ConsoleException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    List<Role> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);

      theResult = IdentityDataStore.getInstance().getRoles(theUserProfile, anItemSelection, aFilter);

    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (RoleNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  public ItemUpdates<Role> removeRoles(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter) throws RoleNotFoundException, ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<Role> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " deleting roles...");
      }
      theResult = IdentityDataStore.getInstance().deleteRoles(anItemSelection, anItemFilter);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, anItemSelection.size() + " roles were deleted successfully.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#getGroup(org.bonitasoft
   * .console.client.BonitaUUID, org.bonitasoft.console.client.SimpleFilter)
   */
  public Group getGroup(BonitaUUID anItemUUID, GroupFilter aFilter) throws SessionTimeOutException, ConsoleException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    Group theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);

      theResult = IdentityDataStore.getInstance().getGroup(theUserProfile, anItemUUID, aFilter);

    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#getGroups(java.util
   * .List, org.bonitasoft.console.client.SimpleFilter)
   */
  public List<Group> getGroups(List<BonitaUUID> anItemSelection, GroupFilter aFilter) throws SessionTimeOutException, ConsoleException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    List<Group> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);

      theResult = IdentityDataStore.getInstance().getGroups(theUserProfile, anItemSelection, aFilter);

    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#getUser(org.bonitasoft
   * .console.client.BonitaUUID, org.bonitasoft.console.client.SimpleFilter)
   */
  public User getUser(UserUUID anItemUUID, UserFilter aFilter) throws SessionTimeOutException, ConsoleException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    User theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);

      theResult = IdentityDataStore.getInstance().getUser(theUserProfile, anItemUUID, aFilter);

    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#getUsers(java.util
   * .List, org.bonitasoft.console.client.SimpleFilter)
   */
  public List<User> getUsers(List<UserUUID> anItemSelection, UserFilter aFilter) throws SessionTimeOutException, ConsoleException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    List<User> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);

      theResult = IdentityDataStore.getInstance().getUsers(theUserProfile, anItemSelection, aFilter);

    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#addUserMetadata(
   * org.bonitasoft.console.client.identity.UserMetadataItem,
   * org.bonitasoft.console.client.SimpleFilter)
   */
  public ItemUpdates<UserMetadataItem> addUserMetadata(UserMetadataItem anItem, SimpleFilter aFilter) throws UserMetadataAlreadyExistsException, ConsoleException, ConsoleSecurityException,
      SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<UserMetadataItem> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " adding new user metadata: " + anItem.getName() + "'.");
      }
      theResult = IdentityDataStore.getInstance().addUserMetadata(theUserProfile, anItem, aFilter);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, anItem.getName() + " added successfully.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (UserMetadataAlreadyExistsException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#getAllUserMetadatas
   * (org.bonitasoft.console.client.SimpleFilter)
   */
  public ItemUpdates<UserMetadataItem> getAllUserMetadatas(SimpleFilter anItemFilter) throws ConsoleException, ConsoleSecurityException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<UserMetadataItem> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " listing user metadata.");
      }
      theResult = IdentityDataStore.getInstance().getAllUserMetadatas(anItemFilter);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  public List<UserMetadataItem> getAllUserMetadatas() throws ConsoleException, ConsoleSecurityException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    List<UserMetadataItem> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " listing ALL user metadata.");
      }
      theResult = IdentityDataStore.getInstance().getAllUserMetadatas();
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#getUserMetadata(
   * org.bonitasoft.console.client.BonitaUUID,
   * org.bonitasoft.console.client.SimpleFilter)
   */
  public UserMetadataItem getUserMetadata(BonitaUUID anItemUUID, SimpleFilter aFilter) throws UserMetadataNotFoundException, ConsoleException, ConsoleSecurityException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    UserMetadataItem theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      theResult = IdentityDataStore.getInstance().getUserMetadata(anItemUUID, aFilter);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#getUserMetadatas
   * (java.util.List, org.bonitasoft.console.client.SimpleFilter)
   */
  public List<UserMetadataItem> getUserMetadatas(List<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws UserMetadataNotFoundException, ConsoleException, ConsoleSecurityException,
      SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    List<UserMetadataItem> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = IdentityDataStore.getInstance().getUserMetadatas(theUserProfile, anItemSelection, aFilter);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#removeUserMetadatas
   * (java.util.Collection, org.bonitasoft.console.client.SimpleFilter)
   */
  public ItemUpdates<UserMetadataItem> removeUserMetadatas(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter) throws UserMetadataNotFoundException, ConsoleException,
      ConsoleSecurityException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<UserMetadataItem> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " removing " + anItemSelection.size() + " user metadata ...");
      }
      theResult = IdentityDataStore.getInstance().removeUserMetadatas(theUserProfile, anItemSelection, anItemFilter);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Metadatas removed successfully.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (UserMetadataNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.identity.IdentityService#updateUserMetadata
   * (org.bonitasoft.console.client.BonitaUUID,
   * org.bonitasoft.console.client.identity.UserMetadataItem)
   */
  public UserMetadataItem updateUserMetadata(BonitaUUID anItemId, UserMetadataItem anItem) throws UserMetadataNotFoundException, UserMetadataAlreadyExistsException, ConsoleException,
      ConsoleSecurityException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    UserMetadataItem theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " updating a user metadata: " + anItemId.getValue() + "'.");
      }
      theResult = IdentityDataStore.getInstance().updateUserMetadata(theUserProfile, anItemId, anItem);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, anItem.getName() + " updated successfully.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (UserMetadataAlreadyExistsException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (UserMetadataNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.identity.IdentityService#getMembershipItem(org.bonitasoft.console.client.BonitaUUID, org.bonitasoft.console.client.SimpleFilter)
   */
  public MembershipItem getMembershipItem(BonitaUUID anItemUUID, SimpleFilter aFilter) throws MembershipNotFoundException, ConsoleException, ConsoleSecurityException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    MembershipItem theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = IdentityDataStore.getInstance().getMembership(theUserProfile, anItemUUID, aFilter);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (MembershipNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.identity.IdentityService#getMembershipItems(java.util.List, org.bonitasoft.console.client.SimpleFilter)
   */
  public List<MembershipItem> getMembershipItems(List<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws MembershipNotFoundException, ConsoleException, ConsoleSecurityException,
      SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    List<MembershipItem> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = IdentityDataStore.getInstance().getMemberships(theUserProfile, anItemSelection, aFilter);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (MembershipNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }
}
