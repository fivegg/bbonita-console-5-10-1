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

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.console.client.cases.CasesConfiguration;
import org.bonitasoft.console.client.cases.CasesConfiguration.Columns;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.forms.client.view.common.URLUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseListConfigurationEditionPanel extends SetupPanel {

  private static final String GRID_HEADER_STYLE = "bos_table_header";
  private static final String EVEN_ROW_STYLE = "bos_even_row";
  private static final String ODD_ROW_STYLE = "bos_odd_row";
  private static final String GRID_COLUMN_COL_STYLE = "bos_position_col";
  private static final String GRID_CONTENT_COL_STYLE = "bos_content_col";
  protected final FlowPanel myOuterPanel;
  protected final CustomMenuBar mySaveButton = new CustomMenuBar();
  protected final CaseDataSource myCaseDataSource;
  protected CasesConfiguration myNewConfiguration;
  protected AsyncHandler<CasesConfiguration> myConfigurationHandler;
  protected final HashMap<Integer, ListBox> myColumnIndexSelectors = new HashMap<Integer, ListBox>();
  protected ListBox myStretchedColumnSelector;

  public CaseListConfigurationEditionPanel(CaseDataSource aDataSource, MessageDataSource aMessageDataSource) {
    super(aMessageDataSource);
    myCaseDataSource = aDataSource;
    myOuterPanel = new FlowPanel();
    myOuterPanel.setStylePrimaryName(DEFAULT_CSS_STYLE);
    myOuterPanel.addStyleName("bos_case_list_configuration_panel");
    initWidget(myOuterPanel);
  }

  @Override
  public String getLocationLabel() {
      return constants.caseListsConfigurationTabName();
  }    
  
  protected void buildContent() {

    // Create the save button.
    mySaveButton.addItem(constants.save(), new Command() {
      public void execute() {
        saveSettings();
      }
    });

    myOuterPanel.add(new HTML(constants.caseListConfigurationTabDescription()));
    ListBox theListBox;
    int theRowIndex = 0;
    final Grid theSelectorsLayout = new Grid(Columns.values().length + 2, 2);
    theSelectorsLayout.setHTML(theRowIndex, 0, constants.caseListsColumnIndexHeader());
    theSelectorsLayout.setHTML(theRowIndex, 1, constants.caseListsColumnContentHeader());
    theSelectorsLayout.getRowFormatter().setStylePrimaryName(theRowIndex, GRID_HEADER_STYLE);
    theSelectorsLayout.getColumnFormatter().setStylePrimaryName(0, GRID_COLUMN_COL_STYLE);
    theSelectorsLayout.getColumnFormatter().setStylePrimaryName(1, GRID_CONTENT_COL_STYLE);
    theRowIndex++;
    for (@SuppressWarnings("unused")
    Columns theColumn : Columns.values()) {
      theListBox = buildListBoxColumnSelection(theRowIndex - 1);
      myColumnIndexSelectors.put(theRowIndex - 1, theListBox);

      theSelectorsLayout.setHTML(theRowIndex, 0, patterns.caseListsColumn(theRowIndex));
      theSelectorsLayout.setWidget(theRowIndex, 1, theListBox);
      if (theRowIndex % 2 == 0) {
        theSelectorsLayout.getRowFormatter().setStylePrimaryName(theRowIndex, EVEN_ROW_STYLE);
      } else {
        theSelectorsLayout.getRowFormatter().setStylePrimaryName(theRowIndex, ODD_ROW_STYLE);
      }

      theRowIndex++;
    }

    buildListBoxStretchedColumnSelection();

    theSelectorsLayout.setHTML(theRowIndex, 0, constants.caseListsColumnStretchDescription());
    theSelectorsLayout.setWidget(theRowIndex, 1, myStretchedColumnSelector);

    myOuterPanel.add(theSelectorsLayout);
    myOuterPanel.add(mySaveButton);

  }

  protected ListBox buildListBoxStretchedColumnSelection() {
    myStretchedColumnSelector = new ListBox();
    myStretchedColumnSelector.addItem("", "-1");
    for (int i = 1; i <= Columns.values().length; i++) {
      myStretchedColumnSelector.addItem(String.valueOf(i), String.valueOf(i - 1));
    }
    
    myStretchedColumnSelector.addChangeHandler(new ChangeHandler() {

      public void onChange(ChangeEvent aEvent) {
        String theIndex = myStretchedColumnSelector.getValue(myStretchedColumnSelector.getSelectedIndex());
        myNewConfiguration.setStretchedColumnIndex(Integer.parseInt(theIndex));
      }
    });
    return myStretchedColumnSelector;
  }

  /**
   * @param aColumn
   * @return
   */
  private ListBox buildListBoxColumnSelection(final Integer anIndex) {
    final ListBox theListBox = new ListBox();
    theListBox.addItem("");
    for (Columns theColumn : Columns.values()) {
      theListBox.addItem(LocaleUtil.getColumnTitle(theColumn), theColumn.name());
    }

    theListBox.addChangeHandler(new ChangeHandler() {

      public void onChange(ChangeEvent aEvent) {
        if (theListBox.getSelectedIndex() > -1) {
          final String theColumnName = theListBox.getValue(theListBox.getSelectedIndex());
          try {
            if ("".equals(theColumnName)) {
              myNewConfiguration.setColumnIndex(null, anIndex);
            } else {
              Columns theColumn = Columns.valueOf(theColumnName);
              final int theCurrentPosition = myNewConfiguration.getColumnIndex(theColumn);
              if (theCurrentPosition >= 0) {
                boolean mustContinue = Window.confirm(patterns.caseListConfigurationColumnIndexConflictsWith(String.valueOf(theCurrentPosition + 1)));
                if (mustContinue) {
                  myNewConfiguration.setColumnIndex(theColumn, anIndex);
                  myColumnIndexSelectors.get(theCurrentPosition).setSelectedIndex(0);
                } else {
                  theColumn = myNewConfiguration.getColumnAt(anIndex);
                  if (theColumn != null) {
                    myColumnIndexSelectors.get(anIndex).setSelectedIndex(theColumn.ordinal() + 1);
                  } else {
                    myColumnIndexSelectors.get(anIndex).setSelectedIndex(0);
                  }
                }
              } else {
                myNewConfiguration.setColumnIndex(theColumn, anIndex);
              }
            }

          } catch (Exception e) {
            GWT.log("Unable to update the column index", e);
          }
        }
      }
    });

    return theListBox;
  }

  private void saveSettings() {
    if (myConfigurationHandler == null) {
      buildConfigurationHandler();
    }
    myCaseDataSource.updateConfiguration(myNewConfiguration, myConfigurationHandler);
  }

  protected RedirectButtonWidget buildRedirectButton(BonitaProcess aProcess, String aURL) {
    Map<String, String> theURLParams = buildURLParams();
    Map<String, String> theURLHashParams = buildURLHashParams(aProcess);
    return new RedirectButtonWidget(aURL, constants.defaultApplicationFormWindowName(), theURLParams, theURLHashParams, constants.redirectButtonTitle());
  }

  private Map<String, String> buildURLParams() {
    Map<String, String> urlParamsMap = new HashMap<String, String>();

    urlParamsMap.put(URLUtils.LOCALE_PARAM, LocaleInfo.getCurrentLocale().getLocaleName());
    
    return urlParamsMap;
  }
  
  private Map<String, String> buildURLHashParams(BonitaProcess aProcess) {
      Map<String, String> urlHashParamsMap = new HashMap<String, String>();

      urlHashParamsMap.put(URLUtils.VIEW_MODE_PARAM, URLUtils.FULL_FORM_APPLICATION_MODE);

      urlHashParamsMap.put(URLUtils.PROCESS_ID_PARAM, aProcess.getUUID().getValue());
      
      return urlHashParamsMap;
    }

  @Override
  public void updateContent() {
    if (myConfigurationHandler == null) {
      buildConfigurationHandler();
    }
    myCaseDataSource.getConfiguration(myConfigurationHandler);
  }

  private void buildConfigurationHandler() {
    myConfigurationHandler = new AsyncHandler<CasesConfiguration>() {

      public void handleSuccess(CasesConfiguration aResult) {
        if (myNewConfiguration == null) {
          myNewConfiguration = new CasesConfiguration();
        }
        myNewConfiguration.update(aResult);
        ListBox theListBox;
        int thePosition;
        int theIndex = 0;
        for (Columns theColumn : Columns.values()) {
          thePosition = myNewConfiguration.getColumnIndex(theColumn);
          if (thePosition >= 0) {
            theListBox = myColumnIndexSelectors.get(thePosition);
            if (theListBox != null) {
              if (thePosition < theListBox.getItemCount()) {
                theListBox.setItemSelected(theIndex + 1, true);
              } else {
                GWT.log("Illegal index.", new IllegalArgumentException());
              }
            }
          }
          theIndex++;
        }
        // Update the stretched column selector.
        myStretchedColumnSelector.setSelectedIndex(myNewConfiguration.getStretchedColumnIndex() + 1);
      }

      public void handleFailure(Throwable aT) {
        GWT.log("Unable to read cases configuration", aT);
        myMessageDataSource.addInfoMessage(messages.unableToUpdateConfiguration());
      }
    };
  }
}
