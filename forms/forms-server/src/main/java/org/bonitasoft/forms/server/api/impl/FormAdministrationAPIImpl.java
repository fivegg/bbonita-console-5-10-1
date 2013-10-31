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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bonitasoft.console.security.server.threadlocal.ThreadLocalManager;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldAvailableValue;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.FormType;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.accessor.FormDefAccessorFactory;
import org.bonitasoft.forms.server.accessor.IFormDefAccessor;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormAdministrationAPI;
import org.bonitasoft.forms.server.api.IFormWorkflowAPI;
import org.bonitasoft.forms.server.api.impl.util.FormFieldValuesUtil;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 * Implementation of {@link IFormAdministrationAPI}
 * 
 * @author Anthony Birembaut
 */
public class FormAdministrationAPIImpl extends FormFieldValuesUtil implements IFormAdministrationAPI {

    /**
     * An instance time to live
     */
    protected final static long INSTANCE_EXPIRATION_TIME = 300000;
    
    /**
     * Last access to the current instance
     */
    protected Long lastAccess = new Date().getTime();
    
    /**
     * The process definition UUID of the process to which this instance is
     * associated
     */
    protected ProcessDefinitionUUID processDefinitionUUID;
    
    /**
     * the {@link Date} of the process deployment
     */
    protected Date processDeployementDate;
    
    /**
     * Utility object allowing to parse the xml definition file to retrieve
     * process variables form information
     */
    protected IFormDefAccessor processFormDefinition;

    /**
     * Map of utility objects allowing retrieve activity variable forms information.
     * Each entry of the map contains an activity ID as a key and the corresponding
     * {@link IFormDefAccessor} object as the value
     */
    protected Map<String, IFormDefAccessor> activitiesFormDefinitions = new HashMap<String, IFormDefAccessor>();
    
    /**
     * Instances Map for the edit mode
     */
    private static Map<String, FormAdministrationAPIImpl> INSTANCES = new ConcurrentHashMap<String, FormAdministrationAPIImpl>();
    
    private static String INSTANCES_MAP_SEPERATOR = "@";

    /**
     * Retrieve an instance of FormAdministrationAPIImpl or create a new one if necessary.
     * The map contains a cache of instances. Each instance has a validity duration equals to the INSTANCE_EXPIRATION_TIME constant value
     * The deployement date is also check because a process can be undeployed and redeployed (after modifications) with the same UUID
     * @param processDefinitionUUID the process definition UUID
     * @param processDeployementDate the deployement date of the process
     * @param isEditMode indicates if the pages to display are in edit mode or view mode (for terminated tasks)
     * @return the FormDefinitionAPIImpl instance
     * @throws IOException
     * @throws ProcessNotFoundException
     */
    public static synchronized FormAdministrationAPIImpl getInstance(final ProcessDefinitionUUID processDefinitionUUID, final Date processDeployementDate) throws ProcessNotFoundException, IOException {

        final String domain = ThreadLocalManager.getDomain();
        String instanceKey;
        if(processDefinitionUUID == null){
            instanceKey = null + INSTANCES_MAP_SEPERATOR + domain;
        }else{
            instanceKey = processDefinitionUUID.getValue() + INSTANCES_MAP_SEPERATOR + domain;
        }
        FormAdministrationAPIImpl instance = INSTANCES.get(instanceKey);
        boolean outOfDateDefinition = false;
        if (instance != null && ((processDeployementDate != null && processDeployementDate.compareTo(instance.processDeployementDate) != 0) || instance.hasExpired())) {
            INSTANCES.remove(processDefinitionUUID.getValue() + INSTANCES_MAP_SEPERATOR + domain);
            outOfDateDefinition = true;
        }
        if (instance == null || outOfDateDefinition) {
            final FormAdministrationAPIImpl newInstance = new FormAdministrationAPIImpl(processDefinitionUUID, processDeployementDate, domain);
            INSTANCES.put(instanceKey, newInstance);
            instance = newInstance;
        } else {
            instance.lastAccess = new Date().getTime();
        }
        return instance;
    }
    
    /**
     * Private contructor to prevent instantiation
     * 
     * @param processDefinitionUUID
     * @throws IOException
     * @throws ProcessNotFoundException
     */
    protected FormAdministrationAPIImpl(final ProcessDefinitionUUID processDefinitionUUID, final Date processDeployementDate, final String domain) throws IOException, ProcessNotFoundException {
        this.defaultDateFormatPattern = DefaultFormsPropertiesFactory.getDefaultFormProperties().getDefaultDateFormat();
        this.processDefinitionUUID = processDefinitionUUID;
        this.processDeployementDate = processDeployementDate;
        this.processFormDefinition = FormDefAccessorFactory.getEngineProcessFormDefAccessor(processDefinitionUUID, false, true);
    }
    
    /**
     * @param activityName
     * @return the right activity form definition accessor
     */
    protected IFormDefAccessor getActivityFormDefinition(final String activityName) throws IOException, ProcessNotFoundException {

        IFormDefAccessor activityFormDefinition = activitiesFormDefinitions.get(activityName);

        if (activityFormDefinition == null) {
            activityFormDefinition = FormDefAccessorFactory.getEngineActivityFormDefAccessor(processDefinitionUUID, activityName, false, true);
            activitiesFormDefinitions.put(activityName, activityFormDefinition);
        }
        return activityFormDefinition;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<FormAction> getActions(final String activityName) throws InvalidFormDefinitionException, ProcessNotFoundException, IOException {
    	final List<FormAction> formActions = new ArrayList<FormAction>();
    	final IFormDefAccessor activityFormDefinition = getActivityFormDefinition(activityName);
    	for (final String pageId : getPageList(activityName)) {
    		formActions.addAll(activityFormDefinition.getActions(pageId));
    	}
    	return formActions;
    }

    /**
     * {@inheritDoc}
     */
    public FormPage getFormPage(final String activityName, final String pageId) throws InvalidFormDefinitionException, ProcessNotFoundException, IOException {
        final List<FormWidget> pageWidgets = getActivityFormDefinition(activityName).getPageWidgets(pageId);
        final List<FormValidator> pageValidators = getActivityFormDefinition(activityName).getPageValidators(pageId);
        final String pageLabel = getActivityFormDefinition(activityName).getPageLabel(pageId);
        return new FormPage(pageId, pageLabel, null, pageWidgets, pageValidators, FormType.entry);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPageList(final String activityName) throws InvalidFormDefinitionException, ProcessNotFoundException, IOException {
        return getActivityFormDefinition(activityName).getPages();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPageList() throws InvalidFormDefinitionException {
        return processFormDefinition.getPages();
    }

    /**
     * {@inheritDoc}
     */
    public List<FormAction> getProcessActions() throws InvalidFormDefinitionException {
    	final List<FormAction> formActions = new ArrayList<FormAction>();
    	for (final String pageId : getPageList()) {
    		formActions.addAll(processFormDefinition.getActions(pageId));
    	}
        return formActions;
    }

    /**
     * {@inheritDoc}
     */
    public FormPage getProcessFormPage(final String pageId) throws InvalidFormDefinitionException, ProcessNotFoundException, IOException {
        final List<FormWidget> pageWidgets = processFormDefinition.getPageWidgets(pageId);
        final List<FormValidator> pageValidators = processFormDefinition.getPageValidators(pageId);
        final String pageLabel = processFormDefinition.getPageLabel(pageId);
        return new FormPage(pageId, pageLabel.substring(1), null, pageWidgets, pageValidators, FormType.entry);
    }

    /**
     * Determinates whether the current instance has expired or not
     * @return true if the current instance has expired, false otherwise
     */
    protected boolean hasExpired() {
        final long now = new Date().getTime();
        return this.lastAccess + INSTANCE_EXPIRATION_TIME < now;
    }

    public void setFormWidgetValues(final ProcessInstanceUUID processInstanceUUID, final FormWidget formWidget, final Locale locale) throws InstanceNotFoundException {
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        formWidget.setLabel(getStringValue(workflowAPI.getFieldValue(processInstanceUUID, formWidget.getLabel(), locale, true)));
        formWidget.setTitle(getStringValue(workflowAPI.getFieldValue(processInstanceUUID, formWidget.getTitle(), locale, true)));
        if (formWidget.getType().name().startsWith("FILE")) {
            final String initialValueStr = formWidget.getInitialValueExpression();
            final String fileName = workflowAPI.getAttachmentFileName(processInstanceUUID, initialValueStr, true);
            final FormFieldValue formFieldValue = new FormFieldValue(fileName, File.class.getName());
            formFieldValue.setAttachmentName(initialValueStr);
            formWidget.setInitialFieldValue(formFieldValue);
        } else if (!formWidget.getType().name().startsWith("BUTTON_")) {
            // convert the value object returned into a FormFieldValue object.
            // This conversion is needed because RPC calls do not support the type 'Object'.
            final Object value = workflowAPI.getFieldValue(processInstanceUUID, formWidget.getInitialValueExpression(), locale, true);
            formWidget.setInitialFieldValue(getFieldValue(value, formWidget, locale));
            //set the available values list from a groovy expression for listboxes, radiobutton groups and checkbox groups
            if (formWidget.getAvailableValuesExpression() != null) {
                final Object availableValuesObject = workflowAPI.getFieldValue(processInstanceUUID, formWidget.getAvailableValuesExpression(), locale, false);
                final List<FormFieldAvailableValue> availableValues = getAvailableValues(availableValuesObject, formWidget);
                formWidget.setAvailableValues(availableValues);
            }
        }
    }

    public void setFormWidgetValues(final ActivityInstanceUUID activityInstanceUUID, final FormWidget formWidget, final Locale locale) throws InstanceNotFoundException, ActivityNotFoundException {
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        formWidget.setLabel(getStringValue(workflowAPI.getFieldValue(activityInstanceUUID, formWidget.getLabel(), locale, true)));
        formWidget.setTitle(getStringValue(workflowAPI.getFieldValue(activityInstanceUUID, formWidget.getTitle(), locale, true)));
        if (!formWidget.getType().name().startsWith("BUTTON_")) {
            // convert the value object returned into a FormFieldValue object.
            // This conversion is needed because RPC calls do not support the type 'Object'.
            final Object value = workflowAPI.getFieldValue(activityInstanceUUID, formWidget.getInitialValueExpression(), locale, true);
            formWidget.setInitialFieldValue(getFieldValue(value, formWidget, locale));
            //set the available values list from a groovy expression for listboxes, radiobutton groups and checkbox groups
            if (formWidget.getAvailableValuesExpression() != null) {
                final Object availableValuesObject = workflowAPI.getFieldValue(activityInstanceUUID, formWidget.getAvailableValuesExpression(), locale, true);
                final List<FormFieldAvailableValue> availableValues = getAvailableValues(availableValuesObject, formWidget);
                formWidget.setAvailableValues(availableValues);
            }
        }
    }
    
}
