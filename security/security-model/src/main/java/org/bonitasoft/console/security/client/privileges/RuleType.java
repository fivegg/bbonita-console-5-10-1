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
package org.bonitasoft.console.security.client.privileges;

/**
 * @author Nicolas Chabanoles, Rodrigue Le Gall
 *
 */
public enum RuleType {
	PROCESS_START, 
	PROCESS_READ, 
	ACTIVITY_READ, 
	ACTIVITY_DETAILS_READ, 
	CATEGORY_READ, 
	DELEGEE_UPDATE,
	PASSWORD_UPDATE,
	LOGOUT,
	ASSIGN_TO_ME_STEP, 
	ASSIGN_TO_STEP, 
	UNASSIGN_STEP, 
    CHANGE_PRIORITY_STEP,
    SUSPEND_STEP,
    RESUME_STEP,
    PROCESS_INSTANTIATION_DETAILS_VIEW,
    PROCESS_ADD_COMMENT,
    PROCESS_MANAGE,
    PROCESS_PDF_EXPORT, 
    REPORT_VIEW,
    REPORT_MANAGE,
    PROCESS_INSTALL,
    REPORT_INSTALL,
    SKIP_STEP,
    CUSTOM
}
