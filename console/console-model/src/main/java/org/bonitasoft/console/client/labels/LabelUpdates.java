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

import java.io.Serializable;

/**
 * @author Nicolas Chabanoles
 *
 */
public class LabelUpdates implements Serializable{


	private static final long serialVersionUID = 7237380735919331739L;
	protected String myLabelName;
	protected String myLabelOwner;
	protected int myNbOfCases;
	
	@SuppressWarnings("unused")
	private LabelUpdates() {
		// mandatory for serialization.
		super();
	}

	/**
	 * Default constructor.
	 * @param aLabelName
	 * @param aLabelOwner
	 * @param aNbOfCases
	 */
	public LabelUpdates(String aLabelName, String aLabelOwner, int aNbOfCases) {
		super();
		myLabelName = aLabelName;
		myLabelOwner = aLabelOwner;
		myNbOfCases = aNbOfCases;
	}
	
	/**
	 * 
	 * Default constructor.
	 * @param aLabelUUID
	 * @param aNbOfCases
	 */
	public LabelUpdates(LabelUUID aLabelUUID, int aNbOfCases) {
		super();
		myLabelName = aLabelUUID.getValue();
		if(aLabelUUID.getOwner()!=null){
			myLabelOwner = aLabelUUID.getOwner().getValue();
		} else {
			myLabelOwner = null;
		}
		myNbOfCases = aNbOfCases;
	}

	/**
	 * @return the labelName
	 */
	public String getLabelName() {
		return myLabelName;
	}

	/**
	 * @return the labelOwner
	 */
	public String getLabelOwner() {
		return myLabelOwner;
	}


	/**
	 * @return the nbOfCases
	 */
	public int getNbOfCases() {
		return myNbOfCases;
	}
	
	
}
