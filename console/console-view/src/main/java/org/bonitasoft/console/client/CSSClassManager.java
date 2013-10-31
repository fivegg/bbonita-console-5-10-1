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
package org.bonitasoft.console.client;

import java.util.Set;

import org.bonitasoft.console.client.cases.CaseItem.CaseItemState;
import org.bonitasoft.console.client.processes.BonitaProcess.BonitaProcessState;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.users.UserUUID;

/**
 * This class is a utility class that gather information related to the css style.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class CSSClassManager {

    /* Text */
    public static final String LINK_LABEL = "link_label";
    public static final String BACK_TO_LINK_LABEL = "back_to_link_label";
    public static final String TITLE_STYLE = "bos_title_text";
    public static final String INFORMATIVE_TEXT = "informative_text";
    public static final String VALIDATION_ERROR_MESSAGE = "bos_validation_error";

    /* Step assignation icons */
    public static final String SOLO = "solo_icon";
    public static final String GROUP = "group_icon";
    public static final String SOLO_GROUP = "solo_group_icon";

    /* Step state icons */
    public static final String READY_EXECUTING = "ready_executing_icon";

    /* menus */
    public static final String POPUP_MENU_ENTRY = "popup_menu_entry";

    /* filter/search */
    public static final String SEARCH_ICON = "bos_search_icon";
    public static final String SEARCH_CLEAR_ICON = "bos_search_clear_icon";
    public static final String CLEAR_ICON = "bos_clear_icon";

    /* group panel */
    public static final String GROUP_PANEL = "bos_group_panel";
    public static final String GROUP_PANEL_CAPTION = "bos_group_panel_caption";
    public static final String GROUP_PANEL_CONTENT = "bos_group_panel_content";
    public static final String GROUP_PANEL_ACTION_CAPTION = "bos_group_panel_action_caption";

    /* rounded panel */
    public static final String ROUNDED_PANEL = "bos_rounded_panel";

    /* comments */
    public static final String COMMENT_ICON_STYLE = "bos_comment_icon";

    /* general */
    public static final String FLOAT_RIGHT = "bos_float_right";
    public static final String FLOAT_LEFT = "bos_float_left";
    public static final String CONTENT_STYLE = "bos_content";
    public static final String LOADING_STYLE = "loading";
    public static final String DESCRIPTION_TEXT_STYLE = "bos_description_text";
    public static final String VALIDATION_OK_MESSAGE = "bos_allow";
    public static final String SECTION_CONTENT_STYLE = "bos_section_body";
    public static final String FORM_LABEL_COLUMN_STYLE = "bos_form_label_column";
    public static final String FORM_INPUT_COLUMN_STYLE = "bos_form_input_column";
    public static final String FORM_MANDATORY_COLUMN_STYLE = "bos_form_mandatory_column";
    public static final String PDF_BUTTON = "bos_pdf_button";

    /**
     * Get the CSS style to associate to the step'state icon.
     * 
     * @param aState
     * @return the CSS style corresponding to the step's state.
     */
    public static String getStepStateIconStyle(StepState aState) {
        if (StepState.READY.equals(aState)) {
            return "c6";
        }
        if (StepState.FINISHED.equals(aState)) {
            return "df";
        }
        if (StepState.SUSPENDED.equals(aState)) {
            return "suspended_step_icon";
        }
        if (StepState.CANCELLED.equals(aState)) {
            return "cancelled_step_icon";
        }
        if (StepState.ABORTED.equals(aState)) {
            return "aborted_step_icon";
        }
        if (StepState.EXECUTING.equals(aState)) {
            return "executing_step_icon";
        }
        if (StepState.SKIPPED == aState) {
            return "skipped_step_icon";
        }
        if (StepState.FAILED == aState) {
            return "failed_step_icon";            
        }        
        return aState.name();
    }

    /**
     * Get the CSS style to associate to the assignment's icon.
     * 
     * @param anAssignment
     * @return the CSS style corresponding to the step's assignment value.
     */
    public static String getStepAssignIconStyle(Set<UserUUID> anAssignment) {
        if (anAssignment.size() == 1) {
            return "solo_icon";
        } else if (anAssignment.size() > 1) {
            return "group_icon";
        } else {
            return "";
        }
    }

    /**
     * Get the CSS style to associate to the star icon.
     * 
     * @param starred
     * @return the CSS style corresponding to the starred state.
     */
    public static String getStarIconStyle(boolean starred) {
        if (starred) {
            return "yellow_star";
        } else {
            return "grey_star";
        }
    }

    /**
     * Get the CSS style to associate to the attachment's icon.
     * 
     * @param hasAttachment
     * @return the CSS style corresponding to the attachment property.
     */
    public static String getAttachmentIconStyle(boolean hasAttachment) {
        if (hasAttachment) {
            return "attachment_icon";
        } else {
            return "";
        }
    }

    /**
     * Get the CSS style for the application icon.
     * 
     * @return the CSS style.
     */
    public static String getOpenInApplicationIconStyle() {

        return "open_application_icon";
    }

    public static String getPriorityIconStyle(String aPriority) {
        return aPriority.toLowerCase() + "_priority";
    }

    public static String getProcessIconStyle(BonitaProcessState aProcessState) {
        return aProcessState.name().toLowerCase() + "_processState";
    }

    public static String getCaseStateIconStyle(CaseItemState aState) {
        return aState.name().toLowerCase() + "_caseState";
    }

}
