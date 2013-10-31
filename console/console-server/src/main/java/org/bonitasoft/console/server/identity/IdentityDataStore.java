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
package org.bonitasoft.console.server.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.identity.Group;
import org.bonitasoft.console.client.identity.GroupFilter;
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
import org.ow2.bonita.facade.CommandAPI;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.exception.MetadataAlreadyExistsException;
import org.ow2.bonita.facade.exception.MetadataNotFoundException;
import org.ow2.bonita.facade.identity.ContactInfo;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.ow2.bonita.facade.paging.GroupCriterion;
import org.ow2.bonita.facade.paging.UserCriterion;
import org.ow2.bonita.facade.runtime.command.WebAddUserCommand;
import org.ow2.bonita.facade.runtime.command.WebSearchGroups;
import org.ow2.bonita.facade.runtime.command.WebSearchResult;
import org.ow2.bonita.facade.runtime.command.WebSearchRoles;
import org.ow2.bonita.facade.runtime.command.WebSearchUsers;
import org.ow2.bonita.facade.runtime.command.WebUpdateUserCommand;
import org.ow2.bonita.search.SearchQueryBuilder;
import org.ow2.bonita.search.index.ContactInfoIndex;
import org.ow2.bonita.search.index.GroupIndex;
import org.ow2.bonita.search.index.RoleIndex;
import org.ow2.bonita.search.index.UserIndex;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class IdentityDataStore {

    private static IdentityDataStore instance;

    private static final String PASSWORD_SUBSTITUTE = "*&bonita&*";

    /**
     * Get the unique instance of UserDataStore.
     * 
     * @return
     */
    public static synchronized IdentityDataStore getInstance() {
        if (instance == null) {
            instance = new IdentityDataStore();
        }

        return instance;
    }

    /**
     * Default constructor.
     */
    protected IdentityDataStore() {
        super();
    }

    public ItemUpdates<Role> addRole(Role aRole, SimpleFilter anItemFilter) throws RoleAlreadyExistsException, ConsoleException {
        try {
            AccessorUtil.getIdentityAPI().addRole(aRole.getName(), aRole.getLabel(), aRole.getDescription());
            return getAllRoles(anItemFilter);
        } catch (org.ow2.bonita.facade.exception.RoleAlreadyExistsException e) {
            e.printStackTrace();
            throw new RoleAlreadyExistsException(e.getName());
        }

    }

    /**
     * @param aUser
     * @param aFilter
     * @return
     * @throws Exception
     */
    public ItemUpdates<User> addUser(User aUser, UserFilter aFilter) throws Exception {
        if (aUser == null) {
            throw new IllegalArgumentException();
        }
        final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();

        final String thePassword;
        if (aUser.getPassword() == null) {
            // ask for an empty password
            thePassword = "";
        } else {
            // ask for a password
            thePassword = aUser.getPassword();
        }

        final Map<String, List<String>> theUserMemberships = new HashMap<String, List<String>>();
        String theGroupID;
        List<String> theListOfRoles;
        for (MembershipItem theMembership : aUser.getMembership()) {
            theGroupID = theMembership.getGroup().getUUID().getValue();
            if (!theUserMemberships.containsKey(theGroupID)) {
                theListOfRoles = new ArrayList<String>();
            } else {
                theListOfRoles = theUserMemberships.get(theGroupID);
            }
            theListOfRoles.add(theMembership.getRole().getUUID().getValue());
            theUserMemberships.put(theGroupID, theListOfRoles);
        }
        final String theManagerUUID;
        if(aUser.getManagerUuid()!=null) {
            theManagerUUID = aUser.getManagerUuid().getValue(); 
        } else {
            theManagerUUID = null;
        }
        final String theDelegateUUID;
        if(aUser.getDelegateUuid()!=null) {
            theDelegateUUID = aUser.getDelegateUuid().getValue(); 
        } else {
            theDelegateUUID = null;
        }
        theCommandAPI.execute(new WebAddUserCommand(aUser.getUsername(), aUser.getFirstName(), aUser.getLastName(), thePassword, aUser.getTitle(), aUser.getJobTitle(), theManagerUUID, theDelegateUUID, aUser.getPersonalContactInfo(), aUser.getProfessionalContactInfo(),theUserMemberships, aUser.getMetadatas()));

        return getUsers(aFilter);
    }

    public Role getRole(UserProfile aUserProfile, BonitaUUID aRole, SimpleFilter aFilter) throws ConsoleException, RoleNotFoundException {
        try {
            org.ow2.bonita.facade.identity.Role theRole;
            theRole = AccessorUtil.getIdentityAPI().getRoleByUUID(aRole.getValue());
            return buildRole(theRole);
        } catch (org.ow2.bonita.facade.exception.RoleNotFoundException e) {
            e.printStackTrace();
            throw new RoleNotFoundException(e.getName());
        }

    }

    /**
     * @param aFilter
     * @param aUserProfile
     * @param aUsername
     * @return
     * @throws UserNotFoundException
     * @throws ConsoleException
     * @throws Exception
     */
    public User getUser(UserProfile aUserProfile, UserUUID aUserUUID, UserFilter aFilter) throws UserNotFoundException, ConsoleException {
        final IdentityAPI identityAPI = AccessorUtil.getIdentityAPI();
        org.ow2.bonita.facade.identity.User user;
        try {
            user = identityAPI.getUserByUUID(aUserUUID.getValue());
            return buildUser(user);
        } catch (org.ow2.bonita.facade.exception.UserNotFoundException e) {
            e.printStackTrace();
            throw new UserNotFoundException();
        }

    }

    /**
     * @param aUserName
     * @return
     * @throws UserNotFoundException
     * @throws ConsoleException
     */
    public User findUserByUserName(String aUserName) throws UserNotFoundException, ConsoleException {
        final IdentityAPI identityAPI = AccessorUtil.getIdentityAPI();
        org.ow2.bonita.facade.identity.User user;
        try {
            user = identityAPI.findUserByUserName(aUserName);
            return buildUser(user);
        } catch (org.ow2.bonita.facade.exception.UserNotFoundException e) {
            throw new UserNotFoundException(aUserName);
        }
    }

    /**
     * @return
     * @throws GroupNotFoundException 
     * @throws RoleNotFoundException 
     * @throws Exception
     */
    public ItemUpdates<User> getUsers(UserFilter aFilter) throws ConsoleException, GroupNotFoundException, RoleNotFoundException {
        if (aFilter.isActive()) {
            final SearchQueryBuilder theSearchQuery = buildSearchUserQuery(aFilter.getSearchPattern());
            final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();

            // Consider the Journal only
            WebSearchResult<org.ow2.bonita.facade.identity.User> theSearchResult;
            try {
                theSearchResult = theCommandAPI.execute(new WebSearchUsers(theSearchQuery, aFilter.getStartingIndex(), aFilter.getMaxElementCount(), false));

                final List<org.ow2.bonita.facade.identity.User> theFoundItems = theSearchResult.getSearchResults();
                final int theTotalItemsAvailable = theSearchResult.getSearchMatchingElementsCount();
                final List<User> theUserList = new ArrayList<User>();
                if (theFoundItems != null) {
                    for (org.ow2.bonita.facade.identity.User user : theFoundItems) {
                        theUserList.add(buildUser(user));
                    }
                }
                return new ItemUpdates<User>(theUserList, theTotalItemsAvailable);
            } catch (Exception e) {
                throw new ConsoleException("User search error.", e);
            }
        } else {
            if (aFilter.getGroupUUID() != null && aFilter.getRoleUUID() != null) {
                // List members of a membership
                throw new ConsoleException("Not yet implemented!", null);
            } else if (aFilter.getGroupUUID() != null) {
                // List members of group
                final String theGroupUUID = aFilter.getGroupUUID().getValue();
                try {
                    final int theNumberOfUsers = AccessorUtil.getIdentityAPI().getNumberOfUsersInGroup(theGroupUUID);
                    if (theNumberOfUsers > aFilter.getStartingIndex()) {
                        final List<org.ow2.bonita.facade.identity.User> theGroups = AccessorUtil.getIdentityAPI().getUsersInGroup(theGroupUUID, aFilter.getStartingIndex(), aFilter.getMaxElementCount(), UserCriterion.DEFAULT);
                        return new ItemUpdates<User>(buildUsers(theGroups), theNumberOfUsers);
                    } else {
                        return new ItemUpdates<User>(new ArrayList<User>(), 0);
                    }
                } catch (org.ow2.bonita.facade.exception.GroupNotFoundException e) {
                   throw new GroupNotFoundException(theGroupUUID);
                }
                
            } else if (aFilter.getRoleUUID() != null) {
             // List members of role
                final String theRoleUUID = aFilter.getRoleUUID().getValue();
                try {
                    final int theNumberOfUsers = AccessorUtil.getIdentityAPI().getNumberOfUsersInRole(theRoleUUID);
                    if (theNumberOfUsers > aFilter.getStartingIndex()) {
                        final List<org.ow2.bonita.facade.identity.User> theGroups = AccessorUtil.getIdentityAPI().getUsersInRole(theRoleUUID, aFilter.getStartingIndex(), aFilter.getMaxElementCount(), UserCriterion.DEFAULT);
                        return new ItemUpdates<User>(buildUsers(theGroups), theNumberOfUsers);
                    } else {
                        return new ItemUpdates<User>(new ArrayList<User>(), 0);
                    }
                } catch (org.ow2.bonita.facade.exception.RoleNotFoundException e) {
                   throw new RoleNotFoundException(theRoleUUID);
                }
            } else {
                final int theNumberOfUsers = AccessorUtil.getIdentityAPI().getNumberOfUsers();
                if (theNumberOfUsers > aFilter.getStartingIndex()) {
                    final List<org.ow2.bonita.facade.identity.User> theGroups = AccessorUtil.getIdentityAPI().getUsers(aFilter.getStartingIndex(), aFilter.getMaxElementCount());
                    return new ItemUpdates<User>(buildUsers(theGroups), theNumberOfUsers);
                } else {
                    return new ItemUpdates<User>(new ArrayList<User>(), 0);
                }
            } 
        }
    }

    /**
     * @param anOldName
     * @param aRole
     * @throws RoleAlreadyExistsException
     * @throws RoleNotFoundException
     */
    public Role updateRole(BonitaUUID aRoleUUID, Role aRole) throws RoleNotFoundException, RoleAlreadyExistsException {

        try {
            org.ow2.bonita.facade.identity.Role role;
            role = AccessorUtil.getIdentityAPI().updateRoleByUUID(aRoleUUID.getValue(), aRole.getName(), aRole.getLabel(), aRole.getDescription());
            return new Role(String.valueOf(role.getUUID()), role.getName(), role.getLabel(), role.getDescription());
        } catch (org.ow2.bonita.facade.exception.RoleNotFoundException e) {
            e.printStackTrace();
            throw new RoleNotFoundException(e.getName());
        } catch (org.ow2.bonita.facade.exception.RoleAlreadyExistsException e) {
            e.printStackTrace();
            throw new RoleAlreadyExistsException(e.getName());
        }

    }

    /**
     * @param aUser
     * @throws UserNotFoundException
     * @throws UserAlreadyExistsException
     * @throws MetadataNotFoundException
     * @throws RoleNotFoundException
     * @throws GroupNotFoundException
     * @throws MembershipNotFoundException
     * @throws ConsoleException
     * @throws Exception
     */
    public User updateUser(BonitaUUID aUserUUID, User aUser) throws UserNotFoundException, UserAlreadyExistsException, UserMetadataNotFoundException, RoleNotFoundException, GroupNotFoundException,
            MembershipNotFoundException, ConsoleException {
        final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();

        // Update personal data of the user.

        String thePassword = null;
        boolean needUpdatePassword = false;
        if (aUser.getPassword() == null || aUser.getPassword().length() == 0) {
            needUpdatePassword = true;
        } else if (!aUser.getPassword().equals(PASSWORD_SUBSTITUTE)) {
            // ask for a new password
            thePassword = aUser.getPassword();
            needUpdatePassword = true;
        }
        final Map<String, Collection<String>> theUserMemberships = new HashMap<String, Collection<String>>();
        for (MembershipItem theMembershipItem : aUser.getMembership()) {
            if (!theUserMemberships.containsKey(theMembershipItem.getGroup().getUUID().getValue())) {
                theUserMemberships.put(theMembershipItem.getGroup().getUUID().getValue(), new HashSet<String>());
            }
            theUserMemberships.get(theMembershipItem.getGroup().getUUID().getValue()).add(theMembershipItem.getRole().getUUID().getValue());
        }
        final String theManagerUuid;
        if (aUser.getManagerUuid() != null) {
            theManagerUuid = aUser.getManagerUuid().getValue();
        } else {
            theManagerUuid = null;
        }
        final String theDelegateUuid;
        if (aUser.getDelegateUuid() != null) {
            theDelegateUuid = aUser.getDelegateUuid().getValue();
        } else {
            theDelegateUuid = null;
        }

        org.ow2.bonita.facade.identity.User user;
        try {
            user = theCommandAPI.execute(new WebUpdateUserCommand(aUserUUID.getValue(), aUser.getUsername(), aUser.getFirstName(), aUser.getLastName(), needUpdatePassword, thePassword, aUser
                    .getTitle(), aUser.getJobTitle(), theManagerUuid, theDelegateUuid, aUser.getPersonalContactInfo(), aUser.getProfessionalContactInfo(), theUserMemberships, aUser.getMetadatas()));
            return buildUser(user);
        } catch (org.ow2.bonita.facade.exception.UserNotFoundException e) {
            e.printStackTrace();
            throw new UserNotFoundException(e.getUserId());
        } catch (org.ow2.bonita.facade.exception.UserAlreadyExistsException e) {
            e.printStackTrace();
            throw new UserAlreadyExistsException(e.getUsername());
        } catch (org.ow2.bonita.facade.exception.MetadataNotFoundException e) {
            e.printStackTrace();
            throw new UserMetadataNotFoundException(e.getName());
        } catch (org.ow2.bonita.facade.exception.RoleNotFoundException e) {
            e.printStackTrace();
            throw new RoleNotFoundException(e.getName());
        } catch (org.ow2.bonita.facade.exception.GroupNotFoundException e) {
            e.printStackTrace();
            throw new GroupNotFoundException(e.getName());
        } catch (org.ow2.bonita.facade.exception.MembershipNotFoundException e) {
            e.printStackTrace();
            throw new MembershipNotFoundException(e.getName());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConsoleException(e.getMessage(), e);
        }

    }

    protected User buildUser(org.ow2.bonita.facade.identity.User anUser) throws ConsoleException {

        try {
            // Do not send the password to the client side.
            final String thePassword = PASSWORD_SUBSTITUTE;
            User userToReturn = new User(anUser.getUUID(), anUser.getUsername(), thePassword, anUser.getFirstName(), anUser.getLastName(), anUser.getTitle(), anUser.getJobTitle(), anUser
                    .getManagerUUID(), anUser.getDelegeeUUID());
            ContactInfo theSource = anUser.getProfessionalContactInfo();
            Map<String, String> theContactInfo = new HashMap<String, String>();
            if (theSource != null) {
                theContactInfo.put(User.ADDRESS_KEY, theSource.getAddress());
                theContactInfo.put(User.BUILDING_KEY, theSource.getBuilding());
                theContactInfo.put(User.CITY_KEY, theSource.getCity());
                theContactInfo.put(User.COUNTRY_KEY, theSource.getCountry());
                theContactInfo.put(User.EMAIL_KEY, theSource.getEmail());
                theContactInfo.put(User.FAX_NUMBER_KEY, theSource.getFaxNumber());
                theContactInfo.put(User.MOBILE_NUMBER_KEY, theSource.getMobileNumber());
                theContactInfo.put(User.PHONE_NUMBER_KEY, theSource.getPhoneNumber());
                theContactInfo.put(User.ROOM_KEY, theSource.getRoom());
                theContactInfo.put(User.STATE_KEY, theSource.getState());
                theContactInfo.put(User.WEBSITE_KEY, theSource.getWebsite());
                theContactInfo.put(User.ZIPCODE_KEY, theSource.getZipCode());
            }
            userToReturn.setProfessionalContactInfo(theContactInfo);

            theSource = anUser.getPersonalContactInfo();
            theContactInfo = new HashMap<String, String>();
            if (theSource != null) {
                theContactInfo.put(User.ADDRESS_KEY, theSource.getAddress());
                theContactInfo.put(User.BUILDING_KEY, theSource.getBuilding());
                theContactInfo.put(User.CITY_KEY, theSource.getCity());
                theContactInfo.put(User.COUNTRY_KEY, theSource.getCountry());
                theContactInfo.put(User.EMAIL_KEY, theSource.getEmail());
                theContactInfo.put(User.FAX_NUMBER_KEY, theSource.getFaxNumber());
                theContactInfo.put(User.MOBILE_NUMBER_KEY, theSource.getMobileNumber());
                theContactInfo.put(User.PHONE_NUMBER_KEY, theSource.getPhoneNumber());
                theContactInfo.put(User.ROOM_KEY, theSource.getRoom());
                theContactInfo.put(User.STATE_KEY, theSource.getState());
                theContactInfo.put(User.WEBSITE_KEY, theSource.getWebsite());
                theContactInfo.put(User.ZIPCODE_KEY, theSource.getZipCode());
            }
            userToReturn.setPersonnalContactInfo(theContactInfo);

            final Set<Membership> theMembershipsSource = anUser.getMemberships();
            final Set<MembershipItem> theUserMemberships = new HashSet<MembershipItem>();
            for (Membership theMembership : theMembershipsSource) {
                theUserMemberships.add(new MembershipItem(new BonitaUUID(theMembership.getUUID()), buildGroup(theMembership.getGroup()), buildRole(theMembership.getRole())));
            }
            userToReturn.setMembership(theUserMemberships);

            final Map<ProfileMetadata, String> theMetadataSource = anUser.getMetadata();
            final HashMap<String, String> theUserMetadata = new HashMap<String, String>();
            for (Entry<ProfileMetadata, String> theMetaEntry : theMetadataSource.entrySet()) {
                theUserMetadata.put(theMetaEntry.getKey().getName(), theMetaEntry.getValue());
            }
            userToReturn.setMetadata(theUserMetadata);
            return userToReturn;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConsoleException("Error while building user item.", e);
        }
    }

    public ItemUpdates<Group> addGroup(final UserProfile aUserProfile, final Group aGroup, final GroupFilter anItemFilter) throws GroupAlreadyExistsException, ConsoleException,
            ConsoleSecurityException, GroupNotFoundException {

        try {
            final Group theParentGroup = aGroup.getParentGroup();
            final String theParentGroupId;
            if (theParentGroup != null) {
                theParentGroupId = theParentGroup.getUUID().getValue();
            } else {
                theParentGroupId = null;
            }
            AccessorUtil.getIdentityAPI().addGroup(aGroup.getName(), aGroup.getLabel(), aGroup.getDescription(), theParentGroupId);

            return getAllGroups(aUserProfile, anItemFilter);
        } catch (org.ow2.bonita.facade.exception.GroupAlreadyExistsException e) {
            e.printStackTrace();
            throw new GroupAlreadyExistsException(e.getName());
        } catch (org.ow2.bonita.facade.exception.GroupNotFoundException e) {
            e.printStackTrace();
            throw new GroupNotFoundException(e.getName());
        }

    }

    /**
     * @param aUserProfile
     * @param aAnItemFilter
     * @return
     * @throws ConsoleException
     * @throws GroupNotFoundException
     */
    public ItemUpdates<Group> getAllGroups(UserProfile aUserProfile, GroupFilter anItemFilter) throws ConsoleException, ConsoleSecurityException, GroupNotFoundException {
        if (anItemFilter.isActive()) {
            final SearchQueryBuilder theSearchQuery = buildSearchGroupQuery(anItemFilter.getSearchPattern());
            final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();

            // Consider the Journal only
            WebSearchResult<org.ow2.bonita.facade.identity.Group> theSearchResult;
            try {
                theSearchResult = theCommandAPI.execute(new WebSearchGroups(theSearchQuery, anItemFilter.getStartingIndex(), anItemFilter.getMaxElementCount(), false));

                final List<org.ow2.bonita.facade.identity.Group> theFoundItems = theSearchResult.getSearchResults();
                final int theTotalItemsAvailable = theSearchResult.getSearchMatchingElementsCount();
                final List<Group> theItemList = new ArrayList<Group>();
                if (theFoundItems != null) {
                    for (org.ow2.bonita.facade.identity.Group theItem : theFoundItems) {
                        theItemList.add(buildGroup(theItem));
                    }
                }
                return new ItemUpdates<Group>(theItemList, theTotalItemsAvailable);
            } catch (Exception e) {
                throw new ConsoleException("Group search error.", e);
            }
        } else {
            if (anItemFilter.getParentGroupUUID() == null) {
                final int theNumberOfGroups = AccessorUtil.getIdentityAPI().getNumberOfGroups();
                if (theNumberOfGroups > anItemFilter.getStartingIndex()) {
                    final List<org.ow2.bonita.facade.identity.Group> theGroups = AccessorUtil.getIdentityAPI().getGroups(anItemFilter.getStartingIndex(), anItemFilter.getMaxElementCount());
                    return new ItemUpdates<Group>(buildGroups(theGroups), theNumberOfGroups);
                } else {
                    return new ItemUpdates<Group>(new ArrayList<Group>(), 0);
                }
            } else {
                // List children of a group
                final String theParentGroupUUID = anItemFilter.getParentGroupUUID().getValue();
                try {
                    final int theNumberOfGroups = AccessorUtil.getIdentityAPI().getNumberOfChildrenGroups(theParentGroupUUID);
                    if (theNumberOfGroups > anItemFilter.getStartingIndex()) {
                        final List<org.ow2.bonita.facade.identity.Group> theGroups = AccessorUtil.getIdentityAPI().getChildrenGroups(theParentGroupUUID, anItemFilter.getStartingIndex(),
                                anItemFilter.getMaxElementCount(), GroupCriterion.DEFAULT);
                        return new ItemUpdates<Group>(buildGroups(theGroups), theNumberOfGroups);
                    } else {
                        return new ItemUpdates<Group>(new ArrayList<Group>(), 0);
                    }
                } catch (org.ow2.bonita.facade.exception.GroupNotFoundException e) {
                    throw new GroupNotFoundException(theParentGroupUUID);
                }
            }
        }
    }

    /**
     * @param aGroups
     * @return
     */
    protected List<Group> buildGroups(List<org.ow2.bonita.facade.identity.Group> aListOfGroup) {
        ArrayList<Group> theResult = new ArrayList<Group>();
        Group theGroupItem;
        for (org.ow2.bonita.facade.identity.Group theSource : aListOfGroup) {
            theGroupItem = buildGroup(theSource);
            theResult.add(theGroupItem);
        }
        return theResult;
    }

    protected Group buildGroup(org.ow2.bonita.facade.identity.Group aSource) {
        final Group theGroupItem = new Group(aSource.getUUID());
        theGroupItem.setName(aSource.getName());
        theGroupItem.setLabel(aSource.getLabel());
        theGroupItem.setDescription(aSource.getDescription());
        if (aSource.getParentGroup() != null) {
            theGroupItem.setParentGroup(buildGroup(aSource.getParentGroup()));
        } else {
            theGroupItem.setParentGroup(null);
        }
        return theGroupItem;
    }

    /**
     * @param aUserProfile
     * @param aAnItemsSelection
     * @param aAnItemFilter
     * @return
     * @throws org.bonitasoft.console.client.identity.exceptions.GroupNotFoundException
     */
    public ItemUpdates<Group> removeGroups(UserProfile aUserProfile, Collection<BonitaUUID> anItemsSelection, GroupFilter anItemFilter) throws ConsoleException, ConsoleSecurityException,
            org.bonitasoft.console.client.identity.exceptions.GroupNotFoundException {
        final Collection<String> theGroups = new ArrayList<String>();
        for (BonitaUUID theBonitaUUID : anItemsSelection) {
            theGroups.add(theBonitaUUID.getValue());
        }

        try {
            AccessorUtil.getIdentityAPI().removeGroups(theGroups);
        } catch (org.ow2.bonita.facade.exception.GroupNotFoundException e) {
            e.printStackTrace();
            throw new GroupNotFoundException(e.getName());
        }
        return getAllGroups(aUserProfile, anItemFilter);
    }

    /**
     * @param aUserProfile
     * @param aGroupUuid
     *            .get
     * @param aGroup
     * @return
     * @throws GroupAlreadyExistsException
     */
    public Group updateGroup(UserProfile aUserProfile, BonitaUUID aGroupUuid, Group aGroup) throws GroupNotFoundException, ConsoleException, ConsoleSecurityException, GroupAlreadyExistsException {

        org.ow2.bonita.facade.identity.Group theGroup;
        try {
            String theParentGroupUUID;
            if (aGroup.getParentGroup() != null) {
                theParentGroupUUID = aGroup.getParentGroup().getUUID().getValue();
            } else {
                theParentGroupUUID = null;
            }
            theGroup = AccessorUtil.getIdentityAPI().updateGroupByUUID(aGroupUuid.getValue(), aGroup.getName(), aGroup.getLabel(), aGroup.getDescription(), theParentGroupUUID);
        } catch (org.ow2.bonita.facade.exception.GroupNotFoundException e) {
            e.printStackTrace();
            throw new GroupNotFoundException(e.getName());
        } catch (org.ow2.bonita.facade.exception.GroupAlreadyExistsException e) {
            e.printStackTrace();
            throw new GroupAlreadyExistsException(e.getName());
        }
        return buildGroup(theGroup);

    }

    /**
     * @param aAnItemSelection
     * @param aAnItemFilter
     * @return
     * @throws ConsoleException
     * @throws org.bonitasoft.console.client.identity.exceptions.RoleNotFoundException
     */
    public ItemUpdates<Role> deleteRoles(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter) throws ConsoleException,
            org.bonitasoft.console.client.identity.exceptions.RoleNotFoundException {
        final Collection<String> theItems = new ArrayList<String>();
        for (BonitaUUID theBonitaUUID : anItemSelection) {
            theItems.add(theBonitaUUID.getValue());
        }
        try {
            AccessorUtil.getIdentityAPI().removeRoles(theItems);
        } catch (org.ow2.bonita.facade.exception.RoleNotFoundException e) {
            e.printStackTrace();
            throw new RoleNotFoundException(e.getName());
        }
        return getAllRoles(anItemFilter);
    }

    public ItemUpdates<User> deleteUsers(Collection<UserUUID> anItemSelection, UserFilter anItemFilter) throws ConsoleException, org.bonitasoft.console.client.identity.exceptions.UserNotFoundException, GroupNotFoundException, RoleNotFoundException {
        final Collection<String> theItems = new ArrayList<String>();
        for (UserUUID theItemUUID : anItemSelection) {
            theItems.add(theItemUUID.getValue());
        }
        try {
            AccessorUtil.getIdentityAPI().removeUsers(theItems);
        } catch (org.ow2.bonita.facade.exception.UserNotFoundException e) {
            e.printStackTrace();
            throw new UserNotFoundException(e.getUserId());
        }
        return getUsers(anItemFilter);
    }

    /**
     * @param aUserProfile
     * @param aAnItemFilter
     * @return
     */
    public ItemUpdates<Role> getAllRoles(SimpleFilter anItemFilter) throws ConsoleException {
        if (anItemFilter.isActive()) {
            final SearchQueryBuilder theSearchQuery = buildSearchRoleQuery(anItemFilter.getSearchPattern());
            final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();

            // Consider the Journal only
            WebSearchResult<org.ow2.bonita.facade.identity.Role> theSearchResult;
            try {
                theSearchResult = theCommandAPI.execute(new WebSearchRoles(theSearchQuery, anItemFilter.getStartingIndex(), anItemFilter.getMaxElementCount(), false));

                final List<org.ow2.bonita.facade.identity.Role> theFoundItems = theSearchResult.getSearchResults();
                final int theTotalItemsAvailable = theSearchResult.getSearchMatchingElementsCount();
                final List<Role> theItemList = new ArrayList<Role>();
                if (theFoundItems != null) {
                    for (org.ow2.bonita.facade.identity.Role theItem : theFoundItems) {
                        theItemList.add(buildRole(theItem));
                    }
                }
                return new ItemUpdates<Role>(theItemList, theTotalItemsAvailable);
            } catch (Exception e) {
                throw new ConsoleException("Role search error.", e);
            }
        } else {
            final int theNumberOfRoles = AccessorUtil.getIdentityAPI().getNumberOfRoles();
            if (theNumberOfRoles > anItemFilter.getStartingIndex()) {
                final List<org.ow2.bonita.facade.identity.Role> theRoles = AccessorUtil.getIdentityAPI().getRoles(anItemFilter.getStartingIndex(), anItemFilter.getMaxElementCount());
                return new ItemUpdates<Role>(buildRoles(theRoles), theNumberOfRoles);
            } else {
                return new ItemUpdates<Role>(new ArrayList<Role>(), theNumberOfRoles);
            }
        }
    }

    /**
     * @param aRoles
     * @return
     */
    protected List<Role> buildRoles(List<org.ow2.bonita.facade.identity.Role> aRoles) {
        ArrayList<Role> theResult = new ArrayList<Role>();
        Role theRole;
        for (org.ow2.bonita.facade.identity.Role theSource : aRoles) {
            theRole = buildRole(theSource);
            if (theRole != null) {
                theResult.add(theRole);
            }
        }
        return theResult;
    }

    /**
     * @param aSource
     * @return
     */
    protected Role buildRole(org.ow2.bonita.facade.identity.Role aSource) {
        if (aSource == null) {
            return null;
        }
        String theLabel = aSource.getLabel();
        if (theLabel == null || theLabel.length() == 0) {
            theLabel = aSource.getName();
        }
        return new Role(aSource.getUUID(), aSource.getName(), theLabel, aSource.getDescription());
    }

    /**
     * @param aUserProfile
     * @param aAnItemSelection
     * @param aFilter
     * @return
     */
    public List<Role> getRoles(UserProfile aUserProfile, Collection<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws RoleNotFoundException, ConsoleException {
        try {
            Collection<String> theRoleIDs = new HashSet<String>();
            for (BonitaUUID theBonitaUUID : anItemSelection) {
                theRoleIDs.add(theBonitaUUID.getValue());
            }
            List<org.ow2.bonita.facade.identity.Role> theRoles = AccessorUtil.getIdentityAPI().getRolesByUUIDs(theRoleIDs);
            return buildRoles(theRoles);
        } catch (org.ow2.bonita.facade.exception.RoleNotFoundException e) {
            e.printStackTrace();
            throw new RoleNotFoundException(e.getName());
        }
    }

    /**
     * @param aUserProfile
     * @param aAnItemUUID
     * @param aFilter
     * @return
     * @throws GroupNotFoundException
     */
    public Group getGroup(UserProfile aUserProfile, BonitaUUID anItemUUID, SimpleFilter aFilter) throws GroupNotFoundException, ConsoleException {
        org.ow2.bonita.facade.identity.Group theGroup;
        try {
            theGroup = AccessorUtil.getIdentityAPI().getGroupByUUID(anItemUUID.getValue());
        } catch (org.ow2.bonita.facade.exception.GroupNotFoundException e) {
            e.printStackTrace();
            throw new GroupNotFoundException(e.getName());
        }
        return buildGroup(theGroup);
    }

    /**
     * @param aUserProfile
     * @param aAnItemSelection
     * @param aFilter
     * @return
     * @throws ConsoleException
     * @throws GroupNotFoundException
     */
    public List<Group> getGroups(UserProfile aUserProfile, Collection<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws ConsoleException, GroupNotFoundException {
        List<org.ow2.bonita.facade.identity.Group> theGroups;
        try {

            final Set<String> theGroupIDs = new HashSet<String>();
            for (BonitaUUID theBonitaUUID : anItemSelection) {
                theGroupIDs.add(theBonitaUUID.getValue());
            }
            theGroups = AccessorUtil.getIdentityAPI().getGroupsByUUIDs(theGroupIDs);
        } catch (org.ow2.bonita.facade.exception.GroupNotFoundException e) {
            e.printStackTrace();
            throw new GroupNotFoundException(e.getName());
        }
        return buildGroups(theGroups);
    }

    /**
     * @param aUserProfile
     * @param anItemSelection
     * @param aFilter
     * @return
     * @throws ConsoleException
     */
    public List<User> getUsers(UserProfile aUserProfile, Collection<UserUUID> anItemSelection, UserFilter aFilter) throws UserNotFoundException, ConsoleException {
        List<org.ow2.bonita.facade.identity.User> theUsers;
        try {
            Collection<String> theUserIDs = new HashSet<String>();
            for (UserUUID theUserUUID : anItemSelection) {
                theUserIDs.add(theUserUUID.getValue());
            }
            theUsers = AccessorUtil.getIdentityAPI().getUsersByUUIDs(theUserIDs);
        } catch (org.ow2.bonita.facade.exception.UserNotFoundException e) {
            e.printStackTrace();
            throw new UserNotFoundException(e.getUserId());
        }
        return buildUsers(theUsers);

    }

    private List<User> buildUsers(List<org.ow2.bonita.facade.identity.User> aUsers) throws ConsoleException {
        ArrayList<User> theResult = new ArrayList<User>();
        User theUser;
        for (org.ow2.bonita.facade.identity.User theSource : aUsers) {
            theUser = buildUser(theSource);
            if (theUser != null) {
                theResult.add(theUser);
            }
        }
        return theResult;
    }

    /**
     * @param aUserProfile
     * @param aAnItem
     * @param aFilter
     * @return
     */
    public ItemUpdates<UserMetadataItem> addUserMetadata(UserProfile aUserProfile, UserMetadataItem anItem, SimpleFilter aFilter) throws UserMetadataAlreadyExistsException, ConsoleException {
        try {
            AccessorUtil.getIdentityAPI().addProfileMetadata(anItem.getName(), anItem.getLabel());
            return getAllUserMetadatas(aFilter);
        } catch (org.ow2.bonita.facade.exception.MetadataAlreadyExistsException e) {
            e.printStackTrace();
            throw new UserMetadataAlreadyExistsException(e.getName());
        }
    }

    /**
     * @param aAnItemFilter
     * @return
     */
    public ItemUpdates<UserMetadataItem> getAllUserMetadatas(SimpleFilter anItemFilter) {
        final int theNumberOfMetadatas = AccessorUtil.getIdentityAPI().getNumberOfProfileMetadata();
        if (theNumberOfMetadatas > anItemFilter.getStartingIndex()) {
            final List<org.ow2.bonita.facade.identity.ProfileMetadata> theItems = AccessorUtil.getIdentityAPI().getProfileMetadata(anItemFilter.getStartingIndex(), anItemFilter.getMaxElementCount());
            return new ItemUpdates<UserMetadataItem>(buildMetadatas(theItems), theNumberOfMetadatas);
        } else {
            return new ItemUpdates<UserMetadataItem>(new ArrayList<UserMetadataItem>(), theNumberOfMetadatas);
        }
    }

    /**
     * @param aItems
     * @return
     */
    protected List<UserMetadataItem> buildMetadatas(List<ProfileMetadata> anItemList) {
        List<UserMetadataItem> theResult = new ArrayList<UserMetadataItem>();
        if (anItemList != null) {
            for (ProfileMetadata theProfileMetadata : anItemList) {
                theResult.add(buildMetadata(theProfileMetadata));
            }
        }
        return theResult;
    }

    protected UserMetadataItem buildMetadata(ProfileMetadata aSource) {

        return new UserMetadataItem(aSource.getUUID(), aSource.getName(), aSource.getLabel());
    }

    /**
     * @param aAnItemUUID
     * @param aFilter
     * @return
     * @throws UserMetadataNotFoundException
     */
    public UserMetadataItem getUserMetadata(BonitaUUID anItemUUID, SimpleFilter aFilter) throws UserMetadataNotFoundException {
        ProfileMetadata theItem;
        try {
            theItem = AccessorUtil.getIdentityAPI().getProfileMetadataByUUID(anItemUUID.getValue());
        } catch (org.ow2.bonita.facade.exception.MetadataNotFoundException e) {
            e.printStackTrace();
            throw new UserMetadataNotFoundException(e.getName());
        }
        return buildMetadata(theItem);
    }

    /**
     * @param aUserProfile
     * @param aAnItemSelection
     * @param aFilter
     * @return
     * @throws ConsoleException
     */
    public List<UserMetadataItem> getUserMetadatas(UserProfile aUserProfile, Collection<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws ConsoleException {
        throw new ConsoleException("API is missing: getMetadatas(...)", null);
    }

    /**
     * @param aUserProfile
     * @param aAnItemSelection
     * @param aAnItemFilter
     * @return
     * @throws UserMetadataNotFoundException
     */
    public ItemUpdates<UserMetadataItem> removeUserMetadatas(UserProfile aUserProfile, Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter) throws UserMetadataNotFoundException {
        try {
            Collection<String> theMetadataCollection = new ArrayList<String>();
            for (BonitaUUID theBonitaUUID : anItemSelection) {
                theMetadataCollection.add(theBonitaUUID.getValue());
            }
            AccessorUtil.getIdentityAPI().removeProfileMetadata(theMetadataCollection);
            return getAllUserMetadatas(anItemFilter);
        } catch (org.ow2.bonita.facade.exception.MetadataNotFoundException e) {
            e.printStackTrace();
            throw new UserMetadataNotFoundException(e.getName());
        }
    }

    /**
     * @param aUserProfile
     * @param aAnItemId
     * @param aAnItem
     * @return
     * @throws UserMetadataNotFoundException
     * @throws UserMetadataAlreadyExistsException
     */
    public UserMetadataItem updateUserMetadata(UserProfile aUserProfile, BonitaUUID anItemId, UserMetadataItem anItem) throws UserMetadataNotFoundException, UserMetadataAlreadyExistsException {
        ProfileMetadata theMetadata;
        try {
            theMetadata = AccessorUtil.getIdentityAPI().updateProfileMetadataByUUID(anItemId.getValue(), anItem.getName(), anItem.getLabel());
        } catch (MetadataNotFoundException e) {
            e.printStackTrace();
            throw new UserMetadataNotFoundException(e.getName());
        } catch (MetadataAlreadyExistsException e) {
            e.printStackTrace();
            throw new UserMetadataAlreadyExistsException(e.getName());
        }
        return buildMetadata(theMetadata);
    }

    /**
     * @return
     */
    public List<UserMetadataItem> getAllUserMetadatas() {
        List<org.ow2.bonita.facade.identity.ProfileMetadata> theItems = AccessorUtil.getIdentityAPI().getAllProfileMetadata();
        List<UserMetadataItem> theResult = new ArrayList<UserMetadataItem>();
        if (theItems != null) {
            theResult.addAll(buildMetadatas(theItems));
        }
        return theResult;
    }

    /*
     * Memberships.
     */

    public MembershipItem getMembership(UserProfile aUserProfile, BonitaUUID aAnItemUUID, SimpleFilter aFilter) throws MembershipNotFoundException {
        final IdentityAPI theIdentityAPI = AccessorUtil.getIdentityAPI();
        Membership theMembersip;
        try {
            theMembersip = theIdentityAPI.getMembershipByUUID(aAnItemUUID.getValue());
        } catch (org.ow2.bonita.facade.exception.MembershipNotFoundException e) {
            throw new MembershipNotFoundException();
        }
        return buildMembership(theMembersip);
    }

    public List<MembershipItem> getMemberships(UserProfile aUserProfile, List<BonitaUUID> anItemSelection, SimpleFilter aFilter) throws MembershipNotFoundException {
        if (anItemSelection == null || anItemSelection.isEmpty()) {
            throw new IllegalArgumentException();
        }

        final IdentityAPI theIdentityAPI = AccessorUtil.getIdentityAPI();

        Membership theMembersip;
        final List<MembershipItem> theResult = new ArrayList<MembershipItem>();
        try {
            for (BonitaUUID theBonitaUUID : anItemSelection) {
                theMembersip = theIdentityAPI.getMembershipByUUID(theBonitaUUID.getValue());
                theResult.add(buildMembership(theMembersip));
            }
        } catch (org.ow2.bonita.facade.exception.MembershipNotFoundException e) {
            throw new MembershipNotFoundException();
        }

        return theResult;
    }

    private MembershipItem buildMembership(final Membership aMembersip) {
        if (aMembersip == null) {
            return null;
        }
        return new MembershipItem(new BonitaUUID(aMembersip.getUUID()), buildGroup(aMembersip.getGroup()), buildRole(aMembersip.getRole()));
    }

    /**
     * @param aSearchPattern
     */
    private SearchQueryBuilder buildSearchUserQuery(final String aSearchPattern) {

        final SearchQueryBuilder theSearchQuery = new SearchQueryBuilder(new UserIndex());
        theSearchQuery.criterion(UserIndex.NAME).startsWith(aSearchPattern).or();
        theSearchQuery.criterion(UserIndex.FIRST_NAME).startsWith(aSearchPattern).or();
        theSearchQuery.criterion(UserIndex.LAST_NAME).startsWith(aSearchPattern).or();
        theSearchQuery.criterion(UserIndex.TITLE).startsWith(aSearchPattern).or();
        theSearchQuery.criterion(UserIndex.JOB_TITLE).startsWith(aSearchPattern).or();
        theSearchQuery.criterion(UserIndex.PROFESSIONAL_INFO + ContactInfoIndex.EMAIL).startsWith(aSearchPattern);
        return theSearchQuery;

    }

    private SearchQueryBuilder buildSearchGroupQuery(final String aSearchPattern) {
        final SearchQueryBuilder theSearchQuery = new SearchQueryBuilder(new GroupIndex());
        theSearchQuery.criterion(GroupIndex.NAME).startsWith(aSearchPattern).or();
        theSearchQuery.criterion(GroupIndex.LABEL).startsWith(aSearchPattern).or();
        theSearchQuery.criterion(GroupIndex.DESCRIPTION).startsWith(aSearchPattern);
        return theSearchQuery;
    }

    private SearchQueryBuilder buildSearchRoleQuery(final String aSearchPattern) {
        final SearchQueryBuilder theSearchQuery = new SearchQueryBuilder(new RoleIndex());
        theSearchQuery.criterion(RoleIndex.NAME).startsWith(aSearchPattern).or();
        theSearchQuery.criterion(RoleIndex.LABEL).startsWith(aSearchPattern).or();
        theSearchQuery.criterion(RoleIndex.DESCRIPTION).startsWith(aSearchPattern);
        return theSearchQuery;
    }
}
