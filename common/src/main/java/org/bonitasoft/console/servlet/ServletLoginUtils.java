package org.bonitasoft.console.servlet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.common.exception.NoCredentialsInSessionException;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.security.server.api.ICredentialsEncryptionAPI;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.ow2.bonita.util.SimpleCallbackHandler;

public class ServletLoginUtils {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(ServletLoginUtils.class.getName());
	
    /**
     * Security context creation and login
     * @param request the HTTP request
     * @return the {@link LoginContext}
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws NoCredentialsInSessionException
     */
    public static LoginContext engineLogin(final HttpServletRequest request) throws GeneralSecurityException, IOException, NoCredentialsInSessionException {
        final HttpSession session = request.getSession();
        final String encryptedCredentials = (String) session.getAttribute(LoginServlet.USER_CREDENTIALS_SESSION_PARAM_KEY);
        if (encryptedCredentials == null) {
            throw new NoCredentialsInSessionException("A user credentials property is required in the session by the forms module.");
        }
        String bonitaStoreLoginContext = (String) session.getAttribute(LoginServlet.LOGIN_CONTEXT_SESSION_PARAM_KEY);
        if (bonitaStoreLoginContext == null) {
            bonitaStoreLoginContext = LoginServlet.JAAS_STORE_LOGIN_CONTEXT;
        }
        final ICredentialsEncryptionAPI credentialsAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
        final String decryptedCredentials = credentialsAPI.decryptCredential(encryptedCredentials);
        final String[] credentials = decryptedCredentials.split(ICredentialsEncryptionAPI.USER_CREDENTIALS_SEPARATOR, 2);
        final String userName = credentials[0];
        String password = null;
        if (credentials.length > 1 ) {
            password = credentials[1];
        } else {
            password = "";
        }
        final CallbackHandler handler = new SimpleCallbackHandler(userName, password);
        final LoginContext loginContext = new LoginContext(bonitaStoreLoginContext, handler);
        loginContext.login();
        return loginContext;
    }
    
    /**
     * Retrieve the user rights
     * @param request the HTTP request
     * @return the {@link User}
     * @throws NoCredentialsInSessionException 
     */
    public static User getUser(final HttpServletRequest request) throws NoCredentialsInSessionException {
        final HttpSession session = request.getSession();
        final User user = (User) session.getAttribute(LoginServlet.USER_SESSION_PARAM_KEY);
        if (user == null) {
            throw new NoCredentialsInSessionException("A user property is required in the session by the forms module.");
        }
        return user;
    }
	
    /**
     * Logout
     * 
     * @param loginContext
     */
    public static void engineLogout(final LoginContext loginContext) {
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
