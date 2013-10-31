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
package org.bonitasoft.console.client.view.steps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.UserRightsManager;
import org.bonitasoft.console.client.events.ActionItemsHandler;
import org.bonitasoft.console.client.events.RemoveItemsHandler;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.steps.EventDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.steps.EventFilter;
import org.bonitasoft.console.client.steps.EventItem;
import org.bonitasoft.console.client.steps.EventUUID;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.MenuChoicesPanel;
import org.bonitasoft.console.security.client.privileges.RuleType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class AdminStepActionWidget extends StepActionWidget {

    protected final EventDataSource myEventDataSource;
    protected CustomDialogBox myEventsListDialog;
    protected EventsListEditorView myEventsList;
    protected final EventFilter myEventFilter = new EventFilter(0, 1000);

    /**
     * Default constructor.
     */
    public AdminStepActionWidget(StepItemDataSource aDataSource, StepItem aStep, UserDataSource aUserDataSource, EventDataSource anEventDataSource) {
        super(aDataSource, aStep, aUserDataSource);
        myEventDataSource = anEventDataSource;
        if (aStep != null) {
            myEventFilter.setStepUUID(aStep.getUUID());
            myEventFilter.setCaseUUID(aStep.getCase().getUUID());
        }
    }

    protected void initMenuBar() {
        final UserRightsManager theUserRightsManager = UserRightsManager.getInstance();
        if (StepState.FAILED.equals(myStep.getState())){
            addSkipEntry();           
        } else {        
            if (myStep.isTask()) {
                if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.ASSIGN_TO_ME_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                    addAssignToMeEntry();
                }
                
                myMenuBar.addItem(BUTTON_OTHERS_TITLE, buildSubMenu());
                
                if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.CHANGE_PRIORITY_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                    myMenuBar.addItem(PRIORITY, buildPriorityMenu());
                }
            } else {
                final MenuChoicesPanel theSubMenu = new MenuChoicesPanel();
                addListEventsSubMenuEntry(theSubMenu);
                myMenuBar.addItem(BUTTON_OTHERS_TITLE, theSubMenu);
            }
        }
        
    }

    /*
     * Build the menu.
     */
    protected MenuChoicesPanel buildSubMenu() {

        final UserRightsManager theUserRightsManager = UserRightsManager.getInstance();
        if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.ASSIGN_TO_STEP, myStep.getUUID().getStepDefinitionUUID())) {
            addAssignToMultipleSubMenuEntry(myActionChoicesPanel);
        }

        if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.UNASSIGN_STEP, myStep.getUUID().getStepDefinitionUUID())) {
            addUnassignSubMenuEntry(myActionChoicesPanel);
        }

        if (myStep.getState() == StepState.SUSPENDED) {
            if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.RESUME_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                addResumeStepSubMenuEntry(myActionChoicesPanel);
            }
        } else {
            if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.SUSPEND_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                addSuspendStepSubMenuEntry(myActionChoicesPanel);
            }
            if (theUserRightsManager.isAdmin() || theUserRightsManager.isAllowed(RuleType.SKIP_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                addSkipStepSubMenuEntry(myActionChoicesPanel);
            }
            addListEventsSubMenuEntry(myActionChoicesPanel);
        }
        return myActionChoicesPanel;
    }

    protected void addListEventsSubMenuEntry(MenuChoicesPanel aSubMenu) {
        // List events, menu entry.
        FlowPanel theActionContainer = new FlowPanel();
        Image theActionIcon = new Image(PICTURE_PLACE_HOLDER);
        Label theActionLink = new Label(constants.showEventsStepActionLabel());
        theActionContainer.add(theActionIcon);
        theActionContainer.add(theActionLink);
        // Associate CSS styles
        theActionIcon.setStylePrimaryName("bos_showStepEvents_icon_in_menu");
        aSubMenu.addChoice(aSubMenu.new MenuChoice(theActionContainer, new Command() {
            public void execute() {
                showEventsList();
            }
        }));
    }

    protected void showEventsList() {
        if (myEventsListDialog == null) {
            buildEventsListDialog();
        }
        myEventsListDialog.center();
        myEventDataSource.listItems(myEventFilter, new AsyncHandler<ItemUpdates<EventItem>>() {

            public void handleSuccess(ItemUpdates<EventItem> aResult) {
                myEventsList.setItems(aResult.getItems());
            }

            public void handleFailure(Throwable aT) {
                GWT.log("Unable to list ovents of step", aT);
                myEventsList.setItems(null);
            }
        });
    }

    protected void buildEventsListDialog() {
        myEventsListDialog = new CustomDialogBox(false, true);
        final FlowPanel theDialogContent = new FlowPanel();
        myEventsList = new EventsListEditorView(myEventDataSource);
        myEventsList.addRemoveHandler(new RemoveItemsHandler<EventItem>() {
            public void removeItemsRequested(Collection<EventItem> anItemSelection) {
                if (anItemSelection != null && !anItemSelection.isEmpty()) {
                    final List<EventUUID> theItemsToDelete = new ArrayList<EventUUID>();
                    for (EventItem theEventItem : anItemSelection) {
                        theItemsToDelete.add(theEventItem.getUUID());
                    }
                    myEventDataSource.getItemFilter().setStepUUID(myStep.getUUID());
                    myEventDataSource.deleteItems(theItemsToDelete, new AsyncHandler<ItemUpdates<EventItem>>() {

                        public void handleSuccess(ItemUpdates<EventItem> aResult) {
                            myEventsList.setItems(aResult.getItems());
                        }

                        public void handleFailure(Throwable aT) {
                            GWT.log("Unable to delete events.", aT);
                        }
                    });
                }
            }
        });
        myEventsList.addActionHandler(new ActionItemsHandler<EventItem>() {

            public void executeAction(String anActionName, Collection<EventItem> anItemSelection) {
                if (EventsListEditorView.EXECUTE_EVENT.equals(anActionName)) {
                    if (anItemSelection != null && anItemSelection.size() == 1) {
                        myEventDataSource.executeEvent(anItemSelection.iterator().next().getUUID(), myEventFilter, new AsyncHandler<ItemUpdates<EventItem>>() {
                            public void handleFailure(Throwable aT) {
                                GWT.log("Unable to execute events.", aT);
                            }

                            public void handleSuccess(ItemUpdates<EventItem> aResult) {
                                if(aResult!=null && aResult.getNbOfItems()>0) {
                                    myEventsList.setItems(aResult.getItems());   
                                } else {
                                    myEventsList.setItems(null);
                                }
                            }
                        });
                    }
                } else if (EventsListEditorView.UPDATE_EVENT.equals(anActionName)) {
                    if (anItemSelection != null && anItemSelection.size() == 1) {
                        final EventItem theEvent = anItemSelection.iterator().next();
                        myEventDataSource.updateItem(theEvent.getUUID(), theEvent, new AsyncHandler<EventItem>() {
                            public void handleFailure(Throwable aT) {
                                GWT.log("Unable to update events.", aT);
                            }

                            public void handleSuccess(EventItem aResult) {
                                // myEventsList.setItems(aResult.getItems());
                                myEventsListDialog.hide();
                                myEventsList.setItems(null);
                            }
                        });
                    }
                }
            }
        });
        final CustomMenuBar theCloseButton = new CustomMenuBar();
        theCloseButton.addStyleName("bos_dialog_buttons");
        theCloseButton.addItem(constants.close(), new Command() {
            public void execute() {
                myEventsListDialog.hide();
                myEventsList.setItems(null);
            }
        });
        theDialogContent.add(myEventsList);
        theDialogContent.add(theCloseButton);
        myEventsListDialog.add(theDialogContent);
        myEventsListDialog.setText(patterns.eventListDialogTitle(myStep.getName()));

    }

    protected void addSkipStepSubMenuEntry(MenuChoicesPanel aSubMenu) {
        // Skip, menu entry.
        FlowPanel theActionContainer = new FlowPanel();
        Image theActionIcon = new Image(PICTURE_PLACE_HOLDER);
        Label theActionLink = new Label(constants.skipStepActionLabel());
        theActionContainer.add(theActionIcon);
        theActionContainer.add(theActionLink);
        // Associate CSS styles
        theActionIcon.setStylePrimaryName("skip_icon_in_menu");
        aSubMenu.addChoice(aSubMenu.new MenuChoice(theActionContainer, new Command() {
            public void execute() {
                myStepDataSource.skipStep(myStep);
            }
        }));
    }
    
    /*
     * Timers
     */
//    protected void buildTimerMenubar() {
//        // Show events, menu entry.
//        FlowPanel theContainer = new FlowPanel();
//        Image theExecuteIcon = new Image(PICTURE_PLACE_HOLDER);
//        Label theExecuteLink = new Label(constants.executeNow());
//        theContainer.add(theExecuteIcon);
//        theContainer.add(theExecuteLink);
//
//        // Associate CSS styles
//        theExecuteIcon.setStylePrimaryName("bos_showStepEvents_icon_in_menu");
//
//        myMenuBar.addItem(theContainer, new Command() {
//            public void execute() {
//                executeStepEvent();
//            }
//        });
////        myMenuBar.addItem(constants.eventUpdateExecutionDate(), new Command() {
////            public void execute() {
////           displayEventEditorPopup();     
////            }
////        });
//        final MenuChoicesPanel theSubMenu = new MenuChoicesPanel();
//        theSubMenu.addChoice(theSubMenu.new MenuChoice(new Label(constants.eventUpdateExecutionDate()), new Command() {
//            public void execute() {
//                displayEventEditorPopup();   
//            }
//        }));
//        theSubMenu.addChoice(theSubMenu.new MenuChoice(new Label(constants.cancel()), new Command() {
//            public void execute() {
//                myStepDataSource.cancelTimer(myStep.getUUID(), new AsyncHandler<StepItem>() {
//                    public void handleFailure(Throwable aT) {
//                        // TODO Auto-generated method stub
//                        
//                    }
//                    public void handleSuccess(StepItem aResult) {
//                        // TODO Auto-generated method stub
//                        
//                    }
//                });
//            }
//        }));
//        myMenuBar.addItem(BUTTON_OTHERS_TITLE, theSubMenu);
//
//    }

//    protected void displayEventEditorPopup() {
//        final CustomDialogBox theDialog = new CustomDialogBox();
//        final FlowPanel theDialogContent = new FlowPanel();
//        final DateBox theDateBox = new DateBox();
//        final CustomMenuBar theUpdateButton = new CustomMenuBar();
//        final CustomMenuBar theCancelButton = new CustomMenuBar();
//        final FlowPanel theButtonsWrapper = new FlowPanel();
//        
//        theUpdateButton.addItem(constants.update(), new Command() {
//            
//            public void execute() {
//                if(theDateBox.getValue()!=null) {
//                    myStepDataSource.updateTimer(myStep.getUUID(), theDateBox.getValue(), new AsyncHandler<StepItem>(){
//                        public void handleFailure(Throwable aT) {
//                            GWT.log("Unable to upate timer.", aT);
//                        }
//                        public void handleSuccess(StepItem aResult) {
//                            theDialog.hide();
//                        }
//                    });
//                }
//            }
//        });
//
//        theCancelButton.addItem(constants.hide(), new Command() {
//            public void execute() {
//                theDateBox.setValue(null);
//                theDialog.hide();
//            }
//        });
//        
//        myEventDataSource.listItems(myEventFilter, new AsyncHandler<ItemUpdates<EventItem>>() {
//            public void handleFailure(Throwable aT) {
//                // TODO Auto-generated method stub
//                
//            }
//            public void handleSuccess(ItemUpdates<EventItem> aResult) {
//                if(aResult!=null && aResult.getNbOfItems()>0) {
//                    for (EventItem theEvent : aResult.getItems()) {
//                        if(theEvent.getType() == EventType.TIMER) {
//                            theDateBox.setValue(theEvent.getExecutionDate());
//                        }
//                    }
//                }
//            }
//        });
//        
//        theButtonsWrapper.add(theUpdateButton);
//        theButtonsWrapper.add(theCancelButton);
//        
//        theDialogContent.add(theDateBox);
//        theDialogContent.add(theButtonsWrapper);
//        
//        theDialog.add(theDialogContent);
//        theDialog.setText(constants.eventUpdateExecutionDate());
//        
//        theDialog.center();
//        theDialog.show();        
//    }


//    protected void executeStepEvent() {
//        myStepDataSource.executeTimer(myStep.getUUID(), null);
//    }

//    /*
//     * Automatic steps 
//     */
//    protected void addShowEventsEntry() {
//        // Show events, menu entry.
//        FlowPanel theLinkContainer = new FlowPanel();
//        Image theAssignToMeIcon = new Image(PICTURE_PLACE_HOLDER);
//        Label theAssignToMeLink = new Label(constants.showEventsStepActionLabel());
//        theLinkContainer.add(theAssignToMeIcon);
//        theLinkContainer.add(theAssignToMeLink);
//
//        // Associate CSS styles
//        theAssignToMeIcon.setStylePrimaryName("bos_showStepEvents_icon_in_menu");
//
//        myMenuBar.addItem(theLinkContainer, new Command() {
//            public void execute() {
//                showEventsList();
//            }
//        });
//    }
}
