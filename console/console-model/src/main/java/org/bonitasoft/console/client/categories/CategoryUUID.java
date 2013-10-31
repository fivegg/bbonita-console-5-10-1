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
package org.bonitasoft.console.client.categories;

import org.bonitasoft.console.client.BonitaUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoryUUID extends BonitaUUID implements Comparable<CategoryUUID> {

  private static final long serialVersionUID = -7495744479013438885L;

  /**
	 * 
	 * Default constructor.
	 * 
	 * @param aLabelName
	 * @param aOwner
	 */
	public CategoryUUID(String aValue) {
		super(aValue);
	}

	/**
	 * Default constructor.
	 */
	public CategoryUUID() {
		super();
		// Mandatory for serialization.
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bonitasoft.console.client.BonitaUUID#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof CategoryUUID) {
			return super.equals(anObject);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(CategoryUUID anotherLabelUUID) {
		if (this == anotherLabelUUID) {
			return 0;
		} else {
			return getValue().compareTo(anotherLabelUUID.getValue());
		}
	}
}
