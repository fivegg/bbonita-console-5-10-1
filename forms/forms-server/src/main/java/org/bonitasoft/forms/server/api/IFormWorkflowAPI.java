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
package org.bonitasoft.forms.server.api;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.forms.client.model.ActivityEditState;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.exception.IllegalActivityTypeException;
import org.bonitasoft.forms.server.exception.FileTooBigException;
import org.ow2.bonita.facade.exception.ActivityDefNotFoundException;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 * Workflow service useful to bind the form to bonita server
 * 
 * @author Anthony Birembaut
 */
public interface IFormWorkflowAPI {
    
    /**
     * Check whether a task is over or not
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @return true if the task is finished or has been aborted
     * @throws ActivityNotFoundException
     */
    boolean isTaskOver(ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException;
    
    /**
     * Check if a task is in the tasklist of the connected user
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param userName
     * @return true if the task is in the tasklist of the connected user, false otherwise
     * @throws TaskNotFoundException
     */
    boolean isTaskInUserTaskList(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException;
    
    /**
     * Retrieve the next task uuid if it is in the user task list
     * @param processInstanceUUID the UUID of the prrocess instance
     * @return the next task UUID or null there is no next task or if the next task is not in the user todolist
     * @throws InstanceNotFoundException 
     */
    ActivityInstanceUUID getProcessInstanceNextTask(ProcessInstanceUUID processInstanceUUID) throws InstanceNotFoundException;

    /**
     * Check the child and parent pocesses and retrieve the next task uuid if it is in the user task list
     * @param processInstanceUUID the UUID of the current process instance
     * @return the next task UUID or null there is no next task or if the next task is not in the user todolist
     * @throws InstanceNotFoundException
     */
    ActivityInstanceUUID getRelatedProcessesNextTask(ProcessInstanceUUID processInstanceUUID) throws InstanceNotFoundException;
    
    /**
     * Retrieve any task uuid of the user todolist. If the process UUID is valid, the task belong to the process otherwise any task from the user todolist can be returned.
     * Return null if no task is found.
     * @param processDefinitionUUID
     * @return the task UUID of one of the task of the user todolist
     */
    ActivityInstanceUUID getAnyTodoListTaskForProcessDefinition(ProcessDefinitionUUID processDefinitionUUID);
    
    /**
     * Retrieve any task uuid of the user todolist. If the process instance UUID is valid, the task belong to the process instance otherwise any task from the user todolist can be returned.
     * Return null if no task is found.
     * @param processInstanceUUID
     * @return the task UUID of one of the task of the user todolist
     */
    ActivityInstanceUUID getAnyTodoListTaskForProcessInstance(ProcessInstanceUUID processInstanceUUID);

    /**
     * Retrieve the deployment date of a process definition
     * @param processDefinitionUUID
     * @return the {@link Date} of the process deployment date
     * @throws ProcessNotFoundException
     */
    Date getProcessDefinitionDate(ProcessDefinitionUUID processDefinitionUUID) throws ProcessNotFoundException;
    
    /**
     * Retrieve the migration date of a process definition
     * @param processDefinitionUUID
     * @return the {@link Date} of the process deployment
     * @throws ProcessNotFoundException 
     */
    Date getMigrationDate(ProcessDefinitionUUID processDefinitionUUID) throws ProcessNotFoundException;
    
    /**
     * Retrieve a field initial value
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param expression the initial value expression
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @return the initial value for this field
     * @throws InstanceNotFoundException
     * @throws ActivityNotFoundException
     */
    Object getFieldValue(ActivityInstanceUUID activityInstanceUUID, String expression, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException, ActivityNotFoundException;
    
    /**
     * Retrieve a field initial value
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param expression the initial value expression
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @return the initial value for this field
     * @throws InstanceNotFoundException
     * @throws ActivityNotFoundException
     */
    Object getFieldValue(ActivityInstanceUUID activityInstanceUUID, String expression, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException;
    
    /**
     * Retrieve a field initial value
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID}
     * @param expression the initial value expression
     * @param locale the user's locale
     * @return the initial value for this field
     * @throws ProcessNotFoundException
     */
    Object getFieldValue(ProcessDefinitionUUID processDefinitionUUID, String expression, Locale locale) throws ProcessNotFoundException;
    
    /**
     * Retrieve a field initial value
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID}
     * @param expression the initial value expression
     * @param locale the user's locale
     * @return the initial value for this field
     * @throws ProcessNotFoundException
     */
    Object getFieldValue(ProcessDefinitionUUID processDefinitionUUID, String expression, Locale locale, Map<String, Object> context) throws ProcessNotFoundException;
    
    /**
     * Retrieve a field initial value
     * @param processInstanceUUID the {@link ProcessInstanceUUID} 
     * @param expression the initial value expression
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @return the initial value for this field
     * @throws InstanceNotFoundException
     */
    Object getFieldValue(ProcessInstanceUUID processInstanceUUID, String expression, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException;
    
    /**
     * Retrieve a field initial value
     * @param processInstanceUUID the {@link ProcessInstanceUUID} 
     * @param expression the initial value expression
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @return the initial value for this field
     * @throws InstanceNotFoundException
     */
    Object getFieldValue(ProcessInstanceUUID processInstanceUUID, String expression, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException;
    
    /**
     * Retrieve a field value
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param expression the initial value expression
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @return the value for this field
     * @throws InstanceNotFoundException
     * @throws ActivityNotFoundException
     */
    Object getFieldValue(ActivityInstanceUUID activityInstanceUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException, ActivityNotFoundException;

    /**
     * Retrieve a field value
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param expression the initial value expression
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @return the value for this field
     * @throws InstanceNotFoundException
     * @throws ActivityNotFoundException
     */
    Object getFieldValue(ActivityInstanceUUID activityInstanceUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException;

    /**
     * Retrieve a field value
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID} 
     * @param expression the initial value expression
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @return the value for this field
     * @throws ProcessNotFoundException
     */
    Object getFieldValue(ProcessDefinitionUUID processDefinitionUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale) throws ProcessNotFoundException;

    /**
     * Retrieve a field value
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID} 
     * @param expression the initial value expression
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @return the value for this field
     * @throws ProcessNotFoundException
     */
    Object getFieldValue(ProcessDefinitionUUID processDefinitionUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, Map<String, Object> context) throws ProcessNotFoundException;
    
    /**
     * Retrieve a field value
     * @param processInstanceUUID the {@link ProcessInstanceUUID} 
     * @param expression the initial value expression
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @return the value for this field
     * @throws InstanceNotFoundException
     */
    Object getFieldValue(ProcessInstanceUUID processInstanceUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException;
    
    /**
     * Retrieve a field value
     * @param processInstanceUUID the {@link ProcessInstanceUUID} 
     * @param expression the initial value expression
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @return the value for this field
     * @throws InstanceNotFoundException
     */
    Object getFieldValue(ProcessInstanceUUID processInstanceUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException;
    
    /**
     * Check whether there is an attachment with the provided name for a process instance or not
     * @param processInstanceUUID the {@link ProcessInstanceUUID} 
     * @param attachmentName the attachment name
     * @param isCurrentValue if true, the file name returned is the current value for the instance. otherwise, it's the value at instantiation
     * @return the file name of the attachment if there is an attachment for this instance with the provided name, null otherwise
     * @throws InstanceNotFoundException
     */
    String getAttachmentFileName(ProcessInstanceUUID processInstanceUUID, String attachmentName, boolean isCurrentValue) throws InstanceNotFoundException;
    
    /**
     * Check whether there is an attachment with the provided name for a process instance or not
     * @param activityInstanceUUID the {@link ActivityInstanceUUID}
     * @param attachmentName the attachment name
     * @param isCurrentValue if true, the file name returned is the current value for the instance. otherwise, it's the value at step execution
     * @return the file name of the attachment if there is an attachment for this instance with the provided name, null otherwise
     * @throws ActivityNotFoundException
     */
    String getAttachmentFileName(ActivityInstanceUUID activityInstanceUUID, String attachmentName, boolean isCurrentValue) throws ActivityNotFoundException;
    
    /**
     * Check whether there is an attachment with the provided name for a process instance or not
     * @param processDefinitionUUID the process definition UUID
     * @param attachmentName the attachment name
     * @return the file name of the attachment if there is an attachment for this process with the provided name, null otherwise
     * @throws InstanceNotFoundException
     */
    String getAttachmentFileName(ProcessDefinitionUUID processDefinitionUUID, String attachmentName) throws ProcessNotFoundException;
    
    /**
     * Start terminate a task and execute a number of actions specifying the pressed submit button id 
     * (this way, only actions related to this button will be performed)
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param values a Map of the fields id and values
     * @param actions a list of {@link FormAction} to execute at form validation
     * @param submitButtonId the pressed submit button id
     * @param locale the user's locale
     * @throws Exception
     */
    void executeActionsAndTerminate(ActivityInstanceUUID activityInstanceUUID, Map<String, FormFieldValue> fieldValues, List<FormAction> actions, Locale locale, String submitButtonId, Map<String, Object> context) throws Exception;
    
    /**
     * Instantiate a process and execute actions specifying the pressed submit button id 
     * (this way, only actions related to this button will be performed).
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID}
     * @param values a Map of the fields id and values
     * @param actions a list of {@link FormAction} to execute at form validation
     * @param submitButtonId the pressed submit button id
     * @param locale the user's locale
     * @return the {@link ProcessInstanceUUID} of the process instance created
     * @throws Exception
     */
    ProcessInstanceUUID executeActionsAndStartInstance(ProcessDefinitionUUID processDefinitionUUID, Map<String, FormFieldValue> fieldValues, List<FormAction> actions, Locale locale, String submitButtonId, Map<String, Object> context) throws Exception;

    /**
     * Execute a number of actions.
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param values a Map of the fields id and values
     * @param actions a list of {@link FormAction} to execute at form validation
     * @param locale the user's locale
     * @throws TaskNotFoundException
     * @throws VariableNotFoundException 
     * @throws ProcessNotFoundException 
     * @throws ActivityDefNotFoundException 
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     * @throws FileTooBigException 
     */
    void executeActions(ActivityInstanceUUID activityInstanceUUID, Map<String, FormFieldValue> fieldValues, List<FormAction> actions, Locale locale) throws TaskNotFoundException, InstanceNotFoundException, ActivityNotFoundException, ActivityDefNotFoundException, ProcessNotFoundException, VariableNotFoundException, FileTooBigException;
    
    /**
     * Execute a number of actions.
     * @param processInstanceUUID the {@link ProcessInstanceUUID}
     * @param values a Map of the fields id and values
     * @param actions a list of {@link FormAction} to execute at form validation
     * @param locale the user's locale
     * @return the {@link ProcessInstanceUUID} of the process instance created
     * @throws VariableNotFoundException 
     * @throws ProcessNotFoundException  
     * @throws InstanceNotFoundException 
     * @throws FileTooBigException 
     */
    void executeActions(ProcessInstanceUUID processInstanceUUID, Map<String, FormFieldValue> fieldValues, List<FormAction> actions, Locale locale) throws ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, FileTooBigException;
    
    /**
     * Retrieve the step attributes for the activity
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param locale the user's locale
     * @return a {@link List} of Candidates as Strings
     * @throws ActivityNotFoundException
     */
    Map<String, String> getAttributes(ActivityInstanceUUID activityInstanceUUID, Locale locale) throws ActivityNotFoundException;
    
    /**
     * Return the activity edition state
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @return the {@link ActivityEditState}
     * @throws ActivityNotFoundException
     */
    ActivityEditState getTaskEditState(ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException;
    
    /**
     * Check whether the activity is a task or not (automatic activity, gateway, timer...)
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @return true if the activity is a task, false otherwise
     * @throws ActivityNotFoundException
     */
    boolean isTask(ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException;

    /**
     * Execute a task
     * @param activityInstanceUUID the {@link ActivityInstanceUUID}
     * @throws IllegalTaskStateException 
     * @throws TaskNotFoundException 
     * @throws ActivityNotFoundException 
     * @throws IllegalActivityTypeException 
     */
    void terminate(ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, ActivityNotFoundException, IllegalActivityTypeException;

    /**
     * Start an instance
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID}
     * @return the {@link ProcessInstanceUUID}
     * @throws ProcessNotFoundException 
     */
    ProcessInstanceUUID startInstance(ProcessDefinitionUUID processDefinitionUUID) throws ProcessNotFoundException;
    
    /**
     * Retrieve the users involved in a process instance
     * @param processInstanceUUID the process instance UUID
     * @return a set of usernames
     * @throws InstanceNotFoundException
     */
    Set<String> getProcessInvolvedUsers(ProcessInstanceUUID processInstanceUUID) throws InstanceNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param expressions the initial values expressions Map
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @param transientDataContext the context of transient data
     * @return the values for the fields as a Map
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> getFieldsValues(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> transientDataContext) throws ActivityNotFoundException, InstanceNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param expressions the initial values expressions Map
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @return the values for the fields as a Map
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> getFieldsValues(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue) throws ActivityNotFoundException, InstanceNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param expressions the initial values expressions Map
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @param transientDataContext
     * @return the values for the fields as a Map
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> getFieldsValues(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue, Map<String, Object> transientDataContext) throws ActivityNotFoundException, InstanceNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param activityInstanceUUID the {@link ActivityInstanceUUID} 
     * @param expressions the initial values expressions Map
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @return the values for the fields as a Map
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> getFieldsValues(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue) throws ActivityNotFoundException, InstanceNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param processInstanceUUID the {@link ProcessInstanceUUID} 
     * @param expressions the initial values expressions Map
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @param transientDataContext the context of transient data
     * @return the values for the fields as a Map
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> getFieldsValues(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> transientDataContext) throws InstanceNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param processInstanceUUID the {@link ProcessInstanceUUID} 
     * @param expressions the initial values expressions Map
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @return the values for the fields as a Map
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> getFieldsValues(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param processInstanceUUID the {@link ProcessInstanceUUID} 
     * @param expressions the initial values expressions Map
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @param transientDataContext the context of transient data
     * @return the values for the fields as a Map
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> getFieldsValues(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue, Map<String, Object> transientDataContext) throws InstanceNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param processInstanceUUID the {@link ProcessInstanceUUID} 
     * @param expressions the initial values expressions Map
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @return the values for the fields as a Map
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> getFieldsValues(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID} 
     * @param expressions the initial values expressions Map
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @param transientDataContext the context of transient data
     * @return the values for the fields as a Map
     * @throws ProcessNotFoundException 
     */
    Map<String, Object> getFieldsValues(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, Map<String, Object> transientDataContext) throws ProcessNotFoundException;

    /**
     * Retrieve some fields initial value
     * Retrieve some fields initial value
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID} 
     * @param expressions the initial values expressions Map
     * @param fieldValues some field values this field may depend on
     * @param locale the user's locale
     * @return the values for the fields as a Map
     * @throws ProcessNotFoundException 
     */
    Map<String, Object> getFieldsValues(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale) throws ProcessNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID} 
     * @param expressions the initial values expressions Map
     * @param locale the user's locale
     * @param transientDataContext the context of transient data
     * @return the values for the fields as a Map
     * @throws ProcessNotFoundException 
     */
    Map<String, Object> getFieldsValues(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Locale locale, Map<String, Object> transientDataContext) throws ProcessNotFoundException;

    /**
     * Retrieve some fields initial value
     * @param processDefinitionUUID the {@link ProcessDefinitionUUID} 
     * @param expressions the initial values expressions Map
     * @param locale the user's locale
     * @return the values for the fields as a Map
     * @throws ProcessNotFoundException 
     */
    Map<String, Object> getFieldsValues(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Locale locale) throws ProcessNotFoundException;
}
