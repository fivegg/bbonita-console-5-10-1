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

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.cases.CasesConfiguration.Columns;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.view.ItemFilterEditor;
import org.bonitasoft.console.client.view.steps.StepRedirectWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
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
public class AdminCaseList extends CaseList {

  /**
   * Override the menu bars attributes definition.
   */
  protected AdminCaseMenuBarWidget myTopMenuBar;
  protected AdminCaseMenuBarWidget myBottomMenuBar;

  protected int myCaseStateColumn = -1;
  protected final HashMap<CaseItem, Image> myCaseState = new HashMap<CaseItem, Image>();

  /**
   * 
   * Default constructor.
   * 
   * @param aCaseSelection
   * @param aCaseDataSource
   * @param aLabelDataSource
   * @param aCategoryDataSource
   */
  public AdminCaseList(final MessageDataSource aMessageDataSource, CaseSelection aCaseSelection, CaseDataSource aCaseDataSource, ProcessDataSource aProcessDataSource,
      LabelDataSource aLabelDataSource, CategoryDataSource aCategoryDataSource) {
    super(aMessageDataSource, aCaseSelection, aCaseDataSource, aProcessDataSource, aLabelDataSource, aCategoryDataSource);
    myLabelDataSource.addModelChangeListener(LabelDataSource.TOTAL_CASE_NUMBER_PROPERTY, this);
  }

  @Override
  protected FlowPanel buildBottomNavBar() {
    FlowPanel theResult = new FlowPanel();
    myBottomMenuBar = new AdminCaseMenuBarWidget((CaseDataSource) myBonitaDataSource, (CaseSelection) myItemSelection, false, myProcessDataSource, myMessageDataSource);
    theResult.add(myBottomMenuBar);

    return theResult;
  }

  @Override
  protected FlowPanel buildTopNavBar() {
    FlowPanel theResult = new FlowPanel();
    myTopMenuBar = new AdminCaseMenuBarWidget((CaseDataSource) myBonitaDataSource, (CaseSelection) myItemSelection, false, myProcessDataSource, myMessageDataSource);
    theResult.add(myTopMenuBar);

    return theResult;
  }

  @Override
  protected void createWidgetsForItemsAndDisplay() {
    myVisibleItems = ((CaseDataSource) myBonitaDataSource).getVisibleItems();

    if (myVisibleItems != null) {
      hideLoading();
      myInnerTable.removeStyleName(LOADING_STYLE);
      for (CaseUUID theCaseUUID : myVisibleItems) {
        CaseItem theCase = ((CaseDataSource) myBonitaDataSource).getItem(theCaseUUID);
        if (!myCaseLabelViewers.containsKey(theCase)) {
          theCase.addModelChangeListener(CaseItem.LABELS_PROPERTY, this);
          theCase.addModelChangeListener(CaseItem.STATE_PROPERTY, this);
          // createWidgetsForCase(theCase);
        }
        createWidgetsForCase(theCase);
      }
      // Update the UI.
      update(myVisibleItems);

    } else {
      displayLoading();
      myInnerTable.addStyleName(LOADING_STYLE);
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
        // Do not display the star icon.
        myInnerTable.setHTML(theCurrentRowIndex, myStarIconColumn, NBSP);
      }
      if (myCaseStateColumn > -1) {
        // Do not display the star icon.
        myInnerTable.setWidget(theCurrentRowIndex, myCaseStateColumn, myCaseState.get(theCaseItem));
      }
      if (myCaseCategoriesColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myCaseCategoriesColumn, myCaseCategoryViewers.get(theCaseItem));
      }
      if (myStepNameColumn > -1) {
        myInnerTable.setHTML(theCurrentRowIndex, myStepNameColumn, myCaseStepName.get(theCaseItem));
      }
      if (myCaseDescriptionColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myCaseDescriptionColumn, myCaseDescriptionWidgets.get(theCaseItem));
      }
      if (myStepStateColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myStepStateColumn, myCaseStepState.get(theCaseItem));
      }
      if (myStepAssignColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myStepAssignColumn, myCaseStepAssign.get(theCaseItem));
      }
      if (myStepPriorityColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myStepPriorityColumn, myCaseStepPriority.get(theCaseItem));
      }
      if (myStepDescriptionColumn > -1) {
        myInnerTable.setHTML(theCurrentRowIndex, myStepDescriptionColumn, myCaseStepDescription.get(theCaseItem));
      }
      if (myDateAndTimeColumn > -1) {
        myInnerTable.setWidget(theCurrentRowIndex, myDateAndTimeColumn, new Label(DateTimeFormat.getFormat(constants.dateShortFormat()).format(theCaseItem.getLastUpdateDate())));
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
        if (nbOfCurrentStep == 1 && currentStep != null) {
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.view.cases.CaseList#createWidgetsForCase(
   * org.bonitasoft.console.client.cases.CaseItem)
   */
  @Override
  protected void createWidgetsForCase(CaseItem aCase) {
    super.createWidgetsForCase(aCase);
    if (!myCaseState.containsKey(aCase) && (myCaseConfiguration == null || (myCaseConfiguration != null && myCaseConfiguration.getColumnIndex(Columns.STEP_STATE_COLUMN) > -1))) {
      final Image theCaseStateIcon;
      theCaseStateIcon = new Image(PICTURE_PLACE_HOLDER);
      myCaseState.put(aCase, theCaseStateIcon);
    }
    updateStateOfCase(aCase);
  }

  @Override
  public String getLocationLabel() {
    return constants.adminCaseList();
  }

  /*
   * (non-Javadoc)
   * 
   * @seejava.beans.ModelChangeListener#propertyChange(java.beans.
   * PropertyChangeEvent)
   */
  public void modelChange(ModelChangeEvent anEvent) {
    if (isAttached()) {
      if (CaseItem.STATE_PROPERTY.equals(anEvent.getPropertyName())) {
        updateStateOfCase((CaseItem) anEvent.getSource());
      } else if (LabelDataSource.TOTAL_CASE_NUMBER_PROPERTY.equals(anEvent.getPropertyName())) {
        updateListSize(((CaseDataSource) myBonitaDataSource).getVisibleItems());
      } else {
        super.modelChange(anEvent);
      }
    } else {
      super.modelChange(anEvent);
    }
  }

  protected void updateStateOfCase(CaseItem aCaseItem) {
    final Image theCaseStateIcon = myCaseState.get(aCaseItem);
    if (theCaseStateIcon != null) {
      theCaseStateIcon.setTitle(patterns.caseState(aCaseItem.getState().name().toLowerCase()));
      theCaseStateIcon.setStyleName(CSSClassManager.getCaseStateIconStyle(aCaseItem.getState()));
    }
  }

  protected void updateLayout() {
    super.updateLayout();
    if (myCaseConfiguration != null) {
      myCaseStateColumn = myCaseConfiguration.getColumnIndex(Columns.CASE_STATE_COLUMN);
    } else {
      // use default layout.
      myCaseStateColumn = 1;
    }
  }

  @Override
  public void notifyItemClicked(CaseUUID anItem, final ClickEvent anEvent) {
    final Cell theCell = myInnerTable.getCellForEvent(anEvent);
    final int theCellIndex = theCell.getCellIndex();
    if ((theCell != null) && (theCellIndex != mySelectColumn) && (theCellIndex != myApplicationLinkColumn) &&(anItem != null)) {
      if(((CaseDataSource) myBonitaDataSource).getItemFilter().searchInHistory()) {
        History.newItem(ViewToken.AdminCaseEditor + ConsoleConstants.TOKEN_SEPARATOR + ConsoleConstants.HISTORY_TOKEN_PARAM_PREFIX + anItem);
      } else {
        History.newItem(ViewToken.AdminCaseEditor + ConsoleConstants.TOKEN_SEPARATOR + ConsoleConstants.JOURNAL_TOKEN_PARAM_PREFIX + anItem);
      }
    }
  }

  @Override
  protected ItemFilterEditor<CaseFilter> buildFilterEditor() {
    final AdminCaseFilterEditor theEditor = new AdminCaseFilterEditor(myMessageDataSource, myBonitaDataSource.getItemFilter(), myLabelDataSource);
    theEditor.addModelChangeListener(CaseFilterEditor.FILTER_UPDATED_PROPERTY, this);
    return theEditor;
  }
}
