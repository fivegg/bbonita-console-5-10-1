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
package org.bonitasoft.console.client.view.identity;

import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.I18NComposite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserViewer extends I18NComposite implements ModelChangeListener {

  public static final String USER_PROPERTY = "user viewer user";
  protected final UserDataSource myUserDataSource;
  protected UserUUID myCurrentUserUUID;
  protected User myCurrentUser;
  protected final FlowPanel myOuterPanel;
  protected final HTML myUserIdentityLabel;
  protected boolean isEditable;
  protected final FlexTable myIDCardPopupPanel = new FlexTable();

  protected CustomDialogBox myUserSearchPopup;
  protected UserFinderPanel myUserFinder;

  protected transient ModelChangeSupport myChanges = new ModelChangeSupport(this);
  protected Image myClearIcon;

  /**
   * Default constructor.
   */
  public UserViewer(final UserDataSource aDataSource, final UserUUID aUserUUID, final boolean isEditable) {
    myUserDataSource = aDataSource;
    myCurrentUserUUID = aUserUUID;
    this.isEditable = isEditable;

    myUserIdentityLabel = new HTML(constants.loadingSmall());
    myOuterPanel = new FlowPanel();

    initContent();
    if (aUserUUID != null) {
      if (myUserDataSource != null) {
        myUserDataSource.getItem(myCurrentUserUUID, new AsyncHandler<User>() {

          public void handleSuccess(User aResult) {
            myCurrentUser = aResult;
            update();
          }

          public void handleFailure(Throwable aT) {
            myCurrentUser = null;
            update();
          }
        });
      }
    } else {
      myCurrentUser = null;
      update();
    }

    myOuterPanel.setStylePrimaryName("bos_item_viewer");
    myUserIdentityLabel.setStylePrimaryName("bos_item_viewer_identity");

    initWidget(myOuterPanel);
  }

  public void setEnabled(boolean isEnabled) {
    isEditable = isEnabled;
    myOuterPanel.clear();
    initContent();
    update();
  }

  private void initContent() {
    final DecoratedPopupPanel theIDCardPopup = new DecoratedPopupPanel(true, false);
    theIDCardPopup.addStyleName("bos_item_viewer_identity_popup");

    myIDCardPopupPanel.setHTML(0, 0, constants.titleLabel());
    myIDCardPopupPanel.getFlexCellFormatter().setStyleName(0, 0, "identity_form_label");

    myIDCardPopupPanel.setHTML(1, 0, constants.firstNameLabel());
    myIDCardPopupPanel.getFlexCellFormatter().setStyleName(1, 0, "identity_form_label");

    myIDCardPopupPanel.setHTML(2, 0, constants.lastNameLabel());
    myIDCardPopupPanel.getFlexCellFormatter().setStyleName(2, 0, "identity_form_label");

    myIDCardPopupPanel.setHTML(3, 0, constants.jobTitleLabel());
    myIDCardPopupPanel.getFlexCellFormatter().setStyleName(3, 0, "identity_form_label");

    myIDCardPopupPanel.setHTML(4, 0, constants.usernameLabel());
    myIDCardPopupPanel.getFlexCellFormatter().setStyleName(4, 0, "identity_form_label");

    theIDCardPopup.setWidget(myIDCardPopupPanel);
    myUserIdentityLabel.addMouseOverHandler(new MouseOverHandler() {

      public void onMouseOver(MouseOverEvent aEvent) {
        if (myCurrentUser != null) {
          theIDCardPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int anOffsetWidth, int anOffsetHeight) {
              int left = myUserIdentityLabel.getAbsoluteLeft() - (anOffsetWidth / 2);
              int top = myUserIdentityLabel.getAbsoluteTop() + myUserIdentityLabel.getOffsetHeight() + 7;
              theIDCardPopup.setPopupPosition(left, top);
            }
          });
        }
      }

    });
    myUserIdentityLabel.addMouseOutHandler(new MouseOutHandler() {

      public void onMouseOut(MouseOutEvent aEvent) {
        theIDCardPopup.hide();
      }
    });

    myOuterPanel.add(myUserIdentityLabel);
    if (isEditable) {
      CustomMenuBar theUserSearchButton = new CustomMenuBar();
      theUserSearchButton.addItem(constants.search(), new Command() {

        public void execute() {
          if (myUserSearchPopup == null) {
            myUserSearchPopup = buildUserSearchPopup();
          }
          myUserSearchPopup.center();
        }
      });

      myClearIcon = new Image(PICTURE_PLACE_HOLDER);
      myClearIcon.setStylePrimaryName(CSSClassManager.CLEAR_ICON);
      myClearIcon.setTitle(constants.remove());
      myClearIcon.addClickHandler(new ClickHandler() {

        public void onClick(ClickEvent aEvent) {
          User theOldValue = myCurrentUser;
          myCurrentUserUUID = null;
          myCurrentUser = null;
          update();
          myChanges.fireModelChange(USER_PROPERTY, theOldValue, myCurrentUser);
        }
      });

      myOuterPanel.add(theUserSearchButton);
      myOuterPanel.add(myClearIcon);
    }

  }

  protected CustomDialogBox buildUserSearchPopup() {
    final CustomDialogBox theResult = new CustomDialogBox(false, true);
    myUserFinder = new UserFinderPanel(false);
    myUserFinder.addModelChangeListener(UserFinderPanel.ITEM_LIST_PROPERTY, this);
    myUserFinder.addModelChangeListener(UserFinderPanel.CANCEL_PROPERTY, this);
    theResult.add(myUserFinder);
    theResult.setText(constants.searchForAUser());
    return theResult;
  }

  private void update() {
    if (myCurrentUser != null) {
      final String theFirstName;
      if (myCurrentUser.getFirstName() == null) {
        theFirstName = "";
      } else {
        theFirstName = myCurrentUser.getFirstName();
      }
      final String theLastName;
      if (myCurrentUser.getLastName() == null) {
        theLastName = "";
      } else {
        theLastName = myCurrentUser.getLastName();
      }
      if (theFirstName.length() > 0 || theLastName.length() > 0) {
        myUserIdentityLabel.setText(patterns.userIdentity(theFirstName, theLastName));
      } else {
        myUserIdentityLabel.setText(myCurrentUser.getUsername());
      }
      myIDCardPopupPanel.setWidget(0, 1, new Label(myCurrentUser.getTitle()));
      myIDCardPopupPanel.setWidget(1, 1, new Label(theFirstName));
      myIDCardPopupPanel.setWidget(2, 1, new Label(theLastName));
      myIDCardPopupPanel.setWidget(3, 1, new Label(myCurrentUser.getJobTitle()));
      myIDCardPopupPanel.setWidget(4, 1, new Label(myCurrentUser.getUsername()));
      myClearIcon.setVisible(true);
    } else {
      myUserIdentityLabel.setText(constants.notYetDefined());
      myIDCardPopupPanel.setText(0, 1, null);
      myIDCardPopupPanel.setText(1, 1, null);
      myIDCardPopupPanel.setText(2, 1, null);
      myIDCardPopupPanel.setText(3, 1, null);
      myIDCardPopupPanel.setText(4, 1, null);
      myClearIcon.setVisible(false);
      if (myUserFinder != null) {
        myUserFinder.clear();
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void modelChange(ModelChangeEvent aEvt) {
    if (UserFinderPanel.ITEM_LIST_PROPERTY.equals(aEvt.getPropertyName())) {
      List<UserUUID> theNewValue = ((List<UserUUID>) aEvt.getNewValue());

      if (myUserFinder != null && aEvt.getSource().equals(myUserFinder)) {
        final User theOldValue = myCurrentUser;
        if (theNewValue != null && !theNewValue.isEmpty()) {
          myCurrentUserUUID = theNewValue.iterator().next();
          myCurrentUser = myUserFinder.getItems().iterator().next();
        } else {
          myCurrentUserUUID = null;
          myCurrentUser = null;
        }

        myUserSearchPopup.hide();
        update();
        myChanges.fireModelChange(USER_PROPERTY, theOldValue, myCurrentUser);
      }
      
    } else if (UserFinderPanel.CANCEL_PROPERTY.equals(aEvt.getPropertyName())) {
      myUserSearchPopup.hide();
    }

  }

  /**
   * @param aDelegateUuid
   */
  public void setUser(UserUUID aUserUuid) {
    myCurrentUserUUID = aUserUuid;
    if (aUserUuid == null) {
      myCurrentUser = null;
      update();
    } else {
      myUserDataSource.getItem(myCurrentUserUUID, new AsyncHandler<User>() {

        public void handleFailure(Throwable aT) {
          myCurrentUser = null;
          update();
        }

        public void handleSuccess(User aResult) {
          myCurrentUser = aResult;
          update();
        }
      });
    }

  }

  public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
    removeModelChangeListener(aPropertyName, aListener);
    myChanges.addModelChangeListener(aPropertyName, aListener);
  }

  public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
    myChanges.removeModelChangeListener(aPropertyName, aListener);
  }

  public User getItem() {
    return myCurrentUser;
  }
}
