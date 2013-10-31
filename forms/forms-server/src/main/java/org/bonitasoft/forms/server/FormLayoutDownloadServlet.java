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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.forms.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.common.exception.NoCredentialsInSessionException;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.servlet.ServletLoginUtils;
import org.bonitasoft.forms.server.accessor.impl.util.FormCacheUtilFactory;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormDefinitionAPI;
import org.bonitasoft.forms.server.provider.FormServiceProvider;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderFactory;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;

/**
 * Servlet allowing to download process instances attachments
 * 
 * @author Anthony Birembaut
 */
public class FormLayoutDownloadServlet extends HttpServlet {

    /**
     * UID
     */
    private static final long serialVersionUID = 5209516978177786895L;

    /**
     * Body content ID
     */
    public static final String BODY_CONTENT_ID = "bodyContentId";

    public static final String IS_PAGE_LAYOUT = "isPageLayout";

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(FormLayoutDownloadServlet.class.getName());

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

        final String bodyContentId = request.getParameter(BODY_CONTENT_ID);
        String bodyContent = null;
        Map<String, Object> urlContext = new HashMap<String, Object>();
        for (Map.Entry<String, String[]> param : ((Map<String, String[]>) request.getParameterMap()).entrySet()) {
            urlContext.put(param.getKey(), param.getValue()[0]);
        }

        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, resolveLocale(getLocale(request)));

        if (bodyContentId != null) {
            try {
                String rawBodyContent = null;
                String isPageLayOutParam = request.getParameter(IS_PAGE_LAYOUT);
                Boolean isPageLayout = Boolean.parseBoolean(isPageLayOutParam);
                if (isPageLayout) {
                    rawBodyContent = FormCacheUtilFactory.getTenancyFormCacheUtil().getPageLayoutContent(bodyContentId);
                } else {
                    rawBodyContent = FormCacheUtilFactory.getTenancyFormCacheUtil().getApplicationLayoutContent(bodyContentId);
                }
                ServletLoginUtils.engineLogin(request);
                FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
                final Map<String, String> activityAttributes = formServiceProvider.getAttributesToInsert(context);
                if (activityAttributes != null) {
                    final Locale userLocale = (Locale) context.get(FormServiceProviderUtil.LOCALE);
                    final FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
                    final Date deployementDate = formServiceProvider.getDeployementDate(context);
                    String localeStr = getLocale(request);
                    final IFormDefinitionAPI definitionAPI = FormAPIFactory.getFormDefinitionAPI(document, deployementDate, localeStr);
                    bodyContent = definitionAPI.applicationAttributes(rawBodyContent, activityAttributes, userLocale);
                } else {
                    bodyContent = rawBodyContent;
                }
            } catch (NoCredentialsInSessionException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
                String refer = request.getParameter("refer");
                String newURL = "login.jsp?redirectUrl=" + URLEncoder.encode(refer);
                try {
                    response.sendRedirect(newURL);
                } catch (IOException e1) {
                    final String errorMessage = "Error while redircting to the URL: " + newURL;
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, errorMessage, e);
                    }
                    throw new ServletException(errorMessage, e);
                }

            } catch (Exception e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "Error while retrieving the body content.", e);
                }
                throw new ServletException(e.getMessage(), e);
            }
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        try {
            final String encodedfileName = URLEncoder.encode("bodyContent", "UTF-8");
            final String userAgent = request.getHeader("User-Agent");
            if (userAgent != null && userAgent.contains("Firefox")) {
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedfileName.replace("+", "%20"));
            } else {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName.replaceAll("\\+", " ") + "\"; filename*=UTF-8''"
                        + encodedfileName.replace("+", "%20"));
            }
            final OutputStream out = response.getOutputStream();
            if (bodyContent == null) {
                response.setContentLength(0);
            } else {
                response.setContentLength(bodyContent.getBytes().length);
                out.write(bodyContent.getBytes());
            }
            out.close();
        } catch (final IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Error while generating the response.", e);
            }
            throw new ServletException(e.getMessage(), e);
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
            localeStr = getFormLocale(request);
        }
        if (localeStr == null && user != null) {
            localeStr = user.getLocale();
        }
        if (localeStr == null) {
            localeStr = request.getLocale().toString();
        }
        return localeStr;
    }

    protected String getFormLocale(final HttpServletRequest request) {
        String userLocaleStr = null;
        final String theLocaleCookieName = FormsServlet.FORM_LOCALE_COOKIE_NAME;
        final Cookie theCookies[] = request.getCookies();
        Cookie theCookie = null;
        if (theCookies != null) {
            for (int i = 0; i < theCookies.length; i++) {
                if (theCookies[i].getName().equals(theLocaleCookieName)) {
                    theCookie = theCookies[i];
                    userLocaleStr = theCookie.getValue().toString();
                    break;
                }
            }
        }
        return userLocaleStr;
    }

    /**
     * @param localeStr
     *            the user's locale as a string
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
