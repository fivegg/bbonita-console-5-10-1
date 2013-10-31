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
import java.util.List;
import java.util.Map.Entry;

import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CustomLabelListWidget extends BonitaPanel implements ModelChangeListener {

  /**
   * The LEFT_PANEL_SEPARATOR_CSS_STYLE_NAME defines
   */
  protected static final String LEFT_PANEL_SEPARATOR_CSS_STYLE_NAME = "left_panel_separator";
  protected static final String MORE_LABEL_KEY = constants.more();

  protected final LabelDataSource myLabelDataSource;
  protected final VerticalPanel myOuterPanel = new VerticalPanel();;
  protected final FlexTable myCustomLabelTable = new FlexTable();

  private LabelUUID mySelectedLabelUUID;
  protected final HashMap<String, LabelModifierWidget> myCustomLabelWidgets = new HashMap<String, LabelModifierWidget>();

  protected ArrayList<LabelModel> myCustomLabels;

  public CustomLabelListWidget(LabelDataSource aLabelDataSource) {
    super();
    myLabelDataSource = aLabelDataSource;

    // Listen to changes in the list of system label.
    myLabelDataSource.addModelChangeListener(LabelDataSource.USER_LABEL_LIST_PROPERTY, this);
    myLabelDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, this);

    // Put a visual separator between the system and custom labels.
    FlowPanel theSeparator = new FlowPanel();
    theSeparator.setStyleName(LEFT_PANEL_SEPARATOR_CSS_STYLE_NAME);
    myOuterPanel.add(theSeparator);

    myOuterPanel.add(myCustomLabelTable);

    this.initWidget(myOuterPanel);
    buildWidgetMap();
    update();
  }

  protected void update() {
    List<LabelModel> theCustomLabels = myLabelDataSource.getCustomLabels();

    int theRow = 0;
    myCustomLabelTable.clear();
    if (theCustomLabels != null) {
      for (LabelModel theLabelModel : theCustomLabels) {
        if (theLabelModel.isVisible()) {
          myCustomLabelTable.setWidget(theRow, 0, myCustomLabelWidgets.get(theLabelModel.getUUID().getValue()));
          myCustomLabelWidgets.get(theLabelModel.getUUID().getValue()).setStylePrimaryName(LabelModifierWidget.getDefaultStyleName());
          theRow++;
        }
      }
    }
  }

  private void buildWidgetMap() {
    // Build widgets for the custom labels.
    List<LabelModel> theCustomLabels = myLabelDataSource.getCustomLabels();
    if (theCustomLabels != null) {
      LabelModifierWidget theCustomLabelWidget;
      for (int i = 0; i < theCustomLabels.size(); i++) {
        LabelModel theLabel = theCustomLabels.get(i);
        theLabel.addModelChangeListener(LabelModel.VISIBILITY_PROPERTY, this);
        theCustomLabelWidget = new LabelModifierWidget(myLabelDataSource, theLabel);
        myCustomLabelWidgets.put(theLabel.getUUID().getValue(), theCustomLabelWidget);
        theLabel.addModelChangeListener(LabelModel.NAME_PROPERTY, new ModelChangeListener() {
          // In case of label renaming, rebind the widget to the new label name.
          public void modelChange(ModelChangeEvent anEvt) {
            LabelUUID theNewUUID = (LabelUUID) anEvt.getNewValue();
            LabelUUID theOldUUID = (LabelUUID) anEvt.getOldValue();
            LabelModifierWidget theWidget = myCustomLabelWidgets.remove(theOldUUID.getValue());
            if (theWidget != null) {
              myCustomLabelWidgets.put(theNewUUID.getValue(), theWidget);
            }
          }
        });
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.beans.ModelChangeListener#propertyChange(java.beans.PropertyChangeEvent
   * )
   */
  @SuppressWarnings("unchecked")
  public void modelChange(ModelChangeEvent anEvent) {
    if (LabelModel.VISIBILITY_PROPERTY.equals(anEvent.getPropertyName())) {
      update();
    } else if (LabelDataSource.USER_LABEL_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
      if (myCustomLabels != null) {
        // Unregister listeners.
        for (int i = 0; i < myCustomLabels.size(); i++) {
          LabelModel theLabel = myCustomLabels.get(i);
          if (theLabel != null) {
            theLabel.removeModelChangeListener(LabelModel.VISIBILITY_PROPERTY, this);
          }
        }
      }
      myCustomLabels = (ArrayList<LabelModel>) anEvent.getNewValue();
      myCustomLabelWidgets.clear();
      // Register again listeners and rebuild widgets.
      buildWidgetMap();
      if (mySelectedLabelUUID != null && myCustomLabelWidgets.containsKey(mySelectedLabelUUID.getValue())) {
        myCustomLabelWidgets.get(mySelectedLabelUUID.getValue()).setSelected(true);
      }
      update();
    }
  }

  public void setSelectedLabel(LabelModel aLabel) {
    if (aLabel != null) {
      mySelectedLabelUUID = aLabel.getUUID();
    } else {
      mySelectedLabelUUID = null;
    }
    for (Entry<String, LabelModifierWidget> entry : myCustomLabelWidgets.entrySet()) {
      String theLabelName = entry.getKey();
      LabelModel theLabelModel = myLabelDataSource.getLabel(theLabelName);
      // The only reason it could be null is that the label has been renamed.
      if (theLabelModel != null) {
        if (theLabelModel.isVisible()) {
          entry.getValue().setSelected(false);
        }
      }
    }
    if (mySelectedLabelUUID != null && myCustomLabelWidgets.containsKey(mySelectedLabelUUID.getValue())) {
      if (aLabel.isVisible()) {
        myCustomLabelWidgets.get(mySelectedLabelUUID.getValue()).setSelected(true);
      }
    }

  }

}
