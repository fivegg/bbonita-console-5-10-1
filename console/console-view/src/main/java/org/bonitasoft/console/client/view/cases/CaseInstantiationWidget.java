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
import java.util.Map;
import java.util.Map.Entry;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.ProcessRedirectWidget;
import org.bonitasoft.console.client.view.steps.FormPageFrame;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Widget allowing to display the instantiation form of a process
 * 
 * @author Anthony Birembaut, Nicolas Chabanoles
 * 
 */
public class CaseInstantiationWidget extends BonitaPanel {

    protected BonitaProcess myProcess;

    protected final FlowPanel myOuterPanel = new FlowPanel();
    protected final DecoratorPanel myInnerPanel = new DecoratorPanel();
    protected final FlowPanel myFirstRowPanel = new FlowPanel();
    protected final FlowPanel mySecondRowPanel = new FlowPanel();
    protected final FlowPanel myThirdRowPanel = new FlowPanel();

    protected URLUtils urlUtils = URLUtilsFactory.getInstance();

    protected String formId = null;

    /**
     * Constructor.
     * 
     * @param aCaseDataSource
     */
    public CaseInstantiationWidget() {
        super();

        initTable();

        this.initWidget(myOuterPanel);
        myOuterPanel.setStylePrimaryName("bos_case_instantiation");
    }

    /**
     * Build the static part of the UI.
     */
    private void initTable() {
        Hyperlink backToInbox = new Hyperlink(patterns.backToDestination(LabelModel.INBOX_LABEL.getUUID().toString()), ViewToken.CaseList.name() + "/lab:" + LabelModel.INBOX_LABEL.getUUID().getValue());
        backToInbox.setStyleName(CSSClassManager.BACK_TO_LINK_LABEL);
        backToInbox.getElement().setId("backToInboxButton");

        myFirstRowPanel.setStylePrimaryName("bos_first_row");
        mySecondRowPanel.setStylePrimaryName("bos_second_row");
        myThirdRowPanel.setStylePrimaryName("bos_third_row");

        final FlowPanel theContentWrapper = new FlowPanel();
        theContentWrapper.add(myFirstRowPanel);
        theContentWrapper.add(mySecondRowPanel);
        theContentWrapper.add(myThirdRowPanel);

        myInnerPanel.setStylePrimaryName("step_editor");
        myInnerPanel.addStyleName(CSSClassManager.ROUNDED_PANEL);

        myInnerPanel.add(theContentWrapper);

        myOuterPanel.add(backToInbox);
        myOuterPanel.add(myInnerPanel);
    }

    /**
     * Update the UI.
     */
    private void update() {
        myFirstRowPanel.clear();
        mySecondRowPanel.clear();
        myThirdRowPanel.clear();
        myFirstRowPanel.add(new ProcessRedirectWidget(myProcess));
        formId = myProcess.getUUID().getValue() + "$entry";
        String theApplicationURL = myProcess.getApplicationUrl();
        if (theApplicationURL == null) {
            theApplicationURL = "";
        }
        if (BonitaConsole.userProfile.useCredentialTransmission()) {
            RpcConsoleServices.getLoginService().generateTemporaryToken(new GetTokenAsyncCallback(theApplicationURL));
        } else {
            String theProcessFormIFrame = buildProcessFormIFrame(theApplicationURL, "");
            insertFormIFrame(theProcessFormIFrame);
        }
    }

    protected class GetTokenAsyncCallback implements AsyncCallback<String> {

        protected String myApplicationURL;

        public GetTokenAsyncCallback(String anApplicationURL) {
            this.myApplicationURL = anApplicationURL;
        }

        public void onSuccess(String temporaryToken) {
            String theCredentialsParam = "&" + URLUtils.USER_CREDENTIALS_PARAM + "=" + temporaryToken;
            String theProcessFormIFrame = buildProcessFormIFrame(myApplicationURL, theCredentialsParam);
            insertFormIFrame(theProcessFormIFrame);
        }

        public void onFailure(Throwable t) {
            String theProcessFormIFrame = buildProcessFormIFrame(myApplicationURL, "");
            insertFormIFrame(theProcessFormIFrame);
        }

    }

    public void insertFormIFrame(String anIFrameURL) {
        final Frame theIFrame = new FormPageFrame();
        theIFrame.setStyleName("form_view_frame");
        final Element theElement = theIFrame.getElement();
        theElement.setId(myProcess.getUUID().getValue());
        theElement.setAttribute("frameBorder", "0");
        theElement.setAttribute("allowTransparency", "true");
        theIFrame.setUrl(anIFrameURL);
        mySecondRowPanel.add(theIFrame);
    }

    private String buildProcessFormIFrame(String anApplicationURL, String aCredentialsParam) {
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
            if (!URLUtils.AUTO_INSTANTIATE.equals(parametersEntry.getKey())) {
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
        processFormIFrame.append(URLUtils.PROCESS_ID_PARAM);
        processFormIFrame.append("=");
        processFormIFrame.append(myProcess.getUUID().getValue());
        processFormIFrame.append("&");
        processFormIFrame.append(URLUtils.AUTO_INSTANTIATE);
        processFormIFrame.append("=");
        if (parametersMap.containsKey(URLUtils.AUTO_INSTANTIATE)) {
            processFormIFrame.append(parametersMap.get(URLUtils.AUTO_INSTANTIATE).get(0));
        } else {
            processFormIFrame.append("false");
        }
        processFormIFrame.append(aCredentialsParam);

        return processFormIFrame.toString();
    }

    public void setProcess(BonitaProcess aProcess) {
        myProcess = aProcess;
        if (myProcess != null) {
            update();
        }
    }

    @Override
    public String getLocationLabel() {
        if (myProcess != null) {
            return patterns.startCase(myProcess.getDisplayName());
        } else {
            return "";
        }
    }
}
