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
package org.bonitasoft.console.client;

import java.io.Serializable;

import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;

/**
 * @author Nicolas Chabanoles
 * 
 */
public abstract class ItemFilter implements Serializable {

  private static final long serialVersionUID = 7370096622260556200L;

  public static final String SEARCH_IN_HISTORY_PROPERTY = "filter search in history";

  protected int myStartingIndex;
  protected int myMaxElementCount;

  protected String mySearchPattern;

  protected transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

  protected ItemFilter() {
    super();
    // Mandatory for serialization.
  }

  public ItemFilter(final int aStartingIndex, final int aPageSize) {
    myStartingIndex = aStartingIndex;
    myMaxElementCount = aPageSize;
    mySearchPattern = null;
  }

  /**
   * @return the startingIndex
   */
  public int getStartingIndex() {
    return myStartingIndex;
  }

  /**
   * @param anStartingIndex
   *          the startingIndex to set
   */
  public void setStartingIndex(int anStartingIndex) {
    myStartingIndex = anStartingIndex;
  }

  /**
   * @return the maxElementCount
   */
  public int getMaxElementCount() {
    return myMaxElementCount;
  }

  /**
   * @param anMaxElementCount
   *          the maxElementCount to set
   */
  public void setMaxElementCount(int anMaxElementCount) {
    myMaxElementCount = anMaxElementCount;
  }

  /**
   * @param searchPattern
   *          the searchPattern to set
   */
  public void setSearchPattern(String searchPattern) {
    mySearchPattern = searchPattern;
  }

  /**
   * @return the searchPattern
   */
  public String getSearchPattern() {
    return mySearchPattern;
  }
  
  public boolean isActive() {
    return mySearchPattern!=null && mySearchPattern.length()>0;
  }

  public abstract ItemFilter createFilter();

  /**
   * Add a property change listener.
   * 
   * @param aPropertyName
   * @param aListener
   */
  public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
    // Avoid duplicate subscription.
    myChanges.removeModelChangeListener(aPropertyName, aListener);
    myChanges.addModelChangeListener(aPropertyName, aListener);
  }

  /**
   * Remove a property change listener.
   * 
   * @param aPropertyName
   * @param aListener
   */
  public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
    myChanges.removeModelChangeListener(aPropertyName, aListener);
  }

  public void updateFilter(ItemFilter aFilter) {
    setMaxElementCount(aFilter.getMaxElementCount());
    setSearchPattern(aFilter.getSearchPattern());
    setStartingIndex(aFilter.getStartingIndex());
  }
}
