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
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Nicolas Chabanoles
 */
public class ListBoxEditor extends I18NComposite implements HasValue<String>, ClickHandler {

  /**
   * 
   */
  private static final String READ_ONLY_STYLE = "bos_field_read_only";
  protected FlowPanel myOuterPanel;
  protected final Label myFieldLabel;
  protected boolean allowHtml;
  protected HTML myHtmlView;
  protected Label myTextView;
  protected ListBox myValueEditor;
  protected final Map<String, String> myAllowedValues;
  protected boolean isEditable;
  private HandlerRegistration myRegistration;
  private HandlerRegistration myMouseOverHandlerRegistration;
  private HandlerRegistration myMouseOutHandlerRegistration;

  /**
   * Default constructor.
   */
  public ListBoxEditor(String aLabel, String aValue, boolean allowHtml, Map<String,String> allowedValues) {
    myOuterPanel = new FlowPanel();
    myFieldLabel = new Label();
    if(aLabel!=null) {
      myFieldLabel.setText(aLabel);
    }
    myOuterPanel.setStylePrimaryName("bos_listbox_editor");
    myFieldLabel.setStylePrimaryName("field_label");
    
    this.allowHtml = allowHtml;
    isEditable = false;
    myAllowedValues = new HashMap<String, String>();
    if(allowedValues!=null && !allowedValues.isEmpty()) {
      myAllowedValues.putAll(allowedValues);
    }
    
    myOuterPanel.add(myFieldLabel);
    initContent(aValue);

    initWidget(myOuterPanel);
  }

  private void initContent(String aValue) {
    if (allowHtml) {
      myHtmlView = new HTML();
      myHtmlView.setStylePrimaryName("field_htmlview");
      if (aValue != null) {
        myHtmlView.setHTML(aValue);
      }
      myHtmlView.addStyleName(READ_ONLY_STYLE);
      myOuterPanel.add(myHtmlView);
    } else {
      myTextView = new Label();
      myTextView.setStylePrimaryName("field_view");
      if (aValue != null && aValue.length()>0) {
        myTextView.setText(aValue);
      } else {
        myTextView.addStyleName("empty_field");  
      }
      myTextView.addStyleName(READ_ONLY_STYLE);
      myOuterPanel.add(myTextView);
    }
  }

  public void setEditable(boolean isEditable) {
    if (this.isEditable != isEditable) {
      this.isEditable = isEditable;
      if (this.isEditable) {
        if (allowHtml) {
          myRegistration = myHtmlView.addClickHandler(this);
          myHtmlView.removeStyleName(READ_ONLY_STYLE);
        } else {
          myRegistration = myTextView.addClickHandler(this);
          myTextView.removeStyleName(READ_ONLY_STYLE);
        }
        buildTooltipPopup();
        
      } else {
        myRegistration.removeHandler();
        myMouseOverHandlerRegistration.removeHandler();
        myMouseOutHandlerRegistration.removeHandler();
        if(allowHtml) {
          myHtmlView.addStyleName(READ_ONLY_STYLE);
        } else {
          myTextView.addStyleName(READ_ONLY_STYLE);
        }
      }
    }
  }

  protected void showEditor() {
    if(myValueEditor == null) {
      myValueEditor = new ListBox();
      for (Entry<String,String> theEntry : myAllowedValues.entrySet()) {
        myValueEditor.addItem(theEntry.getKey(), theEntry.getValue());
      }
     
      myValueEditor.setStylePrimaryName("field_edit");
      myValueEditor.addChangeHandler(new ChangeHandler() {
        public void onChange(ChangeEvent aEvent) {
          hideEditor(true);
        }
      });
      myValueEditor.addBlurHandler(new BlurHandler() {
        
        public void onBlur(BlurEvent aEvent) {
          hideEditor(false);
          
        }
      });
    }
    if (allowHtml) {
      final int theRowCount = myValueEditor.getItemCount();
      for (int i = 0; i < theRowCount; i++) {
        if(myValueEditor.getItemText(i).equals(myHtmlView.getHTML())) {
          myValueEditor.setSelectedIndex(i);
        }
      }
      myOuterPanel.remove(myHtmlView);
    } else {
      final int theRowCount = myValueEditor.getItemCount();
      for (int i = 0; i < theRowCount; i++) {
        if(myValueEditor.getItemText(i).equals(myTextView.getText())) {
          myValueEditor.setSelectedIndex(i);
        }
      }
      myOuterPanel.remove(myTextView);
    }
    myOuterPanel.add(myValueEditor);
    myValueEditor.setFocus(true);
  }

  protected void hideEditor(boolean updateValue) {
    myOuterPanel.remove(myValueEditor);
    if (allowHtml) {
      myOuterPanel.add(myHtmlView);
    } else {
      myOuterPanel.add(myTextView);
    }
    if(updateValue) {
      setValue(myValueEditor.getValue(myValueEditor.getSelectedIndex()), true);
    }
  }

  public boolean isEditable() {
    return isEditable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gwt.user.client.ui.HasValue#getValue()
   */
  public String getValue() {
    if (allowHtml) {
      return myHtmlView.getHTML();
    } else {
      return myTextView.getText();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
   */
  public void setValue(String aValue) {
    if (allowHtml) {
      myHtmlView.setHTML(aValue);
      if (aValue != null && aValue.length()>0) {
        myHtmlView.removeStyleName("empty_field");
      } else {
        myHtmlView.addStyleName("empty_field");  
      }
    } else {
      myTextView.setText(aValue);
      if (aValue != null && aValue.length()>0) {
        myTextView.removeStyleName("empty_field");
      } else {
        myTextView.addStyleName("empty_field");  
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object,
   * boolean)
   */
  public void setValue(String aValue, boolean aFireEvents) {
    setValue(aValue);
    ValueChangeEvent.fire(ListBoxEditor.this, getValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.google.gwt.event.logical.shared.HasValueChangeHandlers#
   * addValueChangeHandler
   * (com.google.gwt.event.logical.shared.ValueChangeHandler)
   */
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> aHandler) {
    return addHandler(aHandler, ValueChangeEvent.getType());
  }

  protected void buildTooltipPopup() {
    final DecoratedPopupPanel thePopup = new DecoratedPopupPanel(true, false);
    thePopup.addStyleName("bos_field_editor_popup");
    HTML theInnerPanel = new HTML(constants.editIcon());

    thePopup.setWidget(theInnerPanel);

    if (allowHtml) {
     myMouseOverHandlerRegistration = myHtmlView.addMouseOverHandler(new MouseOverHandler() {

        public void onMouseOver(MouseOverEvent aEvent) {
          thePopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int anOffsetWidth, int anOffsetHeight) {
              int left = myHtmlView.getAbsoluteLeft() - (anOffsetWidth / 2);
              int top = myHtmlView.getAbsoluteTop() + myHtmlView.getOffsetHeight() + 7;
              thePopup.setPopupPosition(left, top);
            }
          });
        }

      });
     myMouseOutHandlerRegistration = myHtmlView.addMouseOutHandler(new MouseOutHandler() {

        public void onMouseOut(MouseOutEvent aEvent) {
          thePopup.hide();
        }
      });
    } else {
      myMouseOverHandlerRegistration = myTextView.addMouseOverHandler(new MouseOverHandler() {

        public void onMouseOver(MouseOverEvent aEvent) {
          thePopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int anOffsetWidth, int anOffsetHeight) {
              int left = myTextView.getAbsoluteLeft() - (anOffsetWidth / 2);
              int top = myTextView.getAbsoluteTop() + myTextView.getOffsetHeight() + 7;
              thePopup.setPopupPosition(left, top);
            }
          });
        }

      });
      myMouseOutHandlerRegistration = myTextView.addMouseOutHandler(new MouseOutHandler() {

        public void onMouseOut(MouseOutEvent aEvent) {
          thePopup.hide();
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
   * .dom.client.ClickEvent)
   */
  public void onClick(ClickEvent aEvent) {
    showEditor();
  }
}
