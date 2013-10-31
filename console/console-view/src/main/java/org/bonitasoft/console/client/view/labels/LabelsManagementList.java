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
package org.bonitasoft.console.client.view.labels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.StyleSelectionListener;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.labels.LabelFilter;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.model.ItemSelection;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.ItemFilterEditor;
import org.bonitasoft.console.client.view.ItemSelectionWidget;
import org.bonitasoft.console.client.view.identity.ConfirmationDialogbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelsManagementList extends AbstractItemList<LabelUUID, LabelModel, LabelFilter> {

    protected static final String LABEL_LIST_VISIBILITY_COL_STYLE = "label_list_visibility";
    protected static final String SYSTEM_LABEL_WIDGET_PLACEHOLDER = "system_label_widget_placeholder";

    protected final HashMap<String, ItemSelectionWidget<LabelUUID>> myItemSelectionWidgets = new HashMap<String, ItemSelectionWidget<LabelUUID>>();
    protected final HashMap<String, LabelStyleSelectorWidget> myItemStyleSelectorWidgets = new HashMap<String, LabelStyleSelectorWidget>();

    protected TextBox myNewLabelName = new TextBox();
    protected final DialogBox myCreateDialogBox = createDialogBox();
    
    protected ConfirmationDialogbox confirmationDialogbox;

    protected AsyncHandler<ItemUpdates<LabelModel>> myNewLabelHandler;

    private StyleSelectionListener myStyleSelectionListener;

    private ModelChangeListener myLabelNameChangeListener;

    protected LabelsConfiguration myConfiguration;
    private FlowPanel myTopMenubarWrapper;
    private FlowPanel myBottomMenubarWrapper;

    /**
     * Default constructor.
     * 
     * @param aRoleDataSource
     */
    public LabelsManagementList(MessageDataSource aMessageDataSource, ItemSelection<LabelUUID> anItemSelection, LabelDataSource anItemDataSource) {
        super(aMessageDataSource, anItemSelection, anItemDataSource, 20, 20, 3);
        myBonitaDataSource.addModelChangeListener(LabelDataSource.SYSTEM_LABEL_LIST_PROPERTY, this);
        myBonitaDataSource.addModelChangeListener(LabelDataSource.USER_LABEL_LIST_PROPERTY, this);
        myBonitaDataSource.addModelChangeListener(LabelDataSource.VISIBLE_LABEL_LIST_PROPERTY, this);
        newerButtonTop.setHTML(constants.previousPageLinkLabel());
        olderButtonTop.setHTML(constants.nextPageLinkLabel());
        newerButtonBottom.setHTML(constants.previousPageLinkLabel());
        olderButtonBottom.setHTML(constants.nextPageLinkLabel());

        anItemDataSource.getConfiguration(new AsyncHandler<LabelsConfiguration>() {

            public void handleFailure(Throwable aT) {
                updateMenuBars();
                myBonitaDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, LabelsManagementList.this);
            }

            public void handleSuccess(LabelsConfiguration aResult) {
                myConfiguration = new LabelsConfiguration();
                myConfiguration.setCustomLabelsEnabled(aResult.isCustomLabelsEnabled());
                myConfiguration.setStarEnabled(aResult.isStarEnabled());
                updateMenuBars();
                myBonitaDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, LabelsManagementList.this);
            }
        });

        initView();
        createWidgetsForItemsAndDisplay();
        // Not all the rows are editable
        myInnerTable.setTitle("");

    }

    protected void updateMenuBars() {
        // empty wrappers
        myTopMenubarWrapper.clear();
        myBottomMenubarWrapper.clear();
        // fill in wrappers
        myTopMenubarWrapper.add(buildMenuBar());
        myBottomMenubarWrapper.add(buildMenuBar());
    }

    @Override
    protected FlowPanel buildBottomNavBar() {
        final FlowPanel theResult = new FlowPanel();
        myBottomMenubarWrapper = new FlowPanel();
        myBottomMenubarWrapper.add(buildMenuBar());
        theResult.add(myBottomMenubarWrapper);

        return theResult;
    }

    protected void hideSelectedLabel() {
        updateVisibilityOfSelectedLabels(false);

    }

    private void updateVisibilityOfSelectedLabels(final boolean isVisible) {
        if (myItemSelection.getSize() > 0) {
            LabelModel theLabelModel;
            Set<LabelModel> theLabelSelection = new HashSet<LabelModel>();
            for (LabelUUID theLabelUUID : myItemSelection.getSelectedItems()) {
                theLabelModel = ((LabelDataSource) myBonitaDataSource).getLabel(theLabelUUID);
                if (theLabelModel != null) {
                    theLabelSelection.add(theLabelModel);
                }
            }
            ((LabelDataSource) myBonitaDataSource).updateLabelsVisibility(theLabelSelection, isVisible, new AsyncHandler<Void>() {
                public void handleFailure(Throwable anT) {
                    // TODO Auto-generated method stub

                }

                public void handleSuccess(Void anResult) {
                    try {
                        int theRow;
                        String theNewText;
                        if (myItemSelection.getSelectedItems() != null) {
                            for (LabelUUID theLabelUUID : myItemSelection.getSelectedItems()) {
                                theRow = myItemTableRow.get(theLabelUUID);
                                theNewText = (isVisible ? constants.visible() : constants.hidden());
                                if (myInnerTable.getWidget(theRow, 2) != null) {
                                    ((Label) myInnerTable.getWidget(theRow, 2)).setText(theNewText);
                                } else {
                                    // This should never occur.
                                    myInnerTable.setWidget(theRow, 2, new Label(theNewText));
                                }
                            }
                            myItemSelection.clearSelection();
                        }
                    } catch (Exception e) {
                        GWT.log("Unable to update label visibility: ", e);
                    }
                }
            });

        }

    }

    protected void showSelectedLabel() {
        updateVisibilityOfSelectedLabels(true);
    }

    protected void addLabel() {
        myCreateDialogBox.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int anOffsetWidth, int anOffsetHeight) {
                int left = ((Window.getClientWidth() / 2) - (anOffsetWidth / 2));
                int top = (Window.getClientHeight() / 3) - (anOffsetHeight / 2);
                myCreateDialogBox.setPopupPosition(left, top);
            }
        });
        myNewLabelName.setFocus(true);

    }

    @Override
    protected FlowPanel buildTopNavBar() {
        final FlowPanel theResult = new FlowPanel();
        myTopMenubarWrapper = new FlowPanel();
        myTopMenubarWrapper.add(buildMenuBar());
        theResult.add(myTopMenubarWrapper);

        return theResult;
    }

    protected Widget buildMenuBar() {
        final CustomMenuBar theMenubar = new CustomMenuBar();

        if (myConfiguration == null || myConfiguration.isCustomLabelsEnabled()) {
            theMenubar.addItem(constants.add(), new Command() {
                public void execute() {
                    addLabel();
                }

            });
        }
        theMenubar.addItem(constants.hide(), new Command() {
            public void execute() {
                hideSelectedLabel();
            }

        });
        theMenubar.addItem(constants.show(), new Command() {
            public void execute() {
                showSelectedLabel();
            }

        });
        if (myConfiguration == null || myConfiguration.isCustomLabelsEnabled()) {
            theMenubar.addItem(constants.delete(), new Command() {
                public void execute() {
                    if (myItemSelection.getSize() > 0 && !containsSystemLabel(myItemSelection.getSelectedItems())) {
                        confirmationDialogbox = new ConfirmationDialogbox(constants.deleteLabelsDialogbox(), patterns.deleteLabelsWarn(myItemSelection.getSize()), constants.okButton(), constants.cancelButton());
                        confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                            public void onClose(CloseEvent<PopupPanel> event) {                   
                                if(confirmationDialogbox.getConfirmation()){
                                    deleteSelectedLabels();
                                }
                            }} );
                    } else {
                        if (myMessageDataSource != null) {
                            myMessageDataSource.addWarningMessage(messages.invalidLabelSelectionWarn());
                        }
                    }

                }

            });
        }
        return theMenubar;
    }

    protected void deleteSelectedLabels() {
        if (myItemSelection.getSize() > 0) {
            ((LabelDataSource) myBonitaDataSource).deleteItems(myItemSelection.getSelectedItems(), new AsyncHandler<ItemUpdates<LabelModel>>() {
                public void handleFailure(Throwable anT) {
                    myItemSelection.clearSelection();
                }

                public void handleSuccess(ItemUpdates<LabelModel> anResult) {
                    myItemSelection.clearSelection();
                }
            });
        } else {
            myMessageDataSource.addWarningMessage(messages.noLabelSelected());
        }

    }

    @Override
    protected void createWidgetsForItemsAndDisplay() {
        List<LabelUUID> theLabels = ((LabelDataSource) myBonitaDataSource).getVisibleItems();
        if (theLabels != null) {
            hideLoading();
            myInnerTable.removeStyleName(LOADING_STYLE);
            if (myVisibleItems == null) {
                myVisibleItems = new ArrayList<LabelUUID>();
            } else {
                myVisibleItems.clear();
            }

            myVisibleItems.addAll(theLabels);

            for (LabelUUID theLabelUUID : myVisibleItems) {
                if (!myItemTableRow.containsKey(theLabelUUID)) {
                    createWidgetsForItem(theLabelUUID);
                }
            }
            // Update the UI.
            update(myVisibleItems);

        } else {
            displayLoading();
            myInnerTable.addStyleName(LOADING_STYLE);
        }

    }

    protected void createWidgetsForItem(final LabelUUID anItem) {
        myItemSelectionWidgets.put(anItem.getValue(), new ItemSelectionWidget<LabelUUID>(myItemSelection, anItem));
        LabelModel theLabelModel = ((LabelDataSource) myBonitaDataSource).getLabel(anItem);
        if (theLabelModel != null && !theLabelModel.isSystemLabel()) {
            myItemStyleSelectorWidgets.put(anItem.getValue(), new LabelStyleSelectorWidget((LabelDataSource) myBonitaDataSource, theLabelModel));
            if (myStyleSelectionListener == null) {
                myStyleSelectionListener = new StyleSelectionListener() {
                    public void notifySelectionChange(String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle) {
                        myItemStyleSelectorWidgets.get(anItem.getValue()).hide();
                        // myColorPicker.setStylePrimaryName(myLabelModel.getPreviewCSSStyleName());
                    }
                };
            }

            myItemStyleSelectorWidgets.get(anItem.getValue()).setStyleSelectionListener(myStyleSelectionListener);
            if (myLabelNameChangeListener == null) {
                myLabelNameChangeListener = new ModelChangeListener() {
                    public void modelChange(ModelChangeEvent aEvt) {
                        if (aEvt != null && aEvt.getSource() != null) {
                            LabelUUID theNewUUID = ((LabelUUID) aEvt.getNewValue());
                            LabelUUID theOldUUID = ((LabelUUID) aEvt.getOldValue());
                            ItemSelectionWidget<LabelUUID> theItemSeletionWidget = myItemSelectionWidgets.remove(theOldUUID.getValue());
                            if (theItemSeletionWidget != null) {
                                myItemSelectionWidgets.put(theNewUUID.getValue(), theItemSeletionWidget);
                            }
                            LabelStyleSelectorWidget theItemStyleSelector = myItemStyleSelectorWidgets.remove(theOldUUID.getValue());
                            if (theItemSeletionWidget != null) {
                                myItemStyleSelectorWidgets.put(theNewUUID.getValue(), theItemStyleSelector);
                            }
                            // As the UUID changes, re-map UUID to row.
                            Integer theRow = myItemTableRow.get(theOldUUID);
                            unlinkItemWithRow(theRow);
                            linkItemWithRow(theNewUUID, theRow);
                        }

                    }
                };
            }
            theLabelModel.addModelChangeListener(LabelModel.NAME_PROPERTY, myLabelNameChangeListener);
        }
    }

    public void notifyItemClicked(LabelUUID anItem, final ClickEvent anEvent) {
        Cell theCell = myInnerTable.getCellForEvent(anEvent);
        if (theCell != null && theCell.getCellIndex() > 1 && myItemStyleSelectorWidgets.containsKey(anItem.getValue())) {
            myItemStyleSelectorWidgets.get(anItem.getValue()).setPopupPosition(anEvent.getNativeEvent().getClientX() + 1, anEvent.getNativeEvent().getClientY() + 1);
            myItemStyleSelectorWidgets.get(anItem.getValue()).show();
        }
    }

    @Override
    protected void update(List<LabelUUID> anItemList) {

        updateListSize(anItemList);

        if (myTopNavBar != null && !myTopNavBar.isAttached()) {
            // Create the navigation row (Top).
            myInnerTable.setWidget(0, 0, myTopNavBar);
            myInnerTable.getFlexCellFormatter().setColSpan(0, 0, myColumnNumber);
        }

        fillContentRows(anItemList);

        if (myBottomNavBar != null && !myBottomNavBar.isAttached()) {
            // Create the navigation row (Bottom).
            int theBottomNavBarPosition = myInnerTable.getRowCount();
            myInnerTable.setWidget(theBottomNavBarPosition, 0, myBottomNavBar);
            myInnerTable.getFlexCellFormatter().setColSpan(theBottomNavBarPosition, 0, myColumnNumber);
        }

        myInnerTable.getColumnFormatter().setStyleName(2, LABEL_LIST_VISIBILITY_COL_STYLE);
    }

    private void fillContentRows(List<LabelUUID> anItemList) {
        // Add the column titles
        myInnerTable.setWidget(1, 1, new Label(constants.labelname()));
        myInnerTable.setWidget(1, 2, new Label(constants.visibility()));

        // Set CSS style.
        myInnerTable.getRowFormatter().setStylePrimaryName(1, ITEM_LIST_CONTENT_ROW_TITLE_STYLE);

        int theRowOffset = 2;
        int nbItemDisplayed = 0;
        int theCurrentRowIndex = theRowOffset + nbItemDisplayed;
        for (; nbItemDisplayed < anItemList.size(); nbItemDisplayed++) {
            theCurrentRowIndex = theRowOffset + nbItemDisplayed;

            LabelUUID theUUID = anItemList.get(nbItemDisplayed);
            LabelModel theItem = ((LabelDataSource) myBonitaDataSource).getLabel(theUUID);

            // Add a new row to the table, then set each of its columns.
            // layout widgets
            myInnerTable.setWidget(theCurrentRowIndex, 0, myItemSelectionWidgets.get(theUUID.getValue()));

            // Display name of system labels.<br>
            // Display css style selector and name for user labels
            if (theItem.isSystemLabel()) {
                HorizontalPanel theLabelPanel = new HorizontalPanel();
                Image theEmptyPicture = new Image(PICTURE_PLACE_HOLDER);
                theEmptyPicture.setStyleName(SYSTEM_LABEL_WIDGET_PLACEHOLDER);
                Image theLabelIcon = new Image(PICTURE_PLACE_HOLDER);
                theLabelIcon.setStyleName(theItem.getIconCSSStyle());
                // Finally layout widgets.
                theLabelPanel.add(theEmptyPicture);
                theLabelPanel.add(new Label(LocaleUtil.translate(theUUID)));
                theLabelPanel.add(theLabelIcon);
                myInnerTable.setWidget(theCurrentRowIndex, 1, theLabelPanel);
                myInnerTable.getRowFormatter().getElement(theCurrentRowIndex).setTitle("");
            } else {
                myInnerTable.setWidget(theCurrentRowIndex, 1, new LabelModifierWidget(((LabelDataSource) myBonitaDataSource), theItem));
                myInnerTable.getRowFormatter().getElement(theCurrentRowIndex).setTitle(constants.clickToEdit());
            }
            myInnerTable.setWidget(theCurrentRowIndex, 2, new Label((theItem.isVisible() ? constants.visible() : constants.hidden())));

            // Set CSS style.
            myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, ITEM_LIST_CONTENT_ROW_STYLE);

            // Keep link between the user and the row.
            linkItemWithRow(theUUID, theCurrentRowIndex);

        }

        // must not use the theCurrentRowIndex here because it may have an
        // offset
        // depending of the fact we enter into the loop or not.
        fillWithEmptyRows(theRowOffset, theRowOffset + nbItemDisplayed, myColumnNumber);

    }

    @Override
    public void modelChange(ModelChangeEvent anEvent) {
        // The event may come from a subscription made by my super class.
        super.modelChange(anEvent);
        if (LabelDataSource.VISIBLE_LABEL_LIST_PROPERTY.equals(anEvent.getPropertyName()) || LabelDataSource.USER_LABEL_LIST_PROPERTY.equals(anEvent.getPropertyName())
                || LabelDataSource.SYSTEM_LABEL_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
            createWidgetsForItemsAndDisplay();
        }
    }

    @Override
    public String getLocationLabel() {
        return constants.labelManagementView();
    }

    private DialogBox createDialogBox() {
        // Create a dialog box and set the caption text
        final CustomDialogBox theDialogBox = new CustomDialogBox();
        theDialogBox.setText(constants.newLabelWindowTitle());

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        theDialogBox.setWidget(dialogContents);

        myNewLabelName.setMaxLength(20);
        myNewLabelName.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent anEvent) {
                char theChar = anEvent.getCharCode();
                if (KeyCodes.KEY_ENTER == theChar) {
                    createNewLabel();
                }

            }
        });
        HorizontalPanel theButtonPanel = new HorizontalPanel();
        Button theOkButton = new Button(constants.okButton(), new ClickHandler() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
             * .gwt.event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent aArg0) {
                createNewLabel();
            }
        });
        Button theCancelButton = new Button(constants.cancelButton(), new ClickHandler() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
             * .gwt.event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent aArg0) {
                // Clean the form.
                myNewLabelName.setText("");
                myCreateDialogBox.hide();
            }
        });
        theButtonPanel.add(theOkButton);
        theButtonPanel.add(theCancelButton);

        // Layout widgets
        HorizontalPanel theForm = new HorizontalPanel();
        HTML label = new HTML(constants.newLabelWindowInputLabel());

        theForm.add(label);
        theForm.add(myNewLabelName);

        // theForm.setCellHorizontalAlignment(theNewLabelName,
        // HasHorizontalAlignment.ALIGN_RIGHT);

        dialogContents.add(theForm);
        dialogContents.add(theButtonPanel);
        dialogContents.setCellHorizontalAlignment(theButtonPanel, HasHorizontalAlignment.ALIGN_RIGHT);

        // Return the dialog box
        return theDialogBox;
    }

    private void createNewLabel() {
        final String theLabelName = myNewLabelName.getValue();
        if (theLabelName != null && theLabelName.length() > 0) {
            if (myNewLabelHandler == null) {
                myNewLabelHandler = new AsyncHandler<ItemUpdates<LabelModel>>() {
                    public void handleFailure(Throwable aT) {

                    }

                    public void handleSuccess(ItemUpdates<LabelModel> aResult) {
                        myNewLabelName.setText("");
                        myCreateDialogBox.hide();
                    }
                };
            }
            final LabelModel theLabelToCreate = new LabelModel(new LabelUUID(theLabelName, null), LabelModel.DEFAULT_EDITABLE_CSS, LabelModel.DEFAULT_READONLY_CSS, LabelModel.DEFAULT_PREVIEW_CSS,
                    true);
            ((LabelDataSource) myBonitaDataSource).addItem(theLabelToCreate, myNewLabelHandler);

        }
    }

    protected void linkItemWithRow(LabelUUID aUUID, int aRowIndex) {
        // create a new UUID to avoid updates from the outside.
        super.linkItemWithRow(new LabelUUID(aUUID.getValue(), aUUID.getOwner()), aRowIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.view.AbstractItemList#buildFilterEditor()
     */
    @Override
    protected ItemFilterEditor<LabelFilter> buildFilterEditor() {
        return null;
    }
    
    private boolean containsSystemLabel(ArrayList<LabelUUID> myLabelSelection){
        Iterator<LabelUUID> selectionIterator = myLabelSelection.iterator();
        while(selectionIterator.hasNext()){
            LabelUUID myLabelUuid = selectionIterator.next();
            LabelModel myLabelModel = ((LabelDataSource) myBonitaDataSource).getLabel(myLabelUuid);
            if(myLabelModel!=null && myLabelModel.isSystemLabel()){
                return true;
            }
        }
        return false;
    }

}
