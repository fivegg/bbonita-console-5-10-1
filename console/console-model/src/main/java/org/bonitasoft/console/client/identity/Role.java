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

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.Item;

/**
 * @author Anthony Birembaut, Nicolas Chabanoles
 * 
 */
public class Role implements Serializable, Item, Comparable<Role> {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 2224645302902982246L;

	private String name;
	private String label;
	private String description;

	private BonitaUUID uuid;

	

	/**
	 * Default constructor
	 */
	public Role() {
		super();
		// Mandatory for serialization.
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param label
	 * @param description
	 */
	public Role(String id, String name, String label, String description) {
		this.uuid = new BonitaUUID(id);
		this.name = name;
		this.label = label;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the uuid
	 */
	public BonitaUUID getUUID() {
		return uuid;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Role anotherRole) {
		return this.getName().toLowerCase().compareTo(anotherRole.getName());
	}

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.client.Item)
   */
  public void updateItem(Item aSource) {
    if(aSource!=null && aSource!=this && aSource instanceof Role){
      Role theOtherRole = (Role) aSource;
      setDescription(theOtherRole.getDescription());
      setLabel(theOtherRole.getLabel());
      setName(theOtherRole.getName());
    }
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object anObject) {
    if (anObject instanceof Role) {
      return uuid.equals(((Role) anObject).getUUID());
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return uuid.hashCode();
  }
}
