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

import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.SimpleHistoryFilter;
import org.bonitasoft.console.client.cases.CaseUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class EventFilter extends SimpleHistoryFilter {

    private static final long serialVersionUID = -2009099869283259559L;
    
    protected CaseUUID myCaseUUID;
    protected StepUUID myStepUUID;

    /**
     * Default constructor.
     */
    private EventFilter() {
        // Mandatory for serialization.
    }

    public EventFilter(final int aStartingIndex, final int aPageSize) {
        super(aStartingIndex, aPageSize);
        setWithAdminRights(true);
    }

    public CaseUUID getCaseUUID() {
        return myCaseUUID;
    }

    public void setCaseUUID(CaseUUID aCaseUUID) {
        myCaseUUID = aCaseUUID;
    }

    public StepUUID getStepUUID() {
        return myStepUUID;
    }

    public void setStepUUID(StepUUID aStepUUID) {
        myStepUUID = aStepUUID;
    }
    
    @Override
    public ItemFilter createFilter() {
        EventFilter theResult = new EventFilter();
        theResult.updateFilter(this);
        return theResult;
    }

    public void updateFilter(EventFilter aFilter) {
        super.updateFilter(aFilter);
        myCaseUUID = aFilter.getCaseUUID();
        myStepUUID = aFilter.getStepUUID();
    }


}
