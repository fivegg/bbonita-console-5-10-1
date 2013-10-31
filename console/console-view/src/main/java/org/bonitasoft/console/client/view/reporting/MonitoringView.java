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
package org.bonitasoft.console.client.view.reporting;

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.reporting.ReportingDataSource;
import org.bonitasoft.console.client.model.steps.StepDefinitionDataSource;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.ReportType;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class MonitoringView extends BonitaPanel {

    protected final FlowPanel myOuterPanel;
    protected FlexTable myInnerPanel;

    
    protected final static ReportItem[] bonitaDefaultReport = new ReportItem[] { new ReportItem("admin (default)", "admin.rptdesign", null, ReportType.BIRT, ReportScope.ADMIN, false) };
    protected static ReportItem[] bonitaReports;

    protected int myNbOfColumn;

    protected final CustomDialogBox myConfigurationDialog = new CustomDialogBox(true, true);
    // protected final HTML myRefreshLink;
    protected final Label myConfigureLink;
    protected final FlowPanel myLinkPanel = new FlowPanel();

    protected final ReportingDataSource myReportingDataSource;

    protected final UserDataSource myUserDataSource;

    protected final ProcessDataSource myProcessDataSource;

    protected final StepDefinitionDataSource myStepDefinitionDataSource;

    int nbReportAdded = 0;

    private void queryNextReport(int aRow, int aCol, int aReportIndex) {
        // Go to a new line every myNbOfColumn report.
        if (aCol >= (myNbOfColumn - 1)) {
            queryReport(aRow + 1, 0, aReportIndex + 1);
        } else {
            queryReport(aRow, aCol + 1, aReportIndex + 1);
        }
    }

    /**
     * Default constructor.
     * 
     * @param aDataModel
     */
    public MonitoringView(final ReportingDataSource aDataSource, final UserDataSource aUserDataSource, final ProcessDataSource aProcessDataSource, final StepDefinitionDataSource aStepDefinitionDataSource) {
        super();
        myReportingDataSource = aDataSource;
        myUserDataSource = aUserDataSource;
        myProcessDataSource = aProcessDataSource;
        myStepDefinitionDataSource = aStepDefinitionDataSource;
        myNbOfColumn = 1;

        myReportingDataSource.addModelChangeListener(ReportingDataSource.MONITORING_VIEW_CONFIGURATION_PROPERTY, new ModelChangeListener() {

            public void modelChange(ModelChangeEvent evt) {
                queryReports();
            }
        });

        buildConfigurationDialog();

        // Create the links.
        myConfigureLink = new Label(constants.configure());
        myConfigureLink.setStyleName(CSSClassManager.LINK_LABEL);
        myConfigureLink.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent arg0) {
                myConfigurationDialog.center();
            }
        });

        // myRefreshLink = new HTML(constants.refreshIcon());
        // myRefreshLink.setTitle(constants.refresh());
        // myRefreshLink.setStyleName(CSSClassManager.LINK_LABEL);
        //
        // myRefreshLink.addClickHandler(new ClickHandler() {
        // public void onClick(ClickEvent anEvent) {
        // queryReports();
        // }
        // });

        myOuterPanel = new FlowPanel();
        VerticalPanel descriptionPanel = new VerticalPanel();
        descriptionPanel.add(new HTML(constants.monitoringViewDescriptionPanel()));
        descriptionPanel.setStyleName("descriptionPanel");
        myOuterPanel.add(descriptionPanel);
 
        myInnerPanel = new FlexTable();
        myInnerPanel.getFlexCellFormatter().setColSpan(0, 0, myNbOfColumn);
        myInnerPanel.setWidth("100%");

        myOuterPanel.add(myInnerPanel);
        this.initWidget(myOuterPanel);
    }

    protected void buildConfigurationDialog() {
        myConfigurationDialog.setText(constants.monitoringViewConfigurationPanelDescription());
        VerticalPanel theDialogContent = new VerticalPanel();
        theDialogContent.add(new MonitoringViewConfigurationWidget(myReportingDataSource));
        final Button theOkButton = new Button(constants.close(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                myConfigurationDialog.hide();

            }
        });
        theDialogContent.add(theOkButton);
        theDialogContent.setCellHorizontalAlignment(theOkButton, HasHorizontalAlignment.ALIGN_CENTER);
        myConfigurationDialog.add(theDialogContent);
    }

    @Override
    public String getLocationLabel() {
        return constants.dashboard();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        queryReports();
    }

    protected void queryReports() {
        myReportingDataSource.listDesignToDisplayInMonitoringView(new AsyncHandler<List<ReportItem>>() {
            public void handleFailure(Throwable t) {              
                GWT.log("Unable to list reports to display in monitoring view!", t);
            };

            public void handleSuccess(List<ReportItem> result) {
                if (result != null && result.size() > 0) {
                    bonitaReports = new ReportItem[result.size()];
                    for (int i = 0; i < result.size(); i++) {
                        bonitaReports[i] = result.get(i);
                    }
                } else {                  
                    bonitaReports = bonitaDefaultReport;
                    GWT.log("No reports to display in monitoring view: display default report");
                }
                myInnerPanel.clear();
                queryReport(1, 0, 0);
                nbReportAdded = 0;
                myLinkPanel.clear();
                // myLinkPanel.add(myRefreshLink);
                myLinkPanel.add(myConfigureLink);
                myInnerPanel.setWidget(bonitaReports.length + 2, 0, myLinkPanel);
                myInnerPanel.getCellFormatter().setHorizontalAlignment(bonitaReports.length + 2, 0, HasHorizontalAlignment.ALIGN_CENTER);
            };
        });
    }

    protected void queryReport(int aRow, int aCol, int aReportIndex) {

        if (aReportIndex < bonitaReports.length) {
            final ReportItem theReportItem = bonitaReports[aReportIndex];
            final FlowPanel theReportWrapper = new FlowPanel();
            theReportWrapper.setStylePrimaryName("bos_report_wrapper");
            final Label theReportNameSectionTitle = new Label(theReportItem.getUUID().getValue());
            theReportNameSectionTitle.setStylePrimaryName(CSSClassManager.TITLE_STYLE);
            theReportWrapper.add(theReportNameSectionTitle);            
            
            if (theReportItem.getConfigurationElements().isEmpty()){
                theReportWrapper.add(new ReportParametersEditorWidget(myReportingDataSource, theReportItem, myUserDataSource, myProcessDataSource, myStepDefinitionDataSource, nbReportAdded, true));
            } else {
                if (myReportingDataSource.getParameters(nbReportAdded) == null || myReportingDataSource.getParameters(nbReportAdded).isEmpty()) {                
                    myReportingDataSource.setParameters(nbReportAdded, new HashMap<String, String>());
                    theReportWrapper.add(new ReportParametersEditorWidget(myReportingDataSource, theReportItem, myUserDataSource, myProcessDataSource, myStepDefinitionDataSource, nbReportAdded, false));
                } else {
                    theReportWrapper.add(new ReportParametersEditorWidget(myReportingDataSource, theReportItem, myUserDataSource, myProcessDataSource, myStepDefinitionDataSource, nbReportAdded, true));
                }                
            }

            nbReportAdded++;
            queryNextReport(aRow, aCol, aReportIndex);

            myInnerPanel.setWidget(aRow, aCol, theReportWrapper);
        }
    }
}
