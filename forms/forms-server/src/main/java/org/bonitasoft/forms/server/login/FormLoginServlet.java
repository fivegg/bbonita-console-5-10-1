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
package org.bonitasoft.forms.server.login;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.security.server.SimpleCallbackHandler;
import org.bonitasoft.console.security.server.api.ICredentialsEncryptionAPI;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.bonitasoft.forms.client.rpc.login.FormLoginService;

/**
 * @author Haojie Yuan
 * 
 */
public class FormLoginServlet extends LoginServlet implements FormLoginService {

    /**
	 * UID
	 */
	private static final long serialVersionUID = 8448166941739565946L;
	
	/**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(FormLoginServlet.class.getName());
    
    /**
     * process definition uuid
     */
    public static final String PROCESS_UUID = "process";
    
    /**
     * {@inheritDoc}
     */
    public User formLogin(String aTemporaryToken, String anAppLocale) {
        return formLogin(aTemporaryToken, anAppLocale, JAAS_AUTH_LOGIN_CONTEXT, JAAS_STORE_LOGIN_CONTEXT);
    }

    protected User formLogin(String aTemporaryToken, String anAppLocale, String bonitaAuthLoginContext, String bonitaStoreLoginContext) {

        HttpServletRequest request = this.getThreadLocalRequest();
        HttpSession session = request.getSession();
        ICredentialsEncryptionAPI credentialsAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
        try {
            CallbackHandler handler = new SimpleCallbackHandler(session.getId(), "");
            LoginContext loginContext = new LoginContext(bonitaStoreLoginContext, handler);
            loginContext.login();
            String theIdentityKey = credentialsAPI.getCredentialsFromToken(aTemporaryToken);
            loginContext.logout();
            String decryptedCredentials;
            try {
                decryptedCredentials = credentialsAPI.decryptCredential(theIdentityKey);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error occured while trying to decrypt user identity key.");
            }
            String[] credentials = decryptedCredentials.split(ICredentialsEncryptionAPI.USER_CREDENTIALS_SEPARATOR, 2);
            String username;
            if (credentials.length > 0) {
                username = credentials[0];
            } else {
                username = "";
            }
            String password;
            if (credentials.length > 1) {
              password = credentials[1];
            } else {
              password = "";
            }

            User theUser = super.login(username, password, anAppLocale, bonitaAuthLoginContext, bonitaStoreLoginContext);

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, username + " has been granted the access to the U.E. with " + (theUser.isAdmin() ? "admin" : "user") + " rights.");
            }

            return theUser;
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public User formLogin(String username, String password, String appLocale) {
        return formLogin(username, password, appLocale, JAAS_AUTH_LOGIN_CONTEXT, JAAS_STORE_LOGIN_CONTEXT);
    }

    protected User formLogin(String username, String password, String anAppLocale, String bonitaAuthLoginContext, String bonitaStoreLoginContext) {

        try {
            User theUser = super.login(username, password, anAppLocale, bonitaAuthLoginContext, bonitaStoreLoginContext);

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, username + " has been granted the access to the U.E. with " + (theUser.isAdmin() ? "admin" : "user") + " rights.");
            }

            return theUser;
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void formLogout() {
        try {
            super.logout();
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "User is NOT logged in anymore.");
            }
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public User formIsAlreadyLoggedIn(String anAppLocale) {
        return formIsAlreadyLoggedIn(anAppLocale, null, JAAS_STORE_LOGIN_CONTEXT, null);
    }
    
    /**
     * {@inheritDoc}
     */
    public User formIsAlreadyLoggedIn(String anAppLocale, String formID, Map<String, Object> urlContext) {
        return formIsAlreadyLoggedIn(anAppLocale, formID, JAAS_STORE_LOGIN_CONTEXT, urlContext);
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see org.bonitasoft.form.client.FormLoginService#formIsAlreadyLoggedIn ()
     */
    protected User formIsAlreadyLoggedIn(String anAppLocale, String formID, String bonitaStoreLoginContext, Map<String, Object> urlContext) {
        try {
            User theUser = super.isAlreadyLoggedIn(anAppLocale, formID, bonitaStoreLoginContext, urlContext);
            if (theUser == null) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "User is NOT logged in yet.");
                }
                return null;
            }
            return theUser;
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage);
        }
    }
    
}
