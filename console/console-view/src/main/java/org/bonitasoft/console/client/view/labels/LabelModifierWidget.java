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

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.StyleSelectionListener;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget all a user to modify the customs labels.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class LabelModifierWidget extends BonitaPanel implements StyleSelectionListener, ModelChangeListener {

  private LabelModel myLabelModel;
  protected Hyperlink myLabelLink;
  private Image myColorPicker;
  private LabelStyleSelectorWidget myLabelStyleSelectorWidget;
  private HorizontalPanel myInnerPanel;
  private final DecoratorPanel myOuterPanel = new DecoratorPanel();
  private DecoratorPanel myColorPickerContainer;
  private LabelDataSource myLabelDataSource;
  private static final String NOT_SELECTED_STYLE = "user_label_widget";
  private static final String SELECTED_STYLE = "user_label_widget_selected";

  private class StyleSelectorClickHandler implements ClickHandler {

    StyleSelectionListener myListener;

    /**
     * Default constructor.
     * 
     * @param aListener
     */
    public StyleSelectorClickHandler(StyleSelectionListener aListener) {
      super();
      this.myListener = aListener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
     * .event.dom.client.ClickEvent)
     */
    public void onClick(ClickEvent anEvent) {
      if (myLabelStyleSelectorWidget == null) {
        myLabelStyleSelectorWidget = new LabelStyleSelectorWidget(myLabelDataSource, myLabelModel);
        myLabelStyleSelectorWidget.setStyleSelectionListener(myListener);
      }
      // Reposition the popup relative to the button
      Widget source = (Widget) anEvent.getSource();
      int left = source.getAbsoluteLeft() + 1;
      int top = source.getAbsoluteTop() + getOffsetHeight() + 1;
      myLabelStyleSelectorWidget.setPopupPosition(left, top);

      // Show the popup
      myLabelStyleSelectorWidget.show();

    }
  }

  /**
   * Default constructor.
   * 
   * @param aLabelDataSource
   * 
   * @param aLabel
   */
  public LabelModifierWidget(LabelDataSource aLabelDataSource, LabelModel aLabel) {
    super();
    myLabelDataSource = aLabelDataSource;
    myLabelModel = aLabel;
    myLabelModel.addModelChangeListener(LabelModel.PREVIEW_CSS_CLASS_NAME_PROPERTY, this);
    myLabelModel.addModelChangeListener(LabelModel.NAME_PROPERTY, this);

    myInnerPanel = new HorizontalPanel();
    myInnerPanel.setStylePrimaryName(CSSClassManager.CONTENT_STYLE);

    myColorPicker = new Image(PICTURE_PLACE_HOLDER);
    myColorPicker.addClickHandler(new StyleSelectorClickHandler(this));
    myColorPickerContainer = new DecoratorPanel();
    myColorPickerContainer.add(myColorPicker);
    myInnerPanel.add(myColorPickerContainer);
    myLabelLink = new Hyperlink(myLabelModel.getUUID().toString(), ViewToken.CaseList.name() + "/lab:" + myLabelModel.getUUID().getValue());
    myLabelLink.setTitle(patterns.browseToLabel(myLabelModel.getUUID().toString()));
    myInnerPanel.add(myLabelLink);

    myOuterPanel.add(myInnerPanel);
    myOuterPanel.setStylePrimaryName(NOT_SELECTED_STYLE);
    this.initWidget(myOuterPanel);
    
    update();
  }

  /*
   * Update the User Interface.
   */
  private void update() {

    myColorPicker.setStyleName(myLabelModel.getPreviewCSSStyleName());
    myColorPickerContainer.setStylePrimaryName(myLabelModel.getPreviewCSSStyleName() + "_container");
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.bonitasoft.console.client.events.StyleSelectionListener#
   * notifySelectionChange(java.lang.String, java.lang.String, java.lang.String)
   */
  public void notifySelectionChange(String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle) {
    myLabelStyleSelectorWidget.hide();
    myColorPicker.setStylePrimaryName(myLabelModel.getPreviewCSSStyleName());
  }

  /**
   * 
   * @param isSelected
   */
  public void setSelected(boolean isSelected) {
    if (isSelected) {
      myOuterPanel.setStylePrimaryName(SELECTED_STYLE);
    } else {
      myOuterPanel.setStylePrimaryName(NOT_SELECTED_STYLE);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seejava.beans.ModelChangeListener#propertyChange(java.beans.
   * PropertyChangeEvent)
   */
  public void modelChange(ModelChangeEvent anEvent) {
    if (LabelModel.PREVIEW_CSS_CLASS_NAME_PROPERTY.equals(anEvent.getPropertyName())) {
      // Update the UI.
      update();
    }
    if (LabelModel.NAME_PROPERTY.equals(anEvent.getPropertyName())) {
      // myLabelStyleSelectorWidget.hide();
      String theNewName = ((LabelUUID) anEvent.getNewValue()).getValue();
      myLabelLink.setText(theNewName);
      myLabelLink.setTargetHistoryToken(ViewToken.CaseList.name() + "/lab:" + theNewName);
      myLabelLink.setTitle(patterns.browseToLabel(theNewName));
    }
  }

  /**
   * Get the CSS style given to this widget by default.<br>
   * Useful if you want to reset the widget.
   * 
   * @return
   */
  public static String getDefaultStyleName() {
    return NOT_SELECTED_STYLE;
  }
}
