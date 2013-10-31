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
package org.bonitasoft.console.client.view;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StarWidget extends BonitaPanel implements ClickHandler, ModelChangeListener {

  private CaseItem myCaseItem;
  private Image myStarImage;

  private LabelDataSource myLabelDataSource;
  private LabelUUID myStarLabelUUID;

  /**
   * 
   * Default constructor.
   * 
   * @param aLabelDataSource
   * @param aCaseItem
   */
  public StarWidget(LabelDataSource aLabelDataSource, CaseItem aCaseItem) {
    super();
    myLabelDataSource = aLabelDataSource;
    myCaseItem = aCaseItem;

    FlowPanel theOuterPanel = new FlowPanel();
    myStarImage = new Image(PICTURE_PLACE_HOLDER);
    // Set the initial style for the icon
    myStarImage.setStyleName(CSSClassManager.getStarIconStyle(myCaseItem.isStarred()));

    if (!myCaseItem.isArchived()) {
      // register to changes
      myCaseItem.addModelChangeListener(CaseItem.LABELS_PROPERTY, this);
      
      myStarImage.setTitle(constants.markThisCase());
      // listen for click on the icon.
      // each click means the user want to toggle the star flag.
      final HandlerRegistration theRegistration = myStarImage.addClickHandler(this);
      myCaseItem.addModelChangeListener(CaseItem.HISTRORY_PROPERTY, new ModelChangeListener() {
        
        public void modelChange(ModelChangeEvent aEvt) {
          theRegistration.removeHandler();
          myStarImage.setTitle(constants.archivedCasesCannotBeMarkedToolTip());
        }
      });
    } else {
      myStarImage.setTitle(constants.archivedCasesCannotBeMarkedToolTip());
    }
    
    myStarLabelUUID = new LabelUUID(LabelModel.STAR_LABEL.getUUID().getValue(), new UserUUID(BonitaConsole.userProfile.getUsername()));
    theOuterPanel.add(myStarImage);

    initWidget(theOuterPanel);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
   * .dom.client.ClickEvent)
   */
  public void onClick(ClickEvent event) {
    // Make the update occur in the other layers.
    if (myCaseItem.isStarred()) {
      myLabelDataSource.removeCaseFromLabel(myCaseItem.getUUID(), myStarLabelUUID);
    } else {
      myLabelDataSource.addCaseToLabel(myCaseItem.getUUID(), myStarLabelUUID);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.beans.ModelChangeListener#propertyChange(java.beans.PropertyChangeEvent
   * )
   */
  public void modelChange(ModelChangeEvent anEvent) {
    update();

  }

  private void update() {
    // Each time the model is updated, the view must be updated as well.
    // Set the current style for the icon
    myStarImage.setStyleName(CSSClassManager.getStarIconStyle(myCaseItem.isStarred()));
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    update();
  }
}
