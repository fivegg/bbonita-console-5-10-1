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
package org.bonitasoft.console.client.cases;

import java.util.Collection;
import java.util.List;

import org.bonitasoft.console.client.cases.exceptions.CaseNotFoundException;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.processes.exceptions.ProcessNotFoundException;
import org.bonitasoft.console.client.steps.CommentItem;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface CaseService extends RemoteService {

  /**
   * List all the cases.
   * 
   * @return the list of cases.
   * @throws CaseNotFoundException
   */
  public CaseUpdates getAllCases(final CaseFilter aCaseFilter) throws SessionTimeOutException, ConsoleException, CaseNotFoundException, ConsoleSecurityException;

  /**
   * Get a case item based on its ID.
   * 
   * @param anCaseUUID
   * @param aFilter
   * @return
   * @throws SessionTimeOutException
   *           , ConsoleException
   * @throws ProcessNotFoundException 
   */
  public CaseItem getCase(CaseUUID anCaseUUID, CaseFilter aFilter) throws SessionTimeOutException, ConsoleException, ProcessNotFoundException, CaseNotFoundException;

  /**
   * Cancel a case
   * 
   * @param aUUID
   *          the UUID of the case to cancel.
   */
  public CaseUpdates cancelCases(CaseFilter aCaseFilter, Collection<CaseUUID> aCaseSelection) throws SessionTimeOutException, ConsoleException, ConsoleSecurityException;

  /**
   * Delete a case
   * 
   * @param aUUID
   *          the UUID of the case to cancel.
   * @param deleteAttachments
   *          allow to delete attachments cases or not.
   */
  public void deleteCases(Collection<CaseUUID> aCaseSelection, final boolean deleteAttachments) throws SessionTimeOutException, ConsoleException, ConsoleSecurityException;

  public void synchronizeDBs() throws SessionTimeOutException, ConsoleException, ConsoleSecurityException;

  /**
   * Get cases based on their UUID.
   * 
   * @param aCaseSelection
   * @param aCaseFilter
   * @return
   * @throws SessionTimeOutException
   * @throws ConsoleException
   */
  public Collection<CaseItem> getCases(Collection<CaseUUID> aCaseSelection, final CaseFilter aCaseFilter) throws SessionTimeOutException, ConsoleException;

  /**
   * Get the list of comment for the given case.
   * 
   * @param aCaseUUID
   * @param aChainedCallback
   */
  public List<CommentItem> getCaseCommentFeed(final CaseUUID aCaseUUID, final CaseFilter aFilter) throws SessionTimeOutException, ConsoleException;

  /**
   * Add a comment to the case.
   * 
   * @param aCaseUUID
   * @param aComment
   * @throws SessionTimeOutException
   * @throws ConsoleException
   * @throws CaseNotFoundException 
   */
  public List<CommentItem> addCommentToCase(CaseUUID aCaseUUID, String aComment, CaseFilter aCaseFilter) throws SessionTimeOutException, ConsoleException, CaseNotFoundException, ConsoleSecurityException;
  
  /**
   * Get the configuration.
   * @return
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  public CasesConfiguration getConfiguration() throws ConsoleException, SessionTimeOutException;
  
  /**
   * Update configuration.
   * @param aConfiguration
   * @return the reloaded configuration.
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  public CasesConfiguration updateConfiguration(final CasesConfiguration aConfiguration) throws ConsoleException, SessionTimeOutException, ConsoleSecurityException;
}
