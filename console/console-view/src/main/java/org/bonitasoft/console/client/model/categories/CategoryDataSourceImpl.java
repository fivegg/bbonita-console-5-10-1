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
package org.bonitasoft.console.client.model.categories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.model.DefaultFilteredDataSourceImpl;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.SimpleSelection;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoryDataSourceImpl extends DefaultFilteredDataSourceImpl<CategoryUUID, Category, SimpleFilter> implements CategoryDataSource {

    protected ArrayList<Category> myVisibleCategories;

    public CategoryDataSourceImpl(MessageDataSource aMessageDataSource) {
        super(new CategoryData(), new SimpleSelection<CategoryUUID>(), aMessageDataSource);
        setItemFilter(new SimpleFilter(0, 20));
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.bonitasoft.console.client.model.categories.CategoryDataSource#
     * getItemsByName(java.util.List,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @SuppressWarnings("unchecked")
    public void getItemsByName(final List<String> aCategoryNameList, final AsyncHandler<List<Category>> aHandler) {
        if (aCategoryNameList != null && !aCategoryNameList.isEmpty()) {
            Set<String> theBunchOfCategoriesName = new HashSet<String>(aCategoryNameList);
            ((CategoryData) myRPCItemData).getCategoriesByName(theBunchOfCategoriesName, new AsyncHandler<List<Category>>() {
                public void handleFailure(Throwable aT) {
                    if (aT instanceof SessionTimeOutException) {
                        myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                    } else if (aT instanceof ConsoleSecurityException) {
                        myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                    }
                    if (aT instanceof ConsoleException) {
                        if (myMessageDataSource != null) {
                            myMessageDataSource.addErrorMessage((ConsoleException) aT);
                        }
                    }

                    if (aHandler != null) {
                        aHandler.handleFailure(aT);
                    }

                }

                public void handleSuccess(List<Category> aResult) {
                    if (aResult != null) {
                        if (myKnownItems == null) {
                            myKnownItems = new HashMap<CategoryUUID, Category>();
                        }
                        updateItems(aResult);
                        if (aHandler != null) {
                            final ArrayList<Category> theResult = new ArrayList<Category>();
                            for (Category theCategory : aResult) {
                                theResult.add(myKnownItems.get(theCategory.getUUID()));
                            }
                            aHandler.handleSuccess(theResult);
                        }
                    }
                }
            });
        } else {
            if (aHandler != null) {
                aHandler.handleSuccess(new ArrayList<Category>());
            }
        }
    }

    private void notifyHandlerWithBunchOfVisibleCategories(final List<String> aCategoryNameList, final AsyncHandler<List<Category>> aHandler) {
        if (aHandler != null) {
            final List<Category> theResult = new ArrayList<Category>();
            final HashMap<String, Category> theTempResult = new HashMap<String, Category>();
            for (Category theCategory : myVisibleCategories) {
                if (aCategoryNameList.contains(theCategory.getName())) {
                    theTempResult.put(theCategory.getName(), theCategory);
                }
            }
            for (String theCategoryName : aCategoryNameList) {
                if (theTempResult.containsKey(theCategoryName)) {
                    theResult.add(theTempResult.get(theCategoryName));
                }
            }
            aHandler.handleSuccess(theResult);
        }
    }

    public void getVisibleItemsByName(final List<String> aCategoryNameList, final AsyncHandler<List<Category>> aHandler) {
        if (aCategoryNameList != null && !aCategoryNameList.isEmpty()) {
            if (myKnownItems == null) {
                getVisibleCategories(new AsyncHandler<List<Category>>() {
                    public void handleFailure(Throwable aT) {
                        if (aHandler != null) {
                            aHandler.handleFailure(aT);
                        }
                    }

                    public void handleSuccess(List<Category> aResult) {
                        notifyHandlerWithBunchOfVisibleCategories(aCategoryNameList, aHandler);
                    }

                });
            } else {
                notifyHandlerWithBunchOfVisibleCategories(aCategoryNameList, aHandler);
            }
        }
    }

    public void getVisibleCategories(final AsyncHandler<List<Category>> aHandler) {
        ((CategoryData) myRPCItemData).getVisibleCategories(new AsyncHandler<Set<Category>>() {
            public void handleFailure(Throwable aT) {
                if (aT instanceof SessionTimeOutException) {
                    myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                } else if (aT instanceof ConsoleSecurityException) {
                    myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                }
                if (aT instanceof ConsoleException) {
                    if (myMessageDataSource != null) {
                        myMessageDataSource.addErrorMessage((ConsoleException) aT);
                    }
                }

                if (aHandler != null) {
                    aHandler.handleFailure(aT);
                }
            }

            public void handleSuccess(Set<Category> aResult) {
                if (aResult != null) {
                    if (myKnownItems == null) {
                        myKnownItems = new HashMap<CategoryUUID, Category>();
                    }
                    ArrayList<Category> theOldValue = new ArrayList<Category>();
                    if (myVisibleCategories == null) {
                        myVisibleCategories = new ArrayList<Category>();
                    } else {
                        theOldValue.addAll(myVisibleCategories);
                    }
                    updateItems(new ArrayList<Category>(aResult));
                    final ArrayList<Category> theResult = new ArrayList<Category>();
                    for (Category theCategory : aResult) {
                        theResult.add(myKnownItems.get(theCategory.getUUID()));
                    }
                    // Ensure order.
                    Collections.sort(theResult);
                    myVisibleCategories.clear();
                    myVisibleCategories.addAll(theResult);
                    if (aHandler != null) {
                        aHandler.handleSuccess(theResult);
                    }
                    myChanges.fireModelChange(VISIBLE_CATEGORIES_LIST_PROPERTY, theOldValue, myVisibleCategories);
                }
            }
        });

    }

    public void getNumberOfCases(final Category aCategory, final boolean searchInHistory) {

        if (aCategory != null) {

            ((CategoryData) myRPCItemData).getNumberOfCases(aCategory, searchInHistory, new AsyncHandler<Integer>() {
                public void handleFailure(Throwable aT) {
                    if (aT instanceof SessionTimeOutException) {
                        myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                    } else if (aT instanceof ConsoleSecurityException) {
                        myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                    }
                    if (aT instanceof ConsoleException) {
                        if (myMessageDataSource != null) {
                            myMessageDataSource.addErrorMessage((ConsoleException) aT);
                        }
                    }
                    myMessageDataSource.addErrorMessage(messages.unableToUpdateLabel());
                }

                public void handleSuccess(Integer aResult) {
                    GWT.log("Receiving number of cases for category");
                    if (myKnownItems.containsKey(aCategory.getUUID())) {
                        myKnownItems.get(aCategory.getUUID()).setNbOfCases(aResult);
                    } else {
                        // Category not found!
                        GWT.log("Category not found: " + aCategory.getUUID());
                    }
                }
            });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.DefaultFilteredDataSourceImpl#getItem
     * (org.bonitasoft.console.client.BonitaUUID,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @Override
    public void getItem(final CategoryUUID anUUID, final AsyncHandler<Category> aHandler) {
        if (myVisibleCategories != null) {
            if (aHandler != null) {
                aHandler.handleSuccess(getItem(anUUID));
            }
        } else {
            getVisibleCategories(new AsyncHandler<List<Category>>() {
                public void handleFailure(Throwable aT) {
                    if (aHandler != null) {
                        aHandler.handleFailure(aT);
                    }
                }

                public void handleSuccess(List<Category> aResult) {
                    if (aHandler != null) {
                        aHandler.handleSuccess(getItem(anUUID));
                    }
                }
            });
        }
    }
}
