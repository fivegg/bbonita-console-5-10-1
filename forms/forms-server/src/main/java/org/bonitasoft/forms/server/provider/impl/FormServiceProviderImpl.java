/**
 * Copyright (C) 2011 BonitaSoft S.A.
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
package org.bonitasoft.forms.server.provider.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.common.application.ApplicationResourcesUtils;
import org.bonitasoft.console.common.application.ApplicationURLUtils;
import org.bonitasoft.console.common.exception.NoCredentialsInSessionException;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.forms.client.model.ActivityEditState;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormURLComponents;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.exception.CanceledFormException;
import org.bonitasoft.forms.client.model.exception.ForbiddenApplicationAccessException;
import org.bonitasoft.forms.client.model.exception.ForbiddenFormAccessException;
import org.bonitasoft.forms.client.model.exception.FormAlreadySubmittedException;
import org.bonitasoft.forms.client.model.exception.FormInErrorException;
import org.bonitasoft.forms.client.model.exception.IllegalActivityTypeException;
import org.bonitasoft.forms.client.model.exception.MigrationProductVersionNotIdenticalException;
import org.bonitasoft.forms.client.model.exception.SkippedFormException;
import org.bonitasoft.forms.client.model.exception.SuspendedFormException;
import org.bonitasoft.forms.server.accessor.FormDefAccessorFactory;
import org.bonitasoft.forms.server.accessor.IApplicationConfigDefAccessor;
import org.bonitasoft.forms.server.accessor.IApplicationFormDefAccessor;
import org.bonitasoft.forms.server.accessor.impl.EngineApplicationConfigDefAccessorImpl;
import org.bonitasoft.forms.server.accessor.impl.XMLApplicationConfigDefAccessorImpl;
import org.bonitasoft.forms.server.accessor.impl.util.FormCacheUtilFactory;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocumentBuilderFactory;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormValidationAPI;
import org.bonitasoft.forms.server.api.IFormWorkflowAPI;
import org.bonitasoft.forms.server.builder.impl.FormBuilderImpl;
import org.bonitasoft.forms.server.exception.ApplicationFormDefinitionNotFoundException;
import org.bonitasoft.forms.server.exception.FileTooBigException;
import org.bonitasoft.forms.server.exception.FormNotFoundException;
import org.bonitasoft.forms.server.exception.FormValidationException;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.bonitasoft.forms.server.provider.FormServiceProvider;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
import org.ow2.bonita.facade.exception.ActivityDefNotFoundException;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 * Implementation of FormServiceProvider based on Bonita execution engine
 * 
 * @author QiXiang Zhang, Anthony Birembaut, Haojie Yuan
 * 
 */
public class FormServiceProviderImpl implements FormServiceProvider {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(FormServiceProviderImpl.class.getName());

    protected static SimpleDateFormat DATE_FORMAT;

    static {
        if (LOGGER.isLoggable(Level.FINER)) {
            DATE_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
        }
    }

    /**
     * execute actions mode : if true indicates that this is a redirection request and the actions should be executed
     */
    public static final String EXECUTE_ACTIONS_PARAM = "executeActions";

    /**
     * 
     * Default constructor.
     */
    public FormServiceProviderImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public FormDocument getFormDefinitionDocument(final Map<String, Object> context) throws IOException, InvalidFormDefinitionException, FormNotFoundException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getFormDefinitionDocument - start");
        }
        final ProcessDefinitionUUID processDefinitionUUID = getProcessDefinitionUUID(context);
        final Locale locale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
        FormDocument formDefinitionDocument = null;
        Date processDeployementDate = null;
        try {
            processDeployementDate = getDeployementDate(processDefinitionUUID);
            String localeString = null;
            if (locale != null) {
                localeString = locale.getLanguage();
            }
            formDefinitionDocument = FormDocumentBuilderFactory.getFormDocumentBuilder(processDefinitionUUID, localeString, processDeployementDate).getDocument();
        } catch (final ProcessNotFoundException e) {
            if (processDefinitionUUID != null) {
                final String message = "Cannot find a process with UUID " + processDefinitionUUID;
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, message, e);
                }
                throw new FormNotFoundException(message);
            }
        } catch (final FileNotFoundException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "No form definition descriptor was found for process " + processDefinitionUUID, e);
            }
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getFormDefinitionDocument - end");
        }
        return formDefinitionDocument;
    }

    /**
     * Return the process definition UUID based on the context map
     * 
     * @param context Map of context
     * @return the {@link ProcessDefinitionUUID}
     */
    protected ProcessDefinitionUUID getProcessDefinitionUUID(final Map<String, Object> context) {

        @SuppressWarnings("unchecked")
        Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
        ProcessDefinitionUUID processDefinitionUUID = null;
        if (urlContext != null) {
            if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
            } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
                final ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString());
                processDefinitionUUID = processInstanceUUID.getProcessDefinitionUUID();
            } else if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                final ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                processDefinitionUUID = activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID();
            } else {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "The URL context does not contain any BPM entity parameter. Unable to retrive the process definition UUID.");
                }
            }
        } else {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "The URL context is null. Unable to retrive the process definition UUID.");
            }
        }
        return processDefinitionUUID;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAllowed(final String formId, final String permissions, final String productVersion, final String migrationProductVersion, final Map<String, Object> context, final boolean isFormPermissions) throws ForbiddenFormAccessException,
            SuspendedFormException, CanceledFormException, FormNotFoundException, FormAlreadySubmittedException, ForbiddenApplicationAccessException, FormInErrorException, MigrationProductVersionNotIdenticalException, NoCredentialsInSessionException,
            SkippedFormException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - isAllowed - start");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
        final User user = (User) context.get(FormServiceProviderUtil.USER);
        if (user == null) {
            final String message = "Can't find the user.";
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, message);
            }
            throw new NoCredentialsInSessionException(message);
        }
        final String currentProductVersion = FormBuilderImpl.PRODUCT_VERSION;
        if (productVersion != null) {
            if ((migrationProductVersion == null && !currentProductVersion.split("-")[0].equals(productVersion.split("-")[0])) || (migrationProductVersion != null && !currentProductVersion.split("-")[0].equals(migrationProductVersion.split("-")[0]))) {
                final String message = "The migration product version not identical with current product version.";
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, message);
                }
                throw new MigrationProductVersionNotIdenticalException(message);
            }
        }
        if (permissions != null) {
            final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
            String uuidType = permissions.split("#")[0];
            if (FormServiceProviderUtil.ACTIVITY_UUID.equals(uuidType)) {
                String activityDefinitionUUIDStr = permissions.split("#")[1];
                if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                    ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                    if (!activityDefinitionUUIDStr.equals(activityInstanceUUID.getActivityDefinitionUUID().getValue())) {
                        final String message = "The task required is not an instance of activity " + activityDefinitionUUIDStr;
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, message);
                        }
                        throw new ForbiddenFormAccessException(message);
                    }
                    if (!user.isAdmin() && !user.isAllowed(RuleType.ACTIVITY_READ, activityInstanceUUID.getActivityDefinitionUUID().getValue())) {
                        throw new ForbiddenFormAccessException();
                    }
                    ActivityEditState activityEditState = null;
                    try {
                        activityEditState = workflowAPI.getTaskEditState(activityInstanceUUID);
                    } catch (final ActivityNotFoundException e) {
                        final String message = "The activity instance with UUID " + activityInstanceUUID + " does not exist!";
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, message, e);
                        }
                        throw new FormNotFoundException(message);
                    }
                    ProcessInstanceUUID processInstanceUUID = activityInstanceUUID.getProcessInstanceUUID();
                    final Set<String> involvedUsers;
                    try {
                        involvedUsers = workflowAPI.getProcessInvolvedUsers(processInstanceUUID);
                    } catch (final InstanceNotFoundException e) {
                        final String message = "The process instance " + processInstanceUUID + " does not exist!";
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, message, e);
                        }
                        throw new FormNotFoundException(message);
                    }
                    if (ActivityEditState.SUSPENDED.equals(activityEditState)) {
                        throw new SuspendedFormException();
                    } else if (ActivityEditState.CANCELED.equals(activityEditState)) {
                        throw new CanceledFormException();
                    } else if (ActivityEditState.FAILED.equals(activityEditState)) {
                        throw new FormInErrorException();
                    } else if (ActivityEditState.SKIPPED.equals(activityEditState)) {
                        throw new SkippedFormException();
                    } else if (ActivityEditState.NOT_EDITABLE.equals(activityEditState)) {
                        if (!user.isAdmin() && !(involvedUsers.contains(user.getUsername()) && user.isAllowed(RuleType.ACTIVITY_DETAILS_READ, activityInstanceUUID.getValue()))) {
                            throw new ForbiddenFormAccessException();
                        }
                        String formType = getFormType(formId);
                        if (FormServiceProviderUtil.ENTRY_FORM_TYPE.equals(formType)) {
                            final String message = "The activity instance with UUID " + activityInstanceUUID + " cannot be executed anymore. It's either finished or aborted";
                            if (LOGGER.isLoggable(Level.INFO)) {
                                LOGGER.log(Level.INFO, message);
                            }
                            throw new FormAlreadySubmittedException(message);
                        }
                    } else {
                        try {
                            if (!workflowAPI.isTaskInUserTaskList(activityInstanceUUID) && !user.isAdmin() && !user.isAllowed(RuleType.PROCESS_MANAGE, processInstanceUUID.getProcessDefinitionUUID().getValue())) {
                                throw new ForbiddenFormAccessException();
                            }
                        } catch (final TaskNotFoundException e) {
                            final String message = "The activity instance with UUID " + activityInstanceUUID + " does not exist!";
                            if (LOGGER.isLoggable(Level.INFO)) {
                                LOGGER.log(Level.INFO, message, e);
                            }
                            throw new FormNotFoundException(message);
                        }
                    }
                } else {
                    final String message = "A task parameter is required to display the form for activity " + activityDefinitionUUIDStr;
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, message);
                    }
                    throw new ForbiddenFormAccessException(message);
                }
            } else if (FormServiceProviderUtil.PROCESS_UUID.equals(uuidType)) {
                String processDefinitionUUIDStr = permissions.split("#")[1];
                if (!user.isAllowed(RuleType.PROCESS_READ, processDefinitionUUIDStr) && !user.isAllowed(RuleType.PROCESS_MANAGE, processDefinitionUUIDStr)) {
                    final String message = "An attempt was made by user " + user.getUsername() + " to access process " + processDefinitionUUIDStr;
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, message);
                    }
                    throw new ForbiddenApplicationAccessException(message);
                }
                if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                    ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                    if (!processDefinitionUUIDStr.equals(activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID().getValue())) {
                        final String message = "The task required is not an instance of an activity of process" + processDefinitionUUIDStr;
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, message);
                        }
                        throw new ForbiddenFormAccessException(message);
                    }
                } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
                    ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString());
                    if (!processDefinitionUUIDStr.equals(processInstanceUUID.getProcessDefinitionUUID().getValue())) {
                        final String message = "The instance required is not an instance of process" + processDefinitionUUIDStr;
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, message);
                        }
                        throw new ForbiddenFormAccessException(message);
                    }
                    if (isFormPermissions) {
                        final Set<String> involvedUsers;
                        try {
                            involvedUsers = workflowAPI.getProcessInvolvedUsers(processInstanceUUID);
                        } catch (final InstanceNotFoundException e) {
                            final String message = "The process instance " + processInstanceUUID + " does not exist!";
                            if (LOGGER.isLoggable(Level.INFO)) {
                                LOGGER.log(Level.INFO, message, e);
                            }
                            throw new FormNotFoundException(message);
                        }
                        if (!involvedUsers.contains(user.getUsername()) && !user.isAdmin() && !user.isAllowed(RuleType.PROCESS_MANAGE, processInstanceUUID.getProcessDefinitionUUID().getValue())) {
                            throw new ForbiddenFormAccessException();
                        }
                    }
                } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                    ProcessDefinitionUUID processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
                    if (!processDefinitionUUIDStr.equals(processDefinitionUUID.getValue())) {
                        final String message = "The process required does not match the form required" + processDefinitionUUIDStr;
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, message);
                        }
                        throw new ForbiddenFormAccessException(message);
                    }
                    final String formType = getFormType(formId);
                    if (FormServiceProviderUtil.ENTRY_FORM_TYPE.equals(formType) && !user.isAllowed(RuleType.PROCESS_START, processDefinitionUUIDStr)) {
                        final String message = "An attempt was made by user " + user.getUsername() + " to start an instance of process " + processDefinitionUUIDStr;
                        if (LOGGER.isLoggable(Level.INFO)) {
                            LOGGER.log(Level.INFO, message);
                        }
                        throw new ForbiddenFormAccessException(message);
                    }
                }
            }
        } else {
            final String message = "The permissions are undefined for form " + formId;
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, message);
            }
            throw new ForbiddenApplicationAccessException(message);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - isAllowed - end");
        }
        return true;
    }

    /**
     * Parse the formId to extract the form type
     * 
     * @param formIdn the form ID
     * @return "entry", "view" or "recap"
     * @throws FormNotFoundException
     */
    protected String getFormType(final String formId) throws FormNotFoundException {
        String formType = null;
        if (formId == null) {
            final String message = "the Form ID is null. The parameter 'form' is probably missing from the URL.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message);
            }
            throw new FormNotFoundException(message);
        } else {
            String[] formIdComponents = formId.split("\\" + FormServiceProviderUtil.FORM_ID_SEPARATOR);
            if (formIdComponents.length > 1) {
                formType = formIdComponents[1];
            } else {
                final String message = "Wrong FormId " + formId + ". It doesn't contain the form type.";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, message);
                }
                throw new IllegalArgumentException(message);
            }
        }
        return formType;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object resolveExpression(String expression, Map<String, Object> context) throws FormNotFoundException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - resolveExpression - start \nexpression: " + expression);
        }
        ActivityInstanceUUID activityInstanceUUID = null;
        ProcessDefinitionUUID processDefinitionUUID = null;
        ProcessInstanceUUID processInstanceUUID = null;
        boolean isCurrentValue = false;
        final Locale locale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
        if (context.get(FormServiceProviderUtil.IS_CURRENT_VALUE) != null) {
            isCurrentValue = (Boolean) context.get(FormServiceProviderUtil.IS_CURRENT_VALUE);
        }
        final Map<String, Object> transientDataContext = (Map<String, Object>) context.get(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT);
        final Map<String, FormFieldValue> fieldValues = (Map<String, FormFieldValue>) context.get(FormServiceProviderUtil.FIELD_VALUES);
        Object result = null;
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        try {
            Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
            if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                if (fieldValues != null) {
                    if (transientDataContext != null) {
                        result = workflowAPI.getFieldValue(activityInstanceUUID, expression, fieldValues, locale, isCurrentValue, transientDataContext);
                    } else {
                        result = workflowAPI.getFieldValue(activityInstanceUUID, expression, fieldValues, locale, isCurrentValue);
                    }
                } else {
                    if (transientDataContext != null) {
                        result = workflowAPI.getFieldValue(activityInstanceUUID, expression, locale, isCurrentValue, transientDataContext);
                    } else {
                        result = workflowAPI.getFieldValue(activityInstanceUUID, expression, locale, isCurrentValue);
                    }
                }
            } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
                processInstanceUUID = new ProcessInstanceUUID(urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString());
                if (fieldValues != null) {
                    if (transientDataContext != null) {
                        result = workflowAPI.getFieldValue(processInstanceUUID, expression, fieldValues, locale, isCurrentValue, transientDataContext);
                    } else {
                        result = workflowAPI.getFieldValue(processInstanceUUID, expression, fieldValues, locale, isCurrentValue);
                    }
                } else {
                    if (transientDataContext != null) {
                        result = workflowAPI.getFieldValue(processInstanceUUID, expression, locale, isCurrentValue, transientDataContext);
                    } else {
                        result = workflowAPI.getFieldValue(processInstanceUUID, expression, locale, isCurrentValue);
                    }
                }
            } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
                if (fieldValues != null) {
                    if (transientDataContext != null) {
                        result = workflowAPI.getFieldValue(processDefinitionUUID, expression, fieldValues, locale, transientDataContext);
                    } else {
                        result = workflowAPI.getFieldValue(processDefinitionUUID, expression, fieldValues, locale);
                    }
                } else {
                    if (transientDataContext != null) {
                        result = workflowAPI.getFieldValue(processDefinitionUUID, expression, locale, transientDataContext);
                    } else {
                        result = workflowAPI.getFieldValue(processDefinitionUUID, expression, locale);
                    }
                }
            } else {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Unable to resolve expression: " + expression + ". No process entity specified in the context");
                }
            }
        } catch (final InstanceNotFoundException e) {
            final String message = "This processInstanceUUID " + processInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ActivityNotFoundException e) {
            final String message = "This activityInstanceUUID " + activityInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ProcessNotFoundException e) {
            final String message = "This processDefinitionUUID " + processDefinitionUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - resolveExpression - end");
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> resolveExpressions(Map<String, String> expressions, Map<String, Object> context) throws FormNotFoundException {

        if (LOGGER.isLoggable(Level.FINER)) {
            int size = 0;
            if (expressions != null) {
                size = expressions.size();
            }
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - resolveExpressions - start - nb expressions: " + size);
        }
        ActivityInstanceUUID activityInstanceUUID = null;
        ProcessDefinitionUUID processDefinitionUUID = null;
        ProcessInstanceUUID processInstanceUUID = null;
        boolean isCurrentValue = false;
        final Locale locale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
        if (context.get(FormServiceProviderUtil.IS_CURRENT_VALUE) != null) {
            isCurrentValue = (Boolean) context.get(FormServiceProviderUtil.IS_CURRENT_VALUE);
        }
        final Map<String, Object> transientDataContext = (Map<String, Object>) context.get(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT);
        final Map<String, FormFieldValue> fieldValues = (Map<String, FormFieldValue>) context.get(FormServiceProviderUtil.FIELD_VALUES);
        Map<String, Object> results;
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        try {
            Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
            if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                if (fieldValues != null) {
                    if (transientDataContext != null) {
                        results = workflowAPI.getFieldsValues(activityInstanceUUID, expressions, fieldValues, locale, isCurrentValue, transientDataContext);
                    } else {
                        results = workflowAPI.getFieldsValues(activityInstanceUUID, expressions, fieldValues, locale, isCurrentValue);
                    }
                } else {
                    if (transientDataContext != null) {
                        results = workflowAPI.getFieldsValues(activityInstanceUUID, expressions, locale, isCurrentValue, transientDataContext);
                    } else {
                        results = workflowAPI.getFieldsValues(activityInstanceUUID, expressions, locale, isCurrentValue);
                    }
                }
            } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
                processInstanceUUID = new ProcessInstanceUUID(urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString());
                if (fieldValues != null) {
                    if (transientDataContext != null) {
                        results = workflowAPI.getFieldsValues(processInstanceUUID, expressions, fieldValues, locale, isCurrentValue, transientDataContext);
                    } else {
                        results = workflowAPI.getFieldsValues(processInstanceUUID, expressions, fieldValues, locale, isCurrentValue);
                    }
                } else {
                    if (transientDataContext != null) {
                        results = workflowAPI.getFieldsValues(processInstanceUUID, expressions, locale, isCurrentValue, transientDataContext);
                    } else {
                        results = workflowAPI.getFieldsValues(processInstanceUUID, expressions, locale, isCurrentValue);
                    }
                }
            } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
                if (fieldValues != null) {
                    if (transientDataContext != null) {
                        results = workflowAPI.getFieldsValues(processDefinitionUUID, expressions, fieldValues, locale, transientDataContext);
                    } else {
                        results = workflowAPI.getFieldsValues(processDefinitionUUID, expressions, fieldValues, locale);
                    }
                } else {
                    if (transientDataContext != null) {
                        results = workflowAPI.getFieldsValues(processDefinitionUUID, expressions, locale, transientDataContext);
                    } else {
                        results = workflowAPI.getFieldsValues(processDefinitionUUID, expressions, locale);
                    }
                }
            } else {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Unable to resolve expressions: " + expressions + ". No process entity specified in the context");
                }
                results = new HashMap<String, Object>();
            }
        } catch (final InstanceNotFoundException e) {
            final String message = "This processInstanceUUID " + processInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ActivityNotFoundException e) {
            final String message = "This activityInstanceUUID " + activityInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ProcessNotFoundException e) {
            final String message = "This processDefinitionUUID " + processDefinitionUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - resolveExpressions - end");
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> executeActions(List<FormAction> actions, Map<String, Object> context) throws FileTooBigException, FormNotFoundException, FormAlreadySubmittedException, Exception {

        if (LOGGER.isLoggable(Level.FINER)) {
            int size = 0;
            if (actions != null) {
                size = actions.size();
            }
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - ###executeActions - start - nb actions: " + size);
        }
        ActivityInstanceUUID activityInstanceUUID = null;
        ProcessInstanceUUID processInstanceUUID = null;
        ProcessDefinitionUUID processDefinitionUUID = null;
        String submitButtonId = null;
        Map<String, Object> urlContext = new HashMap<String, Object>();
        if (context.get(FormServiceProviderUtil.URL_CONTEXT) != null) {
            urlContext.putAll((Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT));
        }
        final Locale locale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
        final Map<String, FormFieldValue> fieldValues = (Map<String, FormFieldValue>) context.get(FormServiceProviderUtil.FIELD_VALUES);
        final Map<String, Object> transientDataContext = (Map<String, Object>) context.get(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT);
        if (context.get(FormServiceProviderUtil.SUBMIT_BUTTON_ID) != null) {
            submitButtonId = (String) context.get(FormServiceProviderUtil.SUBMIT_BUTTON_ID);
        }
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        try {
            if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
                ProcessInstanceUUID newProcessInstanceUUID = workflowAPI.executeActionsAndStartInstance(processDefinitionUUID, fieldValues, actions, locale, submitButtonId, transientDataContext);
                urlContext.remove(FormServiceProviderUtil.PROCESS_UUID);
                urlContext.remove(FormServiceProviderUtil.TASK_UUID);
                urlContext.put(FormServiceProviderUtil.INSTANCE_UUID, newProcessInstanceUUID.getValue());
            } else if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                processInstanceUUID = activityInstanceUUID.getProcessInstanceUUID();
                processDefinitionUUID = processInstanceUUID.getProcessDefinitionUUID();
                boolean executeActions = false;
                if (urlContext.containsKey(EXECUTE_ACTIONS_PARAM)) {
                    final String executeActionsStr = urlContext.get(EXECUTE_ACTIONS_PARAM).toString();
                    executeActions = Boolean.parseBoolean(executeActionsStr);
                }
                if (submitButtonId != null || executeActions) {
                    final boolean isTask = workflowAPI.isTask(activityInstanceUUID);
                    final boolean isTaskOver = workflowAPI.isTaskOver(activityInstanceUUID);
                    final boolean isEditMode = !isTaskOver && isTask;
                    if (isEditMode) {
                        workflowAPI.executeActionsAndTerminate(activityInstanceUUID, fieldValues, actions, locale, submitButtonId, transientDataContext);
                    } else {
                        throw new FormAlreadySubmittedException();
                    }
                } else {
                    workflowAPI.executeActions(activityInstanceUUID, fieldValues, actions, locale);
                }
            }
        } catch (final FormAlreadySubmittedException e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "The form cannot be submitted anymore.", e);
            }
            throw e;
        } catch (final InstanceNotFoundException e) {
            final String message = "The process instance with UUID " + processInstanceUUID + " cannot be found!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final TaskNotFoundException e) {
            final String message = "The activity instance with UUID " + activityInstanceUUID + " cannot be found!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (ActivityNotFoundException e) {
            final String message = "The activity instance with UUID " + activityInstanceUUID + " cannot be found!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (ActivityDefNotFoundException e) {
            final String message = "The activity definition of activity instance with UUID " + activityInstanceUUID + " cannot be found!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ProcessNotFoundException e) {
            final String message = "The process definition with UUID " + processDefinitionUUID + " cannot be found!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final FileTooBigException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, e.getMessage(), e);
            }
            throw new FileTooBigException(e.getMessage(), e.getFileName(), e.getMaxSize());
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while executing Actions task", e);
            }
            throw new Exception(e.getMessage(), e);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - executeActions - end");
        }
        return urlContext;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public FormURLComponents getNextFormURLParameters(final String formId, Map<String, Object> context) throws FormNotFoundException {
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getNextFormURLParameters - start");
        }
        final HttpServletRequest request = (HttpServletRequest) context.get(FormServiceProviderUtil.REQUEST);
        Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
        ProcessInstanceUUID processInstanceUUID = null;
        if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
            final ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
            processInstanceUUID = activityInstanceUUID.getProcessInstanceUUID();
        } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
            processInstanceUUID = new ProcessInstanceUUID(urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString());
        }
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        ActivityInstanceUUID nextActivityInstanceUUID = null;
        FormURLComponents urlComponents = null;
        String applicationURL = null;
        try {
            nextActivityInstanceUUID = workflowAPI.getProcessInstanceNextTask(processInstanceUUID);
            if (nextActivityInstanceUUID == null) {
                nextActivityInstanceUUID = workflowAPI.getRelatedProcessesNextTask(processInstanceUUID);
                if (nextActivityInstanceUUID != null) {
                    applicationURL = ApplicationURLUtils.getInstance().getOrSetURLMetaData(request, nextActivityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID());
                }
            }
        } catch (InstanceNotFoundException e) {
            final String message = "The process instance with UUID " + processInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ProcessNotFoundException e) {
            final String message = "The process with UUID " + nextActivityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID() + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }
        if (nextActivityInstanceUUID != null) {
            urlComponents = new FormURLComponents();
            if (applicationURL != null) {
                urlComponents.setApplicationURL(applicationURL);
            }
            final String formID = nextActivityInstanceUUID.getActivityDefinitionUUID().getValue() + FormServiceProviderUtil.FORM_ID_SEPARATOR + FormServiceProviderUtil.ENTRY_FORM_TYPE;
            if (urlContext != null) {
                Map<String, Object> newURLContext = new HashMap<String, Object>(urlContext);
                newURLContext.remove(FormServiceProviderUtil.PROCESS_UUID);
                newURLContext.remove(FormServiceProviderUtil.INSTANCE_UUID);
                newURLContext.put(FormServiceProviderUtil.FORM_ID, formID);
                newURLContext.put(FormServiceProviderUtil.TASK_UUID, nextActivityInstanceUUID.getValue());
                newURLContext.put(FormServiceProviderUtil.THEME, nextActivityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID().getValue());
                urlComponents.setUrlContext(newURLContext);
            }
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getNextFormURLParameters - end");
        }
        return urlComponents;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getAttributesToInsert(Map<String, Object> context) throws FormNotFoundException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getAttributesToInsert - start");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
        ActivityInstanceUUID activityInstanceUUID = null;
        if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
            activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
        }
        final Locale locale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        Map<String, String> attributes = null;
        try {
            if (activityInstanceUUID != null) {
                attributes = workflowAPI.getAttributes(activityInstanceUUID, locale);
            }
        } catch (final ActivityNotFoundException e) {
            final String message = "This activityInstanceUUID " + activityInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getAttributesToInsert - end");
        }
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public List<FormValidator> validateField(final List<FormValidator> validators, final String fieldId, final FormFieldValue fieldValue, final Map<String, Object> context) throws FormValidationException, FormNotFoundException {

        return validateField(validators, fieldId, fieldValue, null, context);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<FormValidator> validateField(final List<FormValidator> validators, final String fieldId, final FormFieldValue fieldValue, final String submitButtonId, final Map<String, Object> context) throws FormValidationException, FormNotFoundException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - validateField - start");
        }
        ActivityInstanceUUID activityInstanceUUID = null;
        ProcessDefinitionUUID processDefinitionUUID = null;
        ProcessInstanceUUID processInstanceUUID = null;
        final Locale locale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
        final IFormValidationAPI validationAPI = FormAPIFactory.getFormValidationAPI();
        final Map<String, Object> transientDataContext = (Map<String, Object>) context.get(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT);
        List<FormValidator> nonCompliantFieldValidators = null;
        try {
            Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
            if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                nonCompliantFieldValidators = validationAPI.validateField(activityInstanceUUID, validators, fieldId, fieldValue, submitButtonId, locale, transientDataContext);
            } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
                nonCompliantFieldValidators = validationAPI.validateField(processDefinitionUUID, validators, fieldId, fieldValue, submitButtonId, locale, transientDataContext);
            } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
                processInstanceUUID = new ProcessInstanceUUID(urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString());
                nonCompliantFieldValidators = validationAPI.validateField(processInstanceUUID, validators, fieldId, fieldValue, submitButtonId, locale, transientDataContext);
            }
        } catch (final InstanceNotFoundException e) {
            final String message = "This processInstanceUUID " + processInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ActivityNotFoundException e) {
            final String message = "This activityInstanceUUID " + activityInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ProcessNotFoundException e) {
            final String message = "This processDefinitionUUID " + processDefinitionUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - validateField - end");
        }
        return nonCompliantFieldValidators;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public List<FormValidator> validatePage(final List<FormValidator> validators, final Map<String, FormFieldValue> fields, final Map<String, Object> context) throws FormValidationException, FormNotFoundException {

        return validatePage(validators, fields, null, context);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<FormValidator> validatePage(final List<FormValidator> validators, final Map<String, FormFieldValue> fields, final String submitButtonId, final Map<String, Object> context) throws FormValidationException, FormNotFoundException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - validatePage - start");
        }
        ActivityInstanceUUID activityInstanceUUID = null;
        ProcessDefinitionUUID processDefinitionUUID = null;
        ProcessInstanceUUID processInstanceUUID = null;
        final Locale locale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
        final IFormValidationAPI validationAPI = FormAPIFactory.getFormValidationAPI();
        final Map<String, Object> transientDataContext = (Map<String, Object>) context.get(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT);
        List<FormValidator> nonCompliantFieldValidators = null;
        try {
            Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
            if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                nonCompliantFieldValidators = validationAPI.validatePage(activityInstanceUUID, validators, fields, submitButtonId, locale, transientDataContext);
            } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
                nonCompliantFieldValidators = validationAPI.validatePage(processDefinitionUUID, validators, fields, submitButtonId, locale, transientDataContext);
            } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
                processInstanceUUID = new ProcessInstanceUUID(urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString());
                nonCompliantFieldValidators = validationAPI.validatePage(processInstanceUUID, validators, fields, submitButtonId, locale, transientDataContext);
            }
        } catch (final InstanceNotFoundException e) {
            final String message = "The process instance with UUID " + processInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ActivityNotFoundException e) {
            final String message = "The activity instance with UUID " + activityInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ProcessNotFoundException e) {
            final String message = "The process with UUID " + processDefinitionUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - validatePage - end");
        }
        return nonCompliantFieldValidators;
    }

    /**
     * {@inheritDoc}
     */
    public Date getDeployementDate(final Map<String, Object> context) throws FormNotFoundException, IOException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getDeployementDate - start");
        }
        Date processDeployementDate = null;
        final ProcessDefinitionUUID processDefinitionUUID = getProcessDefinitionUUID(context);
        try {
            processDeployementDate = getDeployementDate(processDefinitionUUID);
        } catch (ProcessNotFoundException e) {
            final String message = "The process with UUID " + processDefinitionUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getDeployementDate - end");
        }
        return processDeployementDate;
    }

    /**
     * Retrieve the process deployementDate
     * 
     * @param processDefinitionUUID the process definition UUID
     * @return a {@link Date}
     * @throws ProcessNotFoundException
     * @throws IOException
     */
    protected Date getDeployementDate(final ProcessDefinitionUUID processDefinitionUUID) throws ProcessNotFoundException, IOException {
        Date processDeployementDate = null;
        if (processDefinitionUUID != null) {
            final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
            processDeployementDate = workflowAPI.getMigrationDate(processDefinitionUUID);
            if (processDeployementDate == null) {
                processDeployementDate = workflowAPI.getProcessDefinitionDate(processDefinitionUUID);
            } else {
                File oldFormsDir = ApplicationResourcesUtils.getApplicationResourceDir(processDefinitionUUID, workflowAPI.getProcessDefinitionDate(processDefinitionUUID));
                if (oldFormsDir.exists()) {
                    if (deleteDir(oldFormsDir)) {
                        LOGGER.info("Delete files success!");
                    }
                    FormCacheUtilFactory.getTenancyFormCacheUtil().clearAll();
                }
            }
            return processDeployementDate;
        }
        return processDeployementDate;
    }

    /**
     * Deletes all files and sub-directories under dir
     * 
     * @param dir the target directory
     * @return true if all deletions were successful; false when deletion fails.
     */
    private static boolean deleteDir(final File dir) {
        if (dir.isDirectory()) {
            final String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * {@inheritDoc}
     */
    public IApplicationConfigDefAccessor getApplicationConfigDefinition(final FormDocument formDefinitionDocument, final Map<String, Object> context) {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getApplicationConfigDefinition - start");
        }
        IApplicationConfigDefAccessor applicationConfigDefAccessor = null;
        if (formDefinitionDocument == null) {
            ProcessDefinitionUUID processDefinitionUUID = getProcessDefinitionUUID(context);
            applicationConfigDefAccessor = new EngineApplicationConfigDefAccessorImpl(processDefinitionUUID);
        } else {
            applicationConfigDefAccessor = new XMLApplicationConfigDefAccessorImpl(formDefinitionDocument);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getApplicationConfigDefinition - end");
        }
        return applicationConfigDefAccessor;
    }

    /**
     * {@inheritDoc}
     */
    public IApplicationFormDefAccessor getApplicationFormDefinition(final String formId, final FormDocument formDefinitionDocument, final Map<String, Object> context) throws ApplicationFormDefinitionNotFoundException, InvalidFormDefinitionException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getApplicationFormDefinition - start");
        }
        IApplicationFormDefAccessor iApplicationDefAccessor = null;
        final Date applicationDeploymentDate = (Date) context.get(FormServiceProviderUtil.APPLICATION_DEPLOYMENT_DATE);
        iApplicationDefAccessor = getApplicationFormDefinition(formId, formDefinitionDocument, applicationDeploymentDate, context);
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getApplicationFormDefinition - end");
        }
        return iApplicationDefAccessor;
    }

    /**
     * Get a Form Definition Accessor object
     * 
     * @param formId the form ID
     * @param formDefinitionDocument
     * @param applicationDeploymentDate
     * @param context the context of URL parameters
     * @return an instance of {@link IApplicationFormDefAccessor}
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws InvalidFormDefinitionException
     */
    protected IApplicationFormDefAccessor getApplicationFormDefinition(final String formId, final FormDocument formDefinitionDocument, final Date applicationDeploymentDate, final Map<String, Object> context)
            throws ApplicationFormDefinitionNotFoundException, InvalidFormDefinitionException {

        IApplicationFormDefAccessor formDefAccessor = null;
        final ProcessDefinitionUUID processDefinitionUUID = getProcessDefinitionUUID(context);
        @SuppressWarnings("unchecked")
        Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
        ActivityInstanceUUID activityInstanceUUID = null;
        String activityName = null;
        if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
            activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
            activityName = activityInstanceUUID.getActivityName();
        }
        String formType = null;
        try {
            formType = getFormType(formId);
        } catch (FormNotFoundException e) {
            throw new ApplicationFormDefinitionNotFoundException(e.getMessage());
        }
        boolean isRecap = false;
        if (FormServiceProviderUtil.RECAP_FORM_TYPE.equals(formType)) {
            isRecap = true;
        }
        boolean isEditMode = false;
        if (FormServiceProviderUtil.ENTRY_FORM_TYPE.equals(formType)) {
            isEditMode = true;
        }
        if (formDefinitionDocument == null) {
            formDefAccessor = FormDefAccessorFactory.getEngineApplicationFormDefAccessor(processDefinitionUUID, activityName, true, isEditMode, isRecap);
        } else {
            try {
                final Locale locale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
                String localeString = null;
                if (locale != null) {
                    localeString = locale.getLanguage();
                }
                formDefAccessor = FormDefAccessorFactory.getXMLApplicationFormDefAccessor(formId, formDefinitionDocument, localeString, applicationDeploymentDate);
            } catch (final ApplicationFormDefinitionNotFoundException e) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "No form definition was found for the form " + formId + ". The forms will be generated using the engine variables.");
                }
                formDefAccessor = FormDefAccessorFactory.getEngineApplicationFormDefAccessor(processDefinitionUUID, activityName, true, isEditMode, isRecap);
            }
        }
        return formDefAccessor;
    }

    /**
     * {@inheritDoc}
     */
    public File getApplicationResourceDir(final Date applicationDeploymentDate, final Map<String, Object> context) throws IOException {
        final ProcessDefinitionUUID processDefinitionUUID = getProcessDefinitionUUID(context);
        return ApplicationResourcesUtils.getApplicationResourceDir(processDefinitionUUID, applicationDeploymentDate);
    }

    /**
     * {@inheritDoc}
     */
    public String getAttachmentFileName(final String attachmentName, final Map<String, Object> context) throws FormNotFoundException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getAttachmentFileName - start");
        }
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        ActivityInstanceUUID activityInstanceUUID = null;
        ProcessDefinitionUUID processDefinitionUUID = null;
        ProcessInstanceUUID processInstanceUUID = null;
        String attachmentFileName = null;
        final boolean isCurrentValue = (Boolean) context.get(FormServiceProviderUtil.IS_CURRENT_VALUE);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
            if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                attachmentFileName = workflowAPI.getAttachmentFileName(activityInstanceUUID, attachmentName, isCurrentValue);
            } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
                attachmentFileName = workflowAPI.getAttachmentFileName(processDefinitionUUID, attachmentName);
            } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
                processInstanceUUID = new ProcessInstanceUUID(urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString());
                attachmentFileName = workflowAPI.getAttachmentFileName(processInstanceUUID, attachmentName, isCurrentValue);
            }
        } catch (final InstanceNotFoundException e) {
            final String message = "This processInstanceUUID " + processInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ActivityNotFoundException e) {
            final String message = "This activityInstanceUUID " + activityInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        } catch (final ProcessNotFoundException e) {
            final String message = "This processDefinitionUUID " + processDefinitionUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getAttachmentFileName - end");
        }
        return attachmentFileName;

    }

    /**
     * {@inheritDoc}
     */
    public boolean isEditMode(final String formID, final Map<String, Object> context) throws FormNotFoundException {

        ActivityInstanceUUID activityInstanceUUID = null;
        boolean isEditMode = false;
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
            if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                final boolean isTask = workflowAPI.isTask(activityInstanceUUID);
                final boolean isTaskOver = workflowAPI.isTaskOver(activityInstanceUUID);
                isEditMode = !isTaskOver && isTask;
            } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                isEditMode = true;
            } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
                isEditMode = false;
            }
        } catch (final ActivityNotFoundException e) {
            final String message = "This activityInstanceUUID " + activityInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }

        return isEditMode;

    }

    /**
     * {@inheritDoc}
     */
    public boolean isCurrentValue(final Map<String, Object> context) throws FormNotFoundException {
        ActivityInstanceUUID activityInstanceUUID = null;
        boolean isCurrentValue = false;
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
            if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
                final boolean isTask = workflowAPI.isTask(activityInstanceUUID);
                final boolean isTaskOver = workflowAPI.isTaskOver(activityInstanceUUID);
                final boolean isEditMode = !isTaskOver && isTask;
                if (isEditMode) {
                    isCurrentValue = true;
                } else {
                    isCurrentValue = false;
                }
            } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
                isCurrentValue = false;
            } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
                if (urlContext.get(FormServiceProviderUtil.RECAP_FORM_TYPE) != null) {
                    boolean isRecap = Boolean.valueOf((String) urlContext.get(FormServiceProviderUtil.RECAP_FORM_TYPE));
                    if (isRecap) {
                        isCurrentValue = true;
                    } else {
                        isCurrentValue = false;
                    }
                } else {
                    isCurrentValue = false;
                }
            }
        } catch (final ActivityNotFoundException e) {
            final String message = "This activityInstanceUUID " + activityInstanceUUID + " does not exist!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FormNotFoundException(message);
        }

        return isCurrentValue;

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> skipForm(String formID, Map<String, Object> context) throws FormNotFoundException, FormAlreadySubmittedException, IllegalActivityTypeException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - skipForm - start");
        }
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        if (context.get(FormServiceProviderUtil.URL_CONTEXT) != null) {
            urlContext.putAll((Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT));
        }
        if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
            ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
            try {
                final boolean isTask = workflowAPI.isTask(activityInstanceUUID);
                final boolean isTaskOver = workflowAPI.isTaskOver(activityInstanceUUID);
                final boolean isEditMode = !isTaskOver && isTask;
                if (isEditMode) {
                    workflowAPI.terminate(activityInstanceUUID);
                    urlContext.remove(FormServiceProviderUtil.PROCESS_UUID);
                    urlContext.remove(FormServiceProviderUtil.INSTANCE_UUID);
                    ActivityInstanceUUID newTaskInstanceUUID = workflowAPI.getAnyTodoListTaskForProcessInstance(activityInstanceUUID.getProcessInstanceUUID());
                    if (newTaskInstanceUUID != null) {
                        String newTaskInstanceUUIDString = newTaskInstanceUUID.getValue();
                        urlContext.put(FormServiceProviderUtil.TASK_UUID, newTaskInstanceUUIDString);
                    }
                } else {
                    throw new FormAlreadySubmittedException();
                }
            } catch (ActivityNotFoundException e) {
                throw new FormNotFoundException(e);
            } catch (TaskNotFoundException e) {
                throw new FormNotFoundException(e);
            } catch (IllegalTaskStateException e) {
                throw new IllegalActivityTypeException(e);
            }
        } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
            ProcessDefinitionUUID processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
            try {
                ProcessInstanceUUID newProcessInstanceUUID = workflowAPI.startInstance(processDefinitionUUID);
                urlContext.remove(FormServiceProviderUtil.PROCESS_UUID);
                urlContext.remove(FormServiceProviderUtil.TASK_UUID);
                urlContext.put(FormServiceProviderUtil.INSTANCE_UUID, newProcessInstanceUUID.getValue());
            } catch (ProcessNotFoundException e) {
                throw new FormNotFoundException(e);
            }
        } else {
            throw new FormNotFoundException("Unable to skip form " + formID + " The process UUID or task UUID are missing from the URL");
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - skipForm - end");
        }
        return urlContext;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public FormURLComponents getAnyTodoListForm(final Map<String, Object> context) throws FormNotFoundException {

        FormURLComponents formURLComponents = null;
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getAnyTodoListForm - start");
        }
        Map<String, Object> urlContext = new HashMap<String, Object>();
        final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
        if (context.get(FormServiceProviderUtil.URL_CONTEXT) != null) {
            urlContext.putAll((Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT));
        }
        ActivityInstanceUUID activityInstanceUUID = null;
        ProcessDefinitionUUID processDefinitionUUID = null;
        if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
            ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString());
            activityInstanceUUID = workflowAPI.getAnyTodoListTaskForProcessInstance(processInstanceUUID);
        } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
            processDefinitionUUID = new ProcessDefinitionUUID(urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString());
            activityInstanceUUID = workflowAPI.getAnyTodoListTaskForProcessDefinition(processDefinitionUUID);
        }
        final String currentFormID = (String) urlContext.get(FormServiceProviderUtil.FORM_ID);
        if (activityInstanceUUID == null && currentFormID != null) {
            formURLComponents = getNextFormURLParameters(currentFormID, urlContext);
            formURLComponents.getUrlContext().remove(FormServiceProviderUtil.TO_DO_LIST);
        } else {
            if (activityInstanceUUID != null) {
                urlContext.remove(FormServiceProviderUtil.PROCESS_UUID);
                urlContext.remove(FormServiceProviderUtil.INSTANCE_UUID);
                urlContext.remove(FormServiceProviderUtil.TO_DO_LIST);
                urlContext.put(FormServiceProviderUtil.THEME, activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID().getValue());
                urlContext.put(FormServiceProviderUtil.TASK_UUID, activityInstanceUUID.getValue());
                urlContext.put(FormServiceProviderUtil.FORM_ID, activityInstanceUUID.getActivityDefinitionUUID().getValue() + FormServiceProviderUtil.FORM_ID_SEPARATOR + FormServiceProviderUtil.ENTRY_FORM_TYPE);
                formURLComponents = new FormURLComponents();
                formURLComponents.setUrlContext(urlContext);
            } else {
                final String message = "Unable to find a task to perform in for the logged in user.";
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, message);
                }
                throw new FormNotFoundException(message);
            }
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getAnyTodoListForm - end");
        }
        return formURLComponents;
    }

    /**
     * {@inheritDoc}
     */
    public ClassLoader getClassloader(final Map<String, Object> context) {
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getClassloader - start");
        }
        final ProcessDefinitionUUID processDefinitionUUID = getProcessDefinitionUUID(context);
        ClassLoader classLoader = ApplicationResourcesUtils.getProcessClassLoader(processDefinitionUUID);
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getClassloader - end");
        }
        return classLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeFormTransientDataContext(HttpSession session, String storageKey, Map<String, Object> transientDataContext, Map<String, Object> context) {
        String id = getInstanceIdToUse(context);
        session.setAttribute(storageKey + id, transientDataContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> retrieveFormTransientDataContext(HttpSession session, String storageKey, Map<String, Object> context) {
        String id = getInstanceIdToUse(context);
        @SuppressWarnings("unchecked")
        Map<String, Object> transientDataContext = (Map<String, Object>) session.getAttribute(storageKey + id);
        if (transientDataContext == null) {
            transientDataContext = new HashMap<String, Object>();
        }
        return transientDataContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFormTransientDataContext(HttpSession session, String storageKey, Map<String, Object> context) {
        String id = getInstanceIdToUse(context);
        session.removeAttribute(storageKey + id);
    }

    private String getInstanceIdToUse(Map<String, Object> context) {
        String id = null;
        @SuppressWarnings("unchecked")
        Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
        if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
            id = urlContext.get(FormServiceProviderUtil.TASK_UUID).toString();
        } else if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) != null) {
            id = urlContext.get(FormServiceProviderUtil.PROCESS_UUID).toString();
        } else if (urlContext.get(FormServiceProviderUtil.INSTANCE_UUID) != null) {
            id = urlContext.get(FormServiceProviderUtil.INSTANCE_UUID).toString();
        }
        return id;
    }

}
