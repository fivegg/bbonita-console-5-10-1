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
package org.bonitasoft.forms.server.accessor.widget.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;

import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldAvailableValue;
import org.bonitasoft.forms.client.model.FormSubTitle;
import org.bonitasoft.forms.client.model.FormSubTitle.SubTitlePosition;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormValidator.ValidatorPosition;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.FormWidget.ItemPosition;
import org.bonitasoft.forms.client.model.FormWidget.SelectMode;
import org.bonitasoft.forms.client.model.WidgetType;
import org.bonitasoft.forms.server.accessor.impl.util.XMLUtil;
import org.bonitasoft.forms.server.accessor.widget.IXMLWidgetBuilder;
import org.bonitasoft.forms.server.constants.XMLForms;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Accessor used to read the page nodes in the XML definition file and retrieve the widgets and validators
 * @author Anthony Birembaut
 */
public class XMLWidgetBuilderImpl extends XMLUtil implements IXMLWidgetBuilder {
    
    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(XMLWidgetBuilderImpl.class.getName());

    /**
     * Contructor
     * @param xpathEvaluator
     */
    public XMLWidgetBuilderImpl(final XPath xpathEvaluator) {
        super(xpathEvaluator);
    }
    
    /**
     * Read a page node and return the list of {@link FormValidator} it contains
     * @param pageNode the page node
     * @return a {@link List} of {@link FormValidator} Object
     * @throws InvalidFormDefinitionException
     */
    public List<FormValidator> getPageValidators(final Node pageNode) throws InvalidFormDefinitionException {
        
        final List<FormValidator> pageValidators = new ArrayList<FormValidator>();

        final String xpath = XMLForms.PAGE_VALIDATORS + "/" + XMLForms.VALIDATOR;
        final NodeList pageValidatorNodes = getNodeListByXpath(pageNode, xpath);
        if (pageValidatorNodes == null) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Failed to parse the form definition file. query : " + xpath);
            }
        } else {
            for (int i=0; i<pageValidatorNodes.getLength(); i++) {
                final Node pageValidatorNode = pageValidatorNodes.item(i);
                final String validatorId = getStringByXpath(pageValidatorNode, "@" + XMLForms.ID);
                final String validatorClass = getStringByXpath(pageValidatorNode,XMLForms.CLASSNAME);
                final String validatorLabel = getStringByXpath(pageValidatorNode,XMLForms.LABEL);
                final String validatorStyle = getStringByXpath(pageValidatorNode,XMLForms.STYLE);
                final String validatorParameter = getStringValue(getNodeByXpath(pageValidatorNode, XMLForms.PARAMETER));
                final ValidatorPosition validatorPosition = getValidatorPositionValue(getNodeByXpath(pageValidatorNode, XMLForms.POSITION));
                final FormValidator formValidator = new FormValidator(validatorId, validatorClass, validatorLabel, validatorStyle);
                formValidator.setPosition(validatorPosition);
                formValidator.setParameter(validatorParameter);
                pageValidators.add(formValidator);
            }
        }
        return pageValidators;
    }
    
    /**
     * Read a page node and return the list of {@link FormWidget} it contains
     * @param pageNode the page node
     * @param isEditMode
     * @return a {@link List} of {@link FormWidget} Object
     */
    public List<FormWidget> getPageWidgets(final Node pageNode, final boolean isEditMode) {

        final List<FormWidget> widgets = new ArrayList<FormWidget>();

        final String xpath = XMLForms.WIDGETS + "/" + XMLForms.WIDGET;
        
        final NodeList widgetNodes = getNodeListByXpath(pageNode, xpath);
        if (widgetNodes == null) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Failed to parse the form definition file. query : " + xpath);
            }
        } else {
            for (int i=0; i<widgetNodes.getLength(); i++) {
                try {
                    final FormWidget widget = parseWidget(widgetNodes.item(i), isEditMode);
                    widget.setViewPageWidget(!isEditMode);
                    widgets.add(widget);
                } catch (final InvalidFormDefinitionException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "invalid widget definition", e);
                    }
                }
            }
        }
        return widgets;
    }
    
    /**
     * Read a widget and return a {@link FormWidget}
     * @param widgetNode the widget node
     * @param isEditMode
     * @return a {@link FormWidget} object
     * @throws InvalidFormDefinitionException
     */
    protected FormWidget parseWidget(final Node widgetNode, final boolean isEditMode) throws InvalidFormDefinitionException {

        final String id = getStringByXpath(widgetNode, "@" + XMLForms.ID);
        final WidgetType type = getWidgetTypeValue(getStringByXpath(widgetNode, "@" + XMLForms.TYPE));
        final String label = getStringValue(getNodeByXpath(widgetNode, XMLForms.LABEL));
        final int maxLength = getIntValue(getNodeByXpath(widgetNode, XMLForms.MAX_LENGTH));
        final int maxHeight = getIntValue(getNodeByXpath(widgetNode, XMLForms.MAX_HEIGHT));
        final String styleNames = getStringValue(getNodeByXpath(widgetNode, XMLForms.STYLE));
        final String labelStyleNames = getStringValue(getNodeByXpath(widgetNode, XMLForms.LABEL_STYLE));
        final String inputStyleNames = getStringValue(getNodeByXpath(widgetNode, XMLForms.INPUT_STYLE));
        final ItemPosition labelPosition = getItemPositionValue(getNodeByXpath(widgetNode, XMLForms.LABEL_POSITION));
        final String title = getStringValue(getNodeByXpath(widgetNode, XMLForms.TITLE));
        final boolean mandatory = getBooleanValue(getNodeByXpath(widgetNode, XMLForms.MANDATORY));
        
        final FormWidget formWidget = new FormWidget(id, label, maxLength, maxHeight, type, styleNames, labelStyleNames, inputStyleNames, labelPosition, title, mandatory);
        
        final Node initialValueNode = getNodeByXpath(widgetNode, XMLForms.INITIAL_VALUE + "/" + XMLForms.EXPRESSION);
        if (initialValueNode != null) {
            formWidget.setInitialValueExpression(initialValueNode.getTextContent());
        }
        final Node variableBoundNode = getNodeByXpath(widgetNode, XMLForms.VARIABLE_BOUND);
        if (variableBoundNode != null && !isEditMode) {
            formWidget.setVariableBound(variableBoundNode.getTextContent());
        }
        final boolean labelButton = getBooleanValue(getNodeByXpath(widgetNode, XMLForms.LABEL_BUTTON));
        formWidget.setLabelButton(labelButton);
        final String itemsStyleNames = getStringValue(getNodeByXpath(widgetNode, XMLForms.ITEMS_STYLE));
        formWidget.setItemsStyle(itemsStyleNames);
        
        final Node availableValuesListNode = getNodeByXpath(widgetNode, XMLForms.AVAILABLE_VALUES + "/" + XMLForms.VALUES_LIST);
        if (availableValuesListNode != null) {
            formWidget.setAvailableValues(getAvailableValues(availableValuesListNode));
        }
        
        final Node availableValuesExpressionNode = getNodeByXpath(widgetNode, XMLForms.AVAILABLE_VALUES + "/" + XMLForms.EXPRESSION);
        if (availableValuesExpressionNode != null) {
            formWidget.setAvailableValuesExpression(availableValuesExpressionNode.getTextContent());
        }
        
        final NodeList validatorNodes = getNodeListByXpath(widgetNode, XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR);
        final List<FormValidator> validators = new ArrayList<FormValidator>();
        for (int i=0; i<validatorNodes.getLength(); i++) {
            final Node validatorNode =  validatorNodes.item(i);
            final String validatorId = getStringByXpath(validatorNode, "@" + XMLForms.ID);
            final String validatorClass = getStringValue(getNodeByXpath(validatorNode, XMLForms.CLASSNAME));
            final String validatorLabel = getStringValue(getNodeByXpath(validatorNode, XMLForms.LABEL));
            final String validatorStyle = getStringValue(getNodeByXpath(validatorNode, XMLForms.STYLE));
            final String validatorParameter = getStringValue(getNodeByXpath(validatorNode, XMLForms.PARAMETER));
            final ValidatorPosition validatorPosition = getValidatorPositionValue(getNodeByXpath(validatorNode, XMLForms.POSITION));
            final FormValidator formValidator = new FormValidator(validatorId, validatorClass, validatorLabel, validatorStyle, validatorParameter);
            formValidator.setPosition(validatorPosition);
            validators.add(formValidator);
        }
        formWidget.setValidators(validators);
        
        if (WidgetType.TABLE.equals(type) || WidgetType.EDITABLE_GRID.equals(type)) {
            parseTableAttributes(widgetNode, formWidget);
        }
        if (WidgetType.IMAGE.equals(type)) {
            formWidget.setImageStyle(getStringValue(getNodeByXpath(widgetNode, XMLForms.IMAGE_STYLE)));
        }
        
        formWidget.setDisplayConditionExpression(getStringValue(getNodeByXpath(widgetNode, XMLForms.DISPLAY_CONDITION)));
        //formats for dates
        formWidget.setDisplayFormat(getStringValue(getNodeByXpath(widgetNode, XMLForms.DISPLAY_FORMAT)));
        //image preview for download widgets
        formWidget.setDisplayAttachmentImage(getBooleanValue(getNodeByXpath(widgetNode, XMLForms.DISPLAY_ATTACHMENT_IMAGE)));
        
        formWidget.setAllowHTMLInLabel(getBooleanValue(getNodeByXpath(widgetNode, XMLForms.ALLOW_HTML_IN_LABEL)));
        formWidget.setAllowHTMLInField(getBooleanValue(getNodeByXpath(widgetNode, XMLForms.ALLOW_HTML_IN_FIELD)));
        
        final Map<String, String> htmlAttributes = new HashMap<String, String>();
        final NodeList htmlAttributeNodes = getNodeListByXpath(widgetNode, XMLForms.HTML_ATTRIBUTES + "/" + XMLForms.HTML_ATTRIBUTE);
        for(int i=0; i<htmlAttributeNodes.getLength(); i++) {
            final Node htmlAttributeNode = htmlAttributeNodes.item(i);
            final String attributeName = getStringByXpath(htmlAttributeNode, "@" + XMLForms.NAME);
            final String attributeValue = htmlAttributeNode.getTextContent();
            htmlAttributes.put(attributeName, attributeValue);
        }
        formWidget.setHtmlAttributes(htmlAttributes);
        
        formWidget.setMaxItems(getIntValue(getNodeByXpath(widgetNode, XMLForms.MAX_ITEMS)));
        
        boolean readOnly = false;
        if (!isEditMode 
                || type.equals(WidgetType.TEXT)
                || type.equals(WidgetType.MESSAGE) 
                || type.equals(WidgetType.FILEDOWNLOAD) 
                || type.equals(WidgetType.IMAGE)
                || type.equals(WidgetType.IFRAME)
                || type.name().startsWith("BUTTON")
                || (type.equals(WidgetType.TABLE) && SelectMode.NONE.equals(formWidget.getSelectMode()))) {
            readOnly = true;
        } else {
            readOnly = getBooleanValue(getNodeByXpath(widgetNode, XMLForms.READ_ONLY));
        }
        formWidget.setReadOnly(readOnly);
        formWidget.setDelayMillis(getIntValue(getNodeByXpath(widgetNode, XMLForms.DELAY_MILLIS)));
        formWidget.setPopupToolTip(getStringValue(getNodeByXpath(widgetNode, XMLForms.POPUP_TOOLTIP)));
        
        final Node subTitleNode = getNodeByXpath(widgetNode, XMLForms.SUB_TITLE);
        if (subTitleNode != null) {
            final String subTitleLabel = getStringValue(getNodeByXpath(subTitleNode, XMLForms.LABEL));
            final SubTitlePosition subTitlePosition = getSubTitlePositionValue(getNodeByXpath(subTitleNode, XMLForms.POSITION));
            FormSubTitle formSubTitle = new FormSubTitle(subTitleLabel, subTitlePosition);
            formWidget.setSubTitle(formSubTitle);
        }
        return formWidget;
    }
    
    protected void parseTableAttributes(final Node widgetNode, final FormWidget formWidget) throws InvalidFormDefinitionException {
    	
        final Node tableStyleNode = getNodeByXpath(widgetNode, XMLForms.TABLE_STYLE);
        if (tableStyleNode != null) {
            formWidget.setTableStyle(tableStyleNode.getTextContent());
        }
        final Node cellsStyleNode = getNodeByXpath(widgetNode, XMLForms.CELL_STYLE);
        if (cellsStyleNode != null) {
            formWidget.setCellsStyle(cellsStyleNode.getTextContent());
        }
        final Node verticalHeaderNode = getNodeByXpath(widgetNode, XMLForms.VERTICAL_HEADER);
        if (verticalHeaderNode != null) {
            formWidget.setVerticalHeaderExpression(verticalHeaderNode.getTextContent());
        }
        final Node horizontalHeaderNode = getNodeByXpath(widgetNode, XMLForms.HORIZONTAL_HEADER);
        if (horizontalHeaderNode != null) {
            formWidget.setHorizontalHeaderExpression(horizontalHeaderNode.getTextContent());
        }
        formWidget.setSelectMode(getSelectMode(getNodeByXpath(widgetNode, XMLForms.SELECT_MODE)));
        final Node selectedItemsStyleNode = getNodeByXpath(widgetNode, XMLForms.SELECTED_ITEMS_STYLE);
        if (selectedItemsStyleNode != null) {
            formWidget.setSelectedItemsStyle(getStringValue(selectedItemsStyleNode));
        }
        formWidget.setVariableRowNumber(getBooleanValue(getNodeByXpath(widgetNode, XMLForms.VARIABLE_ROWS)));
        formWidget.setVariableColumnNumber(getBooleanValue(getNodeByXpath(widgetNode, XMLForms.VARIABLE_COLUMNS)));
        final Node maxRowsNode = getNodeByXpath(widgetNode, XMLForms.MAX_ROWS);
        if (maxRowsNode != null) {
            formWidget.setMaxRowsExpression(getStringValue(maxRowsNode));
        }
        final Node minRowsNode = getNodeByXpath(widgetNode, XMLForms.MIN_ROWS);
        if (minRowsNode != null) {
            formWidget.setMinRowsExpression(getStringValue(minRowsNode));
        }
        final Node maxColumnsNode = getNodeByXpath(widgetNode, XMLForms.MAX_COLUMNS);
        if (maxColumnsNode != null) {
            formWidget.setMaxColumnsExpression(getStringValue(maxColumnsNode));
        }
        final Node minColumnsNode = getNodeByXpath(widgetNode, XMLForms.MIN_COLUMNS);
        if (minColumnsNode != null) {
            formWidget.setMinColumnsExpression(getStringValue(minColumnsNode));
        }
        final Node valueColumnIndexNode = getNodeByXpath(widgetNode, XMLForms.VALUE_COLUMN_INDEX);
        if (valueColumnIndexNode != null) {
            formWidget.setValueColumnIndexExpression(getStringValue(valueColumnIndexNode));
        }
        final Node headingsStyleNode = getNodeByXpath(widgetNode, XMLForms.HEADINGS_STYLE);
        if (headingsStyleNode != null) {
            formWidget.setHeadingsStyle(headingsStyleNode.getTextContent());
        }
        final Node headingsPositionNode = getNodeByXpath(widgetNode, XMLForms.HEADINGS_POSITIONS);
        if (headingsPositionNode != null) {
            formWidget.setLeftHeadings(getBooleanValue(getNodeByXpath(headingsPositionNode, XMLForms.LEFT_HEADINGS)));
            formWidget.setTopHeadings(getBooleanValue(getNodeByXpath(headingsPositionNode, XMLForms.TOP_HEADINGS)));
            formWidget.setRightHeadings(getBooleanValue(getNodeByXpath(headingsPositionNode, XMLForms.RIGHT_HEADINGS)));
            formWidget.setBottomHeadings(getBooleanValue(getNodeByXpath(headingsPositionNode, XMLForms.BOTTOM_HEADINGS)));
        }
    }

    /**
     * Retrieve the available values from an available values list node
     * @param availableValuesListNode
     * @return a List of {@link FormFieldAvailableValue}
     */
    protected List<FormFieldAvailableValue> getAvailableValues(final Node availableValuesListNode) {
        final List<FormFieldAvailableValue> availableValues = new ArrayList<FormFieldAvailableValue>();
        final NodeList availableValuesNodes = getNodeListByXpath(availableValuesListNode, XMLForms.AVAILABLE_VALUE);
        for (int i=0; i<availableValuesNodes.getLength(); i++) {
            final Node availableValueNode = availableValuesNodes.item(i);
            final String valueLabel = getStringValue(getNodeByXpath(availableValueNode, XMLForms.LABEL));
            final String value = getStringValue(getNodeByXpath(availableValueNode, XMLForms.VALUE));
            final FormFieldAvailableValue availableValue = new FormFieldAvailableValue(valueLabel, value);
            availableValues.add(availableValue);
        }
        return availableValues;
    }
    
    /**
     * Read a node and return the list of {@link FormAction} it contains
     * @param parentNode the parent node of the actions
     * @param pageId page for which the actions are required
     * @return a {@link List} of {@link FormAction} objects
     * @throws InvalidFormDefinitionException
     */
    public List<FormAction> getActions(final Node parentNode, final String pageId) throws InvalidFormDefinitionException {

        final List<FormAction> actions = new ArrayList<FormAction>();

        final String xpath = getActionsXpath(pageId);
        final NodeList actionNodes = getNodeListByXpath(parentNode, xpath);
        if (actionNodes == null) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Failed to parse the form definition file. query : " + xpath);
            }
            throw new InvalidFormDefinitionException("The actions were not found in the forms definition file");
        } else {
            for (int i=0; i<actionNodes.getLength(); i++) {
                final Node actionNode = actionNodes.item(i);
                actions.add(parseAction(actionNode));
            }
        }
        return actions;
    }
    
    /**
     * Get the XPath query for the actions of a page
     * @param pageId the page ID
     * @return the XPath query
     */
    protected String getActionsXpath(final String pageId) {
        final StringBuilder actionsXpathBuilder = new StringBuilder();
        actionsXpathBuilder.append(XMLForms.PAGES);
        actionsXpathBuilder.append("/");
        actionsXpathBuilder.append(XMLForms.PAGE);
        actionsXpathBuilder.append("[@");
        actionsXpathBuilder.append(XMLForms.ID);
        actionsXpathBuilder.append("='");
        actionsXpathBuilder.append(pageId);
        actionsXpathBuilder.append("']/");
        actionsXpathBuilder.append(XMLForms.ACTIONS);
        actionsXpathBuilder.append("/");
        actionsXpathBuilder.append(XMLForms.ACTION);
        return actionsXpathBuilder.toString();
    }
    
    /**
     * Read an action node and return an action
     * @param actionNode an action node
     * @return a {@link FormAction}
     * @throws InvalidFormDefinitionException
     */
    protected FormAction parseAction(final Node actionNode) throws InvalidFormDefinitionException {
        final ActionType actionType = getActionTypeValue(getStringByXpath(actionNode, "@" + XMLForms.TYPE));
        final String variableId = getStringValue(getNodeByXpath(actionNode, XMLForms.VARIABLE));
        final String variableType = getStringValue(getNodeByXpath(actionNode, XMLForms.VARIABLE_TYPE));
        final String attachmentName = getStringValue(getNodeByXpath(actionNode, XMLForms.ATTACHMENT));
        final String expression = getStringValue(getNodeByXpath(actionNode, XMLForms.EXPRESSION));
        final String submitButtonId = getStringValue(getNodeByXpath(actionNode, XMLForms.SUBMIT_BUTTON));
        return new FormAction(actionType, variableId, variableType, expression, submitButtonId, attachmentName);
    }

    protected ItemPosition getItemPositionValue(final Node node) throws InvalidFormDefinitionException {
        if (node != null) {
            try {
                return ItemPosition.valueOf(node.getTextContent());
            } catch (final IllegalArgumentException e) {
                final String message = "the property " + node.getNodeName() + " should be one of " + Arrays.toString(ItemPosition.values()) + ".";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, message, e);
                }
                throw new InvalidFormDefinitionException(message, e);
            }
        }
        return ItemPosition.LEFT;
    }
    
    protected ValidatorPosition getValidatorPositionValue(final Node node) throws InvalidFormDefinitionException {
        if (node != null) {
            try {
                return ValidatorPosition.valueOf(node.getTextContent());
            } catch (final IllegalArgumentException e) {
                final String message = "the property " + node.getNodeName() + " should be one of " + Arrays.toString(ValidatorPosition.values()) + ".";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, message, e);
                }
                throw new InvalidFormDefinitionException(message, e);
            }
        }
        return ValidatorPosition.BOTTOM;
    }
    
    protected SubTitlePosition getSubTitlePositionValue(final Node node) throws InvalidFormDefinitionException {
        if (node != null) {
            try {
                return SubTitlePosition.valueOf(node.getTextContent());
            } catch (final IllegalArgumentException e) {
                final String message = "the property " + node.getNodeName() + " should be one of " + Arrays.toString(SubTitlePosition.values()) + ".";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, message, e);
                }
                throw new InvalidFormDefinitionException(message, e);
            }
        }
        return SubTitlePosition.BOTTOM;
    }
    
    protected SelectMode getSelectMode(final Node node) throws InvalidFormDefinitionException {
        if (node != null) {
            try {
                return SelectMode.valueOf(node.getTextContent());
            } catch (final IllegalArgumentException e) {
                final String message = "the property " + node.getNodeName() + " should be one of " + Arrays.toString(SelectMode.values()) + ".";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, message, e);
                }
                throw new InvalidFormDefinitionException(message, e);
            }
        }
        return SelectMode.NONE;
    }
    
    /**
     * @param value the string value of the widget type
     * @return the {@link WidgetType} value of the node if it exists. null
     *         otherwise
     * @throws InvalidFormDefinitionException when the type is not int the definition of the widget or is invalid
     */
    protected WidgetType getWidgetTypeValue(final String value) throws InvalidFormDefinitionException {
        if (value != null) {
            try {
                return WidgetType.valueOf(value);
            } catch (final IllegalArgumentException e) {
                final String message = "the widget type should be one of " + Arrays.toString(WidgetType.values()) + ".";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, message, e);
                }
                throw new InvalidFormDefinitionException(message, e);
            }
        } else {
            final String message = "the widget attribute \"type\" is mandatory.";
            LOGGER.log(Level.SEVERE, message);
            throw new InvalidFormDefinitionException(message);
        }
    }
    
    /**
     * @param value the string value of the action type
     * @return the {@link ActionType} value of the node if it exists. null
     *         otherwise
     * @throws InvalidFormDefinitionException when the type is not int the definition of the widget or is invalid
     */
    protected ActionType getActionTypeValue(final String value) throws InvalidFormDefinitionException {
        if (value != null) {
            try {
                return ActionType.valueOf(value);
            } catch (final IllegalArgumentException e) {
                final String message = "the action type should be one of " + Arrays.toString(ActionType.values()) + ".";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, message, e);
                }
                throw new InvalidFormDefinitionException(message, e);
            }
        } else {
            final String message = "the action attribute \"type\" is mandatory.";
            LOGGER.log(Level.SEVERE, message);
            throw new InvalidFormDefinitionException(message);
        }
    }

}
