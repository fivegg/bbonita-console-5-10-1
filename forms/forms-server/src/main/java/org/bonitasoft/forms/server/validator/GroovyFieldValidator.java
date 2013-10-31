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
package org.bonitasoft.forms.server.validator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormWorkflowAPI;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;

/**
 * @author Anthony Birembaut
 *
 */
public class GroovyFieldValidator extends AbstractFormFieldValidator implements IFormFieldValidator {
	
    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(GroovyFieldValidator.class.getName());

    /**
     * {@inheritDoc}
     */
    public boolean validate(final FormFieldValue fieldInput, final Locale locale) {
    	
    	final Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
    	final String fieldID = getFieldID();
    	if (fieldID != null) {
    		fieldValues.put(fieldID, fieldInput);
    	} else {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "The field ID for the groovy context is undefined.");
            }
    	}
    	final IFormWorkflowAPI formWorkflowAPI = FormAPIFactory.getFormWorkflowAPI();
    	boolean valid = false;
    	String expression = getParameter();
    	if (expression != null && expression.length() > 0) {
    		expression = "${" + expression + "}";
    	} else {
    		LOGGER.log(Level.SEVERE, "The expression used in the groovy validator of " + fieldID + " is empty.");
    	}
    	final Map<String, Object> context = getTransientDataContext();
   	    context.put(CLICKED_BUTTON_VARNAME, getSubmitButtonId());
    	try {
	    	final ActivityInstanceUUID activityInstanceUUID = getActivityInstanceUUID();
	    	if (activityInstanceUUID != null) {
	    		valid = (Boolean)formWorkflowAPI.getFieldValue(activityInstanceUUID, expression, fieldValues, locale, true, context);
	    	} else {
	    		final ProcessDefinitionUUID processDefinitionUUID = getProcessDefinitionUUID();
	    		if (processDefinitionUUID != null) {
	    			valid = (Boolean)formWorkflowAPI.getFieldValue(processDefinitionUUID, expression, fieldValues, locale, context);
	    		} else {
	                if (LOGGER.isLoggable(Level.SEVERE)) {
	                    LOGGER.log(Level.SEVERE, "The process definition UUID and activity definition UUID are undefined.");
	                }
	    		}
	    	}
    	} catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while validating with a groovy expression", e);
            }
		}
    	return valid;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        return "Groovy validator";
    }
}
