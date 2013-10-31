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
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.reporting.ReportingDataSource;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.ReportingConfiguration;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.identity.UserInfoEditorPanel;
import org.bonitasoft.console.security.client.privileges.RuleType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserSettingsEditionView extends BonitaPanel {

    protected static final String loadingHTML = constants.loading();

    protected final FlowPanel myOuterPanel;
    protected final DecoratedTabPanel myInnerPanel;
    protected final CustomMenuBar mySaveButton = new CustomMenuBar();
    protected final FlexTable myReportingPrefInnerTabPanel;
    protected final MessageDataSource myMessageDataSource;
    protected final UserDataSource myUserDataSource;
    protected final UserProfile myUserProfile;
    protected final ReportingDataSource myReportDataSource;

    protected ReportItem mySelectedReport;

    protected HTML myCurrentlySelectedContainer;

    protected final FlowPanel myReportListPanel = new FlowPanel();

    protected UserInfoEditorPanel myUserInfoPrefInnerTabPanel;
    protected final CustomMenuBar mySaveUserProfileButton = new CustomMenuBar();

    /**
     * Default constructor.
     */
    public UserSettingsEditionView(UserProfile aUserProfile, MessageDataSource aMessageDataSource, UserDataSource aUserDataSource, ReportingDataSource aReportingDataSource) {
        super();
        // Store the model.
        myMessageDataSource = aMessageDataSource;
        myUserDataSource = aUserDataSource;
        myUserProfile = aUserProfile;
        myReportDataSource = aReportingDataSource;

        // Create the save button.
        mySaveButton.addItem(constants.save(), new Command() {
            public void execute() {
                saveSettings();
            }
        });

        mySaveUserProfileButton.addItem(constants.save(), new Command() {
            public void execute() {
                saveUserProfile();
            }
        });

        myOuterPanel = new FlowPanel();
        myInnerPanel = new DecoratedTabPanel();

        final User theUser = myUserProfile.getUser();
        /*
         * If the user is not available in the profile, do not build user info
         * editor panel as no data is available.
         */
        if (theUser != null) {
            myUserDataSource.getItem(theUser.getUUID(), new AsyncHandler<User>() {
                public void handleFailure(Throwable aT) {
                    // Do nothing.

                }

                public void handleSuccess(User aResult) {
                    if (aResult != null) {
                        myUserInfoPrefInnerTabPanel = buildUserInfoPanel(aResult.getUUID());
                        myInnerPanel.insert(myUserInfoPrefInnerTabPanel, constants.userInfoTabName(), 0);
                        myInnerPanel.selectTab(0);
                    }
                }
            });

        }

        myReportingPrefInnerTabPanel = new FlexTable();
        myReportingPrefInnerTabPanel.setHTML(0, 0, constants.reportingPrefTabDescription());
        myReportingPrefInnerTabPanel.getFlexCellFormatter().setColSpan(0, 0, 2);
        initReportingPrefPanel();
        myInnerPanel.add(myReportingPrefInnerTabPanel, constants.reportingTabName());

        myOuterPanel.add(myInnerPanel);
        myOuterPanel.setStylePrimaryName("user_settings_outer_panel");
        myInnerPanel.setStylePrimaryName("user_settings_tab_panel");

        myInnerPanel.selectTab(0);

        this.initWidget(myOuterPanel);
    }

    /**
     * @return
     */
    private UserInfoEditorPanel buildUserInfoPanel(UserUUID aUserUUID) {

        final UserInfoEditorPanel theUserInfoEditor = new UserInfoEditorPanel(myUserDataSource) {
            @Override
            protected FlexTable buildContent() {
                myDelegateUserViewer.setEnabled((myUserProfile != null && myUserProfile.isAllowed(RuleType.DELEGEE_UPDATE)));
                myFirstNameTextBox.setEnabled(false);
                myJobTitleTextBox.setEnabled(false);
                myLastNameTextBox.setEnabled(false);
                myManagerUserViewer.setEnabled(false);
                myTitleTextBox.setEnabled(false);
                myUsernameTextBox.setEnabled(false);
                FlexTable theTable = super.buildContent();
                if (myUserProfile != null && myUserProfile.isAllowed(RuleType.PASSWORD_UPDATE)) {
                    myPasswordConfirmTextBox.setEnabled(true);
                    myPasswordTextBox.setEnabled(true);
                    int theActionRow = theTable.getRowCount();
                    theTable.setWidget(theActionRow, 0, mySaveUserProfileButton);
                    theTable.getFlexCellFormatter().setColSpan(theActionRow, 0, 2);
                    theTable.getFlexCellFormatter().setHorizontalAlignment(theActionRow, 0, HasHorizontalAlignment.ALIGN_CENTER);
                } else {
                    myPasswordConfirmTextBox.setEnabled(false);
                    myPasswordTextBox.setEnabled(false);
                    theTable.remove(mySaveUserProfileButton);
                }
                return theTable;
            }
        };

        theUserInfoEditor.update(aUserUUID);
        return theUserInfoEditor;
    }

    protected void initReportingPrefPanel() {

        myReportingPrefInnerTabPanel.setWidget(1, 0, myReportListPanel);
        myReportingPrefInnerTabPanel.getFlexCellFormatter().setColSpan(1, 0, 2);
        final FlowPanel theButtonsPanel = new FlowPanel();
        Label theRefreshLink = new HTML(constants.refreshIcon());
        theRefreshLink.setTitle(constants.refresh());
        theRefreshLink.setStyleName(CSSClassManager.LINK_LABEL);
        theRefreshLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent anEvent) {
                updateStats();
            }
        });

        theButtonsPanel.add(theRefreshLink);
        theButtonsPanel.add(mySaveButton);
        myReportingPrefInnerTabPanel.setWidget(2, 0, theButtonsPanel);
        myReportingPrefInnerTabPanel.getFlexCellFormatter().setColSpan(2, 0, 2);
        myReportingPrefInnerTabPanel.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
        updateStats();
    }

    private void updateStats() {
        myReportListPanel.clear();
        myReportDataSource.getReportingConfiguration(new AsyncHandler<ReportingConfiguration>() {
            public void handleFailure(Throwable anT) {
                myReportListPanel.add(new HTML(constants.userReportingIsDisabledExplanation()));
            }

            public void handleSuccess(ReportingConfiguration anResult) {
                if (anResult != null) {
                    if (anResult.isUserReportingEnabled()) {
                        final ReportFilter theFilter = new ReportFilter(0, 20);
                        theFilter.setScope(ReportScope.USER);
                        myReportDataSource.listItems(theFilter, new AsyncHandler<ItemUpdates<ReportItem>>() {
                            public void handleFailure(Throwable anT) {

                            }

                            public void handleSuccess(ItemUpdates<ReportItem> anResult) {
                                FlowPanel theRow = null;
                                HTML theCell;
                                int theCol = 0;
                                // Layout the reports 2 per rows.
                                for (ReportItem theReportItem : anResult.getItems()) {
                                    if (theReportItem.getFileName().matches(".*Small.*")) {
                                        if (theCol % 2 == 0) {
                                            theRow = new FlowPanel();
                                            theRow.setStyleName("reporting_block");
                                            myReportListPanel.add(theRow);
                                        }
                                        theCell = new HTML();
                                        theCell.setStyleName("small_report_item");
                                        theRow.add(theCell);
                                        buildSelectableReport(theReportItem, theCell);
                                        theCol++;
                                    }
                                }
                            }
                        });
                    } else {
                        myReportListPanel.add(new HTML(constants.userReportingIsDisabledExplanation()));
                    }
                }
            }
        });

    }

    private void buildSelectableReport(final ReportItem aReportItem, final HTML aContainer) {
        if (aContainer == null) {
            GWT.log("Container must not be null. Exit.", null);
            return;
        }

        try {
            if (aReportItem == null || aReportItem.getFileName() == null || aReportItem.getFileName().length() == 0) {
                GWT.log("Report name must neither be null nor empty. Exit.", null);
                return;
            }
            if (aReportItem.getUUID().equals(myUserProfile.getDefaultReportUUID())
                    || (myUserProfile.getDefaultReportUUID() == null && aReportItem.getUUID().equals(ConsoleConstants.DEFAULT_REPORT_UUID))) {
                // Select the report if it is the one used by the user.
                selectReportContainer(aContainer);
            }
            RequestBuilder theRequestBuilder = new RequestBuilder(RequestBuilder.GET, buildReportURL(aReportItem));
            theRequestBuilder.setCallback(new RequestCallback() {
                public void onError(Request aRequest, Throwable anException) {
                    aContainer.setHTML(constants.unableToDisplayReport());
                }

                public void onResponseReceived(Request aRequest, Response aResponse) {
                    if (aResponse.getStatusCode() == Response.SC_OK) {
                        aContainer.setHTML(aResponse.getText());
                    } else {
                        aContainer.setHTML(constants.unableToDisplayReport());
                    }
                    aContainer.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent anEvent) {
                            selectReportContainer(aContainer);
                            mySelectedReport = aReportItem;
                        }

                    });
                }
            });
            aContainer.setHTML(loadingHTML);
            theRequestBuilder.send();
        } catch (RequestException e) {
            aContainer.setHTML(constants.unableToDisplayReport());
        }

    }

    protected String buildReportURL(ReportItem aReportItem) {
        final StringBuffer theURL = new StringBuffer(GWT.getModuleBaseURL()).append("bam/run?");
        theURL.append("ReportId=").append(aReportItem.getUUID());
        theURL.append("&ReportType=").append(aReportItem.getType().name());
        theURL.append("&ReportScope=").append(aReportItem.getScope().name());
        return theURL.toString();
    }

    private void selectReportContainer(HTML aContainer) {
        if (myCurrentlySelectedContainer != null) {
            myCurrentlySelectedContainer.removeStyleName("selected");
        }
        if (aContainer != null) {
            aContainer.addStyleName("selected");
        }
        myCurrentlySelectedContainer = aContainer;
    }

    @Override
    public String getLocationLabel() {
        return constants.userSettings();
    }

    protected void saveSettings() {
        myOuterPanel.addStyleName("loading");
        myUserDataSource.updateDefaultReportUUID(myUserProfile, mySelectedReport.getUUID(), new AsyncHandler<Void>() {
            public void handleFailure(Throwable anT) {
                myOuterPanel.removeStyleName("loading");

            }

            public void handleSuccess(Void anResult) {
                myOuterPanel.removeStyleName("loading");
            }
        });
    }

    protected void saveUserProfile() {
        if (myUserInfoPrefInnerTabPanel != null && myUserInfoPrefInnerTabPanel.validate() && myUserProfile.getUser() != null) {
            final User updatedUser = myUserInfoPrefInnerTabPanel.getUserInfo();
            myOuterPanel.addStyleName("loading");
            myUserDataSource.updateItem(myUserProfile.getUser().getUUID(), updatedUser, new AsyncHandler<User>() {

                public void handleFailure(Throwable anT) {
                    myOuterPanel.removeStyleName("loading");

                }

                public void handleSuccess(User aResult) {
                    myOuterPanel.removeStyleName("loading");
                    myMessageDataSource.addInfoMessage(messages.userProfileUpdated());
                }
            });
        }
    }
}
