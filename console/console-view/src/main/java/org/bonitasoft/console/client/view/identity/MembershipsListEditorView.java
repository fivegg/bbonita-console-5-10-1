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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.events.AddItemHandler;
import org.bonitasoft.console.client.events.HasAddHandler;
import org.bonitasoft.console.client.events.HasRemoveHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.RemoveItemsHandler;
import org.bonitasoft.console.client.identity.Group;
import org.bonitasoft.console.client.identity.MembershipItem;
import org.bonitasoft.console.client.identity.Role;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
import org.bonitasoft.console.client.model.identity.MembershipDataSource;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.I18NComposite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class MembershipsListEditorView extends I18NComposite implements ModelChangeListener, HasAddHandler<MembershipItem>, HasRemoveHandler<MembershipItem> {

  private static final int MIN_ROW_COUNT = 10;
  private static final String ITEM_LIST_EMPTY_ROW_STYLE = "item_list_empty_row";
  private static final String ITEM_LIST_CONTENT_ROW_STYLE = "item_list_content_row";
//  private static final String NBSP = "&nbsp;";

  private Grid myMembershipsList;
  protected final Map<String, Collection<String>> myExistingRuleMemberships;
  protected final List<MembershipItem> myRuleMemberships;
  protected final Map<Integer, MembershipItem> myRowItem = new HashMap<Integer, MembershipItem>();
  protected final Set<Integer> myMembershipRowSelection;
  protected final GroupViewer myGroupViewerPanel;
  protected final RoleViewer myRoleViewerPanel;
  protected Group myGroupToAdd;
  protected Role myRoleToAdd;
  protected Label myMembershipsErrorMessage;
  protected Label myAddErrorValidationMessage;

  protected final FlexTable myOuterPanel;
  protected final GroupDataSource myGroupDataSource;
  protected final RoleDataSource myRoleDataSource;
  protected final MembershipDataSource myMembershipDataSource;
  private ArrayList<AddItemHandler<MembershipItem>> myAddHandlers;
  private ArrayList<RemoveItemsHandler<MembershipItem>> myRemoveHandlers;

  /**
   * Default constructor.
   */
  public MembershipsListEditorView(final GroupDataSource aGroupDataSource, final RoleDataSource aRoleDataSource, final MembershipDataSource aMembershipDataSource) {
    myGroupDataSource = aGroupDataSource;
    myRoleDataSource = aRoleDataSource;
    myMembershipDataSource = aMembershipDataSource;

    myRuleMemberships = new ArrayList<MembershipItem>();
    myExistingRuleMemberships = new HashMap<String, Collection<String>>();

    myOuterPanel = new FlexTable();
    myGroupViewerPanel = new GroupViewer(aGroupDataSource, null, true);
    myRoleViewerPanel = new RoleViewer(aRoleDataSource, null, true);

    myGroupViewerPanel.addModelChangeListener(GroupViewer.GROUP_PROPERTY, this);
    myRoleViewerPanel.addModelChangeListener(RoleViewer.ROLE_PROPERTY, this);

    myMembershipRowSelection = new HashSet<Integer>();

    buildContent();

    myOuterPanel.setStylePrimaryName("bos_membership_list_editor");
    initWidget(myOuterPanel);
  }

  protected void buildContent() {

    myAddErrorValidationMessage = new Label();

    myMembershipsList = new Grid(1, 3);
    myMembershipsList.setWidth("100%");
    myMembershipsList.setStylePrimaryName("item_list");
    myMembershipsList.setWidget(0, 0, buildSelectAllSelector());
    myMembershipsList.setHTML(0, 1, constants.groupPath());
    myMembershipsList.setHTML(0, 2, constants.membershipRoleName());
    myMembershipsList.getColumnFormatter().setStyleName(0, "item_selector");
    myMembershipsList.getColumnFormatter().setStyleName(1, "group_path");
    myMembershipsList.getColumnFormatter().setStyleName(2, "role_name");
    myMembershipsList.getRowFormatter().setStylePrimaryName(0, "item_list_content_row_title");

    final FlowPanel theAddGroupPanel = new FlowPanel();
    theAddGroupPanel.setStylePrimaryName(CSSClassManager.GROUP_PANEL);
    theAddGroupPanel.setVisible(false);
    final Label theAddGroupPanelCaption = new Label(constants.addMembershipGroupPanelCaption());
    theAddGroupPanelCaption.setStylePrimaryName(CSSClassManager.GROUP_PANEL_CAPTION);
    final Label theAddGroupPanelCloseCaption = new Label();
    theAddGroupPanelCloseCaption.setTitle(constants.close());
    theAddGroupPanelCloseCaption.setStylePrimaryName(CSSClassManager.GROUP_PANEL_ACTION_CAPTION);
    theAddGroupPanelCloseCaption.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        myGroupViewerPanel.setItem(null);
        myRoleViewerPanel.setItem(null);
        theAddGroupPanel.setVisible(false);
      }
    });
    final Grid theAddPanel = new Grid(2, 2);
    theAddPanel.setStylePrimaryName(CSSClassManager.GROUP_PANEL_CONTENT);
    theAddPanel.setWidget(0, 0, new Label(constants.chooseAGroup()));
    theAddPanel.setWidget(0, 1, myGroupViewerPanel);
    theAddPanel.setWidget(1, 0, new Label(constants.chooseARole()));
    theAddPanel.setWidget(1, 1, myRoleViewerPanel);

    final CustomMenuBar theAddButton = new CustomMenuBar();
    theAddButton.addItem(constants.add(), new Command() {

      public void execute() {
        addMembershipToList();
        theAddGroupPanel.setVisible(false);
        myGroupViewerPanel.setItem(null);
        myRoleViewerPanel.setItem(null);
      }
    });

    theAddGroupPanel.add(theAddGroupPanelCloseCaption);
    theAddGroupPanel.add(theAddGroupPanelCaption);
    theAddGroupPanel.add(myAddErrorValidationMessage);
    theAddGroupPanel.add(theAddPanel);
    theAddGroupPanel.add(theAddButton);

    final CustomMenuBar theActionButtons = new CustomMenuBar();
    theActionButtons.addItem(constants.add(), new Command() {

      public void execute() {
        theAddGroupPanel.setVisible(true);
      }
    });

    theActionButtons.addItem(constants.delete(), new Command() {

      public void execute() {
        removeSelectedItems();
      }
    });

    myOuterPanel.setWidget(0, 0, myMembershipsList);
    myOuterPanel.setWidget(1, 0, theActionButtons);
    myOuterPanel.setWidget(2, 0, theAddGroupPanel);

    myMembershipsErrorMessage = new Label();
    myMembershipsErrorMessage.setStyleName(CSSClassManager.VALIDATION_ERROR_MESSAGE);
    myOuterPanel.setWidget(3, 0, myMembershipsErrorMessage);
  }

  protected void addMembershipToList() {
    if (myGroupToAdd != null && myRoleToAdd != null) {
      final String theGroupId = myGroupToAdd.getUUID().getValue();
      final String theRoleId = myRoleToAdd.getUUID().getValue();
      boolean alreadyInCandidates = (myExistingRuleMemberships.containsKey(theGroupId) && myExistingRuleMemberships.get(theGroupId).contains(theRoleId));

      if (!alreadyInCandidates) {
        final MembershipItem theMembershipToAdd = new MembershipItem(null, myGroupToAdd, myRoleToAdd);
        if (myAddHandlers != null) {
          for (AddItemHandler<MembershipItem> theHandler : myAddHandlers) {
            theHandler.addItemRequested(theMembershipToAdd);
          }
        }
      }
    }
  }

  private Widget buildSelectAllSelector() {
    final FlowPanel theWrapper = new FlowPanel();
    final CheckBox theSelectAllCheckBox = new CheckBox();
    theWrapper.add(theSelectAllCheckBox);
    theSelectAllCheckBox.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        if (aEvent.getSource() instanceof CheckBox) {
          CheckBox theCheckBox = (CheckBox) aEvent.getSource();
          selectAllRows(theCheckBox.getValue());
          aEvent.stopPropagation();
        }
      }
    });
    final CustomMenuBar theSelector = new CustomMenuBar();
    theSelector.addItem(theWrapper, new Command() {
      public void execute() {
        boolean theNewValue = !theSelectAllCheckBox.getValue();
        theSelectAllCheckBox.setValue(theNewValue, true);
        selectAllRows(theNewValue);
      }
    });
    return theSelector;
  }

  protected void selectAllRows(Boolean aValue) {
    for (int i = 1; i < myMembershipsList.getRowCount(); i++) {
      Widget theWidget = myMembershipsList.getWidget(i, 0);
      if (theWidget instanceof CheckBox) {
        ((CheckBox) theWidget).setValue(aValue, true);
      }
    }
  }

  protected void removeSelectedItems() {
    final Collection<MembershipItem> theItemsToRemove = new HashSet<MembershipItem>();
    int theRowIndex = myMembershipsList.getRowCount() - 1;
    for (int i = theRowIndex; i >= 1; i--) {
      Widget theWidget = myMembershipsList.getWidget(i, 0);
      if (theWidget instanceof CheckBox) {
        if (((CheckBox) theWidget).getValue()) {
          if (myRowItem.get(i) != null) {
            theItemsToRemove.add(myRowItem.get(i));
          }
        }
      }
    }
    for (RemoveItemsHandler<MembershipItem> theHandler : myRemoveHandlers) {
      theHandler.removeItemsRequested(theItemsToRemove);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
   * .bonitasoft.console.client.events.ModelChangeEvent)
   */
  public void modelChange(ModelChangeEvent anEvt) {
    if (RoleViewer.ROLE_PROPERTY.equals(anEvt.getPropertyName())) {
      final Role theNewValue = (Role) anEvt.getNewValue();
      myRoleToAdd = theNewValue;
    } else if (GroupViewer.GROUP_PROPERTY.equals(anEvt.getPropertyName())) {
      final Group theNewValue = (Group) anEvt.getNewValue();
      myGroupToAdd = theNewValue;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.HasAddHandler#addAddHandler(org.bonitasoft
   * .console.client.events.ItemHandler)
   */
  public void addAddHandler(AddItemHandler<MembershipItem> aHandler) {
    if (aHandler != null) {
      if (myAddHandlers == null) {
        myAddHandlers = new ArrayList<AddItemHandler<MembershipItem>>();
      }
      if (!myAddHandlers.contains(aHandler)) {
        myAddHandlers.add(aHandler);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.HasRemoveHandler#addRemoveHandler(
   * org.bonitasoft.console.client.events.RemoveItemsHandler)
   */
  public void addRemoveHandler(RemoveItemsHandler<MembershipItem> aHandler) {
    if (aHandler != null) {
      if (myRemoveHandlers == null) {
        myRemoveHandlers = new ArrayList<RemoveItemsHandler<MembershipItem>>();
      }
      if (!myRemoveHandlers.contains(aHandler)) {
        myRemoveHandlers.add(aHandler);
      }
    }

  }

  /**
   * @param aResult
   */
  public void setItems(List<MembershipItem> aMembershipList) {
    myRuleMemberships.clear();
    myExistingRuleMemberships.clear();
    if (aMembershipList != null) {
      myRuleMemberships.addAll(aMembershipList);
      String theGroupId;
      String theRoleId;
      for (MembershipItem theMembershipItem : aMembershipList) {
        theGroupId = theMembershipItem.getGroup().getUUID().getValue();
        theRoleId = theMembershipItem.getRole().getUUID().getValue();
        if (!myExistingRuleMemberships.containsKey(theGroupId)) {
          myExistingRuleMemberships.put(theGroupId, new HashSet<String>());
        }
        myExistingRuleMemberships.get(theGroupId).add(theRoleId);
      }
    }
    int theNbOfRow = 1;
    if (myRuleMemberships != null) {
      theNbOfRow += myRuleMemberships.size();
    }
    if (theNbOfRow < MIN_ROW_COUNT) {
      theNbOfRow = MIN_ROW_COUNT;
    }
    myMembershipsList.resize(theNbOfRow, 3);
    int theCurrentRow = 1;
    if (myRuleMemberships != null && !myRuleMemberships.isEmpty()) {
      for (MembershipItem theMembership : myRuleMemberships) {
        myMembershipsList.setWidget(theCurrentRow, 0, buildItemSelector(theCurrentRow));
        myMembershipsList.setWidget(theCurrentRow, 1, new Label(Group.buildGroupPath(theMembership.getGroup())));
        myMembershipsList.setWidget(theCurrentRow, 2, new Label(theMembership.getRole().getLabel()));
        myMembershipsList.getRowFormatter().setStylePrimaryName(theCurrentRow, ITEM_LIST_CONTENT_ROW_STYLE);
        // keep mapping between row and membership
        myRowItem.put(theCurrentRow, theMembership);
        theCurrentRow++;
      }
    }
    for (; theCurrentRow < MIN_ROW_COUNT; theCurrentRow++) {
       myMembershipsList.clearCell(theCurrentRow, 0);
       myMembershipsList.clearCell(theCurrentRow, 1);
       myMembershipsList.clearCell(theCurrentRow, 2);
      myMembershipsList.getRowFormatter().setStylePrimaryName(theCurrentRow, ITEM_LIST_EMPTY_ROW_STYLE);
    }

  }

  private Widget buildItemSelector(final int row) {
    final CheckBox theSelectItemCheckBox = new CheckBox();
    theSelectItemCheckBox.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent aEvent) {
        if (aEvent.getSource() instanceof CheckBox) {
          CheckBox theCheckBox = (CheckBox) aEvent.getSource();
          if (theCheckBox.getValue()) {
            myMembershipRowSelection.add(row);
          } else {
            myMembershipRowSelection.remove(row);
          }
        }
      }
    });
    return theSelectItemCheckBox;
  }
}
