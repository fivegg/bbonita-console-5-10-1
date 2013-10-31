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
package org.bonitasoft.console.security.server.accessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.console.security.server.constants.WebBonitaConstants;
import org.bonitasoft.console.security.server.constants.WebBonitaConstantsFactory;

/**
 * @author Ruiheng.Fan
 * 
 */
public class TenancyProperties {

    /**
     * Default name of the preferences file
     */
    public static final String PROPERTIES_FILENAME = "bonita-web-preferences.properties";

    public static final String CASE_LIST_LAYOUT_KEY = "userXP.caseList.layout";

    public static final String CASE_LIST_STRETCHED_COLUMN_KEY = "userXP.caseList.stretchedColumn";
    
    public static final String PROCESSES_DISPLAYED = "userXP.processList.processesDisplayed";
    
    
    /**
     * Current theme property name
     */
    public static final String THEME_PROPERTY_NAME = "currentTheme";

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(TenancyProperties.class.getName());

    /**
     * Indicates that the preferences have been loaded
     */
    public static boolean preferencesLoaded = false;

    /**
     * The loaded properties
     */
    private Properties properties = new Properties();

    /**
     * The properties file
     */
    private File propertiesFile;

    private String domain = null;
    /**
     * Private contructor to prevent instantiation
     * 
     * @throws IOException
     */
    public TenancyProperties(String domain) {
        this.domain = domain;
        // Read properties file.
        try {
            propertiesFile = new File(getDomainCommonConfFolder(), PROPERTIES_FILENAME);
            InputStream inputStream = null;
            try {
                if (!propertiesFile.exists()) {
                    initProperties(propertiesFile);
                }
                inputStream = new FileInputStream(propertiesFile);
                properties.load(inputStream);
            } catch (final IOException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Bonita web preferences file " + propertiesFile.getPath() + " could not be loaded.", e);
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (final IOException e) {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, "Bonita web preferences file stream " + propertiesFile.getPath() + " could not be closed.", e);
                        }
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Bonita web preferences are not available!", e);
            }
            properties = null;
            propertiesFile = null;
        }
    }

    /**
     * Default constructor.
     */
    public TenancyProperties() {
        this(null);
    }

    private void initProperties(final File aPropertiesFile) throws IOException {
        // Create the file.
        aPropertiesFile.createNewFile();
        // Add default content.
        setProperty(CASE_LIST_STRETCHED_COLUMN_KEY, "10");
    }

    public String getProperty(final String propertyName) {
        if (properties == null) {
            return null;
        }
        return properties.getProperty(propertyName);
    }

    public String getProperty(final String propertyName, final String defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        return properties.getProperty(propertyName, defaultValue);
    }

    public void removeProperty(final String propertyName) throws IOException {
        if (properties != null) {
            properties.remove(propertyName);
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(propertiesFile);
                properties.store(outputStream, null);
            } catch (final IOException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Bonita web preferences file " + propertiesFile.getPath() + " could not be loaded.", e);
                }
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (final IOException e) {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, "Bonita web preferences file stream " + propertiesFile.getPath() + " could not be closed.", e);
                        }
                    }
                }
            }
        }
    }

    public void setProperty(final String propertyName, final String propertyValue) throws IOException {
        if (properties != null) {
            properties.setProperty(propertyName, propertyValue);
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(propertiesFile);
                properties.store(outputStream, null);
            } catch (final IOException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Bonita web preferences file " + propertiesFile.getPath() + " could not be loaded.", e);
                }
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (final IOException e) {
                        if (LOGGER.isLoggable(Level.SEVERE)) {
                            LOGGER.log(Level.SEVERE, "Bonita web preferences file stream " + propertiesFile.getPath() + " could not be closed.", e);
                        }
                    }
                }
            }
        }
    }

    /*
     * Utils
     */

    /**
     * Get the folder where to write Domain temporary files for User XP.
     * 
     * @throws ConsoleException
     */
    public File getDomainXPTempFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getXpTempSubFolderPath());
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    

    /**
     * Get the folder where to write Domain temporary files for User XP.
     * 
     * @throws ConsoleException
     */
    public File getDomainThemeXPTempFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getThemeXPTempSubFolderPath());
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to write Domain temporary files for forms application.
     * 
     * @throws ConsoleException
     */
    public File getDomainFormsTempFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getFormsTempSubFolderPath());
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to write Domain temporary files commons to all web applications.
     * 
     * @throws ConsoleException
     */
    public File getDomainCommonTempFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getCommonTempSubFolderPath());
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to get the Domain conf files.
     * 
     * @throws ConsoleException
     */
    public File getDomainCommonConfFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getCommonConfSubFolderPath());
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to get the Domain forms conf files.
     * 
     * @throws ConsoleException
     */
    public File getDomainFormConfFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getFormsConfSubFolderPath());
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to write files.
     * 
     * @throws ConsoleException
     */
    public File getDomainXPConfFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getXpConfSubFolderPath());
        if (!theFolder.exists()) {
            theFolder.mkdirs();
        }
        return theFolder;
    }
    
    /**
     * Get the folder of Domain themes files (ie CSS files)
     * 
     * @throws ConsoleException
     */
    public File getDomainXPThemeFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getThemeXPConfSubFolderPath());
        if (!theFolder.exists()) {
            theFolder.mkdirs();
        }
        return theFolder;
    }   
    
    /**
     * Get the Domain folder where to write XP files.
     * 
     * @throws ConsoleException
     */
    public File getDomainXPWorkFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getXPWorkSubFolderPath());
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the Domain folder where to write Work files.
     * 
     * @throws ConsoleException
     */
    public File getDomainFormsWorkFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getFormsWorkSubFolderPath());
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to write Common files.
     * 
     * @throws ConsoleException
     */
    public File getDomainCommonWorkFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstantsFactory.getWebBonitaConstants(domain).getCommonWorkSubFolderPath());
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
}
