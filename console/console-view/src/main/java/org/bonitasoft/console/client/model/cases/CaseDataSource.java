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

import java.util.List;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.cases.CasesConfiguration;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.steps.CommentItem;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface CaseDataSource extends BonitaFilteredDataSource<CaseUUID, CaseItem, CaseFilter> {

	/**
	 * The name of the property used when the list of myCases is updated.
	 */
  public static final String CASE_LIST_PROPERTY = "case list";
  public static final String TO_CURRENT_POSITION = "redirect position";
  public static final String COMMENTS_PROPERTY = "case comments";
  public static final String FILTER_UPDATED = "case filter";
  static final String CONFIGURATION_PROPERTY = "case datasource configuration";

	/**
	 * Delete the cases listed in the given List.
	 * 
	 * @param aItemSelection
	 */
	public void deleteCases(List<CaseUUID> aItemSelection, boolean deleteAttachments);

	/**
	 * Cancel the cases listed in the given List.
	 * 
	 * @param anItemSelection
	 */
	public void cancelCases(List<CaseUUID> anItemSelection);

	/**
	 * Get the case selection.
	 * 
	 * @return
	 */
	public CaseSelection getCaseSelection();

	/**
	 * Force the DB to be synchronized with the runtime one.
	 * @param aHandler
	 */
	public void synchronizeDBs(AsyncHandler<Void> aHandler);
	
	/**
	 * Get the comment feed of the case based on its given UUID.
	 * Return null if there is no comment.
	 * @param aHandler
	 */
	public void getCaseCommentFeed(CaseUUID aCaseUUID, AsyncHandler<List<CommentItem>> aHandler);

  /**
   * @param aUuid
   * @param aText
   * @param aSaveNewCommentHandler
   */
  public void addCaseComment(CaseUUID aCaseUUID, String aComment, AsyncHandler<List<CommentItem>> aHandler);
  
  /**
   * Get the configuration.
   */
  public void getConfiguration(AsyncHandler<CasesConfiguration> aHandler);

  /**
   * Update configuration.
   */
  public void updateConfiguration(final CasesConfiguration aNewConfiguration, AsyncHandler<CasesConfiguration> aHandler);
  
  /**
   * Add the CategoryDataSource
   * @param aCategoryDataSource
   */
  public void addCategoryDataSource(CategoryDataSource aCategoryDataSource);
  
  /**
   * Add the LabelDataSource
   * @param aLabelDataSource
   */
  public void addLabelDataSource(LabelDataSource aLabelDataSource);
}
