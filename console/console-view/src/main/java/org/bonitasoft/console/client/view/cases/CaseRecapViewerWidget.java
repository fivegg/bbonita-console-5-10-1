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
package org.bonitasoft.console.client.view.cases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.steps.FormPageFrame;
import org.bonitasoft.forms.client.view.common.DOMUtils;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * Widget allowing to display the current state of a Case in a recap.<br>
 * Corresponds to the URL ?instance=&lgt;ProcessInstanceUUID&gt;&recap=true
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class CaseRecapViewerWidget extends BonitaPanel implements ModelChangeListener {

    protected final ProcessDataSource myProcessDataSource;
    protected final CaseDataSource myCaseDataSource;
    protected final StepItemDataSource myStepDataSource;

    protected final DecoratorPanel myOuterPanel = new DecoratorPanel();
    protected final FlowPanel myInnerPanel = new FlowPanel();
    protected final FlowPanel myFirstRowPanel = new FlowPanel();
    protected final FlowPanel mySecondRowPanel = new FlowPanel();
    protected final FlowPanel myThirdRowPanel = new FlowPanel();

    protected Frame myIFrame;

    protected CaseItem myCase;
    protected BonitaProcess myProcess;

    protected boolean isOverviewVisible = true;
    protected boolean mustRefresh;

    protected URLUtils urlUtils = URLUtilsFactory.getInstance();
    protected String formId;

    /**
     * 
     * Default constructor.
     * 
     * @param aDataSource
     * @param aStep
     * @param mustBeVisible
     */
    public CaseRecapViewerWidget(StepItemDataSource aDataSource, CaseItem aCase, CaseDataSource aCaseDataSource, ProcessDataSource aProcessDataSource) {
        super();
        myStepDataSource = aDataSource;
        myCaseDataSource = aCaseDataSource;
        myProcessDataSource = aProcessDataSource;
        myCase = aCase;
        formId = myCase.getProcessUUID().getValue() + "$recap";
        myInnerPanel.add(myFirstRowPanel);
        myInnerPanel.add(mySecondRowPanel);
        myInnerPanel.add(myThirdRowPanel);
        myOuterPanel.add(myInnerPanel);

        myFirstRowPanel.setStylePrimaryName("bos_first_row");
        mySecondRowPanel.setStylePrimaryName("bos_second_row");
        myThirdRowPanel.setStylePrimaryName("bos_third_row");
        myInnerPanel.setStylePrimaryName("bos_case_recap_viewer_inner");
        myOuterPanel.setStylePrimaryName("bos_case_recap_viewer");
        myOuterPanel.addStyleName(CSSClassManager.ROUNDED_PANEL);
        this.initWidget(myOuterPanel);

        myProcessDataSource.getItem(myCase.getProcessUUID(), new AsyncHandler<BonitaProcess>() {
            public void handleFailure(Throwable aT) {
                // Do nothing.
                GWT.log("Unable to get the process definition:", aT);
            }

            public void handleSuccess(BonitaProcess aResult) {
                myProcess = aResult;
                initContent();
                update();
            }
        });
    }

    /**
     * Build the static part of the UI.
     */
    protected void initContent() {

        // Case initiator will not change.
        final String theCaseInitiator = constants.caseStartedBy() + myCase.getStartedBy().getValue();
        final String theProcessDescription = myProcess.getProcessDescription();
        final String theShortDesc;
        if (theProcessDescription.length() > 50) {
            theShortDesc = theProcessDescription.substring(0, 47) + "...";
        } else {
            theShortDesc = theProcessDescription;
        }

        final Grid theWrapper = new Grid(1, 3);
        theWrapper.setHTML(0, 0, theCaseInitiator);
        theWrapper.setHTML(0, 1, DateTimeFormat.getFormat(constants.dateShortFormat()).format(myCase.getLastUpdateDate()));
        theWrapper.setHTML(0, 2, theShortDesc);
        myFirstRowPanel.add(theWrapper);
        theWrapper.addStyleName("bos_step_descriptor");

        // Create click handler for user interaction.
        theWrapper.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent anEvent) {
                final Cell theCell = theWrapper.getCellForEvent(anEvent);
                if (theCell != null) {
                    toggleOverviewVisibility();
                }
            }
        });

    }

    public void refresh() {
        if (isOverviewVisible) {
            try {
                if (DOMUtils.getInstance().isInternetExplorer()) {
                    update();
                } else {
                    if (myIFrame != null) {
                        myIFrame.setUrl(cleanURL(myIFrame.getUrl()));
                    }
                }

            } catch (Exception e) {
                Window.alert("Unable to refresh the Case recap! " + e.getMessage());
            }
        }
        mustRefresh = false;
    }

    /**
     * @param aUrl
     * @return
     */
    protected String cleanURL(String anUrl) {
        final ArrayList<String> paramsToRemove = new ArrayList<String>();
        paramsToRemove.add(URLUtils.USER_CREDENTIALS_PARAM);
        final ArrayList<String> hashToRemove = new ArrayList<String>();
        hashToRemove.add(URLUtils.USER_CREDENTIALS_PARAM);
        return URLUtils.getInstance().rebuildUrl(anUrl, paramsToRemove, null, hashToRemove, null);
    }

    /**
     * Update the UI.
     */
    protected void update() {
        // Either the form content is available or it will be computed
        // asynchronously.
        if (isOverviewVisible) {
            // We already have the content to display.
            buildIframeAndInsertIt(myProcess.getApplicationUrl());
        } else {
            mySecondRowPanel.clear();
        }
    }

    protected void buildIframeAndInsertIt(final String anApplicationURL) {

        String theApplicationURL = anApplicationURL;
        if (theApplicationURL == null) {
            theApplicationURL = "";
        }
        if (BonitaConsole.userProfile.useCredentialTransmission()) {
            RpcConsoleServices.getLoginService().generateTemporaryToken(new GetTokenAsyncCallback(theApplicationURL));
        } else {
            insertFormIFrame(buildProcessFormIFrame(theApplicationURL, ""));
        }
    }

    public void insertFormIFrame(final String aFormIFrame) {
        myIFrame = new FormPageFrame();
        myIFrame.setStyleName("form_view_frame");
        final Element theElement = myIFrame.getElement();
        theElement.setId(formId);
        theElement.setAttribute("frameBorder", "0");
        theElement.setAttribute("allowTransparency", "true");
        myIFrame.setUrl(aFormIFrame);
        mySecondRowPanel.add(myIFrame);
    }

    protected class GetTokenAsyncCallback implements AsyncCallback<String> {

        protected String myApplicationURL;

        public GetTokenAsyncCallback(String anApplicationURL) {
            this.myApplicationURL = anApplicationURL;
        }

        public void onSuccess(String temporaryToken) {
            String theCredentialsParam = "&" + URLUtils.USER_CREDENTIALS_PARAM + "=" + temporaryToken;
            insertFormIFrame(buildProcessFormIFrame(myApplicationURL, theCredentialsParam));
        }

        public void onFailure(Throwable t) {
            insertFormIFrame(buildProcessFormIFrame(myApplicationURL, ""));
        }

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
        processFormIFrame.append("&");
        processFormIFrame.append(URLUtils.RECAP_PARAM);
        processFormIFrame.append("=");
        processFormIFrame.append("true");
        processFormIFrame.append(aCredentialsParam);

        return processFormIFrame.toString();
    }

    /*
     * Switch visibility of the overview.
     */
    void toggleOverviewVisibility() {
        isOverviewVisible = !isOverviewVisible;
        update();
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.beans.ModelChangeListener#propertyChange(java.beans. PropertyChangeEvent)
     */
    public void modelChange(ModelChangeEvent anEvt) {
        update();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Composite#onDetach()
     */
    @Override
    protected void onDetach() {
        super.onDetach();
        mustRefresh = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        if (mustRefresh) {
            refresh();
        }
    }

}
