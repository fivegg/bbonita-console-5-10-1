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
package org.bonitasoft.console.common.application;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.console.security.server.accessor.PropertiesFactory;
import org.bonitasoft.console.security.server.accessor.SecurityProperties;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Anthony Birembaut
 *
 */
public class ApplicationResourcesUtils {
    
    /**
     * The forms directory name in the bar
     */
    public final static String FORMS_DIRECTORY_IN_BAR = "forms";
    
    /**
     * The forms lib directory name in the bar
     */
    public final static String LIB_DIRECTORY_IN_BAR = "lib";
    
    /**
     * The forms validators directory name in the bar
     */
    public final static String VALIDATORS_DIRECTORY_IN_BAR = "validators";

    /**
     * A map used to store the classloaders that are used to load some libraries extracted from the business archive
     */
    protected final static Map<ProcessDefinitionUUID, ClassLoader> PROCESS_CLASSLOADERS = new HashMap<ProcessDefinitionUUID, ClassLoader>();
    
    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(ApplicationResourcesUtils.class.getName());
    
    /**
     * Retrieve the web resources from the business archive and store them in a local directory
     * @param processDefinitionUUID the process definition UUID
     * @param processDeployementDate the process deployement date
     * @throws IOException
     * @throws ProcessNotFoundException
     */
    public static synchronized void retrieveApplicationFiles(final ProcessDefinitionUUID processDefinitionUUID, final Date processDeployementDate) throws IOException, ProcessNotFoundException {

        final File formsDir = getApplicationResourceDir(processDefinitionUUID, processDeployementDate);
        if(!formsDir.exists()) {
            formsDir.mkdirs();
        }
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        final BusinessArchive businessArchive = queryDefinitionAPI.getBusinessArchive(processDefinitionUUID);
        final Map<String, byte[]> formsResources = businessArchive.getResources(FORMS_DIRECTORY_IN_BAR + "/.*");
        for (final Entry<String, byte[]> formResource : formsResources.entrySet()) {
            final String filePath = formResource.getKey().substring(FORMS_DIRECTORY_IN_BAR.length() + 1);
            final byte[] fileContent = formResource.getValue();
            final File formResourceFile = new File(formsDir.getPath() + File.separator + filePath);
            final File formResourceFileDir = formResourceFile.getParentFile();
            if(!formResourceFileDir.exists()) {
            	formResourceFileDir.mkdirs();
            }
            formResourceFile.createNewFile();
            if (fileContent != null) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(formResourceFile);
                    fos.write(fileContent);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch(final IOException e) {
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.log(Level.SEVERE, "unable to close file output stream for business archive resource " + formResourceFile.getPath(), e);
                            }
                        }
                    }
                }
            }
        }
        SecurityProperties.cleanProcessConfig(processDefinitionUUID);

        final File processApplicationsResourcesDir = ApplicationResourcesUtils.getApplicationResourceDir(processDefinitionUUID, processDeployementDate);
        final ClassLoader processClassLoader = createProcessClassloader(processDefinitionUUID, processApplicationsResourcesDir);
        PROCESS_CLASSLOADERS.put(processDefinitionUUID, processClassLoader);
    }
    
    /**
     * Create a classloader for the process
     * @param processDefinitionUUID the process definition UUID
     * @param processApplicationsResourcesDir the process application resources directory
     * @return a Classloader
     * @throws IOException
     */
    private static ClassLoader createProcessClassloader(final ProcessDefinitionUUID processDefinitionUUID, final File processApplicationsResourcesDir) throws IOException {
        ClassLoader processClassLoader = null;
        try {
            final URL[] librariesURLs = getLibrariesURLs(processApplicationsResourcesDir);
            if (librariesURLs.length > 0) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Creating the classloader for process " + processDefinitionUUID.getValue());
                }
                processClassLoader = new URLClassLoader(librariesURLs, Thread.currentThread().getContextClassLoader());
            }
        } catch (final IOException e) {
            final String message = "Unable to create the class loader for the application's libraries";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new IOException(message);
        }
        return processClassLoader;
    }
    
    /**
     * Get the URLs of the validators' jar and their dependencies
     * @param processApplicationsResourcesDir the process application resources directory
     * @return an array of URL
     * @throws IOException
     */
    private static URL[] getLibrariesURLs(final File processApplicationsResourcesDir) throws IOException {
    	final List<URL> urls = new ArrayList<URL>();
    	final File libDirectory = new File(processApplicationsResourcesDir, ApplicationResourcesUtils.LIB_DIRECTORY_IN_BAR + File.separator);
    	if (libDirectory.exists()) {
	        final File[] libFiles = libDirectory.listFiles();
	        for (int i = 0; i < libFiles.length; i++) {
	            urls.add(libFiles[i].toURL());
	        }
    	}
    	final File validatorsDirectory = new File(processApplicationsResourcesDir, ApplicationResourcesUtils.VALIDATORS_DIRECTORY_IN_BAR + File.separator);
        if (validatorsDirectory.exists()) {
	        final File[] validatorsFiles = validatorsDirectory.listFiles();
	        for (int i = 0; i < validatorsFiles.length; i++) {
	            urls.add(validatorsFiles[i].toURL());
	        }
        } else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "The validators directory doesn't exists.");
            }
        }
        final URL[] urlArray = new URL[urls.size()];
        urls.toArray(urlArray);
        return urlArray;
    }
    
    /**
     * Retrieve the class loader associated with the process or create it if there is no classloader associated with this process yet
     * @param processDefinitionUUID the process definition UUID
     * @return a {@link ClassLoader}, null if the process classloader doesn't exists and couldn't be created
     */
    public static synchronized ClassLoader getProcessClassLoader(final ProcessDefinitionUUID processDefinitionUUID) {

        ClassLoader processClassLoader = null;
        if (PROCESS_CLASSLOADERS.containsKey(processDefinitionUUID)) {
            processClassLoader = PROCESS_CLASSLOADERS.get(processDefinitionUUID);
        } else {
            try {
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
                    }
                    final File processApplicationsResourcesDir = new File(processDir, Long.toString(lastDeployementDate));
                    processClassLoader = createProcessClassloader(processDefinitionUUID, processApplicationsResourcesDir);
                }
            } catch (final IOException e) {
                final String message = "Unable to create the class loader for the application's libraries";
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, message, e);
                }
            }
            PROCESS_CLASSLOADERS.put(processDefinitionUUID, processClassLoader);
        }
        return processClassLoader;
    }
    
    /**
     * Delete the the web resources directory if it exists
     * @param processDefinitionUUID the process definition UUID
     * @param domain the UserProfile domain name
     */
    public static synchronized void removeApplicationFiles(final ProcessDefinitionUUID processDefinitionUUID) {
        
    	PROCESS_CLASSLOADERS.remove(processDefinitionUUID);
        try {
            final File formsDir = new File(PropertiesFactory.getTenancyProperties().getDomainFormsWorkFolder(),  processDefinitionUUID.getValue());
            final boolean deleted = deleteDirectory(formsDir);
            if(!deleted) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "unable to delete the web resources directory " + formsDir.getCanonicalPath() + ". You will be able to delete it manually once the JVM will shutdown");
                }
            }
        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while deleting the web resources directory for process " + processDefinitionUUID.getValue(), e);
            }
        }
    }
    
    /**
     * Get the application resource directory
     * @param processDefinitionUUID
     * @param processDeployementDate
     * @return
     * @throws IOException 
     */
    public static File getApplicationResourceDir(final ProcessDefinitionUUID processDefinitionUUID, final Date processDeployementDate) throws IOException {
        return new File(PropertiesFactory.getTenancyProperties().getDomainFormsWorkFolder(), processDefinitionUUID.getValue() + File.separator + processDeployementDate.getTime());
    }
    
    /**
     * Delete a directory and its content
     * @param directory the directory to delete
     * @return return true if the directory and its content were deleted successfully, false otherwise
     */
    private static boolean deleteDirectory(final File directory) {
        boolean success = true;;
        if (directory.exists()) {
            final File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                   success &= deleteDirectory(files[i]);
                } else {
                    success &= files[i].delete();
                }
            }
            success &= directory.delete();
        }
        return success;
    }
}
