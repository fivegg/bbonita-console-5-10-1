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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.forms.client.view.controller;

import org.bonitasoft.forms.client.i18n.FormsResourceBundle;
import org.bonitasoft.forms.client.model.HtmlTemplate;
import org.bonitasoft.forms.client.model.exception.RPCException;
import org.bonitasoft.forms.client.model.exception.SessionTimeOutException;
import org.bonitasoft.forms.client.view.common.DOMUtils;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Handler allowing to display the error template
 * 
 * @author Anthony Birembaut , Ruiheng Fan
 * 
 */
public class ErrorPageHandler implements AsyncCallback<HtmlTemplate> {

    /**
     * Id of the message element on the error page
     */
    protected static final String ERROR_MESSAGE_ELEMENT_ID = "bonita_form_error_message";

    /**
     * Id of the message element on the error page
     */
    protected static final String CAUSE_MESSAGE_ELEMENT_ID = "bonita_form_cause_message";

    /**
     * Utility Class form DOM manipulation
     */
    protected DOMUtils domUtils;

    /**
     * process template panel (can be null in form only mode)
     */
    protected HTMLPanel applicationHTMLPanel;

    /**
     * Element Id
     */
    protected String formID;

    protected String elementId;

    /**
     * current page template panel
     */
    protected HTMLPanel currentPageHTMLPanel;

    /**
     * Error message
     */
    protected String errorMessage;

    /**
     * Cause message
     */
    protected String causeMessage;

    /**
     * Constructor.
     * 
     * @param applicationHTMLPanel
     * @param formID
     * @param currentPageHTMLPanel
     * @param errorMessage
     */
    public ErrorPageHandler(final HTMLPanel applicationHTMLPanel, final String formID, final HTMLPanel currentPageHTMLPanel, final String errorMessage, final String elementId) {
        this(applicationHTMLPanel, formID, errorMessage, elementId);
        this.currentPageHTMLPanel = currentPageHTMLPanel;
    }

    /**
     * Constructor.
     * 
     * @param applicationHTMLPanel
     * @param formID
     * @param errorMessage
     */
    public ErrorPageHandler(final HTMLPanel applicationHTMLPanel, final String formID, final String errorMessage, final String elementId) {

        domUtils = DOMUtils.getInstance();
        this.applicationHTMLPanel = applicationHTMLPanel;
        this.formID = formID;
        this.elementId = elementId;
        this.errorMessage = errorMessage;
        this.causeMessage = "";
    }

    /**
     * Constructor.
     * 
     * @param processHTMLPanel
     * @param formID
     * @param currentPageHTMLPanel
     * @param errorMessage
     * @param throwable
     */
    public ErrorPageHandler(final HTMLPanel applicationHTMLPanel, final String formID, final HTMLPanel currentPageHTMLPanel, final String errorMessage, final Throwable throwable, final String elementId) {
        this(applicationHTMLPanel, formID, errorMessage, throwable, elementId);
        this.currentPageHTMLPanel = currentPageHTMLPanel;
    }

    /**
     * Constructor.
     * 
     * @param applicationHTMLPanel
     * @param formID
     * @param errorMessage
     * @param throwable
     */
    public ErrorPageHandler(final HTMLPanel applicationHTMLPanel, final String formID, final String errorMessage, final Throwable throwable, final String elementId) {

        domUtils = DOMUtils.getInstance();
        this.applicationHTMLPanel = applicationHTMLPanel;
        this.formID = formID;
        this.elementId = elementId;
        this.errorMessage = errorMessage;
        if (throwable instanceof RPCException) {
            this.causeMessage = ((RPCException) throwable).getMessage();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onFailure(final Throwable t) {

        if (t instanceof SessionTimeOutException) {
            Window.Location.reload();
        } else {
            // Hide the loading message.
            DOM.getElementById("loading").getStyle().setProperty("display", "none");

            Window.alert(FormsResourceBundle.getErrors().errorTempateError() + t.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onSuccess(final HtmlTemplate result) {

        if (currentPageHTMLPanel != null) {
            applicationHTMLPanel.remove(currentPageHTMLPanel);
        }
        HTMLPanel pageHTMLPanel = new HTMLPanel(result.getBodyContent());
        final String onloadAttributeValue = domUtils.insertPageTemplate(result.getHeadNodes(), pageHTMLPanel, result.getBodyAttributes(), applicationHTMLPanel, elementId);
        domUtils.insertInElement(pageHTMLPanel, ERROR_MESSAGE_ELEMENT_ID, errorMessage);
        domUtils.insertInElement(pageHTMLPanel, CAUSE_MESSAGE_ELEMENT_ID, causeMessage);
        // Hide the loading message.
        
        Element loadingElement = DOM.getElementById("loading");
        if(loadingElement != null) {
            loadingElement.getStyle().setProperty("display", "none");
        }

        if (onloadAttributeValue != null) {
            domUtils.javascriptEval(onloadAttributeValue);
        }
    }
}
