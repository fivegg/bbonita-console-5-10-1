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
package org.bonitasoft.forms.client.rpc;

import java.util.List;
import java.util.Map;

import org.bonitasoft.forms.client.model.ApplicationConfig;
import org.bonitasoft.forms.client.model.FormFieldAvailableValue;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.FormURLComponents;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.HtmlTemplate;
import org.bonitasoft.forms.client.model.exception.CanceledFormException;
import org.bonitasoft.forms.client.model.exception.FileTooBigException;
import org.bonitasoft.forms.client.model.exception.ForbiddenApplicationAccessException;
import org.bonitasoft.forms.client.model.exception.ForbiddenFormAccessException;
import org.bonitasoft.forms.client.model.exception.FormAlreadySubmittedException;
import org.bonitasoft.forms.client.model.exception.FormInErrorException;
import org.bonitasoft.forms.client.model.exception.IllegalActivityTypeException;
import org.bonitasoft.forms.client.model.exception.MigrationProductVersionNotIdenticalException;
import org.bonitasoft.forms.client.model.exception.RPCException;
import org.bonitasoft.forms.client.model.exception.SessionTimeOutException;
import org.bonitasoft.forms.client.model.exception.SkippedFormException;
import org.bonitasoft.forms.client.model.exception.SuspendedFormException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Form flow service Helps building the forms application
 * 
 * @author Anthony Birembaut
 * 
 */
public interface FormsService extends RemoteService {

    /**
     * Retrieve the application URL welcome page
     * @param urlContext Map containing the URL parameters
     * @return the application welcome page template
     * @throws RPCException
     */
    HtmlTemplate getWelcomePage(Map<String, Object> urlContext) throws RPCException;

    /**
     * Retrieve the application URL welcome page
     * 
     * @return the application URL welcome page
     * @throws RPCException
     */
    String getExternalWelcomePage() throws RPCException;

    /**
     * Retrieve the application config including the application template
     * 
     * @param formID
     * @param urlContext
     * @param includeApplicationTemplate
     * @throws SessionTimeOutException
     * @throws RPCException
     * @throws ForbiddenFormAccessException
     * @throws MigrationProductVersionNotIdenticalException 
     */
    ApplicationConfig getApplicationConfig(String formID, Map<String, Object> urlContext, boolean includeApplicationTemplate) throws RPCException, SessionTimeOutException, ForbiddenApplicationAccessException, MigrationProductVersionNotIdenticalException;

    /**
     * Retrieve the first page in the page flow associated with the form
     * 
     * @param formID
     * @param urlContext
     * @return
     * @throws SessionTimeOutException
     * @throws RPCException
     * @throws SuspendedFormException
     * @throws CanceledFormException
     * @throws FormAlreadySubmittedException
     * @throws ForbiddenFormAccessException
     * @throws FormInErrorException 
     * @throws MigrationProductVersionNotIdenticalException 
     * @throws SkippedFormException 
     */
    FormPage getFormFirstPage(String formID, final Map<String, Object> urlContext) throws SessionTimeOutException, RPCException, SuspendedFormException, CanceledFormException, FormAlreadySubmittedException, ForbiddenFormAccessException, FormInErrorException, MigrationProductVersionNotIdenticalException, SkippedFormException;

    /**
     * Retrieve the next page in the page flow associated with the form
     * 
     * @param formID form id
     * @param urlContext Map containing the URL parameters
     * @param pageId the current page Id
     * @param fieldValues the current page's fields values
     */
    FormPage getFormNextPage(String formID, Map<String, Object> urlContext, String pageId, Map<String, FormFieldValue> fieldValues) throws RPCException, SessionTimeOutException;

    /**
     * Retrieve a list of ids of the pages contained in the admin page flow associated with the activity
     * 
     * @param taskUUIDStr the task UUID as a string
     * @return a list of ids of the pages for the admin flow of the activity
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    List<String> getTaskAdminPageList(String taskUUIDStr) throws RPCException, SessionTimeOutException;

    /**
     * Retrieve a list of ids of the pages contained in the admin page flow associated with the process
     * 
     * @param processInstanceUUIDStr the process instance as a String
     * @return a list of ids of the pages defined for the admin flow of the process
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    List<String> getProcessInstanceAdminPageList(String processInstanceUUIDStr) throws RPCException, SessionTimeOutException;

    /**
     * Retrieve an admin page data containing a list of the widgets to be displayed
     * 
     * @param taskUUIDStr the task UUID as a string
     * @param pageId the page ID
     * @return a {@link FormPage} object containing the elements required to build the page form
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    FormPage getTaskAdminFormPage(String taskUUIDStr, String pageId) throws RPCException, SessionTimeOutException;

    /**
     * Retrieve a process admin page data containing a template and a list of the widgets to be displayed
     * 
     * @param processInstanceUUIDStr the process instance as a String
     * @param pageId the page ID
     * @return a {@link FormPage} object containing the elements required to build the page form
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    FormPage getProcessInstanceAdminFormPage(String processInstanceUUIDStr, String pageId) throws RPCException, SessionTimeOutException;

    /**
     * Validate a form field value using the validators provided
     * 
     * @param formID a form page id
     * @param urlContext url parameters map
     * @param validatorsMap Map of validators to use
     * @param widgetValues a map of the fields ids and values
     * @param submitButtonId the submit button ID
     * @return The list of validators whose validate method returned false
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    Map<String, List<FormValidator>> validateFormFields(String formID, Map<String, Object> urlContext, Map<String, List<FormValidator>> validatorsMap, Map<String, FormFieldValue> widgetValues, String submitButtonId) throws RPCException, SessionTimeOutException;
    
    /**
     * Validate a form field value using the validators provided
     * 
     * @param taskUUIDStr the task UUID as a String
     * @param validators Map of field ID, List<FormValidator> to use
     * @param widgetValues the values to validate as a Map of field ID, {@link FormFieldValue} object
     * @return a Map of field ID, List<FormValidator> for which the field value does not comply with the validation
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    Map<String, List<FormValidator>> validateTaskFieldsAdmin(String taskUUIDStr, Map<String, List<FormValidator>> validatorsMap, Map<String, FormFieldValue> widgetValues) throws RPCException, SessionTimeOutException;

    /**
     * Validate a form field value using the validators provided
     * 
     * @param processInstanceUUIDStr the process instance UUID as a String
     * @param validators Map of field ID, List<FormValidator> to use
     * @param widgetValues the values to validate as a Map of field ID, {@link FormFieldValue} object
     * @return a Map of field ID, List<FormValidator> for which the field value does not comply with the validation
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    Map<String, List<FormValidator>> validateInstanceFieldsAdmin(String processInstanceUUIDStr, Map<String, List<FormValidator>> validatorsMap, Map<String, FormFieldValue> widgetValues) throws RPCException, SessionTimeOutException;

    /**
     * Validate a form page using the validators provided
     * 
     * @param formID a form page id
     * @param urlContext url parameters map
     * @param validators List of validators to use
     * @param fields a map of the fields ids and values
     * @param submitButtonId the submit button ID
     * @return a list of the validators for which the field value does not comply with the validation
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    List<FormValidator> validateFormPage(String formID, Map<String, Object> urlContext, List<FormValidator> validators, Map<String, FormFieldValue> fields, String submitButtonId) throws RPCException, SessionTimeOutException;

    /**
     * Retrieve the confirmation page for a form
     * 
     * @param formID the form id
     * @param urlContext Map containing the URL parameters
     * @return an {@link HtmlTemplate} object representing the page flow confirmation page
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    HtmlTemplate getFormConfirmationTemplate(String formID, Map<String, Object> urlContext) throws RPCException, SessionTimeOutException;

    /**
     * Retrieve the error page for a formID
     * 
     * @param formID form id
     * @param urlContext Map containing the URL parameters
     * @return an {@link HtmlTemplate} object representing the error page
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    HtmlTemplate getApplicationErrorTemplate(String formID, Map<String, Object> urlContext) throws RPCException, SessionTimeOutException;

    /**
     * Skip a form
     * 
     * @param formID current form ID
     * @param urlContext Map containing the URL parameters
     * @return a Map containing the new URL parameters
     * @throws RPCException
     * @throws SessionTimeOutException
     * @throws FormAlreadySubmittedException
     * @throws IllegalActivityTypeException
     */
    Map<String, Object> skipForm(String formID, Map<String, Object> urlContext) throws RPCException, SessionTimeOutException, FormAlreadySubmittedException, IllegalActivityTypeException;

    /**
     * start terminate a task and execute a number of actions specifying the pressed submit button id (this way, only actions
     * related to this button will be performed)
     * 
     * @param formID form id
     * @param urlContext Map containing the URL parameters
     * @param fieldValues a Map of the fields ids and values
     * @param submitButtonId the pressed submit button id
     * @throws RPCException
     * @throws FileTooBigException
     * @throws SessionTimeOutException
     * @throws FormAlreadySubmittedException
     */
    Map<String, Object> executeActions(String formID, Map<String, Object> urlContext, Map<String, FormFieldValue> fieldValues, List<String> pageIds, String submitButtonId) throws RPCException, SessionTimeOutException, FileTooBigException,
            FormAlreadySubmittedException;

    /**
     * Execute a number of actions
     * 
     * @param taskUUIDStr a string representation of the TaskUUID
     * @param fieldValues a Map of the fields ids and values
     * @throws RPCException
     * @throws FileTooBigException
     * @throws SessionTimeOutException
     */
    void executeTaskAdminActions(String taskUUIDStr, Map<String, FormFieldValue> fieldValues) throws RPCException, SessionTimeOutException, FileTooBigException;

    /**
     * Execute a number of actions
     * 
     * @param processInstanceUUIDStr a string representation of the process instance UUID
     * @param fieldValues a Map of the fields ids and values
     * @throws RPCException
     * @throws FileTooBigException
     * @throws SessionTimeOutException
     */
    void executeProcessAdminActions(String processInstanceUUIDStr, Map<String, FormFieldValue> fieldValues) throws RPCException, SessionTimeOutException, FileTooBigException;

    /**
     * Retrieve the next task uuid if it is in the user task list and form id
     * 
     * @param formID form id
     * @param urlContext Map containing the URL parameters
     * @return the next Form URL components or null there is no next task or if the next task is not in the user todolist
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    FormURLComponents getNextFormURL(String formID, Map<String, Object> urlContext) throws RPCException, SessionTimeOutException;

    /**
     * Get async available values
     * 
     * @param formID form id
     * @param urlContext Map containing the URL parameters
     * @param formWidget the widget definition
     * @param currentFieldValue the current value of the widget
     * @return the new list of available values
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    List<FormFieldAvailableValue> getFormAsyncAvailableValues(String formID, Map<String, Object> urlContext, FormWidget formWidget, FormFieldValue currentFieldValue) throws RPCException, SessionTimeOutException;

    /**
     * Get any todolist form URL
     * @param urlContext Map containing the URL parameters
     * @return new urlContext Map containing the URL parameters
     * @throws RPCException
     * @throws SessionTimeOutException
     */
    FormURLComponents getAnyTodoListForm(Map<String, Object> urlContext) throws RPCException, SessionTimeOutException;
}
