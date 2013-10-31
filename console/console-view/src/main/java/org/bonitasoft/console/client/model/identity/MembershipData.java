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
package org.bonitasoft.console.client.model.identity;

import java.util.Collection;
import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.identity.MembershipItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class MembershipData implements RPCData<BonitaUUID, MembershipItem, SimpleFilter >{

  public void updateItem(BonitaUUID anItemUuId, MembershipItem aMembershipItem, AsyncHandler<MembershipItem>... handlers) {
    Window.alert("Not supported: membership updateItem");
  }

  public void addItem(MembershipItem aMembershipItem, SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<MembershipItem>>... handlers) {
    Window.alert("Not supported: membership addItem");
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#deleteItems(java.util.Collection, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void deleteItems(Collection<BonitaUUID> anItemSelection, final SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<MembershipItem>>... handlers) {
    Window.alert("Not supported: membership deleteItems");
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getAllItems(org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void getAllItems(SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<MembershipItem>>... handlers) {
    Window.alert("not yet implemented: MembershipItem getAllItems");
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItem(org.bonitasoft.console.client.BonitaUUID, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void getItem(BonitaUUID anItemUUID, SimpleFilter aFilter, AsyncHandler<MembershipItem>... handlers) {
    GWT.log("RPC: get a MembershipItem");
    RpcConsoleServices.getIdentityService().getMembershipItem(anItemUUID, aFilter, new ChainedCallback<MembershipItem>(handlers));
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItems(java.util.List, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<java.util.List<I>>[])
   */
  public void getItems(List<BonitaUUID> anItemSelection, SimpleFilter aFilter, AsyncHandler<List<MembershipItem>>... handlers) {
    GWT.log("RPC: get a bunch of MembershipItems");
    RpcConsoleServices.getIdentityService().getMembershipItems(anItemSelection, aFilter, new ChainedCallback<List<MembershipItem>>(handlers));
  }

}
