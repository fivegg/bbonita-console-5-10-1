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
package org.bonitasoft.console.client;


/**
 * @author Nicolas Chabanoles
 * 
 */
public class SimpleFilter extends ItemFilter {


  private static final long serialVersionUID = -7494604805958845772L;
  protected boolean withAdminRights;

  /**
   * Default constructor.
   */
  protected SimpleFilter() {
    //Mandatory for serialization.
  }

  public SimpleFilter(final int aStartingIndex, final int aPageSize) {
    this(aStartingIndex, aPageSize, false);
  }
  
  public SimpleFilter(final int aStartingIndex, final int aPageSize, boolean withAdminRights) {
    super(aStartingIndex, aPageSize);
    this.withAdminRights = withAdminRights;
  }

  /**
   * @return the withAdminRights
   */
  public boolean isWithAdminRights() {
    return withAdminRights;
  }

  /**
   * @param anWithAdminRights
   *          the withAdminRights to set
   */
  public void setWithAdminRights(boolean anWithAdminRights) {
    withAdminRights = anWithAdminRights;
  }
  
  @Override
  public ItemFilter createFilter() {
    SimpleFilter theResult = new SimpleFilter();
    theResult.updateFilter(this);
    return theResult;
  }

  public void updateFilter(SimpleFilter aFilter) {
    super.updateFilter(aFilter);
    withAdminRights = aFilter.isWithAdminRights();
  }
}
