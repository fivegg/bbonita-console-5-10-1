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
package org.bonitasoft.forms.client.view.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bonitasoft.forms.client.model.FormFieldAvailableValue;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget displaying a group of checkboxes
 * 
 * @author Anthony Birembaut
 */
public class CheckboxGroupWidget extends Composite implements HasClickHandlers, ClickHandler, HasValueChangeHandlers<Boolean>, ValueChangeHandler<Boolean> {

    /**
     * the flow panel used to display the widget
     */
    protected FlowPanel flowPanel;
    
    /**
     * The set of checkboxes in the group
     */
    protected Set<CheckBox> checkBoxes = new HashSet<CheckBox>();
    
    /**
     * items style
     */
    protected String itemsStyle;
    
    protected List<ClickHandler> clickHandlers;
    
    protected List<ValueChangeHandler<Boolean>> valueChangeHandlers;
    
    protected boolean allowHTML;
    
    /**
     * Constructor
     * 
     * @param availableValues available values of the group
     * @param initialValues initial values
     * @param itemsStyle the css classes of each radio button
     *            
     */
    public CheckboxGroupWidget(final List<FormFieldAvailableValue> availableValues, final Collection<String> initialValues, final String itemsStyle) {
        this(availableValues, initialValues, itemsStyle, false);
    }
    
    /**
     * Constructor
     * 
     * @param availableValues available values of the group
     * @param initialValues initial values
     * @param itemsStyle the css classes of each radio button
     * @param allowHTML allow HTML in the checkboxes labels
     *            
     */
    public CheckboxGroupWidget(final List<FormFieldAvailableValue> availableValues, final Collection<String> initialValues, final String itemsStyle, final boolean allowHTML) {

        flowPanel = new FlowPanel();
        
        this.allowHTML = allowHTML;
        
        this.itemsStyle = itemsStyle;
        
        for (final FormFieldAvailableValue availableValue : availableValues) {
            final CheckBox checkBox = new CheckBox(availableValue.getLabel(), allowHTML);
            checkBox.addClickHandler(this);
            checkBox.addValueChangeHandler(this);
            
            checkBox.setFormValue(availableValue.getValue());
            if (initialValues != null && initialValues.contains(availableValue.getValue()))
            {
                checkBox.setValue(true);
            }
            checkBox.setStyleName("bonita_form_radio");
            if (itemsStyle != null && itemsStyle.length() > 0) {
                checkBox.addStyleName(itemsStyle);
            }
            checkBoxes.add(checkBox);
            flowPanel.add(checkBox);
        }

        initWidget(flowPanel);
    }
    
    /**
     * @return the List of String value of the slected checkboxes of the group
     */
    public List<String> getValue(){
        
        final List<String> values = new ArrayList<String>();
        
        final Iterator<Widget> iterator = flowPanel.iterator();
        while (iterator.hasNext()) {
            final CheckBox checkBox = (CheckBox) iterator.next();
            if (checkBox.getValue()) {
                values.add(checkBox.getFormValue());
            }
        }
        return values;
    }
    
    /**
     * Set the value of the widget
     * @param value
     */
    public void setValue(final Collection<String> values, boolean fireEvents) {
        final List<String> oldValues = getValue();
        if (values != null && oldValues.containsAll(values) && values.containsAll(oldValues)) {
            fireEvents = false;
        }
        for (final CheckBox checkBox : checkBoxes) {
            if (values != null && values.contains(checkBox.getFormValue())) {
                checkBox.setValue(true);
            } else {
                checkBox.setValue(false);
            }
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, true);
        }
    }
    
    /**
     * Set the wigdet available values
     * @param availableValues
     */
    public void setAvailableValues(final List<FormFieldAvailableValue> availableValues, final boolean fireEvents) {
        checkBoxes.clear();
        flowPanel.clear();
        for (final FormFieldAvailableValue availableValue : availableValues) {
            final CheckBox checkBox = new CheckBox(availableValue.getLabel(), allowHTML);
            checkBox.addClickHandler(this);
            checkBox.addValueChangeHandler(this);
            checkBox.setFormValue(availableValue.getValue());
            checkBox.setStyleName("bonita_form_radio");
            if (itemsStyle != null && itemsStyle.length() > 0) {
                checkBox.addStyleName(itemsStyle);
            }
            checkBoxes.add(checkBox);
            flowPanel.add(checkBox);
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, true);
        }
    }
    
    /**
     * Enable or disable the checkbox group
     * @param isEnabled
     */
    public void setEnabled(final boolean isEnabled) {
        for (final CheckBox checkBox : checkBoxes) {
            checkBox.setEnabled(isEnabled);
        }
    }

    /**
     * {@inheritDoc}
     */
    public HandlerRegistration addClickHandler(final ClickHandler clickHandler) {
        if (clickHandlers == null) {
            clickHandlers = new ArrayList<ClickHandler>();
        }
        clickHandlers.add(clickHandler);
        return new EventHandlerRegistration(clickHandler);
    }
    
    /**
     * {@inheritDoc}
     */
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Boolean> valueChangeHandler) {
        if (valueChangeHandlers == null) {
            valueChangeHandlers = new ArrayList<ValueChangeHandler<Boolean>>();
        }
        valueChangeHandlers.add(valueChangeHandler);
        return new EventHandlerRegistration(valueChangeHandler);
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(final ClickEvent clickEvent) {
        for (final ClickHandler clickHandler : clickHandlers) {
            clickHandler.onClick(clickEvent);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void onValueChange(final ValueChangeEvent<Boolean> valueChangeEvent) {
        for (final ValueChangeHandler<Boolean> valueChangeHandler : valueChangeHandlers) {
            valueChangeHandler.onValueChange(valueChangeEvent);
        }
    }
    
    /**
     * Custom Handler registration
     */
    protected class EventHandlerRegistration implements HandlerRegistration {

        protected EventHandler eventHandler;
        
        public EventHandlerRegistration(final EventHandler eventHandler) {
            this.eventHandler = eventHandler;
        }
        
        public void removeHandler() {
            if (eventHandler instanceof ClickHandler) {
                clickHandlers.remove(eventHandler);
            } else if (eventHandler instanceof ValueChangeHandler<?>) {
                valueChangeHandlers.remove(eventHandler);
            }
        }
    }
}
