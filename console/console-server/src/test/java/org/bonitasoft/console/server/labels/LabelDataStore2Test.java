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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.bonitasoft.console.server.BonitaTestCase;
import org.bonitasoft.console.server.Utils;
import org.bonitasoft.console.server.cases.CaseDataStore;
import org.bonitasoft.console.server.users.UserDataStore;
import org.junit.Test;
import org.ow2.bonita.facade.WebAPI;
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
public class LabelDataStore2Test extends BonitaTestCase {

	@Test
	public void testLabelCasesAreTopParent() throws Exception {
		Utils.login(testUsername, "bpm");

		ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("start").addSubProcess("aSubProcess", "aSubProcess")
				.addHumanTask("t", adminUsername).addTransition("start", "aSubProcess").addTransition("start", "t").done();

		ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.1").addHuman(testUsername).addSystemTask("start").addSubProcess("zSubSubProcess", "zSubSubProcess")
				.addHumanTask("o", testUsername).addTransition("start", "zSubSubProcess").addTransition("start", "o").done();

		ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "rc1").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

		final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

		// Start a case.
		ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
		assertEquals(3, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

		// List inbox.
		ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
		thefilterLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(testUsername)));
		CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
		Thread.sleep(1);
		Collection<CaseItem> theInboxCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, testUserProfile, myRequest).getCases();

		assertEquals(1, theInboxCases.size());
		assertEquals(theProcessInstanceUUID.getValue(), theInboxCases.iterator().next().getUUID().getValue());

		Utils.logout();
	}

	private Set<LabelUUID> getLabelUUIDSet(LabelUUID... labelUUID) {
		Set<LabelUUID> result = new HashSet<LabelUUID>();
		if (labelUUID != null) {
			for (LabelUUID uuid : labelUUID) {
				result.add(uuid);
			}
		}
		return result;
	}

	@Test
	public void testAddLabelToCases() throws Exception {
		Utils.login(testUsername, "bpm");

		ProcessDefinition process1 = ProcessBuilder.createProcess("p1", "rc1").addHuman(testUsername).addHumanTask("t", testUsername).done();
		final ProcessDefinitionUUID processUUID = process1.getUUID();

		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(process1));

		// Start a case.
		ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		AccessorUtil.getWebAPI().addLabel("myLabel", testUsername, "editableCss", "readOnlyCss", "previewCss", true, true, null, null, 1, true);
		
		// Add case to "myLabel".
		LabelUUID theMyLabelUUID = new LabelUUID("myLabel", new UserUUID(testUsername));
		Set<CaseUUID> cases = new HashSet<CaseUUID>();
		CaseUUID theCaseUUID = new CaseUUID(theProcessInstanceUUID.getValue());
		cases.add(theCaseUUID);
		LabelDataStore.getInstance().updateLabels(testUserProfile, getLabelUUIDSet(theMyLabelUUID), null, cases);
		// List inbox.
		ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
		thefilterLabels.add(theMyLabelUUID);
		CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
		Thread.sleep(1);
		Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, testUserProfile, myRequest).getCases();

		assertNotNull(theCases);
		assertEquals(1, theCases.size());
		assertEquals(theProcessInstanceUUID.getValue(), theCases.iterator().next().getUUID().getValue());

		Utils.logout();
	}

	@Test
	public void testRemoveLabelFromCases() throws Exception {
		Utils.login(testUsername, "bpm");

		ProcessDefinition process1 = ProcessBuilder.createProcess("p1", "rc1").addHuman(testUsername).addHumanTask("t", testUsername).done();
		final ProcessDefinitionUUID processUUID = process1.getUUID();

		AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(process1));

		// Start a case.
		ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

		AccessorUtil.getWebAPI().addLabel("myLabel", testUsername, "editableCss", "readOnlyCss", "previewCss", true, true, null, null, 1, true);
		
		// Add case to "myLabel".
		LabelUUID theMyLabelUUID = new LabelUUID("myLabel", new UserUUID(testUsername));
		Set<CaseUUID> cases = new HashSet<CaseUUID>();
		CaseUUID theCaseUUID = new CaseUUID(theProcessInstanceUUID.getValue());
		cases.add(theCaseUUID);
		LabelDataStore.getInstance().updateLabels(testUserProfile, getLabelUUIDSet(theMyLabelUUID), null, cases);
		// List "myLabel".
		ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
		thefilterLabels.add(theMyLabelUUID);
		CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
		Thread.sleep(1);
		Collection<CaseItem> theInboxCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, testUserProfile, myRequest).getCases();

		assertNotNull(theInboxCases);
		assertEquals(1, theInboxCases.size());
		// Remove the case from "myLabel".
		LabelDataStore.getInstance().updateLabels(testUserProfile, null, getLabelUUIDSet(theMyLabelUUID), cases);
		// List "myLabel".
		Thread.sleep(1);
		theInboxCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, testUserProfile, myRequest).getCases();

		assertNotNull(theInboxCases);
		assertEquals(0, theInboxCases.size());
		Utils.logout();
	}



	@Test(expected = IllegalArgumentException.class)
	public void testGetLabelsUpdatesWithNull() throws Exception {
		Utils.login(testUsername, "bpm");
		LabelDataStore.getInstance().getLabelUpdates(testUserProfile, null, false);
		Utils.logout();
	}
	
	@Test
  public void testGetLabelConfiguration() throws Exception {
    Utils.login(testUsername, "bpm");
    LabelsConfiguration theConfiguration = LabelDataStore.getInstance().getConfiguration();
    assertTrue(theConfiguration.isCustomLabelsEnabled());
    assertTrue(theConfiguration.isStarEnabled());
    Utils.logout();
  }
	
	@Test
  public void testUpdateLabelConfigurationDisableCustomLabels() throws Exception {
    Utils.login(testUsername, "bpm");
    
    final LabelDataStore theLabelDataStore = LabelDataStore.getInstance();
    final WebAPI theWebAPI = AccessorUtil.getWebAPI();

    List<LabelModel> theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length, theLabels.size());
    
    //Start a case.
    ProcessDefinition process1 = ProcessBuilder.createProcess("p1", "rc1").addHuman(testUsername).addHumanTask("t", testUsername).done();
    final ProcessDefinitionUUID processUUID = process1.getUUID();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(process1));

    // Start a case.
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

    // Create a custom label & bind it to the case.
    final String theCustomLabelName = "myLabel"; 
    theWebAPI.addLabel(theCustomLabelName, testUsername, "editableCss", "readOnlyCss", "previewCss", true, true, null, new HashSet<ProcessInstanceUUID>(Arrays.asList(theProcessInstanceUUID)), 1, false);

    theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length + 1, theLabels.size());
    
    Set<ProcessInstanceUUID> theCases = theWebAPI.getCases(testUsername, new HashSet<String>(Arrays.asList(theCustomLabelName)));
    assertEquals(1, theCases.size());
    
    LabelsConfiguration theConfiguration = new LabelsConfiguration();
    theConfiguration.setCustomLabelsEnabled(false);
    theConfiguration.setStarEnabled(true);
    theLabelDataStore.updateConfiguration(adminUserProfile, theConfiguration);
    
    //Check configuration update
    LabelsConfiguration theNewConfiguration = theLabelDataStore.getConfiguration();
    assertEquals(theConfiguration.isCustomLabelsEnabled(), theNewConfiguration.isCustomLabelsEnabled());
    assertEquals(theConfiguration.isStarEnabled(), theNewConfiguration.isStarEnabled());
    
    // Check the side effects of the configuration update
    // binding between custom labels and cases has been removed
    theCases = theWebAPI.getCases(testUsername, new HashSet<String>(Arrays.asList(theCustomLabelName)));
    assertEquals(0, theCases.size());
    // Custom labels have been deleted.
    theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length, theLabels.size());
    
    //cleanup
    //TODO
    Utils.logout();
  }
	
	@Test
  public void testUpdateLabelConfigurationDisableStarLabel() throws Exception {
    Utils.login(testUsername, "bpm");
    
    final LabelDataStore theLabelDataStore = LabelDataStore.getInstance();
    final WebAPI theWebAPI = AccessorUtil.getWebAPI();

    List<LabelModel> theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length, theLabels.size());
    
    //Start a case.
    ProcessDefinition process1 = ProcessBuilder.createProcess("p1", "rc1").addHuman(testUsername).addHumanTask("t", testUsername).done();
    final ProcessDefinitionUUID processUUID = process1.getUUID();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(process1));

    // Start a case.
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

    // Create a custom label & bind it to the case.
    final String theCustomLabelName = "myLabel"; 
    final Set<ProcessInstanceUUID> theCaseList = new HashSet<ProcessInstanceUUID>(Arrays.asList(theProcessInstanceUUID));
    theWebAPI.addLabel(theCustomLabelName, testUsername, "editableCss", "readOnlyCss", "previewCss", true, true, null, theCaseList, 1, false);

    // Bind the case to the Star label.
    theWebAPI.addCasesToLabel(testUsername, LabelModel.STAR_LABEL.getUUID().getValue(), theCaseList);
    
    theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length + 1, theLabels.size());
    
    Set<ProcessInstanceUUID> theCases = theWebAPI.getCases(testUsername, new HashSet<String>(Arrays.asList(LabelModel.STAR_LABEL.getUUID().getValue())));
    assertEquals(1, theCases.size());
    
    LabelsConfiguration theConfiguration = new LabelsConfiguration();
    theConfiguration.setCustomLabelsEnabled(true);
    theConfiguration.setStarEnabled(false);
    theLabelDataStore.updateConfiguration(adminUserProfile, theConfiguration);
    
    //Check configuration update
    LabelsConfiguration theNewConfiguration = theLabelDataStore.getConfiguration();
    assertEquals(theConfiguration.isCustomLabelsEnabled(), theNewConfiguration.isCustomLabelsEnabled());
    assertEquals(theConfiguration.isStarEnabled(), theNewConfiguration.isStarEnabled());
    
    // Check the side effects of the configuration update
    // binding between custom labels and cases has NOT been removed
    theCases = theWebAPI.getCases(testUsername, new HashSet<String>(Arrays.asList(theCustomLabelName)));
    assertEquals(1, theCases.size());
    // binding between star label and cases has been removed
    theCases = theWebAPI.getCases(testUsername, new HashSet<String>(Arrays.asList(LabelModel.STAR_LABEL.getUUID().getValue())));
    assertEquals(0, theCases.size());
    // Custom labels have been deleted.
    theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length, theLabels.size());
    
    //cleanup
    //TODO
    Utils.logout();
  }
	
	@Test
  public void testUpdateLabelConfigurationReactivateStarLabel() throws Exception {
    Utils.login(testUsername, "bpm");
    
    final LabelDataStore theLabelDataStore = LabelDataStore.getInstance();

    List<LabelModel> theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length, theLabels.size());
    
    // De-activate star label usage.
    LabelsConfiguration theConfiguration = new LabelsConfiguration();
    theConfiguration.setCustomLabelsEnabled(true);
    theConfiguration.setStarEnabled(false);
    theLabelDataStore.updateConfiguration(adminUserProfile, theConfiguration);
    
    theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length - 1, theLabels.size());
    
    theConfiguration = new LabelsConfiguration();
    theConfiguration.setCustomLabelsEnabled(true);
    theConfiguration.setStarEnabled(true);
    theLabelDataStore.updateConfiguration(adminUserProfile, theConfiguration);

    theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length, theLabels.size());
    
    //cleanup
    //TODO
    Utils.logout();
  }
	
	@Test
  public void testCreateDefaultLabelsWhenStarLabelDisabled() throws Exception {
    Utils.login(testUsername, "bpm");
    
    final LabelDataStore theLabelDataStore = LabelDataStore.getInstance();

    List<LabelModel> theLabels = theLabelDataStore.getAllLabels(testUsername);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length, theLabels.size());
    
    // De-activate star label usage.
    LabelsConfiguration theConfiguration = new LabelsConfiguration();
    theConfiguration.setCustomLabelsEnabled(true);
    theConfiguration.setStarEnabled(false);
    theLabelDataStore.updateConfiguration(adminUserProfile, theConfiguration);
    
    // Switch to another user.
    Utils.logout();
    
    final String jack = "jack"; 
    
    Utils.login(jack, "bpm");
    UserDataStore.getUserProfile(jack, false, "en", SecurityAPIFactory.getPrivilegesAPI().getUserRights(jack), false);
    theLabels = theLabelDataStore.getAllLabels(jack);
    assertEquals(theLabelDataStore.DEFAULT_LABELS.length - 1, theLabels.size());
    
    //cleanup
    //TODO
    Utils.logout();
  }

}
