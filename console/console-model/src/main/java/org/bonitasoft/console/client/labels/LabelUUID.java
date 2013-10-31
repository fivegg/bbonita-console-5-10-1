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
package org.bonitasoft.console.client.labels;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.users.UserUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelUUID extends BonitaUUID implements Comparable<LabelUUID> {

	/**
	 * ID used for serialization.
	 */
	private static final long serialVersionUID = 4224877581447839913L;
	protected UserUUID myOwner;

	/**
	 * 
	 * Default constructor.
	 * 
	 * @param aLabelName
	 * @param aOwner
	 */
	public LabelUUID(String aLabelName, UserUUID aOwner) {
		super(aLabelName);
		myOwner = aOwner;
	}

	/**
	 * Default constructor.
	 */
	public LabelUUID() {
		super();
		// Mandatory for serialization.
	}

	/**
	 * Get the value of the owner field.
	 * 
	 * @return the owner
	 */
	public UserUUID getOwner() {
		return this.myOwner;
	}

	/**
	 * Set the value of the owner field.
	 * 
	 * @param aOwner
	 *            the owner to set
	 */
	protected void setOwner(UserUUID aOwner) {
		this.myOwner = aOwner;
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
		if (anObject instanceof LabelUUID) {
			LabelUUID theOtherLabelUUID = (LabelUUID) anObject;
			if ((this.myOwner == null || theOtherLabelUUID.myOwner == null || this.myOwner.equals(theOtherLabelUUID.myOwner))) {
				return super.equals(anObject);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(LabelUUID anotherLabelUUID) {
		if (this == anotherLabelUUID) {
			return 0;
		} else {
			return getValue().compareTo(anotherLabelUUID.getValue());
		}
	}
}
