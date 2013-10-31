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
import org.bonitasoft.console.client.events.AddItemHandler;
import org.bonitasoft.console.client.events.HasAddHandler;
import org.bonitasoft.console.client.events.HasRemoveHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.RemoveItemsHandler;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.steps.StepDefinitionDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.steps.StepDefinition;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.I18NComposite;
import org.bonitasoft.console.client.view.processes.ProcessViewer;

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
public class StepsDefinitionListEditorView extends I18NComposite implements ModelChangeListener, HasAddHandler<StepDefinition>, HasRemoveHandler<StepDefinition> {

  private Grid myItemsList;
  protected final List<StepDefinition> myItems;
  protected final Map<Integer, StepDefinition> myRowItem = new HashMap<Integer, StepDefinition>();
  protected final Set<Integer> myItemRowSelection;
  protected final ProcessViewer myProcessViewerPanel;
  protected final StepDefinitionViewer myStepViewerPanel;
  protected BonitaProcess myProcessToAdd;
  protected StepDefinition myStepDefinitionToAdd;
  protected Label myItemsErrorMessage;
  protected Label myAddErrorValidationMessage;

  protected final FlexTable myOuterPanel;
  protected final ProcessDataSource myBonitaProcessDataSource;
  protected final StepDefinitionDataSource myStepDefinitionDataSource;
  private ArrayList<AddItemHandler<StepDefinition>> myAddHandlers;
  private ArrayList<RemoveItemsHandler<StepDefinition>> myRemoveHandlers;
  private Label myStepViewerLabel;

  /**
   * Default constructor.
   */
  public StepsDefinitionListEditorView(final ProcessDataSource aBonitaProcessDataSource, final StepDefinitionDataSource aStepDataSource) {
    myBonitaProcessDataSource = aBonitaProcessDataSource;
    myStepDefinitionDataSource = aStepDataSource;

    myItems = new ArrayList<StepDefinition>();

    myOuterPanel = new FlexTable();
    myProcessViewerPanel = new ProcessViewer(aBonitaProcessDataSource, null, true);
    myStepViewerPanel = new StepDefinitionViewer(aStepDataSource, null, true);
    
    myProcessViewerPanel.addModelChangeListener(ProcessViewer.PROCESS_PROPERTY, this);
    myStepViewerPanel.addModelChangeListener(StepDefinitionViewer.STEP_PROPERTY, this);

    myItemRowSelection = new HashSet<Integer>();

    buildContent();

    myOuterPanel.setStylePrimaryName("bos_membership_list_editor");
    initWidget(myOuterPanel);
  }

  protected void buildContent() {

    myAddErrorValidationMessage = new Label();

    myItemsList = new Grid(1, 3);
    myItemsList.setWidth("100%");
    myItemsList.setStylePrimaryName("item_list");
    myItemsList.setWidget(0, 0, buildSelectAllSelector());
    myItemsList.setHTML(0, 1, constants.stepLabelLabel());
    myItemsList.setHTML(0, 2, constants.stepNameLabel());
    myItemsList.getRowFormatter().setStylePrimaryName(0, "item_list_content_row_title");

    final FlowPanel theAddBonitaProcessPanel = new FlowPanel();
    theAddBonitaProcessPanel.setStylePrimaryName(CSSClassManager.GROUP_PANEL);
    theAddBonitaProcessPanel.setVisible(false);
    final Label theAddGroupPanelCaption = new Label(constants.addStepGroupPanelCaption());
    theAddGroupPanelCaption.setStylePrimaryName(CSSClassManager.GROUP_PANEL_CAPTION);
    final Label theAddBonitaProcessPanelCloseCaption = new Label();
    theAddBonitaProcessPanelCloseCaption.setTitle(constants.close());
    theAddBonitaProcessPanelCloseCaption.setStylePrimaryName(CSSClassManager.GROUP_PANEL_ACTION_CAPTION);
    theAddBonitaProcessPanelCloseCaption.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        myProcessViewerPanel.setItem(null);
        myStepViewerPanel.setItem(null);
        hideStepViewer();
        theAddBonitaProcessPanel.setVisible(false);
      }
    });
    final Grid theAddPanel = new Grid(2, 2);
    theAddPanel.setStylePrimaryName(CSSClassManager.GROUP_PANEL_CONTENT);
    theAddPanel.setWidget(0, 0, new Label(constants.chooseAProcess()));
    theAddPanel.setWidget(0, 1, myProcessViewerPanel);
    myStepViewerLabel =new Label(constants.chooseAStep());
    theAddPanel.setWidget(1, 0, myStepViewerLabel);
    theAddPanel.setWidget(1, 1, myStepViewerPanel);
    
    hideStepViewer();
    
    final CustomMenuBar theAddButton = new CustomMenuBar();
    theAddButton.addItem(constants.add(), new Command() {

      public void execute() {
        addItemToList();
        theAddBonitaProcessPanel.setVisible(false);
        myProcessViewerPanel.setItem(null);
        myStepViewerPanel.setItem(null);
        hideStepViewer();
      }
    });

    theAddBonitaProcessPanel.add(theAddBonitaProcessPanelCloseCaption);
    theAddBonitaProcessPanel.add(theAddGroupPanelCaption);
    theAddBonitaProcessPanel.add(myAddErrorValidationMessage);
    theAddBonitaProcessPanel.add(theAddPanel);
    theAddBonitaProcessPanel.add(theAddButton);

    final CustomMenuBar theActionButtons = new CustomMenuBar();
    theActionButtons.addItem(constants.add(), new Command() {

      public void execute() {
        theAddBonitaProcessPanel.setVisible(true);
      }
    });

    theActionButtons.addItem(constants.delete(), new Command() {

      public void execute() {
        removeSelectedItems();
      }
    });

    myOuterPanel.setWidget(0, 0, myItemsList);
    myOuterPanel.setWidget(1, 0, theActionButtons);
    myOuterPanel.setWidget(2, 0, theAddBonitaProcessPanel);

    myItemsErrorMessage = new Label();
    myItemsErrorMessage.setStyleName(CSSClassManager.VALIDATION_ERROR_MESSAGE);
    myOuterPanel.setWidget(3, 0, myItemsErrorMessage);
  }

  protected void addItemToList() {
    if (myProcessToAdd != null && myStepDefinitionToAdd != null) {
      final String theStepDefinitionId = myStepDefinitionToAdd.getUUID().getValue();
      boolean alreadyInCandidates = myItems.contains(theStepDefinitionId);
      if (!alreadyInCandidates) {
        if (myAddHandlers != null) {
          for (AddItemHandler<StepDefinition> theHandler : myAddHandlers) {
            theHandler.addItemRequested(myStepDefinitionToAdd);
          }
        }
      }
    }
  }

  private Widget buildSelectAllSelector() {
    final CheckBox theSelectAllCheckBox = new CheckBox();
    theSelectAllCheckBox.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        if (aEvent.getSource() instanceof CheckBox) {
          CheckBox theCheckBox = (CheckBox) aEvent.getSource();
          selectAllRows(theCheckBox.getValue());
        }
      }
    });
    return theSelectAllCheckBox;
  }

  protected void selectAllRows(Boolean aValue) {
    for (int i = 1; i < myItemsList.getRowCount(); i++) {
      Widget theWidget = myItemsList.getWidget(i, 0);
      if (theWidget instanceof CheckBox) {
        ((CheckBox) theWidget).setValue(aValue, true);
      }
    }
  }

  protected void removeSelectedItems() {
    final Collection<StepDefinition> theItemsToRemove = new HashSet<StepDefinition>();
    int theRowIndex = myItemsList.getRowCount() - 1;
    for (int i = theRowIndex; i >= 1; i--) {
      Widget theWidget = myItemsList.getWidget(i, 0);
      if (theWidget instanceof CheckBox) {
        if (((CheckBox) theWidget).getValue()) {
          if (myRowItem.get(i) != null) {
            theItemsToRemove.add(myRowItem.get(i));
          }
        }
      }
    }
    for (RemoveItemsHandler<StepDefinition> theHandler : myRemoveHandlers) {
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
    if (StepDefinitionViewer.STEP_PROPERTY.equals(anEvt.getPropertyName())) {
      final StepDefinition theNewValue = (StepDefinition) anEvt.getNewValue();
      myStepDefinitionToAdd = theNewValue;
    } else if (ProcessViewer.PROCESS_PROPERTY.equals(anEvt.getPropertyName())) {
      final BonitaProcess theNewValue = (BonitaProcess) anEvt.getNewValue();
      myProcessToAdd = theNewValue;
      myStepViewerPanel.setParentProcess(myProcessToAdd);
      if(myProcessToAdd!=null) {
        showStepViewer();
      } else {
        hideStepViewer();
      }
    }
  }

  protected void hideStepViewer() {
    myStepViewerPanel.setVisible(false);
    myStepViewerLabel.setVisible(false);
  }
  
  protected void showStepViewer() {
    myStepViewerPanel.setVisible(true);
    myStepViewerLabel.setVisible(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.HasAddHandler#addAddHandler(org.bonitasoft
   * .console.client.events.ItemHandler)
   */
  public void addAddHandler(AddItemHandler<StepDefinition> aHandler) {
    if (aHandler != null) {
      if (myAddHandlers == null) {
        myAddHandlers = new ArrayList<AddItemHandler<StepDefinition>>();
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
  public void addRemoveHandler(RemoveItemsHandler<StepDefinition> aHandler) {
    if (aHandler != null) {
      if (myRemoveHandlers == null) {
        myRemoveHandlers = new ArrayList<RemoveItemsHandler<StepDefinition>>();
      }
      if (!myRemoveHandlers.contains(aHandler)) {
        myRemoveHandlers.add(aHandler);
      }
    }

  }

  /**
   * @param aResult
   */
  public void setItems(List<StepDefinition> anItemList) {
    myItems.clear();
    if (anItemList != null) {
      myItems.addAll(anItemList);
    }
    int theNbOfRow = 1;
    if (myItems != null) {
      theNbOfRow += myItems.size();
    }
    myItemsList.resize(theNbOfRow, 3);
    if (myItems != null && !myItems.isEmpty()) {
      int theCurrentRow = 1;
      for (StepDefinition theStepDefinition : myItems) {
        myItemsList.setWidget(theCurrentRow, 0, buildItemSelector(theCurrentRow));
        myItemsList.setWidget(theCurrentRow, 1, new Label(theStepDefinition.getLabel()));
        myItemsList.setWidget(theCurrentRow, 2, new Label(theStepDefinition.getName()));
        myItemsList.getRowFormatter().setStylePrimaryName(theCurrentRow, "item_list_content_row");
        // keep mapping between row and membership
        myRowItem.put(theCurrentRow, theStepDefinition);
        theCurrentRow++;
      }
    }
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
