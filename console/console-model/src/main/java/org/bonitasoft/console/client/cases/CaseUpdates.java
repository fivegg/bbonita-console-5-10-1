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

import java.io.Serializable;
import java.util.List;

/**
 * @author Nicolas Chabanoles
 *
 */
public class CaseUpdates implements Serializable{

  private static final long serialVersionUID = -8383272521244159851L;
  protected int myNbOfCases;
	protected List<CaseItem> myCaseList;

	@SuppressWarnings("unused")
	private CaseUpdates() {
		// mandatory for serialization.
		super();
	}

	/**
	 * Default constructor.
	 * @param aLabelName
	 * @param aLabelOwner
	 * @param aNbOfCases
	 */
	public CaseUpdates(List<CaseItem> aCaseList,final int aNbOfCases) {
		super();
		myNbOfCases = aNbOfCases;
		myCaseList = aCaseList;
	}
	

	public int getNbOfCases() {
		return myNbOfCases;
	}
	
	public List<CaseItem> getCases() {
	  return myCaseList;
	}
	
}
