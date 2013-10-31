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
package org.bonitasoft.forms.server.accessor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormType;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.accessor.IApplicationConfigDefAccessor;
import org.bonitasoft.forms.server.accessor.IApplicationFormDefAccessor;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.accessor.impl.util.XMLUtil;
import org.bonitasoft.forms.server.accessor.widget.IXMLWidgetBuilder;
import org.bonitasoft.forms.server.accessor.widget.WidgetBuilderFactory;
import org.bonitasoft.forms.server.constants.XMLForms;
import org.bonitasoft.forms.server.exception.ApplicationFormDefinitionNotFoundException;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of {@link IApplicationFormDefAccessor} allowing to parse the xml form definition file to get the application
 * config
 * 
 * @author Anthony Birembaut, Haojie Yuan
 * 
 */
public class XMLApplicationFormDefAccessorImpl extends XMLUtil implements IApplicationFormDefAccessor {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(XMLApplicationFormDefAccessorImpl.class.getName());

    /**
     * DOM representation of the XML file
     */
    private final FormDocument formDocument;

    /**
     * The xpath query to get the process node
     */
    protected String applicationXpath;

    /**
     * the user locale
     */
    protected String locale;
    
    /**
     * the {@link Date} of the process deployment
     */
    protected Date applicationDeploymentDate;

    /**
     * The form node
     */
    protected Node formNode;

    /**
     * The form id
     */
    protected String formId;

    /**
     * indicate if the form is an entry form (true) or a view form (false)
     */
    protected boolean isEditMode;

    /**
     * XML widget builder
     */
    protected IXMLWidgetBuilder xmlWidgetBuilder;

    /**
     * the process definition UUID
     */
    protected ProcessDefinitionUUID processDefinitionUUID;

    /**
     * 
     * Default constructor.
     * 
     * @param formId
     * @param formDocument
     * @param applicationDeploymentDate
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws InvalidFormDefinitionException 
     */
    public XMLApplicationFormDefAccessorImpl(final String formId, final FormDocument formDocument, final String locale, final Date applicationDeploymentDate) throws ApplicationFormDefinitionNotFoundException, InvalidFormDefinitionException {

        super(formDocument.getXpathEvaluator());
        this.formDocument = formDocument;
        this.applicationDeploymentDate = applicationDeploymentDate;
        this.locale = locale;

        final StringBuilder applicationXpathBuilder = new StringBuilder();
        applicationXpathBuilder.append("//");
        applicationXpathBuilder.append(XMLForms.APPLICATION);
        applicationXpath = applicationXpathBuilder.toString();
        formNode = getFormNode(formId);
        String formType = null;
        if (formNode != null) {
            formType = getStringByXpath(formNode, XMLForms.FORM_TYPE);
        }
        if (FormType.entry.name().equals(formType)) {
            isEditMode = true;
        } else {
            isEditMode = false;
        }
        if (formNode == null) {
            final String message = "The node for the form " + formId + " was not found in the forms definition file";
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, message);
            }
            throw new ApplicationFormDefinitionNotFoundException(message);
        }
        processDefinitionUUID = getProcessDefinitionUUIDFromXML();
        xmlWidgetBuilder = WidgetBuilderFactory.getXMLWidgetBuilder(formDocument.getXpathEvaluator(), processDefinitionUUID, locale, applicationDeploymentDate);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPages() {
        final List<String> pages = new ArrayList<String>();

        final Node applicationNode = getNodeByXpath(formDocument.getDocument(), applicationXpath);
        if (applicationNode == null) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Failed to parse the forms definition file. query : " + applicationXpath);
            }
        } else {
            NodeList pageNodes = getNodeListByXpath(formNode, XMLForms.PAGES + "/" + XMLForms.PAGE);
            if (pageNodes != null) {
                for (int i = 0; i < pageNodes.getLength(); i++) {
                    final String id = getStringByXpath(pageNodes.item(i), "@" + XMLForms.ID);
                    pages.add(id);
                }
            }
        }
        return pages;
    }

    /**
     * {@inheritDoc}
     */
    public String getFirstPageExpression() {
        String formFirstPage = null;
        Node formFirstPageNode = getNodeByXpath(formNode, "@" + XMLForms.FIRST_PAGE);
        if (formFirstPageNode != null) {
            formFirstPage = formFirstPageNode.getTextContent();
        }
        return formFirstPage;
    }

    /**
     * {@inheritDoc}
     */
    public String getFormPermissions() throws InvalidFormDefinitionException {
        String permissions = null;
        Node permissionsNode = getNodeByXpath(formNode, XMLForms.PERMISSIONS);
        if (permissionsNode == null) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Failed to retrieve form permissions. Default permissions will be used.");
            }
        } else {
            permissions = permissionsNode.getTextContent();
        }
        return permissions;
    }

    /**
     * {@inheritDoc}
     */
    public String getNextForm() throws InvalidFormDefinitionException {
        String nextForm = null;
        Node nextFormNode = getNodeByXpath(formNode, XMLForms.NEXT_FORM);
        if (nextFormNode == null) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "No next form is specified for form " + formId);
            }
        } else {
            nextForm = nextFormNode.getTextContent();
        }
        return nextForm;
    }

    /**
     * {@inheritDoc}
     */
    public String getFormPageLayout(final String pageId) throws InvalidFormDefinitionException {

        String templatePath = null;

        final Node pageNode = getPageNode(pageId);
        if (pageNode == null) {
            final String errorMessage = "Failed to parse the forms definition file. Page " + pageId + " not found.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        } else {
            final Node pageTemplateNode = getNodeByXpath(pageNode, XMLForms.PAGE_LAYOUT);
            if (pageTemplateNode == null) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "Failed to parse the form definition file. The default page template will be used.");
                }
                templatePath = null;
            } else {
                templatePath = pageTemplateNode.getTextContent();
            }
        }
        return templatePath;
    }

    /**
     * Retrieve the page node for a given page id
     * 
     * @param pageId
     * @return the {@link Node} for the page
     */
    protected Node getPageNode(final String pageId) {
        final String xpath = getPageXpath(pageId);
        return getNodeByXpath(formNode, xpath);
    }

    /**
     * Build the xpath query to get a application page
     * 
     * @param pageId
     * @return an xpath query under the form of a String
     */
    protected String getPageXpath(final String pageId) {
        final StringBuilder pageXpathBuilder = new StringBuilder();
        pageXpathBuilder.append(XMLForms.PAGES);
        pageXpathBuilder.append("/");
        pageXpathBuilder.append(XMLForms.PAGE);
        pageXpathBuilder.append("[@");
        pageXpathBuilder.append(XMLForms.ID);
        pageXpathBuilder.append("='");
        pageXpathBuilder.append(pageId);
        pageXpathBuilder.append("']");
        return pageXpathBuilder.toString();
    }

    /**
     * Retrieve the page node for a given page id
     * 
     * @param formId the formId
     * @return the {@link Node} for the page
     */
    protected Node getFormNode(final String formId) {
        final String xpath = getFormPageXpath(formId);
        return getNodeByXpath(formDocument.getDocument(), xpath);
    }

    /**
     * Build the xpath query to get a application page
     * 
     * @param formId the formId
     * @return an xpath query under the form of a String
     */
    protected String getFormPageXpath(final String formId) {
        final StringBuilder pageXpathBuilder = new StringBuilder();
        pageXpathBuilder.append(applicationXpath);
        pageXpathBuilder.append("/");
        pageXpathBuilder.append(XMLForms.FORMS);
        pageXpathBuilder.append("/");
        pageXpathBuilder.append(XMLForms.FORM);
        pageXpathBuilder.append("[@");
        pageXpathBuilder.append(XMLForms.ID);
        pageXpathBuilder.append("='");
        pageXpathBuilder.append(formId);
        pageXpathBuilder.append("']");
        return pageXpathBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public List<FormWidget> getPageWidgets(final String pageId) throws InvalidFormDefinitionException, ApplicationFormDefinitionNotFoundException {

        List<FormWidget> widgets = new ArrayList<FormWidget>();

        final Node pageNode = getPageNode(pageId);
        if (pageNode == null) {
            final String errorMessage = "Failed to parse the forms definition file. Page " + pageId + " not found.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        } else {
            widgets = xmlWidgetBuilder.getPageWidgets(pageNode, isEditMode);
        }
        return widgets;
    }

    /**
     * {@inheritDoc}
     */
    public List<FormValidator> getPageValidators(final String pageId) throws InvalidFormDefinitionException, ApplicationFormDefinitionNotFoundException {

        List<FormValidator> pageValidators = new ArrayList<FormValidator>();

        final Node pageNode = getPageNode(pageId);
        if (pageNode == null) {
            final String errorMessage = "Failed to parse the forms definition file. Page " + pageId + " not found.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        } else {
            pageValidators = xmlWidgetBuilder.getPageValidators(pageNode);
        }
        return pageValidators;
    }

    /**
     * {@inheritDoc}
     */
    public String getPageLabel(final String pageId) throws InvalidFormDefinitionException {

        String label = null;

        final Node pageNode = getPageNode(pageId);
        if (pageNode == null) {
            final String errorMessage = "Failed to parse the forms definition file. Page " + pageId + " not found.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        } else {
            final Node pageLabelNode = getNodeByXpath(pageNode, XMLForms.PAGE_LABEL);
            if (pageLabelNode == null) {
                final String errorMessage = "The label for page " + pageId + " for process instantiation was not found in the forms definition file";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, errorMessage);
                }
                throw new InvalidFormDefinitionException(errorMessage);
            } else {
                label = pageLabelNode.getTextContent();
            }
        }
        return label;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isHTMLAllowedInLabel(final String pageId) throws InvalidFormDefinitionException {

        boolean allowHTMLInLabel = false;

        final Node pageNode = getPageNode(pageId);
        if (pageNode == null) {
            final String errorMessage = "Failed to parse the forms definition file. Page " + pageId + " not found.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        } else {
            final Node pageAllowHTMLNode = getNodeByXpath(pageNode, XMLForms.ALLOW_HTML_IN_LABEL);
            if (pageAllowHTMLNode != null) {
                final String allowHTMLInLabelStr = pageAllowHTMLNode.getTextContent();
                allowHTMLInLabel = Boolean.parseBoolean(allowHTMLInLabelStr);
            }
        }
        return allowHTMLInLabel;
    }

    /**
     * {@inheritDoc}
     */
    public String getNextPageExpression(final String pageId) throws InvalidFormDefinitionException {

        String nextPageExpression = null;
        final Node pageNode = getPageNode(pageId);
        if (pageNode == null) {
            final String errorMessage = "Failed to parse the forms definition file. Page " + pageId + " not found.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        } else {
            final Node nextPageNode = getNodeByXpath(pageNode, XMLForms.NEXT_PAGE);
            if (nextPageNode != null) {
                nextPageExpression = nextPageNode.getTextContent();
            }
        }
        return nextPageExpression;
    }

    /**
     * {@inheritDoc}
     */
    public List<TransientData> getTransientData() throws InvalidFormDefinitionException {
        final List<TransientData> transientData = new ArrayList<TransientData>();

        final NodeList dataNodes = getNodeListByXpath(formNode, XMLForms.TRANSIENT_DATA + "/" + XMLForms.DATA);
        if (dataNodes != null) {
            for (int i = 0; i < dataNodes.getLength(); i++) {
                final Node dataNode = dataNodes.item(i);
                final String name = getStringByXpath(dataNode, "@" + XMLForms.NAME);
                if (name == null || name.trim().length() == 0) {
                    final String errorMessage = "Failed to parse the forms definition file for the application " + applicationXpath + ". name for transient data missing.";
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, errorMessage);
                    }
                    throw new InvalidFormDefinitionException(errorMessage);
                }
                final Node classNameNode = getNodeByXpath(dataNode, XMLForms.CLASSNAME);
                String className = null;
                if (classNameNode != null) {
                    className = classNameNode.getTextContent();
                }
                if (className == null || className.trim().length() == 0) {
                    final String errorMessage = "Failed to parse the forms definition file for the applicationXpath " + applicationXpath + ". classname for transient data " + name + " not found.";
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, errorMessage);
                    }
                    throw new InvalidFormDefinitionException(errorMessage);
                }
                final Node valueNode = getNodeByXpath(dataNode, XMLForms.VALUE);
                String value = null;
                if (valueNode != null) {
                    value = valueNode.getTextContent();
                }
                transientData.add(new TransientData(name, className, value));
            }
        }
        return transientData;
    }

    /**
     * Get ProcessDefinitionUUID by Document
     * 
     * @return ProcessDefinitionUUID
     * @throws InvalidFormDefinitionException
     */
    protected ProcessDefinitionUUID getProcessDefinitionUUIDFromXML() throws InvalidFormDefinitionException {
        final IApplicationConfigDefAccessor applicationConfigFormDefinition = new XMLApplicationConfigDefAccessorImpl(formDocument);
        if (applicationConfigFormDefinition instanceof XMLApplicationConfigDefAccessorImpl) {
            final String name = applicationConfigFormDefinition.getApplicationName();
            final String version = applicationConfigFormDefinition.getApplicationVersion();
            return new ProcessDefinitionUUID(name, version);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<FormAction> getActions(final String pageId) throws InvalidFormDefinitionException, ApplicationFormDefinitionNotFoundException {

        return xmlWidgetBuilder.getActions(formNode, pageId);
    }

    /**
     * {@inheritDoc}
     */
    public String getConfirmationLayout() throws InvalidFormDefinitionException {

        String path = null;
        Node confirmationTemplateNode = getNodeByXpath(formNode, XMLForms.CONFIRMATION_LAYOUT);
        if (confirmationTemplateNode == null) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "No confirmation template was found. The default confirmation page will be used.");
            }
            path =  DefaultFormsPropertiesFactory.getDefaultFormProperties().getPageConfirmationTemplate();
        } else {
            path = confirmationTemplateNode.getTextContent();
        }
        return path;
    }

    /**
     * {@inheritDoc}
     */
    public String getConfirmationMessage() throws InvalidFormDefinitionException {

        String message = null;
        Node confirmationMessageNode = getNodeByXpath(formNode, XMLForms.CONFIRMATION_MESSAGE);
        if (confirmationMessageNode == null) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "No confirmation message was found. The default confirmation message will be used.");
            }
        } else {
            message = confirmationMessageNode.getTextContent();
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    public FormType getFormType() throws InvalidFormDefinitionException {
        String formType = null;
        Node formTypeNode = getNodeByXpath(formNode, XMLForms.FORM_TYPE);
        if (formTypeNode == null) {
            String errorMessage = "No form type is specified for form " + formId;
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        } else {
            formType = formTypeNode.getTextContent();
        }
        return FormType.valueOf(formType);
    }
}
