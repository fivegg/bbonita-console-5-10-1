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
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.util.AccessorUtil;

/**
 * Implementation of {@link IFormDefAccessor} used when no xml form
 * definition file is defined or when no form is defined in the xml file for the process
 * 
 * @deprecated
 * @author Anthony Birembaut
 */
public class EngineProcessFormDefAccessorImpl implements IFormDefAccessor {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(EngineProcessFormDefAccessorImpl.class.getName());
    
    /**
     * The process name
     */
    protected String processName;
    
    /**
     * The process label
     */
    protected String processLabel;
    
    /**
     * indicates whether the page has to be displayed in edit mode (for an instantiation) or not (view mode)
     */
    protected boolean isEditMode;
    
    /**
     * indicates whether the page is an instance recap or not (otherwise it's a process instantiation page)
     */
    protected boolean isInstanceRecap;
    
    /**
     * Accessor used to create the widgets and validators from a process definition retrieved from the engine
     */
    protected IEngineWidgetBuilder engineWidgetBuilder = WidgetBuilderFactory.getEngineWidgetBuilder();
    
    /**
     * Widget data objects for the activity
     */
    protected List<FormWidget> processWidgets = new ArrayList<FormWidget>();
    
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
     * Constructor
     * @param processDefinitionUUID the process definition UUID
     * @param isInstanceRecap
     * @param isEditMode 
     * @throws ProcessNotFoundException 
     */
    public EngineProcessFormDefAccessorImpl(final ProcessDefinitionUUID processDefinitionUUID, final boolean isInstanceRecap, final boolean isEditMode) throws ProcessNotFoundException {
        
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        this.isEditMode = isEditMode;
        this.isInstanceRecap = isInstanceRecap;
        processName = processDefinitionUUID.getProcessName();
        processLabel = queryDefinitionAPI.getProcess(processDefinitionUUID).getLabel();
        
        try {
            final Set<DataFieldDefinition> processDataFields = queryDefinitionAPI.getProcessDataFields(processDefinitionUUID);
            final Set<AttachmentDefinition> attachments = queryDefinitionAPI.getAttachmentDefinitions(processDefinitionUUID);
            createWidgets(processDataFields, attachments, isEditMode);
        } catch (final ProcessNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Process " + processDefinitionUUID + " not found.", e);
            }
        }
    }

    /**
     * create the widgets data objects and put it in the processWidgets list
     * @param processDataFields
     * @param attachments
     * @param isEditMode
     */
    protected void createWidgets(final Set<DataFieldDefinition> processDataFields, final Set<AttachmentDefinition> attachments, final boolean isEditMode) {
        for (final DataFieldDefinition dataFieldDefinition : processDataFields) {
            try {
                final FormWidget processWidget = engineWidgetBuilder.createWidget(dataFieldDefinition, processName, isEditMode);
                if (isEditMode) {
                    final FormAction processAction = new FormAction(ActionType.SET_VARIABLE, dataFieldDefinition.getName(), EngineApplicationFormDefAccessorImpl.PROCESS_VARIABLE, IFormExpressionsAPI.FIELDID_PREFIX + processWidget.getId(), null);
                    widgetsActions.put(processWidget.getId(), processAction);
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
                final FormAction processAction = new FormAction(ActionType.SET_ATTACHMENT, null, null, IFormExpressionsAPI.FIELDID_PREFIX + attachmentWidget.getId(), null, attachment.getName());
                widgetsActions.put(attachmentWidget.getId(), processAction);
            }
            processWidgets.add(attachmentWidget);
        }
        Collections.sort(processWidgets);
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
    public String getPageLabel(final String pageId) throws InvalidFormDefinitionException {
        final int nbOfPages = getNbOfPages();
        String pageLabelComplement = null;
        if (nbOfPages > 1) {
            pageLabelComplement = " : page " + (Integer.parseInt(pageId) + 1) + " / " + nbOfPages;
        } else {
            pageLabelComplement = "";
        }
        if (processLabel != null && processLabel.length() != 0) {
            return "#" + toUpperCaseFirstLetter(processLabel + pageLabelComplement);
        } else {
            return "#" + toUpperCaseFirstLetter(processName + pageLabelComplement);
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
    public String getConfirmationTemplate() {
        return  DefaultFormsPropertiesFactory.getDefaultFormProperties().getPageConfirmationTemplate();
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
    public List<FormValidator> getPageValidators(final String pageId) throws InvalidFormDefinitionException {
        return new ArrayList<FormValidator>();
    }

    /**
     * {@inheritDoc} 
     */
    public List<FormWidget> getPageWidgets(final String pageId) throws InvalidFormDefinitionException {
        if (nbOfPages == -1) {
            nbOfPages = getNbOfPages();
        }
        final List<FormWidget> widgets =  engineWidgetBuilder.getPageWidgets(pageId, nbOfPages, processWidgets, processName, isEditMode);
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
    public boolean isHTMLAllowedInLabel(final String pageId) {
        return false;
    }

    /**
     * @return the number of pages
     */
    protected int getNbOfPages() {
        final DefaultFormsProperties defaultProperties =  DefaultFormsPropertiesFactory.getDefaultFormProperties();
        if (processWidgets.size() == 0) {
            return 0;
        } else if (processWidgets.size() % defaultProperties.getMaxWigdetPerPage() == 0) {
            return processWidgets.size() / defaultProperties.getMaxWigdetPerPage();
        } else {
            return processWidgets.size() / defaultProperties.getMaxWigdetPerPage() + 1;
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
