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
package org.bonitasoft.console.client.model.cases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseItem.CaseItemState;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.cases.CaseUpdates;
import org.bonitasoft.console.client.cases.CasesConfiguration;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.ItemSelection;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseDataSourceImpl implements CaseDataSource {

    private final CaseData myRPCCaseData;

    private final AsyncHandler<CaseUpdates> myCaseHandler = new CaseHandler();

    private HashMap<CaseUUID, CaseItem> myKnownCases = null;

    private ArrayList<CaseItem> myVisibleCases = null;

    private CaseFilter myFilter;

    protected final MessageDataSource myMessageDataSource;

    private final transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

    private LabelDataSource myLabelDataSource;

    private final CaseSelection myCaseSelection = new CaseSelection(this);

    protected boolean warningExists;

    private int mySize;

    protected final HashMap<CaseUUID, List<CommentItem>> myCaseComments = new HashMap<CaseUUID, List<CommentItem>>();

    protected CategoryDataSource myCategoryDataSource;

    protected final List<AsyncHandler<CasesConfiguration>> myGetConfigurationHandlers = new ArrayList<AsyncHandler<CasesConfiguration>>();

    private AsyncHandler<CasesConfiguration> myGetConfigurationHandler;

    private CasesConfiguration myConfiguration;

    class UpdateWaitingMessageTimer extends Timer {
        
        private String messageToDisplay;
        
        public UpdateWaitingMessageTimer (String message){
            messageToDisplay = message;
        }
        
        public void run() {
            updateWaitingMessage(messageToDisplay);
        };
    };
    
    protected UpdateWaitingMessageTimer updateWaitingMessageTimer = null;
    
    // private boolean isReloading;

    class CaseHandler implements AsyncHandler<CaseUpdates> {

        public void handleSuccess(CaseUpdates aResult) {
            GWT.log("RPC-response: received new cases");
            if (myKnownCases == null) {
                myKnownCases = new HashMap<CaseUUID, CaseItem>();
            }

            // update the local data.
            updateCases(aResult.getCases());
            
            ArrayList<CaseItem> theOldValue = null;
            if (myVisibleCases != null) {
                theOldValue = new ArrayList<CaseItem>();
                theOldValue.addAll(myVisibleCases);
                myVisibleCases.clear();
            } else {
                myVisibleCases = new ArrayList<CaseItem>();
            }

            final ArrayList<CaseUUID> theVisibleCasesUUIDs = new ArrayList<CaseUUID>();
            for (CaseItem theCaseItem : aResult.getCases()) {
                myVisibleCases.add(getItem(theCaseItem.getUUID()));
                theVisibleCasesUUIDs.add(theCaseItem.getUUID());
            }

            // Keep the selection consistent
            final ArrayList<CaseUUID> selectedItems = new ArrayList<CaseUUID>(myCaseSelection.getSelectedItems());
            for (CaseUUID theCaseUUID : selectedItems) {
                if (!theVisibleCasesUUIDs.contains(theCaseUUID)) {
                    GWT.log("Removed item from selection as it do not belong to the visible cases anymore: " + theCaseUUID.getValue());
                    myCaseSelection.removeItemFromSelection(theCaseUUID);
                }
            }

            if (aResult.getNbOfCases() <= 0) {
                mySize = -1;
                if (myFilter.getLabel() != null) {
                    // Get label updates to compute the number of available
                    // cases (based
                    // on label).
                    myLabelDataSource.getLabelUpdates(myFilter.getLabel(), myFilter.searchInHistory());
                } else if (myFilter.getCategory() != null) {
                    myCategoryDataSource.getNumberOfCases(myFilter.getCategory(), myFilter.searchInHistory());
                }
            } else {
                mySize = aResult.getNbOfCases();
            }
            myMessageDataSource.addInfoMessage(patterns.lastTimeRefreshed(DateTimeFormat.getFormat(constants.timeShortFormat()).format(new Date())));
            myChanges.fireModelChange(CASE_LIST_PROPERTY, theOldValue, myVisibleCases);

            // isReloading = false;
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.bonitasoft.console.client.common.data.AsyncHandler#handleFailure
         * (java.lang.Throwable)
         */
        public void handleFailure(Throwable aT) {
            // isReloading = false;
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            myMessageDataSource.addErrorMessage(messages.unableToLoadCases());
            GWT.log(messages.unableToLoadCases(), aT);
        }
    }
    
    /**
     * 
     * update Waiting Message
     * 
     * @param message
     */
    public void updateWaitingMessage(String message){
        myMessageDataSource.addInfoMessage(message);
    }
    
    /**
     * 
     * Default constructor.
     * 
     * @param aMessageDataSource
     */
    public CaseDataSourceImpl(MessageDataSource aMessageDataSource) {
        super();
        myMessageDataSource = aMessageDataSource;
        myRPCCaseData = new CaseData();
    }

    public void addCategoryDataSource(CategoryDataSource aCategoryDataSource){
        myCategoryDataSource = aCategoryDataSource;
    }
    
    public void addLabelDataSource(LabelDataSource aLabelDataSource){
        
        myLabelDataSource = aLabelDataSource;
        
        final ModelChangeListener theRenamedLabelListener = new ModelChangeListener() {
            // Each time a label is renamed locally update the cases.
            public void modelChange(ModelChangeEvent anEvt) {
                LabelUUID theOldLabelUUID = (LabelUUID) anEvt.getOldValue();
                LabelUUID theNewLabelUUID = (LabelUUID) anEvt.getNewValue();
                List<LabelUUID> theNewLabelList;
                for (CaseItem theCaseItem : myKnownCases.values()) {
                    theNewLabelList = theCaseItem.getLabels();
                    if (theNewLabelList.contains(theOldLabelUUID)) {
                        theNewLabelList.remove(theOldLabelUUID);
                        theNewLabelList.add(theNewLabelUUID);
                        theCaseItem.setCustomLabels(theNewLabelList);
                    }
                }
            }
        };

        myLabelDataSource.addModelChangeListener(LabelDataSource.USER_LABEL_LIST_PROPERTY, new ModelChangeListener() {
            @SuppressWarnings("unchecked")
            public void modelChange(ModelChangeEvent anEvt) {
                ArrayList<LabelModel> theOldList = (ArrayList<LabelModel>) anEvt.getOldValue();
                ArrayList<LabelModel> theNewList = (ArrayList<LabelModel>) anEvt.getNewValue();
                ArrayList<LabelModel> theRemovedLabels = new ArrayList<LabelModel>(theOldList);
                theRemovedLabels.removeAll(theNewList);
                ArrayList<LabelModel> theAddedLabels = new ArrayList<LabelModel>(theNewList);
                theAddedLabels.removeAll(theOldList);
                for (LabelModel theLabelModel : theRemovedLabels) {
                    theLabelModel.removeModelChangeListener(LabelModel.NAME_PROPERTY, theRenamedLabelListener);
                }
                for (LabelModel theLabelModel : theAddedLabels) {
                    theLabelModel.addModelChangeListener(LabelModel.NAME_PROPERTY, theRenamedLabelListener);
                }
            }
        });
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.CaseDataSource#cancelCases(java.util
     * .List)
     */
    @SuppressWarnings("unchecked")
    public void cancelCases(List<CaseUUID> anItemSelection) {
        if (anItemSelection != null && !anItemSelection.isEmpty()) {
            warningExists = false;
            final List<CaseUUID> theCasesToCancel = new ArrayList<CaseUUID>();
            for (CaseUUID theCaseUUID : anItemSelection) {
                if (getItem(theCaseUUID).getState() == CaseItemState.STARTED) {
                    theCasesToCancel.add(theCaseUUID);
                } else {
                    warningExists = true;
                }
            }
            if (theCasesToCancel.size() > 0) {

                GWT.log("RPC: cancelling cases", null);
                myRPCCaseData.cancelCases(myFilter, theCasesToCancel, new AsyncHandler<CaseUpdates>() {
                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * org.bonitasoft.console.client.common.data.AsyncHandler
                     * #handleFailure (java.lang.Throwable)
                     */
                    public void handleFailure(final Throwable aT) {
                        if (aT instanceof SessionTimeOutException) {
                            myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                        } else if (aT instanceof ConsoleSecurityException) {
                            myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                        }
                        myMessageDataSource.addErrorMessage(messages.unableToCancelCases());
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * org.bonitasoft.console.client.common.data.AsyncHandler
                     * #handleSuccess (java.lang.Object)
                     */
                    public void handleSuccess(final CaseUpdates aResult) {
                        if (warningExists) {
                            myMessageDataSource.addWarningMessage(messages.casesCancelledWithWarning());
                        } else {
                            myMessageDataSource.addInfoMessage(messages.casesCancelled(theCasesToCancel.size()));
                        }
                        myCaseHandler.handleSuccess(aResult);

                    }
                });
            } else {
                myMessageDataSource.addWarningMessage(messages.noStartedCasesSelected());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.CaseDataSource#deleteCases(java.util
     * .List)
     */
    @SuppressWarnings("unchecked")
    public void deleteCases(List<CaseUUID> aItemSelection, boolean deleteAttachments) {
        if (aItemSelection != null && !aItemSelection.isEmpty()) {
            GWT.log("RPC: deleting cases", null);
            myMessageDataSource.addInfoMessage(messages.deletingCases());
            updateWaitingMessageTimer = new UpdateWaitingMessageTimer(messages.deletingCases());
            updateWaitingMessageTimer.scheduleRepeating(10000);
            AsyncHandler<Void> theLocalHandler = new AsyncHandler<Void>() {
                /*
                 * (non-Javadoc)
                 * 
                 * @seeorg.bonitasoft.console.client.common.data.AsyncHandler#
                 * handleFailure (java.lang.Throwable)
                 */
                public void handleFailure(final Throwable aT) {
                    if (aT instanceof SessionTimeOutException) {
                        myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                    } else if (aT instanceof ConsoleSecurityException) {
                        myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                    }
                    updateWaitingMessageTimer.cancel();
                    myMessageDataSource.addErrorMessage(messages.unableToDeleteCases());
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @seeorg.bonitasoft.console.client.common.data.AsyncHandler#
                 * handleSuccess (java.lang.Object)
                 */
                public void handleSuccess(final Void aResult) {
                    updateWaitingMessageTimer.cancel();
                    reload();
                    myMessageDataSource.addInfoMessage(messages.casesDeleted());
                }
            };
            myRPCCaseData.deleteCases(aItemSelection, deleteAttachments,theLocalHandler );
        }
    }

    public CaseItem getItem(CaseUUID aCaseUUID) {
        if (myKnownCases != null) {
            return myKnownCases.get(aCaseUUID);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bonitasoft.console.client.model.CaseDataSource#getFilter()
     */
    public CaseFilter getFilter() {
        return myFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#getVisibleItems
     * ()
     */
    public List<CaseUUID> getVisibleItems() {
        List<CaseUUID> theResult = null;
        if (myVisibleCases != null) {
            // Collections.sort(myVisibleCases);
            // Collections.reverse(myVisibleCases);
            theResult = new ArrayList<CaseUUID>();
            for (CaseItem theCaseItem : myVisibleCases) {
                theResult.add(theCaseItem.getUUID());
            }
        }

        return theResult;
    }

    @SuppressWarnings("unchecked")
    private void reloadCases(final Collection<CaseUUID> aCaseSelection, final AsyncHandler<Collection<CaseItem>> aHandler) {
        if (aCaseSelection != null && !aCaseSelection.isEmpty()) {
            GWT.log("RPC: reloading case selection.", null);
            myRPCCaseData.getCases(aCaseSelection, myFilter, new AsyncHandler<Collection<CaseItem>>() {
                public void handleFailure(Throwable aT) {
                    if (aHandler != null) {
                        aHandler.handleFailure(aT);
                    }

                }

                public void handleSuccess(Collection<CaseItem> aResult) {
                    if (aResult != null) {
                        updateCases(aResult);
                        if (aHandler != null) {
                            ArrayList<CaseItem> theResult = new ArrayList<CaseItem>();
                            for (CaseUUID caseUUID : aCaseSelection) {
                                theResult.add(myKnownCases.get(caseUUID));
                            }
                            aHandler.handleSuccess(theResult);
                        }
                    }
                }
            });
        }
    }

    protected void updateCases(Collection<CaseItem> aCaseList) {
        CaseItem theExistingCase;
        List<StepItem> theExistingSteps;
        List<StepItem> theNewSteps;

        StepItem theOldStepItem;
        for (CaseItem theNewCase : aCaseList) {
            // Update the local cache.
            if (myKnownCases.containsKey(theNewCase.getUUID())) {
                theExistingCase = myKnownCases.get(theNewCase.getUUID());
                theExistingSteps = theExistingCase.getSteps();
                theNewSteps = new ArrayList<StepItem>();
                for (StepItem theNewStepItem : theNewCase.getSteps()) {
                    int thePos = theExistingSteps.indexOf(theNewStepItem);
                    if (thePos > -1) {
                        // Update the existing step
                        theOldStepItem = theExistingSteps.get(thePos);
                        theOldStepItem.setAssign(theNewStepItem.getAssign());
                        theOldStepItem.setAuthor(theNewStepItem.getAuthor());
                        theOldStepItem.setLastUpdateDate(theNewStepItem.getLastUpdateDate());
                        theOldStepItem.setState(theNewStepItem.getState());
                        theOldStepItem.setNumberOfComments(theNewStepItem.getNumberOfComments());
                        theOldStepItem.setApplicationURL(theNewStepItem.getApplicationURL());
                        theOldStepItem.setExecutionSummary(theNewStepItem.getExecutionSummary());
                        theOldStepItem.setPriority(theNewStepItem.getPriority());
                        theOldStepItem.setStepDesc(theNewStepItem.getDesc());
                        theOldStepItem.getCase().setIsArchived(theNewStepItem.getCase().isArchived());
                        theNewSteps.add(theOldStepItem);
                    } else {
                        // add a new step
                        theNewSteps.add(theNewStepItem);
                    }
                }
                theExistingCase.setSteps(theNewSteps);
                List<LabelUUID> theNewSystemLabels = new ArrayList<LabelUUID>();
                List<LabelUUID> theNewUserLabels = new ArrayList<LabelUUID>();
                ArrayList<LabelUUID> theNewLabels = theNewCase.getLabels();
                for (LabelUUID theLabelUUID : theNewLabels) {
                    if (myLabelDataSource.getLabel(theLabelUUID).isSystemLabel()) {
                        theNewSystemLabels.add(theLabelUUID);
                    } else {
                        theNewUserLabels.add(theLabelUUID);
                    }
                }
                // Update the existing case with the data coming from the
                // server.
                theExistingCase.setSystemLabels(theNewSystemLabels);
                theExistingCase.setCustomLabels(theNewUserLabels);

                // Update the case state.
                theExistingCase.setState(theNewCase.getState());

                theExistingCase.setIsArchived(theNewCase.isArchived());
                theExistingCase.setLastUpdateDate(theNewCase.getLastUpdateDate());
            } else {
                // A new case is available. Add it into the local cache.
                myKnownCases.put(theNewCase.getUUID(), theNewCase);
            }

        }

    }

    /**
     * @return the case selection
     */
    public CaseSelection getCaseSelection() {
        return myCaseSelection;
    }
    
    public void setItemFilter(CaseFilter aFilter) {
        setItemFilter(aFilter, true);
    }

    public void setItemFilter(CaseFilter aFilter, boolean fireEvent) {
        CaseFilter theOldValue = myFilter;
        myFilter = aFilter;
        if (fireEvent) {
            // reset the visible items.
            resetVisibleItems();
            reload();
            myChanges.fireModelChange(FILTER_UPDATED, theOldValue, myFilter);
        }
    }

    private void resetVisibleItems() {
        if (myVisibleCases != null) {
            ArrayList<CaseItem> theOldValue = new ArrayList<CaseItem>();
            theOldValue.addAll(myVisibleCases);
            myVisibleCases = null;
            myChanges.fireModelChange(CASE_LIST_PROPERTY, theOldValue, myVisibleCases);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.BonitaDataSource#addModelChangeListener
     * (java.lang.String, java.beans.ModelChangeListener)
     */
    public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        // Avoid multiple registration.
        myChanges.removeModelChangeListener(aPropertyName, aListener);
        myChanges.addModelChangeListener(aPropertyName, aListener);

    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.bonitasoft.console.client.model.BonitaDataSource#
     * removeModelChangeListener (java.lang.String,
     * java.beans.ModelChangeListener)
     */
    public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        myChanges.removeModelChangeListener(aPropertyName, aListener);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#getItemFilter
     * ()
     */
    public CaseFilter getItemFilter() {
        return myFilter;
    }

    @SuppressWarnings("unchecked")
    public void reload() {
        // Reload asynchronously data from server.
        if (myFilter != null && myFilter.getMaxElementCount() > 0 && myFilter.getStartingIndex() >= 0) {
            GWT.log("RPC: reloading cases");
            GWT.log("---RPC (Filter): from " + myFilter.getStartingIndex(), null);
            GWT.log("---RPC (Filter): to   " + (myFilter.getStartingIndex() + myFilter.getMaxElementCount()), null);
            myMessageDataSource.addInfoMessage(messages.loading());
            myRPCCaseData.getAllCases(myFilter, myCaseHandler);
        } else {
            Window.alert("Cannot reload cases whitout a valid filter!");
        }
    }

    public int getSize() {
        if (mySize < 0) {
            if (myFilter.getLabel() != null) {
                if (myFilter.getLabel().equals(LabelModel.ADMIN_ALL_CASES.getUUID())) {
                    return myLabelDataSource.getTotalCaseNumbers();
                } else {
                    return myLabelDataSource.getLabel(myFilter.getLabel()).getNbOfCases();
                }
            } else if (myFilter.getCategory() != null) {
                return myCategoryDataSource.getItem(myFilter.getCategory().getUUID()).getNbOfCases();
            } else {
                GWT.log("Illegal state: both label and category is null in case filter!", new Exception());
                return 0;
            }

        } else {
            return mySize;
        }
    }

    @SuppressWarnings("unchecked")
    public void getItem(final CaseUUID aCaseUUID, final AsyncHandler<CaseItem> anAsyncHandler) {
        // if (myKnownCases != null && myKnownCases.containsKey(aCaseUUID)) {
        // anAsyncHandler.handleSuccess(myKnownCases.get(aCaseUUID));
        // } else {
        if (myFilter != null) {
            myRPCCaseData.getCase(aCaseUUID, myFilter, new AsyncHandler<CaseItem>() {
                public void handleFailure(Throwable anT) {
                    if (anAsyncHandler != null) {
                        anAsyncHandler.handleFailure(anT);
                    }

                }

                public void handleSuccess(CaseItem anResult) {
                    if (anResult != null) {
                        if (myKnownCases == null) {
                            myKnownCases = new HashMap<CaseUUID, CaseItem>();
                        }
                        updateCases(Arrays.asList(anResult));
                    }
                    if (anAsyncHandler != null) {
                        anAsyncHandler.handleSuccess(myKnownCases.get(aCaseUUID));
                    }
                }
            });
        } else {
            GWT.log("Unable to get a case without a valid case filter.", new NullPointerException());
        }
        // }

    }

    @SuppressWarnings("unchecked")
    public void synchronizeDBs(final AsyncHandler<Void> aHandler) {
        myRPCCaseData.synchronizeDBs(new AsyncHandler<Void>() {
            public void handleFailure(Throwable anT) {
                if (aHandler != null) {
                    aHandler.handleFailure(anT);
                }
                myMessageDataSource.addErrorMessage(messages.databaseCannotBeSynchronized());
            }

            public void handleSuccess(Void aResult) {
                if (aHandler != null) {
                    aHandler.handleSuccess(aResult);
                }
                myMessageDataSource.addInfoMessage(messages.databaseSynchronized());
            }
        });

    }

    @SuppressWarnings("unchecked")
    public void getCaseCommentFeed(final CaseUUID aCaseUUID, final AsyncHandler<List<CommentItem>> aHandler) {
        if (aCaseUUID == null) {
            throw new IllegalArgumentException("CaseUUID must be not null!");
        }

        if (aHandler != null) {
            // return local data by now.
            if (myCaseComments.containsKey(aCaseUUID)) {
                aHandler.handleSuccess(myCaseComments.get(aCaseUUID));
            } else {
                aHandler.handleSuccess(new ArrayList<CommentItem>());
            }
        }

        // Update local data with server's one, then notify changes.
        myRPCCaseData.getCaseCommentFeed(aCaseUUID, myFilter, new AsyncHandler<List<CommentItem>>() {
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

            public void handleSuccess(List<CommentItem> aResult) {
                updateCaseCommentFeedAndNotify(aCaseUUID, aResult);
            }
        });

    }

    /**
     * @param aCaseUUID
     * @param aResult
     */
    protected void updateCaseCommentFeedAndNotify(CaseUUID aCaseUUID, List<CommentItem> aResult) {
        List<CommentItem> theOldValue = myCaseComments.get(aCaseUUID);
        if (aResult != null) {
            final ArrayList<CommentItem> theCaseComments = new ArrayList<CommentItem>();
            theCaseComments.addAll(aResult);
            myCaseComments.put(aCaseUUID, theCaseComments);
        }
        myChanges.fireModelChange(new ModelChangeEvent(aCaseUUID, COMMENTS_PROPERTY, theOldValue, myCaseComments.get(aCaseUUID)));

    }

    public void getItems(final List<CaseUUID> aUUIDSelection, final AsyncHandler<List<CaseItem>> aHandler) {
        List<CaseUUID> theCasesToReload = null;
        CaseItem theCaseItem;
        for (CaseUUID theCaseUUID : aUUIDSelection) {
            theCaseItem = getItem(theCaseUUID);
            if (theCaseItem == null) {
                if (theCasesToReload == null) {
                    theCasesToReload = new ArrayList<CaseUUID>();
                }
                theCasesToReload.add(theCaseUUID);
            }
        }
        if (theCasesToReload != null && !theCasesToReload.isEmpty()) {
            reloadCases(theCasesToReload, new AsyncHandler<Collection<CaseItem>>() {
                public void handleFailure(Throwable aT) {

                }

                public void handleSuccess(Collection<CaseItem> aCaseList) {
                    if (aCaseList != null) {
                        List<CaseItem> theResult = new ArrayList<CaseItem>();
                        for (CaseUUID theCaseUUID : aUUIDSelection) {
                            theResult.add(getItem(theCaseUUID));
                        }
                        if (aHandler != null) {
                            aHandler.handleSuccess(theResult);
                        }
                    } else {
                        if (aHandler != null) {
                            aHandler.handleSuccess(null);
                        }
                    }

                }
            });
        }
    }

    public void getVisibleItems(CaseFilter aFilter, AsyncHandler<List<CaseItem>> aHandler) {
        // TODO
        throw new RuntimeException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#addItem(org
     * .bonitasoft.console.client.Item,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    public void addItem(CaseItem aAnItem, AsyncHandler<ItemUpdates<CaseItem>> aHandler) {
        Window.alert("Not supported: casedatasource.addItem()");

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#deleteItems
     * (java.util.List, org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    public void deleteItems(List<CaseUUID> anItemSelection, AsyncHandler<ItemUpdates<CaseItem>> aHandler) {
        deleteItems(anItemSelection, aHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#getItemSelection
     * ()
     */
    public ItemSelection<CaseUUID> getItemSelection() {
        return getCaseSelection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#updateItem
     * (org.bonitasoft.console.client.BonitaUUID,
     * org.bonitasoft.console.client.Item,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    public void updateItem(CaseUUID aAnUUID, CaseItem aAnItem, AsyncHandler<CaseItem> aHandler) {
        Window.alert("Not supported: casedatasource.updateItem()");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.cases.CaseDataSource#addCaseComment
     * (org.bonitasoft.console.client.cases.CaseUUID, java.lang.String,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @SuppressWarnings("unchecked")
    public void addCaseComment(final CaseUUID aCaseUUID, final String aComment, final AsyncHandler<List<CommentItem>> aHandler) {
        if (aCaseUUID != null && aComment != null && aComment.length() > 0) {
            myRPCCaseData.addCommentToCase(aCaseUUID, aComment, myFilter, new AsyncHandler<List<CommentItem>>() {
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

                public void handleSuccess(List<CommentItem> aResult) {
                    if (aResult != null) {
                        updateCaseCommentFeedAndNotify(aCaseUUID, aResult);
                        if (aHandler != null) {
                            aHandler.handleSuccess(myCaseComments.get(aCaseUUID));
                        }
                    } else {
                        if (aHandler != null) {
                            aHandler.handleSuccess(null);
                        }
                    }
                }
            });
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.cases.CaseDataSource#getConfiguration
     * (org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    public void getConfiguration(AsyncHandler<CasesConfiguration> aHandler) {

        if (aHandler != null) {
            if (myConfiguration == null) {
                // first time call
                myGetConfigurationHandlers.add(aHandler);
            } else {
                // return local data by now.
                aHandler.handleSuccess(myConfiguration);
            }
        }
        if (myGetConfigurationHandler == null) {
            buildConfigurationHandler();
        }
        myRPCCaseData.getConfiguration(myGetConfigurationHandler);
    }

    private void buildConfigurationHandler() {
        myGetConfigurationHandler = new AsyncHandler<CasesConfiguration>() {

            public void handleSuccess(CasesConfiguration aResult) {
                final CasesConfiguration theOldConfiguration = new CasesConfiguration();
                if (myConfiguration == null) {
                    myConfiguration = new CasesConfiguration();
                }
                theOldConfiguration.update(myConfiguration);
                myConfiguration.update(aResult);
                final List<AsyncHandler<CasesConfiguration>> theHandlers = new ArrayList<AsyncHandler<CasesConfiguration>>(myGetConfigurationHandlers);
                myGetConfigurationHandlers.clear();
                for (AsyncHandler<CasesConfiguration> theHandler : theHandlers) {
                    theHandler.handleSuccess(myConfiguration);
                }
                myChanges.fireModelChange(CONFIGURATION_PROPERTY, theOldConfiguration, myConfiguration);
            }

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

                final List<AsyncHandler<CasesConfiguration>> theHandlers = new ArrayList<AsyncHandler<CasesConfiguration>>(myGetConfigurationHandlers);
                myGetConfigurationHandlers.clear();
                for (AsyncHandler<CasesConfiguration> theHandler : theHandlers) {
                    theHandler.handleFailure(aT);
                }
                GWT.log(messages.unableToReadConfiguration(), aT);
            }
        };

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.cases.CaseDataSource#updateConfiguration
     * (org.bonitasoft.console.client.cases.CasesConfiguration,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    public void updateConfiguration(final CasesConfiguration aNewConfiguration, final AsyncHandler<CasesConfiguration> aHandler) {
        if (aHandler != null) {
            myGetConfigurationHandlers.add(aHandler);
        }
        if (myGetConfigurationHandler == null) {
            buildConfigurationHandler();
        }
        myRPCCaseData.updateConfiguration(aNewConfiguration, myGetConfigurationHandler);
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.console.client.model.BonitaFilteredDataSource#listItems(org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    public void listItems(CaseFilter aFilter, AsyncHandler<ItemUpdates<CaseItem>> aHandler) {
        Window.alert("CaseDataSource not supported method: listItems");
        
    }
    
    public void sendRedirectToCurrentPosition(final Set<UserUUID> theCandidates) {
    	myChanges.fireModelChange(TO_CURRENT_POSITION, null, theCandidates);
    }

}
