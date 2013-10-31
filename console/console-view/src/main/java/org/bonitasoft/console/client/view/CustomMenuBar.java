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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CustomMenuBar extends Composite {

	class MenuItem extends Composite implements HasAllMouseHandlers, HasClickHandlers {

		private static final String LEFT_SIDE_BUTTON_STYLE_NAME = "menu_item_left";
		private static final String RIGHT_SIDE_BUTTON_STYLE_NAME = "menu_item_right";
		private static final String SUBMENU_ICON_STYLE_NAME = "icon_menu_bar_submenu";
		private final Image myLeftSideIcon = new Image(PICTURE_PLACE_HOLDER);
		private final Image myRightSideIcon = new Image(PICTURE_PLACE_HOLDER);
		private final Image mySubMenuIcon = new Image(PICTURE_PLACE_HOLDER);

		private final HorizontalPanel myItemOuterPanel;

		public MenuItem(Widget aMenuItem, boolean hasPopup) {
			super();
			myItemOuterPanel = new HorizontalPanel();

			myItemOuterPanel.setStyleName("menu_item");
			myLeftSideIcon.setStyleName(LEFT_SIDE_BUTTON_STYLE_NAME);
			mySubMenuIcon.setStyleName(SUBMENU_ICON_STYLE_NAME);
			myRightSideIcon.setStyleName(RIGHT_SIDE_BUTTON_STYLE_NAME);

			myItemOuterPanel.add(myLeftSideIcon);
			myItemOuterPanel.add(aMenuItem);
			if (hasPopup) {
				myItemOuterPanel.add(mySubMenuIcon);
			}
			myItemOuterPanel.add(myRightSideIcon);

			this.initWidget(myItemOuterPanel);
		}

		public void setHasRightSibbling(boolean hasSibbling) {
			if (hasSibbling) {
				myItemOuterPanel.remove(myRightSideIcon);
			} else {
				myItemOuterPanel.add(myRightSideIcon);
			}
		}

		public void setHasLeftSibbling(boolean hasSibbling) {
			if (hasSibbling) {
				myItemOuterPanel.remove(myLeftSideIcon);
			} else {
				myItemOuterPanel.insert(myRightSideIcon, 0);
			}
		}

		public HandlerRegistration addMouseDownHandler(MouseDownHandler anHandler) {
			return addDomHandler(anHandler, MouseDownEvent.getType());
		}

		public HandlerRegistration addMouseUpHandler(MouseUpHandler anHandler) {
			return addDomHandler(anHandler, MouseUpEvent.getType());
		}

		public HandlerRegistration addMouseOutHandler(MouseOutHandler anHandler) {
			return addDomHandler(anHandler, MouseOutEvent.getType());
		}

		public HandlerRegistration addMouseOverHandler(MouseOverHandler anHandler) {
			return addDomHandler(anHandler, MouseOverEvent.getType());
		}

		public HandlerRegistration addMouseMoveHandler(MouseMoveHandler anHandler) {
			return addDomHandler(anHandler, MouseMoveEvent.getType());
		}

		public HandlerRegistration addMouseWheelHandler(MouseWheelHandler anHandler) {
			return addDomHandler(anHandler, MouseWheelEvent.getType());
		}

		public HandlerRegistration addClickHandler(ClickHandler handler) {
			return addDomHandler(handler, ClickEvent.getType());
		}
	}

	private static final String PICTURE_PLACE_HOLDER = "pictures/cleardot.gif";
	private static final String DEFAULT_MENU_SEPARATOR = "icon_menu_bar_separator";
	private static final String DEPENDENT_STYLENAME_PRESSED_ITEM = "pressed";

	private final PopupPanel myPopup;
	private final HorizontalPanel myInnerPanel;

	private final MouseHandler myMouseHandler = new MouseHandler();

	/*
	 * Capture the events coming from the mouse to dynamically update the css style of table entries.
	 */
	private class MouseHandler implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler {
		private static final String DEPENDENT_STYLENAME_HOVER_ITEM = "hover";

		public void onMouseOut(MouseOutEvent aEvent) {
			Object theSource = aEvent.getSource();
			if (theSource instanceof Widget) {
				Widget theWidget = (Widget) theSource;
				theWidget.removeStyleDependentName(DEPENDENT_STYLENAME_HOVER_ITEM);
			}
		}

		public void onMouseOver(MouseOverEvent aEvent) {
			Object theSource = aEvent.getSource();
			if (theSource instanceof UIObject) {
				((UIObject) theSource).addStyleDependentName(DEPENDENT_STYLENAME_HOVER_ITEM);
			}
		}

		public void onMouseDown(MouseDownEvent anEvent) {
			Object theSource = anEvent.getSource();
			if (theSource instanceof Widget) {
				Widget theWidget = (Widget) theSource;
				theWidget.addStyleDependentName(DEPENDENT_STYLENAME_PRESSED_ITEM);
			}

		}

		public void onMouseUp(MouseUpEvent anEvent) {
			Object theSource = anEvent.getSource();
			if (theSource instanceof Widget) {
				Widget theWidget = (Widget) theSource;
				theWidget.removeStyleDependentName(DEPENDENT_STYLENAME_PRESSED_ITEM);
			}

		}
	}

	public CustomMenuBar() {
		super();

		myInnerPanel = new HorizontalPanel();
		myInnerPanel.setSpacing(0);

		myPopup = new PopupPanel(true);
		myPopup.setAnimationEnabled(false);
		myPopup.setStylePrimaryName("b-MenuBarPopup");

		// myPopup.addCloseHandler(new CloseHandler<PopupPanel>(){
		// public void onClose(CloseEvent<PopupPanel> anEvent) {
		// for (Iterator iterator = myInnerPanel.iterator(); iterator.hasNext();) {
		// MenuItem theMenuItem = (MenuItem) iterator.next();
		// theMenuItem.removeStyleDependentName(DEPENDENT_STYLENAME_PRESSED_ITEM);
		// }
		//				
		// }
		// });

		this.initWidget(myInnerPanel);
		myInnerPanel.setStyleName("b-MenuBar");
	}

	/**
	 * provide a method to choose to use the same sized button style
	 */
	public void addItem(final String aText, final String styleName, final Command aCommand) {

		HTML theInnerButton = new HTML(aText);
		theInnerButton.setStyleName(styleName);
		MenuItem theMenuItem = new MenuItem(theInnerButton, false);

		theMenuItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent anEvent) {
				aCommand.execute();
			}
		});

		if (myInnerPanel.getWidgetCount() > 0) {
			((MenuItem) myInnerPanel.getWidget(myInnerPanel.getWidgetCount() - 1)).setHasRightSibbling(true);
			addSeparator();
			theMenuItem.setHasLeftSibbling(true);
		}
		myInnerPanel.add(theMenuItem);
		theMenuItem.addMouseOverHandler(myMouseHandler);
		theMenuItem.addMouseOutHandler(myMouseHandler);
		theMenuItem.addMouseDownHandler(myMouseHandler);
		theMenuItem.addMouseUpHandler(myMouseHandler);
	}

	public void addItem(final String aText, final Command aCommand) {

		HTML theInnerButton = new HTML(aText);
		theInnerButton.setStyleName("inner_menu_button");
		MenuItem theMenuItem = new MenuItem(theInnerButton, false);

		theMenuItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent anEvent) {
				aCommand.execute();
			}
		});

		if (myInnerPanel.getWidgetCount() > 0) {
			((MenuItem) myInnerPanel.getWidget(myInnerPanel.getWidgetCount() - 1)).setHasRightSibbling(true);
			addSeparator();
			theMenuItem.setHasLeftSibbling(true);
		}
		myInnerPanel.add(theMenuItem);
		theMenuItem.addMouseOverHandler(myMouseHandler);
		theMenuItem.addMouseOutHandler(myMouseHandler);
		theMenuItem.addMouseDownHandler(myMouseHandler);
		theMenuItem.addMouseUpHandler(myMouseHandler);
	}
	
    public void addItem(final Widget aWidget, final Command aCommand) {

        aWidget.setStyleName("inner_menu_button");
        MenuItem theMenuItem = new MenuItem(aWidget, false);

        theMenuItem.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent anEvent) {
                aCommand.execute();
            }
        });

        if (myInnerPanel.getWidgetCount() > 0) {
            ((MenuItem) myInnerPanel.getWidget(myInnerPanel.getWidgetCount() - 1)).setHasRightSibbling(true);
            addSeparator();
            theMenuItem.setHasLeftSibbling(true);
        }
        myInnerPanel.add(theMenuItem);
        theMenuItem.addMouseOverHandler(myMouseHandler);
        theMenuItem.addMouseOutHandler(myMouseHandler);
        theMenuItem.addMouseDownHandler(myMouseHandler);
        theMenuItem.addMouseUpHandler(myMouseHandler);
    }

	public void addItem(final String aText, final MenuChoicesPanel aPopupContent) {
		final HTML theInnerText = new HTML(aText);
		addItem(theInnerText, aPopupContent);
	}
	
	public void addItem(final Widget aWidget, final MenuChoicesPanel aPopupContent) {
	  final FlowPanel theWrapper = new FlowPanel();
	  theWrapper.add(aWidget);
	  theWrapper.addStyleName("inner_menu_button");

    final MenuItem theMenuItem = new MenuItem(theWrapper, true);

    ClickHandler theClickHandler = new ClickHandler() {
      public void onClick(ClickEvent anEvent) {
        myPopup.clear();
        myPopup.add(aPopupContent);
        // Bind the popup to the content, so that it can close the popup when required.
        aPopupContent.setParent(myPopup);
        
        myPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
          public void setPosition(int offsetWidth, int offsetHeight) {
              int menuAbsoluteLeft = theMenuItem.getAbsoluteLeft();
              int menuWidth = theMenuItem.getOffsetWidth();
            myPopup.setPopupPosition(menuAbsoluteLeft + menuWidth - offsetWidth, CustomMenuBar.this.getAbsoluteTop() + CustomMenuBar.this.getOffsetHeight() - 1);
          }
        });

      }
    };
    theMenuItem.addClickHandler(theClickHandler);

    if (myInnerPanel.getWidgetCount() > 0) {
      ((MenuItem) myInnerPanel.getWidget(myInnerPanel.getWidgetCount() - 1)).setHasRightSibbling(true);
      addSeparator();
      theMenuItem.setHasLeftSibbling(true);
    }
    myInnerPanel.add(theMenuItem);
    theMenuItem.addMouseOverHandler(myMouseHandler);
    theMenuItem.addMouseOutHandler(myMouseHandler);
    theMenuItem.addMouseDownHandler(myMouseHandler);
    theMenuItem.addMouseUpHandler(myMouseHandler);
  }

	private void addSeparator() {
		HTML theSeparator = new HTML();
		theSeparator.setStyleName(DEFAULT_MENU_SEPARATOR);
		myInnerPanel.add(theSeparator);

	}

	@Override
	protected void onDetach() {
		hideAllPopups();
		super.onDetach();
	}

	/**
	 * Hide the popups that are currently showing.
	 */
	public void hideAllPopups(){
		if (myPopup != null) {
			myPopup.hide();
		}
	}
}
