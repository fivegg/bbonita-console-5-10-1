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
package org.bonitasoft.console.client.model.processes;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.console.client.model.AbstractItemSelection;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessSelection extends AbstractItemSelection<BonitaProcessUUID> {
	public enum ProcessSelector {
		All, None;
	}


	public ProcessSelection() {
	  super();
	}

	public void select(ProcessSelector aSelector) {
		ArrayList<BonitaProcessUUID> theOldSelection = new ArrayList<BonitaProcessUUID>(myItemSelection);
		List<BonitaProcessUUID> theProcessUUIDs = myDataSource.getVisibleItems();
		myItemSelection.clear();
		if (theProcessUUIDs != null) {
			switch (aSelector) {
			case All:
				myItemSelection.addAll(theProcessUUIDs);
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
