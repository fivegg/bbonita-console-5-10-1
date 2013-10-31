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

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.security.client.users.UserRights;
import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.security.server.api.ICredentialsEncryptionAPI;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.bonitasoft.console.server.users.UserDataStore;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class SessionManager {

  public static final String USER_PROFILE_SESSION_PARAM_KEY = "userProfile";

  public static LoginContext login(HttpServletRequest aRequest) throws SessionTimeOutException {
    HttpSession theSession = aRequest.getSession();
    UserProfile theUserProfile = (UserProfile) theSession.getAttribute(USER_PROFILE_SESSION_PARAM_KEY);
    if (theUserProfile == null) {
      throw new SessionTimeOutException();
    }
    String theBonitaStoreLoginContext = (String) theSession.getAttribute(LoginServlet.LOGIN_CONTEXT_SESSION_PARAM_KEY);
    if (theBonitaStoreLoginContext == null) {
        theBonitaStoreLoginContext = LoginServlet.JAAS_STORE_LOGIN_CONTEXT;
    }
    try {
      String theLogin = theUserProfile.getUsername();
      String theUserCredentials = (String) theSession.getAttribute(ConsoleLoginServlet.USER_CREDENTIALS_SESSION_PARAM_KEY);
      ICredentialsEncryptionAPI credentialsAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
      String decryptedCredentials = credentialsAPI.decryptCredential(theUserCredentials);
      String[] credentials = decryptedCredentials.split(ICredentialsEncryptionAPI.USER_CREDENTIALS_SEPARATOR, 2);
      String thePassword = null;
      if (credentials.length > 1) {
        thePassword = credentials[1];
      } else {
        thePassword = "";
      }
      return login(theBonitaStoreLoginContext, theLogin, thePassword);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e.getLocalizedMessage());
    }

  }

  private static LoginContext login(final String bonitaStoreLoginContext, final String aLogin, final String aPassword) throws LoginException {

    CallbackHandler handler = new SimpleCallbackHandler(aLogin, aPassword);
    LoginContext loginContext;
    try {
      loginContext = new LoginContext(bonitaStoreLoginContext, handler);
      loginContext.login();
      return loginContext;
    } catch (LoginException e) {
      // make another try after refresh
      javax.security.auth.login.Configuration.getConfiguration().refresh();
      loginContext = new LoginContext(bonitaStoreLoginContext, handler);
      loginContext.login();
      return loginContext;
    }
    

  }

  public static void logout(LoginContext aContext) {
    try {
      aContext.logout();
    } catch (LoginException e) {
      throw new RuntimeException(e.getLocalizedMessage());
    }

  }

  public static String getUserCredentials(HttpServletRequest aRequest) {
    HttpSession theSession = aRequest.getSession();
    return (String) theSession.getAttribute(ConsoleLoginServlet.USER_CREDENTIALS_SESSION_PARAM_KEY);
  }

  public static UserProfile getUserProfile(HttpServletRequest aRequest) {

    HttpSession session = aRequest.getSession();
    return (UserProfile) session.getAttribute(USER_PROFILE_SESSION_PARAM_KEY);
  }

  public static UserProfile buildSession(HttpServletRequest aRequest, String aUserName, String aPassword, boolean isAdmin, String aLocale, UserRights aUserRights, boolean isAutoLogin, boolean isAnonymous, boolean useCredentialTransmission) throws Exception {

    HttpSession theSession = aRequest.getSession();

    String theBonitaStoreLoginContext = (String) theSession.getAttribute(LoginServlet.LOGIN_CONTEXT_SESSION_PARAM_KEY);
    if (theBonitaStoreLoginContext == null) {
        theBonitaStoreLoginContext = LoginServlet.JAAS_STORE_LOGIN_CONTEXT;
    }
    
    LoginContext loginContext = login(theBonitaStoreLoginContext, aUserName, aPassword);

    UserProfile theUserProfile = UserDataStore.getUserProfile(aUserName, isAdmin, aLocale, aUserRights, useCredentialTransmission);
    theUserProfile.setAnonymous(isAnonymous);
    theUserProfile.setAutoLogin(isAutoLogin);

    logout(loginContext);

    theSession.setAttribute(USER_PROFILE_SESSION_PARAM_KEY, theUserProfile);
    return theUserProfile;
  }
}
