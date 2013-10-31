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
package org.bonitasoft.console.server.login;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.login.ConsoleLoginService;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.security.server.SimpleCallbackHandler;
import org.bonitasoft.console.security.server.api.ICredentialsEncryptionAPI;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.bonitasoft.console.server.users.UserDataStore;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ConsoleLoginServlet extends LoginServlet implements ConsoleLoginService {

    private static final long serialVersionUID = 8957066904730153052L;
    private static final Logger LOGGER = Logger.getLogger(ConsoleLoginServlet.class.getName());

    /**
     * {@inheritDoc}
     */
    public UserProfile consoleLogin(String username, String password, String appLocale) {
        return consoleLogin(username, password, appLocale, JAAS_AUTH_LOGIN_CONTEXT, JAAS_STORE_LOGIN_CONTEXT);
    }

    protected UserProfile consoleLogin(String username, String password, String anAppLocale, String bonitaAuthLoginContext, String bonitaStoreLoginContext) {

        try {
            User user = super.login(username, password, anAppLocale, bonitaAuthLoginContext, bonitaStoreLoginContext);

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, username + " has been granted the access to the U.E. with " + (user.isAdmin() ? "admin" : "user") + " rights.");
            }

            UserProfile theUserProfile = SessionManager.buildSession(this.getThreadLocalRequest(), username, password, user.isAdmin(), user.getLocale(), user.getUserRights(), user.isAutoLogin(), user.isAnonymous(), user.useCredentialTransmission());

            // Load the user preferences before sending it to the client side.
            loadUserPreferences(theUserProfile);

            return theUserProfile;
        } catch (SessionTimeOutException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage);
        }
    }

    /*
     * Load the user preferences from the engine.
     */
    protected void loadUserPreferences(UserProfile aUserProfile) throws SessionTimeOutException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            UserDataStore.loadUserPreferences(aUserProfile);
        } catch (SessionTimeOutException e) {
            throw e;
        } catch (Throwable t) {
            String theErrorMessage = "Error while loading user preferences.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage, t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void consoleLogout() {
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
    public UserProfile consoleIsAlreadyLoggedIn(String anAppLocale) {
        return consoleIsAlreadyLoggedIn(anAppLocale, null, JAAS_STORE_LOGIN_CONTEXT, null);
    }

    /**
     * {@inheritDoc}
     */
    public UserProfile consoleIsAlreadyLoggedIn(String locale, String formID, Map<String, Object> urlContext) {
        return consoleIsAlreadyLoggedIn(locale, formID, JAAS_STORE_LOGIN_CONTEXT, urlContext);
    }

    /**
     * Checks whether the client is already logged in or not.
     * 
     * @param appLocale
     * @param autoLogin
     * @param formID
     * @param bonitaStoreLoginContext
     * @param callback
     */
    protected UserProfile consoleIsAlreadyLoggedIn(String anAppLocale, String formID, String bonitaStoreLoginContext, Map<String, Object> urlContext) {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        try {
            User theUser = super.isAlreadyLoggedIn(anAppLocale, formID, bonitaStoreLoginContext, urlContext);
            if (theUser == null) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "User is NOT logged in yet.");
                }
                return null;
            } else {
                // The data may have changed. Force to rebuild profile.
                final UserProfile theUserProfile = SessionManager.buildSession(theRequest, theUser.getUsername(), "", theUser.isAdmin(), theUser.getLocale(), theUser.getUserRights(), theUser.isAutoLogin(), theUser.isAnonymous(),
                        theUser.useCredentialTransmission());
                // Load the user preferences before sending it to the client side.
                loadUserPreferences(theUserProfile);
                return theUserProfile;
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
    public UserProfile consoleLogin(String aTemporaryToken, String anAppLocale) {

        return consoleLogin(aTemporaryToken, anAppLocale, JAAS_AUTH_LOGIN_CONTEXT, JAAS_STORE_LOGIN_CONTEXT);
    }

    protected UserProfile consoleLogin(String aTemporaryToken, String anAppLocale, String bonitaAuthLoginContext, String bonitaStoreLoginContext) {

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

            User user = super.login(username, password, anAppLocale, bonitaAuthLoginContext, bonitaStoreLoginContext);

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, username + " has been granted the access to the U.E. with " + (user.isAdmin() ? "admin" : "user") + " rights.");
            }
            UserProfile theUserProfile = SessionManager.buildSession(this.getThreadLocalRequest(), username, password, user.isAdmin(), user.getLocale(), user.getUserRights(), user.isAutoLogin(), user.isAnonymous(), user.useCredentialTransmission());

            // Load the user preferences before sending it to the client side.
            loadUserPreferences(theUserProfile);

            return theUserProfile;

        } catch (SessionTimeOutException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage);
        }
    }

}
