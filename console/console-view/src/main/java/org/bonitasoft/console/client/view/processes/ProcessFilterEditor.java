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
package org.bonitasoft.console.client.view.processes;

import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.ProcessFilter;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.view.ItemFilterEditor;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessFilterEditor extends ItemFilterEditor<ProcessFilter> {

  protected static final String TRUE = Boolean.TRUE.toString();
  protected static final String FALSE = Boolean.FALSE.toString();

  protected final ListBox myShowHistoryLB = new ListBox();

  /**
   * Default constructor.
   * 
   * @param aFilter
   * @param aMessageDataSource
   */
  public ProcessFilterEditor(MessageDataSource aMessageDataSource, ProcessFilter aFilter) {
    super(aMessageDataSource, aFilter);
    myFilter.addModelChangeListener(ItemFilter.SEARCH_IN_HISTORY_PROPERTY, this);
    initContent();
  }

  protected void initContent() {
    myShowHistoryLB.setStylePrimaryName("bos_item_filter_history");
    myShowHistoryLB.addItem(constants.showOpenProcesses(), FALSE);
    myShowHistoryLB.addItem(constants.showHistorizedProcesses(), TRUE);

    myFilterContentPanel.add(myShowHistoryLB);
    myShowHistoryLB.addChangeHandler(new ChangeHandler() {

      public void onChange(ChangeEvent aEvent) {
        myFilter.setSearchInHistory(Boolean.parseBoolean(myShowHistoryLB.getValue(myShowHistoryLB.getSelectedIndex())));
        myChanges.fireModelChange(FILTER_UPDATED_PROPERTY, null, myFilter);
      }
    });

  }

  public void modelChange(ModelChangeEvent anEvt) {
    if (ItemFilter.SEARCH_IN_HISTORY_PROPERTY.equals(anEvt.getPropertyName())) {
      if ((Boolean) anEvt.getNewValue()) {
        for (int i = 0; i < myShowHistoryLB.getItemCount(); i++) {
          if (myShowHistoryLB.getValue(i).equalsIgnoreCase(TRUE)) {
            myShowHistoryLB.setSelectedIndex(i);
          }
        }

      } else {
        for (int i = 0; i < myShowHistoryLB.getItemCount(); i++) {
          if (myShowHistoryLB.getValue(i).equalsIgnoreCase(FALSE)) {
            myShowHistoryLB.setSelectedIndex(i);
          }
        }
      }
    }
  }

}
