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
package org.bonitasoft.console.client.controller;

import java.util.ArrayList;
import java.util.Set;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.common.Environment;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.i18n.I18N;
import org.bonitasoft.console.client.model.DataModel;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.view.AbstractItemList;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.WidgetFactory;
import org.bonitasoft.console.client.view.WidgetFactory.WidgetKey;
import org.bonitasoft.console.client.view.cases.CaseInstantiationWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This Class defines the main controller of the entire GWT application. It is
 * responsible for the interaction between the different widgets spread all over
 * the window.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public abstract class AbstractViewController implements ValueChangeHandler<String>, ModelChangeListener, I18N {

    protected static final String USER_ID_ELEM_ID = "user_id";
    protected static final String THEME_CHOOSER_ELEM_ID = "bos_theme_chooser";
    protected static final String LOCALE_CHOOSER_ELEM_ID = "bos_locale_chooser";
    protected static final String USER_PREFERENCES_ELEM_ID = "bos_user_preferences";
    protected static final String ABOUT_ELEM_ID = "bos_about";
    protected static final String LOGOUT_ELEM_ID = "bos_logout";
    protected static final String START_CASE_CONTAINER_ELEM_ID = "StartACaseContainer";
    protected static final String YOU_ARE_HERE_ELEM_ID = "you_are_here";
    protected static final String MESSAGE_CONTAINER_ELEM_ID = "MessageContainer";
    protected static final String UI_MODE_SELECTOR_ELEM_ID = "ui_mode_selector";
    protected static final String WIDGET_CONTAINER_CONTENT_ELEM_ID = "WidgetContainerContent";
    protected static final String DEFAULT_VIEW_CONTAINER_ELEM_ID = "default_view_container";
    protected static final String LOADING_ELEM_ID = "loading";
    protected static final String WIDGET_REFRESH_CASES_CONTAINER_ELEM_ID = "WidgetRefreshCasesContainer";
    protected static final String WIDGET_CONTAINER_ELEM_ID = "WidgetContainer";

    protected BonitaPanel myCurrentlyDisplayedWidget;

    protected WidgetFactory myWidgetFactory;

    protected DataModel myDataModel;

    protected BonitaPanel myStartCaseWidget;
    protected BonitaPanel myAdminWidget;
    protected AbstractItemList<CaseUUID, CaseItem, CaseFilter> myCaseList;

    protected String myCurrentTokenName;
    protected String myCurrentTokenParam;

    protected Label myRefreshCaseListWidget;

    protected HTML myLocatorWidget;
    protected UserProfile myUserProfile;
    protected Widget myClearBothWidget;

    protected static ArrayList<String> LABELS;
    protected static ArrayList<String> KEYS;
    protected static ArrayList<String> CONTAINERS;
    protected static Set<String> PARENT_MENUS;
    private RootPanel myCentralPanelContainer;

    protected AbstractViewController() {
        myClearBothWidget = new FlowPanel();
        myClearBothWidget.getElement().setAttribute("style", "clear: both;");
        GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void onUncaughtException(Throwable e) {
                GWT.log("Uncaught exception!", e);
            }
        });
    }

    protected abstract void createRefreshCaseListLink();

    protected String getItemUUIDFromTokenParam(String aCurrentTokenParam) {
        String theCaseId = null;
        String[] params;
        if (aCurrentTokenParam.startsWith(ConsoleConstants.HISTORY_TOKEN_PARAM_PREFIX)) {
            params = aCurrentTokenParam.split(ConsoleConstants.HISTORY_TOKEN_PARAM_PREFIX);
            if (params.length == 2) {
                theCaseId = params[1];
            }
        } else {
            params = aCurrentTokenParam.split(ConsoleConstants.JOURNAL_TOKEN_PARAM_PREFIX);
            if (params.length == 2) {
                theCaseId = params[1];
            }
        }
        return theCaseId;
    }

    protected boolean isTokenPointingToHistory(String aTokenParam) {
        return aTokenParam.startsWith(ConsoleConstants.HISTORY_TOKEN_PARAM_PREFIX);
    }

    /**
     * Display the view.
     */
    public void showView(UserProfile aUserProfile) {
        // Store user profile
        myUserProfile = aUserProfile;

        // Setup user rights.
        UserRightsManager.getInstance().updateUserRights(aUserProfile);

        // Build the model.
        myDataModel = Environment.getDataModel(myUserProfile);

        // Register to events.
        myDataModel.addModelChangeListener(DataModel.TIMEOUT_PROPERTY, this);
        myDataModel.addModelChangeListener(DataModel.MISSING_PRIVILEGES_PROPERTY, this);

        // Get the factory instance
        myWidgetFactory = Environment.getWidgetFactory();
    }

    protected void widgetSelectionChange(WidgetKey aWidgetKey) {
        // Get the widget to be displayed.
        BonitaPanel theWidget = myWidgetFactory.getWidget(aWidgetKey);
        if (theWidget != null) {
            // Update the view only if the requested widget is available.
            switchWidgetToDisplay(theWidget);

            if (myLocatorWidget != null) {
                myLocatorWidget.setText(theWidget.getLocationLabel());
            }
        } else {
            myDataModel.getMessageDataSource().addErrorMessage(messages.unableToDisplaySelectedWidget());
        }
    }

    /**
     * Add an entry in the History.<br>
     * The token will be of the form '#aToken + "/" + aParameter'.<br>
     * If the parameter is null or empty then the token will be of the form
     * '#aToken.
     * 
     * @param aTokenName
     * @param aParameter
     */
    protected void updateHistory(String aTokenName, String aParameter) {
        String theExtraParameter;
        if (aParameter != null && !aParameter.equals("")) {
            theExtraParameter = ConsoleConstants.TOKEN_SEPARATOR + aParameter;
        } else {
            theExtraParameter = "";
        }

        // Update the web browser history.
        History.newItem(aTokenName + theExtraParameter);
    }

    /**
     * Update the widget currently displayed to the one given in parameter. If
     * null is passed in, then nothing will be done. No error will be generated
     * though.
     * 
     * @param theWidgetToDisplay
     */
    protected void switchWidgetToDisplay(BonitaPanel theWidgetToDisplay) {
        if (theWidgetToDisplay != null) {
            if(myCentralPanelContainer == null) {
                myCentralPanelContainer = RootPanel.get("WidgetContainerContent");
            }
            if (myCentralPanelContainer != null) {
                // Remove the former widget from the window.
                if (myCurrentlyDisplayedWidget != null) {
                    myCentralPanelContainer.remove(myCurrentlyDisplayedWidget);
                }
                if (myClearBothWidget != null) {
                    myCentralPanelContainer.remove(myClearBothWidget);
                }
            }

            myCurrentlyDisplayedWidget = theWidgetToDisplay;

            // Display the new widget.
            if (myCentralPanelContainer != null) {
                // Add the new widget inside the window.
                if (myCurrentlyDisplayedWidget != null) {
                    myCentralPanelContainer.add(myCurrentlyDisplayedWidget);
                }
                if (myClearBothWidget != null) {
                    myCentralPanelContainer.add(myClearBothWidget);
                }
            }
        }
    }

    /**
     * History management.
     */
    public void onValueChange(ValueChangeEvent<String> aEvent) {
        if(aEvent.getValue()==null) {
            displayDefaultViewAccordingToPrivileges();
        } else if (myCurrentTokenName == null || (!(aEvent.getValue().equals(myCurrentTokenName) || (aEvent.getValue().equals(myCurrentTokenName + ConsoleConstants.TOKEN_SEPARATOR + myCurrentTokenParam))))) {
            // Update the view only in case of different token.
            // Calling twice the same URL will NOT make the UI to be refreshed.
            updateViewAccordingToToken(aEvent.getValue());
        }
    }

    /**
     * Update the view based on the history navigation.
     * 
     * @param aToken
     */
    protected abstract void updateViewAccordingToToken(String aToken);

    public void modelChange(ModelChangeEvent aEvt) {
        if (DataModel.TIMEOUT_PROPERTY.equals(aEvt.getPropertyName())) {
            Window.Location.reload();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.bonitasoft.console.client.events.ProcessSelectionListener#
     * notifyProcessSelectionChange(org.bonitasoft.console.client.BonitaProcess)
     */
    public void notifyProcessSelectionChange(BonitaPanel aSource, BonitaProcess aProcess) {
        if (aProcess != null && aSource != null) {
            // Be sure the start of the case will not show up with admin rights.
            myDataModel.getCaseDataSource().getItemFilter().setWithAdminRights(false);

        }
    }

    protected void displayCaseInstantiationPanel(final String aProcessUUID) {
        if (aProcessUUID != null) {
            myDataModel.getProcessDataSource().getItem(new BonitaProcessUUID(aProcessUUID, null), new AsyncHandler<BonitaProcess>() {
                public void handleFailure(Throwable aT) {
                    GWT.log("Unable to display the case instantiation panel for process: " + aProcessUUID);

                }

                public void handleSuccess(BonitaProcess aResult) {
                    if (aResult != null) {
                        widgetSelectionChange(WidgetKey.CASE_INSTANTIATION);
                        CaseInstantiationWidget caseInstantiationWidget = (CaseInstantiationWidget) myCurrentlyDisplayedWidget;
                        caseInstantiationWidget.setProcess(aResult);
                        if (myLocatorWidget != null) {
                            myLocatorWidget.setText(myCurrentlyDisplayedWidget.getLocationLabel());
                        }
                    }
                }
            });
        }
    }

    protected abstract void displayDefaultViewAccordingToPrivileges();
}
