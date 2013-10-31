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
package org.bonitasoft.console.client.processes;

import org.bonitasoft.console.client.BonitaUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class BonitaProcessUUID extends BonitaUUID {

	/**
	 * ID used for serialization.
	 */
	private static final long serialVersionUID = -2291123669772786935L;
	private String myLabel;

	/**
	 * Default constructor.
	 */
	public BonitaProcessUUID() {
		super();
		// Mandatory for serialization.
	}

	/**
	 * Default constructor.
	 * 
	 * @param aValue the technical process id
	 * @param aLabel the label display to the user 
	 */
	public BonitaProcessUUID(String aValue, String aLabel) {
		super(aValue);
		myLabel = aLabel;
	}
	
	/**
	 * Get the label associated to the id.
	 * @return
	 */
	public String getLabel(){
		return myLabel;
	}
}
