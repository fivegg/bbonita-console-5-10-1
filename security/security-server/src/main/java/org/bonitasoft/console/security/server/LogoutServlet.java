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
package org.bonitasoft.console.security.server;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet used to logout from the applications
 * 
 * @author Anthony Birembaut
 *
 */
public class LogoutServlet extends HttpServlet {
	
    /**
     * UID
     */
    private static final long serialVersionUID = 739607235407639011L;

    /**
     * the URL param for the redirection URL after login
     */
    protected static final String REDIRECT_URL_PARAM = "redirectUrl";

    /**
     * the domain param
     */
    protected static final String DOMAIN_PARAM = "domain";
    
    /**
     * the URL of the login page
     */
    protected static final String LOGIN_PAGE = "login.jsp";
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        final HttpSession session = request.getSession();
        session.removeAttribute(LoginServlet.USER_CREDENTIALS_SESSION_PARAM_KEY);
        session.removeAttribute(LoginServlet.USER_SESSION_PARAM_KEY);
        session.removeAttribute(LoginServlet.LOGIN_CONTEXT_SESSION_PARAM_KEY);
        session.removeAttribute(CredentialsEncryptionServlet.USERNAME_SESSION_PARAM);
        // Mandatory for console logout
        session.removeAttribute(LoginServlet.USER_PROFILE_SESSION_PARAM_KEY);
        
        final String redirectURL = request.getParameter(REDIRECT_URL_PARAM);
        final String domain = request.getParameter(DOMAIN_PARAM);
        String encodedRedirectURL;
        if (redirectURL != null) {
        	encodedRedirectURL = URLEncoder.encode(redirectURL, "UTF-8");
        } else {
        	encodedRedirectURL = "";
        }
        if (domain != null) {
        	encodedRedirectURL += "&" + DOMAIN_PARAM + "=" + domain;
        }

        response.sendRedirect(LOGIN_PAGE + "?" + REDIRECT_URL_PARAM + "=" + encodedRedirectURL);
    }
    
}
