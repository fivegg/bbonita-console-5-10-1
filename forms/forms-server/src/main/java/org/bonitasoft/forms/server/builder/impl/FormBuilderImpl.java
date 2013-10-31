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
package org.bonitasoft.forms.server.builder.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bonitasoft.console.security.server.accessor.PropertiesFactory;
import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.FormSubTitle.SubTitlePosition;
import org.bonitasoft.forms.client.model.FormType;
import org.bonitasoft.forms.client.model.FormValidator.ValidatorPosition;
import org.bonitasoft.forms.client.model.FormWidget.ItemPosition;
import org.bonitasoft.forms.client.model.FormWidget.SelectMode;
import org.bonitasoft.forms.client.model.WidgetType;
import org.bonitasoft.forms.server.builder.IFormBuilder;
import org.bonitasoft.forms.server.constants.XMLForms;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of the {@link IFormBuilder} interface generating an XML form definition file
 * 
 * @author Anthony Birembaut, Chong Zhao
 */
public class FormBuilderImpl implements IFormBuilder {

    /**
     * the product version constant
     */
    public static final String PRODUCT_VERSION = "5.10";

    /**
     * the product version
     */
    protected String productVersion;

    /**
     * XML tansformer factory
     */
    protected TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /**
     * document buildeer factory
     */
    protected DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    /**
     * DOM representation of the XML file to create
     */
    protected Document document;

    /**
     * the root element
     */
    private Element rootElement;

    /**
     * The current element
     */
    protected Element currentElement;

    /**
     * Logger
     */
    protected static Logger LOGGER = Logger.getLogger(FormBuilderImpl.class.getName());

    /**
     * Instance attribute
     */
    private static FormBuilderImpl INSTANCE = null;

    /**
     * @return the FormExpressionsAPI instance
     */
    public static synchronized FormBuilderImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FormBuilderImpl();
        }
        return INSTANCE;
    }

    /**
     * Private constructor to prevent instantiation
     */
    protected FormBuilderImpl() {
        productVersion = PRODUCT_VERSION;

        documentBuilderFactory.setValidating(true);

        // ignore white space can only be set if parser is validating
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        // select xml schema as the schema language (a.o.t. DTD)
        documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        final URL xsdURL = getClass().getResource("/forms.xsd");
        documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", xsdURL.toExternalForm());

        try {
            transformerFactory.setAttribute("indent-number", Integer.valueOf(2));
        } catch (final Exception e) {
            // Nothing to do: indent-number is not supported
        }
    }

    /**
     * {@inheritDoc}
     */
    public File done() throws IOException {

        final File formsDefinitionFile = File.createTempFile("forms", ".xml", PropertiesFactory.getPlatformProperties().getFormsTempFolder());
        formsDefinitionFile.deleteOnExit();

        document.appendChild(rootElement);

        final Source source = new DOMSource(document);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final Result resultat = new StreamResult(new OutputStreamWriter(outputStream, "UTF-8"));
        try {
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, resultat);

            final byte[] xmlContent = outputStream.toByteArray();
            outputStream.close();

            final FileOutputStream fileOutputStream = new FileOutputStream(formsDefinitionFile);
            try {
                fileOutputStream.write(xmlContent);
                fileOutputStream.flush();
            } finally {
                fileOutputStream.close();
            }
        } catch (final TransformerException e) {
            LOGGER.log(Level.SEVERE, "Error while generating the forms definition file.", e);
        }
        return formsDefinitionFile;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder createFormDefinition() {

        DocumentBuilder builder;
        try {
            builder = documentBuilderFactory.newDocumentBuilder();

            document = builder.newDocument();
            document.setXmlVersion("1.0");

            rootElement = document.createElement(XMLForms.FORMS_DEFINITION);
            rootElement.setAttribute(XMLForms.PRODUCT_VERSION, productVersion);
            final Element migrationVersionElement = document.createElement(XMLForms.MIGRATION_PRODUCT_VERSION);
            migrationVersionElement.setTextContent(productVersion);
            rootElement.appendChild(migrationVersionElement);
            currentElement = rootElement;
        } catch (final ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Invalid parser configuration", e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addExternalWelcomePage(final String url) throws InvalidFormDefinitionException {
        return addWelcomePage(url, true);
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addWelcomePage(final String url) throws InvalidFormDefinitionException {
        return addWelcomePage(url, false);
    }

    protected IFormBuilder addWelcomePage(final String url, final boolean isExternal) throws InvalidFormDefinitionException {
        final String[] welcomePageParentsNames = { XMLForms.FORMS_DEFINITION };
        try {
            peek(welcomePageParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a welcome page is only supported on elements of type " + Arrays.asList(welcomePageParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("welcome page url", url);
        Element welcomePageElement = null;
        if (isExternal) {
            welcomePageElement = document.createElement(XMLForms.EXTERNAL_WELCOME_PAGE);
        } else {
            welcomePageElement = document.createElement(XMLForms.WELCOME_PAGE);
        }
        welcomePageElement.setTextContent(url);
        push(welcomePageElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addHomePage(final String url) throws InvalidFormDefinitionException {
        final String[] homePageParentsNames = { XMLForms.FORMS_DEFINITION };
        try {
            peek(homePageParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a home page is only supported on elements of type " + Arrays.asList(homePageParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("home page url", url);
        Element homePageElement = document.createElement(XMLForms.HOME_PAGE);
        homePageElement.setTextContent(url);
        push(homePageElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public IFormBuilder addMigrationProductVersion(final String migrationProductVersion) throws InvalidFormDefinitionException {
        final String[] migrationProductVersionParentsNames = { XMLForms.FORMS_DEFINITION };
        try {
            peek(migrationProductVersionParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of migration product version property is only supported on elements of type " + Arrays.asList(migrationProductVersionParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("migration product version", migrationProductVersion);
        Element migrationProductVersionElement = document.createElement(XMLForms.MIGRATION_PRODUCT_VERSION);
        migrationProductVersionElement.setTextContent(migrationProductVersion);
        push(migrationProductVersionElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addAction(final ActionType actionType, final String variable, final String variableType, final String expression, final String submitButtonId) throws InvalidFormDefinitionException {
        final String[] actionsParentsNames = { XMLForms.PAGE };
        try {
            peek(actionsParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an action is only supported on elements of type " + Arrays.asList(actionsParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("action type", actionType);
        Element actionsElement = findChildElement(currentElement, XMLForms.ACTIONS);
        if (actionsElement == null) {
            actionsElement = document.createElement(XMLForms.ACTIONS);
        }
        push(actionsElement);
        final Element actionElement = document.createElement(XMLForms.ACTION);
        actionElement.setAttribute(XMLForms.TYPE, actionType.name());
        if (actionType.compareTo(ActionType.SET_VARIABLE) == 0) {
            checkArgNotNull("variable type", actionType);
            addChild(actionElement, XMLForms.VARIABLE, variable, true, true);
            addChild(actionElement, XMLForms.VARIABLE_TYPE, variableType, true, true);
        }
        addChild(actionElement, XMLForms.EXPRESSION, expression, false, false);
        addChild(actionElement, XMLForms.SUBMIT_BUTTON, submitButtonId, false, true);
        push(actionElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addAction(final ActionType actionType, final String variable, final String variableType, final String expression) throws InvalidFormDefinitionException {
        return addAction(actionType, variable, variableType, expression, null);
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addAttachmentAction(final String attachment, final String expression, final String submitButtonId) throws InvalidFormDefinitionException {
        final String[] actionsParentsNames = { XMLForms.PAGE };
        try {
            peek(actionsParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an action is only supported on elements of type " + Arrays.asList(actionsParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        Element actionsElement = findChildElement(currentElement, XMLForms.ACTIONS);
        if (actionsElement == null) {
            actionsElement = document.createElement(XMLForms.ACTIONS);
        }
        push(actionsElement);

        final Element actionElement = document.createElement(XMLForms.ACTION);
        actionElement.setAttribute(XMLForms.TYPE, ActionType.SET_ATTACHMENT.name());
        addChild(actionElement, XMLForms.ATTACHMENT, attachment, true, false);
        addChild(actionElement, XMLForms.EXPRESSION, expression, true, false);
        addChild(actionElement, XMLForms.SUBMIT_BUTTON, submitButtonId, false, true);
        push(actionElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addAttachmentAction(final String attachment, final String expression) throws InvalidFormDefinitionException {
        return addAttachmentAction(attachment, expression, null);
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addAvailableValue(final String label, final String value) throws InvalidFormDefinitionException {
        final String[] availableValuesParentsNames = { XMLForms.WIDGET };
        try {
            peek(availableValuesParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an available value is only supported on elements of type " + Arrays.asList(availableValuesParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("available value label", label);
        checkArgNotNull("available value", value);
        Element availableValuesElement = findChildElement(currentElement, XMLForms.AVAILABLE_VALUES);
        if (availableValuesElement == null) {
            availableValuesElement = document.createElement(XMLForms.AVAILABLE_VALUES);
        }
        push(availableValuesElement);
        Element availableValuesListElement = findChildElement(currentElement, XMLForms.VALUES_LIST);
        if (availableValuesListElement == null) {
            availableValuesListElement = document.createElement(XMLForms.VALUES_LIST);
        }
        push(availableValuesListElement);
        final Element availableValueElement = document.createElement(XMLForms.AVAILABLE_VALUE);
        addChild(availableValueElement, XMLForms.LABEL, label, true, false);
        addChild(availableValueElement, XMLForms.VALUE, value, true, false);
        push(availableValueElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addAvailableValuesExpression(final String expression) throws InvalidFormDefinitionException {
        final String[] availableValuesExpressionParentsNames = { XMLForms.WIDGET };
        try {
            peek(availableValuesExpressionParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an available values expression is only supported on elements of type " + Arrays.asList(availableValuesExpressionParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("available values expression", expression);
        checkStringNotEmpty("available values expression", expression);
        Element availableValuesElement = findChildElement(currentElement, XMLForms.AVAILABLE_VALUES);
        if (availableValuesElement == null) {
            availableValuesElement = document.createElement(XMLForms.AVAILABLE_VALUES);
        }
        push(availableValuesElement);
        final Element availableValuesExpressionElement = document.createElement(XMLForms.EXPRESSION);
        availableValuesExpressionElement.setTextContent(expression);
        push(availableValuesExpressionElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addDisplayFormat(final String displayFormat) throws InvalidFormDefinitionException {
        final String[] displayFormatParentsNames = { XMLForms.WIDGET };
        try {
            peek(displayFormatParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a display format is only supported on elements of type " + Arrays.asList(displayFormatParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("display format", displayFormat);
        final Element displayFormatElement = document.createElement(XMLForms.DISPLAY_FORMAT);
        displayFormatElement.setTextContent(displayFormat);
        push(displayFormatElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addErrorTemplate(final String templateUri) throws InvalidFormDefinitionException {
        final String[] errorTemplateParentsNames = { XMLForms.APPLICATION };
        try {
            peek(errorTemplateParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an error template is only supported on elements of type " + Arrays.asList(errorTemplateParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("error template", templateUri);
        checkStringNotEmpty("error template", templateUri);
        final Element errorTemplateElement = document.createElement(XMLForms.ERROR_TEMPLATE);
        errorTemplateElement.setTextContent(templateUri);
        push(errorTemplateElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addConfirmationLayout(final String templateUri) throws InvalidFormDefinitionException {
        final String[] confirmationTemplateParentsNames = { XMLForms.FORM };
        try {
            peek(confirmationTemplateParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a confirmation layout is only supported on elements of type " + Arrays.asList(confirmationTemplateParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("confirmation layout", templateUri);
        checkStringNotEmpty("confirmation layout", templateUri);
        final Element confirmationTemplateElement = document.createElement(XMLForms.CONFIRMATION_LAYOUT);
        confirmationTemplateElement.setTextContent(templateUri);
        push(confirmationTemplateElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addConfirmationMessage(final String message) throws InvalidFormDefinitionException {
        final String[] confirmationMessageParentsNames = { XMLForms.FORM };
        try {
            peek(confirmationMessageParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a confirmation message is only supported on elements of type " + Arrays.asList(confirmationMessageParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("confirmation message", message);
        final Element confirmationMessageElement = document.createElement(XMLForms.CONFIRMATION_MESSAGE);
        confirmationMessageElement.setTextContent(message);
        push(confirmationMessageElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addInitialValue(final String initialValue) throws InvalidFormDefinitionException {
        final String[] initialValueParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP };
        try {
            peek(initialValueParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an initial value is only supported on elements of type " + Arrays.asList(initialValueParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("initial value", initialValue);
        Element initialValueElement = findChildElement(currentElement, XMLForms.INITIAL_VALUE);
        if (initialValueElement == null) {
            initialValueElement = document.createElement(XMLForms.INITIAL_VALUE);
        } else {
            throw new InvalidFormDefinitionException("This widget already has an initail value defined.");
        }
        push(initialValueElement);
        final Element initialValueExpression = document.createElement(XMLForms.EXPRESSION);
        initialValueExpression.setTextContent(initialValue);
        push(initialValueExpression);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addItemsStyle(final String cssClasses) throws InvalidFormDefinitionException {
        final String[] itemsStyleParentsNames = { XMLForms.WIDGET };
        try {
            peek(itemsStyleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an items style element is only supported on elements of type " + Arrays.asList(itemsStyleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("items style", cssClasses);
        final Element itemsStyleElement = document.createElement(XMLForms.ITEMS_STYLE);
        itemsStyleElement.setTextContent(cssClasses);
        push(itemsStyleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addLabel(final String label) throws InvalidFormDefinitionException {
        final String[] labelParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP, XMLForms.PAGE, XMLForms.APPLICATION };
        try {
            peek(labelParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a label is only supported on elements of type " + Arrays.asList(labelParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("label", label);
        Element labelElement = null;
        if (currentElement.getNodeName().equals(XMLForms.APPLICATION)) {
            labelElement = document.createElement(XMLForms.APPLICATION_LABEL);
        } else if (currentElement.getNodeName().equals(XMLForms.PAGE)) {
            labelElement = document.createElement(XMLForms.PAGE_LABEL);
        } else {
            labelElement = document.createElement(XMLForms.LABEL);
        }
        labelElement.setTextContent(label);
        push(labelElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addLabelStyle(final String cssClasses) throws InvalidFormDefinitionException {
        final String[] labelStyleParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP };
        try {
            peek(labelStyleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a label style is only supported on elements of type " + Arrays.asList(labelStyleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("label style", cssClasses);
        final Element labelStyleElement = document.createElement(XMLForms.LABEL_STYLE);
        labelStyleElement.setTextContent(cssClasses);
        push(labelStyleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addLabelPosition(final ItemPosition labelPosition) throws InvalidFormDefinitionException {
        final String[] labelPositionParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP };
        try {
            peek(labelPositionParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a label position is only supported on elements of type " + Arrays.asList(labelPositionParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("label position", labelPosition);
        final Element labelPositionElement = document.createElement(XMLForms.LABEL_POSITION);
        labelPositionElement.setTextContent(labelPosition.name());
        push(labelPositionElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMandatoryLabel(final String label) throws InvalidFormDefinitionException {
        final String[] mandatoryLabelParentsNames = { XMLForms.APPLICATION };
        try {
            peek(mandatoryLabelParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a mandatory label is only supported on elements of type " + Arrays.asList(mandatoryLabelParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("mandatory label", label);
        final Element mandatoryLabelElement = document.createElement(XMLForms.MANDATORY_LABEL);
        mandatoryLabelElement.setTextContent(label);
        push(mandatoryLabelElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMandatoryBehavior(final boolean isMandatory) throws InvalidFormDefinitionException {
        final String[] mandatoryBehaviorParentsNames = { XMLForms.WIDGET };
        try {
            peek(mandatoryBehaviorParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a mandatory behaviour is only supported on elements of type " + Arrays.asList(mandatoryBehaviorParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("mandatory behavior", isMandatory);
        final Element mandatoryBehaviorElement = document.createElement(XMLForms.MANDATORY);
        mandatoryBehaviorElement.setTextContent(Boolean.toString(isMandatory));
        push(mandatoryBehaviorElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addLabelButtonBehavior(final boolean isLabelButton) throws InvalidFormDefinitionException {
        final String[] labelButtonBehaviorParentsNames = { XMLForms.WIDGET };
        try {
            peek(labelButtonBehaviorParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a label button behaviour is only supported on elements of type " + Arrays.asList(labelButtonBehaviorParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("label button behavior", isLabelButton);
        final Element labelButtonBehaviorElement = document.createElement(XMLForms.LABEL_BUTTON);
        labelButtonBehaviorElement.setTextContent(Boolean.toString(isLabelButton));
        push(labelButtonBehaviorElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMandatoryStyle(final String mandatoryStyle) throws InvalidFormDefinitionException {
        final String[] mandatoryStyleParentsNames = { XMLForms.APPLICATION };
        try {
            peek(mandatoryStyleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a mandatory label style is only supported on elements of type " + Arrays.asList(mandatoryStyleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("mandatory label style", mandatoryStyle);
        final Element mandatoryStyleElement = document.createElement(XMLForms.MANDATORY_STYLE);
        mandatoryStyleElement.setTextContent(mandatoryStyle);
        push(mandatoryStyleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMandatorySymbol(final String symbol) throws InvalidFormDefinitionException {
        final String[] mandatorySymbolParentsNames = { XMLForms.APPLICATION };
        try {
            peek(mandatorySymbolParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a mandatory symbol is only supported on elements of type " + Arrays.asList(mandatorySymbolParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("mandatory symbol", symbol);
        checkStringNotEmpty("mandatory symbol", symbol);
        final Element mandatorySymbolElement = document.createElement(XMLForms.MANDATORY_SYMBOL);
        mandatorySymbolElement.setTextContent(symbol);
        push(mandatorySymbolElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMaxHeight(final int maxHeight) throws InvalidFormDefinitionException {
        final String[] maxHeigthParentsNames = { XMLForms.WIDGET };
        try {
            peek(maxHeigthParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a max heigth is only supported on elements of type " + Arrays.asList(maxHeigthParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element maxHeigthElement = document.createElement(XMLForms.MAX_HEIGHT);
        maxHeigthElement.setTextContent(Integer.toString(maxHeight));
        push(maxHeigthElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMaxLength(final int maxLength) throws InvalidFormDefinitionException {
        final String[] maxLengthParentsNames = { XMLForms.WIDGET };
        try {
            peek(maxLengthParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a max length is only supported on elements of type " + Arrays.asList(maxLengthParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element maxLengthElement = document.createElement(XMLForms.MAX_LENGTH);
        maxLengthElement.setTextContent(Integer.toString(maxLength));
        push(maxLengthElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addPage(final String pageId) throws InvalidFormDefinitionException {
        final String[] pageParentsNames = { XMLForms.FORM };
        try {
            peek(pageParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an entry page is only supported on elements of type " + Arrays.asList(pageParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("page Id", pageId);
        checkStringNotEmpty("page Id", pageId);
        Element pagesElement = findChildElement(currentElement, XMLForms.PAGES);
        if (pagesElement == null) {
            pagesElement = document.createElement(XMLForms.PAGES);
        }
        push(pagesElement);
        final Element pageElement = document.createElement(XMLForms.PAGE);
        pageElement.setAttribute(XMLForms.ID, pageId);
        push(pageElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addEntryForm(String formId) throws InvalidFormDefinitionException {
        final String[] formParentsNames = { XMLForms.APPLICATION };
        try {
            peek(formParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an entry form is only supported on elements of type " + Arrays.asList(formParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("form Id", formId);
        checkStringNotEmpty("form Id", formId);
        Element entryFormsElement = findChildElement(currentElement, XMLForms.FORMS);
        if (entryFormsElement == null) {
            entryFormsElement = document.createElement(XMLForms.FORMS);
        }
        push(entryFormsElement);
        final Element entryFormElement = document.createElement(XMLForms.FORM);
        entryFormElement.setAttribute(XMLForms.ID, formId);
        addChild(entryFormElement, XMLForms.FORM_TYPE, FormType.entry.name(), true, true);
        push(entryFormElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addViewForm(String formId) throws InvalidFormDefinitionException {
        final String[] formParentsNames = { XMLForms.APPLICATION };
        try {
            peek(formParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a view form is only supported on elements of type " + Arrays.asList(formParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("form Id", formId);
        checkStringNotEmpty("form Id", formId);
        Element viewFormsElement = findChildElement(currentElement, XMLForms.FORMS);
        if (viewFormsElement == null) {
            viewFormsElement = document.createElement(XMLForms.FORMS);
        }
        push(viewFormsElement);
        final Element viewFormElement = document.createElement(XMLForms.FORM);
        viewFormElement.setAttribute(XMLForms.ID, formId);
        addChild(viewFormElement, XMLForms.FORM_TYPE, FormType.view.name(), true, true);
        push(viewFormElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addApplication(final String applicationName, final String applicationVersion) throws InvalidFormDefinitionException {
        final String[] applicationParentsNames = { XMLForms.FORMS_DEFINITION };
        try {
            peek(applicationParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a application is only supported on elements of type " + Arrays.asList(applicationParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("application name", applicationName);
        checkArgNotNull("application version", applicationVersion);
        final Element applicationElement = document.createElement(XMLForms.APPLICATION);
        applicationElement.setAttribute(XMLForms.NAME, applicationName);
        applicationElement.setAttribute(XMLForms.VERSION, applicationVersion);
        push(applicationElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addStyle(final String cssClasses) throws InvalidFormDefinitionException {
        final String[] styleParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP };
        try {
            peek(styleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a style is only supported on elements of type " + Arrays.asList(styleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("style", cssClasses);
        final Element styleElement = document.createElement(XMLForms.STYLE);
        styleElement.setTextContent(cssClasses);
        push(styleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addLayout(final String layoutUri) throws InvalidFormDefinitionException {
        final String[] layoutParentsNames = { XMLForms.APPLICATION, XMLForms.PAGE };
        try {
            peek(layoutParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a style is only supported on elements of type " + Arrays.asList(layoutParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("layout", layoutUri);
        checkStringNotEmpty("layout", layoutUri);
        Element templateElement = null;
        if (currentElement.getNodeName().equals(XMLForms.APPLICATION)) {
            templateElement = document.createElement(XMLForms.APPLICATION_LAYOUT);
        } else {
            templateElement = document.createElement(XMLForms.PAGE_LAYOUT);
        }
        templateElement.setTextContent(layoutUri);
        push(templateElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addTitle(final String tooltip) throws InvalidFormDefinitionException {
        final String[] titleParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP };
        try {
            peek(titleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a title is only supported on elements of type " + Arrays.asList(titleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("title", tooltip);
        final Element titleElement = document.createElement(XMLForms.TITLE);
        titleElement.setTextContent(tooltip);
        push(titleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addValidator(final String validatorId, final String label, final String className, final String parameter, final String cssClasses, final ValidatorPosition position) throws InvalidFormDefinitionException {
        final String[] validatorsParentsNames = { XMLForms.PAGE, XMLForms.WIDGET };
        try {
            peek(validatorsParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a validator is only supported on elements of type " + Arrays.asList(validatorsParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("validator id", validatorId);
        checkStringNotEmpty("validator id", validatorId);
        Element validatorsElement = null;
        if (currentElement.getNodeName().equals(XMLForms.PAGE)) {
            validatorsElement = findChildElement(currentElement, XMLForms.PAGE_VALIDATORS);
            if (validatorsElement == null) {
                validatorsElement = document.createElement(XMLForms.PAGE_VALIDATORS);
            }
        } else {
            validatorsElement = findChildElement(currentElement, XMLForms.VALIDATORS);
            if (validatorsElement == null) {
                validatorsElement = document.createElement(XMLForms.VALIDATORS);
            }
        }
        push(validatorsElement);
        final Element validatorElement = document.createElement(XMLForms.VALIDATOR);
        validatorElement.setAttribute(XMLForms.ID, validatorId);
        addChild(validatorElement, XMLForms.LABEL, label, true, false);
        addChild(validatorElement, XMLForms.CLASSNAME, className, true, true);
        addChild(validatorElement, XMLForms.PARAMETER, parameter, false, false);
        addChild(validatorElement, XMLForms.STYLE, cssClasses, false, false);
        if (position != null) {
            addChild(validatorElement, XMLForms.POSITION, position.name(), false, true);
        }
        push(validatorElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addWidget(final String widgetId, final WidgetType widgetType) throws InvalidFormDefinitionException {
        final String[] widgetParentsNames = { XMLForms.PAGE, XMLForms.WIDGETS_GROUP };
        try {
            peek(widgetParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a widget is only supported on elements of type " + Arrays.asList(widgetParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("widget id", widgetId);
        checkStringNotEmpty("widget id", widgetId);
        checkArgNotNull("widget type", widgetType);
        Element widgetsElement = findChildElement(currentElement, XMLForms.WIDGETS);
        if (widgetsElement == null) {
            widgetsElement = document.createElement(XMLForms.WIDGETS);
        }
        push(widgetsElement);
        final Element widgetElement = document.createElement(XMLForms.WIDGET);
        widgetElement.setAttribute(XMLForms.ID, widgetId);
        widgetElement.setAttribute(XMLForms.TYPE, widgetType.name());
        push(widgetElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addVariableBound(final String variableBound) throws InvalidFormDefinitionException {
        final String[] variableBoundParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP };
        try {
            peek(variableBoundParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a bound variable is only supported on elements of type " + Arrays.asList(variableBoundParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("variable bound", variableBound);
        final Element variableBoundElement = document.createElement(XMLForms.VARIABLE_BOUND);
        variableBoundElement.setTextContent(variableBound);
        push(variableBoundElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addAttachmentImageBehavior(final boolean attachmentImage) throws InvalidFormDefinitionException {
        final String[] imagePreviewParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP };
        try {
            peek(imagePreviewParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a image preview behavior is only supported on elements of type " + Arrays.asList(imagePreviewParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("image preview", attachmentImage);
        final Element attachmentImageElement = document.createElement(XMLForms.DISPLAY_ATTACHMENT_IMAGE);
        attachmentImageElement.setTextContent(Boolean.toString(attachmentImage));
        push(attachmentImageElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addAllowHTMLInFieldBehavior(final boolean allowHTMLInField) throws InvalidFormDefinitionException {
        final String[] allowHTMLInFiedParentsNames = { XMLForms.WIDGET };
        try {
            peek(allowHTMLInFiedParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a property to allow or not HTML in form fields is only supported on elements of type " + Arrays.asList(allowHTMLInFiedParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("allow HTML in field", allowHTMLInField);
        final Element allowHTMLInFieldElement = document.createElement(XMLForms.ALLOW_HTML_IN_FIELD);
        allowHTMLInFieldElement.setTextContent(Boolean.toString(allowHTMLInField));
        push(allowHTMLInFieldElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addAllowHTMLInLabelBehavior(final boolean allowHTMLInLabel) throws InvalidFormDefinitionException {
        final String[] allowHTMLInLabelParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP, XMLForms.PAGE };
        try {
            peek(allowHTMLInLabelParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a property to allow or not HTML in form labels is only supported on elements of type " + Arrays.asList(allowHTMLInLabelParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("allow HTML in label", allowHTMLInLabel);
        final Element allowHTMLInLabelElement = document.createElement(XMLForms.ALLOW_HTML_IN_LABEL);
        allowHTMLInLabelElement.setTextContent(Boolean.toString(allowHTMLInLabel));
        push(allowHTMLInLabelElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addHTMLAttribute(final String name, final String value) throws InvalidFormDefinitionException {
        final String[] htmlAttributesParentsNames = { XMLForms.WIDGET };
        try {
            peek(htmlAttributesParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an HTML attribute is only supported on elements of type " + Arrays.asList(htmlAttributesParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("HTML attribute name", name);
        checkArgNotNull("HTML attribute name value", value);
        Element htmlAttributesElement = findChildElement(currentElement, XMLForms.HTML_ATTRIBUTES);
        if (htmlAttributesElement == null) {
            htmlAttributesElement = document.createElement(XMLForms.HTML_ATTRIBUTES);
        }
        push(htmlAttributesElement);
        final Element htmlAttributeElement = document.createElement(XMLForms.HTML_ATTRIBUTE);
        htmlAttributeElement.setAttribute(XMLForms.NAME, name);
        htmlAttributeElement.setTextContent(value);
        push(htmlAttributeElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addTableStyle(final String cssClasses) throws InvalidFormDefinitionException {
        final String[] styleParentsNames = { XMLForms.WIDGET };
        try {
            peek(styleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a table style is only supported on elements of type " + Arrays.asList(styleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("table style", cssClasses);
        final Element styleElement = document.createElement(XMLForms.TABLE_STYLE);
        styleElement.setTextContent(cssClasses);
        push(styleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addImageStyle(final String cssClasses) throws InvalidFormDefinitionException {
        final String[] styleParentsNames = { XMLForms.WIDGET };
        try {
            peek(styleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of an image style is only supported on elements of type " + Arrays.asList(styleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("image style", cssClasses);
        final Element styleElement = document.createElement(XMLForms.IMAGE_STYLE);
        styleElement.setTextContent(cssClasses);
        push(styleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addCellsStyle(final String cssClasses) throws InvalidFormDefinitionException {
        final String[] styleParentsNames = { XMLForms.WIDGET };
        try {
            peek(styleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a cells style is only supported on elements of type " + Arrays.asList(styleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("cells style", cssClasses);
        final Element styleElement = document.createElement(XMLForms.CELL_STYLE);
        styleElement.setTextContent(cssClasses);
        push(styleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addHeadingsStyle(final String cssClasses, final boolean leftHeadings, final boolean topHeadings, final boolean rightHeadings, final boolean bottomHeadings) throws InvalidFormDefinitionException {
        final String[] styleParentsNames = { XMLForms.WIDGET };
        try {
            peek(styleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a headingss style is only supported on elements of type " + Arrays.asList(styleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("headings style", cssClasses);
        final Element styleElement = document.createElement(XMLForms.HEADINGS_STYLE);
        styleElement.setTextContent(cssClasses);
        push(styleElement);
        peek(styleParentsNames);
        final Element headingsPositionsElement = document.createElement(XMLForms.HEADINGS_POSITIONS);
        addChild(headingsPositionsElement, XMLForms.LEFT_HEADINGS, Boolean.toString(leftHeadings), true, true);
        addChild(headingsPositionsElement, XMLForms.TOP_HEADINGS, Boolean.toString(topHeadings), true, true);
        addChild(headingsPositionsElement, XMLForms.RIGHT_HEADINGS, Boolean.toString(rightHeadings), true, true);
        addChild(headingsPositionsElement, XMLForms.BOTTOM_HEADINGS, Boolean.toString(bottomHeadings), true, true);
        push(headingsPositionsElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addReadOnlyBehavior(final boolean isReadOnly) throws InvalidFormDefinitionException {
        final String[] readOnlyBehaviorParentsNames = { XMLForms.WIDGET };
        try {
            peek(readOnlyBehaviorParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a read-only behaviour is only supported on elements of type " + Arrays.asList(readOnlyBehaviorParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("read-only behavior", isReadOnly);
        final Element mandatoryBehaviorElement = document.createElement(XMLForms.READ_ONLY);
        mandatoryBehaviorElement.setTextContent(Boolean.toString(isReadOnly));
        push(mandatoryBehaviorElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addHorizontalHeaderExpression(final String expresssion) throws InvalidFormDefinitionException {
        final String[] horizontalHeaderParentsNames = { XMLForms.WIDGET };
        try {
            peek(horizontalHeaderParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a horizontal header is only supported on elements of type " + Arrays.asList(horizontalHeaderParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("horizontal header expresssion", expresssion);
        final Element horizontalHeaderElement = document.createElement(XMLForms.HORIZONTAL_HEADER);
        horizontalHeaderElement.setTextContent(expresssion);
        push(horizontalHeaderElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMaxColumnsExpression(final String expression) throws InvalidFormDefinitionException {
        final String[] maxColumnsParentsNames = { XMLForms.WIDGET };
        try {
            peek(maxColumnsParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a max columns property is only supported on elements of type " + Arrays.asList(maxColumnsParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element maxColumnsElement = document.createElement(XMLForms.MAX_COLUMNS);
        maxColumnsElement.setTextContent(expression);
        push(maxColumnsElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMaxRowsExpression(final String expression) throws InvalidFormDefinitionException {
        final String[] maxRowsParentsNames = { XMLForms.WIDGET };
        try {
            peek(maxRowsParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a max rows property is only supported on elements of type " + Arrays.asList(maxRowsParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element maxRowsElement = document.createElement(XMLForms.MAX_ROWS);
        maxRowsElement.setTextContent(expression);
        push(maxRowsElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMinColumnsExpression(final String expression) throws InvalidFormDefinitionException {
        final String[] minColumnsParentsNames = { XMLForms.WIDGET };
        try {
            peek(minColumnsParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a min columns property is only supported on elements of type " + Arrays.asList(minColumnsParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element minColumnsElement = document.createElement(XMLForms.MIN_COLUMNS);
        minColumnsElement.setTextContent(expression);
        push(minColumnsElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMinRowsExpression(final String expression) throws InvalidFormDefinitionException {
        final String[] minRowsParentsNames = { XMLForms.WIDGET };
        try {
            peek(minRowsParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a min rows property is only supported on elements of type " + Arrays.asList(minRowsParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element minRowsElement = document.createElement(XMLForms.MIN_ROWS);
        minRowsElement.setTextContent(expression);
        push(minRowsElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addVariableColumnsNumber(final boolean variableColumnsNumber) throws InvalidFormDefinitionException {
        final String[] variableColumnsNumberParentsNames = { XMLForms.WIDGET };
        try {
            peek(variableColumnsNumberParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a variable columns number behaviour is only supported on elements of type " + Arrays.asList(variableColumnsNumberParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("variable columns number behaviour", variableColumnsNumber);
        final Element variableColumnsNumberElement = document.createElement(XMLForms.VARIABLE_COLUMNS);
        variableColumnsNumberElement.setTextContent(Boolean.toString(variableColumnsNumber));
        push(variableColumnsNumberElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addVariableRowsNumber(final boolean variableRowsNumber) throws InvalidFormDefinitionException {
        final String[] variableRowsNumberParentsNames = { XMLForms.WIDGET };
        try {
            peek(variableRowsNumberParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a variable rows number behaviour is only supported on elements of type " + Arrays.asList(variableRowsNumberParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("variable rows number behaviour", variableRowsNumber);
        final Element variableRowsNumberElement = document.createElement(XMLForms.VARIABLE_ROWS);
        variableRowsNumberElement.setTextContent(Boolean.toString(variableRowsNumber));
        push(variableRowsNumberElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addValueColumnIndex(final String expression) throws InvalidFormDefinitionException {
        final String[] valueColumnIndexParentsNames = { XMLForms.WIDGET };
        try {
            peek(valueColumnIndexParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a value column index property is only supported on elements of type " + Arrays.asList(valueColumnIndexParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element valueColumnIndexElement = document.createElement(XMLForms.VALUE_COLUMN_INDEX);
        valueColumnIndexElement.setTextContent(expression);
        push(valueColumnIndexElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addSelectMode(final SelectMode selectMode) throws InvalidFormDefinitionException {
        final String[] styleParentsNames = { XMLForms.WIDGET };
        try {
            peek(styleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a select mode is only supported on elements of type " + Arrays.asList(styleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("select mode", selectMode);
        final Element selectModeElement = document.createElement(XMLForms.SELECT_MODE);
        selectModeElement.setTextContent(selectMode.name());
        push(selectModeElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addSelectedItemsStyle(final String selectedItemsStyle) throws InvalidFormDefinitionException {
        final String[] styleParentsNames = { XMLForms.WIDGET };
        try {
            peek(styleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a selected items style is only supported on elements of type " + Arrays.asList(styleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("selected items style", selectedItemsStyle);
        final Element styleElement = document.createElement(XMLForms.SELECTED_ITEMS_STYLE);
        styleElement.setTextContent(selectedItemsStyle);
        push(styleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addVerticalHeaderExpression(final String expresssion) throws InvalidFormDefinitionException {
        final String[] verticalHeaderParentsNames = { XMLForms.WIDGET };
        try {
            peek(verticalHeaderParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a vertical header is only supported on elements of type " + Arrays.asList(verticalHeaderParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("vertical header expresssion", expresssion);
        final Element verticalHeaderElement = document.createElement(XMLForms.VERTICAL_HEADER);
        verticalHeaderElement.setTextContent(expresssion);
        push(verticalHeaderElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addMaxItems(final int maxItems) throws InvalidFormDefinitionException {
        final String[] maxItemsParentsNames = { XMLForms.WIDGET };
        try {
            peek(maxItemsParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a max items property is only supported on elements of type " + Arrays.asList(maxItemsParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element maxItemsElement = document.createElement(XMLForms.MAX_ITEMS);
        maxItemsElement.setTextContent(Integer.toString(maxItems));
        push(maxItemsElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addTransientData(final String name, final String className, final String value) throws InvalidFormDefinitionException {
        final String[] pageflowParentsNames = { XMLForms.FORM };
        try {
            peek(pageflowParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a transient data is only supported on elements of type " + Arrays.asList(pageflowParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("data name", name);
        checkStringNotEmpty("data name", name);
        Element transientDataElement = findChildElement(currentElement, XMLForms.TRANSIENT_DATA);
        if (transientDataElement == null) {
            transientDataElement = document.createElement(XMLForms.TRANSIENT_DATA);
        }
        push(transientDataElement);
        final Element dataElement = document.createElement(XMLForms.DATA);
        dataElement.setAttribute(XMLForms.NAME, name);
        addChild(dataElement, XMLForms.CLASSNAME, className, true, true);
        addChild(dataElement, XMLForms.VALUE, value, false, false);
        push(dataElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addFirstPageId(final String pageId) throws InvalidFormDefinitionException {
        final String[] firstPageParentsNames = { XMLForms.FORM };
        try {
            peek(firstPageParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a first page attribute is only supported on elements of type " + Arrays.asList(firstPageParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("first page id", pageId);
        currentElement.setAttribute(XMLForms.FIRST_PAGE, pageId);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addNextPageId(final String pageId) throws InvalidFormDefinitionException {
        final String[] firstPageParentsNames = { XMLForms.PAGE };
        try {
            peek(firstPageParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a next page attribute is only supported on elements of type " + Arrays.asList(firstPageParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("next page id", pageId);
        final Element nextPageElement = document.createElement(XMLForms.NEXT_PAGE);
        nextPageElement.setTextContent(pageId);
        push(nextPageElement);
        return this;
    }

    public IFormBuilder addDisplayConditionExpression(final String displayConditionExpression) throws InvalidFormDefinitionException {
        final String[] displayConditionParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP };
        try {
            peek(displayConditionParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a display condition is only supported on elements of type " + Arrays.asList(displayConditionParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("display condition", displayConditionExpression);
        final Element displayConditionElement = document.createElement(XMLForms.DISPLAY_CONDITION);
        displayConditionElement.setTextContent(displayConditionExpression);
        push(displayConditionElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addDelayMillis(final int delayMillis) throws InvalidFormDefinitionException {
        final String[] delayMillisParentsNames = { XMLForms.WIDGET };
        try {
            peek(delayMillisParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a delay millis property is only supported on elements of type " + Arrays.asList(delayMillisParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element delayMillisElement = document.createElement(XMLForms.DELAY_MILLIS);
        delayMillisElement.setTextContent(Integer.toString(delayMillis));
        push(delayMillisElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addSubTitle(final String label, final SubTitlePosition position) throws InvalidFormDefinitionException {
        final String[] subTitleParentsNames = { XMLForms.WIDGET };
        try {
            peek(subTitleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a sub title property is only supported on elements of type " + Arrays.asList(subTitleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element subTitleElement = document.createElement(XMLForms.SUB_TITLE);
        addChild(subTitleElement, XMLForms.LABEL, label, true, false);
        if (position != null) {
            addChild(subTitleElement, XMLForms.POSITION, position.name(), false, true);
        }
        push(subTitleElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addPopupToolTip(String tips) throws InvalidFormDefinitionException {
        final String[] popupToolTipParentsNames = { XMLForms.WIDGET };
        try {
            peek(popupToolTipParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a pupup tooltip property is only supported on elements of type " + Arrays.asList(popupToolTipParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element popupToolTipElement = document.createElement(XMLForms.POPUP_TOOLTIP);
        popupToolTipElement.setTextContent(tips);
        push(popupToolTipElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addPermissions(String permissions) throws InvalidFormDefinitionException {
        final String[] permissionsParentsNames = { XMLForms.APPLICATION, XMLForms.FORM };
        try {
            peek(permissionsParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of permissions property is only supported on elements of type " + Arrays.asList(permissionsParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        final Element permissionsElement = document.createElement(XMLForms.PERMISSIONS);
        permissionsElement.setTextContent(permissions);
        push(permissionsElement);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public IFormBuilder addNextFormId(final String nextFormId) throws InvalidFormDefinitionException {
        final String[] nextFormParentsNames = { XMLForms.FORM };
        try {
            peek(nextFormParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a next form attribute is only supported on elements of type " + Arrays.asList(nextFormParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("next form id", nextFormId);
        final Element nextPageElement = document.createElement(XMLForms.NEXT_FORM);
        nextPageElement.setTextContent(nextFormId);
        push(nextPageElement);
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public IFormBuilder addInputStyle(String cssClasses) throws InvalidFormDefinitionException {
        final String[] styleParentsNames = { XMLForms.WIDGET, XMLForms.WIDGETS_GROUP };
        try {
            peek(styleParentsNames);
        } catch (final InvalidFormDefinitionException e) {
            final String errorMessage = "The addition of a style is only supported on elements of type " + Arrays.asList(styleParentsNames);
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage, e);
        }
        checkArgNotNull("input style", cssClasses);
        final Element styleElement = document.createElement(XMLForms.INPUT_STYLE);
        styleElement.setTextContent(cssClasses);
        push(styleElement);
        return this;
    }

    /**
     * Add a child element
     * 
     * @param parentElement
     * @param childName
     * @param childValue
     * @param isMandatory
     * @throws InvalidFormDefinitionException
     */
    protected void addChild(final Element parentElement, final String childName, final String childValue, final boolean isMandatory, final boolean isEmptyForbidden) throws InvalidFormDefinitionException {
        if (isMandatory) {
            checkArgNotNull(childName, childValue);
        }
        if (isEmptyForbidden) {
            checkStringNotEmpty(childName, childValue);
        }
        if (childValue != null) {
            final Element element = document.createElement(childName);
            element.setTextContent(childValue);
            parentElement.appendChild(element);
        }
    }

    /**
     * Find the first element with the given tag name among an element children
     * 
     * @param parent the parent element
     * @param childName the tag name
     * @return an {@link Element} or null if there are no elements with thegiven tag name among the element's children
     */
    protected Element findChildElement(final Element parent, final String childName) {
        final NodeList nodeList = parent.getElementsByTagName(childName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element element = (Element) nodeList.item(i);
            if (element.getParentNode().isSameNode(parent)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Add an element to the stack
     * 
     * @param element
     */
    protected void push(final Element element) {
        if (element.getParentNode() == null) {
            currentElement.appendChild(element);
        }
        currentElement = element;
    }

    /**
     * Retrieve the first element in the DOM whose type is among the element types provided
     * 
     * @param elementTypes array of required element types
     * @return the first {@link Element} in the stack whose type is among the element types provided
     * @throws InvalidFormDefinitionException if no element among the current element's parents has one of the required type
     */
    protected Element peek(final String[] elementTypes) throws InvalidFormDefinitionException {
        final List<String> elementTypesList = Arrays.asList(elementTypes);
        while (currentElement.getParentNode() != null && currentElement.getParentNode().getNodeType() == Node.ELEMENT_NODE) {
            if (elementTypesList.contains(currentElement.getNodeName())) {
                return currentElement;
            }
            currentElement = (Element) currentElement.getParentNode();
        }
        if (elementTypesList.contains(currentElement.getNodeName())) {
            return currentElement;
        } else {
            throw new InvalidFormDefinitionException("No required element present among the parents of the current element.");
        }
    }

    /**
     * Verify that an element/attribute value is not null
     * 
     * @param name
     * @param value
     * @throws InvalidFormDefinitionException
     */
    protected void checkArgNotNull(final String name, final Object value) throws InvalidFormDefinitionException {
        if (value == null) {
            final String errorMessage = "The property " + name + " shouldn't be null.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        }
    }

    /**
     * Verify that an element/attribute value is not an empty string
     * 
     * @param name
     * @param value
     * @throws InvalidFormDefinitionException
     */
    protected void checkStringNotEmpty(final String name, final String value) throws InvalidFormDefinitionException {
        if (value != null && value.length() == 0) {
            final String errorMessage = "The property " + name + " shouldn't be empty.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        }
    }

}
