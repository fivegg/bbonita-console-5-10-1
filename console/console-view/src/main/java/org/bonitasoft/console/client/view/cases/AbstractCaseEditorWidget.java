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
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.UserRightsManager;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.StarWidget;
import org.bonitasoft.console.client.view.categories.CategoryViewer;
import org.bonitasoft.console.client.view.labels.LabelViewer;
import org.bonitasoft.console.client.view.steps.InstantiationStepEditor;
import org.bonitasoft.console.client.view.steps.StepEditor;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.forms.client.view.common.DOMUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget is able to edit a case and display all the data related to a particular case.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public abstract class AbstractCaseEditorWidget extends BonitaPanel implements ModelChangeListener {

    public enum Mode {
        OVERVIEW, DETAILS;
    }

    protected static final String MODE_SELECTOR = "AbstractCaseEditorWidget_mode_selector";
    protected static final String CASE_EDITOR_STYLE = "case_editor";

    protected static final String BOS_OPENSTEPS_PANEL_STYLE = "bos_opensteps_panel";
    protected static final String BOS_COMMENT_PANEL_STYLE = "bos_comment_panel";
    protected static final String BOS_OVERVIEW_PANEL_STYLE = "bos_overview_panel";
    protected static final String BOS_INNER_PANEL_STYLE = "bos_inner_panel";

    protected static final String COMMENT_TABLE_STYLE = "bos_comment_table";
    protected static final String COMMENT_CONTENT_STYLE = "bos_comment_content";
    protected static final String COMMENT_DATE_STYLE = "bos_comment_date";
    protected static final String COMMENT_AUTHOR_STYLE = "bos_comment_author";
    protected static final String COMMENT_ACTION_STYLE = "bos_comment_action";
    
    protected static final String EVEN_STYLE = "bos_row_even";
    protected static final String ODD_STYLE = "bos_row_odd";

    protected static final int ACTION_COL = 2;
    protected static final int CONTENT_COL = 1;
    protected static final int AUTHOR_COL = 1;
    protected static final int DATE_COL = 0;
    protected static final int NEW_CONTENT_COL = 0;

    protected CaseItem myCase;
    protected final HorizontalPanel myTopNavBar = new HorizontalPanel();
    protected final HorizontalPanel myBottomNavBar = new HorizontalPanel();
    protected final HorizontalPanel myHeader = new HorizontalPanel();
    protected final FlowPanel myOuterPanel = new FlowPanel();
    protected final FlowPanel myInnerPanel = new FlowPanel();
    protected final FlowPanel myOverviewPanel = new FlowPanel();
    protected final FlowPanel myOverviewInnerPanel = new FlowPanel();
    protected final FlowPanel myOpenStepsPanel = new FlowPanel();
    protected final FlowPanel myOpenStepsInnerPanel = new FlowPanel();
    protected final FlowPanel myCommentsPanel = new FlowPanel();
    protected final FlowPanel myCommentsInnerPanel = new FlowPanel();

    protected final StepItemDataSource myStepDataSource;
    protected final LabelDataSource myLabelDataSource;
    protected final CategoryDataSource myCategoryDataSource;
    protected final CaseDataSource myCaseDataSource;
    protected final CaseSelection myCaseSelection;
    protected final Label myNothingToDisplayLabel;
    protected final ProcessDataSource myProcessDataSource;
    protected final UserDataSource myUserDataSource;

    protected final HashMap<StepUUID, StepEditor> myStepEditors = new HashMap<StepUUID, StepEditor>();
    protected InstantiationStepEditor myStepInstantiationEditor;
    protected CaseRecapViewerWidget myCaseRecapWidget;
    protected Panel myModeSelector;
    protected LabelsConfiguration myConfiguration;

    protected Mode myMode;

    protected final int myColumnNumber = 4;

    protected final int myStepEditorsColumn = 0;

    protected final FlowPanel myStarWidgetContainer = new FlowPanel();

    protected CaseHistoryWidget myCaseHistoryWidget;

    private FlexTable myCommentsTable;
    private RadioButton myRecapModeRB;
    private RadioButton myDetailModeRB;

    /**
     * Default constructor.
     * 
     * @param aLabelDataSource
     * @param aStepDataSource
     * @param aUserDataSource
     * @param aCategoryDataSource
     * @param aDataModel
     */
    public AbstractCaseEditorWidget(final CaseDataSource aCaseDataSource, final CaseSelection aCaseSelection, final LabelDataSource aLabelDataSource, final StepItemDataSource aStepDataSource, final ProcessDataSource aProcessDataSource,
            final UserDataSource aUserDataSource, final CategoryDataSource aCategoryDataSource) {
        super();
        myCaseDataSource = aCaseDataSource;
        myCaseSelection = aCaseSelection;
        myStepDataSource = aStepDataSource;
        myLabelDataSource = aLabelDataSource;
        myProcessDataSource = aProcessDataSource;
        myUserDataSource = aUserDataSource;
        myCategoryDataSource = aCategoryDataSource;

        myCaseDataSource.addModelChangeListener(CaseDataSource.CASE_LIST_PROPERTY, this);
        myCaseDataSource.addModelChangeListener(CaseDataSource.TO_CURRENT_POSITION, this);
        myCaseDataSource.addModelChangeListener(CaseDataSource.COMMENTS_PROPERTY, this);
        myLabelDataSource.addModelChangeListener(LabelDataSource.LABEL_CASE_ASSOCIATION_PROPERTY, this);
        myLabelDataSource.getConfiguration(new AsyncHandler<LabelsConfiguration>() {

            public void handleFailure(Throwable aT) {
                myLabelDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, AbstractCaseEditorWidget.this);
            }

            public void handleSuccess(LabelsConfiguration aResult) {
                myConfiguration = new LabelsConfiguration();
                myConfiguration.setCustomLabelsEnabled(aResult.isCustomLabelsEnabled());
                myConfiguration.setStarEnabled(aResult.isStarEnabled());
                myLabelDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, AbstractCaseEditorWidget.this);
                buildHeader();
                myProcessDataSource.getItem(myCase.getProcessUUID(), new AsyncHandler<BonitaProcess>() {
                    public void handleFailure(Throwable aT) {
                        updatedHeader(null);
                    }

                    public void handleSuccess(BonitaProcess aProcess) {
                        updatedHeader(aProcess);
                    }
                });

                // even if it is the same case, update the UI, as some values may
                // have changed.
                update();
            }
        });

        myInnerPanel.setStylePrimaryName(BOS_INNER_PANEL_STYLE);
        myOpenStepsPanel.setStylePrimaryName(BOS_OPENSTEPS_PANEL_STYLE);
        myOpenStepsInnerPanel.setStylePrimaryName(BOS_INNER_PANEL_STYLE);

        myCommentsPanel.setStylePrimaryName(BOS_COMMENT_PANEL_STYLE);
        myCommentsInnerPanel.setStylePrimaryName(BOS_INNER_PANEL_STYLE);
        myOverviewPanel.setStylePrimaryName(BOS_OVERVIEW_PANEL_STYLE);
        myOverviewInnerPanel.setStylePrimaryName(BOS_INNER_PANEL_STYLE);

        myInnerPanel.add(myCommentsPanel);
        myInnerPanel.add(myOverviewPanel);

        myOuterPanel.setStylePrimaryName(CASE_EDITOR_STYLE);
        // Put the top menu.
        myOuterPanel.add(myTopNavBar);
        myOuterPanel.add(myHeader);
        myOuterPanel.add(myInnerPanel);
        // Put the bottom menu.
        myOuterPanel.add(myBottomNavBar);

        initOverviewPanel();
        initOpenStepsPanel();
        initSocialCollaborationPanel();

        // myTopNavBar.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        myTopNavBar.setStylePrimaryName("case_list_navbar");

        // myBottomNavBar.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        myBottomNavBar.setStyleName("case_list_navbar");

        myNothingToDisplayLabel = new Label(constants.noStepsToDisplay());
        myNothingToDisplayLabel.setStyleName(CSSClassManager.INFORMATIVE_TEXT);

        // By default use the recap mode.
        myMode = Mode.DETAILS;

        this.initWidget(myOuterPanel);
    }

    /**
     * Build the specific part of the UI.
     */
    protected void initView() {
        buildBottomNavBar();
        buildTopNavBar();
    }

    protected abstract void buildBottomNavBar();

    protected abstract void buildTopNavBar();

    /**
     * @param aCase
     */
    public void setCaseToDisplay(CaseItem aCase) {
        if (aCase != null) {
            if (!aCase.equals(myCase)) {
                if (myCase != null) {
                    myCase.removeModelChangeListener(CaseItem.STEPS_PROPERTY, this);
                }
                // Update the reference of the Case currently edited.
                myCase = aCase;
                myCase.addModelChangeListener(CaseItem.STEPS_PROPERTY, this);
            }

            int i = 0;
            boolean foundOpenStep = false;
            final List<StepItem> theSteps = myCase.getSteps();
            final int theNumberOfSteps = theSteps.size();
            StepItem theStep;
            while (i < theNumberOfSteps && !foundOpenStep) {
                theStep = theSteps.get(i);
                foundOpenStep = (theStep.getState() == StepState.READY || theStep.getState() == StepState.EXECUTING || theStep.getState() == StepState.SUSPENDED || theStep.getState() == StepState.FAILED);
                i++;
            }
            if (foundOpenStep) {
                myInnerPanel.insert(myOpenStepsPanel, 0);
            } else {
                myInnerPanel.remove(myOpenStepsPanel);
            }

            if (myConfiguration != null) {
                buildHeader();
                myProcessDataSource.getItem(myCase.getProcessUUID(), new AsyncHandler<BonitaProcess>() {
                    public void handleFailure(Throwable aT) {
                        updatedHeader(null);
                    }

                    public void handleSuccess(BonitaProcess aProcess) {
                        updatedHeader(aProcess);
                    }
                });

                // even if it is the same case, update the UI, as some values may
                // have changed.
                update();
            }

        } else {
            Window.alert("Error: trying to display a case with a null reference!");
        }
    }

    /**
     * Update the UI.
     */
    protected void update() {
        if (myMode != null) {
            switch (myMode) {
            case OVERVIEW:
                toggleToOverviewMode();
                break;
            case DETAILS:
                toggleToStepDetailsMode();
                break;
            default:
                Window.alert("Mode not supported: " + myMode.name());
                break;
            }
        } else {
            GWT.log("Mode not set properly", new IllegalArgumentException());
        }

    }

    protected void buildHeader() {
        myHeader.setStylePrimaryName("case_editor_header");
        myHeader.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        myHeader.add(myStarWidgetContainer);
        if (myConfiguration == null || myConfiguration.isStarEnabled()) {
            myStarWidgetContainer.add(new StarWidget(myLabelDataSource, myCase));
        }
        final HorizontalPanel theWrapper = new HorizontalPanel();
        theWrapper.add(new LabelViewer(myLabelDataSource, myCase, true));
        theWrapper.add(new CategoryViewer(myCase.getProcessUUID(), myProcessDataSource, myCategoryDataSource));
        myHeader.add(theWrapper);
    }

    protected void updatedHeader(BonitaProcess aProcess) {
        if (aProcess != null) {
            if (myHeader != null) {
                final Label theCaseDescription = new Label(patterns.caseDescription(aProcess.getDisplayName(), myCase.getCaseInstanceNumber()));
                theCaseDescription.setStylePrimaryName("bos_case_description");
                myHeader.add(theCaseDescription);
            }
        }
    }

    /**
     * @return
     */
    private Widget buildModeChooser() {
        if (myModeSelector == null) {
            myModeSelector = new FlowPanel();
            myRecapModeRB = new RadioButton(MODE_SELECTOR, constants.caseRecapModeSelector());
            myRecapModeRB.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                public void onValueChange(ValueChangeEvent<Boolean> aEvent) {
                    if (aEvent.getValue()) {
                        myMode = Mode.OVERVIEW;
                        update();
                    }
                }
            });

            myDetailModeRB = new RadioButton(MODE_SELECTOR, constants.caseStepModeSelector());
            myDetailModeRB.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                public void onValueChange(ValueChangeEvent<Boolean> aEvent) {
                    if (aEvent.getValue()) {
                        myMode = Mode.DETAILS;
                        update();
                    }
                }
            });
            myModeSelector.add(myDetailModeRB);
            myModeSelector.add(myRecapModeRB);
            switch (myMode) {
            case OVERVIEW:
                myRecapModeRB.setValue(true, false);
                break;
            case DETAILS:
                myDetailModeRB.setValue(true, false);
                break;
            default:
                Window.alert("AbstractCaseEditor.buildModeChooser(): Mode not yet supported!");
                break;
            }
        }
        myModeSelector.setStylePrimaryName("bos_mode_chooser");
        return myModeSelector;
    }

    protected void updateOpenStepsPanel() {
        List<StepItem> theSteps = myCase.getSteps();

        StepEditor theStepEditor;
        StepState theStepState;
        StepItem theStep;
        if (theSteps.size() > 0) {
            for (int i = 0; i < theSteps.size(); i++) {
                theStep = theSteps.get(i);
                theStepState = theStep.getState();
                if (theStepState == StepState.READY || theStepState == StepState.SUSPENDED || theStepState == StepState.FAILED) {
                    if (!myStepEditors.containsKey(theStep.getUUID())) {
                        // The first step is open. All the others are closed.
                        theStepEditor = buildStepEditor(theStep, (i == 0));
                        myStepEditors.put(theStep.getUUID(), theStepEditor);
                    } else {
                        theStepEditor = myStepEditors.get(theStep.getUUID());
                    }
                    if (myOpenStepsInnerPanel.getWidgetIndex(theStepEditor) == -1) {
                        myOpenStepsInnerPanel.add(theStepEditor);
                    }

                    theStep.addModelChangeListener(StepItem.STATE_PROPERTY, new ModelChangeListener() {

                        public void modelChange(ModelChangeEvent aEvt) {
                            final StepItem theStep = ((StepItem) aEvt.getSource());
                            StepState theNewState = (StepState) aEvt.getNewValue();
                            StepUUID theStepUUID = theStep.getUUID();
                            Widget theStepEditor;
                            if (theNewState != StepState.READY && theNewState != StepState.SUSPENDED && theNewState != StepState.FAILED) {
                                // The step should no longer be displayed.
                                theStepEditor = myStepEditors.remove(theStepUUID);
                                if (theStepEditor != null) {
                                    myOpenStepsInnerPanel.remove(theStepEditor);
                                }
                                // Remove event listening.
                                theStep.removeModelChangeListener(StepItem.STATE_PROPERTY, this);
                                try {
                                    if (myCaseRecapWidget != null) {
                                        myCaseRecapWidget.refresh();
                                    }
                                } catch (Exception e) {
                                    Window.alert("Unable to refresh the case recap!");
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    protected abstract StepEditor buildStepEditor(StepItem theStepItem, boolean mustBeVisible);

    protected void toggleToOverviewMode() {
        updateOpenStepsPanel();
        updateCommentsPanel();
        updateRecapPanel();
        if (myOverviewInnerPanel.getWidgetIndex(myCaseRecapWidget) == -1) {
            myOverviewInnerPanel.add(myCaseRecapWidget);
            if (myCaseHistoryWidget != null) {
                myOverviewInnerPanel.remove(myCaseHistoryWidget);
                if (DOMUtils.getInstance().isInternetExplorer()) {
                    myCaseHistoryWidget = null;
                }
            }
        }

        // if(UserRightsManager.getInstance().isAllowed(RuleType.,
        // myCase.getUUID().getProcessDefinition()))
        Widget theModeChooser = buildModeChooser();
        if (!theModeChooser.isAttached()) {
            myOverviewInnerPanel.insert(theModeChooser, myOverviewInnerPanel.getWidgetIndex(myCaseRecapWidget));
        }
        // }

    }

    protected void updateRecapPanel() {
        if (myCaseRecapWidget == null) {
            myCaseRecapWidget = new CaseRecapViewerWidget(myStepDataSource, myCase, myCaseDataSource, myProcessDataSource);
        } else {
            myCaseRecapWidget.refresh();
        }
    }

    protected void toggleToStepDetailsMode() {
        updateOpenStepsPanel();
        updateCommentsPanel();
        updateHistoryPanel();
        if (myCaseRecapWidget != null) {
            myOverviewInnerPanel.remove(myCaseRecapWidget);
            if (DOMUtils.getInstance().isInternetExplorer()) {
                myCaseRecapWidget = null;
            }
        }
        if (!myCaseHistoryWidget.isAttached()) {
            myOverviewInnerPanel.add(myCaseHistoryWidget);
        }
        // if(UserRightsManager.getInstance().isAllowed(RuleType.,
        // myCase.getUUID().getProcessDefinition()))
        Widget theModeChooser = buildModeChooser();
        if (!theModeChooser.isAttached()) {
            myOverviewInnerPanel.insert(theModeChooser, myOverviewInnerPanel.getWidgetIndex(myCaseHistoryWidget));
        }
        // }

    }

    protected void updateCommentsPanel() {
        if (myCommentsTable == null) {
            myCommentsTable = new FlexTable();
            myCommentsTable.setCellSpacing(0);
            myCommentsTable.setCellPadding(0);
            myCommentsTable.setStylePrimaryName(COMMENT_TABLE_STYLE);
        }
        if (myCommentsInnerPanel.getWidgetIndex(myCommentsTable) == -1) {
            myCommentsInnerPanel.add(myCommentsTable);
        }
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            public void execute() {
                myCaseDataSource.getCaseCommentFeed(myCase.getUUID(), new AsyncHandler<List<CommentItem>>() {
                    public void handleFailure(Throwable aT) {
                        fillinCommentsTable(null);

                    }

                    public void handleSuccess(List<CommentItem> aResult) {
                        fillinCommentsTable(aResult);
                    }
                });

            }
        });

    }

    protected void fillinCommentsTable(List<CommentItem> aCommentList) {
        int i = 0;
        if (aCommentList != null && !aCommentList.isEmpty()) {
            for (CommentItem theCommentItem : aCommentList) {
                myCommentsTable.setHTML(i, DATE_COL, LocaleUtil.shortDateFormat(theCommentItem.getDate()));
                myCommentsTable.setHTML(i, AUTHOR_COL, theCommentItem.getUserUUID().getValue());
                /* CSS Style */
                myCommentsTable.getFlexCellFormatter().setStylePrimaryName(i, DATE_COL, COMMENT_DATE_STYLE);
                myCommentsTable.getFlexCellFormatter().setStylePrimaryName(i, AUTHOR_COL, COMMENT_AUTHOR_STYLE);
                myCommentsTable.getFlexCellFormatter().setColSpan(i, DATE_COL, 1);
                myCommentsTable.getFlexCellFormatter().setColSpan(i, AUTHOR_COL, 1);
                if (i % 2 == 0) {
                    myCommentsTable.getRowFormatter().setStyleName(i, EVEN_STYLE);
                } else {
                    myCommentsTable.getRowFormatter().setStyleName(i, ODD_STYLE);
                }
                i++;
                String theComment = theCommentItem.getContent();
                theComment = toMultiLine(theComment, myCommentsTable.getOffsetWidth()/10);
                myCommentsTable.setText(i, CONTENT_COL, theComment);
                myCommentsTable.setHTML(i, ACTION_COL, "");
                /* CSS Style */
                myCommentsTable.getFlexCellFormatter().setStylePrimaryName(i, CONTENT_COL, COMMENT_CONTENT_STYLE);
                myCommentsTable.getFlexCellFormatter().setStylePrimaryName(i, ACTION_COL, COMMENT_ACTION_STYLE);
                myCommentsTable.getFlexCellFormatter().setColSpan(i, CONTENT_COL, 2);
                myCommentsTable.getFlexCellFormatter().setColSpan(i, ACTION_COL, 1);
                if (i % 2 == 0) {
                    myCommentsTable.getRowFormatter().setStyleName(i, EVEN_STYLE);
                } else {
                    myCommentsTable.getRowFormatter().setStyleName(i, ODD_STYLE);
                }
                i++;
            }
        } else {
            //Archived cases without comment shows information
            if (myCase.isArchived()) {
                myCommentsTable.setHTML(1, CONTENT_COL, messages.noCommentsAttached());
                myCommentsTable.getRowFormatter().setStyleName(1, ODD_STYLE);
            } else {
                myCommentsTable.clear();
            }
        }
        // Archived cases have to be considered as readonly.
        if (!myCase.isArchived()) {
            if (UserRightsManager.getInstance().isAllowed(RuleType.PROCESS_ADD_COMMENT, myCase.getProcessUUID().getValue())) {
                myCommentsTable.setWidget(i, NEW_CONTENT_COL, new CaseCommentEditorWidget(myCaseDataSource, myCase));
                myCommentsTable.getFlexCellFormatter().setColSpan(i, NEW_CONTENT_COL, 3);
                myCommentsPanel.setVisible(true);
            } else {
                myCommentsPanel.setVisible(false);
            }
        }
    }

    /**
     * line feed the comment strings
     * 
     * @param str
     * @param len
     * @return sb.toString()
     */
    public static String toMultiLine(String str, int len) {
        char[] chs = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0, sum = 0; i < chs.length; i++) {
            sum += chs[i] < 0xff ? 1 : 2;
            sb.append(chs[i]);
            if (sum >= len || chs[i] == 10 || chs[i] == 13) {
                sum = 0;
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    protected void updateHistoryPanel() {
        if (myCaseHistoryWidget == null) {
            myCaseHistoryWidget = new CaseHistoryWidget(myCaseDataSource, myCase, myProcessDataSource, myStepDataSource);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.beans.ModelChangeListener#propertyChange(java.beans. PropertyChangeEvent)
     */
    @SuppressWarnings("unchecked")
    public void modelChange(ModelChangeEvent anEvent) {
        if (isAttached()) {
            if (CaseDataSource.CASE_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
                // The list of cases has been updated.
                List<CaseItem> theVisibleCases = (List<CaseItem>) anEvent.getNewValue();
                if (myCase != null && theVisibleCases != null && !theVisibleCases.contains(myCase) && this.isAttached() && myCaseDataSource.getItemFilter().getLabel() != null) {
                    // The edited case has been removed
                    redirectToCurrentPosition();
                }
            } else if (CaseDataSource.TO_CURRENT_POSITION.equals(anEvent.getPropertyName())) {    
            	 redirectToCurrentPosition();
            } else if (CaseItem.STEPS_PROPERTY.equals(anEvent.getPropertyName())) {
                // The list of steps of the current case has changed.
                if (((List<StepItem>) anEvent.getOldValue()).size() >= ((List<StepItem>) anEvent.getNewValue()).size()) {
                    // Either the case list has been reloaded in "light" mode
                    // or some steps have been re-assigned
                    myCaseDataSource.getItem(myCase.getUUID(), null);
                } else {
                    // A new step has been added.
                    // A step's form has been submitted.
                    update();
                }
            } else if (CaseDataSource.COMMENTS_PROPERTY.equals(anEvent.getPropertyName())) {
                if (myCase.getUUID().equals(anEvent.getSource())) {
                    fillinCommentsTable((List<CommentItem>) anEvent.getNewValue());
                }
            } else if (LabelDataSource.LABEL_CASE_ASSOCIATION_PROPERTY.equals(anEvent.getPropertyName())) {
                // The cases have been updated using the label data source. Sync
                // data.
                List<CaseUUID> theUpadtedCases = new ArrayList<CaseUUID>();
                if (anEvent.getNewValue() != null && anEvent.getNewValue() instanceof Set) {
                    theUpadtedCases.addAll((Set<CaseUUID>) anEvent.getNewValue());
                }
                if (theUpadtedCases.size() == 1) {
                    // force to load the case with all its steps
                    myCaseDataSource.getItem(theUpadtedCases.iterator().next(), null);
                } else {
                    GWT.log("Size of selection should be equals to 1", new IllegalArgumentException());
                }

            }
        }

        // update even if it is not attached.
        if (LabelDataSource.CONFIGURATION_PROPERTY.equals(anEvent.getPropertyName())) {
            if (myConfiguration == null) {
                myConfiguration = new LabelsConfiguration();
            }
            boolean theOldValue = myConfiguration.isStarEnabled();
            myConfiguration.setCustomLabelsEnabled(((LabelsConfiguration) anEvent.getNewValue()).isCustomLabelsEnabled());
            myConfiguration.setStarEnabled(((LabelsConfiguration) anEvent.getNewValue()).isStarEnabled());
            if (isAttached()) {
                if (!theOldValue && myConfiguration.isStarEnabled()) {
                    // enable usage of the star.
                    myStarWidgetContainer.add(new StarWidget(myLabelDataSource, myCase));
                } else if (theOldValue && !myConfiguration.isStarEnabled()) {
                    // disable usage of the star.
                    myStarWidgetContainer.clear();
                }
            }
        }
    }

    abstract void redirectToCurrentPosition();

    protected void initOverviewPanel() {
        final Label theCaseOverviewTitle = new Label(constants.caseOverviewTitle());
        theCaseOverviewTitle.setStyleName(CSSClassManager.TITLE_STYLE);
        myOverviewPanel.add(theCaseOverviewTitle);
        myOverviewPanel.add(myOverviewInnerPanel);
    }

    protected void initOpenStepsPanel() {
        // final Label theOpenStepsTitle = new
        // Label(constants.openStepsTitle());
        // theOpenStepsTitle.setStyleName(CSSClassManager.TITLE_STYLE);
        // myOpenStepsPanel.add(theOpenStepsTitle);
        myOpenStepsPanel.add(myOpenStepsInnerPanel);
    }

    protected void initSocialCollaborationPanel() {
        final Label theSocialCollaborationTitle = new Label(constants.commentFeedTitle());
        theSocialCollaborationTitle.setStyleName(CSSClassManager.TITLE_STYLE);
        myCommentsPanel.add(theSocialCollaborationTitle);
        myCommentsPanel.add(myCommentsInnerPanel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Composite#onDetach()
     */
    @Override
    protected void onDetach() {
        super.onDetach();
        cleanState();
    }

    protected void cleanState() {
        myStepEditors.clear();
        myStepInstantiationEditor = null;
        myCaseRecapWidget = null;
        myCaseHistoryWidget = null;
        myHeader.clear();
        myStarWidgetContainer.clear();
        if (myCommentsTable != null) {
            final int theNbOfRows = myCommentsTable.getRowCount();
            for (int i = 0; i < theNbOfRows; i++) {
                myCommentsTable.removeRow(0);
            }
        }
        myCommentsInnerPanel.clear();
        myOpenStepsInnerPanel.clear();
        myMode = Mode.DETAILS;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                myDetailModeRB.setValue(true, false);
                myRecapModeRB.setValue(false, false);
            }
        });
        myOverviewInnerPanel.clear();
    }
}
