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

/**
 * @author Ruiheng.Fan
 * 
 */
public class PlatformProperties {

    /**
     * Default name of the preferences file
     */
    public static final String PROPERTIES_FILENAME = "bonita-web-preferences.properties";

    public static final String CASE_LIST_LAYOUT_KEY = "userXP.caseList.layout";

    public static final String CASE_LIST_STRETCHED_COLUMN_KEY = "userXP.caseList.stretchedColumn";

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(PlatformProperties.class.getName());

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
    
    static PlatformProperties instance = new PlatformProperties();

    protected static PlatformProperties getInstance(){
        return instance;
    }
    /**
     * Private contructor to prevent instantiation
     * 
     * @throws IOException
     */
    private PlatformProperties() {
        // Read properties file.
        try {
            propertiesFile = new File(getCommonConfFolder(), PROPERTIES_FILENAME);
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


    private void initProperties(final File aPropertiesFile) throws IOException {
        // Create the file.
        aPropertiesFile.createNewFile();
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
     * Get the folder where to write temporary files for User XP.
     * 
     * @throws ConsoleException
     */
    public File getXPTempFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstants.platformXpTempSubFolderPath);
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to write temporary files for User XP.
     * 
     * @throws ConsoleException
     */
    public File getThemeXPTempFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstants.platformThemeXPTempSubFolderPath);
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to write temporary files for forms application.
     * 
     * @throws ConsoleException
     */
    public File getFormsTempFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstants.platformFormsTempSubFolderPath);
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to write temporary files commons to all web applications.
     * 
     * @throws ConsoleException
     */
    public File getCommonTempFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstants.platformCommonTempSubFolderPath);
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to get the conf files.
     * 
     * @throws ConsoleException
     */
    public File getCommonConfFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstants.platformCommonConfSubFolderPath);
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to get the forms conf files.
     * 
     * @throws ConsoleException
     */
    public File getFormConfFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstants.platformFormsConfSubFolderPath);
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
    public File getXPConfFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theFolder = new File(theBonitaHome, WebBonitaConstants.platformXpConfSubFolderPath);
        if (!theFolder.exists()) {
            theFolder.mkdirs();
        }
        return theFolder;
    }

    /**
     * Get the folder of themes files (ie CSS files)
     * 
     * @throws ConsoleException
     */
    public File getXPThemeFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theFolder = new File(theBonitaHome, WebBonitaConstants.platformThemeXPConfSubFolderPath);
        if (!theFolder.exists()) {
            theFolder.mkdirs();
        }
        return theFolder;
    }  
    
    /**
     * Get the folder where to write XP files.
     * 
     * @throws ConsoleException
     */
    public File getXPWorkFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstants.platformXPWorkSubFolderPath);
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
    /**
     * Get the folder where to write Work files.
     * 
     * @throws ConsoleException
     */
    public File getFormsWorkFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstants.platformFormsWorkSubFolderPath);
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
    public File getCommonWorkFolder() throws IOException {
        final String theBonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (theBonitaHome == null) {
            throw new IOException("BONITA_HOME system property not set!");
        }
        final File theTempFolder = new File(theBonitaHome, WebBonitaConstants.platformCommonWorkSubFolderPath);
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        return theTempFolder;
    }
    
}
