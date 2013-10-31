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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.users.UserUUID;

/**
 * @author Nicolas Chabanoles, Christophe Leroy
 * 
 */
public class StepDefinition implements Item, Comparable<StepDefinition> {

    public enum StepState {
        FINISHED, READY, EXECUTING, SUSPENDED, CANCELLED, ABORTED, SKIPPED
    }

    public enum StepPriority {
        NONE, NORMAL, HIGH, URGENT
    }

    public enum StepType {
        Automatic, Human, Timer, Decision, Subflow, SendEvents, ReceiveEvent, ErrorEvent, SignalEvent;

        public static int ordinal(String value) {
            int i = 0;
            for (StepType s : StepType.values()) {
                if (s.name().equals(value)) {
                    return (i);
                }
                i++;
            }
            return -1;
        }
    }

    public static final String SOLO = "solo";
    public static final String GROUP = "group";

    /**
     * Name of the property used to identify the changes of the state;
     */
    public transient static final String STATE_PROPERTY = "state";
    /**
     * Name of the property used to identify the changes of the assignees;
     */
    public transient static final String ASSIGN_PROPERTY = "assign";
    /**
     * Name of the property used to identify the changes of the priority;
     */
    public transient static final String PRIORITY_PROPERTY = "priority";

    private transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

    /**
     * The ID used for serialization.
     */
    private static final long serialVersionUID = -7188632913129521229L;
    public static final String COMMENT_COUNT_PROPERTY = "step comment count";

    private Set<UserUUID> stepAssign;
    private String stepDesc;
    private String myName;
    private String myLabel;
    private StepUUID myUUID;
    private StepPriority myPriority;
    private boolean isTask;
    private String myProcessName;
    private String myProcessVersion;

    /**
     * Get the value of the uUID field.
     * 
     * @return the uUID
     */
    public StepUUID getUUID() {
        return this.myUUID;
    }

    /**
     * Default constructor.
     */
    protected StepDefinition() {
        super();
        // Mandatory for serialization.
    }

    public StepDefinition(StepUUID aStepUUID, String aStepName, String aStepLabel, Set<String> aSetOfCandidates, String aStepDescription, boolean isTask, StepPriority aPriority) {
        super();
        this.myUUID = aStepUUID;
        myName = aStepName;
        myLabel = aStepLabel;
        this.stepAssign = new HashSet<UserUUID>();
        for (Iterator<String> theIterator = aSetOfCandidates.iterator(); theIterator.hasNext();) {
            String theUser = (String) theIterator.next();
            this.stepAssign.add(new UserUUID(theUser));
        }
        this.stepDesc = aStepDescription;
        myPriority = aPriority;
        this.isTask = isTask;
    }

    /**
     * Add a property change listener.
     * 
     * @param aPropertyName
     * @param aListener
     */
    public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        // Avoid duplicate subscription.
        myChanges.removeModelChangeListener(aPropertyName, aListener);
        myChanges.addModelChangeListener(aPropertyName, aListener);

    }

    public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        myChanges.removeModelChangeListener(aPropertyName, aListener);
    }

    /**
     * Get the set of candidates ID.
     * 
     * @return the stepAssign
     */
    public Set<UserUUID> getAssign() {
        return stepAssign;
    }

    /**
     * Change the set of candidates for the step.
     * 
     * @param stepAssign the set of candidates ID.
     */
    public void setAssign(Set<UserUUID> stepAssign) {
        HashSet<UserUUID> theOldValue = new HashSet<UserUUID>(this.stepAssign);
        this.stepAssign = stepAssign;
        myChanges.fireModelChange(ASSIGN_PROPERTY, theOldValue, this.stepAssign);
    }

    /**
     * Get the step textual description.
     * 
     * @return the description.
     */
    public String getDesc() {
        return stepDesc;
    }

    /**
     * Set the textual description of the step.
     * 
     * @param stepDesc
     */
    public void setStepDesc(String stepDesc) {
        this.stepDesc = stepDesc;
    }

    /**
     * Get the step name.
     * 
     * @return the step name
     */
    public String getName() {
        return myName;
    }

    /**
     * Get the step label.
     * 
     * @return the step label
     */
    public String getLabel() {
        return myLabel;
    }

    /**
     * @return the priority
     */
    public StepPriority getPriority() {
        return myPriority;
    }

    /**
     * @param anPriority the priority to set
     */
    public void setPriority(StepPriority aPriority) {
        StepPriority oldValue = this.myPriority;
        this.myPriority = aPriority;
        myChanges.fireModelChange(PRIORITY_PROPERTY, oldValue, this.myPriority);
    }

    /**
     * @return whether the step is a task or not
     */
    public boolean isTask() {
        return isTask;
    }

    /**
     * Set the process name.
     */
    public void setProcessName(String aProcessName) {
        myProcessName = aProcessName;
    }

    /**
     * Get the name of the process the step belongs to.
     */
    public String getProcessName() {
        return myProcessName;
    }

    /**
     * Set the version of the process the step belongs to.
     */
    public void setProcessVersion(String aProcessVersion) {
        myProcessVersion = aProcessVersion;
    }

    /**
     * Get the version of the process the step belongs to.
     */
    public String getProcessVersion() {
        return myProcessVersion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(StepDefinition anotherStep) {
        return myName.compareTo(anotherStep.getName());
    }

    @Override
    public boolean equals(Object anObj) {
        if (this == anObj) {
            return true;
        }
        if (anObj instanceof StepDefinition) {
            StepDefinition anotherStep = (StepDefinition) anObj;
            return myUUID.equals(anotherStep.getUUID());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.client .Item)
     */
    public void updateItem(Item aSource) {
        if (aSource != null && aSource != this && aSource instanceof StepDefinition) {
            StepDefinition theOtherStep = (StepDefinition) aSource;
            setAssign(theOtherStep.getAssign());
            setPriority(theOtherStep.getPriority());
            setStepDesc(theOtherStep.getDesc());
            setProcessName(theOtherStep.getProcessName());
            setProcessVersion(theOtherStep.getProcessVersion());
        }

    }

}
