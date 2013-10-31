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

public interface ConsolePatterns extends com.google.gwt.i18n.client.Messages {

    String caseDescription(String processDescription, String caseInstanceNumber);

    String backToDestination(String aDestination);

    String startCase(String aProcessLabel);

    String multipleStepsToDo(int aNbOfSteps);

    String variablesEditionLabel(String caseId);

    String stepDescriptionPriority(String aPriority);

    String processStateIconTitle(String aProcessState);

    String caseState(String aCaseState);

    String stepState(String aStepState);

    String mandatoryFieldLabel(String fieldName);

    String numberOfItemsInList(int aStartingIndex, int anEndIndex, int aTotalSize);

    String comments(int aNumberOfComments);

    String commentHeader(String anAuthor, String aDate);

    String newCommentWindowTitle(String anItemLabel);

    String browseToLabel(String aLabelName);

    String userUpdate(String aUsername);

    String userIdentity(String aFirstName, String aLastName);

    String groupIdentity(String aLabel);

    String roleIdentity(String aLabel);

    String stepIdentity(String aProcessName, String aProcessVersion, String aStepLabel);

    String processIdentity(String aDisplayName, String aVersion);

    String browseToCategory(String aValue);

    /**
     * aStartingIndex - aEndingIndex of aTotalNumberOfElement
     */
    String listSize(int aStartingIndex, int aEndingIndex, int aTotalNumberOfElement);

    /**
     * Step has been aborted.
     */
    String stepExecutionSummaryAborted(String aStepLabel);

    /**
     * Step has been canceled.
     */
    String stepExecutionSummaryCancelled(String aLabel);

    /**
     * Step has been finished.
     */
    String stepExecutionSummaryFinished(String aLabel);

    /**
     * Case started by {0}.
     */
    String caseStartedBy(String aUsername);

    /**
     * The selected index is already used to display 'aColumnTitle'. Click on OK
     * to hide the conflicting column or Cancel to come back to previous state.
     */
    String caseListConfigurationColumnIndexConflictsWith(String aColumnTitle);

    /**
     * {0}
     */
    String caseListsColumn(int anIndex);

    /**
     * About section
     * 
     * @param aVersion
     */
    String aboutContent(String aVersion);
    
    /**
     * Product edition
     * 
     * @param anEdition
     */
    String productEdition(String anEdition);

    /**
     * The step has been skipped.
     */
    String stepExecutionSummarySkipped(String aLabel);

    /**
     * Events bound to step {0}
     */
    String eventListDialogTitle(String aName);

    /**
     * Last update {0}.
     */
    String lastTimeRefreshed(String aFormattedDate);
    
    /**
     * warn when deleting one or several users in the admin view
     */
    String deleteUsersWarn(int aNoOfUsers);

    /**
     * warn when the username size over the max password size 
     */
    String maxUsernameSizeWarn(int aSize);
    
    /**
     * warn when the password size over the max password size 
     */
    String maxPasswordSizeWarn(int aSize);
    
    /**
     * warn when deleting cases of one or several  processes in the admin view
     */
    String deleteProcessesCasesWarn(int aNoOfProcesses);

    /**
     *  display Explanations when enabling one or several processes in the admin view
     */
    String enableProcessesExplanations(int aNoOfProcesses);
    
    /**
     * display Explanations when Disabling one or several processes in the admin view
     */
    String disableProcessesExplanations(int aNoOfProcesses);
    
    /**
     * warn when Archiving one or several processes in the admin view
     */
    String archiveProcessesWarn(int aNoOfProcesses);
        
    /**
     * warn when Removing one or several processes in the admin view
     */
    String removeProcessesWarn(int aNoOfProcesses);
    
    /**
     * warn when deleting one or several cases in the admin view
     */
    String deleteCasesWarn(int aNoOfCases);
    
    /**
     * warn when canceling one or several cases in the admin view
     */
    String cancelCasesWarn(int aNoOfCases);
    
    /**
     * warn when deleting one or several Categories in the admin view
     */
    String deleteCategoriesWarn(int aNoOfCategories);
   
    /**
     * warn when deleting one or several Roles in the admin view
     */
    String deleteRolesWarn(int aNoOfRoles);
    
    /**
     * warn when deleting one or several Groups in the admin view
     */
    String deleteGroupsWarn(int aNoOfGroups);
    
    /**
     * warn when deleting one or several MetaData in the admin view
     */
    String deleteUserMetadataWarn(int aNoOfMetadata);
    
    /**
     * warn when deleting one or several Labels in the user view
     */
    String deleteLabelsWarn(int aNoOfLabels);
    
    
}