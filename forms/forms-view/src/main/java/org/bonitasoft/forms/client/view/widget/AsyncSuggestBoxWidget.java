/**
 * Copyright (C) 2011 BonitaSoft S.A.
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

import java.util.List;
import java.util.Map;

import org.bonitasoft.forms.client.model.FormFieldAvailableValue;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.WidgetType;
import org.bonitasoft.forms.client.rpc.FormsServiceAsync;
import org.bonitasoft.forms.client.view.SupportedFieldTypes;
import org.bonitasoft.forms.client.view.common.RpcFormsServices;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Generic form flow AsyncSuggestBox widget
 * 
 * @author qixiang.zhang
 */
public class AsyncSuggestBoxWidget extends Composite {

    /**
     * The default refresh delay
     */
    protected static final int DEFAULT_REFRESH_DELAY = 1000;

    /**
     * the flow panel used to display the widget
     */
    protected FlowPanel flowPanel;

    /**
     * widget field type
     */
    protected WidgetType widgetType;

    /**
     * SuggestBox widget
     */
    protected SuggestBox asyncSuggestBox;

    protected String formID;

    protected Map<String, Object> contextMap;
    
    /**
     * Indicates if the widget has to be displayed with the current value of the data
     */
    protected boolean isCurrentValue;

    /**
     * Form Widget
     */
    protected FormWidget formWidget;

    /**
     * old asyncSuggestBox value
     */
    protected String oldValue;

    /**
     * Default constructor.
     * @param formAsyncSuggestBoxData the Suggestbox data
     * @param formID
     * @param contextMap
     * @param pageId
     * @param isCurrentValue
     * @param fieldValue the field's available value
     */
    public AsyncSuggestBoxWidget(final FormWidget formAsyncSuggestBoxData, final String formID, final Map<String, Object> contextMap, final boolean isCurrentValue, final FormFieldValue fieldValue) {
        this.formID = formID;
        this.contextMap = contextMap;
        this.formWidget = formAsyncSuggestBoxData;
        flowPanel = new FlowPanel();

        createWidget(formAsyncSuggestBoxData, fieldValue);

        initWidget(flowPanel);
    }

    /**
     * Create AsyncSuggestBox
     * 
     * @param formAsyncSuggestBoxData
     * @param fieldValue
     */
    protected void createWidget(final FormWidget formAsyncSuggestBoxData, final FormFieldValue fieldValue) {
        widgetType = formAsyncSuggestBoxData.getType();
        final TextBox textBox = new TextBox();
        textBox.setReadOnly(formAsyncSuggestBoxData.isReadOnly());
        final DefaultSuggestionDisplay suggestionDisplay = new DefaultSuggestionDisplay();
        final String popupStyle = formAsyncSuggestBoxData.getItemsStyle();
        if (popupStyle != null && popupStyle.length() > 0) {
            suggestionDisplay.setPopupStyleName(popupStyle);
        }
        final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
        for (final FormFieldAvailableValue availableValue : formAsyncSuggestBoxData.getAvailableValues()) {
            oracle.add(availableValue.getValue());
        }
        asyncSuggestBox = new SuggestBox(oracle, textBox, suggestionDisplay);
        int refreshDelay = formAsyncSuggestBoxData.getDelayMillis();
        if (refreshDelay <= 0) {
            refreshDelay = DEFAULT_REFRESH_DELAY;
        }
        asyncSuggestBox.addKeyUpHandler(new AsyncSuggestBoxKeyUpHandler(refreshDelay));
        asyncSuggestBox.addKeyPressHandler(new AsyncSuggestBoxKeyPressHandler());
        asyncSuggestBox.setValue(getStringValue(fieldValue));
        if (formAsyncSuggestBoxData.getMaxItems() > 0) {
            asyncSuggestBox.setLimit(formAsyncSuggestBoxData.getMaxItems() - 1);
        }
        flowPanel.add(asyncSuggestBox);
    }

    /**
     * @return AsyncSuggestBox type
     */
    public WidgetType getWidgetType() {
        return widgetType;
    }

    /**
     * @return the suggest box widget
     */
    public SuggestBox getAsyncSuggestBox() {
        return this.asyncSuggestBox;
    }

    /**
     * @return async SuggestBox of value
     */
    public String getValue() {
        return asyncSuggestBox.getValue();
    }

    /**
     * Get the string value of a {@link FormFieldValue}
     * 
     * @param fieldValue the {@link FormFieldValue}
     * @return a String
     */
    protected String getStringValue(final FormFieldValue fieldValue) {
        String value = null;
        if (SupportedFieldTypes.JAVA_STRING_CLASSNAME.equals(fieldValue.getValueType())) {
            value = (String) fieldValue.getValue();
        } else if (fieldValue.getValue() != null) {
            value = fieldValue.getValue().toString();
        }
        return value;
    }

    /**
     * Set the value of the AsyncSuggestBox
     * 
     * @param fieldValue
     */
    public void setValue(final String value, boolean fireEvents) {
        asyncSuggestBox.setValue(value, fireEvents);
    }

    /**
     * Set the available values of the widget (for list widgets only)
     * 
     * @param availableValues
     */
    public void setAvailableValues(final List<FormFieldAvailableValue> availableValues, final boolean fireEvents) {

        final MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) asyncSuggestBox.getSuggestOracle();
        oracle.clear();
        for (final FormFieldAvailableValue availableValue : availableValues) {
            oracle.add(availableValue.getValue());
        }
        if (fireEvents) {
            DomEvent.fireNativeEvent(Document.get().createChangeEvent(), asyncSuggestBox);
        }
    }

    /**
     * AsyncSuggestBox KeyPressHandler
     * 
     */
    protected class AsyncSuggestBoxKeyPressHandler implements KeyPressHandler {

        /**
         * {@inheritDoc}
         */
        public void onKeyPress(KeyPressEvent event) {
            oldValue = asyncSuggestBox.getText();
        }

    }

    /**
     * AsyncSuggestBox KeyUpHandler
     * 
     */
    protected class AsyncSuggestBoxKeyUpHandler implements KeyUpHandler {

        /**
         * DelayMillis to call RPC
         */
        int delayMillis;

        /**
         * Async Suggest Box of value
         */
        String content;

        /**
         * Default constructor.
         * 
         * @param delayMillis
         */
        public AsyncSuggestBoxKeyUpHandler(final int delayMillis) {
            this.delayMillis = delayMillis;
        }

        Timer timer = new Timer() {
            @Override
            public void run() {

                FormsServiceAsync formsServiceAsync = RpcFormsServices.getFormsService();
                FormFieldValue currentFieldValue = new FormFieldValue(content, String.class.getName());
                // RPC Call
                formsServiceAsync.getFormAsyncAvailableValues(formID, contextMap, formWidget, currentFieldValue, new GetAsyncAvailableValuesHandler());
            }

        };

        /**
         * {@inheritDoc}
         */
        public void onKeyUp(KeyUpEvent event) {
            if (!event.isUpArrow() && !event.isDownArrow()) {
                ((DefaultSuggestionDisplay) asyncSuggestBox.getSuggestionDisplay()).hideSuggestions();
            }
            timer.cancel();
            content = asyncSuggestBox.getText();
            if (content.trim().length() > 0 && !content.equals(oldValue))
                timer.schedule(delayMillis);

        }

    }

    /**
     * Handler for available values update
     * 
     */
    protected class GetAsyncAvailableValuesHandler implements AsyncCallback<List<FormFieldAvailableValue>> {

        public void onFailure(final Throwable caught) {
            MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) asyncSuggestBox.getSuggestOracle();
            oracle.clear();
            GWT.log("Async SuggestBox KeyUpHandler", caught);
        }

        public void onSuccess(final List<FormFieldAvailableValue> result) {
            MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) asyncSuggestBox.getSuggestOracle();
            oracle.clear();
            for (final FormFieldAvailableValue resultValue : result) {
                oracle.add(resultValue.getValue());
            }
            asyncSuggestBox.showSuggestionList();
        }
    }

}
