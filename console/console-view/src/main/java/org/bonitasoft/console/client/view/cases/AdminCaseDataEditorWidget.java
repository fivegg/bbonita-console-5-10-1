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
package org.bonitasoft.console.client.view.cases;

import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.forms.client.i18n.FormsResourceBundle;
import org.bonitasoft.forms.client.model.exception.SessionTimeOutException;
import org.bonitasoft.forms.client.rpc.FormsServiceAsync;
import org.bonitasoft.forms.client.view.common.RpcFormsServices;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Widget allowing to diplay the admin view for variables modification
 * 
 * @author Anthony Birembaut
 */
public class AdminCaseDataEditorWidget extends BonitaPanel implements ModelChangeListener {

	protected final FlowPanel flowPanel = new FlowPanel();

	protected CaseItem caseItem;

	protected CaseDataSource caseDataSource;

	protected final FormsServiceAsync formsService = RpcFormsServices.getFormsService();

	protected CaseSelection caseSelection;

	protected MessageDataSource messageDataSource;

	/**
	 * Constructor
	 * 
	 * @param myCaseDataSource
	 * @param aStepDataSource
	 */
	public AdminCaseDataEditorWidget(CaseDataSource caseDataSource, MessageDataSource messageDataSource, CaseSelection caseSelection) {
		super();
		this.caseDataSource = caseDataSource;
		this.caseDataSource.addModelChangeListener(CaseDataSource.CASE_LIST_PROPERTY, this);
		this.caseSelection = caseSelection;
		this.messageDataSource = messageDataSource;
        flowPanel.setStyleName("admin_case_data_formwidget");
		initWidget(flowPanel);
	}

	/**
	 * Set the case to display
	 */
	@Override
	protected void onAttach() {
		List<CaseUUID> caseUUIDs = caseSelection.getSelectedItems();
		if (!caseUUIDs.isEmpty()) {
			CaseUUID caseUUID = caseUUIDs.get(0);
			CaseItem caseItem = caseDataSource.getItem(caseUUID);
			if (caseItem != this.caseItem) {
				if (this.caseItem != null) {
					this.caseItem.removeModelChangeListener(CaseItem.STEPS_PROPERTY, this);
				}
				// Update the reference of the Case currently edited.
				this.caseItem = caseItem;
				this.caseItem.addModelChangeListener(CaseItem.STEPS_PROPERTY, this);
			}
			// even if it is the same case, update the UI, as some values may have changed.
			update();
		} else {
			// notifySelectionWidgetChanged(WidgetKey.ADMIN_CASE_LIST_KEY);
			History.newItem(ViewToken.AdminCaseList.name());
		}
		super.onAttach();
	}

	/**
	 * Update the UI.
	 */
	protected void update() {
		flowPanel.clear();

		Hyperlink backToCases = new Hyperlink(constants.backToCases(), ViewToken.AdminCaseList.name());
		// backToCases.addClickHandler(new ClickHandler() {
		// public void onClick(ClickEvent arg0) {
		// notifySelectionWidgetChanged(WidgetKey.ADMIN_CASE_LIST_KEY);
		// }
		// });
		backToCases.setStyleName(CSSClassManager.BACK_TO_LINK_LABEL);
		flowPanel.add(backToCases);

		formsService.getProcessInstanceAdminPageList(caseItem.getUUID().getValue(), new PageListHandler(caseItem));
	}

	/**
	 * Handler allowing to retrieve the first page
	 */
	class PageListHandler implements AsyncCallback<List<String>> {

		private CaseItem caseItem;

		private StepItem stepItem;

		public PageListHandler(CaseItem caseItem) {
			this.caseItem = caseItem;
		}

		public PageListHandler(StepItem stepItem) {
			this.stepItem = stepItem;
		}

		/**
		 * {@inheritDoc}
		 */
		public void onFailure(final Throwable t) {

			if (t instanceof SessionTimeOutException) {
				Window.Location.reload();
			} else {
				String errorMessage = FormsResourceBundle.getErrors().pageListRetrievalError();
				messageDataSource.addErrorMessage(errorMessage);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void onSuccess(final List<String> pages) {

			if (!pages.isEmpty()) {
				AdminCaseDataFormWidget adminCaseDataFormWidget = null;
				if (caseItem != null) {
					adminCaseDataFormWidget = new AdminCaseDataFormWidget(caseItem, pages, messageDataSource);
					
				} else if (stepItem != null) {
					adminCaseDataFormWidget = new AdminCaseDataFormWidget(stepItem, pages, messageDataSource);
				}
				flowPanel.add(adminCaseDataFormWidget);
			}
			if (caseItem != null && stepItem == null) {
				for (StepItem stepItem : caseItem.getSteps()) {
					if (stepItem.getState().equals(StepState.READY)) {
						formsService.getTaskAdminPageList(stepItem.getUUID().getValue(), new PageListHandler(stepItem));
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void modelChange(ModelChangeEvent anEvent) {

		if (this.isAttached()) {
			if (CaseDataSource.CASE_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
				// notifySelectionWidgetChanged(WidgetKey.ADMIN_CASE_LIST_KEY);
				History.newItem(ViewToken.AdminCaseList.name());
			} else if (CaseItem.STEPS_PROPERTY.equals(anEvent.getPropertyName())) {
				update();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocationLabel() {
		return patterns.variablesEditionLabel(caseItem.getUUID().getValue());
	}
}
