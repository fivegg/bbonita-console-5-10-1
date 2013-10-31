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
import java.util.List;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.steps.EventFilter;
import org.bonitasoft.console.client.steps.EventItem;
import org.bonitasoft.console.client.steps.EventUUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class EventData implements RPCData<EventUUID, EventItem, EventFilter> {


    public void addItem(EventItem anItem, EventFilter aFilter, AsyncHandler<ItemUpdates<EventItem>>... handlers) {
        Window.alert("Operation not supported: EventData.addItem()");

    }

    public void deleteItems(Collection<EventUUID> anItemSelection, EventFilter anItemFilter, AsyncHandler<ItemUpdates<EventItem>>... handlers) {
        GWT.log("RPC: get delete Events");
        RpcConsoleServices.getEventService().deleteItems(anItemSelection, anItemFilter, new ChainedCallback<ItemUpdates<EventItem>>(handlers));
    }

    public void getAllItems(EventFilter anItemFilter, AsyncHandler<ItemUpdates<EventItem>>... handlers) {
        GWT.log("RPC: get all Events");
        RpcConsoleServices.getEventService().getAllEvents(anItemFilter, new ChainedCallback<ItemUpdates<EventItem>>(handlers));
    }

    public void getItem(EventUUID anItemUUID, EventFilter aFilter, AsyncHandler<EventItem>... handlers) {
        GWT.log("RPC: get a Event");
        Window.alert("Operation not supported: EventData.getItem()");
    }

    public void getItems(List<EventUUID> anItemSelection, EventFilter aFilter, AsyncHandler<List<EventItem>>... handlers) {
        GWT.log("RPC: get a bunch of Events");
        Window.alert("Operation not supported: EventData.getItem()");
    }

    public void updateItem(EventUUID anItemId, EventItem anItem, AsyncHandler<EventItem>... handlers) {
        GWT.log("RPC: get update Event");
        RpcConsoleServices.getEventService().updateItem(anItemId, anItem, new ChainedCallback<EventItem>(handlers));
    }
    
    public void executeEvent(EventUUID anItemId, EventFilter aFilter, AsyncHandler<ItemUpdates<EventItem>>... handlers) {
        GWT.log("RPC: execute Event");
        RpcConsoleServices.getEventService().executeEvent(anItemId, aFilter, new ChainedCallback<ItemUpdates<EventItem>>(handlers));
    }
}
