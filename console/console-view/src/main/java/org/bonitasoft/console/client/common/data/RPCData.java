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
package org.bonitasoft.console.client.common.data;

import java.util.Collection;
import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.ItemUpdates;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface RPCData<U extends BonitaUUID, I extends Item, F extends ItemFilter> {

	public void getAllItems(final F anItemFilter, final AsyncHandler<ItemUpdates<I>>... handlers);

	public void getItem(final U anItemUUID, final F aFilter, final AsyncHandler<I>... handlers);
	
	public void getItems(final List<U> anItemSelection, final F aFilter, final AsyncHandler<List<I>>... handlers);
	
	public void addItem(final I anItem, final F aFilter, final AsyncHandler<ItemUpdates<I>>... handlers);
	
	public void updateItem(final U anItemId, final I anItem, final AsyncHandler<I>... handlers);
	
	public void deleteItems(Collection<U> anItemSelection, final F anItemFilter, final AsyncHandler<ItemUpdates<I>>... handlers);

  
}
