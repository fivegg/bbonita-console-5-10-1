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
import org.bonitasoft.console.client.identity.Role;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class RoleData implements RPCData<BonitaUUID, Role, SimpleFilter> {

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#addItem(org.bonitasoft.console.client.Item, org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void addItem(Role anItem, SimpleFilter aFilter, AsyncHandler<ItemUpdates<Role>>... handlers) {
    GWT.log("RPC: add role");
    RpcConsoleServices.getIdentityService().addRole(anItem, aFilter, new ChainedCallback<ItemUpdates<Role>>(handlers));
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#deleteItems(java.util.Collection, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void deleteItems(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<Role>>... handlers) {
    GWT.log("RPC: delete roles");
    RpcConsoleServices.getIdentityService().removeRoles(anItemSelection, anItemFilter, new ChainedCallback<ItemUpdates<Role>>(handlers));
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getAllItems(org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void getAllItems(SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<Role>>... handlers) {
    GWT.log("RPC: list roles");
    RpcConsoleServices.getIdentityService().getAllRoles(anItemFilter, new ChainedCallback<ItemUpdates<Role>>(handlers));
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItem(org.bonitasoft.console.client.BonitaUUID, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void getItem(BonitaUUID anItemUUID, SimpleFilter aFilter, AsyncHandler<Role>... handlers) {
    GWT.log("RPC: get role");
    RpcConsoleServices.getIdentityService().getRole(anItemUUID, aFilter, new ChainedCallback<Role>(handlers));
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItems(java.util.Collection, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<java.util.Collection<I>>[])
   */
  public void getItems(List<BonitaUUID> anItemSelection, SimpleFilter aFilter, AsyncHandler<List<Role>>... handlers) {
    GWT.log("RPC: list roles");
    RpcConsoleServices.getIdentityService().getRoles(anItemSelection, aFilter, new ChainedCallback<List<Role>>(handlers));
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#updateItem(java.lang.String, org.bonitasoft.console.client.Item, org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void updateItem(BonitaUUID anItemUuId, Role anItem, AsyncHandler<Role>... handlers) {
    GWT.log("RPC: update role");
    RpcConsoleServices.getIdentityService().updateRole(anItemUuId, anItem, new ChainedCallback<Role>(handlers));
    
  }

}
