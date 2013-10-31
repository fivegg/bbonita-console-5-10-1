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

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.identity.Role;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
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
public class RoleViewer extends I18NComposite implements ModelChangeListener {

  public static final String ROLE_PROPERTY = "role viewer role";
  protected final RoleDataSource myItemDataSource;
  protected BonitaUUID myCurrentItemUUID;
  protected Role myCurrentItem;
  protected final FlowPanel myOuterPanel;
  protected final HTML myItemDescriptionLabel;
  protected final boolean isEditable;
  protected final FlexTable myIDCardPopupPanel = new FlexTable();

  protected CustomDialogBox myItemSearchPopup;
  protected RoleFinderPanel myItemFinder;

  protected transient ModelChangeSupport myChanges = new ModelChangeSupport(this);
  protected Image myClearIcon;

  /**
   * Default constructor.
   */
  public RoleViewer(final RoleDataSource aDataSource, final BonitaUUID anItemUUID, final boolean isEditable) {
    myItemDataSource = aDataSource;
    myCurrentItemUUID = anItemUUID;
    this.isEditable = isEditable;

    myItemDescriptionLabel = new HTML(constants.loadingSmall());
    myOuterPanel = new FlowPanel();

    initContent();
    if (anItemUUID != null) {
      if (myItemDataSource != null) {
        myItemDataSource.getItem(myCurrentItemUUID, new AsyncHandler<Role>() {

          public void handleSuccess(Role aResult) {
            myCurrentItem = aResult;
            update();
          }

          public void handleFailure(Throwable aT) {
            myCurrentItem = null;
            update();
          }
        });
      }
    } else {
      myCurrentItem = null;
      update();
    }

    myOuterPanel.setStylePrimaryName("bos_item_viewer");
    myItemDescriptionLabel.setStylePrimaryName("bos_item_viewer_identity");

    initWidget(myOuterPanel);
  }

  private void initContent() {
    final DecoratedPopupPanel theIDCardPopup = new DecoratedPopupPanel(true, false);
    theIDCardPopup.addStyleName("bos_item_viewer_identity_popup");

    myIDCardPopupPanel.setHTML(0, 0, constants.roleLabelLabel());
    myIDCardPopupPanel.getFlexCellFormatter().setStyleName(0, 0, "identity_form_label");

    myIDCardPopupPanel.setHTML(1, 0, constants.roleNameLabel());
    myIDCardPopupPanel.getFlexCellFormatter().setStyleName(1, 0, "identity_form_label");

    myIDCardPopupPanel.setHTML(2, 0, constants.roleDescription());
    myIDCardPopupPanel.getFlexCellFormatter().setStyleName(2, 0, "identity_form_label");

    theIDCardPopup.setWidget(myIDCardPopupPanel);
    myItemDescriptionLabel.addMouseOverHandler(new MouseOverHandler() {

      public void onMouseOver(MouseOverEvent aEvent) {
        if (myCurrentItem != null) {
          theIDCardPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int anOffsetWidth, int anOffsetHeight) {
              int left = myItemDescriptionLabel.getAbsoluteLeft() - (anOffsetWidth / 2);
              int top = myItemDescriptionLabel.getAbsoluteTop() + myItemDescriptionLabel.getOffsetHeight() + 7;
              theIDCardPopup.setPopupPosition(left, top);
            }
          });
        }
      }

    });
    myItemDescriptionLabel.addMouseOutHandler(new MouseOutHandler() {

      public void onMouseOut(MouseOutEvent aEvent) {
        theIDCardPopup.hide();
      }
    });

    myOuterPanel.add(myItemDescriptionLabel);
    if (isEditable) {
      CustomMenuBar theUserSearchButton = new CustomMenuBar();
      theUserSearchButton.addItem(constants.search(), new Command() {

        public void execute() {
          if (myItemSearchPopup == null) {
            myItemSearchPopup = buildUserSearchPopup();
          }
          myItemSearchPopup.center();
        }
      });

      myClearIcon = new Image(PICTURE_PLACE_HOLDER);
      myClearIcon.setStylePrimaryName(CSSClassManager.CLEAR_ICON);
      myClearIcon.setTitle(constants.remove());
      myClearIcon.addClickHandler(new ClickHandler() {

        public void onClick(ClickEvent aEvent) {
          final Role theOldValue = myCurrentItem;
          myCurrentItemUUID = null;
          myCurrentItem = null;
          update();
          myChanges.fireModelChange(ROLE_PROPERTY, theOldValue, myCurrentItem);
        }
      });

      myOuterPanel.add(theUserSearchButton);
      myOuterPanel.add(myClearIcon);
    }

  }

  protected CustomDialogBox buildUserSearchPopup() {
    final CustomDialogBox theResult = new CustomDialogBox(false, true);
    myItemFinder = new RoleFinderPanel(false);
    myItemFinder.addModelChangeListener(RoleFinderPanel.ITEM_LIST_PROPERTY, this);
    myItemFinder.addModelChangeListener(RoleFinderPanel.CANCEL_PROPERTY, this);
    theResult.add(myItemFinder);
    theResult.setText(constants.searchForARole());
    return theResult;
  }

  private void update() {
    if (myCurrentItem != null) {
      myItemDescriptionLabel.setText(patterns.roleIdentity(myCurrentItem.getLabel()));
      myIDCardPopupPanel.setWidget(0, 1, new Label(myCurrentItem.getLabel()));
      myIDCardPopupPanel.setWidget(1, 1, new Label(myCurrentItem.getName()));
      myIDCardPopupPanel.setWidget(2, 1, new Label(myCurrentItem.getDescription()));
      myClearIcon.setVisible(true);
    } else {
      myItemDescriptionLabel.setText(constants.notYetDefined());
      myIDCardPopupPanel.setText(0, 1, null);
      myIDCardPopupPanel.setText(1, 1, null);
      myIDCardPopupPanel.setText(2, 1, null);
      myClearIcon.setVisible(false);
      if (myItemFinder != null) {
        myItemFinder.clear();
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void modelChange(ModelChangeEvent aEvt) {
    if (RoleFinderPanel.ITEM_LIST_PROPERTY.equals(aEvt.getPropertyName())) {
      List<UserUUID> theNewValue = ((List<UserUUID>) aEvt.getNewValue());
      if (myItemFinder != null && aEvt.getSource().equals(myItemFinder)) {
        final Role theOldValue = myCurrentItem;
        if (theNewValue != null && !theNewValue.isEmpty()) {
          myCurrentItemUUID = theNewValue.iterator().next();
          myCurrentItem = myItemFinder.getItems().iterator().next();
        } else {
          myCurrentItemUUID = null;
          myCurrentItem = null;
        }

        myItemSearchPopup.hide();
        update();
        myChanges.fireModelChange(ROLE_PROPERTY, theOldValue, myCurrentItem);
      }

    } else if (RoleFinderPanel.CANCEL_PROPERTY.equals(aEvt.getPropertyName())) {
      myItemSearchPopup.hide();
    }

  }

  /**
   * @param aDelegateUuid
   */
  public void setItem(BonitaUUID aUserUuid) {
    myCurrentItemUUID = aUserUuid;
    if (aUserUuid == null) {
      myCurrentItem = null;
      update();
    } else {
      myItemDataSource.getItem(myCurrentItemUUID, new AsyncHandler<Role>() {

        public void handleFailure(Throwable aT) {
          myCurrentItem = null;
          update();
        }

        public void handleSuccess(Role aResult) {
          myCurrentItem = aResult;
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
}
