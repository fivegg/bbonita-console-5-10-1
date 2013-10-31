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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.console.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class DefaultFilteredDataSourceImpl<U extends BonitaUUID, I extends Item, F extends ItemFilter> implements BonitaFilteredDataSource<U, I, F> {

    protected final RPCData<U, I, F> myRPCItemData;

    protected final ItemSelection<U> myItemSelection;

    protected final AsyncHandler<ItemUpdates<I>> myItemsHandler = new ItemsHandler();

    protected HashMap<U, I> myKnownItems = null;

    protected ArrayList<I> myVisibleItems = null;

    protected F myFilter;

    protected final MessageDataSource myMessageDataSource;

    protected final transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

    protected int mySize;

    private boolean isLoading;

    protected final HashMap<U, ArrayList<AsyncHandler<I>>> myGetItemHandlers = new HashMap<U, ArrayList<AsyncHandler<I>>>();

    /**
     * Default constructor.
     */
    public DefaultFilteredDataSourceImpl(final RPCData<U, I, F> aRPCItemData, final ItemSelection<U> anItemSelection, final MessageDataSource aMessageDataSource) {
        myRPCItemData = aRPCItemData;
        myItemSelection = anItemSelection;
        myItemSelection.setDataSource(this);
        myMessageDataSource = aMessageDataSource;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#getItem(java
     * .lang.Object, org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @Override
    public void getItem(final U anUUID, final AsyncHandler<I> aHandler) {
        if (myFilter != null) {
            // Try to limit the number of calls to server.
            ArrayList<AsyncHandler<I>> theItemHandlers = myGetItemHandlers.get(anUUID);

            if (aHandler != null) {
                final I theResult = getItem(anUUID);
                if (theResult != null) {
                    // return local data by now.
                    GWT.log("Returning local data for item: " + anUUID);
                    aHandler.handleSuccess(theResult);
                } else {
                    if (theItemHandlers == null) {
                        // No request is ongoing.
                        theItemHandlers = new ArrayList<AsyncHandler<I>>();
                        myGetItemHandlers.put(anUUID, theItemHandlers);
                    }
                    // add current handler to queue.
                    GWT.log("Adding handler to queue for item: " + anUUID);
                    theItemHandlers.add(aHandler);
                }
            }
            if (theItemHandlers == null) {
                // No request is ongoing.
                // No handler is waiting for answer.
                // Force to reload and trigger event.
                theItemHandlers = new ArrayList<AsyncHandler<I>>();
                myGetItemHandlers.put(anUUID, theItemHandlers);
                GWT.log("Force relaoding item: " + anUUID);
                if (myGetItemHandlers.get(anUUID) == null) {
                    GWT.log("List of handlers NULL before sync!", new NullPointerException());
                } else {
                    GWT.log("List of handlers not null before sync: " + myGetItemHandlers.get(anUUID).size());
                }
                getItemFromServerAndNotifyChanges(anUUID);
            } else if (theItemHandlers.size() == 1) {
                // No request is ongoing.
                // Sync data from server
                GWT.log("Syncing item: " + anUUID);
                if (myGetItemHandlers.get(anUUID) == null) {
                    GWT.log("List of handlers NULL before sync!", new NullPointerException());
                } else {
                    GWT.log("List of handlers not null before sync: " + myGetItemHandlers.get(anUUID).size());
                }
                getItemFromServerAndNotifyChanges(anUUID);
            } else {
                GWT.log("Skipping server call for item: " + anUUID + ", as there is already items in queue: " + theItemHandlers.size());
            }
        } else {
            GWT.log("Impossible to query an item without a valid filter.", new IllegalArgumentException());
        }
    }

    /**
     * Get item and notify all handlers in queue.
     */
    @SuppressWarnings("unchecked")
    protected void getItemFromServerAndNotifyChanges(final U anUUID) {
        myRPCItemData.getItem(anUUID, myFilter, new AsyncHandler<I>() {

            @Override
            public void handleFailure(Throwable anT) {
                final ArrayList<AsyncHandler<I>> theHandlersInQueue = myGetItemHandlers.remove(anUUID);
                GWT.log("Throwing error to handlers in queue for: " + anUUID);
                for (AsyncHandler<I> theAsyncHandler : theHandlersInQueue) {
                    if (theAsyncHandler != null) {
                        theAsyncHandler.handleFailure(anT);
                    }
                }
            }

            @Override
            public void handleSuccess(I anResult) {
                if (myKnownItems == null) {
                    myKnownItems = new HashMap<U, I>();
                }
                if (anResult != null) {
                    updateItems(Arrays.asList(anResult));
                }
                final ArrayList<AsyncHandler<I>> theHandlersInQueue = myGetItemHandlers.remove(anUUID);
                if (theHandlersInQueue == null) {
                    GWT.log("A call to server that was not mandatory as been performed!", new NullPointerException());
                } else {
                    GWT.log("Sending response to handlers in queue for: " + anUUID);
                    final I theResult = myKnownItems.get(anUUID);
                    for (AsyncHandler<I> theAsyncHandler : theHandlersInQueue) {
                        if (theAsyncHandler != null) {
                            theAsyncHandler.handleSuccess(theResult);
                        }
                    }
                }
            }
        });

    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#getItemFilter
     * ()
     */
    @Override
    public F getItemFilter() {
        return myFilter;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#getItems
     * (java .util.List, org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void getItems(final List<U> aUUIDSelection, final AsyncHandler<List<I>> aHandler) {
        if (myFilter != null) {
            if (aUUIDSelection == null || aUUIDSelection.isEmpty()) {
                Window.alert("Querying with an empty selection!");
            }
            myRPCItemData.getItems(aUUIDSelection, myFilter, new AsyncHandler<List<I>>() {

                @Override
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
                    GWT.log("DefaultFilteredDataSource.getItems", aT);
                    if (aHandler != null) {
                        aHandler.handleFailure(aT);
                    }
                }

                @Override
                public void handleSuccess(List<I> anResult) {
                    if (anResult != null) {
                        if (anResult.size() > 0) {
                            if (myKnownItems == null) {
                                myKnownItems = new HashMap<U, I>();
                            }
                            updateItems(anResult);
                        } else {
                            for (U theUUID : aUUIDSelection) {
                                if (myKnownItems != null) {
                                    myKnownItems.remove(theUUID);
                                }
                            }
                        }
                    }
                    if (aHandler != null) {
                        final ArrayList<I> theResult = new ArrayList<I>();
                        for (U theUUID : aUUIDSelection) {
                            if (myKnownItems != null && myKnownItems.get(theUUID) != null) {
                                theResult.add(myKnownItems.get(theUUID));
                            }
                        }
                        aHandler.handleSuccess(theResult);
                    }
                }
            });
        } else {
            GWT.log("Impossible to query items without a valid filter.", new IllegalArgumentException());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#getSize()
     */
    @Override
    public int getSize() {
        return mySize;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#getVisibleItems
     * ()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<U> getVisibleItems() {
        List<U> theResult = null;
        if (myVisibleItems != null) {
            theResult = new ArrayList<U>();
            for (I theCaseItem : myVisibleItems) {
                theResult.add((U) theCaseItem.getUUID());
            }
        } else {
            // DataSource not yet initialized.
            // Reload asynchronously
            if (!isLoading) {
                isLoading = true;
                reload();
            }
        }
        return theResult;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void listItems(final F aFilter, final AsyncHandler<ItemUpdates<I>> aHandler) {
        if (aFilter != null) {
            myRPCItemData.getAllItems(aFilter, new AsyncHandler<ItemUpdates<I>>() {

                @Override
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
                    GWT.log("DefaultFilteredDataSource.listItems", aT);
                    if (aHandler != null) {
                        aHandler.handleFailure(aT);
                    }
                }

                @Override
                public void handleSuccess(ItemUpdates<I> anResult) {
                    if (anResult != null) {
                        if (myKnownItems == null) {
                            myKnownItems = new HashMap<U, I>();
                        }
                        updateItems(anResult.getItems());
                    }
                    if (aHandler != null) {
                        ArrayList<I> theListOfItems = new ArrayList<I>(anResult.getNbOfItems());
                        for (I theItem : anResult.getItems()) {
                            theListOfItems.add(myKnownItems.get(theItem.getUUID()));
                        }
                        final ItemUpdates<I> theResult = new ItemUpdates<I>(theListOfItems, anResult.getNbOfItems());

                        aHandler.handleSuccess(theResult);
                    }
                }
            });
        } else {
            GWT.log("Impossible to query items without a valid filter.", new IllegalArgumentException());
        }
    }

    /**
     * @return the itemSelection
     */
    @Override
    public ItemSelection<U> getItemSelection() {
        return myItemSelection;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#reload()
     */
    @Override
    @SuppressWarnings("unchecked")
    public void reload() {
        // Reload asynchronously data from server.
        if (myFilter != null && myFilter.getMaxElementCount() > 0 && myFilter.getStartingIndex() >= 0) {
            GWT.log("RPC: reloading items");
            GWT.log("---RPC (Filter): from " + myFilter.getStartingIndex());
            GWT.log("---RPC (Filter): to   " + (myFilter.getStartingIndex() + myFilter.getMaxElementCount()));
            resetVisibleItems();
            if (myMessageDataSource != null) {
                myMessageDataSource.addInfoMessage(messages.loading());
            }
            myRPCItemData.getAllItems(myFilter, myItemsHandler);
        } else {
            Window.alert("Cannot reload items whitout a valid filter!");
        }

    }

    private void resetVisibleItems() {
        if (myVisibleItems != null) {
            ArrayList<I> theOldValue = null;
            theOldValue = new ArrayList<I>();
            theOldValue.addAll(myVisibleItems);
            myVisibleItems = null;
            myChanges.fireModelChange(ITEM_LIST_PROPERTY, theOldValue, myVisibleItems);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#setItemFilter
     * (org.bonitasoft.console.client.ItemFilter)
     */
    @Override
    public void setItemFilter(F aFilter) {
        setItemFilter(aFilter, true);
    }
    
    @Override
    public void setItemFilter(F aFilter, boolean fireEvent) {
        myFilter = aFilter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteItems(final List<U> anItemSelection, final AsyncHandler<ItemUpdates<I>> aHandler) {
        if (myFilter != null && anItemSelection != null && !anItemSelection.isEmpty()) {
            GWT.log("RPC: deleting items");
            GWT.log("---RPC (Filter): from " + myFilter.getStartingIndex());
            GWT.log("---RPC (Filter): to   " + (myFilter.getStartingIndex() + myFilter.getMaxElementCount()));
            myRPCItemData.deleteItems(anItemSelection, myFilter, myItemsHandler, new AsyncHandler<ItemUpdates<I>>() {

                @Override
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
                    GWT.log("DefaultFilteredDataSource.deleteItems", aT);
                    if (aHandler != null) {
                        aHandler.handleFailure(aT);
                    }
                }

                @Override
                public void handleSuccess(ItemUpdates<I> aResult) {
                    if (aHandler != null) {
                        aHandler.handleSuccess(aResult);
                    }
                    myChanges.fireModelChange(ITEM_DELETED_PROPERTY, null, anItemSelection);
                };

            });
        } else {
            Window.alert("Cannot delete items whitout a valid filter and non empty selection.");
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#updateItem
     * (org.bonitasoft.console.client.BonitaUUID,
     * org.bonitasoft.console.client.Item,
     * org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
     */
    @Override
    @SuppressWarnings("unchecked")
    public void updateItem(final U anUUID, I anItem, final AsyncHandler<I> aHandler) {
        if (anUUID != null && anItem != null) {
            GWT.log("RPC: updating item");
            myRPCItemData.updateItem(anUUID, anItem, new AsyncHandler<I>() {

                /*
                 * (non-Javadoc)
                 * @seeorg.bonitasoft.console.client.common.data.AsyncHandler#
                 * handleFailure (java.lang.Throwable)
                 */
                @Override
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

                @Override
                public void handleSuccess(I result) {
                    if (result != null) {
                        if (myKnownItems == null) {
                            myKnownItems = new HashMap<U, I>();
                        }
                        updateItems(Arrays.asList(result));
                        final I theResult = myKnownItems.get(result.getUUID());
                        if (aHandler != null) {
                            aHandler.handleSuccess(theResult);
                        }
                        myChanges.fireModelChange(new ModelChangeEvent(result.getUUID(), ITEM_UPDATED_PROPERTY, null, theResult));
                    }
                };
            });
        } else {
            Window.alert("Cannot update item whitout a valid UUID or item.");
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#addItem(org
     * .bonitasoft.console.client.Item,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void addItem(final I anItem, final AsyncHandler<ItemUpdates<I>> aHandler) {
        myRPCItemData.addItem(anItem, myFilter, myItemsHandler, new AsyncHandler<ItemUpdates<I>>() {

            @Override
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
                GWT.log("DefaultFilteredDataSource.addItem", aT);
                if (aHandler != null) {
                    aHandler.handleFailure(aT);
                }
            }

            @Override
            public void handleSuccess(ItemUpdates<I> result) {
                if (aHandler != null) {
                    final ItemUpdates<I> theResult = new ItemUpdates<I>(myVisibleItems, myVisibleItems.size());
                    if (result.getNewlyCreatedItem() != null) {
                        theResult.setNewlyCreatedItem(getItem((U) result.getNewlyCreatedItem().getUUID()));
                    }
                    aHandler.handleSuccess(theResult);
                }
                myChanges.fireModelChange(ITEM_CREATED_PROPERTY, false, getItem((U) result.getNewlyCreatedItem().getUUID()));
            }
        });

    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaDataSource#addModelChangeListener
     * (java.lang.String,
     * org.bonitasoft.console.client.events.ModelChangeListener)
     */
    @Override
    public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        removeModelChangeListener(aPropertyName, aListener);
        myChanges.addModelChangeListener(aPropertyName, aListener);

    }

    /*
     * (non-Javadoc)
     * @seeorg.bonitasoft.console.client.model.BonitaDataSource#
     * removeModelChangeListener (java.lang.String,
     * org.bonitasoft.console.client.events.ModelChangeListener)
     */
    @Override
    public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        myChanges.removeModelChangeListener(aPropertyName, aListener);
    }

    class ItemsHandler implements AsyncHandler<ItemUpdates<I>> {

        @Override
        @SuppressWarnings("unchecked")
        public void handleSuccess(ItemUpdates<I> aResult) {
            GWT.log("RPC-response: received new items");
            isLoading = false;
            if (myKnownItems == null) {
                myKnownItems = new HashMap<U, I>();
            }

            ArrayList<I> theOldValue = null;
            if (myVisibleItems != null) {
                theOldValue = new ArrayList<I>();
                theOldValue.addAll(myVisibleItems);
                myVisibleItems.clear();
            } else {
                myVisibleItems = new ArrayList<I>();
            }

            // update the local data.
            updateItems(aResult.getItems());
            if (aResult.getNewlyCreatedItem() != null) {
                updateItems(Arrays.asList(aResult.getNewlyCreatedItem()));
            }

            final ArrayList<BonitaUUID> theVisibleItemsUUIDs = new ArrayList<BonitaUUID>();
            for (I theItem : aResult.getItems()) {
                myVisibleItems.add(myKnownItems.get(theItem.getUUID()));
                theVisibleItemsUUIDs.add(theItem.getUUID());
            }

            // Keep the selection consistent
            final ArrayList<U> selectedItems = new ArrayList<U>(myItemSelection.getSelectedItems());
            for (U theItemUUID : selectedItems) {
                if (!theVisibleItemsUUIDs.contains(theItemUUID)) {
                    GWT.log("Removed item from selection as it do not belong to the visible items anymore: " + theItemUUID.getValue());
                    myItemSelection.removeItemFromSelection(theItemUUID);
                }
            }

            // Store the total size.
            mySize = aResult.getNbOfItems();
            if (myMessageDataSource != null) {
                myMessageDataSource.addInfoMessage(patterns.lastTimeRefreshed(DateTimeFormat.getFormat(constants.timeShortFormat()).format(new Date())));
            }
            myChanges.fireModelChange(ITEM_LIST_PROPERTY, theOldValue, myVisibleItems);

        }

        /*
         * (non-Javadoc)
         * @see
         * org.bonitasoft.console.client.common.data.AsyncHandler#handleFailure
         * (java.lang.Throwable)
         */
        @Override
        public void handleFailure(Throwable aT) {
            isLoading = false;
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            } else if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }

            GWT.log(messages.unableToLoadCases(), aT);
        }
    }

    @SuppressWarnings("unchecked")
    protected void updateItems(List<I> anItemList) {
        I theExistingItem;
        for (I theNewItem : anItemList) {
            // Update the local cache.
            if (myKnownItems.containsKey(theNewItem.getUUID())) {
                theExistingItem = myKnownItems.get(theNewItem.getUUID());
                theExistingItem.updateItem(theNewItem);
            } else {
                // A new case is available. Add it into the local cache.
                myKnownItems.put((U) theNewItem.getUUID(), theNewItem);
            }

        }

    }

    /*
     * (non-Javadoc)
     * @see
     * org.bonitasoft.console.client.model.BonitaFilteredDataSource#getItem(org
     * .bonitasoft.console.client.BonitaUUID)
     */
    @Override
    public I getItem(U anUUID) {
        if (myKnownItems != null) {
            return myKnownItems.get(anUUID);
        } else {
            return null;
        }
    }

}
