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
package org.bonitasoft.console.client.view.steps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.UserRightsManager;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.cases.CaseCommentEditorWidget;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepEditor extends BonitaPanel implements ModelChangeListener {

    protected static final String CLOSED_DEPENDENT_STYLE_NAME = "closed";
    protected static final String FINISHED_DEPENDENT_STYLE_NAME = "finished";

    protected StepItem myStep;
    protected final DecoratorPanel myOuterPanel = new DecoratorPanel();
    protected final FlowPanel myInnerPanel = new FlowPanel();

    protected final FlowPanel myFirstRowPanel = new FlowPanel();
    protected final FlowPanel mySecondRowPanel = new FlowPanel();
    protected final FlowPanel myThirdRowPanel = new FlowPanel();
    protected final FlowPanel myStepHorizontalActionPanel = new FlowPanel();
    protected final HorizontalPanel myFirstRowRightPanel = new HorizontalPanel();
    protected final Grid myFirstRowContainer = new Grid(1, 2);

    protected boolean mustBeOpen = false;
    protected final StepItemDataSource myStepDataSource;

    protected final CaseDataSource myCaseDataSource;
    protected final ProcessDataSource myProcessDataSource;
    protected final UserDataSource myUserDataSource;

    // FIXME manage step comments instead of case comments
    // protected StepCommentEditorWidget myStepCommentPanel;
    protected CaseCommentEditorWidget myStepCommentPanel;

    protected Label myLastUpdateDate;
    protected StepActionWidget myStepActionWidget;
    protected StepRedirectWidget myStepRedirectWidget;
    protected StepStateWidget myStepStateWidget;
    protected Frame myIFrame;

    protected Image myPriorityPlaceHolder;

    protected Label myAssignationLabel;

    protected HandlerRegistration myHandlerRegistration;
    protected boolean mustRefresh;
    protected URLUtils urlUtils = URLUtilsFactory.getInstance();

    protected String formId;

    /**
     * 
     * Default constructor.
     * 
     * @param aDataSource
     * @param aStep must be NOT null
     * @param mustBeVisible
     */
    public StepEditor(StepItemDataSource aDataSource, StepItem aStep, boolean mustBeVisible, CaseDataSource aCaseDataSource, ProcessDataSource aProcessDataSource, UserDataSource aUserDataSource) {
        super();
        myStepDataSource = aDataSource;
        myCaseDataSource = aCaseDataSource;
        myProcessDataSource = aProcessDataSource;
        myUserDataSource = aUserDataSource;
        myStep = aStep;
        final boolean isTask = myStep.isTask();
        final boolean isTaskOver = myStep.getState().equals(StepState.FINISHED);
        boolean isEditMode = !isTaskOver && isTask;
        if (isEditMode) {
            formId = myStep.getUUID().getStepDefinitionUUID() + "$entry";
        } else {
            formId = myStep.getUUID().getStepDefinitionUUID() + "$view";
        }
        mustBeOpen = mustBeVisible;
        myStep.addModelChangeListener(StepItem.STATE_PROPERTY, this);
        myStep.addModelChangeListener(StepItem.ASSIGN_PROPERTY, this);
        myStep.addModelChangeListener(StepItem.PRIORITY_PROPERTY, this);
        myStep.addModelChangeListener(StepItem.DATE_PROPERTY, this);

        aStep.getCase().addModelChangeListener(CaseItem.STATE_PROPERTY, this);

        myStepRedirectWidget = new StepRedirectWidget(aStep, aCaseDataSource, aProcessDataSource);
        myStepStateWidget = new StepStateWidget(aStep);

        myFirstRowPanel.setStylePrimaryName("bos_first_row");
        mySecondRowPanel.setStylePrimaryName("bos_second_row");
        myThirdRowPanel.setStylePrimaryName("bos_third_row");

        myInnerPanel.add(myFirstRowPanel);
        myInnerPanel.add(mySecondRowPanel);
        myInnerPanel.add(myThirdRowPanel);
        myOuterPanel.add(myInnerPanel);
        myOuterPanel.setStylePrimaryName("step_editor");
        myOuterPanel.addStyleName(CSSClassManager.ROUNDED_PANEL);

        initContent();
        update();

        this.initWidget(myOuterPanel);
    }

    protected StepActionWidget buildStepActionWidget() {
        return new StepActionWidget(myStepDataSource, myStep, myUserDataSource);
    }

    /**
     * Build static part of the UI.
     */
    protected void initContent() {
        myStepActionWidget = buildStepActionWidget();

        final Grid theWrapper = new Grid(1, 6);
        myLastUpdateDate = new Label(LocaleUtil.shortDateFormat(myStep.getLastUpdateDate()));
        myLastUpdateDate.setTitle(constants.lastUpdateDate());
        myPriorityPlaceHolder = new Image(PICTURE_PLACE_HOLDER);
        final Label theStepLabel = new Label(myStep.getLabel());
        myAssignationLabel = new Label();
        myAssignationLabel.setStylePrimaryName("step_editor_step_assign");
        String theStepDescription = myStep.getDesc();
        String theShortDesc;
        if (theStepDescription.length() > 50) {
            theShortDesc = theStepDescription.substring(0, 47) + "...";
        } else {
            theShortDesc = theStepDescription;
        }
        final Label theShortDescription = new Label(theShortDesc);
        theShortDescription.setTitle(theStepDescription);
        theShortDescription.setStylePrimaryName("step_editor_step_description");
        theWrapper.setWidget(0, 0, myLastUpdateDate);
        theWrapper.setWidget(0, 1, myStepStateWidget);
        if (!StepState.FAILED.equals(myStep.getState())) {
            theWrapper.setWidget(0, 2, myPriorityPlaceHolder);
            theWrapper.setWidget(0, 4, myAssignationLabel);
        }
        theWrapper.setWidget(0, 3, theStepLabel);
        theWrapper.setWidget(0, 5, theShortDescription);
        theWrapper.addStyleName("bos_step_descriptor");
        theWrapper.getCellFormatter().setStylePrimaryName(0, 0, "step_editor_first_row_column");
        theWrapper.getCellFormatter().setStylePrimaryName(0, 1, "step_editor_first_row_column");
        theWrapper.getCellFormatter().setStylePrimaryName(0, 2, "step_editor_first_row_column");
        theWrapper.getCellFormatter().setStylePrimaryName(0, 3, "step_editor_first_row_column");
        theWrapper.getCellFormatter().setStylePrimaryName(0, 4, "step_editor_first_row_column");
        myFirstRowContainer.setWidget(0, 0, theWrapper);
        myFirstRowContainer.setWidget(0, 1, myFirstRowRightPanel);
        myFirstRowContainer.setStyleName("step_editor_first_row_Container");
        myFirstRowContainer.getCellFormatter().setStylePrimaryName(0, 1, "step_editor_first_row_column");

        myFirstRowPanel.add(myFirstRowContainer);
        myThirdRowPanel.add(myStepHorizontalActionPanel);
        myStepHorizontalActionPanel.addStyleName("bos_action_panel");

        if (!myStep.getCase().isArchived()) {
            // Archived cases have to be considered as readonly.
            if (UserRightsManager.getInstance().isAllowed(RuleType.PROCESS_ADD_COMMENT, myStep.getCase().getProcessUUID().getValue())) {
                myStepCommentPanel = new CaseCommentEditorWidget(myCaseDataSource, myStep.getCase());
            }
        }

        switch (myStep.getState()) {
        case READY:
        case SUSPENDED:
            // Create click handler for user interaction.
            myHandlerRegistration = theWrapper.addClickHandler(new ClickHandler() {
                /*
                 * (non-Javadoc)
                 * 
                 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com. google.gwt.event.dom.client.ClickEvent)
                 */
                public void onClick(ClickEvent anEvent) {
                    final Cell theCell = theWrapper.getCellForEvent(anEvent);
                    if (theCell != null) {
                        toggleOverviewVisibility();
                    }
                }
            });
            break;
        default:
            break;
        }
    }

    /**
     * Update the UI.
     */
    protected void update() {
        // Either the form content is available or it will be computed
        // asynchronously.
        mySecondRowPanel.clear();

        if (!mustBeOpen) {
            myOuterPanel.addStyleDependentName(CLOSED_DEPENDENT_STYLE_NAME);
        } else {
            myOuterPanel.removeStyleDependentName(CLOSED_DEPENDENT_STYLE_NAME);
        }

        String theAssignValue = null;
        String theFormContent = null;
        switch (myStep.getState()) {
        case ABORTED:
            theFormContent = constants.stepAborted();
            break;
        case FINISHED:
            myOuterPanel.addStyleDependentName(FINISHED_DEPENDENT_STYLE_NAME);
            theAssignValue = "(" + myStep.getAuthor() + ") - ";
            // Use an asynchronous mechanism to build the content.
            buildStepFormIFrame();
            break;
        case SKIPPED:
            myOuterPanel.addStyleDependentName(FINISHED_DEPENDENT_STYLE_NAME);
            // Use an asynchronous mechanism to build the content.
            buildStepFormIFrame();
            break;
        case CANCELLED:
            theFormContent = constants.stepCancelled();
            break;
        case EXECUTING:
            theFormContent = constants.stepExecuting();
            break;
        case READY:
            if (mustBeOpen) {
                // Use an asynchronous mechanism to build the content.
                buildStepFormIFrame();
            }
            break;
        case SUSPENDED:
            theFormContent = constants.stepSuspended();
            break;
        case FAILED:
            theFormContent = constants.stepFailed();
            break;
        default:
            theFormContent = "";
            break;
        }
        if (theAssignValue == null) {
            String theCandidates = "";
            Set<UserUUID> theSetOfCandidates = myStep.getAssign();
            for (UserUUID theCandidate : theSetOfCandidates) {
                theCandidates = theCandidates + "," + theCandidate.toString();
            }
            if (theCandidates.length() > 1) {
                // Remove the extra comma at the beginning of the string.
                theCandidates = theCandidates.substring(1);
            }
            theAssignValue = theCandidates;
        }

        // Finally update widgets.
        final String theStepDescription = myStep.getDesc();
        final String stepLabel = myStep.getLabel();
        final int spaceLength = 150 - theStepDescription.length() - stepLabel.length();
        myAssignationLabel.setTitle(theAssignValue);
        if (theAssignValue.length() > spaceLength) {
            theAssignValue = theAssignValue.substring(0, spaceLength - 3) + "...";
        }
        theAssignValue = "(" + theAssignValue + ")";
        myAssignationLabel.setText(theAssignValue);

        if (myStep.isTask() || (StepState.FAILED.equals(myStep.getState()))) {

            myPriorityPlaceHolder.setStyleName(CSSClassManager.getPriorityIconStyle(myStep.getPriority().name()));
            myPriorityPlaceHolder.setTitle(patterns.stepDescriptionPriority(getPriorityLabel(myStep)));

            myStepHorizontalActionPanel.clear();
            if (this.mustBeOpen && !StepState.FINISHED.equals(myStep.getState()) && !StepState.CANCELLED.equals(myStep.getState()) && !StepState.ABORTED.equals(myStep.getState()) && !StepState.SKIPPED.equals(myStep.getState())) {
                if (myStepRedirectWidget != null) {
                    myFirstRowRightPanel.add(myStepRedirectWidget);
                }
                if (myStepActionWidget != null) {
                    myFirstRowRightPanel.add(myStepActionWidget);
                }
                Widget theMenu = buildMenuOfActions();
                myStepHorizontalActionPanel.add(theMenu);
            } else {
                // Ensure the widgets are removed from the GUI. (Refresh the
                // view
                // may occur when the step changes its state.)
                if (myStepRedirectWidget != null) {
                    myFirstRowRightPanel.remove(myStepRedirectWidget);
                }
                if (myStepActionWidget != null) {
                    myFirstRowRightPanel.remove(myStepActionWidget);
                }
            }
        }

        if (myStepCommentPanel != null) {
            if (!mustBeOpen || myStep.getCase().isArchived()) {
                myThirdRowPanel.remove(myStepCommentPanel);
            } else {
                myThirdRowPanel.add(myStepCommentPanel);
            }
        }

        if (theFormContent != null && mustBeOpen) {
            // We already have the content to display.
            mySecondRowPanel.add(new HTML(theFormContent));
        }
    }

    private void buildStepFormIFrame() {
        if (myStep.getApplicationURL() != null) {
            buildIframeAndInsertIt(myStep.getApplicationURL());
        } else {
            myProcessDataSource.getItem(myStep.getCase().getProcessUUID(), new AsyncHandler<BonitaProcess>() {
                public void handleFailure(Throwable t) {
                    GWT.log("unable to get the process", t);
                };

                public void handleSuccess(BonitaProcess aResult) {
                    buildIframeAndInsertIt(aResult.getApplicationUrl());
                }
            });
        }

    }

    /**
     * @param aStep a {@link StepItem}
     * @return The priority label
     */
    private String getPriorityLabel(StepItem aStep) {
        switch (aStep.getPriority()) {
        case NORMAL:
            return constants.normal();
        case HIGH:
            return constants.high();
        case URGENT:
            return constants.urgent();
        default:
            return "";
        }
    }

    protected void buildIframeAndInsertIt(final String anApplicationURL) {

        String theApplicationURL = anApplicationURL;
        if (theApplicationURL == null) {
            theApplicationURL = "";
        }
        if (BonitaConsole.userProfile.useCredentialTransmission()) {
            RpcConsoleServices.getLoginService().generateTemporaryToken(new GetTokenAsyncCallback(theApplicationURL));
        } else {
            insertFormIFrame(buildStepFormIFrame(theApplicationURL, ""));
        }
    }

    public void insertFormIFrame(String aFormIFrame) {
        myIFrame = new FormPageFrame();
        myIFrame.setStyleName("form_view_frame");
        final Element theElement = myIFrame.getElement();
        theElement.setId(formId);
        theElement.setAttribute("frameBorder", "0");
        theElement.setAttribute("allowTransparency", "true");
        myIFrame.setUrl(aFormIFrame);
        mySecondRowPanel.add(myIFrame);
    }

    private String buildStepFormIFrame(String anApplicationURL, String aCredentialsParam) {
        final Map<String, List<String>> parametersMap = urlUtils.getURLParametersMap(anApplicationURL);
        final String url = urlUtils.removeURLparameters(anApplicationURL);

        StringBuilder processFormIFrame = new StringBuilder();
        processFormIFrame.append(url);
        processFormIFrame.append("?");
        processFormIFrame.append(URLUtils.LOCALE_PARAM);
        processFormIFrame.append("=");
        if (parametersMap.containsKey(URLUtils.LOCALE_PARAM)) {
            processFormIFrame.append(parametersMap.get(URLUtils.LOCALE_PARAM).get(0));
            parametersMap.remove(URLUtils.LOCALE_PARAM);
        } else {
            processFormIFrame.append(LocaleInfo.getCurrentLocale().getLocaleName());
        }
        if (BonitaConsole.userProfile.getDomain() != null) {
            processFormIFrame.append("&");
            processFormIFrame.append(URLUtils.DOMAIN_PARAM);
            processFormIFrame.append("=");
            if (parametersMap.containsKey(URLUtils.DOMAIN_PARAM)) {
                processFormIFrame.append(parametersMap.get(URLUtils.DOMAIN_PARAM).get(0));
                parametersMap.remove(URLUtils.DOMAIN_PARAM);
            } else {
                processFormIFrame.append(BonitaConsole.userProfile.getDomain());
            }
        }
        for (Entry<String, List<String>> parametersEntry : parametersMap.entrySet()) {
            processFormIFrame.append("&");
            processFormIFrame.append(parametersEntry.getKey());
            processFormIFrame.append("=");
            List<String> entryValues = parametersEntry.getValue();
            for (String value : entryValues) {
                processFormIFrame.append(value);
                processFormIFrame.append(",");
            }
            processFormIFrame.deleteCharAt(processFormIFrame.length() - 1);
        }

        processFormIFrame.append("#");
        processFormIFrame.append(URLUtils.VIEW_MODE_PARAM);
        processFormIFrame.append("=");
        processFormIFrame.append(URLUtils.FORM_ONLY_APPLICATION_MODE);
        processFormIFrame.append("&");
        processFormIFrame.append(URLUtils.FORM_ID);
        processFormIFrame.append("=");
        processFormIFrame.append(formId);
        processFormIFrame.append("&");
        processFormIFrame.append(URLUtils.TASK_ID_PARAM);
        processFormIFrame.append("=");
        processFormIFrame.append(myStep.getUUID());
        processFormIFrame.append(aCredentialsParam);

        return processFormIFrame.toString();
    }

    protected class GetTokenAsyncCallback implements AsyncCallback<String> {

        protected String myApplicationURL;

        public GetTokenAsyncCallback(String anApplicationURL) {
            this.myApplicationURL = anApplicationURL;
        }

        public void onSuccess(String temporaryToken) {
            String theCredentialsParam = "&" + URLUtils.USER_CREDENTIALS_PARAM + "=" + temporaryToken;
            insertFormIFrame(buildStepFormIFrame(myApplicationURL, theCredentialsParam));
        }

        public void onFailure(Throwable t) {
            insertFormIFrame(buildStepFormIFrame(myApplicationURL, ""));
        }

    }

    protected Widget buildMenuOfActions() {
        final FlowPanel theMenu = new FlowPanel();
        final UserRightsManager theUserRightsManager = UserRightsManager.getInstance();

        if (myStep.getState() == StepState.FAILED && theUserRightsManager.isAllowed(RuleType.SKIP_STEP, myStep.getUUID().getStepDefinitionUUID())) {
            theMenu.add(buildSkipStepMenuEntry());
        } else {
            if (theUserRightsManager.isAllowed(RuleType.ASSIGN_TO_ME_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                theMenu.add(buildAssignToMeMenuEntry());
            }

            if (myStep.getState() == StepState.SUSPENDED) {
                if (theUserRightsManager.isAllowed(RuleType.RESUME_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                    theMenu.add(buildResumeStepMenuEntry());
                }
            } else {
                if (theUserRightsManager.isAllowed(RuleType.SUSPEND_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                    theMenu.add(buildSuspendMenuEntry());
                }
            }
        }
        return theMenu;
    }

    protected Widget buildAssignToMeMenuEntry() {
        final FlowPanel theAssignToMeContainer = new FlowPanel();
        final Image theAssignToMeIcon = new Image(PICTURE_PLACE_HOLDER);
        final Label theAssignToMeLink = new Label(constants.assignToMe());
        ClickHandler theAssignToMeClickHandler = new ClickHandler() {
            public void onClick(ClickEvent aEvent) {
                myStepDataSource.assignStepToMe(myStep);
            }
        };
        theAssignToMeContainer.add(theAssignToMeIcon);
        theAssignToMeContainer.add(theAssignToMeLink);

        // Associate CSS styles
        theAssignToMeContainer.setStyleName("inline_block");
        theAssignToMeIcon.setStylePrimaryName("assign_to_me_icon");
        theAssignToMeLink.setStyleName(CSSClassManager.LINK_LABEL);
        // Associate the click handlers
        theAssignToMeIcon.addClickHandler(theAssignToMeClickHandler);
        theAssignToMeLink.addClickHandler(theAssignToMeClickHandler);

        return theAssignToMeContainer;
    }

    protected Widget buildSuspendMenuEntry() {
        final FlowPanel theSuspendContainer = new FlowPanel();
        final Image theSuspendIcon = new Image(PICTURE_PLACE_HOLDER);
        final Label theSuspendLink = new Label(constants.suspend());
        ClickHandler theSuspendClickHandler = new ClickHandler() {
            public void onClick(ClickEvent aEvent) {
                myStepDataSource.suspendStep(myStep);
            }
        };

        theSuspendContainer.add(theSuspendIcon);
        theSuspendContainer.add(theSuspendLink);
        // Associate CSS styles
        theSuspendIcon.setStylePrimaryName("suspend_icon");
        theSuspendContainer.setStyleName("inline_block");
        theSuspendLink.setStyleName(CSSClassManager.LINK_LABEL);
        // Associate the click handlers
        theSuspendIcon.addClickHandler(theSuspendClickHandler);
        theSuspendLink.addClickHandler(theSuspendClickHandler);
        return theSuspendContainer;
    }

    protected Widget buildResumeStepMenuEntry() {
        final FlowPanel theResumeContainer = new FlowPanel();
        final Image theResumeIcon = new Image(PICTURE_PLACE_HOLDER);
        final Label theResumeLink = new Label(constants.resume());
        ClickHandler theResumeClickHandler = new ClickHandler() {
            public void onClick(ClickEvent aEvent) {
                myStepDataSource.resumeStep(myStep);
            }
        };

        theResumeContainer.add(theResumeIcon);
        theResumeContainer.add(theResumeLink);
        // Associate CSS styles
        theResumeIcon.setStylePrimaryName("resume_icon");
        theResumeContainer.setStyleName("inline_block");
        theResumeLink.setStyleName(CSSClassManager.LINK_LABEL);
        // Associate the click handlers
        theResumeIcon.addClickHandler(theResumeClickHandler);
        theResumeLink.addClickHandler(theResumeClickHandler);
        return theResumeContainer;
    }

    protected Widget buildSkipStepMenuEntry() {
        final FlowPanel theSkipContainer = new FlowPanel();
        final Image theSkipIcon = new Image(PICTURE_PLACE_HOLDER);
        final Label theSkipLink = new Label(constants.skipStepActionLabel());
        ClickHandler theSkipClickHandler = new ClickHandler() {
            public void onClick(ClickEvent aEvent) {
                myStepDataSource.skipStep(myStep);
            }
        };

        theSkipContainer.add(theSkipIcon);
        theSkipContainer.add(theSkipLink);
        // Associate CSS styles
        theSkipIcon.setStylePrimaryName("skip_icon");
        theSkipContainer.setStyleName("inline_block");
        theSkipLink.setStyleName(CSSClassManager.LINK_LABEL);
        // Associate the click handlers
        theSkipIcon.addClickHandler(theSkipClickHandler);
        theSkipLink.addClickHandler(theSkipClickHandler);
        return theSkipContainer;
    }

    /*
     * Switch visibility of the overview.
     */
    protected void toggleOverviewVisibility() {
        mustBeOpen = !mustBeOpen;
        update();
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.beans.ModelChangeListener#propertyChange(java.beans. PropertyChangeEvent)
     */
    public void modelChange(ModelChangeEvent anEvt) {
        if (isAttached()) {
            update();
        }
        if (StepItem.DATE_PROPERTY.equals(anEvt.getPropertyName())) {
            myLastUpdateDate.setText(DateTimeFormat.getFormat(constants.dateShortFormat()).format(((Date) anEvt.getNewValue())));
        } else if (StepItem.STATE_PROPERTY.equals(anEvt.getPropertyName())) {
            StepState theNewState = (StepState) anEvt.getNewValue();
            switch (theNewState) {
            case READY:
            case SUSPENDED:
                break;
            default:
                // remove click handler for user interaction.
                if (myHandlerRegistration != null) {
                    myHandlerRegistration.removeHandler();
                }
                break;
            }
        }
    }

    public void refresh() {
        if (mustBeOpen) {
            try {
                if (myIFrame != null) {
                    myIFrame.setUrl(cleanURL(myIFrame.getUrl()));
                }
            } catch (Exception e) {
                GWT.log("Unable to refresh the Step editor! ", e);
            }
        }
        mustRefresh = false;
    }

    protected String cleanURL(String anUrl) {
        final List<String> hashParamsToRemove = new ArrayList<String>();
        hashParamsToRemove.add(URLUtils.USER_CREDENTIALS_PARAM);
        return URLUtils.getInstance().rebuildUrl(anUrl, null, null, hashParamsToRemove, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Composite#onDetach()
     */
    @Override
    protected void onDetach() {
        super.onDetach();
        mustRefresh = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        if (mustRefresh) {
            refresh();
        }
    }

}
