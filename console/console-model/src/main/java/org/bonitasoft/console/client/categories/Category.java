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
package org.bonitasoft.console.client.categories;

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;

/**
 * This class defines the concept of 'Label'. A Label is a human readable text
 * value that can be associated to a 'Case'. This joins the concept of 'key
 * words' that can be associated to documents to help the indexing.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class Category implements Item, Comparable<Category> {

  private static final long serialVersionUID = -7295107137117278827L;

  private static final String EMPTY_CSS_STYLE = ""; //$NON-NLS-1$
  
  public transient static final String DEFAULT_CSS_STYLE = "label_default_readonly";
  public transient static final String DEFAULT_PREVIEW_CSS = "label_default_preview";
  
  protected CategoryUUID myUUID;
  protected String myName;

  /**
   * @return the name
   */
  public String getName() {
    return myName;
  }

  protected String myCSSStyleName;
  protected String myPreviewCSSStyleName;
  protected String myIconCSSStyle;

  protected transient ModelChangeSupport changes = new ModelChangeSupport(this);

  protected int myNbOfCases;

  /**
   * The NAME_PROPERTY defines the property used for eventing changes on the
   * label's name.
   */
  public transient static final String NAME_PROPERTY = "categoryName";
  public transient static final String CSS_CLASS_NAME_PROPERTY = "category css style";

  public transient static final String CASES_PROPERTY = "category case list";

  /**
   * Default constructor.
   */
  public Category() {
    super();
    // Mandatory for serialization.
  }

  /**
   * Default constructor.
   * 
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
  public Category(CategoryUUID aUUID, String aName) {
    super();
    myUUID = aUUID;
    myName = aName;
  }

  /**
   * @return the name
   */
  public CategoryUUID getUUID() {
    return myUUID;
  }

  /**
   * Set the name of the label.
   * 
   * @param aName
   * @param name
   *          the name to set
   */
  public void setName(String aName) {
    String theOldValue = myName;
    myName = aName;
    changes.fireModelChange(NAME_PROPERTY, theOldValue, myName);
  }

  /**
   * @return the CSS StyleName
   */
  public String getCSSStyleName() {
    return myCSSStyleName;
  }

  /**
   * Set the CSS style name for the label when it is editable.
   * 
   * @param anEditableCSSStyleName
   */
  public void setCSSStyleName(String aCSSStyleName) {
    String theOldValue = this.myCSSStyleName;
    this.myCSSStyleName = aCSSStyleName;

    changes.fireModelChange(CSS_CLASS_NAME_PROPERTY, theOldValue, myCSSStyleName);
  }
  
  /**
   * @return the CSS StyleName
   */
  public String getPreviewCSSStyleName() {
    return myPreviewCSSStyleName;
  }

  /**
   * Set the CSS style name for the label when it is editable.
   * 
   * @param anEditableCSSStyleName
   */
  public void setPreviewCSSStyleName(String aCSSStyleName) {
    String theOldValue = this.myPreviewCSSStyleName;
    this.myPreviewCSSStyleName = aCSSStyleName;

    changes.fireModelChange(CSS_CLASS_NAME_PROPERTY, theOldValue, myPreviewCSSStyleName);
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
   * Get the css class name that refers to the icon associated to the label. If
   * no icon is associated to the label an empty string is returned.
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
   * @param aResult
   */
  public void setNbOfCases(int aNbOfCases) {
    int theOldValue = myNbOfCases;
    myNbOfCases = aNbOfCases;
    changes.fireModelChange(CASES_PROPERTY, theOldValue, myNbOfCases);
  }

  public int getNbOfCases() {
    return myNbOfCases;
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
    if (anObject instanceof Category) {
      return myUUID.equals(((Category) anObject).getUUID());
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
  public int compareTo(Category o) {
    if (this == o) {
      return 0;
    } else {
      return myName.compareTo(o.getName());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Category: " + myName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.client
   * .Item)
   */
  public void updateItem(Item aSource) {
    if (aSource != null && aSource!=this && aSource instanceof Category) {
      Category theOtherCategory = (Category) aSource;
      setPreviewCSSStyleName(theOtherCategory.getPreviewCSSStyleName());
      setCSSStyleName(theOtherCategory.getCSSStyleName());
      setIconCSSStyle(theOtherCategory.getIconCSSStyle());
      setName(theOtherCategory.getName());
      setNbOfCases(theOtherCategory.getNbOfCases());
    }
  }

}
