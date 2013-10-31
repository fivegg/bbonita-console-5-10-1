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
package org.bonitasoft.console.client.view.steps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.events.ActionItemsHandler;
import org.bonitasoft.console.client.events.AddItemHandler;
import org.bonitasoft.console.client.events.HasActionHandler;
import org.bonitasoft.console.client.events.HasAddHandler;
import org.bonitasoft.console.client.events.HasRemoveHandler;
import org.bonitasoft.console.client.events.RemoveItemsHandler;
import org.bonitasoft.console.client.model.steps.EventDataSource;
import org.bonitasoft.console.client.steps.EventItem;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.I18NComposite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class EventsListEditorView extends I18NComposite implements HasAddHandler<EventItem>, HasRemoveHandler<EventItem>, HasActionHandler<EventItem> {

    public static final String EXECUTE_EVENT = "execute event";
    public static final String UPDATE_EVENT = "update event";

    protected static final int MIN_ROW_COUNT = 10;
    protected static final String ITEM_LIST_EMPTY_ROW_STYLE = "item_list_empty_row";

    

    protected Grid myItemList;
    protected final Set<String> myExistingItems;
    protected final List<EventItem> myItems;
    protected final Map<Integer, EventItem> myRowItem = new HashMap<Integer, EventItem>();
    protected final Set<Integer> myItemRowSelection;

    protected final FlexTable myOuterPanel;
    protected final EventDataSource myEventItemDataSource;
    private ArrayList<AddItemHandler<EventItem>> myAddHandlers;
    private ArrayList<RemoveItemsHandler<EventItem>> myRemoveHandlers;
    private ArrayList<ActionItemsHandler<EventItem>> myActionHandlers;
    private Label myErrorMessage;

    private DateBox myDateBox;

    /**
     * Default constructor.
     */
    public EventsListEditorView(final EventDataSource aEventItemDataSource) {
        myEventItemDataSource = aEventItemDataSource;

        myItems = new ArrayList<EventItem>();
        myExistingItems = new HashSet<String>();

        myOuterPanel = new FlexTable();

        myItemRowSelection = new HashSet<Integer>();

        buildContent();

        myOuterPanel.setStylePrimaryName("bos_event_list_editor");
        initWidget(myOuterPanel);
    }

    protected void buildContent() {

        myItemList = new Grid(1, 6);
        myItemList.setWidth("100%");
        myItemList.setStylePrimaryName("item_list");
        // myItemList.setWidget(0, 0, buildSelectAllSelector());
        myItemList.setHTML(0, 1, constants.eventTypeColumnTitle());
        myItemList.setHTML(0, 2, constants.eventPositionColumnTitle());
        myItemList.setHTML(0, 3, constants.eventDateColumnTitle());
        myItemList.setHTML(0, 4, constants.eventStepColumnTitle());
        myItemList.setHTML(0, 5, constants.eventProcessColumnTitle());
        myItemList.getColumnFormatter().setStyleName(0, "item_selector");
        myItemList.getRowFormatter().setStylePrimaryName(0, "item_list_content_row_title");

        final FlowPanel theActionsWrapper = new FlowPanel();
        final CustomMenuBar theRemoveButton = new CustomMenuBar();
        theRemoveButton.addItem(constants.delete(), new Command() {

            public void execute() {
                removeSelectedItems();
            }
        });
        final CustomMenuBar theExecuteButton = new CustomMenuBar();
        theExecuteButton.addItem(constants.executeNow(), new Command() {
            public void execute() {
                executeSelectedItems();
            }
        });

        final FlowPanel theUpdateDateAndTimeWrapper = new FlowPanel();
        theUpdateDateAndTimeWrapper.setStylePrimaryName("bos_update_action_wrapper");
        myDateBox = new DateBox();
        final CustomMenuBar theUpdateButton = new CustomMenuBar();
        theUpdateButton.addItem(constants.update(), new Command() {
            public void execute() {
                updateSelectedItems();
            }
        });
        theUpdateDateAndTimeWrapper.add(myDateBox);
        theUpdateDateAndTimeWrapper.add(theUpdateButton);

        theActionsWrapper.add(theRemoveButton);
        theActionsWrapper.add(theExecuteButton);
        theActionsWrapper.add(theUpdateDateAndTimeWrapper);

        myOuterPanel.setWidget(0, 0, myItemList);
        myOuterPanel.setWidget(1, 0, theActionsWrapper);

        myErrorMessage = new Label();
        myErrorMessage.setStyleName(CSSClassManager.VALIDATION_ERROR_MESSAGE);
        myOuterPanel.setWidget(3, 0, myErrorMessage);
    }

    protected void updateSelectedItems() {
        if(myDateBox.getValue() != null) {
            myErrorMessage.setText("");
            if (myActionHandlers != null && !myActionHandlers.isEmpty()) {
                final Collection<EventItem> theItemsToUpdate = new HashSet<EventItem>();
                int theRowIndex = myItemList.getRowCount() - 1;
                EventItem theEventToUpdate;
                for (int i = theRowIndex; i >= 1; i--) {
                    Widget theWidget = myItemList.getWidget(i, 0);
                    if (theWidget instanceof CheckBox) {
                        if (((CheckBox) theWidget).getValue()) {
                            if (myRowItem.get(i) != null) {
                                theEventToUpdate = new EventItem(myRowItem.get(i).getUUID());
                                theEventToUpdate.updateItem(myRowItem.get(i));
                                theEventToUpdate.setExecutionDate(myDateBox.getValue());
                                theItemsToUpdate.add(theEventToUpdate);
                            }
                        }
                    }
                }
                if (theItemsToUpdate.size() > 1) {
                    myErrorMessage.setText(messages.tooManyEventsSelected());
                } else {
                    for (ActionItemsHandler<EventItem> theHandler : myActionHandlers) {
                        theHandler.executeAction(UPDATE_EVENT, theItemsToUpdate);
                    }
                }
            }
        }
        
    }

    protected void executeSelectedItems() {
        myErrorMessage.setText("");
        if (myActionHandlers != null && !myActionHandlers.isEmpty()) {
            final Collection<EventItem> theItemsToExecute = new HashSet<EventItem>();
            int theRowIndex = myItemList.getRowCount() - 1;
            for (int i = theRowIndex; i >= 1; i--) {
                Widget theWidget = myItemList.getWidget(i, 0);
                if (theWidget instanceof CheckBox) {
                    if (((CheckBox) theWidget).getValue()) {
                        if (myRowItem.get(i) != null) {
                            theItemsToExecute.add(myRowItem.get(i));
                        }
                    }
                }
            }
            if (theItemsToExecute.size() > 1) {
                myErrorMessage.setText(messages.tooManyEventsSelected());
            } else {
                for (ActionItemsHandler<EventItem> theHandler : myActionHandlers) {
                    theHandler.executeAction(EXECUTE_EVENT, theItemsToExecute);
                }
            }
        }
    }

    // private Widget buildSelectAllSelector() {
    // final FlowPanel theWrapper = new FlowPanel();
    // final CheckBox theSelectAllCheckBox = new CheckBox();
    // theWrapper.add(theSelectAllCheckBox);
    // theSelectAllCheckBox.addClickHandler(new ClickHandler() {
    //
    // public void onClick(ClickEvent aEvent) {
    // if (aEvent.getSource() instanceof CheckBox) {
    // CheckBox theCheckBox = (CheckBox) aEvent.getSource();
    // selectAllRows(theCheckBox.getValue());
    // aEvent.stopPropagation();
    // }
    // }
    // });
    // final CustomMenuBar theSelector = new CustomMenuBar();
    // theSelector.addItem(theWrapper, new Command() {
    // public void execute() {
    // boolean theNewValue = !theSelectAllCheckBox.getValue();
    // theSelectAllCheckBox.setValue(theNewValue, true);
    // selectAllRows(theNewValue);
    // }
    // });
    // return theSelector;
    // }

    protected void selectAllRows(Boolean aValue) {
        for (int i = 1; i < myItemList.getRowCount(); i++) {
            Widget theWidget = myItemList.getWidget(i, 0);
            if (theWidget instanceof CheckBox) {
                ((CheckBox) theWidget).setValue(aValue, true);
            }
        }
    }

    protected void removeSelectedItems() {
        myErrorMessage.setText("");
        if (myRemoveHandlers != null && !myRemoveHandlers.isEmpty()) {
            final Collection<EventItem> theItemsToRemove = new HashSet<EventItem>();
            int theRowIndex = myItemList.getRowCount() - 1;
            for (int i = theRowIndex; i >= 1; i--) {
                Widget theWidget = myItemList.getWidget(i, 0);
                if (theWidget instanceof CheckBox) {
                    if (((CheckBox) theWidget).getValue()) {
                        if (myRowItem.get(i) != null) {
                            theItemsToRemove.add(myRowItem.get(i));
                        }
                    }
                }
            }
            for (RemoveItemsHandler<EventItem> theHandler : myRemoveHandlers) {
                theHandler.removeItemsRequested(theItemsToRemove);
            }
        }
    }

    public void addAddHandler(AddItemHandler<EventItem> aHandler) {
        if (aHandler != null) {
            if (myAddHandlers == null) {
                myAddHandlers = new ArrayList<AddItemHandler<EventItem>>();
            }
            if (!myAddHandlers.contains(aHandler)) {
                myAddHandlers.add(aHandler);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.events.HasRemoveHandler#addRemoveHandler(
     * org.bonitasoft.console.client.events.RemoveItemsHandler)
     */
    public void addRemoveHandler(RemoveItemsHandler<EventItem> aHandler) {
        if (aHandler != null) {
            if (myRemoveHandlers == null) {
                myRemoveHandlers = new ArrayList<RemoveItemsHandler<EventItem>>();
            }
            if (!myRemoveHandlers.contains(aHandler)) {
                myRemoveHandlers.add(aHandler);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.events.HasActionHandler#addActionHandler
     * (org.bonitasoft.console.client.events.ActionItemsHandler)
     */
    public void addActionHandler(ActionItemsHandler<EventItem> aHandler) {
        if (aHandler != null) {
            if (myActionHandlers == null) {
                myActionHandlers = new ArrayList<ActionItemsHandler<EventItem>>();
            }
            if (!myActionHandlers.contains(aHandler)) {
                myActionHandlers.add(aHandler);
            }
        }

    }

    /**
     * @param aResult
     */
    public void setItems(List<EventItem> anItemList) {
        myItems.clear();
        myExistingItems.clear();
        myDateBox.setValue(null);
        if (anItemList != null) {
            myItems.addAll(anItemList);
            String theEventItemId;
            for (EventItem theItem : anItemList) {
                theEventItemId = theItem.getUUID().getValue();
                myExistingItems.add(theEventItemId);
            }
        }
        int theNbOfRow = 1;
        if (myItems != null) {
            theNbOfRow += myItems.size();
        }
        if (theNbOfRow < MIN_ROW_COUNT) {
            theNbOfRow = MIN_ROW_COUNT;
        }
        myItemList.resize(theNbOfRow, 6);
        int theCurrentRow = 1;
        if (myItems != null && !myItems.isEmpty()) {
            for (EventItem theItem : myItems) {
                fillInContentRow(theCurrentRow, theItem);

                // keep mapping between row and membership
                myRowItem.put(theCurrentRow, theItem);
                theCurrentRow++;
            }
        }
        for (; theCurrentRow < MIN_ROW_COUNT; theCurrentRow++) {
            fillInEmptyRow(theCurrentRow);
        }
    }

    protected void fillInContentRow(int aRow, EventItem anItem) {
        myItemList.setWidget(aRow, 0, buildItemSelector(aRow));
        myItemList.setWidget(aRow, 1, new Label(anItem.getType().name()));
        myItemList.setWidget(aRow, 2, new Label(anItem.getPosition().name()));
        myItemList.setWidget(aRow, 3, new Label(DateTimeFormat.getFormat(constants.dateShortFormat()).format(anItem.getExecutionDate())));
        myItemList.setWidget(aRow, 4, new Label(anItem.getStepName()));
        myItemList.setWidget(aRow, 5, new Label(anItem.getProcessName()));
        myItemList.getRowFormatter().setStylePrimaryName(aRow, "item_list_content_row");
    }

    protected void fillInEmptyRow(int aRow) {
        for (int col = 0; col < myItemList.getColumnCount(); col++) {
            myItemList.clearCell(aRow, col);
        }
        myItemList.getRowFormatter().setStylePrimaryName(aRow, ITEM_LIST_EMPTY_ROW_STYLE);
    }

    private Widget buildItemSelector(final int row) {
        final CheckBox theSelectItemCheckBox = new CheckBox();
        theSelectItemCheckBox.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent aEvent) {
                if (aEvent.getSource() instanceof CheckBox) {
                    CheckBox theCheckBox = (CheckBox) aEvent.getSource();
                    if (theCheckBox.getValue()) {
                        myItemRowSelection.clear();
                        myItemRowSelection.add(row);
                        final EventItem theEvent = myRowItem.get(row);
                        if (theEvent != null) {
                            myDateBox.setValue(theEvent.getExecutionDate());
                        }
                    } else {
                        myItemRowSelection.remove(row);
                        myDateBox.setValue(null);
                    }
                }
            }
        });
        return theSelectItemCheckBox;
    }
}
