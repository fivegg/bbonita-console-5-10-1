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
package org.bonitasoft.console.client.identity;

import java.io.Serializable;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.Item;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserMetadataItem implements Serializable, Item, Comparable<UserMetadataItem> {

  private static final long serialVersionUID = -4287816682858247351L;
  private String myName;
  private String myLabel;
  private BonitaUUID myUuid;

  /**
   * Default constructor
   */
  public UserMetadataItem() {
    super();
    // Mandatory for serialization.
  }

  /**
   * 
   * Default constructor.
   * 
   * @param anId
   * @param aName
   * @param aLabel
   * @param aDescription
   */
  public UserMetadataItem(String anId, String aName, String aLabel) {
    this.myUuid = new BonitaUUID(anId);
    this.myName = aName;
    this.myLabel = aLabel;
  }

  public UserMetadataItem(String anId) {
    this.myUuid = new BonitaUUID(anId);
  }

  /**
   * @return the name
   */
  public String getName() {
    return myName;
  }

  /**
   * @param aName
   *          the name to set
   */
  public void setName(String aName) {
    myName = aName;
  }

  /**
   * @return the label
   */
  public String getLabel() {
    return myLabel;
  }

  /**
   * @param aLabel
   *          the label to set
   */
  public void setLabel(String aLabel) {
    myLabel = aLabel;
  }


  public BonitaUUID getUUID() {
    return myUuid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(UserMetadataItem anotherGroup) {
    return this.getName().toLowerCase().compareTo(anotherGroup.getName());
  }
  
  public String getLabelOrName() {
    if(myLabel!=null && myLabel.length()>0) {
      return myLabel;
    } else {
      return myName;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.client
   * .Item)
   */
  public void updateItem(Item aSource) {
    if (aSource != null && aSource!=this && aSource instanceof UserMetadataItem) {
      UserMetadataItem theOtherItem = (UserMetadataItem) aSource;
      myName = theOtherItem.getName();
      myLabel = theOtherItem.getLabel();
    }
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return (myName==null?"":myName) + "(" + (myLabel==null?"":myLabel) + ")";
  }
}
