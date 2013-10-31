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
package org.bonitasoft.console.client.view.steps;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.RedirectButtonWidget;
import org.bonitasoft.forms.client.view.common.URLUtils;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Specialization of the RedirectButtonWidget to deal with a Step
 * 
 * @author Anthony Birembaut
 * @revision Nicolas Chabanoles Changed the outer panel from VerticalPanel to FlowPanel
 * @revision Nicolas Chabanoles Made the widget inherit from I18NComposite to allow externalization of strings.
 * 
 */
public class StepRedirectWidget extends BonitaPanel implements ModelChangeListener {

	/**
	 * name of the new window
	 */
	private static final String APPLICATION_WINDOW_NAME = constants.defaultApplicationFormWindowName();

	/**
	 * Redirect button
	 */
	private RedirectButtonWidget button;

	protected String myApplicationURL;
	protected final FlowPanel myOuterPanel = new FlowPanel();

	/**
	 * Default constructor
	 * 
	 * @param aStep
	 * @param aCaseDataSource
	 * @param aProcessDataSource
	 * @param formId 
	 */
    public StepRedirectWidget(final StepItem aStep, final CaseDataSource aCaseDataSource, final ProcessDataSource aProcessDataSource) {

        aStep.addModelChangeListener(StepItem.STATE_PROPERTY, this);

        final Map<String, String> urlHashParamsMap = new HashMap<String, String>();

        String formId = null;
        final boolean isTask = aStep.isTask();
        final boolean isTaskOver = aStep.getState().equals(StepState.FINISHED);
        boolean isEditMode = !isTaskOver && isTask;
        if (isEditMode) {
            formId = aStep.getUUID().getStepDefinitionUUID() + "$entry";
        } else {
            formId = aStep.getUUID().getStepDefinitionUUID() + "$view";
        }

        urlHashParamsMap.put(URLUtils.FORM_ID, formId);

        urlHashParamsMap.put(URLUtils.TASK_ID_PARAM, aStep.getUUID().getValue());

        final Map<String, String> urlParamsMap = new HashMap<String, String>();

        urlParamsMap.put(URLUtils.LOCALE_PARAM, LocaleInfo.getCurrentLocale().getLocaleName());

        if (BonitaConsole.userProfile.getDomain() != null) {
            urlParamsMap.put(URLUtils.DOMAIN_PARAM, BonitaConsole.userProfile.getDomain());
        }

        if (aStep.getApplicationURL() != null) {
            myApplicationURL = aStep.getApplicationURL();
            urlHashParamsMap.put(URLUtils.VIEW_MODE_PARAM, URLUtils.FULL_FORM_APPLICATION_MODE);
            button = new RedirectButtonWidget(myApplicationURL, APPLICATION_WINDOW_NAME, urlParamsMap, urlHashParamsMap, constants.redirectButtonTitle());
            myOuterPanel.add(button);
        } else {
            aProcessDataSource.getItem(aStep.getCase().getProcessUUID(), new AsyncHandler<BonitaProcess>() {
                public void handleFailure(Throwable anT) {
                    urlHashParamsMap.put(URLUtils.VIEW_MODE_PARAM, URLUtils.FULL_FORM_APPLICATION_MODE);
                    button = new RedirectButtonWidget(Window.Location.getPath(), APPLICATION_WINDOW_NAME, urlParamsMap, urlHashParamsMap, constants.redirectButtonTitle());
                    myOuterPanel.add(button);
                }

                public void handleSuccess(BonitaProcess aProcess) {
                    if (aProcess != null) {
                        myApplicationURL = aProcess.getApplicationUrl();
                    } else {
                        myApplicationURL = null;
                    }
                    if (myApplicationURL == null || "".equals(myApplicationURL)) {
                        urlHashParamsMap.put(URLUtils.VIEW_MODE_PARAM, URLUtils.FULL_FORM_APPLICATION_MODE);
                        button = new RedirectButtonWidget(Window.Location.getPath(), APPLICATION_WINDOW_NAME, urlParamsMap, urlHashParamsMap, constants.redirectButtonTitle());
                    } else {
                        urlHashParamsMap.put(URLUtils.VIEW_MODE_PARAM, URLUtils.FULL_FORM_APPLICATION_MODE);
                        button = new RedirectButtonWidget(myApplicationURL, APPLICATION_WINDOW_NAME, urlParamsMap, urlHashParamsMap, constants.redirectButtonTitle());
                    }

                    myOuterPanel.add(button);
                }

            });

        }
        initWidget(myOuterPanel);
        myOuterPanel.setStylePrimaryName("bos_step_redirect_widget");
    }

	/**
	 * {@inheritDoc}
	 */
	public void modelChange(ModelChangeEvent evt) {
		StepState stepState = (StepState) evt.getNewValue();
		if (StepState.READY != stepState) {
			if (button != null) {
				button.setVisible(false);
			}
		} else {
			if (button != null) {
				button.setVisible(true);
			}
		}
	}

}
