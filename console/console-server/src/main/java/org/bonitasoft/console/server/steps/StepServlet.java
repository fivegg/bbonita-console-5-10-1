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
package org.bonitasoft.console.server.steps;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.StepDefinition;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepService;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.steps.exceptions.StepNotFoundException;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.server.login.SessionManager;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepServlet extends RemoteServiceServlet implements StepService {

    /**
     * The ILLEGAL_TASK_STATE defines the error message in case of an invalid
     * state.
     */
    private static final String ILLEGAL_TASK_STATE = "Illegal Task state.";
    /**
     * The TASK_NOT_FOUND defines the error message in case of an invalid
     * taskUUID.
     */
    private static final String TASK_NOT_FOUND = "Task not found.";
    /**
     * The ID used for serialization.
     */
    private static final long serialVersionUID = 6037770598163181942L;

    private static final Logger LOGGER = Logger.getLogger(StepServlet.class.getName());

    /**
     * Default constructor.
     */
    public StepServlet() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.StepService#assignStep(java.lang.String,
     * java.lang.String)
     */
    public void assignStep(StepUUID aStepUUID, UserUUID anActorId) throws SessionTimeOutException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            StepDataStore.getInstance().assignStep(aStepUUID, anActorId);

        } catch (TaskNotFoundException e) {
            String theErrorMessage = TASK_NOT_FOUND;
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new RuntimeException(theErrorMessage, e);
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new RuntimeException(t);
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
     * org.bonitasoft.console.client.StepService#assignStep(java.lang.String,
     * java.util.Set)
     */
    public void assignStep(StepUUID aStepUUID, Set<UserUUID> aCandidateSet) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);

            StepDataStore.getInstance().assignStep(aStepUUID, aCandidateSet);

        } catch (TaskNotFoundException e) {
            String theErrorMessage = TASK_NOT_FOUND;
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e.getCause());
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new RuntimeException(t);
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
     * org.bonitasoft.console.client.StepService#resumeStep(java.lang.String,
     * boolean)
     */
    public void resumeStep(StepUUID aStepUUID) throws SessionTimeOutException, ConsoleException, StepNotFoundException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            StepDataStore.getInstance().resumeStep(aStepUUID);
        } catch (TaskNotFoundException e) {
            String theErrorMessage = TASK_NOT_FOUND;
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new StepNotFoundException(e.getActivityInstanceUUID().getValue());
        } catch (IllegalTaskStateException e) {
            String theErrorMessage = ILLEGAL_TASK_STATE;
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e.getCause());
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new RuntimeException(t);
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
     * org.bonitasoft.console.client.StepService#suspendStep(java.lang.String,
     * boolean)
     */
    public void suspendStep(StepUUID aStepUUID) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            StepDataStore.getInstance().suspendStep(aStepUUID);
        } catch (TaskNotFoundException e) {
            String theErrorMessage = TASK_NOT_FOUND;
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e.getCause());
        } catch (IllegalTaskStateException e) {
            String theErrorMessage = ILLEGAL_TASK_STATE;
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e.getCause());
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new RuntimeException(t);
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
     * org.bonitasoft.console.client.StepService#unassignStep(org.bonitasoft
     * .console .client.StepUUID)
     */
    public Set<UserUUID> unassignStep(StepUUID aStepUUID) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            return StepDataStore.getInstance().unassignStep(aStepUUID);
        } catch (TaskNotFoundException e) {
            String theErrorMessage = TASK_NOT_FOUND;
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e.getCause());
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new RuntimeException(t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }

    }

    public List<CommentItem> getStepCommentFeed(StepUUID aStepUUID) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            return StepDataStore.getInstance().getStepCommentFeed(aStepUUID);
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new RuntimeException(t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }

    }

    public List<CommentItem> addStepComment(final StepUUID aStepUUID, final String aComment) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return StepDataStore.getInstance().addStepComment(aStepUUID, aComment, theUserProfile);
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new RuntimeException(t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public String setStepPriority(StepUUID aStepUUID, int aPriority) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            return StepDataStore.getInstance().setStepPriority(aStepUUID, aPriority);
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new RuntimeException(t);
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
     * org.bonitasoft.console.client.steps.StepService#getAllSteps(org.bonitasoft
     * .console.client.StepFilter)
     */
    public ItemUpdates<StepItem> getAllSteps(StepFilter anItemFilter) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = SessionManager.login(theRequest);
        try {
            return StepDataStore.getInstance().getAllSteps(anItemFilter);
        } catch (ConsoleException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            throw e;
        } catch (Throwable e) {
            String errorMessage = "Unable to list steps!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new ConsoleException(errorMessage, e);
        } finally {
            SessionManager.logout(theLoginContext);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.steps.StepService#getStep(org.bonitasoft.
     * console.client.steps.StepUUID, org.bonitasoft.console.client.StepFilter)
     */
    public StepItem getStep(StepUUID anItemUUID, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = SessionManager.login(theRequest);
        try {
            return StepDataStore.getInstance().getStep(anItemUUID, aFilter);
        } catch (ConsoleException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            throw e;
        } catch (StepNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Step not found: " + e.getNameOrId());
            }
            throw e;
        } catch (Throwable e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            throw new ConsoleException(null, e);
        } finally {
            SessionManager.logout(theLoginContext);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.steps.StepService#getSteps(java.util.List,
     * org.bonitasoft.console.client.StepFilter)
     */
    public List<StepItem> getSteps(List<StepUUID> anItemSelection, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = SessionManager.login(theRequest);
        try {
            return StepDataStore.getInstance().getSteps(anItemSelection, aFilter);
        } catch (ConsoleException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            throw e;
        } catch (StepNotFoundException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Step not found: " + e.getNameOrId());
            }
            throw e;
        } catch (Throwable e) {
            String errorMessage = "Error while listing steps!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new ConsoleException(errorMessage, e);
        } finally {
            SessionManager.logout(theLoginContext);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.steps.StepService#skipStep(org.bonitasoft
     * .console.client.steps.StepUUID, org.bonitasoft.console.client.StepFilter)
     */
    public StepItem skipStep(StepUUID anItemUUID, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            return StepDataStore.getInstance().skipStep(anItemUUID, aFilter);
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleSecurityException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new ConsoleException("Unaxpected exception.", t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.console.client.steps.StepService#executeTimer(org.bonitasoft.console.client.steps.StepUUID)
     */
    public StepItem executeTimer(StepUUID anItemUUID, StepFilter aStepFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            return StepDataStore.getInstance().executeTimer(anItemUUID, aStepFilter);
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleSecurityException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new ConsoleException("Unaxpected exception.", t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.console.client.steps.StepService#updateTimer(org.bonitasoft.console.client.steps.StepUUID, java.util.Date, org.bonitasoft.console.client.StepFilter)
     */
    public StepItem updateTimer(StepUUID anItemUUID, Date aNewValue, StepFilter aStepFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            return StepDataStore.getInstance().updateTimer(anItemUUID, aNewValue, aStepFilter);
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleSecurityException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new ConsoleException("Unaxpected exception.", t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }
    
    public StepItem cancelTimer(StepUUID anItemUUID, StepFilter aStepFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = null;
        try {
            theLoginContext = SessionManager.login(theRequest);
            return StepDataStore.getInstance().cancelTimer(anItemUUID, aStepFilter);
        } catch (SessionTimeOutException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleSecurityException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (ConsoleException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new ConsoleException("Unaxpected exception.", t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }
    
    /*
     * Step definition
     */
    
    /*
     * (non-Javadoc)
     * 
     * @seeorg.bonitasoft.console.client.steps.StepService#getStepDefinition(org.
     * bonitasoft.console.client.steps.StepUUID,
     * org.bonitasoft.console.client.StepFilter)
     */
    public StepDefinition getStepDefinition(StepUUID anItemUUID, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException {
      HttpServletRequest theRequest = this.getThreadLocalRequest();
      LoginContext theLoginContext = SessionManager.login(theRequest);
      try {
        return StepDataStore.getInstance().getStepDefinition(anItemUUID, aFilter);
      } catch (ConsoleException e) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
        throw e;
      } catch (StepNotFoundException e) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
          LOGGER.log(Level.SEVERE, "Step not found: " + e.getNameOrId());
        }
        throw e;
      } catch (Throwable e) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
          LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        throw new ConsoleException(null, e);
      } finally {
        SessionManager.logout(theLoginContext);
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.steps.StepService#getStepsDefinition(org.
     * bonitasoft.console.client.StepFilter)
     */
    public ItemUpdates<StepDefinition> getStepsDefinition(StepFilter anItemFilter) throws SessionTimeOutException, ConsoleException {
      HttpServletRequest theRequest = this.getThreadLocalRequest();
      LoginContext theLoginContext = SessionManager.login(theRequest);
      try {
        return StepDataStore.getInstance().getStepsDefinition(anItemFilter);
      } catch (ConsoleException e) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
        throw e;
      } catch (Throwable e) {
        String errorMessage = "Unable to list steps!";
        if (LOGGER.isLoggable(Level.SEVERE)) {
          LOGGER.log(Level.SEVERE, errorMessage, e);
        }
        throw new ConsoleException(errorMessage, e);
      } finally {
        SessionManager.logout(theLoginContext);
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.steps.StepService#getStepsDefinition(java
     * .util.List, org.bonitasoft.console.client.StepFilter)
     */
    public List<StepDefinition> getStepsDefinition(List<StepUUID> anItemSelection, StepFilter aFilter) throws SessionTimeOutException, ConsoleException, StepNotFoundException, ConsoleSecurityException {
      HttpServletRequest theRequest = this.getThreadLocalRequest();
      LoginContext theLoginContext = SessionManager.login(theRequest);
      try {
        return StepDataStore.getInstance().getStepsDefinition(anItemSelection, aFilter);
      } catch (ConsoleException e) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
        throw e;
      } catch (StepNotFoundException e) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
          LOGGER.log(Level.SEVERE, "Step not found: " + e.getNameOrId());
        }
        throw e;
      } catch (Throwable e) {
        String errorMessage = "Error while listing steps!";
        if (LOGGER.isLoggable(Level.SEVERE)) {
          LOGGER.log(Level.SEVERE, errorMessage, e);
        }
        throw new ConsoleException(errorMessage, e);
      } finally {
        SessionManager.logout(theLoginContext);
      }
    }
}
