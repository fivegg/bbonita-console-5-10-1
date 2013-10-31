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

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;
import org.bonitasoft.console.client.model.ItemSelection;
import org.bonitasoft.console.client.model.ItemSelection.ItemSelector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class SimpleSelectorWidget<U extends BonitaUUID, I extends Item, F extends ItemFilter> extends I18NComposite implements ModelChangeListener {

  private static final String PARTIAL_SELECTION = "partial-selection";
  protected final CheckBox myCheckBox = new CheckBox();
  protected final CustomMenuBar myMenubar = new CustomMenuBar();
  protected final List<String> mySelectorChoices;
  protected final ItemSelection<U> myItemSelection;
  protected final BonitaFilteredDataSource<U, I, F> myDataSource;

  /**
   * Default constructor.
   */
  public SimpleSelectorWidget(BonitaFilteredDataSource<U, I, F> aDataSource) {
    super();
    myItemSelection = aDataSource.getItemSelection();
    myDataSource = aDataSource;
    mySelectorChoices = new ArrayList<String>();
    for (ItemSelector theSelector : ItemSelector.values()) {
      mySelectorChoices.add(theSelector.name());
    }
    myItemSelection.addModelChangeListener(ItemSelection.ITEM_SELECTION_PROPERTY, this);
    initContent();

    myMenubar.addStyleName("bos_item_selector");
    initWidget(myMenubar);
  }

  private void initContent() {
    myCheckBox.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        if (myCheckBox.getValue()) {
          myItemSelection.select(ItemSelector.All);
        } else {
          myItemSelection.select(ItemSelector.None);
        }
        aEvent.stopPropagation();

      }
    });

    final MenuChoicesPanel theSubMenuChoices = new MenuChoicesPanel();
    for (final String theSelectorChoice : mySelectorChoices) {
      theSubMenuChoices.addChoice(theSubMenuChoices.new MenuChoice(new Label(theSelectorChoice), new Command() {

        public void execute() {
          myItemSelection.select(ItemSelector.valueOf(theSelectorChoice));
        }
      }));
    }

    myMenubar.addItem(myCheckBox, theSubMenuChoices);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
   * .bonitasoft.console.client.events.ModelChangeEvent)
   */
  public void modelChange(ModelChangeEvent aEvt) {
    if (ItemSelection.ITEM_SELECTION_PROPERTY.equals(aEvt.getPropertyName())) {
      if (myItemSelection.getSize() > 0) {
        // Check the box if necessary.
        myCheckBox.setValue(true);
        if (myItemSelection.getSize() == myDataSource.getVisibleItems().size()) {
          // Complete selection.
          myCheckBox.removeStyleDependentName(PARTIAL_SELECTION);
        } else {
          // Partial selection.
          myCheckBox.addStyleDependentName(PARTIAL_SELECTION);
        }
      } else {
        myCheckBox.setValue(false);
        myCheckBox.removeStyleDependentName(PARTIAL_SELECTION);
      }

    }
  }
}
