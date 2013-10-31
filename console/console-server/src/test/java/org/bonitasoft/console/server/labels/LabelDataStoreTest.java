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
package org.bonitasoft.console.server.labels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.bonitasoft.console.server.BonitaTestCase;
import org.bonitasoft.console.server.Utils;
import org.bonitasoft.console.server.users.UserDataStore;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelDataStoreTest extends BonitaTestCase {

	private static final String A_NEW_NAME_FOR_MY_LABEL = "a new name for my label";
	private static final String NEW_LABEL_DISPLAY_NAME = "My new label.";
	private LabelDataStore myLabelDataStore = LabelDataStore.getInstance();

	private LabelModel myInboxLabel = new LabelModel(LabelModel.INBOX_LABEL, new UserUUID(testUsername));
	private LabelModel myMyCaseLabel = new LabelModel(LabelModel.MY_CASES_LABEL, new UserUUID(testUsername));
	private LabelModel myStarLabel = new LabelModel(LabelModel.STAR_LABEL, new UserUUID(testUsername));
	private LabelModel myAllLabel = new LabelModel(LabelModel.ALL_LABEL, new UserUUID(testUsername));

	private ProcessDefinition myBonitaProcess;
	private ProcessInstanceUUID myProcessInstanceUUID;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		Collection<LabelModel> theUserLabels = new ArrayList<LabelModel>();
		theUserLabels.add(myInboxLabel);
		theUserLabels.add(myMyCaseLabel);
		theUserLabels.add(myStarLabel);
	}

	@Test
	public void testCreateDefaultLabelsForUser() throws Exception {
		Utils.login(testUsername, "bpm");

		// Check that the list of labels can be retrieved from DB.
		List<LabelModel> theLabels = myLabelDataStore.getAllLabels(testUsername);
		assertTrue("'Inbox' label is missing.", theLabels.contains(myInboxLabel));
		assertTrue("'My Case' label is missing.", theLabels.contains(myMyCaseLabel));
		assertTrue("'Star' label is missing.", theLabels.contains(myStarLabel));
		assertTrue(myAllLabel.getUUID().getValue() + " label is missing.", theLabels.contains(myAllLabel));
		
		assertTrue(theLabels.contains(LabelModel.ATRISK_LABEL));
		assertTrue(theLabels.contains(LabelModel.OVERDUE_LABEL));

		assertEquals("Wrong number of default labels.", 6, theLabels.size());

		// GetAllLabels do not order the labels.
		Collections.sort(theLabels);

		// Check that the System labels for the user are ordered and equals to the labels.
		assertEquals(Arrays.asList(LabelModel.INBOX_LABEL, LabelModel.STAR_LABEL, LabelModel.MY_CASES_LABEL,LabelModel.ALL_LABEL, LabelModel.ATRISK_LABEL, LabelModel.OVERDUE_LABEL), theLabels);

		Utils.logout();
	}

	@Test
	public void testNewLabelCreation() throws Exception {
		Utils.login(testUsername, "bpm");

		LabelModel theLabel = myLabelDataStore.getLabel(new UserUUID(testUsername), NEW_LABEL_DISPLAY_NAME);
		assertNull("The label '" + NEW_LABEL_DISPLAY_NAME + "' should not exist at this step. But seems to be in the DB already.", theLabel);
		myLabelDataStore.createNewLabel(testUserProfile, NEW_LABEL_DISPLAY_NAME);
		theLabel = myLabelDataStore.getLabel(new UserUUID(testUsername), NEW_LABEL_DISPLAY_NAME);
		assertNotNull("The label '" + NEW_LABEL_DISPLAY_NAME + "' must exist at this step. But seems NOT to be in the DB.", theLabel);
		System.out.println("'" + theLabel.getUUID().getValue() + "' was just created with following attributes:");
		System.out.println("\tEditable CSS class name: " + theLabel.getEditableCSSStyleName());
		System.out.println("\tRead-only CSS class name: " + theLabel.getReadonlyCSSStyleName());
		System.out.println("\tPreview CSS class name: " + theLabel.getPreviewCSSStyleName());

		try {
			myLabelDataStore.createNewLabel(testUserProfile, NEW_LABEL_DISPLAY_NAME);
			fail("Two labels with the same name were created, which is not supposed to be possible.");
		} catch (ConsoleException e) {
			System.out.println("Attempt to create two labels with the same name was detected and rejected. OK");
		}
		Utils.logout();
	}

	/**
	 * Test method for {@link org.bonitasoft.console.server.labels.LabelDataStore#renameLabel(org.bonitasoft.console.client.labels.LabelUUID, String aNewName)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRenameLabel() throws Exception {

		Utils.login(testUsername, "bpm");

		LabelModel theLabel;
		myLabelDataStore.createNewLabel(testUserProfile, NEW_LABEL_DISPLAY_NAME);
		theLabel = myLabelDataStore.getLabel(new UserUUID(testUsername), NEW_LABEL_DISPLAY_NAME);

		myLabelDataStore.renameLabel(testUserProfile, theLabel.getUUID(), A_NEW_NAME_FOR_MY_LABEL);
		// Check that the old name cannot be found anymore
		theLabel = myLabelDataStore.getLabel(new UserUUID(testUsername), NEW_LABEL_DISPLAY_NAME);
		assertNull("The label '" + NEW_LABEL_DISPLAY_NAME + "' should not exist anymore. But seems to still be in the DB.", theLabel);
		// Check that the new name is there.
		theLabel = myLabelDataStore.getLabel(new UserUUID(testUsername), A_NEW_NAME_FOR_MY_LABEL);
		assertNotNull("The label '" + A_NEW_NAME_FOR_MY_LABEL + "' should exist. But seems NOT to be in the DB.", theLabel);
		System.out.println("'" + theLabel.getUUID().getValue() + "' was just updated with following attributes:");
		System.out.println("\tEditable CSS class name: " + theLabel.getEditableCSSStyleName());
		System.out.println("\tRead-only CSS class name: " + theLabel.getReadonlyCSSStyleName());
		System.out.println("\tPreview CSS class name: " + theLabel.getPreviewCSSStyleName());

		Utils.logout();
	}

	@Test
	public void testUpdateLabels() throws Exception {
		Utils.login(testUsername, "bpm");

		final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
		final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
		final QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
		
		// Simulate the Console login which build the user profile and send it back to the client side.
		testUserProfile = UserDataStore.getUserProfile(testUsername, false, "en", SecurityAPIFactory.getPrivilegesAPI().getUserRights(testUsername), false);
		UserDataStore.loadUserPreferences(testUserProfile);
		// From Now the user is logged in from the UE point of view.

		// Get the bar.
		URL testBarURL = getClass().getResource("/approvalWorkflow.bar");
		if (testBarURL == null) {
			throw new RuntimeException("File approvalWorkflow.bar was not found.");
		}

		// Instantiate a case.
		AccessorUtil.getManagementAPI().deleteAllProcesses();
		BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(new File(testBarURL.toURI()));
		myBonitaProcess = theManagementAPI.deploy(businessArchive);
		myProcessInstanceUUID = theRuntimeAPI.instantiateProcess(myBonitaProcess.getUUID());
		final LightProcessInstance theProcessInstance = theQueryRuntimeAPI.getLightProcessInstance(myProcessInstanceUUID);

		// Associate labels to case.
		CaseUUID theCaseUUID = new CaseUUID(myProcessInstanceUUID.getValue());
		List<LabelModel> theDefaultLabelsOfMyCase = myLabelDataStore.getLabelsOfCase(testUserProfile, theProcessInstance);
//		assertTrue("'Inbox' label is missing.", theDefaultLabelsOfMyCase.contains(myInboxLabel));
		assertTrue("'My Case' label is missing.", theDefaultLabelsOfMyCase.contains(myMyCaseLabel));
		assertEquals("Wrong number of default labels.", 1, theDefaultLabelsOfMyCase.size());

		TreeSet<LabelUUID> theSetOfLabelsToAdd = new TreeSet<LabelUUID>();
		TreeSet<LabelUUID> theSetOfLabelsToRemove = new TreeSet<LabelUUID>();
		TreeSet<CaseUUID> theSetOfCasesToUpdate = new TreeSet<CaseUUID>();
		theSetOfCasesToUpdate.add(theCaseUUID);
	
		// Create a new label.
		LabelModel theCustomLabel = myLabelDataStore.getLabel(new UserUUID(testUsername), NEW_LABEL_DISPLAY_NAME);
		assertNull("The label '" + NEW_LABEL_DISPLAY_NAME + "' should not exist at this step. But seems to be in the DB already.", theCustomLabel);

		myLabelDataStore.createNewLabel(testUserProfile, NEW_LABEL_DISPLAY_NAME);
		theCustomLabel = myLabelDataStore.getLabel(new UserUUID(testUsername), NEW_LABEL_DISPLAY_NAME);
		assertNotNull("The label '" + NEW_LABEL_DISPLAY_NAME + "' must exist at this step. But seems NOT to be in the DB.", theCustomLabel);

		theSetOfLabelsToAdd.add(theCustomLabel.getUUID());
		myLabelDataStore.updateLabels(testUserProfile, theSetOfLabelsToAdd, theSetOfLabelsToRemove, theSetOfCasesToUpdate);
		theDefaultLabelsOfMyCase = myLabelDataStore.getLabelsOfCase(testUserProfile, theProcessInstance);
//		assertTrue("'Inbox' label is missing.", theDefaultLabelsOfMyCase.contains(myInboxLabel));
		assertTrue("'My Case' label is missing.", theDefaultLabelsOfMyCase.contains(myMyCaseLabel));
		assertTrue("'a new name for my label' label is missing.", theDefaultLabelsOfMyCase.contains(theCustomLabel));
		assertEquals("Wrong number of default labels.", 2, theDefaultLabelsOfMyCase.size());
		// -- Clean state
		theSetOfLabelsToAdd.clear();
		theSetOfLabelsToRemove.clear();

		// Add star label
		theSetOfLabelsToAdd.add(myStarLabel.getUUID());
		myLabelDataStore.updateLabels(testUserProfile, theSetOfLabelsToAdd, theSetOfLabelsToRemove, theSetOfCasesToUpdate);
		theDefaultLabelsOfMyCase = myLabelDataStore.getLabelsOfCase(testUserProfile, theProcessInstance);
//		assertTrue("'Inbox' label is missing.", theDefaultLabelsOfMyCase.contains(myInboxLabel));
		assertTrue("'My Case' label is missing.", theDefaultLabelsOfMyCase.contains(myMyCaseLabel));
		assertTrue("'a new name for my label' label is missing.", theDefaultLabelsOfMyCase.contains(theCustomLabel));
		assertTrue("'Star' label is missing.", theDefaultLabelsOfMyCase.contains(myStarLabel));
		assertEquals("Wrong number of default labels.", 3, theDefaultLabelsOfMyCase.size());

		// -- Clean state
		theSetOfLabelsToAdd.clear();
		theSetOfLabelsToRemove.clear();

		// Remove custom label
		theSetOfLabelsToRemove.add(theCustomLabel.getUUID());
		myLabelDataStore.updateLabels(testUserProfile, theSetOfLabelsToAdd, theSetOfLabelsToRemove, theSetOfCasesToUpdate);
		theDefaultLabelsOfMyCase = myLabelDataStore.getLabelsOfCase(testUserProfile, theProcessInstance);
//		assertTrue("'Inbox' label is missing.", theDefaultLabelsOfMyCase.contains(myInboxLabel));
		assertTrue("'My Case' label is missing.", theDefaultLabelsOfMyCase.contains(myMyCaseLabel));
		assertTrue("'Star' label is missing.", theDefaultLabelsOfMyCase.contains(myStarLabel));
		assertEquals("Wrong number of default labels.", 2, theDefaultLabelsOfMyCase.size());

		// -- Clean state
		theSetOfLabelsToAdd.clear();
		theSetOfLabelsToRemove.clear();

		// remove star, add custom
		theSetOfLabelsToRemove.add(myStarLabel.getUUID());
		theSetOfLabelsToAdd.add(theCustomLabel.getUUID());
		myLabelDataStore.updateLabels(testUserProfile, theSetOfLabelsToAdd, theSetOfLabelsToRemove, theSetOfCasesToUpdate);
		theDefaultLabelsOfMyCase = myLabelDataStore.getLabelsOfCase(testUserProfile, theProcessInstance);
//		assertTrue("'Inbox' label is missing.", theDefaultLabelsOfMyCase.contains(myInboxLabel));
		assertTrue("'My Case' label is missing.", theDefaultLabelsOfMyCase.contains(myMyCaseLabel));
		assertTrue("'a new name for my label' label is missing.", theDefaultLabelsOfMyCase.contains(theCustomLabel));
		assertEquals("Wrong number of default labels.", 2, theDefaultLabelsOfMyCase.size());
		
		// -- Clean state
		theSetOfLabelsToAdd.clear();
		theSetOfLabelsToRemove.clear();

		Utils.logout();
	}

}
