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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.forms.server.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bonitasoft.forms.client.model.ApplicationConfig;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.HtmlTemplate;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.server.exception.ApplicationFormDefinitionNotFoundException;
import org.bonitasoft.forms.server.exception.FormNotFoundException;
import org.bonitasoft.forms.server.exception.FormServiceProviderNotFoundException;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.bonitasoft.forms.server.exception.InvalidFormTemplateException;

/**
 * API to retrieve the different form components associated with a process and its activities.
 * The application form construction mechanism leverage this API.
 * 
 * @author Anthony Birembaut, Haojie Yuan
 */
public interface IFormDefinitionAPI {

    /**
     * Retrieve the application welcome page
     * 
     * @param context
     *            Map containing the URL parameters
     * @return the HtmlTemplate for the welcome page
     * @throws InvalidFormTemplateException
     * @throws InvalidFormDefinitionException
     * @throws FileNotFoundException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    HtmlTemplate getWelcomePage(Map<String, Object> context) throws FileNotFoundException, InvalidFormTemplateException, InvalidFormDefinitionException,
            ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException;

    /**
     * Retrieve the application external welcome page URL as a string
     * 
     * @return the application external welcome page URL
     * @throws FormServiceProviderNotFoundException
     */
    String getExternalWelcomePage() throws FormServiceProviderNotFoundException;

    /**
     * Retrieve the application home page URL as a string
     * 
     * @return the application home page URL
     * @throws FormServiceProviderNotFoundException
     */
    String getHomePage() throws FormServiceProviderNotFoundException;

    /**
     * Retrieve the application permission string
     * 
     * @param formID
     *            the formID of the application form
     * @param context
     *            Map containing the URL parameters
     * @return the application permission string
     * @throws InvalidFormDefinitionException
     * @throws FormServiceProviderNotFoundException
     */
    String getApplicationPermissions(String formID, Map<String, Object> context) throws InvalidFormDefinitionException, FormServiceProviderNotFoundException;

    /**
     * Retrieve the product version as a string
     * 
     * @return product version
     * @throws FormServiceProviderNotFoundException
     */
    String getProductVersion() throws FormServiceProviderNotFoundException;

    /**
     * Retrieve the migration product version string
     * 
     * @param formID
     *            the formID of the application form
     * @param context
     *            Map containing the URL parameters
     * @return the migration product version string
     * @throws InvalidFormDefinitionException
     * @throws FormServiceProviderNotFoundException
     */
    String getMigrationProductVersion(String formID, Map<String, Object> context) throws InvalidFormDefinitionException, FormServiceProviderNotFoundException;

    /**
     * Retrieve the form permissions string
     * 
     * @param formID
     *            the formID of the specific form
     * @param context
     *            Map containing the URL parameters
     * @return the form permission string
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws InvalidFormDefinitionException
     * @throws FormServiceProviderNotFoundException
     */
    String getFormPermissions(String formID, Map<String, Object> context) throws ApplicationFormDefinitionNotFoundException, InvalidFormDefinitionException,
            FormServiceProviderNotFoundException;

    /**
     * Retrieve the next form
     * 
     * @param formID
     *            the form ID
     * @param context
     *            Map containing the URL parameters
     * @return the next form ID as a string
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws InvalidFormDefinitionException
     * @throws FormServiceProviderNotFoundException
     */
    String getNextForm(String formID, Map<String, Object> context) throws ApplicationFormDefinitionNotFoundException, InvalidFormDefinitionException,
            FormServiceProviderNotFoundException;

    /**
     * Retrieve the application config including the application template
     * 
     * @param context
     *            Map containing the URL parameters
     * @param formID
     *            the form ID
     * @param includeApplicationTemplate
     * @param includeApplicationTemplate
     *            indicate whether the Level 1 HTM template should be returned or not within the ApplicationConfig object
     * @return a {@link ApplicationConfig} object containing the elements required to build the level1 (application template)
     * @throws InvalidFormDefinitionException
     * @throws FormServiceProviderNotFoundException
     */
    ApplicationConfig getApplicationConfig(Map<String, Object> context, String formID, boolean includeApplicationTemplate)
            throws InvalidFormDefinitionException, FormServiceProviderNotFoundException;

    /**
     * Retrieve the expression which can be evaluated to get the first page of the form
     * 
     * @param formID
     *            the form ID
     * @param context
     *            Map containing the URL parameters
     * @return an expression
     * @throws FormNotFoundException
     * @throws InvalidFormDefinitionException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    String getFormFirstPage(String formID, Map<String, Object> context) throws FormNotFoundException, InvalidFormDefinitionException,
            ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException;

    /**
     * Retrieve the list of transient data for a form
     * 
     * @param formID
     *            the form ID
     * @param context
     *            Map containing the URL parameters
     * @return a List of {@link TransientData}
     * @throws InvalidFormDefinitionException
     * @throws FormNotFoundException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    List<TransientData> getFormTransientData(String formID, Map<String, Object> context) throws InvalidFormDefinitionException, FormNotFoundException,
            ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException;

    /**
     * Retrieve a page data containing a template and a list of the widgets to be displayed
     * 
     * @param formID
     *            the form ID
     * @param pageId
     *            the page ID
     * @param context
     *            Map containing the URL parameters
     * @return a {@link FormPage} object containing the elements required to build the level2 (page form)
     * @throws InvalidFormDefinitionException
     * @throws IOException
     * @throws FormNotFoundException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    FormPage getFormPage(String formID, String pageID, Map<String, Object> context) throws InvalidFormDefinitionException, FormNotFoundException,
            FileNotFoundException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException;

    /**
     * Retrieve the confirmation template for a form
     * 
     * @param formID
     *            the form ID
     * @param context
     *            Map containing the URL parameters
     * @return an {@link HtmlTemplate} object representing the page flow confirmation page
     * @throws FileNotFoundException
     * @throws FormNotFoundException
     * @throws InvalidFormDefinitionException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    HtmlTemplate getFormConfirmationLayout(String formID, Map<String, Object> context) throws InvalidFormDefinitionException, FormNotFoundException,
            FileNotFoundException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException;

    /**
     * Retrieve a list of actions to be executed at form submission
     * 
     * @param formID
     *            the form ID
     * @param pageIds
     *            list of pages for which the actions are required
     * @param context
     *            Map containing the URL parameters
     * @return a list of {@link FormAction} to be executed at form submission
     * @throws InvalidFormDefinitionException
     * @throws FormNotFoundException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    List<FormAction> getFormActions(String formID, List<String> pageIds, Map<String, Object> context) throws InvalidFormDefinitionException,
            FormNotFoundException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException;

    /**
     * Parse a page layout and insert some step attributes in it
     * 
     * @param bodyContent
     *            the body content
     * @param stepAttributes
     *            the map of step attributes
     * @param locale
     *            the user's locale
     * 
     * @return bodyContent
     *         the body content
     */
    String applicationAttributes(String bodyContent, Map<String, String> stepAttributes, Locale locale);

    /**
     * Retrieve and initialize the transient data for a form
     * 
     * @param transientData
     *            the List of transient data
     * @param locale
     *            the user's locale
     * @param context
     *            Map containing the URL parameters
     * @return a Map<String, Object> containing the context of transient data
     * @throws FormNotFoundException
     * @throws FormServiceProviderNotFoundException
     * @throws ClassNotFoundException
     */
    Map<String, Object> getTransientDataContext(List<TransientData> transientData, Locale userLocale, Map<String, Object> context)
            throws FormNotFoundException, FormServiceProviderNotFoundException, ClassNotFoundException;

    /**
     * Set the application definition deploy date
     * 
     * @param applicationDefinitionDate
     *            the Date that the application was deployed
     */
    void setApplicationDelpoymentDate(Date applicationDefinitionDate);

    /**
     * Retrieve the application error template
     * 
     * @return the error page template
     * @param context
     *            Map containing the URL parameters
     * @throws InvalidFormDefinitionException
     * @throws FileNotFoundException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    HtmlTemplate getApplicationErrorLayout(Map<String, Object> context) throws InvalidFormDefinitionException, FileNotFoundException,
            ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException;

}
