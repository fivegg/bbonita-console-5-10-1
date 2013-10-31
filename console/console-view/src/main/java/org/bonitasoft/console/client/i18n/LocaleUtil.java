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
package org.bonitasoft.console.client.i18n;

import java.util.Date;

import org.bonitasoft.console.client.cases.CasesConfiguration.Columns;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.model.ItemSelection.ItemSelector;
import org.bonitasoft.console.client.model.cases.CaseSelection.CaseSelector;
import org.bonitasoft.console.client.model.identity.RoleSelection.RoleSelector;
import org.bonitasoft.console.client.model.processes.ProcessSelection.ProcessSelector;
import org.bonitasoft.console.client.processes.BonitaProcess.BonitaProcessState;
import org.bonitasoft.console.client.reporting.TimeUnit;
import org.bonitasoft.console.client.steps.StepDefinition.StepType;
import org.bonitasoft.console.client.steps.StepItem.StepPriority;
import org.bonitasoft.console.client.steps.StepItem.StepState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LocaleUtil {

    private static final ConsoleConstants constants = (ConsoleConstants) GWT.create(ConsoleConstants.class);
    private static final ConsolePatterns patterns = (ConsolePatterns) GWT.create(ConsolePatterns.class);

    public static String translate(CaseSelector aCaseSelector) {
        String theResult;
        switch (aCaseSelector) {
        case All:
            theResult = constants.selectorAll();
            break;
        case None:
            theResult = constants.selectorNone();
            break;
        case Starred:
            theResult = constants.selectorStarred();
            break;
        case Unstarred:
            theResult = constants.selectorUnstarred();
            break;
        default:
            theResult = "";
            break;
        }
        return theResult;
    }

    public static String translate(ProcessSelector aSelector) {
        String theResult;
        switch (aSelector) {
        case All:
            theResult = constants.selectorAll();
            break;
        case None:
            theResult = constants.selectorNone();
            break;
        default:
            theResult = "";
            break;
        }
        return theResult;
    }

    public static String translate(RoleSelector aSelector) {
        String theResult;
        switch (aSelector) {
        case All:
            theResult = constants.selectorAll();
            break;
        case None:
            theResult = constants.selectorNone();
            break;
        default:
            theResult = "";
            break;
        }
        return theResult;
    }

    public static String translate(LabelUUID aLabelUUID) {

        if (LabelModel.INBOX_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.inboxLabelName();
        }
        if (LabelModel.ALL_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.allLabelName();
        }
        if (LabelModel.MY_CASES_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.myCasesLabelName();
        }
        if (LabelModel.STAR_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.starredLabelName();
        }
        if (LabelModel.ADMIN_ALL_CASES.getUUID().equals(aLabelUUID)) {
            return constants.adminCaseList();
        }
        if (LabelModel.ATRISK_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.atRiskLabelName();
        }
        if (LabelModel.OVERDUE_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.overDueLabelName();
        }
        return aLabelUUID.getValue();
    }

    public static String translate(StepState aStepState) {
        switch (aStepState) {
        case ABORTED:
            return constants.aborted();
        case CANCELLED:
            return constants.cancelled();
        case EXECUTING:
            return constants.executing();
        case FINISHED:
            return constants.finished();
        case READY:
            return constants.ready();
        case SUSPENDED:
            return constants.suspended();
        case SKIPPED:
            return constants.skipped();
        case FAILED:
            return constants.failed();            
        default:
            return aStepState.name().toLowerCase();
        }
    }

    public static String getLinkTitle(LabelUUID aLabelUUID) {
        if (LabelModel.INBOX_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.listInboxTooltip();
        }
        if (LabelModel.ALL_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.listAllTooltip();
        }
        if (LabelModel.MY_CASES_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.listMyCasesTooltip();
        }
        if (LabelModel.STAR_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.listStarredTooltip();
        }
        if (LabelModel.ATRISK_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.listAtRiskTooltip();
        }
        if (LabelModel.OVERDUE_LABEL.getUUID().equals(aLabelUUID)) {
            return constants.listOverdueTooltip();
        }
        return patterns.browseToLabel(aLabelUUID.getValue());
    }

    public static String translate(BonitaProcessState aState) {
        switch (aState) {
        case ARCHIVED:
            return constants.archivedState();
        case DISABLED:
            return constants.disabledState();
        case ENABLED:
            return constants.enabledState();

        default:
            return aState.name().toLowerCase();
        }
    }

    public static String translate(ItemSelector aSelector) {
        String theResult;
        switch (aSelector) {
        case All:
            theResult = constants.selectorAll();
            break;
        case None:
            theResult = constants.selectorNone();
            break;
        default:
            theResult = "";
            break;
        }
        return theResult;
    }

    /**
     * @param aColumn
     * @return
     */
    public static String getColumnTitle(Columns aColumn) {
        if (aColumn != null) {
            switch (aColumn) {
            case APPLICATION_COLUMN:
                return constants.caseListApplicationLinkColumnDescription();
            case DESCRIPTION_COLUMN:
                return constants.caseListCaseDescriptionColumnDescription();
            case SELECT_COLUMN:
                return constants.caseListSelectDescriptionColumnDescription();
            case STAR_COLUMN:
                return constants.caseListStarIconDescriptionColumnDescription();
            case STEP_DESCRIPTION_COLUMN:
                return constants.caseListStepDescriptionColumnDescription();
            case STEP_STATE_COLUMN:
                return constants.caseListStepSateColumnDescription();
            case STEP_PRIORITY_COLUMN:
                return constants.caseListStepPriorityColumnDescription();
            case STEP_ASSIGN_COLUMN:
                return constants.caseListStepAssigneeColumnDescription();
            case UPDATE_COLUMN:
                return constants.caseListUpdateTimeDescriptionColumnDescription();
            case CATEGORIES_COLUMN:
                return constants.caseListCategoriesDescriptionColumnDescription();
            case LABELS_COLUMN:
                return constants.caseListLabelsDescriptionColumnDescription();
            case CASE_STATE_COLUMN:
                return constants.caseListCaseStateDescriptionColumnDescription();
            case STEP_NAME_COLUMN:
                return constants.caseListStepNameDescriptionColumnDescription();
            default:
                return aColumn.name();
            }
        } else {
            return "";
        }
    }

    public static String getPriorityLabel(StepPriority aStepPriority) {
        switch (aStepPriority) {
        case NORMAL:
            return constants.normal();
        case HIGH:
            return constants.high();
        case URGENT:
            return constants.urgent();
        default:
            return aStepPriority.name().toLowerCase();
        }
    }

    /**
     * @param aUnit
     * @return
     */
    public static String translate(TimeUnit aUnit) {
        switch (aUnit) {
        case DAY:
            return constants.day();
        case HOUR:
            return constants.hour();
        case MINUTE:
            return constants.minute();
        case MONTH:
            return constants.month();
        case WEEK:
            return constants.week();
        case YEAR:
            return constants.year();
        default:
            return aUnit.name().toLowerCase();
        }
    }

    /**
     * @param aType
     * @return
     */
    public static String translate(StepType aType) {
        switch (aType) {
        case Automatic:
            return constants.stepTypeAutomatic();
        case Human:
            return constants.stepTypeHuman();
        default:
            return aType.name().toLowerCase();
        }
    }
    
    /**
     * @param aDate
     * @return
     */
    public static String shortDateFormat(final Date aDate) {
        return DateTimeFormat.getFormat(constants.dateShortFormat()).format(aDate);
    }
    
}
