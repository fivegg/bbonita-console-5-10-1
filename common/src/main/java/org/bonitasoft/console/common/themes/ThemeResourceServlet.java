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
package org.bonitasoft.console.common.themes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
 * @author Minghui.Dai
 * 
 */
public class ThemeResourceServlet extends HttpServlet {

    /**
     * UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger
     */
    private final static Logger LOGGER = Logger.getLogger(ThemeResourceServlet.class.getName());

    /**
     * theme name : the theme folder's name
     */
    private final static String THEME = "theme";

    /**
     * file name
     */
    private final static String LOCATION = "location";

    /**
     * {@inheritDoc}
     */
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        final String themeName = request.getParameter(THEME);

        final String fileName = request.getParameter(LOCATION);
        try {
            getThemePackageFile(request, response, themeName, fileName);
        } catch (UnsupportedEncodingException e) {
            final String errorMessage = "UnsupportedEncodingException :" + e;
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage);
            }
            throw new ServletException(errorMessage);
        }
    }

    /**
     * Get theme package file
     * 
     * @param request
     * @param response
     * @param themeName
     * @param fileName
     * @throws ServletException
     * @throws UnsupportedEncodingException
     */
    public static void getThemePackageFile(final HttpServletRequest request, final HttpServletResponse response, String themeName, String fileName) throws ServletException, UnsupportedEncodingException {
        byte[] content = null;
        String contentType = null;
        File themesFolder = null;
        if (themeName == null) {
            final String errorMessage = "Error while using the servlet ThemeResourceServlet to get a resource: the parameter " + THEME + " is null.";
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, errorMessage);
            }
            throw new ServletException(errorMessage);
        }
        if (fileName == null) {
            final String errorMessage = "Error while using the servlet ThemeResourceServlet to get a resource: the parameter " + LOCATION + " is null.";
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, errorMessage);
            }
            throw new ServletException(errorMessage);
        }
        themeName = URLDecoder.decode(themeName, "UTF-8");
        fileName = URLDecoder.decode(fileName, "UTF-8");
        response.setCharacterEncoding("UTF-8");
        themesFolder = getThemesParentFolder(request);
        try {

            final File file = new File(themesFolder.getAbsolutePath() + File.separator + themeName + File.separator + fileName);
            try {
                if (!file.getCanonicalPath().startsWith(themesFolder.getCanonicalPath())) {
                    throw new IOException();
                }
            } catch (final IOException e) {
                final String errorMessage = "Error while using the servlet ThemeResourceServlet to get a theme file " + themeName + " For security reasons, access to paths other than " + themesFolder.getName() + " is restricted";
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, errorMessage, e);
                }
                throw new ServletException(errorMessage);
            }
            if (fileName.toLowerCase().endsWith(".jpg")) {
                contentType = "image/jpg";
            } else if (fileName.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (fileName.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.toLowerCase().endsWith(".css")) {
                contentType = "text/css";
            } else if (fileName.toLowerCase().endsWith(".js")) {
                contentType = "application/x-javascript";
            } else if (fileName.toLowerCase().endsWith(".html")) {
                contentType = "text/html";
            } else {
                final FileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
                contentType = mimetypesFileTypeMap.getContentType(file);
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            final int fileLength = (int) file.length();
            content = getFileContent(file, fileLength, fileName);
            response.setContentType(contentType);
            response.setContentLength(content.length);
            response.setBufferSize(content.length);
            final OutputStream out = response.getOutputStream();
            out.write(content, 0, content.length);
            response.flushBuffer();
            out.close();
        } catch (final IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while generating the response.", e);
            }
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * get file content
     * 
     * @param file
     * @param fileLength
     * @param filePath
     * @return
     * @throws ServletException
     */
    protected static byte[] getFileContent(final File file, final int fileLength, final String filePath) throws ServletException {
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
                    LOGGER.log(Level.WARNING, errorMessage, e);
                }
                throw new ServletException(errorMessage, e);
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

    protected static File getThemesParentFolder(final HttpServletRequest request) throws ServletException {
        File myThemesParentFolder = null;
        try {
            myThemesParentFolder = PropertiesFactory.getTenancyProperties().getDomainXPThemeFolder();
        } catch (IOException e) {
            final String errorMessage = "Error while using the servlet ThemeResourceServlet to get themes parent folder.";
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, errorMessage);
            }
            throw new ServletException(errorMessage);
        }
        return myThemesParentFolder;
    }
}
