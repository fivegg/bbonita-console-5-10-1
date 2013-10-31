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
package org.bonitasoft.console.client.common;

import org.bonitasoft.console.client.bam.ReportingService;
import org.bonitasoft.console.client.bam.ReportingServiceAsync;
import org.bonitasoft.console.client.cases.CaseService;
import org.bonitasoft.console.client.cases.CaseServiceAsync;
import org.bonitasoft.console.client.categories.CategoryService;
import org.bonitasoft.console.client.categories.CategoryServiceAsync;
import org.bonitasoft.console.client.identity.IdentityService;
import org.bonitasoft.console.client.identity.IdentityServiceAsync;
import org.bonitasoft.console.client.labels.LabelService;
import org.bonitasoft.console.client.labels.LabelServiceAsync;
import org.bonitasoft.console.client.login.ConsoleLoginService;
import org.bonitasoft.console.client.login.ConsoleLoginServiceAsync;
import org.bonitasoft.console.client.processes.ProcessService;
import org.bonitasoft.console.client.processes.ProcessServiceAsync;
import org.bonitasoft.console.client.steps.EventService;
import org.bonitasoft.console.client.steps.EventServiceAsync;
import org.bonitasoft.console.client.steps.StepService;
import org.bonitasoft.console.client.steps.StepServiceAsync;
import org.bonitasoft.console.client.themes.ThemesService;
import org.bonitasoft.console.client.themes.ThemesServiceAsync;
import org.bonitasoft.console.client.users.UserService;
import org.bonitasoft.console.client.users.UserServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public final class RpcConsoleServices {

    private static ProcessServiceAsync processService;
    private static ConsoleLoginServiceAsync loginService;
    private static LabelServiceAsync labelService;
    private static CaseServiceAsync caseService;
    private static StepServiceAsync stepService;
    private static ReportingServiceAsync reportingService;
    private static UserServiceAsync userService;
    private static IdentityServiceAsync identityService;
    private static CategoryServiceAsync categoryService;
    private static EventServiceAsync eventService;
    private static ThemesServiceAsync themesService;
    private RpcConsoleServices() {
        // Nothing to do here.
    }

    public static String getFileUploadURL() {
        return GWT.getModuleBaseURL() + "fileUpload";
    }

    public static ConsoleLoginServiceAsync getLoginService() {
        if (loginService == null) {
            loginService = (ConsoleLoginServiceAsync) GWT.create(ConsoleLoginService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) loginService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "login");
        }
        return loginService;
    }

    public static ProcessServiceAsync getProcessService() {
        if (processService == null) {
            processService = (ProcessServiceAsync) GWT.create(ProcessService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) processService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "process");
        }
        return processService;
    }

    public static LabelServiceAsync getLabelService() {
        if (labelService == null) {
            labelService = (LabelServiceAsync) GWT.create(LabelService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) labelService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "label");
        }
        return labelService;
    }

    public static CaseServiceAsync getCaseService() {
        if (caseService == null) {
            caseService = (CaseServiceAsync) GWT.create(CaseService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) caseService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "case");
        }
        return caseService;
    }

    public static StepServiceAsync getStepService() {
        if (stepService == null) {
            stepService = (StepServiceAsync) GWT.create(StepService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) stepService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "step");
        }
        return stepService;
    }

    public static ReportingServiceAsync getReportingService() {
        if (reportingService == null) {
            reportingService = (ReportingServiceAsync) GWT.create(ReportingService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) reportingService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "reporting");
        }
        return reportingService;
    }

    public static UserServiceAsync getUserService() {
        if (userService == null) {
            userService = (UserServiceAsync) GWT.create(UserService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) userService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "user");
        }
        return userService;
    }

    public static IdentityServiceAsync getIdentityService() {
        if (identityService == null) {
            identityService = (IdentityServiceAsync) GWT.create(IdentityService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) identityService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "identity");
        }
        return identityService;
    }

    public static CategoryServiceAsync getCategoryService() {
        if (categoryService == null) {
            categoryService = (CategoryServiceAsync) GWT.create(CategoryService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) categoryService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "category");
        }
        return categoryService;
    }

    public static EventServiceAsync getEventService() {
        if (eventService == null) {
            eventService = (EventServiceAsync) GWT.create(EventService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) eventService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "event");
        }
        return eventService;
    }

    public static ThemesServiceAsync getThemesService(){
        if(themesService == null){
            themesService = (ThemesServiceAsync)GWT.create(ThemesService.class);
            final ServiceDefTarget endpoint = (ServiceDefTarget) themesService;
            endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "themes");
        }
        return themesService;
        
    }
}
