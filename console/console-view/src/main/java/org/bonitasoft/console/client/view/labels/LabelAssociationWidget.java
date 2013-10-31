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
package org.bonitasoft.console.client.view.labels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.controller.LabelAssociationController;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelAssociationWidget extends BonitaPanel {

  private static final String PARTIALLY_CHECKED_TRISTATECHECKBOX = "b-tristatecheckbox-partially-checked";

  private static final String CHECKED_TRISTATECHECKBOX = "b-tristatecheckbox-fully-checked";

  private static final String EMPTY_TRISTATECHECKBOX = "b-tristatecheckbox";

  private static final HashSet<LabelUUID> myLabelsToAddToCaseSelection = new HashSet<LabelUUID>();
  private static final HashSet<LabelUUID> myLabelsToRemoveFromCaseSelection = new HashSet<LabelUUID>();

  private enum TriState {
    EMPTY, PARTIAL, FULL;
  }

  /*
   * A ClickHandler implementation that will manage the binding between the
   * myCases and the labels.
   */
  private class LabelClickHandler implements ClickHandler {

    private LabelUUID myLabelUUID;
    private TriState myInitialState;
    private TriState myCurrentState;
    private Image myImage;
    private String myInitialImageStyle;

    /**
     * 
     * Default constructor.
     * 
     * @param aLabelUUID
     * @param anInitialState
     * @param anImage
     */
    public LabelClickHandler(final LabelUUID aLabelUUID, final TriState anInitialState, Image anImage) {
      myLabelUUID = aLabelUUID;
      myInitialState = anInitialState;
      myCurrentState = anInitialState;
      switch (myInitialState) {
      case EMPTY:
        myInitialImageStyle = EMPTY_TRISTATECHECKBOX;
        break;
      case PARTIAL:
        myInitialImageStyle = PARTIALLY_CHECKED_TRISTATECHECKBOX;
        break;
      case FULL:
        myInitialImageStyle = CHECKED_TRISTATECHECKBOX;
        break;
      default:
        break;
      }
      myImage = anImage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
     * .event.dom.client.ClickEvent)
     */
    public void onClick(ClickEvent aEvent) {
      /*
       * Default behavior is to round from EMPTY to PARTIAL and then to FULL.
       * The transition from EMPTY to PARTIAL is equivalent to cancel changes.
       */
      if (myCaseSelection.getSize() > 0) {
        switch (myCurrentState) {
        case EMPTY:
          addLabelToCaseSelection(myLabelUUID);
          break;
        case PARTIAL:
          addLabelToCaseSelection(myLabelUUID);
          break;
        case FULL:
          removeLabelFromCaseSelection(myLabelUUID);
          break;
        default:
          break;
        }
        // Update the current state.
        if (myCurrentState == TriState.FULL) {
          myCurrentState = TriState.EMPTY;
          myImage.setStylePrimaryName(EMPTY_TRISTATECHECKBOX);
        } else {
          if (myCurrentState == TriState.EMPTY && myInitialState == TriState.PARTIAL) {
            // Transition from EMPTY to PARTIAL
            myCurrentState = myInitialState;
            myImage.setStylePrimaryName(myInitialImageStyle);
          } else {
            // Transition from PARTIAL or EMPTY to FULL
            myCurrentState = TriState.FULL;
            myImage.setStylePrimaryName(CHECKED_TRISTATECHECKBOX);
          }
        }
        if (myCurrentState == myInitialState) {
          cancelModifications(myLabelUUID);
        }
      }
    }

    private void cancelModifications(LabelUUID aLabelUUID) {
      myLabelsToAddToCaseSelection.remove(aLabelUUID);
      myLabelsToRemoveFromCaseSelection.remove(aLabelUUID);
    }

    private void addLabelToCaseSelection(LabelUUID aLabelUUID) {
      myLabelsToAddToCaseSelection.add(aLabelUUID);
    }

    private void removeLabelFromCaseSelection(LabelUUID aLabelUUID) {
      myLabelsToRemoveFromCaseSelection.add(aLabelUUID);
    }
  }

  /*
   * Capture the events coming from the mouse to dynamically update the css
   * style of table entries.
   */
  private class MouseHandler implements MouseOverHandler, MouseOutHandler {
    private static final String DEPENDENT_STYLENAME_SELECTED_ITEM = "selected";
    private int myRow;

    public MouseHandler(int aRow) {
      myRow = aRow;
    }

    public void onMouseOut(MouseOutEvent aEvent) {
      myLabelSelection.getRowFormatter().removeStyleName(myRow, DEPENDENT_STYLENAME_SELECTED_ITEM);
    }

    public void onMouseOver(MouseOverEvent aEvent) {
      myLabelSelection.getRowFormatter().addStyleName(myRow, DEPENDENT_STYLENAME_SELECTED_ITEM);
    }
  }

  private CaseSelection myCaseSelection;
  private LabelDataSource myLabelDataSource;
  LabelAssociationController myController;

  ScrollPanel theScrollPanel = new ScrollPanel();
  // Create the panel for the list of labels.
  FlexTable myLabelSelection = new FlexTable();
  protected final Label myApplyLabel = new Label(constants.apply());

  private CaseDataSource myCaseDataSource;

  /**
   * Default constructor.
   * 
   * @param aController
   * @param aCaseDataSource
   * 
   * @param aCaseDataSource
   * 
   * @param aCaseSelection
   * @param aLabelDataSource
   */
  public LabelAssociationWidget(LabelAssociationController aController, CaseDataSource aCaseDataSource, CaseSelection aCaseSelection, LabelDataSource aLabelDataSource) {
    super();
    myController = aController;
    myCaseDataSource = aCaseDataSource;
    myCaseSelection = aCaseSelection;
    myLabelDataSource = aLabelDataSource;

    myApplyLabel.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent aEvent) {
        applyChanges();
      }
    });

    // Update the UI.
    myLabelSelection.setStyleName("label_selection");

    theScrollPanel.add(myLabelSelection);
    update();

    this.initWidget(theScrollPanel);
  }

  /**
   * Update the UI.
   */
  private void update() {
    // first of all clean the panel
    myLabelSelection.clear();

    final List<LabelModel> theLabels = myLabelDataSource.getAllLabels();

    // CheckBox theCheckBox;
    Image theSelectorImage;
    LabelModel theLabelModel;
    Image theLabelIcon;
    Label theLabel;
    MouseHandler theMouseHandler;
    LabelClickHandler theClickHandler;
    int theNbSelectedCaseHavingCurrentLabel;
    int theNbCaseSelected = myCaseSelection.getSelectedItems().size();
    int row = 0;
    // Create an entry for each label.
    for (int i = 0; i < theLabels.size(); i++) {
      theLabelModel = theLabels.get(i);
      if (theLabelModel.isAssignableByUser()) {
        // The state of the checkbox depends on the number of
        // occurrences.
        // If the label is associated to all cases it must be
        // selected.
        // If the label is not associated to any case it must NOT be
        // selected.
        // If it is associated at least one but not all cases, it must
        // be
        // "partially" selected.

        theSelectorImage = new Image(PICTURE_PLACE_HOLDER);
        theNbSelectedCaseHavingCurrentLabel = countSelectedCases(theLabelModel.getUUID()); // myCaseSelection.getSelectedCaseCount(theLabelModel);

        if (theNbSelectedCaseHavingCurrentLabel == 0 || theNbCaseSelected == 0) {
          // Empty checkbox
          theSelectorImage.setStylePrimaryName(EMPTY_TRISTATECHECKBOX);
          theClickHandler = new LabelClickHandler(theLabelModel.getUUID(), TriState.EMPTY, theSelectorImage);
          theSelectorImage.addClickHandler(theClickHandler);
        } else {
          if (theNbSelectedCaseHavingCurrentLabel == theNbCaseSelected) {
            // Checkbox fully checked
            theSelectorImage.setStylePrimaryName(CHECKED_TRISTATECHECKBOX);
            theClickHandler = new LabelClickHandler(theLabelModel.getUUID(), TriState.FULL, theSelectorImage);
            theSelectorImage.addClickHandler(theClickHandler);
          } else {
            // Checkbox partially checked
            theSelectorImage.setStylePrimaryName(PARTIALLY_CHECKED_TRISTATECHECKBOX);
            theClickHandler = new LabelClickHandler(theLabelModel.getUUID(), TriState.PARTIAL, theSelectorImage);
            theSelectorImage.addClickHandler(theClickHandler);
          }
        }

        theLabel = new Label(LocaleUtil.translate(theLabelModel.getUUID()));
        theMouseHandler = new MouseHandler(row);
        theLabel.addMouseOverHandler(theMouseHandler);
        theLabel.addMouseOutHandler(theMouseHandler);
        // Bind the same click handler as the checkbox, to allow the
        // user to click on the label or the check box to achieve the
        // same result.
        theLabel.addClickHandler(theClickHandler);

        theLabelIcon = new Image(PICTURE_PLACE_HOLDER);
        if (theLabelModel.getIconCSSStyle() != null && theLabelModel.getIconCSSStyle().trim().length() > 0) {
          theLabelIcon.setStylePrimaryName(theLabelModel.getIconCSSStyle());
        } else {
            theLabelIcon.setWidth("60%");
        }
        theLabelIcon.addMouseOverHandler(theMouseHandler);
        theLabelIcon.addMouseOutHandler(theMouseHandler);
        // Bind the same click handler as the checkbox, to allow the
        // user to click on the label or the check box to achieve the
        // same result.
        theLabelIcon.addClickHandler(theClickHandler);
        
        myLabelSelection.setWidget(row, 0, theSelectorImage);
        myLabelSelection.setWidget(row, 1, theLabel);
        myLabelSelection.setWidget(row, 2, theLabelIcon);

        myLabelSelection.getRowFormatter().setStyleName(row, CSSClassManager.POPUP_MENU_ENTRY);
        myLabelSelection.getFlexCellFormatter().setColSpan(row, 0, 1);
        row++;
      }
    }
    if (row > 0) {
      myLabelSelection.getRowFormatter().setStyleName(row, "menu_separator");
      myLabelSelection.getFlexCellFormatter().setColSpan(row, 0, 3);
      row++;

      myLabelSelection.setWidget(row, 0, myApplyLabel);
      myLabelSelection.getFlexCellFormatter().setColSpan(row, 0, 3);
      myLabelSelection.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
      myLabelSelection.getRowFormatter().setStyleName(row, CSSClassManager.POPUP_MENU_ENTRY);

      theMouseHandler = new MouseHandler(row);
      myApplyLabel.addMouseOverHandler(theMouseHandler);
      myApplyLabel.addMouseOutHandler(theMouseHandler);
    }
  }

  private int countSelectedCases(LabelUUID aLabelUUID) {
    int theResult = 0;
    ArrayList<CaseUUID> theCaseUUIDs = myCaseSelection.getSelectedItems();
    for (CaseUUID theCaseUUID : theCaseUUIDs) {
      CaseItem theCaseItem = myCaseDataSource.getItem(theCaseUUID);
      if (theCaseItem.getLabels().contains(aLabelUUID)) {
        theResult += 1;
      }
    }
    return theResult;
  }

  /**
   * Apply the chnages on data source.
   */
  void applyChanges() {
      if(myCaseSelection.getSize()>0) {
          myController.applyChanges(myLabelsToAddToCaseSelection, myLabelsToRemoveFromCaseSelection, new HashSet<CaseUUID>(myCaseSelection.getSelectedItems()));
      }
  }

  /*
   * Reset the state of the widget and update the UI.
   */
  private void reset() {
    myLabelsToAddToCaseSelection.clear();
    myLabelsToRemoveFromCaseSelection.clear();
    update();
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    reset();
  }

}
