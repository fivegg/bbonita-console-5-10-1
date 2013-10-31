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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ProcessFilter;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.identity.MembershipItem;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.processes.exceptions.ProcessNotFoundException;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.common.application.ApplicationResourcesUtils;
import org.bonitasoft.console.common.application.ApplicationURLUtils;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.security.server.accessor.PropertiesFactory;
import org.bonitasoft.console.security.server.accessor.TenancyProperties;
import org.bonitasoft.console.server.persistence.PreferencesDataStore;
import org.ow2.bonita.facade.CommandAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.exception.DeploymentException;
import org.ow2.bonita.facade.runtime.command.WebDeleteAllProcessInstancesCommand;
import org.ow2.bonita.facade.runtime.command.WebDeleteDocumentsOfProcessesCommand;
import org.ow2.bonita.facade.runtime.command.WebDeleteProcessesCommand;
import org.ow2.bonita.facade.runtime.command.WebGetManageableProcessesCommand;
import org.ow2.bonita.facade.runtime.command.WebGetNumberOfManageableProcessesCommand;
import org.ow2.bonita.facade.runtime.command.WebGetNumberOfReadableProcessesCommand;
import org.ow2.bonita.facade.runtime.command.WebGetReadableProcessesCommand;
import org.ow2.bonita.facade.runtime.command.WebGetStartableProcessesCommand;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessDataStore {

    private static final String WEB_CUSTOM_DESCRIPTION = "WebCustomDescription";
    private static ProcessDataStore instance;

    private static final Logger LOGGER = Logger.getLogger(ProcessDataStore.class.getName());

    /**
     * Get the unique instance of UserDataStore.
     * 
     * @return
     */
    public static synchronized ProcessDataStore getInstance() {
        if (instance == null) {
            instance = new ProcessDataStore();
        }

        return instance;
    }

    /**
     * Default constructor.
     */
    protected ProcessDataStore() {
        super();
    }

    /**
     * Deploy a BAR.
     * 
     * @param aRequest
     * @param aFileName
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws DeploymentException
     * @throws ProcessNotFoundException
     * @throws ProcessNotFoundException
     * @throws ConsoleException
     */
    public ItemUpdates<BonitaProcess> deploy(final HttpServletRequest aRequest, UserProfile aUserProfile, String aFileName, ProcessFilter aFilter) throws IOException, ClassNotFoundException,
            DeploymentException, ProcessNotFoundException, ConsoleException {
        final File file = new File(aFileName);
        if (!file.exists()) {
            throw new RuntimeException("File " + aFileName + " does not exist.");
        }
        final BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(file);
        final LightProcessDefinition bonitaProcess = AccessorUtil.getManagementAPI().deploy(businessArchive);

        final ItemUpdates<BonitaProcess> theResult = getProcesses(aRequest, aUserProfile, aFilter);
        theResult.setNewlyCreatedItem(buildUIProcess(aRequest, aUserProfile, bonitaProcess));
        return theResult;
    }

    /**
     * 
     * @param aRequest
     * @param source
     * @param aUserProfile
     * @return
     * @throws ProcessNotFoundException
     * @throws ProcessNotFoundException
     */
    protected List<BonitaProcess> buildUIProcesses(final HttpServletRequest aRequest, UserProfile aUserProfile, final List<LightProcessDefinition> source) throws ProcessNotFoundException {
        List<BonitaProcess> uiProcesses = new ArrayList<BonitaProcess>();
        for (LightProcessDefinition theLightProcessDefinition : source) {
            uiProcesses.add(buildUIProcess(aRequest, aUserProfile, theLightProcessDefinition));
        }

        final int processesDisplayed = PreferencesDataStore.getInstance().getIntegerValue(TenancyProperties.PROCESSES_DISPLAYED, 5);
        
        if (uiProcesses != null && uiProcesses.size() > processesDisplayed) {
            Collections.sort(uiProcesses);
            int i = 0;
            for (BonitaProcess theBonitaProcess : uiProcesses) {
                if (i >= processesDisplayed) {
                    theBonitaProcess.setVisible(false);
                }
                i++;
            }
        }
        return uiProcesses;
    }

    /**
     * Build the representation of a process definition.</br> Returns null if
     * the process definition cannot be created due to privileges limitation.
     * 
     * @param aRequest
     * @param source
     * @return
     * @throws ProcessNotFoundException
     * @throws ProcessNotFoundException
     */
    protected BonitaProcess buildUIProcess(final HttpServletRequest aRequest, final UserProfile aUserProfile, final LightProcessDefinition source) throws ProcessNotFoundException {
        String theProcessDisplayName = null;
        if (source.getLabel() != null) {
            theProcessDisplayName = source.getLabel();
        } else {
            theProcessDisplayName = source.getName();
        }
        String theDescription = source.getDescription();
        if (theDescription == null) {
            // Ensure that the description is set.
            theDescription = "";
        }
        String theUrl = getOrSetURLMetaData(aRequest, source.getUUID());
        BonitaProcess theProcess = new BonitaProcess(source.getUUID().toString(), source.getName(), theProcessDisplayName, theDescription, source.getState().toString(), source.getVersion(), true);
        theProcess.setApplicationURL(theUrl);
        String theCustomDescription;
        try {
            theCustomDescription = AccessorUtil.getQueryDefinitionAPI().getProcessMetaData(source.getUUID(), WEB_CUSTOM_DESCRIPTION);
        } catch (org.ow2.bonita.facade.exception.ProcessNotFoundException e) {
            throw new ProcessNotFoundException(new BonitaProcessUUID(source.getUUID().getValue(), source.getLabel()));
        }
        if (theCustomDescription != null) {
            theProcess.setCustomDescriptionDefinition(theCustomDescription);
        }

        theProcess.setCategoriesName(source.getCategoryNames());
        theProcess.setDeployedBy(source.getDeployedBy());
        theProcess.setDeployedDate(source.getDeployedDate());
        return theProcess;
    }

    /**
     * 
     * @param aRequest
     * @param aProcessDefinitionUUID
     * @return
     * @throws ProcessNotFoundException
     */
    private String getOrSetURLMetaData(HttpServletRequest aRequest, ProcessDefinitionUUID aProcessDefinitionUUID) throws ProcessNotFoundException {

        try {
            return ApplicationURLUtils.getInstance().getOrSetURLMetaData(aRequest, aProcessDefinitionUUID);
        } catch (org.ow2.bonita.facade.exception.ProcessNotFoundException e) {
            throw new ProcessNotFoundException(new BonitaProcessUUID(aProcessDefinitionUUID.getValue(), aProcessDefinitionUUID.getProcessName()));
        }
    }

    /**
     * List all the processes.
     * 
     * @param aRequest
     * @return
     * @throws ProcessNotFoundException
     */
    public Set<BonitaProcess> getAllProcesses(final HttpServletRequest aRequest, UserProfile aUserProfile) throws ProcessNotFoundException {
        final Set<LightProcessDefinition> bonitaProcesses = AccessorUtil.getQueryDefinitionAPI().getLightProcesses();
        Set<BonitaProcess> theResult = new HashSet<BonitaProcess>(buildUIProcesses(aRequest, aUserProfile, new ArrayList<LightProcessDefinition>(bonitaProcesses)));
        return theResult;
    }

    /**
     * Get a process identified by its UUID.
     * 
     * @param aRequest
     * @param aProcessUUID
     * @return
     * @throws ProcessNotFoundException
     */
    public BonitaProcess getProcess(final HttpServletRequest aRequest, UserProfile aUserProfile, BonitaProcessUUID aProcessUUID) throws ProcessNotFoundException {

        try {
            return buildUIProcess(aRequest, aUserProfile, AccessorUtil.getQueryDefinitionAPI().getLightProcess(new ProcessDefinitionUUID(aProcessUUID.getValue())));
        } catch (org.ow2.bonita.facade.exception.ProcessNotFoundException e) {
            throw new ProcessNotFoundException(aProcessUUID);
        }
    }

    /**
     * Update the pattern that describes the cases for the given process.
     * 
     * @param aProcessUUID
     * @param aCustomDescriptionPattern
     * @throws ProcessNotFoundException
     */
    public void updateProcessCustomDescriptionPattern(BonitaProcessUUID aProcessUUID, String aCustomDescriptionPattern) throws ProcessNotFoundException {
        try {
            if (aCustomDescriptionPattern != null) {
                AccessorUtil.getRuntimeAPI().addProcessMetaData(new ProcessDefinitionUUID(aProcessUUID.getValue()), WEB_CUSTOM_DESCRIPTION, aCustomDescriptionPattern);
            } else {
                AccessorUtil.getRuntimeAPI().deleteProcessMetaData(new ProcessDefinitionUUID(aProcessUUID.getValue()), WEB_CUSTOM_DESCRIPTION);
            }
        } catch (org.ow2.bonita.facade.exception.ProcessNotFoundException e) {
            throw new ProcessNotFoundException(aProcessUUID);
        }

    }

    /**
     * Delete the given processes.
     * 
     * @param aProcessUUIDCollection
     * @param aFilter
     * @param aRequest
     * @throws Exception
     */
    public ItemUpdates<BonitaProcess> deleteProcesses(HttpServletRequest aRequest, UserProfile aUserProfile, Collection<BonitaProcessUUID> aProcessUUIDCollection, ProcessFilter aFilter, boolean deleteAttachments)
            throws Exception {
        Collection<ProcessDefinitionUUID> theProcessUUIDs = new ArrayList<ProcessDefinitionUUID>();
        for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDCollection) {
            ProcessDefinitionUUID theProcessDefinitionUUID = new ProcessDefinitionUUID(theBonitaProcessUUID.getValue());
            theProcessUUIDs.add(theProcessDefinitionUUID);
            ApplicationResourcesUtils.removeApplicationFiles(theProcessDefinitionUUID);
        }
        CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();
        theCommandAPI.execute(new WebDeleteDocumentsOfProcessesCommand(theProcessUUIDs,deleteAttachments));
        theCommandAPI.execute(new WebDeleteProcessesCommand(theProcessUUIDs));
        return getProcesses(aRequest, aUserProfile, aFilter);
    }
    
    /**
     * @param aRequest
     * @param aFilter
     * @return
     * @throws ProcessNotFoundException
     * @throws ConsoleException
     */
    public ItemUpdates<BonitaProcess> getProcesses(HttpServletRequest aRequest, UserProfile aUserProfile, ProcessFilter aFilter) throws ProcessNotFoundException, ConsoleException {
        final QueryDefinitionAPI theQueryDefinitionAPI;
        if (aFilter.searchInHistory()) {
            theQueryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_HISTORY_KEY);
        } else {
            theQueryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
        }
        final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();

        User theUser = aUserProfile.getUser();
        final List<LightProcessDefinition> theProcesses;
        final Integer theNumberOfVisibleProcesses;
        try {
            if (aFilter.isWithAdminRights() && aUserProfile.isAdmin()) {
                theProcesses = theQueryDefinitionAPI.getLightProcesses(aFilter.getStartingIndex(), aFilter.getMaxElementCount());
                theNumberOfVisibleProcesses = theQueryDefinitionAPI.getNumberOfProcesses();
            } else {
                if (theUser != null) {
                    final Set<String> theUserRoles = new HashSet<String>();
                    final Set<String> theUserGroups = new HashSet<String>();
                    final Set<String> theUserMemberships = new HashSet<String>();
                    if (theUser.getMembership() != null) {
                        for (final MembershipItem theMembership : theUser.getMembership()) {
                            theUserMemberships.add(theMembership.getUUID().getValue());
                            theUserGroups.add(theMembership.getGroup().getUUID().getValue());
                            theUserRoles.add(theMembership.getRole().getUUID().getValue());
                        }
                    }

                    if (aFilter.isWithAdminRights()) {
                        theProcesses = theCommandAPI.execute(new WebGetManageableProcessesCommand(aFilter.getStartingIndex(), aFilter.getMaxElementCount(), aFilter.searchInHistory(), theUser
                                .getUUID().getValue(), theUserRoles, theUserGroups, theUserMemberships, theUser.getUsername()));
                        theNumberOfVisibleProcesses = theCommandAPI.execute(new WebGetNumberOfManageableProcessesCommand(aFilter.searchInHistory(), theUser
                                .getUUID().getValue(), theUserRoles, theUserGroups, theUserMemberships, theUser.getUsername()));
                    } else {
                        theProcesses = theCommandAPI.execute(new WebGetReadableProcessesCommand(aFilter.getStartingIndex(), aFilter.getMaxElementCount(), aFilter.searchInHistory(), theUser.getUUID()
                                .getValue(), theUserRoles, theUserGroups, theUserMemberships, theUser.getUsername()));
                        theNumberOfVisibleProcesses = theCommandAPI.execute(new WebGetNumberOfReadableProcessesCommand(aFilter.searchInHistory(), theUser
                                .getUUID().getValue(), theUserRoles, theUserGroups, theUserMemberships, theUser.getUsername()));
                    }

                } else {
                    if (aFilter.isWithAdminRights()) {
                        theProcesses = theCommandAPI.execute(new WebGetManageableProcessesCommand(aFilter.getStartingIndex(), aFilter.getMaxElementCount(), aFilter.searchInHistory(), null, null,
                                null, null, aUserProfile.getUsername()));
                        theNumberOfVisibleProcesses = theCommandAPI.execute(new WebGetNumberOfManageableProcessesCommand(aFilter.searchInHistory(), aUserProfile.getUsername(), null, null, null, theUser.getUsername()));
                    } else {
                        theProcesses = theCommandAPI.execute(new WebGetReadableProcessesCommand(aFilter.getStartingIndex(), aFilter.getMaxElementCount(), aFilter.searchInHistory(), null, null, null,
                                null, aUserProfile.getUsername()));
                        theNumberOfVisibleProcesses = theCommandAPI.execute(new WebGetNumberOfReadableProcessesCommand(aFilter.searchInHistory(), aUserProfile.getUsername(), null, null, null, theUser.getUsername()));
                    }
                }
            }
        } catch (Exception e) {
            throw new ConsoleException("Unable to list processes.", e);
        }

        ItemUpdates<BonitaProcess> theResult;
        if (theProcesses == null || theProcesses.isEmpty()) {
            theResult = new ItemUpdates<BonitaProcess>(new ArrayList<BonitaProcess>(), 0);
        } else {
            List<BonitaProcess> theBonitaProcesses = buildUIProcesses(aRequest, aUserProfile, theProcesses);
            theResult = new ItemUpdates<BonitaProcess>(theBonitaProcesses, theNumberOfVisibleProcesses);
        }
        return theResult;
    }

    /**
     * Archive the given processes.
     * 
     * @pre the processes must be DISABLED first.
     * @param aProcessUUIDCollection
     * @throws ProcessNotFoundException
     * @throws ConsoleException
     * @throws Exception
     */
    public List<BonitaProcess> archive(HttpServletRequest aRequest, UserProfile aUserProfile, List<BonitaProcessUUID> aProcessUUIDCollection) throws ProcessNotFoundException, ConsoleException {
        Collection<ProcessDefinitionUUID> theProcessUUIDs = new ArrayList<ProcessDefinitionUUID>();
        for (Iterator<BonitaProcessUUID> theIterator = aProcessUUIDCollection.iterator(); theIterator.hasNext();) {
            BonitaProcessUUID theBonitaProcessUUID = (BonitaProcessUUID) theIterator.next();
            theProcessUUIDs.add(new ProcessDefinitionUUID(theBonitaProcessUUID.getValue()));
        }
        try {
            AccessorUtil.getManagementAPI().archive(theProcessUUIDs);
        } catch (DeploymentException e) {
            throw new ConsoleException("Unable to archive processes.", e);
        }
        return getProcesses(aRequest, aUserProfile, aProcessUUIDCollection);
    }

    /**
     * Disable the given processes.
     * 
     * @param aProcessUUIDCollection
     * @param aRequest
     * @throws Exception
     */
    public List<BonitaProcess> disable(HttpServletRequest aRequest, UserProfile aUserProfile, List<BonitaProcessUUID> aProcessUUIDCollection) throws Exception {
        Collection<ProcessDefinitionUUID> theProcessUUIDs = new ArrayList<ProcessDefinitionUUID>();
        for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDCollection) {
            theProcessUUIDs.add(new ProcessDefinitionUUID(theBonitaProcessUUID.getValue()));
        }
        AccessorUtil.getManagementAPI().disable(theProcessUUIDs);
        return getProcesses(aRequest, aUserProfile, aProcessUUIDCollection);
    }

    /**
     * Update the process application URL.
     * 
     * @param aProcessURLAssociation
     * @throws Exception
     */
    public void updateProcessApplicationURL(HashMap<BonitaProcessUUID, String> aProcessURLAssociation) throws Exception {
        String theURLToSet;
        for (Entry<BonitaProcessUUID, String> theProcessURLAssociation : aProcessURLAssociation.entrySet()) {
            theURLToSet = theProcessURLAssociation.getValue();
            ApplicationURLUtils.getInstance().setProcessApplicationURLMetadata(new ProcessDefinitionUUID(theProcessURLAssociation.getKey().getValue()), theURLToSet);
        }
    }

    /**
     * Enable the given processes.
     * 
     * @param aProcessUUIDCollection
     * @throws ProcessNotFoundException
     * @throws ConsoleException
     * @throws DeploymentException
     */
    public List<BonitaProcess> enable(HttpServletRequest aRequest, UserProfile aUserProfile, List<BonitaProcessUUID> aProcessUUIDCollection) throws ProcessNotFoundException, ConsoleException {
        Collection<ProcessDefinitionUUID> theProcessUUIDs = new ArrayList<ProcessDefinitionUUID>();

        // Build the collection of engines UUID.
        for (Iterator<BonitaProcessUUID> theIterator = aProcessUUIDCollection.iterator(); theIterator.hasNext();) {
            BonitaProcessUUID theBonitaProcessUUID = (BonitaProcessUUID) theIterator.next();
            theProcessUUIDs.add(new ProcessDefinitionUUID(theBonitaProcessUUID.getValue()));
        }
        // Apply changes in the engine.
        try {
            AccessorUtil.getManagementAPI().enable(theProcessUUIDs);
        } catch (DeploymentException e) {
            throw new ConsoleException("Unable to enable processes.", e);
        }
        return getProcesses(aRequest, aUserProfile, aProcessUUIDCollection);
    }

    /**
     * Delete all cases of given processes.
     * 
     * @param aProcessUUIDCollection
     * @throws Exception
     */
    public void deleteAllInstances(Collection<BonitaProcessUUID> aProcessUUIDCollection, boolean deleteAttachments) throws Exception {
        Set<ProcessDefinitionUUID> theProcesses = new HashSet<ProcessDefinitionUUID>();
        for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDCollection) {
            theProcesses.add(new ProcessDefinitionUUID(theBonitaProcessUUID.getValue()));
        }
        AccessorUtil.getCommandAPI().execute(new WebDeleteAllProcessInstancesCommand(theProcesses, deleteAttachments));
    }

    /**
     * Get the graphical representation of the process.
     * 
     * @param aProcessUUID
     * @return
     * @throws ProcessNotFoundException
     */
    public String getProcessPicture(BonitaProcessUUID aProcessUUID) throws Exception {
        if (aProcessUUID == null) {
            throw new IllegalArgumentException("The process UUID must be not null!");
        }
        
        File theTempFolder = PropertiesFactory.getTenancyProperties().getDomainXPTempFolder();
        File theFile = new File(theTempFolder, aProcessUUID.getValue() + ".png");
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        final ProcessDefinitionUUID processDefinitionUUID = new ProcessDefinitionUUID(aProcessUUID.getValue());
        final LightProcessDefinition process = queryDefinitionAPI.getLightProcess(processDefinitionUUID);
        if (!theFile.exists() || theFile.lastModified() < process.getDeployedDate().getTime()) {
            // get the process image from the BAR.
            byte[] theProcessImage = AccessorUtil.getQueryDefinitionAPI().getResource(new ProcessDefinitionUUID(aProcessUUID.getValue()), aProcessUUID.getValue() + ".png");

            if (theProcessImage == null) {
                return null;
            }
            // Write the image on the local disk for later use.
            FileOutputStream fos = new FileOutputStream(theFile);
            try {
                fos.write(theProcessImage);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.close();
            }
        }

        return aProcessUUID.getValue() + ".png";
    }

    /**
     * List the processes identified by their UUID.
     * 
     * @param aRequest
     * @param aSelection
     * @return
     * @throws ProcessNotFoundException
     */
    public List<BonitaProcess> getProcesses(HttpServletRequest aRequest, UserProfile aUserProfile, List<BonitaProcessUUID> aSelection) throws ProcessNotFoundException {
        try {
            final QueryDefinitionAPI theQueryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
            List<BonitaProcess> theResult = new ArrayList<BonitaProcess>();
            Set<ProcessDefinitionUUID> theProcessUUIDs = new HashSet<ProcessDefinitionUUID>();
            for (BonitaProcessUUID theBonitaProcessUUID : aSelection) {
                theProcessUUIDs.add(new ProcessDefinitionUUID(theBonitaProcessUUID.getValue()));
            }
            Set<LightProcessDefinition> theProcesses = theQueryDefinitionAPI.getLightProcesses(theProcessUUIDs);
            for (LightProcessDefinition theLightProcessDefinition : theProcesses) {
                theResult.add(buildUIProcess(aRequest, aUserProfile, theLightProcessDefinition));
            }

            return theResult;
        } catch (org.ow2.bonita.facade.exception.ProcessNotFoundException e) {
            throw new ProcessNotFoundException(new BonitaProcessUUID(e.getProcessUUID().getValue(), e.getProcessUUID().getProcessName()));
        }
    }

    /**
     * List the processes the user is allowed to start.
     * 
     * @param aRequest
     * @param aUserProfile
     * @return
     * @throws Exception
     */
    public List<BonitaProcess> getStartableProcesses(HttpServletRequest aRequest, UserProfile aUserProfile) throws Exception {
        final CommandAPI theCommandAPI = AccessorUtil.getCommandAPI();
        User theUser = aUserProfile.getUser();
        final Set<LightProcessDefinition> theProcesses;
        if (theUser != null) {
            Set<String> theUserRoles = new HashSet<String>();
            Set<String> theUserGroups = new HashSet<String>();
            Set<String> theUserMemberships = new HashSet<String>();
            if (theUser.getMembership() != null) {
                for (MembershipItem theMembership : theUser.getMembership()) {
                    theUserMemberships.add(theMembership.getUUID().getValue());
                    theUserGroups.add(theMembership.getGroup().getUUID().getValue());
                    theUserRoles.add(theMembership.getRole().getUUID().getValue());
                }
            }
            theProcesses = theCommandAPI.execute(new WebGetStartableProcessesCommand(theUser.getUUID().getValue(), theUserRoles, theUserGroups, theUserMemberships, theUser.getUsername()));
        } else {
            theProcesses = theCommandAPI.execute(new WebGetStartableProcessesCommand(null, null, null, null, aUserProfile.getUsername()));
        }
        final int processesDisplayed = PreferencesDataStore.getInstance().getIntegerValue(TenancyProperties.PROCESSES_DISPLAYED, 5);
        final List<BonitaProcess> theResult = new ArrayList<BonitaProcess>();
        BonitaProcess theProcess;
        int visibleProcesses = 0;
        for (LightProcessDefinition theLightProcessDefinition : theProcesses) {
            if (aUserProfile.isAllowed(RuleType.PROCESS_READ, theLightProcessDefinition.getUUID().getValue())) {
                theProcess = buildUIProcess(aRequest, aUserProfile, theLightProcessDefinition);
                if (theProcess != null) {
                	theProcess.setVisible(visibleProcesses<processesDisplayed);
                    theResult.add(theProcess);
                }
            } else {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "Skipping process as the user is not allowed to see it: " + aUserProfile.getUsername() + " / " + theLightProcessDefinition.getUUID().getValue());
                }
            }
            visibleProcesses++;
        }
        return theResult;
    }

    /**
     * @param aRequest
     * @param aUserProfile
     * @param aAnItemId
     * @param aAnItem
     * @return
     * @throws ProcessNotFoundException
     * @throws ConsoleException
     */
    public BonitaProcess updateProcess(HttpServletRequest aRequest, UserProfile aUserProfile, BonitaProcessUUID anItemId, BonitaProcess anItem) throws ProcessNotFoundException, ConsoleException {
        if (anItemId == null || anItem == null) {
            throw new ConsoleException("Illegal argument: null is not permitted.", null);
        }
        final Set<String> theNewCategories;
        if (anItem.getCategoriesName() != null) {
            theNewCategories = new HashSet<String>(anItem.getCategoriesName());
        } else {
            theNewCategories = null;
        }
        LightProcessDefinition theProcess;
        try {
            theProcess = AccessorUtil.getWebAPI().setProcessCategories(new ProcessDefinitionUUID(anItemId.getValue()), theNewCategories);
            return buildUIProcess(aRequest, aUserProfile, theProcess);
        } catch (org.ow2.bonita.facade.exception.ProcessNotFoundException e) {
            throw new ProcessNotFoundException(anItemId);
        }

    }
}
