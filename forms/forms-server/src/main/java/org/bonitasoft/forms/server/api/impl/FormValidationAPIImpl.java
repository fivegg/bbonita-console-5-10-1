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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.console.common.application.ApplicationResourcesUtils;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormValidationAPI;
import org.bonitasoft.forms.server.api.IFormWorkflowAPI;
import org.bonitasoft.forms.server.exception.FormValidationException;
import org.bonitasoft.forms.server.validator.AbstractFormFieldValidator;
import org.bonitasoft.forms.server.validator.AbstractFormValidator;
import org.bonitasoft.forms.server.validator.IFormFieldValidator;
import org.bonitasoft.forms.server.validator.IFormPageValidator;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 * implementation of {@link IFormValidationAPI}
 * 
 * @author Anthony Birembaut
 */
public class FormValidationAPIImpl implements IFormValidationAPI {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(FormValidationAPIImpl.class.getName());
    
    /**
     * Validate a form field value using the validator whose name is provided
     * @param activityInstanceUUID the activity instance UUID
     * @param processInstanceUUID the process instance UUID
     * @param processDefinitionUUID the process definition UUID
     * @param validatorClassName class name of the validator to use
     * @param fieldID the ID of the field
     * @param value to validate
     * @param submitButtonId the submit button id
     * @param locale the user's locale
     * @param transientDataContext
     * @param parameter
     * @param regex the regex for regex field validators
     * @return true if the field value comply with the validation. false otherwise
     * @throws FormValidationException 
     */
    protected boolean validateField(final ProcessDefinitionUUID processDefinitionUUID, final ProcessInstanceUUID processInstanceUUID, final ActivityInstanceUUID activityInstanceUUID, final String validatorClassName, final String fieldID, final FormFieldValue value, final String submitButtonId, final Locale locale, final Map<String, Object> transientDataContext, final String parameter) throws FormValidationException{
        
        boolean valid = true;
        try {
        	final ClassLoader processClassLoader = ApplicationResourcesUtils.getProcessClassLoader(processDefinitionUUID);
        	Class<?> validatorClass;
        	if (processClassLoader != null) {
        		validatorClass = Class.forName(validatorClassName, true, processClassLoader);
        	} else {
        		validatorClass = Class.forName(validatorClassName);
        	}
            final Object formFieldValidatorObject = validatorClass.newInstance();
            if (formFieldValidatorObject instanceof AbstractFormValidator) {
            	final AbstractFormValidator formValidator = (AbstractFormValidator)formFieldValidatorObject;
            	formValidator.setParameter(parameter);
            	formValidator.setProcessDefinitionUUID(processDefinitionUUID);
            	formValidator.setProcessInstanceUUID(processInstanceUUID);
            	formValidator.setActivityInstanceUUID(activityInstanceUUID);
            	formValidator.setTransientDataContext(transientDataContext);
            	formValidator.setSubmitButtonId(submitButtonId);
            }
            if (formFieldValidatorObject instanceof AbstractFormFieldValidator) {
            	((AbstractFormFieldValidator)formFieldValidatorObject).setFieldID(fieldID);
            }
            final IFormFieldValidator formFieldValidator = (IFormFieldValidator)formFieldValidatorObject;
            valid = formFieldValidator.validate(value, locale);
        } catch (final ClassNotFoundException e) {
            final String message = "The validator " + validatorClassName + " is not in the classpath";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormValidationException(message, e);
        } catch (final InstantiationException e) {
            final String message = "The validator " + validatorClassName + " cannot be instanciated";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormValidationException(message, e);
        } catch (final IllegalAccessException e) {
            final String message = "The validator " + validatorClassName + " does not have a public default constructor";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormValidationException(message, e);
        }
        return valid;
    }

    /**
     * Validate a form page using the validator whose name is provided
     * @param activityInstanceUUID the activity instance UUID
     * @param processInstanceUUID the process instance UUID
     * @param processDefinitionUUID the process definition UUID
     * @param validatorClassName class name of the validator to use
     * @param fields a map of the fields ids and values
     * @param submitButtonId the submit button id
     * @param locale the user's locale
     * @param transientDataContext
     * @param parameter
     * @return true if the page's fields values comply with the validation. false otherwise
     * @throws FormValidationException 
     */
    protected boolean validatePage(final ProcessDefinitionUUID processDefinitionUUID, final ProcessInstanceUUID processInstanceUUID, final ActivityInstanceUUID activityInstanceUUID, final String validatorClassName, final Map<String, FormFieldValue> fields, final String submitButtonId, final Locale locale, final Map<String, Object> transientDataContext, final String parameter) throws FormValidationException {

        boolean valid = true;
        try {
        	final ClassLoader processClassLoader = ApplicationResourcesUtils.getProcessClassLoader(processDefinitionUUID);
        	Class<?> validatorClass;
        	if (processClassLoader != null) {
        		validatorClass = Class.forName(validatorClassName, true, processClassLoader);
        	} else {
        		validatorClass = Class.forName(validatorClassName);
        	}
            final Object formPageValidatorObject = validatorClass.newInstance();
            if (formPageValidatorObject instanceof AbstractFormValidator) {
            	final AbstractFormValidator formValidator = ((AbstractFormValidator)formPageValidatorObject);
            	formValidator.setParameter(parameter);
            	formValidator.setProcessDefinitionUUID(processDefinitionUUID);
            	formValidator.setProcessInstanceUUID(processInstanceUUID);
            	formValidator.setActivityInstanceUUID(activityInstanceUUID);
            	formValidator.setTransientDataContext(transientDataContext);
            	formValidator.setSubmitButtonId(submitButtonId);
            }
            final IFormPageValidator formPageValidator = (IFormPageValidator)formPageValidatorObject;
            valid = formPageValidator.validate(fields, locale);
        } catch (final ClassNotFoundException e) {
            final String message = "The validator " + validatorClassName + " is not in the classpath";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormValidationException(message, e);
        } catch (final InstantiationException e) {
            final String message = "The validator " + validatorClassName + " cannot be instanciated";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormValidationException(message, e);
        } catch (final IllegalAccessException e) {
            final String message = "The validator " + validatorClassName + " does not have a public default constructor";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormValidationException(message, e);
        }
        return valid;
    }

    /**
     * Validate a form field value using the validators whose name is provided
     * @param activityInstanceUUID the activity instance UUID
     * @param processInstanceUUID the process instance UUID
     * @param processDefinitionUUID the process definition UUID
     * @param validators the list of validators
     * @param fieldID the ID of the field
     * @param value the form field value
     * @param submitButtonId the submit button id
     * @param userLocale the user's locale
     * @param transientDataContext
     * @return a list of the validators for which the field value does not comply with the validation
     * @throws FormValidationException
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     * @throws ProcessNotFoundException 
     */
    protected List<FormValidator> validateField(final ProcessDefinitionUUID processDefinitionUUID, final ProcessInstanceUUID processInstanceUUID, final ActivityInstanceUUID activityInstanceUUID, final List<FormValidator> validators, final String fieldId, final FormFieldValue value, final String submitButtonId, final Locale userLocale, final Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException {
        
        final IFormWorkflowAPI formWorkflowAPI = FormAPIFactory.getFormWorkflowAPI();
        final List<FormValidator> nonCompliantValidators = new ArrayList<FormValidator>();
        for (final FormValidator fieldValidator : validators) {
            if (!validateField(processDefinitionUUID, processInstanceUUID, activityInstanceUUID, fieldValidator.getValidatorClass(), fieldId, value, submitButtonId, userLocale, transientDataContext, fieldValidator.getParameter())) {
                final Map<String, FormFieldValue> fields = new HashMap<String, FormFieldValue>();
                fields.put(fieldId, value);
                if (activityInstanceUUID != null) {
                    fieldValidator.setLabel((String)formWorkflowAPI.getFieldValue(activityInstanceUUID, fieldValidator.getLabel(), fields, userLocale, true));
                } else if (processInstanceUUID != null) {
                    fieldValidator.setLabel((String)formWorkflowAPI.getFieldValue(processInstanceUUID, fieldValidator.getLabel(), fields, userLocale, true));
                } else {
                    fieldValidator.setLabel((String)formWorkflowAPI.getFieldValue(processDefinitionUUID, fieldValidator.getLabel(), fields, userLocale));
                }
                nonCompliantValidators.add(fieldValidator);
            }
        }
        return nonCompliantValidators;
    }

    /**
     * Validate a form page using the validators whose name is provided
     * @param activityInstanceUUID the activity instance UUID
     * @param processInstanceUUID the process instance UUID
     * @param processDefinitionUUID the process definition UUID
     * @param validators the list of validators
     * @param value the form field value
     * @param submitButtonId the submit button id
     * @param userLocale the user's locale
     * @param transientDataContext
     * @return a list of the validators for which the field value does not comply with the validation
     * @throws FormValidationException
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     * @throws ProcessNotFoundException 
     */
    protected List<FormValidator> validatePage(final ProcessDefinitionUUID processDefinitionUUID, final ProcessInstanceUUID processInstanceUUID, final ActivityInstanceUUID activityInstanceUUID, final List<FormValidator> validators, final Map<String, FormFieldValue> fields, final String submitButtonId, final Locale userLocale, final Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException {
        
        final IFormWorkflowAPI formWorkflowAPI = FormAPIFactory.getFormWorkflowAPI();
        final List<FormValidator> nonCompliantValidators = new ArrayList<FormValidator>();
        for (final FormValidator pageValidator : validators) {
            if (!validatePage(processDefinitionUUID, processInstanceUUID, activityInstanceUUID, pageValidator.getValidatorClass(), fields, submitButtonId, userLocale, transientDataContext, pageValidator.getParameter())) {
                if (activityInstanceUUID != null) {
                    pageValidator.setLabel((String)formWorkflowAPI.getFieldValue(activityInstanceUUID, pageValidator.getLabel(), fields, userLocale, true));
                } else if (processInstanceUUID != null) {
                    pageValidator.setLabel((String)formWorkflowAPI.getFieldValue(processInstanceUUID, pageValidator.getLabel(), fields, userLocale, true));
                } else {
                    pageValidator.setLabel((String)formWorkflowAPI.getFieldValue(processDefinitionUUID, pageValidator.getLabel(), fields, userLocale));
                }
                nonCompliantValidators.add(pageValidator);
            }
        }
        return nonCompliantValidators;
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> validateField(final ActivityInstanceUUID activityInstanceUUID, final List<FormValidator> validators, final String fieldId, final FormFieldValue fieldValue, final String submitButtonId, final Locale userLocale, final Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException {
        return validateField(activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID(), activityInstanceUUID.getProcessInstanceUUID(), activityInstanceUUID, validators, fieldId, fieldValue, submitButtonId, userLocale, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> validateField(final ProcessInstanceUUID processInstanceUUID, final List<FormValidator> validators, final String fieldId, final FormFieldValue fieldValue, final String submitButtonId, final Locale userLocale, final Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException {
        return validateField(processInstanceUUID.getProcessDefinitionUUID(), processInstanceUUID, null, validators, fieldId, fieldValue, submitButtonId, userLocale, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> validateField(final ProcessDefinitionUUID processDefinitionUUID, final List<FormValidator> validators, final String fieldId, final FormFieldValue fieldValue, final String submitButtonId, final Locale userLocale, final Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException {
        return validateField(processDefinitionUUID, null, null, validators, fieldId, fieldValue, submitButtonId, userLocale, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> validatePage(final ActivityInstanceUUID activityInstanceUUID, final List<FormValidator> validators, final Map<String, FormFieldValue> fields, final String submitButtonId, final Locale userLocale, final Map<String, Object> transientDataContext)
            throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException {
        return validatePage(activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID(), activityInstanceUUID.getProcessInstanceUUID(), activityInstanceUUID, validators, fields, submitButtonId, userLocale, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> validatePage(final ProcessInstanceUUID processInstanceUUID, final List<FormValidator> validators, final Map<String, FormFieldValue> fields, final String submitButtonId, final Locale userLocale, final Map<String, Object> transientDataContext)
            throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException {
        return validatePage(processInstanceUUID.getProcessDefinitionUUID(), processInstanceUUID, null, validators, fields, submitButtonId, userLocale, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> validatePage(final ProcessDefinitionUUID processDefinitionUUID, final List<FormValidator> validators, final Map<String, FormFieldValue> fields, final String submitButtonId, final Locale userLocale, final Map<String, Object> transientDataContext)
            throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException {
        return validatePage(processDefinitionUUID, null, null, validators, fields, submitButtonId, userLocale, transientDataContext);
    }
}