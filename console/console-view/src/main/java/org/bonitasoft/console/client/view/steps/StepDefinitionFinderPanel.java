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
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.model.BonitaDataSource;
import org.bonitasoft.console.client.model.ItemSelection;
import org.bonitasoft.console.client.model.SimpleSelection;
import org.bonitasoft.console.client.model.steps.StepDefinitionDataSource;
import org.bonitasoft.console.client.model.steps.StepDefinitionDataSourceImpl;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.steps.StepDefinition;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.Focusable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepDefinitionFinderPanel extends Composite implements Focusable, BonitaDataSource<StepUUID> {

    public static final String CANCEL_PROPERTY = "StepDefinition finder cancel";
    protected StepDefinition myStepDefinition = null;

    protected FlexTable myOuterPanel = new FlexTable();
    protected final HTML myErrorMessageLabel = new HTML();
    protected final StepFilter myItemFilter = new StepFilter(null, 0, 10);
    protected AsyncHandler<ItemUpdates<StepDefinition>> myItemHandler;
    protected final SimpleSelection<StepUUID> myFinderItemSelection = new SimpleSelection<StepUUID>();
    protected final boolean mySelectionIsMultiple;

    protected final StepDefinitionDataSource myInternalItemDataSource = new StepDefinitionDataSourceImpl(null);

    protected final transient ModelChangeSupport myChanges = new ModelChangeSupport(this);
    protected boolean isInitiated = false;

    public StepDefinitionFinderPanel(boolean isMultiple) {
        super();
        mySelectionIsMultiple = isMultiple;

        myOuterPanel.setStylePrimaryName("bos_item_finder_panel");
        initWidget(myOuterPanel);
    }

    protected void buildContent() {
        myOuterPanel.setWidget(0, 0, myErrorMessageLabel);
        myOuterPanel.getFlexCellFormatter().setStyleName(0, 0, CSSClassManager.VALIDATION_ERROR_MESSAGE);
        myOuterPanel.getFlexCellFormatter().setColSpan(0, 0, 3);

        myInternalItemDataSource.setItemFilter(myItemFilter);
        StepsDefinitionListWidget myItemList = new StepsDefinitionListWidget(null, myInternalItemDataSource) {

            @Override
            protected void initView() {
                myMinimalSize = 5;
                myMaximalSize = 10;
                super.initView();
                if (myBottomNavBar.getWidgetCount() == 2) {
                    myBottomNavBar.remove(1);
                }
            }

            @Override
            protected String getContentRowTooltip() {
                return "";
            }

            @Override
            public void notifyItemClicked(StepUUID anItem, ClickEvent anEvent) {
             // Modify item selection.
                if (anItem != null) {
                    final Cell theCell = myInnerTable.getCellForEvent(anEvent);
                    if (theCell != null) {
                        final int theCellIndex = theCell.getCellIndex();
                        if (theCellIndex != 0) {
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
        myOuterPanel.setWidget(2, 0, myItemList);
        myOuterPanel.getFlexCellFormatter().setStyleName(2, 0, "identity_form_label");
        myOuterPanel.getFlexCellFormatter().setColSpan(2, 0, 3);

        isInitiated = true;
    }

    protected void setSelectedItems(ItemSelection<StepUUID> anItemSelection) {
        myFinderItemSelection.clearSelection();
        if (anItemSelection != null) {
            for (StepUUID itemUUID : anItemSelection.getSelectedItems()) {
                myFinderItemSelection.addItemToSelection(itemUUID);
            }
            if (validate()) {
                myErrorMessageLabel.setText(null);
                myChanges.fireModelChange(ITEM_LIST_PROPERTY, null, myFinderItemSelection.getSelectedItems());
            } else {
                if (mySelectionIsMultiple) {
                    myErrorMessageLabel.setHTML(constants.selectSomeSteps());
                } else {
                    myErrorMessageLabel.setHTML(constants.selectExactlyOneStep());
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
        return (myFinderItemSelection != null && (myFinderItemSelection.getSize() == 1 || (mySelectionIsMultiple && myFinderItemSelection.getSize() >= 1)));
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
    public List<StepUUID> getVisibleItems() {
        return myFinderItemSelection.getSelectedItems();
    }

    public List<StepDefinition> getItems() {
        final List<StepDefinition> theResult = new ArrayList<StepDefinition>();
        for (StepUUID theItemUUID : myFinderItemSelection.getSelectedItems()) {
            theResult.add(myInternalItemDataSource.getItem(theItemUUID));
        }
        return theResult;
    }

    /**
     * @param aProcess
     */
    public void setParentProcess(BonitaProcess aProcess) {
        if(aProcess!=null) {
            myItemFilter.setProcessUUID(aProcess.getUUID());
            if (!isInitiated) {
                buildContent();
            } else {
                myInternalItemDataSource.setItemFilter(myItemFilter);
                myInternalItemDataSource.reload();
            }
        } else {
            myItemFilter.setProcessUUID(null);
        }
    }

    public void clear() {
        myFinderItemSelection.clearSelection();
        myInternalItemDataSource.getItemSelection().clearSelection();
        myErrorMessageLabel.setText(null);
    }
}
