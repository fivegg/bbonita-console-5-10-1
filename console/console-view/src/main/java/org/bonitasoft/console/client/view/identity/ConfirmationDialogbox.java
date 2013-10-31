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
package org.bonitasoft.console.client.view.identity;

import org.bonitasoft.console.client.view.CustomDialogBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Haojie Yuan
 * 
 */
public class ConfirmationDialogbox extends CustomDialogBox {

    protected final VerticalPanel container = new VerticalPanel();
    protected final VerticalPanel dialogboxBody = new VerticalPanel();
    protected final Label displayingMessage = new Label();
    protected final HorizontalPanel buttonPanel = new HorizontalPanel();
    protected final Button okButton = new Button();
    protected final Button cancelButton = new Button();
    

    protected Boolean confirmation = false;

    /**
     * This is a dialog that requires a confirmation (CustomDialogbox) when deleting one or several users in the admin view.
     * 
     * @param dialogCaption
     * @param dialogMessage
     * @param okButtonCaption
     * @param cancelButtonCaption
     */
    public ConfirmationDialogbox(final String dialogCaption, final String dialogMessage, final String okButtonCaption, final String cancelButtonCaption) {
        super();

        this.initConfirmationDialogbox();

        this.setDialogCaption(dialogCaption);
        this.setDialogMessage(dialogMessage);
        this.setOkButtonText(okButtonCaption);
        this.setCancelButtonText(cancelButtonCaption);

        this.setFocusOnOkButton(true);
        this.center();
    }

    /**
     * 
     */
    public ConfirmationDialogbox() {
        super();
        this.initConfirmationDialogbox();
        this.center();
    }

    /**
     * 
     */
    protected void initConfirmationDialogbox() {
        displayingMessage.setStylePrimaryName("confirmation_dialog_box_text");
        displayingMessage.setVisible(true);
        dialogboxBody.setStyleName("confirmation_dialog_box_body");
        dialogboxBody.add(displayingMessage);
        
        okButton.setStyleName("delete_ok_button");
        okButton.setVisible(true);
        addOkButtonClickHandler();
        
        cancelButton.setStyleName("delete_cancel_button");
        cancelButton.setVisible(true);
        addCancelButtonClickHandler();

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setCellHorizontalAlignment(okButton, HasHorizontalAlignment.ALIGN_RIGHT);
        buttonPanel.addStyleName("confirmation_hp");

        container.add(dialogboxBody);
        container.add(buttonPanel);
        container.setCellHorizontalAlignment(buttonPanel, HasHorizontalAlignment.ALIGN_CENTER);
        container.setVisible(true);

        setWidget(container);
    }

    /**
     * 
     */
    protected void addCancelButtonClickHandler() {
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
    }

    /**
     * 
     */
    protected void addOkButtonClickHandler() {
        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                confirmation = true;
                hide();
            }
        });
    }

    /**
     * @param caption dialog box caption
     */
    public void setDialogCaption(final String caption) {
        setText(caption);
    }

    /**
     * @param message dialog box message
     */
    public void setDialogMessage(final String message) {
        displayingMessage.setText(message);
    }

    /**
     * @param okButtonCaption Ok button displaying text
     */
    public void setOkButtonText(final String okButtonCaption) {
        okButton.setText(okButtonCaption);
    }

    /**
     * @param cancelButtonCaption Cancel button displaying text
     */
    public void setCancelButtonText(final String cancelButtonCaption) {
        cancelButton.setText(cancelButtonCaption);
    }

    /**
     * @return the confirmation
     */
    public Boolean getConfirmation() {
        return confirmation;
    }
    
    /**
     * Set the focus on the OK button
     * @param focused true to set the focus
     */
    public void setFocusOnOkButton(boolean focused) {
        okButton.setFocus(focused);
    }
    
    public void addWidgetToDialogboxBody(Widget aWidget){
        this.dialogboxBody.add(aWidget);
    }
}
