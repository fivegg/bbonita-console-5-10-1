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
import java.util.Map;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.forms.client.view.common.URLUtils;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Specialization of the RedirectButtonWidget to deal with a Process
 * @author Anthony Birembaut
 * @revision Nicolas Chabanoles Changed the outer panel from VerticalPanel to FlowPanel
 *
 */
public class ProcessRedirectWidget extends BonitaPanel {
    
    /**
     * name of the new window
     */
    private static final String APPLICATION_WINDOW_NAME = constants.defaultApplicationFormWindowName();

    /**
     * Constructor
     * @param formID 
     * @param aStep
     */
    public ProcessRedirectWidget(final BonitaProcess aProcess) {

        FlowPanel theOuterPanel = new FlowPanel();

        Map<String, String> urlHashParamsMap = new HashMap<String, String>();
        
        String formID = aProcess.getUUID().getValue()+"$entry";
        
        urlHashParamsMap.put(URLUtils.FORM_ID, formID);
        
        urlHashParamsMap.put(URLUtils.PROCESS_ID_PARAM, aProcess.getUUID().getValue());
        
        Map<String, String> urlParamsMap = new HashMap<String, String>();
        
        urlParamsMap.put(URLUtils.LOCALE_PARAM, LocaleInfo.getCurrentLocale().getLocaleName());
        
        if(BonitaConsole.userProfile.getDomain() != null) {
            urlParamsMap.put(URLUtils.DOMAIN_PARAM, BonitaConsole.userProfile.getDomain());
        }
        
        String theApplicationURL = aProcess.getApplicationUrl();
        RedirectButtonWidget button = null;
        if (theApplicationURL == null || "".equals(theApplicationURL)) {
            urlHashParamsMap.put(URLUtils.VIEW_MODE_PARAM, URLUtils.FULL_FORM_APPLICATION_MODE);
            button = new RedirectButtonWidget(Window.Location.getPath(), APPLICATION_WINDOW_NAME, urlParamsMap, urlHashParamsMap, constants.redirectButtonTitle());
        } else {
            urlHashParamsMap.put(URLUtils.VIEW_MODE_PARAM, URLUtils.FULL_FORM_APPLICATION_MODE);
            button = new RedirectButtonWidget(theApplicationURL, APPLICATION_WINDOW_NAME, urlParamsMap, urlHashParamsMap, constants.redirectButtonTitle());
        }

        theOuterPanel.add(button);

        initWidget(theOuterPanel);
    }

}
