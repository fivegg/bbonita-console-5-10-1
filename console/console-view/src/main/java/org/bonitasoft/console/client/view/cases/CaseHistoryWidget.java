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
package org.bonitasoft.console.client.view.cases;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.controller.UserRightsManager;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.view.I18NComposite;
import org.bonitasoft.console.client.view.steps.InstantiationStepEditor;
import org.bonitasoft.console.client.view.steps.StepEditor;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.forms.client.view.common.DOMUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseHistoryWidget extends I18NComposite implements ModelChangeListener {

  protected static final String CASE_HISTORY_STYLE = "bos_case_history";
  protected static final String DATE_STYLE = "bos_date";
  protected static final String DESCRIPTION_STYLE = "bos_description";
  protected static final String HISTORY_TABLE_STYLE = "bos_table";
  
  protected final FlowPanel myOuterPanel;
  protected final FlexTable myInnerTable;
  protected final CaseDataSource myCaseDataSource;
  protected final CaseItem myCase;
  protected final ProcessDataSource myProcessDataSource;

  protected final HashMap<StepUUID, Label> myItemDateWidgets = new HashMap<StepUUID, Label>();
  protected final HashMap<StepUUID, FlowPanel> myItemSummaryWidgets = new HashMap<StepUUID, FlowPanel>();
  protected final StepItemDataSource myStepItemDataSource;
  protected final FlowPanel myInstantiationFormViewer;

  protected static final int myDateCol = 0;
  protected static final int mySummaryCol = 1;
  

  /**
   * Default constructor.
   * 
   * @param aCaseDataSource
   * @param aCase
   */
  public CaseHistoryWidget(CaseDataSource aCaseDataSource, CaseItem aCase, ProcessDataSource aProcessDataSource, StepItemDataSource aStepItemDataSource) {
    myCaseDataSource = aCaseDataSource;
    myCase = aCase;
    myStepItemDataSource = aStepItemDataSource;
    aCase.addModelChangeListener(CaseItem.STEPS_PROPERTY, this);
    myProcessDataSource = aProcessDataSource;

    myOuterPanel = new FlowPanel();
    myOuterPanel.setStylePrimaryName(CASE_HISTORY_STYLE);
    myInnerTable = new FlexTable();
    myOuterPanel.add(myInnerTable);
    myInnerTable.setStylePrimaryName(HISTORY_TABLE_STYLE);
    initWidget(myOuterPanel);
    myInstantiationFormViewer = buildSummaryPanel(myCase);
    update();
  }

  protected void update() {
    
    int i = 0;
    myInnerTable.setHTML(i, myDateCol, DateTimeFormat.getFormat(constants.dateShortFormat()).format(myCase.getStartedDate()));
    myInnerTable.setWidget(i, mySummaryCol, myInstantiationFormViewer);
    myInnerTable.getFlexCellFormatter().setStyleName(i, myDateCol, DATE_STYLE);
    myInnerTable.getFlexCellFormatter().setStyleName(i, mySummaryCol, DESCRIPTION_STYLE);
    i++;
    final List<StepItem> theSteps = myCase.getSteps();
    Collections.reverse(theSteps);
    for (StepItem theItem : theSteps) {
      switch (theItem.getState()) {
      case ABORTED:
      case CANCELLED:
      case FINISHED:
      case SKIPPED:
        if (!myItemDateWidgets.containsKey(theItem.getUUID())) {
          createWidgetsForItem(theItem);
        }
        final Label theDateLabel = myItemDateWidgets.get(theItem.getUUID());
        final FlowPanel theSummaryPanel = myItemSummaryWidgets.get(theItem.getUUID());
        if (i >= myInnerTable.getRowCount()) {
          GWT.log("New row, inserting item: " + theItem.getLabel());
          myInnerTable.setWidget(i, myDateCol, theDateLabel);
          myInnerTable.setWidget(i, mySummaryCol, theSummaryPanel);
          myInnerTable.getFlexCellFormatter().setStyleName(i, myDateCol, DATE_STYLE);
          myInnerTable.getFlexCellFormatter().setStyleName(i, mySummaryCol, DESCRIPTION_STYLE);
        } else if (!theDateLabel.equals(myInnerTable.getWidget(i, myDateCol))) {
          GWT.log("Item not at the right place, inserting item: " + theItem.getLabel());
          myInnerTable.insertRow(i);
          myInnerTable.setWidget(i, myDateCol, theDateLabel);
          myInnerTable.setWidget(i, mySummaryCol, theSummaryPanel);
        } else {
          GWT.log("Widget already at the right place, skip insertion in table: " + theItem.getLabel());
        }
        i++;
        break;
      default:
        break;
      }
    }
  }

  protected void createWidgetsForItem(final StepItem anItem) {
    anItem.addModelChangeListener(StepItem.DATE_PROPERTY, new ModelChangeListener() {
      public void modelChange(ModelChangeEvent aEvt) {
        myItemDateWidgets.get(anItem.getUUID()).setText(DateTimeFormat.getFormat(constants.dateShortFormat()).format(((Date) aEvt.getNewValue())));
      }
    });
    myItemDateWidgets.put(anItem.getUUID(), new Label(DateTimeFormat.getFormat(constants.dateShortFormat()).format(anItem.getLastUpdateDate())));
    myItemSummaryWidgets.put(anItem.getUUID(), buildSummaryPanel(anItem));
  }

  protected FlowPanel buildSummaryPanel(final CaseItem anItem) {

      final FlowPanel theSummaryWrapper = new FlowPanel();
      final Label theSummary = new Label(patterns.caseStartedBy(myCase.getStartedBy().getValue()));
      theSummaryWrapper.add(theSummary);
      if (UserRightsManager.getInstance().isAllowed(RuleType.PROCESS_INSTANTIATION_DETAILS_VIEW, anItem.getProcessUUID().getValue())) {
        theSummary.setStylePrimaryName(CSSClassManager.POPUP_MENU_ENTRY);
        theSummary.addClickHandler(new ClickHandler() {
          private boolean formIsVisible = false;
          private InstantiationStepEditor myViewer;

          public void onClick(ClickEvent aEvent) {
            toggleSummaryView();
          }

          private void toggleSummaryView() {
            if (formIsVisible) {
              if (myViewer != null) {
                theSummaryWrapper.remove(myViewer);
                if (DOMUtils.getInstance().isInternetExplorer()) {
                    myViewer = null;
                }
                formIsVisible = false;
              }
            } else {
              if (myViewer == null) {
                myViewer = new InstantiationStepEditor(myStepItemDataSource, myCase, myCaseDataSource, myProcessDataSource);
                theSummaryWrapper.add(myViewer);
              } else {
                theSummaryWrapper.add(myViewer);
              }
              
              formIsVisible = true;
            }

          }
        });
      }
      return theSummaryWrapper;
    }
  
  protected FlowPanel buildSummaryPanel(final StepItem anItem) {
    final String theExecutionSummary;
    if (anItem.getExecutionSummary() != null && anItem.getExecutionSummary().length() > 0) {
      theExecutionSummary = anItem.getExecutionSummary();
    } else {
      switch (anItem.getState()) {
      case ABORTED:
        theExecutionSummary = patterns.stepExecutionSummaryAborted(anItem.getLabel());
        break;
      case CANCELLED:
        theExecutionSummary = patterns.stepExecutionSummaryCancelled(anItem.getLabel());
        break;
      case SKIPPED:
          theExecutionSummary = patterns.stepExecutionSummarySkipped(anItem.getLabel());
          break;
      default:
        theExecutionSummary = patterns.stepExecutionSummaryFinished(anItem.getLabel());
        break;
      }
    }

    final FlowPanel theSummaryWrapper = new FlowPanel();
    final Label theSummary = new Label(theExecutionSummary);
    theSummaryWrapper.add(theSummary);
    if (UserRightsManager.getInstance().isAllowed(RuleType.ACTIVITY_DETAILS_READ, anItem.getUUID().getStepDefinitionUUID())) {
      theSummary.setStylePrimaryName(CSSClassManager.POPUP_MENU_ENTRY);
      theSummary.addClickHandler(new ClickHandler() {
        private boolean formIsVisible = false;
        private StepEditor myStepEditor;

        public void onClick(ClickEvent aEvent) {
          toggleSummaryView();
        }

        private void toggleSummaryView() {
          if (formIsVisible) {
            if (myStepEditor != null) {
              theSummaryWrapper.remove(myStepEditor);
              if (DOMUtils.getInstance().isInternetExplorer()) {
                  myStepEditor = null;
              }
              formIsVisible = false;
            }
          } else {
            if (myStepEditor == null) {
              myStepEditor = new StepEditor(myStepItemDataSource, anItem, true, myCaseDataSource, myProcessDataSource, (UserDataSource) null);
              theSummaryWrapper.add(myStepEditor);
            } else {
              theSummaryWrapper.add(myStepEditor);
            }
            
            formIsVisible = true;
          }

        }
      });
    }
    return theSummaryWrapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
   * .bonitasoft.console.client.events.ModelChangeEvent)
   */
  public void modelChange(ModelChangeEvent aEvt) {
    update();
  }
}
