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
package org.bonitasoft.console.client.i18n;

import org.bonitasoft.console.client.reporting.ReportUUID;

public interface ConsoleConstants extends com.google.gwt.i18n.client.Constants {

    public final String PICTURE_PLACEHOLDER = "pictures/cleardot.gif";
    public final String LABEL_TOKEN_PARAM_PREFIX = "lab:";
    public final String CATEGORY_TOKEN_PARAM_PREFIX = "cat:";
    public final String HISTORY_TOKEN_PARAM_PREFIX = "his:";
    public final String JOURNAL_TOKEN_PARAM_PREFIX = "jou:";
    public final String TOKEN_SEPARATOR = "/";
    public final String BOS_VERSION = "5.10.1";
    public final ReportUUID DEFAULT_REPORT_UUID = new ReportUUID("Logged user steps by status (Thumbnail)");
    public final ReportUUID DEFAULT_ADMIN_REPORT_UUID = new ReportUUID("admin (default)");
    public final String CurrentUser = "BonitaCurrentUser";
    public final String Locale = "Locale";
    public final String StartDate = "StartDate";
    public final String EndDate = "EndDate";
    public final String TimeUnit = "TimeUnit";
    public final String ProcessUUID = "ProcessUUID";
    public final String ActivityUUID = "ActivityUUID";
    public final String User = "User";
    public final String UserName = "UserName";
    public final String ActivityType = "ActivityType";
    public final String BR = "<br/>";
    public final String reloadURLParameter = "reload";
    

    String loading();

    String processes();

    String cases();

    String refresh();

    String enableProcess();

    String disableProcess();

    String deleteAllInstances();

    String removeProcess();

    String submit();

    String apply();

    String systemLabels();

    String labels();

    String show();

    String hide();

    String remove();

    String moveTo();

    String moreActions();

    String addStar();

    String removeStar();

    String itemSelector();

    String modifyInstance();

    String deleteInstance();

    String cancelInstance();

    String install();

    String noProcessesAvailable();

    String labelManagementView();

    String startCase();

    String assignToMultiple();

    String assignStepToMultiple();

    String assignToMe();

    String suspend();

    String resume();

    String defaultApplicationFormWindowName();

    String redirectButtonTitle();

    String more();

    String createNewLabel();

    String manageLabels();

    String newLabelWindowTitle();

    String okButton();

    String cancelButton();

    String newLabelWindowInputLabel();

    String logout();

    String adminCaseList();

    String unassign();

    String assignToMultipleTitle();

    int defaultMaxDisplayedItems();

    int defaultMinDisplayedItems();

    String adminProcessList();

    String archiveProcess();

    String noStepsToDisplay();

    String dashboard();

    String userSettings();

    String globalSettings();

    String generalTabDescription();

    String save();

    String restoreToDefault();

    String renameLabelWindowTitle();

    String rename();

    String noStepsToPerform();

    String testReportTabDescription();

    String reportingTabName();

    String test();

    String caseStartedBy();

    String noVariablesForStep();

    String submitButtonLabel();

    String submitButtonTitle();

    String previousPageButtonLabel();

    String previousPageButtonTitle();

    String nextPageButtonLabel();

    String nextPageButtonTitle();

    String backToCases();

    String deployDesignDescription();

    String deployDesignExample();

    String deployDesignLibDescription();

    String deployJarExample();

    String reportingPrefTabDescription();

    String settings();

    String processFileNamePattern();

    String reportFileNamePattern();

    int messageDisplayTime();

    String stepReady();

    String userReportingActivationDescription();

    String userReportingDeActivateDescription();

    String userReportingActivateDescription();

    String globalReportingDeActivateDescription();

    String globalReportingActivateDescription();

    String reporting();

    String caseDesciptionPatternTabDescription();

    String usersTabName();

    String rolesTabName();

    String username();

    String password();

    String firstName();

    String lastName();

    String email();

    String userRoles();

    String usersManagement();

    String userCreation();

    String userUpdate();

    String roleCreation();

    String roleUpdate();

    String usernameLabel();

    String passwordLabel();

    String passwordConfirmLabel();

    String firstNameLabel();

    String lastNameLabel();

    String emailLabel();

    String rolesLabel();

    String roleName();

    String roleLabel();

    String roleDescription();

    String roleNameLabel();

    String roleLabelLabel();

    String roleDescriptionLabel();
    
    String wrongPasswordConfirm();

    String mandatorySymbol();

    String labelname();

    String visibility();

    String visible();

    String hidden();

    String selectorAll();

    String selectorNone();

    String selectorStarred();

    String selectorUnstarred();

    String inboxLabelName();

    String allLabelName();

    String myCasesLabelName();

    String starredLabelName();

    String previousPageLinkLabel();

    String nextPageLinkLabel();

    String add();

    String update();

    String delete();

    String processFileNameExamples();

    String aborted();

    String cancelled();

    String executing();

    String finished();

    String ready();

    String suspended();

    String selectLabelStyle();

    String renameLabel();

    String errorProcessingReport();

    String unableToDisplayReport();

    String markThisCase();

    /**
     * Administration
     */
    String adminModeLink();
    
    /**
     * Switch to the administration view.
     */
    String adminModeTooltip();

    String newer();

    String older();

    String synchro();

    String synchroTabDescription();

    String synchronize();

    String dashboardRefreshFrequency();

    String userAlreadyInList();

    String identityTabDescription();

    String deactivateUserNameCompletion();

    String activateUserNameCompletion();

    String showProcessDesign();

    String cancel();

    String listInboxTooltip();

    String listAllTooltip();

    String listMyCasesTooltip();

    String listStarredTooltip();

    String lastUpdateDate();

    String currentCandidates();

    String archivedState();

    String disabledState();

    String enabledState();

    String newest();

    String oldest();

    String firstPage();

    String lastPage();

    String priority();

    String normal();

    String high();

    String urgent();
    
    String monitoringViewDescriptionPanel();

    String you();

    String about();

    String clickToEdit();

    String refreshIcon();

    String userReportingIsDisabledExplanation();

    String clickToOpen();

    String showHistorizedCases();

    String showHistorizedProcesses();

    String showDetails();

    String hideDetails();

    String stepAborted();

    String stepFinished();

    String stepCancelled();

    String stepExecuting();

    String stepSuspended();
    
    String stepFailed();

    String archivedCasesCannotBeMarkedToolTip();

    String search();

    String stepAtRiskThreshold();

    String labelConfigurationTabDescription();

    String starLabelUsageActivationDescription();

    String customLabelsActivationDescription();

    String labelsTabName();

    String generalUserEditorTab();

    String memberOfUserEditorTab();

    String professionalContactUserEditorTab();

    String personalContactUserEditorTab();

    String metadataUserEditorTab();

    String groupNameLabel();

    String groupLabelLabel();

    String groupDescription();

    String name();

    String label();
    
    String description();

    String groupsTabName();

    String filterRolesToolTip();

    String filterGroupsToolTip();

    String filterUsersToolTip();

    String clearFilter();

    String titleLabel();

    String jobTitleLabel();

    String managerLabel();

    String delegateLabel();

    String groupUpdate();

    String membershipRoleName();

    String address();

    String building();

    String city();

    String country();

    String fax();

    String mobile();

    String phone();

    String room();

    String state();

    String website();

    String zip();

    String searchForAUser();

    String notYetDefined();

    String chooseAGroup();

    String chooseARole();

    String addMembershipGroupPanelCaption();

    String removeMembershipsGroupPanelCaption();

    String removeSelectedMemberships();

    String loadingSmall();

    String groupCreation();

    String searchForAGroup();

    String searchForARole();

    String selectSomeGroups();

    String selectExactlyOneGroup();

    String selectSomeRoles();

    String selectExactlyOneRole();

    String selectSomeUsers();

    String selectExactlyOneUser();

    String groupParentLabel();

    String groupChildOfMine();

    String filterUserMetadataToolTip();

    String userMetadataNameLabel();

    String userMetadataLabelLabel();

    String userMetadataTabName();

    String noMetadataDefined();

    String close();

    String userInfoTabName();

    String version();

    String categoryNameLabel();

    String categoryUpdate();

    String categoryCreation();

    String filterCategoriesToolTip();

    String caseRecapModeSelector();

    String caseStepModeSelector();

    String filterCasesToolTip();

    String filterStepsToolTip();

    String selectSomeSteps();

    String selectExactlyOneStep();

    String searchForAStep();

    String stepLabelLabel();

    String stepNameLabel();

    String searchForAProcess();

    String processLabelLabel();

    String processNameLabel();

    String processDescriptionLabel();

    String processVersionLabel();

    String processStateLabel();

    String addStepGroupPanelCaption();

    String chooseAProcess();

    String chooseAStep();

    String editIcon();

    String usersCannotBeTheirOwnManager();

    String usersCannotBeTheirOwnDelegate();

    String showOpenCases();

    String installProcessPopupTitle();

    String installProcessExplanations();

    String deleteUsers();

    /**
     * Name of the view #ProcessEditor
     */
    String processEditor();

    /**
     * Categories
     */
    String categoriesTabName();

    /**
     * Color
     */
    String categoryColor();

    String showOpenProcesses();

    /**
     * Enter a comment.
     */
    String enterAComment();

    /**
     * Case overview
     */
    String caseOverviewTitle();

    /**
     * Open steps
     */
    String openStepsTitle();

    /**
     * Comment feed
     */
    String commentFeedTitle();

    /**
     * Case history
     */
    String caseHistoryTitle();

    /**
     * edit value
     */
    String editValue();

    /**
     * List of categories to automatically assigned to all cases of this
     * process.
     */
    String categoriesListOfProcess();

    /**
     * Process details.
     */
    String processDetails();

    /**
     * At risk
     */
    String atRiskLabelName();

    /**
     * Overdue
     */
    String overDueLabelName();

    /**
     * List cases having a step at risk, i.e., near of its expected completion
     * date.
     */
    String listAtRiskTooltip();

    /**
     * List cases having a step that should have been completed but is still
     * open.
     */
    String listOverdueTooltip();

    /**
     * Create a metadata.
     */
    String userMetadataCreation();

    /**
     * You must select at list one category.
     */
    String selectSomeCategory();

    /**
     * You must select exactly one category.
     */
    String selectExactlyOneCategory();

    /**
     * Modify the layout of a case description in case lists...
     */
    String caseListConfigurationTabDescription();

    /**
     * Open step in external application link.
     */
    String caseListApplicationLinkColumnDescription();

    /**
     * Case description (following defined pattern).
     */
    String caseListCaseDescriptionColumnDescription();

    /**
     * Case select box.
     */
    String caseListSelectDescriptionColumnDescription();

    /**
     * Star icon to mark the case.
     */
    String caseListStarIconDescriptionColumnDescription();

    /**
     * Step description
     */
    String caseListStepDescriptionColumnDescription();

    /**
     * Date and time of the last case update
     */
    String caseListUpdateTimeDescriptionColumnDescription();

    /**
     * Case Lists
     */
    String caseListsConfigurationTabName();

    /**
     * Step state icon
     */
    String caseListStepSateColumnDescription();

    /**
     * Step priority
     */
    String caseListStepPriorityColumnDescription();

    /**
     * Step candidates
     */
    String caseListStepAssigneeColumnDescription();

    /**
     * Case categories
     */
    String caseListCategoriesDescriptionColumnDescription();

    /**
     * Case labels
     */
    String caseListLabelsDescriptionColumnDescription();

    /**
     * Case state icon (admin only)
     */
    String caseListCaseStateDescriptionColumnDescription();

    /**
     * Step name
     */
    String caseListStepNameDescriptionColumnDescription();

    /**
     * Column
     */
    String caseListsColumnIndexHeader();

    /**
     * Content
     */
    String caseListsColumnContentHeader();

    /**
     * Stretch
     */
    String caseListsColumnStretchDescription();

    /**
     * User view
     */
    String userModeLink();
    
    /**
     * Switch to the user view.
     */
    String userModeTooltip();

    /**
     * Auto-generated forms
     */
    String applicationURLAutoGeneration();

    /**
     * Local form application
     */
    String applicationURLLocalWebapp();

    /**
     * External form application
     */
    String applicationURLExternalWebapp();

    /**
     * Form application
     */
    String applicationToDisplayProcessForms();

    /**
     * M/d/yy
     */
    String dateShortFormat();
    
    /**
     * hh:mm:ss
     */
    String timeShortFormat();

    /**
     * Processes
     */
    String processesNavigationMenuTitle();

    /**
     * Reporting
     */
    String reportingNavigationMenuTitle();

    /**
     * Organization
     */
    String organizationNavigationMenuTitle();

    /**
     * Configuration
     */
    String configurationNavigationMenuTitle();

    /**
     * Server
     */
    String serverNavigationMenuTitle();

    /**
     * User labels
     */
    String userLabelsNavigationMenuTitle();

    /**
     * Settings
     */
    String reportingConfigurationNavigationLink();

    /**
     * Select the way to render forms of steps.<br/>
     * Auto-generated forms will display all the data available.<br/>
     * Local form application will use a Bonita Studio-generated web application
     * deployed on the same server as the User XP.<br/>
     * External form application could be a Bonita Studio-generated form
     * application deployed on a remote server, or any other web application
     * able to interact with this process.
     * <hr>
     */
    String processApplicationFormDescription();

    /**
     * Skip step
     */
    String skipStepActionLabel();

    /**
     * Skipped
     */
    String skipped();

    String failed();
    
    /**
     * Definition
     */
    String groupDefinitionSectionTitle();

    /**
     * Children
     */
    String groupChildrenSectionTitle();

    /**
     * Members
     */
    String groupMembersSectionTitle();

    /**
     * Group
     */
    String group();

    /**
     * Role
     */
    String role();

    /**
     * Members
     */
    String roleMembersSectionTitle();

    /**
     * Type
     */
    String eventTypeColumnTitle();

    /**
     * Position
     */
    String eventPositionColumnTitle();

    /**
     * Expiracy
     */
    String eventDateColumnTitle();

    /**
     * Step
     */
    String eventStepColumnTitle();

    /**
     * Process
     */
    String eventProcessColumnTitle();

    /**
     * Timer / Deadlines
     */
    String showEventsStepActionLabel();

    /**
     * Execute now
     */
    String executeNow();

    /**
     * Change expiracy
     */
    String eventUpdateExecutionDate();

    /**
     * Day
     */
    String day();

    /**
     * Hour
     */
    String hour();

    /**
     * Minute
     */
    String minute();

    /**
     * Month
     */
    String month();

    /**
     * Week
     */
    String week();

    /**
     * Year
     */
    String year();

    /**
     * Interval
     */
    String interval();

    /**
     * From
     */
    String from();

    /**
     * To
     */
    String to();

    /**
     * You must select at list one process.
     */
    String selectSomeProcesses();

    /**
     * You must select exactly one process.
     */
    String selectExactlyOneProcess();

    /**
     * Process
     */
    String reportProcessDefinitionParameter();

    /**
     * Step
     */
    String reportStepDefinitionParameter();

    /**
     * User
     */
    String reportUserParameter();

    /**
     * Run
     */
    String run();

    /**
     * Automatic
     */
    String stepTypeAutomatic();

    /**
     * Human
     */
    String stepTypeHuman();

    /**
     * Step type
     */
    String reportStepTypeParameter();
    
    String monitoringViewCurrentReports();

    String monitoringViewCurrentReportsTooltip();

    String configure();

    String monitoringViewConfigurationPanelDescription();

    String down();

    String up();

    /**
     * PDF version
     */
    String reportPDFVersion();

    /**
     * Path element
     */
    String groupPathElement();
    
    /**
     * Path element
     */
    String groupPath();

    /**
     * back
     */
    String back();

    /**
     * Please specify an upper limit for date search.
     */
    String missingToDateFilterInput();

    /**
     * Please specify a lower limit for date search.
     */
    String missingFromDateFilterInput();    
    
    String fileName();   
    
    /**
     * Constants for the Confirmation Dialogboxs - start
     */    
    String deleteProcessesCasesDialogbox();
    
    String enableProcessesDialogbox();
    
    String disableProcessesDialogbox(); 
    
    String archiveProcessesDialogbox();
    
    String removeProcessesDialogbox();
    
    String cancelCasesDialogbox();
    
    String deleteCasesDialogbox();
    
    String deleteCategoriesDialogbox();
    
    String cancelUserEditionDialogbox();
        
    String cancelUserEditionWarn();
    
    String deleteRolesDialogbox();
    
    String deleteGroupsDialogbox();
    
    String deleteUserMetadataDialogbox();

    String deleteLabelsDialogbox();
    
    String deleteAttachmentsCheckBox();
    
    /**
     * Constants for the Confirmation Dialogboxs - end
     */
    
    /**
     * Please choose a password without the character ' : '
     */
    String passwordCannotContainColonCharacter();

    /**
     * Installed by 
     */
	String processInstallerNameColumnTitle();

	/**
	 * Installation date 
	 */
	String processInstallationNameColumnTitle();

	/**
	 * Process 
	 */
	String processColumnTitle();
}
