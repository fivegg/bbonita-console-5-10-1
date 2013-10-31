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
package org.bonitasoft.console.server.cases;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseService;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.cases.CaseUpdates;
import org.bonitasoft.console.client.cases.CasesConfiguration;
import org.bonitasoft.console.client.cases.exceptions.CaseNotFoundException;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.processes.exceptions.ProcessNotFoundException;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.server.login.SessionManager;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.UncancellableInstanceException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseServlet extends RemoteServiceServlet implements CaseService {

    /**
     * The UNDELETABLE_INSTANCE defines
     */
    private static final String UNCANCELLABLE_INSTANCE = " : Uncancellable Instance!";

    /**
     * The INSTANCE_NOT_FOUND defines
     */
    private static final String INSTANCE_NOT_FOUND = " : Instance not found!";

    /**
     * The ERROR_OCCURED_WHILE_CANCELING_INSTANCE defines
     */
    private static final String ERROR_OCCURED_WHILE_CANCELING_INSTANCE = "Error occured while canceling instance ";

    /**
     * The serialVersionUID defines
     */
    private static final long serialVersionUID = 7535567219619845026L;

    private static final Logger LOGGER = Logger.getLogger(CaseServlet.class.getName());

    /**
     * Default constructor.
     */
    public CaseServlet() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bonitasoft.console.client.CaseService#getAllCases()
     */
    public CaseUpdates getAllCases(final CaseFilter aCaseFilter) throws SessionTimeOutException, ConsoleException, CaseNotFoundException, ConsoleSecurityException {

        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        CaseUpdates theResult;
        try {
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Listing cases of user '" + theUserProfile.getUsername() + "' , admin ? " + theUserProfile.isAdmin() + ".");
            }
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.log(Level.FINER, "###Filter content: \n"
                        + "search in history " + aCaseFilter.searchInHistory() + "\n"
                        + "is active " + aCaseFilter.isActive() + "\n"
                        + "is with admin rights " + aCaseFilter.isWithAdminRights() + "\n"
                        + "active users " + aCaseFilter.getActiveUsers() + "\n"
                        + "case instance number " + aCaseFilter.getCaseInstanceNumber() + "\n"
                        + "max element count " + aCaseFilter.getMaxElementCount() + "\n"
                        + "starting index " + aCaseFilter.getStartingIndex() + "\n"
                        + "search pattern " + aCaseFilter.getSearchPattern() + "\n"
                        + (aCaseFilter.getCategory() == null ? "" : aCaseFilter.getCategory().getName() + "\n")
                        + "completed date from " + aCaseFilter.getCompletedDateFrom() + "\n"
                        + "Completed date to " + aCaseFilter.getCompletedDateTo() + "\n"
                        + "initiators " + aCaseFilter.getInitiators() + "\n"
                        + "involved users " + aCaseFilter.getInvolvedUsers() + "\n"
                        + "label " + aCaseFilter.getLabel() + "\n"
                        + "labels " + aCaseFilter.getLabels() + "\n"
                        + "processes " + aCaseFilter.getProcesses() + "\n"
                        + "processes to ignore " + aCaseFilter.getProcessesToIgnore() + "\n"
                        + "started date from " + aCaseFilter.getStartedDateFrom() + "\n"
                        + "started date to " + aCaseFilter.getStartedDateTo() + "\n"
                        + "state " + aCaseFilter.getState() + "\n"
                        + "Updated date from " + aCaseFilter.getUpdatedDateFrom() + "\n"
                        + "Updated date to " + aCaseFilter.getUpdatedDateTo() + "\n"
                        + "###End filter content");
            }
            theResult = CaseDataStore.getInstance().getAllCases(aCaseFilter, theUserProfile, theRequest);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, (theResult == null ? 0 : theResult.getNbOfCases()) + " case(s) found for user '" + theUserProfile.getUsername() + "'.");
            }
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (CaseNotFoundException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Throwable e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
        return theResult;

    }

    /**
     * Cancel a case
     * 
     * @param aUUID
     * @throws ConsoleException
     */
    public CaseUpdates cancelCases(final CaseFilter aCaseFilter, final Collection<CaseUUID> aCaseSelection) throws SessionTimeOutException, ConsoleException {
        if (aCaseSelection != null && aCaseSelection.size() > 0) {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            LoginContext theLoginContext;
            try {
                theLoginContext = SessionManager.login(theRequest);
            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
                throw new SessionTimeOutException();
            }

            try {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Cancelling " + aCaseSelection.size() + "case(s).");
                }
                UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
                CaseUpdates theResult = CaseDataStore.getInstance().cancelCases(aCaseFilter, theUserProfile, aCaseSelection, theRequest);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Cases canceled.");
                }
                return theResult;
            } catch (InstanceNotFoundException e) {
                String theErrorMessage = ERROR_OCCURED_WHILE_CANCELING_INSTANCE + INSTANCE_NOT_FOUND;
                LOGGER.log(Level.SEVERE, theErrorMessage, e);
                throw new ConsoleException(theErrorMessage, e);
            } catch (UncancellableInstanceException e) {
                String theErrorMessage = ERROR_OCCURED_WHILE_CANCELING_INSTANCE + UNCANCELLABLE_INSTANCE;
                LOGGER.log(Level.SEVERE, theErrorMessage, e);
                throw new ConsoleException(ERROR_OCCURED_WHILE_CANCELING_INSTANCE + UNCANCELLABLE_INSTANCE, e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
                throw new ConsoleException(e.getMessage(), e);
            } finally {
                SessionManager.logout(theLoginContext);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Celete a case
     * 
     * @param aUUID
     * @throws ConsoleException
     */
    public void deleteCases(Collection<CaseUUID> aCaseSelection, final boolean deleteAttachments) throws SessionTimeOutException, ConsoleException {
        if (aCaseSelection != null && aCaseSelection.size() > 0) {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            LoginContext theLoginContext;
            try {
                theLoginContext = SessionManager.login(theRequest);
            } catch (Exception e) {
                throw new SessionTimeOutException();
            }

            try {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Deleting " + aCaseSelection.size() + "case(s)...");
                }
                CaseDataStore.getInstance().deleteCases(aCaseSelection,deleteAttachments);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, aCaseSelection.size() + "case(s) deleted.");
                }
            } catch (Exception e) {
                String theErrorMessage = ERROR_OCCURED_WHILE_CANCELING_INSTANCE;
                LOGGER.log(Level.SEVERE, theErrorMessage, e);
                throw new ConsoleException(theErrorMessage, e);
            } finally {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public CaseItem getCase(CaseUUID aCaseUUID, CaseFilter aFilter) throws ConsoleException, SessionTimeOutException, ProcessNotFoundException, CaseNotFoundException {
        if (null == aCaseUUID || null == aFilter) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Invalid argument (null value is not permitted). Will return null.");
            }
            return null;
        }
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        CaseItem theResult;
        try {
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Getting a case (" + aCaseUUID + ") of user '" + theUserProfile.getUsername() + "'.");
            }
            theResult = CaseDataStore.getInstance().getCase(aCaseUUID, theUserProfile, aFilter, theRequest);

        } catch (InstanceNotFoundException e) {
            LOGGER.severe(e.getMessage());
            throw new CaseNotFoundException(e.getInstanceUUID().getValue());
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }

        return theResult;
    }

    public void synchronizeDBs() throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;

        try {
            theLoginContext = SessionManager.login(theRequest);
            CaseDataStore.getInstance().synchronizeDBs();

        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public Collection<CaseItem> getCases(final Collection<CaseUUID> aCaseSelection, final CaseFilter aCaseFilter) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;

        try {
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return CaseDataStore.getInstance().getCases(theUserProfile, aCaseSelection, aCaseFilter, theRequest);

        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable e) {
            LOGGER.severe(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public List<CommentItem> getCaseCommentFeed(CaseUUID aCaseUUID, final CaseFilter aFilter) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;

        try {
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return CaseDataStore.getInstance().getCaseCommentFeed(aCaseUUID, theUserProfile, aFilter);

        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (InstanceNotFoundException e) {
            LOGGER.severe(e.getMessage());
            throw new ConsoleException(e.getMessage(), e);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.cases.CaseService#addCommentToCase(org.
     * bonitasoft .console.client.cases.CaseUUID, java.lang.String)
     */
    public List<CommentItem> addCommentToCase(CaseUUID aCaseUUID, String aComment, CaseFilter aCaseFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException,
            CaseNotFoundException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;

        try {
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            // if(theUserProfile.isAdmin() ||
            // theUserProfile.isAllowed(RuleType.PROCESS_ADD_COMMENT,
            // aProcessUUID.getValue())) {
            return CaseDataStore.getInstance().addCommentToCase(aCaseUUID, aComment, theUserProfile, aCaseFilter, theRequest);
            // } else {
            // throw new ConsoleSecurityException(theUserProfile.getUsername(),
            // "Add comment to case.");
            // }

        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (CaseNotFoundException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public CasesConfiguration getConfiguration() throws ConsoleException, SessionTimeOutException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            return CaseDataStore.getInstance().getConfiguration();
        } catch (SessionTimeOutException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, e.getMessage());
            }
            throw e;
        } catch (Exception e) {
            String theErrorMessage = "Unable to get the case configuration.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public CasesConfiguration updateConfiguration(final CasesConfiguration aConfiguration) throws ConsoleException, SessionTimeOutException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return CaseDataStore.getInstance().updateConfiguration(theUserProfile, aConfiguration);
        } catch (SessionTimeOutException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, e.getMessage());
            }
            throw e;
        } catch (Exception e) {
            String theErrorMessage = "Unable to update the case configuration.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }
}
