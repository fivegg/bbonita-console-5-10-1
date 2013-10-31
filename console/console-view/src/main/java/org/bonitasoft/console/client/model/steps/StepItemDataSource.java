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
package org.bonitasoft.console.client.model.steps;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepUUID;

/**
 * @author Nicolas Chabanoles
 *
 */
public interface StepItemDataSource extends BonitaFilteredDataSource<StepUUID, StepItem, StepFilter> {

    public static final String STEP_SKIPPED_PROPERTY = "step skipped property";
	public static final String STEP_ASSIGNMENT_PROPERTY = "step assignment property";
	public static final String COMMENTS_PROPERTY = "step comments";
	public static final String STEP_TIMER_EXECUTED_PROPERTY = "step timer executed property";
	public static final String STEP_TIMER_UPDATED_PROPERTY = "step timer updated property";
	
	
	/**
	 * Assign the step to the currently logged in user, i.e., BonitaConsole.user.
	 * @param aStep
	 */
	public void assignStepToMe(final StepItem aStep);
	
	
	/**
	 * Remove assignment to the step.
	 * @param aStep
	 */
	public void unassignStep(final StepItem aStep);
	
	/**
	 * Assign the step to a bunch of cansidates.
	 * @param aStep
	 * @param aCandidateList
	 */
	public void assignStep(final StepItem aStep, Set<String> aCandidateList);
		
	/**
	 * Suspend a step.
	 * @param aStep 
	 * 
	 * @param aHandler 
	 */
	public void suspendStep(final StepItem aStep);

	/**
	 * Resume a step.
	 * @param aStep 
	 * 
	 * @param aHandler 
	 */
	public void resumeStep(final StepItem aStep);
	
	/**
	 * 
	 * @param aStepUUID
	 * @param aHandler
	 */
	public void getStepCommentFeed(StepUUID aStepUUID, final AsyncHandler<List<CommentItem>> aHandler);


	/**
	 * @param aStepUUID
	 * @param aComment
	 * @param aHandler
	 */
	public void addStepComment(StepUUID aStepUUID, String aComment, AsyncHandler<List<CommentItem>> aHandler);
	
	/**
	 * Modify a step's priority
	 * @param aStepUUID
	 * @param aPriority
	 */
	public void setStepPriority(StepItem aStep, int aPriority);


    /**
     * Skip an open human step.
     */
    public void skipStep(StepItem aStep);


    /**
     * Execute a step that stands for a timer event.
     * @param aStepUUID
     * @param aHandler
     */
    public void executeTimer(StepUUID aStepUUID, AsyncHandler<StepItem> aHandler);


    /**
     * @param aStepUUID
     * @param aValue
     * @param aAsyncHandler
     */
    public void updateTimer(StepUUID aStepUUID, Date aValue, AsyncHandler<StepItem> aHandler);


    /**
     * @param aStepUUID
     * @param aHandler
     */
    public void cancelTimer(StepUUID aStepUUID, AsyncHandler<StepItem> aHandler);
}
