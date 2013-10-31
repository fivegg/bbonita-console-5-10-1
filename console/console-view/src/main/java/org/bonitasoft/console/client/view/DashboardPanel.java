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

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.reporting.ReportingDataSource;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.reporting.ReportingConfiguration;
import org.bonitasoft.console.client.users.UserProfile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class DashboardPanel extends HideablePanel implements ModelChangeListener {

    protected static final String loadingHTML = constants.loading();
    protected HTML myReportHTML = new HTML();
    protected VerticalPanel myOuterPanel = new VerticalPanel();
    protected UserProfile myUserProfile;
    protected Hyperlink myMoreLink;
    protected final HTML myRefreshLink;
    protected CaseDataSource myCaseDataSource;
    private int myOnGoingRequests = 0;
    protected int nbOfErrors = 0;
    private final Timer myUpdateTimer = new Timer() {
        public void run() {
            updateStats();
        };
    };
    protected final ReportingDataSource myReportingDataSource;

    /**
     * 
     * Default constructor.
     */
    public DashboardPanel(UserProfile aUserProfile, ReportingDataSource aReportingDataSource) {
        super();
        myUserProfile = aUserProfile;
        myReportingDataSource = aReportingDataSource;
        myUserProfile.addModelChangeListener(UserProfile.PREFERED_REPORT_NAME, this);
        myReportingDataSource.addModelChangeListener(ReportingDataSource.REPORTING_CONFIGURATION_PROPERTY, this);

        buildMoreElement();
        myRefreshLink = new HTML(constants.refreshIcon());
        myRefreshLink.setTitle(constants.refresh());
        myRefreshLink.setStyleName(CSSClassManager.LINK_LABEL);

        myRefreshLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent anEvent) {
                updateStats();
            }
        });
        // Layout widgets.
        myOuterPanel.add(myReportHTML);
        FlowPanel theLinkPanel = new FlowPanel();
        theLinkPanel.add(myMoreLink);
        theLinkPanel.add(myRefreshLink);
        myRefreshLink.addStyleName("bos_float_right");
        myMoreLink.addStyleName("bos_float_right");
        myOuterPanel.add(theLinkPanel);

        this.initWidget(myOuterPanel);
    }

    /**
     * Make the stats to be reloaded.
     */
    public void updateStats() {
        if (myOnGoingRequests == 0) {
            myOnGoingRequests++;

            myReportHTML.setHTML(loadingHTML);

            ReportUUID theReportUUID = myUserProfile.getDefaultReportUUID();
            if (theReportUUID == null) {
                displayReport(null);
            } else {
                myReportingDataSource.getItem(theReportUUID, new AsyncHandler<ReportItem>() {
                    public void handleFailure(Throwable aT) {
                        displayReport(null);
                    }

                    public void handleSuccess(ReportItem result) {
                        displayReport(result);
                    };
                });
            }

        }
    }

    /**
     * @param aResult
     */
    protected void displayReport(ReportItem aResult) {
        try {
            RequestBuilder theRequestBuilder = new RequestBuilder(RequestBuilder.GET, myReportingDataSource.buildReportURL(aResult, ReportScope.USER));
            theRequestBuilder.setCallback(new RequestCallback() {
                public void onError(Request aRequest, Throwable anException) {
                    if (anException instanceof SessionTimeOutException) {
                        // reload the page.
                        Window.Location.reload();
                    }
                    myReportHTML.setHTML("");
                    myOnGoingRequests--;
                    nbOfErrors++;
                    GWT.log("Unable to display report (error count:" + nbOfErrors + ")", anException);
                    if (nbOfErrors >= 3) {
                        GWT.log("Disabling recurrent call after too many consecutive errors.", null);
                        myUpdateTimer.cancel();
                    }
                }

                public void onResponseReceived(Request aRequest, Response aResponse) {
                    if (1 == myOnGoingRequests) {
                        myReportHTML.setHTML("");
                        if (aResponse.getStatusCode() == Response.SC_OK) {
                            myReportHTML.setHTML(aResponse.getText());
                            nbOfErrors = 0;
                        } else {
                            myReportHTML.setHTML(constants.unableToDisplayReport());
                            nbOfErrors++;
                            GWT.log("Unable to display report (error count:" + nbOfErrors + ") " + aResponse.getText(), null);
                            if (nbOfErrors >= 3) {
                                GWT.log("Disabling recurrent call after too many errors.", null);
                                myUpdateTimer.cancel();
                            }
                        }
                    } else {
                        GWT.log("Skipping report result as there is still " + (myOnGoingRequests - 1) + " requests in the pipe.", null);
                    }
                    myOnGoingRequests--;
                }
            });
            GWT.log("RPC: querying reporting", null);
            theRequestBuilder.send();
        } catch (RequestException e) {
            myReportHTML.setHTML("");
        }

    }

    protected void buildMoreElement() {
        myMoreLink = new Hyperlink(constants.more() + " >>", ViewToken.UserStat.name());
        myMoreLink.setStylePrimaryName(CSSClassManager.LINK_LABEL);
    }

    public void modelChange(ModelChangeEvent anEvt) {
        if (UserProfile.PREFERED_REPORT_NAME.equals(anEvt.getPropertyName())) {
            // The user profile has been updated. Render the new report.
            updateStats();
        }
        if (ReportingDataSource.REPORTING_CONFIGURATION_PROPERTY.equals(anEvt.getPropertyName())) {
            if (anEvt.getNewValue() != null) {
                int theFrequency = ((ReportingConfiguration) anEvt.getNewValue()).getDashBoardRefreshFrequency();
                myUpdateTimer.cancel();
                // reschedule the timer.
                if (theFrequency > 0) {
                    myUpdateTimer.scheduleRepeating(theFrequency);
                }

            }
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();

    }

    @Override
    public void showPanel() {
        myUserProfile.addModelChangeListener(UserProfile.PREFERED_REPORT_NAME, this);

        myReportingDataSource.getReportingConfiguration(new AsyncHandler<ReportingConfiguration>() {
            public void handleFailure(Throwable anT) {
                myUpdateTimer.cancel();
            }

            public void handleSuccess(ReportingConfiguration anResult) {
                if (anResult != null) {
                    if (anResult.getDashBoardRefreshFrequency() > 0) {
                        // select the max between global settings set by the
                        // admin and the preference of the user.
                        int theRefreshTime = myUserProfile.getPreferredStatRefreshDelay();
                        myUpdateTimer.scheduleRepeating((anResult.getDashBoardRefreshFrequency() > theRefreshTime ? anResult.getDashBoardRefreshFrequency() : theRefreshTime));
                    } else {
                        myUpdateTimer.cancel();
                    }
                    // Create a graphical view of the statistics
                    updateStats();
                }
            }
        });
    }

    @Override
    public void hidePanel() {
        myUserProfile.removeModelChangeListener(UserProfile.PREFERED_REPORT_NAME, this);
        myUpdateTimer.cancel();

    }
}
