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
package org.bonitasoft.console.server.bam.birt.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import org.bonitasoft.console.server.BonitaTestCase;
import org.bonitasoft.console.server.Utils;
import org.junit.Test;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition.JoinType;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.Misc;
import org.ow2.bonita.util.ProcessBuilder;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class PerfsDataSourceTest extends BonitaTestCase {

	/**
	 * Test method for {@link org.bonitasoft.console.server.bam.birt.datasource.PerfsDataSource#readData()}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReadData() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("p0", adminUsername).addActivityPriority(0)
				.addHumanTask("p1", adminUsername).addActivityPriority(1).addHumanTask("p2", adminUsername).addActivityPriority(2).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readPerf();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(3, theRow.length);
		assertEquals(Misc.getActivityPriority(0, Locale.ENGLISH), theRow[0]);
		assertEquals(0, theRow[1]); // Steps done with priority 0.
		assertEquals(1, theRow[2]); // Steps to do with priority 0.

		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(3, theRow.length);
		assertEquals(Misc.getActivityPriority(1, Locale.ENGLISH), theRow[0]);
		assertEquals(0, theRow[1]); // Steps done with priority 1.
		assertEquals(1, theRow[2]); // Steps to do with priority 1.

		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(3, theRow.length);
		assertEquals(Misc.getActivityPriority(2, Locale.ENGLISH), theRow[0]);
		assertEquals(0, theRow[1]); // Steps done with priority 2.
		assertEquals(1, theRow[2]); // Steps to do with priority 2.

		// Execute tasks with priority 2
		// AccessorUtil.getRuntimeAPI().executeTask(taskUUID, true);
		// Execute tasks with priority 0

		// Execute tasks with priority 1
		Utils.logout();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadData2() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 1000).addTransition("Start", "overdue").addTransition(
				"Start", "onTrack").addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readPerf();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(3, theRow.length);
		assertEquals(Misc.getActivityPriority(0, Locale.ENGLISH), theRow[0]);
		assertEquals(0, theRow[1]); // Steps done with priority 0.
		assertEquals(1, theRow[2]); // Steps to do with priority 0.

		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(3, theRow.length);
		assertEquals(Misc.getActivityPriority(1, Locale.ENGLISH), theRow[0]);
		assertEquals(0, theRow[1]); // Steps done with priority 1.
		assertEquals(1, theRow[2]); // Steps to do with priority 1.

		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(3, theRow.length);
		assertEquals(Misc.getActivityPriority(2, Locale.ENGLISH), theRow[0]);
		assertEquals(0, theRow[1]); // Steps done with priority 2.
		assertEquals(1, theRow[2]); // Steps to do with priority 2.

		// Execute tasks with priority 2
		// AccessorUtil.getRuntimeAPI().executeTask(taskUUID, true);
		// Execute tasks with priority 0

		// Execute tasks with priority 1
		Utils.logout();
	}

	@Test
	public void testUserTaskAtRisk() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(999999999).addHumanTask(
						"atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsAtRisk = AccessorUtil.getBAMAPI().getNumberOfUserStepsAtRisk(0);

		assertEquals(1, theStepsAtRisk);

	}

	@Test
	public void testUserTaskAtRiskWithMultipleInstances() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 1000).addTransition("Start", "overdue").addTransition(
				"Start", "onTrack").addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsAtRisk = AccessorUtil.getBAMAPI().getNumberOfUserStepsAtRisk(0);

		assertEquals(2, theStepsAtRisk);

	}

	@Test
	public void testAllTaskAtRiskWithMultipleInstances() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 1000).addTransition("Start", "overdue").addTransition(
				"Start", "onTrack").addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsAtRisk = AccessorUtil.getBAMAPI().getNumberOfStepsAtRisk(0);

		assertEquals(2, theStepsAtRisk);

	}

	@Test
	public void testUserTaskAtRiskWithoutPriority() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityExecutingTime(999999999).addHumanTask("atRisk", adminUsername)
				.addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsAtRisk = AccessorUtil.getBAMAPI().getNumberOfUserStepsAtRisk(0);

		assertEquals(1, theStepsAtRisk);

	}

	@Test
	public void testTaskAtRiskWithoutEstimatedTime() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername).addHumanTask(
				"onTrack", adminUsername).addHumanTask("atRisk", adminUsername).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		int theStepsAtRisk = AccessorUtil.getBAMAPI().getNumberOfUserStepsAtRisk(0);

		assertEquals(0, theStepsAtRisk);

	}

	@Test
	public void testUserTaskOverdue() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(999999999).addHumanTask(
						"atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsOverdue = AccessorUtil.getBAMAPI().getNumberOfUserOverdueSteps();

		assertEquals(1, theStepsOverdue);

	}

	@Test
	public void testAllTaskOverdue() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(999999999).addHumanTask(
						"atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsOverdue = AccessorUtil.getBAMAPI().getNumberOfOverdueSteps();

		assertEquals(1, theStepsOverdue);

	}

	@Test
	public void testTaskOverdueWithoutPriority() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityExecutingTime(999999999).addHumanTask("atRisk", adminUsername)
				.addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsOverdue = AccessorUtil.getBAMAPI().getNumberOfUserOverdueSteps();

		assertEquals(1, theStepsOverdue);

	}

	@Test
	public void testTaskOverDueWithoutEstimatedTime() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername).addHumanTask(
				"onTrack", adminUsername).addHumanTask("atRisk", adminUsername).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		int theStepsOverDue = AccessorUtil.getBAMAPI().getNumberOfUserOverdueSteps();

		assertEquals(0, theStepsOverDue);

	}

	@Test
	public void testAllTaskOpen() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(999999999).addHumanTask(
						"atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsOpen = AccessorUtil.getBAMAPI().getNumberOfOpenSteps();

		assertEquals(3, theStepsOpen);

	}

	@Test
	public void testUserTaskOpen() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSystemTask("Start").addHuman(adminUsername).addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 60 * 1000).addTransition("Start", "overdue").addTransition(
				"Start", "onTrack").addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsOpen = AccessorUtil.getBAMAPI().getNumberOfUserOpenSteps();

		assertEquals(3, theStepsOpen);

	}

	@Test
	public void testTaskOpenWithoutPriority() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityExecutingTime(999999999).addHumanTask("atRisk", adminUsername)
				.addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsOpen = AccessorUtil.getBAMAPI().getNumberOfOpenSteps();

		assertEquals(3, theStepsOpen);

	}

	@Test
	public void testUserTaskOpenWithoutPriority() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityExecutingTime(999999999).addHumanTask("atRisk", adminUsername)
				.addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);
		int theStepsOpen = AccessorUtil.getBAMAPI().getNumberOfUserOpenSteps();

		assertEquals(3, theStepsOpen);

	}

	@Test
	public void testTaskOpenWithoutEstimatedTime() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername).addHumanTask(
				"onTrack", adminUsername).addHumanTask("atRisk", adminUsername).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		int theStepsOpen = AccessorUtil.getBAMAPI().getNumberOfOpenSteps();

		assertEquals(3, theStepsOpen);

	}

	@Test
	public void testUserTaskOpenWithoutEstimatedTime() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername).addHumanTask(
				"onTrack", adminUsername).addHumanTask("atRisk", adminUsername).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		int theStepsOpen = AccessorUtil.getBAMAPI().getNumberOfUserOpenSteps();

		assertEquals(3, theStepsOpen);

	}

	@Test
	public void testTaskOnTrack() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(999999999).addHumanTask(
						"atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);

		int theAtRiskSteps = AccessorUtil.getBAMAPI().getNumberOfUserStepsAtRisk(0);
		int theOverDueTasks = AccessorUtil.getBAMAPI().getNumberOfUserOverdueSteps();
		int theOpenSteps = AccessorUtil.getBAMAPI().getNumberOfUserOpenSteps();

		assertEquals(1, (theOpenSteps - theOverDueTasks - theAtRiskSteps));

	}

	@Test
	public void testTaskOnTrackWithoutPriority() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityExecutingTime(999999999).addHumanTask("atRisk", adminUsername)
				.addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);

		int theAtRiskSteps = AccessorUtil.getBAMAPI().getNumberOfUserStepsAtRisk(0);
		int theOverDueTasks = AccessorUtil.getBAMAPI().getNumberOfUserOverdueSteps();
		int theOpenSteps = AccessorUtil.getBAMAPI().getNumberOfUserOpenSteps();

		assertEquals(1, (theOpenSteps - theOverDueTasks - theAtRiskSteps));

	}

	@Test
	public void testTaskOnTrackEstimatedTime() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername).addHumanTask(
				"onTrack", adminUsername).addHumanTask("atRisk", adminUsername).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);

		int theAtRiskSteps = AccessorUtil.getBAMAPI().getNumberOfUserStepsAtRisk(0);
		int theOverDueTasks = AccessorUtil.getBAMAPI().getNumberOfUserOverdueSteps();
		int theOpenSteps = AccessorUtil.getBAMAPI().getNumberOfUserOpenSteps();

		assertEquals(3, (theOpenSteps - theOverDueTasks - theAtRiskSteps));

	}

	/**
	 * Test method for {@link org.bonitasoft.console.server.bam.birt.datasource.PerfsDataSource#readUserStepsRepartition()}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReadUserStepsRepartition() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(999999999).addHumanTask(
						"atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(10000).done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readUserStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open overDue.
		Utils.logout();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadUserStepsRepartitionWithMultipleInstances() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 1000).addTransition("Start", "overdue").addTransition(
				"Start", "onTrack").addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start multiple cases.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(4000);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readUserStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open overDue.
		Utils.logout();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadUserStepsRepartitionWithMultipleInstancesEnsureOverdue() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(10 * 1000).addTransition("Start", "overdue").addTransition(
				"Start", "onTrack").addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start multiple cases.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(11 * 1000);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readUserStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(0, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(4, theRow[1]); // Steps open overDue.
		Utils.logout();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadUserStepsRepartitionWithMultipleInstancesOfDifferentProcesses() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition aProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername).addActivityPriority(0)
				.addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(2 * 24 * 60 * 60 * 1000).addHumanTask("atRisk",
						adminUsername).addActivityPriority(2).addActivityExecutingTime(60 * 1000).done();

		ProcessDefinition overdueProcess = ProcessBuilder.createProcess("overdueProcess", "1.0").addHuman(adminUsername).addHumanTask("overdue", adminUsername)
				.addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("overdue2", adminUsername).addActivityPriority(1).addActivityExecutingTime(1000).addHumanTask(
						"overdue3", adminUsername).addActivityPriority(2).addActivityExecutingTime(1000).done();

		ProcessDefinition onTrackProcess = ProcessBuilder.createProcess("onTrackProcess", "1.0").addHuman(adminUsername).addHumanTask("ontrack", adminUsername)
				.addActivityPriority(0).addActivityExecutingTime(3 * 24 * 60 * 60 * 1000).addHumanTask("ontrack2", adminUsername).addActivityPriority(1).addActivityExecutingTime(
						3 * 24 * 60 * 60 * 1000).addHumanTask("ontrack3", adminUsername).addActivityPriority(2).addActivityExecutingTime(3 * 24 * 60 * 60 * 1000).done();

		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(aProcess));
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(overdueProcess));
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(onTrackProcess));

		final ProcessDefinitionUUID processUUID = aProcess.getUUID();
		final ProcessDefinitionUUID overDueProcessUUID = overdueProcess.getUUID();
		final ProcessDefinitionUUID onTrackProcessUUID = onTrackProcess.getUUID();

		// Start multiple cases.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		AccessorUtil.getRuntimeAPI().instantiateProcess(overDueProcessUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(overDueProcessUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(overDueProcessUUID);

		AccessorUtil.getRuntimeAPI().instantiateProcess(onTrackProcessUUID);

		// Be sure the step is overdue.
		Thread.sleep(4000);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readUserStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(4, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(7, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(13, theRow[1]); // Steps open overDue.
		Utils.logout();
	}

	/**
	 * Test method for {@link org.bonitasoft.console.server.bam.birt.datasource.PerfsDataSource#readStepsRepartition()}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReadStepsRepartition() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				999999999).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(10000).addTransition("Start", "overdue").addTransition("Start", "onTrack")
				.addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open overDue.
		Utils.logout();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadStepsRepartitionWithMultipleInstances() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 1000).addTransition("Start", "overdue").addTransition(
				"Start", "onTrack").addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start multiple cases.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(4000);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open overDue.
		Utils.logout();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadUserStepsRepartitionWithMultipleInstances2() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 1000).addTransition("Start", "overdue").addTransition(
				"Start", "onTrack").addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start multiple cases.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(4000);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readUserStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(2, theRow[1]); // Steps open overDue.
		Utils.logout();
	}

	/**
	 * Test method for {@link org.bonitasoft.console.server.bam.birt.datasource.PerfsDataSource#readFinishedCases()}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReadUserPerf() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 1000).addTransition("Start", "overdue").addTransition(
				"Start", "onTrack").addTransition("Start", "atRisk").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start multiple cases.
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(4000);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readUserPerf();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(3, theRow.length);
		assertEquals(Misc.getActivityPriority(0, Locale.ENGLISH), theRow[0]);
		assertEquals(0, theRow[1]); // Steps done with priority 0.
		assertEquals(2, theRow[2]); // Steps to do with priority 0.

		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(3, theRow.length);
		assertEquals(Misc.getActivityPriority(1, Locale.ENGLISH), theRow[0]);
		assertEquals(0, theRow[1]); // Steps done with priority 1.
		assertEquals(2, theRow[2]); // Steps to do with priority 1.

		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(3, theRow.length);
		assertEquals(Misc.getActivityPriority(2, Locale.ENGLISH), theRow[0]);
		assertEquals(0, theRow[1]); // Steps done with priority 2.
		assertEquals(2, theRow[2]); // Steps to do with priority 2.

		// Execute tasks with priority 2
		// AccessorUtil.getRuntimeAPI().executeTask(taskUUID, true);
		// Execute tasks with priority 0

		// Execute tasks with priority 1
		Utils.logout();

	}

	/**
	 * Test method for {@link org.bonitasoft.console.server.bam.birt.datasource.PerfsDataSource#readFinishedCases()}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testReadFinishedCases() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overdue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 1000).addSystemTask("GateAnd").addJoinType(JoinType.AND)
				.addTransition("Start", "overdue").addTransition("Start", "onTrack").addTransition("Start", "atRisk").addTransition("onTrack", "GateAnd").addTransition("overdue", "GateAnd")
				.addTransition("atRisk", "GateAnd").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start multiple cases.
		ProcessInstanceUUID case1 = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		ProcessInstanceUUID case2 = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(4000);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		// [14*int]
		Vector theData = thePerfsDataSource.readFinishedCases();
		assertNotNull(theData);

		Object[] theRow;
		// check values
		// No cases are finished yet.
		assertTrue("The method returned data but was supposed to be empty!", theData.isEmpty());

		// terminate case 1
		Set<ActivityInstance> theActivities = AccessorUtil.getQueryRuntimeAPI().getActivityInstances(case1);
		for (ActivityInstance theActivityInstance : theActivities) {
			if (theActivityInstance.isTask()) {
				AccessorUtil.getRuntimeAPI().executeTask(theActivityInstance.getUUID(), false);
			}
		}
		assertEquals(InstanceState.FINISHED, AccessorUtil.getQueryRuntimeAPI().getLightProcessInstance(case1).getInstanceState());
		// check values
		theData = thePerfsDataSource.readFinishedCases();
		System.out.println("Finished cases (should be 14*0 + 1*1)");
		for (int i = 0; i < 14; i++) {
			theRow = ((Object[]) theData.get(i));
			assertNotNull(theRow);
			System.out.println(theRow[0] + " --> " + theRow[1]);
			assertEquals(2, theRow.length);
			assertEquals(0, theRow[1]); // Cases terminated on day d-14+i
		}
		theRow = ((Object[]) theData.get(14));
		assertNotNull(theRow);
		System.out.println(theRow[0] + " --> " + theRow[1]);
		assertEquals(2, theRow.length);
		assertEquals(1, theRow[1]); // Cases terminated on day d (case1)

		// terminate case 2
		theActivities = AccessorUtil.getQueryRuntimeAPI().getActivityInstances(case2);
		for (ActivityInstance theActivityInstance : theActivities) {
			if (theActivityInstance.isTask()) {
				AccessorUtil.getRuntimeAPI().executeTask(theActivityInstance.getUUID(), true);
			}
		}
		assertEquals(InstanceState.FINISHED, AccessorUtil.getQueryRuntimeAPI().getLightProcessInstance(case2).getInstanceState());
		// check values
		theData = thePerfsDataSource.readFinishedCases();
		System.out.println("Finished cases (should be 13*0 + 1*2)");
		for (int i = 0; i < 14; i++) {
			theRow = ((Object[]) theData.get(i));
			assertNotNull(theRow);
			System.out.println(theRow[0] + " --> " + theRow[1]);
			assertEquals(2, theRow.length);
			assertEquals(0, theRow[1]); // Cases terminated on day d-14+i
		}
		theRow = ((Object[]) theData.get(14));
		assertNotNull(theRow);
		System.out.println(theRow[0] + " --> " + theRow[1]);
		assertEquals(2, theRow.length);
		assertEquals(2, theRow[1]); // Cases terminated on day d (case1 & case2)

		Utils.logout();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadUserStepsRepartition2() throws Exception {

		Utils.login(adminUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("Start").addHumanTask("overDue",
				adminUsername).addActivityPriority(0).addActivityExecutingTime(1000).addHumanTask("onTrack", adminUsername).addActivityPriority(1).addActivityExecutingTime(
				72 * 60 * 60 * 1000).addHumanTask("atRisk", adminUsername).addActivityPriority(2).addActivityExecutingTime(90 * 1000).addSystemTask("GateAnd").addJoinType(JoinType.AND)
				.addTransition("Start", "overDue").addTransition("Start", "onTrack").addTransition("Start", "atRisk").addTransition("onTrack", "GateAnd").addTransition("overDue", "GateAnd")
				.addTransition("atRisk", "GateAnd").done();
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		ProcessInstanceUUID case1 = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		// Be sure the step is overdue.
		Thread.sleep(1010);

		PerfsDataSource thePerfsDataSource = new PerfsDataSource();
		Vector theData = thePerfsDataSource.readUserStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		Object[] theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open overDue.

		// execute step atRisk
		Set<ActivityInstance> theActivities = AccessorUtil.getQueryRuntimeAPI().getActivityInstances(case1);
		for (ActivityInstance theActivityInstance : theActivities) {
			if (theActivityInstance.isTask() && theActivityInstance.getActivityName().equals("atRisk")) {
				AccessorUtil.getRuntimeAPI().executeTask(theActivityInstance.getUUID(), false);
			}
		}

		theData = thePerfsDataSource.readUserStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(0, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open overDue.

		// execute step overdue
		theActivities = AccessorUtil.getQueryRuntimeAPI().getActivityInstances(case1);
		for (ActivityInstance theActivityInstance : theActivities) {
			if (theActivityInstance.isTask() && theActivityInstance.getActivityName().equals("overDue")) {
				AccessorUtil.getRuntimeAPI().executeTask(theActivityInstance.getUUID(), false);
			}
		}

		theData = thePerfsDataSource.readUserStepsRepartition();
		assertNotNull(theData);
		assertTrue(!theData.isEmpty());
		assertEquals(3, theData.size());
		theRow = ((Object[]) theData.get(0));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("at risk", theRow[0]);
		assertEquals(0, theRow[1]); // Steps open atRisk.
		//
		theRow = ((Object[]) theData.get(1));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("on track", theRow[0]);
		assertEquals(1, theRow[1]); // Steps open onTrack.
		//
		theRow = ((Object[]) theData.get(2));
		assertNotNull(theRow);
		assertEquals(2, theRow.length);
		assertEquals("overdue", theRow[0]);
		assertEquals(0, theRow[1]); // Steps open overDue.

		// execute step onTrack
		theActivities = AccessorUtil.getQueryRuntimeAPI().getActivityInstances(case1);
		for (ActivityInstance theActivityInstance : theActivities) {
			if (theActivityInstance.isTask() && theActivityInstance.getActivityName().equals("onTrack")) {
				AccessorUtil.getRuntimeAPI().executeTask(theActivityInstance.getUUID(), false);
			}
		}

		theData = thePerfsDataSource.readUserStepsRepartition();
		assertNotNull(theData);
		assertTrue(theData.isEmpty());
		// assertEquals(3, theData.size());
		// theRow = ((Object[]) theData.get(0));
		// assertNotNull(theRow);
		// assertEquals(2, theRow.length);
		// assertEquals("atRisk", theRow[0]);
		// assertEquals(0, theRow[1]); // Steps open atRisk.
		// //
		// theRow = ((Object[]) theData.get(1));
		// assertNotNull(theRow);
		// assertEquals(2, theRow.length);
		// assertEquals("onTrack", theRow[0]);
		// assertEquals(1, theRow[1]); // Steps open onTrack.
		// //
		// theRow = ((Object[]) theData.get(2));
		// assertNotNull(theRow);
		// assertEquals(2, theRow.length);
		// assertEquals("overDue", theRow[0]);
		// assertEquals(0, theRow[1]); // Steps open overDue.
		Utils.logout();
	}

}
