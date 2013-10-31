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
package org.bonitasoft.console.client.view.categories;

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * This widget display a
 * {@link org.bonitasoft.console.client.categories.Category}.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class CategoryViewer extends Composite implements ModelChangeListener {

  private static final int MAX_LENGTH = 15;
  private static final String ROUNDED_CORNER_SUFFIX_STYLE_NAME = "_container";
  private BonitaProcessUUID myProcessUUID;
  protected final HorizontalPanel myOuterPanel;
  final private HashMap<CategoryUUID, DecoratorPanel> myCategoryTable = new HashMap<CategoryUUID, DecoratorPanel>();
  protected final ProcessDataSource myProcessDataSource;
  protected BonitaProcess myProcess;
  protected final CategoryDataSource myCategoryDataSource;

  /**
   * Default constructor.
   * 
   * @param aDataSource
   * @param aCaseItem
   * @param isDeletable
   */
  public CategoryViewer(final BonitaProcessUUID aProcessUUID, final ProcessDataSource aProcessDataSource, final CategoryDataSource aCategoryDataSource) {

    myProcessUUID = aProcessUUID;
    myProcessDataSource = aProcessDataSource;
    myCategoryDataSource = aCategoryDataSource;

    // Create an empty horizontal panel to layout. It will be filled in
    // later.
    myOuterPanel = new HorizontalPanel();
    myOuterPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    myOuterPanel.setSpacing(5);
    myOuterPanel.setStyleName("label_viewer_readonly");
    
    myProcessDataSource.getItem(myProcessUUID, new AsyncHandler<BonitaProcess>() {
      public void handleFailure(Throwable aT) {

      }

      public void handleSuccess(BonitaProcess aResult) {
        myProcess = aResult;
        myProcess.addModelChangeListener(BonitaProcess.CATEGORIES_PROPERTY, CategoryViewer.this);

        final List<String> theCategories = myProcess.getCategoriesName();
        myCategoryDataSource.getVisibleItemsByName(theCategories, new AsyncHandler<List<Category>>() {
          public void handleFailure(Throwable aT) {
            myCategoryTable.clear();
            fillInTable();
          }

          public void handleSuccess(List<Category> aResult) {
            myCategoryTable.clear();
            for (Category theCategory : aResult) {
              theCategory.addModelChangeListener(Category.CSS_CLASS_NAME_PROPERTY, CategoryViewer.this);
              myCategoryTable.put(theCategory.getUUID(), buildCategoryTable(theCategory));
            }

            fillInTable();
          }
        });
      }
    });

    initWidget(myOuterPanel);
  }

  /**
   * Fill in the table with the labels associated to the case.
   */
  private void fillInTable() {
    // Firstly clean up panel.
    myOuterPanel.clear();
    for (DecoratorPanel theCategoryPanel : myCategoryTable.values()) {
      myOuterPanel.add(theCategoryPanel);
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
    if (Category.CSS_CLASS_NAME_PROPERTY.equals(anEvent.getPropertyName())) {
      Category theCategory = ((Category) anEvent.getSource());
      DecoratorPanel theContainer = myCategoryTable.get(theCategory.getUUID());
      if (theContainer != null) {
        theContainer.setStylePrimaryName((String) anEvent.getNewValue() + ROUNDED_CORNER_SUFFIX_STYLE_NAME);
        theContainer.getWidget().setStylePrimaryName((String) anEvent.getNewValue());
      }
    } else if (BonitaProcess.CATEGORIES_PROPERTY.equals(anEvent.getPropertyName())) {
      myCategoryDataSource.getItemsByName((List<String>) anEvent.getNewValue(), new AsyncHandler<List<Category>>() {
        public void handleFailure(Throwable aT) {
          myCategoryTable.clear();
          fillInTable();
        }

        public void handleSuccess(List<Category> aResult) {
          myCategoryTable.clear();
          for (Category theCategory : aResult) {
            theCategory.addModelChangeListener(Category.CSS_CLASS_NAME_PROPERTY, CategoryViewer.this);
            myCategoryTable.put(theCategory.getUUID(), buildCategoryTable(theCategory));
          }

          fillInTable();
        }
      });
    }

  }

  /**
   * @param aValue
   * @return
   */
  private String buildShortName(String aName) {
    if (aName != null && aName.length() > MAX_LENGTH) {
      return aName.substring(0, MAX_LENGTH) + "...";
    } else {
      return aName;
    }
  }

  protected DecoratorPanel buildCategoryTable(final Category aCategory) {
    final DecoratorPanel theContainer = new DecoratorPanel();
    // Create the layout container.
    final HorizontalPanel theTable = new HorizontalPanel();
    final Label theCategoryName = new Label(buildShortName(aCategory.getName()));
    theCategoryName.setTitle(aCategory.getName());
    theTable.add(theCategoryName);
    final String theCSSStyle;
    if(aCategory.getCSSStyleName()!=null){
      theCSSStyle = aCategory.getCSSStyleName();
    } else {
      theCSSStyle = Category.DEFAULT_CSS_STYLE;
    }
    theTable.setStylePrimaryName(theCSSStyle);
    theContainer.add(theTable);
    theContainer.setStylePrimaryName(theCSSStyle + ROUNDED_CORNER_SUFFIX_STYLE_NAME);
    return theContainer;
  }
}
