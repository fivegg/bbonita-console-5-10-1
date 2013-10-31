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
package org.bonitasoft.forms.server.accessor.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.accessor.IApplicationConfigDefAccessor;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.accessor.impl.util.XMLUtil;
import org.bonitasoft.forms.server.constants.XMLForms;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Haojie Yuan
 * 
 */
public class XMLApplicationConfigDefAccessorImpl extends XMLUtil implements IApplicationConfigDefAccessor {

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(XMLApplicationConfigDefAccessorImpl.class.getName());

    /**
     * DOM representation of the XML file
     */
    private final Document document;

    /**
     * The xpath query to get the process node
     */
    protected String applicationXpath;

    /**
     * Constructor
     * 
     * @param formDocument
     */
    public XMLApplicationConfigDefAccessorImpl(final FormDocument formDocument) {

        super(formDocument.getXpathEvaluator());
        this.document = formDocument.getDocument();

        final StringBuilder applicationXpathBuilder = new StringBuilder();
        applicationXpathBuilder.append("//");
        applicationXpathBuilder.append(XMLForms.APPLICATION);
        applicationXpath = applicationXpathBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationErrorTemplate() {
        String applicationErrorTemplate = null;

        final String xpath = "//" + XMLForms.ERROR_TEMPLATE;

        final Node applicationErrorTemplateNode = getNodeByXpath(document, xpath);
        if (applicationErrorTemplateNode == null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "No error template element was found in the definition file. The default error layout will be used.");
            }
            applicationErrorTemplate = DefaultFormsPropertiesFactory.getDefaultFormProperties().getPageErrorTemplate();
        } else {
            applicationErrorTemplate = applicationErrorTemplateNode.getTextContent();
        }
        return applicationErrorTemplate;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationLabel() throws InvalidFormDefinitionException {

        String applicationLabel = null;

        final String xpath = applicationXpath + "/" + XMLForms.APPLICATION_LABEL;

        final Node processLabelNode = getNodeByXpath(document, xpath);
        if (processLabelNode == null) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Failed to retrieve application label element. query : " + xpath);
            }
            throw new InvalidFormDefinitionException("The application label was not found in the forms definition file");
        } else {
            applicationLabel = processLabelNode.getTextContent();
        }
        return applicationLabel;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationMandatoryLabel() {

        String applicationMandatoryLabel = null;

        final String xpath = applicationXpath + "/" + XMLForms.MANDATORY_LABEL;

        final Node processMandatorySymbolNode = getNodeByXpath(document, xpath);
        if (processMandatorySymbolNode == null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "No mandatory label element was found in the definition file. Default label will be used.");
            }
            applicationMandatoryLabel = DefaultFormsPropertiesFactory.getDefaultFormProperties().getApplicationMandatoryLabel();
        } else {
            applicationMandatoryLabel = processMandatorySymbolNode.getTextContent();
        }
        return applicationMandatoryLabel;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationMandatorySymbol() {

        String applicationMandatorySymbol = null;

        final String xpath = applicationXpath + "/" + XMLForms.MANDATORY_SYMBOL;

        final Node applicationMandatorySymbolNode = getNodeByXpath(document, xpath);
        if (applicationMandatorySymbolNode == null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "No mandatory symbol element was found in the definition file. Default symbol will be used.");
            }
            applicationMandatorySymbol = DefaultFormsPropertiesFactory.getDefaultFormProperties().getApplicationMandatorySymbol();
        } else {
            applicationMandatorySymbol = applicationMandatorySymbolNode.getTextContent();
        }
        return applicationMandatorySymbol;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationMandatorySymbolStyle() {

        String applicationMandatorySymbolClasses = null;

        final String xpath = applicationXpath + "/" + XMLForms.MANDATORY_STYLE;

        final Node applicationMandatorySymbolClassesNode = getNodeByXpath(document, xpath);
        if (applicationMandatorySymbolClassesNode == null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "No CSS classes were found in the definition file for the application mandatory symbol element. Default style will be used");
            }
        } else {
            applicationMandatorySymbolClasses = applicationMandatorySymbolClassesNode.getTextContent();
        }
        return applicationMandatorySymbolClasses;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationName() throws InvalidFormDefinitionException {
        String applicationName = null;

        final String xpath = "//" + XMLForms.APPLICATION;

        final Node applicationNode = getNodeByXpath(document, xpath);
        if (applicationNode == null) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Failed to retrieve application element. query : " + xpath);
            }
            throw new InvalidFormDefinitionException("The application was not found in the forms definition file");
        } else {
            applicationName = getStringByXpath(applicationNode, "@" + XMLForms.NAME);
        }
        return applicationName;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationVersion() throws InvalidFormDefinitionException {
        String applicationVersion = null;

        final String xpath = "//" + XMLForms.APPLICATION;

        final Node applicationNode = getNodeByXpath(document, xpath);
        if (applicationNode == null) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Failed to retrieve application element. query : " + xpath);
            }
            throw new InvalidFormDefinitionException("The application was not found in the forms definition file");
        } else {
            applicationVersion = getStringByXpath(applicationNode, "@" + XMLForms.VERSION);
        }
        return applicationVersion;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationLayout() {
        String applicationLayOut = null;

        final String xpath = "//" + XMLForms.APPLICATION_LAYOUT;

        final Node applicationTemplateNode = getNodeByXpath(document, xpath);
        if (applicationTemplateNode == null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "No process template element was found in the definition file. Default process template will be used.");
            }
            applicationLayOut = DefaultFormsPropertiesFactory.getDefaultFormProperties().getApplicationLayout();
        } else {
            applicationLayOut = applicationTemplateNode.getTextContent();
        }
        return applicationLayOut;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationPermissions() {
        String permissions = null;
        Node permissionsNode = null;

        final String applicationXpath = "//" + XMLForms.APPLICATION;
        final Node applicationNode = getNodeByXpath(document, applicationXpath);

        if (applicationNode == null) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Failed to retrieve application element. query : " + applicationXpath);
            }
        } else {
            permissionsNode = getNodeByXpath(applicationNode, XMLForms.PERMISSIONS);
            if (permissionsNode == null) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Failed to retrieve application permissions. the permission element is missing from the definition file.");
                }
            } else {
                permissions = permissionsNode.getTextContent();
            }
        }
        return permissions;
    }

    /**
     * {@inheritDoc}
     */
    public String getMigrationProductVersion() {

        String migrationProductVersion = null;

        final String xpath = "//" + XMLForms.MIGRATION_PRODUCT_VERSION;

        final Node migrationProductVersionNode = getNodeByXpath(document, xpath);
        if (migrationProductVersionNode == null) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Failed to retrieve migration product version element. query : " + xpath);
            }
        } else {
            migrationProductVersion = migrationProductVersionNode.getTextContent();
        }
        return migrationProductVersion;
    }

    /**
     * {@inheritDoc}
     */
    public String getExternalWelcomePage() {

        return getWelcomePage(true);
    }

    /**
     * {@inheritDoc}
     */
    public String getWelcomePage() {

        return getWelcomePage(false);
    }

    /**
     * {@inheritDoc}
     */
    public String getHomePage() {

        String homePage = null;
        final String xpath = "//" + XMLForms.HOME_PAGE;
        final Node homePageNode = getNodeByXpath(document, xpath);
        homePage = homePageNode.getTextContent();
        return homePage;
    }

    /**
     * Retrieve a welcome page path
     * 
     * @param isExternal indicates whether the required welcome page is external or not
     * @return the welcome page path
     */
    private String getWelcomePage(final boolean isExternal) {

        String externalWelcomePage = null;

        String xpath = null;
        if (isExternal) {
            xpath = "//" + XMLForms.EXTERNAL_WELCOME_PAGE;
        } else {
            xpath = "//" + XMLForms.WELCOME_PAGE;
        }

        final Node externalWelcomePageNode = getNodeByXpath(document, xpath);
        if (externalWelcomePageNode == null) {
            externalWelcomePage = null;
        } else {
            externalWelcomePage = externalWelcomePageNode.getTextContent();
        }
        return externalWelcomePage;
    }

    /**
     * {@inheritDoc}
     */
    public String getProductVersion() {

        String productVersion = null;

        final String xpath = "//" + XMLForms.FORMS_DEFINITION;

        final Node formsDefinitionNode = getNodeByXpath(document, xpath);
        if (formsDefinitionNode == null) {
            productVersion = null;
        } else {
            productVersion = getStringByXpath(formsDefinitionNode, "@" + XMLForms.PRODUCT_VERSION);
        }
        return productVersion;
    }
}
