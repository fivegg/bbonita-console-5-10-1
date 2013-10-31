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
package org.bonitasoft.console.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.security.auth.login.LoginContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.security.server.accessor.PropertiesFactory;
import org.bonitasoft.console.server.login.SessionManager;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ImageRendererServlet extends HttpServlet {

    private static final long serialVersionUID = 5537221784920655079L;
    private static final String IMAGE_REGEX = ".*\\.png";
    private static final String ERROR_MESSAGE = "No image file found.";

    public ImageRendererServlet() {

        super();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // post not supported.
        throw new ServletException();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(request);
            final String name = request.getParameter("image");
            if (name == null) {
                throw new ServletException("Missing mandatory 'image' parameter!");
            }
            final PrintWriter out = response.getWriter();

            final File f = new File(PropertiesFactory.getTenancyProperties().getDomainXPTempFolder(), name);

            if (f.exists() && f.isFile() && f.getAbsolutePath().toLowerCase().matches(IMAGE_REGEX)) {

                response.setContentType("image/png");

                FileInputStream fis = new FileInputStream(f);
                try {
                    response.setContentLength(fis.available());

                    int i = 0;

                    while ((i = fis.read()) != -1) {
                        out.write(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    fis.close();
                }

            } else {
                // response.setContentType("text/html");
                // out.write(ERROR_MESSAGE);
                throw new ServletException(ERROR_MESSAGE);
            }
        } catch (SessionTimeOutException e) {
            e.printStackTrace();
            throw new ServletException();
        } catch (ServletException e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new ServletException(t.getMessage());
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

}
