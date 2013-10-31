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

import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface BonitaFilteredDataSource<U extends BonitaUUID, I extends Item, F extends ItemFilter> extends BonitaDataSource<U> {

    public F getItemFilter();

    public void setItemFilter(F aFilter);
    
    public void setItemFilter(F aFilter, boolean fireEvent);

    public ItemSelection<U> getItemSelection();

    public abstract void reload();

    public abstract int getSize();

    public I getItem(U anUUID);

    public void getItem(U anUUID, AsyncHandler<I> aHandler);

    public void getItems(List<U> aUUIDSelection, AsyncHandler<List<I>> aHandler);

    public void addItem(I anItem, AsyncHandler<ItemUpdates<I>> aHandler);

    public void updateItem(U anUUID, final I anItem, final AsyncHandler<I> aHandler);

    public void deleteItems(List<U> anItemSelection, final AsyncHandler<ItemUpdates<I>> aHandler);

    public void listItems(final F aFilter, final AsyncHandler<ItemUpdates<I>> aHandler);
}