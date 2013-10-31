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
package org.bonitasoft.console.security.server.accessor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.console.security.server.threadlocal.ThreadLocalManager;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
/**
 * Utility class for default properties access
 * @author Anthony Birembaut
 *
 */
public class SecurityProperties {
    
    /**
     * Default name of the form definition file
     */
    public static final String SECURITY_DEFAULT_CONFIG_FILE_NAME = "security-config.properties";
    
    /**
     * property for the key used to encrypt the credentials
     */
    public static final String CRYPTO_KEY_PROPERTY = "forms.application.crypto.key";
    
    /**
     * property for the credentials transmission mechanism activation
     */
    public static final String CREDENTIALS_TRANSMISSION_PROPERTY = "forms.application.credentials.transmission";
    
    /**
     * property for the auto login mechanism activation
     */
    public static final String AUTO_LOGIN_PROPERTY = "forms.application.login.auto";
    
    /**
     * property for the login to use for auto login
     */
    public static final String AUTO_LOGIN_USERNAME_PROPERTY = "forms.application.login.auto.username";
    
    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(SecurityProperties.class.getName());
    
    /**
     * Instances attribute
     */
    private static Map<String, SecurityProperties> INSTANCES = new HashMap<String, SecurityProperties>();
    
    /**
     * The process definition UUID
     */
    protected ProcessDefinitionUUID processDefinitionUUID;
    
    /**
     * default properties 
     */
    protected Properties defaultProperties = new Properties();
    
    protected final static String INSTANCES_MAP_SEPERATOR = "@";
    
    /**
     * @return the {@link SecurityProperties} instance
     */
    public static synchronized SecurityProperties getInstance() {
        final String instanceKey = generateInstanceKey(null);
        SecurityProperties securityProperties = INSTANCES.get(instanceKey);
        if (securityProperties == null) {
            securityProperties = new SecurityProperties(null);
            INSTANCES.put(instanceKey, securityProperties);
        }
        return securityProperties;
    }
    
    /**
     * @param processDefinitionUUID
     * @return the {@link SecurityProperties} instance
     */
    public static synchronized SecurityProperties getInstance(final ProcessDefinitionUUID processDefinitionUUID) {
        final String instanceKey = generateInstanceKey(processDefinitionUUID);
        SecurityProperties securityProperties = INSTANCES.get(instanceKey);
        if (securityProperties == null) {
            securityProperties = new SecurityProperties(processDefinitionUUID);
            INSTANCES.put(instanceKey, securityProperties);
        }
        return securityProperties;
    }

    /**
     * @param processDefinitionUUID
     */
    public static synchronized void cleanProcessConfig(final ProcessDefinitionUUID processDefinitionUUID) {
        String instanceKey = generateInstanceKey(processDefinitionUUID);
    	INSTANCES.remove(instanceKey);
    }

    /**
     * Generate SecurityProperties INSTNACES key from ProcessDefinitionUUID
     * @param processDefinitionUUID
     * @return
     */
    private static String generateInstanceKey(final ProcessDefinitionUUID processDefinitionUUID) {
        final String domain = ThreadLocalManager.getDomain();
        String instanceKey;
        if (processDefinitionUUID == null) {
            instanceKey = null + INSTANCES_MAP_SEPERATOR + domain;
        } else {
            instanceKey = processDefinitionUUID.getValue() + INSTANCES_MAP_SEPERATOR + domain;
        }
        return instanceKey;
    }
    
    /**
     * Private contructor to prevent instantiation
     */
    protected SecurityProperties(final ProcessDefinitionUUID processDefinitionUUID){
    	this.processDefinitionUUID = processDefinitionUUID;
    	InputStream inputStream = null;
    	try {
    		if (processDefinitionUUID != null) {
    			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SECURITY_DEFAULT_CONFIG_FILE_NAME);
		    	if (inputStream == null) {
		    		File securityPropertiesFile = getSecurityPropertiesFile(processDefinitionUUID);
		    		if (securityPropertiesFile == null) {
		    			securityPropertiesFile = new File(PropertiesFactory.getTenancyProperties().getDomainCommonConfFolder(), SECURITY_DEFAULT_CONFIG_FILE_NAME);
		    		}
		            inputStream = new FileInputStream(securityPropertiesFile);
		    	}
    		} else {
    			final File securityPropertiesFile = new File(PropertiesFactory.getTenancyProperties().getDomainCommonConfFolder(), SECURITY_DEFAULT_CONFIG_FILE_NAME);
    			inputStream = new FileInputStream(securityPropertiesFile);
    		}
            defaultProperties.load(inputStream);
        } catch (final IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
            	LOGGER.log(Level.SEVERE, "default security config file " + SECURITY_DEFAULT_CONFIG_FILE_NAME + " is missing form the forms conf directory");
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "default security config file " + SECURITY_DEFAULT_CONFIG_FILE_NAME + " stream could not be closed.", e);
                    }
                }
            }
        }
    }
    
    /**
     * Retrieve the config file in the extracted business archive 
     * @param processDefinitionUUID the process definition UUID
     * @return the config file or null if it doesn't exists
     * @throws IOException
     */
    protected File getSecurityPropertiesFile(final ProcessDefinitionUUID processDefinitionUUID) throws IOException {
    	File securityPropertiesFile = null;
        final File processDir = new File(PropertiesFactory.getTenancyProperties().getDomainFormsWorkFolder(), processDefinitionUUID.getValue());
        if (processDir.exists()) {
            final File[] directories = processDir.listFiles(new FileFilter() {
				public boolean accept(final File pathname) {
					return pathname.isDirectory();
				}
			});
            long lastDeployementDate = 0L;
            for (final File directory : directories) {
            	try {
            		final long deployementDate = Long.parseLong(directory.getName());
            		if (deployementDate > lastDeployementDate) {
            			lastDeployementDate = deployementDate;
            		}
            	} catch (final Exception e) {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, "Process application resources deployement folder contains a directory that does not match a process deployement timestamp: " + directory.getName(), e);
                    }
				}
			}
            if (lastDeployementDate == 0L) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "Process application resources deployement folder contains no directory that match a process deployement timestamp.");
                }
            } else {
                final File file = new File(processDir, lastDeployementDate + File.separator + SECURITY_DEFAULT_CONFIG_FILE_NAME);
                if (file.exists()) {
                	securityPropertiesFile = file;
                }
            }
        }
        return securityPropertiesFile;
    }

    /**
     * @return the crypto key
     */
    public String getLoginKey() {
        return defaultProperties.getProperty(CRYPTO_KEY_PROPERTY);
    }
    
    /**
     * @return the credential transmission property
     */
    public boolean useCredentialsTransmission() {
    	final String useCredentialsTransmission = defaultProperties.getProperty(CREDENTIALS_TRANSMISSION_PROPERTY);
        try {
            return Boolean.parseBoolean(useCredentialsTransmission);
        } catch (final Exception e) {
            return false;
        }
    }
    
    /**
     * @return the application form auto-login property
     */
    public boolean allowAutoLogin() {
    	final String useAutoLogin = defaultProperties.getProperty(AUTO_LOGIN_PROPERTY);
        try {
            return Boolean.parseBoolean(useAutoLogin);
        } catch (final Exception e) {
            return false;
        }
    }
    
    /**
     * @return the auto-login username property
     */
    public String getAutoLoginUserName() {
        return defaultProperties.getProperty(AUTO_LOGIN_USERNAME_PROPERTY);
    }
}


