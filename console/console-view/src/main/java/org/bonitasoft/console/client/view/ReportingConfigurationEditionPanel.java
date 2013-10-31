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
package org.bonitasoft.console.client.view;

import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.reporting.ReportingDataSource;
import org.bonitasoft.console.client.reporting.ReportingConfiguration;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ReportingConfigurationEditionPanel extends SetupPanel {

    protected final FlowPanel myOuterPanel;
    protected final Grid myInnerPanel;
    protected final CustomMenuBar myGlobalSettingsSaveButton = new CustomMenuBar();
    protected final CheckBox myUserReportingActivationStateSelector;
    protected final CheckBox myGlobalReportingActivationStateSelector;

    protected ReportingConfiguration myReportingConfiguration;

    protected final ReportingDataSource myReportingDataSource;
    protected final CaseDataSource myCaseDataSource;
    protected final TextBox myDashboardRefreshFrequency;
    protected final TextBox myStepAtRiskNbOfDays;

    public ReportingConfigurationEditionPanel(ReportingDataSource aReportingDataSource, final CaseDataSource aCaseDataSource, MessageDataSource aMessageDataSource) {
        super(aMessageDataSource);
        myReportingDataSource = aReportingDataSource;
        myCaseDataSource = aCaseDataSource;
        myOuterPanel = new FlowPanel();
        myOuterPanel.setStylePrimaryName(DEFAULT_CSS_STYLE);
        myOuterPanel.addStyleName("bos_reporting_configuration_panel");

        myInnerPanel = new Grid(5, 2);

        // Global settings reporting activation.
        myUserReportingActivationStateSelector = new CheckBox();
        myGlobalReportingActivationStateSelector = new CheckBox();
        myDashboardRefreshFrequency = new TextBox();
        myDashboardRefreshFrequency.setMaxLength(2);
        myDashboardRefreshFrequency.getElement().setAttribute("size", "2");

        myStepAtRiskNbOfDays = new TextBox();
        myStepAtRiskNbOfDays.setMaxLength(3);
        myStepAtRiskNbOfDays.getElement().setAttribute("size", "3");

        initWidget(myOuterPanel);
    }

    @Override
    public String getLocationLabel() {
        return constants.settings();
    }
    
    
    protected void buildContent() {
        final VerticalPanel descriptionPanel = new VerticalPanel();
        descriptionPanel.add(new HTML(constants.generalTabDescription()));
        descriptionPanel.setStyleName("descriptionPanel");
        myOuterPanel.add(descriptionPanel);
       
        myOuterPanel.add(myInnerPanel);
        myGlobalSettingsSaveButton.addItem(constants.save(), new Command() {
            public void execute() {
                String theRefreshFrequency = myDashboardRefreshFrequency.getValue();
                String theRemainingDaysForAtRiskSteps = myStepAtRiskNbOfDays.getValue();
                if (theRefreshFrequency == null || !theRefreshFrequency.matches("\\d+")) {
                    myMessageDataSource.addWarningMessage(messages.invalidValueForRefreshFrequency());
//                    myDashboardRefreshFrequency.setFocus(true);
                } else if (theRemainingDaysForAtRiskSteps == null || !theRemainingDaysForAtRiskSteps.matches("\\d+")) {
                    myMessageDataSource.addWarningMessage(messages.invalidValueForStepAtRiskThreshold());
//                    myStepAtRiskNbOfDays.setFocus(true);
                } else {
                    saveGlobalSettings();
                }
            }
        });
        myOuterPanel.add(myGlobalSettingsSaveButton);
    }

    protected void saveGlobalSettings() {

        if (myReportingConfiguration != null) {
            myOuterPanel.addStyleName("loading");

            myReportingConfiguration.setUserReportingEnabled(myUserReportingActivationStateSelector.getValue());
            myReportingConfiguration.setGlobalReportingEnabled(myGlobalReportingActivationStateSelector.getValue());
            // translate the value into milliseconds
            myReportingConfiguration.setDashBoardRefreshFrequency(Integer.parseInt(myDashboardRefreshFrequency.getValue()) * 60 * 1000);

            myReportingConfiguration.setRemainingDaysForAtRiskSteps(Integer.parseInt(myStepAtRiskNbOfDays.getValue()));

            myReportingDataSource.updateReportingConfiguration(myReportingConfiguration, new AsyncHandler<Void>() {
                public void handleFailure(Throwable anCaught) {
                    myOuterPanel.removeStyleName("loading");
                    if (anCaught instanceof ConsoleException) {
                        myMessageDataSource.addErrorMessage((ConsoleException) anCaught);
                        return;
                    }

                    if (anCaught instanceof SessionTimeOutException) {
                        Window.Location.reload();
                    } else {
                        myMessageDataSource.addErrorMessage(messages.unableToUpdateConfiguration());
                    }

                }

                public void handleSuccess(Void anResult) {
                    myOuterPanel.removeStyleName("loading");
                }
            });
        }
    }

    private void updateMyGeneralPanel() {

        // Reporting configuration.
        myReportingDataSource.getReportingConfiguration(new AsyncHandler<ReportingConfiguration>() {

            public void handleFailure(Throwable caught) {
                myMessageDataSource.addErrorMessage(messages.unableToReadConfiguration());
            };

            public void handleSuccess(ReportingConfiguration aResult) {
                myReportingConfiguration = aResult;
                if (myReportingConfiguration != null) {
                    myUserReportingActivationStateSelector.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                        public void onValueChange(ValueChangeEvent<Boolean> anEvent) {
                            if (anEvent.getValue()) {
                                myInnerPanel.setHTML(0, 0, constants.userReportingDeActivateDescription());
                            } else {
                                myInnerPanel.setHTML(0, 0, constants.userReportingActivateDescription());
                            }
                        }
                    });

                    myGlobalReportingActivationStateSelector.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                        public void onValueChange(ValueChangeEvent<Boolean> anEvent) {
                            if (anEvent.getValue()) {
                                myInnerPanel.setHTML(1, 0, constants.globalReportingDeActivateDescription());
                            } else {
                                myInnerPanel.setHTML(1, 0, constants.globalReportingActivateDescription());
                            }
                        }
                    });

                    // Set the initial values.
                    myUserReportingActivationStateSelector.setValue(myReportingConfiguration.isUserReportingEnabled(), false);
                    if (myReportingConfiguration.isUserReportingEnabled()) {
                        myInnerPanel.setHTML(0, 0, constants.userReportingDeActivateDescription());
                    } else {
                        myInnerPanel.setHTML(0, 0, constants.userReportingActivateDescription());
                    }
                    myGlobalReportingActivationStateSelector.setValue(myReportingConfiguration.isGlobalReportingEnabled(), false);
//                    if (myReportingConfiguration.isGlobalReportingEnabled()) {
//                        myInnerPanel.setHTML(1, 0, constants.globalReportingDeActivateDescription());
//                    } else {
//                        myInnerPanel.setHTML(1, 0, constants.globalReportingActivateDescription());
//                    }
                    // Translate the value from ms to minutes.
                    myDashboardRefreshFrequency.setValue(String.valueOf(myReportingConfiguration.getDashBoardRefreshFrequency() / (60 * 1000)));
                    myInnerPanel.setHTML(2, 0, constants.dashboardRefreshFrequency());

                    myStepAtRiskNbOfDays.setValue(String.valueOf(myReportingConfiguration.getRemainingDaysForAtRiskSteps()));
                    myInnerPanel.setHTML(3, 0, constants.stepAtRiskThreshold());

                    // layout.
                    myInnerPanel.setWidget(0, 1, myUserReportingActivationStateSelector);
//                    myInnerPanel.setWidget(1, 1, myGlobalReportingActivationStateSelector);
                    myInnerPanel.setWidget(2, 1, myDashboardRefreshFrequency);
                    myInnerPanel.setWidget(3, 1, myStepAtRiskNbOfDays);

                }

            };
        });

    }

    @Override
    public void updateContent() {
        updateMyGeneralPanel();

    }
}
