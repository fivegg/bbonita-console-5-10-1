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

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.identity.Group;
import org.bonitasoft.console.client.identity.GroupFilter;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.users.UserFilter;
import org.bonitasoft.console.client.view.BonitaPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class GroupEditorPanel extends BonitaPanel {

    /* Back to link */
    protected Label myBackToLabel = new Label();

    protected GroupEditorWidget myGroupEditorWidget;

    /* Editor */
    protected final FlowPanel myOuterPanel;
    protected final FlowPanel myEditorWrapper;

    protected Group myItem;
    protected final MessageDataSource myMessageDataSource;
    protected final GroupDataSource myItemDataSource;

    // Children
    protected final FlowPanel myChildrenEditorWrapper = new FlowPanel();
    protected GroupsListEditorView myChildrenList;

    // Members
    protected final UserDataSource myUserDataSource;
    protected final FlowPanel myMembersEditorWrapper = new FlowPanel();
    protected UsersListEditorView myMembersList;

    protected final GroupFilter myGroupFilter = new GroupFilter(0, 1000);

    protected final UserFilter myUserFilter = new UserFilter(0, 1000);

    public GroupEditorPanel(MessageDataSource aMessageDataSource, GroupDataSource aGroupDataSource, UserDataSource aUserDataSource) {
        myMessageDataSource = aMessageDataSource;
        myItemDataSource = aGroupDataSource;
        myUserDataSource = aUserDataSource;
        myOuterPanel = new FlowPanel();
        myEditorWrapper = new FlowPanel();
        myOuterPanel.setStylePrimaryName("bos_group_editor_panel");

        myBackToLabel.setStyleName(CSSClassManager.LINK_LABEL);
        myBackToLabel.setText(patterns.backToDestination(constants.usersManagement()));
        myBackToLabel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent aArg0) {
                redirectItemList();
            }
        });

        Label theSectionTitle;
        myOuterPanel.add(myBackToLabel);
//        theSectionTitle = new Label(constants.groupDefinitionSectionTitle());
//        theSectionTitle.setStylePrimaryName(CSSClassManager.TITLE_STYLE);
//        myOuterPanel.add(theSectionTitle);
        myOuterPanel.add(myEditorWrapper);

        theSectionTitle = new Label(constants.groupChildrenSectionTitle());
        theSectionTitle.setStylePrimaryName(CSSClassManager.TITLE_STYLE);
        myOuterPanel.add(theSectionTitle);
        buildChildrenEditor();
        myOuterPanel.add(myChildrenEditorWrapper);

        theSectionTitle = new Label(constants.groupMembersSectionTitle());
        theSectionTitle.setStylePrimaryName(CSSClassManager.TITLE_STYLE);
        myOuterPanel.add(theSectionTitle);
        buildMembersEditor();
        myOuterPanel.add(myMembersEditorWrapper);
        initWidget(myOuterPanel);

    }

    private void buildMembersEditor() {
        myMembersList = new UsersListEditorView(myUserDataSource) {
            @Override
            protected void buildContent() {
                myItemList = new Grid(1, 4);
                myItemList.setWidth("100%");
                myItemList.setStylePrimaryName("item_list");
                myItemList.setHTML(0, 0, constants.usernameLabel());
                myItemList.setHTML(0, 1, constants.firstNameLabel());
                myItemList.setHTML(0, 2, constants.lastNameLabel());
                myItemList.setHTML(0, 3, constants.jobTitleLabel());
                myItemList.getColumnFormatter().setStyleName(0, "item_selector");
                myItemList.getRowFormatter().setStylePrimaryName(0, "item_list_content_row_title");

                myOuterPanel.setWidget(0, 0, myItemList);
            }

            @Override
            protected void fillInContentRow(int aRow, User anItem) {
                myItemList.setWidget(aRow, 0, new Label(anItem.getUsername()));
                myItemList.setWidget(aRow, 1, new Label(anItem.getFirstName()));
                myItemList.setWidget(aRow, 2, new Label(anItem.getLastName()));
                myItemList.setWidget(aRow, 3, new Label(anItem.getJobTitle()));
                myItemList.getRowFormatter().setStylePrimaryName(aRow, "item_list_content_row");
            }

            @Override
            protected void fillInEmptyRow(int aRow) {
                myItemList.clearCell(aRow, 0);
                myItemList.clearCell(aRow, 1);
                myItemList.clearCell(aRow, 2);
                myItemList.clearCell(aRow, 3);
                myItemList.getRowFormatter().setStylePrimaryName(aRow, ITEM_LIST_EMPTY_ROW_STYLE);
            }
        };
        myMembersEditorWrapper.add(myMembersList);

    }

    private void buildChildrenEditor() {
        myChildrenList = new GroupsListEditorView(myItemDataSource) {
            @Override
            protected void buildContent() {
                myItemList = new Grid(1, 3);
                myItemList.setWidth("100%");
                myItemList.setStylePrimaryName("item_list");
                myItemList.setHTML(0, 0, constants.groupPath());
                myItemList.setHTML(0, 1, constants.groupLabelLabel());
                myItemList.setHTML(0, 2, constants.groupDescription());
                myItemList.getColumnFormatter().setStyleName(0, "item_selector");
                myItemList.getRowFormatter().setStylePrimaryName(0, "item_list_content_row_title");

                myOuterPanel.setWidget(0, 0, myItemList);
                myItemList.addClickHandler(new ClickHandler() {
                    
                    public void onClick(ClickEvent aEvent) {
                        Cell theCell = myItemList.getCellForEvent(aEvent);
                        if(theCell!=null) {
                            final int theRow = theCell.getRowIndex();
                            final Group theSelectedItem = myRowItem.get(theRow);
                            if(theSelectedItem != null) {
                                History.newItem(ViewToken.GroupEditor.name() + ConsoleConstants.TOKEN_SEPARATOR + theSelectedItem.getUUID().getValue());
                            }
                        }
                    }
                });

            }

            @Override
            protected void fillInContentRow(int aRow, Group anItem) {
                myItemList.setWidget(aRow, 0, new Label(Group.buildGroupPath(anItem)));
                myItemList.setWidget(aRow, 1, new Label(anItem.getLabel()));
                myItemList.setWidget(aRow, 2, new Label(anItem.getDescription()));
                myItemList.getRowFormatter().setStylePrimaryName(aRow, "item_list_content_row");
            }

            @Override
            protected void fillInEmptyRow(int aRow) {
                myItemList.clearCell(aRow, 0);
                myItemList.clearCell(aRow, 1);
                myItemList.clearCell(aRow, 2);
                myItemList.getRowFormatter().setStylePrimaryName(aRow, ITEM_LIST_EMPTY_ROW_STYLE);
            }
        };
        myChildrenEditorWrapper.add(myChildrenList);

    }

    /**
     * Redirect user to list of groups.
     */
    protected void redirectItemList() {
        History.newItem(ViewToken.UsersManagement.toString());
    }

    public void setItem(Group anItem) {
        myItem = anItem;
        updateItemEditor();
        if (myItem != null) {
            myGroupFilter.setParentGroupUUID(myItem.getUUID());
            myUserFilter.setGroupUUID(myItem.getUUID());
        } else {
            myGroupFilter.setParentGroupUUID(null);
            myUserFilter.setGroupUUID(null);
        }
        updateChildrenEditor(myGroupFilter);

        updateMembersEditor(myUserFilter);
    }

    protected void updateItemEditor() {
        if (myGroupEditorWidget != null) {
            myEditorWrapper.remove(myGroupEditorWidget);
        }
        myGroupEditorWidget = new GroupEditorWidget(myItemDataSource, myItem.getUUID());
        myGroupEditorWidget.addCancelClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                updateItemEditor();
            }
        });
        myGroupEditorWidget.addSaveHandler(new AsyncHandler<Void>() {

            public void handleFailure(Throwable t) {
            }

            public void handleSuccess(Void result) {
                updateItemEditor();
            }
        });
        myEditorWrapper.add(myGroupEditorWidget);

    }

    protected void updateChildrenEditor(GroupFilter aGroupFilter) {
        /* Groups */
        if (aGroupFilter != null) {
            myItemDataSource.listItems(aGroupFilter, new AsyncHandler<ItemUpdates<Group>>() {
                public void handleFailure(Throwable aT) {
                    GWT.log("Unable to list children groups of: " + myItem.getName(), aT);
                    myChildrenList.setItems(null);
                }

                public void handleSuccess(ItemUpdates<Group> result) {
                    myChildrenList.setItems(result.getItems());
                };
            });
        } else {
            myChildrenList.setItems(null);
        }
    }

    protected void updateMembersEditor(UserFilter aUserFilter) {
        /* Members */
        if (aUserFilter != null) {
            // TODO handle paging
            myUserDataSource.listItems(aUserFilter, new AsyncHandler<ItemUpdates<User>>() {
                public void handleFailure(Throwable aT) {
                    GWT.log("Unable to list members of group: " + myItem.getName(), aT);
                    myMembersList.setItems(null);
                }

                public void handleSuccess(ItemUpdates<User> result) {
                    myMembersList.setItems(result.getItems());
                };
            });
        } else {
            myMembersList.setItems(null);
        }
    }

    @Override
    public String getLocationLabel() {
        return constants.group();
    }
}
