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

import org.bonitasoft.console.client.steps.CommentItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface CaseServiceAsync {

  /**
   * List all the cases.
   * 
   * @param aCallBackHandler
   */
  public void getAllCases(final CaseFilter aCaseFilter, AsyncCallback<CaseUpdates> aCallBackHandler);

  /**
   * Cancel a case
   * 
   * @param aUUID
   * @param aCallBackHandler
   */
  public void cancelCases(final CaseFilter aCaseFilter, final Collection<CaseUUID> aCaseSelection, final AsyncCallback<CaseUpdates> aCallback);

  /**
   * Delete a case
   * 
   * @param aUUID
   * @param deleteAttachments
   * @param aCallBackHandler
   */
  public void deleteCases(Collection<CaseUUID> aCaseSelection, final boolean deleteAttachments, AsyncCallback<Void> aCallback);

  public void getCase(CaseUUID anCaseUUID, CaseFilter aFilter, AsyncCallback<CaseItem> aCallback);

  public void synchronizeDBs(final AsyncCallback<Void> aCallback);

  public void getCases(Collection<CaseUUID> aCaseSelection, final CaseFilter aCaseFilter, final AsyncCallback<Collection<CaseItem>> aCallback);

  public void getCaseCommentFeed(final CaseUUID aCaseUUID, final CaseFilter aFilter, final AsyncCallback<List<CommentItem>> aCallback);

  /**
   * Add a comment to the case.
   * 
   * @param aCaseUUID
   * @param aComment
   * @param aChainedCallback
   */
  public void addCommentToCase(CaseUUID aCaseUUID, String aComment, CaseFilter aCaseFilter, AsyncCallback<List<CommentItem>> aCallback);

  /**
   * Get the configuration.
   */
  public void getConfiguration(AsyncCallback<CasesConfiguration> aCallback);

  /**
   * Update configuration.
   */
  public void updateConfiguration(final CasesConfiguration aConfiguration, AsyncCallback<CasesConfiguration> aCallback);
}
