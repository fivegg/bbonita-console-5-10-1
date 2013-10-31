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
package org.bonitasoft.console.client.view.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.events.AddItemHandler;
import org.bonitasoft.console.client.events.HasAddHandler;
import org.bonitasoft.console.client.events.HasRemoveHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.RemoveItemsHandler;
import org.bonitasoft.console.client.identity.Group;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.I18NComposite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class GroupsListEditorView extends I18NComposite implements ModelChangeListener, HasAddHandler<Group>, HasRemoveHandler<Group> {

    protected static final int MIN_ROW_COUNT = 10;
    protected static final String ITEM_LIST_EMPTY_ROW_STYLE = "item_list_empty_row";
    protected Grid myItemList;
    protected final Set<String> myExistingItems;
    protected final List<Group> myItems;
    protected final Map<Integer, Group> myRowItem = new HashMap<Integer, Group>();
    protected final Set<Integer> myItemRowSelection;

    protected final GroupFinderPanel myGroupFinder;
    protected CustomDialogBox myGroupSearchPopup;
    protected Group myGroupToAdd;

    protected final FlexTable myOuterPanel;
    protected final GroupDataSource myGroupDataSource;
    protected ArrayList<AddItemHandler<Group>> myAddHandlers;
    protected ArrayList<RemoveItemsHandler<Group>> myRemoveHandlers;
    protected Label myErrorMessage;

    /**
     * Default constructor.
     */
    public GroupsListEditorView(final GroupDataSource aGroupDataSource) {
        myGroupDataSource = aGroupDataSource;

        myItems = new ArrayList<Group>();
        myExistingItems = new HashSet<String>();
        myGroupFinder = new GroupFinderPanel(false);

        myOuterPanel = new FlexTable();

        myItemRowSelection = new HashSet<Integer>();

        buildContent();

        myOuterPanel.setStylePrimaryName("bos_groups_list_editor");
        initWidget(myOuterPanel);
    }

    protected void buildContent() {

        myItemList = new Grid(1, 4);
        myItemList.setWidth("100%");
        myItemList.setStylePrimaryName("item_list");
        myItemList.setWidget(0, 0, buildSelectAllSelector());
        myItemList.setHTML(0, 1, constants.groupPath());
        myItemList.setHTML(0, 2, constants.groupLabelLabel());
        myItemList.setHTML(0, 3, constants.groupDescription());
        myItemList.getColumnFormatter().setStyleName(0, "item_selector");
        myItemList.getRowFormatter().setStylePrimaryName(0, "item_list_content_row_title");

        final CustomMenuBar theActionButtons = new CustomMenuBar();
        theActionButtons.addItem(constants.add(), new Command() {

            public void execute() {
                if (myGroupSearchPopup == null) {
                    myGroupSearchPopup = buildGroupSearchPopup();
                }
                myGroupSearchPopup.center();
            }
        });

        theActionButtons.addItem(constants.delete(), new Command() {

            public void execute() {
                removeSelectedItems();
            }
        });

        myOuterPanel.setWidget(0, 0, myItemList);
        myOuterPanel.setWidget(1, 0, theActionButtons);

        myErrorMessage = new Label();
        myErrorMessage.setStyleName(CSSClassManager.VALIDATION_ERROR_MESSAGE);
        myOuterPanel.setWidget(3, 0, myErrorMessage);
    }

    protected CustomDialogBox buildGroupSearchPopup() {
        final CustomDialogBox theResult = new CustomDialogBox(false, true);
        myGroupFinder.addModelChangeListener(GroupFinderPanel.ITEM_LIST_PROPERTY, this);
        myGroupFinder.addModelChangeListener(GroupFinderPanel.CANCEL_PROPERTY, this);
        theResult.add(myGroupFinder);
        theResult.setText(constants.searchForAGroup());
        return theResult;
    }

    protected void addItemToList() {
        if (myGroupToAdd != null) {
            final String theGroupId = myGroupToAdd.getUUID().getValue();
            boolean alreadyInList = (myExistingItems.contains(theGroupId));

            if (!alreadyInList) {
                if (myAddHandlers != null) {
                    for (AddItemHandler<Group> theHandler : myAddHandlers) {
                        theHandler.addItemRequested(myGroupToAdd);
                    }
                }
            }
        }
    }

    private Widget buildSelectAllSelector() {
        final FlowPanel theWrapper = new FlowPanel();
        final CheckBox theSelectAllCheckBox = new CheckBox();
        theWrapper.add(theSelectAllCheckBox);
        theSelectAllCheckBox.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent aEvent) {
                if (aEvent.getSource() instanceof CheckBox) {
                    CheckBox theCheckBox = (CheckBox) aEvent.getSource();
                    selectAllRows(theCheckBox.getValue());
                    aEvent.stopPropagation();
                }
            }
        });

        final CustomMenuBar theSelector = new CustomMenuBar();
        theSelector.addItem(theWrapper, new Command() {
            public void execute() {
                boolean theNewValue = !theSelectAllCheckBox.getValue();
                theSelectAllCheckBox.setValue(theNewValue, true);
                selectAllRows(theNewValue);
            }
        });
        return theSelector;
    }

    protected void selectAllRows(Boolean aValue) {
        for (int i = 1; i < myItemList.getRowCount(); i++) {
            Widget theWidget = myItemList.getWidget(i, 0);
            if (theWidget instanceof CheckBox) {
                ((CheckBox) theWidget).setValue(aValue, true);
            }
        }
    }

    protected void removeSelectedItems() {
        final Collection<Group> theItemsToRemove = new HashSet<Group>();
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
        for (RemoveItemsHandler<Group> theHandler : myRemoveHandlers) {
            theHandler.removeItemsRequested(theItemsToRemove);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
     * .bonitasoft.console.client.events.ModelChangeEvent)
     */
    public void modelChange(ModelChangeEvent anEvt) {
        if (GroupFinderPanel.ITEM_LIST_PROPERTY.equals(anEvt.getPropertyName())) {
            final List<Group> theGroups = myGroupFinder.getGroups();
            if (theGroups != null && !theGroups.isEmpty()) {
                myGroupToAdd = theGroups.get(0);
                myGroupSearchPopup.hide();
                addItemToList();
            }
        } else if (GroupFinderPanel.CANCEL_PROPERTY.equals(anEvt.getPropertyName())) {
            myGroupSearchPopup.hide();
        }
    }

    public void addAddHandler(AddItemHandler<Group> aHandler) {
        if (aHandler != null) {
            if (myAddHandlers == null) {
                myAddHandlers = new ArrayList<AddItemHandler<Group>>();
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
    public void addRemoveHandler(RemoveItemsHandler<Group> aHandler) {
        if (aHandler != null) {
            if (myRemoveHandlers == null) {
                myRemoveHandlers = new ArrayList<RemoveItemsHandler<Group>>();
            }
            if (!myRemoveHandlers.contains(aHandler)) {
                myRemoveHandlers.add(aHandler);
            }
        }

    }

    /**
     * @param aResult
     */
    public void setItems(List<Group> anItemList) {
        myItems.clear();
        myExistingItems.clear();
        myGroupFinder.clear();
        if (anItemList != null) {
            myItems.addAll(anItemList);
            String theGroupId;
            for (Group theItem : anItemList) {
                theGroupId = theItem.getUUID().getValue();
                myExistingItems.add(theGroupId);
            }
        }
        int theNbOfRow = 1;
        if (myItems != null) {
            theNbOfRow += myItems.size();
        }
        if (theNbOfRow < MIN_ROW_COUNT) {
            theNbOfRow = MIN_ROW_COUNT;
        }
        myItemList.resize(theNbOfRow, 4);
        int theCurrentRow = 1;
        if (myItems != null && !myItems.isEmpty()) {
            for (Group theItem : myItems) {
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

    protected void fillInEmptyRow(int aRow) {
        myItemList.clearCell(aRow, 0);
        myItemList.clearCell(aRow, 1);
        myItemList.clearCell(aRow, 2);
        myItemList.clearCell(aRow, 3);
        myItemList.getRowFormatter().setStylePrimaryName(aRow, ITEM_LIST_EMPTY_ROW_STYLE);
    }

    protected void fillInContentRow(int aRow, Group anItem) {
        myItemList.setWidget(aRow, 0, buildItemSelector(aRow));
        myItemList.setWidget(aRow, 1, new Label(Group.buildGroupPath(anItem)));
        myItemList.setWidget(aRow, 2, new Label(anItem.getLabel()));
        myItemList.setWidget(aRow, 3, new Label(anItem.getDescription()));
        myItemList.getRowFormatter().setStylePrimaryName(aRow, "item_list_content_row");
    }

    private Widget buildItemSelector(final int row) {
        final CheckBox theSelectItemCheckBox = new CheckBox();
        theSelectItemCheckBox.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent aEvent) {
                if (aEvent.getSource() instanceof CheckBox) {
                    CheckBox theCheckBox = (CheckBox) aEvent.getSource();
                    if (theCheckBox.getValue()) {
                        myItemRowSelection.add(row);
                    } else {
                        myItemRowSelection.remove(row);
                    }
                }
            }
        });
        return theSelectItemCheckBox;
    }
}
