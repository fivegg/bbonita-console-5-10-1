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
package org.bonitasoft.console.server.processes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ProcessFilter;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.processes.ProcessService;
import org.bonitasoft.console.client.processes.exceptions.ProcessNotFoundException;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.server.login.SessionManager;
import org.ow2.bonita.facade.exception.DeploymentException;
import org.ow2.bonita.util.BonitaException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessServlet extends RemoteServiceServlet implements ProcessService {

    private static final long serialVersionUID = 8957066904730153052L;

    protected static final Logger LOGGER = Logger.getLogger(ProcessServlet.class.getName());

    private final ProcessDataStore processDataStore = ProcessDataStore.getInstance();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.ProcessService#deploy(java.lang.String)
     */
    public ItemUpdates<BonitaProcess> deploy(final String fileName, final ProcessFilter aFilter) throws SessionTimeOutException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (theUserProfile.isAdmin() || theUserProfile.isAllowed(RuleType.PROCESS_INSTALL)) {
                return processDataStore.deploy(theRequest, theUserProfile, fileName, aFilter);
            } else {
                throw new ConsoleSecurityException(theUserProfile.getUsername(), this.getClass().getEnclosingMethod().getName());
            }

        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to install process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            String theErrorMessage = "Error while deploying bar.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e);
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
     * @see org.bonitasoft.console.client.ProcessService#getAllProcesses()
     */
    public Set<BonitaProcess> getAllProcesses() throws SessionTimeOutException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return processDataStore.getAllProcesses(theRequest, theUserProfile);
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to list processes: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
        } catch (Exception e) {
            String theErrorMessage = "Unable to list processes.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e);
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
     * org.bonitasoft.console.client.ProcessService#deleteAllInstances(java.
     * util.Collection)
     */
    public void deleteAllInstances(Collection<BonitaProcessUUID> aProcessUUIDCollection, boolean deleteAttachments) throws SessionTimeOutException, ConsoleException, ConsoleSecurityException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (!theUserProfile.isAdmin()) {
                for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDCollection) {
                    if (!theUserProfile.isAllowed(RuleType.PROCESS_MANAGE, theBonitaProcessUUID.getValue())) {
                        throw new ConsoleSecurityException(theUserProfile.getUsername(), "You are not authorized to manage the process: " + theBonitaProcessUUID.getValue());
                    }
                }
            }
            ProcessDataStore.getInstance().deleteAllInstances(aProcessUUIDCollection, deleteAttachments);
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Un-authorized access", e);
            throw e;
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to delete all instances of process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.ProcessService#enable(java.util.Collection)
     */
    public List<BonitaProcess> enable(Collection<BonitaProcessUUID> aProcessUUIDCollection) throws SessionTimeOutException, ConsoleException, ConsoleSecurityException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (!theUserProfile.isAdmin()) {
                for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDCollection) {
                    if (!theUserProfile.isAllowed(RuleType.PROCESS_MANAGE, theBonitaProcessUUID.getValue())) {
                        throw new ConsoleSecurityException(theUserProfile.getUsername(), "You are not authorized to enable the process: " + theBonitaProcessUUID.getValue());
                    }
                }
            }
            return ProcessDataStore.getInstance().enable(theRequest, theUserProfile, new ArrayList<BonitaProcessUUID>(aProcessUUIDCollection));
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Un-authorized access", e);
            throw e;
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to enable process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
        } catch (ConsoleException e) {
            LOGGER.log(Level.SEVERE, "Unable to enable processes", e);
            throw e;
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage, t);
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
     * org.bonitasoft.console.client.ProcessService#undeploy(java.util.Collection
     * )
     */
    public List<BonitaProcess> undeploy(Collection<BonitaProcessUUID> aProcessUUIDCollection) throws SessionTimeOutException, ConsoleException, ConsoleSecurityException {

        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (!theUserProfile.isAdmin()) {
                for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDCollection) {
                    if (!theUserProfile.isAllowed(RuleType.PROCESS_MANAGE, theBonitaProcessUUID.getValue())) {
                        throw new ConsoleSecurityException(theUserProfile.getUsername(), "You are not authorized to disable the process: " + theBonitaProcessUUID.getValue());
                    }
                }
            }
            return ProcessDataStore.getInstance().disable(theRequest, theUserProfile, new ArrayList<BonitaProcessUUID>(aProcessUUIDCollection));
        } catch (DeploymentException e) {
            String theErrorMessage = "Unable to disable process: " + e.getProcessDefinitionUUID() + ". Are there cases still running?";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e);
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to deactivate process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Un-authorized access", e);
            throw e;
        } catch (Throwable t) {
            String theErrorMessage = "Unable to disable process.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage, t);
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
     * org.bonitasoft.console.client.ProcessService#deleteProcess(java.util.
     * Collection)
     */
    public ItemUpdates<BonitaProcess> deleteProcesses(final Collection<BonitaProcessUUID> aProcessUUIDCollection, final ProcessFilter aFilter) throws SessionTimeOutException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (!theUserProfile.isAdmin()) {
                for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDCollection) {
                    if (!theUserProfile.isAllowed(RuleType.PROCESS_MANAGE, theBonitaProcessUUID.getValue())) {
                        throw new ConsoleSecurityException(theUserProfile.getUsername(), "You are not authorized to delete the process: " + theBonitaProcessUUID.getValue());
                    }
                }
            }
            return ProcessDataStore.getInstance().deleteProcesses(theRequest, theUserProfile, aProcessUUIDCollection, aFilter, true);

        } catch (DeploymentException e) {
            String theErrorMessage = "Unable to delete process: " + e.getProcessDefinitionUUID() + ".";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e);
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to delete process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Un-authorized access", e);
            throw e;
        } catch (ConsoleException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Unable to delete processes", e);
            throw e;
        } catch (Throwable t) {
            String theErrorMessage = "Unable to delete process.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage, t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }
  
    /* (non-Javadoc)
     * @see org.bonitasoft.console.client.processes.ProcessService#deleteProcesses(java.util.Collection, ProcessFilter, ProcessFilterboolean)
     */
    @Override
    public ItemUpdates<BonitaProcess> deleteProcesses(Collection<BonitaProcessUUID> aProcessUUIDCollection,  final ProcessFilter aFilter, final boolean deleteAttachments) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (!theUserProfile.isAdmin()) {
                for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDCollection) {
                    if (!theUserProfile.isAllowed(RuleType.PROCESS_MANAGE, theBonitaProcessUUID.getValue())) {
                        throw new ConsoleSecurityException(theUserProfile.getUsername(), "You are not authorized to delete the process: " + theBonitaProcessUUID.getValue());
                    }
                }
            }
            return ProcessDataStore.getInstance().deleteProcesses(theRequest, theUserProfile, aProcessUUIDCollection, aFilter, deleteAttachments);

        } catch (DeploymentException e) {
            String theErrorMessage = "Unable to delete process: " + e.getProcessDefinitionUUID() + ".";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e);
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to delete process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Un-authorized access", e);
            throw e;
        } catch (ConsoleException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Unable to delete processes", e);
            throw e;
        } catch (Throwable t) {
            String theErrorMessage = "Unable to delete process.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage, t);
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
     * org.bonitasoft.console.client.ProcessService#archive(java.util.Collection
     * )
     */
    public List<BonitaProcess> archive(Collection<BonitaProcessUUID> aProcessUUIDCollection) throws SessionTimeOutException, ConsoleException, ConsoleSecurityException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (!theUserProfile.isAdmin()) {
                for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDCollection) {
                    if (!theUserProfile.isAllowed(RuleType.PROCESS_MANAGE, theBonitaProcessUUID.getValue())) {
                        throw new ConsoleSecurityException(theUserProfile.getUsername(), "You are not authorized to archive the process: " + theBonitaProcessUUID.getValue());
                    }
                }
            }
            return ProcessDataStore.getInstance().archive(theRequest, theUserProfile, new ArrayList<BonitaProcessUUID>(aProcessUUIDCollection));
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to archive process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Un-authorized access", e);
            throw e;
        } catch (ConsoleException e) {
            LOGGER.log(Level.SEVERE, "Unable to archive processes", e);
            throw e;
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage, t);
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
     * org.bonitasoft.console.client.ProcessService#updateProcessApplicationURL
     * (java.util.HashMap)
     */
    public void updateProcessApplicationURL(final HashMap<BonitaProcessUUID, String> aProcessURLAssociation) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (!theUserProfile.isAdmin()) {
                for (BonitaProcessUUID theBonitaProcessUUID : aProcessURLAssociation.keySet()) {
                    if (!theUserProfile.isAllowed(RuleType.PROCESS_MANAGE, theBonitaProcessUUID.getValue())) {
                        throw new ConsoleSecurityException(theUserProfile.getUsername(), "You are not authorized to update the process: " + theBonitaProcessUUID.getValue());
                    }
                }
            }
            ProcessDataStore.getInstance().updateProcessApplicationURL(aProcessURLAssociation);
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to update process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Un-authorized access", e);
            throw e;
        } catch (Throwable t) {
            String theErrorMessage = "Unexpected error.";
            LOGGER.log(Level.SEVERE, theErrorMessage, t);
            throw new RuntimeException(theErrorMessage, t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public BonitaProcess getProcess(BonitaProcessUUID aProcessUUID) throws ConsoleException, ProcessNotFoundException, SessionTimeOutException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return ProcessDataStore.getInstance().getProcess(theRequest, theUserProfile, aProcessUUID);
        } catch (ProcessNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Process not found!", e);
            throw e;
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to get process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
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

    public void updateProcessCustomDescriptionPattern(final HashMap<BonitaProcessUUID, String> aProcessCustomDescriptionPatternAssociation) throws SessionTimeOutException, ConsoleException,
            ProcessNotFoundException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (!theUserProfile.isAdmin()) {
                for (BonitaProcessUUID theBonitaProcessUUID : aProcessCustomDescriptionPatternAssociation.keySet()) {
                    if (!theUserProfile.isAllowed(RuleType.PROCESS_MANAGE, theBonitaProcessUUID.getValue())) {
                        throw new ConsoleSecurityException(theUserProfile.getUsername(), "You are not authorized to update the process: " + theBonitaProcessUUID.getValue());
                    }
                }
            }
            String thePattern;
            for (Entry<BonitaProcessUUID, String> theProcessCustomDescriptionPatternAssociation : aProcessCustomDescriptionPatternAssociation.entrySet()) {
                thePattern = theProcessCustomDescriptionPatternAssociation.getValue();
                ProcessDataStore.getInstance().updateProcessCustomDescriptionPattern(theProcessCustomDescriptionPatternAssociation.getKey(), thePattern);
            }
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Un-authorized access", e);
            throw e;
        } catch (SessionTimeOutException e) {
            String theErrorMessage = "Unable to update process: session timed out.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw e;
        } catch (ProcessNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Process not found!", e);
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

    public String getProcessPicture(BonitaProcessUUID aProcessUUID) throws SessionTimeOutException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            return ProcessDataStore.getInstance().getProcessPicture(aProcessUUID);
        } catch (SessionTimeOutException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } catch (ConsoleException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } catch (BonitaException e) {
            String theErrorMessage = "Error while updating process.";
            LOGGER.log(Level.SEVERE, theErrorMessage, e);
            throw new ConsoleException(theErrorMessage, e);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage());
            throw new RuntimeException(t);
        } finally {
            if (theLoginContext != null) {
                SessionManager.logout(theLoginContext);
            }
        }
    }

    public List<BonitaProcess> getProcesses(List<BonitaProcessUUID> aSelection, ProcessFilter aFilter) throws SessionTimeOutException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return ProcessDataStore.getInstance().getProcesses(theRequest, theUserProfile, aSelection);
        } catch (SessionTimeOutException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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

    public ItemUpdates<BonitaProcess> getProcesses(ProcessFilter aFilter) throws SessionTimeOutException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return ProcessDataStore.getInstance().getProcesses(theRequest, theUserProfile, aFilter);
        } catch (SessionTimeOutException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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

    public List<BonitaProcess> getStartableProcesses() throws SessionTimeOutException, ConsoleException {
        LoginContext theLoginContext = null;
        try {
            HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            return ProcessDataStore.getInstance().getStartableProcesses(theRequest, theUserProfile);
        } catch (SessionTimeOutException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
     * org.bonitasoft.console.client.processes.ProcessService#updateProcess(
     * org.bonitasoft.console.client.processes.BonitaProcessUUID,
     * org.bonitasoft.console.client.processes.BonitaProcess)
     */
    public BonitaProcess updateProcess(BonitaProcessUUID anItemId, BonitaProcess anItem) throws SessionTimeOutException, ConsoleException, ConsoleSecurityException {
        LoginContext theLoginContext = null;
        try {
            final HttpServletRequest theRequest = this.getThreadLocalRequest();
            theLoginContext = SessionManager.login(theRequest);
            final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
            if (!theUserProfile.isAdmin()) {
                if (!theUserProfile.isAllowed(RuleType.PROCESS_MANAGE, anItemId.getValue())) {
                    throw new ConsoleSecurityException(theUserProfile.getUsername(), "You are not authorized to update the process: " + anItemId.getValue());
                }
            }
            return ProcessDataStore.getInstance().updateProcess(theRequest, theUserProfile, anItemId, anItem);
        } catch (ConsoleSecurityException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Un-authorized access", e);
            throw e;
        } catch (SessionTimeOutException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
}
