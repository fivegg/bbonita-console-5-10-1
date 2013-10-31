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

import java.util.ArrayList;
import java.util.Collection;

import org.bonitasoft.console.client.processes.BonitaProcessUUID;


/**
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessFilter extends SimpleHistoryFilter {

	private static final long serialVersionUID = -3555398862537844117L;

	protected Collection<BonitaProcessUUID> myProcesses;
	protected Collection<BonitaProcessUUID> myProcessesToIgnore;
	
	/**
   * Default constructor.
   */
  public ProcessFilter() {
   super();
  }
	
	public ProcessFilter(final int aStartingIndex, final int aPageSize) {
		super(aStartingIndex,aPageSize);
	}
	
	/**
     * @return the processCollection
     */
    public Collection<BonitaProcessUUID> getProcesses() {
        return myProcesses;
    }

    /**
     * @param aProcessCollection
     *            the processCollection to set
     */
    public void setProcesses(Collection<BonitaProcessUUID> aProcessCollection) {
        myProcesses = aProcessCollection;
    }
	
	/**
     * @return the processCollection
     */
    public Collection<BonitaProcessUUID> getProcessesToIgnore() {
        return myProcessesToIgnore;
    }
    
    /**
     * @param aProcessUUIDs the collection of processes to ignore.
     */
    public void setProcessesToIgnore(Collection<BonitaProcessUUID> aProcessUUIDs) {
        myProcessesToIgnore = aProcessUUIDs;
    }
	
	@Override
	public ProcessFilter createFilter() {
		ProcessFilter theResult = new ProcessFilter();
		theResult.updateFilter(this);
		return theResult;
	}

	public void updateFilter(ProcessFilter aFilter) {
	  super.updateFilter(aFilter);
	  myProcessesToIgnore = new ArrayList<BonitaProcessUUID>();
	  if(aFilter.getProcessesToIgnore()!=null) {
	      myProcessesToIgnore.addAll(aFilter.getProcessesToIgnore());
	  }
	  myProcesses = new ArrayList<BonitaProcessUUID>();
      if(aFilter.getProcesses()!=null) {
          myProcesses.addAll(aFilter.getProcesses());
      }
	  
	}


}
