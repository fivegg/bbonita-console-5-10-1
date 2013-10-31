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
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.steps.EventDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.view.steps.AdminStepEditor;
import org.bonitasoft.console.client.view.steps.StepEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;

/**
 * This widget is able to edit a case and display all the data related to a
 * particular case.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class AdminCaseEditorWidget extends AbstractCaseEditorWidget {

    protected AdminCaseMenuBarWidget myTopMenuBar;
    protected AdminCaseMenuBarWidget myBottomMenuBar;
    protected final EventDataSource myEventDataSource;
    protected final MessageDataSource myMessageDataSource;
    protected AsyncHandler<CaseItem> myCaseHandler;
    /**
     * Default constructor.
     * 
     * @param aLabelDataSource
     * @param aStepDataSource
     * @param aCategoryDataSource
     * @param anEventDataSource
     * @param aDataModel
     */
    public AdminCaseEditorWidget(CaseDataSource aCaseDataSource, CaseSelection aCaseSelection, LabelDataSource aLabelDataSource, StepItemDataSource aStepDataSource,
            ProcessDataSource aProcessDataSource, UserDataSource aUserDataSource, CategoryDataSource aCategoryDataSource, EventDataSource anEventDataSource, MessageDataSource aMessageDataSource) {
        super(aCaseDataSource, aCaseSelection, aLabelDataSource, aStepDataSource, aProcessDataSource, aUserDataSource, aCategoryDataSource);
        myEventDataSource = anEventDataSource;
        if (myEventDataSource != null) {
            myEventDataSource.addModelChangeListener(EventDataSource.EVENT_EXECUTED_PROPERTY, this);
        }
        myMessageDataSource = aMessageDataSource;
        initView();
    }

    @Override
    protected void buildBottomNavBar() {
        myBottomMenuBar = new AdminCaseMenuBarWidget(myCaseDataSource, myCaseSelection, true, myProcessDataSource, myMessageDataSource);
        myBottomNavBar.add(myBottomMenuBar);
    }

    @Override
    protected void buildTopNavBar() {
        myTopMenuBar = new AdminCaseMenuBarWidget(myCaseDataSource, myCaseSelection, true, myProcessDataSource, myMessageDataSource);
        myTopNavBar.add(myTopMenuBar);
    }

    @Override
    void redirectToCurrentPosition() {
        History.newItem(ViewToken.AdminCaseList.name());
    }

    @Override
    protected StepEditor buildStepEditor(StepItem theStepItem, boolean mustBeVisible) {
        return new AdminStepEditor(myStepDataSource, theStepItem, mustBeVisible, myCaseDataSource, myProcessDataSource, myUserDataSource, myEventDataSource);
    }

    @Override
    public void modelChange(ModelChangeEvent anEvent) {
        if (EventDataSource.EVENT_EXECUTED_PROPERTY.equals(anEvent.getPropertyName())) {
            final CaseUUID theUpdatedCaseUUID = (CaseUUID) anEvent.getSource();
            if (myCase.getUUID().equals(theUpdatedCaseUUID)) {
                if(myCaseHandler==null) {
                    myCaseHandler = new AsyncHandler<CaseItem>() {
                        
                        public void handleSuccess(CaseItem aResult) {
                            // Case reloaded
                            GWT.log("Admin case editor reloaded the case.");
                        }
                        
                        public void handleFailure(Throwable aT) {
                            // The case is not available anymore.
                            // Moved from journal to history?
                            GWT.log("Admin case editor: case not available anymore after event execution. Redirecting...");
                            redirectToCurrentPosition();
                        }
                    };
                }
                myCaseDataSource.getItem(myCase.getUUID(), myCaseHandler);
            } else {
                GWT.log("Ignored event execution notification (not the case currently edited.");
            }
        } else {
            super.modelChange(anEvent);
        }
    }
}
