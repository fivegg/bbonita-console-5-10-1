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
package org.bonitasoft.console.client.view.steps;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * A basic widget that translates the state of a step into a graphical flag. The graphical flag is managed by a css style. This will ease the customization.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class StepCandidatesWidget extends BonitaPanel implements ModelChangeListener {

	/*
	 * The model.
	 */
	private StepItem myStep;
	/*
	 * Create the panel for the layout.
	 */
	private final FlowPanel myOuterPanel = new FlowPanel();

	/*
	 * The place holder image.
	 */
	private Image myPlaceHolderPicture = new Image(PICTURE_PLACE_HOLDER);

	/**
	 * Default constructor.
	 * 
	 * @param aStep
	 */
	public StepCandidatesWidget(StepItem aStep) {
		super();
		// Store the reference to the model
		myStep = aStep;
		// Listen to any change of the step state.
		myStep.addModelChangeListener(StepItem.ASSIGN_PROPERTY, this);

		// Put a place holder for the icon.
		myOuterPanel.add(myPlaceHolderPicture);

		this.initWidget(myOuterPanel);

		update();
	}

	/**
	 * Update the UI.
	 */
	private void update() {
		// Set the CSS style responsible of the icon color.
	  myPlaceHolderPicture.setStyleName(CSSClassManager.getStepAssignIconStyle(myStep.getAssign()));
	  String theTooltip = "";
    for (UserUUID theCandidate : myStep.getAssign()) {
      theTooltip += theCandidate.getValue() + ",";
    }
    if (theTooltip.endsWith(",")) {
      theTooltip = theTooltip.substring(0, theTooltip.length() - 1);
      myPlaceHolderPicture.setTitle(theTooltip);
    }
	}

  public StepItem getItem() {
    return myStep;
  }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.beans.ModelChangeListener#propertyChange(java.beans. PropertyChangeEvent)
	 */
	public void modelChange(ModelChangeEvent anEvent) {
		if (StepItem.ASSIGN_PROPERTY.equals(anEvent.getPropertyName())) {
			// Update the UI.
			update();
		}
	}

}
