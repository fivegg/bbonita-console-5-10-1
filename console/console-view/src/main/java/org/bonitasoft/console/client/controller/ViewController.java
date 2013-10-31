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
package org.bonitasoft.console.client.controller;

import java.util.ArrayList;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.model.reporting.ReportingDataSource;
import org.bonitasoft.console.client.reporting.ReportingConfiguration;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.AboutWidget;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.LocaleChooserWidget;
import org.bonitasoft.console.client.view.LogoutWidget;
import org.bonitasoft.console.client.view.UserIDWidget;
import org.bonitasoft.console.client.view.UserPreferencesWidget;
import org.bonitasoft.console.client.view.WidgetFactory.WidgetKey;
import org.bonitasoft.console.client.view.cases.AbstractCaseEditorWidget;
import org.bonitasoft.console.client.view.categories.CategoryBrowserWidget;
import org.bonitasoft.console.client.view.labels.CustomLabelListWidget;
import org.bonitasoft.console.client.view.labels.SystemLabelListWidget;
import org.bonitasoft.console.security.client.privileges.RuleType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This Class defines the main controller of the entire GWT application. It is
 * responsible for the interaction between the different widgets spread all over
 * the window.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class ViewController extends AbstractViewController implements ValueChangeHandler<String>, ModelChangeListener {

    private static final ViewController INSTANCE = new ViewController();

    protected SystemLabelListWidget mySystemLabelListWidget;
    protected CategoryBrowserWidget myCategoryListWidget;
    protected CustomLabelListWidget myCustomLabelListWidget;
    protected BonitaPanel myStatWidget;
    protected AbstractItemList<CaseUUID, CaseItem, CaseFilter> myCaseList;

    protected CaseFilter mySelectedLabelFilter;

    protected final ModelChangeListener myReportingModelChangeListener = new ModelChangeListener() {
        public void modelChange(ModelChangeEvent anEvt) {
            if (ReportingDataSource.REPORTING_CONFIGURATION_PROPERTY.equals(anEvt.getPropertyName())) {
                ReportingConfiguration theConfiguration = (ReportingConfiguration) anEvt.getNewValue();
                if (theConfiguration != null && theConfiguration.isUserReportingEnabled()) {
                    if (myStatWidget == null) {
                        // Create the widget that display the user statistics.
                        myStatWidget = myWidgetFactory.getWidget(WidgetKey.MY_STATS_KEY);
                    }
                    RootPanel theContainer;
                    theContainer = RootPanel.get("StatisticsViewerContainer");
                    if (theContainer != null && !myStatWidget.isAttached()) {
                        theContainer.add(myStatWidget);
                    }
                } else {
                    if (myStatWidget != null && myStatWidget.isAttached()) {
                        RootPanel theContainer;
                        theContainer = RootPanel.get("StatisticsViewerContainer");
                        if (theContainer != null) {
                            theContainer.remove(myStatWidget);
                        }
                    }
                }
            }
        }
    };

    /**
     * Get the ViewController instance.
     * 
     * @return the unique instance of the ViewController.
     */
    public static ViewController getInstance() {
        return INSTANCE;
    }

    protected ViewController() {
        super();
    }

    protected void createRefreshCaseListLink() {

        // Create the refresh widget
        myRefreshCaseListWidget = new Label();
        myRefreshCaseListWidget.setVisible(false);
        myRefreshCaseListWidget.addClickHandler(new ClickHandler() {

            /**
             * {@inheritDoc}
             */
            public void onClick(ClickEvent aArg0) {
                myDataModel.getCaseDataSource().getItem(new CaseUUID(getItemUUIDFromTokenParam(myCurrentTokenParam)), new AsyncHandler<CaseItem>() {
                    public void handleFailure(Throwable aT) {
                        redirectToCaseList();
                    }

                    public void handleSuccess(CaseItem aResult) {
                        if (aResult == null) {
                            redirectToCaseList();
                        }
                    }

                    private void redirectToCaseList() {
                        final LabelUUID theLabel = mySelectedLabelFilter.getLabel();
                        if (theLabel != null) {
                            History.newItem(ViewToken.CaseList + ConsoleConstants.TOKEN_SEPARATOR + ConsoleConstants.LABEL_TOKEN_PARAM_PREFIX + theLabel.getValue());
                        } else {
                            final Category theCategory = mySelectedLabelFilter.getCategory();
                            History.newItem(ViewToken.CaseList + ConsoleConstants.TOKEN_SEPARATOR + ConsoleConstants.CATEGORY_TOKEN_PARAM_PREFIX + theCategory.getUUID().getValue());
                        }
                    }
                });
            }
        });

    }

    /**
     * Display the view.
     */
    public void showView(UserProfile aUserProfile) {
        super.showView(aUserProfile);

        myDataModel.getReportingDataSource().addModelChangeListener(ReportingDataSource.REPORTING_CONFIGURATION_PROPERTY, myReportingModelChangeListener);
        // Create the widget that display the user statistics (if needed).
        myDataModel.getReportingDataSource().getReportingConfiguration(new AsyncHandler<ReportingConfiguration>() {
            public void handleFailure(Throwable anT) {
                // Nothing to do.
                GWT.log("Unable to get reporting configuration: ", anT);
            }

            public void handleSuccess(ReportingConfiguration aResult) {
                // This is handled by
                // myReportingConfigurationModelChangeListener.
            }
        });

        // Create the filters used to manage the changes of view.
        // Filter on labels + participants (default filter for a user).
        final ArrayList<LabelUUID> theLabels = new ArrayList<LabelUUID>();
        theLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(aUserProfile.getUsername())));
        final ArrayList<UserUUID> theParticipants = new ArrayList<UserUUID>();
        theParticipants.add(new UserUUID(aUserProfile.getUsername()));
        mySelectedLabelFilter = new CaseFilter(theLabels, null, null, null, 0, constants.defaultMaxDisplayedItems());
        mySelectedLabelFilter.setMaxElementCount(20);

        // Finally layout all the widgets inside the HTML page.
        RootPanel theContainer;
        theContainer = RootPanel.get("user_id");
        if (theContainer != null) {
            // Create the User header, i.e., the user ID and main navigation
            // header.
            theContainer.add(new UserIDWidget(myUserProfile));
        }
        theContainer = RootPanel.get("bos_locale_chooser");
        if (theContainer != null) {
            // Create the locale chooser.
            theContainer.add(new LocaleChooserWidget(myUserProfile));
        }
        theContainer = RootPanel.get("bos_user_preferences");
        if (theContainer != null) {
            // Create the user preference link.
            theContainer.add(new UserPreferencesWidget(myUserProfile));
        }
        theContainer = RootPanel.get("bos_about");
        if (theContainer != null) {
            // Create the about link.
            theContainer.add(new AboutWidget(aUserProfile));
        }
        if (myUserProfile.isAllowed(RuleType.LOGOUT)) {
            theContainer = RootPanel.get("bos_logout");
            if (theContainer != null) {
                // Create the about link.
                theContainer.add(new LogoutWidget(myUserProfile));
                // Display the parent
                theContainer = RootPanel.get("bos_logout_parent");
                if (theContainer != null) {
                    theContainer.removeStyleName("bos_empty_menu");
                }
            }
        }
        theContainer = RootPanel.get("SystemLabelBrowserContainer");
        if (theContainer != null) {
            // Create the panel that lists the system labels and the user custom
            // labels.
            mySystemLabelListWidget = (SystemLabelListWidget) myWidgetFactory.getWidget(WidgetKey.SYSTEMLABEL_BROWSER_KEY);
            mySystemLabelListWidget.setSelectedLabel(LabelModel.INBOX_LABEL);
            theContainer.add(mySystemLabelListWidget);
        }
        theContainer = RootPanel.get("CategoryBrowserContainer");
        if (theContainer != null) {
            // Create the panel that lists the system labels and the user custom
            // labels.
            myCategoryListWidget = (CategoryBrowserWidget) myWidgetFactory.getWidget(WidgetKey.CATEGORY_BROWSER_KEY);
            theContainer.add(myCategoryListWidget);
        }
        theContainer = RootPanel.get("UserLabelBrowserContainer");
        if (theContainer != null) {
            // Create the panel that lists the system labels and the user custom
            // labels.
            myCustomLabelListWidget = (CustomLabelListWidget) myWidgetFactory.getWidget(WidgetKey.CUSTOMLABEL_BROWSER_KEY);
            theContainer.add(myCustomLabelListWidget);
        }
        theContainer = RootPanel.get("MoreLabelAndCategoryBrowserContainer");
        if (theContainer != null) {
            // Create the panel that lists the system labels and the user custom
            // labels.
            theContainer.add(myWidgetFactory.getWidget(WidgetKey.MORELABEL_BROWSER_KEY));
        }
        theContainer = RootPanel.get("ProcessBrowserContainer");
        if (theContainer != null) {
            // Create the widget that allows a user to start a case.
            myStartCaseWidget = myWidgetFactory.getWidget(WidgetKey.START_CASE_KEY);
            theContainer.add(myStartCaseWidget);
        }

        theContainer = RootPanel.get("you_are_here");
        if (theContainer != null) {
            // Create the locator widget.
            myLocatorWidget = new HTML();
            theContainer.add(myLocatorWidget);
        }
        theContainer = RootPanel.get("MessageContainer");
        if (theContainer != null) {
            // Create the message widget.
            theContainer.add(myWidgetFactory.getWidget(WidgetKey.MESSAGE_KEY));
        }

        if (UserRightsManager.getInstance().hasSomeAdminRights()) {
            theContainer = RootPanel.get("ui_mode_selector");
            if (theContainer != null) {
                // UI mode selector.
                theContainer.getElement().setTitle(constants.adminModeTooltip());
                theContainer.getElement().setInnerHTML(constants.adminModeLink());
                // Make the parent visible the parent
                theContainer = RootPanel.get("ui_mode_selector_parent");
                if (theContainer != null) {
                    theContainer.removeStyleName("bos_empty_menu");
                }
            }
        } 

        theContainer = RootPanel.get("WidgetContainerContent");
        if (theContainer != null) {
            // mandatory to access the WidgetContainerContent before
            // WidgetContainer
            // to avoid
            // "A widget that has an existing parent widget may not be added to the detach list"
            // kind of gwt error.
            theContainer.add(new Label());
        }

        // Set the CSS style.
        theContainer = RootPanel.get("WidgetContainer");
        if (theContainer != null) {
            theContainer.setStylePrimaryName("center_panel");
        }

        // Add the Hidden refresh case list widget
        theContainer = RootPanel.get("WidgetRefreshCasesContainer");
        if (theContainer != null) {
            createRefreshCaseListLink();
            theContainer.add(myRefreshCaseListWidget);
        }

        // Hide the loading message and display the GUI.
        Element theElement;
        theElement = DOM.getElementById("loading");
        if (theElement != null) {
            theElement.getStyle().setProperty("display", "none");
        }
        theElement = RootPanel.getBodyElement();
        if (theElement != null) {
            theElement.setClassName("user_experience");
        }
        theElement = DOM.getElementById("default_view_container");
        if (theElement != null) {
            theElement.getStyle().setProperty("display", "");
        }

        // Manage web browser history.
        History.addValueChangeHandler(this);

        // Check the History to see if there is a token
        String token = History.getToken();
        // If there is no history token then init with Inbox.
        if (token.length() != 0) {
            updateViewAccordingToToken(token);
        } else {
//            updateHistory(ViewToken.CaseList.name(), ConsoleConstants.LABEL_TOKEN_PARAM_PREFIX + LabelModel.INBOX_LABEL.getUUID().getValue());
            displayDefaultViewAccordingToPrivileges();
        }
    }

    protected void widgetSelectionChange(WidgetKey aWidgetKey) {
        super.widgetSelectionChange(aWidgetKey);
        // As the view changes the selection becomes useless.
        myDataModel.getCaseDataSource().getCaseSelection().clearSelection();

        if (mySystemLabelListWidget != null) {
            mySystemLabelListWidget.setSelectedLabel(null);
        }
        if (myCustomLabelListWidget != null) {
            myCustomLabelListWidget.setSelectedLabel(null);
        }
        if (myCategoryListWidget != null) {
            myCategoryListWidget.setSelectedItem(null);
        }

    }

    @SuppressWarnings("unchecked")
    protected void displayCaseList(Category aCategory) {
        // As the view changes the selection becomes useless.
        myDataModel.getCaseDataSource().getCaseSelection().clearSelection();

        mySelectedLabelFilter.setCategory(aCategory);
        mySelectedLabelFilter.setLabels(null);
        mySelectedLabelFilter.setStartingIndex(0);

        myDataModel.getCaseDataSource().setItemFilter(mySelectedLabelFilter);

        // Reuse my case list widget.
        if (myCaseList == null) {
            myCaseList = (AbstractItemList<CaseUUID, CaseItem, CaseFilter>) myWidgetFactory.getWidget(WidgetKey.CASE_LIST_KEY);
        }
        if (myCurrentlyDisplayedWidget == null || !myCaseList.equals(myCurrentlyDisplayedWidget)) {
            // Update the view only if the requested widget is available.
            switchWidgetToDisplay(myCaseList);
        }

        // Ensure that the right label is selected in the UI.
        if (mySystemLabelListWidget != null) {
            mySystemLabelListWidget.setSelectedLabel(null);
        }
        if (myCustomLabelListWidget != null) {
            myCustomLabelListWidget.setSelectedLabel(null);
        }
        if (myCategoryListWidget != null) {
            myCategoryListWidget.setSelectedItem(aCategory);
        }
        if (myLocatorWidget != null) {
            myLocatorWidget.setText(myCurrentlyDisplayedWidget.getLocationLabel());
        }

    }

    @SuppressWarnings("unchecked")
    protected void displayCaseList(LabelModel aLabel) {
        // As the view changes the selection becomes useless.
        myDataModel.getCaseDataSource().getCaseSelection().clearSelection();

        // Update the CaseDataSource
        final ArrayList<LabelUUID> theLabels = new ArrayList<LabelUUID>();
        final LabelModel theLabelModel;
        theLabelModel = myDataModel.getLabelDataSource().getLabel(aLabel.getUUID().getValue());

        theLabels.add(theLabelModel.getUUID());
        mySelectedLabelFilter.setLabels(theLabels);
        mySelectedLabelFilter.setCategory(null);
        mySelectedLabelFilter.setStartingIndex(0);
        if (!theLabelModel.equals(LabelModel.ALL_LABEL) && !theLabelModel.equals(LabelModel.MY_CASES_LABEL)) {
            // In case of Inbox force to search in journal as there is no point
            // searching in history.
            mySelectedLabelFilter.setSearchInHistory(false);
        }
        myDataModel.getCaseDataSource().setItemFilter(mySelectedLabelFilter);

        // Reuse my case list widget.
        if (myCaseList == null) {
            myCaseList = (AbstractItemList<CaseUUID, CaseItem, CaseFilter>) myWidgetFactory.getWidget(WidgetKey.CASE_LIST_KEY);
        }
        if (myCurrentlyDisplayedWidget == null || !myCaseList.equals(myCurrentlyDisplayedWidget)) {
            // Update the view only if the requested widget is available.
            switchWidgetToDisplay(myCaseList);
        }

        // updateHistory(ViewToken.CaseList.name(),
        // aLabel.getUUID().toString());
        // Ensure that the right label is selected in the UI.
        if (aLabel.isSystemLabel()) {
            if (mySystemLabelListWidget != null) {
                mySystemLabelListWidget.setSelectedLabel(aLabel);
            }
            if (myCustomLabelListWidget != null) {
                myCustomLabelListWidget.setSelectedLabel(null);
            }
        } else {
            if (myCustomLabelListWidget != null) {
                myCustomLabelListWidget.setSelectedLabel(aLabel);
            }
            if (mySystemLabelListWidget != null) {
                mySystemLabelListWidget.setSelectedLabel(null);
            }
        }

        if (myCategoryListWidget != null) {
            myCategoryListWidget.setSelectedItem(null);
        }
        if (myLocatorWidget != null) {
            myLocatorWidget.setText(myCurrentlyDisplayedWidget.getLocationLabel());
        }

    }

    /**
     * Update the view based on the history navigation.
     * 
     * @param aToken
     */
    protected void updateViewAccordingToToken(String aToken) {

        final String theTokenKey;
        final String theParam;
        ViewToken theViewToken;
        try {
            int theSeparatorIndex = aToken.indexOf(ConsoleConstants.TOKEN_SEPARATOR);
            if (theSeparatorIndex != -1) {
                // There is a token and a parameter.
                theTokenKey = aToken.substring(0, theSeparatorIndex);
                theParam = aToken.substring(theSeparatorIndex + 1);
            } else {
                // There is no parameter.
                theTokenKey = aToken;
                theParam = null;
            }
            theViewToken = ViewToken.valueOf(theTokenKey);
            switch (theViewToken) {

            case CaseList:
                final LabelModel theLabelToDisplay;
                final String theLabelName;
                final String theCategoryId;
                if (theParam != null && !"".equals(theParam)) {
                    if (theParam.startsWith(ConsoleConstants.LABEL_TOKEN_PARAM_PREFIX)) {
                        theLabelName = theParam.split(ConsoleConstants.LABEL_TOKEN_PARAM_PREFIX)[1];
                        theCategoryId = null;
                    } else {
                        theCategoryId = theParam.split(ConsoleConstants.CATEGORY_TOKEN_PARAM_PREFIX)[1];
                        theLabelName = null;
                    }
                } else {
                    // If no label is specified then use Inbox as the default.
                    theLabelName = LabelModel.INBOX_LABEL.getUUID().getValue();
                    theCategoryId = null;
                }
                if (theLabelName != null) {
                    theLabelToDisplay = myDataModel.getLabelDataSource().getLabel(theLabelName);
                    if (theLabelToDisplay != null) {
                        displayCaseList(theLabelToDisplay);
                    } else {
                        myDataModel.getLabelDataSource().addModelChangeListener(LabelDataSource.SYSTEM_LABEL_LIST_PROPERTY, new ModelChangeListener() {
                            public void modelChange(ModelChangeEvent aEvt) {
                                myDataModel.removeModelChangeListener(LabelDataSource.SYSTEM_LABEL_LIST_PROPERTY, this);
                                LabelModel theLabelToDisplay = myDataModel.getLabelDataSource().getLabel(theLabelName);
                                if (theLabelToDisplay != null) {
                                    displayCaseList(theLabelToDisplay);
                                }
                            }
                        });
                        myDataModel.getLabelDataSource().addModelChangeListener(LabelDataSource.USER_LABEL_LIST_PROPERTY, new ModelChangeListener() {
                            public void modelChange(ModelChangeEvent aEvt) {
                                myDataModel.removeModelChangeListener(LabelDataSource.USER_LABEL_LIST_PROPERTY, this);
                                LabelModel theLabelToDisplay = myDataModel.getLabelDataSource().getLabel(theLabelName);
                                if (theLabelToDisplay != null) {
                                    displayCaseList(theLabelToDisplay);
                                }
                            }
                        });
                    }
                } else {
                    // handle a category
                    myDataModel.getCategoryDataSource().getItem(new CategoryUUID(theCategoryId), new AsyncHandler<Category>() {
                        public void handleFailure(Throwable aT) {
                            // should redirect to inbox?
                        }

                        public void handleSuccess(Category aResult) {
                            if (aResult != null) {
                                displayCaseList(aResult);
                            }
                        };
                    });
                }

                break;
            case CaseEditor:
                if (theParam != null) {
                    displayCaseEditor(theParam);
                } else {
                    throw new IllegalArgumentException("Invalid URL: " + aToken);
                }
                break;
            case UserStat:
                widgetSelectionChange(WidgetKey.USER_STATS_KEY);
                break;
            case Labels:
                widgetSelectionChange(WidgetKey.LABEL_MANAGEMENT_KEY);
                break;
            case Preferences:
                widgetSelectionChange(WidgetKey.USER_SETTINGS_KEY);
                break;
            // case ExternalContent:
            // if(theParam!=null){
            // widgetSelectionChange(WidgetKey.MONITOR_KEY,theParam);
            // }
            // break;
            case CaseInstantiation:
                // TODO when it will be possible to have a confirmation before
                // instantiation in case of instantiation with no forms
                // designed.
                displayCaseInstantiationPanel(theParam);
                break;
            default:
                GWT.log("Not yet handled: " + theViewToken.name(), new IllegalArgumentException("Invalid URL: " + aToken));
                throw new IllegalArgumentException("Invalid URL: " + aToken);
            }
            // Keep the reference to the token.
            myCurrentTokenName = theTokenKey;
            myCurrentTokenParam = theParam;
        } catch (IllegalArgumentException e) {
            displayDefaultViewAccordingToPrivileges();
        } catch (Throwable t) {
            Window.alert(t.getMessage());
        }

    }

    protected void displayCaseEditor(final String aCaseId) {
        final String theCaseId = getItemUUIDFromTokenParam(aCaseId);
        boolean searchInHistory = isTokenPointingToHistory(aCaseId);

        if (theCaseId != null) {
            if (myDataModel.getCaseDataSource().getItemFilter() == null) {
                // coming from a bookmark or refresh button of the
                // browser
                mySelectedLabelFilter.setSearchInHistory(searchInHistory, false);
                myDataModel.getCaseDataSource().setItemFilter(mySelectedLabelFilter, false);
            } else {
                myDataModel.getCaseDataSource().getItemFilter().setSearchInHistory(searchInHistory, false);
            }
            myDataModel.getCaseDataSource().getItem(new CaseUUID(theCaseId), new AsyncHandler<CaseItem>() {
                public void handleFailure(Throwable anT) {
                    GWT.log("Unable to update view to display: CaseEditor/" + aCaseId, anT);
                    updateViewAccordingToToken(ViewToken.CaseList + ConsoleConstants.TOKEN_SEPARATOR + ConsoleConstants.LABEL_TOKEN_PARAM_PREFIX
                            + LabelModel.INBOX_LABEL.getUUID().getValue());
                }

                public void handleSuccess(CaseItem aCase) {
                    if (aCase != null) {
                        // When editing a case, it should be
                        // considered as the only
                        // one selected.
                        myDataModel.getCaseDataSource().getCaseSelection().clearSelection();
                        myDataModel.getCaseDataSource().getCaseSelection().addItemToSelection(aCase.getUUID());

                        // Get the 'Case Editor' widget.
                        final AbstractCaseEditorWidget theCaseEditorWidget;
                        theCaseEditorWidget = (AbstractCaseEditorWidget) myWidgetFactory.getWidget(WidgetKey.CASE_EDITOR_KEY);

                        // Update the view only if the requested
                        // widget is available.
                        switchWidgetToDisplay(theCaseEditorWidget);
                        theCaseEditorWidget.setCaseToDisplay(aCase);
                        if (myLocatorWidget != null) {
                            myLocatorWidget.setText(theCaseEditorWidget.getLocationLabel());
                        }
                    } else {
                        GWT.log("Unable to update view to display: CaseEditor/" + aCaseId, new NullPointerException());
                    }
                }
            });
        }
        
    }

    protected void displayDefaultViewAccordingToPrivileges() {
        final LabelModel theInboxLabel = myDataModel.getLabelDataSource().getLabel(LabelModel.INBOX_LABEL.getUUID().getValue());
        displayCaseList(theInboxLabel);
    }

}
