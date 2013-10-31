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

public interface ConsoleMessages extends com.google.gwt.i18n.client.Messages {

    String deleteAllProcessInstances(String processName);

    String instantiateProcess(String processName);

    String undeployProcess(String processName);

    String deleteProcess(String processName);

    String unableToLoadCases();

    String unableToCancelCases();

    String unableToDeleteCases();

    String unableToCreateLabel();

    String unableToDeleteLabel();

    String unableToLoadLabels();

    String unableToUpdateStep();

    String stepUpdated();

    String unableToUpdateLabel();

    String unableToLoadProcesses();

    String unableToDeployProcess();

    String installingProcess();

    String deletingCases();

    String deletingProcesses();

    String casesDeleted();

    String enablingProcesses();

    String unableToEnableProcesses();

    String processesEnabled();

    String disablingProcesses();

    String unableToDisableProcesses();

    String unableToDeleteProcesses();

    String processesDeleted();

    String noDisabledProcessesSelected();

    String processesArchived();

    String unableToArchiveProcesses();

    String processDisabled();

    String noEnabledProcessesSelected();

    String processesUpdated(int aSize);

    String reportDesignUploaded();

    String reportLibUploaded();

    String unableToUpdateUserProfile();

    String unableToLoadReports();

    String userProfileUpdated();

    String invalidBarFileToUpload();
    
    String invalidZipFileToUpload();

    String unableToUploadReportDesign();

    String unableToUploadReportLib();

    String unableToUpdateConfiguration();

    String unableToReadConfiguration();

    String configurationUpdated();

    String processDisabledWithWarning();

    String processesEnabledWithWarning();

    String processesArchivedWithWarning();

    String unableToLoadUsers();

    String unableToDeleteUsers();

    String unableToAddUser();

    String userCreated();

    String userUpdated();

    String userAlreadyExists(String username);

    String unableToDeleteRoles();

    String unableToUpdateUser();

    String roleCreated();

    String roleUpdated();

    String unableToUpdateRole();

    String roleAlreadyExists(String name);

    String unableToAddRole();

    String unableToAddRoleToUser();

    String unableToRemoveRoleFromUser();

    String noUserSelected();

    String noRoleSelected();

    String usersDeleted(int anSize);

    String usersDeletedButYour(int anSize);

    String deletionOfYourAccountNotAuthorized();

    String casesCancelledWithWarning();

    String casesCancelled(int aCaseNumber);

    String noStartedCasesSelected();

    String archivingProcesses();

    String processDeployed();

    String caseVariablesModified();

    String noLabelSelected();

    String databaseSynchronized();

    String databaseCannotBeSynchronized();

    String invalidTemplateFileToUpload();

    String invalidValueForRefreshFrequency();

    String noProcessDesignAvailable();

    String unableToListStepComments();

    String emptyLabelNameNotSupported();

    String duplicateLabelName();

    String archivedCasesIgnored();

    String searchPatternEmpty();

    String invalidValueForStepAtRiskThreshold();

    String noGroupSelected();

    String unableToLoadGroups();

    String categoryAlreadyExists(String aValue);

    String noCategorySelected();
    
    /**
     * you can not delete your own account
     */
    String deleteYourAccountWarn();
    
    /**
     * Metadata already exists.
     */
    String metadataAlreadyExists(String aName);

    /**
     * Group already exists.
     */
    String groupAlreadyExists(String aPath);

    /**
     * Please select one event.
     */
    String tooManyEventsSelected();

    /**
     * File upload in progress...
     */
    String uploadingFile();

    /**
     * File uploaded.
     */
    String fileUploaded();

    /**
     * Loading...
     */
    String loading();

    /**
     * This view is not accessible to a simple user because it is an administration view.
     */
    String accessDeniedToAdminView();
    
    /**
     * User already member of this group with this role.
     */
    String alreadyAddedTheMembership();
    
    /**
     * The widget you have selected is not yet available
     */
    String unableToDisplaySelectedWidget();
       
    /**
     * finished case does not have any  comments.
     */
    String noCommentsAttached();

    /**
     * warn if no User Metadata is Selected
     */
    String noUserMetadataSelectedWarn();

    /**
     * warn if no case is Selected
     */
    String noCaseSelectedWarn();
    
    /**
     * warn if no Label is Selected or if system label is selected
     */
    String invalidLabelSelectionWarn();
    
    /**
     * The name you entered contains special chars
     */
    String fileNameWithSpecialChars();
}