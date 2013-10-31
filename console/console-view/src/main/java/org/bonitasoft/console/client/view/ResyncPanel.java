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
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.cases.CaseDataSource;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ResyncPanel extends SetupPanel {

	protected final FlowPanel myOuterPanel;
	protected final CustomMenuBar mySyncButton = new CustomMenuBar();
	protected final CaseDataSource myCaseDataSource;

	public ResyncPanel(final CaseDataSource aCaseDataSource, final MessageDataSource aMessageDataSource) {
		super(aMessageDataSource);
		myCaseDataSource = aCaseDataSource;
		myOuterPanel = new FlowPanel();
	    myOuterPanel.setStylePrimaryName(DEFAULT_CSS_STYLE);
		myOuterPanel.addStyleName("bos_syncdb_panel");
		initWidget(myOuterPanel);
	}

	protected void buildContent() {
		// Create the sync button.
		mySyncButton.addItem(constants.synchronize(), new Command() {
			public void execute() {
				synchronizeDBs();
			}
		});
		myOuterPanel.add(new HTML(constants.synchroTabDescription()));
    myOuterPanel.add(mySyncButton);
	}

	protected void synchronizeDBs() {
		displayLoading();
		myOuterPanel.addStyleName(LOADING_STYLE);
		myCaseDataSource.synchronizeDBs(new AsyncHandler<Void>() {
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

	@Override
	public void updateContent() {
		// Do nothing

	}

  @Override
  public String getLocationLabel() {
    return constants.synchro();
  }
}
