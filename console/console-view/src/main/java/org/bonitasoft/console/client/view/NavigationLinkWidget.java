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
package org.bonitasoft.console.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class NavigationLinkWidget extends BonitaPanel implements ValueChangeHandler<String> {

  private static final String SELECTED_SUFFIX_STYLE = "selected";

  // Create the layout.
  private final DecoratorPanel myOuterPanel = new DecoratorPanel();
  private final FlowPanel myInnerPanel = new FlowPanel();

  private final String myHitoryTokenValue;

  /**
   * 
   * Default constructor.
   * 
   * @param aLabel
   * @param selected
   */
  public NavigationLinkWidget(final String anIconCSSStyle, final String aLabel, final String aHitoryTokenValue) {
    super();
    myHitoryTokenValue = aHitoryTokenValue;

    // Create the widgets.
    final Image theIcon = new Image(PICTURE_PLACE_HOLDER);
    if (anIconCSSStyle != null) {
      theIcon.setStyleName(anIconCSSStyle);
    }

    final HTML theLink = new HTML(aLabel);

    // Set the click handlers.
    if (aHitoryTokenValue != null) {
      // Create the handler for the dynamic part of the widget.
      final ClickHandler theClickHandler = new ClickHandler() {
        public void onClick(ClickEvent aArg0) {
          History.newItem(aHitoryTokenValue);
        }
      };
      theLink.addClickHandler(theClickHandler);
      theIcon.addClickHandler(theClickHandler);
    }
    // Finally layout widgets.
    myInnerPanel.add(theIcon);
    myInnerPanel.add(theLink);

    // Wrap the inner panel into a decorated panel to allow rounded corner
    // design creation.
    myOuterPanel.setWidget(myInnerPanel);
    myOuterPanel.setStylePrimaryName("bos_navigation_link");
    this.initWidget(myOuterPanel);

    // Manage web browser history.
    History.addValueChangeHandler(this);
    final String theToken = History.getToken();
    setSelected(theToken!=null && theToken.equals(aHitoryTokenValue));
  }

  /**
   * Defines whether the label is selected or not.
   * 
   * @param isSelected
   */
  public void setSelected(boolean isSelected) {
    // Associate a css class name.
    if (isSelected) {
      myOuterPanel.addStyleDependentName(SELECTED_SUFFIX_STYLE);
    } else {
      myOuterPanel.removeStyleDependentName(SELECTED_SUFFIX_STYLE);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com
   * .google.gwt.event.logical.shared.ValueChangeEvent)
   */
  public void onValueChange(ValueChangeEvent<String> aEvent) {
    // History token has changed.
    setSelected((aEvent.getValue()!=null && aEvent.getValue().equals(myHitoryTokenValue)));
  }
}
