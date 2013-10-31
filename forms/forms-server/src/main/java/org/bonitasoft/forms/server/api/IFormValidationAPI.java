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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.server.exception.FormValidationException;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 * Forms validation API
 * 
 * @author Anthony Birembaut
 */
public interface IFormValidationAPI {

    /**
     * Validate a form field value using the validators provided
     * @param activityInstanceUUID the activity instance UUID
     * @param validators List of validators to use
     * @param fieldId the ID of the field
     * @param fieldValue to validate as a {@link FormFieldValue} object
     * @param submitButtonId the submit button ID
     * @param userLocale the user's locale
     * @param transientDataContext
     * @return a list of the validators for which the field value does not comply with the validation
     * @throws FormValidationException 
     * @throws ProcessNotFoundException 
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    List<FormValidator> validateField(ActivityInstanceUUID activityInstanceUUID, List<FormValidator> validators, String fieldId, FormFieldValue fieldValue, String submitButtonId, Locale userLocale, Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException;
    
    /**
     * Validate a form page using the validators provided
     * @param activityInstanceUUID the activity instance UUID
     * @param validators List of validators to use
     * @param fields a map of the fields ids and values
     * @param submitButtonId the submit button ID
     * @param userLocale the user's locale
     * @param transientDataContext
     * @return a list of the validators for which the field value does not comply with the validation
     * @throws FormValidationException
     * @throws ProcessNotFoundException 
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    List<FormValidator> validatePage(ActivityInstanceUUID activityInstanceUUID, List<FormValidator> validators, Map<String, FormFieldValue> fields, String submitButtonId, Locale userLocale, Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException;

    /**
     * Validate a form field value using the validators provided
     * @param processInstanceUUID the process instance UUID
     * @param validators List of validators to use
     * @param submitButtonId the submit button ID
     * @param value to validate as a {@link FormFieldValue} object
     * @param submitButtonId the submit button ID
     * @param userLocale the user's locale
     * @param transientDataContext
     * @return a list of the validators for which the field value does not comply with the validation
     * @throws FormValidationException 
     * @throws ProcessNotFoundException 
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    List<FormValidator> validateField(ProcessInstanceUUID processInstanceUUID, List<FormValidator> validators, String fieldId, FormFieldValue fieldValue, String submitButtonId, Locale userLocale, Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException;
    
    /**
     * Validate a form page using the validators provided
     * @param processInstanceUUID the process instance UUID
     * @param validators List of validators to use
     * @param fields a map of the fields ids and values
     * @param submitButtonId the submit button ID
     * @param userLocale the user's locale
     * @param transientDataContext
     * @return a list of the validators for which the field value does not comply with the validation
     * @throws FormValidationException
     * @throws ProcessNotFoundException 
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    
    List<FormValidator> validatePage(ProcessInstanceUUID processInstanceUUID, List<FormValidator> validators, Map<String, FormFieldValue> fields, String submitButtonId, Locale userLocale, Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException;
    
    /**
     * Validate a form field value using the validators provided
     * @param processDefinitionUUID the process definition UUID
     * @param validators List of validators to use
     * @param value to validate as a {@link FormFieldValue} object
     * @param submitButtonId the submit button ID
     * @param userLocale the user's locale
     * @param transientDataContext
     * @return a list of the validators for which the field value does not comply with the validation
     * @throws FormValidationException 
     * @throws ProcessNotFoundException 
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    List<FormValidator> validateField(ProcessDefinitionUUID processDefinitionUUID, List<FormValidator> validators, String fieldId, FormFieldValue fieldValue, String submitButtonId, Locale userLocale, Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException;
    
    /**
     * Validate a form page using the validators provided
     * @param processDefinitionUUID the process definition UUID
     * @param validators List of validators to use
     * @param fields a map of the fields ids and values
     * @param submitButtonId the submit button ID
     * @param userLocale the user's locale
     * @param transientDataContext
     * @return a list of the validators for which the field value does not comply with the validation
     * @throws FormValidationException 
     * @throws ProcessNotFoundException 
     * @throws ActivityNotFoundException 
     * @throws InstanceNotFoundException 
     */
    List<FormValidator> validatePage(ProcessDefinitionUUID processDefinitionUUID, List<FormValidator> validators, Map<String, FormFieldValue> fields, String submitButtonId, Locale userLocale, Map<String, Object> transientDataContext) throws FormValidationException, InstanceNotFoundException, ActivityNotFoundException, ProcessNotFoundException;
}
