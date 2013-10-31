/**
 * Copyright (C) 2010 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 */
package org.bonitasoft.console.security.server.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.security.server.api.ICredentialsEncryptionAPI;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;

/**
 * This filter put the credentials of the remote user in session in order to be automatically logged in.
 * This filter is intended to be used with SSO solutions like CAS
 * @author Anthony Birembaut
 *
 */
public class SetCredentialsInSessionFilter implements Filter {

    protected ICredentialsEncryptionAPI credentialsEncryptionAPI;
    
    private static final Logger LOGGER = Logger.getLogger(SetCredentialsInSessionFilter.class.getName());

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            final HttpServletRequest httpRequest = (HttpServletRequest)request;
            final String username = httpRequest.getRemoteUser();
            if (username != null && username.length() > 0) {
                final String encryptedCredentials = credentialsEncryptionAPI.encryptCredential(username + ICredentialsEncryptionAPI.USER_CREDENTIALS_SEPARATOR);
                final HttpSession session = httpRequest.getSession();
                session.setAttribute(LoginServlet.USER_CREDENTIALS_SESSION_PARAM_KEY, encryptedCredentials);
                //useful for multi-tenancy
                String bonitaStoreLoginContext = LoginServlet.JAAS_STORE_LOGIN_CONTEXT;
                final String domainName = httpRequest.getParameter("domain");
                if (domainName != null) {
                    bonitaStoreLoginContext = bonitaStoreLoginContext + "-" + domainName;
                }
                session.setAttribute(LoginServlet.LOGIN_CONTEXT_SESSION_PARAM_KEY, bonitaStoreLoginContext);
            } else {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "The HttpServletRequest remoteUser should be initialized in order for the SetCredentialsInSessionFilter to work");
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error while setting the credentials in session");
            throw new ServletException(e);
        }
        filterChain.doFilter(request, response);
    }

    public void init(final FilterConfig filterConfig) throws ServletException {
        credentialsEncryptionAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
    }
    
    public void destroy() {

    }
}
