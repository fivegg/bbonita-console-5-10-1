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
package org.bonitasoft.console.client.reporting.exceptions;

import org.bonitasoft.console.client.reporting.ReportUUID;

/**
 * @author Nicolas Chabanoles
 *
 */
public class ReportNotFoundException  extends Exception {

    private static final long serialVersionUID = -2348031000219650755L;
    private ReportUUID myId;
    
    /**
     * Default constructor.
     */
    protected ReportNotFoundException() {
      // Mandatory for the serialization
    }
    
    /**
     * Default constructor.
     */
    public ReportNotFoundException(ReportUUID anId) {
      myId = anId;
    }
    
    public ReportUUID getReportId(){
      return myId;
    }
}
