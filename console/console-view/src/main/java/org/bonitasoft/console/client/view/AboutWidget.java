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

import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.users.UserProfile;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class AboutWidget extends BonitaPanel {

    private FlowPanel myOuterPanel;

    private UserProfile myUserProfile;

    /**
     * Default constructor.
     * 
     * @param aUserProfile the user profile
     */
    public AboutWidget(UserProfile aUserProfile) {
        super();
        myUserProfile = aUserProfile;
        myOuterPanel = new FlowPanel();
        myOuterPanel.add(buildAboutPanel());
        initWidget(myOuterPanel);
        myOuterPanel.setStylePrimaryName("bos_about_widget");
    }

    public Widget buildAboutPanel() {
        final Label theAboutLink = new Label(constants.about());
        theAboutLink.setStylePrimaryName("identif-2");
        theAboutLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                final CustomDialogBox theDialogBox = new CustomDialogBox(true);
                theDialogBox.addStyleName("bos_about_dialog");
                final FlowPanel theDialogContent = new FlowPanel();
                final CustomMenuBar theCloseButton = new CustomMenuBar();
                theCloseButton.addItem(constants.okButton(), new Command() {
                    public void execute() {
                        theDialogBox.hide();
                    }
                });
                theDialogBox.setText(constants.about());
                theDialogContent.add(new HTML(patterns.aboutContent(ConsoleConstants.BOS_VERSION)));
                if (myUserProfile.getEdition() != null) {
                    theDialogContent.add(new HTML(patterns.productEdition(myUserProfile.getEdition())));
                }
                theDialogContent.add(theCloseButton);
                theDialogBox.add(theDialogContent);
                theDialogBox.center();
            }
        });
        return theAboutLink;
    }
}
