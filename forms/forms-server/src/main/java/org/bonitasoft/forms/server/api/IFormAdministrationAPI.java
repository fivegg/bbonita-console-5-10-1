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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldAvailableValue;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 * API to retrieve the different components for the variables administration view.
 * @author Anthony Birembaut
 */
public interface IFormAdministrationAPI {
    
    /**
     * Retrieve a list of ids of the pages contained in the page flow associated with the activity
     * @param activityName the activity Name
     * @return a list of ids of the pages defined for the flow of the activity
     * @throws InvalidFormDefinitionException 
     * @throws IOException 
     * @throws ProcessNotFoundException 
     */
    List<String> getPageList(String activityName) throws InvalidFormDefinitionException, ProcessNotFoundException, IOException;
    
    /**
     * Retrieve a list of ids of the pages contained in the page flow associated with the process
     * @return a list of ids of the pages defined for the flow of the process
     * @throws InvalidFormDefinitionException
     */
    List<String> getPageList() throws InvalidFormDefinitionException;
    
    /**
     * Retrieve a page data containing a template and a list of the widgets to be displayed
     * @param activityName the activity Name
     * @param pageId the page ID
     * @return a {@link FormPage} object containing the elements required to build the level2 (page form)
     * @throws InvalidFormDefinitionException 
     * @throws IOException 
     * @throws ProcessNotFoundException 
     */
    FormPage getFormPage(String activityName, String pageId) throws InvalidFormDefinitionException, ProcessNotFoundException, IOException;
    
    /**
     * Retrieve a page data containing a template and a list of the widgets to be displayed for an instantiation form
     * @param pageId the page ID
     * @return a {@link FormPage} object containing the elements required to build the level2 (page form)
     * @throws InvalidFormDefinitionException
     * @throws ProcessNotFoundException
     * @throws IOException
     */
    FormPage getProcessFormPage(String pageId) throws InvalidFormDefinitionException, ProcessNotFoundException, IOException;

    /**
     * Retrieve a list of actions to be executed at activity form submission
     * @param activityName the activity Name
     * @return a list of {@link FormAction} to be executed at form submission
     * @throws InvalidFormDefinitionException 
     * @throws IOException 
     * @throws ProcessNotFoundException 
     */
    List<FormAction> getActions(String activityName) throws InvalidFormDefinitionException, ProcessNotFoundException, IOException;
    
    /**
     * Retrieve a list of actions to be executed at form submission for a process instantiation
     * @return a list of {@link FormAction} to be executed at form submission
     * @throws InvalidFormDefinitionException 
     */
    List<FormAction> getProcessActions() throws InvalidFormDefinitionException;

    /**
     * Build a field value object from the process definition
     * @param value the value retrieved from the engine
     * @param formWidget the {@link FormWidget} associated with this field value
     * @param locale
     * @return a {@link FormFieldValue} object
     */
    FormFieldValue getFieldValue(Object value, FormWidget formWidget, Locale licale);
    
    /**
     * Build a FormFieldAvailableValue List from a {@link Map} or a {@link List} and set it in the widget
     * @param availableValuesObject  the {@link Map} or {@link List} of values
     * @param widget the widget to set
     * @return a List of {@link FormFieldAvailableValue}
     */
    List<FormFieldAvailableValue> getAvailableValues(Object availableValuesObject, FormWidget widget);
    
    void setFormWidgetValues(ProcessInstanceUUID processInstanceUUID, FormWidget formWidget, Locale locale) throws InstanceNotFoundException;
    void setFormWidgetValues(ActivityInstanceUUID activityInstanceUUID, FormWidget formWidget, Locale locale) throws InstanceNotFoundException, ActivityNotFoundException;
}
