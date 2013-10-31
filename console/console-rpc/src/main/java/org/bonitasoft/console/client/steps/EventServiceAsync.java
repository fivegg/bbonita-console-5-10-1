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

import java.util.Collection;

import org.bonitasoft.console.client.ItemUpdates;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Nicolas Chabanoles
 * 
 */
public interface EventServiceAsync {

    /**
     * List Events that match the given filter.
     * 
     * @param anItemFilter
     * @param aCallBackHandler
     */
    public void getAllEvents(EventFilter anItemFilter, AsyncCallback<ItemUpdates<EventItem>> aCallBackHandler);

    /**
     * @param aAnItemSelection
     * @param aAnItemFilter
     * @param aChainedCallback
     */
    public void deleteItems(Collection<EventUUID> aAnItemSelection, EventFilter anItemFilter, AsyncCallback<ItemUpdates<EventItem>> aCallBackHandler);

    /**
     * @param anItemId
     * @param anItem
     * @param aChainedCallback
     */
    public void updateItem(EventUUID anItemId, EventItem anItem, AsyncCallback<EventItem> aCallBackHandler);

    /**
     * @param aAnItemId
     * @param aFilter
     * @param aChainedCallback
     */
    public void executeEvent(EventUUID aAnItemId, EventFilter aFilter, AsyncCallback<ItemUpdates<EventItem>> aChainedCallback);

}
