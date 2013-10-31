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

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.server.BonitaTestCase;
import org.bonitasoft.console.server.Utils;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseDataStoreTest  extends BonitaTestCase {

	private ProcessDefinition myBonitaProcess;

	private CaseDataStore myCaseDataStore = CaseDataStore.getInstance();

	private CaseFilter adminUserCaseFilter;
	private CaseFilter testUserCaseFilter;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
				
		super.setUp();
		
		Utils.login(adminUsername, "bpm");
		
		Collection<LabelUUID> theAdminLabels = new ArrayList<LabelUUID>();
		theAdminLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
		Collection<LabelUUID> theJohnLabels = new ArrayList<LabelUUID>();
		theJohnLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(testUsername)));
		adminUserCaseFilter = new CaseFilter(theAdminLabels, 0, 20);
		testUserCaseFilter = new CaseFilter(theJohnLabels, 0, 20);

		URL testBarURL = getClass().getResource("/approvalWorkflow.bar");
		if (testBarURL == null) {
			throw new RuntimeException("File approvalWorkflow.bar was not found.");
		}
		
		BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(new File(testBarURL.toURI()));
		myBonitaProcess = AccessorUtil.getManagementAPI().deploy(businessArchive);
		AccessorUtil.getRuntimeAPI().instantiateProcess(myBonitaProcess.getUUID());
		Utils.logout();
	}



	/**
	 * Test method for {@link org.bonitasoft.console.server.cases.CaseDataStore#getAllCases(org.bonitasoft.console.client.users.UserProfile)}.
	 */
	@Test
	public void testGetAllCases() throws Exception {

		Utils.login(adminUsername, "bpm"); 

		Collection<CaseItem> theAdminCaseList = myCaseDataStore.getAllCases(adminUserCaseFilter, adminUserProfile, myRequest).getCases();
		assertTrue("1:Admin's Inbox should contain exactly 1 case but contains " + theAdminCaseList.size() + " cases!", theAdminCaseList.size() == 1);

		// Switch to john.
		Utils.logout();
		Utils.login(testUsername, "bpm");

		// The user currently logged in is john.
		Collection<CaseItem> theJohnCaseList = myCaseDataStore.getAllCases(testUserCaseFilter, testUserProfile, myRequest).getCases();
		assertTrue("1:John's Inbox should contain exactly 0 case but contains " + theJohnCaseList.size() + " cases!", theJohnCaseList.size() == 0);
		
		// Start a case for the user John.
		AccessorUtil.getRuntimeAPI().instantiateProcess(myBonitaProcess.getUUID());
		
		theJohnCaseList = myCaseDataStore.getAllCases(testUserCaseFilter, testUserProfile, myRequest).getCases();
		assertTrue("1:John's Inbox should contain exactly 1 case but contains " + theJohnCaseList.size() + " cases!", theJohnCaseList.size() == 1);
		
		Utils.logout();
	}

	@Test
	public void testGetMyCases() throws Exception {
		Utils.login(adminUsername, "bpm");
		// Build the filter to list the cases started by the user.
		Collection<LabelUUID> theLabels = new ArrayList<LabelUUID>();
		theLabels.add(new LabelUUID(LabelModel.MY_CASES_LABEL.getUUID().getValue(), new UserUUID(adminUserProfile.getUsername())));
		adminUserCaseFilter.setLabels(theLabels);

		Collection<CaseItem> theAdminCaseList = myCaseDataStore.getAllCases(adminUserCaseFilter, adminUserProfile, myRequest).getCases();
		assertTrue("1:The admin's 'My Cases' should contain exactly 1 case but contains " + theAdminCaseList.size() + " cases!", theAdminCaseList.size() == 1);

		// Swtch to john.
		Utils.logout();
		Utils.login(testUsername, "bpm");

		// The user currently logged in is john.
		// Build the filter to list the cases started by the user.
		theLabels = new ArrayList<LabelUUID>();
		theLabels.add(new LabelUUID(LabelModel.MY_CASES_LABEL.getUUID().getValue(), new UserUUID(testUserProfile.getUsername())));
		testUserCaseFilter.setLabels(theLabels);
		Collection<CaseItem> theJohnCaseList = myCaseDataStore.getAllCases(testUserCaseFilter, testUserProfile, myRequest).getCases();
		assertTrue("1:The John's 'My Cases' should contain exactly 0 case but contains " + theJohnCaseList.size() + " cases!", theJohnCaseList.size() == 0);

		// Start a case for the user John.
		AccessorUtil.getRuntimeAPI().instantiateProcess(myBonitaProcess.getUUID());
		theJohnCaseList = myCaseDataStore.getAllCases(testUserCaseFilter, testUserProfile, myRequest).getCases();
		assertTrue("1:The john's 'My cases' should contain exactly 1 case but contains " + theJohnCaseList.size() + " cases!", theJohnCaseList.size() == 1);

		AccessorUtil.getRuntimeAPI().instantiateProcess(myBonitaProcess.getUUID());
		theJohnCaseList = myCaseDataStore.getAllCases(testUserCaseFilter, testUserProfile, myRequest).getCases();
		assertTrue("1:The john's 'My cases' should contain exactly 2 cases but contains " + theJohnCaseList.size() + " cases!", theJohnCaseList.size() == 2);

		Utils.logout();
	}



	@Test
	public void testGetDummyLabelCases() throws Exception {

		Utils.login(adminUsername, "bpm");
		
		// Build the filter to list the cases started by the user.
		Collection<LabelUUID> theLabels = new ArrayList<LabelUUID>();
		theLabels.add(new LabelUUID("My Dummy Label", new UserUUID(adminUserProfile.getUsername())));
		adminUserCaseFilter.setLabels(theLabels);

		Collection<CaseItem> theAdminCaseList = myCaseDataStore.getAllCases(adminUserCaseFilter, adminUserProfile, myRequest).getCases();
		assertTrue("1:The admin's 'My Dummy Label' should contain exactly 0 case but contains " + theAdminCaseList.size() + " cases!", theAdminCaseList.size() == 0);

		// Swtch to john.
		Utils.logout();
		Utils.login(testUsername, "bpm");

		// The user currently logged in is john.
		// Build the filter to list the cases started by the user.
		theLabels = new ArrayList<LabelUUID>();
		theLabels.add(new LabelUUID("My Dummy Label", new UserUUID(testUserProfile.getUsername())));
		testUserCaseFilter.setLabels(theLabels);
		Collection<CaseItem> theJohnCaseList = myCaseDataStore.getAllCases(testUserCaseFilter, testUserProfile, myRequest).getCases();
		assertTrue("1:The john's 'My Dummy Label' should contain exactly 0 cases but contains " + theJohnCaseList.size() + " cases!", theJohnCaseList.size() == 0);

		// Start a case for the user John.
		AccessorUtil.getRuntimeAPI().instantiateProcess(myBonitaProcess.getUUID());
		theJohnCaseList = myCaseDataStore.getAllCases(testUserCaseFilter, testUserProfile, myRequest).getCases();
		assertTrue("1:The john's 'My Dummy Label' should contain exactly 0 cases but contains " + theJohnCaseList.size() + " cases!", theJohnCaseList.size() == 0);
		// Start another case for the user John.
		AccessorUtil.getRuntimeAPI().instantiateProcess(myBonitaProcess.getUUID());
		theJohnCaseList = myCaseDataStore.getAllCases(testUserCaseFilter, testUserProfile, myRequest).getCases();
		assertTrue("1:The john's 'My Dummy Label' should contain exactly 0 cases but contains " + theJohnCaseList.size() + " cases!", theJohnCaseList.size() == 0);

		Utils.logout();
	}

}
