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
package org.bonitasoft.console.client.model.categories;

import java.util.List;

import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface CategoryDataSource extends BonitaFilteredDataSource<CategoryUUID, Category, SimpleFilter> {

  String VISIBLE_CATEGORIES_LIST_PROPERTY = "categories visible items";

  /**
   * Get categories by name.
   * @param aCategories
   * @param aAsyncHandler
   */
  void getItemsByName(List<String> aCategoryNameList, AsyncHandler<List<Category>> aHandler);
  
  /**
   * Get categories by name. Only consider categories visible by the user.
   * @param aCategoryNameList
   * @param aHandler
   */
  void getVisibleItemsByName(List<String> aCategoryNameList, AsyncHandler<List<Category>> aHandler);

  /**
   * List categories the user is allowed to see.
   */
  void getVisibleCategories(AsyncHandler<List<Category>> aHandler);

  public void getNumberOfCases(final Category aCategory, final boolean searchInHistory);
}
