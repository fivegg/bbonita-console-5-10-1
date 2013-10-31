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
import org.bonitasoft.console.client.identity.IdentityConfiguration;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserEditionPanel extends SetupPanel {

	protected final FlexTable myOuterPanel;
	protected final CustomMenuBar mySaveButton = new CustomMenuBar();
	protected final UserDataSource myUserDataSource;
	protected IdentityConfiguration myIdentityConfiguration;
	protected final CheckBox myUserNameCompletionStateSelector;

	public UserEditionPanel(final UserDataSource aUserDataSource, final MessageDataSource aMessageDataSource) {
		super(aMessageDataSource);
		myUserDataSource = aUserDataSource;
		myOuterPanel = new FlexTable();
		myUserNameCompletionStateSelector = new CheckBox();

		initWidget(myOuterPanel);
	}

	protected void buildContent() {
		// Create the sync button.
		mySaveButton.addItem(constants.save(), new Command() {
			public void execute() {
				saveSettings();
			}
		});
		myOuterPanel.setHTML(0, 0, constants.identityTabDescription());
		myOuterPanel.getFlexCellFormatter().setColSpan(0, 0, 2);
		myOuterPanel.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		myUserNameCompletionStateSelector.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> anEvent) {
				if (anEvent.getValue()) {
					myOuterPanel.setHTML(1, 0, constants.deactivateUserNameCompletion());
				} else {
					myOuterPanel.setHTML(1, 0, constants.activateUserNameCompletion());
				}
			}
		});
	}

	protected void saveSettings() {
		
		// make the call to the server only when needed.
		if ((myIdentityConfiguration.isUserCompletionEnabled() && !myUserNameCompletionStateSelector.getValue())
				|| (!myIdentityConfiguration.isUserCompletionEnabled() && myUserNameCompletionStateSelector.getValue())) {
			displayLoading();
			myOuterPanel.addStyleName(LOADING_STYLE);
			myIdentityConfiguration.setUserCompletionEnabled(myUserNameCompletionStateSelector.getValue());
			myUserDataSource.updateConfiguration(myIdentityConfiguration, new AsyncHandler<Void>() {
				public void handleFailure(Throwable anT) {
					hideLoading();
					myOuterPanel.removeStyleName(LOADING_STYLE);
				}

				public void handleSuccess(Void anResult) {
					hideLoading();
					myOuterPanel.removeStyleName(LOADING_STYLE);
				}
			});
		}
	}

	@Override
	public void updateContent() {
		// Case datasource configuration.
		myUserDataSource.getConfiguration(new AsyncHandler<IdentityConfiguration>() {

			public void handleFailure(Throwable caught) {
				// Ensure a minimum height.
				fillWithEmptyRows(myOuterPanel, 1, 2);
			};

			public void handleSuccess(IdentityConfiguration aResult) {
				myIdentityConfiguration = aResult;
				if (myIdentityConfiguration != null) {
					// Set the initial values.
					myUserNameCompletionStateSelector.setValue(myIdentityConfiguration.isUserCompletionEnabled(), false);
					if (myIdentityConfiguration.isUserCompletionEnabled()) {
						myOuterPanel.setHTML(1, 0, constants.deactivateUserNameCompletion());
					} else {
						myOuterPanel.setHTML(1, 0, constants.activateUserNameCompletion());
					}

					// layout.
					myOuterPanel.setWidget(1, 1, myUserNameCompletionStateSelector);

				} else {
					// Ensure a minimum height.
					fillWithEmptyRows(myOuterPanel, 1, 2);
				}

			};
		});

		myOuterPanel.setWidget(2, 0, mySaveButton);
		myOuterPanel.getFlexCellFormatter().setColSpan(2, 0, 2);
		myOuterPanel.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);

		// Ensure a minimum height.
		fillWithEmptyRows(myOuterPanel, 3, 2);

	}

}
