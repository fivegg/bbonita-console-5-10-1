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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.steps.EventFilter;
import org.bonitasoft.console.client.steps.EventItem;
import org.bonitasoft.console.client.steps.EventItem.EventPosition;
import org.bonitasoft.console.client.steps.EventItem.EventType;
import org.bonitasoft.console.client.steps.EventUUID;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.steps.exceptions.EventNotFoundException;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.runtime.CatchingEvent;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.CatchingEventUUID;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class EventDataStore {

    private static final EventDataStore INSTANCE = new EventDataStore();

    /**
     * @return the INSTANCE
     */
    public static EventDataStore getInstance() {
        return INSTANCE;
    }

    /**
     * Default constructor.
     */
    protected EventDataStore() {
        super();
    }

    /**
     * List events matching the given filter.
     * 
     * @param anItemFilter
     * @throws ConsoleException
     */
    public ItemUpdates<EventItem> getAllEvents(EventFilter anItemFilter) throws ConsoleException {
        if (anItemFilter.getStepUUID() != null) {
            final QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
            Set<CatchingEvent> theEvents = theQueryRuntimeAPI.getEvents(new ActivityInstanceUUID(anItemFilter.getStepUUID().getValue()));
            final List<EventItem> theEventList = new ArrayList<EventItem>();
            if (theEvents == null || theEvents.isEmpty()) {
                return new ItemUpdates<EventItem>(theEventList, 0);
            }

            for (CatchingEvent theCatchingEvent : theEvents) {
                theEventList.add(buildEventItem(theCatchingEvent));
            }
            return new ItemUpdates<EventItem>(theEventList, theEventList.size());
        } else {
            throw new ConsoleException("Not yet implemented", null);
        }
    }

    /**
     * Get a particular event.
     */
    public EventItem getEvent(EventUUID anItemUUID, EventFilter aFilter) throws ConsoleException, EventNotFoundException {
        throw new ConsoleException("API missing!", null);
    }

    protected EventItem buildEventItem(CatchingEvent anEvent) {
        final EventUUID theEventUUID = new EventUUID(String.valueOf(anEvent.getUUID().getValue()));
        final EventItem theEventItem = new EventItem(theEventUUID);

        theEventItem.setStepName(anEvent.getActivityName());
        if (anEvent.getProcessInstanceUUID() != null) {
            theEventItem.setCaseUUID(new CaseUUID(anEvent.getProcessInstanceUUID().getValue()));
        }
        theEventItem.setExecutionDate(anEvent.getExecutionDate());
        theEventItem.setPosition(EventPosition.valueOf(anEvent.getPosition().name()));
        if (anEvent.getActivityInstanceUUID() != null) {
            theEventItem.setStepUUID(new StepUUID(anEvent.getActivityInstanceUUID().getValue(), anEvent.getActivityDefinitionUUID().getValue()));
        }
        theEventItem.setType(EventType.valueOf(anEvent.getType().name()));

        return theEventItem;
    }

    /**
     * List the given event collection.
     * 
     * @param anItemSelection
     * @param aFilter
     * @return
     * @throws ConsoleException
     * @throws EventNotFoundException
     */
    public List<EventItem> getEvents(List<EventUUID> anItemSelection, EventFilter aFilter) throws ConsoleException, EventNotFoundException {
        throw new ConsoleException("Not yet implemented", null);
    }

    /**
     * Delete the events.
     * 
     * @param anItemSelection
     * @param anItemFilter
     * @return
     * @throws ConsoleException
     * @throws EventNotFoundException
     */
    public ItemUpdates<EventItem> deleteEvents(Collection<EventUUID> anItemSelection, EventFilter anItemFilter) throws ConsoleException, EventNotFoundException {
        try {
            Collection<CatchingEventUUID> theCatchingEvents = new ArrayList<CatchingEventUUID>();
            for (EventUUID theEventUUID : anItemSelection) {
                theCatchingEvents.add(new CatchingEventUUID(theEventUUID.getValue()));
            }
            AccessorUtil.getRuntimeAPI().deleteEvents(theCatchingEvents);
        } catch (org.ow2.bonita.facade.exception.EventNotFoundException e) {
            throw new EventNotFoundException();
        }

        return getAllEvents(anItemFilter);

    }

    /**
     * Update an event.
     * 
     * @param anItemId
     * @param anItem
     * @return
     * @throws ConsoleException
     * @throws EventNotFoundException
     */
    public EventItem updateEvent(EventUUID anItemId, EventItem anItem) throws ConsoleException, EventNotFoundException {
        try {
            final CatchingEventUUID theCatchingEventUUID = new CatchingEventUUID(anItemId.getValue());
            AccessorUtil.getRuntimeAPI().updateExpirationDate(theCatchingEventUUID, anItem.getExecutionDate());
            CatchingEvent theEvent = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY).getEvent(theCatchingEventUUID);
            return buildEventItem(theEvent);
        } catch (org.ow2.bonita.facade.exception.EventNotFoundException e) {
            throw new EventNotFoundException();
        }
    }

    /**
     * Execute an event.
     * 
     * @param anItemId
     * @param anItemFilter
     * @return
     * @throws EventNotFoundException
     * @throws ConsoleException
     */
    public ItemUpdates<EventItem> executeEvent(EventUUID anItemId, EventFilter anItemFilter) throws EventNotFoundException, ConsoleException {
        executeEvent(anItemId);
        return getAllEvents(anItemFilter);
    }
    
    /**
     * Execute an event.
     * 
     * @param anItemId
     * @param anItemFilter
     * @return
     * @throws EventNotFoundException
     * @throws ConsoleException
     */
    public void executeEvent(EventUUID anItemId) throws EventNotFoundException, ConsoleException {
        try {
            AccessorUtil.getRuntimeAPI().executeEvent(new CatchingEventUUID(anItemId.getValue()));
        } catch (org.ow2.bonita.facade.exception.EventNotFoundException e) {
            throw new EventNotFoundException();
        }
    }

    /**
     * @param aUuid
     * @param aNewValue
     * @throws EventNotFoundException 
     */
    public void updateEvent(EventUUID aUuid, Date aNewValue) throws EventNotFoundException {
        try {
            AccessorUtil.getRuntimeAPI().updateExpirationDate(new CatchingEventUUID(aUuid.getValue()), aNewValue);
        } catch (org.ow2.bonita.facade.exception.EventNotFoundException e) {
            throw new EventNotFoundException();
        }
        
    }
}
