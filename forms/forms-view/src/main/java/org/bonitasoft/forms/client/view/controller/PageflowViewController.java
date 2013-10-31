/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.forms.client.view.controller;

import java.util.Map;

import org.bonitasoft.console.security.client.LoginServiceAsync;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.client.view.common.RpcSecurityServices;
import org.bonitasoft.forms.client.i18n.FormsResourceBundle;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.FormURLComponents;
import org.bonitasoft.forms.client.model.exception.CanceledFormException;
import org.bonitasoft.forms.client.model.exception.ForbiddenFormAccessException;
import org.bonitasoft.forms.client.model.exception.FormAlreadySubmittedException;
import org.bonitasoft.forms.client.model.exception.FormInErrorException;
import org.bonitasoft.forms.client.model.exception.IllegalActivityTypeException;
import org.bonitasoft.forms.client.model.exception.MigrationProductVersionNotIdenticalException;
import org.bonitasoft.forms.client.model.exception.SessionTimeOutException;
import org.bonitasoft.forms.client.model.exception.SkippedFormException;
import org.bonitasoft.forms.client.model.exception.SuspendedFormException;
import org.bonitasoft.forms.client.rpc.FormsServiceAsync;
import org.bonitasoft.forms.client.view.common.DOMUtils;
import org.bonitasoft.forms.client.view.common.RpcFormsServices;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Controller dealing with Task form controls before display
 * 
 * @author Ruiheng Fan
 */
public class PageflowViewController {

    /**
     * login service
     */
    protected LoginServiceAsync loginServiceAsync;

    /**
     * forms RPC service
     */
    protected FormsServiceAsync formsServiceAsync;

    /**
     * Pages view controller (handles the page flow)
     */
    protected FormPagesViewController formPagesViewController;

    /**
     * Utility Class form DOM manipulation
     */
    protected DOMUtils domUtils = DOMUtils.getInstance();

    /**
     * Utility Class form URL manipulation
     */
    protected URLUtils urlUtils = URLUtilsFactory.getInstance();

    /**
     * mandatory form field symbol
     */
    protected String mandatoryFieldSymbol;

    /**
     * mandatory form field label
     */
    protected String mandatoryFieldLabel;

    /**
     * mandatory form field symbol classes
     */
    protected String mandatoryFieldClasses;

    /**
     * The formID UUID retrieved from the request as a String
     */
    protected String formID;

    /**
     * Application template panel (can be null in form only mode)
     */
    protected HTMLPanel applicationHTMLPanel;

    /**
     * The logged in user
     */
    protected User user;

    /**
     * Handler allowing to retrieve the first page
     */
    protected FirstPageHandler firstPageHandler = new FirstPageHandler();

    /**
     * Handler allowing to display the next form
     */
    protected NextFormHandler nextFormHandler = new NextFormHandler();

    /**
     * Handler to deal with what happens after a form has been submitted or skipped
     */
    protected FormTerminationHandler formTerminationHandler = new FormTerminationHandler();

    /**
     * The context of URL parameters
     */
    protected Map<String, Object> urlContext;

    /**
     * Id of the element in which to insert the page
     */
    protected String elementId;

    /**
     * Constructor
     * 
     * @param formID
     * @param urlContext
     * @param user
     * @param applicationHTMLPanel
     */
    public PageflowViewController(String formID, Map<String, Object> urlContext, User user, String elementId, HTMLPanel applicationHTMLPanel) {
        this.formID = formID;
        this.urlContext = urlContext;
        this.user = user;
        this.elementId = elementId;
        this.applicationHTMLPanel = applicationHTMLPanel;
        loginServiceAsync = RpcSecurityServices.getLoginService();
        formsServiceAsync = RpcFormsServices.getFormsService();
    }

    /**
     * create the view for the form
     */
    public void createForm() {
        formsServiceAsync.getFormFirstPage(formID, urlContext, firstPageHandler);
    }

    /**
     * Handler allowing to retrieve the page list
     */
    protected class FirstPageHandler implements AsyncCallback<FormPage> {

        /**
         * {@inheritDoc}
         */
        public void onFailure(final Throwable t) {

            if (t instanceof ForbiddenFormAccessException) {
                String errorMessage;
                if (urlContext.containsKey(URLUtils.PROCESS_ID_PARAM)) {
                    errorMessage = FormsResourceBundle.getMessages().forbiddenProcessStartMessage();
                } else {
                    errorMessage = FormsResourceBundle.getMessages().forbiddenStepReadMessage();
                }
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            } else if (t instanceof MigrationProductVersionNotIdenticalException) {
                final String errorMessage = FormsResourceBundle.getMessages().migrationProductVersionMessage();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            } else if (t instanceof CanceledFormException) {
                final String errorMessage = FormsResourceBundle.getMessages().cancelledTaskMessage();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            } else if (t instanceof SuspendedFormException) {
                final String errorMessage = FormsResourceBundle.getMessages().suspendedTaskMessage();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            } else if (t instanceof FormInErrorException) {
                final String errorMessage = FormsResourceBundle.getMessages().errorTaskMessage();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            } else if (t instanceof SkippedFormException) {
                final String errorMessage = FormsResourceBundle.getMessages().skippedFormMessage();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            } else if (t instanceof FormAlreadySubmittedException) {
                final String errorMessage = FormsResourceBundle.getErrors().formAlreadySubmittedError();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            } else if (t instanceof SessionTimeOutException) {
                Window.Location.reload();
            } else {
                final String errorMessage = FormsResourceBundle.getErrors().pageListRetrievalError();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onSuccess(final FormPage firstPage) {

            if (firstPage != null) {
                try {
                    RequestBuilder theRequestBuilder;
                    final String theURL = urlUtils.buildLayoutURL(firstPage.getPageTemplate().getBodyContentId(),
                            (String) urlContext.get(URLUtils.TASK_ID_PARAM), true);
                    GWT.log("Calling the Form Layout Download Servlet with query: " + theURL);
                    theRequestBuilder = new RequestBuilder(RequestBuilder.GET, theURL);
                    theRequestBuilder.setCallback(new RequestCallback() {

                        public void onError(Request aRequest, Throwable anException) {
                            final String errorMessage = FormsResourceBundle.getErrors().applicationConfigRetrievalError();
                            formsServiceAsync.getApplicationErrorTemplate(firstPage.getPageId(), urlContext, new ErrorPageHandler(null, firstPage.getPageId(),
                                    errorMessage, anException, elementId));
                        }

                        public void onResponseReceived(Request aRequest, Response aResponse) {

                            firstPage.getPageTemplate().setBodyContent(aResponse.getText());
                            formPagesViewController = FormViewControllerFactory.getFormPagesViewController(formID, urlContext, firstPage, applicationHTMLPanel,
                                    user, elementId);
                            formPagesViewController.setMandatoryFieldSymbol(mandatoryFieldSymbol);
                            formPagesViewController.setMandatoryFieldLabel(mandatoryFieldLabel);
                            formPagesViewController.setMandatoryFieldClasses(mandatoryFieldClasses);
                            formPagesViewController.displayPage(0);
                        }
                    });
                    theRequestBuilder.send();
                } catch (Exception e) {
                    Window.alert("Error while trying to query the form layout :" + e.getMessage());
                }

            } else {
                final String processUUIDStr = (String) urlContext.get(URLUtils.PROCESS_ID_PARAM);
                final String instanceUUIDStr = (String) urlContext.get(URLUtils.INSTANCE_ID_PARAM);
                if (processUUIDStr != null) {
                    final String autoInstantiate = (String) urlContext.get(URLUtils.AUTO_INSTANTIATE);
                    // if the parameter autoInstanciate is set explicitly to false, the we skip the form
                    if (!Boolean.FALSE.toString().equals(autoInstantiate)) {
                        formsServiceAsync.skipForm(formID, urlContext, formTerminationHandler);
                    } else {
                        final FlowPanel buttonContainer = new FlowPanel();
                        buttonContainer.setStyleName("bonita_form_button_container");
                        final Button startInstanceButton = new Button(FormsResourceBundle.getMessages().caseStartButtonLabel());
                        startInstanceButton.setStyleName("bonita_form_button");
                        startInstanceButton.addClickHandler(new ClickHandler() {

                            public void onClick(final ClickEvent event) {
                                startInstanceButton.setEnabled(false);
                                formsServiceAsync.skipForm(formID, urlContext, formTerminationHandler);
                            }
                        });
                        buttonContainer.add(startInstanceButton);
                        domUtils.hideLoading();
                        if (applicationHTMLPanel != null) {
                            applicationHTMLPanel.add(buttonContainer, DOMUtils.DEFAULT_FORM_ELEMENT_ID);
                        } else {
                            RootPanel.get(DOMUtils.STATIC_CONTENT_ELEMENT_ID).add(buttonContainer);
                        }
                    }
                } else if (instanceUUIDStr != null) {
                    final String errorMessage = FormsResourceBundle.getErrors().nothingToDisplay();
                    formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage,
                            elementId));
                } else {
                    formsServiceAsync.skipForm(formID, urlContext, formTerminationHandler);
                }
            }
        }
    }



    /**
     * Handler to deal with what happens after a form has been submitted or skipped
     */
    protected class FormTerminationHandler implements AsyncCallback<Map<String, Object>> {

        /**
         * {@inheritDoc}
         */
        public void onFailure(final Throwable t) {

            if (t instanceof IllegalActivityTypeException) {
                final String errorMessage = FormsResourceBundle.getErrors().taskFormSkippedError();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            } else if (t instanceof FormAlreadySubmittedException) {
                final String errorMessage = FormsResourceBundle.getErrors().formAlreadySubmittedOrCanceledError();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, elementId));
            } else if (t instanceof SessionTimeOutException) {
                Window.Location.reload();
            } else {
                final String errorMessage = FormsResourceBundle.getErrors().taskExecutionError();
                formsServiceAsync.getApplicationErrorTemplate(formID, urlContext,
                        new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, t, elementId));
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onSuccess(final Map<String, Object> newContext) {
            urlContext.putAll(newContext);
            String instanceUUIDStr = (String) newContext.get(URLUtils.INSTANCE_ID_PARAM);
            if (instanceUUIDStr != null) {
                if (!domUtils.consoleRedirectToCase(instanceUUIDStr)) {
                    formsServiceAsync.getNextFormURL(formID, urlContext, nextFormHandler);
                }
            } else {
                formsServiceAsync.getNextFormURL(formID, urlContext, nextFormHandler);
            }
        }
    }

    /**
     * Handler allowing to display the next task
     */
    protected class NextFormHandler implements AsyncCallback<FormURLComponents> {

        /**
         * {@inheritDoc}
         */
        public void onFailure(final Throwable t) {

            if (t instanceof SessionTimeOutException) {
                Window.Location.reload();
            } else {
                final String errorMessage = FormsResourceBundle.getErrors().nextTaskRetrievalError();
                formsServiceAsync.getApplicationErrorTemplate(errorMessage, urlContext, new ErrorPageHandler(applicationHTMLPanel, formID, errorMessage, t,
                        elementId));
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onSuccess(final FormURLComponents nextFormIDURL) {
            if (nextFormIDURL != null) {
                final String applicationURL = nextFormIDURL.getApplicationURL();
                final Map<String, Object> urlContext = nextFormIDURL.getUrlContext();
                if (user.isAutoLogin()) {
                    urlContext.put(URLUtils.AUTO_LOGIN_PARAM, true);
                }
                if (applicationURL != null) {
                    if (user.useCredentialTransmission() && !applicationURL.startsWith(URLUtils.HOMEPAGE_SERVLET_ID_IN_PATH)) {
                        loginServiceAsync.generateTemporaryToken(new GetTokenAsyncHandler(applicationURL, urlContext));
                    } else {
                        final String url = urlUtils.getFormRedirectionUrl(applicationURL, urlContext);
                        if (domUtils.isPageInFrame()) {
                            urlUtils.frameRedirect(DOMUtils.DEFAULT_FORM_ELEMENT_ID, url);
                        } else {
                            urlUtils.windowAssign(url);
                        }
                    }
                } else {
                    final String hash = urlUtils.getFormRedirectionHash(urlContext);
                    History.newItem(hash);
                }
            } else {
                if (!domUtils.consoleRefreshAndRedirectToInbox()) {
                    final String defaultConfirmationMessage = FormsResourceBundle.getMessages().submissionConfirmationMessage();
                    formsServiceAsync.getFormConfirmationTemplate(formID, urlContext, new ConfirmationPageHandler(applicationHTMLPanel, elementId,
                            defaultConfirmationMessage, formID, urlContext));
                }
            }
        }
    }

    public void setMandatoryFieldSymbol(final String mandatoryFieldSymbol) {
        this.mandatoryFieldSymbol = mandatoryFieldSymbol;
    }

    public void setMandatoryFieldClasses(final String mandatoryFieldClasses) {
        this.mandatoryFieldClasses = mandatoryFieldClasses;
    }

    public void setMandatoryFieldLabel(final String mandatoryFieldLabel) {
        this.mandatoryFieldLabel = mandatoryFieldLabel;
    }

}
