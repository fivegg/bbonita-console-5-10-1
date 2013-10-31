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
import java.util.HashMap;

import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.labels.LabelDataSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * This widget display a {@link LabelModle}.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class LabelViewer extends Composite implements ModelChangeListener {

  private static final int MAX_LENGTH = 20;
  private static final String ROUNDED_CORNER_SUFFIX_STYLE_NAME = "_container";
  private CaseItem myCaseItem;
  private HorizontalPanel myOuterPanel;
  private boolean isDeletable;
  final private HashMap<String, DecoratorPanel> myLabelTable = new HashMap<String, DecoratorPanel>();
  private LabelDataSource myLabelDataSource;

  private class RemoveLabelClickHandler implements ClickHandler {

    LabelModel myLabel;

    /**
     * Default constructor.
     * 
     * @param aLabel
     */
    public RemoveLabelClickHandler(LabelModel aLabel) {
      super();
      this.myLabel = aLabel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
     * .event.dom.client.ClickEvent)
     */
    public void onClick(ClickEvent aArg0) {
      // myCaseItem.removeLabel(myLabel);
      myLabelDataSource.removeCaseFromLabel(myCaseItem.getUUID(), myLabel.getUUID());
    }
  }

  /**
   * Default constructor.
   * 
   * @param aDataSource
   * @param aCaseItem
   * @param isDeletable
   */
  public LabelViewer(LabelDataSource aDataSource, CaseItem aCaseItem, boolean isDeletable) {

    myCaseItem = aCaseItem;
    this.isDeletable = isDeletable;
    myLabelDataSource = aDataSource;

    LabelModel theLabelModel;
    for (LabelUUID theLabelUUID : aCaseItem.getLabels()) {
      theLabelModel = myLabelDataSource.getLabel(theLabelUUID);
      if (theLabelModel != null && theLabelModel.hasToBeDisplayed()) {
        if (isDeletable) {
          theLabelModel.addModelChangeListener(LabelModel.EDITABLE_CSS_CLASS_NAME_PROPERTY, this);
        } else {
          theLabelModel.addModelChangeListener(LabelModel.READONLY_CSS_CLASS_NAME_PROPERTY, this);
        }
        myLabelTable.put(theLabelUUID.getValue(), buildLabelTable(theLabelModel));
      }
    }

    myCaseItem.addModelChangeListener(CaseItem.LABELS_PROPERTY, this);

    // Create an empty horizontal panel to layout. It will be filled in
    // later.
    myOuterPanel = new HorizontalPanel();
    myOuterPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

    // May be better to set the cellSpacing?
    myOuterPanel.setSpacing(5);
    // DOM.setElementPropertyInt(myOuterPanel.getElement(), "cellPadding", 5);

    if (isDeletable) {
      myOuterPanel.setStyleName("label_viewer_editable");
    } else {
      myOuterPanel.setStyleName("label_viewer_readonly");
    }

    initWidget(myOuterPanel);
    fillInTable();
  }

  /**
   * Fill in the table with the labels associated to the case.
   */
  private void fillInTable() {
    // Firstly clean up panel.
    myOuterPanel.clear();
    LabelModel theLabelModel;
    for (LabelUUID theLabelUUID : myCaseItem.getLabels()) {
      theLabelModel = myLabelDataSource.getLabel(theLabelUUID.getValue());
      // The only reason it could be null is that the label has been
      // renamed and the case has not yet been updated.
      if (theLabelModel != null && theLabelModel.hasToBeDisplayed()) {
        // Check if the label has to be displayed.
        // Add the label in the panel.
        try {
          myOuterPanel.add(myLabelTable.get(theLabelUUID.getValue()));
        } catch (Exception theE) {
          GWT.log("label not found: " + theLabelUUID.getValue() + "," + theLabelUUID.getOwner().getValue(), theE);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seejava.beans.ModelChangeListener#propertyChange(java.beans.
   * PropertyChangeEvent)
   */
  @SuppressWarnings("unchecked")
  public void modelChange(ModelChangeEvent anEvent) {
    if (CaseItem.LABELS_PROPERTY.equals(anEvent.getPropertyName())) {
      ArrayList<LabelUUID> theLabelsToRemove = (ArrayList<LabelUUID>) anEvent.getOldValue();
      theLabelsToRemove.removeAll((ArrayList<LabelUUID>) anEvent.getNewValue());
      ArrayList<LabelUUID> theLabelsToAdd = (ArrayList<LabelUUID>) anEvent.getNewValue();
      theLabelsToAdd.removeAll((ArrayList<LabelUUID>) anEvent.getOldValue());

      LabelModel theLabelModel;
      for (LabelUUID theLabelUUID : theLabelsToRemove) {
        theLabelModel = myLabelDataSource.getLabel(theLabelUUID);
        // The only reason it could be null is that the label has been
        // renamed and the case has not yet been updated.
        if (theLabelModel != null) {
          if (isDeletable) {
            theLabelModel.removeModelChangeListener(LabelModel.EDITABLE_CSS_CLASS_NAME_PROPERTY, this);
          } else {
            theLabelModel.removeModelChangeListener(LabelModel.READONLY_CSS_CLASS_NAME_PROPERTY, this);
          }
        }
        // Release resources.
        myLabelTable.remove(theLabelUUID.getValue());
      }

      for (LabelUUID theLabelUUID : theLabelsToAdd) {
        theLabelModel = myLabelDataSource.getLabel(theLabelUUID);
        if (theLabelModel.hasToBeDisplayed()) {
          if (isDeletable) {
            theLabelModel.addModelChangeListener(LabelModel.EDITABLE_CSS_CLASS_NAME_PROPERTY, this);
          } else {
            theLabelModel.addModelChangeListener(LabelModel.READONLY_CSS_CLASS_NAME_PROPERTY, this);
          }
          if (!myLabelTable.containsKey(theLabelModel.getUUID().getValue())) {
            myLabelTable.put(theLabelModel.getUUID().getValue(), buildLabelTable(theLabelModel));
          }
        }
      }
      // rebuild the table as the list of labels has changed.
      fillInTable();
    }
    if (LabelModel.EDITABLE_CSS_CLASS_NAME_PROPERTY.equals(anEvent.getPropertyName()) || LabelModel.READONLY_CSS_CLASS_NAME_PROPERTY.equals(anEvent.getPropertyName())) {
      LabelModel theLabel = ((LabelModel) anEvent.getSource());
      DecoratorPanel theContainer = myLabelTable.get(theLabel.getUUID().getValue());
      theContainer.setStylePrimaryName((String) anEvent.getNewValue() + ROUNDED_CORNER_SUFFIX_STYLE_NAME);
      theContainer.getWidget().setStylePrimaryName((String) anEvent.getNewValue());
    }

  }

  private DecoratorPanel buildLabelTable(LabelModel aLabelModel) {
    DecoratorPanel theContainer = new DecoratorPanel();
    // Create the layout container.
    final HorizontalPanel theTable = new HorizontalPanel();

    final Label theNameLabel = new Label(buildShortName(LocaleUtil.translate(aLabelModel.getUUID())));
    theNameLabel.setTitle(LocaleUtil.translate(aLabelModel.getUUID()));
    theTable.add(theNameLabel);
    aLabelModel.addModelChangeListener(LabelModel.NAME_PROPERTY, new ModelChangeListener() {
      public void modelChange(ModelChangeEvent anEvt) {
        theNameLabel.setText(buildShortName(((LabelUUID) anEvt.getNewValue()).getValue()));
      }
    });
    if (isDeletable && aLabelModel.isAssignableByUser()) {
      final Label theRemoveLabel;
      theTable.setStylePrimaryName(aLabelModel.getEditableCSSStyleName());
      theRemoveLabel = new Label("X");
      theRemoveLabel.addClickHandler(new RemoveLabelClickHandler(aLabelModel));
      theRemoveLabel.setStyleName("remove");
      theTable.add(theRemoveLabel);
    } else {
      theTable.setStylePrimaryName(aLabelModel.getReadonlyCSSStyleName());
    }
    theContainer.add(theTable);
    theContainer.setStylePrimaryName(aLabelModel.getReadonlyCSSStyleName() + ROUNDED_CORNER_SUFFIX_STYLE_NAME);
    return theContainer;
  }

  /**
   * @param aValue
   * @return
   */
  private String buildShortName(String aName) {
    if (aName != null && aName.length() > MAX_LENGTH) {
      return aName.substring(0, MAX_LENGTH - 3) + "...";
    } else {
      return aName;
    }
  }
}
