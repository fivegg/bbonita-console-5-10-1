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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Nicolas Chabanoles
 */
public class TextBoxEditor extends I18NComposite implements HasValue<String>, ClickHandler {

  protected FlowPanel myOuterPanel;
  protected final Label myFieldLabel;
  protected boolean allowHtml;
  protected HTML myHtmlView;
  protected Label myTextView;
  protected TextBox myTextEditor;
  protected boolean isEditable;
  private HandlerRegistration myRegistration;
//  private HandlerRegistration myMouseOverHandlerRegistration;
//  private HandlerRegistration myMouseOutHandlerRegistration;

  /**
   * Default constructor.
   */
  public TextBoxEditor(String aLabel, String aValue, boolean allowHtml) {
    myOuterPanel = new FlowPanel();
    myFieldLabel = new Label();
    if (aLabel != null) {
      myFieldLabel.setText(aLabel);
    }
    myOuterPanel.setStylePrimaryName("bos_textbox_editor");
    myFieldLabel.setStylePrimaryName("field_label");

    this.allowHtml = allowHtml;
    isEditable = false;

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
      } else {
        myHtmlView.setText(constants.editValue());
        myHtmlView.addStyleName("empty_field");
      }
      myOuterPanel.add(myHtmlView);
    } else {
      myTextView = new Label();
      myTextView.setStylePrimaryName("field_view");
      if (aValue != null && aValue.length() > 0) {
        myTextView.setText(aValue);
      } else {
        myTextView.setText(constants.editValue());
        myTextView.addStyleName("empty_field");
      }
      myOuterPanel.add(myTextView);
    }
  }

  public void setEditable(boolean isEditable) {
    if (this.isEditable != isEditable) {
      this.isEditable = isEditable;
      if (this.isEditable) {
        if (allowHtml) {
          myRegistration = myHtmlView.addClickHandler(this);
        } else {
          myRegistration = myTextView.addClickHandler(this);
        }
        // buildTooltipPopup();
      } else {
        myRegistration.removeHandler();
//        myMouseOverHandlerRegistration.removeHandler();
//        myMouseOutHandlerRegistration.removeHandler();
      }
    }
  }

  protected void showEditor() {
    if (myTextEditor == null) {
      myTextEditor = new TextBox();
      myTextEditor.setStylePrimaryName("field_edit");
      myTextEditor.addValueChangeHandler(new ValueChangeHandler<String>() {
        public void onValueChange(ValueChangeEvent<String> aEvent) {
          hideEditor(true);
        }
      });
      myTextEditor.addBlurHandler(new BlurHandler() {

        public void onBlur(BlurEvent aEvent) {
          hideEditor(false);

        }
      });
    }
    if (allowHtml) {
      myTextEditor.setText(myHtmlView.getHTML());
      myOuterPanel.remove(myHtmlView);
    } else {
      myTextEditor.setText(myTextView.getText());
      myOuterPanel.remove(myTextView);
    }
    myOuterPanel.add(myTextEditor);
    myTextEditor.setFocus(true);
  }

  protected void hideEditor(boolean updateValue) {
    myOuterPanel.remove(myTextEditor);
    if (allowHtml) {
      myOuterPanel.add(myHtmlView);
    } else {
      myOuterPanel.add(myTextView);
    }
    if (updateValue) {
      setValue(myTextEditor.getValue(), true);
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
      if(constants.editValue().equals(myHtmlView.getHTML())) {
        return null;
      } else {
        return myHtmlView.getHTML();
      }
    } else {
      if(constants.editValue().equals(myTextView.getText())) {
        return null;
      } else {
        return myTextView.getText();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
   */
  public void setValue(String aValue) {
    if (allowHtml) {
      if (aValue != null && aValue.trim().length() > 0) {
        myHtmlView.setHTML(aValue);
        myHtmlView.removeStyleName("empty_field");
      } else {
        myHtmlView.setText(constants.editValue());
        myHtmlView.addStyleName("empty_field");
      }
    } else {
      if (aValue != null && aValue.trim().length() > 0) {
        myTextView.setText(aValue);
        myTextView.removeStyleName("empty_field");
      } else {
        myTextView.setText(constants.editValue());
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
    ValueChangeEvent.fire(TextBoxEditor.this, getValue());
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

  // protected void buildTooltipPopup() {
  // final DecoratedPopupPanel thePopup = new DecoratedPopupPanel(true, false);
  // thePopup.addStyleName("bos_field_editor_popup");
  // HTML theInnerPanel = new HTML(constants.editIcon());
  //
  // thePopup.setWidget(theInnerPanel);
  //
  // if (allowHtml) {
  // myMouseOverHandlerRegistration = myHtmlView.addMouseOverHandler(new
  // MouseOverHandler() {
  //
  // public void onMouseOver(MouseOverEvent aEvent) {
  // thePopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
  // public void setPosition(int anOffsetWidth, int anOffsetHeight) {
  // int left = myHtmlView.getAbsoluteLeft();
  // int top = myHtmlView.getAbsoluteTop() + myHtmlView.getOffsetHeight() + 7;
  // thePopup.setPopupPosition(left, top);
  // }
  // });
  // }
  //
  // });
  // myMouseOutHandlerRegistration = myHtmlView.addMouseOutHandler(new
  // MouseOutHandler() {
  //
  // public void onMouseOut(MouseOutEvent aEvent) {
  // thePopup.hide();
  // }
  // });
  // } else {
  // myMouseOverHandlerRegistration = myTextView.addMouseOverHandler(new
  // MouseOverHandler() {
  //
  // public void onMouseOver(MouseOverEvent aEvent) {
  // thePopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
  // public void setPosition(int anOffsetWidth, int anOffsetHeight) {
  // int left = myTextView.getAbsoluteLeft();
  // int top = myTextView.getAbsoluteTop() + myTextView.getOffsetHeight() + 7;
  // thePopup.setPopupPosition(left, top);
  // }
  // });
  // }
  //
  // });
  // myMouseOutHandlerRegistration = myTextView.addMouseOutHandler(new
  // MouseOutHandler() {
  //
  // public void onMouseOut(MouseOutEvent aEvent) {
  // thePopup.hide();
  // }
  // });
  // }
  // }

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
