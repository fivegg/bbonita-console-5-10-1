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
package org.bonitasoft.console.client.view.steps;

import org.bonitasoft.console.client.controller.UserRightsManager;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.steps.EventDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.security.client.privileges.RuleType;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class AdminStepEditor extends StepEditor {

    protected final EventDataSource myEventDataSource;

    /**
     * Default constructor.
     */
    public AdminStepEditor(StepItemDataSource aDataSource, StepItem aStep, boolean mustBeVisible, CaseDataSource aCaseDataSource, ProcessDataSource aProcessDataSource, UserDataSource aUserDataSource,
            EventDataSource anEventDataSource) {
        super(aDataSource, aStep, mustBeVisible, aCaseDataSource, aProcessDataSource, aUserDataSource);
        myEventDataSource = anEventDataSource;
        initContent();
        update();
    }

    @Override
    protected void initContent() {
        if (myEventDataSource != null) {
            super.initContent();
        }
    }

    @Override
    protected void update() {
        if (myEventDataSource != null) {
            super.update();
            if (myStep.isTimer() && this.mustBeOpen && !StepState.FINISHED.equals(myStep.getState()) && !StepState.CANCELLED.equals(myStep.getState()) && !StepState.ABORTED.equals(myStep.getState())) {
                if (myStepActionWidget != null) {
                    myFirstRowPanel.add(myStepActionWidget);
                }
            }
        }
    }

    @Override
    protected StepActionWidget buildStepActionWidget() {
        return new AdminStepActionWidget(myStepDataSource, myStep, myUserDataSource, myEventDataSource);
    }

    @Override
    protected Widget buildMenuOfActions() {
        // Add menu.
        final FlowPanel theMenu = new FlowPanel();
        final UserRightsManager theUserRightsManager = UserRightsManager.getInstance();
        if (myStep.getState() == StepState.FAILED) {
            theMenu.add(buildSkipStepMenuEntry());
        } else {        
            if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.ASSIGN_TO_ME_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                theMenu.add(buildAssignToMeMenuEntry());
            }
    
            if (myStep.getState() == StepState.SUSPENDED) {
                if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.RESUME_STEP, myStep.getUUID().getValue())) {
                    theMenu.add(buildResumeStepMenuEntry());
                }
            } else {
                if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.SUSPEND_STEP, myStep.getUUID().getValue())) {
                    theMenu.add(buildSuspendMenuEntry());
                }
            }
        }
        return theMenu;
    }
}
