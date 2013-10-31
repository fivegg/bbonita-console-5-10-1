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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimetypesFileTypeMap;

import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.api.IFormExpressionsAPI;
import org.bonitasoft.forms.server.exception.FileTooBigException;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ActivityDefNotFoundException;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.DataFieldNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.InitialAttachment;
import org.ow2.bonita.facade.runtime.impl.InitialAttachmentImpl;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.GroovyException;
import org.ow2.bonita.util.Misc;

/**
 * Implementation of {@link IFormExpressionsAPI} allowing groovy
 * expressions evaluation and execution
 * 
 * @author Anthony Birembaut
 * 
 */
public class FormExpressionsAPIImpl implements IFormExpressionsAPI {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(FormExpressionsAPIImpl.class.getName());

    /**
     * Field id regex
     */
    protected static final String FIELD_REGEX = FIELDID_PREFIX + ".*";
    
    /**
     * Max file size for attachments
     */
    protected static final long MAX_FILE_SIZE = DefaultFormsPropertiesFactory.getDefaultFormProperties().getAttachmentMaxSize();

    /**
     * {@inheritDoc}
     */
    public Object evaluateInitialExpression(final ActivityInstanceUUID activityInstanceUUID, final String expression, final Locale locale, final boolean isCurrentValue) throws InstanceNotFoundException, ActivityNotFoundException {
        return evaluateInitialExpression(activityInstanceUUID, expression, locale, isCurrentValue, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object evaluateInitialExpression(final ActivityInstanceUUID activityInstanceUUID, final String expression, final Locale locale, final boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException {

        Object result = null;

        final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        try {
            if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                final Map<String, Object> expressionContext = new HashMap<String, Object>();
                if (context != null) {
                    expressionContext.putAll(context);
                }
                expressionContext.put(BonitaConstants.USER_LOCALE, locale);
                result = runtimeAPI.evaluateGroovyExpression(expression, activityInstanceUUID, expressionContext, !isCurrentValue, false);
            } else {
                result = expression;
            }
        } catch (final GroovyException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expression.", e);
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object evaluateInitialExpression(final ProcessInstanceUUID processInstanceUUID, final String expression, final Locale locale, final boolean isCurrentValue) throws InstanceNotFoundException {
        return evaluateInitialExpression(processInstanceUUID, expression, locale, isCurrentValue, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object evaluateInitialExpression(final ProcessInstanceUUID processInstanceUUID, final String expression, final Locale locale, final boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException {

        Object result = null;

        final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        try {
            if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                final Map<String, Object> expressionContext = new HashMap<String, Object>();
                if (context != null) {
                    expressionContext.putAll(context);
                }
                expressionContext.put(BonitaConstants.USER_LOCALE, locale);
                result = runtimeAPI.evaluateGroovyExpression(expression, processInstanceUUID, expressionContext, !isCurrentValue, false); 
            } else {
                result = expression;
            }
        } catch (final GroovyException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expression.", e);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluateInitialExpression(final ProcessDefinitionUUID processDefinitionUUID, final String expression, final Locale locale) throws ProcessNotFoundException {
        return evaluateInitialExpression(processDefinitionUUID, expression, locale, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object evaluateInitialExpression(final ProcessDefinitionUUID processDefinitionUUID, final String expression, final Locale locale, Map<String, Object> context) throws ProcessNotFoundException {

        Object result = null;

        final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        try {
            if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                final Map<String, Object> expressionContext = new HashMap<String, Object>();
                if (context != null) {
                    expressionContext.putAll(context);
                }
                expressionContext.put(BonitaConstants.USER_LOCALE, locale);
                result = runtimeAPI.evaluateGroovyExpression(expression, processDefinitionUUID, expressionContext);
            } else {
                result = expression;
            }
        } catch (final GroovyException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expression.", e);
            }
        }
        return result;
    }
    
    /**
     * Evaluate a field value
     * @param fieldId the field ID
     * @param fieldValues the form field values
     * @return the value of the field as an Object
     */
    protected Object evaluateFieldValueActionExpression(final String fieldId, final Map<String, FormFieldValue> fieldValues, final Locale locale) {
        
        Object result = null;

        final FormFieldValue fieldValue = fieldValues.get(fieldId);
        if (fieldValue != null) {
            result = fieldValue.getValue();
        }
        return result;
    }
    
    /**
     *{@inheritDoc}
     */
    public Map<String, Object> generateGroovyContext(final Map<String, FormFieldValue> fieldValues, final Locale locale) {
        final Map<String, Object> context = new HashMap<String, Object>();
        for (final Entry<String, FormFieldValue> fieldValuesEntry : fieldValues.entrySet()) {
            final String fieldId = fieldValuesEntry.getKey();
            final Object fieldValue = evaluateFieldValueActionExpression(fieldId, fieldValues, locale);
            context.put(FIELDID_PREFIX + fieldId, fieldValue);
        }
        context.put(BonitaConstants.USER_LOCALE, locale);
        return context;
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluateExpression(final ActivityInstanceUUID activityInstanceUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final boolean isCurrentValue) throws InstanceNotFoundException,
            ActivityNotFoundException {
        return evaluateExpression(activityInstanceUUID, expression, fieldValues, locale, isCurrentValue, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object evaluateExpression(final ActivityInstanceUUID activityInstanceUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final boolean isCurrentValue, final Map<String, Object> context) throws InstanceNotFoundException,
            ActivityNotFoundException {

        Object result = null;

        if (expression != null) {
            // if the whole expression is a field value
            if (expression.matches(FIELD_REGEX)) {
               final String fieldId = expression.substring(FIELDID_PREFIX.length());
               result = evaluateFieldValueActionExpression(fieldId, fieldValues, locale);
            } else if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                // generate the context for groovy
                final Map<String, Object> evalContext = generateGroovyContext(fieldValues, locale);
                if (context != null) {
                    evalContext.putAll(context);
                }
                final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
                try {
                    result = runtimeAPI.evaluateGroovyExpression(expression, activityInstanceUUID, evalContext, !isCurrentValue, false);
                } catch (final GroovyException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expression.", e);
                    }
                }
            } else {
                result = expression;
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object evaluateExpression(final ProcessInstanceUUID processInstanceUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final boolean isCurrentValue) throws InstanceNotFoundException {
        return evaluateExpression(processInstanceUUID, expression, fieldValues, locale, isCurrentValue, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object evaluateExpression(final ProcessInstanceUUID processInstanceUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final boolean isCurrentValue, final Map<String, Object> context) throws InstanceNotFoundException {

        Object result = null;

        if (expression != null) {
            // if the whole expression is a field value
            if (expression.matches(FIELD_REGEX)) {
               final String fieldId = expression.substring(FIELDID_PREFIX.length());
               result = evaluateFieldValueActionExpression(fieldId, fieldValues, locale);
            } else if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                // generate the context for groovy
                final Map<String, Object> evalContext = generateGroovyContext(fieldValues, locale);
                if (context != null) {
                    evalContext.putAll(context);
                }
                final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
                try {
                    result = runtimeAPI.evaluateGroovyExpression(expression, processInstanceUUID, evalContext, !isCurrentValue, false);
                } catch (final GroovyException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expression.", e);
                    }
                }
            } else {
                result = expression;
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object evaluateExpression(final ProcessDefinitionUUID processDefinitionUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale) throws ProcessNotFoundException {
        return evaluateExpression(processDefinitionUUID, expression, fieldValues, locale, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object evaluateExpression(final ProcessDefinitionUUID processDefinitionUUID, final String expression, final Map<String, FormFieldValue> fieldValues, final Locale locale, final Map<String, Object> context) throws ProcessNotFoundException {

        Object result = null;

        if (expression != null) {
            // if the whole expression is a field value
            if (expression.matches(FIELD_REGEX)) {
               final String fieldId = expression.substring(FIELDID_PREFIX.length());
               result = evaluateFieldValueActionExpression(fieldId, fieldValues, locale);
            } else  if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                // generate the context for groovy
                final Map<String, Object> evalContext = generateGroovyContext(fieldValues, locale);
                if (context != null) {
                    evalContext.putAll(context);
                }
                final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
                try {
                    result = runtimeAPI.evaluateGroovyExpression(expression, processDefinitionUUID, evalContext);
                } catch (final GroovyException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expression.", e);
                    }
                }
            } else {
                result = expression;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Object getObjectValue(final Object value, final String dataTypeClassName) {
        Object objectValue = null;
        if (value != null) {
            if (!String.class.getName().equals(dataTypeClassName) && value instanceof String && value.toString().length() == 0) {
                objectValue = null;
            } else if (Boolean.class.getName().equals(dataTypeClassName)) {
                objectValue = Boolean.parseBoolean(value.toString());
            } else if (Integer.class.getName().equals(dataTypeClassName)) {
                objectValue = Integer.parseInt(value.toString());
            } else if (Long.class.getName().equals(dataTypeClassName)) {
                objectValue = Long.parseLong(value.toString());
            } else if (Float.class.getName().equals(dataTypeClassName)) {
                objectValue = Float.parseFloat(value.toString());
            } else if (Double.class.getName().equals(dataTypeClassName)) {
                objectValue = Double.parseDouble(value.toString());
            } else if (Short.class.getName().equals(dataTypeClassName)) {
                objectValue = Short.parseShort(value.toString());
            } else if (Character.class.getName().equals(dataTypeClassName)) {
                objectValue = value.toString().charAt(0);
            } else {
                objectValue = value;
            }
        }
        return objectValue;
    }
    
    /**
     * {@inheritDoc}
     */
    public void executeAction(final ActivityInstanceUUID activityInstanceUUID, final FormAction action, final Map<String, FormFieldValue> fieldValues, final Locale locale) throws InstanceNotFoundException, ActivityNotFoundException, ActivityDefNotFoundException, ProcessNotFoundException, VariableNotFoundException, FileTooBigException {

        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();

        final String expression = action.getExpression();
        
        if (action.getType().equals(ActionType.SET_VARIABLE)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Perform set variable action");
            }
            final Object variableIdObject = evaluateInitialExpression(activityInstanceUUID, action.getVariableId(), locale, true);
            if (variableIdObject instanceof String) {
                final Object value = evaluateExpression(activityInstanceUUID, expression, fieldValues, locale, true);
                final String variableId = (String)variableIdObject;
                final ActivityDefinitionUUID activityDefinitionUUID = activityInstanceUUID.getActivityDefinitionUUID();
                try {
                    DataFieldDefinition dataField = null;
                    if (!FormServiceProviderUtil.PROCESS_VARIABLE_TYPE.equals(action.getVariableType())) {
                        try {
                            dataField = queryDefinitionAPI.getActivityDataField(activityDefinitionUUID, variableId);
                            final Object objectValue = getObjectValue(value, dataField.getDataTypeClassName());
                            runtimeAPI.setActivityInstanceVariable(activityInstanceUUID, variableId, objectValue);
                        } catch (final DataFieldNotFoundException e) {
                            if (FormServiceProviderUtil.ACTIVITY_VARIABLE_TYPE.equals(action.getVariableType())) {
                                throw new DataFieldNotFoundException(e);
                            }
                        }
                    }
                    if (dataField == null) {
                        dataField = queryDefinitionAPI.getProcessDataField(activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID(), variableId);
                        final Object objectValue = getObjectValue(value, dataField.getDataTypeClassName());
                        runtimeAPI.setProcessInstanceVariable(activityInstanceUUID.getProcessInstanceUUID(), variableId, objectValue);
                    }
                } catch (final DataFieldNotFoundException e) {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Data " + variableId + " with type " + action.getVariableType() + " not found. Setting the variable as a String.", e);
                    }
                    final Object objectValue = getObjectValue(value, String.class.getName());
                    runtimeAPI.setVariable(activityInstanceUUID, variableId, objectValue);
                }
            } else {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "The variable to set should be either a String or a groovy expression returning a String which is not the case of value : " + action.getVariableId());
                }
            }
        } else if (action.getType().equals(ActionType.SET_ATTACHMENT)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Perform set attachment action");
            }
            performSetAttachmentAction(activityInstanceUUID.getProcessInstanceUUID(), new HashSet<InitialAttachment>(), action, fieldValues, locale, true);
        } else if (action.getType().equals(ActionType.EXECUTE_SCRIPT)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Perform execute script action");
            }
            try {
                final Map<String, Object> context = generateGroovyContext(fieldValues, locale);
                runtimeAPI.evaluateGroovyExpression(expression, activityInstanceUUID, context, false, true);
            } catch (final GroovyException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while executing action. unable to evaluate the groovy expression", e);
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void executeAction(final ProcessDefinitionUUID processDefinitionUUID, final FormAction action, final Map<String, Object> variableValues, final Set<InitialAttachment> attachments, final Map<String, FormFieldValue> fieldValues, final Locale locale) throws ProcessNotFoundException, FileTooBigException {

        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        
        final String expression = action.getExpression();
        
        if (action.getType().equals(ActionType.SET_VARIABLE)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Perform set variable action");
            }
            final Object variableIdObject = evaluateInitialExpression(processDefinitionUUID, action.getVariableId(), locale);
            if (variableIdObject instanceof String) {
                final Object value = evaluateExpression(processDefinitionUUID, expression, fieldValues, locale);
                final String variableId = (String)variableIdObject;
                Object objectValue = null;
                try {
                    final DataFieldDefinition dataField = queryDefinitionAPI.getProcessDataField(processDefinitionUUID, variableId);
                    objectValue = getObjectValue(value, dataField.getDataTypeClassName());
                } catch (final DataFieldNotFoundException e) {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Process data field " + variableId + " not found. Setting the variable as a String.", e);
                    }
                    objectValue = getObjectValue(value, String.class.getName());
                }
                variableValues.put(variableId, objectValue);
            } else {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "The variable to set should be either a String or a groovy expression returning a String which is not the case of value : " + action.getVariableId());
                }
            }
        } else if (action.getType().equals(ActionType.SET_ATTACHMENT)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Perform set attachment action");
            }
            performSetAttachmentAction(null, attachments, action, fieldValues, locale, true);
        } else if (action.getType().equals(ActionType.EXECUTE_SCRIPT)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Perform execute script action");
            }
            try {
                final Map<String, Object> context = generateGroovyContext(fieldValues, locale);
                runtimeAPI.evaluateGroovyExpression(expression, processDefinitionUUID, context);
            } catch (final GroovyException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while executing action. unable to evaluate the groovy expression", e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void executeAction(final ProcessInstanceUUID processInstanceUUID, final FormAction action, final Map<String, FormFieldValue> fieldValues, final Locale locale) throws InstanceNotFoundException,
            ProcessNotFoundException, VariableNotFoundException, FileTooBigException {

        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();

        final String expression = action.getExpression();
        
        if (action.getType().equals(ActionType.SET_VARIABLE)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Perform set variable action");
            }
            final Object variableIdObject = evaluateInitialExpression(processInstanceUUID, action.getVariableId(), locale, true);
            if (variableIdObject instanceof String) {
                final Object value = evaluateExpression(processInstanceUUID, expression, fieldValues, locale, true);
                final String variableId = (String)variableIdObject;
                Object objectValue = null;
                try {
                    final DataFieldDefinition dataField = queryDefinitionAPI.getProcessDataField(processInstanceUUID.getProcessDefinitionUUID(), variableId);
                    objectValue = getObjectValue(value, dataField.getDataTypeClassName());
                } catch (final DataFieldNotFoundException e) {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Data " + variableId + " with type " + action.getVariableType() + " not found. Setting the variable as a String.", e);
                    }
                    objectValue = getObjectValue(value, String.class.getName());
                }
                runtimeAPI.setProcessInstanceVariable(processInstanceUUID, variableId, objectValue);
            } else {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "The variable to set should be either a String or a groovy expression returning a String which is not the case of value : " + action.getVariableId());
                }
            }
        } else if (action.getType().equals(ActionType.SET_ATTACHMENT)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Perform set attachment action");
            }
            performSetAttachmentAction(processInstanceUUID, new HashSet<InitialAttachment>(), action, fieldValues, locale, true);
        } else if (action.getType().equals(ActionType.EXECUTE_SCRIPT)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Perform execute script action");
            }
            try {
                final Map<String, Object> context = generateGroovyContext(fieldValues, locale);
                runtimeAPI.evaluateGroovyExpression(expression, processInstanceUUID, context, true);
            } catch (final GroovyException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while executing action. unable to evaluate the groovy expression", e);
                }
            }
        }
    }
    
    /**
     *{@inheritDoc}
     */
    public void performSetAttachmentAction(final ProcessInstanceUUID processInstanceUUID, final Set<InitialAttachment> attachments, final FormAction action, final Map<String, FormFieldValue> fieldValues, final Locale locale, final boolean setAttachment) throws FileTooBigException {
        try {
            final String fieldId = action.getExpression().substring(FIELDID_PREFIX.length());
            final FormFieldValue fieldValue = fieldValues.get(fieldId);
            if (fieldValue != null) {
                final Object attachmentNameObject = evaluateInitialExpression(processInstanceUUID, action.getAttachmentName(), locale, true);
                if (attachmentNameObject instanceof String) {
                    String attachmentName = (String)attachmentNameObject;
                    final String fileName = (String)fieldValue.getValue();
                    if (attachmentName.equals(fileName)) {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE, "The value of attachment " + attachmentName + " has not been changed.");
                        }
                    } else {
                        attachmentName = (String)attachmentNameObject;
                        performSetAttachmentAction(processInstanceUUID, attachments, attachmentName, fileName, setAttachment);
                    }
                } else {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "The attachment to set should be either a String or a groovy expression returning a String which is not the case of value : " + action.getVariableId());
                    }
                }
            } else {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while setting the attachment. Unable to find a field with ID " + fieldId + " in the form.");
                }
            }
        } catch (final IndexOutOfBoundsException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Invalid action expression : " + action.getExpression());
            }
        } catch (final InstanceNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Process instance " + processInstanceUUID + " not found.");
            }
        }
    }
    
    /**
     *{@inheritDoc}
     */
    public void performSetAttachmentAction(final ProcessInstanceUUID processInstanceUUID, final Set<InitialAttachment> attachments, final String attachmentName, final String fileName, final boolean setAttachment) throws FileTooBigException {
        
        final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        if (fileName != null && fileName.length() != 0) {
            final File file = new File(fileName);
            if (file.exists()) {
                int fileLength = 0;
                if (file.length() > (MAX_FILE_SIZE * 1048576)) {
                    final String errorMessage = "file " + fileName + " too big !";
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, errorMessage);
                    }
                    throw new FileTooBigException(errorMessage, fileName, String.valueOf(MAX_FILE_SIZE));
                } else {
                    fileLength = (int)file.length();
                    try {
                        final InputStream fileInput = new FileInputStream(file);
                        final byte [] fileContent = new byte[fileLength];
                        try {
                            int offset = 0;
                            int length = fileLength;
                            while (length > 0) {
                                final int read = fileInput.read(fileContent, offset, length);
                                if (read <= 0) {
                                    break;
                                }
                                length -= read;
                                offset += read;
                            }
                            //removing filename suffix
                            String originalFileName = file.getName();
                            while (originalFileName.matches(".+\\.\\d*")) {
                                originalFileName = originalFileName.substring(0, originalFileName.length() - 1);
                            }
                            final MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
                            final String contentType = mimetypesFileTypeMap.getContentType(file);
                            final Map<String, String> metadata = new HashMap<String, String>();
                            metadata.put("content-type", contentType);
                            if (processInstanceUUID != null && setAttachment) {
                                runtimeAPI.createDocumentOrAddDocumentVersion(processInstanceUUID, attachmentName, originalFileName, contentType, fileContent);
                            } else {
                                final InitialAttachmentImpl initialAttachment = new InitialAttachmentImpl(attachmentName, fileContent);
                                initialAttachment.setFileName(originalFileName);
                                initialAttachment.setMetaData(metadata);
                                attachments.add(initialAttachment);
                            }
                        } catch (final FileNotFoundException e) {
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.log(Level.SEVERE, "Error while saving attachment. The file " + fileName + " cannot be openned.", e);
                            }
                        } finally {
                            fileInput.close();
                        }
                    } catch (final IOException e) {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, "Error while reading attachment (file  : " + fileName , e);
                        }
                    }
                }
            } else {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "The file " + fileName + " does not exist. skipping the attachment creation/update.");
                }
            }
        } else {
            if (processInstanceUUID != null && setAttachment) {
                runtimeAPI.createDocumentOrAddDocumentVersion(processInstanceUUID, attachmentName, null, null, null);
            } else {
                final InitialAttachmentImpl initialAttachment = new InitialAttachmentImpl(attachmentName, null);
                attachments.add(initialAttachment);
            }
        }
    }
    
    /**
     *{@inheritDoc}
     */
    public Object getModifiedJavaObject(final ProcessDefinitionUUID processDefinitionUUID, final String variableExpression, final Object objectValue, final Object attributeValue) {
        
        final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        return runtimeAPI.getModifiedJavaObject(processDefinitionUUID, variableExpression, objectValue, attributeValue);
    }

    /**
     *{@inheritDoc}
     */
    public Map<String, Object> evaluateInitialExpressions(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException {

        final Map<String, Object> result = new HashMap<String, Object>();
        final Map<String, String> expressionsToEvaluate = new HashMap<String, String>();
        for (Entry<String, String> expressionEntry : expressions.entrySet()) {
            String expression = expressionEntry.getValue();
            if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                expressionsToEvaluate.put(expressionEntry.getKey(), expression);
            } else {
                result.put(expressionEntry.getKey(), expression);
            }
        }
        if (!expressionsToEvaluate.isEmpty()) {
            final Map<String, Object> expressionContext = new HashMap<String, Object>();
            if (context != null) {
                expressionContext.putAll(context);
            }
            final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
            try {
                result.putAll(runtimeAPI.evaluateGroovyExpressions(expressionsToEvaluate, activityInstanceUUID, expressionContext, !isCurrentValue, false));
            } catch (final GroovyException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expressions.", e);
                }
                for (Entry<String, String> expressionsToEvaluateEntry : expressionsToEvaluate.entrySet()) {
                    result.put(expressionsToEvaluateEntry.getKey(), null);
                }  
            }
        }
        return result;
    }

    /**
     *{@inheritDoc}
     */
    public Map<String, Object> evaluateExpressions(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException {

        final Map<String, Object> result = new HashMap<String, Object>();
        final Map<String, String> expressionsToEvaluate = new HashMap<String, String>();
        for (Entry<String, String> expressionEntry : expressions.entrySet()) {
            String expression = expressionEntry.getValue();
            if (expression != null && expression.matches(FIELD_REGEX)) {
                final String fieldId = expression.substring(FIELDID_PREFIX.length());
                result.put(expressionEntry.getKey(), evaluateFieldValueActionExpression(fieldId, fieldValues, locale));
             } else if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                expressionsToEvaluate.put(expressionEntry.getKey(), expression);
            } else {
                result.put(expressionEntry.getKey(), expression);
            }
        }
        if (!expressionsToEvaluate.isEmpty()) {
            final Map<String, Object> expressionContext = generateGroovyContext(fieldValues, locale);
            if (context != null) {
                expressionContext.putAll(context);
            }
            final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
            try {
                result.putAll(runtimeAPI.evaluateGroovyExpressions(expressionsToEvaluate, activityInstanceUUID, expressionContext, !isCurrentValue, false));
            } catch (final GroovyException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expressions.", e);
                }
                for (Entry<String, String> expressionsToEvaluateEntry : expressionsToEvaluate.entrySet()) {
                    result.put(expressionsToEvaluateEntry.getKey(), null);
                }  
            }
        }
        return result;
    }

    /**
     *{@inheritDoc}
     */
    public Map<String, Object> evaluateInitialExpressions(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException {
        
        final Map<String, Object> result = new HashMap<String, Object>();
        final Map<String, String> expressionsToEvaluate = new HashMap<String, String>();
        for (Entry<String, String> expressionEntry : expressions.entrySet()) {
            String expression = expressionEntry.getValue();
            if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                expressionsToEvaluate.put(expressionEntry.getKey(), expression);
            } else {
                result.put(expressionEntry.getKey(), expression);
            }
        }
        if (!expressionsToEvaluate.isEmpty()) {
            final Map<String, Object> expressionContext = new HashMap<String, Object>();
            if (context != null) {
                expressionContext.putAll(context);
            }
            final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
            try {
                result.putAll(runtimeAPI.evaluateGroovyExpressions(expressionsToEvaluate, processInstanceUUID, expressionContext, !isCurrentValue, false));
            } catch (final GroovyException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expressions.", e);
                }
                for (Entry<String, String> expressionsToEvaluateEntry : expressionsToEvaluate.entrySet()) {
                    result.put(expressionsToEvaluateEntry.getKey(), null);
                }  
            }
        }
        return result;
    }

    /**
     *{@inheritDoc}
     */
    public Map<String, Object> evaluateExpressions(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException {

        final Map<String, Object> result = new HashMap<String, Object>();
        final Map<String, String> expressionsToEvaluate = new HashMap<String, String>();
        for (Entry<String, String> expressionEntry : expressions.entrySet()) {
            String expression = expressionEntry.getValue();
            if (expression != null && expression.matches(FIELD_REGEX)) {
                final String fieldId = expression.substring(FIELDID_PREFIX.length());
                result.put(expressionEntry.getKey(), evaluateFieldValueActionExpression(fieldId, fieldValues, locale));
             } else if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                expressionsToEvaluate.put(expressionEntry.getKey(), expression);
            } else {
                result.put(expressionEntry.getKey(), expression);
            }
        }
        if (!expressionsToEvaluate.isEmpty()) {
            final Map<String, Object> expressionContext = generateGroovyContext(fieldValues, locale);
            if (context != null) {
                expressionContext.putAll(context);
            }
            final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
            try {
                result.putAll(runtimeAPI.evaluateGroovyExpressions(expressionsToEvaluate, processInstanceUUID, expressionContext, !isCurrentValue, false));
            } catch (final GroovyException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expressions.", e);
                }
                for (Entry<String, String> expressionsToEvaluateEntry : expressionsToEvaluate.entrySet()) {
                    result.put(expressionsToEvaluateEntry.getKey(), null);
                }  
            }
        }
        return result;
    }

    /**
     *{@inheritDoc}
     */
    public Map<String, Object> evaluateInitialExpressions(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Locale locale, Map<String, Object> context) throws ProcessNotFoundException {

        final Map<String, Object> result = new HashMap<String, Object>();
        final Map<String, String> expressionsToEvaluate = new HashMap<String, String>();
        for (Entry<String, String> expressionEntry : expressions.entrySet()) {
            String expression = expressionEntry.getValue();
            if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                expressionsToEvaluate.put(expressionEntry.getKey(), expression);
            } else {
                result.put(expressionEntry.getKey(), expression);
            }
        }
        if (!expressionsToEvaluate.isEmpty()) {
            final Map<String, Object> expressionContext = new HashMap<String, Object>();
            if (context != null) {
                expressionContext.putAll(context);
            }
            final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
            try {
                result.putAll(runtimeAPI.evaluateGroovyExpressions(expressionsToEvaluate, processDefinitionUUID, expressionContext));
            } catch (final GroovyException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expressions.", e);
                }
                for (Entry<String, String> expressionsToEvaluateEntry : expressionsToEvaluate.entrySet()) {
                    result.put(expressionsToEvaluateEntry.getKey(), null);
                }  
            } catch (InstanceNotFoundException e) {
                //this exception should never occur as we are working with a process definition. Not a process instance.
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     *{@inheritDoc}
     */
    public Map<String, Object> evaluateExpressions(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, Map<String, Object> context) throws ProcessNotFoundException {

        final Map<String, Object> result = new HashMap<String, Object>();
        final Map<String, String> expressionsToEvaluate = new HashMap<String, String>();
        for (Entry<String, String> expressionEntry : expressions.entrySet()) {
            String expression = expressionEntry.getValue();
            if (expression != null && expression.matches(FIELD_REGEX)) {
                final String fieldId = expression.substring(FIELDID_PREFIX.length());
                result.put(expressionEntry.getKey(), evaluateFieldValueActionExpression(fieldId, fieldValues, locale));
             } else if (expression != null && expression.length() != 0 && Misc.containsAGroovyExpression(expression)) {
                expressionsToEvaluate.put(expressionEntry.getKey(), expression);
            } else {
                result.put(expressionEntry.getKey(), expression);
            }
        }
        if (!expressionsToEvaluate.isEmpty()) {
            final Map<String, Object> expressionContext = generateGroovyContext(fieldValues, locale);
            if (context != null) {
                expressionContext.putAll(context);
            }
            final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
            try {
                result.putAll(runtimeAPI.evaluateGroovyExpressions(expressionsToEvaluate, processDefinitionUUID, expressionContext));
            } catch (final GroovyException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while evaluating the groovy expressions.", e);
                }
                for (Entry<String, String> expressionsToEvaluateEntry : expressionsToEvaluate.entrySet()) {
                    result.put(expressionsToEvaluateEntry.getKey(), null);
                }  
            } catch (InstanceNotFoundException e) {
                //this exception should never occur as we are working with a process definition. Not a process instance.
                e.printStackTrace();
            }
        }
        return result;
    }
}