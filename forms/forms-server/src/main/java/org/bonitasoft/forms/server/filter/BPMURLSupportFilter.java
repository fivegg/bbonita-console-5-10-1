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
 * 
 */
package org.bonitasoft.forms.server.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.servlet.ServletLoginUtils;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormWorkflowAPI;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * This filter transform the regular URL parameters into Hash parameters, with a generated formID.
 *
 * @author Chong Zhao
 */
public class BPMURLSupportFilter implements Filter {
    
    /**
     * the URL param for the locale to use
     */
    protected static final String LOCALE_URL_PARAM = "locale";
    
    /**
     * the URL param for the form locale to use
     */
    protected static final String FORM_LOCALE_URL_PARAM = "formLocale";
    
    /**
     * user's domain URL parameter
     */
    public static final String DOMAIN_PARAM = "domain";
    
    /**
     * user XP's UI mode parameter
     */
    public static final String UI_MODE_PARAM = "ui";
    
    /**
     * Theme parameter
     */
    public static final String THEME_PARAM = "theme";
    
    /**
     * mode parameter
     */
	public static final String MODE_PARAM = "mode";
    
    /**
     * the GWT debug mode param
     */
    public static final String GWT_DEBUG_PARAM = "gwt.codesvr";
    
    /**
     * old pattern task parameter
     */
    public static final String TASK_PARAM = "task";
    
    /**
     * old pattern process parameter
     */
    public static final String PROCESS_PARAM = "process";
    
    /**
     * old pattern process parameter
     */
    public static final String PROCESS_NAME_PARAM = "processName";
    
    /**
     * old pattern instance parameter
     */
    public static final String INSTANCE_PARAM = "instance";
    
    /**
     * old pattern recap parameter
     */
    public static final String RECAP_PARAM = "recap";
    
    /**
     * form type: entry
     */
    public static final String ENTRY_FORM = "entry";
    
    /**
     * form type: view
     */
    public static final String VIEW_FORM = "view";
    
    /**
     * form type: recap
     */
    public static final String OVERVIEW_FORM = "recap";
    
    /**
     * form id parameter
     */
    public static final String FORM_ID_PARAM = "form";
    
    /**
     * form id separator
     */
    public static final String FORM_ID_SEPARATOR = "$";
    
    /**
     * console url prefix
     */
    public static final String CONSOLE_PREFIX = "/console";
    
    /**
     * application url prefix
     */
    public static final String APPLICATION_PREFIX = "/application";
    
    /**
     * console home page keyword
     */
    public static final String HOMEPAGE = "homepage";
    
    /**
     * console app mode
     */
    public static final String MODE_APP = "app";
    
    /**
     * console form mode
     */
    public static final String MODE_FORM = "form";
    
    /**
     * simple engine login name
     */
    private static String SIMPLE_ENGINE_USERNAME = "username";
    
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(BPMURLSupportFilter.class.getName());

    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @SuppressWarnings("unchecked")
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            Map<String, String[]> parameters = httpServletRequest.getParameterMap();
            final List<String> supportedParameterKeysList = Arrays.asList(LOCALE_URL_PARAM, FORM_LOCALE_URL_PARAM, DOMAIN_PARAM, UI_MODE_PARAM, THEME_PARAM, GWT_DEBUG_PARAM);
            Set<String> parameterKeys = new HashSet<String>(parameters.keySet());
            parameterKeys.removeAll(supportedParameterKeysList);
            if (!parameterKeys.isEmpty()) {
                parameters = new HashMap<String, String[]>(parameters);
                String processUUIDStr = null;
                if (parameterKeys.contains(PROCESS_NAME_PARAM)) {
                    final String processUUIDAsArray[] = getProcessUUIDOfLatestVersion(httpServletRequest);
                    if (processUUIDAsArray != null && processUUIDAsArray.length > 0) {
                        parameters.put(PROCESS_PARAM, processUUIDAsArray);
                        parameters.remove(PROCESS_NAME_PARAM);
                        processUUIDStr = processUUIDAsArray[0];
                    }
                }
                if (!parameterKeys.contains(FORM_ID_PARAM) && isForm(httpServletRequest)) {
                    final String formID[] = getFormIDFromRegularURLParameters(httpServletRequest, processUUIDStr);
                    if (formID != null && formID.length > 0) {
                        parameters.put(FORM_ID_PARAM, formID);
                    }
                }
                final StringBuilder hashString = new StringBuilder();
                final StringBuilder queryString = new StringBuilder();
                for (final Entry<String, String[]> parameter : parameters.entrySet()) {
                    final String key = parameter.getKey();
                    final String[] values = parameter.getValue();
                    if (supportedParameterKeysList.contains(key)) {
                        buildQueryString(queryString, key, values);
                    } else {
                        buildQueryString(hashString, key, values);
                    }
                }
                final StringBuilder redirectionURL = new StringBuilder();
                redirectionURL.append(httpServletRequest.getRequestURI());
                if (queryString.length() > 0) {
                    redirectionURL.append("?");
                    redirectionURL.append(queryString);
                }
                if (hashString.length() > 0) {
                    redirectionURL.append("#");
                    redirectionURL.append(hashString);
                }
                httpServletResponse.sendRedirect(redirectionURL.toString());
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error while parsing the regular parameters into hash parameters.");
            throw new ServletException(e);
        }
    }
    
    protected void buildQueryString(final StringBuilder queryString, final String key, final String[] values) {
        if (queryString.length() > 0) {
            queryString.append("&");
        }
        queryString.append(key);
        queryString.append("=");
        if (values.length == 1) {
            queryString.append(values[0]);
        } else if (values.length > 1) {
            final StringBuilder valuesList = new StringBuilder();
            for (final String value : values) {
                if (valuesList.length() > 0) {
                    valuesList.append(",");
                }
                valuesList.append(value);
            }
            queryString.append(valuesList);
        }
    }
    
    /**
     * Get the form id for any form.
     * @param parameters The regular parameters of current URL.
     * @param request The current http servlet request.
     * @return formID
     */
    @SuppressWarnings("deprecation")
    protected String[] getFormIDFromRegularURLParameters (HttpServletRequest request, String processUUID) {
        final String taskUUIDStr = request.getParameter(TASK_PARAM);
        final String instanceUUIDStr = request.getParameter(INSTANCE_PARAM);
        String processUUIDStr = request.getParameter(PROCESS_PARAM);
        if (processUUIDStr == null) {
            processUUIDStr = processUUID;
        }
        final String recapParam = request.getParameter(RECAP_PARAM);
        LoginContext loginContext = null;
        String formType = null;
        String formID[] = new String[1];
        boolean isRecap = false;
        if (recapParam != null) {
            isRecap = Boolean.parseBoolean(recapParam);
        }
        final StringBuilder tempFormID = new StringBuilder();
        try {
            if (taskUUIDStr != null) {
                loginContext = simpleEngineLogin(request);
                final IFormWorkflowAPI workflowAPI = FormAPIFactory.getFormWorkflowAPI();
                final ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(taskUUIDStr);
                final String activityDefinitionUUIDStr = activityInstanceUUID.getActivityDefinitionUUID().getValue();
                
                if (isRecap) {
                    formType = OVERVIEW_FORM;
                } else {
                    final boolean isTask = workflowAPI.isTask(activityInstanceUUID);
                    final boolean isTaskOver = workflowAPI.isTaskOver(activityInstanceUUID);
                    final boolean isEditMode = !isTaskOver && isTask;
                    formType = isEditMode ? ENTRY_FORM : VIEW_FORM;
                }
                tempFormID.append(activityDefinitionUUIDStr);
                tempFormID.append(FORM_ID_SEPARATOR);
                tempFormID.append(formType);
            } else if (processUUIDStr != null) {
                tempFormID.append(processUUIDStr);
                tempFormID.append(FORM_ID_SEPARATOR);
                tempFormID.append(ENTRY_FORM);
            } else if (instanceUUIDStr != null) {
                ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(instanceUUIDStr);
                final ProcessDefinitionUUID processDefinitionUUID = processInstanceUUID.getProcessDefinitionUUID();
                final String processDefinitionUUIDStr = processDefinitionUUID.getValue();
                tempFormID.append(processDefinitionUUIDStr);
                tempFormID.append(FORM_ID_SEPARATOR);
                formType = isRecap ? OVERVIEW_FORM : VIEW_FORM;
                tempFormID.append(formType);
            } else {
            	if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "No regular variables available in the URL for generating form id.");
                }
            	return null;
            }
            formID[0] = tempFormID.toString();
            return formID;
        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Cannot get the formID with current URL parameters.", e);
            }
            return null;
        } finally {
            if (loginContext != null) {
                ServletLoginUtils.engineLogout(loginContext);
            }
        }
        
    }
    
    /**
     * Get the latest version of a given process name.
     * @param processName A regular URL parameter
     * @param request The current http servlet request.
     * @return processUUIDStr
     */
    protected String[] getProcessUUIDOfLatestVersion(HttpServletRequest request) {
    	final String processName = request.getParameter(PROCESS_NAME_PARAM);
        LoginContext loginContext = null;
        try {
            loginContext = ServletLoginUtils.engineLogin(request);
            LightProcessDefinition theProcess = AccessorUtil.getQueryDefinitionAPI().getLastLightProcess(processName);
            String processUUIDStr[] = new String[1];
            processUUIDStr[0] = theProcess.getUUID().getValue();
            return processUUIDStr;
            
        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while getting the UUID of the latest process with name " + processName, e);
            }
            return null;
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }
    }

    /**
     * Is console view mode
     * 
     * @param httpRequest
     * @return isConsole flag
     */
    protected boolean isForm(final HttpServletRequest request) {
    	final String urlPrefix = request.getServletPath().replaceAll(HOMEPAGE, "");
    	final String mode = request.getParameter(MODE_PARAM);
        return urlPrefix.startsWith(APPLICATION_PREFIX) || urlPrefix.startsWith(CONSOLE_PREFIX) && (MODE_APP.equals(mode) || MODE_FORM.equals(mode));
    }
    
    /**
     * Simple engin login
     * 
     * @return the login context
     * @throws LoginException
     */
    protected LoginContext simpleEngineLogin(final HttpServletRequest request) throws LoginException {
        final CallbackHandler handler = new SimpleCallbackHandler(SIMPLE_ENGINE_USERNAME, SIMPLE_ENGINE_USERNAME);
        final LoginContext loginContext = new LoginContext(LoginServlet.JAAS_STORE_LOGIN_CONTEXT, handler);
        loginContext.login();
        return loginContext;
    }
    
    public void destroy() {
    }
}