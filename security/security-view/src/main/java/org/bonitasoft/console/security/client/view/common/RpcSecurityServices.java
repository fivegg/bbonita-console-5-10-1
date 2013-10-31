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
package org.bonitasoft.console.security.client.view.common;

import org.bonitasoft.console.security.client.LoginService;
import org.bonitasoft.console.security.client.LoginServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * RPC services accessor
 * 
 * @author Anthony Birembaut
 */
public final class RpcSecurityServices {

	/**
	 * form service
	 */
	private static LoginServiceAsync loginService;

    /**
     * Default constructor
     */
    private RpcSecurityServices() {
        //Nothing to do here.
    }
    
    /**
     * @return the URL of the logout servlet
     */
    public static String getLogoutURL() {
        return GWT.getModuleBaseURL() + "logout";
    }

	/**
	 * @return an instance of FormsServiceAsync
	 */
	public static LoginServiceAsync getLoginService() {
		if (loginService == null) {
		    loginService = (LoginServiceAsync) GWT.create(LoginService.class);
	        final ServiceDefTarget endpoint = (ServiceDefTarget) loginService;
	        endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "loginservice");
		}
		return loginService;
	}
}
