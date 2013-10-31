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
package org.bonitasoft.console.client.view.categories;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoryWidget extends BonitaPanel implements ModelChangeListener {

  private static final String CATEGORY_WIDGET_STYLE = "bos_category_widget";
  private static final String SELECTED_SUFFIX_STYLE = "selected";
  

  private Category myCategory;
  private final Label myCategoryName = new Label();

  // Create the layout.
  private final DecoratorPanel myOuterPanel = new DecoratorPanel();
  private final HorizontalPanel myInnerPanel = new HorizontalPanel();
  protected CategoryDataSource myCategoryDataSource;
  private DecoratorPanel myColorPickerContainer;
  private Image myColorPicker;

  /**
   * 
   * Default constructor.
   * 
   * @param aLabel
   * @param selected
   */
  public CategoryWidget(CategoryDataSource aCategoryDataSource, Category aCategory, boolean selected) {
    super();
    myCategory = aCategory;
    myCategoryDataSource = aCategoryDataSource;

    myCategory.addModelChangeListener(Category.NAME_PROPERTY, this);
    myCategory.addModelChangeListener(Category.CSS_CLASS_NAME_PROPERTY, this);
    
    myOuterPanel.setStylePrimaryName(CATEGORY_WIDGET_STYLE);
    
    myInnerPanel.setStylePrimaryName(CSSClassManager.CONTENT_STYLE);
    
    // Associate a css class name.
    if (selected) {
      myOuterPanel.addStyleDependentName(SELECTED_SUFFIX_STYLE);
    } else {
      myOuterPanel.removeStyleDependentName(SELECTED_SUFFIX_STYLE);
    }

    // Create the handler for the dynamic part of the widget.
    final ClickHandler theClickHandler = new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see
       * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
       * .event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        History.newItem(ViewToken.CaseList + "/cat:" + myCategory.getUUID().getValue());
      }
    };

    // Create the widgets.
    final Image theLabelIcon = new Image(PICTURE_PLACE_HOLDER);
    theLabelIcon.setStyleName(myCategory.getIconCSSStyle());

    // Set the click handlers.
    myCategoryName.addClickHandler(theClickHandler);
    theLabelIcon.addClickHandler(theClickHandler);

    myColorPicker = new Image(PICTURE_PLACE_HOLDER);
    myColorPickerContainer = new DecoratorPanel();
    myColorPickerContainer.add(myColorPicker);
    
    // Finally layout widgets.
    myInnerPanel.add(myColorPickerContainer);
    myInnerPanel.add(myCategoryName);
    myInnerPanel.add(theLabelIcon);

    myInnerPanel.setStylePrimaryName(CSSClassManager.CONTENT_STYLE);
    
    // Wrap the inner panel into a decorated panel to allow rounded corner
    // design creation.
    myOuterPanel.setWidget(myInnerPanel);
    this.initWidget(myOuterPanel);

    updateContent();
  }

  protected void updateContent() {
    // Set text to display.
    String theLocalizedLabelName = myCategory.getName();
    if (theLocalizedLabelName.length() > 20) {
      theLocalizedLabelName = theLocalizedLabelName.substring(0, 20) + "...";
    }
    myCategoryName.setText(theLocalizedLabelName);
    myCategoryName.setTitle(patterns.browseToCategory(myCategory.getName()));
    final String theCSSStyle;
    if (myCategory.getCSSStyleName() != null) {
      theCSSStyle = myCategory.getPreviewCSSStyleName();
    } else {
      theCSSStyle = Category.DEFAULT_PREVIEW_CSS;
    }
    myColorPicker.setStyleName(theCSSStyle);
    myColorPickerContainer.setStylePrimaryName(theCSSStyle + "_container");
  }

  /**
   * Get the CSS style given to this widget by default.<br>
   * Useful if you want to reset the widget.
   * 
   * @return
   */
  public static String getDefaultStyleName() {
    return CATEGORY_WIDGET_STYLE;
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
   * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
   * .bonitasoft.console.client.events.ModelChangeEvent)
   */
  public void modelChange(ModelChangeEvent aEvt) {
    if (Category.NAME_PROPERTY.equals(aEvt.getPropertyName())) {
      updateContent();
    } else if (Category.CSS_CLASS_NAME_PROPERTY.equals(aEvt.getPropertyName())) {
      updateContent();
    }
  }
}
