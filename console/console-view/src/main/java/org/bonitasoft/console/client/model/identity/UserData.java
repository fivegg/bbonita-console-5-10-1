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

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.identity.IdentityConfiguration;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.users.UserFilter;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserData implements RPCData<UserUUID, User, UserFilter> {

  public void updatePreferredStatReport(final UserProfile aUserProfile, final ReportUUID aReportUUID, final AsyncHandler<Void>... handlers) {
    GWT.log("RPC: updatePreferredStatReportName.");
    RpcConsoleServices.getUserService().updateProfilePreferredStatReportName(aUserProfile, aReportUUID, new ChainedCallback<Void>(handlers));
  }

  public void updateConfiguration(IdentityConfiguration aNewConfiguration, final AsyncHandler<Void>... handlers) {
    GWT.log("RPC: User updateConfiguration.");
    RpcConsoleServices.getIdentityService().updateConfiguration(aNewConfiguration, new ChainedCallback<Void>(handlers));

  }

  public void getIdentityConfiguration(final AsyncHandler<IdentityConfiguration>... handlers) {
    GWT.log("RPC: getIdentityConfiguration.");
    RpcConsoleServices.getIdentityService().getIdentityConfiguration(new ChainedCallback<IdentityConfiguration>(handlers));

  }


  public void addItem(User anItem, UserFilter aFilter, AsyncHandler<ItemUpdates<User>>... handlers) {
    GWT.log("RPC: Adding user.");
    RpcConsoleServices.getIdentityService().addUser(anItem, aFilter, new ChainedCallback<ItemUpdates<User>>(handlers));

  }

  public void deleteItems(Collection<UserUUID> anItemSelection, UserFilter anItemFilter, AsyncHandler<ItemUpdates<User>>... handlers) {
    GWT.log("RPC: Deleting users.");
    RpcConsoleServices.getIdentityService().removeUsers(anItemSelection, anItemFilter, new ChainedCallback<ItemUpdates<User>>(handlers));

  }

  public void getAllItems(UserFilter anItemFilter, AsyncHandler<ItemUpdates<User>>... handlers) {
    GWT.log("RPC: Get users.");
    RpcConsoleServices.getIdentityService().getUsers(anItemFilter, new ChainedCallback<ItemUpdates<User>>(handlers));

  }

  public void getItem(UserUUID anItemUUID, UserFilter aFilter, AsyncHandler<User>... handlers) {
    GWT.log("RPC: Get a user.");
    RpcConsoleServices.getIdentityService().getUser(anItemUUID, aFilter, new ChainedCallback<User>(handlers));

  }

  public void getItems(List<UserUUID> anItemSelection, UserFilter aFilter, AsyncHandler<List<User>>... handlers) {
    GWT.log("RPC: Get a bunch of users.");
    RpcConsoleServices.getIdentityService().getUsers(anItemSelection, aFilter, new ChainedCallback<List<User>>(handlers));

  }

  public void updateItem(UserUUID anItemId, User anItem, AsyncHandler<User>... handlers) {
    GWT.log("RPC: Updating user.");
    RpcConsoleServices.getIdentityService().updateUser(anItemId, anItem, new ChainedCallback<User>(handlers));
  }
}
