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
 * 
 */
package org.bonitasoft.forms.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.common.themes.ThemeResourceServlet;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.security.server.accessor.PropertiesFactory;
import org.bonitasoft.console.security.server.accessor.TenancyProperties;
import org.bonitasoft.console.servlet.ServletLoginUtils;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormDefinitionAPI;
import org.bonitasoft.forms.server.provider.FormServiceProvider;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderFactory;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * Servlet for requesting home page
 * 
 * @author Ruiheng Fan
 */
public class HomepageServlet extends ApplicationResourceServlet {

    /**
     * UID
     */
    private static final long serialVersionUID = -2284578402691230994L;

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(HomepageServlet.class.getName());

    public static final String CONTENT_TYPE = "text/html";

    /**
     * Theme parameter
     */
    public static final String THEME_PARAM = "theme";

    /**
     * user XP's UI mode parameter
     */
    public static final String UI_MODE_PARAM = "ui";

    public static final String DEFAULT_CONSOLE_HOME_PAGE_FILENAME = "BonitaConsole.html";

    public static final String DEFAULT_ADMIN_HOME_PAGE_FILENAME = "admin.html";

    public static final String DEFAULT_APPLICATION_HOME_PAGE_FILENAME = "BonitaApplication.html";

    protected static final String ADMIN_UI = "admin";

    protected static final String CONSOLE_PREFIX = "/console";

    protected static final String HOMEPAGE_SERVLET_ID_IN_PATH = "homepage";
    
    protected static final String DEFAULT_THEME_NAME = "default";

    /**
     * {@inheritDoc}
     */
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {

        LoginContext loginContext = null;
        final String servletPath = request.getServletPath();
        final String urlPrefix = servletPath.replaceAll(HOMEPAGE_SERVLET_ID_IN_PATH, "");
        String themeName = request.getParameter(THEME_PARAM);
        final String ui = request.getParameter(UI_MODE_PARAM);
        final boolean isConsole = isConsole(urlPrefix, ui);
        try {
            if (themeName == null && isConsole) {
                themeName = PropertiesFactory.getTenancyProperties().getProperty(TenancyProperties.THEME_PROPERTY_NAME);
                if (themeName == null) {
                    themeName = DEFAULT_THEME_NAME;
                }
            }
            if (themeName != null && !themeName.isEmpty()) {

                if (isConsole) {
                    String fileName;
                    if (ADMIN_UI.equals(ui)) {
                        fileName = DEFAULT_ADMIN_HOME_PAGE_FILENAME;
                    } else {
                        fileName = DEFAULT_CONSOLE_HOME_PAGE_FILENAME;
                    }
                    ThemeResourceServlet.getThemePackageFile(request, response, themeName, fileName);
                } else {
                    loginContext = engineLogin(request);
                    final Map<String, Object> urlContext = new HashMap<String, Object>();
                    urlContext.put(FormServiceProviderUtil.PROCESS_UUID, themeName);
                    final String locale = getLocale(request);
                    urlContext.put(FormServiceProviderUtil.LOCALE, resolveLocale(locale));
                    final Map<String, Object> context = new HashMap<String, Object>();
                    context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
                    context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
                    final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
                    final Date deployementDate = formServiceProvider.getDeployementDate(context);
                    final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
                    final IFormDefinitionAPI definitionAPI = FormAPIFactory.getFormDefinitionAPI(document, deployementDate, locale);
                    final String fileName = definitionAPI.getHomePage();
                    if (fileName != null) {
                        byte[] content = null;
                        final File processDir = new File(PropertiesFactory.getTenancyProperties().getDomainFormsWorkFolder(), themeName);
                        if (processDir.exists()) {
                            final File file = new File(processDir, deployementDate.getTime() + File.separator + fileName);
                            try {
                                if (!file.getCanonicalPath().startsWith(processDir.getCanonicalPath())) {
                                    throw new IOException();
                                }
                            } catch (final IOException e) {
                                final String errorMessage = "Error while getting the resource " + fileName + " For security reasons, access to paths other than " + processDir.getName() + " is restricted";
                                if (LOGGER.isLoggable(Level.WARNING)) {
                                    LOGGER.log(Level.WARNING, errorMessage, e);
                                }
                                throw new ServletException(errorMessage);
                            }

                            int fileLength = 0;
                            if (file.length() > Integer.MAX_VALUE) {
                                throw new ServletException("file " + fileName + " too big !");
                            } else {
                                fileLength = (int) file.length();
                            }
                            InputStream inputStream = null;
                            try {
                                inputStream = new FileInputStream(file);
                                content = getFileContent(inputStream, fileLength, fileName);
                            } catch (final FileNotFoundException e) {
                                final String errorMessage = "Error while getting the application resource. The file " + fileName + " does not exist.";
                                if (LOGGER.isLoggable(Level.WARNING)) {
                                    LOGGER.log(Level.WARNING, errorMessage);
                                }
                                throw new ServletException(errorMessage);
                            } finally {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            }
                        } else {
                            if (LOGGER.isLoggable(Level.WARNING)) {
                                LOGGER.log(Level.WARNING, "The Process application resources deployement directory does not exist.");
                            }
                        }
                        try {
                            if (content != null) {
                                response.setContentLength(content.length);
                                response.setContentType(CONTENT_TYPE);
                                response.setCharacterEncoding("UTF-8");
                                final OutputStream out = response.getOutputStream();
                                out.write(content);
                                out.close();
                            }
                            return;
                        } catch (final IOException e) {
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.log(Level.SEVERE, "Error while generating the response.", e);
                            }
                            throw new ServletException(e);
                        }
                    } else {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE, "Loading the default HTML template");
                        }
                        try {
                            responseDefaultHomepage(request, response, urlPrefix);
                        } catch (final ServletException e1) {
                            if (LOGGER.isLoggable(Level.SEVERE)) {
                                LOGGER.log(Level.SEVERE, "Faild to get the default homepage");
                            }
                        }
                    }
                }
            } else {
                final String errorMessage = "Failed to get " + THEME_PARAM + " parameter in HomepageServlet.";
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, errorMessage);
                }
                throw new ServletException(errorMessage);
            }

        } catch (final Throwable e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Loading the default HTML template");
            }
            if (isConsole) {
                responseApplyThemeErrorHTML(request, response, urlPrefix);
            } else {
                try {
                    responseDefaultHomepage(request, response, urlPrefix);
                } catch (final ServletException e1) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Faild to get the default homepage");
                    }
                }
            }
        } finally {
            ServletLoginUtils.engineLogout(loginContext);
        }

    }

    /**
     * Simple engin login
     * 
     * @return the login context
     * @throws LoginException
     */
    protected LoginContext engineLogin(final HttpServletRequest request) throws LoginException {
        final CallbackHandler handler = new SimpleCallbackHandler(HOMEPAGE_SERVLET_ID_IN_PATH, HOMEPAGE_SERVLET_ID_IN_PATH);
        final LoginContext loginContext = new LoginContext(LoginServlet.JAAS_STORE_LOGIN_CONTEXT, handler);
        loginContext.login();
        return loginContext;
    }

    /**
     * Is console view mode
     * 
     * @param urlPrefix
     * @param ui
     * @return
     */
    protected boolean isConsole(final String urlPrefix, final String ui) {
        return urlPrefix.startsWith(CONSOLE_PREFIX) && ui != null;
    }

    /**
     * @param request
     * @param response
     * @param urlPrefix
     * @throws ServletException
     */
    protected void responseApplyThemeErrorHTML(final HttpServletRequest request, final HttpServletResponse response, final String urlPrefix) {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        final StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<body>");
        html.append("<h1>Error occurred while applying the theme.</h1>");
        html.append("<a href='");
        html.append(getErrorRedirectionHref(request, urlPrefix));
        html.append("'>Go to default theme</al>");
        html.append("</body>");
        html.append("</html>");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(html);
        } catch (final IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "failed to get the response");
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (final Exception e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while close the response output stream.", e);
                }
            }
        }
    }
    
    protected String getErrorRedirectionHref(final HttpServletRequest request, final String urlPrefix) {
        String href = request.getRequestURL().toString();
        if (href.endsWith(HOMEPAGE_SERVLET_ID_IN_PATH)) {
            href = href.replaceAll(HOMEPAGE_SERVLET_ID_IN_PATH, "");
            if (!href.endsWith("/")) {
                href += "/";
            }
        }
        final String ui = request.getParameter(UI_MODE_PARAM);
        if (urlPrefix.startsWith(CONSOLE_PREFIX) && ADMIN_UI.equals(ui)) {
            href += DEFAULT_ADMIN_HOME_PAGE_FILENAME + "?ui=" + ui;
        } else if (urlPrefix.startsWith(CONSOLE_PREFIX)) {
            href += DEFAULT_CONSOLE_HOME_PAGE_FILENAME + "?ui=" + ui;
        } else {
            href += DEFAULT_APPLICATION_HOME_PAGE_FILENAME;
        }
        return href;
    }

    /**
     * @param HttpServletRequest request
     * @param HttpServletResponse response
     * @param urlPrefix
     * @throws IOException
     */
    protected void responseDefaultHomepage(final HttpServletRequest request, final HttpServletResponse response, final String urlPrefix) throws ServletException {

        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        final String ui = request.getParameter(UI_MODE_PARAM);
        InputStream inputStream = null;
        if (urlPrefix.startsWith(CONSOLE_PREFIX) && ADMIN_UI.equals(ui)) {
            inputStream = getServletContext().getResourceAsStream(urlPrefix + "/" + DEFAULT_ADMIN_HOME_PAGE_FILENAME);
        } else if (urlPrefix.startsWith(CONSOLE_PREFIX)) {
            inputStream = getServletContext().getResourceAsStream(urlPrefix + "/" + DEFAULT_CONSOLE_HOME_PAGE_FILENAME);
        } else {
            inputStream = getServletContext().getResourceAsStream(urlPrefix + "/" + DEFAULT_APPLICATION_HOME_PAGE_FILENAME);
        }
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            final byte[] content = new byte[1024];
            int numRead;
            while ( (numRead = inputStream.read(content) ) >= 0) {
                outputStream.write(content, 0, numRead);
            }
        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while writing the response output stream.", e);
            }
            throw new ServletException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (final IOException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while closing the stream.", e);
                }
                throw new ServletException(e);
            }
        }
    }

    /**
     * @param HttpServletResponse response
     * @param byte[] content
     */
    protected void setResponseContent(final HttpServletResponse response, final byte[] content) {
        if (content != null) {
            response.setContentLength(content.length);
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding("UTF-8");
            OutputStream out = null;
            try {
                out = response.getOutputStream();
                out.write(content);
            } catch (final IOException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while writing the response output stream.", e);
                }
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (final IOException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "Error while closing the response output stream.", e);
                    }
                }
            }
        }
    }

    /**
     * @return the user's locale as a String
     */
    protected String getLocale(final HttpServletRequest request) {
        String localeStr = null;
        final HttpSession session = request.getSession();
        final User user = (User) session.getAttribute(LoginServlet.USER_SESSION_PARAM_KEY);
        if (user != null) {
            localeStr = user.getLocale();
        } else {
            localeStr = request.getLocale().toString();
        }
        return localeStr;
    }

    /**
     * @param localeStr the user's locale as a string
     * @return the user's {@link Locale}
     */
    protected Locale resolveLocale(final String localeStr) {
        final String[] localeParams = localeStr.split("_");
        final String language = localeParams[0];
        Locale userLocale = null;
        if (localeParams.length > 1) {
            final String country = localeParams[1];
            userLocale = new Locale(language, country);
        } else {
            userLocale = new Locale(language);
        }
        return userLocale;
    }
    
    

}