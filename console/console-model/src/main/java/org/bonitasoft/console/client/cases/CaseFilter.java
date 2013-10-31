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
package org.bonitasoft.console.client.cases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.bonitasoft.console.client.SimpleHistoryFilter;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.steps.StepItem.StepState;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseFilter extends SimpleHistoryFilter {

    /**
     * ID used for serialization.
     */
    private static final long serialVersionUID = 3583398099158181410L;

    public static final String LABEL_PROPERTY = "case filter label";

    /**
     * The label to filter on.
     */
    protected Collection<LabelUUID> myLabels;
    protected Collection<String> myInvolvedUsers;
    protected Collection<BonitaProcessUUID> myProcesses;
    protected Collection<BonitaProcessUUID> myProcessesToIgnore;
    protected Collection<String> myInitiators;

    private Collection<String> myActiveUsers;
    private static final Date oldestDate = new Date(0);
    protected Date myStartedDateFrom = null;
    protected Date myStartedDateTo = null;
    protected Date myUpdatedDateFrom = null;
    protected Date myUpdatedDateTo = null;
    protected Date myCompletedDateFrom = null;
    protected Date myCompletedDateTo = null;
    protected int myCaseInstanceNumber;
    protected StepState myState;

    protected Category myCategory;

    

    private CaseFilter() {
        super();
        // Mandatory for serialization.
    }

    public CaseFilter(Collection<LabelUUID> aLabelUUIDCollection, int aStartingIndex, int aMaxElementCount) {
        this(aLabelUUIDCollection, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<BonitaProcessUUID>(), aStartingIndex, aMaxElementCount);
    }

    public CaseFilter(Collection<LabelUUID> aLabelUUIDCollection, Collection<String> aParticipantCollection, int aStartingIndex, int aMaxElementCount) {
        this(aLabelUUIDCollection, aParticipantCollection, new ArrayList<String>(), new ArrayList<BonitaProcessUUID>(), aStartingIndex, aMaxElementCount);
    }

    public CaseFilter(Collection<LabelUUID> aLabelUUIDCollection, Collection<String> aParticipantCollection, Collection<String> anInitiatorCollection, int aStartingIndex, int aMaxElementCount) {
        this(aLabelUUIDCollection, aParticipantCollection, anInitiatorCollection, new ArrayList<BonitaProcessUUID>(), aStartingIndex, aMaxElementCount);
    }

    /**
     * 
     * Default constructor.
     * 
     * @param aLabelUUIDCollection
     * @param aParticipantCollection
     * @param anInitiatorCollection
     * @param aProcessUUIDCollection
     */
    public CaseFilter(Collection<LabelUUID> aLabelUUIDCollection, Collection<String> aParticipantCollection, Collection<String> anInitiatorCollection,
            Collection<BonitaProcessUUID> aProcessUUIDCollection, int aStartingIndex, int aMaxElementCount) {
        super(aStartingIndex, aMaxElementCount);
        if (aLabelUUIDCollection != null) {
            myLabels = new ArrayList<LabelUUID>();
            myLabels.addAll(aLabelUUIDCollection);
        }
        myInvolvedUsers = aParticipantCollection;
        myInitiators = anInitiatorCollection;
        myProcesses = aProcessUUIDCollection;
        myCaseInstanceNumber = -1;
        myState = null;
    }

    /**
     * Get the value of the label field.
     * 
     * @return the label
     */
    public LabelUUID getLabel() {
        if (myLabels != null && myLabels.size() > 0) {
            return (LabelUUID) myLabels.toArray()[0];
        } else {
            return null;
        }
    }

    /**
     * @return the processCollection
     */
    public Collection<BonitaProcessUUID> getProcesses() {
        return myProcesses;
    }

    /**
     * @param aProcessCollection
     *            the processCollection to set
     */
    public void setProcesses(Collection<BonitaProcessUUID> aProcessCollection) {
        myProcesses = aProcessCollection;
    }

    /**
     * @return the processCollection
     */
    public Collection<BonitaProcessUUID> getProcessesToIgnore() {
        return myProcessesToIgnore;
    }
    
    /**
     * @param aProcessUUIDs the collection of processes to ignore.
     */
    public void setProcessesToIgnore(Collection<BonitaProcessUUID> aProcessUUIDs) {
        myProcessesToIgnore = aProcessUUIDs;
    }
    
    /**
     * @return the labelUUIDCollection
     */
    public Collection<LabelUUID> getLabels() {
        return myLabels;
    }

    /**
     * @param aLabelUUIDCollection
     *            the labelUUIDCollection to set
     */
    public void setLabels(Collection<LabelUUID> aLabelUUIDCollection) {
        final Collection<LabelUUID> theOldValue = new ArrayList<LabelUUID>();
        if (myLabels != null && !myLabels.isEmpty()) {
            theOldValue.addAll(myLabels);
        }
        myLabels = new ArrayList<LabelUUID>();
        final Collection<LabelUUID> theNewValue = new ArrayList<LabelUUID>();
        if (aLabelUUIDCollection != null && !aLabelUUIDCollection.isEmpty()) {
            myLabels.addAll(aLabelUUIDCollection);
            theNewValue.addAll(aLabelUUIDCollection);
        }
        myChanges.fireModelChange(LABEL_PROPERTY, theOldValue, theNewValue);
    }

    /**
     * Add a label to the filter.
     * 
     * @param aName
     */
    public void addLabel(LabelUUID aName) {
        if (aName != null) {
            if (myLabels == null) {
                myLabels = new ArrayList<LabelUUID>();
            }
            if (!myLabels.contains(aName)) {
                myLabels.add(aName);
            }
        }
    }

    /**
     * @return the participants
     */
    public Collection<String> getInvolvedUsers() {
        return myInvolvedUsers;
    }

    /**
     * @param aParticipants
     *            the participants to set
     */
    public void setInvolvedUsers(Collection<String> aParticipants) {
        myInvolvedUsers = aParticipants;
    }

    public void setActiveUsers(Collection<String> aActiveUsers) {
        myActiveUsers = aActiveUsers;

    }

    public Collection<String> getActiveUsers() {
        return myActiveUsers;
    }

    /**
     * @return the initiators
     */
    public Collection<String> getInitiators() {
        return myInitiators;
    }

    /**
     * @param anInitiatorCollections
     *            the initiators to set
     */
    public void setInitiators(Collection<String> anInitiatorCollections) {
        myInitiators = anInitiatorCollections;
    }

    /**
     * @return the maxElementCount
     */
    public int getMaxElementCount() {
        return myMaxElementCount;
    }

    /**
     * @param anMaxElementCount
     *            the maxElementCount to set
     */
    public void setMaxElementCount(int anMaxElementCount) {
        myMaxElementCount = anMaxElementCount;
    }

    public boolean searchInHistory() {
        return searchInHistory;
    }

    /**
     * 
     * @param aSearchInHistory
     */
    public void setSearchInHistory(boolean considerHistory) {
        setSearchInHistory(considerHistory, true);
    }
    
    /**
     * 
     * @param aSearchInHistory
     * @param fireEvent
     */
    public void setSearchInHistory(boolean considerHistory, boolean fireEvent) {
        boolean theOldValue = searchInHistory;
        searchInHistory = considerHistory;
        if (fireEvent) {
            myChanges.fireModelChange(SEARCH_IN_HISTORY_PROPERTY, theOldValue, searchInHistory());
        }
    }

    /**
     * @return the startedDateFrom
     */
    public Date getStartedDateFrom() {
        return myStartedDateFrom;
    }

    /**
     * @param aStartedDateFrom
     *            the startedDateFrom to set
     */
    private void setStartedDateFrom(Date aStartedDateFrom) {
        myStartedDateFrom = aStartedDateFrom;
    }

    /**
     * @return the startedDateTo
     */
    public Date getStartedDateTo() {
        return myStartedDateTo;
    }

    /**
     * @param aStartedDateTo
     *            the startedDateTo to set
     */
    private void setStartedDateTo(Date aStartedDateTo) {
        myStartedDateTo = aStartedDateTo;
    }

    /**
     * @return the updatedDateFrom
     */
    public Date getUpdatedDateFrom() {
        return myUpdatedDateFrom;
    }

    /**
     * @param aUpdatedDateFrom
     *            the updatedDateFrom to set
     */
    private void setUpdatedDateFrom(Date aUpdatedDateFrom) {
        myUpdatedDateFrom = aUpdatedDateFrom;
    }

    /**
     * @return the updatedDateTo
     */
    public Date getUpdatedDateTo() {
        return myUpdatedDateTo;
    }

    /**
     * @param aUpdatedDateTo
     *            the updatedDateTo to set
     */
    private void setUpdatedDateTo(Date aUpdatedDateTo) {
        myUpdatedDateTo = aUpdatedDateTo;
    }

    /**
     * @return the completedDateFrom
     */
    public Date getCompletedDateFrom() {
        return myCompletedDateFrom;
    }

    /**
     * @param aCompletedDateFrom
     *            the completedDateFrom to set
     */
    private void setCompletedDateFrom(Date aCompletedDateFrom) {
        myCompletedDateFrom = aCompletedDateFrom;
    }

    /**
     * @return the completedDateTo
     */
    public Date getCompletedDateTo() {
        return myCompletedDateTo;
    }

    /**
     * @param aCompletedDateTo
     *            the completedDateTo to set
     */
    private void setCompletedDateTo(Date aCompletedDateTo) {
        myCompletedDateTo = aCompletedDateTo;
    }

    public void setCategory(Category aCategory) {
        myCategory = aCategory;
    }

    public Category getCategory() {
        return myCategory;
    }

    public StepState getState(){
        return myState;
    }
    
    public void setState(StepState aState){
        myState = aState;
    }
    
    public int getCaseInstanceNumber() {
        return myCaseInstanceNumber;
    }

    public void setCaseInstanceNumber(int aCaseInstanceNumber) {
        myCaseInstanceNumber = aCaseInstanceNumber;
    }

    public CaseFilter createFilter() {
        final CaseFilter theNewFilter = new CaseFilter();
        theNewFilter.updateFilter(this);
        return theNewFilter;
    }

    public boolean isActive() {
        return (mySearchPattern != null) 
        || (myState !=null)
        || (myInvolvedUsers != null && !myInvolvedUsers.isEmpty())
        || (myInitiators != null && !myInitiators.isEmpty())
        || (myActiveUsers != null && !myActiveUsers.isEmpty() && !getLabel().equals(LabelModel.INBOX_LABEL.getUUID())) 
        || (myProcesses != null && !myProcesses.isEmpty())
        || (myStartedDateFrom != null && myStartedDateTo != null)
        || (myUpdatedDateFrom != null && myUpdatedDateTo != null) 
        || (myCompletedDateFrom != null && myCompletedDateTo != null) 
        || myCaseInstanceNumber > -1;
    }

    public void updateFilter(CaseFilter aFilter) {
        setActiveUsers(aFilter.getActiveUsers());
        setCompletedDate(aFilter.getCompletedDateFrom(), aFilter.getCompletedDateTo());
        setInitiators(aFilter.getInitiators());
        setInvolvedUsers(aFilter.getInvolvedUsers());
        setLabels(aFilter.getLabels());
        setMaxElementCount(aFilter.getMaxElementCount());
        setProcesses(aFilter.getProcesses());
        setProcessesToIgnore(aFilter.getProcessesToIgnore());
        setSearchInHistory(aFilter.searchInHistory());
        setSearchPattern(aFilter.getSearchPattern());
        setStartedDate(aFilter.getStartedDateFrom(), aFilter.getStartedDateTo());
        setStartingIndex(aFilter.getStartingIndex());
        setUpdatedDate(aFilter.getUpdatedDateFrom(), aFilter.getUpdatedDateTo());
        setWithAdminRights(aFilter.isWithAdminRights());
        setCaseInstanceNumber(aFilter.getCaseInstanceNumber());
        setState(aFilter.getState());
    }

    /**
     * set case Completed Date
     * 
     * @param aFilter
     */
    public void setCompletedDate(Date aCompletedDateFrom, Date aCompletedDateTo) {
        if (aCompletedDateFrom == null && aCompletedDateTo != null) {
            aCompletedDateFrom = oldestDate;
        } else if (aCompletedDateFrom != null && aCompletedDateTo == null) {
            aCompletedDateTo = new Date();
            if (aCompletedDateFrom.after(aCompletedDateTo)) {
                aCompletedDateTo = aCompletedDateFrom;
            }
        }
        setCompletedDateFrom(aCompletedDateFrom);
        setCompletedDateTo(aCompletedDateTo);
    }

    /**
     * set case Updated Date
     * 
     * @param aFilter
     */
    public void setUpdatedDate(Date aUptatedDateFrom, Date aUptatedDateTo) {
        if (aUptatedDateFrom == null && aUptatedDateTo != null) {
            aUptatedDateFrom = oldestDate;
        } else if (aUptatedDateFrom != null && aUptatedDateTo == null) {
            aUptatedDateTo = new Date();
            if (aUptatedDateFrom.after(aUptatedDateTo)) {
                aUptatedDateTo = aUptatedDateFrom;
            }
        }
        setUpdatedDateFrom(aUptatedDateFrom);
        setUpdatedDateTo(aUptatedDateTo);
    }

    /**
     * set case Started Date
     * 
     * @param aFilter
     */
    public void setStartedDate(Date aStartedDateFrom, Date aStartedDateTo) {
        if (aStartedDateFrom == null && aStartedDateTo != null) {
            aStartedDateFrom = oldestDate;
        } else if (aStartedDateFrom != null && aStartedDateTo == null) {
            aStartedDateTo = new Date();
            if (aStartedDateFrom.after(aStartedDateTo)) {
                aStartedDateTo = aStartedDateFrom;
            }
        }
        setStartedDateFrom(aStartedDateFrom);
        setStartedDateTo(aStartedDateTo);
    }
}
