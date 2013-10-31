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
package org.bonitasoft.console.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ItemUpdates<I extends Item> implements Serializable {

    private static final long serialVersionUID = 4649081668894717351L;
    protected int myNbOfItems;
    protected ArrayList<I> myItemList;
    protected I myNewlyCreatedItem;

    protected ItemUpdates() {
        // mandatory for serialization.
        super();
    }

    /**
     * 
     * Default constructor.
     * 
     * @param anItemList
     * @param aNbOfItems
     */
    public ItemUpdates(List<I> anItemList, final int aNbOfItems) {
        super();
        myNbOfItems = aNbOfItems;
        if (anItemList != null) {
            myItemList = new ArrayList<I>(anItemList);
        }
    }

    /**
     * @return the itemList
     */
    public ArrayList<I> getItems() {
        return myItemList;
    }

    /**
     * @return the nbOfItems
     */
    public int getNbOfItems() {
        return myNbOfItems;
    }

    public I getNewlyCreatedItem() {
        return myNewlyCreatedItem;
    }

    public void setNewlyCreatedItem(I aNewlyCreatedItem) {
        myNewlyCreatedItem = aNewlyCreatedItem;
    }
}
