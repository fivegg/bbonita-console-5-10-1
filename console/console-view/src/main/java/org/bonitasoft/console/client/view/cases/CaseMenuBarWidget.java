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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.LabelAssociationController;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.model.SimpleSelection.SimpleSelector;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseSelection;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CaseSelectorWidget;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.MenuChoicesPanel;
import org.bonitasoft.console.client.view.labels.LabelAssociationWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseMenuBarWidget extends BonitaPanel implements LabelAssociationController, ModelChangeListener {

  private static final String LABELS_BUTTON_TITLE = constants.labels();
  private static final String REFRESH_TITLE = constants.refresh();

  protected FlowPanel myOuterPanel = new FlowPanel();

  private Label myRefreshLabel;
  private CaseSelection myCaseSelection;
  private LabelDataSource myLabelDataSource;
  private LabelModel myLabelCurrentlyDisplayed;
  private Category myCategoryCurrentlyDisplayed;
  private Label myBackToLabel = new Label();

  private CustomMenuBar myLabelsMenu;

  // Create the Label menu
  private MenuChoicesPanel myLabelMenuChoicesPanel;

  private CaseDataSource myCaseDataSource;
  protected LabelUUID myStarLabelUUID;
  protected LabelUUID myInboxLabelUUID;
  protected AsyncHandler<CaseItem> myRedirectHandler;
  protected LabelsConfiguration myConfiguration;
  private boolean myBackLinkEnabled;
  private CaseSelectorWidget myItemSelector;

  /**
   * 
   * Default constructor.
   * 
   * @param aCaseDataSource
   * @param aCaseSelection
   * @param aLabelDataSource
   * @param aPosition
   * @param backEnabled
   */
  public CaseMenuBarWidget(final CaseDataSource aCaseDataSource, final CaseSelection aCaseSelection, final LabelDataSource aLabelDataSource, final boolean backEnabled) {
    super();

    myCaseSelection = aCaseSelection;
    myLabelDataSource = aLabelDataSource;
    myCaseDataSource = aCaseDataSource;

    myLabelDataSource.getConfiguration(new AsyncHandler<LabelsConfiguration>() {

      public void handleFailure(Throwable aT) {
        update();
        myLabelDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, CaseMenuBarWidget.this);
      }

      public void handleSuccess(LabelsConfiguration aResult) {
        myConfiguration = new LabelsConfiguration();
        myConfiguration.setCustomLabelsEnabled(aResult.isCustomLabelsEnabled());
        myConfiguration.setStarEnabled(aResult.isStarEnabled());
        
        if (aResult.isStarEnabled()) {
            myItemSelector = new CaseSelectorWidget(myCaseDataSource);
        } else {
            // Create the Selector Widget
            myItemSelector = new CaseSelectorWidget(myCaseDataSource) {
                /*
                 * (non-Javadoc)
                 * 
                 * @see org.bonitasoft.console.client.view.CaseSelectorWidget#buildChoices()
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
        }
        
        update();
        myLabelDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, CaseMenuBarWidget.this);
      }
    });

    myStarLabelUUID = new LabelUUID(LabelModel.STAR_LABEL.getUUID().getValue(), new UserUUID(BonitaConsole.userProfile.getUsername()));
    myInboxLabelUUID = new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(BonitaConsole.userProfile.getUsername()));

    myBackLinkEnabled = backEnabled;
    if (myBackLinkEnabled) {
      myBackToLabel.getElement().setId("backToInboxButton");
      myBackToLabel.setStyleName(CSSClassManager.LINK_LABEL);
      myBackToLabel.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent aArg0) {
          redirectUserToCaseList();
        }
      });
    }

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
                  redirectUserToCaseList();
                }

                public void handleSuccess(CaseItem aResult) {
                  if (aResult == null) {
                    redirectUserToCaseList();
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
    this.initWidget(myOuterPanel);
  }

  /**
   * Set the label currently displayed. This is useful to know the context of
   * what is displayed, e.g., the 'Move to' menu has to know from which label
   * the case must be moved.
   * 
   * @param aLabel
   */
  public void setLabelToDisplay(LabelModel aLabel) {
    myLabelCurrentlyDisplayed = aLabel;
    if (aLabel != null) {
      myBackToLabel.setText(patterns.backToDestination(LocaleUtil.translate(myLabelCurrentlyDisplayed.getUUID())));
    } else {
      myBackToLabel.setText("");
    }
  }

  /**
   * Set the label currently displayed. This is useful to know the context of
   * what is displayed, e.g., the 'Move to' menu has to know from which label
   * the case must be moved.
   * 
   * @param aLabel
   */
  public void setCategoryToDisplay(Category aCategory) {
    myCategoryCurrentlyDisplayed = aCategory;
    if (myCategoryCurrentlyDisplayed != null) {
      myBackToLabel.setText(patterns.backToDestination(myCategoryCurrentlyDisplayed.getName()));
    } else {
      myBackToLabel.setText("");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.bonitasoft.console.client.controller.LabelAssociationController#
   * applyChanges(java.util.Set, java.util.Set, java.util.Set)
   */
  public void applyChanges(Set<LabelUUID> aSetOfLabelUUIDToAdd, Set<LabelUUID> aSetOfLabelUUIDToRemove, Set<CaseUUID> aSetOfCaseUUID) {
    myLabelsMenu.hideAllPopups();
    Set<CaseUUID> theFilteredCases = new HashSet<CaseUUID>();
    CaseItem theCaseItem;
    for (CaseUUID theCaseUUID : aSetOfCaseUUID) {
      theCaseItem = myCaseDataSource.getItem(theCaseUUID);
      if (theCaseItem != null && !theCaseItem.isArchived()) {
        theFilteredCases.add(theCaseUUID);
      }
    }

    myLabelDataSource.updateLabels(aSetOfLabelUUIDToAdd, aSetOfLabelUUIDToRemove, theFilteredCases);
  }

  /*
   * Change the History token to be redirected to the case list.
   */
  private void redirectUserToCaseList() {
    if (myLabelCurrentlyDisplayed != null && myCategoryCurrentlyDisplayed == null) {
      if (myLabelCurrentlyDisplayed.equals(LabelModel.ADMIN_ALL_CASES.getUUID())) {
        History.newItem(ViewToken.AdminCaseList.toString());
      } else {
        History.newItem(ViewToken.CaseList + "/lab:" + myLabelCurrentlyDisplayed.getUUID().getValue());
      }
    } else if (myLabelCurrentlyDisplayed == null && myCategoryCurrentlyDisplayed != null) {
      History.newItem(ViewToken.CaseList + "/cat:" + myCategoryCurrentlyDisplayed.getUUID().getValue());
    } else {
      Window.alert("Invalid state!");
    }
  }

  private void update() {
    myOuterPanel.clear();

    if (myBackLinkEnabled) {
      myOuterPanel.add(myBackToLabel);
    } else {
      myOuterPanel.add(myItemSelector);
    }
    if (myConfiguration == null || myConfiguration.isCustomLabelsEnabled() || myConfiguration.isStarEnabled()) {
      // Create a menu bar
      myLabelsMenu = new CustomMenuBar();

      myLabelMenuChoicesPanel = new MenuChoicesPanel();
      myLabelMenuChoicesPanel.addChoice(myLabelMenuChoicesPanel.new MenuChoice(new LabelAssociationWidget((LabelAssociationController) this, myCaseDataSource, myCaseSelection, myLabelDataSource),
          null));
      final Hyperlink theManageLabelsLink = new Hyperlink(constants.manageLabels(), ViewToken.Labels.name());
      theManageLabelsLink.setStylePrimaryName(CSSClassManager.LINK_LABEL);
      myLabelMenuChoicesPanel.addChoice(myLabelMenuChoicesPanel.new MenuChoice(theManageLabelsLink, null));
      myLabelsMenu.addItem(LABELS_BUTTON_TITLE, myLabelMenuChoicesPanel);

      myOuterPanel.add(myLabelsMenu);
    }
    myOuterPanel.add(myRefreshLabel);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
   * .bonitasoft.console.client.events.ModelChangeEvent)
   */
  public void modelChange(ModelChangeEvent anEvt) {
    if (LabelDataSource.CONFIGURATION_PROPERTY.equals(anEvt.getPropertyName())) {
      if (myConfiguration == null) {
        myConfiguration = new LabelsConfiguration();
      }
      myConfiguration.setCustomLabelsEnabled(((LabelsConfiguration) anEvt.getNewValue()).isCustomLabelsEnabled());
      myConfiguration.setStarEnabled(((LabelsConfiguration) anEvt.getNewValue()).isStarEnabled());
      update();
    }
  }

}
