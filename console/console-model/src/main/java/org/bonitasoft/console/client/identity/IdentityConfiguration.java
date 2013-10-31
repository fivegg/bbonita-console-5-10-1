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
package org.bonitasoft.console.client.identity;

import java.io.Serializable;

/**
 * @author Nicolas Chabanoles
 *
 */
public class IdentityConfiguration implements Serializable {

	private static final long serialVersionUID = 78277114446747859L;
	
	private boolean isUserCompletionEnabled;
	
	
	public IdentityConfiguration() {
		super();
		isUserCompletionEnabled = true;
	}

	public IdentityConfiguration(IdentityConfiguration aModel) {
		if(aModel==null){
			throw new IllegalArgumentException("The model must be non null!");
		}
		this.isUserCompletionEnabled = aModel.isUserCompletionEnabled();
	}

	/**
	 * @return the isUserCompletionEnabled
	 */
	public boolean isUserCompletionEnabled() {
		return this.isUserCompletionEnabled;
	}


	public void setUserCompletionEnabled(boolean enabled) {
		this.isUserCompletionEnabled = enabled;
	}
	

	@Override
	public boolean equals(Object anObj) {
		if(anObj == this) {
			return true;
		}
		if(anObj instanceof IdentityConfiguration){
			IdentityConfiguration theOtherConfiguration = (IdentityConfiguration) anObj;
			return (this.isUserCompletionEnabled == theOtherConfiguration.isUserCompletionEnabled);
		}
		return false;
	}
}
