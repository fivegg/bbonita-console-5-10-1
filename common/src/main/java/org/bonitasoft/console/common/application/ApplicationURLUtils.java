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
package org.bonitasoft.console.common.application;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.util.AccessorUtil;

/**
 * Utility class dealing with the metadatas for the URLs of the dedicated application
 * 
 * @author Nicolas Chabanoles, Anthony Birembaut
 */
public class ApplicationURLUtils {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(ApplicationURLUtils.class.getName());

    /**
     * Dedicated application host page
     */
    private static final String HOSTPAGE_PATH = "/application/BonitaApplication.html";

    /**
     * Dedicated application URL metadata name
     */
    public static final String DEDICATED_APP_URL_META_NAME = "dedicated_application_URL";
    
    /**
     * Homepage servlet ID in path
     */
    public static final String HOMEPAGE_SERVLET_ID_IN_PATH = "homepage";
    
    /**
     * Theme parameter
     */
    public static final String THEME_PARAM = "theme";

    /**
     * Instance attribute
     */
    private static ApplicationURLUtils INSTANCE = null;

    /**
     * @return the FormExpressionsAPI instance
     */
    public static synchronized ApplicationURLUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationURLUtils();
        }
        return INSTANCE;
    }

    /**
     * Private contructor to prevent instantiation
     */
    private ApplicationURLUtils() {
    }

    /**
     * Retrieve the dedicated application URL and set it in the
     * 
     * @param request the current request
     * @param aProcessDefinitionUUID the process definition UUID
     * @return the URL of the dedicated application (null if no dedicated application is deployed for the process)
     * @throws ProcessNotFoundException
     */
    public String getOrSetURLMetaData(final HttpServletRequest request, final ProcessDefinitionUUID aProcessDefinitionUUID) throws ProcessNotFoundException {
        String theResult = null;

        final String theMeta = AccessorUtil.getQueryDefinitionAPI().getProcessMetaData(aProcessDefinitionUUID, DEDICATED_APP_URL_META_NAME);

        if (theMeta == null || theMeta.length() == 0) {
            // The Metadata is not set yet.
            // Try to autodetect it.
            final String theScheme = request.getScheme();
            final String theServerName = request.getServerName();
            final int theServerPort = request.getServerPort();

            URL theApplicationURL;
            try {
                theApplicationURL = new URL(theScheme + "://" + theServerName + ":" + theServerPort + "/" + aProcessDefinitionUUID + HOSTPAGE_PATH);
                final boolean urlIsReachable = checkURLConnection(theApplicationURL);
                if (urlIsReachable) {
                    setProcessApplicationURLMetadata(aProcessDefinitionUUID, theApplicationURL.toString());
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Adding the " + DEDICATED_APP_URL_META_NAME + " metadata for the process " + aProcessDefinitionUUID + ": " + theApplicationURL.toString());
                    }
                    theResult = theApplicationURL.toString();
                } else {
                    theResult = HOMEPAGE_SERVLET_ID_IN_PATH + "?" + THEME_PARAM + "=" + aProcessDefinitionUUID;
                }
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }

        } else {
            theResult = theMeta;
        }
        /*
         * If the meta is set do not modify it else { URL theApplicationURL; try { theApplicationURL = new URL(theMeta); boolean
         * urlIsReachable = checkURLConnection(theApplicationURL); if (!urlIsReachable) { // Remove the meta as it seems to be
         * obsolete. AccessorUtil.getManagementAPI().deleteMetaData (DEDICATED_APP_URL_META_NAME + "-" +
         * aProcessDefinitionUUID); if (LOGGER.isLoggable(Level.INFO)) { LOGGER.log(Level.INFO, "Deleting metadata :" +
         * DEDICATED_APP_URL_META_NAME + "-" + aProcessDefinitionUUID + " : " + theMeta); } } else { theResult = theMeta; } }
         * catch (MalformedURLException e) { // TODO Auto-generated catch block e.printStackTrace(); } }
         */
        return theResult;
    }

    /**
     * @param aProcessDefinitionUUID
     * @param anApplicationURL
     * @throws ProcessNotFoundException
     */
    public void setProcessApplicationURLMetadata(final ProcessDefinitionUUID aProcessDefinitionUUID, final String anApplicationURL) throws ProcessNotFoundException {
        AccessorUtil.getRuntimeAPI().addProcessMetaData(aProcessDefinitionUUID, DEDICATED_APP_URL_META_NAME, anApplicationURL);
    }

    /**
     * Check if the dedicated application URL is reachable
     * 
     * @param anUrlToTest the URL to check
     * @return true if the URL is reacheable, false otherwise
     */
    private boolean checkURLConnection(final URL anUrlToTest) {

        final HttpClient client = new HttpClient();

        // Create a method instance.
        final GetMethod method = new GetMethod(anUrlToTest.toString());

        // See rfc2616 Section 14.10: "signal that the connection will be closed after completion of the response"
        method.setRequestHeader("Connection", "close");
        
        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

        boolean urlReachable = false;
        try {
            // Execute the method.
            final int statusCode = client.executeMethod(method);

            urlReachable = (statusCode == HttpStatus.SC_OK);

        } catch (final HttpException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Fatal protocol violation: " + e.getMessage());
            }
        } catch (final IOException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Fatal transport error: " + e.getMessage());
            }
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        return urlReachable;
    }

}
