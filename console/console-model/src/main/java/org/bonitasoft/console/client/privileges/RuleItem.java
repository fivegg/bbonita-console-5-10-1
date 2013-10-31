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
package org.bonitasoft.console.client.privileges;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.security.client.privileges.RuleType;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class RuleItem implements Item, Serializable, Comparable<RuleItem> {

    private static final long serialVersionUID = 3114907201449660274L;

    protected RuleUUID myUUID;
    protected String myName;
    protected String myLabel;
    protected String myDescription;
    protected RuleType myType;
    protected HashSet<String> myUsers;
    protected HashSet<String> myRoles;
    protected HashSet<String> myGroups;
    protected HashSet<String> myMemberships;
    protected HashSet<String> myEntities;
    protected HashSet<String> myExceptions;

    protected RuleItem() {
        // Mandatory for serialization
        super();
        this.myUsers = new HashSet<String>();
        this.myRoles = new HashSet<String>();
        this.myGroups = new HashSet<String>();
        this.myMemberships = new HashSet<String>();
        this.myEntities = new HashSet<String>();
        this.myExceptions = new HashSet<String>();
    }

    public RuleItem(String anId, String aName, String aLabel, String aDescription, RuleType aType) {
        super();
        this.myUUID = new RuleUUID(anId);
        this.myName = aName;
        this.myLabel = aLabel;
        this.myDescription = aDescription;
        this.myType = aType;
        this.myUsers = new HashSet<String>();
        this.myRoles = new HashSet<String>();
        this.myGroups = new HashSet<String>();
        this.myMemberships = new HashSet<String>();
        this.myEntities = new HashSet<String>();
        this.myExceptions = new HashSet<String>();
    }

    /**
     * @return the uUID
     */
    public RuleUUID getUUID() {
        return myUUID;
    }

    /**
     * @param aUuid
     *            the uUID to set
     */
    public void setUUID(RuleUUID aUuid) {
        myUUID = aUuid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return myName;
    }

    /**
     * @param aName
     *            the name to set
     */
    public void setName(String aName) {
        myName = aName;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return myLabel;
    }

    /**
     * @param aLabel
     *            the label to set
     */
    public void setLabel(String aLabel) {
        myLabel = aLabel;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return myDescription;
    }

    /**
     * @param aDescription
     *            the description to set
     */
    public void setDescription(String aDescription) {
        myDescription = aDescription;
    }

    /**
     * @return the type
     */
    public RuleType getType() {
        return myType;
    }

    /**
     * @param aType
     *            the type to set
     */
    public void setType(RuleType aType) {
        myType = aType;
    }

    public void setEntities(Set<String> anEntitySet) {
        this.myEntities.clear();
        if (anEntitySet != null && !anEntitySet.isEmpty()) {
            myEntities.addAll(anEntitySet);
        }
    }

    // Users
    public void setUsers(Set<String> aUserIdSet) {
        this.myUsers.clear();
        if (aUserIdSet != null && !aUserIdSet.isEmpty()) {
            myUsers.addAll(aUserIdSet);
        }
    }

    public Set<String> getUserIDs() {
        return Collections.unmodifiableSet(this.myUsers);
    }

    public void addUserIds(Set<String> aUserIdSet) {
        this.myUsers.addAll(aUserIdSet);
    }

    public void addUserId(String aUserId) {
        this.myUsers.add(aUserId);
    }

    // Roles
    public void setRoles(Set<String> aRoleIdSet) {
        this.myRoles.clear();
        if (aRoleIdSet != null && !aRoleIdSet.isEmpty()) {
            myRoles.addAll(aRoleIdSet);
        }
    }

    public Set<String> getRoleIDs() {
        return Collections.unmodifiableSet(this.myRoles);
    }

    public void addRoleIds(Set<String> aRoleIdSet) {
        this.myRoles.addAll(aRoleIdSet);
    }

    public void addRoleId(String aRoleId) {
        this.myRoles.add(aRoleId);
    }

    // Groups
    public void setGroups(Set<String> aGroupIdSet) {
        this.myGroups.clear();
        if (aGroupIdSet != null && !aGroupIdSet.isEmpty()) {
            myGroups.addAll(aGroupIdSet);
        }
    }

    public Set<String> getGroupIDs() {
        return Collections.unmodifiableSet(this.myGroups);
    }

    public void addGroupIds(Set<String> aRoleIdSet) {
        this.myGroups.addAll(aRoleIdSet);
    }

    public void addGroupId(String aRoleId) {
        this.myGroups.add(aRoleId);
    }

    // Memberships
    public void setMemberships(Set<String> aMembershipIdSet) {
        this.myMemberships.clear();
        if (aMembershipIdSet != null && !aMembershipIdSet.isEmpty()) {
            myMemberships.addAll(aMembershipIdSet);
        }
    }

    public Set<String> getMembershipIDs() {
        return Collections.unmodifiableSet(this.myMemberships);
    }

    public void addMembershipIds(Set<String> aMembershipIdSet) {
        this.myMemberships.addAll(aMembershipIdSet);
    }

    public void addMembershipId(String aMembershipId) {
        this.myGroups.add(aMembershipId);
    }

    // Entities

    public Set<String> getEntities() {
        return Collections.unmodifiableSet(this.myEntities);
    }

    public void addEntities(Set<String> anEntitySet) {
        this.myEntities.addAll(anEntitySet);
    }

    public void addEntity(String anEntity) {
        this.myEntities.add(anEntity);
    }

    public void setExceptions(Set<String> anExceptionSet) {
        this.myExceptions.clear();
        if (anExceptionSet != null && !anExceptionSet.isEmpty()) {
            myExceptions.addAll(anExceptionSet);
        }
    }

    public Set<String> getExceptions() {
        return Collections.unmodifiableSet(this.myExceptions);
    }

    public void addExceptions(Set<String> anExceptionSet) {
        this.myExceptions.addAll(anExceptionSet);
    }

    public void addException(String anException) {
        this.myExceptions.add(anException);
    }

    /**
     * A rule is applicable when it targets at list an entity and an item.<br>
     * Simple rule do not need to target items.
     */
    public boolean isApplicable() {
        switch (myType) {
        case LOGOUT:
        case PASSWORD_UPDATE:
        case DELEGEE_UPDATE:
        case PROCESS_INSTALL:
        case REPORT_INSTALL:
            return ((!myUsers.isEmpty() || !myGroups.isEmpty() || !myRoles.isEmpty() || !myMemberships.isEmpty() || !myEntities.isEmpty()));
        default:
            return (!myExceptions.isEmpty() && (!myUsers.isEmpty() || !myGroups.isEmpty() || !myRoles.isEmpty() || !myMemberships.isEmpty() || !myEntities.isEmpty()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(RuleItem anotherRule) {
        return getName().compareTo(anotherRule.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.
     * client .Item)
     */
    public void updateItem(Item aSource) {
        if (aSource != null && aSource != this && aSource instanceof RuleItem) {
            RuleItem theOtherRule = (RuleItem) aSource;
            setDescription(theOtherRule.getDescription());
            setUsers(theOtherRule.getUserIDs());
            setRoles(theOtherRule.getRoleIDs());
            setGroups(theOtherRule.getGroupIDs());
            setMemberships(theOtherRule.getMembershipIDs());
            setEntities(theOtherRule.getEntities());
            setExceptions(theOtherRule.getExceptions());
            setLabel(theOtherRule.getLabel());
            setName(theOtherRule.getName());
            setType(theOtherRule.getType());
        }
    }
}
