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
package org.bonitasoft.forms.server.api.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.ActivityAttribute;
import org.bonitasoft.forms.client.model.ActivityEditState;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.exception.IllegalActivityTypeException;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormExpressionsAPI;
import org.bonitasoft.forms.server.api.IFormWorkflowAPI;
import org.bonitasoft.forms.server.exception.FileTooBigException;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.AttachmentDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ActivityDefNotFoundException;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.DataFieldNotFoundException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.Document;
import org.ow2.bonita.facade.runtime.InitialAttachment;
import org.ow2.bonita.facade.runtime.command.WebExecuteTask;
import org.ow2.bonita.facade.runtime.command.WebInstantiateProcess;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.light.LightTaskInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.Command;
import org.ow2.bonita.util.Misc;

/**
 * implementation of {@link IFormWorkflowAPI}
 * 
 * @author Anthony Birembaut
 */
public class FormWorkflowAPIImpl implements IFormWorkflowAPI {

    public static final int SECONDS_IN_A_DAY = 86400;

    public static final int SECONDS_IN_AN_HOUR = 3600;

    public static final int SECONDS_IN_A_MINUTE = 60;

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(FormWorkflowAPIImpl.class.getName());
    
    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ActivityInstanceUUID activityInstanceUUID, final String expression, final Locale locale, final boolean isCurrentValue) throws InstanceNotFoundException, ActivityNotFoundException {

        return getFieldValue(activityInstanceUUID, expression, locale, isCurrentValue, new HashMap<String, Object>());
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ActivityInstanceUUID activityInstanceUUID, final String expression, final Locale locale, final boolean isCurrentValue, final Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateInitialExpression(activityInstanceUUID, expression, locale, isCurrentValue, context);
    }

    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ProcessDefinitionUUID processDefinitionUUID, final String expression, final Locale locale) throws ProcessNotFoundException {

        return getFieldValue(processDefinitionUUID, expression, locale, new HashMap<String, Object>());
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ProcessDefinitionUUID processDefinitionUUID, final String expression, final Locale locale, final Map<String, Object> context) throws ProcessNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateInitialExpression(processDefinitionUUID, expression, locale, context);
    }

    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ProcessInstanceUUID processInstanceUUID, final String expression, final Locale locale, final boolean isCurrentValue) throws InstanceNotFoundException {

        return getFieldValue(processInstanceUUID, expression, locale, isCurrentValue, new HashMap<String, Object>());
    }

    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ProcessInstanceUUID processInstanceUUID, final String expression, final Locale locale, final boolean isCurrentValue, final Map<String, Object> context) throws InstanceNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateInitialExpression(processInstanceUUID, expression, locale, isCurrentValue, context);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ActivityInstanceUUID activityInstanceUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final boolean isCurrentValue) throws InstanceNotFoundException, ActivityNotFoundException {

        return getFieldValue(activityInstanceUUID, expression, fieldValues, locale, isCurrentValue, new HashMap<String, Object>());
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ActivityInstanceUUID activityInstanceUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final boolean isCurrentValue, final Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateExpression(activityInstanceUUID, expression, fieldValues, locale, isCurrentValue, context);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ProcessDefinitionUUID processDefinitionUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale) throws ProcessNotFoundException {

        return getFieldValue(processDefinitionUUID, expression, fieldValues, locale, new HashMap<String, Object>());
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ProcessDefinitionUUID processDefinitionUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final Map<String, Object> context) throws ProcessNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateExpression(processDefinitionUUID, expression, fieldValues, locale, context);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ProcessInstanceUUID processInstanceUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final boolean isCurrentValue) throws InstanceNotFoundException {
        
        return getFieldValue(processInstanceUUID, expression, fieldValues, locale, isCurrentValue, new HashMap<String, Object>());
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final ProcessInstanceUUID processInstanceUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final boolean isCurrentValue, final Map<String, Object> context) throws InstanceNotFoundException {
        
        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateExpression(processInstanceUUID, expression, fieldValues, locale, isCurrentValue, context);
    }

    /**
     * {@inheritDoc}
     */
    public void executeActionsAndTerminate(final ActivityInstanceUUID activityInstanceUUID, final Map<String, FormFieldValue> fieldValues, final List<FormAction> actions, final Locale locale, final String submitButtonId, final Map<String, Object> context)
            throws Exception {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        
        Map<String, Object> processVariables = null;
        Map<String, Object> activityVariables = null;
        Map<String, Object> undefinedVariables = null;
        Set<InitialAttachment> attachments = null;
        List<String> scriptsToExecute = null;
        Map<String, Object> scriptContext = null;
        
        for (final FormAction action : actions) {
            if (action.getSubmitButtonId() == null || action.getSubmitButtonId().length() == 0 || action.getSubmitButtonId().equals(submitButtonId)) {

                final String expression = action.getExpression();
                if (action.getType().equals(ActionType.SET_VARIABLE)) {
                    final Object variableIdObject = formExpressionsAPI.evaluateInitialExpression(activityInstanceUUID, action.getVariableId(), locale, true, context);
                    if (variableIdObject instanceof String) {
                        final Object value = formExpressionsAPI.evaluateExpression(activityInstanceUUID, expression, fieldValues, locale, true, context);
                        final String variableId = (String)variableIdObject;
                        final ActivityDefinitionUUID activityDefinitionUUID = activityInstanceUUID.getActivityDefinitionUUID();
                        boolean undefinedVariable = false;
                        if (variableId.contains(BonitaConstants.JAVA_VAR_SEPARATOR)) {
                            undefinedVariable = true;
                        } else {
                            try {
                                DataFieldDefinition dataField = null;
                                if (!FormServiceProviderUtil.PROCESS_VARIABLE_TYPE.equals(action.getVariableType())) {
                                    try {
                                        dataField = queryDefinitionAPI.getActivityDataField(activityDefinitionUUID, variableId);
                                        final Object objectValue = formExpressionsAPI.getObjectValue(value, dataField.getDataTypeClassName());
                                        if (activityVariables == null) {
                                            activityVariables = new HashMap<String, Object>();
                                        }
                                        activityVariables.put(variableId, objectValue);
                                    } catch (final DataFieldNotFoundException e) {
                                        if (FormServiceProviderUtil.ACTIVITY_VARIABLE_TYPE.equals(action.getVariableType())) {
                                            if (LOGGER.isLoggable(Level.INFO)) {
                                                LOGGER.log(Level.INFO, "Data " + variableId + " with type " + action.getVariableType() + " not found. Setting the variable with the output type of the form field.", e);
                                            }
                                            throw new DataFieldNotFoundException(e);
                                        }
                                    }
                                }
                                if (dataField == null) {
                                    dataField = queryDefinitionAPI.getProcessDataField(activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID(), variableId);
                                    final Object objectValue = formExpressionsAPI.getObjectValue(value, dataField.getDataTypeClassName());
                                    if (processVariables == null) {
                                        processVariables = new HashMap<String, Object>();
                                    }
                                    processVariables.put(variableId, objectValue);
                                }
                            } catch (final DataFieldNotFoundException e) {
                                if (LOGGER.isLoggable(Level.INFO)) {
                                    LOGGER.log(Level.INFO, "Data " + variableId + " with type " + action.getVariableType() + " not found. Setting the variable with the output type of the form field.", e);
                                }
                                undefinedVariable = true;
                            }
                        }
                        if (undefinedVariable) {
                            final Object objectValue = formExpressionsAPI.getObjectValue(value, String.class.getName());
                            if (undefinedVariables == null) {
                                undefinedVariables = new HashMap<String, Object>();
                            }
                            undefinedVariables.put(variableId, objectValue);
                        }
                    } else {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, "The variable to set should be either a String or a groovy expression returning a String which is not the case of value : " + action.getVariableId());
                        }
                    }
                } else if (action.getType().equals(ActionType.SET_ATTACHMENT)) {
                    if (attachments == null) {
                        attachments = new HashSet<InitialAttachment>();
                    }
                    formExpressionsAPI.performSetAttachmentAction(activityInstanceUUID.getProcessInstanceUUID(), attachments, action, fieldValues, locale, false);
                } else if (action.getType().equals(ActionType.EXECUTE_SCRIPT)) {
                    if (scriptsToExecute == null) {
                        scriptContext = formExpressionsAPI.generateGroovyContext(fieldValues, locale);
                        scriptContext.putAll(context);
                        scriptsToExecute = new ArrayList<String>();
                    }
                    scriptsToExecute.add(expression);
                }
            }
        }
        if (LOGGER.isLoggable(Level.INFO)) {
            final String username = AccessorUtil.getManagementAPI().getLoggedUser();
            LOGGER.log(Level.INFO, username + " executing task " + activityInstanceUUID.getValue());
        }
        final Command<Void> executeTaskCommand = new WebExecuteTask(activityInstanceUUID, processVariables, activityVariables, undefinedVariables, attachments, scriptsToExecute, scriptContext);
        AccessorUtil.getCommandAPI().execute(executeTaskCommand);
    }

    /**
     * {@inheritDoc}
     */
    public ProcessInstanceUUID executeActionsAndStartInstance(final ProcessDefinitionUUID processDefinitionUUID, final Map<String, FormFieldValue> fieldValues, final List<FormAction> actions, final Locale locale,
            final String submitButtonId, final Map<String, Object> context) throws Exception {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        
        Map<String, Object> processVariables = null;
        Set<InitialAttachment> attachments = null;
        List<String> scriptsToExecute = null;
        Map<String, Object> scriptContext = null;

        for (final FormAction action : actions) {
            if (action.getSubmitButtonId() == null || action.getSubmitButtonId().length() == 0 || action.getSubmitButtonId().equals(submitButtonId)) {

                final String expression = action.getExpression();
                
                if (action.getType().equals(ActionType.SET_VARIABLE)) {
                    final Object variableIdObject = formExpressionsAPI.evaluateInitialExpression(processDefinitionUUID, action.getVariableId(), locale, context);
                    if (variableIdObject instanceof String) {
                        final Object value = formExpressionsAPI.evaluateExpression(processDefinitionUUID, expression, fieldValues, locale, context);
                        final String variableId = (String)variableIdObject;
                        Object objectValue = null;
                        if (variableId.contains(BonitaConstants.JAVA_VAR_SEPARATOR)) {
                            objectValue = formExpressionsAPI.getObjectValue(value, String.class.getName());
                        } else {
                            try {
                                final DataFieldDefinition dataField = queryDefinitionAPI.getProcessDataField(processDefinitionUUID, variableId);
                                objectValue = formExpressionsAPI.getObjectValue(value, dataField.getDataTypeClassName());
                            } catch (final DataFieldNotFoundException e) {
                                if (LOGGER.isLoggable(Level.INFO)) {
                                    LOGGER.log(Level.INFO, "Process data field " + variableId + " not found. Setting the variable with the output type of the form field.", e);
                                }
                                objectValue = formExpressionsAPI.getObjectValue(value, String.class.getName());
                            }
                        }
                        if (processVariables == null) {
                            processVariables = new HashMap<String, Object>();
                        }
                        processVariables.put(variableId, objectValue);
                    } else {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, "The variable to set should be either a String or a groovy expression returning a String which is not the case of value : " + action.getVariableId());
                        }
                    }
                } else if (action.getType().equals(ActionType.SET_ATTACHMENT)) {
                    if (attachments == null) {
                        attachments = new HashSet<InitialAttachment>();
                    }
                    formExpressionsAPI.performSetAttachmentAction(null, attachments, action, fieldValues, locale, true);
                } else if (action.getType().equals(ActionType.EXECUTE_SCRIPT)) {
                    if (scriptsToExecute == null) {
                        scriptContext = formExpressionsAPI.generateGroovyContext(fieldValues, locale);
                        scriptContext.putAll(context);
                        scriptsToExecute = new ArrayList<String>();
                    }
                    scriptsToExecute.add(expression);
                }
            }
        }
        if (LOGGER.isLoggable(Level.INFO)) {
            final String username = AccessorUtil.getManagementAPI().getLoggedUser();
            LOGGER.log(Level.INFO, username + " starting an instance of " + processDefinitionUUID.getValue());
        }
        final Command<ProcessInstanceUUID> instantiateProcessCommand = new WebInstantiateProcess(processDefinitionUUID, processVariables, attachments, scriptsToExecute, scriptContext);
        return AccessorUtil.getCommandAPI().execute(instantiateProcessCommand);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void executeActions(final ActivityInstanceUUID activityInstanceUUID, final Map<String, FormFieldValue> fieldValues, final List<FormAction> actions, final Locale locale) throws TaskNotFoundException, InstanceNotFoundException, ActivityNotFoundException, ActivityDefNotFoundException, ProcessNotFoundException, VariableNotFoundException, FileTooBigException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        for (final FormAction action : actions) {
            formExpressionsAPI.executeAction(activityInstanceUUID, action, fieldValues, locale);
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void executeActions(final ProcessDefinitionUUID processDefinitionUUID, final Map<String, FormFieldValue> fieldValues, final List<FormAction> actions, final Locale locale) throws ProcessNotFoundException,
            VariableNotFoundException, FileTooBigException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        final Set<InitialAttachment> attachments = new HashSet<InitialAttachment>();
        for (final FormAction action : actions) {
            formExpressionsAPI.executeAction(processDefinitionUUID, action, variableValues, attachments, fieldValues, locale);
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void executeActions(final ProcessInstanceUUID processInstanceUUID, final Map<String, FormFieldValue> fieldValues, final List<FormAction> actions, final Locale locale) throws ProcessNotFoundException,
            VariableNotFoundException, InstanceNotFoundException, FileTooBigException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        for (final FormAction action : actions) {
            formExpressionsAPI.executeAction(processInstanceUUID, action, fieldValues, locale);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Date getProcessDefinitionDate(final ProcessDefinitionUUID processDefinitionUUID) throws ProcessNotFoundException {

        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        return queryDefinitionAPI.getLightProcess(processDefinitionUUID).getDeployedDate();
    }
    


    /**
     * {@inheritDoc}
     */
    public Date getMigrationDate(ProcessDefinitionUUID processDefinitionUUID) throws ProcessNotFoundException {
        
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        return queryDefinitionAPI.getMigrationDate(processDefinitionUUID);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTaskOver(final ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException {

        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final ActivityState activityInstanceState = queryRuntimeAPI.getActivityInstanceState(activityInstanceUUID);
        return ActivityState.ABORTED.equals(activityInstanceState) || ActivityState.FINISHED.equals(activityInstanceState) || ActivityState.SKIPPED.equals(activityInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    public ActivityEditState getTaskEditState(final ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException {

        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final ActivityState activityInstanceState = queryRuntimeAPI.getActivityInstanceState(activityInstanceUUID);
        if (ActivityState.CANCELLED.equals(activityInstanceState)) {
            return ActivityEditState.CANCELED;
        } else if (ActivityState.SUSPENDED.equals(activityInstanceState)) {
            return ActivityEditState.SUSPENDED;
        } else if (ActivityState.FAILED.equals(activityInstanceState)) {
            return ActivityEditState.FAILED;
        } else if (ActivityState.SKIPPED.equals(activityInstanceState)) {
            return ActivityEditState.SKIPPED;
        } else if (ActivityState.ABORTED.equals(activityInstanceState) || ActivityState.FINISHED.equals(activityInstanceState) || ActivityState.SKIPPED.equals(activityInstanceState) || !isTask(activityInstanceUUID)) {
            return ActivityEditState.NOT_EDITABLE;
        } else {
            return ActivityEditState.EDITABLE;
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isTaskInUserTaskList(final ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException {

        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        return queryRuntimeAPI.canExecuteTask(activityInstanceUUID);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public ActivityInstanceUUID getProcessInstanceNextTask(final ProcessInstanceUUID processInstanceUUID) throws InstanceNotFoundException {

        ActivityInstanceUUID nextActivityInstanceUUID = null;

        if (processInstanceUUID != null) {
            final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

            nextActivityInstanceUUID = queryRuntimeAPI.getOneTask(processInstanceUUID, ActivityState.READY);
        }
        return nextActivityInstanceUUID;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public ActivityInstanceUUID getRelatedProcessesNextTask(final ProcessInstanceUUID processInstanceUUID) throws InstanceNotFoundException {
        ActivityInstanceUUID activityInstanceUUID = null;
        try {
            activityInstanceUUID = getSubprocessNextTask(processInstanceUUID);
        } catch (InstanceNotFoundException e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Cannot find the instance " + processInstanceUUID + ". It may have been removed upon process completion.");
            }
        }

        if (activityInstanceUUID == null) {
            try {
                activityInstanceUUID = getParentProcessNextTask(processInstanceUUID);
            } catch (InstanceNotFoundException e) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Cannot find the instance " + processInstanceUUID + ". It may have been removed upon process completion.");
                }
            }
        }

        return activityInstanceUUID;
    }
    
    /**
     * Check the children pocesses and retrieve the next task uuid if it is in
     * the user task list
     * 
     * @param processInstanceUUID
     *            the UUID of the current process instance
     * @return the next task UUID or null there is no next task or if the next
     *         task is not in the user todolist
     * @throws InstanceNotFoundException
     */
    private ActivityInstanceUUID getSubprocessNextTask(final ProcessInstanceUUID processInstanceUUID) throws InstanceNotFoundException {

        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final Set<ProcessInstanceUUID> children = queryRuntimeAPI.getChildrenInstanceUUIDsOfProcessInstance(processInstanceUUID);
        ActivityInstanceUUID taskUUID = null;
        for (final ProcessInstanceUUID childProcessInstanceUUID : children) {
            taskUUID = queryRuntimeAPI.getOneTask(childProcessInstanceUUID, ActivityState.READY);
            if (taskUUID != null) {
                break;
            } else {
                taskUUID = getSubprocessNextTask(childProcessInstanceUUID);
            }
        }
        return taskUUID;
    }

    /**
     * Check the parent pocesses and retrieve the next task uuid if it is in the
     * user task list
     * 
     * @param processInstanceUUID
     *            the UUID of the current process instance
     * @return the next task UUID or null there is no next task or if the next
     *         task is not in the user todolist
     * @throws InstanceNotFoundException
     */
    private ActivityInstanceUUID getParentProcessNextTask(final ProcessInstanceUUID processInstanceUUID) throws InstanceNotFoundException {

        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final LightProcessInstance processInstance = queryRuntimeAPI.getLightProcessInstance(processInstanceUUID);
        final ProcessInstanceUUID parentProcessInstanceUUID = processInstance.getParentInstanceUUID();
        if (parentProcessInstanceUUID != null) {
            ActivityInstanceUUID taskUUID = queryRuntimeAPI.getOneTask(parentProcessInstanceUUID, ActivityState.READY);
            if (taskUUID != null) {
                return taskUUID;
            } else {
                taskUUID = getSubprocessNextTask(parentProcessInstanceUUID);
                if (taskUUID != null) {
                    return taskUUID;
                } else {
                    return getParentProcessNextTask(parentProcessInstanceUUID);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public ActivityInstanceUUID getAnyTodoListTaskForProcessDefinition(final ProcessDefinitionUUID processDefinitionUUID) {

        ActivityInstanceUUID activityInstanceUUID = null;

        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

        if (processDefinitionUUID != null) {
            activityInstanceUUID = queryRuntimeAPI.getOneTask(processDefinitionUUID, ActivityState.READY);
        } else {
            activityInstanceUUID = queryRuntimeAPI.getOneTask(ActivityState.READY);
        }

        return activityInstanceUUID;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public ActivityInstanceUUID getAnyTodoListTaskForProcessInstance(ProcessInstanceUUID processInstanceUUID) {

        ActivityInstanceUUID activityInstanceUUID = null;

        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

        if (processInstanceUUID != null) {
            activityInstanceUUID = queryRuntimeAPI.getOneTask(processInstanceUUID, ActivityState.READY);
        } else {
            activityInstanceUUID = queryRuntimeAPI.getOneTask(ActivityState.READY);
        }

        return activityInstanceUUID;
    
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public String getAttachmentFileName(final ProcessInstanceUUID processInstanceUUID, String attachmentName, final boolean isCurrentValue) throws InstanceNotFoundException {

        String attachmentFileName = null;

        if (attachmentName != null && attachmentName.length() > 0) {
            if (attachmentName.matches("\\$\\{.*\\}")) {
                attachmentName = attachmentName.substring(2, attachmentName.length() - 1);
            }
            final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
            final LightProcessInstance processInstance = queryRuntimeAPI.getLightProcessInstance(processInstanceUUID);
            final Date startDate = processInstance.getStartedDate();
            Document document = null;
            if (isCurrentValue) {
                document = queryRuntimeAPI.getLastDocument(processInstanceUUID, attachmentName, new Date());
            } else {
                document = queryRuntimeAPI.getLastDocument(processInstanceUUID, attachmentName, startDate);
            }
            if (document != null) {
                attachmentFileName = document.getContentFileName();
            }
        }
        return attachmentFileName;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getAttachmentFileName(final ActivityInstanceUUID activityInstanceUUID, String attachmentName, final boolean isCurrentValue) throws ActivityNotFoundException {

        String attachmentFileName = null;

        if (attachmentName != null && attachmentName.length() > 0) {
            if (attachmentName.matches("\\$\\{.*\\}")) {
                attachmentName = attachmentName.substring(2, attachmentName.length() - 1);
            }
            final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
            Document document = null;
            if (isCurrentValue) {
                document = queryRuntimeAPI.getLastDocument(activityInstanceUUID.getProcessInstanceUUID(), attachmentName, new Date());
            } else {
                document = queryRuntimeAPI.getLastDocument(activityInstanceUUID.getProcessInstanceUUID(), attachmentName, activityInstanceUUID);
            }
            if (document != null) {
                attachmentFileName = document.getContentFileName();
            }
        }
        return attachmentFileName;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getAttachmentFileName(final ProcessDefinitionUUID processDefinitionUUID, String attachmentName) throws ProcessNotFoundException {

        String attachmentFileName = null;

        if (attachmentName != null && attachmentName.length() > 0) {
            if (attachmentName.matches("\\$\\{.*\\}")) {
                attachmentName = attachmentName.substring(2, attachmentName.length() - 1);
            }
            final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
            final AttachmentDefinition attachment = queryDefinitionAPI.getAttachmentDefinition(processDefinitionUUID, attachmentName);
            if (attachment != null) {
                attachmentFileName = attachment.getFileName();
            }
        }
        return attachmentFileName;
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public Map<String, String> getAttributes(final ActivityInstanceUUID activityInstanceUUID, final Locale locale) throws ActivityNotFoundException {

        final Map<String, String> attributes = new HashMap<String, String>();

        ResourceBundle labels = ResourceBundle.getBundle("locale.i18n.ActivityAttributeLabels", locale);
        if (locale.getLanguage() != null && labels.getLocale() != null 
                && !locale.getLanguage().equals(labels.getLocale().getLanguage())) {
            labels = ResourceBundle.getBundle("locale.i18n.ActivityAttributeLabels", Locale.ENGLISH);
        }

        SimpleDateFormat dateFormat = null;

        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final LightActivityInstance lightActivityInstance = queryRuntimeAPI.getLightActivityInstance(activityInstanceUUID);
        if (lightActivityInstance.isTask()) {
            final LightTaskInstance lightTaskInstance = lightActivityInstance.getTask();
            final StringBuilder candidates = new StringBuilder();
            if (lightTaskInstance.isTaskAssigned()) {
                candidates.append(lightTaskInstance.getTaskUser());
                attributes.put(ActivityAttribute.assignee.name(), lightTaskInstance.getTaskUser());
            } else {
                try {
                    final Set<String> taskCandidates = AccessorUtil.getQueryRuntimeAPI().getTaskCandidates(lightTaskInstance.getUUID());
                    for (final String candidate : taskCandidates) {
                        if (candidates.length() > 0) {
                            candidates.append(", ");
                        }
                        candidates.append(candidate);
                    }
                } catch (final TaskNotFoundException e) {
                    throw new ActivityNotFoundException("bai_QRAPII_11", lightTaskInstance.getUUID());
                }
            }
            attributes.put(ActivityAttribute.candidates.name(), candidates.toString());
            if (lightTaskInstance.getCreatedDate() != null) {
                dateFormat = getInitializedDateFormat(dateFormat, locale);
                attributes.put(ActivityAttribute.createdDate.name(), dateFormat.format(lightTaskInstance.getCreatedDate()));
            }
            attributes.put(ActivityAttribute.executedBy.name(), lightTaskInstance.getEndedBy());
        }
        final String dynamicLabel = lightActivityInstance.getDynamicLabel();
        if (dynamicLabel != null && dynamicLabel.length() > 0) {
            attributes.put(ActivityAttribute.label.name(), dynamicLabel);
        } else {
            attributes.put(ActivityAttribute.label.name(), lightActivityInstance.getActivityLabel());
        }
        final String dynamicDescription = lightActivityInstance.getDynamicDescription();
        if (dynamicDescription != null && dynamicDescription.length() > 0) {
            attributes.put(ActivityAttribute.description.name(), dynamicDescription);
        } else {
            attributes.put(ActivityAttribute.description.name(), lightActivityInstance.getActivityDescription());
        }
        attributes.put(ActivityAttribute.type.name(), lightActivityInstance.getType().name());
        attributes.put(ActivityAttribute.state.name(), lightActivityInstance.getState().name().toLowerCase());

        String priorityEngineValue = Misc.getActivityPriority(lightActivityInstance.getPriority(), Locale.ENGLISH);
        String priorityDisplayValue = labels.getString("label.priority." + priorityEngineValue.toLowerCase());
        attributes.put(ActivityAttribute.priority.name(), priorityDisplayValue);

        final Date readyDate = lightActivityInstance.getReadyDate();
        if (readyDate != null) {
            dateFormat = getInitializedDateFormat(dateFormat, locale);
            attributes.put(ActivityAttribute.readyDate.name(), dateFormat.format(readyDate));
        }
        if (lightActivityInstance.getStartedDate() != null) {
            dateFormat = getInitializedDateFormat(dateFormat, locale);
            attributes.put(ActivityAttribute.startedDate.name(), dateFormat.format(lightActivityInstance.getStartedDate()));
        }
        if (lightActivityInstance.getEndedDate() != null) {
            dateFormat = getInitializedDateFormat(dateFormat, locale);
            attributes.put(ActivityAttribute.endedDate.name(), dateFormat.format(lightActivityInstance.getEndedDate()));
        }
        final Date expectedEndDate = lightActivityInstance.getExpectedEndDate();
        if (expectedEndDate != null) {
            attributes.put(ActivityAttribute.expectedEndDate.name(), dateFormat.format(expectedEndDate));
            final long remainingTime = expectedEndDate.getTime() - new Date().getTime();
            final StringBuilder remainingTimeStr = new StringBuilder();
            if (remainingTime > 0) {
                final Long duration = remainingTime / 1000;
                final int days = duration.intValue() / SECONDS_IN_A_DAY;
                if (days >= 1) {
                    remainingTimeStr.append(days);
                    if (days > 1) {
                        remainingTimeStr.append(labels.getString("label.days"));
                    } else {
                        remainingTimeStr.append(labels.getString("label.day"));
                        remainingTimeStr.append(" ");
                    }
                }
                if (days <= 1) {
                    int remaining = duration.intValue() % SECONDS_IN_A_DAY;
                    final int hours = remaining / SECONDS_IN_AN_HOUR;
                    if (hours > 0) {
                        remainingTimeStr.append(hours);
                        if (hours > 1) {
                            remainingTimeStr.append(labels.getString("label.hours"));
                        } else {
                            remainingTimeStr.append(labels.getString("label.hour"));
                            remainingTimeStr.append(" ");
                        }
                    }
                    if (hours <= 1) {
                        remaining = remaining % SECONDS_IN_AN_HOUR;
                        final int minutes = remaining / SECONDS_IN_A_MINUTE;
                        if (minutes > 0) {
                            remainingTimeStr.append(minutes);
                            remainingTimeStr.append(labels.getString("label.minutes"));
                        }
                    }
                }
                attributes.put(ActivityAttribute.remainingTime.name(), remainingTimeStr.toString());
            } else {
                attributes.put(ActivityAttribute.remainingTime.name(), labels.getString("label.overdue"));
            }
        }
        if (lightActivityInstance.getLastUpdateDate() != null) {
            dateFormat = getInitializedDateFormat(dateFormat, locale);
            attributes.put(ActivityAttribute.lastUpdate.name(), dateFormat.format(lightActivityInstance.getLastUpdateDate()));
        }
        return attributes;
    }

    protected SimpleDateFormat getInitializedDateFormat(SimpleDateFormat dateFormat, final Locale locale) {
        if (dateFormat == null) {
            ResourceBundle labels = ResourceBundle.getBundle("locale.i18n.ActivityAttributeLabels", locale);
            dateFormat = new SimpleDateFormat(labels.getString("label.dateShortFormat"));
        }
        return dateFormat;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isTask(final ActivityInstanceUUID activityInstanceUUID) throws ActivityNotFoundException {

        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final LightActivityInstance activityInstance = queryRuntimeAPI.getLightActivityInstance(activityInstanceUUID);
        return activityInstance.isTask();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public ProcessInstanceUUID startInstance(final ProcessDefinitionUUID processDefinitionUUID) throws ProcessNotFoundException {
        
        final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        return runtimeAPI.instantiateProcess(processDefinitionUUID);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void terminate(final ActivityInstanceUUID activityInstanceUUID) throws TaskNotFoundException, IllegalTaskStateException, ActivityNotFoundException, IllegalActivityTypeException {
        if (isTask(activityInstanceUUID)) {
            final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
            runtimeAPI.executeTask(activityInstanceUUID, true);
        } else {
            throw new IllegalActivityTypeException("The activity " + activityInstanceUUID.getValue() + " is not a task!");
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Set<String> getProcessInvolvedUsers(ProcessInstanceUUID processInstanceUUID) throws InstanceNotFoundException {
        final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        final LightProcessInstance processInstance = queryRuntimeAPI.getLightProcessInstance(processInstanceUUID);
        final Set<String> involvedUsers = new HashSet<String>();
        ProcessInstanceUUID rootProcessInstanceUUID = processInstance.getRootInstanceUUID();
        if (processInstanceUUID.equals(rootProcessInstanceUUID)) {
            involvedUsers.add(processInstance.getStartedBy());
            involvedUsers.addAll(queryRuntimeAPI.getInvolvedUsersOfProcessInstance(processInstanceUUID));
        } else {
            final LightProcessInstance rootProcessInstance = queryRuntimeAPI.getLightProcessInstance(rootProcessInstanceUUID);
            involvedUsers.add(rootProcessInstance.getStartedBy());
            involvedUsers.addAll(queryRuntimeAPI.getInvolvedUsersOfProcessInstance(rootProcessInstanceUUID));
        }
        return involvedUsers;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> transientDataContext)
            throws ActivityNotFoundException, InstanceNotFoundException {
        
        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateExpressions(activityInstanceUUID, expressions, fieldValues, locale, isCurrentValue, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue) throws ActivityNotFoundException, InstanceNotFoundException {
        
        return getFieldsValues(activityInstanceUUID, expressions, fieldValues, locale, isCurrentValue, new HashMap<String, Object>());
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue, Map<String, Object> transientDataContext) throws ActivityNotFoundException, InstanceNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateInitialExpressions(activityInstanceUUID, expressions, locale, isCurrentValue, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue) throws ActivityNotFoundException, InstanceNotFoundException {
        
        return getFieldsValues(activityInstanceUUID, expressions, locale, isCurrentValue, new HashMap<String, Object>());
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> transientDataContext)
            throws InstanceNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateExpressions(processInstanceUUID, expressions, fieldValues, locale, isCurrentValue, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException {
        
        return getFieldsValues(processInstanceUUID, expressions, fieldValues, locale, isCurrentValue, new HashMap<String, Object>());
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue, Map<String, Object> transientDataContext) throws InstanceNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateInitialExpressions(processInstanceUUID, expressions, locale, isCurrentValue, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException {
        
        return getFieldsValues(processInstanceUUID, expressions, locale, isCurrentValue, new HashMap<String, Object>());
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, Map<String, Object> transientDataContext) throws ProcessNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateExpressions(processDefinitionUUID, expressions, fieldValues, locale, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale) throws ProcessNotFoundException {
        
        return getFieldsValues(processDefinitionUUID, expressions, fieldValues, locale, new HashMap<String, Object>());
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Locale locale, Map<String, Object> transientDataContext) throws ProcessNotFoundException {

        final IFormExpressionsAPI formExpressionsAPI = FormAPIFactory.getFormExpressionsAPI();
        return formExpressionsAPI.evaluateInitialExpressions(processDefinitionUUID, expressions, locale, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFieldsValues(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Locale locale) throws ProcessNotFoundException {
        
        return getFieldsValues(processDefinitionUUID, expressions, locale, new HashMap<String, Object>());
    }

}