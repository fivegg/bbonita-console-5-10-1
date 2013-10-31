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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepItemData implements RPCData<StepUUID, StepItem, StepFilter> {

    /**
     * Ask the server to assign the step to the specified actor.
     * 
     * @param aStepUUID
     * @param anActorId
     * @param handlers
     */
    public void assignStep(final StepUUID aStepUUID, final UserUUID anActorId, final AsyncHandler<Void>... handlers) {
        GWT.log("RPC: assignStep", null);
        RpcConsoleServices.getStepService().assignStep(aStepUUID, anActorId, new ChainedCallback<Void>(handlers));
    }

    /**
     * Ask the server to assign the step the the set of candidates.
     * 
     * @param aStepUUID
     * @param aCandidateSet
     * @param handlers
     */
    public void assignStep(final StepUUID aStepUUID, final Set<UserUUID> aCandidateSet, final AsyncHandler<Void>... handlers) {
        GWT.log("RPC: assignStep", null);
        RpcConsoleServices.getStepService().assignStep(aStepUUID, aCandidateSet, new ChainedCallback<Void>(handlers));
    }

    /**
     * Suspend a step.
     * 
     * @param aStepUUID
     * @param isAssigned
     * @param handlers
     */
    public void suspendStep(final StepUUID aStepUUID, final AsyncHandler<Void>... handlers) {
        GWT.log("RPC: suspendStep", null);
        RpcConsoleServices.getStepService().suspendStep(aStepUUID, new ChainedCallback<Void>(handlers));
    }

    /**
     * Resume a step.
     * 
     * @param aStepUUID
     * @param handlers
     */
    public void resumeStep(final StepUUID aStepUUID, final AsyncHandler<Void>... handlers) {
        GWT.log("RPC: resumeStep", null);
        RpcConsoleServices.getStepService().resumeStep(aStepUUID, new ChainedCallback<Void>(handlers));
    }

    /**
     * Unassign a step.<br>
     * Return the new assignation value.
     * 
     * @param aStepUUID
     * @param handlers
     */
    public void unassignStep(final StepUUID aStepUUID, final AsyncHandler<Set<UserUUID>>... handlers) {
        GWT.log("RPC: unassignStep", null);
        RpcConsoleServices.getStepService().unassignStep(aStepUUID, new ChainedCallback<Set<UserUUID>>(handlers));
    }

    /**
     * 
     * @param aStepUUID
     * @param handlers
     */
    public void getStepCommentFeed(final StepUUID aStepUUID, final AsyncHandler<List<CommentItem>>... handlers) {
        GWT.log("RPC: getStepCommentFeed", null);
        RpcConsoleServices.getStepService().getStepCommentFeed(aStepUUID, new ChainedCallback<List<CommentItem>>(handlers));
    }

    /**
     * 
     * @param aStepUUID
     * @param aComment
     * @param aAsyncHandler
     */
    public void addStepComment(final StepUUID aStepUUID, final String aComment, final AsyncHandler<List<CommentItem>>... handlers) {
        GWT.log("RPC: addStepComment", null);
        RpcConsoleServices.getStepService().addStepComment(aStepUUID, aComment, new ChainedCallback<List<CommentItem>>(handlers));
    }

    /**
     * Modify a step's priority
     * 
     * @param aStep
     * @param priority
     * @param asyncHandler
     */
    public void setStepPriority(StepItem aStep, int priority, final AsyncHandler<String>... handlers) {
        GWT.log("RPC: setStepPriority");
        RpcConsoleServices.getStepService().setStepPriority(aStep.getUUID(), priority, new ChainedCallback<String>(handlers));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.common.data.RPCData#addItem(org.bonitasoft
     * .console.client.Item, org.bonitasoft.console.client.ItemFilter,
     * org.bonitasoft
     * .console.client.common.data.AsyncHandler<org.bonitasoft.console
     * .client.ItemUpdates<I>>[])
     */
    public void addItem(StepItem anItem, StepFilter aFilter, AsyncHandler<ItemUpdates<StepItem>>... handlers) {
        Window.alert("Operation not supported: StepData.addItem()");

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.common.data.RPCData#deleteItems(java.util
     * .Collection, org.bonitasoft.console.client.ItemFilter,
     * org.bonitasoft.console
     * .client.common.data.AsyncHandler<org.bonitasoft.console
     * .client.ItemUpdates<I>>[])
     */
    public void deleteItems(Collection<StepUUID> anItemSelection, StepFilter anItemFilter, AsyncHandler<ItemUpdates<StepItem>>... handlers) {
        Window.alert("Operation not supported: StepData.deleteItems()");

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.common.data.RPCData#getAllItems(org.bonitasoft
     * .console.client.ItemFilter,
     * org.bonitasoft.console.client.common.data.AsyncHandler
     * <org.bonitasoft.console.client.ItemUpdates<I>>[])
     */
    public void getAllItems(StepFilter anItemFilter, AsyncHandler<ItemUpdates<StepItem>>... handlers) {
        GWT.log("RPC: get all steps");
        RpcConsoleServices.getStepService().getAllSteps(anItemFilter, new ChainedCallback<ItemUpdates<StepItem>>(handlers));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.common.data.RPCData#getItem(org.bonitasoft
     * .console.client.BonitaUUID, org.bonitasoft.console.client.ItemFilter,
     * org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
     */
    public void getItem(StepUUID anItemUUID, StepFilter aFilter, AsyncHandler<StepItem>... handlers) {
        GWT.log("RPC: get a step");
        RpcConsoleServices.getStepService().getStep(anItemUUID, aFilter, new ChainedCallback<StepItem>(handlers));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.common.data.RPCData#getItems(java.util.
     * List, org.bonitasoft.console.client.ItemFilter,
     * org.bonitasoft.console.client
     * .common.data.AsyncHandler<java.util.List<I>>[])
     */
    public void getItems(List<StepUUID> anItemSelection, StepFilter aFilter, AsyncHandler<List<StepItem>>... handlers) {
        GWT.log("RPC: get a bunch of steps");
        RpcConsoleServices.getStepService().getSteps(anItemSelection, aFilter, new ChainedCallback<List<StepItem>>(handlers));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.common.data.RPCData#updateItem(org.bonitasoft
     * .console.client.BonitaUUID, org.bonitasoft.console.client.Item,
     * org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
     */
    public void updateItem(StepUUID anItemId, StepItem anItem, AsyncHandler<StepItem>... handlers) {
        Window.alert("Operation not supported: StepData.updateItem()");

    }

    /**
     * @param aUuid
     * @param aFilter
     * @param aAsyncHandler
     */
    public void skipStep(StepUUID anItemUUID, StepFilter aFilter, AsyncHandler<StepItem>... handlers) {
        GWT.log("RPC: skipping a step");
        RpcConsoleServices.getStepService().skipStep(anItemUUID, aFilter, new ChainedCallback<StepItem>(handlers));
    }

    /**
     * @param aStepUUID
     * @param aAsyncHandler
     */
    public void executeTimer(StepUUID aStepUUID, StepFilter aStepFilter, AsyncHandler<StepItem>... handlers) {
        GWT.log("RPC: executing a step timer");
        RpcConsoleServices.getStepService().executeTimer(aStepUUID, aStepFilter, new ChainedCallback<StepItem>(handlers));
    }

    /**
     * @param aStepUUID
     * @param aValue
     * @param aFilter
     * @param aAsyncHandler
     */
    public void updateTimer(StepUUID aStepUUID, Date aValue, StepFilter aFilter, AsyncHandler<StepItem>... handlers) {
        GWT.log("RPC: updating a step timer");
        RpcConsoleServices.getStepService().updateTimer(aStepUUID, aValue, aFilter, new ChainedCallback<StepItem>(handlers));
    }

    /**
     * @param aStepUUID
     * @param aValue
     * @param aFilter
     * @param aAsyncHandler
     */
    public void cancelTimer(StepUUID aStepUUID, StepFilter aFilter, AsyncHandler<StepItem>... handlers) {
        GWT.log("RPC: cancelling a step timer");
        RpcConsoleServices.getStepService().cancelTimer(aStepUUID, aFilter, new ChainedCallback<StepItem>(handlers));
    }
}
