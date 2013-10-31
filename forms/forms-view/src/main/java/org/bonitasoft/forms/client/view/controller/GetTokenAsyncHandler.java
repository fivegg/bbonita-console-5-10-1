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
package org.bonitasoft.forms.client.view.controller;

import java.util.Map;

import org.bonitasoft.forms.client.view.common.DOMUtils;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author  Anthony Birembaut
 */
public class GetTokenAsyncHandler implements AsyncCallback<String> {
    
    /**
     * Utility Class form DOM manipulation
     */
    protected DOMUtils domUtils = DOMUtils.getInstance();
    
    /**
     * Utility Class form URL manipulation
     */
    protected URLUtils urlUtils = URLUtilsFactory.getInstance();

    protected String applicationURL;

    protected Map<String, Object> urlContext;

    public GetTokenAsyncHandler(final String applicationURL, final Map<String, Object> urlContext) {
        this.applicationURL = applicationURL;
        this.urlContext = urlContext;
    }

    public void onSuccess(final String temporaryToken) {
        urlContext.put(URLUtils.USER_CREDENTIALS_PARAM, temporaryToken);
        final String url = urlUtils.getFormRedirectionUrl(applicationURL, urlContext);
        if (domUtils.isPageInFrame()) {
            urlUtils.frameRedirect(DOMUtils.DEFAULT_FORM_ELEMENT_ID, url);
        } else {
            urlUtils.windowAssign(url);
        }
    }

    public void onFailure(final Throwable t) {
        final String url = urlUtils.getFormRedirectionUrl(applicationURL, urlContext);
        if (domUtils.isPageInFrame()) {
            urlUtils.frameRedirect(DOMUtils.DEFAULT_FORM_ELEMENT_ID, url);
        } else {
            urlUtils.windowAssign(url);
        }
    }
}