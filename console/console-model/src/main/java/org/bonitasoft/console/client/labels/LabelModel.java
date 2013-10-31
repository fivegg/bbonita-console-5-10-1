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
package org.bonitasoft.console.client.labels;

import java.io.Serializable;

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.users.UserUUID;

/**
 * This class defines the concept of 'Label'. A Label is a human readable text
 * value that can be associated to a 'Case'. This joins the concept of 'key
 * words' that can be associated to documents to help the indexing.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class LabelModel implements Serializable, Item,  Comparable<LabelModel> {

	/**
	 * The EMPTY_CSS_STYLE defines
	 */
	private static final String EMPTY_CSS_STYLE = ""; //$NON-NLS-1$

	public static final LabelModel INBOX_LABEL = new LabelModel(new LabelUUID("Inbox", null), "label_default_editable", "label_default_readonly", "label_default_preview", true, true, true, 0,false,null);
	public static final LabelModel STAR_LABEL = new LabelModel(new LabelUUID("Starred", null),null,null,null, true, true, false, 1,true,"yellow_star");
	public static final LabelModel MY_CASES_LABEL = new LabelModel(new LabelUUID("My Cases", null),null,null,null, true, true, false, 2,false,null);
	public static final LabelModel ALL_LABEL = new LabelModel(new LabelUUID("All", null),null,null,null, false, true, false, 3,false,null);
	public static final LabelModel MY_TEAM_LABEL = new LabelModel(new LabelUUID("My Team", null),null,null,null, true, true, false, 4,false,null);
	public static final LabelModel DELEGEE_LABEL = new LabelModel(new LabelUUID("Delegee", null),null,null,null, true, true, false, 5,false,null);
	
	public static final LabelModel ADMIN_ALL_CASES = new LabelModel(new LabelUUID("&BONITA&", null),null,null,null, false, true, false, -1,false,null);
	
//	public static final LabelModel ONTRACK_LABEL = new LabelModel(new LabelUUID("On track", null),null,null,null, true, true, false, 4,false,null);
	public static final LabelModel ATRISK_LABEL = new LabelModel(new LabelUUID("At risk", null),null,null,null, true, true, false, 5,false,null);
	public static final LabelModel OVERDUE_LABEL = new LabelModel(new LabelUUID("Overdue", null),null,null,null, true, true, false, 6,false,null);
	
//	public static final LabelModel URGENT_LABEL = new LabelModel(new LabelUUID("Urgent", null),null,null,null, true, true, false, 7,false,null);
//	public static final LabelModel HIGH_LABEL = new LabelModel(new LabelUUID("High", null),null,null,null, true, true, false, 8,false,null);
//	public static final LabelModel NORMAL_LABEL = new LabelModel(new LabelUUID("Normal", null),null,null,null, true, true, false, 9,false,null);
	
	/**
	 * The serialVersionUID defines
	 */
	private static final long serialVersionUID = -8048564307817271887L;

	protected LabelUUID myUUID;
	protected String myEditableCSSStyleName;
	protected String myReadonlyCSSStyleName;
	protected String myPreviewCSSStyleName;
	protected boolean isVisible;
	protected boolean hasToBeDisplayed;
	protected boolean isAssignableByUser;
	protected String myIconCSSStyle;

	protected int myDisplayOrder;
	protected int myNbOfCases;

	protected transient ModelChangeSupport changes = new ModelChangeSupport(this);

	protected boolean isSystemLabel;

	/**
	 * The NAME_PROPERTY defines the property used for eventing changes on the
	 * label's name.
	 */
	public transient static final String NAME_PROPERTY = "name";
	/**
	 * The VISIBILITY_PROPERTY defines the property used for eventing changes on
	 * the label's visibility.
	 */
	public transient static final String VISIBILITY_PROPERTY = "visibility";

	public transient static final String CASES_PROPERTY = "label cases number";

	public transient static final String DEFAULT_EDITABLE_CSS = "label_default_editable";
	public transient static final String DEFAULT_READONLY_CSS = "label_default_readonly";
	public transient static final String DEFAULT_PREVIEW_CSS = "label_default_preview";
	public transient static final String EDITABLE_CSS_CLASS_NAME_PROPERTY = "editable_css_class";
	public transient static final String READONLY_CSS_CLASS_NAME_PROPERTY = "readonly_css_class";
	public transient static final String PREVIEW_CSS_CLASS_NAME_PROPERTY = "preview_css_class";

	

	/**
	 * Default constructor.
	 */
	public LabelModel() {
		super();
		// Mandatory for serialization.
	}


	
	/**
	 * Default constructor.
	 * @param aUUID
	 * @param aEditableCSSStyleName
	 * @param aReadonlyCSSStyleName
	 * @param aPreviewCSSStyleName
	 * @param aIsVisible
	 * @param aHasToBeDisplayed
	 * @param aIsAssignableByUser
	 * @param aIconCSSStyle
	 * @param aDisplayOrder
	 * @param aIsSystemLabel
	 */
	public LabelModel(LabelUUID aUUID, String anEditableCSSStyleName, String aReadonlyCSSStyleName, String aPreviewCSSStyleName, boolean isVisible, boolean isSystemLabel, boolean hasToBeDisplayed,
			 int aDisplayOrder, boolean isAssignableByUser, String anIconCSSStyle) {
		super();
		myUUID = aUUID;
		myEditableCSSStyleName = anEditableCSSStyleName;
		myReadonlyCSSStyleName = aReadonlyCSSStyleName;
		myPreviewCSSStyleName = aPreviewCSSStyleName;
		this.isVisible = isVisible;
		this.hasToBeDisplayed = hasToBeDisplayed;
		this.isAssignableByUser = isAssignableByUser;
		myIconCSSStyle = anIconCSSStyle;
		myDisplayOrder = aDisplayOrder;
		this.isSystemLabel = isSystemLabel;
	}



	/**
	 * Default constructor.
	 * 
	 * @param aUUID
	 * @param aAnEditableCSSStyleName
	 * @param aReadonlyCSSStyleName
	 * @param aPreviewCSSStyleName
	 * @param isVisible
	 */
	public LabelModel(LabelUUID aUUID, String anEditableCSSStyleName, String aReadonlyCSSStyleName, String aPreviewCSSStyleName, boolean isVisible) {
		super();
		this.myUUID = aUUID;
		this.isVisible = isVisible;
		this.myEditableCSSStyleName = anEditableCSSStyleName;
		this.myReadonlyCSSStyleName = aReadonlyCSSStyleName;
		this.myPreviewCSSStyleName = aPreviewCSSStyleName;
		this.hasToBeDisplayed = true;
		this.isSystemLabel = false;
		this.isAssignableByUser = true;
	}




	/**
	 * Default constructor.
	 * 
	 * @param aLabel
	 * @param aUserUUID
	 */
	public LabelModel(LabelModel aLabel, UserUUID aUserUUID) {
		super();
		this.myUUID = new LabelUUID(aLabel.getUUID().getValue(), aUserUUID);
		this.isVisible = aLabel.isVisible;
		this.myEditableCSSStyleName = aLabel.getEditableCSSStyleName();
		this.myReadonlyCSSStyleName = aLabel.getReadonlyCSSStyleName();
		this.myPreviewCSSStyleName = aLabel.getPreviewCSSStyleName();
		this.hasToBeDisplayed = aLabel.hasToBeDisplayed;
		this.isSystemLabel = aLabel.isSystemLabel;
		this.myIconCSSStyle = aLabel.myIconCSSStyle;
		this.myDisplayOrder = aLabel.getDisplayOrder();
		this.isAssignableByUser = aLabel.isAssignableByUser;
		this.myNbOfCases = aLabel.getNbOfCases();
	}

	/**
	 * @return the name
	 */
	public LabelUUID getUUID() {
		return myUUID;
	}

	/**
	 * Set the name of the label.
	 * 
	 * @param aName
	 * @param name
	 *            the name to set
	 */
	public void setName(String aName) {
		LabelUUID theOldValue = new LabelUUID(this.myUUID.getValue(), this.myUUID.getOwner());
		this.myUUID.setValue(aName);
		changes.fireModelChange(NAME_PROPERTY, theOldValue, new LabelUUID(this.myUUID.getValue(), this.myUUID.getOwner()));
	}

	/**
	 * @return the myEditableCSSStyleName
	 */
	public String getEditableCSSStyleName() {
		return myEditableCSSStyleName;
	}

	/**
	 * Set the CSS style name for the label when it is editable.
	 * 
	 * @param anEditableCSSStyleName
	 */
	public void setEditableCSSStyleName(String anEditableCSSStyleName) {
		String theOldValue = this.myEditableCSSStyleName;
		this.myEditableCSSStyleName = anEditableCSSStyleName;

		changes.fireModelChange(EDITABLE_CSS_CLASS_NAME_PROPERTY, theOldValue, myEditableCSSStyleName);
	}

	/**
	 * @return the myReadonlyCSSStyleName
	 */
	public String getReadonlyCSSStyleName() {
		return myReadonlyCSSStyleName;
	}

	/**
	 * @return the myPreviewCSSStyleName
	 */
	public String getPreviewCSSStyleName() {
		return myPreviewCSSStyleName;
	}

	/**
	 * @param aReadonlyCSSStyleName
	 *            the myReadonlyCSSStyleName to set
	 */
	public void setReadonlyCSSStyleName(String aReadonlyCSSStyleName) {
		String theOldValue = this.myReadonlyCSSStyleName;
		this.myReadonlyCSSStyleName = aReadonlyCSSStyleName;
		changes.fireModelChange(READONLY_CSS_CLASS_NAME_PROPERTY, theOldValue, myReadonlyCSSStyleName);
	}

	/**
	 * @param aPreviewCSSStyleName
	 *            the myPreviewCSSStyleName to set
	 */
	public void setPreviewCSSStyleName(String aPreviewCSSStyleName) {
		String theOldValue = this.myPreviewCSSStyleName;
		this.myPreviewCSSStyleName = aPreviewCSSStyleName;
		changes.fireModelChange(PREVIEW_CSS_CLASS_NAME_PROPERTY, theOldValue, myPreviewCSSStyleName);
	}

	/**
	 * Indicates whether the Label is visible or not.
	 * 
	 * @return the visibility.
	 */
	public boolean isVisible() {
		return this.isVisible;
	}

	/**
	 * Add a property change listener.
	 * 
	 * @param aPropertyName
	 * @param aListener
	 */
	public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
		// Avoid duplicate subscription.
		changes.removeModelChangeListener(aPropertyName, aListener);
		changes.addModelChangeListener(aPropertyName, aListener);
	}

	/**
	 * Remove a property change listener.
	 * 
	 * @param aPropertyName
	 * @param aListener
	 */
	public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
		changes.removeModelChangeListener(aPropertyName, aListener);
	}

	/**
	 * Change the visibility of the label.
	 * 
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible) {
		boolean theOldValue = this.isVisible;
		this.isVisible = isVisible;
		changes.fireModelChange(VISIBILITY_PROPERTY, theOldValue, isVisible);
	}

	/**
	 * Get the css class name that refers to the icon associated to the label.
	 * If no icon is associated to the label an empty string is returned.
	 * 
	 * @return the css class name
	 */
	public String getIconCSSStyle() {
		return myIconCSSStyle;
	}

	/**
	 * Set the css class name that refers to the icon associated to the label.
	 * 
	 * @param aCSSStyle
	 * 
	 */
	public void setIconCSSStyle(String aCSSStyle) {
		if (aCSSStyle != null) {
			myIconCSSStyle = aCSSStyle;
		} else {
			myIconCSSStyle = EMPTY_CSS_STYLE;
		}
	}

	/**
	 * Should the label be displayed, or it is an hidden label.
	 * 
	 * @return true
	 */
	public boolean hasToBeDisplayed() {
		return this.hasToBeDisplayed;
	}

	/**
	 * 
	 * @return true if the label is a system label.
	 */
	public boolean isSystemLabel() {
		return this.isSystemLabel;
	}

	/**
	 * @return the isAssignableByUser
	 */
	public boolean isAssignableByUser() {
		return isAssignableByUser;
	}

	/**
	 * @param isAssignableByUser
	 */
	public void setAssignableByUser(boolean isAssignableByUser) {
		this.isAssignableByUser = isAssignableByUser;
	}

	/**
	 * @return the display order of the label.
	 */
	public int getDisplayOrder() {
		return myDisplayOrder;
	}

	/**
	 * @param aDisplayOrder
	 */
	public void setDisplayOrder(int aDisplayOrder) {
		this.myDisplayOrder = aDisplayOrder;
	}

	/**
	 * @return the nbOfCases
	 */
	public int getNbOfCases() {
		return myNbOfCases;
	}

	/**
	 * @param anNbOfCases
	 *            the nbOfCases to set
	 */
	public void setNbOfCases(int anNbOfCases) {
		int theOldValue = myNbOfCases;
		myNbOfCases = anNbOfCases;
		changes.fireModelChange(CASES_PROPERTY, theOldValue, myNbOfCases);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof LabelModel) {
			return myUUID.equals(((LabelModel) anObject).getUUID());
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
		return myUUID.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(LabelModel o) {
		if (this == o) {
			return 0;
		} else {
			if (this.isSystemLabel && !o.isSystemLabel) {
				return -1;
			} else if (!this.isSystemLabel && o.isSystemLabel) {
				return 1;
			} else {
				if (this.getDisplayOrder() == o.getDisplayOrder()) {
					return myUUID.getValue().compareTo(o.getUUID().getValue());
				} else if (this.getDisplayOrder() < o.getDisplayOrder()) {
					return -1;
				} else /* if(this.getDisplayOrder() > o.getDisplayOrder()) */{
					return 1;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return myUUID.getValue() + " " + myUUID.getOwner().getValue() + " " + myDisplayOrder;
	}



  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.client.Item)
   */
  public void updateItem(Item aSource) {
    if(aSource!=null && aSource!=this && aSource instanceof LabelModel){
      LabelModel theOtherLabel = (LabelModel) aSource;
      setDisplayOrder(theOtherLabel.getDisplayOrder());
      setEditableCSSStyleName(theOtherLabel.getEditableCSSStyleName());
      setIconCSSStyle(theOtherLabel.getIconCSSStyle());
      setName(theOtherLabel.getUUID().getValue());
      setNbOfCases(theOtherLabel.getNbOfCases());
      setPreviewCSSStyleName(theOtherLabel.getPreviewCSSStyleName());
      setReadonlyCSSStyleName(theOtherLabel.getReadonlyCSSStyleName());
      setVisible(theOtherLabel.isVisible());
    }
    
  }

}
