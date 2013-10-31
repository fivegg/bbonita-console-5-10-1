/**
 * Copyright (C) 2011 BonitaSoft S.A.
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
 * Object containing the components of an URL
 * 
 * @author Anthony Birembaut, Chong Zhao
 */
public class FormURLComponents implements Serializable{

	/**
	 * UID
	 */
	private static final long serialVersionUID = 5255416377673207717L;

	/**
	 * The Application URL
	 */
	private String applicationURL;
	
	/**
	 * true if the application URL is different from the current URL
	 */
	private boolean changeApplication;
	
	/**
	 * the context of additionnal URL parameters
	 */
	private Map<String, Object> urlContext;

	/**
     * Default Constructor
     */
    public FormURLComponents(){
        super();
        // Mandatory for serialization
    }
	
	public String getApplicationURL() {
		return applicationURL;
	}

	public void setApplicationURL(final String applicationURL) {
		this.applicationURL = applicationURL;
	}

    public boolean isChangeApplication() {
        return changeApplication;
    }

    public void setChangeApplication(boolean changeApplication) {
        this.changeApplication = changeApplication;
    }

    /**
     * @return the urlContext
     */
    public Map<String, Object> getUrlContext() {
        return urlContext;
    }

    /**
     * @param urlContext the urlContext to set
     */
    public void setUrlContext(Map<String, Object> urlContext) {
        this.urlContext = urlContext;
    }
    
    
}
