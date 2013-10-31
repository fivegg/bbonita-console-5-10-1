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
package org.bonitasoft.console.server.users;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserService;
import org.bonitasoft.console.server.login.SessionManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserServlet extends RemoteServiceServlet implements UserService {

	private static final long serialVersionUID = -8246434011641863500L;
	private static final Logger LOGGER = Logger.getLogger(UserServlet.class.getName());

	private static final UserDataStore userDataStore = UserDataStore.getInstance();

	public void updateProfilePreferredStatReportName(final UserProfile aUserProfile, ReportUUID aReportUUID) throws SessionTimeOutException, ConsoleException {
		LoginContext theLoginContext = null;
		try {
			HttpServletRequest theRequest = this.getThreadLocalRequest();
			theLoginContext = SessionManager.login(theRequest);
			UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
			if (theUserProfile.getUsername().equals(aUserProfile.getUsername())) {
				userDataStore.updateUserProfile(theUserProfile, aReportUUID);
				theUserProfile.setDefaultReportUUID(aReportUUID);
			}
		} catch (SessionTimeOutException e){
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		} catch (Throwable t) {
			String theErrorMessage = "Unable to update the preferred report name for user.";
			LOGGER.log(Level.SEVERE, theErrorMessage, t);
			throw new ConsoleException(theErrorMessage, t);
		} finally {
			if (theLoginContext != null) {
				SessionManager.logout(theLoginContext);
			}
		}
	}

}
