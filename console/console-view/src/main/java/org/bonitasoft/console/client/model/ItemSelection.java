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

import org.bonitasoft.console.client.BonitaUUID;

/**
 * @author Nicolas Chabanoles
 * @param <U> 
 * 
 */
public interface ItemSelection<U extends BonitaUUID> extends BonitaDataSource<U> {
	
  public enum ItemSelector {
    All, None;
  }
  
	/**
	 * The PROCESS_SELECTION_PROPERTY is the property name used for eventing
	 * changes in term of selected processes.
	 */
	public static final String ITEM_SELECTION_PROPERTY = "item selection";
	
	/**
	 * Get the list of myCases selected by the user.
	 * @return the list of myCases. The list may be empty but never null.
	 */
	public ArrayList<U> getSelectedItems();

	/**
	 * 
	 * @param anItem 
	 * @param aCase
	 */
	public void addItemToSelection(U anItem);

	/**
	 * 
	 * @param anItem 
	 * @param aCase
	 */
	public void removeItemFromSelection(U anItem);

	
	/**
	 * Clear the selection.
	 */
	public void clearSelection();


	/**
	 * Get the number of cases selected. It is equivalent to getSelectedCases().size().
	 * @return the size of the selection.
	 */
	public int getSize();
	
	public void setDataSource(BonitaDataSource<U> aDataSource);

  /**
   * @param aASelector
   */
  public void select(ItemSelector aSelector);
}
