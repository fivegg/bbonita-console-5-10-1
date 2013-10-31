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
package org.bonitasoft.console.security.client.users;

import java.io.Serializable;

import org.bonitasoft.console.security.client.privileges.RuleType;

/**
 * Logged in user
 * 
 * @author Anthony Birembaut
 */
public class User implements Serializable{

    /**
     * UID
     */
    private static final long serialVersionUID = 2475901011717222275L;

    /**
     * the user's username
     */
    private String username;
    
    /**
     * The user's display name (by default, it's identical to the username)
     */
    private String displayName;
    
    /**
     * Indicates whether the user is admin or not
     */
    private boolean isAdmin;
    
    /**
     * Indicates the locale to use to diplay the user interface
     */
    private String locale;
    
    /**
     * the domain
     */
    private String domain;
    
    /**
     * Indicates whether this user correspond to an auto login account
     */
    private boolean isAutoLogin;
    
    /**
     * Indicates whether this user correspond to an anonymous login
     */
    private boolean isAnonymous;
    
    /**
     * Indicates whether the credential transmission mechanism should be used
     */
    private boolean useCredentialTransmission;
    
    /**
     * Contains the user's rights
     */
    private UserRights userRights;

    /**
     * Constructor
     * @param userName
     * @param isAdmin
     * @param locale
     * @param userRights
     * @param useCredentialTransmission
     */
    public User(final String username, final boolean isAdmin, final String locale, final UserRights userRights, final boolean useCredentialTransmission) {
        super();
        this.username = username;
        this.displayName = username;
        this.isAdmin = isAdmin;
        this.locale = locale;
        this.userRights = userRights;
        this.useCredentialTransmission = useCredentialTransmission;
        isAutoLogin = false;
        isAnonymous = false;
    }
    
    /**
     * Default Constructor
     */
    public User() {
        super();
        // Mandatory for serialization
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(final boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    public void setUseCredentialTransmission(final boolean useCredentialTransmission) {
        this.useCredentialTransmission = useCredentialTransmission;
    }

    public boolean useCredentialTransmission() {
        return useCredentialTransmission;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setUserRights(final UserRights userRights) {
        this.userRights = userRights;
    }

    public UserRights getUserRights() {
        return userRights;
    }

    public boolean isAutoLogin() {
        return isAutoLogin;
    }

    public void setAutoLogin(final boolean isAutoLogin) {
        this.isAutoLogin = isAutoLogin;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(final boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public boolean isAllowed(final RuleType ruleType, final String item) {
        if (isAdmin) {
            return true;
        } else {
            if (this.userRights == null) {
                throw new IllegalArgumentException();
            }
            return this.userRights.isAllowed(ruleType, item);
        }
    }
}
