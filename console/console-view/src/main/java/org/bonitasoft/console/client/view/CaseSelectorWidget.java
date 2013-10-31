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

import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.model.cases.CaseSelection.CaseSelector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseSelectorWidget extends I18NComposite implements ModelChangeListener {

  private static final String PARTIAL_SELECTION = "partial-selection";
  protected final CheckBox myCheckBox = new CheckBox();
  protected final CustomMenuBar myMenubar = new CustomMenuBar();
  protected final List<String> mySelectorChoices;
  protected final CaseSelection myItemSelection;
  protected final CaseDataSource myDataSource;

  /**
   * Default constructor.
   */
  public CaseSelectorWidget(CaseDataSource aDataSource) {
    super();
    myItemSelection = aDataSource.getCaseSelection();
    myDataSource = aDataSource;
    mySelectorChoices = buildChoices();
    myItemSelection.addModelChangeListener(CaseSelection.ITEM_SELECTION_PROPERTY, this);
    initContent();

    myMenubar.addStyleName("bos_item_selector");
    initWidget(myMenubar);
  }

  /**
   * @return
   */
  protected List<String> buildChoices() {
    final List<String> theChoices = new ArrayList<String>();
    for (CaseSelector theSelector : CaseSelector.values()) {
      theChoices.add(theSelector.name());
    }
    return theChoices;
  }

  private void initContent() {
    myCheckBox.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        if (myCheckBox.getValue()) {
          myItemSelection.select(CaseSelector.All);
        } else {
          myItemSelection.select(CaseSelector.None);
        }
        aEvent.stopPropagation();

      }
    });

    final MenuChoicesPanel theSubMenuChoices = buildSubmenuChoices();

    myMenubar.addItem(myCheckBox, theSubMenuChoices);
  }

  protected MenuChoicesPanel buildSubmenuChoices() {
    final MenuChoicesPanel theSubMenuChoices = new MenuChoicesPanel();
    for (final String theSelectorChoice : mySelectorChoices) {
      theSubMenuChoices.addChoice(theSubMenuChoices.new MenuChoice(new Label(theSelectorChoice), new Command() {

        public void execute() {
          myItemSelection.select(CaseSelector.valueOf(theSelectorChoice));
        }
      }));
    }
    return theSubMenuChoices;
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org.bonitasoft.console.client.events.ModelChangeEvent)
   */
  public void modelChange(ModelChangeEvent aEvt) {
    if(CaseSelection.ITEM_SELECTION_PROPERTY.equals(aEvt.getPropertyName())){
      if (myItemSelection.getSize() > 0) {
        // Check the box if necessary.
        myCheckBox.setValue(true);
        if(myItemSelection.getSize()==myDataSource.getVisibleItems().size()) {
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
