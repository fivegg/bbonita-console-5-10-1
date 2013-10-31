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
package org.bonitasoft.forms.server.accessor.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.server.accessor.DefaultFormsProperties;
import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.accessor.IFormDefAccessor;
import org.bonitasoft.forms.server.accessor.widget.IEngineWidgetBuilder;
import org.bonitasoft.forms.server.accessor.widget.WidgetBuilderFactory;
import org.bonitasoft.forms.server.api.IFormExpressionsAPI;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.bonitasoft.forms.server.exception.NotHandledTypeException;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.def.element.AttachmentDefinition;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ActivityDefNotFoundException;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.util.AccessorUtil;

/**
 * Implementation of {@link IFormDefAccessor} used when no xml form
 * definition file is defined or when no form is defined in the xml file for the
 * required activity
 * 
 * @deprecated
 * @author Anthony Birembaut
 */
public class EngineActivityFormDefAccessorImpl implements IFormDefAccessor {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(EngineActivityFormDefAccessorImpl.class.getName());

    /**
     * the activity Name
     */
    protected String activityName;
    
    /**
     * The process name
     */
    protected String processName;
    
    /**
     * indicates whether the page has to be displayed in edit mode (for a ready
     * task) or not (view mode)
     */
    protected boolean isEditMode;

    /**
     * Accessor used to create the widgets and validators from a process definition retrieved from the engine
     */
    protected IEngineWidgetBuilder engineWidgetBuilder = WidgetBuilderFactory.getEngineWidgetBuilder();
    
    /**
     * The activity definition
     */
    protected ActivityDefinition activityDefinition;

    /**
     * Widget data objects for the activity
     */
    protected List<FormWidget> activityWidgets = new ArrayList<FormWidget>();
    
    /**
     * the action for each widget
     */
    protected Map<String, FormAction> widgetsActions = new HashMap<String, FormAction>();
    
    /**
     * the pages widgets
     */
    protected Map<String, List<String>> pagesWidgets = new HashMap<String, List<String>>();
    
    /**
     * Nb of pages in the pageflow
     */
    protected int nbOfPages = -1;
    
    /**
     * @param processDefinitionUUID
     * @param isEditMode 
     * @param activityId
     */
    public EngineActivityFormDefAccessorImpl(final ProcessDefinitionUUID processDefinitionUUID, final String activityName, final boolean includeProcessVariables, final boolean isEditMode) {

        this.activityName = activityName;
        this.isEditMode = isEditMode;
        processName = processDefinitionUUID.getProcessName();

        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        
        try {
            Set<DataFieldDefinition> processDataFields = new HashSet<DataFieldDefinition>();
            Set<AttachmentDefinition> attachments = new HashSet<AttachmentDefinition>();
            if (includeProcessVariables) {
                processDataFields = queryDefinitionAPI.getProcessDataFields(processDefinitionUUID);
                attachments = queryDefinitionAPI.getAttachmentDefinitions(processDefinitionUUID);
            }
            activityDefinition = queryDefinitionAPI.getProcessActivity(processDefinitionUUID, activityName);
            final Set<DataFieldDefinition> activityDataFields = queryDefinitionAPI.getActivityDataFields(activityDefinition.getUUID());
            createActivityWidgets(processDataFields, attachments, activityDataFields, isEditMode);
        } catch (final ProcessNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Process " + processDefinitionUUID + " not found.", e);
            }
        } catch (final ActivityNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Activity " + activityName + " not found for process " + processDefinitionUUID, e);
            }
        } catch (final ActivityDefNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Activity " + activityDefinition.getUUID() + " not found.", e);
            }
        }
    }

    /**
     * create the widgets data objects and put it in the activityWidgets list
     * @param processDataFields
     * @param attachments
     * @param activityDataFields
     */
    protected void createActivityWidgets(final Set<DataFieldDefinition> processDataFields, final Set<AttachmentDefinition> attachments, final Set<DataFieldDefinition> activityDataFields, final boolean isEditMode) {
        
        final List<FormWidget> processWidgets = new ArrayList<FormWidget>();
        for (final DataFieldDefinition dataFieldDefinition : processDataFields) {
            try {
                final FormWidget processWidget = engineWidgetBuilder.createWidget(dataFieldDefinition, processName, isEditMode);
                if (isEditMode) {
                    final FormAction activityAction = new FormAction(ActionType.SET_VARIABLE, dataFieldDefinition.getName(), EngineApplicationFormDefAccessorImpl.PROCESS_VARIABLE, IFormExpressionsAPI.FIELDID_PREFIX + processWidget.getId(), null);
                    widgetsActions.put(processWidget.getId(), activityAction);
                }
                processWidgets.add(processWidget);
            } catch (final NotHandledTypeException e) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, e.getMessage());
                }
            }
        }
        for (final AttachmentDefinition attachment : attachments) {
            final FormWidget attachmentWidget = engineWidgetBuilder.createWidget(attachment, processName, isEditMode);
            if (isEditMode) {
                final FormAction activityAction = new FormAction(ActionType.SET_ATTACHMENT, null, null, IFormExpressionsAPI.FIELDID_PREFIX + attachmentWidget.getId(), null, attachment.getName());
                widgetsActions.put(attachmentWidget.getId(), activityAction);
            }
            processWidgets.add(attachmentWidget);
        }
        Collections.sort(processWidgets);
        final List<FormWidget> taskWidgets = new ArrayList<FormWidget>();
        for (final DataFieldDefinition dataFieldDefinition : activityDataFields) {
            try {
                final FormWidget taskWidget = engineWidgetBuilder.createWidget(dataFieldDefinition, activityName, isEditMode);
                if (isEditMode) {
                    final FormAction activityAction = new FormAction(ActionType.SET_VARIABLE, dataFieldDefinition.getName(), EngineApplicationFormDefAccessorImpl.ACTIVITY_VARIABLE, IFormExpressionsAPI.FIELDID_PREFIX + taskWidget.getId(), null);
                    widgetsActions.put(taskWidget.getId(), activityAction);
                }
                taskWidgets.add(taskWidget);
            } catch (final NotHandledTypeException e) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, e.getMessage());
                }
            }
        }
        Collections.sort(taskWidgets);
        activityWidgets.addAll(processWidgets);
        activityWidgets.addAll(taskWidgets);
    }

    /**
     * {@inheritDoc}
     */
    public List<FormAction> getActions(final String pageId) throws InvalidFormDefinitionException {
        
        final List<FormAction> actions = new ArrayList<FormAction>();
        List<String> widgetIds = pagesWidgets.get(pageId);
        if (widgetIds == null) {
            getPageWidgets(pageId);
            widgetIds = pagesWidgets.get(pageId);
        }
        for (final String widgetId : widgetIds) {
            final FormAction widgetAction = widgetsActions.get(widgetId);
            if (widgetAction != null) {
                actions.add(widgetAction);
            }
        }
        return actions;
    }

    /**
     * {@inheritDoc}
     */
    public String getConfirmationTemplate() {
        return DefaultFormsPropertiesFactory.getDefaultFormProperties().getPageConfirmationTemplate();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getConfirmationMessage() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getPageLabel(final String pageId) throws InvalidFormDefinitionException {
        final int nbOfPages = getNbOfPages();
        String pageLabelComplement = null;
        if (nbOfPages > 1) {
            pageLabelComplement = " : page " + (Integer.parseInt(pageId) + 1) + " / " + nbOfPages;
        } else {
            pageLabelComplement = "";
        }
        final String activityName = activityDefinition.getName();
        final String activityLabel = activityDefinition.getLabel();
        if (activityLabel != null && activityLabel.length() != 0) {
            return toUpperCaseFirstLetter(activityLabel + pageLabelComplement);
        } else {
            return toUpperCaseFirstLetter(activityName + pageLabelComplement);
        }
    }
    
    /**
     * put set the fisrt letter of a label to uppercase
     * @param label the label
     * @return the new label
     */
    protected String toUpperCaseFirstLetter(final String label) {
        if (label.length() > 0) {
            final Character firstLetter = Character.toUpperCase(label.charAt(0));
            return firstLetter + label.substring(1, label.length());
        } else {
            return label;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getFormPageTemplate(final String pageId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> getPageValidators(final String pageId) throws InvalidFormDefinitionException {
        return new ArrayList<FormValidator>();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPages() {
        if (nbOfPages == -1) {
            nbOfPages = getNbOfPages();
        }
        final List<String> pages = new ArrayList<String>();
        for (int i = 0; i < nbOfPages; i++) {
            pages.add(Integer.toString(i));
        }
        return pages;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<String> getViewPages() throws InvalidFormDefinitionException {
        return getPages();
    }

    /**
     * {@inheritDoc}
     */
    public List<FormWidget> getPageWidgets(final String pageId) throws InvalidFormDefinitionException {
        if (nbOfPages == -1) {
            nbOfPages = getNbOfPages();
        }
        final List<FormWidget> widgets = engineWidgetBuilder.getPageWidgets(pageId, nbOfPages, activityWidgets, activityName, isEditMode);
        final List<String> widgetIds = new ArrayList<String>();
        for (final FormWidget formWidget : widgets) {
            widgetIds.add(formWidget.getId());
        }
        pagesWidgets.put(pageId, widgetIds);
        return widgets;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isHTMLAllowedInLabel(final String pageId) {
        return false;
    }
    
    /**
     * @return the number of pages
     */
    protected int getNbOfPages() {
        final DefaultFormsProperties defaultProperties = DefaultFormsPropertiesFactory.getDefaultFormProperties();
        if (activityWidgets.size() != 0 && activityWidgets.size() % defaultProperties.getMaxWigdetPerPage() == 0) {
            return activityWidgets.size() / defaultProperties.getMaxWigdetPerPage();
        } else {
            return activityWidgets.size() / defaultProperties.getMaxWigdetPerPage() + 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getFirstPageExpression() throws InvalidFormDefinitionException {
        if (nbOfPages == -1) {
            nbOfPages = getNbOfPages();
        }
        if (nbOfPages > 0) {
        	return "0";
        } else {
        	return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getNextPageExpression(final String pageId) throws InvalidFormDefinitionException {
        try {
            final int currentPageIndex = Integer.parseInt(pageId);
            return Integer.toString(currentPageIndex + 1);
        } catch (final NumberFormatException e) {
            throw new InvalidFormDefinitionException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<TransientData> getTransientData() throws InvalidFormDefinitionException {
        return new ArrayList<TransientData>();
    }
    
}
