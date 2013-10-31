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
package org.bonitasoft.console.client.processes;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ProcessFilter;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Nicolas Chabanoles
 * 
 */
public interface ProcessServiceAsync {

  void deploy(final String aFileName, ProcessFilter aFilter, final AsyncCallback<ItemUpdates<BonitaProcess>> aCallback);

  void getAllProcesses(final AsyncCallback<Set<BonitaProcess>> aCallback);

  void undeploy(final Collection<BonitaProcessUUID> aProcessUUIDCollection, final AsyncCallback<List<BonitaProcess>> aCallback);

  void enable(final Collection<BonitaProcessUUID> aProcessUUIDCollection, final AsyncCallback<List<BonitaProcess>> aCallback);

  void deleteAllInstances(final Collection<BonitaProcessUUID> aProcessUUIDCollection,final boolean deleteAttachments, final AsyncCallback<Void> aCallback);

  void deleteProcesses(final Collection<BonitaProcessUUID> aProcessUUIDCollection,  final ProcessFilter anItemFilter, final boolean deleteAttachments, final AsyncCallback<ItemUpdates<BonitaProcess>> aCallback);
  
  void deleteProcesses(final Collection<BonitaProcessUUID> anItemSelection, final ProcessFilter anItemFilter, AsyncCallback<ItemUpdates<BonitaProcess>> aCallback);
  
  void archive(final Collection<BonitaProcessUUID> anProcessUUIDCollection, final AsyncCallback<List<BonitaProcess>> aCallback);

  void updateProcessApplicationURL(final HashMap<BonitaProcessUUID, String> anProcessURLAssociation, final AsyncCallback<Void> aCallback);

  void getProcess(final BonitaProcessUUID anProcessUUID, final AsyncCallback<BonitaProcess> aCallback);

  void updateProcessCustomDescriptionPattern(final HashMap<BonitaProcessUUID, String> aProcessCustomDescriptionPatternAssociation, final AsyncCallback<Void> aCallback);

  void getProcessPicture(final BonitaProcessUUID aProcessUUID, final AsyncCallback<String> aCallback);

  void getProcesses(final List<BonitaProcessUUID> aSelection, ProcessFilter aFilter, final AsyncCallback<List<BonitaProcess>> aCallback);

  void getStartableProcesses(final AsyncCallback<List<BonitaProcess>> aCallback);

  void updateProcess(BonitaProcessUUID anItemId, BonitaProcess anItem, AsyncCallback<BonitaProcess> aCallback);

  void getProcesses(ProcessFilter aAnItemFilter, AsyncCallback<ItemUpdates<BonitaProcess>> aCallback);

}
