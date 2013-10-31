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
package org.bonitasoft.console.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bonitasoft.console.security.server.accessor.PropertiesFactory;

public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = -7016515158304375336L;
	
	private static final Logger LOGGER = Logger.getLogger(FileUploadServlet.class.getName());

	@SuppressWarnings("unchecked")
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("text/html;charset=UTF-8");
	    final PrintWriter responsePW = response.getWriter();
		try {
			if (!ServletFileUpload.isMultipartContent(request)) {
				return;
			}
			final FileItemFactory fileItemFactory = new DiskFileItemFactory();
			final ServletFileUpload serviceFileUpload = new ServletFileUpload(fileItemFactory);
			final List<FileItem> items = (List<FileItem>) serviceFileUpload.parseRequest(request);

			for (final Iterator<FileItem> i = items.iterator(); i.hasNext();) {
				final FileItem item = (FileItem) i.next();
				if (item.isFormField()) {
					continue;
				}
				String fileName = item.getName();
				int slash = fileName.lastIndexOf("/");
				if (slash == -1) {
					slash = fileName.lastIndexOf("\\");
				}
				if (slash != -1) {
					fileName = fileName.substring(slash + 1);
				}
				File uploadedFile = new File(PropertiesFactory.getTenancyProperties().getDomainCommonTempFolder(), fileName);
				int suffix = 0;
				while (uploadedFile.exists()) {
					uploadedFile = new File(PropertiesFactory.getTenancyProperties().getDomainCommonTempFolder(), fileName + "." + suffix);
					suffix++;
				}
				if (LOGGER.isLoggable(Level.FINE)) {
				    LOGGER.log(Level.FINE, "uploaded File Path: " + uploadedFile.getPath());
				}
				uploadedFile.deleteOnExit();
				final String uploadedFilePath = uploadedFile.getPath();
				final String encodedUploadedFilePath = URLEncoder.encode(uploadedFilePath, "UTF-8");
				responsePW.print(encodedUploadedFilePath);
				item.write(uploadedFile);
				responsePW.flush();
			}
		} catch (final Exception e) {
			final String theErrorMessage = "Exception while uploading file.";
			if (LOGGER.isLoggable(Level.SEVERE)) {
			    LOGGER.log(Level.SEVERE,theErrorMessage,e);
			}
			throw new ServletException(theErrorMessage, e);
		}
	}
}
