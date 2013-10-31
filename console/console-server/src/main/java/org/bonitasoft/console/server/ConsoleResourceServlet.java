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
package org.bonitasoft.console.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.security.server.accessor.PropertiesFactory;

/**
 * Servlet allowing to download process application resources
 * 
 * @author Anthony Birembaut - Christophe Leroy
 */
public class ConsoleResourceServlet extends HttpServlet {

    /**
     * UID
     */
    private static final long serialVersionUID = -4669644185120817675L;

    /**
     * resource : indicate the path of the resource
     */
    public static final String RESOURCE_PATH_PARAM = "location";

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(ConsoleResourceServlet.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

        final String resourcePath = request.getParameter(RESOURCE_PATH_PARAM);
        String resourceFileName = null;
        byte[] content = null;
        String contentType = null;
        if (resourcePath == null) {
            final String errorMessage = "Error while using the servlet ConsoleResourceServlet to get a resource: the parameter " + RESOURCE_PATH_PARAM + " is undefined.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new ServletException(errorMessage);
        }

        try {
            final File resourcesDir = PropertiesFactory.getTenancyProperties().getDomainXPThemeFolder();
            if (resourcesDir.exists()) {
                final File file = new File(resourcesDir, resourcePath);
                try {
                    if (!file.getCanonicalPath().startsWith(resourcesDir.getCanonicalPath())) {
                        throw new IOException();
                    }
                } catch (final IOException e) {
                    final String errorMessage = "Error while getting the resource " + resourcePath + " For security reasons, access to paths other than " + resourcesDir.getName() + " is restricted";
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, errorMessage, e);
                    }
                    throw new ServletException(errorMessage);
                }

                int fileLength = 0;
                if (file.length() > Integer.MAX_VALUE) {
                    throw new ServletException("file " + resourcePath + " too big !");
                } else {
                    fileLength = (int) file.length();
                }
                resourceFileName = file.getName();
                content = getFileContent(file, fileLength, resourcePath);
                if (resourceFileName.endsWith(".css")) {
                    contentType = "text/css";
                } else {
                    final FileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
                    contentType = mimetypesFileTypeMap.getContentType(file);
                }
            } else {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "The console resources deployement directory does not exist.");
                }
            }
        } catch (final Exception e) {
            final String errorMessage = "Error while getting the resource " + resourcePath;
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new ServletException(errorMessage, e);
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);
        response.setCharacterEncoding("UTF-8");
        try {
            final String encodedfileName = URLEncoder.encode(resourceFileName, "UTF-8");
            final String userAgent = request.getHeader("User-Agent");
            if (userAgent != null && userAgent.contains("Firefox")) {
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedfileName);
            } else {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName.replaceAll("\\+", " ") + "\"; filename*=UTF-8''" + encodedfileName);
            }
            response.setContentLength(content.length);
            final OutputStream out = response.getOutputStream();
            out.write(content);
            out.close();
        } catch (final IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while generating the response.", e);
            }
            throw new ServletException(e.getMessage(), e);
        }
    }

    protected byte[] getFileContent(final File file, final int fileLength, final String filePath) throws ServletException {
        byte[] content = null;
        try {
            final InputStream fileInput = new FileInputStream(file);
            final byte[] fileContent = new byte[fileLength];
            try {
                int offset = 0;
                int length = fileLength;
                while (length > 0) {
                    final int read = fileInput.read(fileContent, offset, length);
                    if (read <= 0) {
                        break;
                    }
                    length -= read;
                    offset += read;
                }
                content = fileContent;
            } catch (final FileNotFoundException e) {
                final String errorMessage = "Error while getting the resource. The file " + filePath + " does not exist.";
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, errorMessage);
                }
                throw new ServletException(errorMessage);
            } finally {
                fileInput.close();
            }
        } catch (final IOException e) {
            final String errorMessage = "Error while reading resource: " + filePath;
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new ServletException(errorMessage, e);
        }
        return content;
    }

}
