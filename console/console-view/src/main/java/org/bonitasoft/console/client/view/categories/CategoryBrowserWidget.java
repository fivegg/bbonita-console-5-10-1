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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CategoryBrowserWidget extends BonitaPanel implements ModelChangeListener {

  /**
   * The LEFT_PANEL_SEPARATOR_CSS_STYLE_NAME defines
   */
  protected static final String LEFT_PANEL_SEPARATOR_CSS_STYLE_NAME = "left_panel_separator";
  protected static final String MORE_LABEL_KEY = constants.more();

  protected final CategoryDataSource myCategoryDataSource;
  protected final FlowPanel myOuterPanel = new FlowPanel();;

  private CategoryUUID mySelectedItemUUID;
  protected final HashMap<CategoryUUID, CategoryWidget> myCategoryWidgets = new HashMap<CategoryUUID, CategoryWidget>();

  protected final ArrayList<Category> myCategories = new ArrayList<Category>();

  public CategoryBrowserWidget(CategoryDataSource aCategoryDataSource) {
    super();
    myCategoryDataSource = aCategoryDataSource;
    myCategoryDataSource.addModelChangeListener(CategoryDataSource.VISIBLE_CATEGORIES_LIST_PROPERTY, this);
    myCategoryDataSource.addModelChangeListener(CategoryDataSource.ITEM_CREATED_PROPERTY, this);
    myCategoryDataSource.addModelChangeListener(CategoryDataSource.ITEM_DELETED_PROPERTY, this);
    this.initWidget(myOuterPanel);
    update();

  }

  protected void update() {
    myCategoryDataSource.getVisibleCategories(new AsyncHandler<List<Category>>() {
      /*
       * (non-Javadoc)
       * 
       * @see
       * org.bonitasoft.console.client.common.data.AsyncHandler#handleFailure
       * (java.lang.Throwable)
       */
      public void handleFailure(Throwable aT) {
        myOuterPanel.clear();
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.bonitasoft.console.client.common.data.AsyncHandler#handleSuccess
       * (java.lang.Object)
       */
      public void handleSuccess(List<Category> aResult) {
        if (aResult != null) {
          if (!myCategories.containsAll(aResult) || !aResult.containsAll(myCategories)) {
            myCategories.clear();
            myCategories.addAll(aResult);
            buildWidgetMap();
            fillinContent();
          }
        }
      }
    });

  }

  protected void fillinContent() {
    myOuterPanel.clear();
    if (myCategories != null) {
      int theNbOfDisplayedCategories = 0;
      for (Category theCategory : myCategories) {
        if (theNbOfDisplayedCategories < 5) {
          myOuterPanel.add(myCategoryWidgets.get(theCategory.getUUID()));
          theNbOfDisplayedCategories++;
        }
      }
    }
  }

  private void buildWidgetMap() {
    final HashMap<CategoryUUID, CategoryWidget> theExistingItems = new HashMap<CategoryUUID, CategoryWidget>();
    theExistingItems.putAll(myCategoryWidgets);
    myCategoryWidgets.clear();

    if (!myCategories.isEmpty()) {
      CategoryWidget theCategoryWidget;
      for (Category theCategory : myCategories) {
        // Reuse existing widgets.
        if (!theExistingItems.containsKey(theCategory.getUUID())) {
          theCategoryWidget = new CategoryWidget(myCategoryDataSource, theCategory, false);
          myCategoryWidgets.put(theCategory.getUUID(), theCategoryWidget);
        } else {
          myCategoryWidgets.put(theCategory.getUUID(), theExistingItems.get(theCategory.getUUID()));
        }
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
    if (CategoryDataSource.VISIBLE_CATEGORIES_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
      if (anEvent.getNewValue() != null) {
        ArrayList<Category> theNewCategories = (ArrayList<Category>) anEvent.getNewValue();
        if (theNewCategories == null || theNewCategories.isEmpty() || !myCategories.containsAll(theNewCategories) || !theNewCategories.containsAll(myCategories)) {
          // The state is not the one currently displayed.
          myCategories.clear();
          myCategories.addAll(theNewCategories);
          // build missing widgets.
          buildWidgetMap();
          if (mySelectedItemUUID != null && myCategoryWidgets.containsKey(mySelectedItemUUID)) {
            myCategoryWidgets.get(mySelectedItemUUID).setSelected(true);
          }
          fillinContent();
        }
      }
    } else if (CategoryDataSource.ITEM_CREATED_PROPERTY.equals(anEvent.getPropertyName())) {
      update();
      if (mySelectedItemUUID != null && myCategoryWidgets.containsKey(mySelectedItemUUID)) {
        myCategoryWidgets.get(mySelectedItemUUID).setSelected(true);
      }
    } else if (CategoryDataSource.ITEM_DELETED_PROPERTY.equals(anEvent.getPropertyName())) {
      update();
      if (mySelectedItemUUID != null && myCategoryWidgets.containsKey(mySelectedItemUUID)) {
        myCategoryWidgets.get(mySelectedItemUUID).setSelected(true);
      }
    }
  }

  public void setSelectedItem(Category aCategory) {
    if (aCategory != null) {
      mySelectedItemUUID = aCategory.getUUID();
    } else {
      mySelectedItemUUID = null;
    }
    for (CategoryUUID theCategoryUUID : myCategoryWidgets.keySet()) {
      Category theCategory = myCategoryDataSource.getItem(theCategoryUUID);
      if (theCategory != null) {
        if (theCategoryUUID.equals(mySelectedItemUUID)) {
          myCategoryWidgets.get(theCategoryUUID).setSelected(true);
        } else {
          myCategoryWidgets.get(theCategoryUUID).setSelected(false);
        }
      }
    }
  }

}
