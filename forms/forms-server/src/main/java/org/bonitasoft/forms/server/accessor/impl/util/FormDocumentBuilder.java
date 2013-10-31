/**
 * Copyright (C) 2010 BonitaSoft S.A.
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
package org.bonitasoft.forms.server.accessor.impl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bonitasoft.console.common.application.ApplicationResourcesUtils;
import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;

/**
 * The Form definition file document builder
 * 
 * @author Anthony Birembaut, Nicolas Chabanoles
 *
 */
public class FormDocumentBuilder {
    
    /**
     * The form definition file name
     */
    public final static String FORM_DEFINITION_DEFAULT_FILE_NAME = "forms.xml";
    
    /**
     * The form definition file name prefixe
     */
    public final static String FORM_DEFINITION_FILE_PREFIX = "forms_";
    
    /**
     * The form definition file name suffixe
     */
    public final static String FORM_DEFINITION_FILE_SUFFIX = ".xml";

    /**
     * The document for the process definition UUID
     */
    protected FormDocument document;
    
    /**
     * The process definition UUID
     */
    protected ProcessDefinitionUUID processDefinitionUUID;
    
    /**
     * The locale as a string
     */
    protected String locale;
    
    /**
     * Last access to the current instance
     */
    protected Long lastAccess = new Date().getTime();

    /**
     * the {@link Date} of the process deployment
     */
    protected Date processDeployementDate;
    
    /**
     * indicate if the form definition file should be retrieved from the business archive only
     */
    protected final boolean getFormDefinitionFromBAR;

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(FormDocumentBuilder.class.getName());
    
    /**
     * Instances map by process
     */
    private static Map<String, Map<String, FormDocumentBuilder>> INSTANCES;

    /**
     * Separator for the instance map keys
     */
    protected final static String INSTANCES_MAP_SEPERATOR = "@";
    
    /**
     * Retrieve an instance of FormDocumentBuilder or create a new one if necessary.
     * The map contains a cache of instances. Each instance has a validity duration equals to the INSTANCE_EXPIRATION_TIME constant value
     * The deployement date is also check because a process can be undeployed and redeployed (after modifications) with the same UUID
     * @param processDefinitionUUID the process definition UUID
     * @param locale the user's locale
     * @param processDeployementDate the deployement date of the process
     * @param domain the domain to use
     * @throws IOException
     * @throws ProcessNotFoundException
     * @throws InvalidFormDefinitionException 
     */
    public static synchronized FormDocumentBuilder getInstance(final ProcessDefinitionUUID processDefinitionUUID, final String locale, final Date processDeployementDate, final String domain) throws ProcessNotFoundException, IOException,
            InvalidFormDefinitionException {

        return getInstance(processDefinitionUUID, locale, processDeployementDate, domain, false);
    }
    
    /**
     * Retrieve an instance of FormDocumentBuilder or create a new one if necessary.
     * The map contains a cache of instances. Each instance has a validity duration equals to the INSTANCE_EXPIRATION_TIME constant value
     * The deployement date is also check because a process can be undeployed and redeployed (after modifications) with the same UUID
     * @param processDefinitionUUID the process definition UUID
     * @param locale the user's locale
     * @param processDeployementDate the deployement date of the process
     * @param domain the domain to use
     * @param getFormDefinitionFromBAR indicate if the form definition file should be retrieved from the business archive only (if false, it's sought in the classpath first)
     * @throws IOException
     * @throws ProcessNotFoundException
     * @throws InvalidFormDefinitionException 
     */
    public static synchronized FormDocumentBuilder getInstance(final ProcessDefinitionUUID processDefinitionUUID, final String locale, final Date processDeployementDate, final String domain, final boolean getFormDefinitionFromBAR) throws ProcessNotFoundException, IOException,
            InvalidFormDefinitionException {

        if (INSTANCES == null) {
            INSTANCES = new LinkedHashMap<String, Map<String, FormDocumentBuilder>>(DefaultFormsPropertiesFactory.getDefaultFormProperties().getMaxProcessesInCache(), .75F, true) {

                private static final long serialVersionUID = 7451370208143315146L;

                @Override
                protected boolean removeEldestEntry(final Map.Entry<String, Map<String, FormDocumentBuilder>> eldest) {
                    return size() > DefaultFormsPropertiesFactory.getDefaultFormProperties().getMaxProcessesInCache();
                };
            };
        }

        FormDocumentBuilder instance = null;
        if (processDefinitionUUID == null) {
            try {
                instance = new FormDocumentBuilder(null, locale, processDeployementDate, getFormDefinitionFromBAR);
            } catch (final FileNotFoundException e) {
                if (locale != null) {
                    instance = new FormDocumentBuilder(null, null, processDeployementDate, getFormDefinitionFromBAR);
                } else {
                    throw new FileNotFoundException("The forms definition file for process was not found.");
                }
            }
        } else {
            Map<String, FormDocumentBuilder> localeInstances = INSTANCES.get(processDefinitionUUID.getValue() + INSTANCES_MAP_SEPERATOR + domain);
            if (localeInstances != null) {
                instance = localeInstances.get(locale);
            }
            boolean outOfDateDefinition = false;
            if (instance != null && ((processDeployementDate != null && processDeployementDate.compareTo(instance.processDeployementDate) != 0) || instance.hasExpired())) {
                localeInstances.remove(locale);
                outOfDateDefinition = true;
            }
            if (instance == null || outOfDateDefinition) {
                if (localeInstances == null) {
                    localeInstances = new LinkedHashMap<String, FormDocumentBuilder>() {

                        private static final long serialVersionUID = -2092174987934309788L;

                        @Override
                        protected boolean removeEldestEntry(final java.util.Map.Entry<String, FormDocumentBuilder> eldest) {
                            return size() > DefaultFormsPropertiesFactory.getDefaultFormProperties().getMaxLanguagesInCache();
                        }
                    };
                }
                try {
                    instance = new FormDocumentBuilder(processDefinitionUUID, locale, processDeployementDate, getFormDefinitionFromBAR);
                    localeInstances.put(locale, instance);
                    INSTANCES.put(processDefinitionUUID.getValue() + INSTANCES_MAP_SEPERATOR + domain, localeInstances);
                } catch (final FileNotFoundException e) {
                    if (locale != null) {
                        instance = new FormDocumentBuilder(processDefinitionUUID, null, processDeployementDate, getFormDefinitionFromBAR);
                    } else {
                        throw new FileNotFoundException("The forms definition file for process " + processDefinitionUUID + "in domain " + domain + " was not found.");
                    }
                }
            } else {
                instance.lastAccess = new Date().getTime();
            }
        }
        return instance;
    }
    
    /**
     * Private constructor to prevent instantiation
     * @param processDefinitionUUID the process definition UUID
     * @param locale the user's locale
     * @param processDeployementDate the deployement date of the process
     * @param getFormDefinitionFromBAR indicate if the form definition file should be retrieved from the business archive only
     * @throws ProcessNotFoundException
     * @throws IOException if the forms definition file is not found
     * @throws InvalidFormDefinitionException if the form definition file cannot be parsed
     */
    protected FormDocumentBuilder(final ProcessDefinitionUUID processDefinitionUUID, final String locale, final Date processDeployementDate, final boolean getFormDefinitionFromBAR) throws ProcessNotFoundException, IOException, InvalidFormDefinitionException{
        if(LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Building instance of the Form document builder for process " + processDefinitionUUID + " with locale " + locale + " and deployement date " + processDeployementDate.getTime());
        }
        this.processDefinitionUUID = processDefinitionUUID;
        this.locale = locale;
        this.processDeployementDate = processDeployementDate;
        this.getFormDefinitionFromBAR = getFormDefinitionFromBAR;
        final InputStream formsDefinitionStream = getFormsDefinitionInputStream();
        try {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.document = new FormDocument(builder.parse(formsDefinitionStream));
        } catch (final Exception e) {
            final String errorMessage = "Failed to parse the forms definition file";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new InvalidFormDefinitionException(errorMessage);
        } finally {
            if (formsDefinitionStream != null) {
                formsDefinitionStream.close();
            }
        }
    }

    /**
     * @return the form definition as an input stream
     * @throws IOException
     * @throws ProcessNotFoundException
     */
    protected InputStream getFormsDefinitionInputStream() throws IOException, ProcessNotFoundException {

        InputStream formsDefinitionInputStream = null;
        URL formsParametersURL = null;
        String localizedFileName = null;
        if (locale != null) {
            localizedFileName = FORM_DEFINITION_FILE_PREFIX + locale + FORM_DEFINITION_FILE_SUFFIX;
            if (!getFormDefinitionFromBAR) {
                formsParametersURL = Thread.currentThread().getContextClassLoader().getResource(localizedFileName);
            }
        }
        if (!getFormDefinitionFromBAR && formsParametersURL == null) {
            formsParametersURL = Thread.currentThread().getContextClassLoader().getResource(FORM_DEFINITION_DEFAULT_FILE_NAME);
        }
        if (formsParametersURL == null) {
            if (processDefinitionUUID == null) {
                throw new FileNotFoundException("The forms definition file for the process was not found.");
            }
            // try to get the form file from the application resource directory where files from the bar are exctracted
            final File processApplicationsResourcesDir = ApplicationResourcesUtils.getApplicationResourceDir(processDefinitionUUID, processDeployementDate);
            if (!processApplicationsResourcesDir.exists()) {
                ApplicationResourcesUtils.retrieveApplicationFiles(processDefinitionUUID, processDeployementDate);
            }
            File formsFile = null;
            if (locale != null) {
                formsFile = new File(processApplicationsResourcesDir, localizedFileName);
            }
            if (formsFile == null || !formsFile.exists()) {
                formsFile = new File(processApplicationsResourcesDir, FORM_DEFINITION_DEFAULT_FILE_NAME);
            }
            if (formsFile.exists()) {
                formsDefinitionInputStream = new FileInputStream(formsFile);
            } else {
                throw new FileNotFoundException("The forms definition file for process " + processDefinitionUUID + " was not found.");
            }
        } else {
            formsDefinitionInputStream = formsParametersURL.openStream();
        }
        return formsDefinitionInputStream;
    }
    
    /**
     * @return the document
     */
    public FormDocument getDocument() {
        return document;
    }
    
    /**
     * Determinates whether the current instance has expired or not
     * @return true if the current instance has expired, false otherwise
     */
    protected boolean hasExpired() {
        final long now = new Date().getTime();
        return this.lastAccess + DefaultFormsPropertiesFactory.getDefaultFormProperties().getProcessesTimeToLiveInCache() < now;
    }
}
