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
package org.bonitasoft.console.client.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.console.client.Feature;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.security.client.users.UserRights;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserProfile implements Serializable {

    /**
     * ID used for serialization.
     */
    private static final long serialVersionUID = 1940844173066923676L;

    public static final String PREFERED_REPORT_NAME = "user preferred report";

    protected String myUsername;
    protected User myUser;

    /**
     * Indicates wheter the user is admin or not
     */
    protected boolean isAdmin;

    /**
     * Indicates the locale to use to display the user interface
     */
    protected String myLocale;
    
    /**
     * the domain
     */
    private String myDomain;

    private ReportUUID myDefaultReportUUID;

    private transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

    protected List<LabelModel> myLabels;

    /**
     * Indicates whether the credential transmission mechanism should be used
     */
    private boolean useCredentialTransmission;
    
    /**
     * Indicates whether this user correspond to an auto login account
     */
    private boolean isAutoLogin;
    
    /**
     * Indicates whether this user correspond to an anonymous login
     */
    private boolean isAnonymous;

    /**
     * the user rights
     */
    private UserRights myUserRights;
    
    /**
     * the user rights
     */
    private List<String> myAvailableFeatures;
    
    /**
     * The product edition
     */
    private String myEdition;

    /**
     * 
     * Default constructor.
     */
    public UserProfile() {
        super();
        // Mandatory for serialization.
    }

    /**
     * 
     * Default constructor.
     * 
     * @param aUserRights
     * 
     * @param aUserUUID
     * @param aLabelList
     * @param aPermissionsDefinition
     * 
     * @param aValue
     */
    public UserProfile(String aUsername, User aUser, boolean aIsAdmin, String aLocale, UserRights aUserRights, boolean useCredentialTransmission, List<LabelModel> aLabelList) {
        myUsername = aUsername;
        myUser = aUser;
        isAdmin = aIsAdmin;
        myLocale = aLocale;
        myUserRights = aUserRights;
        myLabels = aLabelList;
        this.setUseCredentialTransmission(useCredentialTransmission);
    }

    public void setFeatures(List<String> aFeatureList) {
        myAvailableFeatures = aFeatureList;
    }
    /**
     * @return the userUUID
     */
    public String getUsername() {
        return myUsername;
    }

    public User getUser() {
        return myUser;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getLocale() {
        return myLocale;
    }

    public String getDomain() {
        return myDomain;
    }
    
    public void setUseCredentialTransmission(boolean useCredentialTransmission) {
        this.useCredentialTransmission = useCredentialTransmission;
    }

    public boolean useCredentialTransmission() {
        return useCredentialTransmission;
    }

    public ReportUUID getDefaultReportUUID() {
        return myDefaultReportUUID;
    }

    public void setDefaultReportUUID(ReportUUID aReportUUID) {
        ReportUUID theOldValue = myDefaultReportUUID;
        myDefaultReportUUID = aReportUUID;
        // Notify changes.
        if (myChanges != null) {
            myChanges.fireModelChange(PREFERED_REPORT_NAME, theOldValue, myDefaultReportUUID);
        }
    }

    /**
     * @return the labels
     */
    public List<LabelModel> getLabels() {
        ArrayList<LabelModel> theResult = new ArrayList<LabelModel>();
        if (myLabels != null) {
            theResult.addAll(myLabels);
        }
        return theResult;
    }

    /**
     * @param aLabelList
     *            the labels to set
     */
    public void setLabels(List<LabelModel> aLabelList) {
        if (aLabelList != null) {
            myLabels = new ArrayList<LabelModel>(aLabelList);
        } else {
            myLabels = null;
        }
    }

    /**
     * Add a property change listener.
     * 
     * @param aPropertyName
     * @param aListener
     */
    public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        // Avoid duplicate subscription.
        myChanges.removeModelChangeListener(aPropertyName, aListener);
        myChanges.addModelChangeListener(aPropertyName, aListener);

    }

    /**
     * Remove a property change listener.
     * 
     * @param aPropertyName
     * @param aListener
     */
    public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        myChanges.removeModelChangeListener(aPropertyName, aListener);
    }

    /**
     * Return the amount of time to wait between two calls to reporting refresh.<br>
     * This time is expressed in millisecond.
     * 
     * @return
     */
    public int getPreferredStatRefreshDelay() {
        // 1 minute.
        return 60 * 1000;
    }

    public void setRights(UserRights aUserRights) {
        myUserRights = aUserRights;
    }

    public boolean isAllowed(RuleType aRuleType, String anItem) {
        if (myUserRights == null) {
            throw new IllegalArgumentException();
        }
        return myUserRights.isAllowed(aRuleType, anItem);
    }
    public boolean isAllowed(RuleType aRuleType) {
        if (myUserRights == null) {
            throw new IllegalArgumentException();
        }
        return myUserRights.isAllowed(aRuleType);
    }

    /**
     * @return the userRights
     */
    public UserRights getUserRights() {
        return myUserRights;
    }

    private void setUsername(String aUsername) {
        myUsername = aUsername;
    }

    private void setUser(User aUser) {
        myUser = aUser;
    }

    private void setAdmin(boolean aIsAdmin) {
        isAdmin = aIsAdmin;
    }

    private void setLocale(String aLocale) {
        myLocale = aLocale;
    }

    public void setDomain(String aDomain) {
        myDomain = aDomain;
    }

    public boolean isAutoLogin() {
		return isAutoLogin;
	}

	public void setAutoLogin(boolean isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
	}

	public boolean isAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

    public void setEdition(String anEdition) {
        this.myEdition = anEdition;
    }

    public String getEdition() {
        return myEdition;
    }
    
	/**
     * Update the user profile with the given values.
     */
    public void update(UserProfile aUserProfile) {
        setAdmin(aUserProfile.isAdmin());
        setLabels(aUserProfile.getLabels());
        setLocale(aUserProfile.getLocale());
        setDefaultReportUUID(aUserProfile.getDefaultReportUUID());
        setRights(aUserProfile.getUserRights());
        setUseCredentialTransmission(aUserProfile.useCredentialTransmission());
        setUser(aUserProfile.getUser());
        setUsername(aUserProfile.getUsername());
        setFeatures(aUserProfile.getFeatures());
        setEdition(aUserProfile.getEdition());
    }

    private List<String> getFeatures() {
        return myAvailableFeatures;
    }

    public boolean hasAccessToAdminCaseList() {
        return (isAdmin() || myUserRights.hasPrivileges(RuleType.PROCESS_MANAGE));
    }

    public boolean hasAccessToReporting() {
        return (isAdmin() || myUserRights.hasPrivileges(RuleType.REPORT_VIEW) || myUserRights.hasPrivileges(RuleType.REPORT_MANAGE));
    }
    
    public boolean hasAccessToFeature(Feature aFeature) {
        if(myAvailableFeatures!=null) {
            return myAvailableFeatures.contains(aFeature.name());
        } else {
            return false;
        }
    }
}
