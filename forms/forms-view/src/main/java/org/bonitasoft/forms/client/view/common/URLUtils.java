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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.forms.client.view.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

/**
 * Utility Class form URL manipulation
 * 
 * @author Anthony Birembaut
 */
public class URLUtils {

    /**
     * Generic forms app path
     */
    public static final String FORMS_APP_PATH = "/bonita-app";

    /**
     * parameter for the encrypted user credentials
     */
    public static final String USER_CREDENTIALS_PARAM = "identityKey";

    /**
     * form id : indicate the form id for which the form has to be displayed
     */
    public static final String FORM_ID = "form";

    /**
     * task id : indicate the task id for which the form has to be displayed
     */
    public static final String TASK_ID_PARAM = "task";

    /**
     * process id : indicate the process for witch the form has to be displayed
     */
    public static final String PROCESS_ID_PARAM = "process";

    /**
     * process name:the name of the process
     */
    public static final String PROCESS_NAME_PARAM = "processName";

    /**
     * instance id : indicates the process instance for witch the form has to be displayed
     */
    public static final String INSTANCE_ID_PARAM = "instance";

    /**
     * recap mode : indicates if the required instance page flow has to be displayed in recap mode
     */
    public static final String RECAP_PARAM = "recap";

    /**
     * todolist mode : if true, get one task of a given process or process instance among your todolist
     */
    public static final String TODOLIST_PARAM = "todolist";

    /**
     * current value : indicates if the required attachment version is the current version
     */
    public static final String CURRENT_VALUE_PARAM = "currentValue";

    /**
     * auto submit mode : indicates that the form should be automatically submitted
     */
    public static final String AUTO_SUBMIT_PARAM = "autoSubmit";

    /**
     * auto instantiate mode : indicates that the instance shouldn't be automatically created if there is no instantiation form
     */
    public static final String AUTO_INSTANTIATE = "autoInstantiate";

    /**
     * auto login mode : indicates that the user should be automatically logged in
     */
    public static final String AUTO_LOGIN_PARAM = "autoLogin";

    /**
     * attachment : indicate the name of the process attachment
     */
    public static final String ATTACHMENT_PARAM = "attachment";

    /**
     * attachment : indicate the path of the process attachment
     */
    public static final String ATTACHMENT_PATH_PARAM = "attachmentPath";

    /**
     * attachment : indicate the file name of the process attachment
     */
    public static final String ATTACHMENT_FILE_NAME_PARAM = "attachmentFileName";

    /**
     * the URL param for the redirection URL after login
     */
    public static final String REDIRECT_URL_PARAM = "redirectUrl";

    /**
     * form view mode indicate that the process template should be displayed
     */
    public static final String FULL_FORM_APPLICATION_MODE = "app";

    /**
     * form view mode indicate that the process template should not be displayed
     */
    public static final String FORM_ONLY_APPLICATION_MODE = "form";

    /**
     * view mode : indicate whether the process template has to be displayed or
     * not
     */
    public static final String VIEW_MODE_PARAM = "mode";

    /**
     * user's locale URL parameter
     */
    public static final String LOCALE_PARAM = "locale";

    /**
     * user's domain URL parameter
     */
    public static final String DOMAIN_PARAM = "domain";

    /**
     * Default GWT locale name (if no locale is specified in the URL)
     */
    public static final String DEFAULT_GWT_LOCALE_NAME = "default";

    /**
     * Default locale returned
     */
    public static final String DEFAULT_LOCALE = "en";

    /**
     * Locale cookie name
     */
    public static final String BOS_LOCALE_COOKIE_NAME = "BOS_Locale";

    /**
     * Locale cookie name for form
     */
    public static final String FORM_LOCALE_COOKIE_NAME = "Form_Locale";

    /**
     * The separator for history token parameters
     */
    public static final String HASH_PARAMETERS_SEPARATOR = "&";

    /**
     * The default history token
     */
    public static final String DEFAULT_HISTORY_TOKEN = "default";

    /**
     * The URL param for loading the process' custom hostpage
     */
    public static final String THEME = "theme";

    /**
     * The URL param for UI mode
     */
    public static final String UI = "ui";

    public static final String USER = "user";

    /**
     * The URL param for link refer
     */
    public static final String LINK_REFER = "refer";

    /**
     * The URL Servlet name for formLayoutDownload
     */
    public static final String FORM_LAYOUT_DOWNLOAD = "formLayoutDownload";

    /**
     * The URL param name for formLayoutDownload
     */
    public static final String BODY_CONTENT_ID = "bodyContentId";

    /**
     * a Boolean to know if we proceed a page layout or not
     */
    public static final String IS_PAGE_LAYOUT = "isPageLayout";

    /**
     * Homepage servlet ID in path
     */
    public static final String HOMEPAGE_SERVLET_ID_IN_PATH = "homepage";

    /**
     * Instance attribute
     */
    protected static URLUtils INSTANCE = null;

    /**
     * @return the view controller instance
     */
    public static URLUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new URLUtils();
        }
        return INSTANCE;
    }

    /**
     * Private contructor to prevent instantiation
     */
    protected URLUtils() {
        super();
    }

    /**
     * @return the autoGeneratedFormsUrl
     */
    public String removeURLparameters(String anApplicationURL) {
        String url = null;
        final int startParamsIndex = anApplicationURL.indexOf("?");
        final int startTokenIndex = anApplicationURL.indexOf("#");
        if (startParamsIndex >= 0) {
            url = anApplicationURL.substring(0, startParamsIndex);
        } else if (startTokenIndex >= 0) {
            url = anApplicationURL.substring(0, startTokenIndex);
        } else {
            url = anApplicationURL;
        }
        return url;
    }

    /**
     * @return the hashParamsString
     */
    public String getHashParamsString(String anApplicationURL) {
        String hashParamsString = null;
        final int startTokenIndex = anApplicationURL.indexOf("#");
        if (startTokenIndex >= 0) {
            hashParamsString = anApplicationURL.substring(startTokenIndex + 1);
        }
        return hashParamsString;
    }

    /**
     * @return the locale ("en" if the locale is not set)
     */
    public String getLocale() {
        String localeStr = null;
        if (Window.Location.getParameter(LOCALE_PARAM) == null) {
            localeStr = Cookies.getCookie(BOS_LOCALE_COOKIE_NAME);
        }
        if (localeStr == null || localeStr.length() == 0) {
            localeStr = LocaleInfo.getCurrentLocale().getLocaleName();
        }
        if (DEFAULT_GWT_LOCALE_NAME.equals(localeStr)) {
            localeStr = DEFAULT_LOCALE;
        }
        return localeStr;
    }

    /**
     * 
     * @return the locale for form
     */
    public String getLocaleForForm() {
        String localeStr = null;
        localeStr = Window.Location.getParameter(LOCALE_PARAM);
        String formLocaleInCookie = Cookies.getCookie(FORM_LOCALE_COOKIE_NAME);
        if (localeStr == null) {
            if (formLocaleInCookie == null) {
                localeStr = Window.Location.getParameter(LOCALE_PARAM);
            } else {
                final String currentLocale = Window.Location.getParameter(LOCALE_PARAM);
                if (!DEFAULT_GWT_LOCALE_NAME.equalsIgnoreCase(currentLocale)) {
                    localeStr = Window.Location.getParameter(LOCALE_PARAM);
                }
            }
        }
        if (localeStr == null) {
            localeStr = formLocaleInCookie;
        }
        if (localeStr == null || localeStr.length() == 0) {
            localeStr = LocaleInfo.getCurrentLocale().getLocaleName();
        }
        if (DEFAULT_GWT_LOCALE_NAME.equals(localeStr)) {
            localeStr = DEFAULT_LOCALE;
        }

        return localeStr;

    }

    /**
     * Retrieve the URL and remove the hash
     * 
     * @return the new url to set
     */
    public String removeHashFromUrl() {
        return rebuildUrl(Window.Location.getPath(), Window.Location.getParameterMap(), null, null, null, null, null);
    }

    /**
     * Retrieve the url and rebuild it removing the required parameters and adding the provided parameters. If a parameter to add if already in the URL it needs
     * to be removed first
     * 
     * @param paramsToRemove
     *            list of the params to remove
     * @param paramsToAdd
     *            list of the params to add
     * @param hashParamsToRemove
     * @param hashParamsToAdd
     * @return the new url to set
     */
    public String rebuildUrl(final List<String> paramsToRemove, final Map<String, String> paramsToAdd, final List<String> hashParamsToRemove,
            final Map<String, String> hashParamsToAdd) {

        final String hash = Window.Location.getHash();
        String token = null;
        if (hash != null && hash.startsWith("#")) {
            token = hash.substring(1);
        }
        return rebuildUrl(Window.Location.getPath(), Window.Location.getParameterMap(), token, paramsToRemove, paramsToAdd, hashParamsToRemove, hashParamsToAdd);
    }

    /**
     * rebuild a URL removing the required parameters and adding the provided parameters. If a parameter to add if already in the URL it needs to be removed
     * first
     * 
     * @param href
     *            the full URL with parameters an hash
     * @param paramsToRemove
     *            list of the params to remove
     * @param paramsToAdd
     *            list of the params to add
     * @param hashParamsToRemove
     * @param hashParamsToAdd
     * @return the new url to set
     */
    public String rebuildUrl(final String href, final List<String> paramsToRemove, final Map<String, String> paramsToAdd,
            final List<String> hashParamsToRemove, final Map<String, String> hashParamsToAdd) {
        final Map<String, List<String>> parametersMap = getURLParametersMap(href);
        final String url = removeURLparameters(href);
        final String token = getHashParamsString(href);
        return rebuildUrl(url, parametersMap, token, paramsToRemove, paramsToAdd, hashParamsToRemove, hashParamsToAdd);
    }

    /**
     * rebuild a URL removing the required parameters and adding the provided parameters. If a parameter to add if already in the URL it needs to be removed
     * first
     * 
     * @param url
     *            the URL
     * @param parametersMap
     *            the parameters in the URL
     * @param hash
     *            the hash in the URL
     * @param paramsToRemove
     *            list of the params to remove
     * @param paramsToAdd
     *            map of the params to add
     * @param hashParamsToRemove
     * @param hashParamsToAdd
     * @return the new url to set
     */
    protected String rebuildUrl(final String url, final Map<String, List<String>> parametersMap, final String hash, final List<String> paramsToRemove,
            final Map<String, String> paramsToAdd, final List<String> hashParamsToRemove, final Map<String, String> hashParamsToAdd) {

        final StringBuilder urlParams = new StringBuilder();
        if (parametersMap != null && !parametersMap.isEmpty()) {
            for (final Entry<String, List<String>> urlParamEntry : parametersMap.entrySet()) {
                if (paramsToRemove == null || !paramsToRemove.contains(urlParamEntry.getKey())) {
                    if (urlParams.length() > 0) {
                        urlParams.append("&");
                    }
                    urlParams.append(urlParamEntry.getKey());
                    urlParams.append("=");
                    final List<String> paramValues = urlParamEntry.getValue();
                    for (final String paramValue : paramValues) {
                        urlParams.append(paramValue);
                        urlParams.append(",");
                    }
                    urlParams.deleteCharAt(urlParams.length() - 1);
                }
            }
        }
        if (paramsToAdd != null && !paramsToAdd.isEmpty()) {
            for (final Entry<String, String> urlParamEntry : paramsToAdd.entrySet()) {
                if (urlParams.length() > 0) {
                    urlParams.append("&");
                }
                urlParams.append(urlParamEntry.getKey());
                urlParams.append("=");
                urlParams.append(urlParamEntry.getValue());
            }
        }
        final StringBuilder href = new StringBuilder(url);
        if (urlParams.length() > 0) {
            href.append("?");
            href.append(urlParams.toString());
        }

        final StringBuilder hashParams = new StringBuilder();
        if (hash != null) {
            final Map<String, String> hashParameters = getHashParameters(hash);
            for (final Entry<String, String> hashParamEntry : hashParameters.entrySet()) {
                if (hashParamsToRemove == null || !hashParamsToRemove.contains(hashParamEntry.getKey())) {
                    if (hashParams.length() > 0) {
                        hashParams.append("&");
                    }
                    hashParams.append(hashParamEntry.getKey());
                    if (hashParamEntry.getValue() != null) {
                        hashParams.append("=");
                        hashParams.append(hashParamEntry.getValue());
                    }
                }
            }
        }
        if (hashParamsToAdd != null && !hashParamsToAdd.isEmpty()) {
            for (final Entry<String, String> hashParamEntry : hashParamsToAdd.entrySet()) {
                if (hashParams.length() > 0) {
                    hashParams.append("&");
                }
                hashParams.append(hashParamEntry.getKey());
                if (hashParamEntry.getValue() != null) {
                    hashParams.append("=");
                    hashParams.append(hashParamEntry.getValue());
                }
            }
        }
        if (hashParams.length() > 0) {
            href.append("#");
            href.append(hashParams.toString());
        }
        return href.toString();
    }

    /**
     * Elaborate the redirection URL for a given task
     * 
     * @param applicationURL
     *            the application URL to redirect to
     * @param newTaskUUIDStr
     *            the task UUID as a string
     * @param hashParamsToAdd
     * @return the redirection URL
     */
    public String getTaskRedirectionUrl(final String applicationURL, final String newTaskUUIDStr, final Map<String, String> hashParamsToAdd) {
        final StringBuilder url = new StringBuilder(applicationURL);
        url.append("?");
        url.append(URLUtils.LOCALE_PARAM);
        url.append("=");
        url.append(getLocale());
        url.append("#");
        url.append(URLUtils.TASK_ID_PARAM);
        url.append("=");
        url.append(newTaskUUIDStr);
        final String viewMode = getHashParameter(URLUtils.VIEW_MODE_PARAM);
        if (viewMode != null) {
            url.append(HASH_PARAMETERS_SEPARATOR);
            url.append(URLUtils.VIEW_MODE_PARAM);
            url.append("=");
            url.append(viewMode);
        }
        if (hashParamsToAdd != null && !hashParamsToAdd.isEmpty()) {
            for (final Entry<String, String> urlParamEntry : hashParamsToAdd.entrySet()) {
                url.append(HASH_PARAMETERS_SEPARATOR);
                url.append(urlParamEntry.getKey());
                url.append("=");
                url.append(urlParamEntry.getValue());
            }
        }
        return url.toString();
    }

    /**
     * Elaborate the redirection URL hash for a given task
     * 
     * @param newTaskUUIDStr
     *            the task UUID as a string
     * @return
     */
    public String getTaskRedirectionHash(final String newTaskUUIDStr) {
        String hash = URLUtils.TASK_ID_PARAM + "=" + newTaskUUIDStr;
        final String viewMode = getHashParameter(URLUtils.VIEW_MODE_PARAM);
        if (viewMode != null) {
            hash += HASH_PARAMETERS_SEPARATOR + URLUtils.VIEW_MODE_PARAM + "=" + viewMode;
        }
        return hash;
    }

    /**
     * change the form frame URL
     * this method is meant to be called in the form frame (not in the application/console window)
     * TODO do not call this method in case there is no app redirection (all in the bar)
     * 
     * @param url
     *            the url to redirect to
     * @param frameId
     *            the ID of the frame to refresh
     */
    native public void frameRedirect(String frameId, String url)
    /*-{
        var formFrameWindow = window.parent;
        if (formFrameWindow != window.top) {
            var parentWindow = formFrameWindow.parent;
            try {
                var formFrame = parentWindow.document.getElementById(frameId);
                formFrame.src = url;
            } catch(e) {//in case the parent page is not on the same server as the forms application
                formFrameWindow.location = url;
            }
        }
    }-*/;

    /**
     * change the form frame URL
     * this method is meant to be called in the form frame (not in the application/console window)
     * TODO do not call this method in case there is no app redirection (all in the bar)
     * 
     * @param url
     *            the url to redirect to
     * @param frameId
     *            the ID of the frame to refresh
     */
    native public void changeFrameHashString(String frameId, String newHashString)
    /*-{
        var formFrameWindow = window.parent;
        if (formFrameWindow != window.top) {
            var parentWindow = formFrameWindow.parent;
            try{
                var formFrame = parentWindow.document.getElementById(frameId);
                formFrame.location.hash = newHashString;
            } catch(e) {
                formFrameWindow.location.hash = newHashString;
            }
        }
    }-*/;

    /**
     * Display the login page
     */
    public void showLoginView() {

        final String theRedirectURL = GWT.getModuleBaseURL() + "login.jsp";

        final List<String> paramsToRemove = new ArrayList<String>();
        paramsToRemove.add(URLUtils.LOCALE_PARAM);
        final List<String> hashParamToRemove = new ArrayList<String>();
        hashParamToRemove.add(URLUtils.AUTO_LOGIN_PARAM);
        hashParamToRemove.add(URLUtils.USER_CREDENTIALS_PARAM);
        final String redirectUrl = rebuildUrl(paramsToRemove, null, hashParamToRemove, null);
        String theURLSuffix = "?" + REDIRECT_URL_PARAM + "=";
        try {
            theURLSuffix += URL.encodeQueryString(redirectUrl);
        } catch (final Exception e) {
            Window.alert("Unable to redirect to login page: Invalid URL");
            theURLSuffix += GWT.getModuleBaseURL();
        } finally {
            windowRedirect(theRedirectURL + theURLSuffix);
        }
    }

    public void windowRedirect(final String href) {
        Window.Location.replace(href);
    }

    public void windowAssign(final String href) {
        Window.Location.assign(href);
    }

    public void saveLocale(final String localeName) {
        final Date now = new Date();
        // Expiration in 120 days.
        final Date theExpirationTime = new Date(now.getTime() + (1000 * 60 * 60 * 24 * 120));
        Cookies.setCookie(BOS_LOCALE_COOKIE_NAME, localeName, theExpirationTime);
    }

    public void saveLocaleForForm(final String localeName) {
        final Date now = new Date();
        // Expiration in 120 days.
        final Date theExpirationTime = new Date(now.getTime() + (1000 * 60 * 60 * 24 * 120));
        Cookies.setCookie(FORM_LOCALE_COOKIE_NAME, localeName, theExpirationTime);
    }

    @Deprecated
    public String getAttachmentURL(final String servletURL, final String taskUUIDStr, final String processUUIDStr, final String instanceUUIDStr,
            final boolean isCurrentValue, final String attachmentName) {
        String urlParams = null;
        if (taskUUIDStr != null) {
            urlParams = "?" + URLUtils.TASK_ID_PARAM + "=" + taskUUIDStr + "&" + URLUtils.ATTACHMENT_PARAM + "=" + attachmentName;
        } else if (processUUIDStr != null) {
            urlParams = "?" + URLUtils.PROCESS_ID_PARAM + "=" + processUUIDStr + "&" + URLUtils.ATTACHMENT_PARAM + "=" + attachmentName;
        } else {
            urlParams = "?" + URLUtils.INSTANCE_ID_PARAM + "=" + instanceUUIDStr + "&" + URLUtils.CURRENT_VALUE_PARAM + "=" + isCurrentValue + "&"
                    + URLUtils.ATTACHMENT_PARAM + "=" + attachmentName;
        }
        return servletURL + urlParams;
    }

    public String getAttachmentURL(final String servletURL, final String formID, final Map<String, Object> contextMap, final boolean isCurrentValue,
            final String attachmentName) {
        final StringBuilder urlParams = new StringBuilder();
        urlParams.append("?");
        final int size = contextMap.size();
        int i = 0;
        for (final Entry<String, Object> hashParamEntry : contextMap.entrySet()) {
            i++;
            if (hashParamEntry.getValue() != null) {
                urlParams.append(hashParamEntry.getKey());
                urlParams.append("=");
                urlParams.append(hashParamEntry.getValue());
                if (i != size) {
                    urlParams.append("&");
                }
            }
        }
        urlParams.append("&" + URLUtils.ATTACHMENT_PARAM + "=" + attachmentName);
        final List<String> paramsToRemove = new ArrayList<String>();
        paramsToRemove.add(URLUtils.LOCALE_PARAM);
        final List<String> hashParamToRemove = new ArrayList<String>();
        hashParamToRemove.add(URLUtils.AUTO_LOGIN_PARAM);
        hashParamToRemove.add(URLUtils.USER_CREDENTIALS_PARAM);
        final String redirectUrl = rebuildUrl(paramsToRemove, null, hashParamToRemove, null);
        String theURLSuffix = "";
        try {
            theURLSuffix += URL.encodeQueryString(redirectUrl);
        } catch (final Exception e) {
            theURLSuffix += GWT.getModuleBaseURL();
        }
        urlParams.append("&" + URLUtils.LINK_REFER + "=" + theURLSuffix);
        return servletURL + urlParams.toString();
    }

    public String getFileURL(final String servletURL, final String filepath, final String filename) {
        final String urlParams = "?" + URLUtils.ATTACHMENT_PATH_PARAM + "=" + filepath + "&" + URLUtils.ATTACHMENT_FILE_NAME_PARAM + "=" + filename;
        return servletURL + urlParams;
    }

    public String getHashParameter(final String hashParameterName) {
        final String hash = History.getToken();
        final String[] parameters = hash.split(HASH_PARAMETERS_SEPARATOR);
        for (final String parameter : parameters) {
            final String[] parameterEntry = parameter.split("=");
            final String name = parameterEntry[0];
            if (hashParameterName.equals(name) && parameterEntry.length > 0) {
                return parameterEntry[1];
            }
        }
        return null;
    }

    /**
     * @param String
     *            hash
     * @return Parameters Map of given hash String
     */
    public Map<String, String> getHashParameters(final String hash) {
        final Map<String, String> parametersMap = new HashMap<String, String>();
        final String[] parameters = hash.split(HASH_PARAMETERS_SEPARATOR);
        for (final String parameter : parameters) {
            final String[] parameterEntry = parameter.split("=");
            final String name = parameterEntry[0];
            String value = null;
            if (parameterEntry.length > 1) {
                value = parameterEntry[1];
            }
            parametersMap.put(name, value);
        }
        return parametersMap;
    }

    /**
     * @return Current URL hash Parameters Map
     */
    public Map<String, Object> getHashParameters() {
        String hash = History.getToken();
        final Map<String, Object> parametersMap = new HashMap<String, Object>();
        final String[] parameters = hash.split(HASH_PARAMETERS_SEPARATOR);
        for (final String parameter : parameters) {
            final String[] parameterEntry = parameter.split("=");
            final String name = parameterEntry[0];
            String value = null;
            if (parameterEntry.length > 1) {
                value = parameterEntry[1];
            }
            parametersMap.put(name, value);
        }
        return parametersMap;
    }

    public String rebuildHash(final List<String> hashParamsToRemove, final Map<String, String> hashParamsToAdd) {
        final String hash = History.getToken();
        final StringBuilder hashParams = new StringBuilder();
        if (hash != null) {
            final Map<String, String> hashParameters = getHashParameters(hash);
            for (final Entry<String, String> hashParamEntry : hashParameters.entrySet()) {
                if (hashParamsToRemove == null || !hashParamsToRemove.contains(hashParamEntry.getKey())) {
                    if (hashParams.length() > 0) {
                        hashParams.append("&");
                    }
                    hashParams.append(hashParamEntry.getKey());
                    if (hashParamEntry.getValue() != null) {
                        hashParams.append("=");
                        hashParams.append(hashParamEntry.getValue());
                    }
                }
            }
        }
        if (hashParamsToAdd != null && !hashParamsToAdd.isEmpty()) {
            for (final Entry<String, String> hashParamEntry : hashParamsToAdd.entrySet()) {
                if (hashParams.length() > 0) {
                    hashParams.append("&");
                }
                hashParams.append(hashParamEntry.getKey());
                if (hashParamEntry.getValue() != null) {
                    hashParams.append("=");
                    hashParams.append(hashParamEntry.getValue());
                }
            }
        }
        return hashParams.toString();
    }

    /**
     * parse anApplicationURL As Parameters Map ,and set autoGeneratedFormsUrl and hashParams
     * 
     * @param anApplicationURL
     * @return parametersMap
     */
    public Map<String, List<String>> getURLParametersMap(final String anApplicationURL) {
        String urlParams = null;
        final int startParamsIndex = anApplicationURL.indexOf("?");
        final int startTokenIndex = anApplicationURL.indexOf("#");
        if (startParamsIndex >= 0) {
            if (startTokenIndex >= 0) {
                urlParams = anApplicationURL.substring(startParamsIndex + 1, startTokenIndex);
            } else {
                urlParams = anApplicationURL.substring(startParamsIndex + 1);
            }
        }
        final Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
        if (urlParams != null) {
            final String[] splittedParams = urlParams.split("&");
            for (final String param : splittedParams) {
                final int equalsIndex = param.indexOf("=");
                if (equalsIndex >= 0) {
                    final String key = param.substring(0, equalsIndex);
                    final String values = param.substring(equalsIndex + 1);
                    final List<String> valuesList = Arrays.asList(values.split(","));
                    parametersMap.put(key, valuesList);
                }
            }
        }
        return parametersMap;
    }

    public String getFormID() {
        return getHashParameter("form");
    }

    /**
     * @param applicationURL
     * @param urlContext
     * @return
     */
    public String getFormRedirectionUrl(String applicationURL, Map<String, Object> urlContext) {
        final StringBuilder url = new StringBuilder(applicationURL);
        if (applicationURL.contains("?")) {
            url.append("&");
        } else {
            url.append("?");
        }
        url.append(URLUtils.LOCALE_PARAM);
        url.append("=");
        url.append(getLocale());
        url.append("#");
        Iterator<Entry<String, Object>> it = urlContext.entrySet().iterator();
        int size = urlContext.size();
        for (int i = 0; i < size; i++) {
            Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue().toString();
            url.append(key + "=" + value);
            if (i < size - 1) {
                url.append("&");
            }
        }
        return url.toString();
    }

    /**
     * @param urlContext
     * @return
     */
    public String getFormRedirectionHash(Map<String, Object> urlContext) {
        StringBuilder hashBuilder = new StringBuilder();
        Iterator<Entry<String, Object>> it = urlContext.entrySet().iterator();
        int size = urlContext.size();
        for (int i = 0; i < size; i++) {
            Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue().toString();
            hashBuilder.append(key + "=" + value);
            if (i < size - 1) {
                hashBuilder.append("&");
            }
        }
        return hashBuilder.toString();
    }

    public String buildLayoutURL(final String bodyContentId, final String taskId, boolean isPageLayout) {
        final StringBuffer theURL = new StringBuffer(GWT.getModuleBaseURL()).append(FORM_LAYOUT_DOWNLOAD);
        if (bodyContentId != null) {
            theURL.append("?" + BODY_CONTENT_ID + "=").append(bodyContentId);
            theURL.append("&" + IS_PAGE_LAYOUT + "=").append(isPageLayout);
            if (taskId != null) {
                theURL.append("&" + TASK_ID_PARAM + "=").append(taskId);
            }
        } else {
            return null;
        }

        return theURL.toString();
    }

}
