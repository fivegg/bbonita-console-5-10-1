package org.bonitasoft.console.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
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
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MenuChoicesPanel extends Composite {
	private PopupPanel myParent;

	public class MenuChoice extends Composite implements HasAllMouseHandlers, ClickHandler {

		private static final String DISABLE_SUFFIX_STYLE = "disabled";
        private final FlowPanel myChoiceOuterPanel;
		private final Command myCommand;
		private boolean enabled;

		public MenuChoice(Widget aChoiceContent, Command aCommand) {
			super();
			myChoiceOuterPanel = new FlowPanel();
			myChoiceOuterPanel.setStyleName("menu_choice");
			myChoiceOuterPanel.add(aChoiceContent);
			myCommand = aCommand;
			enabled = true;
			addDomHandler(this, ClickEvent.getType());
			this.initWidget(myChoiceOuterPanel);
		}
		
		public void setEnabled(boolean enabled) {
		    this.enabled = enabled;
		    if(this.enabled) {
		        myChoiceOuterPanel.removeStyleDependentName(DISABLE_SUFFIX_STYLE);    
		    } else {
		        myChoiceOuterPanel.addStyleDependentName(DISABLE_SUFFIX_STYLE);
		    }
		}

		public boolean isEnabled() {
            return this.enabled;
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

		public void onClick(ClickEvent anEvent) {
			if (this.enabled && myCommand != null) {
				myCommand.execute();
				if(myParent!=null){
					myParent.hide();
				}
			}

		}
	}

	// Create the container
	private final DecoratorPanel myContainer = new DecoratorPanel();
	// Create the panel for the layout.
	private final VerticalPanel myOuterPanel = new VerticalPanel();

	private final MouseHandler myMouseHandler = new MouseHandler();

	/*
	 * Capture the events coming from the mouse to dynamically update the css style of table entries.
	 */
	private class MouseHandler implements MouseOverHandler, MouseOutHandler, MouseUpHandler {
		private static final String DEPENDENT_STYLENAME_SELECTED_ITEM = "selected";

		public void onMouseOut(MouseOutEvent aEvent) {
			Object theSource = aEvent.getSource();
			if (theSource instanceof MenuChoice) {
				((MenuChoice) theSource).removeStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
			}
		}

		public void onMouseOver(MouseOverEvent aEvent) {
			Object theSource = aEvent.getSource();
			if (theSource instanceof MenuChoice) {
			    final MenuChoice theMenuChoice = ((MenuChoice) theSource); 
			    if(theMenuChoice.isEnabled()) {
			        theMenuChoice.addStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
			    }
			}
		}

		public void onMouseUp(MouseUpEvent anEvent) {
			Object theSource = anEvent.getSource();
			if (theSource instanceof MenuChoice) {
				((MenuChoice) theSource).removeStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
			}
			
		}
	}

	public MenuChoicesPanel() {
		super();
		myContainer.add(myOuterPanel);
		myContainer.removeStyleName(myContainer.getStylePrimaryName());
		this.initWidget(myContainer);
	}

	public void addChoice(MenuChoice aChoice) {
		aChoice.addMouseOverHandler(myMouseHandler);
		aChoice.addMouseOutHandler(myMouseHandler);
		aChoice.addMouseUpHandler(myMouseHandler);
		myOuterPanel.add(aChoice);
	}
	
	/**
	 * @param aParent the parent to set
	 */
	public void setParent(PopupPanel aParent) {
		myParent = aParent;
	}

	public void clearChoices() {
		myOuterPanel.clear();
		
	}

}
