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
package org.bonitasoft.console.client.steps;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Nicolas Chabanoles
 * 
 */
public interface StepServiceAsync {

    /**
     * Assign a step to an actor.
     * 
     * @param aStepUUID
     * @param anActorId
     * @param aCallBackHandler
     */
    public void assignStep(StepUUID aStepUUID, UserUUID anActorId, AsyncCallback<Void> aCallBackHandler);

    /**
     * Assign a step to a bunch of candidates.
     * 
     * @param aStepUUID
     * @param aCandidateSet
     * @param aCallBackHandler
     */
    public void assignStep(StepUUID aStepUUID, Set<UserUUID> aCandidateSet, AsyncCallback<Void> aCallBackHandler);

    /**
     * Suspend a step.
     * 
     * @param aStepUUID
     * @param isAssigned
     * @param aCallBackHandler
     */
    public void suspendStep(StepUUID aStepUUID, AsyncCallback<Void> aCallBackHandler);

    /**
     * Resume a step.
     * 
     * @param aStepUUID
     * @param aCallBackHandler
     */
    public void resumeStep(StepUUID aStepUUID, AsyncCallback<Void> aCallBackHandler);

    /**
     * Unassign a step.
     * 
     * @param aStepUUID
     * @param aCallBackHandler
     */
    public void unassignStep(StepUUID aStepUUID, AsyncCallback<Set<UserUUID>> aCallBackHandler);

    /**
     * List the comments attached to a step.
     * 
     * @param aStepUUID
     * @param aCallBackHandler
     */
    public void getStepCommentFeed(StepUUID aStepUUID, AsyncCallback<List<CommentItem>> aCallBackHandler);

    /**
     * Add a comment to the given step.
     * 
     * @param aStepUUID
     * @param aComment
     * @param aChainedCallback
     */
    public void addStepComment(StepUUID aStepUUID, String aComment, AsyncCallback<List<CommentItem>> aCallBackHandler);

    /**
     * Modify a step's priority
     * 
     * @param aStep
     * @param priority
     * @param aCallBackHandler
     */
    public void setStepPriority(StepUUID aStepUUID, int priority, AsyncCallback<String> aCallBackHandler);

    /**
     * List steps that match the given filter.
     * 
     * @param anItemFilter
     * @param aCallBackHandler
     */
    public void getAllSteps(StepFilter anItemFilter, AsyncCallback<ItemUpdates<StepItem>> aCallBackHandler);

    /**
     * @param anItemUUID
     * @param aFilter
     * @param aChainedCallback
     */
    public void getStep(StepUUID anItemUUID, StepFilter aFilter, AsyncCallback<StepItem> aCallBackHandler);

    /**
     * @param anItemSelection
     * @param aFilter
     * @param aChainedCallback
     */
    public void getSteps(List<StepUUID> anItemSelection, StepFilter aFilter, AsyncCallback<List<StepItem>> aCallBackHandler);

    /**
     * @param aAnItemUUID
     * @param aFilter
     * @param aChainedCallback
     */
    public void skipStep(StepUUID anItemUUID, StepFilter aFilter, AsyncCallback<StepItem> aCallBackHandler);

    public void executeTimer(StepUUID anItemUUID, StepFilter aStepFilter, AsyncCallback<StepItem> aCallBackHandler);

    /**
     * @param aStepUUID
     * @param aValue
     * @param aFilter
     * @param aChainedCallback
     */
    public void updateTimer(StepUUID aStepUUID, Date aNewValue, StepFilter aFilter, AsyncCallback<StepItem> aCallBackHandler);

    /**
     * @param aStepUUID
     * @param aFilter
     * @param aChainedCallback
     */
    public void cancelTimer(StepUUID aStepUUID, StepFilter aFilter, AsyncCallback<StepItem> aCallBackHandler);

    
    /**
     * Step definition
     */
    
    /**
     * List steps that match the given filter.
     * 
     * @param anItemFilter
     * @param aCallBackHandler
     */
    public void getStepsDefinition(StepFilter anItemFilter, AsyncCallback<ItemUpdates<StepDefinition>> aCallBackHandler);

    /**
     * @param anItemUUID
     * @param aFilter
     * @param aChainedCallback
     */
    public void getStepDefinition(StepUUID anItemUUID, StepFilter aFilter, AsyncCallback<StepDefinition> aCallBackHandler);

    /**
     * @param anItemSelection
     * @param aFilter
     * @param aChainedCallback
     */
    public void getStepsDefinition(List<StepUUID> anItemSelection, StepFilter aFilter, AsyncCallback<List<StepDefinition>> aCallBackHandler);
}
