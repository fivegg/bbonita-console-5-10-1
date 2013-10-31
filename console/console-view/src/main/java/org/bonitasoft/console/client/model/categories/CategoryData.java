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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoryData implements RPCData<CategoryUUID, Category, SimpleFilter >{

  public void updateItem(CategoryUUID aCategoryUuId, Category aCategory, AsyncHandler<Category>... handlers) {
    GWT.log("RPC: updateCategory");
    RpcConsoleServices.getCategoryService().updateCategory(aCategoryUuId, aCategory, new ChainedCallback<Category>(handlers));
  }

  public void addItem(Category aCategory, SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<Category>>... handlers) {
    GWT.log("RPC: addCategory");
    RpcConsoleServices.getCategoryService().addCategory(aCategory, anItemFilter, new ChainedCallback<ItemUpdates<Category>>(handlers));
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#deleteItems(java.util.Collection, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void deleteItems(Collection<CategoryUUID> anItemSelection, final SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<Category>>... handlers) {
    GWT.log("RPC: removeCategorys");
    RpcConsoleServices.getCategoryService().removeCategories(anItemSelection,anItemFilter, new ChainedCallback<ItemUpdates<Category>>(handlers));
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getAllItems(org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void getAllItems(SimpleFilter anItemFilter, AsyncHandler<ItemUpdates<Category>>... handlers) {
    GWT.log("RPC: getAllCategorys");
    RpcConsoleServices.getCategoryService().getAllCategories(anItemFilter, new ChainedCallback<ItemUpdates<Category>>(handlers));
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItem(org.bonitasoft.console.client.CategoryUUID, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void getItem(CategoryUUID anItemUUID, SimpleFilter aFilter, AsyncHandler<Category>... handlers) {
    GWT.log("RPC: get a Category");
    RpcConsoleServices.getCategoryService().getCategory(anItemUUID, aFilter, new ChainedCallback<Category>(handlers));
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.common.data.RPCData#getItems(java.util.Collection, org.bonitasoft.console.client.ItemFilter, org.bonitasoft.console.client.common.data.AsyncHandler<java.util.Collection<I>>[])
   */
  public void getItems(List<CategoryUUID> anItemSelection, SimpleFilter aFilter, AsyncHandler<List<Category>>... handlers) {
    GWT.log("RPC: get a bunch of Categories");
    RpcConsoleServices.getCategoryService().getCategories(anItemSelection, aFilter, new ChainedCallback<List<Category>>(handlers));
    
  }

  public void getCategoriesByName(Set<String> aBunchOfCategoriesName, AsyncHandler<List<Category>>... handlers ) {
    GWT.log("RPC: get a bunch of Categories by their name");
    RpcConsoleServices.getCategoryService().getCategoriesByName(aBunchOfCategoriesName, new ChainedCallback<List<Category>>(handlers));
    
  }

  @SuppressWarnings("unchecked")
  public void getVisibleCategories(AsyncHandler<Set<Category>> aHandler) {
   GWT.log("RPC: get visible categories");
   RpcConsoleServices.getCategoryService().getVisibleCategories(new ChainedCallback<Set<Category>>(aHandler));
  }

  /**
   * @param aCategory
   * @param aSearchInHistory
   * @param aAsyncHandler
   */
  @SuppressWarnings("unchecked")
  public void getNumberOfCases(Category aCategory, boolean searchInHistory, AsyncHandler<Integer> aHandler) {
    GWT.log("RPC: get visible categories");
    RpcConsoleServices.getCategoryService().getNumberOfCases(aCategory, searchInHistory, new ChainedCallback<Integer>(aHandler));
  }

}
