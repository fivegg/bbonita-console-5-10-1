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
package org.bonitasoft.forms.server.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

import org.bonitasoft.forms.client.model.ActivityAttribute;
import org.bonitasoft.forms.client.model.ApplicationConfig;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.FormType;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormValidator.ValidatorPosition;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.HeadNode;
import org.bonitasoft.forms.client.model.HtmlTemplate;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.client.model.WidgetType;
import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.accessor.IApplicationConfigDefAccessor;
import org.bonitasoft.forms.server.accessor.IApplicationFormDefAccessor;
import org.bonitasoft.forms.server.accessor.impl.XMLApplicationConfigDefAccessorImpl;
import org.bonitasoft.forms.server.accessor.impl.util.FormCacheUtilFactory;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.api.IFormDefinitionAPI;
import org.bonitasoft.forms.server.exception.ApplicationFormDefinitionNotFoundException;
import org.bonitasoft.forms.server.exception.FormNotFoundException;
import org.bonitasoft.forms.server.exception.FormServiceProviderNotFoundException;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.bonitasoft.forms.server.exception.InvalidFormTemplateException;
import org.bonitasoft.forms.server.provider.FormServiceProvider;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderFactory;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.util.Misc;

/**
 * Implementation of {@link IFormDefinitionAPI}
 * 
 * @author Anthony Birembaut, Haojie Yuan
 */
public class FormDefinitionAPIImpl implements IFormDefinitionAPI {

    /**
     * onload attribute to evaluate only with ie7 and ie8
     */
    private static final String IE_ONLOAD = "ieonload";

    /**
     * HTML script element name
     */
    private static final String SCRIPT_ELEMENT_NAME = "script";

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(FormDefinitionAPIImpl.class.getName());

    /**
     * default dateformat pattern
     */
    protected String defaultDateFormatPattern;

    /**
     * The document containing the form definition for the application
     */
    protected FormDocument formDefinitionDocument;

    /**
     * the {@link Date} of the application deployment
     */
    protected Date applicationDeploymentDate;

    /**
     * the user's locale as a String
     */
    protected String locale;

    /**
     * Constructor
     * 
     * @param document
     * @param applicationDeployementDate
     *            the deployement date of the application
     * @param locale
     *            the user's locale as a String
     * @return the FormDefinitionAPIImpl instance
     * @throws InvalidFormDefinitionException
     */
    public FormDefinitionAPIImpl(final FormDocument document, final Date applicationDeploymentDate, final String locale) throws InvalidFormDefinitionException {
        this.defaultDateFormatPattern = DefaultFormsPropertiesFactory.getDefaultFormProperties().getDefaultDateFormat();
        this.formDefinitionDocument = document;
        this.applicationDeploymentDate = applicationDeploymentDate;
        this.locale = locale;
    }

    /**
     * get the application config definition accessor by FormServiceProvider
     * 
     * @return an instance of {@link IApplicationConfigDefAccessor}
     * @throws FormServiceProviderNotFoundException
     */
    protected IApplicationConfigDefAccessor getApplicationConfigDefinition(final Map<String, Object> context) throws FormServiceProviderNotFoundException {
        FormServiceProvider formServiceProvider = null;
        formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        return formServiceProvider.getApplicationConfigDefinition(formDefinitionDocument, context);
    }

    /**
     * get XMLApplicationFormDefAccessorImpl by FormServiceProvider
     * 
     * @param formId
     *            the form ID
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws InvalidFormDefinitionException
     * @throws FormServiceProviderNotFoundException
     */
    protected IApplicationFormDefAccessor getApplicationDefinition(final String formId, final Map<String, Object> context)
            throws ApplicationFormDefinitionNotFoundException, InvalidFormDefinitionException, FormServiceProviderNotFoundException {
        context.put(FormServiceProviderUtil.APPLICATION_DEPLOYMENT_DATE, applicationDeploymentDate);
        FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        final IApplicationFormDefAccessor applicationDefAccessor = formServiceProvider.getApplicationFormDefinition(formId, formDefinitionDocument, context);
        return applicationDefAccessor;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationDelpoymentDate(Date applicationDefinitionDate) {
        this.applicationDeploymentDate = applicationDefinitionDate;
    }

    /**
     * {@inheritDoc}
     */
    public HtmlTemplate getWelcomePage(final Map<String, Object> urlContext) throws FileNotFoundException, InvalidFormTemplateException,
            InvalidFormDefinitionException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {
        if (urlContext.get(FormServiceProviderUtil.PROCESS_UUID) == null) {
            final ProcessDefinitionUUID processDefinitionUUID = getProcessDefinitionUUIDFromXML(urlContext);
            if (processDefinitionUUID != null) {
                urlContext.put(FormServiceProviderUtil.PROCESS_UUID, processDefinitionUUID.getValue());
            }
        }
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, locale);
        final String welcomePageTemplateLocation = getApplicationConfigDefinition(context).getWelcomePage();
        if (welcomePageTemplateLocation != null) {
            return getLayout(welcomePageTemplateLocation, "welcome", applicationDeploymentDate, context);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getProductVersion() throws FormServiceProviderNotFoundException {
        return getApplicationConfigDefinition(new HashMap<String, Object>()).getProductVersion();
    }

    /**
     * {@inheritDoc}
     */
    public String getHomePage() throws FormServiceProviderNotFoundException {
        return getApplicationConfigDefinition(new HashMap<String, Object>()).getHomePage();
    }

    /**
     * {@inheritDoc}
     */
    public String getExternalWelcomePage() throws FormServiceProviderNotFoundException {
        return getApplicationConfigDefinition(new HashMap<String, Object>()).getExternalWelcomePage();
    }

    /**
     * {@inheritDoc}
     */
    public String getMigrationProductVersion(final String formID, final Map<String, Object> context) throws InvalidFormDefinitionException,
            FormServiceProviderNotFoundException {
        String migrationProductVersion = FormCacheUtilFactory.getTenancyFormCacheUtil().getMigrationProductVersion(formID, locale, applicationDeploymentDate);
        if (migrationProductVersion == null) {
            migrationProductVersion = getApplicationConfigDefinition(context).getMigrationProductVersion();
            if (formID != null) {
                FormCacheUtilFactory.getTenancyFormCacheUtil().storeMigrationProductVersion(formID, locale, applicationDeploymentDate, migrationProductVersion);
            }
        }
        return migrationProductVersion;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationPermissions(final String formID, final Map<String, Object> context) throws InvalidFormDefinitionException,
            FormServiceProviderNotFoundException {
        String applicationPermissions = FormCacheUtilFactory.getTenancyFormCacheUtil().getApplicationPermissions(formID, locale, applicationDeploymentDate);
        if (applicationPermissions == null) {
            applicationPermissions = getApplicationConfigDefinition(context).getApplicationPermissions();
            if (formID != null) {
                FormCacheUtilFactory.getTenancyFormCacheUtil().storeApplicationPermissions(formID, locale, applicationDeploymentDate, applicationPermissions);
            }
        }
        return applicationPermissions;

    }

    /**
     * {@inheritDoc}
     */
    public String getFormPermissions(final String formID, final Map<String, Object> context) throws ApplicationFormDefinitionNotFoundException,
            InvalidFormDefinitionException, FormServiceProviderNotFoundException {
        String formPermissions = FormCacheUtilFactory.getTenancyFormCacheUtil().getFormPermissions(formID, locale, applicationDeploymentDate);
        if (formPermissions == null) {
            formPermissions = getApplicationDefinition(formID, context).getFormPermissions();
            if (formID != null) {
                FormCacheUtilFactory.getTenancyFormCacheUtil().storeFormPermissions(formID, locale, applicationDeploymentDate, formPermissions);
            }
        }
        return formPermissions;
    }

    /**
     * {@inheritDoc}
     */
    public String getNextForm(final String formID, final Map<String, Object> context) throws ApplicationFormDefinitionNotFoundException,
            InvalidFormDefinitionException, FormServiceProviderNotFoundException {
        String nextForm = FormCacheUtilFactory.getTenancyFormCacheUtil().getNextForm(formID, locale, applicationDeploymentDate);
        if (nextForm == null) {
            nextForm = getApplicationDefinition(formID, context).getNextForm();
            if (formID != null) {
                FormCacheUtilFactory.getTenancyFormCacheUtil().storeNextForm(formID, locale, applicationDeploymentDate, nextForm);
            }
        }
        return nextForm;
    }

    /**
     * {@inheritDoc}
     */
    public String getFormFirstPage(final String formID, final Map<String, Object> context) throws InvalidFormDefinitionException, FormNotFoundException,
            ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {
        String firstPage = FormCacheUtilFactory.getTenancyFormCacheUtil().getFirstPage(formID, locale, applicationDeploymentDate);
        if (firstPage == null) {
            final IApplicationFormDefAccessor getApplicationDefinition = getApplicationDefinition(formID, context);
            firstPage = getApplicationDefinition.getFirstPageExpression();
            if (formID != null) {
                FormCacheUtilFactory.getTenancyFormCacheUtil().storeFirstPage(formID, locale, applicationDeploymentDate, firstPage);
            }
        }
        return firstPage;
    }

    /**
     * {@inheritDoc}
     */
    public FormPage getFormPage(final String formID, final String pageId, final Map<String, Object> context) throws InvalidFormDefinitionException,
            FormNotFoundException, FileNotFoundException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {
        FormPage formPage = FormCacheUtilFactory.getTenancyFormCacheUtil().getPage(formID, locale, applicationDeploymentDate, pageId);

        if (formPage == null) {
            IApplicationFormDefAccessor applicationFormDefinition = getApplicationDefinition(formID, context);
            try {
                final FormType formType = applicationFormDefinition.getFormType();
                final HtmlTemplate pageTemplate = getFormPageLayout(applicationFormDefinition, formType, pageId, context);
                final List<FormWidget> pageWidgets = applicationFormDefinition.getPageWidgets(pageId);
                final List<FormValidator> pageValidators = applicationFormDefinition.getPageValidators(pageId);
                final String pageLabel = applicationFormDefinition.getPageLabel(pageId);
                final boolean allowHTMLInLabel = applicationFormDefinition.isHTMLAllowedInLabel(pageId);
                formPage = new FormPage(pageId, pageLabel, pageTemplate, pageWidgets, pageValidators, formType, allowHTMLInLabel);
                formPage.setNextPageExpression(applicationFormDefinition.getNextPageExpression(pageId));
                if (formID != null) {
                    FormCacheUtilFactory.getTenancyFormCacheUtil().storePage(formID, locale, applicationDeploymentDate, formPage);
                }
            } catch (final InvalidFormDefinitionException e) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Failed to parse the forms definition file.");
                }
                formPage = null;
            }
        }

        if (formPage != null) {
            // store bodycontent in the cach using formId PageId
            String bodyContentId = null;
            if (formID != null) {
                bodyContentId = FormCacheUtilFactory.getTenancyFormCacheUtil().storePageLayoutContent(formID, pageId, locale, applicationDeploymentDate,
                        formPage.getPageTemplate().getBodyContent());
            }
            formPage.getPageTemplate().setBodyContentId(bodyContentId);
            formPage.getPageTemplate().setBodyContent(null);
        }

        return formPage;
    }

    /**
     * Retrieve the template associated with a instantiation form page
     * 
     * @param applicationDefAccessor
     *            the application form definition accessor
     * @param formType
     *            the form type
     * @param pageId
     *            the page ID
     * @param context
     *            the Map of context
     * @return an {@link HtmlTemplate} object containing the elements required to
     *         build the level2 (page template)
     * @throws InvalidFormDefinitionException
     * @throws FileNotFoundException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    protected HtmlTemplate getFormPageLayout(final IApplicationFormDefAccessor applicationDefAccessor, final FormType formType, final String pageId,
            final Map<String, Object> context) throws InvalidFormDefinitionException, FileNotFoundException, ApplicationFormDefinitionNotFoundException,
            FormServiceProviderNotFoundException {

        final String templatePath = applicationDefAccessor.getFormPageLayout(pageId);
        final String defaultTemplatePath = DefaultFormsPropertiesFactory.getDefaultFormProperties().getGlobalPageTemplate();
        HtmlTemplate htmlTemplate = null;
        try {
            htmlTemplate = getPageLayout(templatePath, applicationDeploymentDate, context);
        } catch (final FileNotFoundException e) {
            final String message = "Using the default page template.";
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, message);
            }
            boolean isActivity = false;
            @SuppressWarnings("unchecked")
            Map<String, Object> urlContext = (Map<String, Object>) context.get(FormServiceProviderUtil.URL_CONTEXT);
            if (urlContext.get(FormServiceProviderUtil.TASK_UUID) != null) {
                isActivity = true;
            }
            htmlTemplate = buildPageLayout(context, defaultTemplatePath, applicationDefAccessor.getPageWidgets(pageId),
                    applicationDefAccessor.getPageValidators(pageId), formType, isActivity);
        }
        return htmlTemplate;
    }

    /**
     * {@inheritDoc}
     */
    public ApplicationConfig getApplicationConfig(final Map<String, Object> context, final String formID, final boolean includeApplicationTemplate)
            throws InvalidFormDefinitionException, FormServiceProviderNotFoundException {
        ApplicationConfig applicationConfig = FormCacheUtilFactory.getTenancyFormCacheUtil().getApplicationConfig(formID, locale, applicationDeploymentDate,
                includeApplicationTemplate);
        if (applicationConfig == null) {
            final IApplicationConfigDefAccessor applicationConfigFormDefinition = getApplicationConfigDefinition(context);
            HtmlTemplate applicationTemplate = null;
            String applicationLabel = null;
            if (includeApplicationTemplate) {
                try {
                    applicationTemplate = getApplicationLayout(context);

                    // store bodycontent in the cach using formId PageId
                    String bodyContentId = null;
                    if (formID != null) {
                        bodyContentId = FormCacheUtilFactory.getTenancyFormCacheUtil().storeApplicationLayoutContent(formID, locale, applicationDeploymentDate,
                                applicationTemplate.getBodyContent());
                    }
                    applicationTemplate.setBodyContentId(bodyContentId);
                    applicationTemplate.setBodyContent(null);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (InvalidFormTemplateException e) {
                    e.printStackTrace();
                } catch (ApplicationFormDefinitionNotFoundException e) {
                    e.printStackTrace();
                }
                if (formID != null) {
                    applicationLabel = applicationConfigFormDefinition.getApplicationLabel();
                }
            }
            final String mandatorySymbol = applicationConfigFormDefinition.getApplicationMandatorySymbol();
            final String mandatoryLabel = applicationConfigFormDefinition.getApplicationMandatoryLabel();
            final String mandatorySymbolClasses = applicationConfigFormDefinition.getApplicationMandatorySymbolStyle();
            final String userXPURL = DefaultFormsPropertiesFactory.getDefaultFormProperties().getUserXPURL();
            applicationConfig = new ApplicationConfig(applicationLabel, applicationTemplate, mandatorySymbol, mandatoryLabel, mandatorySymbolClasses, userXPURL);
            if (formID != null) {
                FormCacheUtilFactory.getTenancyFormCacheUtil().storeApplicationConfig(formID, locale, applicationDeploymentDate, includeApplicationTemplate,
                        applicationConfig);
            }
        }
        return applicationConfig;
    }

    /**
     * {@inheritDoc}
     */
    public List<TransientData> getFormTransientData(String formID, final Map<String, Object> context) throws InvalidFormDefinitionException,
            FormNotFoundException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {
        List<TransientData> transientData = FormCacheUtilFactory.getTenancyFormCacheUtil().getTransientData(formID, locale, applicationDeploymentDate);
        if (transientData == null) {
            transientData = getApplicationDefinition(formID, context).getTransientData();
            if (formID != null) {
                FormCacheUtilFactory.getTenancyFormCacheUtil().storeTransientData(formID, locale, applicationDeploymentDate, transientData);
            }
        }
        return transientData;
    }

    /**
     * {@inheritDoc}
     */
    public List<FormAction> getFormActions(String formID, List<String> pageIds, final Map<String, Object> context) throws InvalidFormDefinitionException,
            FormNotFoundException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {
        final List<FormAction> formActions = new ArrayList<FormAction>();
        final IApplicationFormDefAccessor applicationFormDefinition = getApplicationDefinition(formID, context);
        for (final String pageId : pageIds) {
            List<FormAction> pageActions = FormCacheUtilFactory.getTenancyFormCacheUtil().getPageActions(formID, locale, applicationDeploymentDate, formID,
                    pageId);
            if (pageActions == null) {
                pageActions = applicationFormDefinition.getActions(pageId);
                if (formID != null) {
                    FormCacheUtilFactory.getTenancyFormCacheUtil().storePageActions(formID, locale, applicationDeploymentDate, formID, pageId, pageActions);
                }
            }
            formActions.addAll(pageActions);
        }
        return formActions;
    }

    /**
     * {@inheritDoc}
     */
    public HtmlTemplate getFormConfirmationLayout(final String formID, final Map<String, Object> context) throws InvalidFormDefinitionException,
            FormNotFoundException, FileNotFoundException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {
        final IApplicationFormDefAccessor applicationFormDefinition = getApplicationDefinition(formID, context);
        final String applicationConfirmationTemplatePath = applicationFormDefinition.getConfirmationLayout();
        final String applicationConfirmationMessage = applicationFormDefinition.getConfirmationMessage();
        final HtmlTemplate applicationConfirmationTemplate = getPageLayout(applicationConfirmationTemplatePath, applicationDeploymentDate, context);
        applicationConfirmationTemplate.setDynamicMessage(applicationConfirmationMessage);
        return applicationConfirmationTemplate;
    }

    /**
     * Retrieve the application template data
     * 
     * @return a {@link HtmlTemplate} object containing the elements required to
     *         build the level1 (application template)
     * @throws InvalidFormDefinitionException
     * @throws InvalidFormTemplateException
     * @throws FileNotFoundException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     * @throws Exception
     */
    protected HtmlTemplate getApplicationLayout(final Map<String, Object> context) throws FileNotFoundException, InvalidFormTemplateException,
            InvalidFormDefinitionException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {

        final IApplicationConfigDefAccessor applicationConfigFormDefinition = getApplicationConfigDefinition(context);
        final String applicationTemplateLocation = applicationConfigFormDefinition.getApplicationLayout();
        return getLayout(applicationTemplateLocation, "application", applicationDeploymentDate, context);
    }

    /**
     * {@inheritDoc}
     */
    public String applicationAttributes(final String bodyContent, final Map<String, String> stepAttributes, final Locale locale) {
        ResourceBundle labels = ResourceBundle.getBundle("locale.i18n.ActivityAttributeLabels", locale);
        if (locale.getLanguage() != null && labels.getLocale() != null && !locale.getLanguage().equals(labels.getLocale().getLanguage())) {
            labels = ResourceBundle.getBundle("locale.i18n.ActivityAttributeLabels", Locale.ENGLISH);
        }
        final StringBuilder regex = new StringBuilder();
        final HashMap<String, String> encodedStrings = new HashMap<String, String>();
        regex.append("\\$(");
        for (String stepAttribute : ActivityAttribute.attributeValues()) {
            stepAttribute = encodeString(stepAttribute, encodedStrings);
            if (regex.length() > 3) {
                regex.append("|");
            }
            regex.append(stepAttribute);
            regex.append("|");
            regex.append("label.");
            regex.append(stepAttribute);
        }
        regex.append(")");
        final Pattern pattern = Pattern.compile(regex.toString());
        final Matcher matcher = pattern.matcher(bodyContent);
        final StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            final String itemFound = matcher.group(1);
            if (itemFound.startsWith("label.")) {
                String label = null;
                try {
                    label = labels.getString(itemFound);
                } catch (final Exception e) {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, "No step attribute label " + itemFound + " was found.");
                    }
                    label = "";
                }
                matcher.appendReplacement(stringBuffer, label);
            } else {
                final String stepAttribute = ActivityAttribute.valueOfAttibute(itemFound).name();
                if (stepAttribute != null) {
                    final String attributeValue = encodeString(stepAttributes.get(stepAttribute), encodedStrings);
                    if (attributeValue != null) {
                        matcher.appendReplacement(stringBuffer, attributeValue);
                    } else {
                        matcher.appendReplacement(stringBuffer, "");
                    }
                }
            }
        }
        matcher.appendTail(stringBuffer);
        return decodeString(stringBuffer.toString(), encodedStrings);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getTransientDataContext(List<TransientData> transientData, Locale userLocale, Map<String, Object> context)
            throws FormNotFoundException, FormServiceProviderNotFoundException, ClassNotFoundException {
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        final Map<String, Object> transientDataContext = new HashMap<String, Object>();
        final Map<String, String> expressionsToEvaluate = new HashMap<String, String>();
        for (final TransientData data : transientData) {
            final String valueStr = data.getValue();
            if (valueStr != null) {
                if (Misc.containsAGroovyExpression(valueStr)) {
                    expressionsToEvaluate.put(data.getName(), valueStr);
                }
            }
        }
        final Map<String, Object> resolvedExpressions = formServiceProvider.resolveExpressions(expressionsToEvaluate, context);
        for (final TransientData data : transientData) {
            final String name = data.getName();
            final String className = data.getClassname();
            final String valueStr = data.getValue();
            Object evaluatedValue = null;
            if (valueStr != null) {
                try {
                    evaluatedValue = resolvedExpressions.get(name);
                    if (evaluatedValue != null) {
                        final ClassLoader classloader = evaluatedValue.getClass().getClassLoader();
                        try {
                            Class<?> dataClass;
                            if (classloader != null) {
                                try {
                                    dataClass = Class.forName(className, true, classloader);
                                } catch (ClassNotFoundException e) {
                                    dataClass = Class.forName(className);
                                }
                            } else {
                                dataClass = Class.forName(className);
                            }
                            evaluatedValue.getClass().asSubclass(dataClass);
                        } catch (final ClassCastException e) {
                            throw new IllegalArgumentException();
                        }
                    } else {
                        if (className.equals(Boolean.class.getName())) {
                            evaluatedValue = Boolean.valueOf(valueStr);
                        } else if (className.equals(Long.class.getName())) {
                            evaluatedValue = Long.valueOf(valueStr);
                        } else if (className.equals(Double.class.getName())) {
                            evaluatedValue = Double.valueOf(valueStr);
                        } else if (className.equals(Date.class.getName())) {
                            final long dateAsLong = Long.parseLong(valueStr);
                            evaluatedValue = new Date(dateAsLong);
                        } else {
                            evaluatedValue = valueStr;
                        }
                    }
                } catch (final IllegalArgumentException e) {
                    final String errorMessage = "The value of transient data " + name + " doesn't match with the declared classname " + className;
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, errorMessage);
                    }
                    throw new IllegalArgumentException(errorMessage);
                }
            }
            transientDataContext.put(name, evaluatedValue);
        }
        return transientDataContext;
    }

    /**
     * {@inheritDoc}
     */
    public HtmlTemplate getApplicationErrorLayout(final Map<String, Object> context) throws InvalidFormDefinitionException, FileNotFoundException,
            ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {
        final IApplicationConfigDefAccessor applicationConfigFormDefinition = getApplicationConfigDefinition(context);
        final String applicationErrorTemplatePath = applicationConfigFormDefinition.getApplicationErrorTemplate();
        final HtmlTemplate applicationErrorTemplate = getPageLayout(applicationErrorTemplatePath, applicationDeploymentDate, context);
        return applicationErrorTemplate;
    }

    /**
     * get a page layout
     * 
     * @param layoutPath
     *            The path of the layout
     * @param applicationDeploymentDate
     *            The date of the application deployed
     * @param context
     *            Map containing the URL parameters
     * @return HtmlTemplate
     * @throws FileNotFoundException
     * @throws InvalidFormDefinitionException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    public HtmlTemplate getPageLayout(final String layoutPath, final Date applicationDeploymentDate, final Map<String, Object> context)
            throws FileNotFoundException, InvalidFormDefinitionException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {
        try {
            if (layoutPath == null) {
                throw new IOException();
            }
            InputStream htmlFileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(layoutPath);
            if (htmlFileStream == null) {
                final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
                final File dir = formServiceProvider.getApplicationResourceDir(applicationDeploymentDate, context);
                final File htmlFile = new File(dir, layoutPath);
                if (htmlFile.exists()) {
                    htmlFileStream = new FileInputStream(htmlFile);
                } else {
                    throw new IOException();
                }
            }

            final Source source = new Source(htmlFileStream);
            
            final Map<String, String> bodyAttributes = new HashMap<String, String>();
            
            final Element headElement = source.getFirstElement("head");
            final List<HeadNode> headNodes = new ArrayList<HeadNode>();
            if (headElement != null) {
                for (final Element headChildElement : headElement.getChildElements()) {
                    final Map<String, String> nodeAttributes = new HashMap<String, String>();
                    final Attributes attributes = headChildElement.getAttributes();
                    if (attributes != null) {
                        for (final Attribute attribute : attributes) {
                            nodeAttributes.put(attribute.getKey().toLowerCase(), attribute.getValue());
                        }
                    }
                    // fix for IE7 and IE8 failing to evaluate scripts in inserted HTML pages headers
                    if (SCRIPT_ELEMENT_NAME.equalsIgnoreCase(headChildElement.getName())) {
                        bodyAttributes.put(IE_ONLOAD, headChildElement.getContent().toString());
                    }
                    headNodes.add(new HeadNode(headChildElement.getName(), nodeAttributes, headChildElement.getContent().toString()));
                }
            }

            String body = null;
            final Element bodyElement = source.getFirstElement("body");
            if (bodyElement != null) {
                final Attributes attributes = bodyElement.getAttributes();
                if (attributes != null) {
                    for (final Attribute attribute : attributes) {
                        bodyAttributes.put(attribute.getKey(), attribute.getValue());
                    }
                }
                body = bodyElement.getContent().toString();
            } else {
                body = source.toString();
            }

            return new HtmlTemplate(body, bodyAttributes, headNodes);

        } catch (final IOException e) {
            String message = null;
            if (layoutPath == null) {
                message = "No page template defined.";
            } else {
                message = "Page template file " + layoutPath + " could not be found.";
            }
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, message);
            }
            throw new FileNotFoundException(message);
        }
    }

    /**
     * get a layout
     * 
     * @param layoutLocation
     *            The location of the layout
     * @param layoutTypeName
     *            The type of the layout
     * @param applicationDeploymentDate
     *            The date of the application deployed
     * @param context
     *            Map containing the URL parameters
     * @return HtmlTemplate
     * @throws FileNotFoundException
     * @throws InvalidFormTemplateException
     * @throws InvalidFormDefinitionException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    public HtmlTemplate getLayout(final String layoutpath, final String layoutTypeName, final Date applicationDeploymentDate, final Map<String, Object> context)
            throws FileNotFoundException, InvalidFormTemplateException, InvalidFormDefinitionException, ApplicationFormDefinitionNotFoundException,
            FormServiceProviderNotFoundException {

        InputStream htmlFileStream = null;
        try {
            if (layoutpath == null) {
                throw new IOException();
            }
            htmlFileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(layoutpath);
            if (htmlFileStream == null) {
                final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
                final File dir = formServiceProvider.getApplicationResourceDir(applicationDeploymentDate, context);
                final File htmlFile = new File(dir, layoutpath);
                if (htmlFile.exists()) {
                    htmlFileStream = new FileInputStream(htmlFile);
                } else {
                    throw new IOException();
                }
            }

            final Source source = new Source(htmlFileStream);
            
            final Map<String, String> bodyAttributes = new HashMap<String, String>();
            
            final List<HeadNode> headNodes = new ArrayList<HeadNode>();
            final Element headElement = source.getFirstElement("head");
            if (headElement != null) {
                for (final Element headChildElement : headElement.getChildElements()) {
                    final Map<String, String> nodeAttributes = new HashMap<String, String>();
                    final Attributes attributes = headChildElement.getAttributes();
                    if (attributes != null) {
                        for (final Attribute attribute : attributes) {
                            nodeAttributes.put(attribute.getKey(), attribute.getValue());
                        }
                    }
                    // fix for IE7 and IE8 failing to evaluate scripts in inserted HTML pages headers
                    if (SCRIPT_ELEMENT_NAME.equalsIgnoreCase(headChildElement.getName())) {
                        bodyAttributes.put(IE_ONLOAD, headChildElement.getContent().toString());
                    }
                    headNodes.add(new HeadNode(headChildElement.getName(), nodeAttributes, headChildElement.getContent().toString()));
                }
            } else {
                final String errorMessage = "Non standard HTML for the " + layoutTypeName + " template : missing body element.";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, errorMessage);
                }
                throw new InvalidFormTemplateException(errorMessage);
            }

            String body = null;
            final Element bodyElement = source.getFirstElement("body");
            if (bodyElement != null) {
                final Attributes attributes = bodyElement.getAttributes();
                if (attributes != null) {
                    for (final Attribute attribute : attributes) {
                        bodyAttributes.put(attribute.getKey(), attribute.getValue());
                    }
                }
                body = bodyElement.getContent().toString();
            } else {
                final String errorMessage = "Non standard HTML for the " + layoutTypeName + " template : missing head element.";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, errorMessage);
                }
                throw new InvalidFormTemplateException(errorMessage);
            }
            return new HtmlTemplate(body, bodyAttributes, headNodes);
        } catch (final IOException e) {
            final String message = layoutTypeName + "  template file " + layoutpath + " could not be found";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new FileNotFoundException(message);
        } finally {
            if (htmlFileStream != null) {
                try {
                    htmlFileStream.close();
                } catch (final IOException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "unable to close input stream for template " + layoutpath, e);
                    }
                }
            }
        }
    }

    /**
     * Generates the HTML template when no page template is defined
     * 
     * @param defaultTemplatePath
     * @param pageWidgets
     * @param pageValidators
     * @param isActivity
     * @return HtmlTemplate
     * @throws FileNotFoundException
     * @throws InvalidFormDefinitionException
     * @throws ApplicationFormDefinitionNotFoundException
     * @throws FormServiceProviderNotFoundException
     */
    protected HtmlTemplate buildPageLayout(final Map<String, Object> context, final String defaultTemplatePath, final List<FormWidget> pageWidgets,
            final List<FormValidator> pageValidators, final FormType formType, final boolean isActivity) throws FileNotFoundException,
            InvalidFormDefinitionException, ApplicationFormDefinitionNotFoundException, FormServiceProviderNotFoundException {

        HtmlTemplate pageTemplate = null;

        final HtmlTemplate pageBaseTemplate = getPageLayout(defaultTemplatePath, applicationDeploymentDate, context);
        final String baseBody = pageBaseTemplate.getBodyContent();
        final Source source = new Source(baseBody);

        final Element formContainerElement = source.getFirstElementByClass("bonita_form_container");
        final int insertionIndex = formContainerElement.getStartTag().getEnd();

        final OutputDocument outputDocument = new OutputDocument(source);
        final StringBuilder formContent = new StringBuilder();
        formContent.append("\n<div class=\"bonita_form_page_validation_container\">");
        List<FormValidator> bottomPageValidators = null;
        for (final FormValidator pageValidator : pageValidators) {
            if (!ValidatorPosition.BOTTOM.equals(pageValidator.getPosition())) {
                formContent.append("<div id=\"" + pageValidator.getId() + "\"></div>\n");
            } else {
                bottomPageValidators = new ArrayList<FormValidator>();
                bottomPageValidators.add(pageValidator);
            }
        }
        formContent.append("\n</div>\n");
        boolean isButtonContainer = false;
        for (final FormWidget pageWidget : pageWidgets) {
            List<FormValidator> bottomFieldValidators = null;
            if (FormType.entry == formType && !pageWidget.getValidators().isEmpty()) {
                for (final FormValidator fieldValidator : pageWidget.getValidators()) {
                    if (!ValidatorPosition.BOTTOM.equals(fieldValidator.getPosition())) {
                        formContent.append("<div id=\"" + fieldValidator.getId() + "\"></div>\n");
                    } else {
                        bottomFieldValidators = new ArrayList<FormValidator>();
                        bottomFieldValidators.add(fieldValidator);
                    }
                }
            }
            if (pageWidget.getType().name().startsWith("BUTTON_")) {
                if (!isButtonContainer) {
                    formContent.append("<div class=\"bonita_form_button_container\">\n");
                }
                isButtonContainer = true;
            } else {
                if (isButtonContainer) {
                    formContent.append("</div>\n");
                }
                isButtonContainer = false;
            }
            if (FormType.entry == formType || pageWidget.getVariableBound() != null || pageWidget.getType().equals(WidgetType.BUTTON)
                    || pageWidget.getType().equals(WidgetType.BUTTON_NEXT) || pageWidget.getType().equals(WidgetType.BUTTON_PREVIOUS)) {
                formContent.append("<div id=\"" + pageWidget.getId() + "\"></div>\n");
            }
            if (FormType.entry != formType && pageWidget.getType().equals(WidgetType.BUTTON_SUBMIT)) {
                formContent.append("<hr class=\"bonita_clear_float\">");
            }
            if (FormType.entry == formType && bottomFieldValidators != null) {
                for (final FormValidator fieldValidator : bottomFieldValidators) {
                    formContent.append("<div id=\"" + fieldValidator.getId() + "\"></div>\n");
                }
            }
        }
        if (bottomPageValidators != null) {
            formContent.append("\n<div class=\"bonita_form_page_validation_container\">");
            for (final FormValidator pageValidator : bottomPageValidators) {
                formContent.append("<div id=\"" + pageValidator.getId() + "\"></div>\n");
            }
            formContent.append("\n</div>\n");
        }

        outputDocument.insert(insertionIndex, formContent.toString());

        final StringBuilder headerContent = new StringBuilder();
        headerContent.append("\n<div id=\"bonita_form_page_label\" class=\"bonita_form_page_label\"></div>");
        if (isActivity && FormType.entry == formType) {
            headerContent.append("\n<div id=\"bonita_form_step_header\">");
            headerContent.append("\n\t<div id=\"bonita-object-area-top\"></div>");
            headerContent.append("\n\t<div id=\"bonita-object-area-middle\">");
            headerContent.append("\n\t\t<div id=\"bonita-object-area-from\">$label.bonita_step_readyDate $bonita_step_readyDate</div>");
            headerContent.append("\n\t\t<div id=\"bonita-object-area-to\">$label.bonita_step_expectedEndDate $bonita_step_expectedEndDate</div>");
            headerContent
                    .append("\n\t\t<div id=\"bonita-object-area-priority\">$label.bonita_step_priority <span class=\"bonita-priority\">$bonita_step_priority</span></div>");
            headerContent.append("\n\t\t<div id=\"bonita-object-area-description\">$bonita_step_description</div>");
            headerContent.append("\n\t</div>");
            headerContent.append("\n\t<div id=\"bonita-object-area-bottom\"></div>");
            headerContent.append("\n</div>");
        }
        outputDocument.insert(0, headerContent.toString());

        pageTemplate = new HtmlTemplate(outputDocument.toString(), pageBaseTemplate.getBodyAttributes(), pageBaseTemplate.getHeadNodes());

        return pageTemplate;
    }

    protected ProcessDefinitionUUID getProcessDefinitionUUIDFromXML(Map<String, Object> context) throws InvalidFormDefinitionException,
            FormServiceProviderNotFoundException {
        final IApplicationConfigDefAccessor applicationConfigDefinition = getApplicationConfigDefinition(context);
        if (applicationConfigDefinition instanceof XMLApplicationConfigDefAccessorImpl) {
            final String processName = applicationConfigDefinition.getApplicationName();
            final String processVersion = applicationConfigDefinition.getApplicationVersion();
            return new ProcessDefinitionUUID(processName, processVersion);
        } else {
            return null;
        }
    }

    private String encodeString(final String source, final HashMap<String, String> encodedStrings) {
        String to = null;
        try {
            if (source != null) {
                if (encodedStrings.containsKey(source)) {
                    to = encodedStrings.get(source);
                } else {
                    to = java.net.URLEncoder.encode(source, "UTF-8").replaceAll("\\+", " ");
                    encodedStrings.put(source, to);
                }
            }
        } catch (final UnsupportedEncodingException e) {
            to = source;
        }
        return to;
    }

    private String decodeString(String source, final HashMap<String, String> encodedStrings) {
        final Iterator<Entry<String, String>> it = encodedStrings.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            source = source.replaceAll(Pattern.quote(entry.getValue()), Matcher.quoteReplacement(entry.getKey()));
        }
        return source;
    }
}
