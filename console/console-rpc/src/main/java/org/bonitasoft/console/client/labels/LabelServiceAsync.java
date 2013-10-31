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

import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface LabelServiceAsync {

	/**
	 * Create a new custom label.
	 * 
	 * @param aLabelName
	 * @param aCallback
	 */
	public void createNewLabel(String aLabelName, AsyncCallback<LabelModel> aCallback);

	/**
	 * Remove a custom label, i.e., a label that was created by the user.
	 * 
	 * @param aLabelUUID
	 * @param aCallback
	 */
	public void removeLabels(List<LabelUUID> aLabelUUIDList, AsyncCallback<Void> aCallback);

	/**
	 * @param aLabelUUID
	 * @param aEditableCSSStyle
	 * @param aPreviewCSSStyle
	 * @param aReadOnlyCSSStyle
	 * @param aCallback
	 */
	public void updateLabelCSSStyle(LabelUUID aLabelUUID, String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle, AsyncCallback<Void> aCallback);

	/**
	 * Rename an existing label for a particular user.
	 * 
	 * @param aLabelUUID
	 * @param aNewName
	 */
	public void renameLabel(LabelUUID aLabelUUID, String aNewName, AsyncCallback<Void> aCallback);

	/**
	 * Update the label definition.
	 * @param aSetOfLabelUUIDToAdd
	 * @param aSetOfLabelUUIDToRemove
	 * @param anSetOfCaseUUID
	 * @param anChainedCallback
	 */
	public void updateLabels(Set<LabelUUID> aSetOfLabelUUIDToAdd, Set<LabelUUID> aSetOfLabelUUIDToRemove, Set<CaseUUID> aSetOfCaseUUID, AsyncCallback<Void> aCallback);

	/**
	 * Get the label updates, i.e. the number of case in the label.
	 * @param aLabelUUID
	 * @param searchInHistory
	 * @param aCallback
	 */
	public void getLabelUpdates(final LabelUUID aLabelUUID, final boolean searchInHistory, final AsyncCallback<LabelUpdates> aCallback);

	/**
	 * Update the label visibility of the given labels.
	 * @param aLabelUUIDSelection
	 * @param isVisible
	 * @param aCallback
	 */
	public void updateLabelsVisibility(Set<LabelUUID> aLabelUUIDSelection, boolean isVisible, AsyncCallback<Void> aCallback);

	/**
	 * List all the labels of the current user.
	 * @param aCallback
	 */
	public void getAllLabels(AsyncCallback<List<LabelModel>> aCallback);

	/**
	 * Update configuration.
	 * @param aConfiguration
	 * @throws ConsoleException
	 * @throws SessionTimeOutException
	 */
	public void updateConfiguration(final LabelsConfiguration aConfiguration,AsyncCallback<Void> aCallback);
  
  /**
   * Get the configuration.
   * @return
   * @throws ConsoleException
   * @throws SessionTimeOutException
   */
  public void getConfiguration(AsyncCallback<LabelsConfiguration> aCallback);
}
