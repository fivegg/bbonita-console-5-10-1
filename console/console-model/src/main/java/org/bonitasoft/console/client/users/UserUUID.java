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
package org.bonitasoft.console.client.users;

import org.bonitasoft.console.client.BonitaUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserUUID extends BonitaUUID {

	/**
	 * ID used for serialization.
	 */
	private static final long serialVersionUID = -8021792888174044268L;
	
	/**
	 * 
	 * Default constructor.
	 */
	public UserUUID() {
		super();
		// Mandatory for serialization.
	}
	
	/**
	 * 
	 * Default constructor.
	 * @param aLogin
	 */
	public UserUUID(String aLogin) {
		super(aLogin);
	}
}
