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
package org.bonitasoft.console.client.view;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.events.FileUploadedHandler;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.view.ConsoleFormPanel.SubmitCompleteEvent;
import org.bonitasoft.console.client.view.ConsoleFormPanel.SubmitCompleteHandler;
import org.bonitasoft.console.client.view.ConsoleFormPanel.SubmitEvent;
import org.bonitasoft.console.client.view.ConsoleFormPanel.SubmitHandler;

import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class FileUploadWidget extends BonitaPanel {

    protected final FlowPanel theOuterPanel = new FlowPanel();
    protected final ConsoleFormPanel formPanel = new ConsoleFormPanel();
    protected final VerticalPanel fileUploadPanelWrapper = new VerticalPanel();
    protected final FlowPanel fileUploadInputWrapper = new FlowPanel();
    protected FileUpload fileUpload = getFileUpload();
    protected FileUploadedHandler myFileUploadedHandler;
    protected String myFileNamePattern;
    protected MessageDataSource myMessageDataSource;
    protected Label myFileNameExample;
    protected Label myUploadErrorMessage;
    protected String myErrorMessage;
    protected final ChangeHandler myFileNameValidationHandler;
    protected FileSubmitHandler theHandler;
    protected CustomMenuBar theSubmitButton;

    public FileUploadWidget(final MessageDataSource aMessageDataSource, final String aFileNamePattern, final FileUploadedHandler aFileUploadedHandler, String aFileNameExample, final String anErrorMessage) {
        this(aMessageDataSource, aFileNamePattern, aFileUploadedHandler, anErrorMessage);
        myFileNameExample.setText(aFileNameExample);
    }

    public FileUploadWidget(final MessageDataSource aMessageDataSource, final String aFileNamePattern, final FileUploadedHandler aFileUploadedHandler, final String anErrorMessage) {
        myMessageDataSource = aMessageDataSource;
        myFileNamePattern = aFileNamePattern;
        myFileUploadedHandler = aFileUploadedHandler;
        myFileNameExample = new Label();
        myErrorMessage = anErrorMessage;
        myUploadErrorMessage = new Label(myErrorMessage);
        myUploadErrorMessage.setStyleName(CSSClassManager.VALIDATION_ERROR_MESSAGE);
        myUploadErrorMessage.setVisible(false);
        
        theSubmitButton = new CustomMenuBar();
        theSubmitButton.setVisible(false);
        
        myFileNameValidationHandler = new ChangeHandler() {
            
            public void onChange(ChangeEvent event) {
                String fileName = fileUpload.getFilename();
                if (!validFileName(fileName)) {
                	displayErrorMessage();
                }else{
                	hideErrorMessage();
                }
            }
        };
        
        fileUpload.addChangeHandler(myFileNameValidationHandler);
        initSubmitHandler();
        formPanel.addSubmitHandler(theHandler);
        formPanel.addSubmitCompleteHandler(theHandler);

        formPanel.setEncoding(ConsoleFormPanel.ENCODING_MULTIPART);
        formPanel.setMethod(ConsoleFormPanel.METHOD_POST);
        FormElement.as(formPanel.getElement()).setAcceptCharset("UTF-8");
        formPanel.setAction(RpcConsoleServices.getFileUploadURL());
        fileUploadInputWrapper.add(fileUpload);
        fileUploadInputWrapper.add(myFileNameExample);
        fileUploadPanelWrapper.add(fileUploadInputWrapper);
        fileUploadPanelWrapper.add(myUploadErrorMessage);
        formPanel.add(fileUploadPanelWrapper);
        
        theSubmitButton.addItem(constants.install(), new Command() {
            public void execute() {
            	 String fileName = fileUpload.getFilename();
                 if (!validFileName(fileName)) {
                	displayErrorMessage();
                 }else{
                	hideErrorMessage();
                 	formPanel.submit();
                 }
            	
            }
        });

        theOuterPanel.add(formPanel);
        theOuterPanel.add(theSubmitButton);

        initWidget(theOuterPanel);
        theOuterPanel.setStylePrimaryName("bos_file_upload_widget");
    }
    
    public void reset(){
    	formPanel.reset();
    	myUploadErrorMessage.setVisible(false);
     	theSubmitButton.setVisible(false);
    }
    
    private void displayErrorMessage(){
    	myUploadErrorMessage.setVisible(true);
     	theSubmitButton.setVisible(false);
    }
    
    private void hideErrorMessage(){
    	 myUploadErrorMessage.setVisible(false);
    	 theSubmitButton.setVisible(true);
    }
   
    protected void initSubmitHandler(){
        theHandler = new DeploySubmitHandler();
    }
    
    /**
     * @param fileName
     * @return
     */
    protected boolean validFileName(String fileName) {
        if (myFileNamePattern == null || myFileNamePattern.length()==0) {
            return fileName != null && fileName.length()>0;
        } else {
            return fileName.matches(myFileNamePattern);
        }
    }

    private class DeploySubmitHandler extends FileSubmitHandler {

        public void onSubmitComplete(final SubmitCompleteEvent anEvent) {
            String response = anEvent.getResults();
            response = URL.decodeQueryString(response);
            response = response.replaceAll("&amp;", "&");
            response = response.replaceAll("&lt;", "<");
            response = response.replaceAll("&gt;", ">");
            String fileName = response;
            final int divIndex = fileName.indexOf("<div");
            if(divIndex > 0 ){
            	fileName = fileName.substring(0, divIndex);
            }
            myFileUploadedHandler.fileUploaded(fileName);
            // clean the form.
            fileUploadInputWrapper.remove(fileUpload);
            fileUpload = getFileUpload();
            fileUploadInputWrapper.insert(fileUpload, 0);
            if(myMessageDataSource!=null) {
                myMessageDataSource.addInfoMessage(messages.fileUploaded());
            }
        }
    }

    protected abstract class FileSubmitHandler implements SubmitHandler, SubmitCompleteHandler {
        public void onSubmit(SubmitEvent anEvent) {
            String fileName = fileUpload.getFilename();
            if (!validFileName(fileName)) {
                anEvent.cancel();
                if(myMessageDataSource!=null) {
                    myMessageDataSource.addWarningMessage(myErrorMessage);
                }
            } else {
                if(myMessageDataSource!=null) {
                    myMessageDataSource.addInfoMessage(messages.uploadingFile());
                }
            }
        }
        public abstract void onSubmitComplete(SubmitCompleteEvent anEvent);
    }
    
    protected FileUpload getFileUpload() {
        FileUpload fileUpload = new FileUpload();
        // mandatory
        fileUpload.setName("uploadFormElement");
        if(myFileNameValidationHandler != null) {
            fileUpload.addChangeHandler(myFileNameValidationHandler);
        }
        fileUpload.getElement().setAttribute("size", "45%");
        return fileUpload;
    }
}