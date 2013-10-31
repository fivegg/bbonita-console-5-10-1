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
package org.bonitasoft.console.client.controller;

import org.bonitasoft.console.client.Feature;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.security.client.privileges.PrivilegePolicy;
import org.bonitasoft.console.security.client.privileges.RuleType;

/**
 * This class is responsible for gathering all methods dealing with user rights validation.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class UserRightsManager {

    private static UserRightsManager INSTANCE = new UserRightsManager();
    private final UserProfile myUserProfile;

    /**
     * Default constructor.
     */
    private UserRightsManager() {
        myUserProfile = new UserProfile();
    }

    public static UserRightsManager getInstance() {
        return INSTANCE;
    }

    public void updateUserRights(UserProfile aUserProfile) {
        myUserProfile.update(aUserProfile);
    }

    public boolean isAllowed(RuleType aRuleType, String anId) {
        if (myUserProfile != null) {
            return myUserProfile.isAllowed(aRuleType, anId);
        } else {
            return true;
        }
    }

    public boolean isAllowed(RuleType aRuleType) {
        if (myUserProfile != null) {
            return myUserProfile.isAllowed(aRuleType);
        } else {
            return true;
        }
    }

    public boolean isAdmin() {
        if (myUserProfile != null) {
            return myUserProfile.isAdmin();
        }
        return false;
    }

    public boolean hasSomeAdminRights() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
            return (hasAccessToProcessesManagement() || hasAccessToReportingManagement() || hasAccessToAdminCaseList() || hasAccessToUsersManagement() || hasAccessToCategoriesManagement());
        }
        return false;
    }

    public boolean hasAccessToProcessesManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
            if (myUserProfile.isAllowed(RuleType.PROCESS_INSTALL)) {
                return true;
            }
            if (myUserProfile.getUserRights().hasPrivileges(RuleType.PROCESS_MANAGE)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToReportingManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
            if (myUserProfile.isAllowed(RuleType.REPORT_INSTALL)) {
                return true;
            }
            if (myUserProfile.getUserRights().hasPrivileges(RuleType.REPORT_MANAGE)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToAdminCaseList() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
            if (myUserProfile.getUserRights().hasPrivileges(RuleType.PROCESS_MANAGE)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToReporting() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
            if (myUserProfile.getUserRights().hasPrivileges(RuleType.REPORT_VIEW)) {
                return true;
            }
            if (myUserProfile.getUserRights().hasPrivileges(RuleType.REPORT_MANAGE)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToUsersManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
            // if
            // (myUserProfile.getUserRights().hasPrivileges(RuleType.USER_ADD))
            // {
            // return true;
            // }
            // if
            // (myUserProfile.getUserRights().hasPrivileges(RuleType.USER_MANAGE))
            // {
            // return true;
            // }
            // if
            // (myUserProfile.getUserRights().hasPrivileges(RuleType.USER_DELETE))
            // {
            // return true;
            // }
        }
        return false;
    }

    public boolean hasAccessToCategoriesManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
            // if
            // (myUserProfile.getUserRights().hasPrivileges(RuleType.CATEGORY_ADD))
            // {
            // return true;
            // }
            // if
            // (myUserProfile.getUserRights().hasPrivileges(RuleType.CATEGORY_MANAGE))
            // {
            // return true;
            // }
            // if
            // (myUserProfile.getUserRights().hasPrivileges(RuleType.CATEGORY_DELETE))
            // {
            // return true;
            // }
        }
        return false;
    }

    public boolean hasAccessToCaseListsManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToSettingsManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToLabelsManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToSyncManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToLicenseManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToRemoteDeployment() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToPrivilegeManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAccessToThemesManagement() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return
     */
    public boolean hasAccessToDocumentManagement() {
        if (myUserProfile != null) {
            if (isAdmin() && hasAccessToDocumentFeature()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccessToDocumentFeature() {
        return myUserProfile.hasAccessToFeature(Feature.DOCUMENTS);
    }

    public PrivilegePolicy getPolicy(RuleType aRuleType) {
        return myUserProfile.getUserRights().getPolicy(aRuleType);
    }

    /**
     * @return
     */
    public boolean hasAccessToFailedStateManagment() {
        if (myUserProfile != null) {
            if (isAdmin() && hasAccessToStateFailedFeatures()) {
                return true;
            }
        }
        return false;
    }
    public boolean hasAccessToMonitoring() {
        if (myUserProfile != null) {
            if (isAdmin()) {
                return true;
            }
        }
        return false;
    }    

    public boolean hasAccessToResourceMonitoring() {
        if (myUserProfile != null) {
            if (isAdmin() && hasAccessToResourceMonitoringFeatures()) {
                return true;
            }
        }
        return false;        
    }
    
    public boolean hasAccessToBPMMonitoring() {
        if (myUserProfile != null) {
            if (isAdmin() && hasAccessToBPMMonitoringFeatures()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasAccessToResourceMonitoringFeatures(){
        return myUserProfile.hasAccessToFeature(Feature.RESOURCE_MONITORING);
    }
    
    private boolean hasAccessToBPMMonitoringFeatures(){
        return myUserProfile.hasAccessToFeature(Feature.BPM_MONITORING);
    }    
    
    private boolean hasAccessToStateFailedFeatures() {
        return myUserProfile.hasAccessToFeature(Feature.FAILED_STATE);
    }
}
