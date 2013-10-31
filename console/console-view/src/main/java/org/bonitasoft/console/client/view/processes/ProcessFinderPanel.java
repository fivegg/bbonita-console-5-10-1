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
package org.bonitasoft.console.client.view.processes;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ProcessFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.model.BonitaDataSource;
import org.bonitasoft.console.client.model.ItemSelection;
import org.bonitasoft.console.client.model.SimpleSelection;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.processes.ProcessSelection;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.Focusable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessFinderPanel extends Composite implements Focusable, BonitaDataSource<BonitaProcessUUID> {

    public static final String CANCEL_PROPERTY = "process finder cancel";
    protected final ProcessDataSource myItemFinderDataSource;

    protected FlexTable myOuterPanel = new FlexTable();
    protected final HTML myErrorMessageLabel = new HTML();
    protected final ProcessFilter myItemFilter = new ProcessFilter(0, 20);
    protected AsyncHandler<ItemUpdates<BonitaProcess>> myItemHandler;
    protected final SimpleSelection<BonitaProcessUUID> myItemFinderSelection = new SimpleSelection<BonitaProcessUUID>();
    protected final boolean mySelectionIsMultiple;

    protected final transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

    public ProcessFinderPanel(final ProcessDataSource aDataSource, boolean isMultiple) {
        super();
        myItemFinderDataSource = aDataSource;
        mySelectionIsMultiple = isMultiple;
        myOuterPanel = buildContent();

        myOuterPanel.setStylePrimaryName("bos_item_finder_panel");
        initWidget(myOuterPanel);
    }

    private FlexTable buildContent() {
        FlexTable thePanel = new FlexTable();

        thePanel.setWidget(0, 0, myErrorMessageLabel);
        thePanel.getFlexCellFormatter().setStyleName(0, 0, CSSClassManager.VALIDATION_ERROR_MESSAGE);
        thePanel.getFlexCellFormatter().setColSpan(0, 0, 3);

        final ProcessList myItemList = new ProcessList(null, (ProcessSelection) myItemFinderDataSource.getItemSelection(), myItemFinderDataSource) {

            @Override
            protected void initView() {
                super.initView();
                if (myBottomNavBar.getWidgetCount() == 2) {
                    myBottomNavBar.remove(1);
                }
            }

            @Override
            public void notifyItemClicked(BonitaProcessUUID anItem, ClickEvent anEvent) {
                // Modify item selection.
                if (anItem != null) {
                    final Cell theCell = myInnerTable.getCellForEvent(anEvent);
                    if (theCell != null) {
                        final int theCellIndex = theCell.getCellIndex();
                        if (theCellIndex != mySelectColumnIndex) {
                            if (mySelectionIsMultiple) {
                                if (myItemSelection.getSelectedItems().contains(anItem)) {
                                    myItemSelection.removeItemFromSelection(anItem);
                                } else {
                                    myItemSelection.addItemToSelection(anItem);
                                }
                            } else {
                                if (!myItemSelection.getSelectedItems().contains(anItem)) {
                                    myItemSelection.clearSelection();
                                    myItemSelection.addItemToSelection(anItem);
                                } else {
                                    myItemSelection.clearSelection();
                                }
                            }
                        } else {
                            // The check box has already inserted the item into the selection.
                            if (mySelectionIsMultiple) {
                                // Nothing to do here.
                            } else {
                                if (myItemSelection.getSize() > 1) {
                                    // I have to solve the inconsistency here.
                                    myItemSelection.clearSelection();
                                    myItemSelection.addItemToSelection(anItem);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            protected FlowPanel buildTopNavBar() {
                final FlowPanel theResult = new FlowPanel();
                final Label theRefreshLink = new Label(constants.refresh());
                theRefreshLink.setStyleName(CSSClassManager.LINK_LABEL);
                theRefreshLink.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent aEvent) {
                        myBonitaDataSource.reload();
                    }
                });
                theResult.add(theRefreshLink);
                return theResult;
            }

            @Override
            protected FlowPanel buildBottomNavBar() {
                final FlowPanel theResult = new FlowPanel();

                final CustomMenuBar theActionMenu = new CustomMenuBar();
                theActionMenu.addItem(constants.add(), new Command() {
                    public void execute() {
                        setSelectedItems(myItemSelection);
                    }

                });
                theActionMenu.addItem(constants.cancel(), new Command() {
                    public void execute() {
                        cancel();
                    }
                });

                theResult.add(theActionMenu);
                return theResult;
            }

        };
        thePanel.setWidget(2, 0, myItemList);
        thePanel.getFlexCellFormatter().setColSpan(2, 0, 3);

        return thePanel;
    }

    protected void setSelectedItems(ItemSelection<BonitaProcessUUID> anItemSelection) {
        myItemFinderSelection.clearSelection();
        if (anItemSelection != null) {
            for (BonitaProcessUUID itemUUID : anItemSelection.getSelectedItems()) {
                myItemFinderSelection.addItemToSelection(itemUUID);
            }
            if (validate()) {
                myErrorMessageLabel.setText(null);
                myChanges.fireModelChange(ITEM_LIST_PROPERTY, null, myItemFinderSelection.getSelectedItems());
            } else {
                if (mySelectionIsMultiple) {
                    myErrorMessageLabel.setHTML(constants.selectSomeProcesses());
                } else {
                    myErrorMessageLabel.setHTML(constants.selectExactlyOneProcess());
                }
            }
        }
    }

    private void cancel() {
        clear();
        myChanges.fireModelChange(CANCEL_PROPERTY, false, true);
    }

    public void setFocus() {
    }

    public boolean validate() {
        return (myItemFinderSelection != null && (myItemFinderSelection.getSize() == 1 || (mySelectionIsMultiple && myItemFinderSelection.getSize() >= 1)));
    }

    /**
     * @param anErrorMessage
     */
    public void setErrorMessage(String anErrorMessage) {
        myErrorMessageLabel.setHTML(anErrorMessage);
    }

    public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        removeModelChangeListener(aPropertyName, aListener);
        myChanges.addModelChangeListener(aPropertyName, aListener);
    }

    public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        myChanges.removeModelChangeListener(aPropertyName, aListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.BonitaDataSource#getVisibleItems()
     */
    public List<BonitaProcessUUID> getVisibleItems() {
        return myItemFinderSelection.getSelectedItems();
    }

    public List<BonitaProcess> getItems() {
        final List<BonitaProcess> theResult = new ArrayList<BonitaProcess>();
        for (BonitaProcessUUID theItemUUID : myItemFinderSelection.getSelectedItems()) {
            theResult.add(myItemFinderDataSource.getItem(theItemUUID));
        }
        return theResult;
    }

    public void clear() {
        myItemFinderSelection.clearSelection();
        myItemFinderDataSource.getItemSelection().clearSelection();
        myErrorMessageLabel.setText(null);
    }
}
