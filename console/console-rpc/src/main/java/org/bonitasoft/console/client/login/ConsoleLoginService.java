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
package org.bonitasoft.console.client.login;

import java.util.Map;

import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.security.client.LoginService;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface ConsoleLoginService extends LoginService {

	/**
	 * Check the user identity based on login / password pair..
	 * 
	 * @param login
	 * @param password
	 * @param appLocale
	 * @return the user profile (in case of success)
	 */
	UserProfile consoleLogin(String login, String password, String appLocale);

	/**
	 * Check the identity of the user based on a temporary token.
	 * 
	 * @param aTemporaryToken
	 * @param appLocale
	 * @param callback
	 * @return the user profile (in case of success)
	 */
	UserProfile consoleLogin(String aTemporaryToken, String appLocale);

	/**
	 * Logout.
	 */
	void consoleLogout();

	/**
	 * Checks whether the client is already logged in or not.<br>
	 * The profile of the currently logged in user is returned.<br>
	 * If no user is connected, then return null.
	 * @param appLocale
	 * @return the user profile of the user currently logged in.
	 */
	UserProfile consoleIsAlreadyLoggedIn(String appLocale);
	
	/**
     * Checks whether the client is already logged in or not.
     * 
     * @param locale
     * @param urlContext 
     * @param formID 
     */
	UserProfile consoleIsAlreadyLoggedIn(String locale, String formID, Map<String, Object> urlContext);
	
}
