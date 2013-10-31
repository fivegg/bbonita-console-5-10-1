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
package org.bonitasoft.forms.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object representing a widget to place in a page of a task form flow
 * 
 * @author Anthony Birembaut
 */
public class FormWidget implements Serializable, Comparable<FormWidget> {

    /**
     * UID
     */
    private static final long serialVersionUID = -2420889539040109129L;
    
    /**
     * Possible label positions
     */
    public static enum ItemPosition {LEFT, TOP, RIGHT, BOTTOM};
    
    /**
     * Possible selection mode
     */
    public static enum SelectMode {NONE, SINGLE, MULTIPLE};

    /**
     * ID of the widget
     */
    private String id;

    /**
     * label of the widget
     */
    private String label;

    /**
     * CSS classes for the label
     */
    private String labelStyle;
    
    /**
     * CSS classes for the input
     */
    private String inputStyle;
    
    /**
     * label position
     */
    private ItemPosition labelPosition;

    /**
     * max length if the widget is a text input or a text area
     */
    private int maxLength;
    
    /**
     * max height if the widget is a text area
     */
    private int maxHeight;

    /**
     * type of the widget
     */
    private WidgetType type;

    /**
     * CSS classes for the widget
     */
    private String style;

    /**
     * title of the widget
     */
    private String title;
    
    /**
     * indicate whether a field is mandatory or not
     */
    private boolean mandatory;
    
    /**
     * indicates that this field is a one to one variable bound
     */
    private String variableBound;
    
    /**
     * indicates that this widget is part of a view page
     */
    private boolean viewPageWidget;

    /**
     * initial value of the field (under the form of a groovy expression)
     */
    private String initialValueExpression = null;
    
    /**
     * Connectors to execute to initialize the field
     */
    private List<Connector> initialValueConnectors = null;
    
    /**
     * initial value of the field interpreted
     */
    private FormFieldValue initialFieldValue;

    /**
     * List of validators to apply to the field
     */
    private List<FormValidator> validators;
    
    /**
     * The date display format
     */
    private String displayFormat;
    
    /**
     * CSS class names for the items of a radiobutton or checkbox group widget
     */
    private String itemsStyle;
    
    /**
     * Widget available values useful for selectboxes and radiobuttons
     */
    private List<FormFieldAvailableValue> availableValues;
    
    /**
     * table available values
     */
    private List<List<FormFieldAvailableValue>> tableAvailableValues;
    
    /**
     * Widget available values expression used to evaluate to fill in the available values. The evaluation of the expression has to return a collection
     */
    private String availableValuesExpression = null;
    
    /**
     * Connectors to execute to initialize the availableValues
     */
    private List<Connector> availableValuesConnectors = null;
    
    /**
     * if true indicates that a label should replace the button
     */
    private boolean labelButton;
    
    /**
     * if true indicates that HTML is allowed in the label
     */
    private boolean allowHTMLInLabel;
    
    /**
     * if true indicates that  HTML is allowed in the field
     */
    private boolean allowHTMLInField;
    
    /**
     * The style for the img element of the widgets containing an image
     */
    private String imageStyle;
    
    /**
     * The style for the table element of the widgets containing a table
     */
    private String tableStyle;
    
    /**
     * The style for a table widget's cells
     */
    private String cellsStyle;
    
    /**
     * The horizontal header for tables as an expression
     */
    private String horizontalHeaderExpression = null;
    
    /**
     * The horizontal header for tables
     */
    private List<String> horizontalHeader;
    
    /**
     * The horizontal header for tables as an expression
     */
    private String verticalHeaderExpression = null;
    
    /**
     * The vertical header for tables
     */
    private List<String> verticalHeader;
    
    /**
     * The Headings syle for table widgets
     */
    private String headingsStyle;
    
    /**
     * indicates whether the left column of a table widget should be considered as header or not
     */
    private boolean leftHeadings;
    
    /**
     * indicates whether the top column of a table widget should be considered as header or not
     */
    private boolean topHeadings;
    
    /**
     * indicates whether the right column of a table widget should be considered as header or not
     */
    private boolean rightHeadings;
    
    /**
     * indicates whether the bottom row of a table widget should be considered as header or not
     */
    private boolean bottomHeadings;
    
    /**
     * Selection mode for static tables
     */
    private SelectMode selectMode;
    
    /**
     * CSS classes for the selected items
     */
    private String selectedItemsStyle;
    
    /**
     * indicates if the row number is variable or not
     */
    private boolean isVariableRowNumber;
    
    /**
     * indicates if the column number is variable or not
     */
    private boolean isVariableColumnNumber;
    
    /**
     * The max number of rows for tables
     */
    private String maxRowsExpression;
    
    /**
     * The min number of rows for tables
     */
    private String minRowsExpression;
    
    /**
     * The max number of columns for tables
     */
    private String maxColumnsExpression;
    
    /**
     * The min number of columns for tables
     */
    private String minColumnsExpression;
    
    /**
     * specify the index of column which is used as the value of the selected row(s) (for table widgets)
     */
    private String valueColumnIndexExpression;
    
    /**
     * if true indicates that a label should replace the button
     */
    private boolean readOnly;
    
    /**
     * if true indicates that the widget can be duplicated
     */
    private boolean multiple;
    
    /**
     * The max number of instances
     */
    private String maxInstancesExpression;
    
    /**
     * The min number of instances
     */
    private String minInstancesExpression;
    
    /**
     * The the iterator name
     */
    private String iteratorName;
    
    /**
     * The max number of items
     */
    private int maxItems;
    
    /**
     * try to display a preview if the attachment is an image (for file download widgets)
     */
    private boolean displayAttachmentImage;

    /**
     * Condition to display or not a data field
     */
    private String displayConditionExpression;
    
    /**
     * HTML attributes
     */
    private Map<String, String> htmlAttributes;
    
    /**
     * the add item label
     */
    private String addItemLabel;
    
    /**
     * the add item title
     */
    private String addItemTitle;
    
    /**
     * the add item label style
     */
    private String addItemLabelStyle;
    
    /**
     * the remove item label
     */
    private String removeItemLabel;
    
    /**
     * the remove item label
     */
    private String removeItemTitle;
    
    /**
     * the remove item label style
     */
    private String removeItemLabelStyle;
    
    /**
     * child widgets
     */
    private List<FormWidget> childWidgets;
    
    /**
     * The widget position inside a group
     */
    private WidgetPosition widgetPositionInGroup;
    
    /**
     * rows styles for group widgets
     */
    private Map<Integer, String> rowsStyles;
    
    /**
     * columns styles for group widgets
     */
    private Map<Integer, String> columnsStyles;
    
    /**
     * List of ids of widgets this widget depends on
     */
    private Set<String> dependsOnWidgets;
    
    /**
     * List of ids of widgets this widget is updated by
     */
    private Set<String> isUpdatedByWidgets;
    
    /**
     * Condition to display or not a data field before an event
     */
    private String displayBeforeEventExpression;
    
    /**
     * Condition to display or not a data field after an event
     */
    private String displayAfterEventExpression;
    
    /**
     * Widget available values after an event triggered by a dependency
     */
    private List<FormFieldAvailableValue> availableValuesAfterEvent;
    
    /**
     * table available values after an event triggered by a dependency
     */
    private List<List<FormFieldAvailableValue>> tableAvailableValuesAfterEvent;
    
    /**
     * Widget available values expression after an event triggered by a dependency
     */
    private String availableValuesAfterEventExp;
    
    /**
     * Connectors to execute to change the field's available values after a dependency event
     */
    private List<Connector> availableValuesAfterEventConnectors;
    
    /**
     * value of the field after an event triggered by a dependency
     */
    private String valueAfterEventExp;
    
    /**
     * Connectors to execute to change the field's value after a dependency event
     */
    private List<Connector> valueAfterEventConnectors;
    
    /**
     * Display asnySuggestBox delay time 
     */
    private int delayMillis;
    
    /**
     * Display a tool tip
     */
    private String popupToolTip;
    
    /**
     * display a sub title
     */
    private FormSubTitle subTitle;
    
    /**
     * use this id can get the initial value expression
     */
    private String initialValueExpressionId;
    
    /**
     * use this id can get the initial value connectors
     */
    private String initialValueConnectorsId;
    
    /**
     * use this id can get the available values expression
     */
    private String availableValuesExpressionId;
    
    /**
     * use this id can get the available values connectors
     */
    private String availableValuesConnectorsId;
    
    /**
     * use this id can get the value after event expression
     */
    private String valueAfterEventExpId;
    
    /**
     * use this id can get the value after event connectors
     */
    private String valueAfterEventConnectorsId;
    
    /**
     * use this id can get the available values after event expression
     */
    private String availableValuesAfterEventExpId;
    
    /**
     * use this id can get the available values after event connectors
     */
    private String availableValuesAfterEventConnectorsId;
    
    /**
     * Constructor
     * @param id
     * @param label
     * @param maxLength
     * @param maxHeight
     * @param type
     * @param style
     * @param labelStyle
     * @param inputStyle
     * @param labelPosition
     * @param title
     * @param mandatory
     * @param popupToolTip 
     */
    public FormWidget(final String id, final String label, final int maxLength, final int maxHeight, final WidgetType type, final String style, 
            final String labelStyle, final String inputStyle, final ItemPosition labelPosition, final String title, final boolean mandatory) {
        this.id = id;
        this.label = label;
        this.maxLength = maxLength;
        this.maxHeight = maxHeight;
        this.type = type;
        this.style = style;
        this.labelStyle = labelStyle;
        this.inputStyle = inputStyle;
        this.title = title;
        this.mandatory = mandatory;
        this.labelPosition = labelPosition;
    }

    /**
     * Default Constructor
     */
    public FormWidget() {
        super();
        // Mandatory for serialization
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }
    
    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(final int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public WidgetType getType() {
        return type;
    }

    public void setType(final WidgetType type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(final String style) {
        this.style = style;
    }
    
    public String getInitialValueExpression() {
        return initialValueExpression;
    }

    public void setInitialValueExpression(final String initialValueExpression) {
        this.initialValueExpression = initialValueExpression;
    }

    public List<FormValidator> getValidators() {
        if (validators == null) {
            validators = new ArrayList<FormValidator>();
        }
        return validators;
    }

    public void setValidators(final List<FormValidator> validators) {
        this.validators = validators;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(final String labelStyle) {
        this.labelStyle = labelStyle;
    }

    public void setInputStyle(final String inputStyle) {
        this.inputStyle = inputStyle;
    }

    public String getInputStyle() {
        return inputStyle;
    }
    
    public ItemPosition getLabelPosition() {
        return labelPosition;
    }

    public void setLabelPosition(final ItemPosition labelPosition) {
        this.labelPosition = labelPosition;
    }

    public List<FormFieldAvailableValue> getAvailableValues() {
        if (availableValues == null) {
            availableValues = new ArrayList<FormFieldAvailableValue>();
        }
        return availableValues;
    }

    public void setAvailableValues(final List<FormFieldAvailableValue> availableValues) {
        this.availableValues = availableValues;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    public FormFieldValue getInitialFieldValue() {
        return initialFieldValue;
    }

    public void setInitialFieldValue(final FormFieldValue initialFieldValue) {
        this.initialFieldValue = initialFieldValue;
    }

    public String getDisplayFormat() {
        return displayFormat;
    }

    public void setDisplayFormat(final String displayFormat) {
        this.displayFormat = displayFormat;
    }

    public String getVariableBound() {
        return variableBound;
    }

    public void setVariableBound(final String variableBound) {
        this.variableBound = variableBound;
    }

    public boolean isViewPageWidget() {
        return viewPageWidget;
    }

    public void setViewPageWidget(final boolean viewPageWidget) {
        this.viewPageWidget = viewPageWidget;
    }

    public String getItemsStyle() {
        return itemsStyle;
    }

    public void setItemsStyle(final String itemsStyle) {
        this.itemsStyle = itemsStyle;
    }

    public String getAvailableValuesExpression() {
        return availableValuesExpression;
    }

    public void setAvailableValuesExpression(final String availableValuesExpression) {
        this.availableValuesExpression = availableValuesExpression;
    }
    
    public boolean isLabelButton() {
        return labelButton;
    }

    public void setLabelButton(final boolean labelButton) {
        this.labelButton = labelButton;
    }

    public boolean allowHTMLInLabel() {
        return allowHTMLInLabel;
    }

    public void setAllowHTMLInLabel(final boolean allowHTMLInLabel) {
        this.allowHTMLInLabel = allowHTMLInLabel;
    }

    public boolean allowHTMLInField() {
        return allowHTMLInField;
    }

    public void setAllowHTMLInField(final boolean allowHTMLInField) {
        this.allowHTMLInField = allowHTMLInField;
    }
    
    public String getImageStyle() {
		return imageStyle;
	}

	public void setImageStyle(final String imageStyle) {
		this.imageStyle = imageStyle;
	}

	public String getHeadingsStyle() {
        return headingsStyle;
    }

    public void setHeadingsStyle(final String headingsStyle) {
        this.headingsStyle = headingsStyle;
    }

    public boolean hasLeftHeadings() {
        return leftHeadings;
    }

    public void setLeftHeadings(final boolean leftHeadings) {
        this.leftHeadings = leftHeadings;
    }

    public boolean hasTopHeadings() {
        return topHeadings;
    }

    public void setTopHeadings(final boolean topHeadings) {
        this.topHeadings = topHeadings;
    }

    public boolean hasRightHeadings() {
        return rightHeadings;
    }

    public void setRightHeadings(final boolean rightHeadings) {
        this.rightHeadings = rightHeadings;
    }

    public boolean hasBottomHeadings() {
        return bottomHeadings;
    }

    public void setBottomHeadings(final boolean bottomHeadings) {
        this.bottomHeadings = bottomHeadings;
    }

    public String getTableStyle() {
		return tableStyle;
	}

	public void setTableStyle(final String tableStyle) {
		this.tableStyle = tableStyle;
	}

	public String getCellsStyle() {
        return cellsStyle;
    }

    public void setCellsStyle(final String cellsStyle) {
        this.cellsStyle = cellsStyle;
    }

    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setMultiple(final boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isMultiple() {
        return multiple;
    }
    
    public String getMaxInstancesExpression() {
        return maxInstancesExpression;
    }

    public void setMaxInstancesExpression(final String maxInstancesExpression) {
        this.maxInstancesExpression = maxInstancesExpression;
    }

    public String getMinInstancesExpression() {
        return minInstancesExpression;
    }

    public void setMinInstancesExpression(final String minInstancesExpression) {
        this.minInstancesExpression = minInstancesExpression;
    }
    
    public String getIteratorName() {
		return iteratorName;
	}

	public void setIteratorName(final String iteratorName) {
		this.iteratorName = iteratorName;
	}

	public void setDisplayAttachmentImage(final boolean displayAttachmentImage) {
        this.displayAttachmentImage = displayAttachmentImage;
    }

    public boolean isDisplayAttachmentImage() {
        return displayAttachmentImage;
    }

    public String getDisplayConditionExpression() {
		return displayConditionExpression;
	}

	public void setDisplayConditionExpression(final String displayConditionExpression) {
		this.displayConditionExpression = displayConditionExpression;
	}

	public String getAddItemLabel() {
        return addItemLabel;
    }

    public void setAddItemLabel(final String addItemLabel) {
        this.addItemLabel = addItemLabel;
    }

    public String getAddItemTitle() {
        return addItemTitle;
    }

    public void setAddItemTitle(final String addItemTitle) {
        this.addItemTitle = addItemTitle;
    }

    public String getAddItemLabelStyle() {
        return addItemLabelStyle;
    }

    public void setAddItemLabelStyle(final String addItemLabelStyle) {
        this.addItemLabelStyle = addItemLabelStyle;
    }

    public String getRemoveItemLabel() {
        return removeItemLabel;
    }

    public void setRemoveItemLabel(final String removeItemLabel) {
        this.removeItemLabel = removeItemLabel;
    }

    public String getRemoveItemTitle() {
        return removeItemTitle;
    }
    
    public void setRemoveItemTitle(final String removeItemTitle) {
        this.removeItemTitle = removeItemTitle;
    }
    
    public String getRemoveItemLabelStyle() {
        return removeItemLabelStyle;
    }

    public void setRemoveItemLabelStyle(final String removeItemLabelStyle) {
        this.removeItemLabelStyle = removeItemLabelStyle;
    }

    public WidgetPosition getWidgetPositionInGroup() {
        return widgetPositionInGroup;
    }

    public void setWidgetPositionInGroup(final WidgetPosition widgetPositionInGroup) {
        this.widgetPositionInGroup = widgetPositionInGroup;
    }

    public Map<Integer, String> getRowsStyles() {
        return rowsStyles;
    }

    public void setRowsStyles(final Map<Integer, String> rowsStyles) {
        this.rowsStyles = rowsStyles;
    }

    public Map<Integer, String> getColumnsStyles() {
        return columnsStyles;
    }

    public void setColumnsStyles(final Map<Integer, String> columnsStyles) {
        this.columnsStyles = columnsStyles;
    }

    public List<List<FormFieldAvailableValue>> getTableAvailableValues() {
        return tableAvailableValues;
    }

    public void setTableAvailableValues(final List<List<FormFieldAvailableValue>> tableAvailableValues) {
        this.tableAvailableValues = tableAvailableValues;
    }

    public String getHorizontalHeaderExpression() {
        return horizontalHeaderExpression;
    }

    public void setHorizontalHeaderExpression(final String horizontalHeaderExpression) {
        this.horizontalHeaderExpression = horizontalHeaderExpression;
    }

    public List<String> getHorizontalHeader() {
        return horizontalHeader;
    }

    public void setHorizontalHeader(final List<String> horizontalHeader) {
        this.horizontalHeader = horizontalHeader;
    }

    public String getVerticalHeaderExpression() {
        return verticalHeaderExpression;
    }

    public void setVerticalHeaderExpression(final String verticalHeaderExpression) {
        this.verticalHeaderExpression = verticalHeaderExpression;
    }

    public List<String> getVerticalHeader() {
        return verticalHeader;
    }

    public void setVerticalHeader(final List<String> verticalHeader) {
        this.verticalHeader = verticalHeader;
    }

    public SelectMode getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(final SelectMode selectMode) {
        this.selectMode = selectMode;
    }

    public String getSelectedItemsStyle() {
        return selectedItemsStyle;
    }

    public void setSelectedItemsStyle(final String selectedItemsStyle) {
        this.selectedItemsStyle = selectedItemsStyle;
    }

    public String getMaxRowsExpression() {
        return maxRowsExpression;
    }

    public void setMaxRowsExpression(final String maxRowsExpression) {
        this.maxRowsExpression = maxRowsExpression;
    }

    public String getMinRowsExpression() {
        return minRowsExpression;
    }

    public void setMinRowsExpression(final String minRowsExpression) {
        this.minRowsExpression = minRowsExpression;
    }

    public String getMaxColumnsExpression() {
        return maxColumnsExpression;
    }

    public void setMaxColumnsExpression(final String maxColumnsExpression) {
        this.maxColumnsExpression = maxColumnsExpression;
    }

    public String getMinColumnsExpression() {
        return minColumnsExpression;
    }

    public void setMinColumnsExpression(final String minColumnsExpression) {
        this.minColumnsExpression = minColumnsExpression;
    }
    
    public boolean isVariableRowNumber() {
        return isVariableRowNumber;
    }

    public void setVariableRowNumber(final boolean isVariableRowNumber) {
        this.isVariableRowNumber = isVariableRowNumber;
    }

    public boolean isVariableColumnNumber() {
        return isVariableColumnNumber;
    }

    public void setVariableColumnNumber(final boolean isVariableColumnNumber) {
        this.isVariableColumnNumber = isVariableColumnNumber;
    }

    public String getValueColumnIndexExpression() {
        return valueColumnIndexExpression;
    }

    public void setValueColumnIndexExpression(final String valueColumnIndexExpression) {
        this.valueColumnIndexExpression = valueColumnIndexExpression;
    }

    public void setHtmlAttributes(final Map<String, String> htmlAttributes) {
        this.htmlAttributes = htmlAttributes;
    }
    
    public Map<String, String> getHtmlAttributes() {
        if (htmlAttributes == null) {
            htmlAttributes = new HashMap<String, String>();
        }
        return htmlAttributes;
    }

    public void setChildWidgets(final List<FormWidget> childWidgets) {
        this.childWidgets = childWidgets;
    }
    
    public List<FormWidget> getChildWidgets() {
        if (childWidgets == null) {
            childWidgets = new ArrayList<FormWidget>();
        }
        return childWidgets;
    }
    
    public Set<String> getDependsOnWidgets() {
        if (dependsOnWidgets == null) {
            dependsOnWidgets = new HashSet<String>();
        }
        return dependsOnWidgets;
    }

    public void setDependsOnWidgets(final Set<String> dependsOnWidgets) {
        this.dependsOnWidgets = dependsOnWidgets;
    }

    public Set<String> getIsUpdatedByWidgets() {
        if (isUpdatedByWidgets == null) {
        	isUpdatedByWidgets = new HashSet<String>();
        }
		return isUpdatedByWidgets;
	}

	public void setIsUpdatedByWidgets(final Set<String> isUpdatedByWidgets) {
		this.isUpdatedByWidgets = isUpdatedByWidgets;
	}

	public String getDisplayBeforeEventExpression() {
		return displayBeforeEventExpression;
	}

	public void setDisplayBeforeEventExpression(final String displayBeforeEventExpression) {
		this.displayBeforeEventExpression = displayBeforeEventExpression;
	}

	public String getDisplayAfterEventExpression() {
		return displayAfterEventExpression;
	}

	public void setDisplayAfterEventExpression(final String displayAfterEventExpression) {
		this.displayAfterEventExpression = displayAfterEventExpression;
	}

	public List<FormFieldAvailableValue> getAvailableValuesAfterEvent() {
        return availableValuesAfterEvent;
    }

    public void setAvailableValuesAfterEvent(final List<FormFieldAvailableValue> availableValuesAfterEvent) {
        this.availableValuesAfterEvent = availableValuesAfterEvent;
    }

    public String getAvailableValuesAfterEventExp() {
        return availableValuesAfterEventExp;
    }

    public void setAvailableValuesAfterEventExp(final String availableValuesAfterEventExp) {
        this.availableValuesAfterEventExp = availableValuesAfterEventExp;
    }

    public List<List<FormFieldAvailableValue>> getTableAvailableValuesAfterEvent() {
        return tableAvailableValuesAfterEvent;
    }

    public void setTableAvailableValuesAfterEvent(final List<List<FormFieldAvailableValue>> tableAvailableValuesAfterEvent) {
        this.tableAvailableValuesAfterEvent = tableAvailableValuesAfterEvent;
    }

    public String getValueAfterEventExp() {
        return valueAfterEventExp;
    }

    public void setValueAfterEventExp(final String valueAfterEventExp) {
        this.valueAfterEventExp = valueAfterEventExp;
    }

    public List<Connector> getInitialValueConnectors() {
        return initialValueConnectors;
    }

    public void setInitialValueConnectors(final List<Connector> initialValueConnectors) {
        this.initialValueConnectors = initialValueConnectors;
    }

    public List<Connector> getAvailableValuesConnectors() {
        return availableValuesConnectors;
    }

    public void setAvailableValuesConnectors(final List<Connector> availableValuesConnectors) {
        this.availableValuesConnectors = availableValuesConnectors;
    }

    public List<Connector> getAvailableValuesAfterEventConnectors() {
        return availableValuesAfterEventConnectors;
    }

    public void setAvailableValuesAfterEventConnectors(final List<Connector> availableValuesAfterEventConnectors) {
        this.availableValuesAfterEventConnectors = availableValuesAfterEventConnectors;
    }

    public List<Connector> getValueAfterEventConnectors() {
        return valueAfterEventConnectors;
    }

    public void setValueAfterEventConnectors(final List<Connector> valueAfterEventConnectors) {
        this.valueAfterEventConnectors = valueAfterEventConnectors;
    }
    
    public int getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(final int maxItems) {
		this.maxItems = maxItems;
	}

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }
    
    public FormSubTitle getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(FormSubTitle subTitle) {
        this.subTitle = subTitle;
    }

	public void setPopupToolTip(String popupToolTip) {
		this.popupToolTip = popupToolTip;
	}

	public String getPopupToolTip() {
		return popupToolTip;
	}
	
    public String getInitialValueExpressionId() {
        return initialValueExpressionId;
    }

    public void setInitialValueExpressionId(String initialValueExpressionId) {
        this.initialValueExpressionId = initialValueExpressionId;
    }

    public String getInitialValueConnectorsId() {
        return initialValueConnectorsId;
    }

    public void setInitialValueConnectorsId(String initialValueConnectorsId) {
        this.initialValueConnectorsId = initialValueConnectorsId;
    }

    public String getAvailableValuesExpressionId() {
        return availableValuesExpressionId;
    }

    public void setAvailableValuesExpressionId(String availableValuesExpressionId) {
        this.availableValuesExpressionId = availableValuesExpressionId;
    }

    public String getAvailableValuesConnectorsId() {
        return availableValuesConnectorsId;
    }

    public void setAvailableValuesConnectorsId(String availableValuesConnectorsId) {
        this.availableValuesConnectorsId = availableValuesConnectorsId;
    }
	
    public String getValueAfterEventExpId() {
        return valueAfterEventExpId;
    }

    public void setValueAfterEventExpId(String valueAfterEventExpId) {
        this.valueAfterEventExpId = valueAfterEventExpId;
    }
    
    public String getValueAfterEventConnectorsId() {
        return valueAfterEventConnectorsId;
    }

    public void setValueAfterEventConnectorsId(String valueAfterEventConnectorsId) {
        this.valueAfterEventConnectorsId = valueAfterEventConnectorsId;
    }

    public String getAvailableValuesAfterEventExpId() {
        return availableValuesAfterEventExpId;
    }

    public void setAvailableValuesAfterEventExpId(String availableValuesAfterEventExpId) {
        this.availableValuesAfterEventExpId = availableValuesAfterEventExpId;
    }

    public String getAvailableValuesAfterEventConnectorsId() {
        return availableValuesAfterEventConnectorsId;
    }

    public void setAvailableValuesAfterEventConnectorsId(String availableValuesAfterEventConnectorsId) {
        this.availableValuesAfterEventConnectorsId = availableValuesAfterEventConnectorsId;
    }

    /**
     * Compare this form widget to another form widget using alphabetical order on their Id
     * 
     * {@inheritDoc}
     */
    public int compareTo(final FormWidget otherFormWidget) {
        return this.id.toLowerCase().compareTo(otherFormWidget.getId().toLowerCase());
    }

}