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

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.model.MessageDataSource;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public abstract class ItemFilterEditor<F extends ItemFilter> extends I18NComposite implements ModelChangeListener {
  
  protected static final String ADVANCED_SUFFIX_STYLE = "open";
  protected static final String SIMPLE_SUFFIX_STYLE = "closed";
  protected static final String FILTER_ELEMENT_STYLE = "bos_filter_element";
  protected static final String FILTER_ELEMENT_TITLE_STYLE = "bos_filter_element_title";
  protected static final String FILTER_ELEMENT_WRAPPER_STYLE = "bos_filter_element_wrapper";
  protected static final String FILTER_ITEM_COL1_STYLE = "bos_filter_element_column_1";
  protected static final String FILTER_ITEM_COL2_STYLE = "bos_filter_element_column_2";
  
  
  public static final String FILTER_UPDATED_PROPERTY = "filter editor updated";
  private static final String ROUNDED_STYLE = "bos_search_natural_engine";

  protected final FlowPanel myOuterPanel = new FlowPanel();
  protected final FlowPanel myPageBrowserPanel = new FlowPanel();
  protected final FlowPanel myFilterContentPanel = new FlowPanel();

  protected final MultiWordSuggestOracle mySearchOracle = new MultiWordSuggestOracle();

  protected final MessageDataSource myMessageDataSource;
  protected final F myFilter;
  protected Widget myPageBrowser;
  
  protected transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

  /**
   * Default constructor.
   * 
   * @param aFilter
   */
  @SuppressWarnings("unchecked")
  public ItemFilterEditor(MessageDataSource aMessageDataSource, F aFilter) {
    super();
    myOuterPanel.setStylePrimaryName("bos_item_filter_editor");
    myPageBrowserPanel.setStylePrimaryName("bos_item_filter_editor_page_browser");
    myFilterContentPanel.setStylePrimaryName("bos_item_filter_editor_content");
    myOuterPanel.add(myFilterContentPanel);
    myOuterPanel.add(myPageBrowserPanel);
    myMessageDataSource = aMessageDataSource;
    myFilter = (F) aFilter.createFilter();

    initWidget(myOuterPanel);
  }

  protected boolean performNaturalSearch(SuggestBox aSuggestBox) {
    final String theSearchPattern = aSuggestBox.getValue();
    boolean isSearching = false;
    if (theSearchPattern != null && theSearchPattern.length() > 0) {
      myFilter.setSearchPattern(theSearchPattern);
      myFilter.setStartingIndex(0);
      mySearchOracle.add(theSearchPattern);
      myChanges.fireModelChange(FILTER_UPDATED_PROPERTY, null, myFilter);
      isSearching = true;
    } else {
      if(myFilter.getSearchPattern()==null || myFilter.getSearchPattern().length()==0){
      if(myMessageDataSource!=null) {
        myMessageDataSource.addWarningMessage(messages.searchPatternEmpty());
      }
      } else {
        // clear pattern filter
        clearFilterPatternAndNotify();
      }
    }
    return isSearching;
  }

  protected SimplePanel createNaturalSearchElement(HTML aSearchScopeExplanations) {
    DecoratorPanel theNaturalSearchPanel = new DecoratorPanel();
    theNaturalSearchPanel.setStylePrimaryName(ROUNDED_STYLE);
    final SuggestBox theSearchSB = new SuggestBox(mySearchOracle);

    HorizontalPanel theNaturalSearch = new HorizontalPanel();
    final Image theMagnifyIcon = new Image(PICTURE_PLACE_HOLDER);
    theMagnifyIcon.setStylePrimaryName(CSSClassManager.SEARCH_ICON);
    if (aSearchScopeExplanations != null) {
      final DecoratedPopupPanel theExplanationsPopup = new DecoratedPopupPanel(true, false);
      theExplanationsPopup.setWidget(aSearchScopeExplanations);
      theMagnifyIcon.addMouseOverHandler(new MouseOverHandler() {

        public void onMouseOver(MouseOverEvent aEvent) {
          theExplanationsPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int anOffsetWidth, int anOffsetHeight) {
              int left = theMagnifyIcon.getAbsoluteLeft() - (anOffsetWidth / 2);
              int top = theMagnifyIcon.getAbsoluteTop() + theMagnifyIcon.getHeight() + 7;
              theExplanationsPopup.setPopupPosition(left, top);
            }
          });
        }

      });
      theMagnifyIcon.addMouseOutHandler(new MouseOutHandler() {

        public void onMouseOut(MouseOutEvent aEvent) {
          theExplanationsPopup.hide();
        }
      });
    }
    final Image theActionIcon = new Image(PICTURE_PLACE_HOLDER);
    theActionIcon.setStylePrimaryName(CSSClassManager.SEARCH_CLEAR_ICON);
    theActionIcon.setVisible(false);
    theActionIcon.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        theSearchSB.setValue(null);
        theActionIcon.setVisible(false);
       clearFilterPatternAndNotify();
      }
    });
    final DecoratedPopupPanel theClearTooltip = new DecoratedPopupPanel(true, false);
    theClearTooltip.setWidget(new HTML(constants.clearFilter()));
    theActionIcon.addMouseOverHandler(new MouseOverHandler() {

      public void onMouseOver(MouseOverEvent aEvent) {
        theClearTooltip.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
          public void setPosition(int anOffsetWidth, int anOffsetHeight) {
            int left = theActionIcon.getAbsoluteLeft() - (anOffsetWidth / 2);
            int top = theActionIcon.getAbsoluteTop() + theActionIcon.getHeight() + 7;
            theClearTooltip.setPopupPosition(left, top);
          }
        });
      }

    });
    // theActionIcon.addMouseMoveHandler(new MouseMoveHandler() {
    //
    // public void onMouseMove(MouseMoveEvent aEvent) {
    // theClearTooltip.setPopupPositionAndShow(new PopupPanel.PositionCallback()
    // {
    // public void setPosition(int anOffsetWidth, int anOffsetHeight) {
    // int left = theActionIcon.getAbsoluteLeft() - (anOffsetWidth / 2);
    // int top = theActionIcon.getAbsoluteTop() + theActionIcon.getHeight() + 7;
    // theClearTooltip.setPopupPosition(left, top);
    // }
    // });
    // }
    //
    // });

    theActionIcon.addMouseOutHandler(new MouseOutHandler() {

      public void onMouseOut(MouseOutEvent aEvent) {
        theClearTooltip.hide();
      }
    });

    theSearchSB.getTextBox().addKeyDownHandler(new KeyDownHandler() {

      public void onKeyDown(KeyDownEvent anEvent) {
        int theKey = anEvent.getNativeKeyCode();
        if (KeyCodes.KEY_ENTER == theKey) {
          if (performNaturalSearch(theSearchSB)) {
            theActionIcon.setVisible(true);
          } else {
            theActionIcon.setVisible(false);
          }
        }
      }
    });

    theSearchSB.getTextBox().addBlurHandler(new BlurHandler() {

      public void onBlur(BlurEvent aEvent) {
        theSearchSB.setValue(myFilter.getSearchPattern());
        if (theSearchSB.getValue() == null || theSearchSB.getValue().length() == 0) {
          theActionIcon.setVisible(false);
        } else {
          theActionIcon.setVisible(true);
        }
      }
    });

    theNaturalSearch.add(theMagnifyIcon);
    theNaturalSearch.add(theSearchSB);
    theNaturalSearch.add(theActionIcon);
    theNaturalSearchPanel.add(theNaturalSearch);

    return theNaturalSearchPanel;
  }

  protected void clearFilterPatternAndNotify() {
    myFilter.setSearchPattern(null);
    myChanges.fireModelChange(FILTER_UPDATED_PROPERTY, null, myFilter);
  }

  protected abstract void initContent();

  /**
   * Add a property change listener.
   * 
   * @param aPropertyName
   * @param aListener
   */
  public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
    // Avoid duplicate subscription.
    myChanges.removeModelChangeListener(aPropertyName, aListener);
    myChanges.addModelChangeListener(aPropertyName, aListener);
  }

  /**
   * Remove a property change listener.
   * 
   * @param aPropertyName
   * @param aListener
   */
  public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
    myChanges.removeModelChangeListener(aPropertyName, aListener);
  }
  
  public void addPageBrowser(Widget aPageBrowser) {
    if(myPageBrowser!=null) {
      myPageBrowserPanel.remove(myPageBrowser);
    }
    myPageBrowser = aPageBrowser;
    if(myPageBrowser!=null) {
      myPageBrowserPanel.add(myPageBrowser);
    }
  }
}
