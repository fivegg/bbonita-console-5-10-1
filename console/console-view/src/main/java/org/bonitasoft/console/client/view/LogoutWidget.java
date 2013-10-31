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
package org.bonitasoft.console.client.view;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.security.client.view.common.RpcSecurityServices;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The logout widget allows to close the current session of the user.<br/>
 * As a consequence the user might be redirected to the login page.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class LogoutWidget extends BonitaPanel {

    protected final FlowPanel myOuterPanel = new FlowPanel();
    protected final UserProfile myUserProfile;

    /**
     * Default constructor.
     * 
     * @param aUserProfile
     */
    public LogoutWidget(UserProfile aUserProfile) {
        super();
        myUserProfile = aUserProfile;
        // set the css style name
        myOuterPanel.setStyleName("bos_logout_widget");
        if (myUserProfile.isAllowed(RuleType.LOGOUT)) {
            myOuterPanel.add(buildLogoutLink());
        }
        this.initWidget(myOuterPanel);

    }

    private Widget buildLogoutLink() {

    	Anchor theLogoutLink = new Anchor(constants.logout());
        theLogoutLink.setStylePrimaryName("identif-2");
        
        URLUtils urlUtils = URLUtilsFactory.getInstance();
        List<String> paramsToRemove = new ArrayList<String>();
        paramsToRemove.add(URLUtils.LOCALE_PARAM);
        final String domain = Window.Location.getParameter(URLUtils.DOMAIN_PARAM);
        String theRedirectURL = urlUtils.rebuildUrl(urlUtils.removeHashFromUrl(), paramsToRemove, null, null, null);
        String theURL = RpcSecurityServices.getLogoutURL();
        String theURLSuffix = "?" + URLUtils.REDIRECT_URL_PARAM + "=";
        try {
            theURLSuffix += URL.encodeQueryString(theRedirectURL);
        } catch (Exception e) {
            Window.alert("Unable to redirect to login page: Invalid URL");
            theURLSuffix += GWT.getModuleBaseURL();
        }
        if (domain != null) {
        	theURLSuffix += "&" + URLUtils.DOMAIN_PARAM + "=" + domain;
        }
        theLogoutLink.setHref(theURL + theURLSuffix);

        return theLogoutLink;
    }

}
