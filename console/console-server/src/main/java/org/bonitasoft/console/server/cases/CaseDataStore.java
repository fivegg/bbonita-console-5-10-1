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
package org.bonitasoft.console.server.cases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.attachments.Attachment;
import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseItem.CaseItemState;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.cases.CaseUpdates;
import org.bonitasoft.console.client.cases.CasesConfiguration;
import org.bonitasoft.console.client.cases.exceptions.CaseNotFoundException;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.identity.MembershipItem;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.processes.exceptions.ProcessNotFoundException;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepPriority;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.common.application.ApplicationURLUtils;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.security.server.accessor.TenancyProperties;
import org.bonitasoft.console.server.labels.LabelDataStore;
import org.bonitasoft.console.server.persistence.PreferencesDataStore;
import org.ow2.bonita.facade.CommandAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.WebAPI;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.UncancellableInstanceException;
import org.ow2.bonita.facade.exception.UndeletableInstanceException;
import org.ow2.bonita.facade.runtime.Comment;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.runtime.command.GetProcessInstancesActivitiesCommand;
import org.ow2.bonita.facade.runtime.command.WebDeleteDocumentsOfProcessInstancesCommand;
import org.ow2.bonita.facade.runtime.command.WebDeleteProcessInstancesCommand;
import org.ow2.bonita.facade.runtime.command.WebGetLightParentProcessInstances;
import org.ow2.bonita.facade.runtime.command.WebGetLightParentUserInstancesCommand;
import org.ow2.bonita.facade.runtime.command.WebGetLightProcessInstancesFromActiveUsers;
import org.ow2.bonita.facade.runtime.command.WebGetLightProcessInstancesFromActiveUsersAndActivityInstanceExpectedEndDate;
import org.ow2.bonita.facade.runtime.command.WebGetLightProcessInstancesFromCategoryCommand;
import org.ow2.bonita.facade.runtime.command.WebGetLightProcessInstancesFromInvolvedUsers;
import org.ow2.bonita.facade.runtime.command.WebGetLightProcessInstancesWithOverdueTasksCommand;
import org.ow2.bonita.facade.runtime.command.WebGetManageableLightParentProcessInstances;
import org.ow2.bonita.facade.runtime.command.WebIsUserActiveInProcessInstancesCommand;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.light.LightTaskInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.Misc;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseDataStore {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(CaseDataStore.class.getName());

    private static final CaseDataStore INSTANCE = new CaseDataStore();

    protected static final int DEFAULT_REMAINING_DAYS = 0;

    /**
     * @return the iNSTANCE
     */
    public static CaseDataStore getInstance() {
        return INSTANCE;
    }

    protected CaseDataStore() {
        super();
    }

    private CaseItem buildCase(final UserProfile aUserProfile, CaseFilter aCaseFilter, final LightProcessInstance aSource, final List<LightActivityInstance> anActivityList,
            Map<ActivityInstanceUUID, Integer> aActivityInstanceCommentsCount, final BonitaProcessUUID theBonitaProcessUUID, final List<LabelModel> labelModels, HttpServletRequest aRequest,
            boolean isUserActiveInCase) throws ConsoleException, ProcessNotFoundException {
        List<StepItem> theSteps = buildSteps(aUserProfile, aCaseFilter, anActivityList, aActivityInstanceCommentsCount, aRequest);
        CaseUUID theCaseUUID = new CaseUUID(aSource.getProcessInstanceUUID().toString());
        // Here I am assuming that the step list follows a chronological order.
        CaseItem theCaseItem = new CaseItem(theCaseUUID, Long.toString(aSource.getNb()), theBonitaProcessUUID, theSteps, new ArrayList<Attachment>(), aSource.getLastUpdate(), aSource.getStartedBy(),
                aSource.getStartedDate());
        InstanceState theInstanceState = aSource.getInstanceState();
        theCaseItem.setState(CaseItemState.valueOf(theInstanceState.name()));
        theCaseItem.setIsArchived(aSource.isArchived());

        final List<LabelModel> theUserLabels = aUserProfile.getLabels();
        final String theUsername = aUserProfile.getUsername();
        LabelModel theUserInboxLabel = null;
        LabelModel theUserMyCaseLabel = null;
        for (LabelModel theLabelModel : theUserLabels) {
            if (theLabelModel.getUUID().equals(LabelModel.INBOX_LABEL.getUUID())) {
                theUserInboxLabel = theLabelModel;
            } else if (theLabelModel.getUUID().equals(LabelModel.MY_CASES_LABEL.getUUID())) {
                theUserMyCaseLabel = theLabelModel;
            }
        }

        for (LabelModel theLabelModel : labelModels) {
            theCaseItem.addLabel(theLabelModel);
        }

        if (isUserActiveInCase) {
            theCaseItem.addLabel(theUserInboxLabel);
        }

        if (theUsername.equals(aSource.getStartedBy())) {
            theCaseItem.addLabel(theUserMyCaseLabel);
        }
        return theCaseItem;

    }

    /**
     * Build Objects that will be serialized to the UI.
     * 
     * @param aUserProfile
     * @param aCaseFilter
     * @param source
     * @return
     * @throws ConsoleException
     * @throws ProcessNotFoundException
     * @throws InstanceNotFoundException
     * @throws Exception
     */
    protected List<CaseItem> buildCases(UserProfile aUserProfile, CaseFilter aCaseFilter, final Collection<LightProcessInstance> aSource, HttpServletRequest aRequest) throws ConsoleException,
            CaseNotFoundException, ProcessNotFoundException {
        final Set<ProcessInstanceUUID> allTheProcessInstanceUUIDs = new HashSet<ProcessInstanceUUID>();
        for (LightProcessInstance processInstance : aSource) {
            if (aCaseFilter.isWithAdminRights() || aUserProfile.isAllowed(RuleType.PROCESS_READ, processInstance.getProcessDefinitionUUID().getValue())) {
                allTheProcessInstanceUUIDs.add(processInstance.getUUID());
            } else {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "Skipping case as the user is not allowed to see the process: " + aUserProfile.getUsername() + " / "
                            + processInstance.getProcessDefinitionUUID().getValue());
                }
            }
        }
        final CommandAPI commandAPI = AccessorUtil.getCommandAPI();
        Map<ProcessInstanceUUID, List<LightActivityInstance>> lightActivityInstances;
        try {
            lightActivityInstances = commandAPI.execute(new GetProcessInstancesActivitiesCommand(allTheProcessInstanceUUIDs, aCaseFilter.isWithAdminRights()));
        } catch (Exception e) {
            throw new ConsoleException("Unbale to list steps of cases.", e);
        }

        final Map<ProcessInstanceUUID, Boolean> processInstancesIsActiveUser;
        if (!aCaseFilter.searchInHistory()) {
            try {
                processInstancesIsActiveUser = commandAPI.execute(new WebIsUserActiveInProcessInstancesCommand(allTheProcessInstanceUUIDs, aUserProfile.getUsername()));
            } catch (Exception e) {
                throw new ConsoleException("Unbale to check if user is active in process instances.", e);
            }
        } else {
            processInstancesIsActiveUser = null;
        }
        Map<ActivityInstanceUUID, Integer> theActivityInstanceComments = null;
        if (lightActivityInstances != null && lightActivityInstances.size() > 0) {
            try {
                theActivityInstanceComments = getActivitiesCommentCountFromMap(lightActivityInstances);
            } catch (InstanceNotFoundException e) {
                throw new CaseNotFoundException("Unable to list comments of case.", e);
            }

        }

        final List<CaseItem> theCases = new ArrayList<CaseItem>();
        CaseItem theCase;
        final Map<ProcessDefinitionUUID, BonitaProcessUUID> processes = new HashMap<ProcessDefinitionUUID, BonitaProcessUUID>();

        QueryDefinitionAPI queryDefinitionAPI;
        if (aCaseFilter.searchInHistory()) {
            // When cases are retrieved from history there is no way to know if
            // the
            // process definition is in history or in journal.
            // Search in both.
            queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        } else {
            queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        }

        // Get labels of cases
        UserUUID theUserUUID = new UserUUID(aUserProfile.getUsername());

        Map<CaseUUID, List<LabelModel>> theCasesLabels = LabelDataStore.getInstance().getLabelsOfCases(theUserUUID, allTheProcessInstanceUUIDs);
        List<LabelModel> theLabels;
        for (LightProcessInstance processInstance : aSource) {
            if (processInstance.getParentInstanceUUID() == null) {
                final ProcessDefinitionUUID processUUID = processInstance.getProcessDefinitionUUID();
                if (aCaseFilter.isWithAdminRights() || aUserProfile.isAllowed(RuleType.PROCESS_READ, processUUID.getValue())) {
                	if(aCaseFilter.getState() != null && aCaseFilter.getState().name().equals(CaseItemState.FINISHED.name()) && !processInstance.getInstanceState().name().equals(aCaseFilter.getState().name()) ){
                		continue;
                	}
                	BonitaProcessUUID theBonitaProcessUUID = processes.get(processUUID);
                    if (theBonitaProcessUUID == null) {
                        theBonitaProcessUUID = buildProcessUUID(processUUID, queryDefinitionAPI);
                        processes.put(processUUID, theBonitaProcessUUID);
                    }
                    theLabels = theCasesLabels.get(new CaseUUID(processInstance.getUUID().getValue()));
                    theCase = buildCase(aUserProfile, aCaseFilter, processInstance, lightActivityInstances.get(processInstance.getUUID()), theActivityInstanceComments, theBonitaProcessUUID,
                            theLabels, aRequest, (processInstancesIsActiveUser != null && processInstancesIsActiveUser.get(processInstance.getUUID())));
                    if (theCase != null) {
                        theCases.add(theCase);
                    }
                } else {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, "Skipping case as the user is not allowed to see the process: " + aUserProfile.getUsername() + " / "
                                + processInstance.getProcessDefinitionUUID().getValue());
                    }
                }
            } else {
                // The case is a 'sub process'.
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "The case " + processInstance.getUUID().getValue() + " was ignored because it corresponds to a subprocess instance.");
                }
            }
        }

        // Sort List before returning the result.
        Collections.sort(theCases);
        Collections.reverse(theCases);
        return theCases;
    }

    protected Map<ActivityInstanceUUID, Integer> getActivitiesCommentCountFromMap(Map<ProcessInstanceUUID, List<LightActivityInstance>> aLightActivityInstances) throws InstanceNotFoundException {
        Set<ActivityInstanceUUID> theActivities = new HashSet<ActivityInstanceUUID>();
        for (List<LightActivityInstance> listOfActivities : aLightActivityInstances.values()) {
            if (listOfActivities != null && listOfActivities.size() > 0) {
                for (LightActivityInstance theLightActivityInstance : listOfActivities) {
                    theActivities.add(theLightActivityInstance.getUUID());
                }

            }
        }
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        Map<ActivityInstanceUUID, Integer> theResult = queryRuntimeAPI.getNumberOfActivityInstanceComments(theActivities);
        return theResult;
    }

    /**
     * 
     * @param aUserProfile
     * @param aCaseFilter
     * @param aSource
     * @param aActivityInstanceCommentsCount
     * @return
     * @throws ProcessNotFoundException
     * @throws InstanceNotFoundException
     */
    List<StepItem> buildSteps(UserProfile aUserProfile, CaseFilter aCaseFilter, List<LightActivityInstance> aSource, Map<ActivityInstanceUUID, Integer> aActivityInstanceCommentsCount,
            HttpServletRequest aRequest) throws ProcessNotFoundException {
        final List<StepItem> theSteps = new ArrayList<StepItem>();

        Map<ProcessDefinitionUUID, String> applicationURLs = new HashMap<ProcessDefinitionUUID, String>();
        if (aSource != null) {
            QueryRuntimeAPI theQueryRuntimeAPI;
            if (aCaseFilter.searchInHistory()) {
                theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
            } else {
                theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
            }
            if (aUserProfile.hasAccessToAdminCaseList() && aCaseFilter.isWithAdminRights()) {
                for (LightActivityInstance theLightActivityInstance : aSource) {
                    if (aUserProfile.isAllowed(RuleType.ACTIVITY_READ, theLightActivityInstance.getActivityDefinitionUUID().getValue())) {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE, "According to rule, the step was created.");
                        }
                        // Do not list the BonitaInit, BonitaEnd and subflow
                        // activities as
                        // they are special activities.
                        if (!"BonitaInit".equals(theLightActivityInstance.getActivityName()) && !"BonitaEnd".equals(theLightActivityInstance.getActivityName())
                                && !theLightActivityInstance.isSubflow()) {
                            theSteps.add(buildStep(theLightActivityInstance, aRequest, applicationURLs, aActivityInstanceCommentsCount, theQueryRuntimeAPI));
                        }
                    }
                }

            } else {
                for (LightActivityInstance theLightActivityInstance : aSource) {
                    if (aUserProfile.isAllowed(RuleType.ACTIVITY_READ, theLightActivityInstance.getActivityDefinitionUUID().getValue())) {
                        if (theLightActivityInstance.isTask()) {
                            LightTaskInstance theLightTaskInstance = theLightActivityInstance.getTask();

                            switch (theLightTaskInstance.getState()) {
                            case FINISHED:
                                theSteps.add(buildStep(theLightTaskInstance, aRequest, applicationURLs, aActivityInstanceCommentsCount, theQueryRuntimeAPI));
                                break;
                            case FAILED:
                                theSteps.add(buildStep(theLightTaskInstance, aRequest, applicationURLs, aActivityInstanceCommentsCount, theQueryRuntimeAPI));
                                break;                                
                            case EXECUTING:

                                break;
                            case ABORTED:
                            case CANCELLED:
                            case SUSPENDED:
                            case READY:
                                if (theLightTaskInstance.isTaskAssigned()) {
                                    if (theLightTaskInstance.getTaskUser().equals(aUserProfile.getUsername())) {
                                        // The step is assigned to the current
                                        // user.
                                        theSteps.add(buildStep(theLightTaskInstance, aRequest, applicationURLs, aActivityInstanceCommentsCount, theQueryRuntimeAPI));
                                    }
                                } else {
                                    Set<String> taskCandidates;
                                    try {
                                        try {
                                            taskCandidates = theQueryRuntimeAPI.getTaskCandidates(theLightTaskInstance.getUUID());
                                        } catch (TaskNotFoundException e) {
                                            theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
                                            taskCandidates = theQueryRuntimeAPI.getTaskCandidates(theLightTaskInstance.getUUID());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        throw new RuntimeException("Error occured while building steps.");
                                    }
                                    if (taskCandidates.contains(aUserProfile.getUsername())) {
                                        // The current user belongs to the
                                        // candidates.
                                        theSteps.add(buildStep(theLightTaskInstance, aRequest, applicationURLs, aActivityInstanceCommentsCount, theQueryRuntimeAPI));
                                    }
                                }
                                break;
                            default:
                                break;
                            }
                        }
                    } else {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE, "According to rule, the step was ignored.");
                        }
                    }
                }
            }
        }

        Collections.sort(theSteps);
        Collections.reverse(theSteps);
        return theSteps;
    }

    private BonitaProcessUUID buildProcessUUID(final ProcessDefinitionUUID aProcessUUID, QueryDefinitionAPI aQueryDefinitionAPI) throws ProcessNotFoundException {
        try {
            LightProcessDefinition theProcessDefinition = aQueryDefinitionAPI.getLightProcess(aProcessUUID);
            String theProcessLabel = null;
            if (theProcessDefinition.getLabel() != null) {
                theProcessLabel = theProcessDefinition.getLabel();
            } else {
                theProcessLabel = theProcessDefinition.getName();
            }
            String theDescription = theProcessDefinition.getDescription();
            if (theDescription == null) {
                // Ensure that the description is set.
                theDescription = "";
            }
            BonitaProcessUUID theResult = new BonitaProcessUUID(theProcessDefinition.getUUID().toString(), theProcessLabel);
            return theResult;
        } catch (org.ow2.bonita.facade.exception.ProcessNotFoundException e) {
            throw new ProcessNotFoundException(new BonitaProcessUUID(e.getProcessId(), ""));
        }
    }

    private StepItem buildStep(LightActivityInstance anActivityInstance, HttpServletRequest aRequest, Map<ProcessDefinitionUUID, String> anApplicationURLs,
            Map<ActivityInstanceUUID, Integer> aActivityInstanceCommentsCount, QueryRuntimeAPI aQueryRuntimeAPI) throws ProcessNotFoundException {
        Set<String> theStepAssign = new HashSet<String>();
        StepPriority thePriority = null;
        String theAuthor = null;

        boolean isTask = anActivityInstance.isTask();
        if (isTask) {
            LightTaskInstance taskInstance = anActivityInstance.getTask();
            if (taskInstance.isTaskAssigned()) {
                theStepAssign.add(taskInstance.getTaskUser());
            } else {
                Set<String> taskCandidates = Collections.emptySet();
                try {
                    try {
                        taskCandidates = aQueryRuntimeAPI.getTaskCandidates(taskInstance.getUUID());
                    } catch (TaskNotFoundException e) {
                        aQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
                        taskCandidates = aQueryRuntimeAPI.getTaskCandidates(taskInstance.getUUID());
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error occured while building step: " + taskInstance.getUUID());
                }
                theStepAssign = taskCandidates;
            }
            theAuthor = taskInstance.getEndedBy();
            int theIntPriority = taskInstance.getPriority();
            thePriority = StepPriority.valueOf(Misc.getActivityPriority(theIntPriority, Locale.ENGLISH).toUpperCase());
        } else {
            theAuthor = "";
            thePriority = StepPriority.NONE;
        }

        final StepUUID theStepUUID = new StepUUID(anActivityInstance.getUUID().getValue(), anActivityInstance.getActivityDefinitionUUID().getValue());
        final StepState theStepState = StepState.valueOf(anActivityInstance.getState().name());

        final Date theReadyDate = anActivityInstance.getReadyDate();
        final Date theLastUpdateDate = anActivityInstance.getLastUpdateDate();

        final String theName = anActivityInstance.getActivityName();
        String activityLabel = anActivityInstance.getDynamicLabel();
		if (activityLabel == null || activityLabel.length() == 0) {
			activityLabel = anActivityInstance.getActivityLabel();
		}
		if (activityLabel != null && activityLabel.length() > 50) {
			activityLabel = activityLabel.substring(0, 50) + "...";
		}
        String activityDescription = anActivityInstance.getDynamicDescription();
        if (activityDescription == null || activityDescription.length() == 0) {
            activityDescription = anActivityInstance.getActivityDescription();
        }
        String theLabel = null;
        if (activityLabel != null) {
            theLabel = activityLabel;
        } else {
            theLabel = theName;
        }
        String theDescription = null;
        if (activityDescription != null) {
            theDescription = activityDescription;
        } else {
            theDescription = "";
        }

        StepItem theStepItem = new StepItem(theStepUUID, theName, theLabel, theStepState, theStepAssign, theDescription, theAuthor, theLastUpdateDate, isTask, thePriority, theReadyDate);
        theStepItem.setIsTimer(anActivityInstance.isTimer());
        
        if (!anActivityInstance.getProcessInstanceUUID().equals(anActivityInstance.getRootInstanceUUID())) {
            try {
                String url = anApplicationURLs.get(anActivityInstance.getProcessDefinitionUUID());
                if (url == null) {
                    url = ApplicationURLUtils.getInstance().getOrSetURLMetaData(aRequest, anActivityInstance.getProcessDefinitionUUID());
                    anApplicationURLs.put(anActivityInstance.getProcessDefinitionUUID(), url);
                }
                theStepItem.setApplicationURL(url);
            } catch (org.ow2.bonita.facade.exception.ProcessNotFoundException e) {
                throw new ProcessNotFoundException(new BonitaProcessUUID(e.getProcessId(), e.getProcessId()));
            }
        }
        if (aActivityInstanceCommentsCount != null && aActivityInstanceCommentsCount.containsKey(anActivityInstance.getUUID())) {
            theStepItem.setNumberOfComments(aActivityInstanceCommentsCount.get(anActivityInstance.getUUID()));
        }

        if (theStepState == StepState.FINISHED) {
            theStepItem.setExecutionSummary(anActivityInstance.getDynamicExecutionSummary());
        }
        return theStepItem;
    }

    /**
     * Build the list of cases the given user is involved in.
     * 
     * @param aCaseFilter
     * 
     * @param aUserProfile
     * @return
     * @throws CaseNotFoundException
     * @throws ProcessNotFoundException
     * @throws
     * @throws InstanceNotFoundException
     * @throws Exception
     */
    public CaseUpdates getAllCases(CaseFilter aCaseFilter, UserProfile aUserProfile, HttpServletRequest aRequest) throws ConsoleException, CaseNotFoundException, ConsoleSecurityException,
            ProcessNotFoundException {
        final List<LightProcessInstance> theProcessInstances;
        // Initially set to -1 as we will probably not know the size, except in
        // case
        // of a search or if the result is empty.
        int theTotalCaseAvailable = -1;
        List<CaseItem> theResult = null;

        final String theUserName = aUserProfile.getUsername();
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

        final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();
        final LabelUUID theFilteredLabel = aCaseFilter.getLabel();
        if (theFilteredLabel != null) {
            // Browsing a Label
            if (theFilteredLabel.equals(LabelModel.ADMIN_ALL_CASES.getUUID())) {
                if (aUserProfile.hasAccessToAdminCaseList()) {
                    try {
                        if (aUserProfile.isAdmin()) {
                            theProcessInstances = theCommandAPI.execute(new WebGetLightParentProcessInstances(aCaseFilter.getStartingIndex(), aCaseFilter.getMaxElementCount(), aCaseFilter
                                    .searchInHistory()));
                        } else {
                            theProcessInstances = theCommandAPI.execute(new WebGetManageableLightParentProcessInstances(aCaseFilter.getStartingIndex(), aCaseFilter.getMaxElementCount(), aCaseFilter
                                    .searchInHistory(), theUserID, theUserRoles, theUserGroups, theUserMemberships, theUserName));
                        }
                    } catch (Exception e) {
                        throw new ConsoleException("Unable to list all cases.", e);
                    }
                } else {
                    throw new ConsoleSecurityException(theUserName, "CaseDataStore.getAllCases(" + LabelModel.ADMIN_ALL_CASES.getUUID() + ")");
                }
            } else if (theFilteredLabel.equals(LabelModel.INBOX_LABEL.getUUID())) {
                // Retrieving cases the user is member of the active users.
                if (!aCaseFilter.searchInHistory()) {
                    try {
                        theProcessInstances = theCommandAPI.execute(new WebGetLightProcessInstancesFromActiveUsers(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUserName, aCaseFilter
                                .getStartingIndex(), aCaseFilter.getMaxElementCount()));
                    } catch (Exception e) {
                        throw new ConsoleException("Unable to list cases in which the user is active.", e);
                    }
                } else {
                    theProcessInstances = null;
                }
            } else if (theFilteredLabel.equals(LabelModel.MY_CASES_LABEL.getUUID())) {
                // Retrieve cases the user has started.
                try {
                    theProcessInstances = theCommandAPI.execute(new WebGetLightParentUserInstancesCommand(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUserName, aCaseFilter
                            .getStartingIndex(), aCaseFilter.getMaxElementCount(), aCaseFilter.searchInHistory()));
                } catch (Exception e) {
                    throw new ConsoleException("Unable to list cases started by user.", e);
                }
            } else if (theFilteredLabel.equals(LabelModel.ALL_LABEL.getUUID())) {
                // Retrieve cases the user is involved in.
                try {
                    theProcessInstances = theCommandAPI.execute(new WebGetLightProcessInstancesFromInvolvedUsers(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUserName, aCaseFilter
                            .getStartingIndex(), aCaseFilter.getMaxElementCount(), aCaseFilter.searchInHistory()));
                } catch (Exception e) {
                    throw new ConsoleException("Unable to list cases the user is involved in.", e);
                }

            } else if (theFilteredLabel.equals(LabelModel.ATRISK_LABEL.getUUID())) {
                // Retrieve cases with steps at risk
                final int theNbOfRemainingDays = PreferencesDataStore.getInstance().getIntegerValue(PreferencesDataStore.REMAINING_DAYS_FOR_STEP_ATRISK_KEY, DEFAULT_REMAINING_DAYS);
                try {
                    theProcessInstances = theCommandAPI.execute(new WebGetLightProcessInstancesFromActiveUsersAndActivityInstanceExpectedEndDate(theUserID, theUserRoles, theUserGroups,
                            theUserMemberships, theUserName, theNbOfRemainingDays, aCaseFilter.getStartingIndex(), aCaseFilter.getMaxElementCount(), aCaseFilter.searchInHistory()));
                } catch (Exception e) {
                    throw new ConsoleException("Unable to list cases with tasks at risk.", e);
                }
            } else if (theFilteredLabel.equals(LabelModel.OVERDUE_LABEL.getUUID())) {
                // Retrieve cases with steps at risk
                try {
                    theProcessInstances = theCommandAPI.execute(new WebGetLightProcessInstancesWithOverdueTasksCommand(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUserName,
                            aCaseFilter.getStartingIndex(), aCaseFilter.getMaxElementCount(), aCaseFilter.searchInHistory()));
                } catch (Exception e) {
                    throw new ConsoleException("Unable to list cases with overdue tasks.", e);
                }
            } /*
               * //TODO OnTrack, Urgent, High, Normal
               */else {
                final WebAPI theWebAPI = AccessorUtil.getWebAPI();
                final HashSet<String> theLabelsName = new HashSet<String>();
                // Translate the label UUIDs into String.
                theLabelsName.add(theFilteredLabel.getValue());
                theProcessInstances = theWebAPI.getLightProcessInstances(theUserName, theLabelsName, aCaseFilter.getStartingIndex(), aCaseFilter.getMaxElementCount());
            }
        } else if (aCaseFilter.getCategory() != null) {
            final Category theFilteredCategory = aCaseFilter.getCategory();
            // Browsing a Category
            // Retrieve cases of process in a category
            try {
                theProcessInstances = theCommandAPI.execute(new WebGetLightProcessInstancesFromCategoryCommand(theUserName, theFilteredCategory.getName(), aCaseFilter.getStartingIndex(), aCaseFilter
                        .getMaxElementCount(), aCaseFilter.searchInHistory()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new ConsoleException("Unable to list cases from category", e);
            }
        } else {
            throw new ConsoleException("Invalid state: either a Label or a Category must be used as filter!", null);
        }

        if (theProcessInstances != null && !theProcessInstances.isEmpty()) {
            theResult = buildCases(aUserProfile, aCaseFilter, theProcessInstances, aRequest);

            // If the number of elements is less than the page's size it means
            // that
            // there is no more elements matching the request.
            // Otherwise return -1 --> will trigger a subsequent call to compute
            // size.
            if (theResult.size() < aCaseFilter.getMaxElementCount()) {
                theTotalCaseAvailable = aCaseFilter.getStartingIndex() + theResult.size();
            }
            return new CaseUpdates(theResult, theTotalCaseAvailable);

        } else {
            return new CaseUpdates(new ArrayList<CaseItem>(), 0);
        }

    }

    /**
     * Delete the cases given in parameter.
     * 
     * @param aCaseSelection
     * @throws Exception
     * @throws UndeletableInstanceException
     * @throws InstanceNotFoundException
     */
    public void deleteCases(Collection<CaseUUID> aCaseSelection, final boolean deleteAttachments) throws Exception {
        if (aCaseSelection != null && !aCaseSelection.isEmpty()) {
            Set<ProcessInstanceUUID> theProcessInstancesToDelete = new HashSet<ProcessInstanceUUID>();
            for (CaseUUID theCaseUUID : aCaseSelection) {
                theProcessInstancesToDelete.add(new ProcessInstanceUUID(theCaseUUID.toString()));
            }
            CommandAPI commandAPI = AccessorUtil.getCommandAPI();
            if (deleteAttachments) {
                commandAPI.execute(new WebDeleteDocumentsOfProcessInstancesCommand(theProcessInstancesToDelete));
            }
            commandAPI.execute(new WebDeleteProcessInstancesCommand(theProcessInstancesToDelete));
        }
    }

    /**
     * 
     * @param aCaseFilter
     * @param aCaseSelection
     * @param aRequest
     * @throws InstanceNotFoundException
     * @throws UncancellableInstanceException
     * @throws ConsoleException
     * @throws Exception
     */
    public CaseUpdates cancelCases(CaseFilter aCaseFilter, final UserProfile aUserProfile, Collection<CaseUUID> aCaseSelection, HttpServletRequest aRequest) throws CaseNotFoundException,
            UncancellableInstanceException, ConsoleException, Exception {
        if (aCaseSelection != null && !aCaseSelection.isEmpty()) {
            ArrayList<ProcessInstanceUUID> theProcessInstances = new ArrayList<ProcessInstanceUUID>();
            for (CaseUUID theCaseUUID : aCaseSelection) {
                theProcessInstances.add(new ProcessInstanceUUID(theCaseUUID.getValue()));
            }
            AccessorUtil.getRuntimeAPI().cancelProcessInstances(theProcessInstances);
            CaseUpdates theResult = getAllCases(aCaseFilter, aUserProfile, aRequest);
            return theResult;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public CaseItem getCase(CaseUUID aCaseUUID, UserProfile aUserProfile, CaseFilter aFilter, HttpServletRequest aRequest) throws InstanceNotFoundException, ConsoleException, ProcessNotFoundException {
        final QueryRuntimeAPI queryRuntimeAPI;
        final QueryDefinitionAPI queryDefinitionAPI;
        if (aFilter.searchInHistory()) {
            queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
            // We cannot know where the definition is.
            queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        } else {
            queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
            queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        }
        LightProcessInstance theProcessInstance = queryRuntimeAPI.getLightProcessInstance(new ProcessInstanceUUID(aCaseUUID.toString()));
        if (null == theProcessInstance) {
            return null;
        }
        final List<LightActivityInstance> lightActivityInstances = queryRuntimeAPI.getLightActivityInstancesFromRoot(theProcessInstance.getUUID());
        final Map<ActivityInstanceUUID, Integer> theActivityInstanceComments = getActivitiesCommentCount(lightActivityInstances);
        final Map<ProcessInstanceUUID, Boolean> processInstancesIsActiveUser;
        if (!aFilter.searchInHistory()) {
            // Active users are only available when not looking in archives.
            try {
                final CommandAPI commandAPI = AccessorUtil.getCommandAPI();
                final Set<ProcessInstanceUUID> theInstanceUUIDs = new HashSet<ProcessInstanceUUID>();
                theInstanceUUIDs.add(theProcessInstance.getUUID());
                processInstancesIsActiveUser = commandAPI.execute(new WebIsUserActiveInProcessInstancesCommand(theInstanceUUIDs, aUserProfile.getUsername()));
            } catch (Exception e) {
                throw new ConsoleException("Unbale to check if user is active in process instances.", e);
            }
        } else {
            processInstancesIsActiveUser = null;
        }
        // Get the labels for the given case.
        final List<LabelModel> theLabels = LabelDataStore.getInstance().getLabelsOfCase(aUserProfile, theProcessInstance);
        final CaseItem theResult = buildCase(aUserProfile, aFilter, theProcessInstance, lightActivityInstances, theActivityInstanceComments, buildProcessUUID(theProcessInstance
                .getProcessDefinitionUUID(), queryDefinitionAPI), theLabels, aRequest, (processInstancesIsActiveUser != null && processInstancesIsActiveUser.get(theProcessInstance.getUUID())));

        return theResult;
    }

    protected Map<ActivityInstanceUUID, Integer> getActivitiesCommentCount(List<LightActivityInstance> aLightActivityInstances) throws InstanceNotFoundException {
        Set<ActivityInstanceUUID> theActivities = new HashSet<ActivityInstanceUUID>();
        for (LightActivityInstance anActivity : aLightActivityInstances) {
            theActivities.add(anActivity.getUUID());
        }
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Map<ActivityInstanceUUID, Integer> theResult = queryRuntimeAPI.getNumberOfActivityInstanceComments(theActivities);
        return theResult;
    }

    /**
     * Call the WebAPI to force the Runtime API and the WebAPI do get back to
     * synchronization state in term of case UUIDs.
     * 
     * @throws ConsoleException
     */
    public void synchronizeDBs() throws ConsoleException {
        final long start = System.currentTimeMillis();
        try {
            final WebAPI theWebAPI = AccessorUtil.getWebAPI();
            theWebAPI.deletePhantomCases();
        } catch (Exception theE) {
            throw new ConsoleException();
        } finally {
            System.err.println("Time of synchronizeDBs: " + (System.currentTimeMillis() - start) + " ms.");
        }
    }

    /**
     * Get cases based on their UUID.
     * 
     * @param aUserProfile
     * @param aCaseSelection
     * @param aCaseFilter
     * @param aRequest
     * @return
     * @throws ConsoleException
     * @throws InstanceNotFoundException
     * @throws Exception
     */
    public Collection<CaseItem> getCases(UserProfile aUserProfile, Collection<CaseUUID> aCaseSelection, CaseFilter aCaseFilter, HttpServletRequest aRequest) throws ConsoleException,
            InstanceNotFoundException, Exception {
        try {
            if (aUserProfile == null || aCaseSelection == null || aCaseSelection.isEmpty()) {
                throw new IllegalArgumentException();
            }
            final QueryRuntimeAPI queryRuntimeAPI;
            if (aCaseFilter.searchInHistory()) {
                queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
            } else {
                queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
            }
            List<ProcessInstanceUUID> theProcessInstanceUUIDs = new ArrayList<ProcessInstanceUUID>();
            for (CaseUUID theCaseUUID : aCaseSelection) {
                theProcessInstanceUUIDs.add(new ProcessInstanceUUID(theCaseUUID.getValue()));
            }
            Collection<LightProcessInstance> theProcessInstances = queryRuntimeAPI.getLightProcessInstances(theProcessInstanceUUIDs);
            if (null == theProcessInstances || theProcessInstances.isEmpty()) {
                return new ArrayList<CaseItem>();
            }
            return buildCases(aUserProfile, aCaseFilter, theProcessInstances, aRequest);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * List all the comments attached to the case, i.e., comments of all the
     * cases steps.
     * 
     * @param aCaseUUID
     * @param aUserProfile
     * @param aFilter
     * @return
     * @throws SessionTimeOutException
     * @throws ConsoleException
     * @throws InstanceNotFoundException
     */
    public List<CommentItem> getCaseCommentFeed(CaseUUID aCaseUUID, UserProfile aUserProfile, CaseFilter aFilter) throws SessionTimeOutException, ConsoleException, InstanceNotFoundException {
        if (aCaseUUID == null) {
            throw new IllegalArgumentException("The case UUID must be not null!");
        }

        final QueryRuntimeAPI queryRuntimeAPI;
        if (aFilter.searchInHistory()) {
            queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
        } else {
            queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        }

        List<Comment> theComments = queryRuntimeAPI.getCommentFeed(new ProcessInstanceUUID(aCaseUUID.getValue()));
        List<CommentItem> theResult = null;
        if (theComments != null) {
            theResult = new ArrayList<CommentItem>();
            for (Comment theComment : theComments) {
                theResult.add(buildCommentItem(theComment));
            }
        }
        return theResult;
    }

    /**
     * @param aUserProfile
     * @param aCaseUUID
     * @param aComment
     * @return
     */
    public List<CommentItem> addCommentToCase(CaseUUID aCaseUUID, String aComment, UserProfile aUserProfile, CaseFilter aFilter, HttpServletRequest aRequest) throws SessionTimeOutException,
            ConsoleException, CaseNotFoundException {
        if (aCaseUUID == null) {
            throw new ConsoleException("The case UUID must be not null!", null);
        }
        RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
        try {
            theRuntimeAPI.addComment(new ProcessInstanceUUID(aCaseUUID.getValue()), aComment, aUserProfile.getUsername());
            List<CommentItem> theResult = getCaseCommentFeed(aCaseUUID, aUserProfile, aFilter);
            return theResult;
        } catch (InstanceNotFoundException e) {
            throw new CaseNotFoundException(e.getInstanceUUID().getValue());
        }

    }

    /**
     * Build a commentItem based on a comment issue from the engine.
     * 
     * @param aComment
     * @return
     */
    private CommentItem buildCommentItem(Comment aComment) {
        CommentItem theResult;
        if (aComment.getActivityUUID() != null) {
            theResult = new CommentItem(aComment.getActivityUUID().getValue(), aComment.getActivityUUID().getActivityDefinitionUUID().getValue(), aComment.getUserId(), aComment.getDate(), aComment
                    .getMessage());
        } else {
            theResult = new CommentItem(aComment.getInstanceUUID().getValue(), aComment.getUserId(), aComment.getDate(), aComment.getMessage());
        }
        return theResult;
    }

    /**
     * Read configuration.
     */
    public CasesConfiguration getConfiguration() {
        final CasesConfiguration theConfiguration = new CasesConfiguration();
        final String theLayout = PreferencesDataStore.getInstance().getStringValue(TenancyProperties.CASE_LIST_LAYOUT_KEY, null);
        theConfiguration.setLayout(theLayout);
        final int theStretchedColumn = PreferencesDataStore.getInstance().getIntegerValue(TenancyProperties.CASE_LIST_STRETCHED_COLUMN_KEY, 10);
        theConfiguration.setStretchedColumnIndex(theStretchedColumn);
        return theConfiguration;
    }

    /**
     * Persist new configuration.
     * 
     * @param aUserProfile
     * @param aConfiguration
     * @throws ConsoleException
     */
    public CasesConfiguration updateConfiguration(UserProfile aUserProfile, CasesConfiguration aConfiguration) throws ConsoleException {
        String theLayout = aConfiguration.getColumnLayout();
        int theStretchedColumnIndex = aConfiguration.getStretchedColumnIndex();
        try {
            final PreferencesDataStore thePreferencesDataStore = PreferencesDataStore.getInstance();
            thePreferencesDataStore.setPreference(TenancyProperties.CASE_LIST_LAYOUT_KEY, theLayout);
            thePreferencesDataStore.setIntegerPreference(TenancyProperties.CASE_LIST_STRETCHED_COLUMN_KEY, theStretchedColumnIndex);
            return getConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConsoleException("Unable to update case list layout configuration.", e);
        }

    }
}
