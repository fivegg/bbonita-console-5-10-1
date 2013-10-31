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
package org.bonitasoft.console.client.view.identity;

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.identity.UserMetadataItem;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.identity.UserMetadataDataSource;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.ItemFilterEditor;
import org.bonitasoft.console.client.view.ItemSelectionWidget;
import org.bonitasoft.console.client.view.SimpleFilterEditor;
import org.bonitasoft.console.client.view.SimpleSelectorWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserMetadataListWidget extends AbstractItemList<BonitaUUID, UserMetadataItem, SimpleFilter> {

    protected CustomDialogBox addUpdateItemDialogBox;
    protected ConfirmationDialogbox confirmationDialogbox;
    protected final HashMap<BonitaUUID, ItemSelectionWidget<BonitaUUID>> myItemSelectionWidgets = new HashMap<BonitaUUID, ItemSelectionWidget<BonitaUUID>>();

    /**
     * Default constructor.
     * 
     * @param aRoleDataSource
     */
    public UserMetadataListWidget(MessageDataSource aMessageDataSource, UserMetadataDataSource aUserMetadataDataSource) {
        super(aMessageDataSource, aUserMetadataDataSource.getItemSelection(), aUserMetadataDataSource, 20, 20, 3);
        myBonitaDataSource.addModelChangeListener(UserDataSource.ITEM_LIST_PROPERTY, this);
        newerButtonTop.setHTML(constants.previousPageLinkLabel());
        olderButtonTop.setHTML(constants.nextPageLinkLabel());
        newerButtonBottom.setHTML(constants.previousPageLinkLabel());
        olderButtonBottom.setHTML(constants.nextPageLinkLabel());

        initView();
        createWidgetsForItemsAndDisplay();
        addUpdateItemDialogBox = new CustomDialogBox(false, true);

    }

    @Override
    protected FlowPanel buildBottomNavBar() {
        return buildTopNavBar();

    }

    @Override
    protected FlowPanel buildTopNavBar() {
        final FlowPanel theFirstCell = new FlowPanel();
        final CustomMenuBar theTopMenu = new CustomMenuBar();
        theTopMenu.addItem(constants.add(), new Command() {
            public void execute() {
                addItem();
            }

        });

        theTopMenu.addItem(constants.delete(), new Command() {

            public void execute() {
                // add a ConfirmationDialogbox when you delete user Metadata.
                if (myItemSelection.getSize() > 0) {
                    confirmationDialogbox = new ConfirmationDialogbox(constants.deleteUserMetadataDialogbox(), patterns.deleteUserMetadataWarn(myItemSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
                    confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>() {
                        public void onClose(CloseEvent<PopupPanel> event) {
                            if (confirmationDialogbox.getConfirmation()) {
                                deleteSelectedItems();
                            }
                        }
                    });
                } else {
                    if (myMessageDataSource != null) {
                        myMessageDataSource.addWarningMessage(messages.noUserMetadataSelectedWarn());
                    }
                }
            }

        });

        Label theRefreshLink = new Label(constants.refresh());
        theRefreshLink.setStylePrimaryName(CSSClassManager.LINK_LABEL);
        theRefreshLink.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent aEvent) {
                myBonitaDataSource.reload();

            }
        });

        // Create the Selector Widget
        theFirstCell.add(new SimpleSelectorWidget<BonitaUUID, UserMetadataItem, SimpleFilter>(myBonitaDataSource));
        theFirstCell.add(theTopMenu);
        theFirstCell.add(theRefreshLink);

        return theFirstCell;
    }

    protected void deleteSelectedItems() {
        if (myItemSelection.getSize() > 0) {
            myBonitaDataSource.deleteItems(myItemSelection.getSelectedItems(), new AsyncHandler<ItemUpdates<UserMetadataItem>>() {
                public void handleFailure(Throwable anT) {
                    myItemSelection.clearSelection();
                }

                public void handleSuccess(ItemUpdates<UserMetadataItem> anResult) {
                    myItemSelection.clearSelection();
                }
            });
        } else {
            myMessageDataSource.addWarningMessage(messages.noGroupSelected());
        }

    }

    protected void updateItem(final BonitaUUID anItem) {
        if (anItem != null) {
            final UserMetadataEditorPanel theItemEditorPanel = new UserMetadataEditorPanel((UserMetadataDataSource) myBonitaDataSource, anItem);
            theItemEditorPanel.addCancelClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    addUpdateItemDialogBox.hide();
                }
            });
            theItemEditorPanel.addSaveHandler(new AsyncHandler<Void>() {

                public void handleFailure(Throwable t) {
                }

                public void handleSuccess(Void result) {
                    addUpdateItemDialogBox.hide();
                    Integer theItemRow = myItemTableRow.get(anItem);
                    if (theItemRow != null) {
                        UserMetadataItem theItem = myBonitaDataSource.getItem(anItem);
                        myInnerTable.setWidget(theItemRow, 0, myItemSelectionWidgets.get(theItem.getUUID()));
                        myInnerTable.setWidget(theItemRow, 1, new Label(theItem.getName()));
                        myInnerTable.setWidget(theItemRow, 2, new Label(theItem.getLabel()));
                    }
                }
            });
            addUpdateItemDialogBox.clear();
            addUpdateItemDialogBox.add(theItemEditorPanel);
            addUpdateItemDialogBox.setText(constants.groupUpdate());
            addUpdateItemDialogBox.center();
            theItemEditorPanel.setFocus();
        }
    }

    protected void addItem() {
        final UserMetadataEditorPanel theItemEditorPanel = new UserMetadataEditorPanel((UserMetadataDataSource) myBonitaDataSource, null);
        theItemEditorPanel.addCancelClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                addUpdateItemDialogBox.hide();
            }
        });
        theItemEditorPanel.addSaveHandler(new AsyncHandler<Void>() {

            public void handleFailure(Throwable t) {
            }

            public void handleSuccess(Void result) {
                addUpdateItemDialogBox.hide();
            }
        });
        addUpdateItemDialogBox.clear();
        addUpdateItemDialogBox.add(theItemEditorPanel);
        addUpdateItemDialogBox.setText(constants.userMetadataCreation());
        addUpdateItemDialogBox.center();
        theItemEditorPanel.setFocus();
    }

    @Override
    protected void createWidgetsForItemsAndDisplay() {
        myVisibleItems = myBonitaDataSource.getVisibleItems();
        if (myVisibleItems != null) {
            hideLoading();
            myInnerTable.removeStyleName(LOADING_STYLE);
            for (BonitaUUID theItemUUID : myVisibleItems) {
                if (!myItemTableRow.containsKey(theItemUUID)) {
                    createWidgetsForItem(theItemUUID);
                }
            }
            // Update the UI.
            update(myVisibleItems);
        } else {
            displayLoading();
            myInnerTable.addStyleName(LOADING_STYLE);
        }

    }

    protected void createWidgetsForItem(BonitaUUID anItem) {
        myItemSelectionWidgets.put(anItem, new ItemSelectionWidget<BonitaUUID>(myItemSelection, anItem));
    }

    public void notifyItemClicked(BonitaUUID anItem, final ClickEvent anEvent) {
        Cell theCell = myInnerTable.getCellForEvent(anEvent);
        if (theCell != null && theCell.getCellIndex() > 0 && anItem != null) {
            updateItem(anItem);
        }
    }

    @Override
    protected void update(List<BonitaUUID> anItemList) {

        updateListSize(anItemList);

        if (myTopNavBar != null && !myTopNavBar.isAttached()) {
            // Create the navigation row (Top).
            myInnerTable.setWidget(0, 0, myTopNavBar);
            myInnerTable.getFlexCellFormatter().setColSpan(0, 0, myColumnNumber);
        }

        // Add the column titles
        myInnerTable.setWidget(1, 1, new Label(constants.name()));
        myInnerTable.setWidget(1, 2, new Label(constants.label()));

        // Set CSS style.
        myInnerTable.getRowFormatter().setStylePrimaryName(1, ITEM_LIST_CONTENT_ROW_TITLE_STYLE);

        fillInContentRow(anItemList);

        if (myBottomNavBar != null && !myBottomNavBar.isAttached()) {
            // Create the navigation row (Bottom).
            int theBottomNavBarPosition = myInnerTable.getRowCount();
            myInnerTable.setWidget(theBottomNavBarPosition, 0, myBottomNavBar);
            myInnerTable.getFlexCellFormatter().setColSpan(theBottomNavBarPosition, 0, myColumnNumber);
        }
    }

    protected void fillInContentRow(List<BonitaUUID> anItemList) {

        int theRowOffset = 2;
        int nbItemDisplayed = 0;
        int theCurrentRowIndex = theRowOffset + nbItemDisplayed;
        UserMetadataItem theItem;
        for (BonitaUUID theItemUUID : anItemList) {
            theCurrentRowIndex = theRowOffset + nbItemDisplayed;

            theItem = myBonitaDataSource.getItem(theItemUUID);
            // Add a new row to the table, then set each of its columns.
            // layout widgets
            myInnerTable.setWidget(theCurrentRowIndex, 0, myItemSelectionWidgets.get(theItem.getUUID()));
            myInnerTable.setWidget(theCurrentRowIndex, 1, new Label(theItem.getName()));
            myInnerTable.setWidget(theCurrentRowIndex, 2, new Label(theItem.getLabel()));

            // Set CSS style.
            if (theCurrentRowIndex % 2 == 0) {
                myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);
                myInnerTable.getRowFormatter().addStyleName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE + EVEN_STYLE_SUFFIX);
            } else {
                myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);
            }
            myInnerTable.getRowFormatter().getElement(theCurrentRowIndex).setTitle(getContentRowTooltip());
            // Keep link between the item and the row.
            linkItemWithRow(theItem.getUUID(), theCurrentRowIndex);

            nbItemDisplayed++;
        }

        fillWithEmptyRows(theRowOffset, theRowOffset + nbItemDisplayed, myColumnNumber);

    }

    protected String getContentRowTooltip() {
        return constants.clickToEdit();
    }

    @Override
    public void modelChange(ModelChangeEvent anEvent) {
        // The event may come from a subscription made by my super class.
        super.modelChange(anEvent);
        if (GroupDataSource.ITEM_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
            createWidgetsForItemsAndDisplay();
        }
        if (SimpleFilterEditor.FILTER_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
            final String theSearchPattern = ((SimpleFilter) anEvent.getNewValue()).getSearchPattern();
            myBonitaDataSource.getItemFilter().setSearchPattern(theSearchPattern);
            myBonitaDataSource.reload();
        }
    }

    @Override
    protected ItemFilterEditor<SimpleFilter> buildFilterEditor() {
        return null;
    }
}
