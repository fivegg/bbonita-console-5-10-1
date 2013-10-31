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
package org.bonitasoft.console.server.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.server.BonitaTestCase;
import org.bonitasoft.console.server.Utils;
import org.junit.Test;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.ProcessBuilder;

/**
 * @author Nicolas Chabanoles
 *
 */
public class StepDataStoreTest extends BonitaTestCase {

	/**
	 * Test method for {@link org.bonitasoft.console.server.steps.StepDataStore#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		assertNotNull(StepDataStore.getInstance());
	}

//	/**
//	 * Test method for {@link org.bonitasoft.console.server.steps.StepDataStore#unassignStep(org.bonitasoft.console.client.steps.StepUUID)}.
//	 */
//	@Test
//	public void testUnassignStep() throws Exception {
//		Utils.login(adminUserUUID.getValue(), "bpm");
//		
//		ProcessDefinition theProcess = ProcessBuilder.createProcess("MyProcess", "1.0").addHuman(adminUserUUID.getValue()).addHumanTask("t", adminUserUUID.getValue()).done();
//		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));
//		final ProcessDefinitionUUID theProcessUUID = theProcess.getUUID();
//		// Start a case.
//		ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(theProcessUUID);
//		Set<ActivityInstance> theActivities = AccessorUtil.getQueryRuntimeAPI().getActivityInstances(theProcessInstanceUUID);
//		assertEquals(1, theActivities.size());
//		ActivityInstance theActivityInstance = theActivities.iterator().next();
//		final StepUUID theStepUUID = new StepUUID(theActivityInstance.getUUID().getValue());
//		final StepDataStore theStepDataStore = StepDataStore.getInstance();
//		
//		theStepDataStore.unassignStep(theStepUUID);
//		fail("Not yet implemented"); // TODO
//		
//		Utils.logout();
//	}

//	/**
//	 * Test method for {@link org.bonitasoft.console.server.steps.StepDataStore#suspendStep(org.bonitasoft.console.client.steps.StepUUID, boolean)}.
//	 */
//	@Test
//	public void testSuspendStep() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	/**
//	 * Test method for {@link org.bonitasoft.console.server.steps.StepDataStore#resumeStep(org.bonitasoft.console.client.steps.StepUUID, boolean)}.
//	 */
//	@Test
//	public void testResumeStep() {
//		fail("Not yet implemented"); // TODO
//	}

	
	@Test
	public void testAssignStepEngine() throws Exception {
		Utils.login(adminUsername, "bpm");
		
		final QueryRuntimeAPI theQueryRuntime = AccessorUtil.getQueryRuntimeAPI();
		final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
		
		ProcessDefinition theProcess = ProcessBuilder.createProcess("MyProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));
		final ProcessDefinitionUUID theProcessUUID = theProcess.getUUID();
		// Start a case.
		ProcessInstanceUUID theProcessInstanceUUID = theRuntimeAPI.instantiateProcess(theProcessUUID);
		Set<ActivityInstance> theActivities = theQueryRuntime.getActivityInstances(theProcessInstanceUUID);
		assertEquals(1, theActivities.size());
		TaskInstance theTaskInstance = theActivities.iterator().next().getTask();

		

		assertEquals(1, theTaskInstance.getTaskCandidates().size());
		assertEquals(adminUsername, theTaskInstance.getTaskCandidates().iterator().next());
		
		theRuntimeAPI.assignTask(theTaskInstance.getUUID(), testUsername);
		
		
		theActivities = theQueryRuntime.getActivityInstances(theProcessInstanceUUID);
		assertEquals(1, theActivities.size());
		theTaskInstance = theActivities.iterator().next().getTask();				
		assertTrue(theTaskInstance.isTaskAssigned());
		assertEquals(testUsername, theTaskInstance.getTaskUser());
				
		
		HashSet<String> theCandidates = new HashSet<String>();
		theCandidates.add(testUsername);
		theCandidates.add(adminUsername);
		theRuntimeAPI.assignTask(theTaskInstance.getUUID(), theCandidates);
				
		theActivities = theQueryRuntime.getActivityInstances(theProcessInstanceUUID);
		assertEquals(1, theActivities.size());
		theTaskInstance = theActivities.iterator().next().getTask();
		assertFalse(theTaskInstance.isTaskAssigned());
		assertEquals(2, theTaskInstance.getTaskCandidates().size());
		assertTrue(theTaskInstance.getTaskCandidates().contains(adminUsername));
		assertTrue(theTaskInstance.getTaskCandidates().contains(testUsername));
		
		theRuntimeAPI.assignTask(theTaskInstance.getUUID(), adminUsername);
		
		
		theActivities = theQueryRuntime.getActivityInstances(theProcessInstanceUUID);
		assertEquals(1, theActivities.size());
		theTaskInstance = theActivities.iterator().next().getTask();				
		assertTrue(theTaskInstance.isTaskAssigned());
		assertEquals(adminUsername, theTaskInstance.getTaskUser());
		
		Utils.logout();
	}
	
	
	/**
	 * Test method for {@link org.bonitasoft.console.server.steps.StepDataStore#assignStep(org.bonitasoft.console.client.steps.StepUUID, java.util.Set)}.
	 */
	@Test
	public void testAssignStepStepUUIDSetOfUserUUID() throws Exception {
		Utils.login(adminUsername, "bpm");
		
		ProcessDefinition theProcess = ProcessBuilder.createProcess("MyProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));
		final ProcessDefinitionUUID theProcessUUID = theProcess.getUUID();
		// Start a case.
		ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(theProcessUUID);
		Set<ActivityInstance> theActivities = AccessorUtil.getQueryRuntimeAPI().getActivityInstances(theProcessInstanceUUID);
		assertEquals(1, theActivities.size());
		TaskInstance theTaskInstance = theActivities.iterator().next().getTask();
		final StepUUID theStepUUID = new StepUUID(theTaskInstance.getUUID().getValue(), theTaskInstance.getUUID().getActivityDefinitionUUID().getValue());
		final StepDataStore theStepDataStore = StepDataStore.getInstance();
		

		if(theTaskInstance.isTaskAssigned()){
			assertEquals(adminUsername, theTaskInstance.getTaskUser());
		} else {
			assertEquals(1, theTaskInstance.getTaskCandidates().size());
			assertEquals(adminUsername, theTaskInstance.getTaskCandidates().iterator().next());
		}
		
		final Set<UserUUID> theCandidates = new HashSet<UserUUID>();
		theCandidates.add(new UserUUID(adminUsername));
		theCandidates.add(new UserUUID(testUsername));
		theStepDataStore.assignStep(theStepUUID, theCandidates );
		
		theTaskInstance = AccessorUtil.getQueryRuntimeAPI().getActivityInstance(theTaskInstance.getUUID()).getTask();
		
		assertFalse(theTaskInstance.isTaskAssigned());
		assertEquals(2, theTaskInstance.getTaskCandidates().size());
				
		Utils.logout();
	}

//	/**
//	 * Test method for {@link org.bonitasoft.console.server.steps.StepDataStore#assignStep(org.bonitasoft.console.client.steps.StepUUID, org.bonitasoft.console.client.users.UserUUID)}.
//	 */
//	@Test
//	public void testAssignStepStepUUIDUserUUID() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	/**
//	 * Test method for {@link org.bonitasoft.console.server.steps.StepDataStore#getStepCommentFeed(org.bonitasoft.console.client.steps.StepUUID)}.
//	 */
//	@Test
//	public void testGetStepCommentFeed() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	/**
//	 * Test method for {@link org.bonitasoft.console.server.steps.StepDataStore#addStepComment(org.bonitasoft.console.client.steps.StepUUID, java.lang.String, org.bonitasoft.console.client.users.UserProfile)}.
//	 */
//	@Test
//	public void testAddStepComment() {
//		fail("Not yet implemented"); // TODO
//	}

}
