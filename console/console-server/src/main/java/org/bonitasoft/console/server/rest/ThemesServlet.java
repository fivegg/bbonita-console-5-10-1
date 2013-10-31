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
package org.bonitasoft.console.server.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.security.server.accessor.PropertiesFactory;

import com.google.gson.Gson;

/**
 * @author Christophe Leroy
 * 
 */
@Deprecated
public class ThemesServlet extends HttpServlet {
    private static final long serialVersionUID = 6020304876924216909L;

    private static final Logger LOGGER = Logger.getLogger(ThemesServlet.class.getName());

    /**
     * List all css availables in the theme folder
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ThemesBean result = new ThemesBean();

        // Get current theme
        final String currentTheme = PropertiesFactory.getTenancyProperties().getProperty("currentTheme");
        result.setCurrentTheme(currentTheme);

        // Get all available themes
        final File themeFolder = PropertiesFactory.getTenancyProperties().getDomainXPThemeFolder();
        if (themeFolder != null && themeFolder.isDirectory()) {
            final FilenameFilter filter = new CSSFileFilter();
            result.setThemes(themeFolder.list(filter));
        }

        final Gson gson = new Gson();
        final PrintWriter out = resp.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final BufferedReader reader = req.getReader();
        final String newTheme = URLDecoder.decode(reader.readLine(), "UTF-8");
        if (newTheme != null) {
            PropertiesFactory.getTenancyProperties().setProperty("currentTheme", newTheme);
        }
    }
}
