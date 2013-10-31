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

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;
import org.bonitasoft.console.client.steps.EventFilter;
import org.bonitasoft.console.client.steps.EventItem;
import org.bonitasoft.console.client.steps.EventUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface EventDataSource extends BonitaFilteredDataSource<EventUUID, EventItem, EventFilter> {
    
    public static final String EVENT_EXECUTED_PROPERTY = "event executed";

    public void executeEvent(EventUUID anItemId, EventFilter anItemFilter, final AsyncHandler<ItemUpdates<EventItem>> aHandler);
}
