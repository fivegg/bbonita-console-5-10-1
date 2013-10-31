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
package org.bonitasoft.console.client.attachments;

import java.io.Serializable;

/**
 * An instance of the Attachment class corresponds to a file attached to a
 * {@link org.bonitasoft.console.client.cases.CaseItem Case}.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class Attachment implements Serializable {

	/**
	 * ID used for serialisation.
	 */
	private static final long serialVersionUID = -631084305039449352L;
	private String myTitle;
	private String myType;
	
	/**
	 * Default constructor.
	 */
	protected Attachment() {
		// Mandatory for serialization.
	}
	
	/**
	 * Default constructor.
	 * @param aTitle
	 * @param aType
	 */
	public Attachment(String aTitle, String aType) {
		super();
		this.myTitle = aTitle;
		this.myType = aType;
	}

	/**
	 * Get the title of the attached file.
	 * 
	 * @return the title.
	 */
	public String getTitle() {
		return myTitle;
	}

	/**
	 * Get the file type of the attached file, e.g., .tar
	 * 
	 * @return the file type.
	 */
	public String getType() {
		return myType;
	}

	/**
	 * Get the URL of the attached file from which it can be downloaded.
	 * 
	 * @return the URL.
	 */
	// public URL getURL();
}
