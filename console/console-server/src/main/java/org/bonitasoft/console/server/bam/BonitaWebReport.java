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
package org.bonitasoft.console.server.bam;

import java.io.IOException;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.server.login.SessionManager;

/**
 * An Implementation of the WebReport servlet that ensures that the user is authentified against the Bonita Engine.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class BonitaWebReport extends WebReport {

	private static final long serialVersionUID = 1L;

	protected static final Logger LOGGER = Logger.getLogger(BonitaWebReport.class.getName());

	public BonitaWebReport() {
		super();
	}

	
	
	
	/* (non-Javadoc)
	 * @see org.bonitasoft.console.server.bam.WebReport#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LoginContext theLoginContext = null;
		try {

			// Authenticate user based on the session data.
			theLoginContext = SessionManager.login(req);

			// Forward the request to the super class.
			super.service(req, resp);

		} catch (Exception e) {
			LOGGER.severe("Error occurred while processing the reporting request: " + e.getMessage());
			e.printStackTrace();
			throw new ServletException(e);
		} finally {
			if (theLoginContext != null) {
				try {
					theLoginContext.logout();
				} catch (LoginException e) {
					LOGGER.warning("Unable to logout from bonita: " + e.getMessage());
				}
			}
		}
	}
	
}
