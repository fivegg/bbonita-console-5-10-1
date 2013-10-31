/**
 * Copyright (C) 2009 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 *
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
package org.bonitasoft.forms.server.builder;

import java.io.File;
import java.io.IOException;

import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.FormSubTitle.SubTitlePosition;
import org.bonitasoft.forms.client.model.FormValidator.ValidatorPosition;
import org.bonitasoft.forms.client.model.FormWidget.ItemPosition;
import org.bonitasoft.forms.client.model.FormWidget.SelectMode;
import org.bonitasoft.forms.client.model.WidgetType;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;


/**
 * Form definition builder
 * 
 * @author Aurelien Pupier, Anthony Birembaut, Chong Zhao
 */
public interface IFormBuilder {

    /** 
     * Build a XML form definition file.
     * This is the last method to call once the form has been built.
     * It perform the XSD validation and generates the XML file 
     * @return a {@link File}
     * @throws InvalidFormDefinitionException if the generated document is not valid
     * @throws IOException 
     */
    File done() throws IOException;
    
    /**
     * Initiate the form definition
     * @return an implementation of {@link IFormBuilder}
     */
    IFormBuilder createFormDefinition();
    
    /**
     * Add a welcome page to the webapp
     * @param url the relative URL of the welcome page
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addWelcomePage(String url) throws InvalidFormDefinitionException;
    
    /**
     * Add an external welcome page to the webapp
     * @param url the absolute URL of the welcome page
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addExternalWelcomePage(String url) throws InvalidFormDefinitionException;

    /**
     * Add an user defined homepage to the webapp
     * @param url the absolute URL of the homepage
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */    
    IFormBuilder addHomePage(final String url) throws InvalidFormDefinitionException;
   
    /**
     * Add an application
     * @param applicationName the name of the application
     * @param applicationVersion the version of the application
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addApplication(String applicationName, String applicationVersion) throws InvalidFormDefinitionException;

	/**
	 * Add a label on an application, page or widget
	 * @param label
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addLabel(String label) throws InvalidFormDefinitionException;

	/**
	 * Add a layout on an application or a page
	 * @param layoutUri
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addLayout(String layoutUri) throws InvalidFormDefinitionException;

	/**
	 * Add a mandatory field symbol on an application
	 * @param symbol
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addMandatorySymbol(String symbol) throws InvalidFormDefinitionException;
	
	/**
	 * Add a mandatory field label on an application
	 * @param label
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addMandatoryLabel(String label) throws InvalidFormDefinitionException;
	
	/**
     * Add a mandatory field label and symbol style (css class names) on an application
     * @param label
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
     */
	IFormBuilder addMandatoryStyle(String mandatoryStyle) throws InvalidFormDefinitionException;

	/**
     * Add a confirmation layout on an application
     * @param layoutUri
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addConfirmationLayout(String layoutUri) throws InvalidFormDefinitionException;
	
    /**
     * Add a confirmation message on an application
     * @param message
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addConfirmationMessage(String message) throws InvalidFormDefinitionException;
    
	/**
	 * Add an error template on an application
	 * @param templateUri
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addErrorTemplate(String templateUri) throws InvalidFormDefinitionException;
    
	/**
	 * Add an entry form on an application
	 * If an application has no entry form, it means that it hasn't been defined, 
	 * and the form for the application will be automatically generated. 
	 * Whereas if it has an empty entry form, the application will be automatically instantiated.
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addEntryForm(String formId) throws InvalidFormDefinitionException;
	
	/**
     * Add a view form on an application
     * If an application has no form, it means that it hasn't been defined, 
     * and the form for the application will be automatically generated. 
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addViewForm(String formId) throws InvalidFormDefinitionException;
    
	/**
	 * Add a page in the edition page flows and create the form if it doesn't exist yet 
	 * @param pageId
	 * @return
	 * @throws InvalidFormDefinitionException
	 */
	IFormBuilder addPage(String pageId) throws InvalidFormDefinitionException;
    
	/**
	 * Add a widget on pages
	 * @param widgetId
	 * @param widgetType
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addWidget(String widgetId, WidgetType widgetType) throws InvalidFormDefinitionException;

	/**
	 * Add a max length number of characters property to a widget for textbox and textarea widgets
	 * @param maxLength
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addMaxLength(int maxLength) throws InvalidFormDefinitionException;
	
	/**
	 * Add a max height number of characters property to a widget for textarea and multiple listbox widgets
	 * @param maxHeight
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addMaxHeight(int maxHeight) throws InvalidFormDefinitionException;
	
	/**
	 * Add a title (tooltip) to a widget field
	 * @param tooltip
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addTitle(String tooltip) throws InvalidFormDefinitionException;
	
	/**
	 * Add CSS classes names to a widget
	 * @param cssClasses
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addStyle(String cssClasses) throws InvalidFormDefinitionException;
	
	/**
	 * Add an initial value to a widget (can be a groovy expression)
	 * @param initialValue
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addInitialValue(String initialValue) throws InvalidFormDefinitionException;
	
	/**
	 * Add CSS class names to a widget label
	 * @param cssClasses
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addLabelStyle(String cssClasses) throws InvalidFormDefinitionException;
	
	/**
     * Specify the position of a widget label
     * @param labelPosition
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addLabelPosition(ItemPosition labelPosition) throws InvalidFormDefinitionException;
    
	/**
	 * Add a mandatory property to a widget
	 * @param isMandatory
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addMandatoryBehavior(boolean isMandatory) throws InvalidFormDefinitionException;
	
	/**
	 * Add an available value to a widget and create the list of available values if it doesn't exist yet 
	 * (for radiobutton group, simple and multiple selectbox, checkbox group).
	 * @param label the label displayed
	 * @param value the value to save
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addAvailableValue(String label, String value) throws InvalidFormDefinitionException;
	
	/**
     * Add an available values expression property to a widget
     * @param expression the expression to evaluate to fill in the available values. The evaluation of the expression has to return a collection
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addAvailableValuesExpression(String expression) throws InvalidFormDefinitionException;
    
	
	/**
	 * Add CSS class names to the items of a radiobutton or checkbox group widget
	 * @param cssClasses
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addItemsStyle(String cssClasses) throws InvalidFormDefinitionException;
	
	/**
	 * Indicates that the fields is a one to one variable binding and set the variable
	 * @param variableBound
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addVariableBound(String variableBound) throws InvalidFormDefinitionException;
	
	/**
     * Indicates that the button should be displayed as a label instead of an html button
     * @param isLabelButton
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addLabelButtonBehavior(boolean isLabelButton) throws InvalidFormDefinitionException;

	/**
	 * Add a validator on a page or a widget and create the list of validators if it doesn't exist yet 
	 * @param validatorId the validator Id
	 * @param label the error label
     * @param className the classname of the validator
     * @param parameter the parameter of the validator
     * @param cssClasses the css classes for the error label
     * @param position the position of the error label
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addValidator(String validatorId, String label, String className, String parameter, String cssClasses, ValidatorPosition position) throws InvalidFormDefinitionException;
	
    /**
     * Add an action on an application and create the list of actions if it doesn't exist yet
     * @param actionType the action type
     * @param variableName the name of the variable (if it's a set variable action)
     * @param variableType the type of the variable (if it's a set variable action)
     * @param expression the expression to evaluate (and store if it's a set variable action)
     * @param submitButtonId the submit button associated with the action
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
	IFormBuilder addAction(ActionType actionType, String variableName, String variableType, String expression, String submitButtonId) throws InvalidFormDefinitionException;
	
    /**
     * Add an action on an application and create the list of actions if it doesn't exist yet
     * @param actionType the action type
     * @param variableName the name of the variable (if it's a set variable action)
     * @param variableType the type of the variable (if it's a set variable action)
     * @param expression the expression to evaluate (and store if it's a set variable action)
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addAction(ActionType actionType, String variableName, String variableType, String expression) throws InvalidFormDefinitionException;

    /**
     * Add an attachment action on an application and create the list of actions if it doesn't exist yet
     * @param attachment the attachment name
     * @param expression the field id (with prefix field_)
     * @param submitButtonId the submit button associated with the action
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addAttachmentAction(String attachment, String expression, String submitButtonId) throws InvalidFormDefinitionException;
    
    /**
     * Add an attachment action on an application and create the list of actions if it doesn't exist yet
     * @param attachment the attachment name
     * @param expression the field id (with prefix field_)
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addAttachmentAction(String attachment, String expression) throws InvalidFormDefinitionException;
	
	/**
	 * Add a display format pattern for the display value of date widgets 
	 * @param displayFormat
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addDisplayFormat(String displayFormat) throws InvalidFormDefinitionException;
	   
    /**
     * Add an attachement image behavior for the display of image previews on file download widgets or the display of attachments in image widgets
     * @param attachmentImage
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addAttachmentImageBehavior(boolean attachmentImage) throws InvalidFormDefinitionException;
    
    /**
     * Add allow HTML in label behavior
     * @param allowHTMLInLabel
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addAllowHTMLInLabelBehavior(boolean allowHTMLInLabel) throws InvalidFormDefinitionException;
    
    /**
     * Add allow HTML in field behavior
     * @param allowHTMLInField
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addAllowHTMLInFieldBehavior(boolean allowHTMLInField) throws InvalidFormDefinitionException;
    
    /**
     * Add a Html attribute to a widget
     * @param name the name of the attribute
     * @param value the valueof the attribute
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addHTMLAttribute(String name, String value) throws InvalidFormDefinitionException;
    
    /**
     * Add table style for table widgets
     * @param cssClasses the CSS classes for the table
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addTableStyle(String cssClasses) throws InvalidFormDefinitionException;
    
    /**
     * Add image style for image widgets
     * @param cssClasses the CSS classes for the image
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addImageStyle(String cssClasses) throws InvalidFormDefinitionException;
    
    /**
     * Add cells style for table widgets
     * @param cssClasses the CSS classes for the cells
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addCellsStyle(String cssClasses) throws InvalidFormDefinitionException;
    
    /**
     * Add headings style for table widgets
     * @param cssClasses the CSS classes for the headings
     * @param leftHeadings if true, indicates that the left column of the grid should be considered as a header
     * @param topHeadings if true, indicates that the top row of the grid should be considered as a header
     * @param rightHeadings if true, indicates that the right column of the grid should be considered as a header
     * @param bottomHeadings if true, indicates that the bottom row of the grid should be considered as a header
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addHeadingsStyle(String cssClasses, boolean leftHeadings, boolean topHeadings, boolean rightHeadings, boolean bottomHeadings) throws InvalidFormDefinitionException;

    /**
     * Add a vertical header  under the form of an expression
     * @param expresssion the expression (should return a list after evaluation)
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addVerticalHeaderExpression(String expresssion) throws InvalidFormDefinitionException;
    
    /**
     * Add an horizontal header under the form of an expression
     * @param expresssion the expression (should return a list after evaluation)
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addHorizontalHeaderExpression(String expresssion) throws InvalidFormDefinitionException;
    
    /**
     * Add a selection mode to a widget (for table widgets for example)
     * @param selectMode the selection mode. {@link SelectMode#NONE} if the selection should be disabled
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addSelectMode(SelectMode selectMode) throws InvalidFormDefinitionException;
    
    /**
     * Add a selected items style to a widget (for table widgets for example)
     * @param selectedItemsStyle the selection mode. {@link SelectMode#NONE} if the selection should be disabled
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addSelectedItemsStyle(String selectedItemsStyle) throws InvalidFormDefinitionException;
    
    /**
     * add a minimum number of rows to a widget (for editable tables)
     * @param expression the expression (should return an integer after evaluation)
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addMinRowsExpression(String expression) throws InvalidFormDefinitionException;
    
    /**
     * add a maximum number of rows to a widget (for tables)
     * @param expression the expression (should return an integer after evaluation)
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addMaxRowsExpression(String expression) throws InvalidFormDefinitionException;
    
    /**
     * add a variable rows number behavior (for editable tables)
     * @param variableRowsNumber the variable rows number
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addVariableRowsNumber(boolean variableRowsNumber) throws InvalidFormDefinitionException;
    
    /**
     * add a minimum number of columns to a widget (for editable tables)
     * @param expression the expression (should return an integer after evaluation)
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addMinColumnsExpression(String expression) throws InvalidFormDefinitionException;
    
    /**
     * add a maximum number of columns to a widget (for editable tables)
     * @param expression the expression (should return an integer after evaluation)
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addMaxColumnsExpression(String expression) throws InvalidFormDefinitionException;
    
    /**
     * add a variable columns number behavior (for editable tables)
     * @param variableColumnsNumber the variable columns number
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addVariableColumnsNumber(boolean variableColumnsNumber) throws InvalidFormDefinitionException;
    
    /**
     * specify the index of column which is used as the value of the selected row(s)
     * @param expression the expression (should return an integer after evaluation)
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addValueColumnIndex(String expression) throws InvalidFormDefinitionException;
    
    /**
     * Add a readonly property to a widget
     * @param isReadOnly the readonly behavior
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addReadOnlyBehavior(boolean isReadOnly) throws InvalidFormDefinitionException;
    
	/**
	 * Add a max items property to a widget for suggestbox widgets
	 * @param maxItems
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addMaxItems(int maxItems) throws InvalidFormDefinitionException;
    
    /**
     * Add a transient data on a page flow
     * @param name name of the transient data
     * @param className classnameof the transient data
     * @param value value of the transient data
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addTransientData(String name, String className, String value) throws InvalidFormDefinitionException;
    
    /**
     * Add a first page Id on a page flow
     * @param pageId the id of the first page
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addFirstPageId(String pageId) throws InvalidFormDefinitionException;
    
    /**
     * Add a next page id on a page flow
     * @param pageId the id of the first page
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addNextPageId(String pageId) throws InvalidFormDefinitionException;
    
	/**
	 * Add a display condition expression to display or not a widget
	 * @param displayConditionExpression
     * @return an implementation of {@link IFormBuilder}
	 * @throws InvalidFormDefinitionException 
	 */
	IFormBuilder addDisplayConditionExpression(String displayConditionExpression) throws InvalidFormDefinitionException;
	
	/**
     * Add a delay millisecond property to a widget for asynchronous suggestbox widgets
     * @param delayMillis
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addDelayMillis(int delayMillis) throws InvalidFormDefinitionException;

    /**
     * Add a sub title property to a widget to accept an "example" parameter
     * @param label sub title label
     * @param position sub title position
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addSubTitle(String label,SubTitlePosition position) throws InvalidFormDefinitionException;
    
    /**
     * Add a popup tooltip, that will be displayed to help the user when he clicks on the 
     * bulb icon that is placed beside of a widget.
     * @param example The tips to display
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addPopupToolTip(String tips) throws InvalidFormDefinitionException;
    
    /**
     * Add permissions, that will be decide whether a user has right to view/submit the form.
     * @param example The permissions
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addPermissions(String permissions) throws InvalidFormDefinitionException;
    
    /**
     * Add migration product version, that will be generated with the current version of the product used.
     * @param example The migrationProductVersion
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    @Deprecated
    IFormBuilder addMigrationProductVersion(String migrationProductVersion) throws InvalidFormDefinitionException;
    
    /**
     * Add a next form id on entry form or view form.
     * @param example The nextFormId
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException
     */
    IFormBuilder addNextFormId(String nextFormId) throws InvalidFormDefinitionException;
    
    /**
     * Add CSS classes names to a widget input
     * @param cssClasses
     * @return an implementation of {@link IFormBuilder}
     * @throws InvalidFormDefinitionException 
     */
    IFormBuilder addInputStyle(String cssClasses) throws InvalidFormDefinitionException;
}
