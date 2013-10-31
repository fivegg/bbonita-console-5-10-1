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
package org.bonitasoft.forms.client.view.controller;

import java.util.Map;

import org.bonitasoft.forms.client.i18n.FormsResourceBundle;
import org.bonitasoft.forms.client.model.HtmlTemplate;
import org.bonitasoft.forms.client.model.exception.SessionTimeOutException;
import org.bonitasoft.forms.client.rpc.FormsServiceAsync;
import org.bonitasoft.forms.client.view.common.DOMUtils;
import org.bonitasoft.forms.client.view.common.RpcFormsServices;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Handler allowing to display the confirmation template
 * 
 * @author Anthony Birembaut
 * 
 */
class ConfirmationPageHandler implements AsyncCallback<HtmlTemplate> {

    /**
     * Id of the message element on the confirmation page
     */
    protected static final String CONFIRM_MESSAGE_ELEMENT_ID = "bonita_form_confirm_message";

    /**
     * Utility Class form DOM manipulation
     */
    protected DOMUtils formsTemplateUtils;

    /**
     * forms RPC service
     */
    protected FormsServiceAsync formsServiceAsync;

    /**
     * process template panel (can be null in form only mode)
     */
    protected HTMLPanel applicationHTMLPanel;

    /**
     * Element Id
     */
    protected String elementId;

    /**
     * current page template panel
     */
    protected HTMLPanel currentPageHTMLPanel;

    /**
     * Confirmation message
     */
    protected String defaultConfirmationMessage;

    /**
     * The form ID retrieved from the request as a String
     */
    protected String formId;

    protected Map<String, Object> urlContext;

    /**
     * Constructor.
     * 
     * @param applicationHTMLPanel
     * @param elementId
     * @param currentPageHTMLPanel
     * @param defaultConfirmationMessage
     * @param formId
     */
    public ConfirmationPageHandler(final HTMLPanel applicationHTMLPanel, final String elementId, final HTMLPanel currentPageHTMLPanel, final String defaultConfirmationMessage, final String formId, final Map<String, Object> urlContext) {
        this(applicationHTMLPanel, elementId, defaultConfirmationMessage, formId, urlContext);
        this.currentPageHTMLPanel = currentPageHTMLPanel;
    }

    /**
     * Constructor.
     * 
     * @param applicationHTMLPanel
     * @param elementId
     * @param defaultConfirmationMessage
     * @param formId
     */
    public ConfirmationPageHandler(final HTMLPanel applicationHTMLPanel, final String elementId, final String defaultConfirmationMessage, final String formId, final Map<String, Object> urlContext) {
        formsServiceAsync = RpcFormsServices.getFormsService();
        formsTemplateUtils = DOMUtils.getInstance();
        this.applicationHTMLPanel = applicationHTMLPanel;
        this.formId = formId;
        this.urlContext = urlContext;
        this.elementId = elementId;
        this.defaultConfirmationMessage = defaultConfirmationMessage;
    }

    /**
     * {@inheritDoc}
     */
    public void onFailure(final Throwable t) {

        if (t instanceof SessionTimeOutException) {
            Window.Location.reload();
        } else {
            final String errorMessage = FormsResourceBundle.getErrors().confirmationTempateError();
            formsServiceAsync.getApplicationErrorTemplate(formId, urlContext, new ErrorPageHandler(applicationHTMLPanel, formId, currentPageHTMLPanel, errorMessage, t, elementId));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onSuccess(final HtmlTemplate result) {

        String confirmMessage = result.getDynamicMessage();
        if (confirmMessage == null) {
            confirmMessage = defaultConfirmationMessage;
        }
        if (currentPageHTMLPanel != null && applicationHTMLPanel != null) {
            applicationHTMLPanel.remove(currentPageHTMLPanel);
        }
        HTMLPanel pageHTMLPanel = new HTMLPanel(result.getBodyContent());
        formsTemplateUtils.hideLoading();
        final String onloadAttributeValue = formsTemplateUtils.insertPageTemplate(result.getHeadNodes(), pageHTMLPanel, result.getBodyAttributes(), applicationHTMLPanel, elementId);
        formsTemplateUtils.insertInElement(applicationHTMLPanel, CONFIRM_MESSAGE_ELEMENT_ID, confirmMessage);
        if (onloadAttributeValue != null) {
            formsTemplateUtils.javascriptEval(onloadAttributeValue);
        }
    }
}
