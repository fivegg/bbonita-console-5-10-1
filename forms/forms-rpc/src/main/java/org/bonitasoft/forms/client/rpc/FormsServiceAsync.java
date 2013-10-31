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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async version of the FormFlow service
 * @author Anthony Birembaut
 *
 */
public interface FormsServiceAsync {
    
    /**
     * Retrieve the application URL welcome page
     * @param urlContext Map containing the URL parameters
     * @return the application URL welcome page
     * @param aCallBackHandler
     */
    void getWelcomePage(Map<String, Object> urlContext, AsyncCallback<HtmlTemplate> aCallBackHandler);

    /**
     * Retrieve the application URL welcome page
     * @return the application URL welcome page
     * @param aCallBackHandler
     */
    void getExternalWelcomePage(AsyncCallback<String> aCallBackHandler);

    /**
     * Retrieve the application config including the application template
     * @param formID
     * @param urlContext
     * @param includeApplicationTemplate
     * @param callback
     */
    void getApplicationConfig(String formID, Map<String, Object> urlContext, boolean includeApplicationTemplate, AsyncCallback<ApplicationConfig> callback);
    
    /**
     * Retrieve the first page in the page flow associated with the form
     * @param formID
     * @param callback
     * @param urlContext
     */
    void getFormFirstPage(String formID, final Map<String, Object> urlContext, AsyncCallback<FormPage> callback);

    /**
     * Retrieve the next page in the page flow associated with the form
    * @param formID form id 
    * @param urlContext
    * @param pageId
    * @param fieldValues
    * @param callback
    */
   void getFormNextPage(String formID, Map<String, Object> urlContext,String pageId, Map<String, FormFieldValue> fieldValues, AsyncCallback<FormPage> callback);

    /**
     * Retrieve a list of ids of the pages contained in the admin page flow associated with the activity
     * @param taskUUIDStr the task UUID as a string
     * @param aCallBackHandler
     */
    void getTaskAdminPageList(String taskUUIDStr, AsyncCallback<List<String>> aCallBackHandler);
    
    /**
     * Retrieve a list of ids of the pages contained in the admin page flow associated with the process
     * @param processInstanceUUIDStr the process instance UUID as a String
     * @param aCallBackHandler
     */
    void getProcessInstanceAdminPageList(String processInstanceUUIDStr, AsyncCallback<List<String>> aCallBackHandler);
    
    /**
     * Retrieve an admin page data containing a list of the widgets to be displayed
     * @param taskUUIDStr the task UUID as a string
     * @param pageId the page ID
     * @param aCallBackHandler 
     */
    void getTaskAdminFormPage(String taskUUIDStr, String pageId, AsyncCallback<FormPage> aCallBackHandler);
    
    /**
     * Retrieve a process admin page data containing a template and a list of the widgets to be displayed
     * @param processInstanceUUIDStr the process instance as a String
     * @param pageId the page ID
     * @param aCallBackHandler
     */
    void getProcessInstanceAdminFormPage(String processInstanceUUIDStr, String pageId, AsyncCallback<FormPage> aCallBackHandler);
    
    /**
     * Validate some form field values using the validators provided
     * @param formID
     * @param urlContext
     * @param validatorsMap
     * @param widgetValues
     * @param submitButtonId
     * @param aCallBackHandler
     */
    void validateFormFields(String formID, Map<String, Object> urlContext, Map<String, List<FormValidator>> validatorsMap, Map<String, FormFieldValue> widgetValues, String submitButtonId, AsyncCallback<Map<String, List<FormValidator>>> aCallBackHandler);

    /**
     * Validate some form field values using the validators provided
     * @param taskUUIDStr the task UUID as a String
     * @param validators Map of field ID, List<FormValidator> to use
     * @param widgetValues the values to validate as a Map of field ID, {@link FormFieldValue} object
     * @param aCallBackHandler
     */
    void validateTaskFieldsAdmin(String taskUUIDStr, Map<String, List<FormValidator>> validatorsMap, Map<String, FormFieldValue> widgetValues, AsyncCallback<Map<String, List<FormValidator>>> aCallBackHandler);
    
    /**
     * Validate some form field values using the validators provided
     * @param processInstanceUUIDStr the process instance UUID as a String
     * @param validators Map of field ID, List<FormValidator> to use
     * @param widgetValues the values to validate as a Map of field ID, {@link FormFieldValue} object
     * @param aCallBackHandler
     */
    void validateInstanceFieldsAdmin(String processInstanceUUIDStr, Map<String, List<FormValidator>> validatorsMap, Map<String, FormFieldValue> widgetValues, AsyncCallback<Map<String, List<FormValidator>>> aCallBackHandler);
    
    /**
     * Validate a form page using the validators provided
     * @param formID a form page id 
     * @param urlContext combin url param to context
     * @param validators List of validators to use
     * @param fields a map of the fields ids and values
     * @param submitButtonId
     * @param aCallBackHandler
     */
    void validateFormPage(String formID, Map<String, Object> urlContext, List<FormValidator> validators, Map<String, FormFieldValue> fields, String submitButtonId, AsyncCallback<List<FormValidator>> aCallBackHandler);

    /**
     * Retrieve the confirmation page for a form
     * @param formID the form id
     * @param urlContext Map containing the URL parameters 
     * @param aCallBackHandler
     */
    void getFormConfirmationTemplate(String formID, Map<String, Object> urlContext, AsyncCallback<HtmlTemplate> aCallBackHandler);

    /**
     * Retrieve the error page for a application
     * @param formID form id 
     * @param urlContext Map containing the URL parameters 
     * @param callback
     */
    void getApplicationErrorTemplate(String formID, Map<String, Object> urlContext, AsyncCallback<HtmlTemplate> callback);

    /**
     * start terminate a task and set a number of variables specifying the pressed submit button id 
     * (this way, only actions related to this button will be performed)
     * @param formID current id of form
     * @param urlContext Map containing the URL parameters
     * @param fieldValues variables a Map of the fields ids and values
     * @param pageIds the page flow followed by the user
     * @param submitButtonId the pressed submit button id
     * @param aCallBackHandler
     */
    void executeActions(String formID, Map<String, Object> urlContext, Map<String, FormFieldValue> fieldValues, List<String> pageIds, String submitButtonId, AsyncCallback<Map<String, Object>> aCallBackHandler);

    /**
     * Execute a number of actions
     * @param taskUUIDStr a string representation of the TaskUUID
     * @param fieldValues a Map of the fields ids and values
     * @param aCallBackHandler 
     */
    void executeTaskAdminActions(String taskUUIDStr, Map<String, FormFieldValue> fieldValues, AsyncCallback<Void> aCallBackHandler);

    /**
     * Skip a form
     * @param formID current form ID
     * @param urlContext Map containing the URL parameters
     * @param aCallBackHandler
     */
    void skipForm(String formID, Map<String, Object> urlContext, AsyncCallback<Map<String, Object>> aCallBackHandler);

    /**
     * Execute a number of actions
     * @param processInstanceUUIDStr a string representation of the processDefinitionUUID
     * @param fieldValues a Map of the fields ids and values
     */
    void executeProcessAdminActions (String processInstanceUUIDStr, Map<String, FormFieldValue> fieldValues, AsyncCallback<Void> aCallBackHandler);

    /**
     * Retrieve the next task uuid if it is in the user task list and form id
     * @param formID form id
     * @param urlContext Map containing the URL parameters 
     * @param aCallBackHandler
     */
    void getNextFormURL(String formID, Map<String, Object> urlContext, AsyncCallback<FormURLComponents> aCallBackHandler);

    /**
     * Get async available values
     * @param formID form id
     * @param urlContext Map containing the URL parameters 
     * @param formWidget the widget definition
     * @param currentFieldValue the current value of the widget
     * @param asyncCallback
     */
    void getFormAsyncAvailableValues(String formID, Map<String, Object> urlContext, FormWidget formWidget, FormFieldValue currentFieldValue, AsyncCallback<List<FormFieldAvailableValue>> asyncCallback);

    /**
     * Get any todolist form URL
     * @param urlContext Map containing the URL parameters 
     * @param asyncCallback
     */
    void getAnyTodoListForm(Map<String, Object> urlContext, AsyncCallback<FormURLComponents> asyncCallback);
}
