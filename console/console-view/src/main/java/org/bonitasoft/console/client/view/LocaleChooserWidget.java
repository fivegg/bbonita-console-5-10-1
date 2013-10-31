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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LocaleChooserWidget extends Composite {

    protected final static String DEFAULT_LANGUAGE_DISPLAY_NAME = "English";
    protected final FlowPanel myOuterPanel = new FlowPanel();
    protected final UserProfile myUserProfile;

    /**
     * 
     * Default constructor.
     * 
     * @param aUserProfile
     * 
     * @param aListener
     */
    public LocaleChooserWidget(UserProfile aUserProfile) {
        super();
        myUserProfile = aUserProfile;
        // set the css style name
        myOuterPanel.setStyleName("bos_locale_chooser");
        myOuterPanel.add(buildLocaleChooser());

        this.initWidget(myOuterPanel);
    }

    protected Widget buildLocaleChooser() {
        final ListBox theLocaleChooser = new ListBox();
        theLocaleChooser.setStyleName("bonita_locale_chooser");

        String theCurrentLocale = null;
        if (Window.Location.getParameter(URLUtils.LOCALE_PARAM) == null) {
        	theCurrentLocale = URLUtils.DEFAULT_LOCALE;
        } else {
        	theCurrentLocale = URLUtilsFactory.getInstance().getLocale();
        }

        String[] theAvailableLocaleNames = LocaleInfo.getAvailableLocaleNames();
        for (String theLocaleName : theAvailableLocaleNames) {
            if (theLocaleName.equals(URLUtils.DEFAULT_GWT_LOCALE_NAME)) {
                theLocaleChooser.addItem(DEFAULT_LANGUAGE_DISPLAY_NAME, theLocaleName);
                if (URLUtils.DEFAULT_LOCALE.equals(theCurrentLocale)) {
                    theLocaleChooser.setSelectedIndex(theLocaleChooser.getItemCount() - 1);
                }
            } else {
                String theLocalizedName = LocaleInfo.getLocaleNativeDisplayName(theLocaleName);
                theLocaleChooser.addItem(theLocalizedName, theLocaleName);
                if (theLocaleName.equals(theCurrentLocale)) {
                    theLocaleChooser.setSelectedIndex(theLocaleChooser.getItemCount() - 1);
                }
            }
        }
        theLocaleChooser.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                String theLocaleName = theLocaleChooser.getValue(theLocaleChooser.getSelectedIndex());
                URLUtils theURLUtils = URLUtilsFactory.getInstance();
                List<String> paramsToRemove = new ArrayList<String>();
                paramsToRemove.add(URLUtils.LOCALE_PARAM);
                Map<String, String> paramsToAdd = new HashMap<String, String>();
                paramsToAdd.put(URLUtils.LOCALE_PARAM, theLocaleName);
                theURLUtils.windowRedirect(theURLUtils.rebuildUrl(paramsToRemove, paramsToAdd, null, null));
            }
        });
        return theLocaleChooser;
    }

}
