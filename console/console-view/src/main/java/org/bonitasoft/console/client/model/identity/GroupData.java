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
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.identity.Group;
import org.bonitasoft.console.client.identity.GroupFilter;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class GroupData implements RPCData<BonitaUUID, Group, GroupFilter >{

  public void updateItem(BonitaUUID aGroupUuId, Group aGroup, AsyncHandler<Group>... handlers) {
    GWT.log("RPC: updateGroup");
    RpcConsoleServices.getIdentityService().updateGroup(aGroupUuId, aGroup, new ChainedCallback<Group>(handlers));
  }

  public void addItem(Group aGroup, GroupFilter anItemFilter, AsyncHandler<ItemUpdates<Group>>... handlers) {
    GWT.log("RPC: addGroup");
    RpcConsoleServices.getIdentityService().addGroup(aGroup, anItemFilter, new ChainedCallback<ItemUpdates<Group>>(handlers));
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#deleteItems(java.util.Collection, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void deleteItems(Collection<BonitaUUID> anItemSelection, final GroupFilter anItemFilter, AsyncHandler<ItemUpdates<Group>>... handlers) {
    GWT.log("RPC: removeGroups");
    RpcConsoleServices.getIdentityService().removeGroups(anItemSelection,anItemFilter, new ChainedCallback<ItemUpdates<Group>>(handlers));
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getAllItems(org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void getAllItems(GroupFilter anItemFilter, AsyncHandler<ItemUpdates<Group>>... handlers) {
    GWT.log("RPC: getAllGroups");
    RpcConsoleServices.getIdentityService().getAllGroups(anItemFilter, new ChainedCallback<ItemUpdates<Group>>(handlers));
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItem(org.bonitasoft.console.client.BonitaUUID, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void getItem(BonitaUUID anItemUUID, GroupFilter aFilter, AsyncHandler<Group>... handlers) {
    GWT.log("RPC: get a Group");
    RpcConsoleServices.getIdentityService().getGroup(anItemUUID, aFilter, new ChainedCallback<Group>(handlers));
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItems(java.util.Collection, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<java.util.Collection<I>>[])
   */
  public void getItems(List<BonitaUUID> anItemSelection, GroupFilter aFilter, AsyncHandler<List<Group>>... handlers) {
    GWT.log("RPC: get a bunch of Groups");
    RpcConsoleServices.getIdentityService().getGroups(anItemSelection, aFilter, new ChainedCallback<List<Group>>(handlers));
    
  }

}
