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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.view.cases.CaseRecapViewerWidget;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.i18n.client.LocaleInfo;

/**
 * Widget allowing to display the instantiation values recap
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class InstantiationStepEditor extends CaseRecapViewerWidget {

    protected static final String FINISHED_DEPENDENT_STYLE_NAME = "finished";
    
    protected URLUtils urlUtils = URLUtilsFactory.getInstance();
    
    /**
     * 
     * Default constructor.
     * 
     * @param aDataSource
     * @param aStep
     * @param mustBeVisible
     */
    public InstantiationStepEditor(StepItemDataSource aDataSource, CaseItem aCase, CaseDataSource aCaseDataSource, ProcessDataSource aProcessDataSource) {
        super(aDataSource, aCase, aCaseDataSource, aProcessDataSource);
        formId = myCase.getProcessUUID().getValue()+"$view";
        myOuterPanel.removeStyleName("bos_case_recap_viewer");
        myOuterPanel.setStylePrimaryName("step_editor");
        myOuterPanel.addStyleName(CSSClassManager.ROUNDED_PANEL);
        myOuterPanel.addStyleDependentName(FINISHED_DEPENDENT_STYLE_NAME);
    }

    
    /**
     * Build the static part of the UI.
     */
    protected void initContent() {
        // Nothing to do here.
    }
    
    protected String buildProcessFormIFrame(String anApplicationURL, String aCredentialsParam) {
        final Map<String, List<String>> parametersMap = urlUtils.getURLParametersMap(anApplicationURL);
        final String url = urlUtils.removeURLparameters(anApplicationURL);
        
        StringBuilder processFormIFrame = new StringBuilder();
        processFormIFrame.append(url);
        processFormIFrame.append("?");
        processFormIFrame.append(URLUtils.LOCALE_PARAM);
        processFormIFrame.append("=");
        if (parametersMap.containsKey(URLUtils.LOCALE_PARAM)) {
            processFormIFrame.append(parametersMap.get(URLUtils.LOCALE_PARAM).get(0));
            parametersMap.remove(URLUtils.LOCALE_PARAM);
        } else {
            processFormIFrame.append(LocaleInfo.getCurrentLocale().getLocaleName());
        }
        if (BonitaConsole.userProfile.getDomain() != null) {
            processFormIFrame.append("&");
            processFormIFrame.append(URLUtils.DOMAIN_PARAM);
            processFormIFrame.append("=");
            if (parametersMap.containsKey(URLUtils.DOMAIN_PARAM)) {
                processFormIFrame.append(parametersMap.get(URLUtils.DOMAIN_PARAM).get(0));
                parametersMap.remove(URLUtils.DOMAIN_PARAM);
            } else {
                processFormIFrame.append(BonitaConsole.userProfile.getDomain());
            }
        }
        for (Entry<String, List<String>> parametersEntry : parametersMap.entrySet()) {
            processFormIFrame.append("&");
            processFormIFrame.append(parametersEntry.getKey());
            processFormIFrame.append("=");
            List<String> entryValues = parametersEntry.getValue();
            for (String value : entryValues) {
                processFormIFrame.append(value);
                processFormIFrame.append(",");
            }
            processFormIFrame.deleteCharAt(processFormIFrame.length() - 1);
        }
        
        processFormIFrame.append("#");
        processFormIFrame.append(URLUtils.FORM_ID);
        processFormIFrame.append("=");
        processFormIFrame.append(formId);
        processFormIFrame.append("&");
        processFormIFrame.append(URLUtils.VIEW_MODE_PARAM);
        processFormIFrame.append("=");
        processFormIFrame.append(URLUtils.FORM_ONLY_APPLICATION_MODE);
        processFormIFrame.append("&");
        processFormIFrame.append(URLUtils.INSTANCE_ID_PARAM);
        processFormIFrame.append("=");
        processFormIFrame.append(myCase.getUUID());
        processFormIFrame.append(aCredentialsParam);

        return processFormIFrame.toString();
    }

}
