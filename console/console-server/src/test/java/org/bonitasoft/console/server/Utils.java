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
package org.bonitasoft.console.server;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * @author Nicolas Chabanoles
 *
 */
public class Utils {

	static {
		// Initialize the Jaas login configuration with a default value
		final String defaultLoginFile = "src/test/resources/jaas-standard.cfg";
		final String loginFile = System.getProperty(BonitaConstants.JAAS_PROPERTY, defaultLoginFile);
		if (loginFile.equals(defaultLoginFile)) {
			System.setProperty(BonitaConstants.JAAS_PROPERTY, defaultLoginFile);
		}
	}
	
	private static LoginContext loginContext;
	
	public static synchronized void login(String aUserName, String aPassword) throws LoginException {
		if(loginContext!=null) {
			throw new LoginException("You must logout before trying to log in!");
		}
		loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(aUserName, aPassword));
		loginContext.login();
		
	}
	
	public static synchronized void logout() throws LoginException {
		if (loginContext != null) {
			loginContext.logout();
			loginContext = null;
		} else {
			throw new LoginException("You must login before trying to log out!");
		}
	}

	public static boolean isLogin() {
		
		return loginContext != null;
	}
}
