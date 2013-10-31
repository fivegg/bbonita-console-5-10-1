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
package org.bonitasoft.console.client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.identity.Group;
import org.bonitasoft.console.client.identity.Role;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.ItemSelection;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.AboutWidget;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.LocaleChooserWidget;
import org.bonitasoft.console.client.view.LogoutWidget;
import org.bonitasoft.console.client.view.NavigationLinkWidget;
import org.bonitasoft.console.client.view.UserIDWidget;
import org.bonitasoft.console.client.view.UserPreferencesWidget;
import org.bonitasoft.console.client.view.WidgetFactory.WidgetKey;
import org.bonitasoft.console.client.view.cases.AbstractCaseEditorWidget;
import org.bonitasoft.console.client.view.identity.GroupEditorPanel;
import org.bonitasoft.console.client.view.identity.RoleEditorPanel;
import org.bonitasoft.console.client.view.identity.UserEditor;
import org.bonitasoft.console.client.view.processes.ProcessEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This Class defines the main controller of the entire GWT application. It is responsible for the interaction between the
 * different widgets spread all over the window.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class AdminViewController extends AbstractViewController implements ValueChangeHandler<String>, ModelChangeListener {

    protected static final String PROCESSES_LINK_CONTAINER_ELEM_ID = "bos_ProcessesLinkContainer";
    protected static final String CASE_LISTS_CONFIGURATION_LINK_CONTAINER_ELEM_ID = "bos_CaseListsConfigurationLinkContainer";
    protected static final String REPORTING_SETTINGS_LINK_CONTAINER_ELEM_ID = "bos_GlobalSettingsLinkContainer";
    protected static final String LABELS_SETTINGS_LINK_CONTAINER_ELEM_ID = "bos_LabelsSettingsLinkContainer";
    protected static final String SYNC_DB_LINK_CONTAINER_ELEM_ID = "bos_SyncDBLinkContainer";
    protected static final String CASES_LINK_CONTAINER_ELEM_ID = "bos_CasesLinkContainer";
    protected static final String REPORT_DEPLOYMENT_LINK_CONTAINER_ELEM_ID = "bos_ReportDeploymentLinkContainer";
    protected static final String REPORTING_LINK_CONTAINER_ELEM_ID = "bos_ReportingLinkContainer";
    protected static final String USERS_LINK_CONTAINER_ELEM_ID = "bos_UsersLinkContainer";
    protected static final String PRIVILEGES_LINK_CONTAINER_ELEM_ID = "bos_PrivilegesLinkContainer";
    protected static final String CATEGORIES_LINK_CONTAINER_ELEM_ID = "bos_CategoriesLinkContainer";
    protected static final String REMOTE_DEPLOYMENT_LINK_CONTAINER_ELEM_ID = "bos_RemoteDeploymentLinkContainer";
    protected static final String LICENSE_LINK_CONTAINER_ELEM_ID = "bos_LicenseLinkContainer";

    protected static final String PROCESSES_MENU_TITLE_CONTAINER_ELEM_ID = "bos_ProcessesMenuHeaderTitle";
    protected static final String ORGANIZATION_MENU_TITLE_CONTAINER_ELEM_ID = "bos_OrganizationMenuHeaderTitle";
    protected static final String REPORTING_MENU_TITLE_CONTAINER_ELEM_ID = "bos_ReportingMenuHeaderTitle";
    protected static final String USER_LABELS_MENU_TITLE_CONTAINER_ELEM_ID = "bos_UserLabelMenuHeaderTitle";
    protected static final String THEMES_MENU_TITLE_CONTAINER_ELEM_ID = "bos_ThemeMenuHeaderTitle";
    protected static final String SERVER_MENU_TITLE_CONTAINER_ELEM_ID = "bos_ServerMenuHeaderTitle";    

    protected static final AdminViewController INSTANCE = new AdminViewController();

    protected BonitaPanel myAdminWidget;
    protected CaseFilter myAdminCaseFilter;

    protected static HashMap<String, String> MENUS;

    /**
     * Get the ViewController instance.
     * 
     * @return the unique instance of the ViewController.
     */
    public static AdminViewController getInstance() {
        return INSTANCE;
    }

    protected AdminViewController() {
        super();
    }

    @Override
    protected void createRefreshCaseListLink() {

        // Create the refresh widget
        myRefreshCaseListWidget = new Label();
        myRefreshCaseListWidget.setVisible(false);
        myRefreshCaseListWidget.addClickHandler(new ClickHandler() {

            /**
             * {@inheritDoc}
             */
            public void onClick(ClickEvent aArg0) {

                myDataModel.getCaseDataSource().getItem(new CaseUUID(getItemUUIDFromTokenParam(myCurrentTokenParam)), new AsyncHandler<CaseItem>() {
                    public void handleFailure(Throwable aT) {
                        redirectToCaseList();
                    }

                    public void handleSuccess(CaseItem aResult) {
                        if (aResult == null) {
                            redirectToCaseList();
                        }
                    }

                    private void redirectToCaseList() {
                        History.newItem(ViewToken.AdminCaseList.toString());
                    }
                });
            }
        });

    }

    protected void buildListOfLinks() {
        PARENT_MENUS = new HashSet<String>();
        CONTAINERS = new ArrayList<String>();
        LABELS = new ArrayList<String>();
        KEYS = new ArrayList<String>();
        MENUS = new HashMap<String, String>();
        final UserRightsManager theUserRightsManager = UserRightsManager.getInstance();
        if (theUserRightsManager.hasAccessToProcessesManagement()) {
            PARENT_MENUS.add("bos_ProcessesMenu");
            CONTAINERS.add(PROCESSES_LINK_CONTAINER_ELEM_ID);
            LABELS.add(constants.processes());
            KEYS.add(ViewToken.Processes.name());
            MENUS.put(PROCESSES_MENU_TITLE_CONTAINER_ELEM_ID, constants.processesNavigationMenuTitle());
        }
        if (theUserRightsManager.hasAccessToAdminCaseList()) {
            PARENT_MENUS.add("bos_ProcessesMenu");
            CONTAINERS.add(CASES_LINK_CONTAINER_ELEM_ID);
            LABELS.add(constants.cases());
            KEYS.add(ViewToken.AdminCaseList.name());
            MENUS.put(PROCESSES_MENU_TITLE_CONTAINER_ELEM_ID, constants.processesNavigationMenuTitle());
        }
        if (theUserRightsManager.hasAccessToReporting()) {
            PARENT_MENUS.add("bos_ReportingMenu");
            CONTAINERS.add(REPORTING_LINK_CONTAINER_ELEM_ID);
            LABELS.add(constants.reportingTabName());
            KEYS.add(ViewToken.Reporting.name());
            MENUS.put(REPORTING_MENU_TITLE_CONTAINER_ELEM_ID, constants.reportingNavigationMenuTitle());
        }
        if (theUserRightsManager.hasAccessToUsersManagement()) {
            PARENT_MENUS.add("bos_OrganizationMenu");
            CONTAINERS.add(USERS_LINK_CONTAINER_ELEM_ID);
            LABELS.add(constants.usersManagement());
            KEYS.add(ViewToken.UsersManagement.name());
            MENUS.put(ORGANIZATION_MENU_TITLE_CONTAINER_ELEM_ID, constants.organizationNavigationMenuTitle());
        }

        if (theUserRightsManager.hasAccessToCategoriesManagement()) {
            PARENT_MENUS.add("bos_ProcessesMenu");
            CONTAINERS.add(CATEGORIES_LINK_CONTAINER_ELEM_ID);
            LABELS.add(constants.categoriesTabName());
            KEYS.add(ViewToken.CategoriesManagement.name());
            MENUS.put(PROCESSES_MENU_TITLE_CONTAINER_ELEM_ID, constants.processesNavigationMenuTitle());
        }
        if (theUserRightsManager.hasAccessToSettingsManagement()) {
            PARENT_MENUS.add("bos_ReportingMenu");
            CONTAINERS.add(REPORTING_SETTINGS_LINK_CONTAINER_ELEM_ID);
            LABELS.add(constants.reportingConfigurationNavigationLink());
            KEYS.add(ViewToken.GlobalConfiguration.name());
            MENUS.put(REPORTING_MENU_TITLE_CONTAINER_ELEM_ID, constants.reportingNavigationMenuTitle());
        }
        if (theUserRightsManager.hasAccessToLabelsManagement()) {
            PARENT_MENUS.add("bos_UserLabelMenu");
            CONTAINERS.add(LABELS_SETTINGS_LINK_CONTAINER_ELEM_ID);
            LABELS.add(constants.labelsTabName());
            KEYS.add(ViewToken.LabelsConfiguration.name());
            MENUS.put(USER_LABELS_MENU_TITLE_CONTAINER_ELEM_ID, constants.userLabelsNavigationMenuTitle());
        }
        if (theUserRightsManager.hasAccessToSyncManagement()) {
            PARENT_MENUS.add("bos_UserLabelMenu");
            CONTAINERS.add(SYNC_DB_LINK_CONTAINER_ELEM_ID);
            LABELS.add(constants.synchro());
            KEYS.add(ViewToken.SyncDb.name());
            MENUS.put(USER_LABELS_MENU_TITLE_CONTAINER_ELEM_ID, constants.userLabelsNavigationMenuTitle());
        }
    }

    protected String getItemUUIDFromTokenParam(String aCurrentTokenParam) {
        String theCaseId = null;
        String[] params;
        if (aCurrentTokenParam.startsWith(ConsoleConstants.HISTORY_TOKEN_PARAM_PREFIX)) {
            params = aCurrentTokenParam.split(ConsoleConstants.HISTORY_TOKEN_PARAM_PREFIX);
            if (params.length == 2) {
                theCaseId = params[1];
            }
        } else {
            params = aCurrentTokenParam.split(ConsoleConstants.JOURNAL_TOKEN_PARAM_PREFIX);
            if (params.length == 2) {
                theCaseId = params[1];
            }
        }
        return theCaseId;
    }

    /**
     * Display the view.
     */
    public void showView(UserProfile aUserProfile) {
        super.showView(aUserProfile);

        buildListOfLinks();

        // Create the filters used to manage the changes of view.
        // Filter on labels + participants (default filter for a user).
        final ArrayList<LabelUUID> theLabels = new ArrayList<LabelUUID>();
        final ArrayList<UserUUID> theParticipants = new ArrayList<UserUUID>();
        theParticipants.add(new UserUUID(aUserProfile.getUsername()));
        // Filter empty for the admin.
        theLabels.clear();
        theLabels.add(LabelModel.ADMIN_ALL_CASES.getUUID());
        myAdminCaseFilter = new CaseFilter(theLabels, null, null, null, 0, constants.defaultMaxDisplayedItems());
        myAdminCaseFilter.setMaxElementCount(20);
        myAdminCaseFilter.setWithAdminRights(true);

        // Finally layout all the widgets inside the HTML page.
        RootPanel theContainer;
        theContainer = RootPanel.get(USER_ID_ELEM_ID);
        if (theContainer != null) {
            // Create the User header, i.e., the user ID and main navigation
            // header.
            theContainer.add(new UserIDWidget(myUserProfile));
        }
        theContainer = RootPanel.get(LOCALE_CHOOSER_ELEM_ID);
        if (theContainer != null) {
            // Create the locale chooser.
            theContainer.add(new LocaleChooserWidget(myUserProfile));
        }
        theContainer = RootPanel.get(USER_PREFERENCES_ELEM_ID);
        if (theContainer != null) {
            // Create the user preference link.
            theContainer.add(new UserPreferencesWidget(myUserProfile));
        }
        theContainer = RootPanel.get(ABOUT_ELEM_ID);
        if (theContainer != null) {
            // Create the about link.
            theContainer.add(new AboutWidget(aUserProfile));
        }
        theContainer = RootPanel.get(LOGOUT_ELEM_ID);
        if (theContainer != null) {
            // Create the about link.
            theContainer.add(new LogoutWidget(myUserProfile));
        }

        // Populate menus.
        NavigationLinkWidget theLink;
        for (int i = 0; i < CONTAINERS.size(); i++) {
            theContainer = RootPanel.get(CONTAINERS.get(i));
            if (theContainer != null) {
                theLink = new NavigationLinkWidget(null, LABELS.get(i), KEYS.get(i));
                theContainer.add(theLink);
            }
        }

        // Insert menus title
        HTML theTitle;
        for (Map.Entry<String, String> theMenuTitle : MENUS.entrySet()) {
            theContainer = RootPanel.get(theMenuTitle.getKey());
            if (theContainer != null) {
                theTitle = new HTML(theMenuTitle.getValue());
                theContainer.add(theTitle);
            }
        }
        // Make menus visible.
        for (String theMenuID : PARENT_MENUS) {
            theContainer = RootPanel.get(theMenuID);
            if (theContainer != null) {
                theContainer.removeStyleName("bos_empty_menu");
            }
        }

        theContainer = RootPanel.get(YOU_ARE_HERE_ELEM_ID);
        if (theContainer != null) {
            // Create the locator widget.
            myLocatorWidget = new HTML();
            theContainer.add(myLocatorWidget);
        }
        theContainer = RootPanel.get(MESSAGE_CONTAINER_ELEM_ID);
        if (theContainer != null) {
            // Create the message widget.
            theContainer.add(myWidgetFactory.getWidget(WidgetKey.MESSAGE_KEY));
        }

        theContainer = RootPanel.get(UI_MODE_SELECTOR_ELEM_ID);
        if (theContainer != null) {
            // UI mode selector.
            theContainer.getElement().setTitle(constants.userModeTooltip());
            theContainer.getElement().setInnerHTML(constants.userModeLink());
        }

        theContainer = RootPanel.get(WIDGET_CONTAINER_CONTENT_ELEM_ID);
        if (theContainer != null) {
            // mandatory to access the WidgetContainerContent before
            // WidgetContainer
            // to avoid
            // "A widget that has an existing parent widget may not be added to the detach list"
            // kind of gwt error.
            theContainer.add(new Label());
        }

        // Set the CSS style.
        theContainer = RootPanel.get(WIDGET_CONTAINER_ELEM_ID);
        if (theContainer != null) {
            theContainer.setStylePrimaryName("center_panel");
        }

        // Add the Hidden refresh case list widget
        theContainer = RootPanel.get(WIDGET_REFRESH_CASES_CONTAINER_ELEM_ID);
        if (theContainer != null) {
            createRefreshCaseListLink();
            theContainer.add(myRefreshCaseListWidget);
        }

        // Hide the loading message and display the GUI.
        Element theElement;
        theElement = DOM.getElementById(LOADING_ELEM_ID);
        if (theElement != null) {
            theElement.getStyle().setProperty("display", "none");
        }
        theElement = RootPanel.getBodyElement();
        if (theElement != null) {
            theElement.setClassName("user_experience");
            theElement.addClassName("bos_admin_mode");
        }
        theElement = DOM.getElementById(DEFAULT_VIEW_CONTAINER_ELEM_ID);
        if (theElement != null) {
            theElement.getStyle().setProperty("display", "");
        }

        // Manage web browser history.
        // Check the History to see if there is a token
        String token = History.getToken();

        // If there is no history token then init with Inbox.
        if (token.length() != 0) {
            updateViewAccordingToToken(token);
        } else {
            // updateViewAccordingToToken(ViewToken.AdminCaseList.name());
            displayDefaultViewAccordingToPrivileges();
        }

        History.addValueChangeHandler(this);
    }

    /**
     * Update the view based on the history navigation.
     */
    protected void updateViewAccordingToToken(String aToken) {

        final String theTokenKey;
        final String theParam;
        ViewToken theViewToken;
        try {
            int theSeparatorIndex = aToken.indexOf(ConsoleConstants.TOKEN_SEPARATOR);
            if (theSeparatorIndex != -1) {
                // There is a token and a parameter.
                theTokenKey = aToken.substring(0, theSeparatorIndex);
                theParam = aToken.substring(theSeparatorIndex + 1);
            } else {
                // There is no parameter.
                theTokenKey = aToken;
                theParam = null;
            }
            theViewToken = ViewToken.valueOf(theTokenKey);
            final UserRightsManager theUserRightsManager = UserRightsManager.getInstance();
            switch (theViewToken) {

            case ProcessEditor:
                if (theUserRightsManager.hasAccessToProcessesManagement()) {
                    displayProcessEditor(theParam);
                }
                break;
            case UserEditor:
                if (theUserRightsManager.hasAccessToUsersManagement()) {
                    displayUserEditor(theParam);
                }
                break;
            case AdminCaseEditor:
                if (theUserRightsManager.hasAccessToProcessesManagement()) {
                    displayAdminCaseEditor(theParam);
                }
                break;
            case AdminCaseList:
                if (theUserRightsManager.hasAccessToAdminCaseList()) {
                    displayAdminCaseList(theParam);
                }
                break;
            case Processes:
                if (theUserRightsManager.hasAccessToProcessesManagement()) {
                    displayProcessList(theParam);
                }
                break;
            case CategoriesManagement:
                if (theUserRightsManager.hasAccessToCategoriesManagement()) {
                    displayCategoriesList(theParam);
                }
                break;
            case CaseDataUpdate:
                if (theUserRightsManager.hasAccessToProcessesManagement()) {
                    displayCaseDataUpdate(theParam);
                }
                break;
            case Reporting:
                if (theUserRightsManager.hasAccessToReporting()) {
                    displayGlobalReporting(theParam);
                }
                break;
            case UsersManagement:
                if (theUserRightsManager.hasAccessToUsersManagement()) {
                    displayUserManagement(theParam);
                }
                break;
            case GroupEditor:
                if (theUserRightsManager.hasAccessToUsersManagement()) {
                    displayGroupEditor(theParam);
                }
                break;
            case RoleEditor:
                if (theUserRightsManager.hasAccessToUsersManagement()) {
                    displayRoleEditor(theParam);
                }
                break;                
            case CaseListsManagement:
                if (theUserRightsManager.hasAccessToCaseListsManagement()) {
                    displayCaseListsManagement(theParam);
                }
                break;
            case GlobalConfiguration:
                if (theUserRightsManager.hasAccessToSettingsManagement()) {
                    displayGlobalSettings(theParam);
                }
                break;
            case LabelsConfiguration:
                if (theUserRightsManager.hasAccessToLabelsManagement()) {
                    displayLabelsManagement(theParam);
                }
                break;
            case SyncDb:
                if (theUserRightsManager.hasAccessToSyncManagement()) {
                    displaySyncDB(theParam);
                }
                break;
            // case ExternalContent:
            // if(theParam!=null){
            // widgetSelectionChange(WidgetKey.MONITOR_KEY,theParam);
            // }
            // break;
            case CaseInstantiation:
                // TODO when it will be possible to have a confirmation before
                // instantiation in case of instantiation with no forms
                // designed.
                displayCaseInstantiationPanel(theParam);
                break;
            default:
                GWT.log("Not yet handled", null);
                throw new IllegalArgumentException("Invalid URL");
            }
            // Keep the reference to the token.
            myCurrentTokenName = theTokenKey;
            myCurrentTokenParam = theParam;
        } catch (IllegalArgumentException e) {
            displayDefaultViewAccordingToPrivileges();
        } catch (Throwable t) {
            Window.alert(t.getMessage());
            GWT.log("Exception: ", t);
        }

    }

    private void displaySyncDB(String aParam) {
        widgetSelectionChange(WidgetKey.SYNC_CONFIGURATION_KEY);
    }

    private void displayLabelsManagement(String aParam) {
        widgetSelectionChange(WidgetKey.LABELS_CONFIGURATION_KEY);
    }

    @Deprecated
    private void displayThemesManagement(String aParam) {
        widgetSelectionChange(WidgetKey.THEMES_CONFIGURATION_KEY);
    }

    private void displayGlobalSettings(String aParam) {
        widgetSelectionChange(WidgetKey.GLOBAL_CONFIGURATION_KEY);

    }

    private void displayCaseListsManagement(String aParam) {
        widgetSelectionChange(WidgetKey.CASE_LISTS_CONFIGURATION_KEY);
    }

    protected void displayUserManagement(final String aParam) {
        widgetSelectionChange(WidgetKey.USERS_MANAGEMENT_KEY);
    }

    protected void displayGlobalReporting(final String aParam) {
        myDataModel.getReportingDataSource().getItemFilter().setWithAdminRights(true);
        widgetSelectionChange(WidgetKey.MONITOR_KEY);
    }

    protected void displayCaseDataUpdate(final String theParam) {
        myDataModel.getCaseDataSource().getCaseSelection().addItemToSelection(new CaseUUID(theParam));
        widgetSelectionChange(WidgetKey.CASE_DATA_UPDATE_KEY);
    }

    protected void displayCategoriesList(final String theParam) {
        widgetSelectionChange(WidgetKey.CATEGORIES_MANAGEMENT_KEY);
    }

    protected void displayProcessList(String aParam) {
        myDataModel.getProcessDataSource().getItemFilter().setWithAdminRights(true);
        widgetSelectionChange(WidgetKey.PROCESS_LIST_KEY);
    }

    protected void displayAdminCaseList(String aParam) {
        myDataModel.getCaseDataSource().setItemFilter(myAdminCaseFilter);
        myDataModel.getProcessDataSource().getItemFilter().setWithAdminRights(true);
        widgetSelectionChange(WidgetKey.ADMIN_CASE_LIST_KEY);
    }

    protected void displayAdminCaseEditor(final String anItem) {
        if (anItem != null) {
            String theCaseId = getItemUUIDFromTokenParam(anItem);
            boolean searchInHistory = isTokenPointingToHistory(anItem);

            if (theCaseId != null) {
                if (myDataModel.getCaseDataSource().getItemFilter() == null) {
                    // coming from a bookmark or refresh button of the browser
                    myAdminCaseFilter.setSearchInHistory(searchInHistory, false);
                    myDataModel.getCaseDataSource().setItemFilter(myAdminCaseFilter, false);
                } else {
                    myDataModel.getCaseDataSource().getItemFilter().setSearchInHistory(searchInHistory, false);
                }
                myDataModel.getCaseDataSource().getItem(new CaseUUID(theCaseId), new AsyncHandler<CaseItem>() {
                    public void handleFailure(Throwable anT) {
                        GWT.log("Unable to update view to display: AdminCaseEditor/" + anItem, anT);

                    }

                    public void handleSuccess(CaseItem aCase) {
                        if (aCase != null) {
                            // When editing a case, it should be considered as
                            // the only
                            // one selected.
                            myDataModel.getCaseDataSource().getCaseSelection().clearSelection();
                            myDataModel.getCaseDataSource().getCaseSelection().addItemToSelection(aCase.getUUID());

                            // Get the 'Case Editor' widget.
                            final AbstractCaseEditorWidget theCaseEditorWidget;
                            theCaseEditorWidget = (AbstractCaseEditorWidget) myWidgetFactory.getWidget(WidgetKey.ADMIN_CASE_EDITOR_KEY);

                            // Update the view only if the requested widget is
                            // available.
                            switchWidgetToDisplay(theCaseEditorWidget);
                            theCaseEditorWidget.setCaseToDisplay(aCase);
                            if (myLocatorWidget != null) {
                                myLocatorWidget.setText(theCaseEditorWidget.getLocationLabel());
                            }
                        } else {
                            GWT.log("Unable to update view to display: AdminCaseEditor/" + anItem, new NullPointerException());
                        }
                    }
                });
            } else {
                updateViewAccordingToToken(ViewToken.AdminCaseList.name());
            }
        }
    }

    protected void displayProcessEditor(final String anItem) {
        if (anItem != null) {
            myDataModel.getProcessDataSource().getItem(new BonitaProcessUUID(anItem, anItem), new AsyncHandler<BonitaProcess>() {
                public void handleFailure(Throwable anT) {
                    // FIXME should display a "Process Not Found" error page
                    GWT.log("Unable to update view to display: ProcessEditor/" + anItem, anT);

                }

                public void handleSuccess(BonitaProcess aProcess) {
                    if (aProcess != null) {
                        // When editing a case, it should be considered as the
                        // only
                        // one selected.
                        final ItemSelection<BonitaProcessUUID> theProcessSelection = myDataModel.getProcessDataSource().getItemSelection();
                        theProcessSelection.clearSelection();
                        theProcessSelection.addItemToSelection(aProcess.getUUID());

                        // Get the 'Process Editor' widget.
                        final ProcessEditor theProcessEditorWidget;
                        theProcessEditorWidget = (ProcessEditor) myWidgetFactory.getWidget(WidgetKey.PROCESS_EDITOR_KEY);

                        // Update the view only if the requested widget is
                        // available.
                        switchWidgetToDisplay(theProcessEditorWidget);
                        theProcessEditorWidget.setItem(aProcess);
                    } else {
                        // FIXME should display a "Process Not Found" error page
                        GWT.log("Unable to update view to display: ProcessEditor/" + anItem);
                    }
                }
            });
        }
    }

    protected void displayUserEditor(final String anItem) {
        if (anItem != null) {
            myDataModel.getUserDataSource().getItem(new UserUUID(anItem), new AsyncHandler<User>() {
                public void handleFailure(Throwable anT) {
                    // FIXME should display a "User Not Found" error page
                    GWT.log("Unable to update view to display: UserEditor/" + anItem, anT);

                }

                public void handleSuccess(User aUser) {
                    if (aUser != null) {
                        // When editing a case, it should be considered as the
                        // only
                        // one selected.
                        final ItemSelection<UserUUID> theUserSelection = myDataModel.getUserDataSource().getItemSelection();
                        theUserSelection.clearSelection();
                        theUserSelection.addItemToSelection(aUser.getUUID());

                        // Get the 'User Editor' widget.
                        final UserEditor theUserEditorWidget;
                        theUserEditorWidget = (UserEditor) myWidgetFactory.getWidget(WidgetKey.USER_EDITOR_KEY);

                        // Update the view only if the requested widget is
                        // available.
                        switchWidgetToDisplay(theUserEditorWidget);
                        theUserEditorWidget.setItem(aUser);
                    } else {
                        // FIXME should display a "User Not Found" error page
                        GWT.log("Unable to update view to display: UserEditor/" + anItem);
                    }
                }
            });
        } else {
            final UserEditor theUserEditorWidget;
            theUserEditorWidget = (UserEditor) myWidgetFactory.getWidget(WidgetKey.USER_EDITOR_KEY);

            // Update the view only if the requested widget is
            // available.
            switchWidgetToDisplay(theUserEditorWidget);
            theUserEditorWidget.setItem(null);
            widgetSelectionChange(WidgetKey.USER_EDITOR_KEY);
        }
    }

    private void displayGroupEditor(final String anItem) {
        if (anItem != null) {
            myDataModel.getGroupDataSource().getItem(new BonitaUUID(anItem), new AsyncHandler<Group>() {
                public void handleFailure(Throwable anT) {
                    // FIXME should display a "Group Not Found" error page
                    GWT.log("Unable to update view to display: GroupEditor/" + anItem, anT);
                }

                public void handleSuccess(Group aGroup) {
                    if (aGroup != null) {
                        // When editing a group, it should be considered as the
                        // only
                        // one selected.
                        final ItemSelection<BonitaUUID> theItemSelection = myDataModel.getGroupDataSource().getItemSelection();
                        theItemSelection.clearSelection();
                        theItemSelection.addItemToSelection(aGroup.getUUID());

                        // Get the 'Group Editor' widget.
                        final GroupEditorPanel theGroupEditorWidget;
                        theGroupEditorWidget = (GroupEditorPanel) myWidgetFactory.getWidget(WidgetKey.GROUP_EDITOR_KEY);

                        if (theGroupEditorWidget != null) {
                            // Update the view only if the requested widget is
                            // available.
                            switchWidgetToDisplay(theGroupEditorWidget);
                            theGroupEditorWidget.setItem(aGroup);
                            if (myLocatorWidget != null) {
                                myLocatorWidget.setText(theGroupEditorWidget.getLocationLabel());
                            }
                        } else {
                            myDataModel.getMessageDataSource().addErrorMessage("The widget you have selected is not yet available.");
                        }
                    } else {
                        // FIXME should display a "Group Not Found" error page
                        GWT.log("Unable to update view to display: GroupEditor/" + anItem);
                    }
                }
            });
        }

    }

    private void displayRoleEditor(final String anItem) {
        if (anItem != null) {
            myDataModel.getRoleDataSource().getItem(new BonitaUUID(anItem), new AsyncHandler<Role>() {
                public void handleFailure(Throwable anT) {
                    // FIXME should display a "Group Not Found" error page
                    GWT.log("Unable to update view to display: GroupEditor/" + anItem, anT);
                }

                public void handleSuccess(Role aRole) {
                    if (aRole != null) {
                        // When editing a group, it should be considered as the
                        // only
                        // one selected.
                        final ItemSelection<BonitaUUID> theItemSelection = myDataModel.getRoleDataSource().getItemSelection();
                        theItemSelection.clearSelection();
                        theItemSelection.addItemToSelection(aRole.getUUID());

                        // Get the 'Role Editor' widget.
                        final RoleEditorPanel theRoleEditorWidget;
                        theRoleEditorWidget = (RoleEditorPanel) myWidgetFactory.getWidget(WidgetKey.ROLE_EDITOR_KEY);

                        if (theRoleEditorWidget != null) {
                            // Update the view only if the requested widget is
                            // available.
                            switchWidgetToDisplay(theRoleEditorWidget);
                            theRoleEditorWidget.setItem(aRole);
                            if (myLocatorWidget != null) {
                                myLocatorWidget.setText(theRoleEditorWidget.getLocationLabel());
                            }
                        } else {
                            myDataModel.getMessageDataSource().addErrorMessage("The widget you have selected is not yet available.");
                        }
                    } else {
                        // FIXME should display a "Role Not Found" error page
                        GWT.log("Unable to update view to display: RoleEditor/" + anItem);
                    }
                }
            });
        }

    }

    protected void displayDefaultViewAccordingToPrivileges() {
        final UserRightsManager theUserRightsManager = UserRightsManager.getInstance();
        if (theUserRightsManager.hasAccessToAdminCaseList()) {
            displayAdminCaseList(null);
        } else if (theUserRightsManager.hasAccessToReporting()) {
            displayGlobalReporting(null);
        }
    }
}
