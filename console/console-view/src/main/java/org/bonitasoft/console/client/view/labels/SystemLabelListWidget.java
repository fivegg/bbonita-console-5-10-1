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
import java.util.Iterator;
import java.util.List;

import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class SystemLabelListWidget extends BonitaPanel implements ModelChangeListener {

  /**
   * The LEFT_PANEL_SEPARATOR_CSS_STYLE_NAME defines
   */
  protected static final String LEFT_PANEL_SEPARATOR_CSS_STYLE_NAME = "left_panel_separator";
  protected static final String MORE_LABEL_KEY = constants.more();

  protected final LabelDataSource myLabelDataSource;
  protected final VerticalPanel myOuterPanel = new VerticalPanel();;
  protected final FlexTable mySystemLabelTable = new FlexTable();

  private LabelUUID mySelectedLabelUUID;
  protected final HashMap<LabelUUID, SystemLabelWidget> mySystemLabelWidgets = new HashMap<LabelUUID, SystemLabelWidget>();

  protected ArrayList<LabelModel> mySystemLabels;
  protected LabelsConfiguration myConfiguration;

  public SystemLabelListWidget(LabelDataSource aLabelDataSource) {
    super();
    myLabelDataSource = aLabelDataSource;

    // Listen to changes in the list of system label.
    myLabelDataSource.addModelChangeListener(LabelDataSource.SYSTEM_LABEL_LIST_PROPERTY, this);
    myLabelDataSource.addModelChangeListener(LabelDataSource.USER_LABEL_LIST_PROPERTY, this);
    myLabelDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, this);

    myOuterPanel.add(mySystemLabelTable);

    this.initWidget(myOuterPanel);
    buildWidgetMap();

    myLabelDataSource.getConfiguration(new AsyncHandler<LabelsConfiguration>() {

      public void handleFailure(Throwable aT) {
        update();
      }

      public void handleSuccess(LabelsConfiguration aResult) {
        myConfiguration = new LabelsConfiguration();
        myConfiguration.setCustomLabelsEnabled(aResult.isCustomLabelsEnabled());
        myConfiguration.setStarEnabled(aResult.isStarEnabled());
        update();
      }
    });

  }

  protected void update() {
    List<LabelModel> theSystemLabels = myLabelDataSource.getSystemLabels();

    int theRow = 0;
    mySystemLabelTable.clear();
    if (theSystemLabels != null) {
      for (LabelModel theLabelModel : theSystemLabels) {
        if (theLabelModel.isVisible()) {
          mySystemLabelTable.setWidget(theRow, 0, mySystemLabelWidgets.get(theLabelModel.getUUID()));
          mySystemLabelWidgets.get(theLabelModel.getUUID()).setStylePrimaryName(SystemLabelWidget.getDefaultStyleName());
          theRow++;
        }
      }
    }
  }

  private void buildWidgetMap() {
    // Build widgets for the system labels.
    List<LabelModel> theSystemLabels = myLabelDataSource.getSystemLabels();
    SystemLabelWidget theSystemWidget;
    if (theSystemLabels != null) {
      for (Iterator<LabelModel> iterator = theSystemLabels.iterator(); iterator.hasNext();) {
        LabelModel theLabelModel = (LabelModel) iterator.next();
        theLabelModel.addModelChangeListener(LabelModel.VISIBILITY_PROPERTY, this);
        theSystemWidget = new SystemLabelWidget(myLabelDataSource, theLabelModel, false);
        if (theLabelModel.getUUID() != null) {
            theSystemWidget.addStyleName(SystemLabelWidget.SYSTEM_LABEL_STYLE_PREFIX + theLabelModel.getUUID().getValue().replaceAll(" ", "_"));
        }
        mySystemLabelWidgets.put(theLabelModel.getUUID(), theSystemWidget);
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
    } else if (LabelDataSource.SYSTEM_LABEL_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
      if (mySystemLabels != null) {
        // Unregister listeners.
        for (LabelModel theLabelModel : mySystemLabels) {
          theLabelModel.removeModelChangeListener(LabelModel.VISIBILITY_PROPERTY, this);
        }
      }
      mySystemLabels = (ArrayList<LabelModel>) anEvent.getNewValue();
      mySystemLabelWidgets.clear();
      // Register again listeners and rebuild widgets.
      buildWidgetMap();

      if (mySystemLabelWidgets.containsKey(mySelectedLabelUUID)) {
        mySystemLabelWidgets.get(mySelectedLabelUUID).setSelected(true);
      }
      update();
    } else if (LabelDataSource.CONFIGURATION_PROPERTY.equals(anEvent.getPropertyName())) {
      final LabelsConfiguration theNewConfiguration = (LabelsConfiguration) anEvent.getNewValue();
      if (theNewConfiguration != null) {
        if (myConfiguration == null) {
          myConfiguration = new LabelsConfiguration();
        }
        myConfiguration.setCustomLabelsEnabled(theNewConfiguration.isCustomLabelsEnabled());
        myConfiguration.setStarEnabled(theNewConfiguration.isStarEnabled());
      } else {
        myConfiguration = null;
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
    for (LabelUUID theLabelUUID : mySystemLabelWidgets.keySet()) {
      LabelModel theLabelModel = myLabelDataSource.getLabel(theLabelUUID);
      // The only reason it could be null is that the label has been renamed.
      if (theLabelModel != null) {
        if (theLabelModel.isVisible()) {
          mySystemLabelWidgets.get(theLabelUUID).setSelected(false);
        }
      }
    }
    if (mySystemLabelWidgets.containsKey(mySelectedLabelUUID)) {
      if (aLabel.isVisible()) {
        mySystemLabelWidgets.get(mySelectedLabelUUID).setSelected(true);
      }
    }
  }
}
