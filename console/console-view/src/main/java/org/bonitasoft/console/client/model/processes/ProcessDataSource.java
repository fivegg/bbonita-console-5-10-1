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
package org.bonitasoft.console.client.model.processes;

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ProcessFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;

import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface ProcessDataSource extends BonitaFilteredDataSource<BonitaProcessUUID, BonitaProcess, ProcessFilter> {

  public static final String DEFAULT_APPLICATION_URL = Window.Location.getPath();
  public static final String HOST_PAGE_PATH = "/application/BonitaApplication.html";
  public static final String DEFAULT_CASEDESCRIPTION_PATTERN = "process.name+ - #+case.indexNumber";
  public static final String PROCESS_INSTANCES_PROPERTY = "process instance";
  public static final String STARTABLE_PROCESS_LIST_PROPERTY = "startable process list";

  public String buildWebAppURL(BonitaProcess aProcess);
  
  /**
   * Get the definition of all deployed processes that the current user can
   * start.
   * 
   * @return the list of definition.
   */
  public void getStartableProcesses(AsyncHandler<List<BonitaProcess>> aHandler);

  /**
   * Request to deploy a process onto the server.
   * 
   * @param aFileName
   * @param aDeployHandler
   */
  public void deploy(String aFileName, AsyncHandler<BonitaProcess> aDeployHandler);

  /**
   * Request to enable a List of processes.
   * 
   * @param aProcessUUIDList
   * @param aAsyncHandler
   */
  public void enable(List<BonitaProcessUUID> aProcessUUIDList, AsyncHandler<Void> aAsyncHandler);

  /**
   * Request to disable a List of processes from the server.
   * 
   * @param aProcessUUIDList
   * @param aAsyncHandler
   */
  public void disable(List<BonitaProcessUUID> aProcessUUIDList, AsyncHandler<Void> aAsyncHandler);

  /**
   * Request to archive a List of processes from the server.
   * 
   * @param aProcessUUIDList
   * @param aAsyncHandler
   */
  public void archive(List<BonitaProcessUUID> aProcessUUIDList, AsyncHandler<Void> aAsyncHandler);

  /**
   * Request to delete all instances of all the given processes.
   * 
   * @param aProcessUUIDList
   * 
   * @param aSelectedItems
   * @param deleteAttachments
   * @param aAsyncHandler
   */
  public void deleteAllInstances(List<BonitaProcessUUID> aProcessUUIDList, boolean deleteAttachments, AsyncHandler<Void> aAsyncHandler);
  
  /**
   * Request to delete the given processes.
   * 
   * @param aProcessUUIDList
   * 
   * @param aSelectedItems
   * @param deleteAttachments
   * @param aAsyncHandler
   */
  public void deleteProcesses(List<BonitaProcessUUID> aProcessUUIDList, boolean deleteAttachments, final AsyncHandler<ItemUpdates<BonitaProcess>> aAsyncHandler);
  
  /**
   * Update the given processes to set the application URL to the given one.
   * 
   * @param aProcessURLAssociation
   * @param anAsyncHandler
   */
  public void updateProcessApplicationURL(HashMap<BonitaProcessUUID, String> aProcessURLAssociation, AsyncHandler<Void> anAsyncHandler);

  /**
   * Update the given processes to set the case description pattern to the given
   * one.
   * 
   * @param aProcessURLAssociation
   * @param anAsyncHandler
   */
  public void updateProcessCaseDescription(HashMap<BonitaProcessUUID, String> aPatternChanges, AsyncHandler<Void> anAsyncHandler);

  public void getProcessImage(BonitaProcessUUID aProcessUUID, AsyncHandler<String> aHandler);

  /**
   * @param aAsyncHandler
   */
  public void getAllProcesses(AsyncHandler<List<BonitaProcessUUID>> anAsyncHandler);
}
