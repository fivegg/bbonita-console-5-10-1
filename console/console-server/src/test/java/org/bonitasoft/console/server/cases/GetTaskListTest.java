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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.bonitasoft.console.server.BonitaTestCase;
import org.bonitasoft.console.server.Utils;
import org.junit.Test;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.BusinessArchiveFactory;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class GetTaskListTest extends BonitaTestCase {

	private static final Collection<ActivityState> desiredStepStates = new ArrayList<ActivityState>();

	static {
		// Initialize the Jaas login configuration with a default value
		final String defaultLoginFile = "src/test/resources/jaas-standard.cfg";
		final String loginFile = System.getProperty(BonitaConstants.JAAS_PROPERTY, defaultLoginFile);
		if (loginFile.equals(defaultLoginFile)) {
			System.setProperty(BonitaConstants.JAAS_PROPERTY, defaultLoginFile);
		}
		desiredStepStates.add(ActivityState.READY);
		desiredStepStates.add(ActivityState.EXECUTING);
		desiredStepStates.add(ActivityState.SUSPENDED);
		desiredStepStates.add(ActivityState.FINISHED);

	}

	/**
	 * Test method for .
	 */
	@Test
	public void getTaskList() throws Exception {
		Utils.login(adminUsername, "bpm");

		ProcessDefinition theBonitaProcess;
		ProcessInstanceUUID theAdminProcessInstanceUUID;
		ProcessInstanceUUID theTestUserProcessInstanceUUID;

		URL testBarURL = getClass().getResource("/approvalWorkflow.bar");
		if (testBarURL == null) {
			throw new RuntimeException("File approvalWorkflow.bar was not found.");
		}

		BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(new File(testBarURL.toURI()));
		theBonitaProcess = AccessorUtil.getManagementAPI().deploy(businessArchive);
		theAdminProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(theBonitaProcess.getUUID());

		// The user currently logged in is admin.
		Collection<TaskInstance> theAdminTaskList = AccessorUtil.getQueryRuntimeAPI().getTaskList(theAdminProcessInstanceUUID, desiredStepStates);
		assertTrue("1:Admin's tasklist should contain exactly 1 task but contains " + theAdminTaskList.size() + " tasks!", theAdminTaskList.size() == 1);

		// Swtch to john.
		Utils.logout();
		Utils.login(testUsername, "bpm");

		// The user currently logged in is john.
		Collection<TaskInstance> theJohnTaskList = AccessorUtil.getQueryRuntimeAPI().getTaskList(theAdminProcessInstanceUUID, desiredStepStates);
		assertTrue("1:" + testUsername + "'s tasklist should contain exactly 1 task but contains " + theJohnTaskList.size() + " tasks!", theJohnTaskList.size() == 0);

		// Start a case for the user John.
		theTestUserProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(theBonitaProcess.getUUID());
		theJohnTaskList = AccessorUtil.getQueryRuntimeAPI().getTaskList(theTestUserProcessInstanceUUID, desiredStepStates);
		assertTrue("1:" + testUsername + "'s tasklist should contain exactly 1 task but contains " + theJohnTaskList.size() + " tasks!", theJohnTaskList.size() == 1);

		theJohnTaskList = AccessorUtil.getQueryRuntimeAPI().getTaskList(theAdminProcessInstanceUUID, desiredStepStates);
		assertTrue("1:" + testUsername + "'s tasklist should contain exactly 0 task but contains " + theJohnTaskList.size() + " tasks!", theJohnTaskList.size() == 0);
		Utils.logout();
	}

}
