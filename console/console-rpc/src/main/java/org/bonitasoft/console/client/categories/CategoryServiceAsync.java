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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Nicolas Chabanoles
 */
public interface CategoryServiceAsync {

  void getAllCategories(final SimpleFilter anItemFilter, final AsyncCallback<ItemUpdates<Category>> aCallback);

  void getCategories(List<CategoryUUID> anItemSelection, SimpleFilter aFilter, AsyncCallback<List<Category>> aCallback);

  void getCategory(CategoryUUID anItemUUID, SimpleFilter aFilter, AsyncCallback<Category> aCallback);

  void addCategory(final Category aCategory, final SimpleFilter anItemFilter, final AsyncCallback<ItemUpdates<Category>> aCallback);

  void updateCategory(final CategoryUUID aCategoryUUID, final Category aCategory, final AsyncCallback<Category> aCallback);

  void removeCategories(final Collection<CategoryUUID> anItemsSelection, final SimpleFilter anItemFilter, final AsyncCallback<ItemUpdates<Category>> aCallback);

  void getCategoriesByName(Set<String> aBunchOfCategoriesName, AsyncCallback<List<Category>> aCallback);

  void getVisibleCategories(AsyncCallback<Set<Category>> aCallback);

  void getNumberOfCases(Category aCategory, boolean searchInHistory, AsyncCallback<Integer> aCallback);

}
