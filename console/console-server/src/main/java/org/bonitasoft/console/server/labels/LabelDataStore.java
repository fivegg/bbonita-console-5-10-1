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
package org.bonitasoft.console.server.labels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.identity.MembershipItem;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelUpdates;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.ow2.bonita.facade.CommandAPI;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.WebAPI;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.Label;
import org.ow2.bonita.facade.runtime.command.WebDeleteAllCustomLabelsExceptCommand;
import org.ow2.bonita.facade.runtime.command.WebDeleteAllLabelsByNameCommand;
import org.ow2.bonita.facade.runtime.command.WebGetNumberOfManageableParentProcessInstances;
import org.ow2.bonita.facade.runtime.command.WebGetNumberOfParentProcessInstancesWithActiveUserAndActivityInstanceExpectedEndDate;
import org.ow2.bonita.facade.runtime.command.WebGetNumberOfParentProcessInstancesWithOverdueTasks;
import org.ow2.bonita.facade.runtime.command.WebGetProcessInstancesNumberWithActiveUser;
import org.ow2.bonita.facade.runtime.command.WebGetProcessInstancesNumberWithInvolvedUser;
import org.ow2.bonita.facade.runtime.command.WebGetProcessInstancesNumberWithStartedBy;
import org.ow2.bonita.facade.runtime.command.WebUpdateLabelCommand;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelDataStore {

  private static final LabelDataStore INSTANCE = new LabelDataStore();
  private static final Logger LOGGER = Logger.getLogger(LabelDataStore.class.getName());

  private static final int DEFAULT_REMAINING_DAYS = 0;
  private static final String LABEL_STAR_USAGE_ACTIVATION_STATE = "xp.label.star.usage.enable";
  private static final String CUSTOM_LABELS_USAGE_ACTIVATION_STATE = "xp.label.custom.usage.enable";
  public final LabelModel[] DEFAULT_LABELS;

  /**
   * @return the iNSTANCE
   */
  public static LabelDataStore getInstance() {
    return INSTANCE;
  }

  /**
   * Default constructor.
   */
  private LabelDataStore() {
    super();
    DEFAULT_LABELS = new LabelModel[] { LabelModel.INBOX_LABEL, LabelModel.STAR_LABEL, LabelModel.MY_CASES_LABEL, LabelModel.ALL_LABEL, LabelModel.ATRISK_LABEL, LabelModel.OVERDUE_LABEL/*, LabelModel.MY_TEAM_LABEL, LabelModel.DELEGEE_LABEL*/ };
  }

  /**
   * @param aSource
   * @return
   */
  private LabelModel buildLabelModel(Label aSource) {
    LabelUUID theLabelUUID = new LabelUUID(aSource.getName(), new UserUUID(aSource.getOwnerName()));
    boolean isAssignableByUser = (!aSource.isSystemLabel()) || (theLabelUUID.equals(LabelModel.STAR_LABEL.getUUID()));

    LabelModel theLabelModel = new LabelModel(theLabelUUID, aSource.getEditableCSSStyleName(), aSource.getReadonlyCSSStyleName(), aSource.getPreviewCSSStyleName(), aSource.isVisible(), aSource
        .isSystemLabel(), aSource.isHasToBeDisplayed(), aSource.getDisplayOrder(), isAssignableByUser, aSource.getIconCSSStyle());

    return theLabelModel;
  }

  /**
   * Create a new label.
   * 
   * @param aUserUUID
   * @param aLabelName
   * @return the newly created label.
   * @throws ConsoleException
   */
  public LabelModel createNewLabel(UserProfile aUserProfile, String aLabelName) throws ConsoleException {
    UserUUID theUserUUID = new UserUUID(aUserProfile.getUsername());
    // Create a label only if no other label have the same name.
    if (getLabel(theUserUUID, aLabelName) == null) {
      LabelUUID theNewLabelUUID = new LabelUUID(aLabelName, theUserUUID);
      // Create the label
      LabelModel theNewLabel = new LabelModel(theNewLabelUUID, LabelModel.DEFAULT_EDITABLE_CSS, LabelModel.DEFAULT_READONLY_CSS, LabelModel.DEFAULT_PREVIEW_CSS, true);
      AccessorUtil.getWebAPI().addLabel(aLabelName, theUserUUID.getValue(), LabelModel.DEFAULT_EDITABLE_CSS, LabelModel.DEFAULT_READONLY_CSS, LabelModel.DEFAULT_PREVIEW_CSS, true, true, null, null,
          0, false);

      // Update the user profile.
      ArrayList<LabelModel> theUserLabels = new ArrayList<LabelModel>(aUserProfile.getLabels());
      theUserLabels.add(theNewLabel);
      aUserProfile.setLabels(theUserLabels);

      return theNewLabel;
    } else {
      throw new ConsoleException("Cannot create label(" + aLabelName + "): a label with the same name already exist!", new IllegalArgumentException(aLabelName));
    }

  }

  /**
   * Get a label identified by its name.
   * 
   * @param aUserUUID
   * @param aName
   * @return the label if it exists or null.
   */
  public LabelModel getLabel(UserUUID aUserUUID, String aName) {
    LabelUUID theLabelUUID = new LabelUUID(aName, aUserUUID);
    Label theSearchResult = AccessorUtil.getWebAPI().getLabel(theLabelUUID.getOwner().getValue(), theLabelUUID.getValue());
    LabelModel theResult = null;
    if (theSearchResult != null) {
      theResult = buildLabelModel(theSearchResult);
    }
    return theResult;
  }

  /**
   * Delete a label.
   * 
   * @param aUserUUID
   * @param aLabelUUID
   */
  public void removeCustomLabels(UserProfile aUserProfile, List<LabelUUID> aLabelUUIDList) {
    if (aUserProfile != null && aLabelUUIDList != null && !aLabelUUIDList.isEmpty()) {
      final Collection<LabelModel> theUserLabels = aUserProfile.getLabels();
      final List<LabelModel> theNewLabels = new ArrayList<LabelModel>();
      final Collection<String> theLabelsToDelete = new ArrayList<String>();
      for (LabelModel theLabelModel : theUserLabels) {
        if (aLabelUUIDList.contains(theLabelModel.getUUID())) {
          if (theLabelModel.isSystemLabel()) {
            // System labels cannot be deleted.
            if (LOGGER.isLoggable(Level.WARNING)) {
              LOGGER.log(Level.WARNING, "Skipped suppression of a system label: " + theLabelModel.getUUID().getValue() + " for user " + theLabelModel.getUUID().getOwner());
            }
          } else {
            theLabelsToDelete.add(theLabelModel.getUUID().getValue());
          }
        } else {
          theNewLabels.add(theLabelModel);
        }
      }
      AccessorUtil.getWebAPI().removeLabels(aUserProfile.getUsername(), theLabelsToDelete);
      aUserProfile.setLabels(theNewLabels);
    }
  }

  /**
   * Get labels of case.<br/>
   * The Inbox label is never part of the labels returned by this method.<br/>
   * It has to be computed externally.
   * @param aUserProfile
   * @param aProcessInstance
   * @return the list of labels associated to the case.
   * @throws ConsoleException
   */
  public List<LabelModel> getLabelsOfCase(UserProfile aUserProfile, LightProcessInstance aProcessInstance) throws ConsoleException {
    // Get the labels of the user.
    final String theUsername = aUserProfile.getUsername();
    Set<Label> caseLabels = AccessorUtil.getWebAPI().getCaseLabels(theUsername, aProcessInstance.getUUID());
    List<LabelModel> theCaseLabels = new ArrayList<LabelModel>();
    for (Label label : caseLabels) {
      theCaseLabels.add(buildLabelModel(label));
    }

    final Collection<LabelModel> theUserLabels = aUserProfile.getLabels();
//    LabelModel theUserInboxLabel = null;
    LabelModel theUserMyCaseLabel = null;
    for (LabelModel theLabelModel : theUserLabels) {
//      if (theLabelModel.getUUID().equals(LabelModel.INBOX_LABEL.getUUID())) {
//        theUserInboxLabel = theLabelModel;
//      } else 
      if (theLabelModel.getUUID().equals(LabelModel.MY_CASES_LABEL.getUUID())) {
        theUserMyCaseLabel = theLabelModel;
      }
    }

//    Set<String> theActiveUsers = aProcessInstance.getActiveUsers();
//    if (theActiveUsers != null && theActiveUsers.contains(theUsername)) {
//      if (theUserInboxLabel != null) {
//        theCaseLabels.add(theUserInboxLabel);
//      }
//    }
    if (aProcessInstance.getStartedBy().equals(theUsername)) {
      if (theUserMyCaseLabel != null) {
        theCaseLabels.add(theUserMyCaseLabel);
      }
    }
    Collections.sort(theCaseLabels);
    return theCaseLabels;
  }

  public Map<CaseUUID, List<LabelModel>> getLabelsOfCases(UserUUID aUserUUID, Set<ProcessInstanceUUID> aCaseUUIDs) throws ConsoleException {

    // Retrieve data from engine.
    Map<ProcessInstanceUUID, Set<Label>> casesLabels = AccessorUtil.getWebAPI().getCasesLabels(aUserUUID.getValue(), aCaseUUIDs);

    // This map will store the result of the method.
    Map<CaseUUID, List<LabelModel>> theResult = new HashMap<CaseUUID, List<LabelModel>>();

    // This map will contain all the labels already created. This will avoid
    // the creation of multiple instances of the same label.
    final Map<String, LabelModel> theLabelModels = new HashMap<String, LabelModel>();

    // Build the result.
    Set<ProcessInstanceUUID> theProcessInstanceUUIDs = casesLabels.keySet();
    Set<Label> thelabels;
    List<LabelModel> theResultLabels = null;
    for (ProcessInstanceUUID theProcessInstanceUUID : theProcessInstanceUUIDs) {
      thelabels = casesLabels.get(theProcessInstanceUUID);
      if (thelabels != null) {
        theResultLabels = new ArrayList<LabelModel>();
        for (Label theLabel : thelabels) {
          LabelModel theLabelModel = theLabelModels.get(theLabel.getName());
          if (theLabelModel == null) {
            theLabelModel = buildLabelModel(theLabel);
            // Remember that the label has been created.
            theLabelModels.put(theLabel.getName(), theLabelModel);
          }
          // Add the label to the result.
          theResultLabels.add(theLabelModel);
        }

      }

      theResult.put(new CaseUUID(theProcessInstanceUUID.getValue()), theResultLabels);
    }

    // Return the Map.
    return theResult;
  }

  /**
   * @param aUserUUID
   * @return all the labels for the given user.
   * @throws Exception
   */
  public List<LabelModel> getAllLabels(User aUser) throws Exception {
    final WebAPI theWebAPI = AccessorUtil.getWebAPI();

    final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
    Set<Label> theLabels = theWebAPI.getLabels(aUser.getUsername());

    List<LabelModel> theResult = new ArrayList<LabelModel>();
    if (theLabels != null) {
      for (Label label : theLabels) {
        theResult.add(buildLabelModel(label));
      }
    }

    if (!theResult.contains(LabelModel.STAR_LABEL)) {
      // Check if it is needed to re-create it.
      // Empty metadata means true.
      final String theStarUsageActivationState = theManagementAPI.getMetaData(LABEL_STAR_USAGE_ACTIVATION_STATE);
      if (theStarUsageActivationState == null || theStarUsageActivationState.equalsIgnoreCase("true")) {
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.log(Level.FINE, "No Starred label found for user create one as the feature has been enabled.");
        }

        LabelModel label = new LabelModel(LabelModel.STAR_LABEL, new UserUUID(aUser.getUsername()));
        theWebAPI.addLabel(label.getUUID().getValue(), label.getUUID().getOwner().getValue(), label.getEditableCSSStyleName(), label.getReadonlyCSSStyleName(), label.getPreviewCSSStyleName(), label
            .isVisible(), label.hasToBeDisplayed(), label.getIconCSSStyle(), null, label.getDisplayOrder(), label.isSystemLabel());
        // Add the star label to the result list.
        theResult.add(label);
      } else {
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.log(Level.FINE, "No Starred label returned for user as the feature has been disabled.");
        }
      }
    }

    return theResult;
  }

  public List<LabelModel> getAllLabels(String aUserName) throws Exception {
    final WebAPI theWebAPI = AccessorUtil.getWebAPI();
    final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
    Set<Label> theLabels = theWebAPI.getLabels(aUserName);

    List<LabelModel> theResult = new ArrayList<LabelModel>();
    if (theLabels != null) {
      for (Label label : theLabels) {
        theResult.add(buildLabelModel(label));
      }
    }

    if (!theResult.contains(LabelModel.STAR_LABEL)) {
      // Check if it is needed to re-create it.
      // Empty metadata means true.
      final String theStarUsageActivationState = theManagementAPI.getMetaData(LABEL_STAR_USAGE_ACTIVATION_STATE);
      if (theStarUsageActivationState == null || theStarUsageActivationState.equalsIgnoreCase("true")) {
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.log(Level.FINE, "No Starred label found for user create one as the feature has been enabled.");
        }

        LabelModel label = new LabelModel(LabelModel.STAR_LABEL, new UserUUID(aUserName));
        theWebAPI.addLabel(label.getUUID().getValue(), label.getUUID().getOwner().getValue(), label.getEditableCSSStyleName(), label.getReadonlyCSSStyleName(), label.getPreviewCSSStyleName(), label
            .isVisible(), label.hasToBeDisplayed(), label.getIconCSSStyle(), null, label.getDisplayOrder(), label.isSystemLabel());
        // Add the star label to the result list.
        theResult.add(label);
      } else {
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.log(Level.FINE, "No Starred label returned for user as the feature has been disabled.");
        }
      }
    }

    return theResult;
  }

  /**
   * Create all the default labels for the given user, except the Star label
   * that is handled by the getAllLabels().
   * 
   * @param aUserUUID
   * @return all the newly created labels
   */
  public List<LabelModel> createDefaultLabelsForNewUser(UserUUID aUserUUID) {
    List<LabelModel> theResult = new ArrayList<LabelModel>();

    final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
    final String theStarUsageActivationState = theManagementAPI.getMetaData(LABEL_STAR_USAGE_ACTIVATION_STATE);
    boolean addStarLabel = false;
    if (theStarUsageActivationState == null || theStarUsageActivationState.equalsIgnoreCase("true")) {
      addStarLabel = true;
    }
    for (LabelModel theLabelModel : DEFAULT_LABELS) {
      if (theLabelModel.equals(LabelModel.STAR_LABEL)) {
        if (addStarLabel) {
          theResult.add(new LabelModel(theLabelModel, aUserUUID));
        }
      } else {
        theResult.add(new LabelModel(theLabelModel, aUserUUID));
      }
    }

    final WebAPI theWebApi = AccessorUtil.getWebAPI();
    final Set<ProcessInstanceUUID> caseList = null;
    for (LabelModel label : theResult) {
      // Do not insert the Star label. It will be added by the getAllLabels() if
      // necessary.
      if (!label.equals(LabelModel.STAR_LABEL)) {
        theWebApi.addLabel(label.getUUID().getValue(), label.getUUID().getOwner().getValue(), label.getEditableCSSStyleName(), label.getReadonlyCSSStyleName(), label.getPreviewCSSStyleName(), label
            .isVisible(), label.hasToBeDisplayed(), label.getIconCSSStyle(), caseList, label.getDisplayOrder(), label.isSystemLabel());
      }
    }
    return theResult;

  }

  /**
   * @param aLabelUUID
   * @param aEditableCSSStyle
   * @param aPreviewCSSStyle
   * @param aReadOnlyCSSStyle
   */
  public void updateLabelCSSStyle(LabelUUID aLabelUUID, String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle) {
    AccessorUtil.getWebAPI().updateLabelCSS(aLabelUUID.getOwner().getValue(), aLabelUUID.getValue(), aEditableCSSStyle, aPreviewCSSStyle, aReadOnlyCSSStyle);
  }

  /**
   * 
   * @param aLabelUUID
   * @param isVisible
   * @throws InstanceNotFoundException
   */
  public void renameLabel(UserProfile aUserProfile, LabelUUID aLabelUUID, String aNewName) throws InstanceNotFoundException {

    final WebAPI theWebAPI = AccessorUtil.getWebAPI();
    Collection<LabelModel> theUserLabels = aUserProfile.getLabels();
    for (LabelModel theLabelModel : theUserLabels) {
      if (theLabelModel.getUUID().equals(aLabelUUID)) {
        theWebAPI.updateLabelName(aLabelUUID.getOwner().getValue(), aLabelUUID.getValue(), aNewName);
        theLabelModel.setName(aNewName);
      }
    }
  }

  public void updateLabels(UserProfile aUserProfile, Set<LabelUUID> aSetOfLabelUUIDToAdd, Set<LabelUUID> aSetOfLabelUUIDToRemove, Set<CaseUUID> aSetOfCaseUUID) throws Exception, ConsoleException {
    final Set<LabelUUID> labelsToAdd = new HashSet<LabelUUID>();
    if (aSetOfLabelUUIDToAdd != null) {
      labelsToAdd.addAll(aSetOfLabelUUIDToAdd);
    }
    final Set<LabelUUID> labelsToRemove = new HashSet<LabelUUID>();
    if (aSetOfLabelUUIDToRemove != null) {
      labelsToRemove.addAll(aSetOfLabelUUIDToRemove);
    }

    if (labelsToAdd.contains(LabelModel.INBOX_LABEL.getUUID())) {
      // Make a special case of the Inbox label.
      throw new ConsoleException("Users cannot manually set the Inbox label!", new IllegalArgumentException());

    }
    if (labelsToRemove.contains(LabelModel.INBOX_LABEL.getUUID())) {
      // Make a special case of the Inbox label.
      throw new ConsoleException("Users cannot manually remove the Inbox label!", new IllegalArgumentException());
    }

    Set<ProcessInstanceUUID> instanceUUIDs = new HashSet<ProcessInstanceUUID>();
    for (CaseUUID caseUUID : aSetOfCaseUUID) {
      instanceUUIDs.add(new ProcessInstanceUUID(caseUUID.getValue()));
    }
    Set<String> labelsToAddString = new HashSet<String>();
    for (LabelUUID labelUUID : labelsToAdd) {
      labelsToAddString.add(labelUUID.getValue());
    }
    Set<String> labelsToRemoveString = new HashSet<String>();
    for (LabelUUID labelUUID : labelsToRemove) {
      labelsToRemoveString.add(labelUUID.getValue());
    }

    AccessorUtil.getCommandAPI().execute(new WebUpdateLabelCommand(aUserProfile.getUsername(), labelsToAddString, labelsToRemoveString, instanceUUIDs));
  }

  public void deleteCases(Collection<CaseUUID> aCaseSelection) {
    if (aCaseSelection != null && !aCaseSelection.isEmpty()) {
      Set<ProcessInstanceUUID> theCases = new HashSet<ProcessInstanceUUID>();
      for (CaseUUID theCaseUUID : aCaseSelection) {
        theCases.add(new ProcessInstanceUUID(theCaseUUID.getValue()));
      }
      AccessorUtil.getWebAPI().removeAllCasesFromLabels(theCases);
    }
  }

  public LabelUpdates getLabelUpdates(final UserProfile aUserProfile, final LabelUUID aLabelUUID, boolean searchInHistory) throws Exception {
    if (aUserProfile == null || aLabelUUID == null) {
      throw new IllegalArgumentException();
    }

    final WebAPI theWebAPI = AccessorUtil.getWebAPI();
    final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();
    final QueryRuntimeAPI theQueryRuntimeAPI;
    if (searchInHistory) {
      theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
    } else {
      theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
    }
    final String theUsername = aUserProfile.getUsername();
    final User theUser = aUserProfile.getUser();
    final String theUserID;
    final Set<String> theUserRoles;
    final Set<String> theUserGroups;
    final Set<String> theUserMemberships;
    if (theUser != null) {
      theUserID = theUser.getUUID().getValue();
      theUserGroups = new HashSet<String>();
      theUserRoles = new HashSet<String>();
      theUserMemberships = new HashSet<String>();
      if (theUser.getMembership() != null) {
        for (MembershipItem theMembership : theUser.getMembership()) {
          theUserMemberships.add(theMembership.getUUID().getValue());
          theUserGroups.add(theMembership.getGroup().getUUID().getValue());
          theUserRoles.add(theMembership.getRole().getUUID().getValue());
        }
      }
    } else {
      theUserID = null;
      theUserRoles = null;
      theUserGroups = null;
      theUserMemberships = null;
    }
    
    Integer theCaseNumber;
    if (aLabelUUID.equals(LabelModel.INBOX_LABEL.getUUID())) {
      theCaseNumber = theCommandAPI.execute(new WebGetProcessInstancesNumberWithActiveUser(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUsername, searchInHistory));
    } else if (aLabelUUID.equals(LabelModel.ALL_LABEL.getUUID())) {
      theCaseNumber = theCommandAPI.execute(new WebGetProcessInstancesNumberWithInvolvedUser(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUsername, searchInHistory));
    } else if (aLabelUUID.equals(LabelModel.MY_CASES_LABEL.getUUID())) {
      theCaseNumber = theCommandAPI.execute(new WebGetProcessInstancesNumberWithStartedBy(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUsername, searchInHistory));
    } else if (aLabelUUID.equals(LabelModel.ADMIN_ALL_CASES.getUUID())) {
    	if (aUserProfile.isAdmin()) {
            theCaseNumber = theQueryRuntimeAPI.getNumberOfParentProcessInstances();
        } else if(aUserProfile.hasAccessToAdminCaseList()){
            theCaseNumber = theCommandAPI.execute(new WebGetNumberOfManageableParentProcessInstances(searchInHistory, theUserID, theUserRoles, theUserGroups,
                    theUserMemberships, theUsername));
        }
    	else {
        	throw new ConsoleSecurityException(theUsername, LabelDataStore.class.getName() + "getLabelUpdates(non-authorized argument)");
        }
    } else if (aLabelUUID.equals(LabelModel.ATRISK_LABEL.getUUID())) {
      theCaseNumber = theCommandAPI.execute(new WebGetNumberOfParentProcessInstancesWithActiveUserAndActivityInstanceExpectedEndDate(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUsername, DEFAULT_REMAINING_DAYS));
    } else if (aLabelUUID.equals(LabelModel.OVERDUE_LABEL.getUUID())) {
      theCaseNumber = theCommandAPI.execute(new WebGetNumberOfParentProcessInstancesWithOverdueTasks(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUsername));
    } else {
      // Listing a custom label
      Map<String, Integer> theCountOfCases = theWebAPI.getCasesNumber(aUserProfile.getUsername(), Arrays.asList(aLabelUUID.getValue()));
      if (theCountOfCases != null && theCountOfCases.size() > 0) {
        theCaseNumber = theCountOfCases.get(aLabelUUID.getValue());
      } else {
        theCaseNumber = 0;
      }

      // TODO onTrack, OverDue
    }

    return new LabelUpdates(aLabelUUID, theCaseNumber);

  }

  public void updateLabelsVisibility(UserProfile aUserProfile, Set<LabelUUID> anLabelUUIDSelection, boolean isVisible) {
    if (anLabelUUIDSelection != null && !anLabelUUIDSelection.isEmpty()) {
      Map<String, Boolean> theLabelsVisibility = new HashMap<String, Boolean>();
      for (LabelUUID theLabelUUID : anLabelUUIDSelection) {
        theLabelsVisibility.put(theLabelUUID.getValue(), isVisible);
      }
      AccessorUtil.getWebAPI().updateLabelVisibility(aUserProfile.getUsername(), theLabelsVisibility);
    }

  }

  public LabelsConfiguration getConfiguration() throws Exception {

    final LabelsConfiguration theResult = new LabelsConfiguration();
    final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();

    // Empty metadata means true.
    String theStarLabelUsageState = managementAPI.getMetaData(LABEL_STAR_USAGE_ACTIVATION_STATE);
    theResult.setStarEnabled(theStarLabelUsageState == null || theStarLabelUsageState.equalsIgnoreCase("true"));

    // Empty metadata means true.
    String theCustomLabelsUsageState = managementAPI.getMetaData(CUSTOM_LABELS_USAGE_ACTIVATION_STATE);
    theResult.setCustomLabelsEnabled(theCustomLabelsUsageState == null || theCustomLabelsUsageState.equalsIgnoreCase("true"));

    return theResult;

  }

  public void updateConfiguration(UserProfile aUserProfile, LabelsConfiguration aConfiguration) throws Exception {
    if (aUserProfile == null || aUserProfile.getUsername() == null || aUserProfile.getUsername().equals("") || !aUserProfile.isAdmin()) {
      throw new IllegalArgumentException("Invalid user.");
    }

    if (aConfiguration == null) {
      throw new IllegalArgumentException("Invalid configuration.");
    }

    final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();

    final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
    if (aConfiguration.isStarEnabled()) {
      // Empty metadata means true.
      managementAPI.deleteMetaData(LABEL_STAR_USAGE_ACTIVATION_STATE);
    } else {
      managementAPI.addMetaData(LABEL_STAR_USAGE_ACTIVATION_STATE, "false");
      theCommandAPI.execute(new WebDeleteAllLabelsByNameCommand(LabelModel.STAR_LABEL.getUUID().getValue()));
    }

    if (aConfiguration.isCustomLabelsEnabled()) {
      // Empty metadata means true.
      managementAPI.deleteMetaData(CUSTOM_LABELS_USAGE_ACTIVATION_STATE);
    } else {
      managementAPI.addMetaData(CUSTOM_LABELS_USAGE_ACTIVATION_STATE, "false");
      final String[] theLabelsNameToKeep = new String[DEFAULT_LABELS.length];
      int i = 0;
      for (LabelModel theLabel : DEFAULT_LABELS) {
        theLabelsNameToKeep[i] = theLabel.getUUID().getValue();
        i++;
      }
      theCommandAPI.execute(new WebDeleteAllCustomLabelsExceptCommand(theLabelsNameToKeep));
    }

    // try {
    // PreferencesDataStore.getInstance().setBooleanPreference(PreferencesDataStore.LABEL_STAR_USAGE_ACTIVATION_STATE,
    // aConfiguration.isStarEnabled());
    // PreferencesDataStore.getInstance().setBooleanPreference(PreferencesDataStore.CUSTOM_LABELS_USAGE_ACTIVATION_STATE,
    // aConfiguration.isCustomLabelsEnabled());
    // } catch (IOException e) {
    // e.printStackTrace();
    // LOGGER.log(Level.SEVERE,"Unable to persist the preference: " +
    // e.getMessage());
    // throw new ConsoleException();
    // }

  }
}
