/**
 * Copyright (C) 2010 BonitaSoft S.A.
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
package org.bonitasoft.console.client.model.processes;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ProcessFilter;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessData implements RPCData<BonitaProcessUUID, BonitaProcess, ProcessFilter> {

  /**
   * Default constructor.
   */
  public ProcessData() {
    // Nothing to do here.
  }

  public void getAllProcesses(final AsyncHandler<Set<BonitaProcess>>... handlers) {
    GWT.log("RPC: getAllProcesses");
    RpcConsoleServices.getProcessService().getAllProcesses(new ChainedCallback<Set<BonitaProcess>>(handlers));
  }

  public void deploy(final String fileName, ProcessFilter aFilter, final AsyncHandler<ItemUpdates<BonitaProcess>>... handlers) {
    GWT.log("RPC: deploy");
    RpcConsoleServices.getProcessService().deploy(fileName, aFilter, new ChainedCallback<ItemUpdates<BonitaProcess>>(handlers));
  }

  public void undeploy(final List<BonitaProcessUUID> aProcessUUIDList, AsyncHandler<List<BonitaProcess>>... handlers) {
    GWT.log("RPC: undeploy");
    RpcConsoleServices.getProcessService().undeploy(aProcessUUIDList, new ChainedCallback<List<BonitaProcess>>(handlers));
  }

  public void enable(final List<BonitaProcessUUID> aProcessUUIDList, AsyncHandler<List<BonitaProcess>>... handlers) {
    GWT.log("RPC: enable");
    RpcConsoleServices.getProcessService().enable(aProcessUUIDList, new ChainedCallback<List<BonitaProcess>>(handlers));
  }

  public void deleteAllInstances(final List<BonitaProcessUUID> aProcessUUIDList, boolean deleteAttachments, AsyncHandler<Void>... handlers) {
    GWT.log("RPC: deleteAllInstances");
    RpcConsoleServices.getProcessService().deleteAllInstances(aProcessUUIDList, deleteAttachments, new ChainedCallback<Void>(handlers));
  }
  
  public void deleteProcesses(final List<BonitaProcessUUID> aProcessUUIDList,  ProcessFilter anItemFilter, boolean deleteAttachments, AsyncHandler<ItemUpdates<BonitaProcess>>... handlers) {
      GWT.log("RPC: deleteProcesses");
      RpcConsoleServices.getProcessService().deleteProcesses(aProcessUUIDList, anItemFilter, deleteAttachments, new ChainedCallback<ItemUpdates<BonitaProcess>>(handlers));
    }
  
  public void archive(List<BonitaProcessUUID> aProcessUUIDList, final AsyncHandler<List<BonitaProcess>>... handlers) {
    GWT.log("RPC: archive");
    RpcConsoleServices.getProcessService().archive(aProcessUUIDList, new ChainedCallback<List<BonitaProcess>>(handlers));
  }

  public void updateProcessApplicationURL(HashMap<BonitaProcessUUID, String> aProcessURLAssociation, final AsyncHandler<Void>... handlers) {
    GWT.log("RPC: updateProcessApplicationURL");
    RpcConsoleServices.getProcessService().updateProcessApplicationURL(aProcessURLAssociation, new ChainedCallback<Void>(handlers));

  }

  public void updateProcessCustomDescriptionPattern(HashMap<BonitaProcessUUID, String> aProcessCaseDescriptionPatternAssociation, final AsyncHandler<Void>... handlers) {
    GWT.log("RPC: updateProcessCustomDescriptionPattern");
    RpcConsoleServices.getProcessService().updateProcessCustomDescriptionPattern(aProcessCaseDescriptionPatternAssociation, new ChainedCallback<Void>(handlers));

  }

  public void getProcessImage(BonitaProcessUUID aProcessUUID, final AsyncHandler<String>... handlers) {
    GWT.log("RPC: getProcessImage");
    RpcConsoleServices.getProcessService().getProcessPicture(aProcessUUID, new ChainedCallback<String>(handlers));

  }

  public void getStartableProcesses(final AsyncHandler<List<BonitaProcess>>... handlers) {
    GWT.log("RPC: getStartableProcesses");
    RpcConsoleServices.getProcessService().getStartableProcesses(new ChainedCallback<List<BonitaProcess>>(handlers));

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#addItem(org.bonitasoft
   * .console.client.Item, org.bonitasoft.console.client.ProcessFilter,
   * org.bonitasoft
   * .console.client.common.data.AsyncHandler<org.bonitasoft.console
   * .client.ItemUpdates<I>>[])
   */
  public void addItem(BonitaProcess aAnItem, ProcessFilter aFilter, AsyncHandler<ItemUpdates<BonitaProcess>>... aHandlers) {
    Window.alert("Not supported: ProcessData.addItem()");

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#deleteItems(java.util
   * .Collection, org.bonitasoft.console.client.ItemFilter,
   * org.bonitasoft.console
   * .client.common.data.AsyncHandler<org.bonitasoft.console
   * .client.ItemUpdates<I>>[])
   */
  public void deleteItems(Collection<BonitaProcessUUID> anItemSelection, ProcessFilter anItemFilter, AsyncHandler<ItemUpdates<BonitaProcess>>... handlers) {
    GWT.log("RPC: delete processes");
    RpcConsoleServices.getProcessService().deleteProcesses(anItemSelection, anItemFilter, new ChainedCallback<ItemUpdates<BonitaProcess>>(handlers));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#getAllItems(org.bonitasoft
   * .console.client.ItemFilter,
   * org.bonitasoft.console.client.common.data.AsyncHandler
   * <org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void getAllItems(ProcessFilter anItemFilter, AsyncHandler<ItemUpdates<BonitaProcess>>... handlers) {
    GWT.log("RPC: get processes");
    RpcConsoleServices.getProcessService().getProcesses(anItemFilter, new ChainedCallback<ItemUpdates<BonitaProcess>>(handlers));

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#getItem(org.bonitasoft
   * .console.client.BonitaUUID, org.bonitasoft.console.client.ItemFilter,
   * org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void getItem(BonitaProcessUUID anItemUUID, ProcessFilter aFilter, AsyncHandler<BonitaProcess>... handlers) {
    GWT.log("RPC: getProcess");
    RpcConsoleServices.getProcessService().getProcess(anItemUUID, new ChainedCallback<BonitaProcess>(handlers));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#getItems(java.util.List,
   * org.bonitasoft.console.client.ItemFilter,
   * org.bonitasoft.console.client.common
   * .data.AsyncHandler<java.util.List<I>>[])
   */
  public void getItems(List<BonitaProcessUUID> anItemSelection, ProcessFilter aFilter, AsyncHandler<List<BonitaProcess>>... handlers) {
    GWT.log("RPC: getProcesses");
    RpcConsoleServices.getProcessService().getProcesses(anItemSelection, aFilter, new ChainedCallback<List<BonitaProcess>>(handlers));

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#updateItem(org.bonitasoft
   * .console.client.BonitaUUID, org.bonitasoft.console.client.Item,
   * org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void updateItem(BonitaProcessUUID anItemId, BonitaProcess anItem, AsyncHandler<BonitaProcess>... handlers) {
    GWT.log("RPC: update process");
    RpcConsoleServices.getProcessService().updateProcess(anItemId, anItem, new ChainedCallback<BonitaProcess>(handlers));

  }
}
