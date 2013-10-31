/**
 * Copyright (C) 2009-2011 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.forms.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.common.exception.NoCredentialsInSessionException;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.servlet.ServletLoginUtils;
import org.bonitasoft.forms.client.model.ApplicationConfig;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldAvailableValue;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.FormURLComponents;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.HtmlTemplate;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.client.model.exception.CanceledFormException;
import org.bonitasoft.forms.client.model.exception.FileTooBigException;
import org.bonitasoft.forms.client.model.exception.ForbiddenApplicationAccessException;
import org.bonitasoft.forms.client.model.exception.ForbiddenFormAccessException;
import org.bonitasoft.forms.client.model.exception.FormAlreadySubmittedException;
import org.bonitasoft.forms.client.model.exception.FormInErrorException;
import org.bonitasoft.forms.client.model.exception.IllegalActivityTypeException;
import org.bonitasoft.forms.client.model.exception.MigrationProductVersionNotIdenticalException;
import org.bonitasoft.forms.client.model.exception.RPCException;
import org.bonitasoft.forms.client.model.exception.SessionTimeOutException;
import org.bonitasoft.forms.client.model.exception.SkippedFormException;
import org.bonitasoft.forms.client.model.exception.SuspendedFormException;
import org.bonitasoft.forms.client.rpc.FormsService;
import org.bonitasoft.forms.server.accessor.impl.util.FormCacheUtil;
import org.bonitasoft.forms.server.accessor.impl.util.FormCacheUtilFactory;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormAdministrationAPI;
import org.bonitasoft.forms.server.api.IFormDefinitionAPI;
import org.bonitasoft.forms.server.api.IFormValidationAPI;
import org.bonitasoft.forms.server.api.IFormWorkflowAPI;
import org.bonitasoft.forms.server.api.impl.util.FormFieldValuesUtil;
import org.bonitasoft.forms.server.exception.FormNotFoundException;
import org.bonitasoft.forms.server.provider.FormServiceProvider;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderFactory;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.impl.StandardQueryAPIAccessorImpl;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Servlet implementing the Forms service for async calls
 * 
 * @author Anthony Birembaut, Qixiang Zhang
 */
public class FormsServlet extends RemoteServiceServlet implements FormsService {

    /**
     * UID
     */
    private static final long serialVersionUID = -638652293467914223L;

    /**
     * page flow transient data param key inside the session
     */
    public static final String TRANSIENT_DATA_SESSION_PARAM_KEY_PREFIX = "transientData-";

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(FormsServlet.class.getName());

    protected static SimpleDateFormat DATE_FORMAT;

    static {
        if (LOGGER.isLoggable(Level.FINER)) {
            DATE_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
        }
    }

    /**
     * FormFieldValuesUtil
     */
    private FormFieldValuesUtil formFieldValuesUtil = new FormFieldValuesUtil();

    public static final String FORM_LOCALE_COOKIE_NAME = "Form_Locale";

    /**
     * {@inheritDoc}
     */
    public String processCall(final String payload) throws SerializationException {
        try {
            return super.processCall(payload);
        } catch (final SerializationException e) {
            LOGGER.log(Level.SEVERE,
                    "The Object returned by the RPC call is not supported by the client. Complex java types and XML types are not supported as data field's inputs.");
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    public ApplicationConfig getApplicationConfig(final String formID, final Map<String, Object> urlContext, final boolean includeApplicationTemplate)
            throws RPCException, SessionTimeOutException, ForbiddenApplicationAccessException, MigrationProductVersionNotIdenticalException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getApplicationConfig - start");
        }
        LoginContext loginContext = null;
        String localeStr = getLocale();
        Map<String, Object> context = initContext(urlContext, resolveLocale(localeStr));
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
            final Date deployementDate = formServiceProvider.getDeployementDate(context);
            final IFormDefinitionAPI definitionAPI = FormAPIFactory.getFormDefinitionAPI(document, deployementDate, localeStr);
            final String permissions = definitionAPI.getApplicationPermissions(formID, context);
            final String productVersion = definitionAPI.getProductVersion();
            final String migrationProductVersion = definitionAPI.getMigrationProductVersion(formID, context);
            final HttpSession session = request.getSession();
            final User user = (User) session.getAttribute(LoginServlet.USER_SESSION_PARAM_KEY);
            context.put(FormServiceProviderUtil.USER, user);
            formServiceProvider.isAllowed(formID, permissions, productVersion, migrationProductVersion, context, false);
            return definitionAPI.getApplicationConfig(context, formID, includeApplicationTemplate);
        } catch (final ForbiddenApplicationAccessException e) {
            throw new ForbiddenApplicationAccessException(e);
        } catch (final MigrationProductVersionNotIdenticalException e) {
            throw new MigrationProductVersionNotIdenticalException(e);
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the Process Config from a process instance", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
            if (LOGGER.isLoggable(Level.FINER)) {
                String time = DATE_FORMAT.format(new Date());
                LOGGER.log(Level.FINER, "### " + time + " - getApplicationConfig - end");
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SkippedFormException
     */
    public FormPage getFormFirstPage(final String formID, final Map<String, Object> urlContext) throws SessionTimeOutException, RPCException,
            SuspendedFormException, CanceledFormException, FormAlreadySubmittedException, ForbiddenFormAccessException, FormInErrorException,
            MigrationProductVersionNotIdenticalException, SkippedFormException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getFormFirstPage - start");
        }
        LoginContext loginContext = null;
        String localeStr = getLocale();
        Locale userLocale = resolveLocale(localeStr);
        Map<String, Object> context = initContext(urlContext, userLocale);
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
            final Date deployementDate = formServiceProvider.getDeployementDate(context);
            final boolean isEditMode = formServiceProvider.isEditMode(formID, context);
            final boolean isCurrentValue = formServiceProvider.isCurrentValue(context);
            final IFormDefinitionAPI definitionAPI = (IFormDefinitionAPI) FormAPIFactory.getFormDefinitionAPI(document, deployementDate, localeStr);
            final String permissions = definitionAPI.getFormPermissions(formID, context);
            final String productVersion = definitionAPI.getProductVersion();
            final String migrationProductVersion = definitionAPI.getMigrationProductVersion(formID, context);
            final HttpSession session = request.getSession();
            final User user = (User) session.getAttribute(LoginServlet.USER_SESSION_PARAM_KEY);
            context.put(FormServiceProviderUtil.USER, user);
            formServiceProvider.isAllowed(formID, permissions, productVersion, migrationProductVersion, context, true);
            final String pageIdExpression = definitionAPI.getFormFirstPage(formID, context);
            FormPage formPage = null;
            if (pageIdExpression != null) {
                setClassloader(formServiceProvider, context);
                final List<TransientData> transientData = definitionAPI.getFormTransientData(formID, context);
                final Map<String, Object> transientDataContext = definitionAPI.getTransientDataContext(transientData, userLocale, context);
                
                //add by robin
                //final String strIP=request.getRemoteAddr();
                transientDataContext.put("HttpServletRequest",request);
                //end add
                
                setFormTransientDataContext(formServiceProvider, formID, transientDataContext, context);
                context.put(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT, transientDataContext);
                context.put(FormServiceProviderUtil.IS_EDIT_MODE, isEditMode);
                context.put(FormServiceProviderUtil.IS_CURRENT_VALUE, isCurrentValue);
                context.put(FormServiceProviderUtil.FIELD_VALUES, new HashMap<String, FormFieldValue>());
                final String pageId = (String) formServiceProvider.resolveExpression(pageIdExpression, context);
                formPage = definitionAPI.getFormPage(formID, pageId, context);
                if (formPage != null) {
                    formPage.setPageLabel((String) formServiceProvider.resolveExpression(formPage.getPageLabel(), context));
                    formFieldValuesUtil.setFormWidgetsValues(formPage.getFormWidgets(), context);
                    formFieldValuesUtil.clearExpressionsOrConnectors(formID, pageId, localeStr, deployementDate, formPage.getFormWidgets());
                }
                return formPage;
            }
            return formPage;
        } catch (final ForbiddenFormAccessException e) {
            throw new ForbiddenFormAccessException(e);
        } catch (final MigrationProductVersionNotIdenticalException e) {
            throw new MigrationProductVersionNotIdenticalException(e);
        } catch (final CanceledFormException e) {
            throw new CanceledFormException(e);
        } catch (final SuspendedFormException e) {
            throw new SuspendedFormException(e);
        } catch (final FormInErrorException e) {
            throw new FormInErrorException(e);
        } catch (final SkippedFormException e) {
            throw new SkippedFormException(e);
        } catch (final FormAlreadySubmittedException e) {
            throw new FormAlreadySubmittedException(e);
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the first page for application " + formID, e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            ServletLoginUtils.engineLogout(loginContext);
            if (LOGGER.isLoggable(Level.FINER)) {
                String time = DATE_FORMAT.format(new Date());
                LOGGER.log(Level.FINER, "### " + time + " - getFormFirstPage - end");
            }
        }
    }

    /**
     * Set the classloader matching the given context
     * 
     * @param formServiceProvider
     *            the form service provider
     * @param context
     *            the context (including URL parameters)
     */
    protected void setClassloader(final FormServiceProvider formServiceProvider, final Map<String, Object> context) {
        final ClassLoader classloader = formServiceProvider.getClassloader(context);
        if (classloader != null) {
            Thread.currentThread().setContextClassLoader(classloader);
        }
    }

    /**
     * Initialize the context map
     * 
     * @param urlContext
     *            the map of URL parameters
     * @param locale
     *            the user locale
     * @return the context map
     */
    protected Map<String, Object> initContext(final Map<String, Object> urlContext, Locale locale) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, locale);
        return context;
    }

    public FormPage getFormNextPage(final String formID, final Map<String, Object> urlContext, final String pageIdExpression,
            final Map<String, FormFieldValue> fieldValues) throws RPCException, SessionTimeOutException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - getFormNextPage - start");
        }
        LoginContext loginContext = null;
        String localeStr = getLocale();
        Locale userLocale = resolveLocale(localeStr);
        Map<String, Object> context = initContext(urlContext, userLocale);
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            final Date deployementDate = formServiceProvider.getDeployementDate(context);
            final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
            final boolean isEditMode = formServiceProvider.isEditMode(formID, context);
            final boolean isCurrentValue = formServiceProvider.isCurrentValue(context);
            final IFormDefinitionAPI definitionAPI = FormAPIFactory.getFormDefinitionAPI(document, deployementDate, localeStr);
            final String permissions = definitionAPI.getFormPermissions(formID, context);
            final String productVersion = definitionAPI.getProductVersion();
            final String migrationProductVersion = definitionAPI.getMigrationProductVersion(formID, context);
            final HttpSession session = request.getSession();
            final User user = (User) session.getAttribute(LoginServlet.USER_SESSION_PARAM_KEY);
            context.put(FormServiceProviderUtil.USER, user);
            formServiceProvider.isAllowed(formID, permissions, productVersion, migrationProductVersion, context, true);
            setClassloader(formServiceProvider, context);
            final Map<String, Object> transientDataContext = getFormTransientDataContext(formServiceProvider, formID, context);
            context.put(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT, transientDataContext);
            context.put(FormServiceProviderUtil.IS_CURRENT_VALUE, isCurrentValue);
            context.put(FormServiceProviderUtil.IS_EDIT_MODE, isEditMode);
            context.put(FormServiceProviderUtil.FIELD_VALUES, fieldValues);
            final String pageId = (String) formServiceProvider.resolveExpression(pageIdExpression, context);
            FormPage formPage = definitionAPI.getFormPage(formID, pageId, context);
            formPage.setPageLabel((String) formServiceProvider.resolveExpression(formPage.getPageLabel(), context));
            formFieldValuesUtil.setFormWidgetsValues(formPage.getFormWidgets(), context);
            formFieldValuesUtil.clearExpressionsOrConnectors(formID, pageId, localeStr, deployementDate, formPage.getFormWidgets());
            return formPage;
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the process instance next form page " + pageIdExpression, e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            ServletLoginUtils.engineLogout(loginContext);
            if (LOGGER.isLoggable(Level.FINER)) {
                String time = DATE_FORMAT.format(new Date());
                LOGGER.log(Level.FINER, "### " + time + " - getFormNextPage - end");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public FormPage getTaskAdminFormPage(final String taskUUIDStr, final String pageId) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
            final ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(taskUUIDStr);
            final ProcessDefinitionUUID processDefinitionUUID = activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID();
            final Date deployementDate = workflowAPI.getProcessDefinitionDate(processDefinitionUUID);
            final String activityName = activityInstanceUUID.getActivityName();
            final IFormAdministrationAPI adminAPI = FormAPIFactory.getFormAdministrationAPI(processDefinitionUUID, deployementDate);
            final FormPage formPage = adminAPI.getFormPage(activityName, pageId);
            final Locale userLocale = resolveLocale(getLocale());
            formPage.setPageLabel((String) workflowAPI.getFieldValue(activityInstanceUUID, formPage.getPageLabel(), userLocale, true));
            final List<FormWidget> widgets = formPage.getFormWidgets();
            for (final FormWidget formWidget : widgets) {
                adminAPI.setFormWidgetValues(activityInstanceUUID, formWidget, userLocale);
            }
            formFieldValuesUtil.clearExpressionsOrConnectors(taskUUIDStr, pageId, getLocale(), deployementDate, widgets);
            return formPage;
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the admin Task Form Page", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public FormPage getProcessInstanceAdminFormPage(final String processInstanceUUIDStr, final String pageId) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
            final ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(processInstanceUUIDStr);
            final Date deployementDate = workflowAPI.getProcessDefinitionDate(processInstanceUUID.getProcessDefinitionUUID());
            final IFormAdministrationAPI adminAPI = FormAPIFactory.getFormAdministrationAPI(processInstanceUUID.getProcessDefinitionUUID(), deployementDate);
            final FormPage formPage = adminAPI.getProcessFormPage(pageId);
            final Locale userLocale = resolveLocale(getLocale());
            formPage.setPageLabel((String) workflowAPI.getFieldValue(processInstanceUUID, formPage.getPageLabel(), userLocale, true));
            final List<FormWidget> widgets = formPage.getFormWidgets();
            for (final FormWidget formWidget : widgets) {
                adminAPI.setFormWidgetValues(processInstanceUUID, formWidget, userLocale);
            }
            formFieldValuesUtil.clearExpressionsOrConnectors(processInstanceUUIDStr, pageId, getLocale(), deployementDate, widgets);
            return formPage;
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the admin Process instance Form Page", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getProcessInstanceAdminPageList(final String processInstanceUUIDStr) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
            final ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(processInstanceUUIDStr);
            final Date deployementDate = workflowAPI.getProcessDefinitionDate(processInstanceUUID.getProcessDefinitionUUID());
            final IFormAdministrationAPI adminAPI = FormAPIFactory.getFormAdministrationAPI(processInstanceUUID.getProcessDefinitionUUID(), deployementDate);
            final List<String> pageList = adminAPI.getPageList();
            return pageList;
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the admin Process instance Pages List", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getTaskAdminPageList(final String taskUUIDStr) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
            final ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(taskUUIDStr);
            final ProcessDefinitionUUID processDefinitionUUID = activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID();
            final Date deployementDate = workflowAPI.getProcessDefinitionDate(processDefinitionUUID);
            final String activityName = activityInstanceUUID.getActivityName();
            final IFormAdministrationAPI adminAPI = FormAPIFactory.getFormAdministrationAPI(processDefinitionUUID, deployementDate);
            final List<String> pageList = adminAPI.getPageList(activityName);
            return pageList;
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the admin Task Page List", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }
    //add by robin for transfer HttpServletRequest
    protected void injectHttpServletRequest(final Map<String, Object> urlContext,HttpServletRequest request){
    	  
    	if (urlContext.containsKey(FormServiceProviderUtil.TASK_UUID)){
    		LOGGER.log(Level.WARNING, "&&&&&&&&&& injectHttpServletRequest  taskuuid");
    		try {
				final QueryRuntimeAPI api = AccessorUtil.getQueryRuntimeAPI();
				ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(urlContext.get(FormServiceProviderUtil.TASK_UUID).toString());
				ActivityInstance actIns=api.getActivityInstance(activityInstanceUUID);
				LOGGER.log(Level.WARNING, "&&&&&&&&&& injectHttpServletRequest gettttttttt ActivityInstance");
				ProcessInstanceUUID procInsUUID= actIns.getProcessInstanceUUID();
				String webIPAddress=(String)api.getProcessInstanceVariable(procInsUUID, "webIPAddress");
				webIPAddress=request.getRemoteAddr();
				final RuntimeAPI apiRun = AccessorUtil.getRuntimeAPI();
				apiRun.setProcessInstanceVariable(procInsUUID, "webIPAddress", webIPAddress);
			} catch (ActivityNotFoundException e) {
				
			} catch (InstanceNotFoundException e) {
				
			} catch (VariableNotFoundException e) {
				
			}
    	}
    	LOGGER.log(Level.WARNING, "&&&&&&&&&& injectHttpServletRequest noooooooooo taskuuid");
    }
    //end add
    
    
    /**
     * {@inheritDoc}
     */
    public Map<String, Object> skipForm(final String formID, final Map<String, Object> urlContext) throws RPCException, SessionTimeOutException,
            FormAlreadySubmittedException, IllegalActivityTypeException {
        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - skipForm - start");
        }
        LoginContext loginContext = null;
        Map<String, Object> context = initContext(urlContext, resolveLocale(getLocale()));
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            
            //add by robin
            LOGGER.log(Level.WARNING, "&&&&&&&&&& skipForm "+urlContext.toString());
            this.injectHttpServletRequest(urlContext, request);
            //end add
            
            
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            return formServiceProvider.skipForm(formID, context);
        } catch (final FormAlreadySubmittedException e) {
            throw new FormAlreadySubmittedException(e.getMessage(), e);
        } catch (final IllegalActivityTypeException e) {
            throw new IllegalActivityTypeException(e);
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while terminating task", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
            if (LOGGER.isLoggable(Level.FINER)) {
                String time = DATE_FORMAT.format(new Date());
                LOGGER.log(Level.FINER, "### " + time + " - skipForm - end");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void executeTaskAdminActions(final String taskUUIDStr, final Map<String, FormFieldValue> fieldValues) throws RPCException, SessionTimeOutException,
            FileTooBigException {
        LoginContext loginContext = null;
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
            final ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(taskUUIDStr);
            final ProcessDefinitionUUID processDefinitionUUID = activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID();
            final Date deployementDate = workflowAPI.getProcessDefinitionDate(processDefinitionUUID);
            final String activityName = activityInstanceUUID.getActivityName();
            final IFormAdministrationAPI adminAPI = FormAPIFactory.getFormAdministrationAPI(processDefinitionUUID, deployementDate);
            final List<FormAction> actions = adminAPI.getActions(activityName);
            workflowAPI.executeActions(activityInstanceUUID, fieldValues, actions, resolveLocale(getLocale()));
        } catch (final org.bonitasoft.forms.server.exception.FileTooBigException e) {
            throw new FileTooBigException(e.getMessage(), e.getFileName(), e);
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while executing admin Task Actions", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<FormValidator>> validateFormFields(final String formID, final Map<String, Object> urlContext,
            final Map<String, List<FormValidator>> validatorsMap, final Map<String, FormFieldValue> widgetValues, final String submitButtonId)
            throws RPCException, SessionTimeOutException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - validateFormFields - start");
        }
        LoginContext loginContext = null;
        Map<String, Object> context = initContext(urlContext, resolveLocale(getLocale()));
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            setClassloader(formServiceProvider, context);
            final Map<String, List<FormValidator>> nonCompliantValidators = new HashMap<String, List<FormValidator>>();
            final Map<String, Object> transientDataContext = getFormTransientDataContext(formServiceProvider, formID, context);
            context.put(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT, transientDataContext);
            for (final Entry<String, List<FormValidator>> validatorsEntry : validatorsMap.entrySet()) {
                final String fieldId = getFieldId(validatorsEntry.getKey());
                final FormFieldValue fieldValue = getFieldValue(validatorsEntry.getKey(), widgetValues);
                final List<FormValidator> nonCompliantFieldValidators = formServiceProvider.validateField(validatorsEntry.getValue(), fieldId, fieldValue,
                        submitButtonId, context);
                if (!nonCompliantFieldValidators.isEmpty()) {
                    nonCompliantValidators.put(validatorsEntry.getKey(), nonCompliantFieldValidators);
                }
            }
            return nonCompliantValidators;
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while validating Field", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            ServletLoginUtils.engineLogout(loginContext);
            if (LOGGER.isLoggable(Level.FINER)) {
                String time = DATE_FORMAT.format(new Date());
                LOGGER.log(Level.FINER, "### " + time + " - validateFormFields - end");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<FormValidator>> validateTaskFieldsAdmin(final String taskUUIDStr, final Map<String, List<FormValidator>> validatorsMap,
            final Map<String, FormFieldValue> widgetValues) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(taskUUIDStr);
            final Locale locale = resolveLocale(getLocale());
            final IFormValidationAPI validationAPI = FormAPIFactory.getFormValidationAPI();
            final Map<String, List<FormValidator>> nonCompliantValidators = new HashMap<String, List<FormValidator>>();
            for (final Entry<String, List<FormValidator>> validatorsEntry : validatorsMap.entrySet()) {
                final String fieldId = validatorsEntry.getKey();
                final FormFieldValue fieldValue = getFieldValue(fieldId, widgetValues);
                final List<FormValidator> nonCompliantFieldValidators = validationAPI.validateField(activityInstanceUUID, validatorsEntry.getValue(), fieldId,
                        fieldValue, null, locale, new HashMap<String, Object>());
                if (!nonCompliantFieldValidators.isEmpty()) {
                    nonCompliantValidators.put(fieldId, nonCompliantFieldValidators);
                }
            }
            return nonCompliantValidators;
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while validating Field", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> validateFormPage(final String formID, final Map<String, Object> urlContext, final List<FormValidator> validators,
            final Map<String, FormFieldValue> fields, final String submitButtonId) throws RPCException, SessionTimeOutException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - validateFormPage - start");
        }
        LoginContext loginContext = null;
        Map<String, Object> context = initContext(urlContext, resolveLocale(getLocale()));
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            setClassloader(formServiceProvider, context);
            final Map<String, Object> transientDataContext = getFormTransientDataContext(formServiceProvider, formID, context);
            context.put(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT, transientDataContext);
            return formServiceProvider.validatePage(validators, fields, submitButtonId, context);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while validating Page", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            ServletLoginUtils.engineLogout(loginContext);
            if (LOGGER.isLoggable(Level.FINER)) {
                String time = DATE_FORMAT.format(new Date());
                LOGGER.log(Level.FINER, "### " + time + " - validateFormPage - end");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<FormValidator>> validateInstanceFieldsAdmin(final String processInstanceUUIDStr,
            final Map<String, List<FormValidator>> validatorsMap, final Map<String, FormFieldValue> widgetValues) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(processInstanceUUIDStr);
            final Locale locale = resolveLocale(getLocale());
            final IFormValidationAPI validationAPI = FormAPIFactory.getFormValidationAPI();
            final Map<String, List<FormValidator>> nonCompliantValidators = new HashMap<String, List<FormValidator>>();
            for (final Entry<String, List<FormValidator>> validatorsEntry : validatorsMap.entrySet()) {
                final String fieldId = validatorsEntry.getKey();
                final FormFieldValue fieldValue = getFieldValue(fieldId, widgetValues);
                final List<FormValidator> nonCompliantFieldValidators = validationAPI.validateField(processInstanceUUID, validatorsEntry.getValue(), fieldId,
                        fieldValue, null, locale, new HashMap<String, Object>());
                if (!nonCompliantFieldValidators.isEmpty()) {
                    nonCompliantValidators.put(fieldId, nonCompliantFieldValidators);
                }
            }
            return nonCompliantValidators;
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while validating Field", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * Retrieve the true ID of the field with the given client ID
     * 
     * @param key
     *            the ID returned by the client part
     * @return the true ID
     */
    protected String getFieldId(String key) {
        return key;
    }

    /**
     * Retrieve the value of the field with the given ID
     * 
     * @param fieldId
     *            the field ID
     * @param widgetValues
     *            the values of the fields
     * @return a {@link FormFieldValue}
     */
    protected FormFieldValue getFieldValue(String fieldId, Map<String, FormFieldValue> widgetValues) {
        return widgetValues.get(fieldId);
    }

    /**
     * {@inheritDoc}
     */
    public HtmlTemplate getFormConfirmationTemplate(final String formID, final Map<String, Object> urlContext) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        String localeStr = getLocale();
        Map<String, Object> context = initContext(urlContext, resolveLocale(localeStr));
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            final Date deployementDate = formServiceProvider.getDeployementDate(context);
            final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
            final IFormDefinitionAPI definitionAPI = FormAPIFactory.getFormDefinitionAPI(document, deployementDate, localeStr);
            final HtmlTemplate htmlTemplate = definitionAPI.getFormConfirmationLayout(formID, context);
            setClassloader(formServiceProvider, context);
            try {
                final Map<String, Object> transientDataContext = getFormTransientDataContext(formServiceProvider, formID, context);
                context.put(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT, transientDataContext);
                final boolean isEditMode = formServiceProvider.isEditMode(formID, context);
                context.put(FormServiceProviderUtil.IS_EDIT_MODE, isEditMode);
                htmlTemplate.setDynamicMessage((String) formServiceProvider.resolveExpression(htmlTemplate.getDynamicMessage(), context));
            } catch(FormNotFoundException e ) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Cannot evaluate the confirmation message. The process instance have been removed upon process completion.");
                }
            }
            return htmlTemplate;
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the Process Confirmation Template", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public HtmlTemplate getApplicationErrorTemplate(final String formID, final Map<String, Object> urlContext) throws RPCException, SessionTimeOutException {

        LoginContext loginContext = null;
        String localeStr = getLocale();
        Map<String, Object> context = initContext(urlContext, resolveLocale(localeStr));
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            final Date deployementDate = formServiceProvider.getDeployementDate(context);
            final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
            final IFormDefinitionAPI definitionAPI = FormAPIFactory.getFormDefinitionAPI(document, deployementDate, localeStr);
            final HtmlTemplate htmlTemplate = definitionAPI.getApplicationErrorLayout(context);
            return htmlTemplate;
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the Process Error Template", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public FormURLComponents getNextFormURL(final String formID, final Map<String, Object> urlContext) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        Map<String, Object> context = initContext(urlContext, resolveLocale(getLocale()));
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            context.put(FormServiceProviderUtil.REQUEST, request);
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            removeFormTransientDataContext(formServiceProvider, formID, context);
            return formServiceProvider.getNextFormURLParameters(formID, context);
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the next task", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void executeProcessAdminActions(final String processInstanceUUIDStr, final Map<String, FormFieldValue> fieldValues) throws RPCException,
            SessionTimeOutException, FileTooBigException {
        LoginContext loginContext = null;
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
            final ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(processInstanceUUIDStr);
            final String locale = getLocale();
            final Date deployementDate = workflowAPI.getProcessDefinitionDate(processInstanceUUID.getProcessDefinitionUUID());
            final IFormAdministrationAPI adminAPI = FormAPIFactory.getFormAdministrationAPI(processInstanceUUID.getProcessDefinitionUUID(), deployementDate);
            final List<FormAction> actions = adminAPI.getProcessActions();
            workflowAPI.executeActions(processInstanceUUID, fieldValues, actions, resolveLocale(locale));
        } catch (final org.bonitasoft.forms.server.exception.FileTooBigException e) {
            throw new FileTooBigException(e.getMessage(), e.getFileName(), e);
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while executing the Processes admin Actions", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getExternalWelcomePage() throws RPCException {
        try {
            final Map<String, Object> context = new HashMap<String, Object>();
            context.put(FormServiceProviderUtil.LOCALE, resolveLocale(getLocale()));
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
            final IFormDefinitionAPI definitionAPI = FormAPIFactory.getFormDefinitionAPI(document, null, getLocale());
            return definitionAPI.getExternalWelcomePage();
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the external welcome page URL", e);
            }
            throw new RPCException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public HtmlTemplate getWelcomePage(final Map<String, Object> urlContext) throws RPCException {
        try {
            final Map<String, Object> context = new HashMap<String, Object>();
            context.put(FormServiceProviderUtil.LOCALE, resolveLocale(getLocale()));
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
            final IFormDefinitionAPI definitionAPI = FormAPIFactory.getFormDefinitionAPI(document, null, getLocale());
            return definitionAPI.getWelcomePage(urlContext);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the internal welcome page", e);
            }
            throw new RPCException(e.getMessage(), e);
        }
    }

    /**
     * store the transient data context for the current page flow displayed in the session
     * 
     * @param formServiceProvider
     * @param formID
     *            the form ID
     * @param transientDataContext
     *            the transient data context
     * @param context 
     */
    protected void setFormTransientDataContext(final FormServiceProvider formServiceProvider, final String formID, final Map<String, Object> transientDataContext, Map<String, Object> context) {

        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();
        formServiceProvider.storeFormTransientDataContext(session, TRANSIENT_DATA_SESSION_PARAM_KEY_PREFIX + formID, transientDataContext, context);
    }
    

    /**
     * Get the transient data context for the current page flow displayed from the session
     * 
     * @param formServiceProvider
     * @param formID
     *            the form ID
     * @param context 
     * @return a Map<String, Object> containing the context of transient data
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> getFormTransientDataContext(final FormServiceProvider formServiceProvider, final String formID, Map<String, Object> context) {

        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();
        return formServiceProvider.retrieveFormTransientDataContext(session, TRANSIENT_DATA_SESSION_PARAM_KEY_PREFIX + formID, context);
    }
    
    /**
     * Get the transient data context for the current page flow displayed from the session
     * 
     * @param formServiceProvider
     * @param formID
     *            the form ID
     * @param context 
     * @return a Map<String, Object> containing the context of transient data
     */
    @SuppressWarnings("unchecked")
    protected void removeFormTransientDataContext(final FormServiceProvider formServiceProvider, final String formID, Map<String, Object> context) {

        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();
        formServiceProvider.removeFormTransientDataContext(session, TRANSIENT_DATA_SESSION_PARAM_KEY_PREFIX + formID, context);
    }

    /**
     * {@inheritDoc}
     */
    public List<FormFieldAvailableValue> getFormAsyncAvailableValues(final String formID, final Map<String, Object> urlContext, final FormWidget formWidget,
            final FormFieldValue currentFieldValue) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        Map<String, Object> context = initContext(urlContext, resolveLocale(getLocale()));
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            setClassloader(formServiceProvider, context);
            final Map<String, Object> transientDataContext = getFormTransientDataContext(formServiceProvider, formID, context);
            context.put(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT, transientDataContext);
            // put the current value of the field in the field context
            final Map<String, FormFieldValue> fieldContext = new HashMap<String, FormFieldValue>();
            fieldContext.put(formWidget.getId(), currentFieldValue);
            context.put(FormServiceProviderUtil.FIELD_VALUES, fieldContext);
            Object availableValuesObject = null;
            if (formWidget.getAvailableValuesExpressionId() != null) {
                // evaluate the available values expression
                final FormCacheUtil formCacheUtil = FormCacheUtilFactory.getTenancyFormCacheUtil();
                availableValuesObject = formServiceProvider.resolveExpression(
                        formCacheUtil.getAvailableValuesExpression(formWidget.getAvailableValuesExpressionId()), context);
            } else {
                availableValuesObject = formWidget.getAvailableValuesExpression();
            }
            return formFieldValuesUtil.getAvailableValues(availableValuesObject, formWidget);
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting activity async available values of the widget " + formWidget.getId(), e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * @param localeStr
     *            the user's locale as a string
     * @return the user's {@link Locale}
     */
    protected Locale resolveLocale(final String localeStr) {
        final String[] localeParams = localeStr.split("_");
        final String language = localeParams[0];
        Locale userLocale = null;
        if (localeParams.length > 1) {
            final String country = localeParams[1];
            userLocale = new Locale(language, country);
        } else {
            userLocale = new Locale(language);
        }
        return userLocale;
    }

    /**
     * @return the user's locale as a String
     */
    protected String getLocale() {
        String localeStr = null;
        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();
        final User user = (User) session.getAttribute(LoginServlet.USER_SESSION_PARAM_KEY);
        if (user != null) {
            localeStr = getFormLocale();
        }
        if (localeStr == null && user != null) {
            localeStr = user.getLocale();
        }
        if (localeStr == null) {
            localeStr = request.getLocale().toString();
        }
        return localeStr;
    }

    protected String getFormLocale() {
        String userLocaleStr = null;
        final String theLocaleCookieName = FORM_LOCALE_COOKIE_NAME;
        final Cookie theCookies[] = this.getThreadLocalRequest().getCookies();
        Cookie theCookie = null;
        if (theCookies != null) {
            for (int i = 0; i < theCookies.length; i++) {
                if (theCookies[i].getName().equals(theLocaleCookieName)) {
                    theCookie = theCookies[i];
                    userLocaleStr = theCookie.getValue().toString();
                    break;
                }
            }
        }
        return userLocaleStr;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> executeActions(String formID, Map<String, Object> urlContext, Map<String, FormFieldValue> fieldValues, List<String> pageIds,
            String submitButtonId) throws RPCException, SessionTimeOutException, FileTooBigException, FormAlreadySubmittedException {

        if (LOGGER.isLoggable(Level.FINER)) {
            String time = DATE_FORMAT.format(new Date());
            LOGGER.log(Level.FINER, "### " + time + " - executeActions - start");
        }
        LoginContext loginContext = null;
        String localeStr = getLocale();
        Map<String, Object> context = initContext(urlContext, resolveLocale(getLocale()));
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            context.put(FormServiceProviderUtil.FIELD_VALUES, fieldValues);
            context.put(FormServiceProviderUtil.SUBMIT_BUTTON_ID, submitButtonId);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            final Date deployementDate = formServiceProvider.getDeployementDate(context);
            final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
            final IFormDefinitionAPI definitionAPI = FormAPIFactory.getFormDefinitionAPI(document, deployementDate, localeStr);
            final List<FormAction> actions = definitionAPI.getFormActions(formID, pageIds, context);
            setClassloader(formServiceProvider, context);
            final Map<String, Object> transientDataContext = getFormTransientDataContext(formServiceProvider, formID, context);
            context.put(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT, transientDataContext);
            return formServiceProvider.executeActions(actions, context);
        } catch (final FormAlreadySubmittedException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "The form with ID " + formID + " has already been submitted by someone else.", e);
            }
            throw new FormAlreadySubmittedException(e);
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final org.bonitasoft.forms.server.exception.FileTooBigException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, e.getMessage(), e);
            }
            throw new FileTooBigException(e.getMessage(), e.getFileName(), e.getMaxSize(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while executing Actions", e);
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            ServletLoginUtils.engineLogout(loginContext);
            if (LOGGER.isLoggable(Level.FINER)) {
                String time = DATE_FORMAT.format(new Date());
                LOGGER.log(Level.FINER, "### " + time + " - executeActions - end");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public FormURLComponents getAnyTodoListForm(Map<String, Object> urlContext) throws RPCException, SessionTimeOutException {
        LoginContext loginContext = null;
        Map<String, Object> context = initContext(urlContext, resolveLocale(getLocale()));
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            loginContext = ServletLoginUtils.engineLogin(request);
            final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
            return formServiceProvider.getAnyTodoListForm(context);
        } catch (final NoCredentialsInSessionException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Session timeout");
            }
            throw new SessionTimeOutException(e.getMessage(), e);
        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "Error while getting any todolist form");
            }
            throw new RPCException(e.getMessage(), e);
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

}
