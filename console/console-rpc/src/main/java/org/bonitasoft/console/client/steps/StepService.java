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
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.steps.exceptions.StepNotFoundException;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface StepService extends RemoteService {

    /**
     * Assign a step to an actor.
     * 
     * @param aStepUUID
     * @param anActorId
     */
    public void assignStep(StepUUID aStepUUID, UserUUID anActorId) throws SessionTimeOutException, ConsoleException;

    /**
     * Assign a step to a bunch of candidates.
     * 
     * @param aStepUUID
     * @param aCandidateSet
     */
    public void assignStep(StepUUID aStepUUID, Set<UserUUID> aCandidateSet) throws SessionTimeOutException, ConsoleException;

    /**
     * Suspend a step.
     * 
     * @param aStepUUID
     */
    public void suspendStep(StepUUID aStepUUID) throws SessionTimeOutException, ConsoleException;

    /**
     * Resume a step.
     * 
     * @param aStepUUID
     * @throws StepNotFoundException 
     */
    public void resumeStep(StepUUID aStepUUID) throws SessionTimeOutException, ConsoleException, StepNotFoundException;

    /**
     * Unassign a step.
     * 
     * @param aStepUUID
     * @return the new assignment resulting to the un-assignment. The set may be
     *         empty.
     */
    public Set<UserUUID> unassignStep(StepUUID aStepUUID) throws SessionTimeOutException, ConsoleException;

    /**
     * @param aStepUUID
     * @return
     * @throws SessionTimeOutException
     * @throws ConsoleException
     */
    public List<CommentItem> getStepCommentFeed(StepUUID aStepUUID) throws SessionTimeOutException, ConsoleException;

    /**
     * @param aStepUUID
     * @param aComment
     * @return
     * @throws SessionTimeOutException
     * @throws ConsoleException
     */
    public List<CommentItem> addStepComment(StepUUID aStepUUID, String aComment) throws SessionTimeOutException, ConsoleException;

    /**
     * Modify a step's priority
     * 
     * @param aStepUUID
     * @param priority
     * @throws SessionTimeOutException
     * @throws ConsoleException
     */
    public String setStepPriority(StepUUID aStepUUID, int priority) throws SessionTimeOutException, ConsoleException;

    /**
     * List steps that match the given filter.
     * 
     * @param anItemFilter
     * @param aCallBackHandler
     */
    public ItemUpdates<StepItem> getAllSteps(StepFilter anItemFilter) throws SessionTimeOutException, ConsoleException;

    /**
     * @param anItemUUID
     * @param aFilter
     * @param aChainedCallback
     */
    public StepItem getStep(StepUUID anItemUUID, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException;

    /**
     * @param anItemSelection
     * @param aFilter
     * @param aChainedCallback
     */
    public List<StepItem> getSteps(List<StepUUID> anItemSelection, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException;

    public StepItem skipStep(StepUUID anItemUUID, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException;
    
    public StepItem executeTimer(StepUUID anItemUUID, StepFilter aStepFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException;
    
    public StepItem updateTimer(StepUUID anItemUUID, Date aNewValue, StepFilter aStepFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException;
    
    public StepItem cancelTimer(StepUUID anItemUUID, StepFilter aStepFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException;
    
    /*
     * Step definition
     */

    /**
     * List steps that match the given filter.
     * 
     * @param anItemFilter
     * @param aCallBackHandler
     */
    public ItemUpdates<StepDefinition> getStepsDefinition(StepFilter anItemFilter) throws SessionTimeOutException, ConsoleException;

    /**
     * @param anItemUUID
     * @param aFilter
     * @param aChainedCallback
     */
    public StepDefinition getStepDefinition(StepUUID anItemUUID, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException;

    /**
     * @param anItemSelection
     * @param aFilter
     * @param aChainedCallback
     */
    public List<StepDefinition> getStepsDefinition(List<StepUUID> anItemSelection, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException;
}
