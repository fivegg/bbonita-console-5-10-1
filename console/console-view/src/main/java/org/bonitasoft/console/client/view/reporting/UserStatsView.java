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

import java.util.ArrayList;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.reporting.ReportingDataSource;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.ReportType;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomMenuBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserStatsView extends BonitaPanel {

    protected FlexTable myOuterPanel;

    protected final CustomMenuBar myReloadButton = new CustomMenuBar();

    protected final String myLoadingMessage = constants.loading();

    protected final FlowPanel myReportListPanel = new FlowPanel();

    protected FlowPanel myCurrentRow = null;

    protected final ReportingDataSource myReportingDataSource;

    protected ReportFilter myReportFilter = new ReportFilter(0, 100);

    protected ReportItem[] bonitaReports;
    
    protected boolean isFinished = false;

    private class BonitaReportRequestCallback implements RequestCallback {

        protected int myRow;
        protected int myCol;
        private int myReportIndex;
        private HTML myCell;

        public BonitaReportRequestCallback(int aRow, int aCol, int aReportIndex, HTML aCell) {
            super();
            myRow = aRow;
            myCol = aCol;
            myReportIndex = aReportIndex;
            myCell = aCell;
        }

        public void onError(Request anRequest, Throwable anException) {
            myCell.setHTML(constants.errorProcessingReport());
        }

        public void onResponseReceived(Request aRequest, Response aResponse) {
            // myOuterPanel.remove(myLoadingMessage);
            HTML theReport = new HTML();
            if (aResponse.getStatusCode() == Response.SC_OK) {
                theReport.setHTML(aResponse.getText());

            } else {
                theReport.setHTML(constants.unableToDisplayReport());
                GWT.log("Unable to display report" + aResponse.getText(), null);
            }
            myCell.setHTML(theReport.getHTML());
            theReport.setStyleName("bonita_report");

            // Go to a new line every 2 report.
            if (myCol == 1) {
                queryReport(myRow + 1, 0, myReportIndex + 1);
            } else {
                queryReport(myRow, myCol + 1, myReportIndex + 1);
            }
            if ((myReportIndex + 1)== (bonitaReports.length - 1)) {
            	
            }
        }
    }

    /**
     * Default constructor.
     * 
     * @param aDataModel
     */
    public UserStatsView(ReportingDataSource aDataSource) {
        super();
        myOuterPanel = new FlexTable();
        myReportingDataSource = aDataSource;

        myReportFilter.setScope(ReportScope.USER);
     
        // Create the reload button.
        myReloadButton.addItem(constants.refresh(), new Command() {
            public void execute() {
                refresh();
            }
        });
        myReloadButton.setVisible(false);
        myOuterPanel.setStyleName("user_report_container");
        myOuterPanel.setWidget(0, 0, myReportListPanel);
        myOuterPanel.setWidget(1, 0, myReloadButton);
        myOuterPanel.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
        this.initWidget(myOuterPanel);
    }

    protected void refresh() {
    	myReloadButton.setVisible(false);
    	queryReports();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        queryReports();
    }

    private void queryReports() {
        myReportListPanel.clear();
        myReportingDataSource.listItems(myReportFilter, new AsyncHandler<ItemUpdates<ReportItem>>() {

            public void handleSuccess(ItemUpdates<ReportItem> aResult) {
                final ArrayList<ReportItem> theReports = aResult.getItems();
                if (theReports != null) {
                    final ArrayList<ReportItem> theReportsToDisplay = new ArrayList<ReportItem>(theReports.size());
                    for (ReportItem theReportItem : theReports) {
                        // Filter Small reports
                        if(!theReportItem.getFileName().matches(".*Small.*")) {
                            theReportsToDisplay.add(theReportItem);            
                        }
                    }
                    bonitaReports = new ReportItem[theReportsToDisplay.size()];
                    theReportsToDisplay.toArray(bonitaReports);
                    queryReport(0, 0, 0);
                }
            }

            public void handleFailure(Throwable aT) {
                // Display default reports.
                bonitaReports = new ReportItem[] { new ReportItem("Logged user steps per priority", "userStepsPerPriority.rpttemplate", null, ReportType.BIRT, ReportScope.USER, false),
                        new ReportItem("Logged user steps by status", "userStepsReadyOverdue.rptdesign", null, ReportType.BIRT, ReportScope.USER, false),
                        new ReportItem("Logged user workload by priority", "userPerfs.rptdesign", null, ReportType.BIRT, ReportScope.USER, false) };
                queryReport(0, 0, 0);
            }
        });

    }

    private void queryReport(int aRow, int aCol, int aReportIndex) {
        final RequestBuilder theRequestBuilder;
        if (aReportIndex < bonitaReports.length) {
            GWT.log("Calling report generation for: " + bonitaReports[aReportIndex].getUUID().getValue());
            try {

                final HTML theCell;
                if (aCol % 2 == 0) {
                    myCurrentRow = new FlowPanel();
                    myCurrentRow.setStyleName("reporting_block");
                    myReportListPanel.add(myCurrentRow);
                }
                theCell = new HTML();
                theCell.setHTML(myLoadingMessage);
                theCell.setStyleName("report_item");
                myCurrentRow.add(theCell);
                theRequestBuilder = new RequestBuilder(RequestBuilder.GET, myReportingDataSource.buildReportURL(bonitaReports[aReportIndex], ReportScope.USER));
                theRequestBuilder.setCallback(new BonitaReportRequestCallback(aRow, aCol, aReportIndex, theCell));
                theRequestBuilder.send();
            } catch (RequestException e) {
                GWT.log("Error while trying to query the reports:" + e.getMessage(), e);
            }
        } else {
        	myReloadButton.setVisible(true);
        }
    }

    @Override
    public String getLocationLabel() {
        return constants.dashboard();
    }
}
