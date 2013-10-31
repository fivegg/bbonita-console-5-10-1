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
package org.bonitasoft.console.client.model.cases;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.model.AbstractItemSelection;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseSelection extends AbstractItemSelection<CaseUUID> {

	public enum CaseSelector {
		All, Starred, Unstarred, None;
	}

	/**
	 * This property name used for eventing changes in term of labels associated to the selected cases.
	 */
	public static final String CASE_SELECTION_LABEL_PROPERTY = "case selection labels";

	private CaseDataSource myCaseDataSource;

	public CaseSelection(CaseDataSource aCaseDataSource) {
		super();
		myCaseDataSource = aCaseDataSource;
	}

	public void select(CaseSelector selector) {
		List<CaseUUID> theOldSelection = new ArrayList<CaseUUID>(myItemSelection);
		List<CaseUUID> theVisibleCases = myCaseDataSource.getVisibleItems();
		myItemSelection.clear();
		if (theVisibleCases != null) {
			switch (selector) {
			case All:
				myItemSelection.addAll(theVisibleCases);
				break;
			case Starred:
				for (CaseUUID theCaseUUID : theVisibleCases) {
				    final boolean isStarred = myCaseDataSource.getItem(theCaseUUID).isStarred();
					if (isStarred) {
						myItemSelection.add(theCaseUUID);
					}
				}
				break;
			case Unstarred:
				for (CaseUUID theCaseUUID : theVisibleCases) {
				    final boolean isStarred = myCaseDataSource.getItem(theCaseUUID).isStarred();
					if (!isStarred) {
						myItemSelection.add(theCaseUUID);
					}
				}
				break;
			case None:
				// Selection has already been cleared.
				break;
			default:
				break;
			}
		}
		// Alert listeners that the list has been updated.
		myChanges.fireModelChange(ITEM_SELECTION_PROPERTY, theOldSelection, myItemSelection);
	}
}
