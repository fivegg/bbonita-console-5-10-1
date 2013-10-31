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

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.cases.CaseUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class EventItem implements Item {

    private static final long serialVersionUID = -9028867176421310353L;

    public enum EventPosition {
        START, INTERMEDIATE, BOUNDARY, DEADLINE;
    }

    public enum EventType {
        TIMER, MESSAGE, SIGNAL, ERROR;
    }

    private EventPosition myPosition;
    private EventType myType;
    private Date myExecutionDate;
    private CaseUUID myCaseUUID;
    private StepUUID myStepUUID;
    private String myProcessName;
    private String myStepName;
    private EventUUID myUUID;

    /**
     * Default constructor.
     */
    protected EventItem() {
        // Mandatory for serialization.
    }

    /**
     * Default constructor.
     */
    public EventItem(EventUUID aEventUUID) {
        myUUID = aEventUUID;
    }

    public EventUUID getUUID() {
        return myUUID;
    }
    
    public EventPosition getPosition() {
        return myPosition;
    }
    
    public void setPosition(EventPosition aPosition) {
        myPosition = aPosition;
    }

    public EventType getType() {
        return myType;
    }
    
    public void setType(EventType aType) {
        myType = aType;
    }

    public Date getExecutionDate() {
        return myExecutionDate;
    }
    
    public void setExecutionDate(Date aDate) {
        myExecutionDate = aDate;
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
    
    public String getProcessName() {
        return myProcessName;
    }

    public void setProcessName(String aProcessName) {
        myProcessName = aProcessName;
    }
    
    public String getStepName() {
        return myStepName;
    }
    
    public void setStepName(String aStepName) {
        myStepName = aStepName;
    }

    public void updateItem(Item aSource) {
        if (aSource != null && aSource != this && aSource instanceof EventItem) {
            EventItem theOtherItem = (EventItem) aSource;
            setCaseUUID(theOtherItem.getCaseUUID());
            setExecutionDate(theOtherItem.getExecutionDate());
            setPosition(theOtherItem.getPosition());
            setProcessName(theOtherItem.getProcessName());
            setStepName(theOtherItem.getStepName());
            setStepUUID(theOtherItem.getStepUUID());
            setType(theOtherItem.getType());
        }
    }

}
