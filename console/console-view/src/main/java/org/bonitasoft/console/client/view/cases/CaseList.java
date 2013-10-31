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
package org.bonitasoft.console.client.view.cases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.cases.CasesConfiguration;
import org.bonitasoft.console.client.cases.CasesConfiguration.Columns;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.ItemFilterEditor;
import org.bonitasoft.console.client.view.ItemSelectionWidget;
import org.bonitasoft.console.client.view.StarWidget;
import org.bonitasoft.console.client.view.categories.CategoryViewer;
import org.bonitasoft.console.client.view.labels.LabelViewer;
import org.bonitasoft.console.client.view.steps.StepCandidatesWidget;
import org.bonitasoft.console.client.view.steps.StepPriorityWidget;
import org.bonitasoft.console.client.view.steps.StepRedirectWidget;
import org.bonitasoft.console.client.view.steps.StepStateWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Label;

/**
 * This widget display a tabular view of all the steps a user should see. The
 * list depends on the selection of a label.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class CaseList extends AbstractItemList<CaseUUID, CaseItem, CaseFilter> {

  protected static final String CASE_LIST_APPLICATION_STYLE = "case_list_application";
  protected static final String CASE_LIST_DATE_TIME_STYLE = "case_list_dateTime";
  protected static final String CASE_LIST_STEP_STYLE = "case_list_step";
  protected static final String CASE_LIST_DESCRIPTION_STYLE = "case_list_description";
  protected static final String CASE_LIST_STAR_STYLE = "case_list_star";
  protected static final String CASE_LIST_SELECT_STYLE = "case_list_select";
  protected static final String CASE_ARCHIVED_STYLE_NAME = "item_archived";
  protected static final String CASE_LIST_STYLE = "bos_case_list";
  protected static final String CASE_LIST_LABELS_STYLE = "bos_case_list_labels";
  protected static final String CASE_LIST_CATEGORIES_STYLE = "bos_case_list_categories";
  protected static final String CASE_LIST_STEP_STATE_STYLE = "bos_case_list_step_state";
  protected static final String CASE_LIST_STEP_ASSIGN_STYLE = "bos_case_list_step_assignee";
  protected static final String CASE_LIST_STEP_PRIORITY_STYLE = "bos_case_list_step_priority";
  protected static final String CASE_LIST_STEP_NAME_STYLE = "bos_case_list_step_name";
  protected static final String CASE_LIST_STRETCHED_COLUMN_STYLE = "bos_case_list_stretched";
  

  protected final ProcessDataSource myProcessDataSource;
  protected LabelDataSource myLabelDataSource;
  protected CaseMenuBarWidget myTopMenuBar;
  protected CaseMenuBarWidget myBottomMenuBar;

  protected final HashMap<CaseItem, LabelViewer> myCaseLabelViewers = new HashMap<CaseItem, LabelViewer>();
  protected final HashMap<CaseItem, ItemSelectionWidget<CaseUUID>> myCaseSelectionWidgets = new HashMap<CaseItem, ItemSelectionWidget<CaseUUID>>();
  protected final HashMap<CaseItem, StarWidget> myCaseStarWidgets = new HashMap<CaseItem, StarWidget>();
  protected final HashMap<CaseItem, String> myCaseStepName = new HashMap<CaseItem, String>();
  protected final HashMap<CaseItem, String> myCaseStepDescription = new HashMap<CaseItem, String>();
  protected final HashMap<CaseItem, StepCandidatesWidget> myCaseStepAssign = new HashMap<CaseItem, StepCandidatesWidget>();
  protected final HashMap<CaseItem, StepPriorityWidget> myCaseStepPriority = new HashMap<CaseItem, StepPriorityWidget>();
  protected final HashMap<CaseItem, StepStateWidget> myCaseStepState = new HashMap<CaseItem, StepStateWidget>();

  protected final HashMap<CaseItem, CaseDescriptionWidget> myCaseDescriptionWidgets = new HashMap<CaseItem, CaseDescriptionWidget>();
  protected final HashMap<CaseItem, CategoryViewer> myCaseCategoryViewers = new HashMap<CaseItem, CategoryViewer>();
  protected final HashMap<CaseItem, Label> myCaseUpdateDateAndTime = new HashMap<CaseItem, Label>();

  protected LabelUUID myLabelCurrentlyDisplayed;
  protected CategoryUUID myCategoryCurrentlyDisplayed;
  protected LabelsConfiguration myLabelConfiguration;

  private CategoryDataSource myCategoryDataSource;

  protected CasesConfiguration myCaseConfiguration;

  protected int mySelectColumn = -1;
  protected int myStarIconColumn = -1;
  protected int myCaseLabelsColumn = -1;
  protected int myCaseCategoriesColumn = -1;
  protected int myCaseDescriptionColumn = -1;
  protected int myStepStateColumn = -1;
  protected int myStepAssignColumn = -1;
  protected int myStepPriorityColumn = -1;
  protected int myStepNameColumn = -1;
  protected int myStepDescriptionColumn = -1;
  protected int myDateAndTimeColumn = -1;
  protected int myApplicationLinkColumn = -1;
  private int myStretchedColumnIndex;

  /**
   * 
   * Default constructor.
   * 
   * @param aCaseSelection
   * @param aCaseDataSource
   * @param aLabelDataSource
   */
  public CaseList(final MessageDataSource aMessageDataSource, CaseSelection aCaseSelection, CaseDataSource aCaseDataSource, ProcessDataSource aProcessDataSource, LabelDataSource aLabelDataSource,
      CategoryDataSource aCategoryDataSource) {
    super(aMessageDataSource, aCaseSelection, aCaseDataSource, constants.defaultMaxDisplayedItems(), constants.defaultMaxDisplayedItems(), Columns.values().length);
    addStyleName(CASE_LIST_STYLE);
    // store the data model
    myProcessDataSource = aProcessDataSource;
    myLabelDataSource = aLabelDataSource;
    myCategoryDataSource = aCategoryDataSource;
    // Listen changes on the list of myCases to display.
    myBonitaDataSource.addModelChangeListener(CaseDataSource.CASE_LIST_PROPERTY, this);
    myLabelDataSource.addModelChangeListener(LabelDataSource.LABEL_CASE_ASSOCIATION_PROPERTY, this);

    initView();

    myLabelDataSource.getConfiguration(new AsyncHandler<LabelsConfiguration>() {

      public void handleFailure(Throwable aT) {
        myLabelDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, CaseList.this);
        loadCaseConfiguration();
      }

      public void handleSuccess(LabelsConfiguration aResult) {
        myLabelConfiguration = new LabelsConfiguration();
        myLabelConfiguration.setCustomLabelsEnabled(aResult.isCustomLabelsEnabled());
        myLabelConfiguration.setStarEnabled(aResult.isStarEnabled());
        myLabelDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, CaseList.this);
        loadCaseConfiguration();

      }
    });

  }

  /**
   * 
   */
  protected void loadCaseConfiguration() {
    ((CaseDataSource) myBonitaDataSource).getConfiguration(new AsyncHandler<CasesConfiguration>() {
      public void handleFailure(Throwable aT) {
        updateLayout();
        createWidgetsForItemsAndDisplay();

      }

      public void handleSuccess(CasesConfiguration result) {
        myCaseConfiguration = new CasesConfiguration();
        if (result != null) {
          myCaseConfiguration.update(result);
        }
        ((CaseDataSource) myBonitaDataSource).addModelChangeListener(CaseDataSource.CONFIGURATION_PROPERTY, CaseList.this);
        updateLayout();
        createWidgetsForItemsAndDisplay();
      };
    });

  }

  /*
   * Update the UI.
   */
  protected void update(final List<CaseUUID> aCaseList) {
      
    for (int i = 0; i < aCaseList.size(); i++) {
        for (int j = i + 1; j < aCaseList.size(); j++) {
            if (aCaseList.get(i).equals(aCaseList.get(j))) {
                aCaseList.remove(i);
            }
        }
    }

    updateListSize(aCaseList);

    if (!myTopNavBar.isAttached()) {
      // Create the navigation row (Top).
      myInnerTable.setWidget(0, 0, myTopNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(0, 0, myColumnNumber);
      myInnerTable.getRowFormatter().setStyleName(0, "case_list_navbar");
    }

    fillContentRows(aCaseList);

    if (myBottomNavBar != null && !myBottomNavBar.isAttached()) {
      // Create the navigation row (Bottom).
      int theBottomNavBarPosition = myInnerTable.getRowCount();
      myInnerTable.setWidget(theBottomNavBarPosition, 0, myBottomNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(theBottomNavBarPosition, 0, myColumnNumber);
      myInnerTable.getRowFormatter().setStyleName(theBottomNavBarPosition, "case_list_navbar");
    }

    if (mySelectColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(mySelectColumn, CASE_LIST_SELECT_STYLE);
    }
    if (myStarIconColumn > -1) {
      if (myLabelConfiguration == null || myLabelConfiguration.isStarEnabled()) {
        myInnerTable.getColumnFormatter().setStyleName(myStarIconColumn, CASE_LIST_STAR_STYLE);
      } else {
        myInnerTable.getColumnFormatter().removeStyleName(myStarIconColumn, CASE_LIST_STAR_STYLE);
      }
    }
    if (myCaseDescriptionColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myCaseDescriptionColumn, CASE_LIST_DESCRIPTION_STYLE);
    }
    if (myCaseLabelsColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myCaseLabelsColumn, CASE_LIST_LABELS_STYLE);
    }
    if (myCaseCategoriesColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myCaseCategoriesColumn, CASE_LIST_CATEGORIES_STYLE);
    }
    if (myStepStateColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myStepStateColumn, CASE_LIST_STEP_STATE_STYLE);
    }
    if (myStepAssignColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myStepAssignColumn, CASE_LIST_STEP_ASSIGN_STYLE);
    }
    if (myStepPriorityColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myStepPriorityColumn, CASE_LIST_STEP_PRIORITY_STYLE);
    }
    if (myStepNameColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myStepNameColumn, CASE_LIST_STEP_NAME_STYLE);
    }
    if (myStepDescriptionColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myStepDescriptionColumn, CASE_LIST_STEP_STYLE);
    }
    if (myDateAndTimeColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myDateAndTimeColumn, CASE_LIST_DATE_TIME_STYLE);
    }
    if (myApplicationLinkColumn > -1) {
      myInnerTable.getColumnFormatter().setStyleName(myApplicationLinkColumn, CASE_LIST_APPLICATION_STYLE);
    }

    // Add a style to force a column to take all the available space.
    if(myStretchedColumnIndex > -1) {
      myInnerTable.getColumnFormatter().addStyleName(myStretchedColumnIndex, CASE_LIST_STRETCHED_COLUMN_STYLE);
    }
  }

  protected void fillContentRows(final List<CaseUUID> aCaseList) {
    int theRowOffset = 1;
    int nbCaseDisplayed = 0;
    int theCurrentRowIndex = theRowOffset + nbCaseDisplayed;
    for (; nbCaseDisplayed < aCaseList.size(); nbCaseDisplayed++) {

      theCurrentRowIndex = theRowOffset + nbCaseDisplayed;
      CaseUUID theCaseUUID = aCaseList.get(nbCaseDisplayed);
      CaseItem theCaseItem = ((CaseDataSource) myBonitaDataSource).getItem(theCaseUUID);

      List<StepItem> theSteps = theCaseItem.getSteps();

      // Add a new row to the table, then set each of its columns.
      // layout widgets
      if (mySelectColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, mySelectColumn, myCaseSelectionWidgets.get(theCaseItem));
      }
      if (myStarIconColumn > -1) {
        if (myLabelConfiguration == null || myLabelConfiguration.isStarEnabled()) {
          myInnerTable.setWidget(theCurrentRowIndex, myStarIconColumn, myCaseStarWidgets.get(theCaseItem));
        } else {
          myInnerTable.setHTML(theCurrentRowIndex, myStarIconColumn, "");
        }
      }
      if (myCaseCategoriesColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myCaseCategoriesColumn, myCaseCategoryViewers.get(theCaseItem));
      }
      if (myCaseLabelsColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myCaseLabelsColumn, myCaseLabelViewers.get(theCaseItem));
      }
      if (myStepNameColumn > -1) {
        myInnerTable.setHTML(theCurrentRowIndex, myStepNameColumn, myCaseStepName.get(theCaseItem));
      }
      if (myCaseDescriptionColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myCaseDescriptionColumn, myCaseDescriptionWidgets.get(theCaseItem));
      }
      if (myStepStateColumn > -1) {
        if (myCaseStepState.containsKey(theCaseItem)) {
          myInnerTable.setWidget(theCurrentRowIndex, myStepStateColumn, myCaseStepState.get(theCaseItem));
        } else {
          myInnerTable.setHTML(theCurrentRowIndex, myStepStateColumn, NBSP);
        }
      }
      if (myStepAssignColumn > -1) {
        if (myCaseStepAssign.containsKey(theCaseItem)) {
          myInnerTable.setWidget(theCurrentRowIndex, myStepAssignColumn, myCaseStepAssign.get(theCaseItem));
        } else {
          myInnerTable.setHTML(theCurrentRowIndex, myStepAssignColumn, NBSP);
        }
      }
      if (myStepPriorityColumn > -1) {
        if (myCaseStepPriority.containsKey(theCaseItem)) {
          myInnerTable.setWidget(theCurrentRowIndex, myStepPriorityColumn, myCaseStepPriority.get(theCaseItem));
        } else {
          myInnerTable.setHTML(theCurrentRowIndex, myStepPriorityColumn, NBSP);
        }
      }
      if (myStepDescriptionColumn > -1) {
        myInnerTable.setHTML(theCurrentRowIndex, myStepDescriptionColumn, myCaseStepDescription.get(theCaseItem));
      }
      if (myDateAndTimeColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myDateAndTimeColumn, myCaseUpdateDateAndTime.get(theCaseItem));
      }
      if (myApplicationLinkColumn > -1) {
        // determinate whether there is only one step among the current
        // ready steps or not .
        // in that case currentStep remains null and the link button to the
        // form app isn't displayed
        StepItem currentStep = null;
        int nbOfCurrentStep = 0;
        for (StepItem stepItem : theSteps) {
          if ((StepState.READY == stepItem.getState()) && (nbOfCurrentStep == 0)) {
            currentStep = stepItem;
            nbOfCurrentStep++;
          }
        }

        if ((nbOfCurrentStep == 1) && (currentStep != null)) {
          myInnerTable.setWidget(theCurrentRowIndex, myApplicationLinkColumn, new StepRedirectWidget(currentStep, (CaseDataSource) myBonitaDataSource, myProcessDataSource));
        } else {
          myInnerTable.setHTML(theCurrentRowIndex, myApplicationLinkColumn, NBSP);
        }
      }
      // Set CSS style.
      myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, "case_list_content_row");
      myInnerTable.getRowFormatter().getElement(theCurrentRowIndex).setTitle(constants.clickToOpen());

      if (theCaseItem.isArchived()) {
        myInnerTable.getRowFormatter().addStyleName(theCurrentRowIndex, CASE_ARCHIVED_STYLE_NAME);
      } else {
        myInnerTable.getRowFormatter().removeStyleName(theCurrentRowIndex, CASE_ARCHIVED_STYLE_NAME);
      }
      if (myItemSelection.getSelectedItems().contains(theCaseItem.getUUID())) {
        myInnerTable.getRowFormatter().addStyleName(theCurrentRowIndex, ITEM_SELECTED_STYLE);
      } else {
        myInnerTable.getRowFormatter().removeStyleName(theCurrentRowIndex, ITEM_SELECTED_STYLE);
      }
      myInnerTable.getFlexCellFormatter().setColSpan(theCurrentRowIndex, 0, 1);
      // Keep link between the case and the row.
      linkItemWithRow(theCaseItem.getUUID(), theCurrentRowIndex);

    }

    fillWithEmptyRows(theRowOffset, theRowOffset + nbCaseDisplayed, myColumnNumber);
  }

  @Override
  protected void createWidgetsForItemsAndDisplay() {
    myVisibleItems = ((CaseDataSource) myBonitaDataSource).getVisibleItems();

    if (myVisibleItems != null) {
        hideLoading();
        myInnerTable.removeStyleName(LOADING_STYLE);
        for (CaseUUID theCaseUUID : myVisibleItems) {
            CaseItem theCase = ((CaseDataSource) myBonitaDataSource).getItem(theCaseUUID);
            createWidgetsForCase(theCase);
            if ((myLabelConfiguration == null || myLabelConfiguration.isStarEnabled()) && (!myCaseStarWidgets.containsKey(theCase))) {
                myCaseStarWidgets.put(theCase, new StarWidget(myLabelDataSource, theCase));
            }
        }
        // Update the UI.
        update(myVisibleItems);

        registerToEvents();

    } else {
        displayLoading();
        myInnerTable.addStyleName(LOADING_STYLE);
    }

  }

  private void registerToEvents() {
    final CaseFilter theFilter = ((CaseDataSource) myBonitaDataSource).getItemFilter();
    // remove listener
    if (theFilter != null) {
      LabelModel theLabelModel;
      if (myLabelCurrentlyDisplayed != null) {
        theLabelModel = myLabelDataSource.getLabel(myLabelCurrentlyDisplayed);
        if (theLabelModel != null) {
          theLabelModel.removeModelChangeListener(LabelModel.CASES_PROPERTY, this);
        }
      }
      // remove listener
      Category theCategory;
      if (myCategoryCurrentlyDisplayed != null) {
        theCategory = myCategoryDataSource.getItem(myCategoryCurrentlyDisplayed);
        if (theCategory != null) {
          theCategory.removeModelChangeListener(Category.CASES_PROPERTY, this);
        }
      }

      // restore listeners
      myLabelCurrentlyDisplayed = theFilter.getLabel();
      if (theFilter.getCategory() != null) {
        myCategoryCurrentlyDisplayed = theFilter.getCategory().getUUID();
      } else {
        myCategoryCurrentlyDisplayed = null;
      }

      if (myLabelCurrentlyDisplayed != null) {
        theLabelModel = myLabelDataSource.getLabel(myLabelCurrentlyDisplayed);
        if(theLabelModel != null) {
          theLabelModel.addModelChangeListener(LabelModel.CASES_PROPERTY, this);
        } else {
          GWT.log("Label not found! + " + myLabelCurrentlyDisplayed.getValue(),new NullPointerException());
        }
      } else {
        theLabelModel = null;
      }
      if (myCategoryCurrentlyDisplayed != null) {
        theCategory = myCategoryDataSource.getItem(myCategoryCurrentlyDisplayed);
        theCategory.addModelChangeListener(Category.CASES_PROPERTY, this);
      } else {
        theCategory = null;
      }

      myTopMenuBar.setLabelToDisplay(theLabelModel);
      myBottomMenuBar.setLabelToDisplay(theLabelModel);
      myTopMenuBar.setCategoryToDisplay(theCategory);
      myBottomMenuBar.setCategoryToDisplay(theCategory);
    }

  }

  /**
   * Widgets are created once for each case to display. It avoids to create many
   * widgets each time the view is refreshed.
   * 
   * @param aCase
   */
  protected void createWidgetsForCase(CaseItem aCase) {
    if (!myCaseCategoryViewers.containsKey(aCase) && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.CATEGORIES_COLUMN) > -1))) {
      myCaseCategoryViewers.put(aCase, new CategoryViewer(aCase.getProcessUUID(), myProcessDataSource, myCategoryDataSource));
    }
    if (!myCaseLabelViewers.containsKey(aCase) && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.LABELS_COLUMN) > -1))) {
      myCaseLabelViewers.put(aCase, new LabelViewer(myLabelDataSource, aCase, false));
    }
    if (!myCaseSelectionWidgets.containsKey(aCase) && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.SELECT_COLUMN) > -1))) {
      myCaseSelectionWidgets.put(aCase, new ItemSelectionWidget<CaseUUID>(myItemSelection, aCase.getUUID()));
    }
    if (!myCaseDescriptionWidgets.containsKey(aCase) && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.DESCRIPTION_COLUMN) > -1))) {
      myCaseDescriptionWidgets.put(aCase, new CaseDescriptionWidget(aCase, myProcessDataSource));
    }
    if (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.UPDATE_COLUMN) > -1)) {
      final Label theLabel = new Label(DateTimeFormat.getFormat(constants.dateShortFormat()).format(aCase.getLastUpdateDate()));
      theLabel.setTitle(constants.lastUpdateDate());
      myCaseUpdateDateAndTime.put(aCase, theLabel);
    }

    // Compute open steps
    List<StepItem> theOpenSteps = getOpenSteps(aCase);
    // create widgets
    if (theOpenSteps.size() == 0) {
      if (aCase.getSteps() == null || aCase.getSteps().size() == 0) {
        if (myCaseStepState.containsKey(aCase)) {
          myCaseStepState.remove(aCase);
        }
        if (myCaseStepPriority.containsKey(aCase)) {
          myCaseStepPriority.remove(aCase);
        }
        if (myCaseStepAssign.containsKey(aCase)) {
          myCaseStepAssign.remove(aCase);
        }
        if (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_NAME_COLUMN) > -1)) {
          myCaseStepName.put(aCase, constants.noStepsToPerform());
        }
        if (myCaseStepDescription.containsKey(aCase)) {
          myCaseStepDescription.remove(aCase);
        }
      } else {
        // Display the last executed step.
        final StepItem theStep = aCase.getSteps().get(0);
        if ((!myCaseStepState.containsKey(aCase) || !myCaseStepState.get(aCase).getItem().equals(theStep))
            && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_STATE_COLUMN) > -1))) {
          myCaseStepState.put(aCase, new StepStateWidget(theStep));
        }
        if ((!myCaseStepPriority.containsKey(aCase) || !myCaseStepPriority.get(aCase).getItem().equals(theStep))
            && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_PRIORITY_COLUMN) > -1))) {
            if (!StepState.FAILED.equals(theStep.getState())){
                myCaseStepPriority.put(aCase, new StepPriorityWidget(theStep));   
            }          
        }
        if ((!myCaseStepAssign.containsKey(aCase) || !myCaseStepAssign.get(aCase).getItem().equals(theStep))
            && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_ASSIGN_COLUMN) > -1))) {
          myCaseStepAssign.put(aCase, new StepCandidatesWidget(theStep));
        }
        if (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_NAME_COLUMN) > -1)) {
          myCaseStepName.put(aCase, theStep.getLabel());
        }
        if (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_DESCRIPTION_COLUMN) > -1)) {
          myCaseStepDescription.put(aCase, theStep.getDesc());
        }
      }
    } else if (theOpenSteps.size() == 1) {
      final StepItem theStep = theOpenSteps.get(0);
      // 1 step to perform
      if ((!myCaseStepState.containsKey(aCase) || !myCaseStepState.get(aCase).getItem().equals(theStep))
          && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_STATE_COLUMN) > -1))) {
        myCaseStepState.put(aCase, new StepStateWidget(theStep));
      }
      if ((!myCaseStepPriority.containsKey(aCase) || !myCaseStepPriority.get(aCase).getItem().equals(theStep))
          && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_PRIORITY_COLUMN) > -1))) {
          if (!StepState.FAILED.equals(theStep.getState())){
              myCaseStepPriority.put(aCase, new StepPriorityWidget(theStep));
          }
      }
      if ((!myCaseStepAssign.containsKey(aCase) || !myCaseStepAssign.get(aCase).getItem().equals(theStep))
          && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_ASSIGN_COLUMN) > -1))) {
        myCaseStepAssign.put(aCase, new StepCandidatesWidget(theStep));
      }
      if (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_NAME_COLUMN) > -1)) {
        myCaseStepName.put(aCase, theStep.getLabel());
      }
      if (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_DESCRIPTION_COLUMN) > -1)) {
        myCaseStepDescription.put(aCase, theStep.getDesc());
      }
    } else {
      // Multiple steps to perform
      if (myCaseStepState.containsKey(aCase)) {
        myCaseStepState.remove(aCase);
      }
      if (myCaseStepPriority.containsKey(aCase)) {
        myCaseStepPriority.remove(aCase);
      }
      if (myCaseStepAssign.containsKey(aCase)) {
        myCaseStepAssign.remove(aCase);
      }
      if (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_NAME_COLUMN) > -1)) {
        myCaseStepName.put(aCase, patterns.multipleStepsToDo(theOpenSteps.size()));
      }
      if (myCaseStepDescription.containsKey(aCase)) {
        myCaseStepDescription.remove(aCase);
      }
    }

    // Listen changes
//    aCase.addModelChangeListener(CaseItem.STEPS_PROPERTY, this);
    aCase.addModelChangeListener(CaseItem.LAST_UPDATE_PROPERTY, this);
    
  }

  protected List<StepItem> getOpenSteps(CaseItem aCase) {
    final ArrayList<StepItem> theOpenSteps = new ArrayList<StepItem>();
    final List<StepItem> theCaseSteps = aCase.getSteps();
    for (StepItem theStep : theCaseSteps) {
      if (StepState.FINISHED != theStep.getState() && StepState.ABORTED != theStep.getState() && StepState.CANCELLED != theStep.getState() && StepState.SKIPPED != theStep.getState()) {
        theOpenSteps.add(theStep);
      } else {
        theStep.removeModelChangeListener(StepItem.STATE_PROPERTY, this);
      }
    }

    return theOpenSteps;
  }

  /*
   * (non-Javadoc)
   * 
   * @seejava.beans.ModelChangeListener#propertyChange(java.beans.
   * PropertyChangeEvent)
   */
  public void modelChange(ModelChangeEvent anEvent) {
    if (isAttached()) {
      super.modelChange(anEvent);
      if (CaseDataSource.CASE_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
        createWidgetsForItemsAndDisplay();
      } else if (LabelModel.CASES_PROPERTY.equals(anEvent.getPropertyName())) {
        updateListSize(((CaseDataSource) myBonitaDataSource).getVisibleItems());
      } else if (Category.CASES_PROPERTY.equals(anEvent.getPropertyName())) {
        updateListSize(((CaseDataSource) myBonitaDataSource).getVisibleItems());
      } else if (CaseFilterEditor.FILTER_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
        final CaseFilter theFilterToUpdate = ((CaseDataSource) myBonitaDataSource).getItemFilter();
        theFilterToUpdate.updateFilter(((CaseFilter) anEvent.getNewValue()));
        // Use the setItemFilter with the existing value on purpose to force the case list to be reset and display loading...
        myBonitaDataSource.setItemFilter(theFilterToUpdate);
      } else if (LabelDataSource.LABEL_CASE_ASSOCIATION_PROPERTY.equals(anEvent.getPropertyName())) {
        // The cases have been updated using the label data source. Sync
        // data.
        myBonitaDataSource.reload();
      } 
    }
    // update even if it is not attached.
    if (LabelDataSource.CONFIGURATION_PROPERTY.equals(anEvent.getPropertyName())) {
      if (myLabelConfiguration == null) {
        myLabelConfiguration = new LabelsConfiguration();
      }
      boolean theOldValue = myLabelConfiguration.isStarEnabled();
      myLabelConfiguration.setCustomLabelsEnabled(((LabelsConfiguration) anEvent.getNewValue()).isCustomLabelsEnabled());
      myLabelConfiguration.setStarEnabled(((LabelsConfiguration) anEvent.getNewValue()).isStarEnabled());
      if (isAttached()) {
        if (!theOldValue && myLabelConfiguration.isStarEnabled()) {
          // enable usage of the star.
          createWidgetsForItemsAndDisplay();
        } else if (theOldValue && !myLabelConfiguration.isStarEnabled()) {
          // disable usage of the star.
          myCaseStarWidgets.clear();
          createWidgetsForItemsAndDisplay();
        }
      }
    } else if (CaseDataSource.CONFIGURATION_PROPERTY.equals(anEvent.getPropertyName())) {
      if (myCaseConfiguration == null) {
        myCaseConfiguration = new CasesConfiguration();
      }
      if (anEvent.getNewValue() != null) {
        myCaseConfiguration.update((CasesConfiguration) anEvent.getNewValue());
      }
      updateLayout();
      createWidgetsForItemsAndDisplay();
    } else if (CaseItem.LAST_UPDATE_PROPERTY.equals(anEvent.getPropertyName())) {
        createWidgetsForItemsAndDisplay();
    } 
//    else if (CaseItem.STEPS_PROPERTY.equals(anEvent.getPropertyName())) {
//      createWidgetsForItemsAndDisplay();
//    } 

  }

  @Override
  public String getLocationLabel() {
    final LabelUUID theLabelId = ((CaseDataSource) myBonitaDataSource).getItemFilter().getLabel();
    if (theLabelId != null) {
      return LocaleUtil.translate(theLabelId);
    } else {
      final Category theCategory = ((CaseDataSource) myBonitaDataSource).getItemFilter().getCategory();
      if (theCategory != null) {
        return theCategory.getName();
      } else {
        return "";
      }
    }
  }

  @Override
  protected FlowPanel buildBottomNavBar() {
    final FlowPanel theResult = new FlowPanel();
    myBottomMenuBar = new CaseMenuBarWidget((CaseDataSource) myBonitaDataSource, (CaseSelection) myItemSelection, myLabelDataSource, false);
    theResult.add(myBottomMenuBar);
    return theResult;
  }

  @Override
  protected FlowPanel buildTopNavBar() {
    final FlowPanel theResult = new FlowPanel();
    myTopMenuBar = new CaseMenuBarWidget((CaseDataSource) myBonitaDataSource, (CaseSelection) myItemSelection, myLabelDataSource, false);
    theResult.add(myTopMenuBar);
    return theResult;
  }

  public void notifyItemClicked(CaseUUID anItem, final ClickEvent anEvent) {
    final Cell theCell = myInnerTable.getCellForEvent(anEvent);
    final int theCellIndex = theCell.getCellIndex();
    if (theCell != null && (theCellIndex != mySelectColumn) && (theCellIndex != myStarIconColumn) && (theCellIndex != myApplicationLinkColumn) && anItem != null) {
      if(((CaseDataSource) myBonitaDataSource).getItemFilter().searchInHistory()) {
        History.newItem(ViewToken.CaseEditor + ConsoleConstants.TOKEN_SEPARATOR + ConsoleConstants.HISTORY_TOKEN_PARAM_PREFIX + anItem);
      } else {
        History.newItem(ViewToken.CaseEditor + ConsoleConstants.TOKEN_SEPARATOR + ConsoleConstants.JOURNAL_TOKEN_PARAM_PREFIX + anItem);
      }
    }
  }

  protected void updateLayout() {
    if (myCaseConfiguration != null) {
      mySelectColumn = myCaseConfiguration.getColumnIndex(Columns.SELECT_COLUMN);
      myStarIconColumn = myCaseConfiguration.getColumnIndex(Columns.STAR_COLUMN);
      myCaseLabelsColumn = myCaseConfiguration.getColumnIndex(Columns.LABELS_COLUMN);
      myCaseCategoriesColumn = myCaseConfiguration.getColumnIndex(Columns.CATEGORIES_COLUMN);
      myCaseDescriptionColumn = myCaseConfiguration.getColumnIndex(Columns.DESCRIPTION_COLUMN);
      myStepStateColumn = myCaseConfiguration.getColumnIndex(Columns.STEP_STATE_COLUMN);
      myStepAssignColumn = myCaseConfiguration.getColumnIndex(Columns.STEP_ASSIGN_COLUMN);
      myStepPriorityColumn = myCaseConfiguration.getColumnIndex(Columns.STEP_PRIORITY_COLUMN);
      myStepNameColumn = myCaseConfiguration.getColumnIndex(Columns.STEP_NAME_COLUMN);
      myStepDescriptionColumn = myCaseConfiguration.getColumnIndex(Columns.STEP_DESCRIPTION_COLUMN);
      myDateAndTimeColumn = myCaseConfiguration.getColumnIndex(Columns.UPDATE_COLUMN);
      myApplicationLinkColumn = myCaseConfiguration.getColumnIndex(Columns.APPLICATION_COLUMN);
      myStretchedColumnIndex = myCaseConfiguration.getStretchedColumnIndex();
    } else {
      // use default layout.
      mySelectColumn = 0;
      myStarIconColumn = 1;
      myCaseLabelsColumn = 3;
      myCaseCategoriesColumn = 4;
      myCaseDescriptionColumn = 5;
      myStepStateColumn = 6;
      myStepAssignColumn = 7;
      myStepPriorityColumn = 8;
      myStepNameColumn = 9;
      myStepDescriptionColumn = 10;
      myDateAndTimeColumn = 11;
      myApplicationLinkColumn = 12;
      
      myStretchedColumnIndex = myStepDescriptionColumn;
    }

  }

  @Override
  protected ItemFilterEditor<CaseFilter> buildFilterEditor() {
    final CaseFilterEditor theEditor = new CaseFilterEditor(myMessageDataSource, myBonitaDataSource.getItemFilter(), myLabelDataSource);
    theEditor.addModelChangeListener(CaseFilterEditor.FILTER_UPDATED_PROPERTY, this);
    return theEditor;
  }

}
