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
package org.bonitasoft.console.client.processes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class BonitaProcess implements Serializable, Item,
		Comparable<BonitaProcess> {

	/**
	 * Process state.<br>
	 */
	public static enum BonitaProcessState {
		ENABLED, DISABLED, ARCHIVED
	}

	/**
	 * The serial version UUID
	 */
	private static final long serialVersionUID = -4336355768119131419L;

	public static final String CUSTOM_DESCRIPTION_PATTERN = "process custom cases description";

	public static final String CATEGORIES_PROPERTY = "process categories";

	public static final String STATE_PROPERTY = "process state";

	private BonitaProcessUUID myUUID;
	private String myName;
	private String myDisplayName;
	private String myProcessDescription;
	private BonitaProcessState myState;
	private String myVersion;

	private boolean isVisible;

	private String myUrl;

	private String myCustomDescriptionDefinition;

	private transient ModelChangeSupport myChanges = new ModelChangeSupport(
			this);

	private String myDesignURL;

	private List<String> myCategoriesName;

	private String myDeployedBy;

	private Date myDeployedDate;

	/**
	 * Default constructor.
	 */
	public BonitaProcess() {
		super();
		// Mandatory for serialization.
		myCategoriesName = new ArrayList<String>();
	}

	/**
	 * 
	 * Default constructor.
	 * 
	 * @param aProcessDefinition
	 * @param aProcessDescription
	 * @param isVisible
	 */
	public BonitaProcess(String aUUID, String aName, String aDisplayName,
			String aProcessDescription, String aState, String aVersion,
			boolean isVisible) {
		super();
		myName = aName;
		myDisplayName = aDisplayName;
		myProcessDescription = aProcessDescription;
		myState = BonitaProcessState.valueOf(aState);
		myVersion = aVersion;
		this.isVisible = isVisible;
		this.myUUID = new BonitaProcessUUID(aUUID, myDisplayName);
		this.myUrl = "";
		myCategoriesName = new ArrayList<String>();
	}

	/**
	 * Get the value of the ID field.
	 * 
	 * @return the process ID
	 */
	public BonitaProcessUUID getUUID() {
		return myUUID;
	}

	/**
	 * Get the value of the processDescription field.
	 * 
	 * @return the processDescription
	 */
	public String getProcessDescription() {
		return this.myProcessDescription;
	}

	/**
	 * Get the visibility of the process definition. The visibility is a piece
	 * of configuration that the user can change.
	 * 
	 * @return the visibility
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @param anIsVisible
	 *            the isVisible to set
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Get the value of the processName field.
	 * 
	 * @return the process name
	 */
	public String getName() {
		return myName;
	}

	/**
	 * Get the value of the processLabel field.
	 * 
	 * @return the process label
	 */
	public String getDisplayName() {
		return myDisplayName;
	}

	/**
	 * Get the state of the process.<br>
	 * Returned value belongs to ProcessState.
	 * 
	 * @return the state of the process
	 */
	public BonitaProcessState getState() {
		return myState;
	}

	/**
	 * Set the state of the process.
	 * 
	 * @param aNewState
	 */
	public void setState(BonitaProcessState aNewState) {
		BonitaProcessState theOldValue = myState;
		myState = aNewState;
		myChanges.fireModelChange(STATE_PROPERTY, theOldValue, myState);
	}

	/**
	 * Get the version of the process.
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return myVersion;
	}

	@Override
	public boolean equals(Object anObj) {
		if (this == anObj) {
			return true;
		}
		if (anObj instanceof BonitaProcess) {
			BonitaProcess anotherProcess = (BonitaProcess) anObj;
			return myUUID.equals(anotherProcess.getUUID());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(BonitaProcess anotherBonitaProcess) {
		return getDisplayName()
				.compareTo(anotherBonitaProcess.getDisplayName());
	}

	public void setApplicationURL(String anUrl) {
		myUrl = anUrl;
	}

	/**
	 * @return the url
	 */
	public String getApplicationUrl() {
		return myUrl;
	}

	public void setCustomDescriptionDefinition(
			String aCustomDescriptionDefinition) {
		String theOldValue = myCustomDescriptionDefinition;
		myCustomDescriptionDefinition = aCustomDescriptionDefinition;
		// Notify changes.
		myChanges.fireModelChange(CUSTOM_DESCRIPTION_PATTERN, theOldValue,
				aCustomDescriptionDefinition);
	}

	/**
	 * Get the pattern to use as the case description.
	 */
	public String getCustomDescriptionDefinition() {
		return myCustomDescriptionDefinition;

	}

	/**
	 * Add a property change listener.
	 * 
	 * @param aPropertyName
	 * @param aListener
	 */
	public void addModelChangeListener(String aPropertyName,
			ModelChangeListener aListener) {
		// Avoid duplicate subscription.
		myChanges.removeModelChangeListener(aPropertyName, aListener);
		myChanges.addModelChangeListener(aPropertyName, aListener);

	}

	/**
	 * Remove a property change listener.
	 * 
	 * @param aPropertyName
	 * @param aListener
	 */
	public void removeModelChangeListener(String aPropertyName,
			ModelChangeListener aListener) {
		myChanges.removeModelChangeListener(aPropertyName, aListener);

	}

	public String getDesignURL() {
		return myDesignURL;
	}

	public void setDesignURL(String aUrl) {
		myDesignURL = aUrl;
	}

	public List<String> getCategoriesName() {
		return Collections.unmodifiableList(myCategoriesName);
	}

	public void setCategoriesName(Collection<String> aSetOfCategoryNames) {
		List<String> theOldValue = new ArrayList<String>(myCategoriesName);
		myCategoriesName.clear();
		if (aSetOfCategoryNames != null) {
			myCategoriesName.addAll(aSetOfCategoryNames);
		}
		myChanges.fireModelChange(CATEGORIES_PROPERTY, theOldValue,
				getCategoriesName());
	}

	public String getDeployedBy() {
		return myDeployedBy;
	}

	public void setDeployedBy(String deployedBy) {
		myDeployedBy = deployedBy;
	}
	
	 public Date getDeployedDate() {
			return myDeployedDate;
	  }
	  
	  public void setDeployedDate(Date deployedDate) {
			myDeployedDate = deployedDate;
	  }

	public void updateItem(Item aSource) {
		if (aSource != null && aSource != this
				&& aSource instanceof BonitaProcess) {
			BonitaProcess theOtherProcess = (BonitaProcess) aSource;
			setApplicationURL(theOtherProcess.getApplicationUrl());
			setCustomDescriptionDefinition(theOtherProcess
					.getCustomDescriptionDefinition());
			setDesignURL(theOtherProcess.getDesignURL());
			setState(theOtherProcess.getState());
		    setVisible(theOtherProcess.isVisible());
			setCategoriesName(theOtherProcess.getCategoriesName());
			setDeployedBy(theOtherProcess.getDeployedBy());
			setDeployedDate(theOtherProcess.getDeployedDate());
		}

	}

}
