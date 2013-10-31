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
package org.bonitasoft.console.client.view.cases;

import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseDescriptionWidget extends BonitaPanel implements ModelChangeListener {

	private CaseItem myCase;
	private final Label myOuterPanel = new Label();
	private ProcessDataSource myProcessDataSource;

	/**
	 * 
	 * Default constructor.
	 * 
	 * @param aCase
	 */
	public CaseDescriptionWidget(CaseItem aCase, ProcessDataSource aProcessDataSource) {
		super();
		myCase = aCase;
		myProcessDataSource = aProcessDataSource;
		myCase.addModelChangeListener(CaseItem.STEPS_PROPERTY, this);
		update();
		myOuterPanel.setStylePrimaryName("bos_case_description");
		this.initWidget(myOuterPanel);
	}

	/*
	 * Update the UI.
	 */
	private void update() {

		myProcessDataSource.getItem(myCase.getProcessUUID(), new AsyncHandler<BonitaProcess>() {
			public void handleFailure(Throwable anT) {
				GWT.log("Unable to get the process of my case!", anT);

			}

			public void handleSuccess(BonitaProcess aProcess) {
				if (aProcess != null) {
					// Listen to changes.
					aProcess.addModelChangeListener(BonitaProcess.CUSTOM_DESCRIPTION_PATTERN, CaseDescriptionWidget.this);
					String theCustomDescriptionPattern = aProcess.getCustomDescriptionDefinition();
					if (theCustomDescriptionPattern == null) {
					    myOuterPanel.setText(buildDesciptionFromPattern(ProcessDataSource.DEFAULT_CASEDESCRIPTION_PATTERN, aProcess));
					} else {
						myOuterPanel.setText(buildDesciptionFromPattern(theCustomDescriptionPattern, aProcess));
					}
				}

			}
		});

	}

	/**
	 * This method builds a String description of the case based on a custom expression.<br>
	 * The expression can contain the following keywords separated by the '+' character.<br>
	 * process.name refers to the call to BonitaProcess.getDisplayName()<br>
	 * case.indexNumber refers to the call to CaseItem.getCaseInstanceNumber()<br>
	 * case.initiator refers to the call to CaseItem.getStartedBy()<br>
	 * case.startDate refers to the call to CaseItem.getStartedDate()<br>
	 * case.currentState refers to the call to CaseItem.getState()<br>
	 * if the sequence of characters is not equal to one of the key word, it will be used as a constant and displayed as it is.<br>
	 * 
	 * @param aCustomDescriptionPattern
	 * @return
	 */
	/*
	 * case.numberOfsteps refers to the call to CaseItem.getSteps.size()<br>
	 */
	protected String buildDesciptionFromPattern(final String aCustomDescriptionPattern, final BonitaProcess aProcess) {
		if (null == aCustomDescriptionPattern) {
			return null;
		}
		String theResult = "";
		String[] theElements = aCustomDescriptionPattern.split("\\+");
		for (String theString : theElements) {
			String theCleanString = theString.trim();
			if ("process.name".equalsIgnoreCase(theCleanString)) {
				theResult += aProcess.getDisplayName();
			} else if ("case.indexNumber".equalsIgnoreCase(theCleanString)) {
				theResult += myCase.getCaseInstanceNumber();
			} /*else if ("case.numberOfsteps".equalsIgnoreCase(theCleanString)) {
				theResult += myCase.getSteps().size();
			} */else if ("case.initiator".equalsIgnoreCase(theCleanString)) {
				theResult += myCase.getStartedBy();
			} else if ("case.startDate".equalsIgnoreCase(theCleanString)) {
				theResult += myCase.getStartedDate();
			} else if ("case.currentState".equalsIgnoreCase(theCleanString)) {
				theResult += myCase.getState();
			} else {
				// keep the string as a literal.
				theResult += theString;
			}
		}
		return theResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.ModelChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void modelChange(ModelChangeEvent aEvt) {
		// Event can be:
		// BonitaProcess.CUSTOM_DESCRIPTION_PATTERN
		// CaseItem.STEPS_PROPERTY

		update();
	}

}
