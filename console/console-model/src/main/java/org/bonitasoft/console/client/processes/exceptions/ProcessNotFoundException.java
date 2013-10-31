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
package org.bonitasoft.console.client.processes.exceptions;

import org.bonitasoft.console.client.processes.BonitaProcessUUID;

/**
 * @author Nicolas Chabanoles
 *
 */
public class ProcessNotFoundException extends Exception {

  private static final long serialVersionUID = -1587437080555553840L;
  private BonitaProcessUUID myId;
  
  /**
   * Default constructor.
   */
  protected ProcessNotFoundException() {
    // Mandatory for the serialization
  }
  
  /**
   * Default constructor.
   */
  public ProcessNotFoundException(BonitaProcessUUID anId) {
    myId = anId;
  }
  
  public BonitaProcessUUID getProcessId(){
    return myId;
  }
}
