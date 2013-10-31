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
package org.bonitasoft.console.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.events.ItemClickHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;
import org.bonitasoft.console.client.model.ItemSelection;
import org.bonitasoft.console.client.model.MessageDataSource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * This widget display a tabular view of all the steps a user should see. The
 * list depends on the selection of a label.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public abstract class AbstractItemList<U extends BonitaUUID, I extends Item, F extends ItemFilter> extends BonitaPanel implements ItemClickHandler<U>, ModelChangeListener {

  protected static final String ITEM_LIST_STYLE = "item_list";
  protected static final String ITEMS_LINK_STYLE = "items_link";
  protected static final String ITEM_LIST_NAVBAR_STYLE = "item_list_navbar";
  protected static final String ITEM_LIST_PAGE_NAVBAR_STYLE = "item_list_page_navbar";
  protected static final String ITEM_LIST_PAGE_BOTTOM_NAVBAR_STYLE = "item_list_page_bottom_navbar";
  protected static final String ITEM_LIST_CONTENT_ROW_TITLE_STYLE = "item_list_content_row_title";
  protected static final String ITEM_LIST_EMPTY_ROW_STYLE = "item_list_empty_row";
  protected static final String ITEM_LIST_SELECT_COLUMN_STYLE = "item_list_select_column";
  protected static final String ITEM_SELECTED_STYLE = "item_selected";
  protected static final String ITEM_LIST_CONTENT_ROW_STYLE = "item_list_content_row";
  protected static final String EVEN_STYLE_SUFFIX = "-even";
  /**
   * The NBSP defines
   */
  protected static final String NBSP = "&nbsp;";

  /*
   * The "Please wait while loading..." message
   */
  protected static final HTML PLEASE_WAIT_WHILE_LOADING = new HTML(constants.loading());

  /*
   * The maximum number of myCases displayed by the widget, i.e., number of
   * myCases per page.
   */
  protected static final int DEFAULT_MAX_DISPLAYED_ITEMS = constants.defaultMaxDisplayedItems();
  /*
   * The minimum number of rows displayed by the widget.
   */
  protected static final int DEFAULT_MINIMUM_DISPLAYED_ITEMS = constants.defaultMinDisplayedItems();
  
  

  protected final BonitaFilteredDataSource<U, I, F> myBonitaDataSource;

  protected FlowPanel myTopNavBar;
  protected FlowPanel myBottomNavBar;

  protected final HTML myCountLabelTop = new HTML();
  protected final HTML newestButtonTop = new HTML(constants.newest(), true);
  protected final HTML newerButtonTop = new HTML(constants.newer(), true);
  protected final HTML olderButtonTop = new HTML(constants.older(), true);
  protected final HTML oldestButtonTop = new HTML(constants.oldest(), true);

  protected final HTML myCountLabelBottom = new HTML();
  protected final HTML newestButtonBottom = new HTML(constants.newest(), true);
  protected final HTML newerButtonBottom = new HTML(constants.newer(), true);
  protected final HTML olderButtonBottom = new HTML(constants.older(), true);
  protected final HTML oldestButtonBottom = new HTML(constants.oldest(), true);

  protected final FlexTable myInnerTable = new FlexTable();
  protected final ItemSelection<U> myItemSelection;

  protected List<U> myVisibleItems;

  protected final HashMap<U, Integer> myItemTableRow = new HashMap<U, Integer>();
  protected final HashMap<Integer, U> myRowTableItem = new HashMap<Integer, U>();

  protected int myMinimalSize;

  protected int myMaximalSize;

  protected int myColumnNumber;

  protected MessageDataSource myMessageDataSource;
  protected ItemFilterEditor<F> myFilterEditor;

  /**
   * 
   * Default constructor.
   * 
   * @param anItemSelection
   * @param aBonitaDataSource
   * @param aLabelDataSource
   */
  public AbstractItemList(final MessageDataSource aMessageDataSource, final ItemSelection<U> anItemSelection, BonitaFilteredDataSource<U, I, F> aBonitaDataSource, int aMinimalSize, int aMaximalSize,
      int aNBOfColumn) {
    myMessageDataSource = aMessageDataSource;
    myItemSelection = anItemSelection;
    myBonitaDataSource = aBonitaDataSource;

    if (aMinimalSize < 0) {
      myMinimalSize = DEFAULT_MINIMUM_DISPLAYED_ITEMS;
    } else {
      myMinimalSize = aMinimalSize;
    }
    if (aMinimalSize < 0) {
      myMaximalSize = DEFAULT_MAX_DISPLAYED_ITEMS;
    } else {
      myMaximalSize = aMaximalSize;
    }

    myColumnNumber = aNBOfColumn;

    // Listen changes on the list.
    myItemSelection.addModelChangeListener(ItemSelection.ITEM_SELECTION_PROPERTY, this);

    // Setup the table.
    initInnerTable();

    // Finally layout widget.
    this.initWidget(myInnerTable);
    setStylePrimaryName(ITEM_LIST_STYLE);
  }

  protected void initView() {
    // Create the 'navigation' bar at the upper-right.
    myTopNavBar = buildTopNavBar();
    myTopNavBar.setStyleName(ITEM_LIST_NAVBAR_STYLE);
    HorizontalPanel thePageNavigator = new HorizontalPanel();
    thePageNavigator.setStyleName(ITEM_LIST_PAGE_NAVBAR_STYLE);
    // Create navigation handler.
    newestButtonTop.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        // FIXME should use the HyperLink feature and let the ViewController
        // handle the reload.
        myBonitaDataSource.getItemFilter().setStartingIndex(0);
        myItemSelection.clearSelection();
        myBonitaDataSource.reload();
      }
    });

    newerButtonTop.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        int theStartingIndex = myBonitaDataSource.getItemFilter().getStartingIndex();
        if (theStartingIndex >= myMaximalSize) {
          // FIXME should use the HyperLink feature and let the ViewController
          // handle the reload.
          myBonitaDataSource.getItemFilter().setStartingIndex(theStartingIndex - myMaximalSize);
          myItemSelection.clearSelection();
          myBonitaDataSource.reload();
        }
      }
    });

    olderButtonTop.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        int theStartingIndex = myBonitaDataSource.getItemFilter().getStartingIndex();
        myBonitaDataSource.getItemFilter().setStartingIndex(theStartingIndex + myMaximalSize);
        myItemSelection.clearSelection();
        // FIXME should use the HyperLink feature and let the ViewController
        // handle the reload.
        myBonitaDataSource.reload();
      }
    });

    oldestButtonTop.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {

        myBonitaDataSource.getItemFilter().setStartingIndex(getIndexOfLastPage(myBonitaDataSource.getSize(), myMaximalSize));
        myItemSelection.clearSelection();
        // FIXME should use the HyperLink feature and let the ViewController
        // handle the reload.
        myBonitaDataSource.reload();
      }
    });

    // Make the text looks like a link
    newestButtonTop.setStylePrimaryName(ITEMS_LINK_STYLE);
    newerButtonTop.setStylePrimaryName(ITEMS_LINK_STYLE);
    olderButtonTop.setStylePrimaryName(ITEMS_LINK_STYLE);
    oldestButtonTop.setStylePrimaryName(ITEMS_LINK_STYLE);

    newestButtonTop.setTitle(constants.firstPage());
    oldestButtonTop.setTitle(constants.lastPage());

    thePageNavigator.add(newestButtonTop);
    thePageNavigator.add(newerButtonTop);
    thePageNavigator.add(myCountLabelTop);
    thePageNavigator.add(olderButtonTop);
    thePageNavigator.add(oldestButtonTop);

    thePageNavigator.setSpacing(3);

    myFilterEditor = buildFilterEditor();
    if (myFilterEditor != null) {
      myTopNavBar.add(myFilterEditor);
      myFilterEditor.addPageBrowser(thePageNavigator);
    } else {
      myTopNavBar.add(thePageNavigator);
    }
    
    // Create the 'navigation' bar at the bottom-right
    myBottomNavBar = buildBottomNavBar();
    if (myBottomNavBar != null) {
      myBottomNavBar.setStyleName(ITEM_LIST_NAVBAR_STYLE);
    }
    thePageNavigator = new HorizontalPanel();
    thePageNavigator.setStyleName(ITEM_LIST_PAGE_BOTTOM_NAVBAR_STYLE);
    // Create navigation handler.
    newestButtonBottom.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        // FIXME should use the HyperLink feature and let the ViewController
        // handle the reload.
        myBonitaDataSource.getItemFilter().setStartingIndex(0);
        myBonitaDataSource.reload();
      }
    });

    newerButtonBottom.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        int theStartingIndex = myBonitaDataSource.getItemFilter().getStartingIndex();
        if (theStartingIndex >= myMaximalSize) {
          myBonitaDataSource.getItemFilter().setStartingIndex(theStartingIndex - myMaximalSize);
          // FIXME should use the HyperLink feature and let the ViewController
          // handle the reload.
          myBonitaDataSource.reload();
        }
      }
    });

    olderButtonBottom.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        int theStartingIndex = myBonitaDataSource.getItemFilter().getStartingIndex();
        myBonitaDataSource.getItemFilter().setStartingIndex(theStartingIndex + myMaximalSize);
        // FIXME should use the HyperLink feature and let the ViewController
        // handle the reload.
        myBonitaDataSource.reload();
      }
    });

    oldestButtonBottom.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        myBonitaDataSource.getItemFilter().setStartingIndex(getIndexOfLastPage(myBonitaDataSource.getSize(), myMaximalSize));
        // FIXME should use the HyperLink feature and let the ViewController
        // handle the reload.
        myBonitaDataSource.reload();
      }
    });

    // Make the text looks like a link
    newestButtonBottom.setStylePrimaryName(ITEMS_LINK_STYLE);
    newerButtonBottom.setStylePrimaryName(ITEMS_LINK_STYLE);
    olderButtonBottom.setStylePrimaryName(ITEMS_LINK_STYLE);
    oldestButtonBottom.setStylePrimaryName(ITEMS_LINK_STYLE);

    thePageNavigator.add(newestButtonBottom);
    thePageNavigator.add(newerButtonBottom);
    thePageNavigator.add(myCountLabelBottom);
    thePageNavigator.add(olderButtonBottom);
    thePageNavigator.add(oldestButtonBottom);
    thePageNavigator.setSpacing(3);

    if (myBottomNavBar != null) {
      myBottomNavBar.add(thePageNavigator);
    }
  }

  protected abstract ItemFilterEditor<F> buildFilterEditor();

  protected int getIndexOfLastPage(int aTotalSize, int aPageSize) {
    int theResult = (aTotalSize - (aTotalSize % aPageSize));
    if(theResult >= aTotalSize) {
      return aTotalSize - aPageSize;
    } else {
      return theResult;
    }
  }

  protected abstract FlowPanel buildBottomNavBar();

  protected abstract FlowPanel buildTopNavBar();

  /**
   * Build the inner table.
   */
  protected void initInnerTable() {
    myInnerTable.setCellSpacing(0);
    myInnerTable.setCellPadding(0);
    myInnerTable.getColumnFormatter().setStyleName(0, ITEM_LIST_SELECT_COLUMN_STYLE);
    myInnerTable.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aEvent) {
        Cell theCell = myInnerTable.getCellForEvent(aEvent);
        if (myVisibleItems != null) {
          // Only columns from index 2 should handle the click event.
          // Column 0 contains the checkbox.
          // The column 0 already handles click events for its own
          // reasons.

//          if (theCell != null && theCell.getCellIndex() > 0) {
            U theBonitaUUID = myRowTableItem.get(theCell.getRowIndex());

            if (theBonitaUUID != null) {
              // The selected row is not an empty row.
              notifyItemClicked(theBonitaUUID, aEvent);
            }
//          }
//          if (theCell != null && theCell.getCellIndex() == 0) {
//            U theBonitaUUID = myRowTableItem.get(theCell.getRowIndex());
//            if (theBonitaUUID != null) {
//              if (myItemSelection.getSelectedItems().contains(theBonitaUUID)) {
//                myInnerTable.getRowFormatter().addStyleName(theCell.getRowIndex(), ITEM_SELECTED_STYLE);
//              } else {
//                myInnerTable.getRowFormatter().removeStyleName(theCell.getRowIndex(), ITEM_SELECTED_STYLE);
//              }
//            }
//          }
        }
      }
    });

  }

  /*
   * Update the UI.
   */
  protected abstract void update(final List<U> anItemList);
  
  protected boolean isSelectable(final I anItem) {
      return true;
  }

  /**
   * Fill the table to ensure a minimal size.
   * 
   * @param aStartingRow
   */
  protected void fillWithEmptyRows(final int aRowOffset, final int aStartingRow, final int aNbColumn) {
    // Fill any remaining slots with empty cells.
    int nbRowInTable = aStartingRow;
    for (; nbRowInTable < myMinimalSize + aRowOffset; nbRowInTable++) {
      for (int i = 0; i < aNbColumn; i++) {
        myInnerTable.setHTML(nbRowInTable, i, NBSP);
        unlinkItemWithRow(nbRowInTable);
      }
      // Set CSS style.
      myInnerTable.getRowFormatter().setStyleName(nbRowInTable, ITEM_LIST_EMPTY_ROW_STYLE);
      myInnerTable.getRowFormatter().getElement(nbRowInTable).setTitle("");
    }
  }

  /**
   * Filter myCases then update the UI.
   */
  protected abstract void createWidgetsForItemsAndDisplay();

  /*
   * (non-Javadoc)
   * 
   * @seejava.beans.ModelChangeListener#propertyChange(java.beans.
   * PropertyChangeEvent)
   */
  @SuppressWarnings("unchecked")
  public void modelChange(ModelChangeEvent anEvent) {
    if (ItemSelection.ITEM_SELECTION_PROPERTY.equals(anEvent.getPropertyName())) {
      updateSelectedItems((ArrayList<? extends BonitaUUID>) anEvent.getOldValue(), (ArrayList<? extends BonitaUUID>) anEvent.getNewValue());
    }

  }

  /**
   * Update the css style of the row displaying the given case to reflect the
   * selection state.
   * 
   * @param aNewItemSelection
   */
  protected void updateSelectedItems(ArrayList<? extends BonitaUUID> anOldCaseSelection, ArrayList<? extends BonitaUUID> aNewItemSelection) {
    for (Iterator<? extends BonitaUUID> theIterator = anOldCaseSelection.iterator(); theIterator.hasNext();) {
      BonitaUUID theUUID = theIterator.next();
      if (!aNewItemSelection.contains(theUUID) && myItemTableRow.containsKey(theUUID)) {
        myInnerTable.getRowFormatter().removeStyleName(myItemTableRow.get(theUUID), ITEM_SELECTED_STYLE);
      }
    }
    for (Iterator<? extends BonitaUUID> theIterator = aNewItemSelection.iterator(); theIterator.hasNext();) {
      BonitaUUID theUUID = theIterator.next();
      if (!anOldCaseSelection.contains(theUUID) && myItemTableRow.containsKey(theUUID)) {
        myInnerTable.getRowFormatter().addStyleName(myItemTableRow.get(theUUID), ITEM_SELECTED_STYLE);
      }
    }

  }

  protected void updateListSize(final List<U> anItemList) {

    String theCountLabelText;
    int theStartingIndex = myBonitaDataSource.getItemFilter().getStartingIndex();
    int theAvailableItems = myBonitaDataSource.getSize();
    if (anItemList.size() > 0) {
      theCountLabelText = patterns.listSize((theStartingIndex + 1) ,(theStartingIndex + anItemList.size()), theAvailableItems);
    } else {
      theCountLabelText = patterns.listSize(0,0,0);
    }
    myCountLabelTop.setText(theCountLabelText);
    myCountLabelBottom.setText(theCountLabelText);
    // Navigation links may or not be displayed depending on the number of cases
    // available.
    if (theStartingIndex == 0) {
      newerButtonTop.setVisible(false);
      newerButtonBottom.setVisible(false);
      newestButtonTop.setVisible(false);
      newestButtonBottom.setVisible(false);
    } else {
      newerButtonTop.setVisible(true);
      newerButtonBottom.setVisible(true);
      newestButtonTop.setVisible(true);
      newestButtonBottom.setVisible(true);
    }
    if ((theStartingIndex + myMaximalSize) >= theAvailableItems) {
      olderButtonTop.setVisible(false);
      olderButtonBottom.setVisible(false);
      oldestButtonTop.setVisible(false);
      oldestButtonBottom.setVisible(false);
    } else {
      olderButtonTop.setVisible(true);
      olderButtonBottom.setVisible(true);
      oldestButtonTop.setVisible(true);
      oldestButtonBottom.setVisible(true);
    }

  }

  protected void linkItemWithRow(U aUUID, int aRowIndex) {
    myItemTableRow.put(aUUID, aRowIndex);
    myRowTableItem.put(aRowIndex, aUUID);
  }

  protected void unlinkItemWithRow(int aRowIndex) {
    U theUUID = myRowTableItem.remove(aRowIndex);
    myItemTableRow.remove(theUUID);
  }

}
