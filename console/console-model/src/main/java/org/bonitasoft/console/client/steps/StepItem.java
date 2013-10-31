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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.attachments.Attachment;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.users.UserUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepItem implements Item, Comparable<StepItem> {

    public enum StepState {
        FINISHED, READY, EXECUTING, SUSPENDED, CANCELLED, ABORTED, SKIPPED, FAILED
    }

    public enum StepPriority {
        NONE, NORMAL, HIGH, URGENT
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
    public static final String DATE_PROPERTY = "step last update";

    private StepState stepState;
    private Set<UserUUID> stepAssign;
    private String stepDesc;
    private CaseItem myCase;
    private UserUUID myAuthor;
    private Date myLastUpdateDate;
    private String myName;
    private String myLabel;
    private ArrayList<Attachment> myAttachments;
    private StepUUID myUUID;
    private StepPriority myPriority;
    private String myApplicationURL;
    private boolean isTask;
    private Date myReadyDate;
    private int myNumberOfComments;
    private String myExecutionSummary;
    private boolean isLight;
    private boolean isTimer;
//    private Date myDueDate;

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
    protected StepItem() {
        super();
        // Mandatory for serialization.
    }

    public StepItem(StepUUID aStepUUID, String aStepName, String aStepLabel, StepState aStepState, Set<String> aSetOfCandidates, String aStepDescription, String anAuthor, Date aLastUpdateDate,
            boolean isTask, StepPriority aPriority, Date aReadyDate) {
        super();
        this.myUUID = aStepUUID;
        myName = aStepName;
        myLabel = aStepLabel;
        this.stepState = aStepState;
        this.stepAssign = new HashSet<UserUUID>();
        for (Iterator<String> theIterator = aSetOfCandidates.iterator(); theIterator.hasNext();) {
            String theUser = (String) theIterator.next();
            this.stepAssign.add(new UserUUID(theUser));
        }
        this.stepDesc = aStepDescription;
        myAuthor = new UserUUID(anAuthor);
        myLastUpdateDate = aLastUpdateDate;
        myAttachments = null;
        myPriority = aPriority;
        this.isTask = isTask;
        this.myReadyDate = aReadyDate;
        myExecutionSummary = null;
    }

    public StepItem(StepUUID aStepUUID, String aStepName, String aStepLabel, StepState aStepState, Set<String> aSetOfCandidates, String aStepDescription, String anAuthor, Date aLastUpdateDate,
            boolean isTask, Date aReadyDate) {
        this(aStepUUID, aStepName, aStepLabel, aStepState, aSetOfCandidates, aStepDescription, anAuthor, aLastUpdateDate, isTask, StepPriority.NONE, aReadyDate);
    }

    /**
     * Default constructor.
     * 
     * @param aStepUUID
     * @param aStepName
     * @param aStepLabel
     * @param aStepState
     * @param aStepDescription
     * @param aLastUpdateDate
     * @param aIsTask
     * @param aPriority
     * @param aReadyDate
     */
    public StepItem(StepUUID aStepUUID, String aStepName, String aStepLabel, StepState aStepState, String aStepDescription, Date aLastUpdateDate, boolean isTask, StepPriority aPriority,
            Date aReadyDate) {
        this.isLight = true;
        this.myUUID = aStepUUID;
        myName = aStepName;
        myLabel = aStepLabel;
        this.stepState = aStepState;
        this.stepAssign = new HashSet<UserUUID>();
        this.stepDesc = aStepDescription;
        myLastUpdateDate = aLastUpdateDate;
        myAttachments = null;
        myPriority = aPriority;
        this.isTask = isTask;
        this.myReadyDate = aReadyDate;
        myExecutionSummary = null;
    }

    /**
     * @param anAuthor
     *            the author to set
     */
    public void setAuthor(UserUUID anAuthor) {
        myAuthor = anAuthor;
    }

    /**
     * @param anLastUpdateDate
     *            the lastUpdateDate to set
     */
    public void setLastUpdateDate(Date aLastUpdateDate) {
        Date theOldValue = myLastUpdateDate;
        myLastUpdateDate = aLastUpdateDate;
        myChanges.fireModelChange(DATE_PROPERTY, theOldValue, myLastUpdateDate);
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
     * Link the step to a case.
     * 
     * @param aCase
     */
    public void setCase(CaseItem aCase) {
        myCase = aCase;
    }

    /**
     * Get the case the step belongs to.
     * 
     * @return the case.
     */
    public CaseItem getCase() {
        return this.myCase;
    }

    /**
     * Get the state of the step.
     * 
     * @return the step state.
     */
    public StepState getState() {
        return stepState;
    }

    /**
     * @param aState
     *            the state to set
     */
    public void setState(StepState aState) {
        StepState oldValue = this.stepState;
        this.stepState = aState;
        myChanges.fireModelChange(STATE_PROPERTY, oldValue, this.stepState);
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
     * @param stepAssign
     *            the set of candidates ID.
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
     * Get the author of a step. The author is the name of the person that
     * completed the step or the name of the role the step is assign to.
     * 
     * @return the UUID of the author
     */
    public UserUUID getAuthor() {
        return myAuthor;
    }

    /**
     * Get the last update time and date.
     * 
     * @return the update date
     */
    public Date getLastUpdateDate() {
        return myLastUpdateDate;
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
     * Get all the files attached to the step. An empty list is returned if
     * none.
     * 
     * @return the attachments.
     */
    public ArrayList<Attachment> getAttachments() {
        if (myAttachments == null) {
            myAttachments = new ArrayList<Attachment>();
        }
        return myAttachments;
    }

    /**
     * Defines whether the step has attachment or not.
     * 
     * @return true if some files are attached to the step.
     */
    public boolean hasAttachment() {

        return (myAttachments != null && !myAttachments.isEmpty());
    }

    /**
     * @return the priority
     */
    public StepPriority getPriority() {
        return myPriority;
    }

    /**
     * @param anPriority
     *            the priority to set
     */
    public void setPriority(StepPriority aPriority) {
        StepPriority oldValue = this.myPriority;
        this.myPriority = aPriority;
        myChanges.fireModelChange(PRIORITY_PROPERTY, oldValue, this.myPriority);
    }

    /**
     * @param myApplicationURL
     *            the application URL of the process to set
     */
    public void setApplicationURL(String myApplicationURL) {
        this.myApplicationURL = myApplicationURL;
    }

    /**
     * @return the application URL of the process
     */
    public String getApplicationURL() {
        return myApplicationURL;
    }

    /**
     * @return whether the step is a task or not
     */
    public boolean isTask() {
        return isTask;
    }

    public boolean isTimer() {
        return isTimer;
    }

    public void setIsTimer(boolean isTimer) {
        this.isTimer = isTimer;
    }
//    
//    public Date getDueDate() {
//        return myDueDate;
//    }
//
//    public void setDueDate(Date aDueDate) {
//        myDueDate = aDueDate;
//    }    
    
    /**
     * @return the readyDate
     */
    public Date getReadyDate() {
        return myReadyDate;
    }

    /**
     * @return the number of comments associated to this step.
     */
    public int getNumberOfComments() {
        return myNumberOfComments;
    }

    /**
     * 
     * @param aNumberOfComment
     */
    public void setNumberOfComments(int aNumberOfComment) {
        int theOldValue = myNumberOfComments;
        myNumberOfComments = aNumberOfComment;
        myChanges.fireModelChange(COMMENT_COUNT_PROPERTY, theOldValue, myNumberOfComments);
    }

    public String getExecutionSummary() {
        return myExecutionSummary;
    }

    /**
     * @param aDynamicExecutionSummary
     */
    public void setExecutionSummary(String aDynamicExecutionSummary) {
        myExecutionSummary = aDynamicExecutionSummary;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(StepItem anotherStep) {
        int theUpdateDateComparison = myLastUpdateDate.compareTo(anotherStep.myLastUpdateDate);
        if (theUpdateDateComparison == 0) {
            return myName.compareTo(anotherStep.getName());
        } else {
            return theUpdateDateComparison;
        }
    }

    @Override
    public boolean equals(Object anObj) {
        if (this == anObj) {
            return true;
        }
        if (anObj instanceof StepItem) {
            StepItem anotherStep = (StepItem) anObj;
            return myUUID.equals(anotherStep.getUUID());
        }
        return false;
    }

    public boolean isLight() {
        return isLight;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.
     * client .Item)
     */
    public void updateItem(Item aSource) {
        if (aSource != null && aSource != this && aSource instanceof StepItem) {
            StepItem theOtherStep = (StepItem) aSource;
            setAuthor(theOtherStep.getAuthor());
            setExecutionSummary(theOtherStep.getExecutionSummary());
            setLastUpdateDate(theOtherStep.getLastUpdateDate());
            setPriority(theOtherStep.getPriority());
            setState(theOtherStep.getState());
            setStepDesc(theOtherStep.getDesc());
            if (!theOtherStep.isLight) {
                // do not read from the source when it is a light one.
                setCase(theOtherStep.getCase());
                setApplicationURL(theOtherStep.getApplicationURL());
                setAssign(theOtherStep.getAssign());
                setNumberOfComments(theOtherStep.getNumberOfComments());
            }
        }

    }
    
    public BonitaProcessUUID getProcessUUID(){
        String stepUUIDStr = this.getUUID().getValue();
        String stepUUIDInfo[] = stepUUIDStr.split("--");
        String processUUIDValue = stepUUIDInfo[0]+"--"+stepUUIDInfo[1];
        String processUUIDLabel =stepUUIDInfo[0];
        return new BonitaProcessUUID(processUUIDValue, processUUIDLabel);
    }

}
