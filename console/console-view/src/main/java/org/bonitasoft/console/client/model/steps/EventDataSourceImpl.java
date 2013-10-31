/**
 * Copyright (C) 2010 BonitaSoft S.A.
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

import java.util.ArrayList;
import java.util.HashMap;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.model.DefaultFilteredDataSourceImpl;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.SimpleSelection;
import org.bonitasoft.console.client.steps.EventFilter;
import org.bonitasoft.console.client.steps.EventItem;
import org.bonitasoft.console.client.steps.EventUUID;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class EventDataSourceImpl extends DefaultFilteredDataSourceImpl<EventUUID, EventItem, EventFilter> implements EventDataSource {

    /**
     * Default constructor.
     * 
     * @param aRPCItemData
     * @param aAnItemSelection
     * @param aMessageDataSource
     */
    public EventDataSourceImpl(MessageDataSource aMessageDataSource) {
        super(new EventData(), new SimpleSelection<EventUUID>(), aMessageDataSource);
        setItemFilter(new EventFilter(0, 20));
    }

    @SuppressWarnings("unchecked")
    public void executeEvent(final EventUUID anItemId, final EventFilter anItemFilter, final AsyncHandler<ItemUpdates<EventItem>> aHandler) {
        if (anItemFilter != null && anItemFilter.getStepUUID()!=null) {
            ((EventData) myRPCItemData).executeEvent(anItemId, anItemFilter, new AsyncHandler<ItemUpdates<EventItem>>() {
                public void handleFailure(Throwable aT) {
                    if (aT instanceof SessionTimeOutException) {
                        myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                    } else if (aT instanceof ConsoleSecurityException) {
                        myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                    }
                    if (aT instanceof ConsoleException) {
                        if (myMessageDataSource != null) {
                            myMessageDataSource.addErrorMessage((ConsoleException) aT);
                        }
                    }
                    if (aHandler != null) {
                        aHandler.handleFailure(aT);
                    }
                }

                public void handleSuccess(ItemUpdates<EventItem> anResult) {
                    if (anResult != null) {
                        if (myKnownItems == null) {
                            myKnownItems = new HashMap<EventUUID, EventItem>();
                        }
                        updateItems(anResult.getItems());
                    }
                    if (aHandler != null) {
                        final ArrayList<EventItem> theResult = new ArrayList<EventItem>();
                        for (EventItem theItem : anResult.getItems()) {
                            theResult.add(myKnownItems.get(theItem.getUUID()));
                        }
                        aHandler.handleSuccess(new ItemUpdates<EventItem>(theResult, anResult.getNbOfItems()));
                    }
                    myChanges.fireModelChange(new ModelChangeEvent(anItemFilter.getCaseUUID(), EVENT_EXECUTED_PROPERTY, true, false));
                }
            });
        } else {
            GWT.log("Impossible to execute item without a valid filter.", new IllegalArgumentException());
        }
    }
}
