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
package org.bonitasoft.console.client.view;

import java.util.HashMap;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.model.DataModel;
import org.bonitasoft.console.client.model.processes.ProcessSelection;
import org.bonitasoft.console.client.view.cases.AdminCaseDataEditorWidget;
import org.bonitasoft.console.client.view.cases.AdminCaseEditorWidget;
import org.bonitasoft.console.client.view.cases.AdminCaseList;
import org.bonitasoft.console.client.view.cases.CaseEditorWidget;
import org.bonitasoft.console.client.view.cases.CaseInstantiationWidget;
import org.bonitasoft.console.client.view.cases.CaseList;
import org.bonitasoft.console.client.view.categories.CategoriesListWidget;
import org.bonitasoft.console.client.view.categories.CategoryBrowserWidget;
import org.bonitasoft.console.client.view.identity.GroupEditorPanel;
import org.bonitasoft.console.client.view.identity.IdentityManagementView;
import org.bonitasoft.console.client.view.identity.RoleEditorPanel;
import org.bonitasoft.console.client.view.identity.UserEditor;
import org.bonitasoft.console.client.view.labels.CustomLabelListWidget;
import org.bonitasoft.console.client.view.labels.LabelsManagementList;
import org.bonitasoft.console.client.view.labels.MoreLabelAndCategoryListWidget;
import org.bonitasoft.console.client.view.labels.SystemLabelListWidget;
import org.bonitasoft.console.client.view.processes.ProcessEditor;
import org.bonitasoft.console.client.view.processes.ProcessList;
import org.bonitasoft.console.client.view.reporting.MonitoringView;
import org.bonitasoft.console.client.view.reporting.UserStatsView;

import com.google.gwt.core.client.GWT;

/**
 * This class is responsible for the creation of widgets on demand. A single instance of each widget will be created then stored
 * for later request.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class WidgetFactory {

    public enum WidgetKey {
        CASE_LIST_KEY, USER_SETTINGS_KEY, MY_STATS_KEY, START_CASE_KEY, SYSTEMLABEL_BROWSER_KEY, CUSTOMLABEL_BROWSER_KEY, CATEGORY_BROWSER_KEY, LABEL_MANAGEMENT_KEY, CASE_INSTANTIATION, CASE_EDITOR_KEY, MONITOR_KEY, MESSAGE_KEY, PROCESS_LIST_KEY, ADMIN_CASE_LIST_KEY, ADMIN_CASE_EDITOR_KEY, USER_STATS_KEY, CASE_DATA_UPDATE_KEY, USERS_MANAGEMENT_KEY, PROCESS_EDITOR_KEY, CATEGORIES_MANAGEMENT_KEY, MORELABEL_BROWSER_KEY, GLOBAL_CONFIGURATION_KEY, LABELS_CONFIGURATION_KEY, SYNC_CONFIGURATION_KEY, CASE_LISTS_CONFIGURATION_KEY, GROUP_EDITOR_KEY, ROLE_EDITOR_KEY, USER_EDITOR_KEY, THEMES_CONFIGURATION_KEY;
    }

    // Unique instance of the factory.
    protected static WidgetFactory INSTANCE;

    protected static final ConsoleConstants constants = (ConsoleConstants) GWT.create(ConsoleConstants.class);

    protected HashMap<WidgetKey, BonitaPanel> myWidgetMap = new HashMap<WidgetKey, BonitaPanel>();
    protected DataModel myDataModel;

    /**
     * 
     * Default constructor. This constructor is private to avoid the creation of an instance by another class.
     * 
     * @param aDataModel
     */
    protected WidgetFactory(DataModel aDataModel) {
        super();
        myDataModel = aDataModel;
    }

    public static WidgetFactory getInstance(DataModel aDataModel) {
        if (INSTANCE == null) {
            INSTANCE = new WidgetFactory(aDataModel);
        }
        return INSTANCE;
    }

    /**
     * 
     * @param aWidgetKey the key that identifies a widget class (allowed values are the WidgetFactory.STEP_LIST_KEY)
     * @return the widget or null if not found.
     */
    public BonitaPanel getWidget(WidgetKey aWidgetKey) {
        final BonitaPanel theWidget;
        if (myWidgetMap.containsKey(aWidgetKey)) {
            theWidget = myWidgetMap.get(aWidgetKey);

        } else {
            theWidget = buildWidget(aWidgetKey);

        }
        if (theWidget != null && theWidget instanceof SetupPanel) {
            ((SetupPanel) theWidget).update();
        }
        return theWidget;
    }

    protected BonitaPanel buildWidget(WidgetKey aWidgetKey) {
        BonitaPanel theWidget = null;
        switch (aWidgetKey) {
        case ADMIN_CASE_LIST_KEY:
            theWidget = new AdminCaseList(myDataModel.getMessageDataSource(), myDataModel.getCaseDataSource().getCaseSelection(), myDataModel.getCaseDataSource(), myDataModel.getProcessDataSource(), myDataModel.getLabelDataSource(),
                    myDataModel.getCategoryDataSource());
            myWidgetMap.put(WidgetKey.ADMIN_CASE_LIST_KEY, theWidget);
            break;
        case CASE_INSTANTIATION:
            theWidget = new CaseInstantiationWidget();
            myWidgetMap.put(WidgetKey.CASE_INSTANTIATION, theWidget);
            break;
        case CASE_EDITOR_KEY:
            theWidget = new CaseEditorWidget(myDataModel.getCaseDataSource(), myDataModel.getCaseDataSource().getCaseSelection(), myDataModel.getLabelDataSource(), myDataModel.getStepDataSource(), myDataModel.getProcessDataSource(),
                    myDataModel.getUserDataSource(), myDataModel.getCategoryDataSource());
            myWidgetMap.put(WidgetKey.CASE_EDITOR_KEY, theWidget);
            break;
        case PROCESS_EDITOR_KEY:
            theWidget = new ProcessEditor(myDataModel.getProcessDataSource(), myDataModel.getCategoryDataSource());
            myWidgetMap.put(WidgetKey.PROCESS_EDITOR_KEY, theWidget);
            break;
        case USER_EDITOR_KEY:
            theWidget = new UserEditor(myDataModel.getMessageDataSource(), myDataModel.getUserDataSource(), myDataModel.getRoleDataSource(), myDataModel.getGroupDataSource(), myDataModel.getUserMetadataDataSource());
            myWidgetMap.put(WidgetKey.USER_EDITOR_KEY, theWidget);
            break;
        case GROUP_EDITOR_KEY:
            theWidget = new GroupEditorPanel(myDataModel.getMessageDataSource(), myDataModel.getGroupDataSource(), myDataModel.getUserDataSource());
            myWidgetMap.put(WidgetKey.GROUP_EDITOR_KEY, theWidget);
            break;
        case ROLE_EDITOR_KEY:
            theWidget = new RoleEditorPanel(myDataModel.getMessageDataSource(), myDataModel.getRoleDataSource(), myDataModel.getUserDataSource());
            myWidgetMap.put(WidgetKey.ROLE_EDITOR_KEY, theWidget);
            break;
        case ADMIN_CASE_EDITOR_KEY:
            theWidget = new AdminCaseEditorWidget(myDataModel.getCaseDataSource(), myDataModel.getCaseDataSource().getCaseSelection(), myDataModel.getLabelDataSource(), myDataModel.getStepDataSource(), myDataModel.getProcessDataSource(),
                    myDataModel.getUserDataSource(), myDataModel.getCategoryDataSource(), myDataModel.getEventDataSource(), myDataModel.getMessageDataSource());
            myWidgetMap.put(WidgetKey.ADMIN_CASE_EDITOR_KEY, theWidget);
            break;
        case SYSTEMLABEL_BROWSER_KEY:
            theWidget = new SystemLabelListWidget(myDataModel.getLabelDataSource());
            myWidgetMap.put(WidgetKey.SYSTEMLABEL_BROWSER_KEY, theWidget);
            break;
        case CATEGORY_BROWSER_KEY:
            theWidget = new CategoryBrowserWidget(myDataModel.getCategoryDataSource());
            myWidgetMap.put(WidgetKey.SYSTEMLABEL_BROWSER_KEY, theWidget);
            break;
        case CUSTOMLABEL_BROWSER_KEY:
            theWidget = new CustomLabelListWidget(myDataModel.getLabelDataSource());
            myWidgetMap.put(WidgetKey.CUSTOMLABEL_BROWSER_KEY, theWidget);
            break;
        case MORELABEL_BROWSER_KEY:
            theWidget = new MoreLabelAndCategoryListWidget(myDataModel.getLabelDataSource(), myDataModel.getCategoryDataSource());
            myWidgetMap.put(WidgetKey.MORELABEL_BROWSER_KEY, theWidget);
            break;
        case LABEL_MANAGEMENT_KEY:
            // theWidget = new LabelManagementView(myDataModel);
            theWidget = new LabelsManagementList(myDataModel.getMessageDataSource(), myDataModel.getLabelDataSource().getItemSelection(), myDataModel.getLabelDataSource());
            myWidgetMap.put(WidgetKey.LABEL_MANAGEMENT_KEY, theWidget);
            break;
        case MESSAGE_KEY:
            theWidget = new MessageWidget(myDataModel);
            myWidgetMap.put(WidgetKey.MESSAGE_KEY, theWidget);
            break;
        case MONITOR_KEY:
            theWidget = new MonitoringView(myDataModel.getReportingDataSource(), myDataModel.getUserDataSource(), myDataModel.getProcessDataSource(), myDataModel.getStepDefinitionDataSource());
            myWidgetMap.put(WidgetKey.MONITOR_KEY, theWidget);
            break;
        case USER_STATS_KEY:
            theWidget = new UserStatsView(myDataModel.getReportingDataSource());
            myWidgetMap.put(WidgetKey.USER_STATS_KEY, theWidget);
            break;
        case MY_STATS_KEY:
            theWidget = new CollapsibleWidget(constants.dashboard(), new DashboardPanel(BonitaConsole.userProfile, myDataModel.getReportingDataSource()));
            myWidgetMap.put(WidgetKey.MY_STATS_KEY, theWidget);
            break;
        case START_CASE_KEY:
            theWidget = new CollapsibleWidget(constants.startCase(), new StartCasePanel(myDataModel.getProcessDataSource()), true);
            myWidgetMap.put(WidgetKey.START_CASE_KEY, theWidget);
            break;
        case USER_SETTINGS_KEY:
            theWidget = new UserSettingsEditionView(BonitaConsole.userProfile, myDataModel.getMessageDataSource(), myDataModel.getUserDataSource(), myDataModel.getReportingDataSource());
            myWidgetMap.put(WidgetKey.USER_SETTINGS_KEY, theWidget);
            break;
        case CASE_LIST_KEY:
            theWidget = new CaseList(myDataModel.getMessageDataSource(), myDataModel.getCaseDataSource().getCaseSelection(), myDataModel.getCaseDataSource(), myDataModel.getProcessDataSource(), myDataModel.getLabelDataSource(),
                    myDataModel.getCategoryDataSource());
            myWidgetMap.put(WidgetKey.CASE_LIST_KEY, theWidget);
            break;
        case PROCESS_LIST_KEY:
            theWidget = new ProcessList(myDataModel.getMessageDataSource(), (ProcessSelection) myDataModel.getProcessDataSource().getItemSelection(), myDataModel.getProcessDataSource());
            myWidgetMap.put(WidgetKey.PROCESS_LIST_KEY, theWidget);
            break;
        case CATEGORIES_MANAGEMENT_KEY:
            theWidget = new CategoriesListWidget(myDataModel.getMessageDataSource(), myDataModel.getCategoryDataSource());
            myWidgetMap.put(WidgetKey.CATEGORIES_MANAGEMENT_KEY, theWidget);
            break;
        case CASE_DATA_UPDATE_KEY:
            theWidget = new AdminCaseDataEditorWidget(myDataModel.getCaseDataSource(), myDataModel.getMessageDataSource(), myDataModel.getCaseDataSource().getCaseSelection());
            myWidgetMap.put(WidgetKey.CASE_DATA_UPDATE_KEY, theWidget);
            break;
        case USERS_MANAGEMENT_KEY:
            theWidget = new IdentityManagementView(myDataModel.getMessageDataSource(), myDataModel.getUserDataSource(), myDataModel.getGroupDataSource(), myDataModel.getRoleDataSource(), myDataModel.getUserMetadataDataSource());
            myWidgetMap.put(WidgetKey.USERS_MANAGEMENT_KEY, theWidget);
            break;
        case LABELS_CONFIGURATION_KEY:
            theWidget = new LabelsConfigurationEditionPanel(myDataModel.getLabelDataSource(), myDataModel.getCaseDataSource(), myDataModel.getMessageDataSource());
            myWidgetMap.put(WidgetKey.LABELS_CONFIGURATION_KEY, theWidget);
            break;
        case GLOBAL_CONFIGURATION_KEY:
            theWidget = new ReportingConfigurationEditionPanel(myDataModel.getReportingDataSource(), myDataModel.getCaseDataSource(), myDataModel.getMessageDataSource());
            myWidgetMap.put(WidgetKey.GLOBAL_CONFIGURATION_KEY, theWidget);
            break;
        case SYNC_CONFIGURATION_KEY:
            theWidget = new ResyncPanel(myDataModel.getCaseDataSource(), myDataModel.getMessageDataSource());
            myWidgetMap.put(WidgetKey.SYNC_CONFIGURATION_KEY, theWidget);
            break;
        default:
            break;
        }
        return theWidget;
    }

}