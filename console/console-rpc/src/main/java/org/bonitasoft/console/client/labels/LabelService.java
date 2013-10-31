/**
 * Copyright (C) 2009 BonitaSoft S.A.
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
re: you can redistribute it and/or modify
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

import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface LabelService extends RemoteService {

	/**
	 * Create a new Label.
	 * 
	 * @param aLabelName
	 *            the name of the label.
	 * @return the newly created label.
	 */
	public LabelModel createNewLabel(String aLabelName) throws ConsoleException, SessionTimeOutException;

	/**
	 * Remove a custom label.
	 * 
	 * @param aLabel
	 *            to be removed
	 */
	public void removeLabels(List<LabelUUID> aLabel) throws SessionTimeOutException, ConsoleException;

	/**
	 * @param aLabelUUID
	 * @param aEditableCSSStyle
	 * @param aPreviewCSSStyle
	 * @param aReadOnlyCSSStyle
	 */
	public void updateLabelCSSStyle(LabelUUID aLabelUUID, String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle) throws SessionTimeOutException, ConsoleException;

	public void updateLabelsVisibility(Set<LabelUUID> aLabelUUIDSelection, boolean isVisible) throws SessionTimeOutException, ConsoleException;

	/**
	 * Rename an existing label for a particular user.
	 * 
	 * @param aLabelUUID
	 * @param aNewName
	 * @throws ConsoleException
	 * @throws SessionTimeOutException
	 */
	public void renameLabel(LabelUUID aLabelUUID, String aNewName) throws ConsoleException, SessionTimeOutException;

	/**
	 * 
	 * @param aSetOfLabelUUIDToAdd
	 * @param aSetOfLabelUUIDToRemove
	 * @param anSetOfCaseUUID
	 */
	public void updateLabels(Set<LabelUUID> aSetOfLabelUUIDToAdd, Set<LabelUUID> aSetOfLabelUUIDToRemove, Set<CaseUUID> aSetOfCaseUUID) throws ConsoleException, SessionTimeOutException;

	/**
	 * Get the updated data of the given label.
	 * 
	 * @return
	 * @throws ConsoleException
	 * @throws SessionTimeOutException
	 */
	public LabelUpdates getLabelUpdates(final LabelUUID aLabelUUID, final boolean searchInHistory) throws ConsoleException, SessionTimeOutException;
	
	/**
	 * List the labels of a user.
	 * @return
	 * @throws ConsoleException
	 * @throws SessionTimeOutException
	 */
	public List<LabelModel> getAllLabels() throws ConsoleException, SessionTimeOutException;
	
	/**
	 * Update the configuration.
	 * @param aConfiguration
	 * @throws ConsoleException
	 * @throws SessionTimeOutException
	 */
	 public void updateConfiguration(final LabelsConfiguration aConfiguration) throws ConsoleException, SessionTimeOutException;
	 
	 /**
	  * Get the configuration.
	  * @return
	  * @throws ConsoleException
	  * @throws SessionTimeOutException
	  */
	 public LabelsConfiguration getConfiguration() throws ConsoleException, SessionTimeOutException;
}
