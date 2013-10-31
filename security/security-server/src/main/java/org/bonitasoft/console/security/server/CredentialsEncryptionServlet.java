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
package org.bonitasoft.console.security.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.security.server.api.ICredentialsEncryptionAPI;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;

/**
 * @author Anthony Birembaut
 *
 */
public class CredentialsEncryptionServlet extends HttpServlet {
    
    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(CredentialsEncryptionServlet.class.getName());
    
    /**
     * UID
     */
    private static final long serialVersionUID = 739607235407639011L;

    /**
     * the URL param for the redirection URL after login
     */
    protected static final String REDIRECT_URL_PARAM = "redirectUrl";
    
    /**
     * the URL param for the encrypted credentials 
     */
    protected static final String CREDENTIALS_URL_PARAM = "identityKey";
    
    /**
     * the URL param for thelocale to use
     */
    protected static final String LOCALE_URL_PARAM = "locale";
    
    /**
     * the request param for the username
     */
    protected static final String USERNAME_REQUEST_PARAM = "username";
    
    /**
     * the request param for the password
     */
    protected static final String PASSWORD_REQUEST_PARAM = "password";

    /**
     * the request param for the locale
     */
    protected static final String LOCALE_PARAMETER_NAME = "locale";
    
    /**
     * the request param for the form locale
     */
    protected static final String FORM_LOCALE_PARAMETER_NAME = "formLocale";

    /**
     * the request param for the username
     */
    public static final String USERNAME_SESSION_PARAM = "username";
    
    /**
     * the home page request
     */
    public static final String CONSOLE_PREFIX = "/console";
    
    /**
     * console home page keyword
     */
    public static final String UI_MODE_PARAM = "ui=";
    
    public static final String HOMEPAGE = "homepage";
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        
        String username = request.getParameter(USERNAME_REQUEST_PARAM);
        String password = request.getParameter(PASSWORD_REQUEST_PARAM);
        final String locale = getUserLocale(request);
        String formLocale = request.getParameter(FORM_LOCALE_PARAMETER_NAME);
        if(formLocale == null) {
        	formLocale = locale;
        }
        final String redirectURL = request.getParameter(REDIRECT_URL_PARAM);
        final ICredentialsEncryptionAPI credentialsAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
        String temporaryToken = null;
        try {
            final String encryptedCredentials = credentialsAPI.encryptCredential(username + ICredentialsEncryptionAPI.USER_CREDENTIALS_SEPARATOR + password);
            final CallbackHandler handler = new SimpleCallbackHandler(username, password);
            final LoginContext loginContext = new LoginContext(getLoginContext(request), handler);
            loginContext.login();
            temporaryToken = credentialsAPI.generateTemporaryToken(encryptedCredentials);
            loginContext.logout();
        } catch (final LoginException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (final GeneralSecurityException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            throw new ServletException(e);
        }
        request.getSession().setAttribute(USERNAME_SESSION_PARAM, username);
        if (redirectURL != null) {
            response.sendRedirect(buildRedirectionUrl(redirectURL, locale, formLocale, temporaryToken));
        }
    }
    
    protected String getLoginContext(final HttpServletRequest request) {
        return LoginServlet.JAAS_STORE_LOGIN_CONTEXT;
    }
    
    /**
     * Build the redirection URL
     * @param redirectURL
     * @param locale
     * @param temporaryToken
     * @return
     */
    protected String buildRedirectionUrl(final String redirectURL, final String locale, final String formLocale, final String temporaryToken) {

        try {
            final String decodedURL = URLDecoder.decode(redirectURL, "UTF-8");
            String gwtTokens;
            String theURL;
            if (decodedURL.contains("#")) {
                gwtTokens = decodedURL.substring(decodedURL.lastIndexOf("#"));
                theURL = decodedURL.substring(0, decodedURL.lastIndexOf("#"));
            } else {
                gwtTokens = "";
                theURL = decodedURL;
            }
            
            String formLocaleParam = "";
            if (!decodedURL.contains(UI_MODE_PARAM) && decodedURL.contains(HOMEPAGE)) {
            	formLocaleParam = "&" + FORM_LOCALE_PARAMETER_NAME + "=" + formLocale;
            }
            //Locale Handling
            String localeSuffix;
            if (theURL.contains("?")) {
            	localeSuffix = "&" + LOCALE_URL_PARAM + "=" + locale;
            	localeSuffix += formLocaleParam;
            } else {
            	localeSuffix = "?" + LOCALE_URL_PARAM + "=" + locale;
            	localeSuffix += formLocaleParam;
            }
            //Identity token Handling
            String urlHash;
            if (temporaryToken == null) {
                urlHash = gwtTokens;
            } else if (gwtTokens.length() > 0) {
                urlHash = gwtTokens + "&" + CREDENTIALS_URL_PARAM + "=" + temporaryToken;
            } else {
                urlHash = "#" + CREDENTIALS_URL_PARAM + "=" + temporaryToken;
            }
            return theURL + localeSuffix + urlHash;
        } catch (final UnsupportedEncodingException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            return "";
        }
    }

    /**
     * @param request the client request
     * @return the user locale as a string if it's supported, "en" otherwise
     */
    protected String getUserLocale(final HttpServletRequest request) {
        String localeName = request.getParameter(LOCALE_PARAMETER_NAME);
        if(localeName != null && localeName.length() > 0) {
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("*** Servlet using locale from request parameter: " + localeName);
            }
        } else {
            final Locale userLocale = request.getLocale();
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.fine("*** Servlet using locale from request: " + request.getLocale().toString());
            }
            localeName = userLocale.toString();
        }
        return localeName;
    }
}
