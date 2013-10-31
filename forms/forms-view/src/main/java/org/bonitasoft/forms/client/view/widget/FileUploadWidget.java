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
package org.bonitasoft.forms.client.view.widget;

import java.util.Map;

import org.bonitasoft.forms.client.i18n.FormsResourceBundle;
import org.bonitasoft.forms.client.view.common.RpcFormsServices;

import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Widget displaying either a file upload input if its initial value is null or a download link and file upload input to overide the current file
 * 
 * @author Anthony Birembaut
 */
public class FileUploadWidget extends Composite {

    /**
     * the flow panel used to display the widgets
     */
    protected FlowPanel flowPanel;
    
    /**
     * the form panel used to display the widget
     */
    protected FormPanel formPanel;
    
    protected FlowPanel buttonPanel;
    
    protected Label uploadLabel;
    
    protected Label cancelLabel;
    
    protected Label modifyLabel;
    
    protected Label removeLabel;
    
    protected FileDownloadWidget fileDownloadWidget;
    
    protected FileUpload fileUpload;
    
    protected Image loadingImage;
    
    protected String uploadedFilePath;
    
    protected String attachmentName;

    /**
     * Constructor
     * @param contextMap 
     * 
     * @param taskUUIDStr
     * @param processUUIDStr
     * @param instanceUUIDStr
     * @param fileName
     * @param hasImagePreview
     */
    public FileUploadWidget(final String formID, Map<String, Object> contextMap, final boolean isCurrentValue, final String fieldId, final String attachmentExpression, final String fileName, final boolean hasImagePreview) {
        
        if (attachmentExpression != null) {
            if (attachmentExpression.matches("\\$\\{.*\\}")) {
                this.attachmentName = attachmentExpression.substring(2, attachmentExpression.length() - 1);
            } else {
                this.attachmentName = attachmentExpression;
            }
        } else {
            this.attachmentName = fieldId;
        }
        
        flowPanel = new FlowPanel();
        
        createFileUploadForm(this.attachmentName);
        
        fileDownloadWidget = new FileDownloadWidget(formID, contextMap, isCurrentValue, this.attachmentName, hasImagePreview);
        
        loadingImage = new Image("images/ajax-loader.gif");
        loadingImage.setTitle(FormsResourceBundle.getMessages().uploadingLabel());
        
        buttonPanel = new FlowPanel();
        
        uploadLabel = new Label();
        uploadLabel.setText(FormsResourceBundle.getMessages().uploadButtonLabel());
        uploadLabel.setTitle(FormsResourceBundle.getMessages().uploadButtonTitle());
        uploadLabel.setStyleName("bonita_upload_button");
        cancelLabel = new Label();
        cancelLabel.setText(FormsResourceBundle.getMessages().cancelButtonLabel());
        cancelLabel.setTitle(FormsResourceBundle.getMessages().cancelButtonTitle());
        cancelLabel.setStyleName("bonita_upload_button");
        modifyLabel = new Label();
        modifyLabel.setText(FormsResourceBundle.getMessages().modifyButtonLabel());
        modifyLabel.setTitle(FormsResourceBundle.getMessages().modifyButtonTitle());
        modifyLabel.setStyleName("bonita_upload_button");
        removeLabel = new Label();
        removeLabel.setText(FormsResourceBundle.getMessages().removeButtonLabel());
        removeLabel.setTitle(FormsResourceBundle.getMessages().removeButtonTitle());
        removeLabel.setStyleName("bonita_upload_button");
        
        buttonPanel.add(uploadLabel);
        buttonPanel.add(cancelLabel);
        buttonPanel.add(modifyLabel);
        buttonPanel.add(removeLabel);
        
        buttonPanel.addStyleName("bonita_upload_button_group");
        
        loadingImage.setVisible(false);
        if (fileName != null) {
            uploadedFilePath = fileName;
            fileDownloadWidget.setFileName(fileName);
            formPanel.setVisible(false);
            uploadLabel.setVisible(false);
        } else {
            fileDownloadWidget.setVisible(false);
            modifyLabel.setVisible(false);
            removeLabel.setVisible(false);
        }
        cancelLabel.setVisible(false);
        
        flowPanel.add(formPanel);
        flowPanel.add(loadingImage);
        flowPanel.add(fileDownloadWidget);
        flowPanel.add(buttonPanel);

        uploadLabel.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
                formPanel.submit();
            }
        });
        
        modifyLabel.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
                formPanel.setVisible(true);
                fileDownloadWidget.setVisible(false);
                modifyLabel.setVisible(false);
                removeLabel.setVisible(false);
                uploadLabel.setVisible(true);
                cancelLabel.setVisible(true);
            }
        });
        
        removeLabel.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
                formPanel.clear();
                uploadedFilePath = null;
                fileUpload = addFileUploalToFormPanel(attachmentName);
                formPanel.setVisible(true);
                fileDownloadWidget.setVisible(false);
                modifyLabel.setVisible(false);
                removeLabel.setVisible(false);
                uploadLabel.setVisible(true);
                cancelLabel.setVisible(false);
            }
        });
        
        cancelLabel.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
                formPanel.setVisible(false);
                fileDownloadWidget.setVisible(true);
                modifyLabel.setVisible(true);
                removeLabel.setVisible(true);
                uploadLabel.setVisible(false);
                cancelLabel.setVisible(false);
            }
        });
        
        initWidget(flowPanel);
    }
    
    protected void createFileUploadForm(final String FileUloadName) {
        
        formPanel = new FormPanel();
        formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
        formPanel.setMethod(FormPanel.METHOD_POST);
        FormElement.as(formPanel.getElement()).setAcceptCharset("UTF-8");
        formPanel.setAction(RpcFormsServices.getFileUploadURL());
        fileUpload = addFileUploalToFormPanel(FileUloadName);
    }
    
    protected FileUpload addFileUploalToFormPanel(final String FileUloadName) {
        
        final FileUpload fileUpload = new FileUpload();
        fileUpload.setStyleName("bonita_file_upload");
        // mandatory
        fileUpload.setName(FileUloadName);
        formPanel.add(fileUpload);
        final UploadSubmitHandler uploadHandler = new UploadSubmitHandler();
        formPanel.addSubmitHandler(uploadHandler);
        formPanel.addSubmitCompleteHandler(uploadHandler);
        return fileUpload;
    }

    protected class UploadSubmitHandler implements SubmitHandler, SubmitCompleteHandler {
        
        protected String filePath;
        
        /**
         * {@inheritDoc}
         */
        public void onSubmit(final SubmitEvent event) {
            filePath = fileUpload.getFilename();
            if (filePath == null || filePath.length() == 0) {
                event.cancel();
            } else {
                formPanel.setVisible(false);
                loadingImage.setVisible(true);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onSubmitComplete(final SubmitCompleteEvent event) {
            String response = event.getResults();
            response = URL.decodeQueryString(response);
            response = response.replaceAll("&amp;", "&");
            response = response.replaceAll("&lt;", "<");
            response = response.replaceAll("&gt;", ">");
            uploadedFilePath = response;
            String realFileName = null;
            if (filePath.contains("\\")) {
                realFileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            } else if (filePath.contains("/")) {
                realFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            } else {
                realFileName = filePath;
            }
            final int divIndex = realFileName.indexOf("<div");
            if(divIndex > 0 ){
            	realFileName = realFileName.substring(0, divIndex);
            }
            loadingImage.setVisible(false);
            fileDownloadWidget.setFilePath(uploadedFilePath, realFileName);
            fileDownloadWidget.setVisible(true);
            modifyLabel.setVisible(true);
            removeLabel.setVisible(true);
            uploadLabel.setVisible(false);
            cancelLabel.setVisible(false);
        }
    }

    /**
     * @return the path to the uploaded file
     */
    public String getValue(){
        return uploadedFilePath;
    }
    
    /**
     * Disable the fileupload
     */
    public void disable() {
        flowPanel.remove(formPanel);
        flowPanel.remove(buttonPanel);
        fileDownloadWidget.setVisible(true);
    }
    
    public String getAttachmentName() {
        return attachmentName;
    }
}
