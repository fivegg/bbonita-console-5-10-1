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
import org.bonitasoft.console.client.identity.UserMetadataItem;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserMetadataItemData implements RPCData<BonitaUUID, UserMetadataItem, SimpleFilter> {

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#addItem(org.bonitasoft.console.client.Item, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void addItem(UserMetadataItem anItem, SimpleFilter aFilter, AsyncHandler<ItemUpdates<UserMetadataItem>>... handlers) {
    GWT.log("RPC: add user metadata");
    RpcConsoleServices.getIdentityService().addUserMetadata(anItem, aFilter, new ChainedCallback<ItemUpdates<UserMetadataItem>>(handlers));
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#deleteItems(java.util.List, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void deleteItems(Collection<BonitaUUID> anItemSelection, SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<UserMetadataItem>>... handlers) {
    GWT.log("RPC: delete user metadatas");
    RpcConsoleServices.getIdentityService().removeUserMetadatas(anItemSelection, anItemFilter, new ChainedCallback<ItemUpdates<UserMetadataItem>>(handlers));    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getAllItems(org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void getAllItems(SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<UserMetadataItem>>... handlers) {
    GWT.log("RPC: list user metadata");
    RpcConsoleServices.getIdentityService().getAllUserMetadatas(anItemFilter, new ChainedCallback<ItemUpdates<UserMetadataItem>>(handlers));    
  }

  public void getAllItems(AsyncHandler<List<UserMetadataItem>>... handlers) {
    GWT.log("RPC: list ALL user metadata");
    RpcConsoleServices.getIdentityService().getAllUserMetadatas(new ChainedCallback<List<UserMetadataItem>>(handlers));
  }
  
  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItem(org.bonitasoft.console.client.BonitaUUID, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void getItem(BonitaUUID anItemUUID, SimpleFilter aFilter, AsyncHandler<UserMetadataItem>... handlers) {
    GWT.log("RPC: get a user metadata");
    RpcConsoleServices.getIdentityService().getUserMetadata(anItemUUID, aFilter, new ChainedCallback<UserMetadataItem>(handlers));
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItems(java.util.List, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<java.util.List<I>>[])
   */
  public void getItems(List<BonitaUUID> anItemSelection, SimpleFilter aFilter, AsyncHandler<List<UserMetadataItem>>... handlers) {
    GWT.log("RPC: get a set of user metadata");
    RpcConsoleServices.getIdentityService().getUserMetadatas(anItemSelection, aFilter, new ChainedCallback<List<UserMetadataItem>>(handlers));
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#updateItem(org.bonitasoft.console.client.BonitaUUID, org.bonitasoft.console.client.Item, org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void updateItem(BonitaUUID anItemId, UserMetadataItem anItem, AsyncHandler<UserMetadataItem>... handlers) {
    GWT.log("RPC: update a user metadata");
    RpcConsoleServices.getIdentityService().updateUserMetadata(anItemId, anItem, new ChainedCallback<UserMetadataItem>(handlers));
  }

 

}
