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

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.server.exception.FileTooBigException;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ActivityDefNotFoundException;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.InitialAttachment;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 * API dealing with expression evaluation and execution
 * 
 * @author Anthony Birembaut
 *
 */
public interface IFormExpressionsAPI {
    
    /**
     * prefixes for fields values in the expressions
     */
    String FIELDID_PREFIX = "field_";
    
    /**
     * evaluate an initial value expression (at form construction)
     * @param activityInstanceUUID the activity instance UUID
     * @param expression the expression
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @return The result of the evaluation
     * @throws InstanceNotFoundException
     * @throws ActivityNotFoundException
     */
    Object evaluateInitialExpression(ActivityInstanceUUID activityInstanceUUID, String expression, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException, ActivityNotFoundException;
    
    /**
     * evaluate an initial value expression (at form construction)
     * @param processInstanceUUID the process instance UUID
     * @param expression the expression
     * @param locale the user's locale
     * @return The result of the evaluation
     * @throws InstanceNotFoundException
     */
    Object evaluateInitialExpression(ProcessInstanceUUID processInstanceUUID, String expression, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException;
    
    /**
     * evaluate an initial value expression (at form construction)
     * @param processDefinitionUUID the process definition UUID
     * @param expression the expression
     * @param locale the user's locale
     * @return The result of the evaluation
     * @throws ProcessNotFoundException
     */
    Object evaluateInitialExpression(ProcessDefinitionUUID processDefinitionUUID, String expression, Locale locale) throws ProcessNotFoundException;

    /**
     * evaluate an initial value expression (at form construction)
     * @param activityInstanceUUID the activity instance UUID
     * @param expression the expression
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluation
     * @throws InstanceNotFoundException
     * @throws ActivityNotFoundException
     */
    Object evaluateInitialExpression(ActivityInstanceUUID activityInstanceUUID, String expression, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException;
    
    /**
     * evaluate an initial value expression (at form construction)
     * @param processInstanceUUID the process instance UUID
     * @param expression the expression
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluation
     * @throws InstanceNotFoundException
     */
    Object evaluateInitialExpression(ProcessInstanceUUID processInstanceUUID, String expression, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException;
    
    /**
     * evaluate an initial value expression (at form construction)
     * @param processDefinitionUUID the process definition UUID
     * @param expression the expression
     * @param locale the user's locale
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluation
     * @throws ProcessNotFoundException
     */
    Object evaluateInitialExpression(ProcessDefinitionUUID processDefinitionUUID, String expression, Locale locale, Map<String, Object> context) throws ProcessNotFoundException;
    
    /**
     * Evaluate an expression (at form submission)
     * @param activityInstanceUUID the activity instance UUID
     * @param expression the expression
     * @param fieldValues the form field values
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at step end
     * @return The result of the evaluation
     * @throws InstanceNotFoundException
     * @throws ActivityNotFoundException
     */
    Object evaluateExpression(ActivityInstanceUUID activityInstanceUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException, ActivityNotFoundException;
    
    /**
     * Evaluate an expression (at form submission)
     * @param processInstanceUUID the process instance UUID
     * @param expression the expression
     * @param fieldValues the form field values
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @return The result of the evaluation
     * @throws InstanceNotFoundException
     */
    Object evaluateExpression(ProcessInstanceUUID processInstanceUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue) throws InstanceNotFoundException;
    
    /**
     * Evaluate an action expression (at form submission)
     * @param processDefinitionUUID the process definition UUID
     * @param expression the expression
     * @param fieldValues the form field values
     * @param locale the user's locale
     * @return The result of the evaluation
     * @throws ProcessNotFoundException
     */
    Object evaluateExpression(ProcessDefinitionUUID processDefinitionUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale) throws ProcessNotFoundException;
    
    /**
     * Evaluate an expression (at form submission)
     * @param activityInstanceUUID the activity instance UUID
     * @param expression the expression
     * @param fieldValues the form field values
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluation
     * @throws InstanceNotFoundException
     * @throws ActivityNotFoundException
     */
    Object evaluateExpression(ActivityInstanceUUID activityInstanceUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException;
    
    /**
     * Evaluate an expression (at form submission)
     * @param processInstanceUUID the process instance UUID
     * @param expression the expression
     * @param fieldValues the form field values
     * @param locale the user's locale
     * @param isCurrentValue if true, value returned is the current value for the instance. otherwise, it's the value at instantiation
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluation
     * @throws InstanceNotFoundException
     */
    Object evaluateExpression(ProcessInstanceUUID processInstanceUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException;
    
    /**
     * Evaluate an action expression (at form submission)
     * @param processDefinitionUUID the process definition UUID
     * @param expression the expression
     * @param fieldValues the form field values
     * @param locale the user's locale
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluation
     * @throws ProcessNotFoundException
     */
    Object evaluateExpression(ProcessDefinitionUUID processDefinitionUUID, String expression, Map<String, FormFieldValue> fieldValues, Locale locale, Map<String, Object> context) throws ProcessNotFoundException;
    
    /**
     * Execute an action. This includes the expression evaluation (with injection of the necessary field values) and execution
     * @param activityInstanceUUID the activity instance UUID
     * @param action the {@link FormAction} to execute
     * @param fieldValues a map of the field ids and values
     * @param locale the user's locale as a String
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     * @throws ProcessNotFoundException 
     * @throws ActivityDefNotFoundException
     * @throws VariableNotFoundException
     * @throws FileTooBigException 
     */
    void executeAction(ActivityInstanceUUID activityInstanceUUID, FormAction action, Map<String, FormFieldValue> fieldValues, Locale locale) throws InstanceNotFoundException, ActivityNotFoundException, ActivityDefNotFoundException, ProcessNotFoundException, VariableNotFoundException, FileTooBigException;

    /**
     * Execute an action. This includes the expression evaluation (with injection of the necessary field values) and execution
     * @param processInstanceUUID the process instance UUID
     * @param action the {@link FormAction} to execute
     * @param fieldValues a map of the field ids and values
     * @param locale the user's locale as a String
     * @throws InstanceNotFoundException 
     * @throws ProcessNotFoundException 
     * @throws VariableNotFoundException
     * @throws FileTooBigException 
     */
    void executeAction(ProcessInstanceUUID processInstanceUUID, FormAction action, Map<String, FormFieldValue> fieldValues, Locale locale) throws InstanceNotFoundException, ProcessNotFoundException, VariableNotFoundException, FileTooBigException;

    /**
     * Execute an action. This includes the expression evaluation (with injection of the necessary field values) and execution
     * @param processDefinitionUUID the process definition UUID
     * @param action the {@link FormAction} to execute
     * @param fieldValues a map of the field ids and values
     * @param variableValues the map of variable values to complete
     * @param attachments the map of attachments to complete
     * @param locale the user's locale as a String
     * @throws ProcessNotFoundException
     * @throws FileTooBigException 
     */
    void executeAction(ProcessDefinitionUUID processDefinitionUUID, FormAction action, Map<String, Object> variableValues, Set<InitialAttachment> attachments, Map<String, FormFieldValue> fieldValues, Locale locale) throws ProcessNotFoundException, FileTooBigException;
    
    /**
     * Generate the form fields context for a groovy evaluation
     * @param fieldValues
     * @param locale
     * @return the context
     */
    Map<String, Object> generateGroovyContext(Map<String, FormFieldValue> fieldValues, Locale locale);
    
    /**
     * Get the right object value according to the datafield definition
     * @param value the value as extracted from the {@link FormFieldValue} object
     * @param dataTypeClassName the datafield classname
     * @return The object matching the {@link DataFieldDefinition}
     */
    Object getObjectValue(Object value, String dataTypeClassName);
    
    /**
     * Perform a set attachment action
     * @param processInstanceUUID
     * @param attachments
     * @param action
     * @param fieldValues
     * @param locale
     * @param setAttachment
     * @throws FileTooBigException
     */
    void performSetAttachmentAction(ProcessInstanceUUID processInstanceUUID, Set<InitialAttachment> attachments, FormAction action, Map<String, FormFieldValue> fieldValues, Locale locale, boolean setAttachment) throws FileTooBigException;

    /**
     * Perform a set attachment action
     * @param processInstanceUUID
     * @param attachments
     * @param attachmentName
     * @param fileName
     * @param setAttachment
     * @throws FileTooBigException
     */
    void performSetAttachmentAction(ProcessInstanceUUID processInstanceUUID, Set<InitialAttachment> attachments, String attachmentName, String fileName, boolean setAttachment) throws FileTooBigException;

    /**
     * get a new value of an object using an expression and an attibute
     * @param processDefinitionUUID the process definition UUID
     * @param variableExpression the expression (specifying which method of the object to call)
     * @param objectValue the current object value
     * @param attributeValue the attribute value
     * @return the new object value
     */
    Object getModifiedJavaObject(ProcessDefinitionUUID processDefinitionUUID, String variableExpression, Object objectValue, Object attributeValue);

    /**
     * evaluate an initial value expression (at form construction)
     * @param activityInstanceUUID the activity instance UUID
     * @param expressions the map of expressions to evaluate
     * @param locale the user's locale
     * @param isCurrentValue if true, values returned are the current values for the instance. otherwise, it's the values at step end
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluations as a Map
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> evaluateInitialExpressions(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException;

    /**
     * Evaluate an expression (at form submission)
     * @param activityInstanceUUID the activity instance UUID
     * @param expressions the map of expressions to evaluate
     * @param fieldValues the form field values
     * @param locale the user's locale
     * @param isCurrentValue if true, values returned are the current values for the instance. otherwise, it's the values at step end
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluations as a Map
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> evaluateExpressions(ActivityInstanceUUID activityInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException, ActivityNotFoundException;

    /**
     * Evaluate an expression (at form construction)
     * @param processInstanceUUID the process instance UUID
     * @param expressions the map of expressions to evaluate
     * @param locale the user's locale
     * @param isCurrentValue if true, values returned are the current values for the instance. otherwise, it's the values at process instantiation
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluations as a Map
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> evaluateInitialExpressions(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException;

    /**
     * Evaluate an expression (at form submission)
     * @param processInstanceUUID the process instance UUID
     * @param expressions the map of expressions to evaluate
     * @param fieldValues the form field values
     * @param locale the user's locale
     * @param isCurrentValue if true, values returned are the current values for the instance. otherwise, it's the values at process instantiation
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluations as a Map
     * @throws InstanceNotFoundException 
     */
    Map<String, Object> evaluateExpressions(ProcessInstanceUUID processInstanceUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, boolean isCurrentValue, Map<String, Object> context) throws InstanceNotFoundException;

    /**
     * Evaluate an expression (at form construction)
     * @param processDefinitionUUID the process definition UUID
     * @param expressions the map of expressions to evaluate
     * @param locale the user's locale
     * @param isCurrentValue if true, values returned are the current values for the instance. otherwise, it's the values at process instantiation
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluations as a Map
     * @throws ProcessNotFoundException 
     */
    Map<String, Object> evaluateInitialExpressions(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Locale locale, Map<String, Object> context) throws ProcessNotFoundException;

    /**
     * Evaluate an expression (at form submission)
     * @param processDefinitionUUID the process definition UUID
     * @param expressions the map of expressions to evaluate
     * @param fieldValues the form field values
     * @param locale the user's locale
     * @param isCurrentValue if true, values returned are the current values for the instance. otherwise, it's the values at process instantiation
     * @param context some additional context for groovy evaluation
     * @return The result of the evaluations as a Map
     * @throws ProcessNotFoundException 
     */
    Map<String, Object> evaluateExpressions(ProcessDefinitionUUID processDefinitionUUID, Map<String, String> expressions, Map<String, FormFieldValue> fieldValues, Locale locale, Map<String, Object> context) throws ProcessNotFoundException;
}
