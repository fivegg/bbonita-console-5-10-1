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
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.processes.exceptions.ProcessNotFoundException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * 
 * @author Nicolas Chabanoles
 * 
 */
public interface ProcessService extends RemoteService {

  ItemUpdates<BonitaProcess> deploy(final String fileName, ProcessFilter aFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  Set<BonitaProcess> getAllProcesses() throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  List<BonitaProcess> undeploy(final Collection<BonitaProcessUUID> aProcessUUIDCollection) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  List<BonitaProcess> enable(final Collection<BonitaProcessUUID> aProcessUUIDCollection) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  void deleteAllInstances(final Collection<BonitaProcessUUID> aProcessUUIDCollection, final boolean deleteAttachments) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  ItemUpdates<BonitaProcess> deleteProcesses(final Collection<BonitaProcessUUID> aProcessUUIDCollection,  final ProcessFilter anItemFilter, final boolean deleteAttachments) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;
  
  ItemUpdates<BonitaProcess> deleteProcesses(final Collection<BonitaProcessUUID> anItemSelection, final ProcessFilter anItemFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  List<BonitaProcess> archive(final Collection<BonitaProcessUUID> aProcessUUIDCollection) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  void updateProcessApplicationURL(final HashMap<BonitaProcessUUID, String> aProcessURLAssociation) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  BonitaProcess getProcess(final BonitaProcessUUID aProcessUUID) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException, ProcessNotFoundException;

  void updateProcessCustomDescriptionPattern(final HashMap<BonitaProcessUUID, String> aProcessCustomDescriptionPatternAssociation) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException,
      ProcessNotFoundException;

  String getProcessPicture(BonitaProcessUUID aProcessUUID) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  List<BonitaProcess> getProcesses(final List<BonitaProcessUUID> aSelection, ProcessFilter aFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  ItemUpdates<BonitaProcess> getProcesses(ProcessFilter anItemFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  List<BonitaProcess> getStartableProcesses() throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

  BonitaProcess updateProcess(BonitaProcessUUID anItemId, BonitaProcess anItem) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;
}
