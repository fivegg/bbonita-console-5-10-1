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
package org.bonitasoft.console.client.categories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.categories.exceptions.CategoryAlreadyExistsException;
import org.bonitasoft.console.client.categories.exceptions.CategoryNotFoundException;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface CategoryService extends RemoteService {

  /**
   * List Categorys that match the given filter.
   * 
   * @param anItemFilter
   * @param aCallback
   */
  ItemUpdates<Category> getAllCategories(final SimpleFilter anItemFilter) throws ConsoleException, SessionTimeOutException;

  List<Category> getCategories(List<CategoryUUID> anItemSelection, SimpleFilter aFilter) throws CategoryNotFoundException, ConsoleException, SessionTimeOutException;

  Category getCategory(CategoryUUID anItemUUID, SimpleFilter aFilter) throws CategoryNotFoundException, ConsoleException, SessionTimeOutException;

  /**
   * Create a new Category.
   * 
   * @param aCategory
   * @param anItemFilter
   * @param aCallback
   * @throws CategoryNotFoundException
   * @throws CategoryAlreadyExistsException
   */
  ItemUpdates<Category> addCategory(final Category aCategory, final SimpleFilter anItemFilter) throws ConsoleException, SessionTimeOutException, CategoryAlreadyExistsException,
      CategoryNotFoundException;

  /**
   * Update the given Category definition.
   * 
   * @param aCategoryName
   * @param aCategory
   * @param aCallback
   */
  Category updateCategory(final CategoryUUID aCategoryUUID, final Category aCategory) throws CategoryNotFoundException, CategoryAlreadyExistsException, ConsoleException, SessionTimeOutException;

  /**
   * Remove the given Categorys.
   * 
   * @param anItemsSelection
   * @param anItemFilter
   * @param aCallback
   */
  ItemUpdates<Category> removeCategories(final Collection<CategoryUUID> anItemsSelection, final SimpleFilter anItemFilter) throws CategoryNotFoundException, ConsoleException, SessionTimeOutException;

  /**
   * Get categories identified by their name.
   * 
   * @param aBunchOfCategoriesName
   * @return
   * @throws SessionTimeOutException
   * @throws CategoryNotFoundException
   */
  List<Category> getCategoriesByName(Set<String> aBunchOfCategoriesName) throws SessionTimeOutException, CategoryNotFoundException;

  /**
   * List visible categories; based on rules.
   * 
   * @return the list of categories the user is allowed to see
   * @throws SessionTimeOutException
   */
  Set<Category> getVisibleCategories() throws SessionTimeOutException;

  /**
   * Get the number of cases having the given category.
   * 
   * @return
   * @throws SessionTimeOutException
   * @throws ConsoleException 
   */
  Integer getNumberOfCases(Category aCategory, boolean searchInHistory) throws SessionTimeOutException, ConsoleException;
}
