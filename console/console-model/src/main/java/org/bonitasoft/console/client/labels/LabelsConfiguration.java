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
package org.bonitasoft.console.client.labels;

import java.io.Serializable;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelsConfiguration implements Serializable {

  static final long serialVersionUID = 1902657971754564655L;
  private boolean isStarEnabled;
  private boolean isCustomLabelsEnabled;

  public LabelsConfiguration() {
    super();
    isStarEnabled = false;
    isCustomLabelsEnabled = false;
  }

  /**
   * @return the isGlobalReportingEnabled
   */
  public boolean isStarEnabled() {
    return isStarEnabled;
  }

  public void setStarEnabled(boolean enable) {
    isStarEnabled = enable;
  }

  public boolean isCustomLabelsEnabled() {
    return isCustomLabelsEnabled;
  }

  public void setCustomLabelsEnabled(boolean enable) {
    isCustomLabelsEnabled = enable;
  }

  @Override
  public boolean equals(Object anObj) {
    if (anObj == this) {
      return true;
    }
    if (anObj instanceof LabelsConfiguration) {
      LabelsConfiguration theOtherConfiguration = (LabelsConfiguration) anObj;
      return (this.isStarEnabled == theOtherConfiguration.isStarEnabled) && (this.isCustomLabelsEnabled == theOtherConfiguration.isCustomLabelsEnabled);
    }
    return false;
  }
}
