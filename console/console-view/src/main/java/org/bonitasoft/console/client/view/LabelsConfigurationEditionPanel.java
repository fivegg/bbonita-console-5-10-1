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

import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelsConfigurationEditionPanel extends SetupPanel {

    protected final FlowPanel myOuterPanel;
    protected final FlowPanel myInnerPanel = new FlowPanel();
    protected final CustomMenuBar myGlobalSettingsSaveButton = new CustomMenuBar();

    protected LabelsConfiguration myLabelsConfiguration;
    protected final CheckBox myStarLabelActivationStateSelector;
    protected final CheckBox myCustomLabelActivationStateSelector;

    protected final LabelDataSource myLabelDataSource;
    protected final CaseDataSource myCaseDataSource;

    public LabelsConfigurationEditionPanel(final LabelDataSource aReportingDataSource, final CaseDataSource aCaseDataSource, final MessageDataSource aMessageDataSource) {
        super(aMessageDataSource);
        myLabelDataSource = aReportingDataSource;
        myCaseDataSource = aCaseDataSource;
        myOuterPanel = new FlowPanel();
        myOuterPanel.setStylePrimaryName(DEFAULT_CSS_STYLE);
        myOuterPanel.addStyleName("bos_userLabelsConfigurationPanel");

        myStarLabelActivationStateSelector = new CheckBox();
        myCustomLabelActivationStateSelector = new CheckBox();

        initWidget(myOuterPanel);
    }

    @Override
    public String getLocationLabel() {
        return constants.labels();
    }    
    
    protected void buildContent() {
        final VerticalPanel descriptionPanel = new VerticalPanel();
        descriptionPanel.add(new HTML(constants.labelConfigurationTabDescription()));
        descriptionPanel.setStyleName("descriptionPanel");
        myOuterPanel.add(descriptionPanel);
        
        myGlobalSettingsSaveButton.addItem(constants.save(), new Command() {
            public void execute() {
                saveGlobalSettings();
            }
        });

        final FlowPanel theStarLabelSelectorWrapper = new FlowPanel();
        theStarLabelSelectorWrapper.add(myStarLabelActivationStateSelector);

        final FlowPanel theCustomLabelSelectorWrapper = new FlowPanel();
        theCustomLabelSelectorWrapper.add(myCustomLabelActivationStateSelector);
        // layout.
        myInnerPanel.add(theStarLabelSelectorWrapper);
        myInnerPanel.add(theCustomLabelSelectorWrapper);
        myInnerPanel.add(myGlobalSettingsSaveButton);
        myOuterPanel.add(myInnerPanel);
    }

    protected void saveGlobalSettings() {

        if (myLabelsConfiguration != null) {
            myLabelsConfiguration.setCustomLabelsEnabled(myCustomLabelActivationStateSelector.getValue());
            myLabelsConfiguration.setStarEnabled(myStarLabelActivationStateSelector.getValue());
            myOuterPanel.addStyleName("loading");

            myLabelDataSource.updateConfiguration(myLabelsConfiguration, new AsyncHandler<Void>() {
                public void handleFailure(Throwable anCaught) {
                    myOuterPanel.removeStyleName("loading");
                    myMessageDataSource.addErrorMessage(messages.unableToUpdateConfiguration());
                }

                public void handleSuccess(Void anResult) {
                    myMessageDataSource.addInfoMessage(messages.configurationUpdated());
                    myOuterPanel.removeStyleName("loading");
                }
            });
        }
    }

    private void updateMyGeneralPanel() {

        // Reporting configuration.
        myLabelDataSource.getConfiguration(new AsyncHandler<LabelsConfiguration>() {

            public void handleFailure(Throwable caught) {
                GWT.log("Unable to display the Labels configuration page properly: ", caught);
            };

            public void handleSuccess(LabelsConfiguration aResult) {
                myLabelsConfiguration = aResult;
                if (myLabelsConfiguration != null) {

                    myStarLabelActivationStateSelector.setValue(myLabelsConfiguration.isStarEnabled(), false);
                    myCustomLabelActivationStateSelector.setValue(myLabelsConfiguration.isCustomLabelsEnabled(), false);

                    myStarLabelActivationStateSelector.setText(constants.starLabelUsageActivationDescription());
                    myCustomLabelActivationStateSelector.setText(constants.customLabelsActivationDescription());
                }
            };
        });
    }

    @Override
    public void updateContent() {
        updateMyGeneralPanel();

    }
    
}
