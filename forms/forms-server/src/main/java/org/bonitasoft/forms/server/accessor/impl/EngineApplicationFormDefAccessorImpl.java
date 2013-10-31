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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormType;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.server.accessor.DefaultFormsProperties;
import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.accessor.IApplicationFormDefAccessor;
import org.bonitasoft.forms.server.accessor.widget.IEngineWidgetBuilder;
import org.bonitasoft.forms.server.accessor.widget.WidgetBuilderFactory;
import org.bonitasoft.forms.server.api.IFormExpressionsAPI;
import org.bonitasoft.forms.server.exception.ApplicationFormDefinitionNotFoundException;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.bonitasoft.forms.server.exception.NotHandledTypeException;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
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
 * Implementation of {@link IApplicationFormDefAccessor} allowing to generate the application config from the engine
 * 
 * @author Anthony Birembaut, Haojie Yuan
 */
public class EngineApplicationFormDefAccessorImpl implements IApplicationFormDefAccessor {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(EngineApplicationFormDefAccessorImpl.class.getName());

    /**
     * indicates whether the page has to be displayed in edit mode (for a ready task) or not (view mode)
     */
    protected boolean isEditMode;

    /**
     * The application name
     */
    protected String applicationName;

    /**
     * the activity Name
     */
    protected String activityName;

    /**
     * The application label
     */
    protected String applicationLabel;

    /**
     * The process definition UUID of the process to which this instance is associated
     */
    private final ProcessDefinitionUUID processDefinitionUUID;

    /**
     * The activity definition
     */
    protected ActivityDefinition activityDefinition;

    /**
     * Accessor used to create the widgets and validators from a process definition retrieved from the engine
     */
    protected IEngineWidgetBuilder engineWidgetBuilder = WidgetBuilderFactory.getEngineWidgetBuilder();

    /**
     * the action for each widget
     */
    protected Map<String, FormAction> widgetsActions = new HashMap<String, FormAction>();

    /**
     * Widget data objects for the application
     */
    final List<FormWidget> applicationWidgets = new ArrayList<FormWidget>();

    /**
     * the pages widgets
     */
    protected Map<String, List<String>> pagesWidgets = new HashMap<String, List<String>>();

    /**
     * Nb of pages in the form
     */
    protected int nbOfPages = -1;

    /**
     * the type of process variable
     */
    public static final String PROCESS_VARIABLE = "PROCESS_VARIABLE";

    /**
     * the type of activity variable
     */
    public static final String ACTIVITY_VARIABLE = "ACTIVITY_VARIABLE";

    /**
     * undefined variable type
     */
    public static final String UNDEFINED = "UNDEFINED";

    protected boolean isRecap = false;

    /**
     * Default constructor.
     * 
     * @param processDefinitionUUID
     * @param activityName
     * @param includeProcessVariables
     * @param isEditMode
     */
    public EngineApplicationFormDefAccessorImpl(final ProcessDefinitionUUID processDefinitionUUID, final String activityName, final boolean includeProcessVariables, final boolean isEditMode, final boolean isRecap) {
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        this.activityName = activityName;
        this.isEditMode = isEditMode;
        this.processDefinitionUUID = processDefinitionUUID;
        this.applicationName = processDefinitionUUID.getProcessName();
        this.isRecap = isRecap;

        try {
            Set<DataFieldDefinition> processDataFields = new HashSet<DataFieldDefinition>();
            Set<AttachmentDefinition> attachments = new HashSet<AttachmentDefinition>();
            Set<DataFieldDefinition> processDataFieldSet = new TreeSet<DataFieldDefinition>(new Comparator<DataFieldDefinition>() {

                @Override
                public int compare(DataFieldDefinition o1, DataFieldDefinition o2) {
                    return o1.getLabel().compareTo(o2.getLabel());
                }
                
            });
            
            if (activityName == null) {
                processDataFields = queryDefinitionAPI.getProcessDataFields(processDefinitionUUID);
                processDataFieldSet.addAll(processDataFields);

                attachments = queryDefinitionAPI.getAttachmentDefinitions(processDefinitionUUID);
                createWidgets(processDataFieldSet, attachments, isEditMode);
            } else {
                
                if (includeProcessVariables) {
                    processDataFields = queryDefinitionAPI.getProcessDataFields(processDefinitionUUID);
                    processDataFieldSet.addAll(processDataFields);
                    
                    attachments = queryDefinitionAPI.getAttachmentDefinitions(processDefinitionUUID);
                }
                activityDefinition = queryDefinitionAPI.getProcessActivity(processDefinitionUUID, activityName);
                final Set<DataFieldDefinition> activityDataFields = queryDefinitionAPI.getActivityDataFields(activityDefinition.getUUID());
                createWidgets(processDataFieldSet, attachments, activityDataFields, isEditMode);
            }
        } catch (final ProcessNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Application " + processDefinitionUUID + " not found.", e);
            }
        } catch (ActivityNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Activity " + activityName + " not found for application " + processDefinitionUUID, e);
            }
        } catch (ActivityDefNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Activity " + activityDefinition.getUUID() + " not found.", e);
            }
        }
    }

    /**
     * create the widgets data objects and put it in the applicationWidgets list
     * 
     * @param applicationDataFields
     * @param attachments
     * @param isEditMode
     */
    protected void createWidgets(final Set<DataFieldDefinition> processDataFields, final Set<AttachmentDefinition> attachments, final boolean isEditMode) {
        for (final DataFieldDefinition dataFieldDefinition : processDataFields) {
            try {
                final FormWidget applicationWidget = engineWidgetBuilder.createWidget(dataFieldDefinition, applicationName, isEditMode);
                if (isEditMode) {
                    final FormAction applicationAction = new FormAction(ActionType.SET_VARIABLE, dataFieldDefinition.getName(), PROCESS_VARIABLE, IFormExpressionsAPI.FIELDID_PREFIX + applicationWidget.getId(), null);
                    widgetsActions.put(applicationWidget.getId(), applicationAction);
                }
                applicationWidgets.add(applicationWidget);
            } catch (final NotHandledTypeException e) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, e.getMessage());
                }
            }
        }
        for (final AttachmentDefinition attachment : attachments) {
            final FormWidget attachmentWidget = engineWidgetBuilder.createWidget(attachment, applicationName, isEditMode);
            if (isEditMode) {
                final FormAction applicationAction = new FormAction(ActionType.SET_ATTACHMENT, null, null, IFormExpressionsAPI.FIELDID_PREFIX + attachmentWidget.getId(), null, attachment.getName());
                widgetsActions.put(attachmentWidget.getId(), applicationAction);
            }
            applicationWidgets.add(attachmentWidget);
        }
        Collections.sort(applicationWidgets);
    }

    /**
     * create the widgets data objects and put it in the activityWidgets list
     * 
     * @param applicationDataFields
     * @param attachments
     * @param activityDataFields
     * @param isEditMode
     */
    protected void createWidgets(final Set<DataFieldDefinition> applicationDataFields, final Set<AttachmentDefinition> attachments, final Set<DataFieldDefinition> activityDataFields, final boolean isEditMode) {
        applicationName = activityName;
        for (final DataFieldDefinition dataFieldDefinition : applicationDataFields) {
            try {
                final FormWidget applicationWidget = engineWidgetBuilder.createWidget(dataFieldDefinition, applicationName, isEditMode);
                if (isEditMode) {
                    final FormAction applicationAction = new FormAction(ActionType.SET_VARIABLE, dataFieldDefinition.getName(), PROCESS_VARIABLE, IFormExpressionsAPI.FIELDID_PREFIX + applicationWidget.getId(), null);
                    widgetsActions.put(applicationWidget.getId(), applicationAction);
                }
                applicationWidgets.add(applicationWidget);
            } catch (final NotHandledTypeException e) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, e.getMessage());
                }
            }
        }
        for (final AttachmentDefinition attachment : attachments) {
            final FormWidget attachmentWidget = engineWidgetBuilder.createWidget(attachment, applicationName, isEditMode);
            if (isEditMode) {
                final FormAction applicationAction = new FormAction(ActionType.SET_ATTACHMENT, null, null, IFormExpressionsAPI.FIELDID_PREFIX + attachmentWidget.getId(), null, attachment.getName());
                widgetsActions.put(attachmentWidget.getId(), applicationAction);
            }
            applicationWidgets.add(attachmentWidget);
        }
        Collections.sort(applicationWidgets);
        final List<FormWidget> taskWidgets = new ArrayList<FormWidget>();
        for (final DataFieldDefinition dataFieldDefinition : activityDataFields) {
            try {
                final FormWidget taskWidget = engineWidgetBuilder.createWidget(dataFieldDefinition, activityName, isEditMode);
                if (isEditMode) {
                    final FormAction activityAction = new FormAction(ActionType.SET_VARIABLE, dataFieldDefinition.getName(), ACTIVITY_VARIABLE, IFormExpressionsAPI.FIELDID_PREFIX + taskWidget.getId(), null);
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
        applicationWidgets.addAll(taskWidgets);
    }

    /**
     * {@inheritDoc}
     */
    public String getFormPermissions() {
        if (activityName == null) {
            return FormServiceProviderUtil.PROCESS_UUID + "#" + processDefinitionUUID.getValue();
        } else {
            return FormServiceProviderUtil.ACTIVITY_UUID + "#" + activityDefinition.getUUID().getValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getFirstPageExpression() {
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
    public String getNextPageExpression(String pageId) throws InvalidFormDefinitionException {
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
    public String getFormPageLayout(String pageId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationVersion() {
        return processDefinitionUUID.getProcessVersion();
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * {@inheritDoc}
     */
    public List<FormWidget> getPageWidgets(String pageId) throws InvalidFormDefinitionException {
        if (nbOfPages == -1) {
            nbOfPages = getNbOfPages();
        }
        final List<FormWidget> widgets = engineWidgetBuilder.getPageWidgets(pageId, nbOfPages, applicationWidgets, applicationName, isEditMode);
        final List<String> widgetIds = new ArrayList<String>();
        for (final FormWidget formWidget : widgets) {
            widgetIds.add(formWidget.getId());
        }
        pagesWidgets.put(pageId, widgetIds);
        return widgets;
    }

    /**
     * @return the number of pages
     */
    protected int getNbOfPages() {
        final DefaultFormsProperties defaultProperties = DefaultFormsPropertiesFactory.getDefaultFormProperties();
        if (activityName == null) {
            if (applicationWidgets.size() == 0) {
                return 0;
            } else if (applicationWidgets.size() % defaultProperties.getMaxWigdetPerPage() == 0) {
                return applicationWidgets.size() / defaultProperties.getMaxWigdetPerPage();
            } else {
                return applicationWidgets.size() / defaultProperties.getMaxWigdetPerPage() + 1;
            }
        } else {
            if (applicationWidgets.size() != 0 && applicationWidgets.size() % defaultProperties.getMaxWigdetPerPage() == 0) {
                return applicationWidgets.size() / defaultProperties.getMaxWigdetPerPage();
            } else {
                return applicationWidgets.size() / defaultProperties.getMaxWigdetPerPage() + 1;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> getPageValidators(String pageId) throws InvalidFormDefinitionException {
        return new ArrayList<FormValidator>();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws ProcessNotFoundException
     */
    public String getPageLabel(String pageId) throws InvalidFormDefinitionException {

        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        try {
            applicationLabel = queryDefinitionAPI.getProcess(processDefinitionUUID).getLabel();
        } catch (ProcessNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Application " + processDefinitionUUID + " not found.", e);
            }
        }
        final int nbOfPages = getNbOfPages();
        String pageLabelComplement = null;
        if (nbOfPages > 1) {
            pageLabelComplement = " : page " + (Integer.parseInt(pageId) + 1) + " / " + nbOfPages;
        } else {
            pageLabelComplement = "";
        }
        if (isRecap) {
            if (applicationLabel != null && applicationLabel.length() != 0) {
                return "Current state : " + toUpperCaseFirstLetter(applicationLabel + pageLabelComplement);
            } else {
                return "Current state : " + toUpperCaseFirstLetter(applicationName + pageLabelComplement);
            }
        } else if (activityName == null) {
            if (applicationLabel != null && applicationLabel.length() != 0) {
                return "#" + toUpperCaseFirstLetter(applicationLabel + pageLabelComplement);
            } else {
                return "#" + toUpperCaseFirstLetter(applicationName + pageLabelComplement);
            }
        } else {
            final String activityName = activityDefinition.getName();
            final String activityLabel = activityDefinition.getLabel();
            if (activityLabel != null && activityLabel.length() != 0) {
                return toUpperCaseFirstLetter(activityLabel + pageLabelComplement);
            } else {
                return toUpperCaseFirstLetter(activityName + pageLabelComplement);
            }
        }
    }

    /**
     * put set the fisrt letter of a label to uppercase
     * 
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
    public boolean isHTMLAllowedInLabel(String pageId) throws InvalidFormDefinitionException {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public List<TransientData> getTransientData() throws InvalidFormDefinitionException {
        return new ArrayList<TransientData>();
    }

    /**
     * {@inheritDoc}
     */
    public List<FormAction> getActions(String pageId) throws InvalidFormDefinitionException, ApplicationFormDefinitionNotFoundException {
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
    public String getConfirmationLayout() {
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
    public String getNextForm() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public FormType getFormType() throws InvalidFormDefinitionException {
        if (isEditMode) {
            return FormType.entry;
        } else {
            return FormType.view;
        }
    }

}
