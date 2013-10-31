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
package org.bonitasoft.console.client.reporting;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.users.UserUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ReportItem implements Item, Serializable, Comparable<ReportItem> {

    private static final long serialVersionUID = 3114907201449660274L;

    protected ReportUUID myUUID;
    protected Date myDeploymentDate;
    protected UserUUID myDeployedByUser;
    protected String myFileName;
    protected String myDescription;
    protected ReportType myType;
    protected ReportScope myScope;

    private boolean isCustom;

    private Set<String> myConfigurationElements = new HashSet<String>();

    protected ReportItem() {
        // Mandatory for serialization
        super();
    }

    public ReportItem(String anId, String aFileName, String aDescription, ReportType aType, ReportScope aScope, boolean custom) {
        super();
        this.myUUID = new ReportUUID(anId);
        this.myFileName = aFileName;
        this.myDescription = aDescription;
        this.myType = aType;
        this.myScope = aScope;
        this.isCustom = custom;
    }

    /**
     * @return the uUID
     */
    public ReportUUID getUUID() {
        return myUUID;
    }

    /**
     * @param aUuid
     *            the uUID to set
     */
    public void setUUID(ReportUUID aUuid) {
        myUUID = aUuid;
    }

    /**
     * @return the filename
     */
    public String getFileName() {
        return myFileName;
    }

    /**
     * @param aName
     *            the name to set
     */
    public void setName(String aName) {
        myFileName = aName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return myDescription;
    }

    /**
     * @param aDescription
     *            the description to set
     */
    public void setDescription(String aDescription) {
        myDescription = aDescription;
    }

    /**
     * @return the type
     */
    public ReportType getType() {
        return myType;
    }

    /**
     * @param aType
     *            the type to set
     */
    public void setType(ReportType aType) {
        myType = aType;
    }

    /**
     * @param aFileName
     */
    public void setFileName(String aFileName) {
        myFileName = aFileName;

    }

    public ReportScope getScope() {
        return myScope;
    }

    public void setScope(ReportScope aScope) {
        myScope = aScope;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public boolean requiresConfiguration() {
        return !myConfigurationElements.isEmpty();
    }
    
    public void addElementToConfiguration(String anElement) {
        myConfigurationElements.add(anElement);
    }
    
    public void removeElementFromConfiguration(String anElement) {
        myConfigurationElements.remove(anElement);
    }
    
    public Set<String> getConfigurationElements() {
        final HashSet<String> theResult = new HashSet<String>();
        theResult.addAll(myConfigurationElements);
        return theResult;
    }
    
    public void setConfigurationElements(Set<String> theNewConfiguration) {
        myConfigurationElements.clear();
        if(theNewConfiguration!=null && !theNewConfiguration.isEmpty()) {
            myConfigurationElements.addAll(theNewConfiguration);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(ReportItem anotherItem) {
        return getUUID().getValue().compareTo(anotherItem.getUUID().getValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.Item#updateItem(org.bonitasoft.console.
     * client .Item)
     */
    public void updateItem(Item aSource) {
        if (aSource != null && aSource != this && aSource instanceof ReportItem) {
            ReportItem theOtherItem = (ReportItem) aSource;
            setDescription(theOtherItem.getDescription());
            setType(theOtherItem.getType());
            setFileName(theOtherItem.getFileName());
            setScope(theOtherItem.getScope());
            setConfigurationElements(theOtherItem.getConfigurationElements());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getUUID() + ":" + getFileName() + " - " + getScope();
    }

}
