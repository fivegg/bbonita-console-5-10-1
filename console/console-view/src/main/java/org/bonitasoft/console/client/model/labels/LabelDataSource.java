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
package org.bonitasoft.console.client.model.labels;

import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.labels.LabelFilter;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface LabelDataSource extends BonitaFilteredDataSource<LabelUUID, LabelModel, LabelFilter> {

	static final String LABEL_CASE_ASSOCIATION_PROPERTY = "label case association";
	static final String SYSTEM_LABEL_LIST_PROPERTY = "system label list";
	static final String USER_LABEL_LIST_PROPERTY = "user label list";
	static final String VISIBLE_LABEL_LIST_PROPERTY = "visible labels list";
	static final String TOTAL_CASE_NUMBER_PROPERTY = "total case number";
  static final String CONFIGURATION_PROPERTY = "label datasource configuration";

	/**
	 * List all the labels.
	 * 
	 * @return the list of labels.
	 */
	public List<LabelModel> getAllLabels();

	/**
	 * Get the system labels.
	 * 
	 * @return the list of system labels.
	 */
	public List<LabelModel> getSystemLabels();

	/**
	 * Get the custom labels.
	 * 
	 * @return the list of labels created by the user.
	 */
	public List<LabelModel> getCustomLabels();

	/**
	 * Get the label that has the given name.
	 * 
	 * @param aName
	 * @return the label or null if not found.
	 */
	public LabelModel getLabel(String aName);

	/**
	 * Get the label that has the given UUID.
	 * 
	 * @param aLabelUUID
	 * @return the label or null if not found.
	 */
	public LabelModel getLabel(LabelUUID aLabelUUID);

	/**
	 * Request server to add the given case to the given label.
	 * 
	 * @param aCaseUUID
	 * @param aLabelUUID
	 */
	public void addCaseToLabel(CaseUUID aCaseUUID, LabelUUID aLabelUUID);

	/**
	 * Request server to remove the given case from the given label.
	 * 
	 * @param aCaseUUID
	 * @param aCurrentlyDisplayedLabel
	 */
	public void removeCaseFromLabel(CaseUUID aCaseUUID, LabelUUID aCurrentlyDisplayedLabel);

	/**
	 * @param aLabel
	 * @param aEditableCSSStyle
	 * @param aPreviewCSSStyle
	 * @param aReadOnlyCSSStyle
	 */
	public void updateLabelCSSStyle(LabelModel aLabel, String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle);

	/**
	 * @param aLabelSelection
	 * @param isVisible
	 * @param anHandler
	 */
	public void updateLabelsVisibility(Set<LabelModel> aLabelSelection, boolean isVisible, final AsyncHandler<Void> anHandler);

	/**
	 * Rename the given label.
	 * 
	 * @param aLabel
	 * @param aNewName
	 * @param aHandler
	 */
	public void renameLabel(LabelModel aLabel, String aNewName, AsyncHandler<Void> aHandler);

	/**
	 * 
	 * @param aSetOfLabelUUIDToAdd
	 * @param aSetOfLabelUUIDToRemove
	 * @param aSetOfCaseUUID
	 */
	public void updateLabels(Set<LabelUUID> aSetOfLabelUUIDToAdd, Set<LabelUUID> aSetOfLabelUUIDToRemove, Set<CaseUUID> aSetOfCaseUUID);


	/**
	 * Compute the number of cases in the given label.
	 * @param aLabelUUID
	 * @param searchInHistory
	 */
	void getLabelUpdates(final LabelUUID aLabelUUID, final boolean searchInHistory);

	/**
	 * Get the total number of cases (independent from labels).
	 * @return
	 */
	int getTotalCaseNumbers();
	
	/**
	 * Update the configuration.
	 * @param aConfiguration
	 * @param aHandler
	 */
	void updateConfiguration(final LabelsConfiguration aConfiguration, AsyncHandler<Void> aHandler);

	/**
	 * Get the current configuration.
	 * @param aHandler
	 */
  void getConfiguration(final AsyncHandler<LabelsConfiguration> aHandler);
}
