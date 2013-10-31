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
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.steps.exceptions.EventNotFoundException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * 
 * @author Nicolas Chabanoles
 * 
 */
public interface EventService extends RemoteService {

    /**
     * List Events that match the given filter.
     * 
     * @param anItemFilter
     * @param aCallBackHandler
     */
    public ItemUpdates<EventItem> getAllEvents(EventFilter anItemFilter) throws SessionTimeOutException, ConsoleException;

    /**
     * Delete events selection.
     * @param anItemSelection
     * @param anItemFilter
     * @param aCallback
     * @throws SessionTimeOutException 
     * @throws ConsoleException 
     */
    public ItemUpdates<EventItem> deleteItems(Collection<EventUUID> anItemSelection, EventFilter anItemFilter) throws SessionTimeOutException, EventNotFoundException, ConsoleException;

    /**
     * Update item
     * @param anItemId
     * @param anItem
     * @param aChainedCallback
     */
    public EventItem updateItem(EventUUID anItemId, EventItem anItem) throws SessionTimeOutException, EventNotFoundException, ConsoleException;
    
    /**
     * Execute the given event.
     * @param anItemId
     * @param aFilter
     * @return
     */
    public ItemUpdates<EventItem> executeEvent(EventUUID anItemId, EventFilter aFilter) throws SessionTimeOutException, EventNotFoundException, ConsoleException;
}
