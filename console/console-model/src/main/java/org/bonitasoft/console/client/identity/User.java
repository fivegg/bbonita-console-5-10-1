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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.users.UserUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class User implements Serializable, Item, Comparable<User> {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -9194213606037225215L;

	public static transient final String WEBSITE_KEY = "website";
	public static transient final String COUNTRY_KEY = "country";
	public static transient final String STATE_KEY = "state";
	public static transient final String CITY_KEY = "city";
	public static transient final String ZIPCODE_KEY = "zip";
	public static transient final String ADDRESS_KEY = "address";
	public static transient final String ROOM_KEY = "room";
	public static transient final String BUILDING_KEY = "building";
	public static transient final String FAX_NUMBER_KEY = "fax";
	public static transient final String MOBILE_NUMBER_KEY = "mobile";
	public static transient final String PHONE_NUMBER_KEY = "phone";
	public static transient final String EMAIL_KEY = "email";
	
	
	private UserUUID uuid;
	private String firstName;
	private String lastName;
	private String password;
	private String username;
	
	private String myTitle;
  private String myJobTitle;
  private UserUUID myManagerUuid;
  
	private Map<String, String> metadata;
	private Set<MembershipItem> memberships;
	
  
  
  private Map<String, String> myPersonnalContactInfo;
  private Map<String, String> myProfessionalContactInfo;

  private UserUUID myDelegateUuid;

	/**
	 * Default constructor
	 */
	public User() {
		super();
		// Mandatory for serialization.
	}

	/**
	 * 
	 * Default constructor.
	 * 
	 * @param anId
	 * @param username
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param aTitle 
	 * @param aJobTitle 
	 * @param managerUuid 
	 * @param email
	 */
	public User(String anId, String username, String password, String firstName, String lastName, String aTitle, String aJobTitle, String managerUuid, String delegateUuid) {
		this.uuid = new UserUUID(anId);
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.username = username;
		this.myTitle = aTitle;
		this.myJobTitle = aJobTitle;
		if(managerUuid!=null) {
		  this.myManagerUuid = new UserUUID(managerUuid);
		} else {
		  this.myManagerUuid = null;
		}
		if(delegateUuid!=null) {
		  myDelegateUuid = new UserUUID(delegateUuid);
		} else {
		  myDelegateUuid = null;
		}
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Map<String, String> getMetadatas() {
		if (metadata == null) {
			return new HashMap<String,String>();
		} else {
			return metadata;
		}
	}

	public void setMetadata(Map<String, String> metadata) {
	  if(metadata != null) {
	    this.metadata = new HashMap<String, String>(metadata);
	  } else {
	    this.metadata = null;
	  }
	}
	
	public Set<MembershipItem> getMembership() {
    if (memberships == null) {
      return new HashSet<MembershipItem>();
    } else {
      return memberships;
    }
  }

  public void setMembership(Set<MembershipItem> memberships) {
    if(memberships != null) {
      this.memberships = new HashSet<MembershipItem>(memberships);
    } else {
      this.memberships = null;
    }
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(User anotherUser) {
		return this.getUsername().toLowerCase().compareTo(anotherUser.getUsername());
	}


  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.Item#getUUID()
   */
  public UserUUID getUUID() {
    return uuid;
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.client.Item)
   */
  public void updateItem(Item aSource) {
    if(aSource != null && aSource!=this && aSource instanceof User){
      User theOtherUser = (User) aSource;
      setDelegateUuid(theOtherUser.getDelegateUuid());
      setFirstName(theOtherUser.getFirstName());
      setJobTitle(theOtherUser.getJobTitle());
      setLastName(theOtherUser.getLastName());
      setManagerUuid(theOtherUser.getManagerUuid());
      setMembership(theOtherUser.getMembership());
      setMetadata(theOtherUser.getMetadatas());
      setPassword(getPassword());
      setPersonnalContactInfo(theOtherUser.getPersonalContactInfo());
      setProfessionalContactInfo(theOtherUser.getProfessionalContactInfo());
      setTitle(theOtherUser.getTitle());
      setUsername(theOtherUser.getUsername());
      
    }
  }

  /**
   * @return
   */
  public Map<String, String> getProfessionalContactInfo() {
    return myProfessionalContactInfo;
  }

  /**
   * @return
   */
  public Map<String, String> getPersonalContactInfo() {
    return myPersonnalContactInfo;
  }

  /**
   * @return
   */
  public UserUUID getDelegateUuid() {
    return myDelegateUuid;
  }
  
  public void setDelegateUuid(UserUUID aDelegate) {
    myDelegateUuid = aDelegate;
  }

  /**
   * @return
   */
  public UserUUID getManagerUuid() {
    return myManagerUuid;
  }

  /**
   * @return
   */
  public String getJobTitle() {
    return myJobTitle;
  }
  
  public void setJobTitle(String aJobTitle) {
    myJobTitle = aJobTitle;
  }

  /**
   * @return
   */
  public String getTitle() {
    return myTitle;
  }
  
  public void setTitle(String aTitle) {
    myTitle = aTitle;
  }

  /**
   * @param personnalContactInfo the personnalContactInfo to set
   */
  public void setPersonnalContactInfo(Map<String, String> personnalContactInfo) {
    this.myPersonnalContactInfo = personnalContactInfo;
  }

  /**
   * @return the personnalContactInfo
   */
  public Map<String, String> getPersonnalContactInfo() {
    return myPersonnalContactInfo;
  }

  /**
   * @param professionalContactInfo the professionalContactInfo to set
   */
  public void setProfessionalContactInfo(Map<String, String> professionalContactInfo) {
    this.myProfessionalContactInfo = professionalContactInfo;
  }

  public void setManagerUuid(UserUUID aManager) {
    myManagerUuid = aManager;
  }
  
}
