package org.bonitasoft.console.server.processes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.client.ProcessFilter;
import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelUpdates;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.server.BonitaTestCase;
import org.bonitasoft.console.server.Utils;
import org.bonitasoft.console.server.cases.CaseDataStore;
import org.bonitasoft.console.server.labels.LabelDataStore;
import org.junit.Test;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.command.WebDeleteAllProcessesCommand;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.ProcessBuilder;

public class ProcessDataStoreTest extends BonitaTestCase {

	@Test
	public void testGetInstance() {
		assertNotNull("The ProcessDataStore instance is null!", ProcessDataStore.getInstance());
	}

	@Test
	public void testGetAllProcessesHttpServletRequest() throws Exception {

		Utils.login(adminUsername, "bpm");

		Set<BonitaProcess> theProcesses = ProcessDataStore.getInstance().getAllProcesses(myRequest, adminUserProfile);
		assertNotNull(theProcesses);
		assertEquals(0, theProcesses.size());

		// Process definitions deployment.
		final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
		ProcessDefinition theProcess;
		int theNbOfProcesses = 17;
		for (int i = 0; i < theNbOfProcesses; i++) {
			theProcess = ProcessBuilder.createProcess("twoHumanTasksInParallelProcess" + i, "1.0").addHuman(adminUsername).addSystemTask("start").addHumanTask("aHumanTask",
					adminUsername).addHumanTask("t", adminUsername).addTransition("start", "aHumanTask").addTransition("start", "t").done();
			theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));
		}
		theProcesses = ProcessDataStore.getInstance().getAllProcesses(myRequest, adminUserProfile);
		assertNotNull(theProcesses);
		assertEquals(theNbOfProcesses, theProcesses.size());

		AccessorUtil.getCommandAPI().execute(new WebDeleteAllProcessesCommand());
		Utils.logout();
	}

	@Test
	public void testGetAllProcessesHttpServletRequestBoolean() throws Exception {

		Utils.login(adminUsername, "bpm");

		Set<BonitaProcess> theProcesses = ProcessDataStore.getInstance().getAllProcesses(myRequest, adminUserProfile);
		assertNotNull(theProcesses);
		assertEquals(0, theProcesses.size());

		// Process definitions deployment.
		final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
		ProcessDefinition theProcess;
		int theNbOfProcesses = 17;
		for (int i = 0; i < theNbOfProcesses; i++) {
			theProcess = ProcessBuilder.createProcess("twoHumanTasksInParallelProcess" + i, "1.0").addHuman(adminUsername).addSystemTask("start").addHumanTask("aHumanTask",
					adminUsername).addHumanTask("t", adminUsername).addTransition("start", "aHumanTask").addTransition("start", "t").done();
			theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));
		}
		theProcesses = ProcessDataStore.getInstance().getAllProcesses(myRequest, adminUserProfile);
		assertNotNull(theProcesses);
		assertEquals(theNbOfProcesses, theProcesses.size());

		AccessorUtil.getCommandAPI().execute(new WebDeleteAllProcessesCommand());
		Utils.logout();
	}

	@Test
	public void testDeleteAllProcessInstances() throws Exception {
		final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
		final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
		final QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

		Utils.login(adminUsername, "bpm");

		Set<LightProcessInstance> theProcessInstances = theQueryRuntimeAPI.getLightProcessInstances();
		assertNotNull(theProcessInstances);
		assertEquals(0, theProcessInstances.size());

		// Process definitions deployment and instantiation.
		ProcessDefinition theProcess;
		int theNbOfInstances = 41;
		Collection<BonitaProcessUUID> theProcessDefinitionUUIDs = new ArrayList<BonitaProcessUUID>();
		for (int i = 0; i < theNbOfInstances; i++) {
			theProcess = ProcessBuilder.createProcess("twoHumanTasksInParallelProcess" + i, "1.0").addHuman(adminUsername).addSystemTask("start").addHumanTask("aHumanTask",
					adminUsername).addHumanTask("t", adminUsername).addTransition("start", "aHumanTask").addTransition("start", "t").done();
			theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));
			theRuntimeAPI.instantiateProcess(theProcess.getUUID());
			theProcessDefinitionUUIDs.add(new BonitaProcessUUID(theProcess.getUUID().getValue(), theProcess.getUUID().getValue()));
		}

		theProcessInstances = theQueryRuntimeAPI.getLightProcessInstances();
		assertNotNull(theProcessInstances);
		assertEquals(theNbOfInstances, theProcessInstances.size());

		ProcessDataStore.getInstance().deleteAllInstances(theProcessDefinitionUUIDs, true);

		theProcessInstances = theQueryRuntimeAPI.getLightProcessInstances();
		assertNotNull(theProcessInstances);
		assertEquals(0, theProcessInstances.size());

		AccessorUtil.getCommandAPI().execute(new WebDeleteAllProcessesCommand());
		Utils.logout();
	}

	@Test
	public void testDeleteProcesses() throws Exception {
		final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
		final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
		final QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

		Utils.login(adminUsername, "bpm");

		Set<BonitaProcess> theProcesses = ProcessDataStore.getInstance().getAllProcesses(myRequest, adminUserProfile);
		assertNotNull(theProcesses);
		assertEquals(0, theProcesses.size());

		Set<LightProcessInstance> theProcessInstances = theQueryRuntimeAPI.getLightProcessInstances();
		assertNotNull(theProcessInstances);
		assertEquals(0, theProcessInstances.size());

		// Process definitions deployment and instantiation.
		ProcessDefinition theProcess;
		int theNbOfInstances = 8;
		Collection<BonitaProcessUUID> theProcessDefinitionUUIDs = new ArrayList<BonitaProcessUUID>();
		for (int i = 0; i < theNbOfInstances; i++) {
			theProcess = ProcessBuilder.createProcess("twoHumanTasksInParallelProcess" + i, "1.0").addHuman(adminUsername).addSystemTask("start").addHumanTask("aHumanTask",
					adminUsername).addHumanTask("t", adminUsername).addTransition("start", "aHumanTask").addTransition("start", "t").done();
			theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));
			theRuntimeAPI.instantiateProcess(theProcess.getUUID());
			theProcessDefinitionUUIDs.add(new BonitaProcessUUID(theProcess.getUUID().getValue(), theProcess.getUUID().getValue()));
		}

		theProcessInstances = theQueryRuntimeAPI.getLightProcessInstances();
		assertNotNull(theProcessInstances);
		assertEquals(theNbOfInstances, theProcessInstances.size());

		final ProcessFilter theProcessFilter = new ProcessFilter(0, 20);
		ProcessDataStore.getInstance().deleteProcesses(myRequest, adminUserProfile, theProcessDefinitionUUIDs, theProcessFilter, true);

		// Check that there is no more instances.
		theProcessInstances = theQueryRuntimeAPI.getLightProcessInstances();
		assertNotNull(theProcessInstances);
		assertEquals(0, theProcessInstances.size());

		theProcesses = ProcessDataStore.getInstance().getAllProcesses(myRequest, adminUserProfile);
		assertNotNull(theProcesses);
		assertEquals(0, theProcesses.size());

		AccessorUtil.getCommandAPI().execute(new WebDeleteAllProcessesCommand());
		Utils.logout();
	}

	@Test
	public void testDeleteProcessesAndCheckLabels() throws Exception {
		final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
		final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
		final QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

		Utils.login(adminUsername, "bpm");

		Set<BonitaProcess> theProcesses = ProcessDataStore.getInstance().getAllProcesses(myRequest, adminUserProfile);
		assertNotNull(theProcesses);
		assertEquals(0, theProcesses.size());

		Set<LightProcessInstance> theProcessInstances = theQueryRuntimeAPI.getLightProcessInstances();
		assertNotNull(theProcessInstances);
		assertEquals(0, theProcessInstances.size());

		// Process definitions deployment and instantiation.
		ProcessDefinition theProcess;
		int theNbOfInstances = 8;
		Collection<BonitaProcessUUID> theProcessDefinitionUUIDs = new ArrayList<BonitaProcessUUID>();
		for (int i = 0; i < theNbOfInstances; i++) {
			theProcess = ProcessBuilder.createProcess("twoHumanTasksInParallelProcess" + i, "1.0").addHuman(adminUsername).addSystemTask("start").addHumanTask("aHumanTask",
					adminUsername).addHumanTask("t", adminUsername).addTransition("start", "aHumanTask").addTransition("start", "t").done();
			theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));
			theRuntimeAPI.instantiateProcess(theProcess.getUUID());
			theProcessDefinitionUUIDs.add(new BonitaProcessUUID(theProcess.getUUID().getValue(), theProcess.getUUID().getValue()));

		}

		theProcessInstances = theQueryRuntimeAPI.getLightProcessInstances();
		assertNotNull(theProcessInstances);
		assertEquals(theNbOfInstances, theProcessInstances.size());

		// List the Inbox,i.e. add labels (Inbox and MyCases) to instances.
		ArrayList<LabelUUID> theLabels = new ArrayList<LabelUUID>();
		final String theInboxLabelName = LabelModel.INBOX_LABEL.getUUID().getValue();
		final LabelUUID theInboxLabelUUID = new LabelUUID(theInboxLabelName, new UserUUID(adminUsername));
		theLabels.add(theInboxLabelUUID);
		CaseFilter theCaseFilter = new CaseFilter(theLabels, 0, 20);
		CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest);

		// Check labels data

		LabelUpdates theLabelUpdates = LabelDataStore.getInstance().getLabelUpdates(adminUserProfile, theInboxLabelUUID, false);
		assertNotNull(theLabelUpdates);
		assertEquals(theInboxLabelName, theLabelUpdates.getLabelName());
		assertEquals(theNbOfInstances, theLabelUpdates.getNbOfCases());

		final LabelUUID theAllCasesLabelUUID = LabelModel.ADMIN_ALL_CASES.getUUID();
		final String theAllCasesLabelName = theAllCasesLabelUUID.getValue();

		theLabelUpdates = LabelDataStore.getInstance().getLabelUpdates(adminUserProfile, theAllCasesLabelUUID, false);
		assertNotNull(theLabelUpdates);
		assertEquals(theAllCasesLabelName, theLabelUpdates.getLabelName());
		assertEquals(theNbOfInstances, theLabelUpdates.getNbOfCases());

		final ProcessFilter theProcessFilter = new ProcessFilter(0, 20);
		ProcessDataStore.getInstance().deleteProcesses(myRequest, adminUserProfile,theProcessDefinitionUUIDs,theProcessFilter,true);

		// Check that there is no more instances.
		theProcessInstances = theQueryRuntimeAPI.getLightProcessInstances();
		assertNotNull(theProcessInstances);
		assertEquals(0, theProcessInstances.size());

		// Check that there is no more processes.
		theProcesses = ProcessDataStore.getInstance().getAllProcesses(myRequest, adminUserProfile);
		assertNotNull(theProcesses);
		assertEquals(0, theProcesses.size());

		// Check labels data again (this time there should be 0 instances
		// associated to labels).
		theLabelUpdates = LabelDataStore.getInstance().getLabelUpdates(adminUserProfile, theInboxLabelUUID, false);
		assertNotNull(theLabelUpdates);
		assertEquals(theInboxLabelName, theLabelUpdates.getLabelName());
		assertEquals(0, theLabelUpdates.getNbOfCases());

		AccessorUtil.getCommandAPI().execute(new WebDeleteAllProcessesCommand());
		Utils.logout();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetProcessPictureNullBehaviour() throws Exception {
		Utils.login(adminUsername, "bpm");
		ProcessDataStore.getInstance().getProcessPicture(null);

		// This should not be executed. Is there in case of failure of the test.
		// In case of success the teardown method will handle this for us.
		Utils.logout();
	}

	@Test
	public void testGetProcessPictureNoPicture() throws Exception {
		Utils.login(adminUsername, "bpm");
		final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();

		// Build the bar and deploy it.
		ProcessDefinition theProcess = ProcessBuilder.createProcess("aProcessWithoutPicture", "1.0").addHuman(adminUsername).addSystemTask("start").addHumanTask("aHumanTask",
				adminUsername).addHumanTask("t", adminUsername).addTransition("start", "aHumanTask").addTransition("start", "t").done();
		BusinessArchive theBar = BusinessArchiveFactory.getBusinessArchive(theProcess);
		theManagementAPI.deploy(theBar);

		String theProcessPicture = ProcessDataStore.getInstance().getProcessPicture(new BonitaProcessUUID(theProcess.getUUID().getValue(), theProcess.getUUID().getValue()));
		assertNull(theProcessPicture);
		
		AccessorUtil.getCommandAPI().execute(new WebDeleteAllProcessesCommand());
		Utils.logout();
	}

	@Test
	public void testGetProcessPictureWithPicture() throws Exception {
		Utils.login(adminUsername, "bpm");
		final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();

		// Build the bar with its resources and deploy the whole.
		ProcessDefinition theProcess = ProcessBuilder.createProcess("aProcessWithPicture", "1.0").addHuman(adminUsername).addSystemTask("start").addHumanTask("aHumanTask",
				adminUsername).addHumanTask("t", adminUsername).addTransition("start", "aHumanTask").addTransition("start", "t").done();
		Map<String, byte[]> theResources = new HashMap<String, byte[]>();
		theResources.put(theProcess.getUUID().getValue() + ".png", new byte[0]);
		BusinessArchive theBar = BusinessArchiveFactory.getBusinessArchive(theProcess, theResources);
		theManagementAPI.deploy(theBar);

		String theProcessPicture = ProcessDataStore.getInstance().getProcessPicture(new BonitaProcessUUID(theProcess.getUUID().getValue(), theProcess.getUUID().getValue()));
		assertNotNull(theProcessPicture);
		
		AccessorUtil.getCommandAPI().execute(new WebDeleteAllProcessesCommand());
		Utils.logout();
	}
}
