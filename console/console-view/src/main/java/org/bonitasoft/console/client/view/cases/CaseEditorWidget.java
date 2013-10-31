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

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.view.steps.StepEditor;

import com.google.gwt.user.client.History;

/**
 * This widget is able to edit a case and display all the data related to a
 * particular case.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class CaseEditorWidget extends AbstractCaseEditorWidget {

  private CaseMenuBarWidget myTopMenuBar;
  private CaseMenuBarWidget myBottomMenuBar;

  /**
   * Default constructor.
   * 
   * @param aLabelDataSource
   * @param aStepDataSource
   * @param aDataModel
   */
  public CaseEditorWidget(final CaseDataSource aCaseDataSource, final CaseSelection aCaseSelection, final LabelDataSource aLabelDataSource, final StepItemDataSource aStepDataSource,
      final ProcessDataSource aProcessDataSource, final UserDataSource aUserDataSource, final CategoryDataSource aCategoryDataSource) {
    super(aCaseDataSource, aCaseSelection, aLabelDataSource, aStepDataSource, aProcessDataSource, aUserDataSource, aCategoryDataSource);
    initView();
  }

  @Override
  protected void buildBottomNavBar() {
    myBottomMenuBar = new CaseMenuBarWidget(myCaseDataSource, myCaseSelection, myLabelDataSource, true);
    myBottomNavBar.add(myBottomMenuBar);
  }

  @Override
  protected void buildTopNavBar() {
    myTopMenuBar = new CaseMenuBarWidget(myCaseDataSource, myCaseSelection, myLabelDataSource, true);
    myTopNavBar.add(myTopMenuBar);
  }

  @Override
  protected void update() {
    super.update();
    CaseFilter theFilter = myCaseDataSource.getItemFilter();
    if (theFilter != null) {
      if (theFilter.getLabel() != null) {
        final LabelModel theLabel = myLabelDataSource.getLabel(theFilter.getLabel());
        // First clear then set. The order is important here.
        myTopMenuBar.setCategoryToDisplay(null);
        myBottomMenuBar.setCategoryToDisplay(null);
        myTopMenuBar.setLabelToDisplay(theLabel);
        myBottomMenuBar.setLabelToDisplay(theLabel);
      } else {
        if (theFilter.getCategory() != null) {
//          final Category theCategory = myCategoryDataSource.getItem(theFilter.getCategory());
          final Category theCategory = theFilter.getCategory();
          // First clear then set. The order is important here.
          myTopMenuBar.setLabelToDisplay(null);
          myBottomMenuBar.setLabelToDisplay(null);
          myTopMenuBar.setCategoryToDisplay(theCategory);
          myBottomMenuBar.setCategoryToDisplay(theCategory);
        } 
      }
    }

  }

  @Override
  void redirectToCurrentPosition() {
    if (myCaseDataSource.getItemFilter().getLabel() != null) {
      History.newItem(ViewToken.CaseList + "/lab:" + myCaseDataSource.getItemFilter().getLabel());
    } else if (myCaseDataSource.getItemFilter().getCategory() != null) {
      History.newItem(ViewToken.CaseList + "/cat:" + myCaseDataSource.getItemFilter().getCategory());
    }
    
  }
  
  @Override
  protected StepEditor buildStepEditor(StepItem theStepItem, boolean mustBeVisible) {
    return new StepEditor(myStepDataSource, theStepItem, mustBeVisible, myCaseDataSource, myProcessDataSource, myUserDataSource);
  }
}
