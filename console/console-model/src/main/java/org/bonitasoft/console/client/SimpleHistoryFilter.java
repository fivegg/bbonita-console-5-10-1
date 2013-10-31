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


/**
 * @author Nicolas Chabanoles
 * 
 */
public class SimpleHistoryFilter extends SimpleFilter {

  private static final long serialVersionUID = 6434254878720728448L;
  protected boolean searchInHistory;
	
	/**
   * Default constructor.
   */
  protected SimpleHistoryFilter() {
   super();
  }
	
	public SimpleHistoryFilter(final int aStartingIndex, final int aPageSize) {
		super(aStartingIndex, aPageSize);
		searchInHistory = false;
	}

	
	public boolean searchInHistory() {
		return searchInHistory;
	}

	public void setSearchInHistory(boolean considerHistory) {
		searchInHistory = considerHistory;
	}
	
	@Override
	public ItemFilter createFilter() {
		SimpleHistoryFilter theResult = new SimpleHistoryFilter();
		theResult.updateFilter(this);
		return theResult;
	}

	
	public void updateFilter(SimpleHistoryFilter aFilter) {
	  super.updateFilter(aFilter);
	  searchInHistory = aFilter.searchInHistory();
	}
}
