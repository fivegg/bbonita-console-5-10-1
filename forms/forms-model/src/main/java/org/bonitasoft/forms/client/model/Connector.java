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
package org.bonitasoft.forms.client.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Anthony Birembaut
 *
 */
public class Connector implements Serializable {

    /** 
     * UID 
     */
    private static final long serialVersionUID = -4190294573271008139L;

    /**
     * The connector classname
     */
    private String className;

    /**
     * The connector's input parameters
     */
    private Map<String, Serializable[]> inputParameters;
    
    /**
     * The connector's output parameters
     */
    private Map<String, Serializable[]> outputParameters;

    /**
     * indicates if any exception occuring should be thrown or ignored
     */
    private boolean throwingException = true;

    /**
     * Connector
     * @param className
     * @param throwingException
     */
    public Connector(final String className, final boolean throwingException) {
        super();
        this.className = className;
        this.throwingException = throwingException;
    }

    /**
     * Default Constructor
     */
    public Connector() {
        super();
        // Mandatory for serialization
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public boolean isThrowingException() {
        return throwingException;
    }

    public void setThrowingException(final boolean throwingException) {
        this.throwingException = throwingException;
    }

    public Map<String, Serializable[]> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(final Map<String, Serializable[]> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public Map<String, Serializable[]> getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(final Map<String, Serializable[]> outputParameters) {
        this.outputParameters = outputParameters;
    }

}
