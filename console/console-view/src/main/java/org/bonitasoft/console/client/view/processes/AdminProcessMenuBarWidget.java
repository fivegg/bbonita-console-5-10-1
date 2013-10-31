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
package org.bonitasoft.console.client.view.processes;

import java.util.ArrayList;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.UserRightsManager;
import org.bonitasoft.console.client.events.FileUploadedHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.processes.ProcessSelection;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.FileUploadWidget;
import org.bonitasoft.console.client.view.MenuChoicesPanel;
import org.bonitasoft.console.client.view.MenuChoicesPanel.MenuChoice;
import org.bonitasoft.console.client.view.ProcessSelectorWidget;
import org.bonitasoft.console.client.view.identity.ConfirmationDialogbox;
import org.bonitasoft.console.security.client.privileges.RuleType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class AdminProcessMenuBarWidget extends BonitaPanel {

    protected static final String SERVLET_PATH = "image/renderer?image=";
    private static final String REFRESH_TITLE = constants.refresh();

    private static final String ARCHIVE_BUTTON_TITLE = constants.archiveProcess();
    private static final String ENABLE_BUTTON_TITLE = constants.enableProcess();
    private static final String DISABLE_BUTTON_TITLE = constants.disableProcess();

    private static final String DELETE_ALL_INSTANCES_TITLE = constants.deleteAllInstances();
    private static final String DELETE_BUTTON_TITLE = constants.removeProcess();

    protected static final String OPEN_DESIGN_BUTTON_TITLE = constants.showProcessDesign();

    private FlowPanel myOuterPanel = new FlowPanel();
    // Create a menu bar
    protected final CustomMenuBar myDirectActionMenu = new CustomMenuBar();
    protected final CustomMenuBar myMoreActionMenu = new CustomMenuBar();
    private ProcessDataSource myProcessDataSource;
    private ProcessSelection myProcessSelection;
    protected ConfirmationDialogbox confirmationDialogbox;
    
    private Label myRefreshLabel;

    private AsyncHandler<ItemUpdates<BonitaProcess>> myDeleteSelectionHandler = new AsyncHandler<ItemUpdates<BonitaProcess>>() {
        public void handleFailure(Throwable aT) {
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
                return;
            } else {
                myMessageDataSource.addErrorMessage(messages.unableToDeleteProcesses());
            }
        }

        public void handleSuccess(ItemUpdates<BonitaProcess> aResult) {
            myProcessSelection.clearSelection();
            myMessageDataSource.addInfoMessage(messages.processesDeleted());
        }
    };

    protected MessageDataSource myMessageDataSource;

    private ProcessSelectorWidget myItemSelector;

    private CustomDialogBox myInstallPopup;
    private FileUploadWidget fileUploadWidget;
    private MenuChoice myDeleteAllInstancesActionLink;
    private MenuChoice myEnableProcessActionLink;
    private MenuChoice myDisableProcessActionLink;
    private MenuChoice myArchiveProcessActionLink;
    private MenuChoice myDeleteProcessActionLink;
    private MenuChoice myOpenProcessDesignActionLink;

    /**
     * Default constructor.
     * 
     * @param aProcessDataSource
     * @param aProcessSelection
     * @param aPosition
     */
    public AdminProcessMenuBarWidget(final MessageDataSource aMessageDataSource, ProcessDataSource aProcessDataSource, ProcessSelection aProcessSelection) {
        super();
        myMessageDataSource = aMessageDataSource;
        myProcessDataSource = aProcessDataSource;
        myProcessSelection = aProcessSelection;

        // Create the Selector Widget
        myItemSelector = new ProcessSelectorWidget(myProcessDataSource);

        final UserRightsManager theUserRightsManager = UserRightsManager.getInstance();
        if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.PROCESS_INSTALL)) {
            myDirectActionMenu.addItem(constants.install(), new Command() {
                public void execute() {
                    showInstallPopup();
                }
            });
        }
        MenuChoicesPanel theSubMenu = new MenuChoicesPanel();
        myOpenProcessDesignActionLink = theSubMenu.new MenuChoice(new Label(OPEN_DESIGN_BUTTON_TITLE), new Command() {
            public void execute() {
                displayDesignOfSelectedProcesses();
            }
        });

        myDeleteAllInstancesActionLink = theSubMenu.new MenuChoice(new HTML(DELETE_ALL_INSTANCES_TITLE), new Command() {
            public void execute() {
                //add a ConfirmationDialogbox when you delete processes cases.
                if (myProcessSelection.getSize() > 0) {
                   confirmationDialogbox = new ConfirmationDialogbox(constants.deleteProcessesCasesDialogbox(), patterns.deleteProcessesCasesWarn(myProcessSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
                   final CheckBox deleteAttachmentsCheckBox = new CheckBox(constants.deleteAttachmentsCheckBox()); 
                   confirmationDialogbox.addWidgetToDialogboxBody(deleteAttachmentsCheckBox);
                   confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                        public void onClose(CloseEvent<PopupPanel> event) {
                            if(confirmationDialogbox.getConfirmation()){
                                     deleteAllCasesOfSelectedProcesses(deleteAttachmentsCheckBox.getValue());
                            }
                        }} );
                } 
            }
        });

        myEnableProcessActionLink = theSubMenu.new MenuChoice(new HTML(ENABLE_BUTTON_TITLE), new Command() {
            public void execute() {
                //add a ConfirmationDialogbox when you enable processes
                if (myProcessSelection.getSize() > 0) {
                   confirmationDialogbox = new ConfirmationDialogbox(constants.enableProcess(), patterns.enableProcessesExplanations(myProcessSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
                    confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                        public void onClose(CloseEvent<PopupPanel> event) {
                            if(confirmationDialogbox.getConfirmation()){
                                enableSelectedProcesses();
                            }
                        }} );
                } 
            }
        });

        myDisableProcessActionLink = theSubMenu.new MenuChoice(new HTML(DISABLE_BUTTON_TITLE), new Command() {
            public void execute() {
                //add a ConfirmationDialogbox when you disable processes
                if (myProcessSelection.getSize() > 0) {
                   confirmationDialogbox = new ConfirmationDialogbox(constants.disableProcessesDialogbox(), patterns.disableProcessesExplanations(myProcessSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
                    confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                        public void onClose(CloseEvent<PopupPanel> event) {
                            if(confirmationDialogbox.getConfirmation()){
                                disableSelectedProcesses();
                            }
                        }} );
                } 
            }
        });

        myArchiveProcessActionLink = theSubMenu.new MenuChoice(new HTML(ARCHIVE_BUTTON_TITLE), new Command() {
            public void execute() {
                //add a ConfirmationDialogbox when you archive processes
                if (myProcessSelection.getSize() > 0) {
                   confirmationDialogbox = new ConfirmationDialogbox(constants.archiveProcessesDialogbox(), patterns.archiveProcessesWarn(myProcessSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
                    confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                        public void onClose(CloseEvent<PopupPanel> event) {
                            if(confirmationDialogbox.getConfirmation()){
                                archiveSelectedProcesses();
                            }
                        }} );
                } 
            }
        });

        myDeleteProcessActionLink = theSubMenu.new MenuChoice(new HTML(DELETE_BUTTON_TITLE), new Command() {
            public void execute() {
                //add a ConfirmationDialogbox when you remove processes
                if (myProcessSelection.getSize() > 0) {
                   confirmationDialogbox = new ConfirmationDialogbox(constants.removeProcessesDialogbox(), patterns.removeProcessesWarn(myProcessSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
                   final CheckBox deleteAttachmentsCheckBox = new CheckBox(constants.deleteAttachmentsCheckBox()); 
                   confirmationDialogbox.addWidgetToDialogboxBody(deleteAttachmentsCheckBox); 
                   confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                        public void onClose(CloseEvent<PopupPanel> event) {
                            if(confirmationDialogbox.getConfirmation()){
                                deleteSelectedProcesses(deleteAttachmentsCheckBox.getValue());
                            }
                        }} );
                } 
            }
        });

        // Enable / disable action links depending on selection content.
        myProcessSelection.addModelChangeListener(ProcessSelection.ITEM_SELECTION_PROPERTY, new ModelChangeListener() {

            public void modelChange(ModelChangeEvent aEvt) {
                updateActionLinksState();
            }
        });

        theSubMenu.addChoice(myOpenProcessDesignActionLink);
        theSubMenu.addChoice(myDeleteAllInstancesActionLink);
        theSubMenu.addChoice(myEnableProcessActionLink);
        theSubMenu.addChoice(myDisableProcessActionLink);
        theSubMenu.addChoice(myArchiveProcessActionLink);
        theSubMenu.addChoice(myDeleteProcessActionLink);
        myMoreActionMenu.addItem(constants.moreActions(), theSubMenu);

        // Ensure links state.
        updateActionLinksState();

        myRefreshLabel = new Label(REFRESH_TITLE);
        myRefreshLabel.setStyleName(CSSClassManager.LINK_LABEL);
        myRefreshLabel.addClickHandler(new ClickHandler() {

            /*
             * (non-Javadoc)
             * 
             * @see
             * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
             * .gwt.event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent aArg0) {
                reloadProcesses();
            }

        });

        // Finally layout widgets
        myOuterPanel.add(myItemSelector);
        myOuterPanel.add(myDirectActionMenu);
        myOuterPanel.add(myMoreActionMenu);
        myOuterPanel.add(myRefreshLabel);

        this.initWidget(myOuterPanel);
    }

    protected void updateActionLinksState() {

        if (myProcessSelection.getSize() == 0) {
            myOpenProcessDesignActionLink.setEnabled(false);
            myArchiveProcessActionLink.setEnabled(false);
            myDeleteAllInstancesActionLink.setEnabled(false);
            myDeleteProcessActionLink.setEnabled(false);
            myDisableProcessActionLink.setEnabled(false);
            myEnableProcessActionLink.setEnabled(false);
        } else {
            boolean isManagementAllowed = true;
            final UserRightsManager theUserRightsManager = UserRightsManager.getInstance();
            if (!theUserRightsManager.isAdmin()) {
                for (BonitaProcessUUID theProcessUUID : myProcessSelection.getSelectedItems()) {
                    if (!theUserRightsManager.isAllowed(RuleType.PROCESS_MANAGE, theProcessUUID.getValue())) {
                        isManagementAllowed = false;
                    }
                }
            }
            myOpenProcessDesignActionLink.setEnabled(true);
            myArchiveProcessActionLink.setEnabled(isManagementAllowed);
            myDeleteAllInstancesActionLink.setEnabled(isManagementAllowed);
            myDisableProcessActionLink.setEnabled(isManagementAllowed);
            myEnableProcessActionLink.setEnabled(isManagementAllowed);
            myDeleteProcessActionLink.setEnabled(isManagementAllowed);
        }
    }

    protected void showInstallPopup() {
        if (myInstallPopup == null) {
            buildInstallPopup();
        }
        myInstallPopup.center();
    }

    /**
   * 
   */
    private void buildInstallPopup() {
        myInstallPopup = new CustomDialogBox(false, true);
        myInstallPopup.setText(constants.installProcessPopupTitle());
        final FlowPanel theContentPanel = new FlowPanel();
        theContentPanel.addStyleName("bos_deploy_process_panel");
        theContentPanel.add(new HTML(constants.installProcessExplanations()));
        
        fileUploadWidget = new FileUploadWidget(myMessageDataSource, constants.processFileNamePattern(), new FileUploadedHandler() {
            public void fileUploaded(String aFileName) {
                myProcessDataSource.deploy(aFileName, null);
            }
        }, ""/* constants.processFileNameExamples() */, messages.invalidBarFileToUpload());
        
        
        theContentPanel.add(fileUploadWidget);

        final CustomMenuBar theCancelButton = new CustomMenuBar();
        theCancelButton.addItem(constants.close(), new Command() {

            public void execute() {
            	fileUploadWidget.reset();
                myInstallPopup.hide();
            }
        });
        theCancelButton.addStyleName("bos_hide_popup_button");
        theContentPanel.add(theCancelButton);
        myInstallPopup.add(theContentPanel);
    }

    protected void deleteSelectedProcesses(final boolean deleteAttachments) {
        if (myProcessSelection.getSize() > 0) {
            myProcessDataSource.deleteProcesses(myProcessSelection.getSelectedItems(), deleteAttachments, myDeleteSelectionHandler);
        }
    }

    protected void reloadProcesses() {
        myProcessDataSource.reload();
    }

    protected void enableSelectedProcesses() {
        if (myProcessSelection.getSize() > 0) {
            myProcessDataSource.enable(myProcessSelection.getSelectedItems(), null);
        }
    }

    private void disableSelectedProcesses() {
        if (myProcessSelection.getSize() > 0) {
            myProcessDataSource.disable(myProcessSelection.getSelectedItems(), null);
        }
    }

    private void archiveSelectedProcesses() {
        if (myProcessSelection.getSize() > 0) {
            myProcessDataSource.archive(myProcessSelection.getSelectedItems(), new AsyncHandler<Void>() {
                public void handleFailure(Throwable aT) {

                }

                public void handleSuccess(Void aResult) {
                    reloadProcesses();
                }
            });
        }
    }

    private void deleteAllCasesOfSelectedProcesses(final boolean deleteAttachments) {
        if (myProcessSelection.getSize() > 0) {
            myProcessDataSource.deleteAllInstances(myProcessSelection.getSelectedItems(), deleteAttachments, null);
        }
    }

    protected void displayDesignOfSelectedProcesses() {

        ArrayList<BonitaProcessUUID> theSelectedItems = myProcessSelection.getSelectedItems();
        if (theSelectedItems != null && !theSelectedItems.isEmpty()) {
            for (final BonitaProcessUUID theItemUUID : theSelectedItems) {
                myProcessDataSource.getProcessImage(theItemUUID, new AsyncHandler<String>() {
                    public void handleFailure(Throwable aT) {
                        // Do nothing.
                        
                    }

                    public void handleSuccess(String aURL) {
                        if (aURL != null) {
                            final CustomDialogBox thePopup = new CustomDialogBox(false, false);
                            final BonitaProcess theProcess = myProcessDataSource.getItem(theItemUUID);
                            if (theProcess != null) {
                                thePopup.setText(patterns.processIdentity(theProcess.getDisplayName(), theProcess.getVersion()));
                            }
                            final Frame theIFrame = new Frame(GWT.getModuleBaseURL() + SERVLET_PATH + aURL);
                            final FlowPanel thePopupContent = new FlowPanel();
                            thePopupContent.setStylePrimaryName("bos_process_design");
                            final CustomMenuBar theCloseButton = new CustomMenuBar();
                            theCloseButton.addItem(constants.close(), new Command() {
                                public void execute() {
                                    thePopup.hide();
                                }
                            });

                            theIFrame.setWidth("800px");
                            theIFrame.setHeight("600px");

                            thePopupContent.add(theIFrame);
                            thePopupContent.add(theCloseButton);

                            thePopup.add(thePopupContent);
                            thePopup.show();
                        }
                    }
                });
            }
        }

    }
}
