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

import org.bonitasoft.console.client.events.StyleSelectionListener;
import org.bonitasoft.console.client.labels.LabelModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * This widget allows a user to select a css style using some preview mechanism
 * to associate to a {@link PersistedLabelModel}.
 * 
 * @author Nicolas Chabanoles
 */
public class CSSStyleSelectorWidget extends I18NPopupPanel {

  protected static final String[] editableCSSClassName = new String[] { "label_red_editable", "label_dark_red_editable", "label_green_editable", "label_dark_green_editable", "label_blue_editable",
      "label_dark_blue_editable", LabelModel.DEFAULT_EDITABLE_CSS, "label_dark_grey_editable" };
  protected static final String[] readonlyCSSClassName = new String[] { "label_red_readonly", "label_dark_red_readonly", "label_green_readonly", "label_dark_green_readonly", "label_blue_readonly",
      "label_dark_blue_readonly", LabelModel.DEFAULT_READONLY_CSS, "label_dark_grey_readonly" };
  protected static final String[] previewCSSClassName = new String[] { "label_red_preview", "label_dark_red_preview", "label_green_preview", "label_dark_green_preview", "label_blue_preview",
      "label_dark_blue_preview", LabelModel.DEFAULT_PREVIEW_CSS, "label_dark_grey_preview" };

  protected static final String CONTAINER_CSS_SUFFIX = "_container";
  protected static final String B_CHARACTER = "b";
  protected static final String TITLE_LABEL_KEY = constants.selectLabelStyle();
  protected StyleSelectionListener myStyleSelectionListener;

  private class StyleClickHandler implements ClickHandler {

    String myEditableCSSStyle;
    String myPreviewCSSStyle;
    String myReadOnlyCSSStyle;

    /**
     * Default constructor.
     * 
     * @param aLabel
     * @param aEditableCSSStyle
     * @param aPreviewCSSStyle
     * @param aReadOnlyCSSStyle
     */
    public StyleClickHandler(String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle) {
      super();
      this.myEditableCSSStyle = aEditableCSSStyle;
      this.myPreviewCSSStyle = aPreviewCSSStyle;
      this.myReadOnlyCSSStyle = aReadOnlyCSSStyle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
     * .dom.client.ClickEvent)
     */
    public void onClick(ClickEvent aArg0) {
      myStyleSelectionListener.notifySelectionChange(myEditableCSSStyle, myPreviewCSSStyle, myReadOnlyCSSStyle);
    }
  }

  /**
   * 
   * Default constructor.
   * 
   * @param aLabelDataSource
   * @param aLabelModel
   */
  public CSSStyleSelectorWidget() {
    super();
    FlexTable theOuterPanel = new FlexTable();
    Label theLabel;
    DecoratorPanel theContainer;
    theLabel = new Label(TITLE_LABEL_KEY);
    theOuterPanel.setWidget(0, 0, theLabel);
    theOuterPanel.getFlexCellFormatter().setColSpan(0, 0, 4);

    if (editableCSSClassName.length != readonlyCSSClassName.length || editableCSSClassName.length != previewCSSClassName.length) {
      Window.alert("Invalid list of CSS style definitions in class LabelStyleSelectorWidget!");
    }

    int theRow = 1;
    int theCol = 0;
    for (int i = 0; i < editableCSSClassName.length; i++) {
      theContainer = new DecoratorPanel();
      theLabel = new Label(B_CHARACTER);
      theContainer.add(theLabel);
      theContainer.setStylePrimaryName(previewCSSClassName[i] + CONTAINER_CSS_SUFFIX);
      theLabel.setStyleName(previewCSSClassName[i]);
      theLabel.addClickHandler(new StyleClickHandler(editableCSSClassName[i], previewCSSClassName[i], readonlyCSSClassName[i]));
      theOuterPanel.setWidget(theRow, theCol, theContainer);
      // Go to a new line every 4 choices.
      if (theCol == 3) {
        theRow++;
        theCol = 0;
      } else {
        theCol++;
      }
    }

    this.add(theOuterPanel);
    // Set the auto hide feature.
    setAutoHideEnabled(true);
  }

  /**
   * 
   * @param aListener
   */
  public void setStyleSelectionListener(StyleSelectionListener aListener) {
    myStyleSelectionListener = aListener;

  }

}
