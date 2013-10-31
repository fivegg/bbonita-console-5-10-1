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

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseItem.CaseItemState;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.SimpleSelection.SimpleSelector;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CaseSelectorWidget;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.MenuChoicesPanel;
import org.bonitasoft.console.client.view.identity.ConfirmationDialogbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class AdminCaseMenuBarWidget extends BonitaPanel {

  protected static final String SERVLET_PATH = "image/renderer?image=";

  protected static final String REFRESH_TITLE = constants.refresh();

  protected static final String DELETE_INSTANCE_BUTTON_TITLE = constants.deleteInstance();

  protected static final String CANCEL_INSTANCE_TITLE = constants.cancelInstance();

  protected static final String MODIFY_INSTANCE_BUTTON_TITLE = constants.modifyInstance();

  protected static final String OPEN_DESIGN_BUTTON_TITLE = constants.showProcessDesign();

  protected boolean myBackLinkEnabled;

  protected final FlowPanel myOuterPanel = new FlowPanel();
  // Create a menu bar
  protected final CustomMenuBar myDirectActionMenu = new CustomMenuBar();
  protected final CaseDataSource myCaseDataSource;
  protected final CaseSelection myCaseSelection;
  protected final MessageDataSource myMessageDataSource;
  protected ConfirmationDialogbox confirmationDialogbox;
  
  protected Label myRefreshLabel;
  protected AsyncHandler<CaseItem> myRedirectHandler;
  protected Label myBackToLabel = new Label(constants.backToCases());

  protected final ProcessDataSource myProcessDataSource;
  protected final MenuChoicesPanel myMoreActionsMenu = new MenuChoicesPanel();

  protected AsyncHandler<CaseItem> myCaseModifyHandler;

  private CaseSelectorWidget myItemSelector;

  /**
   * Default constructor.
   * 
   * @param aCaseDataSource
   * @param aCaseSelection
   * @param isBackEnabled
   * @param aProcessDataSource
   * @param aMessageDataSource
   */
  public AdminCaseMenuBarWidget(CaseDataSource aCaseDataSource, CaseSelection aCaseSelection, boolean isBackEnabled, ProcessDataSource aProcessDataSource, MessageDataSource aMessageDataSource) {
    super();

    myCaseDataSource = aCaseDataSource;
    myCaseSelection = aCaseSelection;
    myProcessDataSource = aProcessDataSource;
    myMessageDataSource = aMessageDataSource;
    
    this.initWidget(myOuterPanel);
    buildContent(isBackEnabled);
  }

  protected void buildContent(boolean isBackEnabled) {

    if (isBackEnabled) {
      addBackLinkToOuterPanel();
    } else {
      addSelectorToOuterPanel();
    }

    buildMyDirectActionMenu(isBackEnabled);
    buildRefreshLabel();
    buildMenuPanel();

  }

  private void addBackLinkToOuterPanel() {
    myBackToLabel.setStyleName(CSSClassManager.LINK_LABEL);
    myBackToLabel.getElement().setId("backToInboxButton");
    myBackToLabel.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent aArg0) {
        redirectUserToAdminCaseList();
      }
    });
    myOuterPanel.add(myBackToLabel);
  }

  protected void buildMenuPanel() {
    myOuterPanel.add(myDirectActionMenu);
    myOuterPanel.add(myRefreshLabel);
  }

  protected void buildRefreshLabel() {
    myRefreshLabel = new Label(REFRESH_TITLE);
    myRefreshLabel.setStyleName(CSSClassManager.LINK_LABEL);
    myRefreshLabel.addClickHandler(new ClickHandler() {

      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        if (myOuterPanel.getWidgetIndex(myItemSelector) != -1) {
          myCaseDataSource.reload();
        } else {
          ArrayList<CaseUUID> theSelectedCases = myCaseSelection.getSelectedItems();
          if (theSelectedCases != null && !theSelectedCases.isEmpty()) {
            final CaseUUID theCurrentlyDisplayedCaseUUID = theSelectedCases.get(0);
            if (myRedirectHandler == null) {
              myRedirectHandler = new AsyncHandler<CaseItem>() {

                public void handleFailure(Throwable aT) {
                  redirectUserToAdminCaseList();
                }

                public void handleSuccess(CaseItem aResult) {
                  if (aResult == null) {
                      redirectUserToAdminCaseList();
                  } else {
                      // also reload comments
                      myCaseDataSource.getCaseCommentFeed(theCurrentlyDisplayedCaseUUID, null);
                  }
                }
              };
            }
            myCaseDataSource.getItem(theCurrentlyDisplayedCaseUUID, myRedirectHandler);
          }
        }
      }
    });

  }

  protected void buildMyDirectActionMenu(boolean isBackEnabled) {
    if (isBackEnabled) {
      myDirectActionMenu.addItem(MODIFY_INSTANCE_BUTTON_TITLE, new Command() {
        public void execute() {
          modifySelectedCases();
        }
      });
    }

    myDirectActionMenu.addItem(CANCEL_INSTANCE_TITLE, new Command() {
      public void execute() {
          //add a ConfirmationDialogbox when you cancel a Case.
          if (myCaseSelection.getSize() > 0) {
             confirmationDialogbox = new ConfirmationDialogbox(constants.cancelCasesDialogbox(), patterns.cancelCasesWarn(myCaseSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
              confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                  public void onClose(CloseEvent<PopupPanel> event) {
                      if(confirmationDialogbox.getConfirmation()){
                          cancelSelectedCases();
                      }
                  }} );
            } else {
              if (myMessageDataSource != null) {
                  myMessageDataSource.addWarningMessage(messages.noCaseSelectedWarn());
              }
            }   
      }

    });

    myDirectActionMenu.addItem(DELETE_INSTANCE_BUTTON_TITLE, new Command() {
      public void execute() {
          //add a ConfirmationDialogbox when you delete a Case.
          if (myCaseSelection.getSize() > 0) {
             confirmationDialogbox = new ConfirmationDialogbox(constants.deleteCasesDialogbox(), patterns.deleteCasesWarn( myCaseSelection.getSelectedItems().size()), constants.okButton(), constants.cancelButton());
             final CheckBox deleteAttachmentsCheckBox = new CheckBox(constants.deleteAttachmentsCheckBox());
             confirmationDialogbox.addWidgetToDialogboxBody(deleteAttachmentsCheckBox);
             
             confirmationDialogbox.addCloseHandler(new CloseHandler<PopupPanel>(){
                  public void onClose(CloseEvent<PopupPanel> event) {
                      if(confirmationDialogbox.getConfirmation()){
                          deleteSelectedCases(deleteAttachmentsCheckBox.getValue());
                      }
                  }} );
          } else {
              if (myMessageDataSource != null) {
                  myMessageDataSource.addWarningMessage(messages.noCaseSelectedWarn());
              }
          }   
      }

    });

    if (isBackEnabled) {
      buildMoreActionSubMenu();
    }

  }

  protected void buildMoreActionSubMenu() {
    // Create the choices, i.e., the menu content.
    myMoreActionsMenu.addChoice(myMoreActionsMenu.new MenuChoice(new Label(OPEN_DESIGN_BUTTON_TITLE), new Command() {
      public void execute() {
        displayDesignOfSelectedCases();
      }
    }));
    myDirectActionMenu.addItem(constants.moreActions(), myMoreActionsMenu);
  }

  protected void addSelectorToOuterPanel() {
    // Create the Selector Widget
    myItemSelector = new CaseSelectorWidget(myCaseDataSource) {
      /*
       * (non-Javadoc)
       * 
       * @see
       * org.bonitasoft.console.client.view.CaseSelectorWidget#buildChoices()
       */
      @Override
      protected List<String> buildChoices() {
        final List<String> theChoices = new ArrayList<String>();
        for (SimpleSelector theSelector : SimpleSelector.values()) {
          theChoices.add(theSelector.name());
        }
        return theChoices;
      }
    };
    myOuterPanel.add(myItemSelector);
  }

  protected void displayDesignOfSelectedCases() {

    ArrayList<CaseUUID> theSelectedCases = myCaseSelection.getSelectedItems();
    if (theSelectedCases != null && !theSelectedCases.isEmpty()) {
      CaseUUID theCaseUUID = theSelectedCases.get(0);
      myCaseDataSource.getItem(theCaseUUID, new AsyncHandler<CaseItem>() {
        public void handleFailure(Throwable aT) {
          // Do nothing.

        }

        public void handleSuccess(final CaseItem aCase) {
          if (aCase != null && aCase.getProcessUUID() != null) {
            myProcessDataSource.getProcessImage(aCase.getProcessUUID(), new AsyncHandler<String>() {

              public void handleFailure(Throwable aT) {
                // Do nothing.

              }

              public void handleSuccess(String aURL) {
                if (aURL != null) {
                  Window.open(GWT.getModuleBaseURL() + SERVLET_PATH + aURL, "_blank", "");
                }

              }
            });
          }
        }
      });
    }

  }

  protected void modifySelectedCases() {
    // Redirect the user to the case data update widget
    if (myCaseSelection.getSelectedItems().size() > 0) {
      final CaseUUID theCaseUUID = myCaseSelection.getSelectedItems().get(0);
      if (myCaseModifyHandler == null) {
        myCaseModifyHandler = new AsyncHandler<CaseItem>() {
          public void handleFailure(Throwable aT) {
            // Do nothing.

          }

          public void handleSuccess(CaseItem aResult) {
            if (aResult != null && aResult.getState() == CaseItemState.STARTED) {
              History.newItem(ViewToken.CaseDataUpdate.name() + "/" + theCaseUUID.getValue());
            }
          }
        };
      }
      myCaseDataSource.getItem(theCaseUUID, myCaseModifyHandler);

    }
  }

  protected void deleteSelectedCases(final boolean deleteAttachments) {
      myCaseDataSource.deleteCases(myCaseSelection.getSelectedItems(), deleteAttachments);
  }

  protected void cancelSelectedCases() {
    myCaseDataSource.cancelCases(myCaseSelection.getSelectedItems());
  }

  /*
   * Change the History token to be redirected to the case list.
   */
  private void redirectUserToAdminCaseList() {
    History.newItem(ViewToken.AdminCaseList.toString());
  }
}
