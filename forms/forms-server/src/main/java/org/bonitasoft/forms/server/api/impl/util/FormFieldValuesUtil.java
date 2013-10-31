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
package org.bonitasoft.forms.server.api.impl.util;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.forms.client.model.FormFieldAvailableValue;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.WidgetType;
import org.bonitasoft.forms.server.exception.FormNotFoundException;
import org.bonitasoft.forms.server.exception.FormServiceProviderNotFoundException;
import org.bonitasoft.forms.server.provider.FormServiceProvider;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderFactory;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;

/**
 * @author Anthony Birembaut, Haojie Yuan
 *
 */
public class FormFieldValuesUtil {
    
    protected static final String WIDGET_LABEL = "label";

    protected static final String EXPRESSION_KEY_SEPARATOR = ":";

    protected static final String WIDGET_TITLE = "title";

    protected static final String WIDGET_SUBTITLE = "subtitle";

    protected static final String WIDGET_TOOLTIP = "tooltip";

    protected static final String WIDGET_DISPLAY_CONDITION = "display-condition";

    protected static final String WIDGET_VALUE = "value";

    protected static final String WIDGET_AVAILABLE_VALUES = "available-value";

    protected static final String WIDGET_VALUE_COLUMN_INDEX = "value-column-index";

    protected static final String WIDGET_MAX_COLUMNS = "max-columns";

    protected static final String WIDGET_MIN_COLUMNS = "min-columns";

    protected static final String WIDGET_MAX_ROWS = "max-rows";

    protected static final String WIDGET_MIN_ROWS = "min-rows";

    protected static final String WIDGET_VERTICAL_HEADER = "verical-header";

    protected static final String WIDGET_HORIZONTAL_HEADER = "horizontal-header";
    
    protected static final String EXPRESSION_KEY = "${";
    
    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(FormFieldValuesUtil.class.getName());
        
    /**
     * default dateformat pattern
     */
    protected String defaultDateFormatPattern;
    
    /**
     * Build a field value object from the process definition
     * @param value the value retrieved from the engine
     * @param formWidget the {@link FormWidget} associated with this field value
     * @return a {@link FormFieldValue} object
     */
    public FormFieldValue getFieldValue(final Object value, final FormWidget formWidget, final Locale locale) {        
        FormFieldValue fieldValue = null;
        if (value != null) {
            // deals with the types whose corresponding form fields
            // input and output value is not a string
            if (value instanceof Date) {
                String formatPattern = null;
                if (formWidget.getDisplayFormat() != null && formWidget.getDisplayFormat().length() > 0) {
                    formatPattern = formWidget.getDisplayFormat();
                } else {
                    formatPattern = defaultDateFormatPattern;
                }
                fieldValue = new FormFieldValue((Date)value, Date.class.getName(), formatPattern);
            } else if (value instanceof Collection<?>) {
                //perform several levels of toString on the items of the collection
                fieldValue = new FormFieldValue((Serializable)processCollectionValue(value), Collection.class.getName());
            } else if (value instanceof String && formWidget.getType().equals(WidgetType.DATE)) {
                final String valueStr = (String)value;
                String formatPattern = null;
                if (formWidget.getDisplayFormat() != null && formWidget.getDisplayFormat().length() > 0) {
                    formatPattern = formWidget.getDisplayFormat();
                } else {
                    formatPattern = defaultDateFormatPattern;
                }
                Date dateValue = null;
                if (valueStr.length() > 0) {
                    try {
                        final DateFormat dateFormat = new SimpleDateFormat(formatPattern, locale);
                        dateValue = dateFormat.parse(valueStr);
                    } catch (final ParseException e) {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, "The initial value for widget " + formWidget.getId() + " is not consistent with the pattern " + formatPattern, e);
                        }
                    }
                }
                fieldValue = new FormFieldValue(dateValue, Date.class.getName(), formatPattern);
            } else if (value instanceof String && formWidget.getType().equals(WidgetType.CHECKBOX)) {
                Boolean booleanValue = null;
                try {
                    booleanValue = Boolean.parseBoolean((String)value);
                } catch (final Exception e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "The initial value for widget " + formWidget.getId() + " is not consistent with a boolean.", e);
                    }
                    booleanValue = Boolean.valueOf(false);
                }
                fieldValue = new FormFieldValue(booleanValue, Boolean.class.getName());
            } else if (value instanceof Serializable) {
                fieldValue = new FormFieldValue((Serializable)value, value.getClass().getName());
            } else {
                fieldValue = new FormFieldValue(value.toString(), String.class.getName());
            }
        } else {
            fieldValue = getFieldNullValue(formWidget);
        }
        return fieldValue;
    }
    
    protected List<Serializable> processCollectionValue(final Object value) {
        final List<Serializable> valueList = new ArrayList<Serializable>();
        final Collection<?> collectionValues = (Collection<?>) value;
        for (final Object collectionValue : collectionValues) {
            if (collectionValue == null) {
                valueList.add(null);
            } else if (collectionValue instanceof Collection<?>) {
                valueList.add((Serializable)processCollectionValue(collectionValue));
            } else if (collectionValue instanceof Serializable) {
                valueList.add((Serializable)collectionValue);
            } else {
                valueList.add(collectionValue.toString());
            }
        }
        return valueList;
    }
    
    protected FormFieldValue getFieldNullValue(final FormWidget formWidget) {
        FormFieldValue fieldValue = null;
        if (formWidget.getType().equals(WidgetType.CHECKBOX)) {
            fieldValue = new FormFieldValue(Boolean.FALSE, Boolean.class.getName());
        } else if (formWidget.getType().equals(WidgetType.LISTBOX_SIMPLE) || formWidget.getType().equals(WidgetType.RADIOBUTTON_GROUP)) {
            if (formWidget.getAvailableValues().size() > 0) {
                fieldValue = new FormFieldValue(formWidget.getAvailableValues().get(0).getValue(), String.class.getName());
            } else {
                fieldValue = new FormFieldValue("", String.class.getName());
            }
        } else if (formWidget.getType().equals(WidgetType.DATE)) {
            String formatPattern = null;
            if (formWidget.getDisplayFormat() != null && formWidget.getDisplayFormat().length() > 0) {
                formatPattern = formWidget.getDisplayFormat();
            } else {
                formatPattern = defaultDateFormatPattern;
            }
            fieldValue = new FormFieldValue(null, Date.class.getName(), formatPattern);
        } else if (formWidget.getType().equals(WidgetType.DURATION)) {
            fieldValue = new FormFieldValue(Long.valueOf(0), Long.class.getName());
        } else {
            fieldValue = new FormFieldValue(null, Serializable.class.getName());
        }
        return fieldValue;
    }
    
    /**
     * Build a FormFieldAvailableValue List from a String {@link Collection}
     * @param collection
     * @return a List of {@link FormFieldAvailableValue}
     */
    protected List<FormFieldAvailableValue> getAvailableValuesFromCollection(final Collection<?> collection) {
        final List<FormFieldAvailableValue> availableValues = new ArrayList<FormFieldAvailableValue>();
        for (final Object availableValue : collection) {
            final String availableValueStr = getStringValue(availableValue);
            availableValues.add(new FormFieldAvailableValue(availableValueStr, availableValueStr));
        }
        return availableValues;
    }
    
    /**
     * Build a FormFieldAvailableValue List from a {@link Map}
     * @param availableValuesMap
     * @return a List of {@link FormFieldAvailableValue}
     */
    protected List<FormFieldAvailableValue> getAvailableValuesFromMap(final Map<?, ?> availableValuesMap) {
        final List<FormFieldAvailableValue> availableValues = new ArrayList<FormFieldAvailableValue>();
        for (final Entry<?, ?> availableValueEntry : availableValuesMap.entrySet()) {
            final Object key = availableValueEntry.getKey();
            final String keyStr = getStringValue(key);
            final Object value = availableValueEntry.getValue();
            final String valueStr = getStringValue(value);
            availableValues.add(new FormFieldAvailableValue(keyStr, valueStr));
        }
        return availableValues;
    }

    /**
     * Build a FormFieldAvailableValue List from a {@link Map} or a {@link List} and set it in the widget
     * @param availableValuesObject  the {@link Map} or {@link List} of values
     * @param widget the widget to set
     * @return a List of {@link FormFieldAvailableValue}
     */
    public List<FormFieldAvailableValue> getAvailableValues(final Object availableValuesObject, final FormWidget widget) {

        List<FormFieldAvailableValue> availableValuesList = new ArrayList<FormFieldAvailableValue>();
        try {
            availableValuesList = getAvailableValues(availableValuesObject);
        } catch (IllegalArgumentException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "The available values expression for widget " + widget.getId() + " should return a Collection or a Map");
            }
        }
        return availableValuesList;
    }
    
    /**
     * Build a FormFieldAvailableValue List from a {@link Map} or a {@link List} and set it in the widget
     * @param availableValuesObject  the {@link Map} or {@link List} of values
     * @param widget the widget to set
     * @return a List of {@link FormFieldAvailableValue}
     * @throws IllegalArgumentException
     */
    protected List<FormFieldAvailableValue> getAvailableValues(final Object availableValuesObject) throws IllegalArgumentException {

        List<FormFieldAvailableValue> availableValuesList = null;
        if (availableValuesObject != null && availableValuesObject instanceof Collection<?>) {
            final Collection<?> availableValuesCollection = (Collection<?>)availableValuesObject;
            availableValuesList = getAvailableValuesFromCollection(availableValuesCollection);
        } else if (availableValuesObject != null && availableValuesObject instanceof Map<?, ?>) {
            final Map<?, ?> availableValuesMap = (Map<?, ?>)availableValuesObject;
            availableValuesList = getAvailableValuesFromMap(availableValuesMap);
        } else {
            throw new IllegalArgumentException();
        }
        return availableValuesList;
    }

    /**
     * Build a FormFieldAvailableValue List of List from a {@link List} of {@link Map} or a {@link List} of {@link List} and set it in the widget
     * @param availableValuesObject  the {@link List} of {@link Map} or {@link List} of {@link List} of values
     * @param widget the widget to set
     * @return a List of List of {@link FormFieldAvailableValue}
     */
    public List<List<FormFieldAvailableValue>> getTableAvailableValues(final Object tableAvailableValuesObject, final FormWidget widget) {

        final List<List<FormFieldAvailableValue>> tableAvailableValuesList = new ArrayList<List<FormFieldAvailableValue>>();
        try {
            final Collection<?> availableValuesObjects = (Collection<?>)tableAvailableValuesObject;
            for (final Object availableValuesObject : availableValuesObjects) {
                tableAvailableValuesList.add(getAvailableValues(availableValuesObject));
            }
        } catch (final ClassCastException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "The available values expression for widget " + widget.getId() + " should return a Collection of Collections or Maps", e);
            }
        }
        return tableAvailableValuesList;
    }
        
    /**
     * Retrieve the String value of an object
     * @param object the object
     * @return the String representation of this Object
     */
    protected String getStringValue(final Object object) {
        if (object != null) {
            return object.toString();
        }
        return null;
    }
    
    /**
     * Get display condition
     * @param conditionExpression
     * @param condition
     * @throws FormNotFoundException
     * @throws FormServiceProviderNotFoundException 
     */
    protected String getDisplayConditionStr(final String conditionExpression, final Object condition) throws FormNotFoundException, FormServiceProviderNotFoundException {
        if (conditionExpression != null && conditionExpression.length() > 0) {
            if (Boolean.FALSE.toString().equals(conditionExpression)) {
                return conditionExpression;
            } else {
                if (condition != null) {
                    return condition.toString();
                } else {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, "the display condition expression returns a null value: " + conditionExpression);
                    }
                    return Boolean.FALSE.toString();
                }
            }
        }
        return Boolean.TRUE.toString();
    }
    
    /**
     * Add the widget value to evaluate to the Map of expression to evaluated
     * @param formWidget the widget
     * @param expressionsToEvaluate the map of expressions to evaluate
     * @param context the context including the URL parameters
     */
    protected void addWidgetValueExpressionToEvaluate(final FormWidget formWidget, final Map<String, String> expressionsToEvaluate, final Map<String, Object> context) {
        boolean isEditMode = false;
        if (context.get(FormServiceProviderUtil.IS_EDIT_MODE) != null) {
            isEditMode = Boolean.valueOf(context.get(FormServiceProviderUtil.IS_EDIT_MODE).toString());
        }
        if (!isEditMode && formWidget.getVariableBound() != null) {
            expressionsToEvaluate.put(formWidget.getId() + EXPRESSION_KEY_SEPARATOR + WIDGET_VALUE, formWidget.getVariableBound());
        } else if (isEditMode || formWidget.isViewPageWidget()) {
            expressionsToEvaluate.put(formWidget.getId() + EXPRESSION_KEY_SEPARATOR + WIDGET_VALUE, formWidget.getInitialValueExpression());
        }
    }
    
    
    /**
     * Generate the Map of groovy expressions to evaluate for a widget
     * @param formWidget the widget
     * @param context the context including the URL parameters
     * @return the Map of expressions to evaluate
     */
    protected Map<String, String> getWidgetExpressions(final FormWidget formWidget, final Map<String, Object> context) {
        String widgetId = formWidget.getId();
        Map<String, String> expressionsToEvaluate = new HashMap<String, String>();
        if (formWidget.getLabel() != null) {
            expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_LABEL, formWidget.getLabel());
        }
        if (formWidget.getTitle() != null) {
            expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_TITLE, formWidget.getTitle());
        }
        if (formWidget.getSubTitle() != null) {
            final String label = formWidget.getSubTitle().getLabel();
            if (label != null) {
                expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_SUBTITLE, label);
            }
        }
        if (formWidget.getPopupToolTip() != null) {
            expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_TOOLTIP, formWidget.getPopupToolTip());
        }
        expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_DISPLAY_CONDITION, formWidget.getDisplayConditionExpression());
        
        addWidgetValueExpressionToEvaluate(formWidget, expressionsToEvaluate, context);
        if (formWidget.getAvailableValuesExpression() != null) {
            expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_AVAILABLE_VALUES, formWidget.getAvailableValuesExpression());
        }
        if (WidgetType.TABLE.equals(formWidget.getType()) || WidgetType.EDITABLE_GRID.equals(formWidget.getType())) {
            if (formWidget.getValueColumnIndexExpression() != null) {
                expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_VALUE_COLUMN_INDEX, formWidget.getValueColumnIndexExpression());
            }
            if (formWidget.getMaxColumnsExpression() != null) {
                expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_MAX_COLUMNS, formWidget.getMaxColumnsExpression());
            }
            if (formWidget.getMinColumnsExpression() != null) {
                expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_MIN_COLUMNS, formWidget.getMinColumnsExpression());
            }
            if (formWidget.getMaxRowsExpression() != null) {
                expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_MAX_ROWS, formWidget.getMaxRowsExpression());
            }
            if (formWidget.getMinRowsExpression() != null) {
                expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_MIN_ROWS, formWidget.getMinRowsExpression());
            }
            if (formWidget.getVerticalHeaderExpression() != null) {
                expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_VERTICAL_HEADER, formWidget.getVerticalHeaderExpression());
            }
            if (formWidget.getHorizontalHeaderExpression() != null) {
                expressionsToEvaluate.put(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_HORIZONTAL_HEADER, formWidget.getHorizontalHeaderExpression());
            }
        }
        return expressionsToEvaluate;
    }
    
    /**
     * Set the values of the form widget
     * @param formWidget the widget
     * @param previousPagesFields
     * @param locale
     * @param isCurrentValue
     * @param context the context including the URL parameters
     * @throws FormNotFoundException 
     * @throws FormServiceProviderNotFoundException 
     */
    public void setFormWidgetValues(final FormWidget formWidget, final Map<String, Object> evaluatedExpressions, final Map<String, Object> context) throws FormNotFoundException, FormServiceProviderNotFoundException {
        String widgetId = formWidget.getId();
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        final Locale locale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
        formWidget.setLabel(getStringValue(evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_LABEL)));
        formWidget.setTitle(getStringValue(evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_TITLE)));
        if (formWidget.getSubTitle() != null) {
            formWidget.getSubTitle().setLabel(getStringValue(evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_SUBTITLE)));
        }
        formWidget.setPopupToolTip(getStringValue(evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_TOOLTIP)));
        formWidget.setDisplayConditionExpression(getDisplayConditionStr(formWidget.getDisplayConditionExpression(), evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_DISPLAY_CONDITION)));
        Object value = evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_VALUE);
        if (formWidget.getType().name().startsWith("FILE") || (formWidget.getType().equals(WidgetType.IMAGE) && formWidget.isDisplayAttachmentImage())) {
            String fileName = null;
            String attachmentName = null;
            if (value != null) {
                attachmentName = value.toString();
                fileName = formServiceProvider.getAttachmentFileName(attachmentName, context);
            }
            final FormFieldValue formFieldValue = new FormFieldValue(fileName, File.class.getName());
            formFieldValue.setAttachmentName(attachmentName);
            formWidget.setInitialFieldValue(formFieldValue);
        } else if (!formWidget.getType().name().startsWith("BUTTON")) {
            // convert the value object returned into a FormFieldValue object.
            formWidget.setInitialFieldValue(getFieldValue(value, formWidget, locale));
            //set the available values list from a groovy expression for listboxes, radiobutton groups and checkbox groups
            final Object availableValuesObject = evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_AVAILABLE_VALUES);
            if (availableValuesObject != null) {
                if (formWidget.getType().equals(WidgetType.TABLE)) {
                    final List<List<FormFieldAvailableValue>> availableValues = getTableAvailableValues(availableValuesObject, formWidget);
                    formWidget.setTableAvailableValues(availableValues);
                } else {
                    final List<FormFieldAvailableValue> availableValues = getAvailableValues(availableValuesObject, formWidget);
                    formWidget.setAvailableValues(availableValues);
                }
            }
        }
    }
    
    /**
     * Set the tables parameters
     * @param formWidget the widget
     * @param evaluatedExpressions the map of evaluated expressions
     * @param context the context including the URL parameters
     * @throws FormNotFoundException 
     * @throws FormServiceProviderNotFoundException 
     */
    @SuppressWarnings("unchecked")
    public void setTablesParams(final FormWidget formWidget, final Map<String, Object> evaluatedExpressions, final Map<String, Object> context) throws FormNotFoundException, FormServiceProviderNotFoundException {
        String widgetId = formWidget.getId();
        if (WidgetType.TABLE.equals(formWidget.getType()) || WidgetType.EDITABLE_GRID.equals(formWidget.getType())) {
            String valueColumnIndexStr = null;    
            final Object valueColumnIndex = evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_VALUE_COLUMN_INDEX);
            if (valueColumnIndex != null) {
                valueColumnIndexStr = valueColumnIndex.toString();
            }
            formWidget.setValueColumnIndexExpression(valueColumnIndexStr);
            String maxColumnsStr = null;
            final Object maxColumns = evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_MAX_COLUMNS);
            if (maxColumns != null) {
                maxColumnsStr = maxColumns.toString();
            }
            formWidget.setMaxColumnsExpression(maxColumnsStr);
            String minColumnsStr = null;
            final Object minColumns = evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_MIN_COLUMNS);
            if (minColumns != null) {
                minColumnsStr = minColumns.toString();
            }
            formWidget.setMinColumnsExpression(minColumnsStr);
            String maxRowsStr = null;
            final Object maxRows = evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_MAX_ROWS);
            if (maxRows != null) {
                maxRowsStr = maxRows.toString();
            }
            formWidget.setMaxRowsExpression(maxRowsStr);
            String minRowsStr = null;
            final Object minRows = evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_MIN_ROWS);
            if (minRows != null) {
                minRowsStr = minRows.toString();
            }
            formWidget.setMinRowsExpression(minRowsStr);
            final Object verticalHeader = evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_VERTICAL_HEADER);
            List<String> verticalHeaderList = null;
            if (verticalHeader != null) {
                try {
                    verticalHeaderList = new ArrayList<String>((Collection<String>)verticalHeader);
                } catch (final ClassCastException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "The vertical header expression for widget " + formWidget.getId() + " should return a Collection.", e);
                    }
                }
            }
            formWidget.setVerticalHeader(verticalHeaderList);
            final Object horizontalHeader = evaluatedExpressions.get(widgetId + EXPRESSION_KEY_SEPARATOR + WIDGET_HORIZONTAL_HEADER);
            List<String> horizontalHeaderList = null;
            if (horizontalHeader != null) {
                try {
                    horizontalHeaderList = new ArrayList<String>((Collection<String>)horizontalHeader);
                } catch (final ClassCastException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "The horizontal header expression for widget " + formWidget.getId() + " should return a Collection.", e);
                    }
                }
            }
            formWidget.setHorizontalHeader(horizontalHeaderList);
        }
    }

    /**
     * set the widget values of a form page
     * @param widgets the widgets of the page
     * @param context the context including the URL parameters
     * @throws FormServiceProviderNotFoundException 
     * @throws FormNotFoundException 
     */
    public void setFormWidgetsValues(List<FormWidget> widgets, Map<String, Object> context) throws FormNotFoundException, FormServiceProviderNotFoundException {
        final Map<String, String> expressionsToEvaluate = new HashMap<String, String>();
        for (final FormWidget formWidget : widgets) {
            expressionsToEvaluate.putAll(getWidgetExpressions(formWidget, context));
        }
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        final Map<String, Object> evaluatedExpressions = formServiceProvider.resolveExpressions(expressionsToEvaluate, context);
        for (final FormWidget formWidget : widgets) {
            setFormWidgetValues(formWidget, evaluatedExpressions, context);
            setTablesParams(formWidget, evaluatedExpressions, context);
        }
    }
    
    public void clearExpressionsOrConnectors(final String formID, final String pageID, final String locale, final Date processDeployementDate, final List<FormWidget> formWidgets) {
        
        for (FormWidget formWidget : formWidgets) {
            if (formWidget.getInitialValueExpression() != null && formWidget.getInitialValueExpression().contains(EXPRESSION_KEY)) {
                formWidget.setInitialValueExpression(null);
            }

            if (formWidget.getAvailableValuesExpression() != null && formWidget.getAvailableValuesExpression().contains(EXPRESSION_KEY)) {
                formWidget.setAvailableValuesExpression(null);
            }
        }

    }

}
