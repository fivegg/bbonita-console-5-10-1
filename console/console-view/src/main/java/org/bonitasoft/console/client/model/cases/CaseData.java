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
package org.bonitasoft.console.client.model.cases;

import java.util.Collection;
import java.util.List;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.cases.CaseUpdates;
import org.bonitasoft.console.client.cases.CasesConfiguration;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.steps.CommentItem;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseData {

	/**
	 * @param handlers
	 */
	public void getAllCases(final CaseFilter aCaseFilter, final AsyncHandler<CaseUpdates>... handlers) {
		GWT.log("RPC: getAllCases");
		RpcConsoleServices.getCaseService().getAllCases(aCaseFilter, new ChainedCallback<CaseUpdates>(handlers));
	}

	/**
	 * Request the server to cancel a case.
	 * 
	 * @param aUUID
	 * @param handlers
	 */
	public void cancelCases(final CaseFilter aCaseFilter,List<CaseUUID> aCaseSelection, final AsyncHandler<CaseUpdates>... handlers) {
		GWT.log("RPC: cancelCases");
		RpcConsoleServices.getCaseService().cancelCases(aCaseFilter, aCaseSelection, new ChainedCallback<CaseUpdates>(handlers));
	}

	/**
	 * Request the server to delete a case.
	 * 
	 * @param aUUID
	 * @param handlers
	 */
	public void deleteCases(List<CaseUUID> aCaseSelection, final boolean deleteAttachments, final AsyncHandler<Void>... handlers) {
		GWT.log("RPC: deleteCases");
		RpcConsoleServices.getCaseService().deleteCases(aCaseSelection, deleteAttachments, new ChainedCallback<Void>(handlers));
	}

	public void getCase(final CaseUUID aCaseUUID, final CaseFilter aFilter, final AsyncHandler<CaseItem>... handlers) {
		GWT.log("RPC: getCase");
		RpcConsoleServices.getCaseService().getCase(aCaseUUID, aFilter, new ChainedCallback<CaseItem>(handlers));

	}

	public void synchronizeDBs(final AsyncHandler<Void>... handlers) {
		GWT.log("RPC: synchronizeDBs");
		RpcConsoleServices.getCaseService().synchronizeDBs(new ChainedCallback<Void>(handlers));
		
	}

	public void getCases(final Collection<CaseUUID> aCaseSelection, final CaseFilter aCaseFilter, final AsyncHandler<Collection<CaseItem>>... handlers) {
		GWT.log("RPC: getCases");
		RpcConsoleServices.getCaseService().getCases(aCaseSelection, aCaseFilter, new ChainedCallback<Collection<CaseItem>>(handlers));

		
	}

	public void getCaseCommentFeed(final CaseUUID aCaseUUID, final CaseFilter aFilter, final AsyncHandler<List<CommentItem>>... handlers) {
		GWT.log("RPC: getCases");
		RpcConsoleServices.getCaseService().getCaseCommentFeed(aCaseUUID, aFilter, new ChainedCallback<List<CommentItem>>(handlers));
		
	}

  /**
   * @param aCaseUUID
   * @param aComment
   * @param aAsyncHandler
   */
  public void addCommentToCase(CaseUUID aCaseUUID, String aComment, CaseFilter aCaseFilter, AsyncHandler<List<CommentItem>>... handlers) {
    GWT.log("RPC: addCommentToCase");
    RpcConsoleServices.getCaseService().addCommentToCase(aCaseUUID, aComment, aCaseFilter, new ChainedCallback<List<CommentItem>>(handlers));
  }

  /**
   * Get the configuration.
   */
  @SuppressWarnings("unchecked")
  public void getConfiguration(AsyncHandler<CasesConfiguration> handler) {
    GWT.log("RPC: get case configuration");
    RpcConsoleServices.getCaseService().getConfiguration(new ChainedCallback<CasesConfiguration>(handler));
  }

  /**
   * Update configuration.
   */
  @SuppressWarnings("unchecked")
  public void updateConfiguration(final CasesConfiguration aConfiguration, AsyncHandler<CasesConfiguration> aCallback) {
    GWT.log("RPC: update cases configuration");
    RpcConsoleServices.getCaseService().updateConfiguration(aConfiguration, new ChainedCallback<CasesConfiguration>(aCallback));
  }
}
