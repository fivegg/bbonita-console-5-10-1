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
package org.bonitasoft.console.client.view.steps;

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.model.ItemSelection.ItemSelector;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.steps.StepDefinitionDataSource;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.steps.StepDefinition;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.ItemFilterEditor;
import org.bonitasoft.console.client.view.ItemSelectionWidget;
import org.bonitasoft.console.client.view.SimpleFilterEditor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepsDefinitionListWidget extends AbstractItemList<StepUUID, StepDefinition, StepFilter> {

  protected CustomDialogBox addUpdateItemDialogBox;
  protected final HashMap<StepUUID, ItemSelectionWidget<StepUUID>> myItemSelectionWidgets = new HashMap<StepUUID, ItemSelectionWidget<StepUUID>>();
  protected final HashMap<StepUUID, Widget> myItemNameWidgets = new HashMap<StepUUID, Widget>();
  protected final HashMap<StepUUID, Widget> myItemLabelWidgets = new HashMap<StepUUID, Widget>();

  /**
   * Default constructor.
   * 
   * @param aRoleDataSource
   */
  public StepsDefinitionListWidget(MessageDataSource aMessageDataSource, StepDefinitionDataSource aStepDataSource) {
    super(aMessageDataSource, aStepDataSource.getItemSelection(), aStepDataSource, 20, 20, 3);
    myBonitaDataSource.addModelChangeListener(StepDefinitionDataSource.ITEM_LIST_PROPERTY, this);
    newerButtonTop.setHTML(constants.previousPageLinkLabel());
    olderButtonTop.setHTML(constants.nextPageLinkLabel());
    newerButtonBottom.setHTML(constants.previousPageLinkLabel());
    olderButtonBottom.setHTML(constants.nextPageLinkLabel());

    initView();
    createWidgetsForItemsAndDisplay();
    addUpdateItemDialogBox = new CustomDialogBox(false, true);

  }

  @Override
  protected FlowPanel buildBottomNavBar() {
    return buildTopNavBar();

  }

  @Override
  protected FlowPanel buildTopNavBar() {
    final FlowPanel theFirstCell = new FlowPanel();
    final HorizontalPanel theMenuPanel = new HorizontalPanel();

    Label theRefreshLink = new Label(constants.refresh());
    theRefreshLink.setStylePrimaryName(CSSClassManager.LINK_LABEL);
    theRefreshLink.addClickHandler(new ClickHandler() {
      
      public void onClick(ClickEvent aEvent) {
        myBonitaDataSource.reload();
      }
    });
    
 // Create the Selector Widget
    HorizontalPanel theSelectorWidget = new HorizontalPanel();
    theSelectorWidget.add(new Label(constants.itemSelector()));
    Label theLinkLabel;
    for (final ItemSelector aSelector : ItemSelector.values()) {
      theLinkLabel = new Label(LocaleUtil.translate(aSelector));
      theLinkLabel.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent arg0) {
          myItemSelection.select(aSelector);
        }
      });
      theLinkLabel.setStyleName(CSSClassManager.LINK_LABEL);
      
      theSelectorWidget.add(theLinkLabel);
    }

    theSelectorWidget.setSpacing(3);
    
    theMenuPanel.add(theRefreshLink);
    DOM.setElementPropertyInt(theMenuPanel.getElement(), "cellPadding", 3);
    
    theFirstCell.add(theMenuPanel);
    theFirstCell.add(theSelectorWidget);
        
    return theFirstCell;
  }


  @Override
  protected void createWidgetsForItemsAndDisplay() {
    // Do not create object. Use HTML instead.
    myVisibleItems = ((StepDefinitionDataSource) myBonitaDataSource).getVisibleItems();
    if (myVisibleItems != null) {
      hideLoading();
      myInnerTable.removeStyleName(LOADING_STYLE);
      for (StepUUID theItemUUID : myVisibleItems) {
        if (!myItemTableRow.containsKey(theItemUUID)) {
          // theItem.addModelChangeListener(CaseItem.LABELS_PROPERTY, this);
          createWidgetsForItem(theItemUUID);
        }
      }
      // Update the UI.
      update(myVisibleItems);
    } else {
      displayLoading();
      myInnerTable.addStyleName(LOADING_STYLE);
    }

  }

  protected void createWidgetsForItem(StepUUID anItem) {
    StepDefinition theItem = myBonitaDataSource.getItem(anItem);
    myItemSelectionWidgets.put(anItem, new ItemSelectionWidget<StepUUID>(myItemSelection, anItem));
    myItemNameWidgets.put(anItem, new Label(theItem.getName()));
    myItemLabelWidgets.put(anItem, new Label(theItem.getLabel()));
  }

  public void notifyItemClicked(StepUUID anItem, final ClickEvent anEvent) {
    //Nothing to do.
  }

  @Override
  protected void update(List<StepUUID> anItemList) {

    updateListSize(anItemList);

    if (myTopNavBar!=null && !myTopNavBar.isAttached()) {
      // Create the navigation row (Top).
      myInnerTable.setWidget(0, 0, myTopNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(0, 0, myColumnNumber);
      myInnerTable.getRowFormatter().setStyleName(0, "identity_list_navbar");
    }

    // Add the column titles
    myInnerTable.setWidget(1, 1, new Label(constants.name()));
    myInnerTable.setWidget(1, 2, new Label(constants.label()));

    // Set CSS style.
    myInnerTable.getRowFormatter().setStylePrimaryName(1, "identity_list_content_row_title");

    fillInContentRow(anItemList);

    if (myBottomNavBar!=null && !myBottomNavBar.isAttached()) {
      // Create the navigation row (Bottom).
      int theBottomNavBarPosition = myInnerTable.getRowCount();
      myInnerTable.setWidget(theBottomNavBarPosition, 0, myBottomNavBar);
      myInnerTable.getFlexCellFormatter().setColSpan(theBottomNavBarPosition, 0, myColumnNumber);
      myInnerTable.getRowFormatter().setStyleName(theBottomNavBarPosition, "identity_list_navbar");
    }
  }

  protected void fillInContentRow(List<StepUUID> anItemList) {

    int theRowOffset = 2;
    int nbItemDisplayed = 0;
    int theCurrentRowIndex = theRowOffset + nbItemDisplayed;
    StepDefinition theItem;
    for (StepUUID theItemUUID : anItemList) {
      theCurrentRowIndex = theRowOffset + nbItemDisplayed;

      theItem = myBonitaDataSource.getItem(theItemUUID);
      // Add a new row to the table, then set each of its columns.
      // layout widgets
      myInnerTable.setWidget(theCurrentRowIndex, 0, myItemSelectionWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 1, myItemNameWidgets.get(theItem.getUUID()));
      myInnerTable.setWidget(theCurrentRowIndex, 2, myItemLabelWidgets.get(theItem.getUUID()));

      // Set CSS style.
      myInnerTable.getRowFormatter().setStylePrimaryName(theCurrentRowIndex, "identity_list_content_row");
      myInnerTable.getRowFormatter().getElement(theCurrentRowIndex).setTitle(getContentRowTooltip());
      // Keep link between the item and the row.
      linkItemWithRow(theItem.getUUID(), theCurrentRowIndex);

      nbItemDisplayed++;
    }

    fillWithEmptyRows(theRowOffset, theRowOffset + nbItemDisplayed, myColumnNumber);

  }


  protected String getContentRowTooltip() {
    return constants.clickToEdit();
  }

  @Override
  public void modelChange(ModelChangeEvent anEvent) {
    // The event may come from a subscription made by my super class.
    super.modelChange(anEvent);
    if (StepDefinitionDataSource.ITEM_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
      createWidgetsForItemsAndDisplay();
    }
    if (SimpleFilterEditor.FILTER_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
      final String theSearchPattern = ((StepFilter) anEvent.getNewValue()).getSearchPattern();
      final BonitaProcessUUID theProcessUUID = ((StepFilter) anEvent.getNewValue()).getProcessUUID();
      myBonitaDataSource.getItemFilter().setSearchPattern(theSearchPattern);
      myBonitaDataSource.getItemFilter().setProcessUUID(theProcessUUID);
      myBonitaDataSource.reload();
    }
  }

  @Override
  protected ItemFilterEditor<StepFilter> buildFilterEditor() {
//    SimpleFilterEditor<StepFilter> theEditor = new SimpleFilterEditor<StepFilter>(myMessageDataSource, myBonitaDataSource.getItemFilter(), constants.filterStepsToolTip());
//    theEditor.addModelChangeListener(SimpleFilterEditor.FILTER_UPDATED_PROPERTY, this);
//    return theEditor;
    return null;
  }
}
