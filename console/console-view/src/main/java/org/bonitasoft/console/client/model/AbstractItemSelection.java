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
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;

import com.google.gwt.user.client.Window;

/**
 * An abstract implementation of the <{@link ItemSelection} ItemSelection based on an ArrayList.
 * 
 * @author Nicolas Chabanoles
 * @param <U>
 * 
 */
public abstract class AbstractItemSelection<U extends BonitaUUID> implements ItemSelection<U> {

	protected ArrayList<U> myItemSelection = new ArrayList<U>();
	protected transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

	protected BonitaDataSource<U> myDataSource;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bonitasoft.console.client.model.ItemSelection#addItemToSelection(java.lang.Object)
	 */
	public void addItemToSelection(U anItem) {
		if (anItem != null && myItemSelection != null && !myItemSelection.contains(anItem)) {
			ArrayList<U> theOldSelection = new ArrayList<U>(myItemSelection);

			// Add the case to the selection.
			myItemSelection.add(anItem);

			// Alert listeners that the list has been updated.
			myChanges.fireModelChange(ItemSelection.ITEM_SELECTION_PROPERTY, theOldSelection, myItemSelection);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bonitasoft.console.client.model.ItemSelection#clearSelection()
	 */
	public void clearSelection() {
		if (myItemSelection != null) {
			ArrayList<U> theOldSelection = new ArrayList<U>(myItemSelection);
			myItemSelection.clear();
			// Alert listeners that the list has been updated.
			myChanges.fireModelChange(ItemSelection.ITEM_SELECTION_PROPERTY, theOldSelection, myItemSelection);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bonitasoft.console.client.model.ItemSelection#getSelectedItems()
	 */
	public ArrayList<U> getSelectedItems() {

		return myItemSelection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bonitasoft.console.client.model.ItemSelection#getSize()
	 */
	public int getSize() {
		return myItemSelection.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bonitasoft.console.client.model.ItemSelection#removeItemFromSelection(java.lang.Object)
	 */
	public void removeItemFromSelection(U anItem) {
		if (anItem != null && myItemSelection != null && myItemSelection.contains(anItem)) {
			ArrayList<U> theOldSelection = new ArrayList<U>(myItemSelection);

			// Remove case from selection.
			myItemSelection.remove(anItem);

			// Alert listeners that the list has been updated.
			myChanges.fireModelChange(ITEM_SELECTION_PROPERTY, theOldSelection, myItemSelection);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bonitasoft.console.client.model.BonitaDataSource#addModelChangeListener(java.lang.String, java.beans.ModelChangeListener)
	 */
	public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
		// Avoid multiple registration.
		myChanges.removeModelChangeListener(aPropertyName, aListener);
		myChanges.addModelChangeListener(aPropertyName, aListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bonitasoft.console.client.model.BonitaDataSource#removeModelChangeListener(java.lang.String, java.beans.ModelChangeListener)
	 */
	public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
		myChanges.removeModelChangeListener(aPropertyName, aListener);

	}

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.model.ItemSelection#setDataSource(org.bonitasoft.console.client.model.BonitaDataSource)
   */
  public void setDataSource(BonitaDataSource<U> aDataSource) {
    myDataSource = aDataSource;
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.model.BonitaDataSource#getVisibleItems()
   */
  public List<U> getVisibleItems() {
    Window.alert("ItemSelection getVisibleItems: Not supported!");
    return null;
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.model.ItemSelection#select(org.bonitasoft.console.client.model.ItemSelection.ItemSelector)
   */
  public void select(ItemSelector aSelector) {
    List<U> theOldSelection = new ArrayList<U>(myItemSelection);
    List<U> theVisibleItems = myDataSource.getVisibleItems();
    myItemSelection.clear();
    if (theVisibleItems != null) {
      switch (aSelector) {
      case All:
        myItemSelection.addAll(theVisibleItems);
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
