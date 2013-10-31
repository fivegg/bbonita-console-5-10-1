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
package org.bonitasoft.console.server.users;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.console.client.identity.IdentityConfiguration;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.security.client.users.UserRights;
import org.bonitasoft.console.server.identity.IdentityDataStore;
import org.bonitasoft.console.server.labels.LabelDataStore;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserDataStore {

    private static final String USER_COMPLETION_META_KEY = "user_xp_username_completion_enable";
    private static UserDataStore instance;

    private static final Logger LOGGER = Logger.getLogger(UserDataStore.class.getName());

    /**
     * Get the unique instance of UserDataStore.
     * 
     * @return
     */
    public static synchronized UserDataStore getInstance() {
        if (instance == null) {
            instance = new UserDataStore();
        }

        return instance;
    }

    /**
     * Default constructor.
     */
    private UserDataStore() {
        super();
    }

    /**
     * Get the user profile attached to the given UserUUID.<br>
     * If the user does not exist it will create one.
     * 
     * @param aUserRights
     * 
     * @param aUserUUID
     * @param useCredentialTransmission
     * @return the user profile.
     * @throws Exception
     */
    public static UserProfile getUserProfile(String aUsername, boolean isAdminProfile, String aLocale, UserRights aUserRights, boolean useCredentialTransmission) throws Exception {

        List<LabelModel> theUserLabels;
        User theUser = null;

        try {
            theUser = IdentityDataStore.getInstance().findUserByUserName(aUsername);

        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Unable to get the user definition corresponding to username: " + aUsername);
            }
        }

        UserUUID theUserUUID = new UserUUID(aUsername);
        if (theUser != null) {
            theUserLabels = LabelDataStore.getInstance().getAllLabels(theUser);
        } else {
            theUserLabels = LabelDataStore.getInstance().getAllLabels(aUsername);
        }

        if (theUserLabels == null || theUserLabels.isEmpty() || !theUserLabels.contains(LabelModel.INBOX_LABEL)) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Label Inbox does not exist for user. Will create default labels...");
            }
            // The user does not exist yet.
            // Create the default labels for the user.
            theUserLabels = LabelDataStore.getInstance().createDefaultLabelsForNewUser(theUserUUID);
        } else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "User labels are already created.");
            }
        }
        return new UserProfile(aUsername, theUser, isAdminProfile, aLocale, aUserRights, useCredentialTransmission, theUserLabels);
    }

    public static void loadUserPreferences(UserProfile aUserProfile) {
        final String thePreferredReport = AccessorUtil.getManagementAPI().getMetaData(getMetadataKey(aUserProfile));
        final ReportUUID theReportUUID;
        if (thePreferredReport == null) {
            theReportUUID = null;
        } else {
            theReportUUID = new ReportUUID(thePreferredReport);
        }
        aUserProfile.setDefaultReportUUID(theReportUUID);
    }

    /**
     * Update the userprofile with the given report name.
     * 
     * @param aProfile
     * @param aReportName
     */
    public void updateUserProfile(UserProfile aProfile, ReportUUID aReportUUID) {
        final String theMetadataValue;
        if (aReportUUID == null) {
            theMetadataValue = null;
        } else {
            theMetadataValue = aReportUUID.getValue();
        }
        AccessorUtil.getManagementAPI().addMetaData(getMetadataKey(aProfile), theMetadataValue);
    }

    /**
     * Build the metadata key (for the stat preference) based on the user UUID.
     * 
     * @param aUserProfile
     * @return
     */
    private static String getMetadataKey(UserProfile aUserProfile) {
        return aUserProfile.getUsername() + "--$" + "defaultStatReportName";
    }

    /**
     * Get the identity configuration based on the metadata stored in the D.B.
     * of the engine.
     * 
     * @return the configuration
     */
    public IdentityConfiguration getIdentityConfiguration() {

        final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();

        IdentityConfiguration theResult = new IdentityConfiguration();
        String theState = null;
        // Get the metadata for the global settings.
        theState = managementAPI.getMetaData(USER_COMPLETION_META_KEY);
        if ("false".equalsIgnoreCase(theState)) {
            // Non empty metadata means false.
            theResult.setUserCompletionEnabled(false);
        }

        return theResult;
    }

    public void updateConfiguration(IdentityConfiguration aNewConfiguration) {
        if (aNewConfiguration == null) {
            throw new IllegalArgumentException("Configuration must not be null!");
        }
        ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
        if (aNewConfiguration.isUserCompletionEnabled()) {
            theManagementAPI.deleteMetaData(USER_COMPLETION_META_KEY);
        } else {
            theManagementAPI.addMetaData(USER_COMPLETION_META_KEY, "false");
        }

    }
}
