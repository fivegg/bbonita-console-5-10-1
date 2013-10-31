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
package org.bonitasoft.forms.server.accessor;

import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;

/**
 * Accessor API allowing to retrieve the configuration of a Forms definition for a process
 * 
 * @deprecated
 * @author Anthony Birembaut
 */
public interface IProcessConfigDefAccessor {
    
    /**
     * get the process label
     * 
     * @return the id of the process
     * @throws InvalidFormDefinitionException if the process label cannot be found
     */
    String getProcessLabel() throws InvalidFormDefinitionException;
    
    /**
     * @return the path of the process template in the classpath
     */
    String getProcessTemplate();

    /**
     * @return the symbol to use to indicate that a form field is mandatory
     */
    String getProcessMandatorySymbol();

    /**
     * @return the label to use to indicate that a form field is mandatory
     */
    String getProcessMandatoryLabel();
    
    /**
     * @return the CSS classes asociated with the symbol to use to indicate that a form field is mandatory
     */
    String getProcessMandatorySymbolStyle();
    
    /**
     * @return the path to the process error template
     */
    String getProcessErrorTemplate();
    
    /**
     * @return the process name as specified in the forms.xml
     * @throws InvalidFormDefinitionException 
     */
    String getProcessName() throws InvalidFormDefinitionException;
    
    /**
     * @return the process version as specified in the forms.xml
     * @throws InvalidFormDefinitionException 
     */
    String getProcessVersion() throws InvalidFormDefinitionException;
}
