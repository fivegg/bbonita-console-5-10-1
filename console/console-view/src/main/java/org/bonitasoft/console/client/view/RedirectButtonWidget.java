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
package org.bonitasoft.console.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Widget allowing to open an application in a new window
 * 
 * @author Anthony Birembaut
 * @revision Nicolas Chabanoles Changed the outer panel from VerticalPanel to FlowPanel
 * 
 */
public class RedirectButtonWidget extends Composite {

    /**
     * The PICTURE_PLACEHOLDER
     */
    private static final String PICTURE_PLACEHOLDER = "pictures/cleardot.gif";

    /**
     * picture used to display the widget
     */
    private Image theApplicationIcon;

    /**
     * full redirection URL with parameters
     */
    private String theRedirectionUrl;

    /**
     * name of the new window to open
     */
    private String theNewWindowName;

    /**
     * The URL params map
     */
    private Map<String, String> theUrlParamsMap;

    /**
     * The URL Hash params map
     */
    private Map<String, String> theUrlHashParamsMap;

    protected URLUtils urlUtils = URLUtilsFactory.getInstance();

    /**
     * Constructor
     * 
     * @param redirectionUrl URL of the application to open
     * @param windowName name of the new window
     * @param urlParamsMap map of parameters to pass in the URLS
     * @param urlHashParams map of hash parameters to pass in the URLS
     */
    public RedirectButtonWidget(final String redirectionUrl, final String windowName, final Map<String, String> urlParamsMap, Map<String, String> urlHashParamsMap, String iconTitle) {
        super();

        theNewWindowName = windowName;

        FlowPanel theOuterPanel = new FlowPanel();

        theApplicationIcon = new Image(PICTURE_PLACEHOLDER);

        theApplicationIcon.setStyleName(CSSClassManager.getOpenInApplicationIconStyle());

        theApplicationIcon.setTitle(iconTitle);

        theRedirectionUrl = redirectionUrl;

        theUrlParamsMap = urlParamsMap;

        theUrlHashParamsMap = urlHashParamsMap;

        theApplicationIcon.addClickHandler(new RedirectOnClickHandler());

        theOuterPanel.add(theApplicationIcon);

        initWidget(theOuterPanel);
    }

    /**
     * Listener to redirect to a new window on a click
     */
    private class RedirectOnClickHandler implements ClickHandler {

        /**
         * {@inheritDoc}
         */
        public void onClick(final ClickEvent event) {

            if (BonitaConsole.userProfile.useCredentialTransmission()) {
                RpcConsoleServices.getLoginService().generateTemporaryToken(new AsyncCallback<String>() {

                    public void onSuccess(String temporaryToken) {
                        Map<String, String> urlHashParamsMap = new HashMap<String, String>();
                        urlHashParamsMap.put(URLUtils.USER_CREDENTIALS_PARAM, temporaryToken);
                        String fullRedirectionUrl = buildFullRedirectionURL(urlHashParamsMap);
                        Window.open(fullRedirectionUrl, theNewWindowName, "");
                    }

                    public void onFailure(Throwable t) {
                        String fullRedirectionUrl = buildFullRedirectionURL(null);
                        Window.open(fullRedirectionUrl, theNewWindowName, "");
                    }
                });
            } else {
                String fullRedirectionUrl = buildFullRedirectionURL(null);
                Window.open(fullRedirectionUrl, theNewWindowName, "");
            }
        }
    }

    /**
     * Update the redirectionURL.
     * 
     * @param redirectionUrl
     */
    public void setRedirectionURL(final String redirectionUrl) {
        theRedirectionUrl = redirectionUrl;
    }

    private String buildFullRedirectionURL(final Map<String, String> additionnalUrlHashParamsMap) {
        final Map<String, List<String>> parametersMap = urlUtils.getURLParametersMap(theRedirectionUrl);
        final String url = urlUtils.removeURLparameters(theRedirectionUrl);
        StringBuilder urlParams = new StringBuilder();
        urlParams.append(url);
        if (!url.isEmpty()) {
            urlParams.append("?");
            for (Entry<String, String> urlParamEntry : theUrlParamsMap.entrySet()) {
                if (urlParams.length() > 1 && !urlParams.substring(urlParams.length() - 1).equals("?")) {
                    urlParams.append("&");
                }
                urlParams.append(urlParamEntry.getKey());
                urlParams.append("=");
                if (parametersMap.containsKey(urlParamEntry.getKey())) {
                    List<String> entryValues = parametersMap.get(urlParamEntry.getKey());
                    for (String value : entryValues) {
                        urlParams.append(value);
                        urlParams.append(",");
                    }
                    urlParams.deleteCharAt(urlParams.length() - 1);
                } else {
                    urlParams.append(urlParamEntry.getValue());
                }
            }
            for (Entry<String, List<String>> anApplicationURLParamEntry : parametersMap.entrySet()) {
                if (!theUrlParamsMap.containsKey(anApplicationURLParamEntry.getKey())) {
                    if (urlParams.length() > 1) {
                        urlParams.append("&");
                    }
                    if (!anApplicationURLParamEntry.getKey().equals(urlUtils.AUTO_INSTANTIATE)) {
                        urlParams.append(anApplicationURLParamEntry.getKey());
                        urlParams.append("=");
                        List<String> entryValues = parametersMap.get(anApplicationURLParamEntry.getKey());
                        for (String value : entryValues) {
                            urlParams.append(value);
                            urlParams.append(",");
                        }
                        urlParams.deleteCharAt(urlParams.length() - 1);
                    }
                }
            }
        }
        Map<String, String> urlHashParamsMap = new HashMap<String, String>(theUrlHashParamsMap);
        if (additionnalUrlHashParamsMap != null) {
            urlHashParamsMap.putAll(additionnalUrlHashParamsMap);
        }
        StringBuilder urlHashParams = new StringBuilder();
        if (!urlHashParamsMap.isEmpty()) {
            urlHashParams.append("#");
            for (Entry<String, String> urlHashParamEntry : urlHashParamsMap.entrySet()) {
                if (urlHashParams.length() > 1) {
                    urlHashParams.append("&");
                }
                if (urlHashParamEntry.getKey() != null && urlHashParamEntry.getValue() != null) {
                    urlHashParams.append(urlHashParamEntry.getKey());
                    urlHashParams.append("=");
                    urlHashParams.append(urlHashParamEntry.getValue());
                }
            }
        }
        return urlParams.toString() + urlHashParams.toString();
    }
}
