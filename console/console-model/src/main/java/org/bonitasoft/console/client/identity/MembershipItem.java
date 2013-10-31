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

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.Item;

/**
 * @author Nicolas Chabanoles
 *
 */
public class MembershipItem implements Item {

  private static final long serialVersionUID = 3681412765311909705L;
  
  BonitaUUID myUUID;
  private Group myGroup;
  private Role myRole;
  
  /**
   * Default constructor.
   */
  public MembershipItem() {
  
  }

  public MembershipItem(BonitaUUID aMembershipUUID, Group aGroup, Role aRole) {
    myUUID = aMembershipUUID;
    myGroup = aGroup;
    myRole = aRole;
  }

  public BonitaUUID getUUID() {
    return myUUID;
  }


  public void updateItem(Item aSource) {
    if(aSource instanceof MembershipItem) {
      setGroup(((MembershipItem) aSource).getGroup());
      setRole(((MembershipItem) aSource).getRole());
    }
    
  }


  /**
   * @param group the group to set
   */
  public void setGroup(Group group) {
    myGroup = group;
  }


  /**
   * @return the group
   */
  public Group getGroup() {
    return myGroup;
  }


  /**
   * @param role the role to set
   */
  public void setRole(Role role) {
    myRole = role;
  }


  /**
   * @return the role
   */
  public Role getRole() {
    return myRole;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object aObj) {
    if(this == aObj) {
      return true;
    }
    if(aObj instanceof MembershipItem) {
      MembershipItem theOtherMembershipItem = (MembershipItem) aObj;
      if(myUUID == null || theOtherMembershipItem.getUUID() == null) {
        return (myGroup.equals(theOtherMembershipItem.getGroup()) && myRole.equals(theOtherMembershipItem.getRole()));
      } else {
        return myUUID.equals(theOtherMembershipItem.getUUID());
      }
    } else {
      return false;
    }
    
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    if(myUUID!=null) {
      myUUID.hashCode();
    } else {
      return (myGroup.getName() + myRole.getName()).hashCode();
    }
    return super.hashCode();
  }
}
