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
package org.bonitasoft.console.client;

import java.io.Serializable;

/**
 * @author Nicolas Chabanoles
 *
 */
@SuppressWarnings("nls")
public class BonitaUUID implements Serializable {

	
	/**
	 * ID used for serialization.
	 */
	private static final long serialVersionUID = -5279239728820040086L;
	private String myValue = "Undefined";


	/**
	 * Default constructor.
	 */
	public BonitaUUID() {
		// Mandatory for serialization.
	}
	
	/**
	 * Default constructor.
	 * @param aValue 
	 */
	public BonitaUUID(String aValue) {
		if(aValue != null) {
			myValue = aValue;
		}
	}
	
	/**
	 * Get the value of the value field.
	 * @return the value
	 */
	public String getValue() {
		return this.myValue;
	}

	/**
	 * Set the value of the value field.
	 * @param aValue the value to set
	 */
	public void setValue(String aValue) {
		this.myValue = aValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof BonitaUUID) {
			BonitaUUID anotherBonitaUUID = (BonitaUUID)anObject;
			return myValue.equals(anotherBonitaUUID.myValue);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return myValue.hashCode();
	}
	
	/**
	 * Equivalent to getValue().
	 */
	@Override
	public String toString() {
		return myValue.toString();
	}

	
}
