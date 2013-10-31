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
package org.bonitasoft.console.client.cases;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CasesConfiguration implements Serializable {

  private static final String CSV_SEPARATOR = ",";
  private static final long serialVersionUID = -6255852336621246478L;

  public enum Columns {
    SELECT_COLUMN, STAR_COLUMN, CASE_STATE_COLUMN, LABELS_COLUMN, CATEGORIES_COLUMN, DESCRIPTION_COLUMN, STEP_STATE_COLUMN, STEP_ASSIGN_COLUMN, STEP_PRIORITY_COLUMN, STEP_NAME_COLUMN, STEP_DESCRIPTION_COLUMN, UPDATE_COLUMN, APPLICATION_COLUMN;
  }

  private HashMap<Integer, Columns> myColumns = new HashMap<Integer, Columns>();
  private HashMap<Columns, Integer> myColumnIndex = new HashMap<Columns, Integer>();
  private int myStretchedColumnIndex;

  public CasesConfiguration() {
    super();
  }

  public int getColumnIndex(Columns aColumn) {
    if (myColumnIndex.containsKey(aColumn)) {
      return myColumnIndex.get(aColumn);
    } else {
      return -1;
    }
  }

  public Columns getColumnAt(int anIndex) {
    return myColumns.get(anIndex);
  }

  /**
   * Return a CSV of Columns.
   */
  public String getColumnLayout() {
    final StringBuffer theLayout = new StringBuffer();
    Columns theColumn;
    for (int i = 0; i < Columns.values().length; i++) {
      theColumn = myColumns.get(i);
      if (theColumn != null) {
        theLayout.append(theColumn.name()).append(CSV_SEPARATOR);
      } else {
        theLayout.append(CSV_SEPARATOR);
      }
    }
    if (theLayout.length() > 1) {
      return theLayout.substring(0, theLayout.length() - 1);
    } else {
      return theLayout.toString();
    }
  }

  /**
   * Bind a column to a given index.<br/>
   * A negative index means that the column must be removed from mapping.</br> A
   * null column means that the index must be removed from mapping.</br>
   * 
   * @param aColumn
   * @param anIndex
   */
  public Columns setColumnIndex(Columns aColumn, int anIndex) throws IllegalArgumentException {

    final Columns theConflictingColumn;
    final Integer thePreviousIndex;

    if (aColumn != null) {
      // remove previous mapping for the given Column
      thePreviousIndex = myColumnIndex.remove(aColumn);
      if (thePreviousIndex != null) {
        myColumns.remove(thePreviousIndex);
      }
    }

    if (anIndex >= 0) {
      // remove previous mapping for the given index
      theConflictingColumn = myColumns.remove(anIndex);
      if (theConflictingColumn != null) {
        myColumnIndex.remove(theConflictingColumn);
      }
    } else {
      theConflictingColumn = null;
    }

    if (aColumn != null && anIndex >= 0) {
      // create new mapping
      myColumnIndex.put(aColumn, anIndex);
      myColumns.put(anIndex, aColumn);
    }

    return theConflictingColumn;
  }

  /**
   * Set the layout from a CSV string.
   */
  public void setLayout(String aLayout) {
    myColumns.clear();
    myColumnIndex.clear();
    if (aLayout == null) {
      setDefaultLayout();
    } else {
      String[] theColumns = aLayout.split(CSV_SEPARATOR);
      int i = 0;
      Columns theColumn;
      for (String theColumnName : theColumns) {
        try {
          theColumn = Columns.valueOf(theColumnName);
          setColumnIndex(theColumn, i);
        } catch (Exception e) {
          // ignore the item.
        }
        i++;
      }
    }
  }
  
  public void setStretchedColumnIndex(int anIndex) {
    myStretchedColumnIndex = anIndex;
  }
  
  public int getStretchedColumnIndex() {
    return myStretchedColumnIndex;
  }

  /**
   * Generate the default layout.<br/>
   * It is based on the order of the Columns enum.
   */
  private void setDefaultLayout() {
    int i = 0;
    for (Columns theColumn : Columns.values()) {
      setColumnIndex(theColumn, i);
      i++;
    }
  }

  /**
   * @param aSource
   */
  public void update(CasesConfiguration aSource) {
    if (aSource != null) {
      setLayout(aSource.getColumnLayout());
    } else {
      setDefaultLayout();
    }
    setStretchedColumnIndex(aSource.getStretchedColumnIndex());
  }


}
