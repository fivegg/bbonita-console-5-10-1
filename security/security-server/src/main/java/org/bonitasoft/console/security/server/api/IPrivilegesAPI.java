/**
 * Copyright (C) 2010 BonitaSoft S.A.
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
package org.bonitasoft.console.security.server.api;

import java.util.Map;

import org.bonitasoft.console.security.client.users.UserRights;
import org.bonitasoft.console.security.server.accessor.SecurityProperties;

/**
 * API to deal with user privileges
 * 
 * @author Anthony Birembaut
 */
public interface IPrivilegesAPI {

    /**
     * Retrieve the rights of a user depending on his username and his memeberships
     * @param username the user's username
     * @return a {@link UserRights} object
     */
    UserRights getUserRights(String username);
    
    /**
     * Retrieve the SecurityProperties
     * @param processDefinitionUUID 
     * @return a {@link SecurityProperties} object
     */
    SecurityProperties getSecurityProperties(Map<String, Object> urlContext);
}
