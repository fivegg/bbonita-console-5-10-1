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
package org.bonitasoft.console.server.categories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.categories.exceptions.CategoryAlreadyExistsException;
import org.bonitasoft.console.client.categories.exceptions.CategoryNotFoundException;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.identity.MembershipItem;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.users.UserProfile;
import org.ow2.bonita.facade.CommandAPI;
import org.ow2.bonita.facade.runtime.command.WebGetProcessInstancesNumberWithInvolvedUserAndCategory;
import org.ow2.bonita.facade.runtime.command.WebGetVisibleCategoriesCommand;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoryDataStore {

  private static CategoryDataStore instance;

  /**
   * Get the unique instance of UserDataStore.
   * 
   * @return
   */
  public static synchronized CategoryDataStore getInstance() {
    if (instance == null) {
      instance = new CategoryDataStore();
    }

    return instance;
  }

  /**
   * Default constructor.
   */
  private CategoryDataStore() {
    super();
  }

  public ItemUpdates<Category> addCategory(final UserProfile aUserProfile, final Category aCategory, final SimpleFilter anItemFilter) throws CategoryAlreadyExistsException, ConsoleException,
      ConsoleSecurityException, CategoryNotFoundException {

    try {
      AccessorUtil.getWebAPI().addCategory(aCategory.getName(), aCategory.getIconCSSStyle(), aCategory.getPreviewCSSStyleName(), aCategory.getCSSStyleName());
      return getAllCategories(aUserProfile, anItemFilter);
    } catch (org.ow2.bonita.facade.exception.CategoryAlreadyExistsException e) {
      throw new CategoryAlreadyExistsException(e.getName());
    }
  }

  /**
   * @param aUserProfile
   * @param aAnItemFilter
   * @return
   * @throws ConsoleException
   */
  public ItemUpdates<Category> getAllCategories(UserProfile aUserProfile, SimpleFilter anItemFilter) throws ConsoleException, ConsoleSecurityException {
    Set<org.ow2.bonita.facade.runtime.Category> theCategories = AccessorUtil.getWebAPI().getAllCategories();
    if (theCategories != null) {
      List<org.ow2.bonita.facade.runtime.Category> theListOfCategory = new ArrayList<org.ow2.bonita.facade.runtime.Category>(theCategories);
      // FIXME paging
      if (anItemFilter.getStartingIndex() > theListOfCategory.size()) {
        return new ItemUpdates<Category>(new ArrayList<Category>(), 0);
      }
      final int theLastIndex = anItemFilter.getStartingIndex() + anItemFilter.getMaxElementCount();
      if (theLastIndex > theListOfCategory.size()) {
        theListOfCategory = theListOfCategory.subList(anItemFilter.getStartingIndex(), theListOfCategory.size());
      } else {
        theListOfCategory = theListOfCategory.subList(anItemFilter.getStartingIndex(), theLastIndex);
      }
      return new ItemUpdates<Category>(buildCategories(theListOfCategory), theCategories.size());
    } else {
      return new ItemUpdates<Category>(new ArrayList<Category>(), 0);
    }
  }

  /**
   * @param aCategorys
   * @return
   */
  private List<Category> buildCategories(List<org.ow2.bonita.facade.runtime.Category> aListOfCategory) {
    ArrayList<Category> theResult = new ArrayList<Category>();
    Category theCategoryItem;
    for (org.ow2.bonita.facade.runtime.Category theSource : aListOfCategory) {
      theCategoryItem = buildCategory(theSource);
      theResult.add(theCategoryItem);
    }
    return theResult;
  }

  private Category buildCategory(org.ow2.bonita.facade.runtime.Category aSource) {
    final Category theCategoryItem = new Category(new CategoryUUID(aSource.getUUID()), aSource.getName());
    theCategoryItem.setCSSStyleName(aSource.getReadonlyCSSStyleName());
    theCategoryItem.setPreviewCSSStyleName(aSource.getPreviewCSSStyleName());
    theCategoryItem.setIconCSSStyle(aSource.getIconCSSStyle());
    return theCategoryItem;
  }

  /**
   * @param aUserProfile
   * @param aAnItemsSelection
   * @param aAnItemFilter
   * @return
   * @throws org.bonitasoft.console.client.identity.exception.CaseNotFoundException
   */
  public ItemUpdates<Category> removeCategories(UserProfile aUserProfile, Collection<CategoryUUID> anItemsSelection, SimpleFilter anItemFilter) throws ConsoleException, ConsoleSecurityException,
      CategoryNotFoundException {
    final Set<org.ow2.bonita.facade.uuid.CategoryUUID> theCategories = new HashSet<org.ow2.bonita.facade.uuid.CategoryUUID>();
    for (CategoryUUID theCategoryUUID : anItemsSelection) {
      theCategories.add(new org.ow2.bonita.facade.uuid.CategoryUUID(theCategoryUUID.getValue()));
    }
    try {
      AccessorUtil.getWebAPI().deleteCategoriesByUUIDs(theCategories);
    } catch (Exception e) {
      e.printStackTrace();
      throw new CategoryNotFoundException();
    }
    return getAllCategories(aUserProfile, anItemFilter);
  }

  /**
   * @param aUserProfile
   * @param aCategoryUuid
   *          .get
   * @param aCategory
   * @return
   * @throws CategoryAlreadyExistsException
   */
  public Category updateCategory(UserProfile aUserProfile, CategoryUUID aCategoryUuid, Category aCategory) throws CategoryNotFoundException, ConsoleException, ConsoleSecurityException,
      CategoryAlreadyExistsException {

    org.ow2.bonita.facade.runtime.Category theCategory;
    try {
      theCategory = AccessorUtil.getWebAPI().updateCategoryByUUID(aCategoryUuid.getValue(), aCategory.getName(), aCategory.getIconCSSStyle(), aCategory.getPreviewCSSStyleName(),
          aCategory.getCSSStyleName());
    } catch (org.ow2.bonita.facade.exception.CategoryNotFoundException e) {
      e.printStackTrace();
      throw new CategoryNotFoundException(e.getName());
    } catch (org.ow2.bonita.facade.exception.CategoryAlreadyExistsException e) {
      e.printStackTrace();
      throw new CategoryAlreadyExistsException(e.getName());
    }
    return buildCategory(theCategory);
  }

  /**
   * @param aUserProfile
   * @param aAnItemUUID
   * @param aFilter
   * @return
   * @throws CategoryNotFoundException
   */
  public Category getCategory(UserProfile aUserProfile, CategoryUUID anItemUUID, SimpleFilter aFilter) throws CategoryNotFoundException, ConsoleException {
    // org.ow2.bonita.facade.runtime.Category theCategory;
    // try {
    // theCategory =
    // AccessorUtil.getWebAPI().getCategoryByUUID(anItemUUID.getValue());
    // } catch (org.ow2.bonita.facade.exception.CategoryNotFoundException e) {
    // e.printStackTrace();
    // throw new CategoryNotFoundException(e.getName());
    // }
    // return buildCategory(theCategory);
    throw new ConsoleException("No API yet!", null);
  }

  /**
   * @param aUserProfile
   * @param aAnItemSelection
   * @param aFilter
   * @return
   * @throws ConsoleException
   * @throws CategoryNotFoundException
   */
  public List<Category> getCategories(UserProfile aUserProfile, Collection<CategoryUUID> anItemSelection, SimpleFilter aFilter) throws ConsoleException, CategoryNotFoundException {
    Set<org.ow2.bonita.facade.runtime.Category> theCategories;
    try {

      final Set<org.ow2.bonita.facade.uuid.CategoryUUID> theCategoryIDs = new HashSet<org.ow2.bonita.facade.uuid.CategoryUUID>();
      for (CategoryUUID theCategoryUUID : anItemSelection) {
        theCategoryIDs.add(new org.ow2.bonita.facade.uuid.CategoryUUID(theCategoryUUID.getValue()));
      }
      theCategories = AccessorUtil.getWebAPI().getCategoriesByUUIDs(theCategoryIDs);
      if (theCategoryIDs == null || theCategories.isEmpty()) {
        return new ArrayList<Category>();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new CategoryNotFoundException();
    }
    return buildCategories(new ArrayList<org.ow2.bonita.facade.runtime.Category>(theCategories));
  }

  /**
   * @param aUserProfile
   * @param aBunchOfCategoriesName
   * @return
   * @throws CategoryNotFoundException
   */
  public List<Category> getCategoriesByName(UserProfile aUserProfile, Set<String> aBunchOfCategoriesName) throws CategoryNotFoundException {
    try {
      Set<org.ow2.bonita.facade.runtime.Category> theCategories = AccessorUtil.getWebAPI().getCategories(aBunchOfCategoriesName);
      if (theCategories == null || theCategories.isEmpty()) {
        return new ArrayList<Category>();
      }
      return buildCategories(new ArrayList<org.ow2.bonita.facade.runtime.Category>(theCategories));
    } catch (Exception e) {
      e.printStackTrace();
      throw new CategoryNotFoundException();
    }
  }

  public Set<Category> getVisibleCategories(User aUser) throws ConsoleException {
    final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();
    Set<String> theUserRoles = new HashSet<String>();
    Set<String> theUserGroups = new HashSet<String>();
    Set<String> theUserMemberships = new HashSet<String>();
    if (aUser.getMembership() != null) {
      for (MembershipItem theMembership : aUser.getMembership()) {
        theUserMemberships.add(theMembership.getUUID().getValue());
        theUserGroups.add(theMembership.getGroup().getUUID().getValue());
        theUserRoles.add(theMembership.getRole().getUUID().getValue());
      }
    }
    Set<org.ow2.bonita.facade.runtime.Category> theCategories;
    try {
      theCategories = theCommandAPI.execute(new WebGetVisibleCategoriesCommand(aUser.getUUID().getValue(), theUserRoles, theUserGroups, theUserMemberships, aUser.getUsername()));

      final Set<Category> theResult = new HashSet<Category>();
      if (theCategories != null) {
        for (org.ow2.bonita.facade.runtime.Category category : theCategories) {
          theResult.add(buildCategory(category));
        }
      }
      return theResult;
    } catch (Exception e) {
      throw new ConsoleException();
    }
  }

  public Set<Category> getVisibleCategories(String aUsername) throws ConsoleException {
    final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();
    Set<org.ow2.bonita.facade.runtime.Category> theCategories;
    try {
      theCategories = theCommandAPI.execute(new WebGetVisibleCategoriesCommand(null, null, null, null, aUsername));

      final Set<Category> theResult = new HashSet<Category>();
      if (theCategories != null) {
        for (org.ow2.bonita.facade.runtime.Category category : theCategories) {
          theResult.add(buildCategory(category));
        }
      }
      return theResult;
    } catch (Exception e) {
      throw new ConsoleException();
    }
  }

  /**
   * List visible categories for the given user; based on rules.
   * 
   * @param aUserProfile
   * @return the visible categories
   * @throws ConsoleException
   */
  public Set<Category> getVisibleCategories(UserProfile aUserProfile) throws ConsoleException {
    if (aUserProfile.getUser() != null) {
      return getVisibleCategories(aUserProfile.getUser());
    } else {
      return getVisibleCategories(aUserProfile.getUsername());
    }
  }

  /**
   * @param aUserProfile
   * @param aCategory
   * @param searchInHistory
   * @return
   * @throws ConsoleException
   */
  public Integer getNumberOfCases(UserProfile aUserProfile, Category aCategory, boolean searchInHistory) throws ConsoleException {
    final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();
    try {
      final String theUsername = aUserProfile.getUsername();
      final User theUser = aUserProfile.getUser();
      final String theUserID;
      final Set<String> theUserRoles;
      final Set<String> theUserGroups;
      final Set<String> theUserMemberships;
      if (theUser != null) {
        theUserID = theUser.getUUID().getValue();
        theUserGroups = new HashSet<String>();
        theUserRoles = new HashSet<String>();
        theUserMemberships = new HashSet<String>();
        if (theUser.getMembership() != null) {
          for (MembershipItem theMembership : theUser.getMembership()) {
            theUserMemberships.add(theMembership.getUUID().getValue());
            theUserGroups.add(theMembership.getGroup().getUUID().getValue());
            theUserRoles.add(theMembership.getRole().getUUID().getValue());
          }
        }
      } else {
        theUserID = null;
        theUserRoles = null;
        theUserGroups = null;
        theUserMemberships = null;
      }
      return theCommandAPI.execute(new WebGetProcessInstancesNumberWithInvolvedUserAndCategory(theUserID, theUserRoles, theUserGroups, theUserMemberships, theUsername, aCategory.getName(), searchInHistory));
    } catch (Exception e) {
      throw new ConsoleException("Unable to compute case number.",e);
    }
  }
}
