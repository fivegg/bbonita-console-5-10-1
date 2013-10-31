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
package org.bonitasoft.console.client.view.cases;

import java.util.Collection;

import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.categories.CategoryDataSourceImpl;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSourceImpl;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSourceImpl;
import org.bonitasoft.console.client.view.ItemFilterEditor;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseFilterEditor extends ItemFilterEditor<CaseFilter> {

  protected static final String TRUE = Boolean.TRUE.toString();
  protected static final String FALSE = Boolean.FALSE.toString();

  protected final ListBox myShowHistoryLB = new ListBox();
  protected final LabelDataSource myLabelDataSource;
  protected final ProcessDataSource myProcessDataSource = new ProcessDataSourceImpl(myMessageDataSource);
  protected final UserDataSource myUserDataSource = new UserDataSourceImpl(myMessageDataSource);
  protected final CategoryDataSource myCategoryDataSource = new CategoryDataSourceImpl(myMessageDataSource);

  /**
   * Default constructor.
   * 
   * @param aFilter
   * @param aMessageDataSource
   * @param aLabelDataSource
   * @param aCategoryDataSource 
   * @param aUserDataSource 
   * @param aProcessDataSource 
   */
  public CaseFilterEditor(MessageDataSource aMessageDataSource, CaseFilter aFilter, LabelDataSource aLabelDataSource) {
    super(aMessageDataSource, aFilter);
    myLabelDataSource = aLabelDataSource;
    aFilter.addModelChangeListener(ItemFilter.SEARCH_IN_HISTORY_PROPERTY, this);
    aFilter.addModelChangeListener(CaseFilter.LABEL_PROPERTY, this);
    initContent();
  }

  protected void initContent() {
    myShowHistoryLB.setStylePrimaryName("bos_item_filter_history");
    myShowHistoryLB.addItem(constants.showOpenCases(), FALSE);
    myShowHistoryLB.addItem(constants.showHistorizedCases(), TRUE);

    final FlowPanel theWrapper = new FlowPanel();
    theWrapper.setStylePrimaryName(FILTER_ELEMENT_STYLE);
    theWrapper.add(myShowHistoryLB);
    myFilterContentPanel.add(theWrapper);
    myShowHistoryLB.addChangeHandler(new ChangeHandler() {

      public void onChange(ChangeEvent aEvent) {
        myFilter.setSearchInHistory(Boolean.parseBoolean(myShowHistoryLB.getValue(myShowHistoryLB.getSelectedIndex())));
        myChanges.fireModelChange(FILTER_UPDATED_PROPERTY, null, myFilter);
      }
    });
    if (myFilter.getLabel() != null && myFilter.getLabel().equals(LabelModel.INBOX_LABEL.getUUID())) {
      myShowHistoryLB.setVisible(false);
    } else {
      myShowHistoryLB.setVisible(true);
    }
  }

  @SuppressWarnings("unchecked")
  public void modelChange(ModelChangeEvent anEvt) {
    if (ItemFilter.SEARCH_IN_HISTORY_PROPERTY.equals(anEvt.getPropertyName())) {
      boolean theNewSearchInHistoryValue =(Boolean) anEvt.getNewValue(); 
      if (theNewSearchInHistoryValue) {
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
      // update local filter
      myFilter.setSearchInHistory(theNewSearchInHistoryValue);
    } else if (CaseFilter.LABEL_PROPERTY.equals(anEvt.getPropertyName())) {
      final LabelUUID theLabelUUID = ((CaseFilter) anEvt.getSource()).getLabel();
      if (theLabelUUID != null) {
        final LabelModel theLabelModel = myLabelDataSource.getLabel(theLabelUUID);
        if (theLabelModel != null) {
          myShowHistoryLB.setVisible(theLabelModel.equals(LabelModel.ALL_LABEL) || theLabelModel.equals(LabelModel.MY_CASES_LABEL));
        } else {
          myShowHistoryLB.setVisible(false);
        }
      } else {
        myShowHistoryLB.setVisible(true);
      }
      // update local filter
      myFilter.setLabels((Collection<LabelUUID>)anEvt.getNewValue());
    }
  }

}
