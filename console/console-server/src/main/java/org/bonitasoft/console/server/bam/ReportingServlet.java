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
package org.bonitasoft.console.server.bam;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.bam.ReportingService;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.reporting.ReportingConfiguration;
import org.bonitasoft.console.client.reporting.exceptions.ReportNotFoundException;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.server.login.SessionManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class ReportingServlet extends RemoteServiceServlet implements ReportingService {

    private static final long serialVersionUID = 2780143215801052559L;
    protected static final Logger LOGGER = Logger.getLogger(ReportingServlet.class.getName());

    public ReportingConfiguration getReportingConfiguration() throws ConsoleException {

        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return ReportingDataStore.getInstance().getReportingConfiguration(new UserUUID(theUserProfile.getUsername()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConsoleException("Unable to get the reporting configuration.", e);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }

    }

    public void updateReportingConfiguration(ReportingConfiguration aConfiguration) throws ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (theUserProfile.isAdmin()) {
                ReportingDataStore.getInstance().updateReportingConfiguration(new UserUUID(theUserProfile.getUsername()), aConfiguration);
            } else {
                throw new IllegalArgumentException("Unauthorized user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConsoleException("Unable to update the reporting configuration.", e);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }

    }

    public ReportItem getReport(ReportUUID anItemUUID, ReportFilter aFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException, ReportNotFoundException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return ReportingDataStore.getInstance().getItem(theUserProfile, anItemUUID, aFilter, this.getServletContext());
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t.getMessage());
            throw new RuntimeException(theErrorMessage, t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public ItemUpdates<ReportItem> listReports(ReportFilter anItemFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return ReportingDataStore.getInstance().getItems(theUserProfile, anItemFilter, this.getServletContext());
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t.getMessage());
            throw new RuntimeException(theErrorMessage, t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public List<ReportItem> listReports(List<ReportUUID> anItemSelection, ReportFilter aFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return ReportingDataStore.getInstance().getItems(theUserProfile, anItemSelection, aFilter, this.getServletContext());
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t.getMessage());
            throw new RuntimeException(theErrorMessage, t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    // Settings
    public List<ReportItem> listDesignToDisplayInMonitoringView(ReportFilter aFilter) throws ConsoleException, ConsoleSecurityException, SessionTimeOutException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (theUserProfile.isAdmin() || theUserProfile.hasAccessToReporting()) {
                return ReportingDataStore.getInstance().listDesignToDisplayInMonitoringView(theUserProfile, aFilter, this.getServletContext());
            } else {
                throw new ConsoleSecurityException(theUserProfile.getUsername(), ReportingServlet.class.getName() + ".");
            }
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConsoleException("Unable to list the report designs.", e);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public void setDesignToDisplayInMonitoringView(List<ReportUUID> aNewList) throws ConsoleException, ConsoleSecurityException {
        LoginContext theLoginContext = null;
        try {
            final HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (theUserProfile.isAdmin() || theUserProfile.hasAccessToReporting()) {
                ReportingDataStore.getInstance().setDesignToDisplayInMonitoringView(aNewList);
            } else {
                throw new ConsoleSecurityException(theUserProfile.getUsername(), ReportingServlet.class.getName() + ".");
            }
        } catch (final ConsoleSecurityException e) {
            e.printStackTrace();
            throw e;
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ConsoleException("Unable set designs to display in MonitoringView.", e);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }
}
