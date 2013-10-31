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
public class Group implements Serializable, Item, Comparable<Group> {

  private static final long serialVersionUID = -1910405080819607583L;
  public static final String PATH_SEPARATOR = "/";
  private String myName;
  private String myLabel;
  private String myDescription;
  private BonitaUUID myUuid;
  private Group myParentGroup;

  /**
   * Default constructor
   */
  public Group() {
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
  public Group(String anId, String aName, String aLabel, String aDescription) {
    this.myParentGroup = null;
    this.myUuid = new BonitaUUID(anId);
    this.myName = aName;
    this.myLabel = aLabel;
    this.myDescription = aDescription;
  }

  public Group(String anId) {
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

  /**
   * @return the description
   */
  public String getDescription() {
    return myDescription;
  }

  /**
   * @param aDescription
   *          the description to set
   */
  public void setDescription(String aDescription) {
    myDescription = aDescription;
  }

  public BonitaUUID getUUID() {
    return myUuid;
  }

  public Group getParentGroup() {
    return myParentGroup;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Group anotherGroup) {
    return this.getName().toLowerCase().compareTo(anotherGroup.getName());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.client
   * .Item)
   */
  public void updateItem(Item aSource) {
    if (aSource != null && aSource!=this && aSource instanceof Group) {
      Group theOtherGroup = (Group) aSource;
      myName = theOtherGroup.getName();
      myLabel = theOtherGroup.getLabel();
      myDescription = theOtherGroup.getDescription();
      myParentGroup = theOtherGroup.getParentGroup();
    }
  }

  /**
   * @param aUuid
   */
  public void setParentGroup(Group aParent) {
    myParentGroup = aParent;
  }

  public static String buildGroupPath(Group aGroup) {
    if (aGroup == null) {
      return "";
    }
    String theGroupPath = aGroup.getName();
    theGroupPath = PATH_SEPARATOR + theGroupPath;
    Group theParentGroup = aGroup.getParentGroup();
    while (theParentGroup != null) {
      theGroupPath = PATH_SEPARATOR + theParentGroup.getName() + theGroupPath;
      theParentGroup = theParentGroup.getParentGroup();
    }

    return theGroupPath;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object anObject) {
    if (anObject instanceof Group) {
      return myUuid.equals(((Group) anObject).getUUID());
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
    return myUuid.hashCode();
  }
}
