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
package org.bonitasoft.console.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcess.BonitaProcessState;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.bonitasoft.console.server.cases.CaseDataStore;
import org.bonitasoft.console.server.labels.LabelDataStore;
import org.bonitasoft.console.server.processes.ProcessDataStore;
import org.bonitasoft.console.server.users.UserDataStore;
import org.junit.Test;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.ProcessBuilder;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ScenarioBasedTest extends BonitaTestCase {

	private LabelDataStore myLabelDataStore = LabelDataStore.getInstance();
	private ProcessDataStore myProcessDataStore = ProcessDataStore.getInstance();
	private CaseDataStore myCaseDataStore = CaseDataStore.getInstance();

	private LabelModel myUserInboxLabel = new LabelModel(LabelModel.INBOX_LABEL, new UserUUID(testUsername));
	private LabelModel myUserMyCaseLabel = new LabelModel(LabelModel.MY_CASES_LABEL, new UserUUID(testUsername));
	private LabelModel myUserStarLabel = new LabelModel(LabelModel.STAR_LABEL, new UserUUID(testUsername));

	private LabelModel myAdminInboxLabel = new LabelModel(LabelModel.INBOX_LABEL, new UserUUID(adminUsername));

	private CaseFilter myUserFilter;
	private CaseFilter myAdminFilter;

	// private ProcessDefinition myBonitaProcess;
	private ProcessInstanceUUID myProcessInstanceUUID;

	@Test
	public void scenarioSimpleUser() throws Exception {
		// Log on the engine.
		Utils.login(testUsername, "bpm");

		// Simulate the Console login which build the user profile and send it back to the client side.
		testUserProfile = UserDataStore.getUserProfile(testUsername, false, "en", SecurityAPIFactory.getPrivilegesAPI().getUserRights(testUsername), false);
		UserDataStore.loadUserPreferences(testUserProfile);
		// From Now on the user is logged in from the UE point of view.

		// Get the bar.
		URL testBarURL = getClass().getResource("/approvalWorkflow.bar");
		if (testBarURL == null) {
			throw new RuntimeException("File approvalWorkflow.bar was not found.");
		}

		// Deploy a process.
		AccessorUtil.getManagementAPI().deleteAllProcesses();
		BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(new File(testBarURL.toURI()));
		ProcessDefinition theProcessDefinition = AccessorUtil.getManagementAPI().deploy(businessArchive);

		// List the processes
		Set<BonitaProcess> theProcesses = myProcessDataStore.getAllProcesses(myRequest, adminUserProfile);
		assertEquals("Wrong number of processes.", 1, theProcesses.size());
		for (BonitaProcess theBonitaProcess : theProcesses) {
			assertEquals("Wrong UUID.", theBonitaProcess.getUUID().getValue(), theProcessDefinition.getUUID().getValue());
			assertEquals("Wrong State.", theBonitaProcess.getState().name(), theProcessDefinition.getState().name());
			assertEquals("Wrong State.", theBonitaProcess.getState().name(), BonitaProcessState.ENABLED.name());
		}

		// Instantiate a case (do not have particular action for this, use directly the API).
		myProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(theProcessDefinition.getUUID());

		// User is redirected to his Inbox.
		// List the cases that have the 'Inbox' label.
		Collection<LabelUUID> theLabels = new ArrayList<LabelUUID>();
		theLabels.add(myUserInboxLabel.getUUID());
		myUserFilter = new CaseFilter(theLabels, 0, 20);

		Thread.sleep(1);
		Collection<CaseItem> theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());
		CaseUUID theCaseUUID = null;
		for (CaseItem theCase : theCases) {
			assertTrue("'Inbox' label is missing.", theCase.getLabels().contains(myUserInboxLabel.getUUID()));
			assertTrue("'My Case' label is missing.", theCase.getLabels().contains(myUserMyCaseLabel.getUUID()));
			assertEquals("Wrong number of default labels.", 2, theCase.getLabels().size());
			theCaseUUID = theCase.getUUID();
		}

		assertEquals("Wrong caseUUID", myProcessInstanceUUID.getValue(), theCaseUUID.getValue());

		TreeSet<LabelUUID> theSetOfLabelsToAdd = new TreeSet<LabelUUID>();
		TreeSet<LabelUUID> theSetOfLabelsToRemove = new TreeSet<LabelUUID>();
		TreeSet<CaseUUID> theSetOfCasesToUpdate = new TreeSet<CaseUUID>();
		theSetOfCasesToUpdate.add(theCaseUUID);

		// -- Reload the Inbox.
		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());
		for (CaseItem theCase : theCases) {
			assertTrue("'Inbox' label is missing.", theCase.getLabels().contains(myUserInboxLabel.getUUID()));
			assertTrue("'My Case' label is missing.", theCase.getLabels().contains(myUserMyCaseLabel.getUUID()));
			assertEquals("Wrong number of default labels.", 2, theCase.getLabels().size());
			theCaseUUID = theCase.getUUID();
		}

		// Add the Star label
		theSetOfLabelsToAdd.clear();
		theSetOfLabelsToRemove.clear();
		theSetOfLabelsToAdd.add(myUserStarLabel.getUUID());
		myLabelDataStore.updateLabels(testUserProfile, theSetOfLabelsToAdd, theSetOfLabelsToRemove, theSetOfCasesToUpdate);
		// -- Reload the Inbox.
		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());
		for (CaseItem theCase : theCases) {
			assertTrue("'Inbox' label is missing.", theCase.getLabels().contains(myUserInboxLabel.getUUID()));
			assertTrue("'My Case' label is missing.", theCase.getLabels().contains(myUserMyCaseLabel.getUUID()));
			assertTrue("'Star' label is missing.", theCase.getLabels().contains(myUserStarLabel.getUUID()));
			assertEquals("Wrong number of default labels.", 3, theCase.getLabels().size());
			theCaseUUID = theCase.getUUID();
		}
		// Browse to 'Starred'.
		theLabels.clear();
		theLabels.add(myUserStarLabel.getUUID());
		myUserFilter.setLabels(theLabels);
		// -- Reload the 'Starred'.
		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());
		for (CaseItem theCase : theCases) {
			assertTrue("'Inbox' label is missing.", theCase.getLabels().contains(myUserInboxLabel.getUUID()));
			assertTrue("'My Case' label is missing.", theCase.getLabels().contains(myUserMyCaseLabel.getUUID()));
			assertTrue("'Star' label is missing.", theCase.getLabels().contains(myUserStarLabel.getUUID()));
			assertEquals("Wrong number of default labels.", 3, theCase.getLabels().size());
			theCaseUUID = theCase.getUUID();
		}

		// Create a new label.
		LabelModel theNewLabelModel = myLabelDataStore.createNewLabel(testUserProfile, "first custom label");
		assertEquals("Wrong label name.", "first custom label", theNewLabelModel.getUUID().getValue());
		assertEquals("Wrong label owner.", testUsername, theNewLabelModel.getUUID().getOwner().getValue());

		// -- Reload the 'Starred' (check that the new label has *not* been associated to the case).
		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());
		for (CaseItem theCase : theCases) {
			assertTrue("'Inbox' label is missing.", theCase.getLabels().contains(myUserInboxLabel.getUUID()));
			assertTrue("'My Case' label is missing.", theCase.getLabels().contains(myUserMyCaseLabel.getUUID()));
			assertTrue("'Star' label is missing.", theCase.getLabels().contains(myUserStarLabel.getUUID()));
			assertEquals("Wrong number of default labels.", 3, theCase.getLabels().size());
			theCaseUUID = theCase.getUUID();
		}

		// Associate the new label to the case.
		theSetOfLabelsToAdd.clear();
		theSetOfLabelsToRemove.clear();
		theSetOfLabelsToAdd.add(theNewLabelModel.getUUID());
		myLabelDataStore.updateLabels(testUserProfile, theSetOfLabelsToAdd, theSetOfLabelsToRemove, theSetOfCasesToUpdate);
		// -- Reload the 'Starred' (check that the new label has been associated to the case).
		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());
		for (CaseItem theCase : theCases) {
			assertTrue("'Inbox' label is missing.", theCase.getLabels().contains(myUserInboxLabel.getUUID()));
			assertTrue("'My Case' label is missing.", theCase.getLabels().contains(myUserMyCaseLabel.getUUID()));
			assertTrue("'Star' label is missing.", theCase.getLabels().contains(myUserStarLabel.getUUID()));
			assertTrue("'first custom label' label is missing.", theCase.getLabels().contains(theNewLabelModel.getUUID()));
			assertEquals("Wrong number of default labels.", 4, theCase.getLabels().size());
			theCaseUUID = theCase.getUUID();
		}

		// Browse to 'first custom label'. Check that the case is there.
		theLabels.clear();
		theLabels.add(theNewLabelModel.getUUID());
		myUserFilter.setLabels(theLabels);
		// -- Reload the 'first custom label'.
		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());
		for (CaseItem theCase : theCases) {
			assertTrue("'My Case' label is missing.", theCase.getLabels().contains(myUserMyCaseLabel.getUUID()));
			assertTrue("'Star' label is missing.", theCase.getLabels().contains(myUserStarLabel.getUUID()));
			assertTrue("'first custom label' label is missing.", theCase.getLabels().contains(theNewLabelModel.getUUID()));
			assertTrue("'Inbox' label is missing.", theCase.getLabels().contains(myUserInboxLabel.getUUID()));
			assertEquals("Wrong number of labels.", 4, theCase.getLabels().size());
			theCaseUUID = theCase.getUUID();
		}

		// -- Browse back to 'first custom label'.
		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());
		for (CaseItem theCase : theCases) {
			assertTrue("'My Case' label is missing.", theCase.getLabels().contains(myUserMyCaseLabel.getUUID()));
			assertTrue("'Star' label is missing.", theCase.getLabels().contains(myUserStarLabel.getUUID()));
			assertTrue("'Inbox' label is missing.", theCase.getLabels().contains(myUserInboxLabel.getUUID()));
			assertEquals("Wrong number of labels.", 4, theCase.getLabels().size());
			theCaseUUID = theCase.getUUID();
		}

		// Rename the 'first custom label'.
		LabelUUID theOldLabelUUID = new LabelUUID(theNewLabelModel.getUUID().getValue(), theNewLabelModel.getUUID().getOwner());
		myLabelDataStore.renameLabel(testUserProfile, theNewLabelModel.getUUID(), "new name");		
		// -- reflect the changes of model on client side.
		theNewLabelModel.setName("new name");
		// -- Reload cases based on the old name. Check that there is no cases.
		theLabels.clear();
		theLabels.add(theOldLabelUUID);
		myUserFilter.setLabels(theLabels);
		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 0, theCases.size());
		// --Reload cases based on the new name. Check that the case is there.
		theLabels.clear();
		theLabels.add(theNewLabelModel.getUUID());
		myUserFilter.setLabels(theLabels);
		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());
		for (CaseItem theCase : theCases) {
			assertTrue("'My Case' label is missing.", theCase.getLabels().contains(myUserMyCaseLabel.getUUID()));
			assertTrue("'Star' label is missing.", theCase.getLabels().contains(myUserStarLabel.getUUID()));
			assertTrue("'new name' label is missing.", theCase.getLabels().contains(theNewLabelModel.getUUID()));
			assertEquals("Wrong number of default labels.", 4, theCase.getLabels().size());
			theCaseUUID = theCase.getUUID();
		}


		Utils.logout();
	}

	@Test
	public void scenarioAdminUser() throws Exception {
		// Log on the engine.
		Utils.login(adminUsername, "bpm");

		// Simulate the Console login which build the user profile and send it back to the client side.
		adminUserProfile = UserDataStore.getUserProfile(adminUsername, true, "en", SecurityAPIFactory.getPrivilegesAPI().getUserRights(adminUsername), false);
		UserDataStore.loadUserPreferences(adminUserProfile);
		// From Now the admin user is logged in from the UE point of view.

		// Load the 'Inbox'
		// List the cases that have the 'Inbox' label.
		Collection<LabelUUID> theLabels = new ArrayList<LabelUUID>();
		theLabels.add(myUserInboxLabel.getUUID());
		myAdminFilter = new CaseFilter(theLabels, 0, 20);

		Thread.sleep(1);
		Collection<CaseItem> theCases = myCaseDataStore.getAllCases(myAdminFilter, adminUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 0, theCases.size());

		// List the processes.
		Set<BonitaProcess> theProcesses = myProcessDataStore.getAllProcesses(myRequest, adminUserProfile);
		assertEquals("Wrong number of processes.", 0, theProcesses.size());

		// Get the bar.
		URL testBarURL = getClass().getResource("/approvalWorkflow.bar");
		if (testBarURL == null) {
			throw new RuntimeException("File approvalWorkflow.bar was not found.");
		}

		// Deploy a process.
		AccessorUtil.getManagementAPI().deleteAllProcesses();
		BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(new File(testBarURL.toURI()));
		ProcessDefinition theProcessDefinition = AccessorUtil.getManagementAPI().deploy(businessArchive);

		// Check the deployment.
		theProcesses = myProcessDataStore.getAllProcesses(myRequest, adminUserProfile);
		assertEquals("Wrong number of processes.", 1, theProcesses.size());
		for (BonitaProcess theBonitaProcess : theProcesses) {
			assertEquals("Wrong UUID.", theBonitaProcess.getUUID().getValue(), theProcessDefinition.getUUID().getValue());
			assertEquals("Wrong State.", theBonitaProcess.getState().name(), theProcessDefinition.getState().name());
			assertEquals("Wrong State.", theBonitaProcess.getState().name(), BonitaProcessState.ENABLED.name());
		}

		// Browse to 'admin cases'. Check that the list is empty.
		theLabels.clear();
		theLabels.add(LabelModel.ADMIN_ALL_CASES.getUUID());
		myAdminFilter.setLabels(theLabels);
		myAdminFilter.setWithAdminRights(true);

		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myAdminFilter, adminUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 0, theCases.size());

		// Switch as another user.
		Utils.logout();
		Utils.login(testUsername, "bpm");

		// Instantiate a case.
		// Do not have particular action for this, use directly the API.
		AccessorUtil.getRuntimeAPI().instantiateProcess(theProcessDefinition.getUUID());

		// Switch back as admin.
		Utils.logout();
		Utils.login(adminUsername, "bpm");

		// Browse to 'admin cases'. Check that the case from the test user is there.
		theLabels.clear();
		theLabels.add(LabelModel.ADMIN_ALL_CASES.getUUID());
		myAdminFilter.setLabels(theLabels);
		myAdminFilter.setWithAdminRights(true);

		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myAdminFilter, adminUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theCases.size());

		// Browse back to 'Inbox'. Check that it is empty.
		theLabels.clear();
		theLabels.add(myAdminInboxLabel.getUUID());
		myAdminFilter.setLabels(theLabels);
		myAdminFilter.setWithAdminRights(false);

		Thread.sleep(1);
		theCases = myCaseDataStore.getAllCases(myAdminFilter, adminUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 0, theCases.size());

		Utils.logout();
	}

	@Test
	public void subCasesUpdateLabelsTest() throws Exception {

		Utils.login(testUsername, "bpm");

		// Simulate the Console login which build the user profile and send it back to the client side.
		testUserProfile = UserDataStore.getUserProfile(testUsername, false, "en", SecurityAPIFactory.getPrivilegesAPI().getUserRights(testUsername), false);
		UserDataStore.loadUserPreferences(testUserProfile);
		// From Now the user is logged in from the UE point of view.

		ProcessDefinition grandParentProcess = ProcessBuilder.createProcess("grandParentProcess", "1.0").addSubProcess("parentProcess", "parentProcess").done();

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("childProcess", "childProcess").done();

		ProcessDefinition childProcess = ProcessBuilder.createProcess("childProcess", "1.0").addHuman(testUsername).addSubProcess("grandChildProcess", "grandChildProcess").done();

		ProcessDefinition grandChildProcess = ProcessBuilder.createProcess("grandChildProcess", "1.0").addHuman(testUsername).addSubProcess("greatGrandChild", "greatGrandChild").done();

		ProcessDefinition greatGrandChild = ProcessBuilder.createProcess("greatGrandChild", "1.0").addHuman(testUsername).addHumanTask("t", testUsername).done();

		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(greatGrandChild));
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(grandChildProcess));
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(childProcess));
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(grandParentProcess));

		final ProcessDefinitionUUID processUUID = grandParentProcess.getUUID();

		// Start a case.
		ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		// As there is no gate at the end of the processes some may terminate before expected.
		assertEquals(5, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

		// Translate the processInstanceUUID into CaseUUID.
		CaseUUID theCaseUUID = new CaseUUID(theProcessInstanceUUID.getValue());

		// User is redirected to his Inbox.
		// List the cases that have the 'Inbox' label.
		Collection<LabelUUID> theLabels = new ArrayList<LabelUUID>();
		theLabels.add(myUserInboxLabel.getUUID());
		myUserFilter = new CaseFilter(theLabels, 0, 20);

		Thread.sleep(1);
		Collection<CaseItem> theUserCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Invlaid number of cases in Inbox!", 1, theUserCases.size());
		CaseItem theCaseItem = theUserCases.iterator().next();
		assertEquals("Invalid case UUID!", theCaseUUID.getValue(), theUserCases.iterator().next().getUUID().getValue());

		// Check default labels of case.
		assertTrue("'Inbox' label is missing.", theCaseItem.getLabels().contains(myUserInboxLabel.getUUID()));
		assertTrue(myUserMyCaseLabel.getUUID().getValue() + " label is missing.", theCaseItem.getLabels().contains(myUserMyCaseLabel.getUUID()));
		assertEquals("Wrong number of default labels.", 2, theCaseItem.getLabels().size());

		// Reload cases
		Thread.sleep(1);
		theUserCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theUserCases.size());
		theCaseItem = theUserCases.iterator().next();
		assertEquals("Invalid case UUID!", theCaseUUID.getValue(), theUserCases.iterator().next().getUUID().getValue());
		// Check labels of case.
		assertEquals("Wrong number of labels.", 2, theCaseItem.getLabels().size());
		assertTrue("'Inbox' label is missing.", theCaseItem.getLabels().contains(myUserInboxLabel.getUUID()));
		assertTrue(myUserMyCaseLabel.getUUID().getValue() + " label is missing.", theCaseItem.getLabels().contains(myUserMyCaseLabel.getUUID()));

		// Reload cases of Inbox
		theLabels = new ArrayList<LabelUUID>();
		theLabels.add(myUserInboxLabel.getUUID());
		myUserFilter = new CaseFilter(theLabels, 0, 20);
		Thread.sleep(1);
		theUserCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases in the Inbox.", 1, theUserCases.size());

		TreeSet<LabelUUID> theSetOfLabelsToAdd = new TreeSet<LabelUUID>();
		TreeSet<LabelUUID> theSetOfLabelsToRemove = new TreeSet<LabelUUID>();
		TreeSet<CaseUUID> theSetOfCasesToUpdate = new TreeSet<CaseUUID>();
		theSetOfCasesToUpdate.add(theCaseUUID);

		// Star the case.
		theSetOfLabelsToAdd.add(myUserStarLabel.getUUID());
		myLabelDataStore.updateLabels(testUserProfile, theSetOfLabelsToAdd, theSetOfLabelsToRemove, theSetOfCasesToUpdate);

		// Reload cases
		Thread.sleep(1);
		theUserCases = myCaseDataStore.getAllCases(myUserFilter, testUserProfile, myRequest).getCases();
		assertEquals("Wrong number of cases.", 1, theUserCases.size());
		theCaseItem = theUserCases.iterator().next();
		assertEquals("Invalid case UUID!", theCaseUUID.getValue(), theUserCases.iterator().next().getUUID().getValue());
		// Check labels of case.
		assertTrue(myUserStarLabel.getUUID().getValue() + " label is missing.", theCaseItem.getLabels().contains(myUserStarLabel.getUUID()));
		assertTrue(myUserMyCaseLabel.getUUID().getValue() + " label is missing.", theCaseItem.getLabels().contains(myUserMyCaseLabel.getUUID()));
		assertTrue(myUserInboxLabel.getUUID().getValue() + " label is missing.", theCaseItem.getLabels().contains(myUserInboxLabel.getUUID()));
		assertEquals("Wrong number of default labels.", 3, theCaseItem.getLabels().size());
		// -- Clean state
		theSetOfLabelsToAdd.clear();
		theSetOfLabelsToRemove.clear();

		Utils.logout();
	}
}
