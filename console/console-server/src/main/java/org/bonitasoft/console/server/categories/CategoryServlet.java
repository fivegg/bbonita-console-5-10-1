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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryService;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.categories.exceptions.CategoryAlreadyExistsException;
import org.bonitasoft.console.client.categories.exceptions.CategoryNotFoundException;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.server.login.SessionManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoryServlet extends RemoteServiceServlet implements CategoryService {

  private static final long serialVersionUID = 1331662742614812985L;
  private static final Logger LOGGER = Logger.getLogger(CategoryServlet.class.getName());

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.categories.CategoryService#addCategory(org
   * .bonitasoft.console.client.categories.Category,
   * org.bonitasoft.console.client.SimpleFilter)
   */
  public ItemUpdates<Category> addCategory(Category aCategory, SimpleFilter anItemFilter) throws ConsoleException, SessionTimeOutException, CategoryAlreadyExistsException, CategoryNotFoundException {

    if (aCategory == null) {
      throw new ConsoleException();
    }

    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<Category> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, theUserProfile.getUsername() + " adding new category: " + aCategory.getName() + "'.");
      }
      theResult = CategoryDataStore.getInstance().addCategory(theUserProfile, aCategory, anItemFilter);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, aCategory.getName() + " added successfully.");
      }
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (CategoryAlreadyExistsException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (CategoryNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.categories.CategoryService#getAllCategorys
   * (org.bonitasoft.console.client.SimpleFilter)
   */
  public ItemUpdates<Category> getAllCategories(SimpleFilter anItemFilter) throws ConsoleException, SessionTimeOutException {

    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<Category> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = CategoryDataStore.getInstance().getAllCategories(theUserProfile, anItemFilter);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.categories.CategoryService#getCategory(org
   * .bonitasoft.console.client.categories.CategoryUUID,
   * org.bonitasoft.console.client.SimpleFilter)
   */
  public Category getCategory(CategoryUUID anItemUUID, SimpleFilter aFilter) throws CategoryNotFoundException, ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    Category theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = CategoryDataStore.getInstance().getCategory(theUserProfile, anItemUUID, aFilter);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (CategoryNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.categories.CategoryService#getCategorys(java
   * .util.List, org.bonitasoft.console.client.SimpleFilter)
   */
  public List<Category> getCategories(List<CategoryUUID> anItemSelection, SimpleFilter aFilter) throws CategoryNotFoundException, ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    List<Category> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = CategoryDataStore.getInstance().getCategories(theUserProfile, anItemSelection, aFilter);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (CategoryNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.categories.CategoryService#removeCategorys
   * (java.util.Collection, org.bonitasoft.console.client.SimpleFilter)
   */
  public ItemUpdates<Category> removeCategories(Collection<CategoryUUID> anItemsSelection, SimpleFilter anItemFilter) throws CategoryNotFoundException, ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    ItemUpdates<Category> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = CategoryDataStore.getInstance().removeCategories(theUserProfile, anItemsSelection, anItemFilter);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (CategoryNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.categories.CategoryService#updateCategory
   * (org.bonitasoft.console.client.categories.CategoryUUID,
   * org.bonitasoft.console.client.categories.Category)
   */
  public Category updateCategory(CategoryUUID aCategoryUUID, Category aCategory) throws CategoryNotFoundException, ConsoleException, SessionTimeOutException, CategoryAlreadyExistsException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    Category theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = CategoryDataStore.getInstance().updateCategory(theUserProfile, aCategoryUUID, aCategory);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (ConsoleException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (CategoryAlreadyExistsException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (CategoryNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.categories.CategoryService#getCategoriesByName
   * (java.util.Set)
   */
  public List<Category> getCategoriesByName(Set<String> aBunchOfCategoriesName) throws SessionTimeOutException, CategoryNotFoundException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    List<Category> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = CategoryDataStore.getInstance().getCategoriesByName(theUserProfile, aBunchOfCategoriesName);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (CategoryNotFoundException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.categories.CategoryService#getVisibleCategories
   * ()
   */
  public Set<Category> getVisibleCategories() throws SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    Set<Category> theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = CategoryDataStore.getInstance().getVisibleCategories(theUserProfile);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    } catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.categories.CategoryService#getNumberOfCases()
   */
  public Integer getNumberOfCases(final Category aCategory, final boolean searchInHistory) throws SessionTimeOutException, ConsoleException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    Integer theResult;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      theResult = CategoryDataStore.getInstance().getNumberOfCases(theUserProfile, aCategory, searchInHistory);
    } catch (SessionTimeOutException e) {
      LOGGER.severe(e.getMessage());
      throw e;
    }catch (ConsoleException e) {
      e.printStackTrace();
      throw e;
    }  catch (Throwable e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
    return theResult;
  }

}
