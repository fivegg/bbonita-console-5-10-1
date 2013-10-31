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

import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.model.steps.StepDefinitionDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.steps.StepDefinition;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.I18NComposite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepDefinitionViewer extends I18NComposite implements ModelChangeListener {

    public static final String STEP_PROPERTY = "step definition viewer step";
    protected final StepDefinitionDataSource myItemDataSource;
    protected BonitaProcess myCurrentParentProcess;
    protected StepUUID myCurrentItemUUID;
    protected StepDefinition myCurrentItem;
    protected final FlowPanel myOuterPanel;
    protected final HTML myItemDescriptionLabel;
    protected final boolean isEditable;
    protected final FlexTable myIDCardPopupPanel = new FlexTable();

    protected CustomDialogBox myItemSearchPopup;
    protected StepDefinitionFinderPanel myItemFinder;

    protected transient ModelChangeSupport myChanges = new ModelChangeSupport(this);
    protected Image myClearIcon;
    private CustomMenuBar myStepDefinitionSearchButton;

    /**
     * Default constructor.
     */
    public StepDefinitionViewer(final StepDefinitionDataSource aDataSource, final StepUUID anItemUUID, final boolean isEditable) {
        myItemDataSource = aDataSource;
        myCurrentItemUUID = anItemUUID;
        this.isEditable = isEditable;

        myItemDescriptionLabel = new HTML(constants.loadingSmall());
        myOuterPanel = new FlowPanel();

        initContent();
        setItem(anItemUUID);

        myOuterPanel.setStylePrimaryName("bos_item_viewer");
        myItemDescriptionLabel.setStylePrimaryName("bos_item_viewer_identity");

        initWidget(myOuterPanel);
    }

    private void initContent() {
        final DecoratedPopupPanel theIDCardPopup = new DecoratedPopupPanel(true, false);
        theIDCardPopup.addStyleName("bos_item_viewer_identity_popup");

        myIDCardPopupPanel.setHTML(0, 0, constants.stepLabelLabel());
        myIDCardPopupPanel.getFlexCellFormatter().setStyleName(0, 0, "identity_form_label");

        myIDCardPopupPanel.setHTML(1, 0, constants.stepNameLabel());
        myIDCardPopupPanel.getFlexCellFormatter().setStyleName(1, 0, "identity_form_label");

        myIDCardPopupPanel.setHTML(2, 0, constants.processNameLabel());
        myIDCardPopupPanel.getFlexCellFormatter().setStyleName(2, 0, "identity_form_label");

        myIDCardPopupPanel.setHTML(3, 0, constants.processVersionLabel());
        myIDCardPopupPanel.getFlexCellFormatter().setStyleName(3, 0, "identity_form_label");

        theIDCardPopup.setWidget(myIDCardPopupPanel);
        myItemDescriptionLabel.addMouseOverHandler(new MouseOverHandler() {

            public void onMouseOver(MouseOverEvent aEvent) {
                if (myCurrentItem != null) {
                    theIDCardPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                        public void setPosition(int anOffsetWidth, int anOffsetHeight) {
                            int left = myItemDescriptionLabel.getAbsoluteLeft() - (anOffsetWidth / 2);
                            int top = myItemDescriptionLabel.getAbsoluteTop() + myItemDescriptionLabel.getOffsetHeight() + 7;
                            theIDCardPopup.setPopupPosition(left, top);
                        }
                    });
                }
            }

        });
        myItemDescriptionLabel.addMouseOutHandler(new MouseOutHandler() {

            public void onMouseOut(MouseOutEvent aEvent) {
                theIDCardPopup.hide();
            }
        });

        myOuterPanel.add(myItemDescriptionLabel);
        if (isEditable) {
            myStepDefinitionSearchButton = new CustomMenuBar();
            myStepDefinitionSearchButton.addItem(constants.search(), new Command() {

                public void execute() {
                    if (myItemSearchPopup == null) {
                        myItemSearchPopup = buildStepDefinitionSearchPopup();
                    }
                    myItemSearchPopup.center();
                }
            });

            myClearIcon = new Image(PICTURE_PLACE_HOLDER);
            myClearIcon.setStylePrimaryName(CSSClassManager.CLEAR_ICON);
            myClearIcon.setTitle(constants.remove());
            myClearIcon.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent aEvent) {
                    myCurrentItemUUID = null;
                    myCurrentItem = null;
                    update();
                    myChanges.fireModelChange(STEP_PROPERTY, null, myCurrentItem);
                }
            });
        }

    }

    protected CustomDialogBox buildStepDefinitionSearchPopup() {
        final CustomDialogBox theResult = new CustomDialogBox(false, true);
        myItemFinder.addModelChangeListener(StepDefinitionFinderPanel.ITEM_LIST_PROPERTY, this);
        myItemFinder.addModelChangeListener(StepDefinitionFinderPanel.CANCEL_PROPERTY, this);
        theResult.add(myItemFinder);
        theResult.setText(constants.searchForAStep());
        return theResult;
    }

    private void update() {
        if (isEditable) {
            if (myCurrentParentProcess != null) {
                myOuterPanel.add(myStepDefinitionSearchButton);
                if (myItemFinder == null) {
                    myItemFinder = new StepDefinitionFinderPanel(false);
                }
            } else {
                myOuterPanel.remove(myStepDefinitionSearchButton);
            }
            if (myItemFinder != null) {
                myItemFinder.setSelectedItems(null);
                myItemFinder.setParentProcess(myCurrentParentProcess);
            }
        }
        if (myCurrentItem != null) {
            myItemDescriptionLabel.setText(patterns.stepIdentity(myCurrentItem.getProcessName(), myCurrentItem.getProcessVersion(), myCurrentItem.getLabel()));
            myIDCardPopupPanel.setWidget(0, 1, new Label(myCurrentItem.getLabel()));
            myIDCardPopupPanel.setWidget(1, 1, new Label(myCurrentItem.getName()));
            myIDCardPopupPanel.setWidget(2, 1, new Label(myCurrentItem.getProcessName()));
            myIDCardPopupPanel.setWidget(3, 1, new Label(myCurrentItem.getProcessVersion()));
            if (isEditable) {
                myOuterPanel.add(myClearIcon);
            }
        } else {
            myItemDescriptionLabel.setText(constants.notYetDefined());
            myIDCardPopupPanel.setText(0, 1, null);
            myIDCardPopupPanel.setText(1, 1, null);
            myIDCardPopupPanel.setText(2, 1, null);
            myIDCardPopupPanel.setText(3, 1, null);
            if (isEditable) {
                myOuterPanel.remove(myClearIcon);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void modelChange(ModelChangeEvent aEvt) {
        if (StepDefinitionFinderPanel.ITEM_LIST_PROPERTY.equals(aEvt.getPropertyName())) {
            List<StepUUID> theNewValue = ((List<StepUUID>) aEvt.getNewValue());

            if (theNewValue != null && !theNewValue.isEmpty()) {
                myCurrentItemUUID = theNewValue.iterator().next();
                myCurrentItem = myItemFinder.getItems().iterator().next();
            } else {
                myCurrentItemUUID = null;
                myCurrentItem = null;
            }
            if (myItemFinder != null && aEvt.getSource().equals(myItemFinder)) {
                myItemSearchPopup.hide();
                myChanges.fireModelChange(STEP_PROPERTY, null, myCurrentItem);
            }
            update();
        } else if (StepDefinitionFinderPanel.CANCEL_PROPERTY.equals(aEvt.getPropertyName())) {
            myItemSearchPopup.hide();
        }

    }

    public void setItem(StepUUID aStepUUID) {
        myCurrentItemUUID = aStepUUID;
        if (aStepUUID == null) {
            myCurrentItem = null;
            update();
        } else {
            myItemDataSource.getItem(myCurrentItemUUID, new AsyncHandler<StepDefinition>() {

                public void handleFailure(Throwable aT) {
                    myCurrentItem = null;
                    update();
                }

                public void handleSuccess(StepDefinition aResult) {
                    myCurrentItem = aResult;
                    update();
                }
            });
        }

    }

    public void setParentProcess(BonitaProcess aProcess) {
        if (myCurrentParentProcess == null && aProcess != null || myCurrentParentProcess != null && aProcess == null || !myCurrentParentProcess.getUUID().equals(aProcess.getUUID())) {
            myCurrentItem = null;
            myCurrentItemUUID = null;
        }
        myCurrentParentProcess = aProcess;
        update();
    }

    public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        removeModelChangeListener(aPropertyName, aListener);
        myChanges.addModelChangeListener(aPropertyName, aListener);
    }

    public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        myChanges.removeModelChangeListener(aPropertyName, aListener);
    }
}
