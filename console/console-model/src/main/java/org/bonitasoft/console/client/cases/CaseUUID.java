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
package org.bonitasoft.console.client.cases;

import org.bonitasoft.console.client.BonitaUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseUUID extends BonitaUUID implements Comparable<CaseUUID> {

	/**
	 * ID used for serialization.
	 */
	private static final long serialVersionUID = 3487823819255905251L;

	/**
	 * Default constructor.
	 */
	protected CaseUUID() {
		super();
		// Mandatory for serialization.
	}

	/**
	 * Default constructor.
	 * 
	 * @param aValue
	 */
	public CaseUUID(String aValue) {
		super(aValue);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(CaseUUID anotherCaseUUID) {
		return getValue().compareTo(anotherCaseUUID.getValue());
	}
}
