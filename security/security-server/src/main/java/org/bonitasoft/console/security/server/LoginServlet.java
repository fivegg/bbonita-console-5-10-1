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

import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.security.client.LoginService;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.client.users.UserRights;
import org.bonitasoft.console.security.server.accessor.SecurityProperties;
import org.bonitasoft.console.security.server.api.ICredentialsEncryptionAPI;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LoginServlet extends RemoteServiceServlet implements LoginService {

    /**
     * UID
     */
    private static final long serialVersionUID = 8957066904730153052L;

    /**
     * Encrypted user credentials session param key
     */
    public static final String USER_CREDENTIALS_SESSION_PARAM_KEY = "userCredentials";

    /**
     * User param name in session
     */
    public static final String USER_SESSION_PARAM_KEY = "user";

    /**
     * User profile param name in session
     */
    public static final String USER_PROFILE_SESSION_PARAM_KEY = "userProfile";

    /**
     * login context param name in session
     */
    public static final String LOGIN_CONTEXT_SESSION_PARAM_KEY = "loginContext";

    /**
     * JAAS Auth login context
     */
    public static final String JAAS_AUTH_LOGIN_CONTEXT = "BonitaAuth";

    /**
     * JAAS Store login context
     */
    public static final String JAAS_STORE_LOGIN_CONTEXT = "BonitaStore";

    /**
     * Locale cookie name
     */
    public static final String BOS_LOCALE_COOKIE_NAME = "BOS_Locale";

    /**
     * auto login mode : indicates that the user should be automatically logged in
     */
    public static final String AUTO_LOGIN_PARAM = "autoLogin";

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
   
    /**
     * Indicates that the properties have been loaded
     */
    protected static boolean defaultPropertiesLoaded = false;
    
    /**
     * indicates that the credential transmission mechanism should be used
     */
    protected static boolean useCredentialsTransmission = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        loadProperties();
        super.init();
    }
    
    protected void loadProperties() {
        if (!defaultPropertiesLoaded) {
            useCredentialsTransmission = SecurityAPIFactory.getPreferencesAPI().getUseCredentialTransmissionPreference();
            defaultPropertiesLoaded = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public User login(final String username, final String password, final String appLocale) {
        return login(username, password, appLocale, JAAS_AUTH_LOGIN_CONTEXT, JAAS_STORE_LOGIN_CONTEXT);
    }

    protected User login(final String username, final String password, final String appLocale, final String bonitaAuthLoginContext, final String bonitaStoreLoginContext) {

        LoginContext loginContext = null;
        final CallbackHandler handler = new SimpleCallbackHandler(username, password);
        try {
            loginContext = new LoginContext(bonitaAuthLoginContext, handler);
            loginContext.login();
            loginContext.logout();
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, username + " successfully logged in.");
            }
        } catch (final javax.security.auth.login.LoginException e) {
            try {
                // make another try after refresh
                javax.security.auth.login.Configuration.getConfiguration().refresh();
                loginContext = new LoginContext(bonitaAuthLoginContext, handler);
                loginContext.login();
                loginContext.logout();
            } catch (final javax.security.auth.login.LoginException e2) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Problem while login.", e2);
                }
                throw new RuntimeException("Authentication failed for user '" + username + "'");
            }
        }
        final HttpServletRequest request = this.getThreadLocalRequest();
        final String userLocale = getUserLocale(request, appLocale);
        final HttpSession session = request.getSession();
        final ICredentialsEncryptionAPI credentialsAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
        try {
            final String encryptedCredentials = credentialsAPI.encryptCredential(username + ICredentialsEncryptionAPI.USER_CREDENTIALS_SEPARATOR + password);
            loginContext = new LoginContext(bonitaStoreLoginContext, handler);
            loginContext.login();
            final boolean isUserAdmin = SecurityAPIFactory.getPreferencesAPI().isUserAdmin(username);
            final UserRights userRights = SecurityAPIFactory.getPrivilegesAPI().getUserRights(username);
            final User user = new User(username, isUserAdmin, userLocale, userRights, useCredentialsTransmission);
            session.setAttribute(USER_CREDENTIALS_SESSION_PARAM_KEY, encryptedCredentials);
            session.setAttribute(USER_SESSION_PARAM_KEY, user);
            session.setAttribute(LOGIN_CONTEXT_SESSION_PARAM_KEY, bonitaStoreLoginContext);
            return user;
        } catch (final Exception e) {
            final String errorMessage = "Problem while building the user object.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new RuntimeException(errorMessage);
        } finally {
            logout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public User login(final String temporaryToken, final String appLocale) {

        return login(temporaryToken, appLocale, JAAS_AUTH_LOGIN_CONTEXT, JAAS_STORE_LOGIN_CONTEXT);
    }

    protected User login(final String temporaryToken, final String appLocale, final String bonitaAuthLoginContext, final String bonitaStoreLoginContext) {

        LoginContext loginContext = null;
        final HttpServletRequest request = this.getThreadLocalRequest();
        final String userLocale = getUserLocale(request, appLocale);
        final HttpSession session = request.getSession();
        final ICredentialsEncryptionAPI credentialsAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
        try {
            CallbackHandler handler = new SimpleCallbackHandler(session.getId(), "");
            loginContext = new LoginContext(bonitaStoreLoginContext, handler);
            loginContext.login();
            final String encryptedCredentials = credentialsAPI.getCredentialsFromToken(temporaryToken);
            loginContext.logout();
            String decryptedCredentials;
            try {
                decryptedCredentials = credentialsAPI.decryptCredential(encryptedCredentials);
            } catch (final Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error occured while trying to decrypt user identity key.");
            }
            final String[] credentials = decryptedCredentials.split(ICredentialsEncryptionAPI.USER_CREDENTIALS_SEPARATOR, 2);
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
            handler = new SimpleCallbackHandler(username, password);
            loginContext = new LoginContext(bonitaAuthLoginContext, handler);
            loginContext.login();
            loginContext.logout();
            loginContext = new LoginContext(bonitaStoreLoginContext, handler);
            loginContext.login();
            final boolean isUserAdmin = SecurityAPIFactory.getPreferencesAPI().isUserAdmin(username);
            final UserRights userRights = SecurityAPIFactory.getPrivilegesAPI().getUserRights(username);
            final User user = new User(username, isUserAdmin, userLocale, userRights, useCredentialsTransmission);
            session.setAttribute(USER_CREDENTIALS_SESSION_PARAM_KEY, encryptedCredentials);
            session.setAttribute(USER_SESSION_PARAM_KEY, user);
            session.setAttribute(LOGIN_CONTEXT_SESSION_PARAM_KEY, bonitaStoreLoginContext);
            return user;

        } catch (final Exception e) {
            final String errorMessage = "Auto authentication failed.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new RuntimeException(errorMessage);
        } finally {
            logout(loginContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void logout() {
        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();
        session.removeAttribute(USER_CREDENTIALS_SESSION_PARAM_KEY);
        session.removeAttribute(USER_SESSION_PARAM_KEY);
        session.removeAttribute(LOGIN_CONTEXT_SESSION_PARAM_KEY);
        session.removeAttribute(CredentialsEncryptionServlet.USERNAME_SESSION_PARAM);
        // Mandatory for console logout
        session.removeAttribute(USER_PROFILE_SESSION_PARAM_KEY);
    }

    /**
     * {@inheritDoc}
     */
    public User isAlreadyLoggedIn(final String appLocale, final String formID, final String storeLoginContext, final Map<String, Object> urlContext) {

        User user = null;
        boolean autoLogin = false;

        final HttpServletRequest request = this.getThreadLocalRequest();
        final HttpSession session = request.getSession();
        String currentStoreLoginContext = (String) session.getAttribute(LOGIN_CONTEXT_SESSION_PARAM_KEY);
        if (currentStoreLoginContext == null) {
            currentStoreLoginContext = JAAS_STORE_LOGIN_CONTEXT;
        }
        if (urlContext != null) {
            if (urlContext.containsKey(AUTO_LOGIN_PARAM)) {
                autoLogin = Boolean.valueOf(urlContext.get(AUTO_LOGIN_PARAM).toString());
            }
        }
        // User tries to use another login context
        if (!autoLogin && !currentStoreLoginContext.equals(storeLoginContext)) {
            logout();
        } else {
            final String userLocale = getUserLocale(request, appLocale);
            user = (User) session.getAttribute(USER_SESSION_PARAM_KEY);
            if (user == null) {
                final ICredentialsEncryptionAPI credentialsAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
                final String encryptedCredentials = (String) session.getAttribute(USER_CREDENTIALS_SESSION_PARAM_KEY);
                if (encryptedCredentials != null) {
                    // Login with some encrypted credentials only (for the SSO filters)
                    user = loginWithTrustedCredentials(credentialsAPI, encryptedCredentials, session, userLocale, storeLoginContext);
                } else if (autoLogin) {
                    // Auto login for the dedicated applications that support it
                    user = autoLogin(credentialsAPI, session, userLocale, urlContext, storeLoginContext);
                }
            } else {
                // User already exists in session. Reload is requested. Update user rights and locale.
                refreshUser(user, userLocale, storeLoginContext);
            }
        }
        return user;
    }

    protected void refreshUser(final User user, final String userLocale, final String storeLoginContext) {
        LoginContext loginContext = null;
        try {
            final CallbackHandler handler = new SimpleCallbackHandler(user.getUsername(), "");
            loginContext = new LoginContext(storeLoginContext, handler);
            loginContext.login();
            final UserRights userRights = SecurityAPIFactory.getPrivilegesAPI().getUserRights(user.getUsername());
            user.setUserRights(userRights);
            if (!userLocale.equals(user.getLocale())) {
                user.setLocale(userLocale);
            }
        } catch (final Exception e) {
            final String errorMessage = "Authentication check failed.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new RuntimeException(errorMessage);
        } finally {
            logout(loginContext);
        }
    }

    protected User autoLogin(final ICredentialsEncryptionAPI credentialsAPI, final HttpSession session, final String userLocale, final Map<String, Object> urlContext, final String storeLoginContext) {
        User user = null;
        LoginContext loginContext = null;
        try {
            SecurityProperties securityProperties = SecurityAPIFactory.getPrivilegesAPI().getSecurityProperties(urlContext);
            if (securityProperties.allowAutoLogin()) {
                String username = securityProperties.getAutoLoginUserName();
                boolean isAnonymous = false;
                if (username == null || username.trim().length() == 0) {
                    username = session.getId();
                    isAnonymous = true;
                }
                final CallbackHandler handler = new SimpleCallbackHandler(username, "");
                loginContext = new LoginContext(storeLoginContext, handler);
                loginContext.login();
                final UserRights userRights = SecurityAPIFactory.getPrivilegesAPI().getUserRights(username);
                user = new User(username, false, userLocale, userRights, useCredentialsTransmission);
                user.setAutoLogin(true);
                user.setAnonymous(isAnonymous);
                session.setAttribute(USER_SESSION_PARAM_KEY, user);
                final String encryptedCredentials = credentialsAPI.encryptCredential(username);
                session.setAttribute(USER_CREDENTIALS_SESSION_PARAM_KEY, encryptedCredentials);
                session.setAttribute(LOGIN_CONTEXT_SESSION_PARAM_KEY, storeLoginContext);
            }
        } catch (final Exception e) {
            final String errorMessage = "Auto login failed.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new RuntimeException(errorMessage);
        } finally {
            logout(loginContext);
        }
        return user;
    }

    protected User loginWithTrustedCredentials(final ICredentialsEncryptionAPI credentialsAPI, final String encryptedCredentials, final HttpSession session, final String userLocale, final String storeLoginContext) {
        User user = null;
        LoginContext loginContext = null;
        try {
            final String decryptedCredentials = credentialsAPI.decryptCredential(encryptedCredentials);
            final String[] credentials = decryptedCredentials.split(ICredentialsEncryptionAPI.USER_CREDENTIALS_SEPARATOR, 2);
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
            final CallbackHandler handler = new SimpleCallbackHandler(username, password);
            loginContext = new LoginContext(storeLoginContext, handler);
            loginContext.login();
            final boolean isUserAdmin = SecurityAPIFactory.getPreferencesAPI().isUserAdmin(username);
            final UserRights userRights = SecurityAPIFactory.getPrivilegesAPI().getUserRights(username);
            user = new User(username, isUserAdmin, userLocale, userRights, useCredentialsTransmission);
            session.setAttribute(USER_SESSION_PARAM_KEY, user);
        } catch (final Exception e) {
            final String errorMessage = "Authentication check failed.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new RuntimeException(errorMessage);
        } finally {
            logout(loginContext);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    public String generateTemporaryToken() {
        LoginContext loginContext = null;
        try {
            final HttpServletRequest request = this.getThreadLocalRequest();
            final HttpSession session = request.getSession();
            final String encryptedCredentials = (String) session.getAttribute(USER_CREDENTIALS_SESSION_PARAM_KEY);
            if (encryptedCredentials == null) {
                throw new Exception("A user credentials property is required in the session.");
            }
            String bonitaStoreLoginContext = (String) session.getAttribute(LOGIN_CONTEXT_SESSION_PARAM_KEY);
            if (bonitaStoreLoginContext == null) {
                bonitaStoreLoginContext = JAAS_STORE_LOGIN_CONTEXT;
            }
            final ICredentialsEncryptionAPI credentialsAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
            final String decryptedCredentials = credentialsAPI.decryptCredential(encryptedCredentials);
            final String[] credentials = decryptedCredentials.split(ICredentialsEncryptionAPI.USER_CREDENTIALS_SEPARATOR, 2);
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
            final CallbackHandler handler = new SimpleCallbackHandler(username, password);
            loginContext = new LoginContext(bonitaStoreLoginContext, handler);
            loginContext.login();
            return credentialsAPI.generateTemporaryToken(encryptedCredentials);
        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while generating the temporary token");
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            logout(loginContext);
        }
    }

    /**
     * @param request the client request
     * @return the user locale as a string if it's supported, "en" otherwise
     */
    protected String getUserLocale(final HttpServletRequest request, final String appLocale) {
        String userLocaleStr = null;
        final String theLocaleCookieName = BOS_LOCALE_COOKIE_NAME;
        final Cookie theCookies[] = request.getCookies();
        Cookie theCookie = null;
        if (theCookies != null) {
            for (int i = 0; i < theCookies.length; i++) {
                if (theCookies[i].getName().equals(theLocaleCookieName)) {
                    theCookie = theCookies[i];
                    break;
                }
            }
        }
        if (theCookie != null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("LoginServlet using locale from cookies: " + theCookie.getValue());
            }
            if ("default".equals(theCookie.getValue())) {
                userLocaleStr = new Locale("en").toString();
            } else {
                userLocaleStr = new Locale(theCookie.getValue()).toString();
            }
        } else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("LoginServlet using locale from request: " + request.getLocale().getDisplayName());
            }
            userLocaleStr = appLocale;
        }
        return userLocaleStr;
    }

    /**
     * Logout
     * 
     * @param loginContext
     */
    protected void logout(final LoginContext loginContext) {
        try {
            if (loginContext != null) {
                loginContext.logout();
            }
        } catch (final LoginException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while performing the logout", e);
            }
        }
    }
}
