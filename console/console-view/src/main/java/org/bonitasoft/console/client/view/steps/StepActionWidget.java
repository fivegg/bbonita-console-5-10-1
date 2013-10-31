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
package org.bonitasoft.console.client.view.steps;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.controller.UserRightsManager;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepPriority;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.MenuChoicesPanel;
import org.bonitasoft.console.client.view.identity.UserFinderPanel;
import org.bonitasoft.console.security.client.privileges.RuleType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepActionWidget extends BonitaPanel implements ModelChangeListener {

    /**
     * The ASSIGN_TO_MULTIPLE defines
     */
    protected static final String ASSIGN_TO_MULTIPLE = constants.assignToMultiple();
    /**
     * The RESUME defines
     */
    protected static final String RESUME = constants.resume();
    /**
     * The SUSPEND defines
     */
    protected static final String SUSPEND = constants.suspend();
    /**
     * The UNASSIGN defines
     */
    protected static final String UNASSIGN = constants.unassign();
    /**
     * The PRIORITY defines
     */
    protected static final String PRIORITY = constants.priority();

    /**
     * The ASSIGN_TO_ME defines
     */
    protected static final String ASSIGN_TO_ME = constants.assignToMe();
    protected final FlowPanel myOuterPanel = new FlowPanel();
    protected static final String BUTTON_OTHERS_TITLE = "";
    protected static final String UPDATE_TITLE = constants.apply();
    protected static final String CANCEL_TITLE = constants.cancelButton();
    protected static final String ASSIGN_TO_MANY = constants.assignToMultipleTitle();

    protected final CustomMenuBar myMenuBar;
    protected final MenuChoicesPanel myActionChoicesPanel = new MenuChoicesPanel();
    protected StepItem myStep;
    protected final StepItemDataSource myStepDataSource;

    protected final CustomDialogBox myAssignToManyPopup;
    protected final Button myUpdateButton = new Button(UPDATE_TITLE);
    protected final Button myCancelButton = new Button(CANCEL_TITLE);
    protected final TextBox myNewCandidateTB = new TextBox();
    protected CustomDialogBox myUserSearchPopup;
    protected final UserFinderPanel myUserFinder;

    protected UserDataSource myUserDataSource;
    protected final ListBox myCandidates;
    protected final Label myCandidatesHeader;
    protected final CustomMenuBar myRemoveCandidateButton;
    protected final Label myErrorMessage;

    /**
     * Default constructor.
     * 
     * @param aDataSource
     * @param aStep
     */
    public StepActionWidget(StepItemDataSource aDataSource, StepItem aStep, UserDataSource aUserDataSource) {
        super();
        myStepDataSource = aDataSource;
        myUserDataSource = aUserDataSource;
        myUserFinder = new UserFinderPanel(false);
        myStep = aStep;
        
        myStep.addModelChangeListener(StepItem.STATE_PROPERTY, this);
        
        myErrorMessage = new Label();
        myErrorMessage.setStyleName("bonita_form_validation_error");

        myCandidatesHeader = new Label();
        myCandidatesHeader.setText(constants.currentCandidates());

        myMenuBar = new CustomMenuBar();
        initMenuBar();

        myCandidates = new ListBox(true);
        myRemoveCandidateButton = new CustomMenuBar();
        myAssignToManyPopup = buildAssignToManyPopup();

        myOuterPanel.add(myMenuBar);
        this.initWidget(myOuterPanel);
        myOuterPanel.setStylePrimaryName("bos_step_action_widget");
    }

    protected void initMenuBar() {
        if (StepState.FAILED.equals(myStep.getState())  && UserRightsManager.getInstance().isAllowed(RuleType.SKIP_STEP,myStep.getUUID().getStepDefinitionUUID())){
            addSkipEntry();
        } else {
            if (UserRightsManager.getInstance().isAllowed(RuleType.ASSIGN_TO_ME_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                addAssignToMeEntry();
            }
            myMenuBar.addItem(BUTTON_OTHERS_TITLE, buildSubMenu());
        }
        if (UserRightsManager.getInstance().isAllowed(RuleType.CHANGE_PRIORITY_STEP, myStep.getUUID().getStepDefinitionUUID())) {
            myMenuBar.addItem(PRIORITY, buildPriorityMenu());
        }
    }

    protected void addAssignToMeEntry() {
        // Assign to me, menu entry.
        FlowPanel theAssignToMeContainer = new FlowPanel();
        Image theAssignToMeIcon = new Image(PICTURE_PLACE_HOLDER);
        Label theAssignToMeLink = new Label(ASSIGN_TO_ME);
        theAssignToMeContainer.add(theAssignToMeIcon);
        theAssignToMeContainer.add(theAssignToMeLink);

        // Associate CSS styles
        theAssignToMeIcon.setStylePrimaryName("assign_to_me_icon_in_menu");

        myMenuBar.addItem(theAssignToMeContainer, new Command() {
            public void execute() {
                myStepDataSource.assignStepToMe(myStep);
            }
        });

    }

    protected void addSkipEntry() {
        // Assign to me, menu entry.
        FlowPanel theSkipContainer = new FlowPanel();
        Image theSkipIcon = new Image(PICTURE_PLACE_HOLDER);
        Label theSkipLink = new Label(constants.skipStepActionLabel());
        theSkipContainer.add(theSkipIcon);
        theSkipContainer.add(theSkipLink);

        // Associate CSS styles
        theSkipIcon.setStylePrimaryName("skip_icon_in_menu");

        myMenuBar.addItem(theSkipContainer, new Command() {
            public void execute() {
                myStepDataSource.skipStep(myStep);
            }
        });

    }    
    
    protected Widget buildAssignToMeMenu() {
        final FlowPanel theAssignToMeContainer = new FlowPanel();
        final Image theAssignToMeIcon = new Image(PICTURE_PLACE_HOLDER);
        final Label theAssignToMeLink = new Label(ASSIGN_TO_ME);
        theAssignToMeContainer.add(theAssignToMeIcon);
        theAssignToMeContainer.add(theAssignToMeLink);

        // Associate CSS styles
        theAssignToMeIcon.setStylePrimaryName("assign_to_me_icon_in_menu");

        return theAssignToMeContainer;
    }

    protected MenuChoicesPanel buildPriorityMenu() {

        MenuChoicesPanel theSubMenu = new MenuChoicesPanel();

        // Normal menu entry.
        final FlowPanel theNormalContainer = new FlowPanel();
        final Image theNormalIcon = new Image(PICTURE_PLACE_HOLDER);
        final Label theNormalLink = new Label(constants.normal());
        if (StepPriority.NORMAL.equals(myStep.getPriority())) {
            theNormalLink.addStyleName("current_priority_label");
        }
        theNormalContainer.add(theNormalIcon);
        theNormalContainer.add(theNormalLink);

        // High menu entry.
        final FlowPanel theHighContainer = new FlowPanel();
        final Image theHighIcon = new Image(PICTURE_PLACE_HOLDER);
        final Label theHighLink = new Label(constants.high());
        if (StepPriority.HIGH.equals(myStep.getPriority())) {
            theHighLink.addStyleName("current_priority_label");
        }
        theHighContainer.add(theHighIcon);
        theHighContainer.add(theHighLink);

        // Urgent menu entry.
        final FlowPanel theUrgentContainer = new FlowPanel();
        final Image theUrgentIcon = new Image(PICTURE_PLACE_HOLDER);
        final Label theUrgentLink = new Label(constants.urgent());
        if (StepPriority.URGENT.equals(myStep.getPriority())) {
            theUrgentLink.addStyleName("current_priority_label");
        }
        theUrgentContainer.add(theUrgentIcon);
        theUrgentContainer.add(theUrgentLink);

        // Associate CSS styles
        theNormalIcon.setStylePrimaryName(CSSClassManager.getPriorityIconStyle(StepPriority.NORMAL.name().toLowerCase()));
        theHighIcon.setStylePrimaryName(CSSClassManager.getPriorityIconStyle(StepPriority.HIGH.name().toLowerCase()));
        theUrgentIcon.setStylePrimaryName(CSSClassManager.getPriorityIconStyle(StepPriority.URGENT.name().toLowerCase()));

        theSubMenu.addChoice(theSubMenu.new MenuChoice(theNormalContainer, new Command() {
            public void execute() {
                myStepDataSource.setStepPriority(myStep, 0);
                theNormalLink.addStyleName("current_priority_label");
                theHighLink.removeStyleName("current_priority_label");
                theUrgentLink.removeStyleName("current_priority_label");
            }
        }));

        theSubMenu.addChoice(theSubMenu.new MenuChoice(theHighContainer, new Command() {
            public void execute() {
                myStepDataSource.setStepPriority(myStep, 1);
                theHighLink.addStyleName("current_priority_label");
                theNormalLink.removeStyleName("current_priority_label");
                theUrgentLink.removeStyleName("current_priority_label");
            }
        }));

        theSubMenu.addChoice(theSubMenu.new MenuChoice(theUrgentContainer, new Command() {
            public void execute() {
                myStepDataSource.setStepPriority(myStep, 2);
                theUrgentLink.addStyleName("current_priority_label");
                theNormalLink.removeStyleName("current_priority_label");
                theHighLink.removeStyleName("current_priority_label");
            }
        }));

        return theSubMenu;
    }

    /*
     * Build the menu.
     */
    protected MenuChoicesPanel buildSubMenu() {
        
        if (UserRightsManager.getInstance().isAllowed(RuleType.ASSIGN_TO_STEP, myStep.getUUID().getStepDefinitionUUID())) {
            addAssignToMultipleSubMenuEntry(myActionChoicesPanel);
        }

        if (UserRightsManager.getInstance().isAllowed(RuleType.UNASSIGN_STEP, myStep.getUUID().getStepDefinitionUUID())) {
            addUnassignSubMenuEntry(myActionChoicesPanel);
        }

        if (myStep.getState() == StepState.SUSPENDED) {
            if (UserRightsManager.getInstance().isAllowed(RuleType.RESUME_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                addResumeStepSubMenuEntry(myActionChoicesPanel);
            }
        } else {
            if (UserRightsManager.getInstance().isAllowed(RuleType.SUSPEND_STEP, myStep.getUUID().getStepDefinitionUUID())) {
                addSuspendStepSubMenuEntry(myActionChoicesPanel);
            }
        }
        return myActionChoicesPanel;
    }

    protected void addSuspendStepSubMenuEntry(MenuChoicesPanel aSubMenu) {
        // Suspend, menu entry.
        FlowPanel theSuspendContainer = new FlowPanel();
        Image theSuspendIcon = new Image(PICTURE_PLACE_HOLDER);
        Label theSuspendLink = new Label(SUSPEND);
        theSuspendContainer.add(theSuspendIcon);
        theSuspendContainer.add(theSuspendLink);
        // Associate CSS styles
        theSuspendIcon.setStylePrimaryName("suspend_icon_in_menu");
        aSubMenu.addChoice(aSubMenu.new MenuChoice(theSuspendContainer, new Command() {
            public void execute() {
                myStepDataSource.suspendStep(myStep);
            }
        }));

    }

    protected void addResumeStepSubMenuEntry(MenuChoicesPanel aSubMenu) {
        // Resume, menu entry.
        FlowPanel theResumeContainer = new FlowPanel();
        Image theResumeIcon = new Image(PICTURE_PLACE_HOLDER);
        Label theResumeLink = new Label(RESUME);
        theResumeContainer.add(theResumeIcon);
        theResumeContainer.add(theResumeLink);
        // Associate CSS styles
        theResumeIcon.setStylePrimaryName("resume_icon_in_menu");
        aSubMenu.addChoice(aSubMenu.new MenuChoice(theResumeContainer, new Command() {
            public void execute() {
                myStepDataSource.resumeStep(myStep);
            }
        }));
    }
    
    protected void addUnassignSubMenuEntry(MenuChoicesPanel aSubMenu) {
        // Unassign, menu entry.
        FlowPanel theUnassignContainer = new FlowPanel();
        Image theUnassignIcon = new Image(PICTURE_PLACE_HOLDER);
        Label theUnassignLink = new Label(UNASSIGN);
        theUnassignContainer.add(theUnassignIcon);
        theUnassignContainer.add(theUnassignLink);
        theUnassignIcon.setStylePrimaryName("unassign_icon_in_menu");
        aSubMenu.addChoice(aSubMenu.new MenuChoice(theUnassignContainer, new Command() {
            public void execute() {
                myStepDataSource.unassignStep(myStep);
            }
        }));
    }

    protected void addAssignToMultipleSubMenuEntry(MenuChoicesPanel aSubMenu) {
        // Assign to multiple, menu entry.
        FlowPanel theAssignToMultipleContainer = new FlowPanel();
        Image theAssignToMultipleIcon = new Image(PICTURE_PLACE_HOLDER);
        Label theAssignToMultipleLink = new Label(ASSIGN_TO_MULTIPLE);
        theAssignToMultipleContainer.add(theAssignToMultipleIcon);
        theAssignToMultipleContainer.add(theAssignToMultipleLink);

        theAssignToMultipleIcon.setStylePrimaryName("assign_to_icon_in_menu");
        aSubMenu.addChoice(aSubMenu.new MenuChoice(theAssignToMultipleContainer, new Command() {
            public void execute() {
                showAssignToManyPopup();
            }
        }));
    }

    /*
     * Display the popup menu.
     */
    protected void showAssignToManyPopup() {
        final Set<UserUUID> theBunchOfCandidates = myStep.getAssign();

        myCandidates.clear();

        for (UserUUID theUUID : theBunchOfCandidates) {
            myCandidates.addItem(theUUID.getValue());
        }

        // If the userdatasource is empty try to reload data to get completion
        // feature available.
        if (myUserDataSource.getSize() == 0) {
            myUserDataSource.reload();
        }

        myAssignToManyPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int anOffsetWidth, int anOffsetHeight) {
                int left = ((Window.getClientWidth() / 2) - (anOffsetWidth / 2));
                int top = myMenuBar.getAbsoluteTop();
                myAssignToManyPopup.setPopupPosition(left, top);
            }
        });
    }

    /**
     * Update the step assignation with the content of the popup.
     */
    protected void assignStepTo() {
        if (myCandidates != null && (myCandidates.getItemCount() != 0)) {

            HashSet<String> theSet = new HashSet<String>();
            for (int i = 0; i < myCandidates.getItemCount(); i++) {
                theSet.add(myCandidates.getValue(i));
            }
            myStepDataSource.assignStep(myStep, theSet);
            myAssignToManyPopup.hide();
        }

    }

    /**
     * Build the popup for assignation.
     */
    protected CustomDialogBox buildAssignToManyPopup() {

        // Create the buttons.
        HorizontalPanel theActionPanel = new HorizontalPanel();
        theActionPanel.add(myUpdateButton);
        theActionPanel.add(myCancelButton);

        // Create candidates panel.
        Grid theCandidatesPanel = new Grid(4, 2);
        theCandidatesPanel.setStylePrimaryName("bos_assign_to_many_candidates_panel");

        CustomMenuBar theAddCandidateButton = new CustomMenuBar();
        theAddCandidateButton.addItem(constants.add(), new Command() {
            public void execute() {
                addCandidateToList(myNewCandidateTB.getValue());
            }
        });

        CustomMenuBar theUserSearchButton = new CustomMenuBar();
        theUserSearchButton.addItem(constants.search() + "...", new Command() {

            public void execute() {
                if (myUserSearchPopup == null) {
                    myUserSearchPopup = buildUserSearchPopup();
                }
                myUserSearchPopup.center();
            }
        });

        VerticalPanel theVerticalActionsPanel = new VerticalPanel();
        theVerticalActionsPanel.add(theUserSearchButton);
        theVerticalActionsPanel.add(myRemoveCandidateButton);

        int theAddRow = 0;
        int theErrorMessageRow = 1;
        int theCandiatesHeaderRow = 2;
        int theCandidatesRow = 3;

        theCandidatesPanel.setWidget(theAddRow, 0, myNewCandidateTB);
        theCandidatesPanel.setWidget(theAddRow, 1, theAddCandidateButton);
        theCandidatesPanel.getCellFormatter().setHorizontalAlignment(theAddRow, 1, HasHorizontalAlignment.ALIGN_CENTER);

        theCandidatesPanel.setWidget(theErrorMessageRow, 0, myErrorMessage);
        theCandidatesPanel.setWidget(theCandiatesHeaderRow, 0, myCandidatesHeader);
        theCandidatesPanel.setWidget(theCandidatesRow, 0, myCandidates);
        theCandidatesPanel.getCellFormatter().setVerticalAlignment(theCandidatesRow, 1, HasVerticalAlignment.ALIGN_BOTTOM);
        theCandidatesPanel.setWidget(theCandidatesRow, 1, theVerticalActionsPanel);

        myCandidates.setVisibleItemCount(10);
        myCandidates.setWidth("100%");
        myRemoveCandidateButton.addItem(constants.remove(), new Command() {
            public void execute() {
                if (myCandidates != null && myCandidates.getSelectedIndex() > -1) {
                    // browse item in reverse order.
                    for (int i = myCandidates.getItemCount() - 1; i >= 0; i--) {
                        if (myCandidates.isItemSelected(i)) {
                            myCandidates.removeItem(i);
                        }
                    }
                }
            }
        });

        // Create a dialog box and set the caption text
        final CustomDialogBox theDialogBox = new CustomDialogBox();
        theDialogBox.setText(constants.assignStepToMultiple());

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        theDialogBox.setWidget(dialogContents);

        dialogContents.add(new HTML(ASSIGN_TO_MANY));

        dialogContents.add(theCandidatesPanel);
        dialogContents.add(theActionPanel);
        dialogContents.setCellHorizontalAlignment(theActionPanel, HasHorizontalAlignment.ALIGN_RIGHT);

        myUpdateButton.addClickHandler(new ClickHandler() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
             * .gwt .event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent aArg0) {
                assignStepTo();
            }
        });

        myCancelButton.addClickHandler(new ClickHandler() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
             * .gwt .event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent aArg0) {
                myAssignToManyPopup.hide();
            }
        });

        // Return the dialog box
        return theDialogBox;

    }

    protected CustomDialogBox buildUserSearchPopup() {
        final CustomDialogBox theResult = new CustomDialogBox(false, true);
        myUserFinder.addModelChangeListener(UserFinderPanel.ITEM_LIST_PROPERTY, this);
        myUserFinder.addModelChangeListener(UserFinderPanel.CANCEL_PROPERTY, this);
        theResult.add(myUserFinder);
        theResult.setText(constants.searchForAUser());
        return theResult;
    }

    protected void addCandidateToList(final String aCandidateUsername) {
        if (aCandidateUsername != null && aCandidateUsername.length() > 0 && myCandidates != null) {
            boolean alreadyInCandidates = false;
            for (int i = 0; i < myCandidates.getItemCount(); i++) {
                if (myCandidates.getValue(i).equals(aCandidateUsername)) {
                    alreadyInCandidates = true;
                }
            }
            if (!alreadyInCandidates) {
                myCandidates.addItem(aCandidateUsername);
                myNewCandidateTB.setValue(null);
                myErrorMessage.setText(null);
            } else {
                myErrorMessage.setText(constants.userAlreadyInList());
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
     * .bonitasoft.console.client.events.ModelChangeEvent)
     */
    @SuppressWarnings("unchecked")
    public void modelChange(ModelChangeEvent aEvt) {
        if (UserFinderPanel.ITEM_LIST_PROPERTY.equals(aEvt.getPropertyName())) {
            List<UserUUID> theNewValue = ((List<UserUUID>) aEvt.getNewValue());

            if (theNewValue != null && !theNewValue.isEmpty()) {
                String theUserName = myUserFinder.getItems().iterator().next().getUsername();
                addCandidateToList(theUserName);
            }
            myUserSearchPopup.hide();
        } else if (UserFinderPanel.CANCEL_PROPERTY.equals(aEvt.getPropertyName())) {
            myUserSearchPopup.hide();
        } else if (StepItem.STATE_PROPERTY.equals(aEvt.getPropertyName())) {
            StepState theNewState = (StepState) aEvt.getNewValue();
            switch (theNewState) {
            case READY:
            case SUSPENDED:
                myActionChoicesPanel.clearChoices();
                this.buildSubMenu();
                break;
            }
        }
    }
}
