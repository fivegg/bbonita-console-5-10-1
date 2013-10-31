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
package org.bonitasoft.console.client.view.reporting;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.reporting.ReportingDataSource;
import org.bonitasoft.console.client.model.steps.StepDefinitionDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.TimeUnit;
import org.bonitasoft.console.client.steps.StepDefinition;
import org.bonitasoft.console.client.steps.StepDefinition.StepType;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.identity.UserViewer;
import org.bonitasoft.console.client.view.processes.ProcessViewer;
import org.bonitasoft.console.client.view.steps.StepDefinitionViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ReportParametersEditorWidget extends BonitaPanel {

    protected HTML errorMessageLabel = new HTML();

    protected ReportItem myItem = null;
    protected final ReportingDataSource myReportDataSource;
    protected final UserDataSource myUserDataSource;
    protected final ProcessDataSource myProcessDataSource;
    protected final StepDefinitionDataSource myStepDefinitionDataSource;
    protected AsyncHandler<Void> myCreateOrUpdateHandler;

    protected final FlowPanel myOuterPanel;
    protected final FlowPanel myConfigurationPanel;
    protected final FlowPanel myReportResultPanel;

    /*
     * Configuration edition
     */
    protected UserViewer myInvolvedUserViewer;
    protected ProcessViewer myProcessViewer;
    protected StepDefinitionViewer myStepViewer;
    protected DateBox myDateFrom;
    protected DateBox myDateTo;
    protected ListBox myIntervalLB;
    protected ListBox myStepTypeLB;

    protected BonitaProcessUUID myProcessUUID;
    protected String myUserName;
    protected String myUserUUID;
    protected StepUUID myStepUUID;

    private boolean forceReload;

    protected final Anchor myDownloadLink;

    protected final FlowPanel myFooterPanel;

    protected final HTML myRefreshLink;

    protected Integer myReportNb;

    public ReportParametersEditorWidget(final ReportingDataSource aReportingDataSource, final ReportItem aReportItem, final UserDataSource aUserDataSource, final ProcessDataSource aProcessDataSource,
            final StepDefinitionDataSource aStepDefinitionDataSource, Integer aReportNb) {
        this(aReportingDataSource, aReportItem, aUserDataSource, aProcessDataSource, aStepDefinitionDataSource, aReportNb, false);
    }
    
    public ReportParametersEditorWidget(final ReportingDataSource aReportingDataSource, final ReportItem aReportItem, final UserDataSource aUserDataSource, final ProcessDataSource aProcessDataSource,
            final StepDefinitionDataSource aStepDefinitionDataSource, Integer aReportNb, boolean aRun) {
        super();
        myReportNb = aReportNb;
        myReportDataSource = aReportingDataSource;
        myUserDataSource = aUserDataSource;
        myProcessDataSource = aProcessDataSource;
        myStepDefinitionDataSource = aStepDefinitionDataSource;

        myOuterPanel = new FlowPanel();
        myOuterPanel.setStylePrimaryName("bos_report_parameters_editor");
        myConfigurationPanel = new FlowPanel();
        myConfigurationPanel.setStylePrimaryName("bos_report_parameters");
        myReportResultPanel = new FlowPanel();
        myOuterPanel.setStylePrimaryName("bos_report");

        myFooterPanel = new FlowPanel();
        myFooterPanel.setStylePrimaryName("bos_report_parameters_footer");

        myOuterPanel.add(myConfigurationPanel);
        myOuterPanel.add(myReportResultPanel);
        myOuterPanel.add(myFooterPanel);

        myItem = aReportItem;

        myDownloadLink = new Anchor(constants.reportPDFVersion());
        myDownloadLink.setVisible(false);
        myDownloadLink.setStyleName(CSSClassManager.LINK_LABEL);

        myRefreshLink = new HTML(constants.refreshIcon());
        myRefreshLink.setTitle(constants.refresh());
        myRefreshLink.setStyleName(CSSClassManager.LINK_LABEL);

        myRefreshLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent anEvent) {
                run();
            }
        });

        if (myItem != null) {
            buildContent();
        }

        initWidget(myOuterPanel);
        
        if(aRun){
            run();
        }
    }

    /**
     * Creation of parameters of the report. If the user have already use a report, all parameter values are token from a cookie
     * 
     */
    protected void buildContent() {
        if (myItem.requiresConfiguration()) {
            final Set<String> theConfiguration = myItem.getConfigurationElements();
            final FlowPanel theTimeRow = new FlowPanel();
            theTimeRow.setStylePrimaryName("bos_time_row");
            final FlowPanel theProcessRow = new FlowPanel();
            theProcessRow.setStylePrimaryName("bos_process_row");
            final FlowPanel theStepRow = new FlowPanel();
            theStepRow.setStylePrimaryName("bos_step_row");
            final FlowPanel theTypeRow = new FlowPanel();
            theTypeRow.setStylePrimaryName("bos_type_row");
            final FlowPanel theUserRow = new FlowPanel();
            theUserRow.setStylePrimaryName("bos_user_row");
            final FlowPanel theErrorMessageRow = new FlowPanel();

            HashMap<String, String> parameters = myReportDataSource.getParameters(myReportNb);

            // Layout fields
            if (theConfiguration.contains(ConsoleConstants.StartDate)) {
                myDateFrom = new DateBox();
                if (parameters != null && parameters.containsKey(ConsoleConstants.StartDate)) {
                    long theTimeStamp = Long.parseLong(parameters.get(ConsoleConstants.StartDate));
                    myDateFrom.setValue(new Date(theTimeStamp));
                }
                theTimeRow.add(new Label(constants.from()));
                theTimeRow.add(myDateFrom);
            }
            if (theConfiguration.contains(ConsoleConstants.EndDate)) {
                myDateTo = new DateBox();
                if (parameters != null && parameters.containsKey(ConsoleConstants.EndDate)) {
                    long theTimeStamp = Long.parseLong(parameters.get(ConsoleConstants.EndDate));
                    myDateTo.setValue(new Date(theTimeStamp));
                }
                theTimeRow.add(new Label(constants.to()));
                theTimeRow.add(myDateTo);
            }
            if (theConfiguration.contains(ConsoleConstants.TimeUnit)) {
                myIntervalLB = new ListBox();
                for (TimeUnit theUnit : TimeUnit.values()) {
                    myIntervalLB.addItem(LocaleUtil.translate(theUnit), theUnit.name());
                }
                if (parameters != null && parameters.containsKey(ConsoleConstants.TimeUnit)) {
                    myIntervalLB.setSelectedIndex(TimeUnit.ordinal(parameters.get(ConsoleConstants.TimeUnit)));
                } else {
                    myIntervalLB.setSelectedIndex(TimeUnit.WEEK.ordinal());
                }
                theTimeRow.add(new Label(constants.interval()));
                theTimeRow.add(myIntervalLB);
            }
            if (theConfiguration.contains(ConsoleConstants.ProcessUUID)) {
                if (parameters != null && parameters.containsKey(ConsoleConstants.ProcessUUID)) {
                    BonitaProcessUUID processUUID = new BonitaProcessUUID(parameters.get(ConsoleConstants.ProcessUUID), null);
                    myProcessViewer = new ProcessViewer(myProcessDataSource, processUUID, true);
                    myProcessUUID = processUUID;
                } else {
                    myProcessViewer = new ProcessViewer(myProcessDataSource, null, true);
                }
                myProcessViewer.addModelChangeListener(ProcessViewer.PROCESS_PROPERTY, new ModelChangeListener() {

                    public void modelChange(ModelChangeEvent aEvt) {
                        final BonitaProcess theProcess = ((BonitaProcess) aEvt.getNewValue());
                        myProcessUUID = theProcess.getUUID();
                        if (myStepViewer != null) {
                            myStepViewer.setParentProcess(theProcess);
                            myStepViewer.setItem(null);
                        }
                    }
                });
                theProcessRow.add(new Label(constants.reportProcessDefinitionParameter()));
                theProcessRow.add(myProcessViewer);
            }

            if (theConfiguration.contains(ConsoleConstants.ActivityUUID)) {
                if (parameters != null && parameters.containsKey(ConsoleConstants.ActivityUUID)) {
                    StepUUID stepUUID = new StepUUID(parameters.get(ConsoleConstants.ActivityUUID), null);
                    myStepViewer = new StepDefinitionViewer(myStepDefinitionDataSource, stepUUID, true);
                    myStepUUID = stepUUID;
                } else {
                    myStepViewer = new StepDefinitionViewer(myStepDefinitionDataSource, null, true);
                }
                myStepViewer.addModelChangeListener(StepDefinitionViewer.STEP_PROPERTY, new ModelChangeListener() {
                    public void modelChange(ModelChangeEvent aEvt) {
                        final StepDefinition theItem = ((StepDefinition) aEvt.getNewValue());
                        myStepUUID = theItem.getUUID();
                    }
                });
                theStepRow.add(new Label(constants.reportStepDefinitionParameter()));
                theStepRow.add(myStepViewer);
            }
            if (theConfiguration.contains(ConsoleConstants.ActivityType)) {
                myStepTypeLB = new ListBox();
                myStepTypeLB.addItem(LocaleUtil.translate(StepType.Automatic), StepType.Automatic.name());
                myStepTypeLB.addItem(LocaleUtil.translate(StepType.Human), StepType.Human.name());
                if (parameters != null && parameters.containsKey(ConsoleConstants.ActivityType)) {
                    myStepTypeLB.setSelectedIndex(StepType.ordinal(parameters.get(ConsoleConstants.ActivityType)));
                }
                theTypeRow.add(new Label(constants.reportStepTypeParameter()));
                theTypeRow.add(myStepTypeLB);
            }

            if (theConfiguration.contains(ConsoleConstants.User)) {
                if (parameters != null && parameters.containsKey(ConsoleConstants.User)) {
                    UserUUID userUUID = new UserUUID(parameters.get(ConsoleConstants.User));
                    GWT.log("User of the rapport (from cookie):" + parameters.get(ConsoleConstants.User));
                    myInvolvedUserViewer = new UserViewer(myUserDataSource, userUUID, true);
                    myUserName = parameters.get(ConsoleConstants.UserName);
                } else {
                    myInvolvedUserViewer = new UserViewer(myUserDataSource, null, true);
                }
                myInvolvedUserViewer.addModelChangeListener(UserViewer.USER_PROPERTY, new ModelChangeListener() {
                    public void modelChange(ModelChangeEvent aEvt) {
                        final User theItem = ((User) aEvt.getNewValue());
                        myUserName = theItem.getUsername();
                        myUserUUID = theItem.getUUID().getValue();
                    }
                });
                theUserRow.add(new Label(constants.reportUserParameter()));
                theUserRow.add(myInvolvedUserViewer);
            }

            theErrorMessageRow.add(errorMessageLabel);
            theErrorMessageRow.setStyleName("identity_form_mandatory");

            myConfigurationPanel.add(theErrorMessageRow);
            myConfigurationPanel.add(theTimeRow);
            myConfigurationPanel.add(theProcessRow);
            myConfigurationPanel.add(theStepRow);
            myConfigurationPanel.add(theTypeRow);
            myConfigurationPanel.add(theUserRow);

            // Add the run button.
            final FlowPanel theButtonsRow = new FlowPanel();
            final CustomMenuBar theRunButton = new CustomMenuBar();
            theRunButton.addItem(constants.run(), new Command() {

                public void execute() {
                    if (myItem != null) {
                        run();
                    }

                }
            });
            theButtonsRow.add(theRunButton);
            theButtonsRow.setStyleName("identity_form_button_group");
            myConfigurationPanel.add(theButtonsRow);

        } else {
            // Directly display the report.
            run();
        }
        // Add the refresh button.
        final FlowPanel theButtonsRow = new FlowPanel();
        theButtonsRow.setStyleName("identity_form_button_group");
        theButtonsRow.add(myRefreshLink);
        theButtonsRow.add(myDownloadLink);

        myRefreshLink.setVisible(false);
        myFooterPanel.add(theButtonsRow);

    }

    public void setFocus() {
    }

    protected boolean validate() {
        final Set<String> theConfiguration = myItem.getConfigurationElements();
        StringBuilder errorMessages = new StringBuilder();
        if (theConfiguration.contains(ConsoleConstants.User) && myUserName == null) {
            errorMessages.append(patterns.mandatoryFieldLabel(constants.reportUserParameter()));
            errorMessages.append(ConsoleConstants.BR);
        }
        if (theConfiguration.contains(ConsoleConstants.ProcessUUID) && myProcessUUID == null) {
            errorMessages.append(patterns.mandatoryFieldLabel(constants.reportProcessDefinitionParameter()));
            errorMessages.append(ConsoleConstants.BR);
        }
        if (theConfiguration.contains(ConsoleConstants.ActivityUUID) && myStepUUID == null) {
            errorMessages.append(patterns.mandatoryFieldLabel(constants.reportStepDefinitionParameter()));
            errorMessages.append(ConsoleConstants.BR);
        }
        if (theConfiguration.contains(ConsoleConstants.StartDate) && myDateFrom.getValue() == null) {
            errorMessages.append(patterns.mandatoryFieldLabel(constants.from()));
            errorMessages.append(ConsoleConstants.BR);
        }
        if (theConfiguration.contains(ConsoleConstants.EndDate) && myDateTo.getValue() == null) {
            errorMessages.append(patterns.mandatoryFieldLabel(constants.to()));
        }
        errorMessageLabel.setHTML(errorMessages.toString());
        return errorMessages.length() == 0;
    }

    protected void run() {

        if (validate()) {
            try {
                RequestBuilder theRequestBuilder;
                final String theURL = myReportDataSource.buildReportURL(myItem, ReportScope.ADMIN);
                final String theCompleteURL = addParametersToURL(theURL);
                GWT.log("Calling the reporting engine with query: " + theCompleteURL);
                theRequestBuilder = new RequestBuilder(RequestBuilder.GET, theCompleteURL);
                theRequestBuilder.setCallback(new RequestCallback() {
                    public void onError(Request aRequest, Throwable anException) {
                        myReportResultPanel.clear();
                        myReportResultPanel.add(new HTML(constants.errorProcessingReport() + anException.getMessage()));
                        myDownloadLink.setHref(null);
                        myDownloadLink.setVisible(false);
                        myRefreshLink.setVisible(false);
                    }

                    public void onResponseReceived(Request aRequest, Response aResponse) {
                        myReportResultPanel.clear();
                        HTML theReport = new HTML();
                        theReport.setStyleName("bonita_report");
                        if (aResponse.getStatusCode() == Response.SC_OK) {
                            theReport.setHTML(aResponse.getText());
                            myDownloadLink.setHref(theCompleteURL + "&OutputFormat=pdf");
                            myDownloadLink.setVisible(true);
                            myRefreshLink.setVisible(true);
                        } else {
                            theReport.setHTML(constants.unableToDisplayReport() + "<BR/>" + aResponse.getText());
                            GWT.log("Unable to display report" + aResponse.getText(), null);
                            myDownloadLink.setHref(null);
                            myDownloadLink.setVisible(false);
                            myRefreshLink.setVisible(false);
                        }
                        myReportResultPanel.add(theReport);
                    }
                });
                myReportResultPanel.clear();
                myReportResultPanel.add(new HTML(constants.loading()));
                theRequestBuilder.send();
            } catch (RequestException e) {
                Window.alert("Error while trying to query the reports:" + e.getMessage());
            }
        }
    }

    /**
     * Add parameters in the URL and save its in a cookie
     * 
     * @param aURL
     * @return
     */
    protected String addParametersToURL(String aURL) {
        final Set<String> theConfiguration = myItem.getConfigurationElements();
        final StringBuffer theCompleteURL = new StringBuffer(aURL);
        final String theSeparator = "&";
        final String theEqualSymbol = "=";
        HashMap<String, String> parameters = new HashMap<String, String>();

        if (theConfiguration.contains(ConsoleConstants.StartDate)) {
            theCompleteURL.append(theSeparator);
            theCompleteURL.append(ConsoleConstants.StartDate);
            theCompleteURL.append(theEqualSymbol);
            theCompleteURL.append(myDateFrom.getValue().getTime());
            parameters.put(ConsoleConstants.StartDate, String.valueOf(myDateFrom.getValue().getTime()));
        }
        if (theConfiguration.contains(ConsoleConstants.EndDate)) {
            theCompleteURL.append(theSeparator);
            theCompleteURL.append(ConsoleConstants.EndDate);
            theCompleteURL.append(theEqualSymbol);
            theCompleteURL.append(myDateTo.getValue().getTime());
            parameters.put(ConsoleConstants.EndDate, String.valueOf(myDateTo.getValue().getTime()));
        }
        if (theConfiguration.contains(ConsoleConstants.TimeUnit)) {
            theCompleteURL.append(theSeparator);
            theCompleteURL.append(ConsoleConstants.TimeUnit);
            theCompleteURL.append(theEqualSymbol);
            theCompleteURL.append(myIntervalLB.getValue(myIntervalLB.getSelectedIndex()));
            parameters.put(ConsoleConstants.TimeUnit, myIntervalLB.getValue(myIntervalLB.getSelectedIndex()));
        }
        if (theConfiguration.contains(ConsoleConstants.ActivityType)) {
            theCompleteURL.append(theSeparator);
            theCompleteURL.append(ConsoleConstants.ActivityType);
            theCompleteURL.append(theEqualSymbol);
            theCompleteURL.append(myStepTypeLB.getValue(myStepTypeLB.getSelectedIndex()));
            parameters.put(ConsoleConstants.ActivityType, myStepTypeLB.getValue(myStepTypeLB.getSelectedIndex()));
        }
        if (theConfiguration.contains(ConsoleConstants.ProcessUUID)) {
            theCompleteURL.append(theSeparator);
            theCompleteURL.append(ConsoleConstants.ProcessUUID);
            theCompleteURL.append(theEqualSymbol);
            theCompleteURL.append(myProcessUUID);
            parameters.put(ConsoleConstants.ProcessUUID, myProcessUUID.getValue());
        }
        if (theConfiguration.contains(ConsoleConstants.ActivityUUID)) {
            theCompleteURL.append(theSeparator);
            theCompleteURL.append(ConsoleConstants.ActivityUUID);
            theCompleteURL.append(theEqualSymbol);
            theCompleteURL.append(myStepUUID);
            parameters.put(ConsoleConstants.ActivityUUID, myStepUUID.getValue());
        }
        if (theConfiguration.contains(ConsoleConstants.User)) {
            theCompleteURL.append(theSeparator);
            theCompleteURL.append(ConsoleConstants.User);
            theCompleteURL.append(theEqualSymbol);
            theCompleteURL.append(myUserName);
            parameters.put(ConsoleConstants.User, myUserUUID);
            parameters.put(ConsoleConstants.UserName, myUserName);
        }
        if (forceReload) {
            theCompleteURL.append(theSeparator);
            theCompleteURL.append(ConsoleConstants.reloadURLParameter);
            theCompleteURL.append(theEqualSymbol);
            theCompleteURL.append("true");
        }
        myReportDataSource.setParameters(myReportNb, parameters);
        return theCompleteURL.toString();
    }

    /**
     * Add the reload=${reload} parameter to the URL when querying the report.
     */
    public void setForceReload(boolean reload) {
        forceReload = reload;
    }

}
