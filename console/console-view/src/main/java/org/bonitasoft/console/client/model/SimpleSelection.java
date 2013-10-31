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
package org.bonitasoft.console.client.model;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class SimpleSelection<U extends BonitaUUID> extends AbstractItemSelection<U> {
	public enum SimpleSelector {
		All, None;
	}

	public SimpleSelection() {
	}

	public void select(final SimpleSelector aSelector) {

		List<U> theItems = myDataSource.getVisibleItems();
		ArrayList<U> theOldSelection = new ArrayList<U>(myItemSelection);
		myItemSelection.clear();
		if (theItems != null) {
			switch (aSelector) {
			case All:
					myItemSelection.addAll(theItems);
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
