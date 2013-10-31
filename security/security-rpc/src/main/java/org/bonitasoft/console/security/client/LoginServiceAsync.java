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
package org.bonitasoft.console.security.client;

import java.util.Map;

import org.bonitasoft.console.security.client.users.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface LoginServiceAsync {

	/**
	 * Check the identity of the user based on the login / password pair.
	 * 
	 * @param login
	 * @param password
	 * @param appLocale
	 * @param callback
	 */
	void login(String login, String password, String appLocale, AsyncCallback<User> callback);

    /**
     * Check the user identity based on a temporary token
     * 
     * @param temporaryToken
     * @param appLocale
     * @param callback
     */
    void login(String temporaryToken, String appLocale, AsyncCallback<User> callback);
	
	/**
	 * Logout the user currently logged in.
	 * @param appLocale
	 * @param callback
	 */
	void logout(AsyncCallback<Void> callback);
	
	/**
     * Checks whether the client is already logged in or not.<br>
     * If no user is connected, then return null.
     * @param appLocale
     * @param processDefinitionUUIDStr
	 * @param storeLoginContext 
     * @param callback
     */
    void isAlreadyLoggedIn(String appLocale, String formID, String storeLoginContext, Map<String, Object> urlContext, AsyncCallback<User> callback);
    
    /**
     * Retrieve the generated token
     */
    void generateTemporaryToken(AsyncCallback<String> aCallBackHandler);

}
