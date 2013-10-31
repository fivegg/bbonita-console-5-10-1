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
package org.bonitasoft.console.server.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.EventFilter;
import org.bonitasoft.console.client.steps.EventItem;
import org.bonitasoft.console.client.steps.StepDefinition;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepPriority;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.steps.exceptions.EventNotFoundException;
import org.bonitasoft.console.client.steps.exceptions.StepNotFoundException;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.runtime.Comment;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightTaskInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.Misc;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepDataStore {

    private static final StepDataStore INSTANCE = new StepDataStore();

    /**
     * @return the iNSTANCE
     */
    public static StepDataStore getInstance() {
        return INSTANCE;
    }

    /**
     * Default constructor.
     */
    protected StepDataStore() {
        super();
    }

    /**
     * 
     * @param aStepUUID
     * @return
     * @throws TaskNotFoundException
     */
    public Set<UserUUID> unassignStep(StepUUID aStepUUID) throws TaskNotFoundException {
        ActivityInstanceUUID theTaskUUID = new ActivityInstanceUUID(aStepUUID.toString());
        // Update task on engine.
        AccessorUtil.getRuntimeAPI().unassignTask(theTaskUUID);
        LightTaskInstance task = AccessorUtil.getQueryRuntimeAPI().getLightTaskInstance(theTaskUUID);
        Set<UserUUID> theResult = new HashSet<UserUUID>();
        if (task.isTaskAssigned()) {
            theResult.add(new UserUUID(task.getTaskUser()));
        } else {
            Set<String> taskCandidates = AccessorUtil.getQueryRuntimeAPI().getTaskCandidates(task.getUUID());
            for (String theString : taskCandidates) {
                theResult.add(new UserUUID(theString));
            }
        }
        return theResult;
    }

    /**
     * 
     * @param aStepUUID
     * @param isAssigned
     * @throws TaskNotFoundException
     * @throws IllegalTaskStateException
     */
    public void suspendStep(StepUUID aStepUUID) throws TaskNotFoundException, IllegalTaskStateException {
        ActivityInstanceUUID theTaskUUID = new ActivityInstanceUUID(aStepUUID.toString());
        // Suspend task on engine.
        AccessorUtil.getRuntimeAPI().suspendTask(theTaskUUID, false);

    }

    /**
     * 
     * @param aStepUUID
     * @throws TaskNotFoundException
     * @throws IllegalTaskStateException
     */
    public void resumeStep(StepUUID aStepUUID) throws TaskNotFoundException, IllegalTaskStateException {
        ActivityInstanceUUID theTaskUUID = new ActivityInstanceUUID(aStepUUID.toString());
        // Resume task on engine.
        AccessorUtil.getRuntimeAPI().resumeTask(theTaskUUID, false);
    }

    /**
     * 
     * @param aStepUUID
     * @param aCandidateSet
     * @throws TaskNotFoundException
     */
    public void assignStep(StepUUID aStepUUID, Set<UserUUID> aCandidateSet) throws TaskNotFoundException {
        ActivityInstanceUUID theTaskUUID = new ActivityInstanceUUID(aStepUUID.toString());
        Set<String> theCandidates = new HashSet<String>();
        for (UserUUID theUserUUID : aCandidateSet) {
            theCandidates.add(theUserUUID.toString());
        }
        // Assign task on engine.
        RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
        if (theCandidates.size() > 1) {
            theRuntimeAPI.assignTask(theTaskUUID, theCandidates);
        } else {
            theRuntimeAPI.assignTask(theTaskUUID, theCandidates.iterator().next());
        }

    }

    /**
     * 
     * @param aStepUUID
     * @param anActorId
     * @throws TaskNotFoundException
     */
    public void assignStep(StepUUID aStepUUID, UserUUID anActorId) throws TaskNotFoundException {
        ActivityInstanceUUID theTaskUUID = new ActivityInstanceUUID(aStepUUID.toString());
        // Assign task on engine.
        AccessorUtil.getRuntimeAPI().assignTask(theTaskUUID, anActorId.toString());

    }

    public List<CommentItem> getStepCommentFeed(StepUUID aStepUUID) throws SessionTimeOutException, ConsoleException, InstanceNotFoundException {
        if (aStepUUID == null) {
            throw new IllegalArgumentException("The step UUID must be not null!");
        }

        QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

        List<Comment> theComments = theQueryRuntimeAPI.getActivityInstanceCommentFeed(new ActivityInstanceUUID(aStepUUID.getValue()));
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
     * Build a commentItem based on a comment issue from the engine.
     * 
     * @param aComment
     * @return
     */
    private CommentItem buildCommentItem(final Comment aComment) {
        return new CommentItem(aComment.getActivityUUID().getValue(), aComment.getActivityUUID().getActivityDefinitionUUID().getValue(), aComment.getUserId(), aComment.getDate(), aComment
                .getMessage());
    }

    /**
     * 
     * @param aStepUUID
     * @param aComment
     * @param aUserProfile
     * @return
     * @throws InstanceNotFoundException
     * @throws ActivityNotFoundException
     */
    public List<CommentItem> addStepComment(final StepUUID aStepUUID, final String aComment, final UserProfile aUserProfile) throws InstanceNotFoundException, ActivityNotFoundException {
        if (aStepUUID == null || aComment == null || aComment.length() == 0) {
            throw new IllegalArgumentException();
        }

        final QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
        final ActivityInstanceUUID theActivityInstanceUUID = new ActivityInstanceUUID(aStepUUID.getValue());
        theRuntimeAPI.addComment(theActivityInstanceUUID, aComment, aUserProfile.getUsername());
        final List<Comment> theComments = theQueryRuntimeAPI.getActivityInstanceCommentFeed(new ActivityInstanceUUID(aStepUUID.getValue()));
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
     * Modify a step's priority
     * 
     * @param aStepUUID
     * @param aPriority
     * @return the value of the new Priority as a String
     * @throws ActivityNotFoundException
     */
    public String setStepPriority(StepUUID aStepUUID, int aPriority) throws ActivityNotFoundException {
        ActivityInstanceUUID theTaskUUID = new ActivityInstanceUUID(aStepUUID.toString());
        AccessorUtil.getRuntimeAPI().setActivityInstancePriority(theTaskUUID, aPriority);
        return Misc.getActivityPriority(aPriority, Locale.ENGLISH);
    }

    /**
     * @param anItemFilter
     * @return
     */
    public ItemUpdates<StepItem> getAllSteps(StepFilter anItemFilter) throws ConsoleException {
        throw new ConsoleException("Not yet implemented", null);
    }

    /**
     * @param anItemUUID
     * @param aFilter
     * @return
     */
    public StepItem getStep(StepUUID anItemUUID, StepFilter aFilter) throws ConsoleException, StepNotFoundException {
        final QueryRuntimeAPI theQueryRuntimeAPI;
        if (aFilter.searchInHistory()) {
            theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
        } else {
            theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        }
        try {
            LightActivityInstance theTask = theQueryRuntimeAPI.getLightActivityInstance(new ActivityInstanceUUID(anItemUUID.getValue()));
            return buildStepItem(theTask);
        } catch (ActivityNotFoundException e) {
            throw new StepNotFoundException(anItemUUID.getValue());
        }
    }

    /**
     * @param aTask
     */
    private StepItem buildStepItem(LightActivityInstance aTask) {
        final StepUUID theStepUUID = new StepUUID(aTask.getUUID().getValue(), aTask.getActivityDefinitionUUID().getValue());
        final String theStepName = aTask.getActivityName();
        final String theStepLabel = aTask.getActivityLabel();
        final StepState theStepState = StepState.valueOf(aTask.getState().name());
        final String theStepDescription = aTask.getDynamicDescription();
        final Date theLastUpdateDate = aTask.getLastUpdateDate();
        boolean isTask = aTask.isTask();
        final String theAuthor;
        if (isTask) {
            theAuthor = aTask.getTask().getEndedBy();
        } else {
            theAuthor = null;
        }
        final StepPriority thePriority = StepPriority.valueOf(Misc.getActivityPriority(aTask.getPriority(), Locale.ENGLISH).toUpperCase());
        final Date theReadyDate = aTask.getReadyDate();
        final StepItem theStepItem = new StepItem(theStepUUID, theStepName, theStepLabel, theStepState, theStepDescription, theLastUpdateDate, isTask, thePriority, theReadyDate);
        if (theAuthor != null) {
            theStepItem.setAuthor(new UserUUID(theAuthor));
        } else {
            theStepItem.setAuthor(null);
        }
        theStepItem.setIsTimer(aTask.isTimer());
        return theStepItem;
    }

    /**
     * @param anItemSelection
     * @param aFilter
     * @return
     */
    public List<StepItem> getSteps(List<StepUUID> anItemSelection, StepFilter aFilter) throws ConsoleException, StepNotFoundException {
        throw new ConsoleException("Not yet implemented", null);
    }

    /**
     * @param aAnItemUUID
     * @param aFilter
     * @throws StepNotFoundException
     * @throws ConsoleException
     */
    public StepItem skipStep(StepUUID anItemUUID, StepFilter aFilter) throws StepNotFoundException, ConsoleException {
        final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
        try {
            theRuntimeAPI.skipTask(new ActivityInstanceUUID(anItemUUID.getValue()), null);
        } catch (TaskNotFoundException e) {
            throw new StepNotFoundException(anItemUUID.getValue());
        } catch (IllegalTaskStateException e) {
            throw new ConsoleException("Invalid step state.", e);
        }
        try {
            return getStep(anItemUUID, aFilter);
        } catch (StepNotFoundException e) {
            // The step cannot be found as the instance may have been archived.
            // Try one more time in the history.
            final StepFilter theFilter = new StepFilter(aFilter.getProcessUUID(), aFilter.getStartingIndex(), aFilter.getMaxElementCount());
            theFilter.updateFilter(aFilter);
            theFilter.setSearchInHistory(true);
            return getStep(anItemUUID, theFilter);
        }
    }

    /**
     * @param aFilter
     * @param aAnItemUUID
     * @return
     * @throws ConsoleException
     * @throws EventNotFoundException
     * @throws StepNotFoundException
     */
    public StepItem executeTimer(StepUUID anItemUUID, StepFilter aFilter) throws EventNotFoundException, ConsoleException, StepNotFoundException {
        final EventFilter theEventFilter = new EventFilter(0, 20);
        theEventFilter.setStepUUID(anItemUUID);
        final EventDataStore theEventDataStore = EventDataStore.getInstance();
        final ItemUpdates<EventItem> theEvents = theEventDataStore.getAllEvents(theEventFilter);
        if (theEvents != null && theEvents.getItems() != null) {
            final List<EventItem> theEventItems = theEvents.getItems();
            if (theEventItems.size() == 1) {
                theEventDataStore.executeEvent(theEventItems.get(0).getUUID());
            }
        }
        try {
            return getStep(anItemUUID, aFilter);
        } catch (StepNotFoundException e) {
            // The step cannot be found as the instance may have been archived.
            // Try one more time in the history.
            final StepFilter theFilter = new StepFilter(aFilter.getProcessUUID(), aFilter.getStartingIndex(), aFilter.getMaxElementCount());
            theFilter.updateFilter(aFilter);
            theFilter.setSearchInHistory(true);
            return getStep(anItemUUID, theFilter);
        }
    }

    /**
     * @param aAnItemUUID
     * @param aNewValue
     * @param aStepFilter
     * @return
     * @throws StepNotFoundException
     * @throws ConsoleException
     * @throws EventNotFoundException
     */
    public StepItem updateTimer(StepUUID anItemUUID, Date aNewValue, StepFilter aStepFilter) throws ConsoleException, StepNotFoundException, EventNotFoundException {
        final EventFilter theEventFilter = new EventFilter(0, 20);
        theEventFilter.setStepUUID(anItemUUID);
        final EventDataStore theEventDataStore = EventDataStore.getInstance();
        final ItemUpdates<EventItem> theEvents = theEventDataStore.getAllEvents(theEventFilter);
        if (theEvents != null && theEvents.getItems() != null) {
            final List<EventItem> theEventItems = theEvents.getItems();
            if (theEventItems.size() == 1) {
                theEventDataStore.updateEvent(theEventItems.get(0).getUUID(), aNewValue);
            }
        }
        try {
            return getStep(anItemUUID, aStepFilter);
        } catch (StepNotFoundException e) {
            // The step cannot be found as the instance may have been archived.
            // Try one more time in the history.
            final StepFilter theFilter = new StepFilter(aStepFilter.getProcessUUID(), aStepFilter.getStartingIndex(), aStepFilter.getMaxElementCount());
            theFilter.updateFilter(aStepFilter);
            theFilter.setSearchInHistory(true);
            return getStep(anItemUUID, theFilter);
        }
    }

    /**
     * @param aAnItemUUID
     * @param aStepFilter
     * @return
     * @throws ConsoleException
     * @throws EventNotFoundException
     * @throws StepNotFoundException
     */
    public StepItem cancelTimer(StepUUID anItemUUID, StepFilter aStepFilter) throws ConsoleException, EventNotFoundException, StepNotFoundException {
        final EventFilter theEventFilter = new EventFilter(0, 20);
        theEventFilter.setStepUUID(anItemUUID);
        final EventDataStore theEventDataStore = EventDataStore.getInstance();
        final ItemUpdates<EventItem> theEvents = theEventDataStore.getAllEvents(theEventFilter);
        if (theEvents != null && theEvents.getItems() != null) {
            final List<EventItem> theEventItems = theEvents.getItems();
            if (theEventItems.size() == 1) {
                theEventDataStore.deleteEvents(Arrays.asList(theEventItems.get(0).getUUID()), theEventFilter);
            }
        }
        try {
            return getStep(anItemUUID, aStepFilter);
        } catch (StepNotFoundException e) {
            // The step cannot be found as the instance may have been archived.
            // Try one more time in the history.
            final StepFilter theFilter = new StepFilter(aStepFilter.getProcessUUID(), aStepFilter.getStartingIndex(), aStepFilter.getMaxElementCount());
            theFilter.updateFilter(aStepFilter);
            theFilter.setSearchInHistory(true);
            return getStep(anItemUUID, theFilter);
        }
    }

    /*
     * Step definition
     */
    /**
     * @param aTaskIDs
     * @return
     */
    private List<StepDefinition> buildStepsDefinition(Locale aLocale, Set<ActivityDefinition> aSetOfActivities) {
        List<StepDefinition> theResult = new ArrayList<StepDefinition>();
        for (ActivityDefinition theActivityDefinition : aSetOfActivities) {

            theResult.add(buildStepDefinition(aLocale, theActivityDefinition));
        }
        return theResult;
    }

    /**
     * @param aActivityDefinition
     * @return
     */
    private StepDefinition buildStepDefinition(Locale aLocale, ActivityDefinition anActivityDefinition) {
        StepUUID theStepUUID = new StepUUID(anActivityDefinition.getUUID().getValue(), anActivityDefinition.getUUID().getValue());
        final String theLabel;
        if (anActivityDefinition.getLabel() != null) {
            theLabel = anActivityDefinition.getLabel();
        } else {
            theLabel = anActivityDefinition.getName();
        }
        final String theDescription = null;
        final StepDefinition.StepPriority thePriority = StepDefinition.StepPriority.valueOf(Misc.getActivityPriority(anActivityDefinition.getPriority(), aLocale).toUpperCase());
        final StepDefinition theStepDefinition = new StepDefinition(theStepUUID, anActivityDefinition.getName(), theLabel, anActivityDefinition.getPerformers(), theDescription, anActivityDefinition
                .isTask(), thePriority);
        theStepDefinition.setProcessName(anActivityDefinition.getProcessDefinitionUUID().getProcessName());
        theStepDefinition.setProcessVersion(anActivityDefinition.getProcessDefinitionUUID().getProcessVersion());
        return theStepDefinition;
    }

    /**
     * @param anItemUUID
     * @param aFilter
     * @returns
     * @throws StepNotFoundException
     * @throws ConsoleException
     */
    public StepDefinition getStepDefinition(StepUUID anItemUUID, StepFilter aFilter) throws StepNotFoundException, ConsoleException {
        final QueryDefinitionAPI theQueryDefinitionAPI;
        if (aFilter.searchInHistory()) {
            theQueryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
        } else {
            theQueryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        }
        ActivityDefinitionUUID theActivityUUID = new ActivityDefinitionUUID(anItemUUID.getValue());
        try {

            ActivityDefinition theActivity = theQueryDefinitionAPI.getProcessActivity(theActivityUUID.getProcessUUID(), theActivityUUID.getActivityName());
            if (theActivity != null) {
                return buildStepDefinition(Locale.ENGLISH, theActivity);
            } else {
                throw new StepNotFoundException(theActivityUUID.getActivityName());
            }
        } catch (ProcessNotFoundException e) {
            throw new ConsoleException("Process not found.", e);
        } catch (ActivityNotFoundException e) {
            throw new StepNotFoundException(theActivityUUID.getActivityName());
        }

    }

    /**
     * @param aAnItemFilter
     * @return
     */
    public ItemUpdates<StepDefinition> getStepsDefinition(StepFilter anItemFilter) throws ConsoleException {
        final BonitaProcessUUID theProcessUUID = anItemFilter.getProcessUUID();
        if (theProcessUUID != null) {
            final QueryDefinitionAPI theQueryDefinitionAPI;
            if (anItemFilter.searchInHistory()) {
                theQueryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
            } else {
                theQueryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
            }

            try {
                Set<ActivityDefinition> theActivities = theQueryDefinitionAPI.getProcessActivities(new ProcessDefinitionUUID(theProcessUUID.getValue()));
                if (theActivities != null) {
                    List<StepDefinition> theSteps = buildStepsDefinition(Locale.ENGLISH, theActivities);
                    Collections.sort(theSteps);
                    return new ItemUpdates<StepDefinition>(theSteps, theSteps.size());
                } else {
                    return new ItemUpdates<StepDefinition>(new ArrayList<StepDefinition>(), 0);
                }
            } catch (ProcessNotFoundException e) {
                throw new ConsoleException("Process not found.", e);
            }

        } else {
            throw new ConsoleException("You must specify a process to search step definition!", null);
        }

    }

    /**
     * @param anItemSelection
     * @param aFilter
     * @return
     * @throws ConsoleException
     * @throws StepNotFoundException
     */
    public List<StepDefinition> getStepsDefinition(List<StepUUID> anItemSelection, StepFilter aFilter) throws StepNotFoundException, ConsoleException {

        ArrayList<StepDefinition> theResult = new ArrayList<StepDefinition>();
        // FIXME
        for (StepUUID theStepUUID : anItemSelection) {
            theResult.add(getStepDefinition(theStepUUID, aFilter));
        }
        return theResult;
    }
}
