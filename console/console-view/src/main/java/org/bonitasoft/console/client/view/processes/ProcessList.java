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
package org.bonitasoft.console.client.view.processes;

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ProcessFilter;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.processes.ProcessSelection;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.ItemFilterEditor;
import org.bonitasoft.console.client.view.ItemSelectionWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * This widget display a tabular view of all the steps a user should see. The
 * list depends on the selection of a label.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessList extends AbstractItemList<BonitaProcessUUID, BonitaProcess, ProcessFilter> {

    protected static final String PROCESS_LIST_STATE_COL_STYLE = "process_list_state";
    protected static final String PROCESS_LIST_DESCRIPTION_COL_STYLE = "process_list_description";
    protected static final String PROCESS_LIST_COLUMN_COL_STYLE = "process_list_column";

    /**
     * Override the menu bars attributes definition.
     */
    protected AdminProcessMenuBarWidget myTopMenuBar;
    protected AdminProcessMenuBarWidget myBottomMenuBar;

    protected final HashMap<BonitaProcessUUID, ItemSelectionWidget<BonitaProcessUUID>> myItemSelectionWidgets = new HashMap<BonitaProcessUUID, ItemSelectionWidget<BonitaProcessUUID>>();
    protected final HashMap<BonitaProcessUUID, Image> myItemStateWidgets = new HashMap<BonitaProcessUUID, Image>();

    protected int mySelectColumnIndex = 0;
    protected int myNameColumnIndex = 2;
    protected int myDescriptionColumnIndex = 7;
    protected int myVersionColumnIndex = 3;
    protected int myStateColumnIndex = 1;
    protected int myStartACaseColumnIndex = 4;
    protected int myInstallationDateColumnIndex = 6;
    protected int myInstallerNameColumnIndex = 5;

    /**
     * Default constructor.
     * 
     * @param aProcessSelection
     * @param aProcessDataSource
     */
    public ProcessList(final MessageDataSource aMessageDataSource, ProcessSelection aProcessSelection, ProcessDataSource aProcessDataSource) {
        super(aMessageDataSource, aProcessSelection, aProcessDataSource, 20, 20, 8);
        // Listen changes on the list of items to display.
        myBonitaDataSource.addModelChangeListener(ProcessDataSource.ITEM_LIST_PROPERTY, this);
        myBonitaDataSource.addModelChangeListener(ProcessDataSource.ITEM_CREATED_PROPERTY, this);
        myBonitaDataSource.addModelChangeListener(ProcessDataSource.ITEM_DELETED_PROPERTY, this);

        newerButtonTop.setHTML(constants.previousPageLinkLabel());
        olderButtonTop.setHTML(constants.nextPageLinkLabel());
        newerButtonBottom.setHTML(constants.previousPageLinkLabel());
        olderButtonBottom.setHTML(constants.nextPageLinkLabel());

        initView();
        createWidgetsForItemsAndDisplay();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bonitasoft.console.client.view.AbstractItemList#initView()
     */
    @Override
    protected void initView() {
        super.initView();
        myInnerTable.getColumnFormatter().setStyleName(mySelectColumnIndex, ITEM_LIST_SELECT_COLUMN_STYLE);
        myInnerTable.getColumnFormatter().setStyleName(myDescriptionColumnIndex, PROCESS_LIST_DESCRIPTION_COL_STYLE);
        myInnerTable.getColumnFormatter().setStyleName(myStateColumnIndex, PROCESS_LIST_STATE_COL_STYLE);
    }

    /**
     * Filter items then update the UI.
     */
    protected void createWidgetsForItemsAndDisplay() {
        myVisibleItems = ((ProcessDataSource) myBonitaDataSource).getVisibleItems();
        if (myVisibleItems != null) {
            hideLoading();
            myInnerTable.removeStyleName(LOADING_STYLE);
            for (BonitaProcessUUID theItem : myVisibleItems) {
                if (!myItemTableRow.containsKey(theItem)) {
                    createWidgetsForItem(theItem);
                }
            }
            // Update the UI.
            update(myVisibleItems);
        } else {
            displayLoading();
            myInnerTable.addStyleName(LOADING_STYLE);
        }
    }

    /**
     * Widgets are created once for each item to display. It avoids to create
     * many widgets each time the view is refreshed.
     * 
     * @param anItem
     */
    private void createWidgetsForItem(final BonitaProcessUUID anItem) {
        myItemSelectionWidgets.put(anItem, new ItemSelectionWidget<BonitaProcessUUID>(myItemSelection, anItem));
        myBonitaDataSource.getItem(anItem).addModelChangeListener(BonitaProcess.STATE_PROPERTY, this);
        myItemStateWidgets.put(anItem, new Image(PICTURE_PLACE_HOLDER));
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.beans.ModelChangeListener#propertyChange(java.beans.
     * PropertyChangeEvent)
     */
    public void modelChange(ModelChangeEvent anEvent) {
        if (ProcessDataSource.ITEM_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
            createWidgetsForItemsAndDisplay();
        } else if (ProcessDataSource.ITEM_CREATED_PROPERTY.equals(anEvent.getPropertyName())) {
            createWidgetsForItemsAndDisplay();
        } else if (ProcessDataSource.ITEM_DELETED_PROPERTY.equals(anEvent.getPropertyName())) {
            createWidgetsForItemsAndDisplay();
        } else if (ProcessFilterEditor.FILTER_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
            final ProcessFilter theDataSourceFilter = myBonitaDataSource.getItemFilter();
            final ProcessFilter theFilterToApply = (ProcessFilter) anEvent.getNewValue();
            theDataSourceFilter.updateFilter(theFilterToApply);
            myBonitaDataSource.reload();
        } else if (BonitaProcess.STATE_PROPERTY.equals(anEvent.getPropertyName())) {
            updateItemStateStyle((BonitaProcess) anEvent.getSource());
        } else {
            // The event may come from a subscribtion made by my super class.
            super.modelChange(anEvent);
        }
    }

    @Override
    public String getLocationLabel() {
        return constants.adminProcessList();
    }

    @Override
    protected FlowPanel buildTopNavBar() {
        // Create the 'navigation' bar at the upper-right.
        FlowPanel theResult = new FlowPanel();
        myTopMenuBar = new AdminProcessMenuBarWidget(myMessageDataSource, (ProcessDataSource) myBonitaDataSource, (ProcessSelection) myItemSelection);
        theResult.add(myTopMenuBar);

        return theResult;
    }

    @Override
    protected FlowPanel buildBottomNavBar() {
        // Create the 'navigation' bar at the bottom-right.
        FlowPanel theResult = new FlowPanel();
        myBottomMenuBar = new AdminProcessMenuBarWidget(myMessageDataSource, (ProcessDataSource) myBonitaDataSource, (ProcessSelection) myItemSelection);
        theResult.add(myBottomMenuBar);

        return theResult;
    }

    public void notifyItemClicked(BonitaProcessUUID anItem, final ClickEvent anEvent) {
        final Cell theCell = myInnerTable.getCellForEvent(anEvent);
        final int theCellIndex = theCell.getCellIndex();
        if ((theCell != null) && (theCellIndex > mySelectColumnIndex) && (anItem != null)) {
            History.newItem(ViewToken.ProcessEditor + "/" + anItem);
        }
    }

    @Override
    protected void update(List<BonitaProcessUUID> anItemList) {

        updateListSize(anItemList);

        if (myTopNavBar != null && !myTopNavBar.isAttached()) {
            // Create the navigation row (Top).
            myInnerTable.setWidget(0, 0, myTopNavBar);
            myInnerTable.getFlexCellFormatter().setColSpan(0, 0, myColumnNumber);
        }

     // Add the column titles
        final Label myNameColumnLabel = new Label(constants.processColumnTitle());
        myNameColumnLabel.setStylePrimaryName(PROCESS_LIST_COLUMN_COL_STYLE);
        
        final Label myInstallerNameColumnLabel = new Label(constants.processInstallerNameColumnTitle());
        myInstallerNameColumnLabel.setStylePrimaryName(PROCESS_LIST_COLUMN_COL_STYLE);
        
        final Label myInstallationDateColumnLabel = new Label(constants.processInstallationNameColumnTitle());
        myInstallationDateColumnLabel.setStylePrimaryName(PROCESS_LIST_COLUMN_COL_STYLE);
        
        myInnerTable.setWidget(1, myNameColumnIndex, myNameColumnLabel);
        myInnerTable.setWidget(1, myInstallerNameColumnIndex, myInstallerNameColumnLabel);
        myInnerTable.setWidget(1, myInstallationDateColumnIndex, myInstallationDateColumnLabel);
        myInnerTable.setWidget(1, myDescriptionColumnIndex, new Label(constants.description()));

        // Set CSS style.
        myInnerTable.getRowFormatter().setStylePrimaryName(1, ITEM_LIST_CONTENT_ROW_TITLE_STYLE);
        
        fillContentRows(anItemList);

        if (myBottomNavBar != null && !myBottomNavBar.isAttached()) {
            // Create the navigation row (Bottom).
            int theBottomNavBarPosition = myInnerTable.getRowCount();
            myInnerTable.setWidget(theBottomNavBarPosition, 0, myBottomNavBar);
            myInnerTable.getFlexCellFormatter().setColSpan(theBottomNavBarPosition, 0, myColumnNumber);
        }

    }

    private void fillContentRows(List<BonitaProcessUUID> anItemList) {

        final int theRowOffset = 2;
        int nbItemDisplayed = 0;
        for (; nbItemDisplayed < anItemList.size(); nbItemDisplayed++) {

            final int theCurrentRowIndex = theRowOffset + nbItemDisplayed;
            BonitaProcessUUID theUUID = anItemList.get(nbItemDisplayed);
            BonitaProcess theItem = ((ProcessDataSource) myBonitaDataSource).getItem(theUUID);

            final String theProcessDescription = theItem.getProcessDescription();
            final String theShortDesc;
            if (theProcessDescription.length() > 70) {
                theShortDesc = theProcessDescription.substring(0, 67) + "...";
            } else {
                theShortDesc = theProcessDescription;
            }
            final Label theProcessDescLabel = new Label(theShortDesc);
            theProcessDescLabel.setTitle(theProcessDescription);

            // Add a new row to the table, then set each of its columns.
            // layout widgets
            myInnerTable.setWidget(theCurrentRowIndex, mySelectColumnIndex, myItemSelectionWidgets.get(theItem.getUUID()));
            myInnerTable.getFlexCellFormatter().setColSpan(theCurrentRowIndex, mySelectColumnIndex, 1);
            myInnerTable.setWidget(theCurrentRowIndex, myNameColumnIndex, new Label(theItem.getDisplayName()));
            myInnerTable.setWidget(theCurrentRowIndex, myDescriptionColumnIndex, theProcessDescLabel);
            myInnerTable.setWidget(theCurrentRowIndex, myVersionColumnIndex, new Label(theItem.getVersion()));
            myInnerTable.setWidget(theCurrentRowIndex, myStateColumnIndex, myItemStateWidgets.get(theItem.getUUID()));
            myInnerTable.setWidget(theCurrentRowIndex, myInstallerNameColumnIndex,new Label(theItem.getDeployedBy()));
            myInnerTable.setWidget(theCurrentRowIndex, myInstallationDateColumnIndex, new Label(LocaleUtil.shortDateFormat(theItem.getDeployedDate())));

            updateItemStateStyle(theItem);

            // Set CSS style.
            if (theCurrentRowIndex % 2 == 0) {
                myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);
                myInnerTable.getRowFormatter().addStyleName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE + EVEN_STYLE_SUFFIX);
            } else {
                myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);
            }

            // Keep link between the process and the row.
            linkItemWithRow(theItem.getUUID(), theCurrentRowIndex);

        }

        fillWithEmptyRows(theRowOffset, theRowOffset + nbItemDisplayed, myColumnNumber);

    }

    /**
     * Update the CSS style of the icon representing the process state.
     * 
     * @param aProcess
     */
    protected void updateItemStateStyle(BonitaProcess aProcess) {
        final Image theProcessState = myItemStateWidgets.get(aProcess.getUUID());
        if (theProcessState != null) {
            final String theTitle = patterns.processStateIconTitle(LocaleUtil.translate(aProcess.getState()));
            theProcessState.setTitle(theTitle);
            final String theCSSStyle = CSSClassManager.getProcessIconStyle(aProcess.getState());
            theProcessState.setStyleName(theCSSStyle);
        }
    }

    @Override
    protected ItemFilterEditor<ProcessFilter> buildFilterEditor() {
        final ProcessFilterEditor theEditor = new ProcessFilterEditor(myMessageDataSource, myBonitaDataSource.getItemFilter());
        theEditor.addModelChangeListener(ProcessFilterEditor.FILTER_UPDATED_PROPERTY, this);
        return theEditor;
    }
}
