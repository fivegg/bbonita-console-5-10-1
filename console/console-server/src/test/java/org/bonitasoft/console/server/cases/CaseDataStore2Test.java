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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.cases.CasesConfiguration;
import org.bonitasoft.console.client.cases.CasesConfiguration.Columns;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.server.BonitaTestCase;
import org.bonitasoft.console.server.Utils;
import org.bonitasoft.console.server.categories.CategoryDataStore;
import org.bonitasoft.console.server.labels.LabelDataStore;
import org.bonitasoft.console.server.persistence.PreferencesDataStore;
import org.junit.Test;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.privilege.PrivilegePolicy;
import org.ow2.bonita.facade.privilege.Rule.RuleType;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.light.LightTaskInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.ProcessBuilder;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseDataStore2Test extends BonitaTestCase {

  private static final String CASE_LIST_LAYOUT_KEY = "userXP.caseList.layout";

  @Test
  public void testBuildStepsWhithSubCases() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.0").addHuman(adminUsername).addSubProcess("zSubSubProcess", "zSubSubProcess").done();

    ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

    // Start a case.
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    assertEquals(3, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    // Build Steps
    ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
    thefilterLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
    CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
    LightProcessInstance theProcessInstance = AccessorUtil.getQueryRuntimeAPI().getLightProcessInstance(new ProcessInstanceUUID(theProcessInstanceUUID));
    assertNotNull(theProcessInstance);
    List<LightActivityInstance> lightActivityInstances = AccessorUtil.getQueryRuntimeAPI().getLightActivityInstancesFromRoot(theProcessInstance.getUUID());
    Map<ActivityInstanceUUID, Integer> theCommentCont = CaseDataStore.getInstance().getActivitiesCommentCount(lightActivityInstances);
    List<StepItem> theSteps = CaseDataStore.getInstance().buildSteps(adminUserProfile, theCaseFilter, lightActivityInstances, theCommentCont, myRequest);

    assertEquals(1, theSteps.size());
    // Check the number of steps.
    assertEquals("t", theSteps.get(0).getName());
    Utils.logout();
  }

  @Test
  public void testGetInboxWhithSubCases() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.0").addHuman(adminUsername).addSubProcess("zSubSubProcess", "zSubSubProcess").done();

    ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

    // Start a case.
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    assertEquals(3, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    // List inbox.
    ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
    thefilterLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
    CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(1, theCases.size());
    // Check that the UUID is the one of the parent.
    assertEquals(theProcessInstanceUUID.getValue(), theCases.iterator().next().getUUID().getValue());
    // Check the number of steps.
    assertEquals(1, theCases.iterator().next().getSteps().size());
    Utils.logout();
  }

  @Test
  public void testGetInboxWhithSubCases2() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSubProcess("callChild", "child").addHumanTask("human1", adminUsername)
        .addTransition("callChild", "human1").done();

    ProcessDefinition childProcess = ProcessBuilder.createProcess("child", "1.0").addHuman(adminUsername).addHumanTask("Step1", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(childProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

    // Start a case.
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    assertEquals(2, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    // List inbox.
    ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
    thefilterLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
    CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(1, theCases.size());
    // Check that the UUID is the one of the parent.
    assertEquals(theProcessInstanceUUID.getValue(), theCases.iterator().next().getUUID().getValue());
    // Check the number of steps.
    List<StepItem> theSteps = theCases.iterator().next().getSteps();
    assertEquals(1, theSteps.size());
    assertEquals("Step1", theSteps.get(0).getName());
    Utils.logout();
  }

  @Test
  public void testGetMyCaseWhitSubCases() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.0").addHuman(adminUsername).addSubProcess("zSubSubProcess", "zSubSubProcess").done();

    ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

    // Start a case.
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    assertEquals(3, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    // List inbox.
    ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
    thefilterLabels.add(new LabelUUID(LabelModel.MY_CASES_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
    CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(1, theCases.size());
    // Check that the UUID is the one of the parent.
    assertEquals(theProcessInstanceUUID.getValue(), theCases.iterator().next().getUUID().getValue());
    // Check the number of steps.
    assertEquals(1, theCases.iterator().next().getSteps().size());
    Utils.logout();
  }

  @Test
  public void testGetCasesInACategoryGlobalPolicyAllow() throws Exception {

    Utils.login(adminUsername, "bpm");

    final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
    final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
    final QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

    final String categoryName = "HR";

    ProcessDefinition processWithCategory = ProcessBuilder.createProcess("processWithCategory", "1.0").addHuman(adminUsername).addHumanTask("auto", adminUsername).addCategory(categoryName).done();
    ProcessDefinition processWithoutCategory = ProcessBuilder.createProcess("processWithoutCategory", "1.0").addHuman(adminUsername).addHumanTask("auto", adminUsername).done();

    theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(processWithCategory));
    theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(processWithoutCategory));

    final ProcessDefinitionUUID processWithCategoryUUID = processWithCategory.getUUID();
    final ProcessDefinitionUUID processWithoutCategoryUUID = processWithoutCategory.getUUID();

    // Start a case.
    ProcessInstanceUUID theProcessInstanceWithCategoryUUID = theRuntimeAPI.instantiateProcess(processWithCategoryUUID);
    theRuntimeAPI.instantiateProcess(processWithoutCategoryUUID);
    assertEquals(2, theQueryRuntimeAPI.getProcessInstances().size());

    List<Category> theCategories = CategoryDataStore.getInstance().getCategoriesByName(adminUserProfile, new HashSet<String>(Arrays.asList(categoryName)));
    assertNotNull(theCategories);
    assertEquals(1, theCategories.size());
    final Category theCategory = theCategories.get(0);

    // List category
    CaseFilter theCaseFilter = new CaseFilter(null, 0, 20);
    theCaseFilter.setCategory(theCategory);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(1, theCases.size());
    // Check that the UUID is the one of the parent.
    assertEquals(theProcessInstanceWithCategoryUUID.getValue(), theCases.iterator().next().getUUID().getValue());
    // Check the number of steps.
    assertEquals(1, theCases.iterator().next().getSteps().size());
    Utils.logout();
  }

  @Test
  public void testGetCasesInACategoryGlobalPolicyDeny() throws Exception {

    Utils.login(adminUsername, "bpm");

    final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
    final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
    final QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();

    final String categoryName = "HR";

    theManagementAPI.setRuleTypePolicy(RuleType.CATEGORY_READ, PrivilegePolicy.DENY_BY_DEFAULT);

    ProcessDefinition processWithCategory = ProcessBuilder.createProcess("processWithCategory", "1.0").addHuman(adminUsername).addHumanTask("auto", adminUsername).addCategory(categoryName).done();
    ProcessDefinition processWithoutCategory = ProcessBuilder.createProcess("processWithoutCategory", "1.0").addHuman(adminUsername).addHumanTask("auto", adminUsername).done();

    theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(processWithCategory));
    theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(processWithoutCategory));

    final ProcessDefinitionUUID processWithCategoryUUID = processWithCategory.getUUID();
    final ProcessDefinitionUUID processWithoutCategoryUUID = processWithoutCategory.getUUID();

    // Start a case.
    ProcessInstanceUUID theProcessInstanceWithCategoryUUID = theRuntimeAPI.instantiateProcess(processWithCategoryUUID);
    theRuntimeAPI.instantiateProcess(processWithoutCategoryUUID);
    assertEquals(2, theQueryRuntimeAPI.getProcessInstances().size());

    List<Category> theCategories = CategoryDataStore.getInstance().getCategoriesByName(adminUserProfile, new HashSet<String>(Arrays.asList(categoryName)));
    assertNotNull(theCategories);
    assertEquals(1, theCategories.size());
    final Category theCategory = theCategories.get(0);

    // List category
    CaseFilter theCaseFilter = new CaseFilter(null, 0, 20);
    theCaseFilter.setCategory(theCategory);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(1, theCases.size());
    // Check that the UUID is the one of the parent.
    assertEquals(theProcessInstanceWithCategoryUUID.getValue(), theCases.iterator().next().getUUID().getValue());
    // Check the number of steps.
    assertEquals(1, theCases.iterator().next().getSteps().size());

    theManagementAPI.setRuleTypePolicy(RuleType.CATEGORY_READ, PrivilegePolicy.ALLOW_BY_DEFAULT);
    Utils.logout();
  }

  @Test
  public void testGetStarWhitSubCases() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.0").addHuman(adminUsername).addSubProcess("zSubSubProcess", "zSubSubProcess").done();

    ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

    // Start a case.
    AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    assertEquals(3, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    // List inbox.
    ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
    thefilterLabels.add(new LabelUUID(LabelModel.STAR_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
    CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(0, theCases.size());
    Utils.logout();
  }

  @Test
  public void testGetInboxWhitSubCasesWithMultipleTasks() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("start").addSubProcess("aSubProcess", "aSubProcess").addHumanTask("t",
        adminUsername).addTransition("start", "aSubProcess").addTransition("start", "t").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.1").addHuman(testUsername).addSystemTask("start").addSubProcess("zSubSubProcess", "zSubSubProcess").addHumanTask("o",
        testUsername).addTransition("start", "zSubSubProcess").addTransition("start", "o").done();

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
    thefilterLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
    CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(1, theCases.size());
    // Check that the UUID is the one of the parent.
    assertEquals(theProcessInstanceUUID.getValue(), theCases.iterator().next().getUUID().getValue());
    // Check the number of steps.
    assertEquals(2, theCases.iterator().next().getSteps().size());
    Utils.logout();
  }

  @Test
  public void testGetAllWithSubCases() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.0").addHuman(adminUsername).addSubProcess("zSubSubProcess", "zSubSubProcess").done();

    ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    ProcessDefinition parentProcessToArchive = ProcessBuilder.createProcess("parentProcessToArchive", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcessToArchive));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();
    final ProcessDefinitionUUID processUUIDToArchive = parentProcess.getUUID();

    // Start a case.
    AccessorUtil.getRuntimeAPI().instantiateProcess(processUUIDToArchive);
    Thread.sleep(1);
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

    assertEquals(6, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    // List inbox.
    ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
    thefilterLabels.add(new LabelUUID(LabelModel.ALL_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
    CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(2, theCases.size());
    // Check that the UUID is the one of the parent.
    assertEquals(theProcessInstanceUUID.getValue(), theCases.iterator().next().getUUID().getValue());
    // Check the number of steps.
    assertEquals(1, theCases.iterator().next().getSteps().size());

    // End.
    Utils.logout();
  }

  @Test
  public void testFillInDBWith20() throws Exception {
    fillInDB(20, 1000);
  }

  @Test
  public void testFillInDBWith100() throws Exception {
    fillInDB(100, 1000);
  }

  @Test
  public void testFillInDBWith200() throws Exception {
    fillInDB(200, 3000);
  }

  /*
   * @Test public void testFillInDBWith1000() throws Exception {
   * testFillInDB(1000, 10000); }
   */

  @Test
  public void testFilledUpWith20() throws Exception {
    filledUpWithSubProcesses(20, 1500);
    System.err.println("\n");
  }

  @Test
  public void testFilledUpWith100() throws Exception {
    filledUpWithSubProcesses(100, 1500);
    System.err.println("\n");
  }

  @Test
  public void testFilledUpWith200() throws Exception {
    filledUpWithSubProcesses(200, 2000);
    System.err.println("\n");
  }

  @Test
  public void testFilledUpWith1000() throws Exception {
    filledUpWithSubProcesses(1000, 8000);
  }

  @Test
  public void testFilledUpWith1000x5() throws Exception {
    filledUpWithSubProcesses(1000, 10000);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(10000, 1000);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(10000, 1000);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(10000, 1000);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(10000, 1000);
    System.err.println("\n");
  }

  @Test
  public void testFilledUpWith200x2() throws Exception {
    filledUpWithSubProcesses(200, 4000);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(4000, 200);
    System.err.println("\n");
  }

  @Test
  public void testFilledUpWith200WithoutSubProcesses() throws Exception {
    filledUpWithoutSubProcesses(200, 10000);
    System.err.println("\n");
  }

  @Test
  public void testFilledUpWith200x5WithoutSubProcesses() throws Exception {
    filledUpWithoutSubProcesses(200, 10000);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(10000, 200);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(10000, 200);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(10000, 200);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(10000, 200);
    System.err.println("\n");
  }

  @Test
  public void testGetAdminCaseListWhithSubCases() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.0").addHuman(adminUsername).addSubProcess("zSubSubProcess", "zSubSubProcess").done();

    ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

    // Start a case.
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    assertEquals(3, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    // List admin case list.

    CaseFilter theCaseFilter = new CaseFilter(Arrays.asList(LabelModel.ADMIN_ALL_CASES.getUUID()), 0, 20);
    theCaseFilter.setWithAdminRights(true);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(1, theCases.size());
    // Check that the UUID is the one of the parent.
    assertEquals(theProcessInstanceUUID.getValue(), theCases.iterator().next().getUUID().getValue());
    // Check the number of steps.
    assertEquals(1, theCases.iterator().next().getSteps().size());

    Utils.logout();
  }

  @Test
  public void testGetAdminCaseListWhithSubCasesStartedByAnotherUser() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.0").addHuman(adminUsername).addSubProcess("zSubSubProcess", "zSubSubProcess").done();

    ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

    Utils.logout();
    Utils.login(testUsername, "bpm");
    // Start a case.
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    assertEquals(3, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    Utils.logout();
    Utils.login(adminUsername, "bpm");
    // List admin case list.

    CaseFilter theCaseFilter = new CaseFilter(Arrays.asList(LabelModel.ADMIN_ALL_CASES.getUUID()), 0, 20);
    theCaseFilter.setWithAdminRights(true);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(1, theCases.size());
    // Check that the UUID is the one of the parent.
    assertEquals(theProcessInstanceUUID.getValue(), theCases.iterator().next().getUUID().getValue());
    // Check the number of steps.
    assertEquals(1, theCases.iterator().next().getSteps().size());

    Utils.logout();
  }

  @Test
  public void testGetAdminCaseListWhithSubCasesX21() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.0").addHuman(adminUsername).addSubProcess("zSubSubProcess", "zSubSubProcess").done();

    ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

    // Start a case.
    for (int i = 0; i < 21; i++) {
      AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    }

    // List inbox.

    CaseFilter theCaseFilter = new CaseFilter(Arrays.asList(LabelModel.ADMIN_ALL_CASES.getUUID()), 0, 20);
    theCaseFilter.setWithAdminRights(true);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(20, theCases.size());

    Utils.logout();
  }

  @Test
  public void testGetAdminCaseListWhithSubCasesX21StartedByAnotherUser() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addSubProcess("aSubProcess", "aSubProcess").done();

    ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.0").addHuman(adminUsername).addSubProcess("zSubSubProcess", "zSubSubProcess").done();

    ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

    final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

    Utils.logout();
    Utils.login(testUsername, "bpm");
    // Start a case.
    for (int i = 0; i < 21; i++) {
      AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    }

    Utils.logout();
    Utils.login(adminUsername, "bpm");
    // List admin case list.

    CaseFilter theCaseFilter = new CaseFilter(Arrays.asList(LabelModel.ADMIN_ALL_CASES.getUUID()), 0, 20);
    theCaseFilter.setWithAdminRights(true);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check only one case is built.
    assertEquals(20, theCases.size());

    Utils.logout();
  }

  @Test
  public void testPagingOfAdminCaseListWhithCancelledCasesWithoutSearchInHistory() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition theProcess = ProcessBuilder.createProcess("theProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));

    final ProcessDefinitionUUID processUUID = theProcess.getUUID();

    // Start a case.
    int theInitialInstanceCount = 20;
    for (int i = 0; i < theInitialInstanceCount; i++) {
      AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    }

    assertEquals(theInitialInstanceCount, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    // List admin case list.
    CaseFilter theCaseFilter = new CaseFilter(Arrays.asList(LabelModel.ADMIN_ALL_CASES.getUUID()), 0, 20);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that theInitialInstanceCount cases are built.
    assertEquals(theInitialInstanceCount, theCases.size());

    theCaseFilter.setStartingIndex(20);
    theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that 0 cases are built.
    assertEquals(0, theCases.size());

    // start a new case --> on second page
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

    // reload 2nd page
    theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that 1 cases are built.
    assertEquals(1, theCases.size());

    // cancel the last case
    AccessorUtil.getRuntimeAPI().cancelProcessInstance(theProcessInstanceUUID);

    // reload 1st page
    theCaseFilter.setStartingIndex(0);
    theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    assertEquals(theInitialInstanceCount, theCases.size());

    // reload 2nd page
    theCaseFilter.setStartingIndex(20);
    theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that 0 cases are built.
    assertEquals(0, theCases.size());

    Utils.logout();
  }

  @Test
  public void testPagingOfAdminCaseListWhithCancelledCasesAndSearchInHistory() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition theProcess = ProcessBuilder.createProcess("theProcess", "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));

    final ProcessDefinitionUUID processUUID = theProcess.getUUID();

    // Start cases and cancel them.
    int theInitialInstanceCount = 20;
    ProcessInstanceUUID theInstanceUUID;
    for (int i = 0; i < theInitialInstanceCount; i++) {
      theInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
      AccessorUtil.getRuntimeAPI().cancelProcessInstance(theInstanceUUID);
    }

    assertEquals(theInitialInstanceCount, AccessorUtil.getQueryRuntimeAPI().getProcessInstances().size());

    // List admin case list.
    CaseFilter theCaseFilter = new CaseFilter(Arrays.asList(LabelModel.ADMIN_ALL_CASES.getUUID()), 0, 20);
    theCaseFilter.setWithAdminRights(true);
    theCaseFilter.setSearchInHistory(true);
    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that 0 cases are built.
    assertEquals(theInitialInstanceCount, theCases.size());

    theCaseFilter.setStartingIndex(20);
    theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that 0 cases are built.
    assertEquals(0, theCases.size());

    // start a new case --> create a second page
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

    // reload 2nd page
    theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that 0 cases are built.
    assertEquals(0, theCases.size());

    // cancel the last case
    AccessorUtil.getRuntimeAPI().cancelProcessInstance(theProcessInstanceUUID);

    // reload 1st page
    theCaseFilter.setStartingIndex(0);
    theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    assertEquals(theInitialInstanceCount, theCases.size());

    // reload 2nd page
    theCaseFilter.setStartingIndex(20);
    theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that 1 cases are built.
    assertEquals(1, theCases.size());

    Utils.logout();
  }

  @Test
  public void testBuildCasesWhithAdminRightsWithProcessFullAuto() throws Exception {

    Utils.login(adminUsername, "bpm");

    ProcessDefinition theFullAutoProcess = ProcessBuilder.createProcess("FullyAutomatic", "1.0").addSystemTask("auto1").done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(theFullAutoProcess));

    final ProcessDefinitionUUID processUUID = theFullAutoProcess.getUUID();

    // Start a case.
    AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

    CaseFilter theCaseFilter = new CaseFilter(Arrays.asList(LabelModel.ADMIN_ALL_CASES.getUUID()), 0, 20);
    theCaseFilter.setWithAdminRights(true);
    theCaseFilter.setSearchInHistory(true);

    Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    assertEquals(1, theCases.size());
    assertNotNull(theCases.iterator().next().getSteps());

    Utils.logout();
  }

  @Test
  public void testPagingOfAdminCaseListWhithCancelledCasesJournalOnly() throws Exception {

    Utils.login(adminUsername, "bpm");

    final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
    final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
    final QueryRuntimeAPI theQueryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI(AccessorUtil.QUERYLIST_JOURNAL_KEY);
    final CaseDataStore theCaseDataStore = CaseDataStore.getInstance();

    ProcessDefinition theHumanProcess = ProcessBuilder.createProcess("humanProcess", "1.0").addHuman("nicolas").addHumanTask("t", "nicolas").addHumanTask("t2", "nicolas").done();
    ProcessDefinition theSystemProcess = ProcessBuilder.createProcess("systemProcess", "A_b").addSystemTask("auto").addSystemTask("auto2").done();

    theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(theHumanProcess));
    theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(theSystemProcess));

    final ProcessDefinitionUUID humanProcessUUID = theHumanProcess.getUUID();
    final ProcessDefinitionUUID systemProcessUUID = theSystemProcess.getUUID();

    // Start a case.
    int theInitialInstanceCount = 20;
    for (int i = 0; i < theInitialInstanceCount; i++) {
      theRuntimeAPI.instantiateProcess(humanProcessUUID);
      Thread.sleep(1);
      theRuntimeAPI.instantiateProcess(systemProcessUUID);
    }

    // As we are querying the Journal only, completed cases are not visible.
    assertEquals(theInitialInstanceCount, theQueryRuntimeAPI.getProcessInstances().size());

    // List admin case list.
    CaseFilter theCaseFilter = new CaseFilter(Arrays.asList(LabelModel.ADMIN_ALL_CASES.getUUID()), 0, 20);
    theCaseFilter.setWithAdminRights(true);
    Collection<CaseItem> theCases = theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();

    // As we are querying the Journal only, completed cases are not visible.
    // Check that theInitialInstanceCount cases are built.
    assertEquals(theInitialInstanceCount, theCases.size());

    theCaseFilter.setStartingIndex(20);
    theCases = theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that 0 cases are built.
    assertEquals(0, theCases.size());

    // start a new case --> a second page will be created
    ProcessInstanceUUID theProcessInstanceUUID = theRuntimeAPI.instantiateProcess(humanProcessUUID);

    // reload 2nd page
    theCases = theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // Check that 1 cases are built.
    assertEquals(1, theCases.size());

    // cancel the last case
    theRuntimeAPI.cancelProcessInstance(theProcessInstanceUUID);

    // reload 1st page
    theCaseFilter.setStartingIndex(0);
    theCases = theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    assertEquals(theInitialInstanceCount, theCases.size());

    // reload 2nd page
    theCaseFilter.setStartingIndex(20);
    theCases = theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
    // As we are querying the Journal only, completed cases are not visible.
    // Check that 0 cases are built.
    assertEquals(0, theCases.size());

    Utils.logout();
  }

  // @Test
  // public void
  // testPagingOfAdminCaseListWhithCancelledCasesJournalAndHistory() throws
  // Exception {
  //
  // Utils.login(adminUserUUID.getValue(), "bpm");
  //
  // final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
  // final RuntimeAPI theRuntimeAPI = AccessorUtil.getRuntimeAPI();
  // final QueryRuntimeAPI theQueryRuntimeAPI =
  // AccessorUtil.getQueryRuntimeAPI();
  // final CaseDataStore theCaseDataStore = CaseDataStore.getInstance();
  //		
  // ProcessDefinition theHumanProcess =
  // ProcessBuilder.createProcess("humanProcess",
  // "1.0").addHuman("nicolas").addHumanTask("t",
  // "nicolas").addHumanTask("t2", "nicolas").done();
  // ProcessDefinition theSystemProcess =
  // ProcessBuilder.createProcess("systemProcess",
  // "A_b").addSystemTask("auto").addSystemTask("auto2").done();
  //
  // theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(theHumanProcess));
  // theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(theSystemProcess));
  //		
  //
  // final ProcessDefinitionUUID humanProcessUUID = theHumanProcess.getUUID();
  // final ProcessDefinitionUUID systemProcessUUID =
  // theSystemProcess.getUUID();
  //
  // // Start a case.
  // int theInitialInstanceCount = 10;
  // for (int i = 0; i < theInitialInstanceCount; i++) {
  // theRuntimeAPI.instantiateProcess(humanProcessUUID);
  // Thread.sleep(1);
  // theRuntimeAPI.instantiateProcess(systemProcessUUID);
  // }
  //		
  // assertEquals(theInitialInstanceCount*2,
  // theQueryRuntimeAPI.getProcessInstances().size());
  //
  // // List admin case list.
  // CaseFilter theCaseFilter = new CaseFilter(null, 0, 20);
  // theCaseFilter.setWithAdminRights(true);
  // Collection<CaseItem> theCases =
  // theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile,
  // myRequest).getCases();
  // // Check that theInitialInstanceCount cases are built.
  // assertEquals(theInitialInstanceCount*2, theCases.size());
  //		
  // theCaseFilter.setStartingIndex(20);
  // theCases = theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile,
  // myRequest).getCases();
  // // Check that 0 cases are built.
  // assertEquals(0, theCases.size());
  //		
  // // start a new case --> on second page
  // ProcessInstanceUUID theProcessInstanceUUID =
  // theRuntimeAPI.instantiateProcess(humanProcessUUID);
  //
  // // reload 2nd page
  // theCases = theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile,
  // myRequest).getCases();
  // // Check that 1 cases are built.
  // assertEquals(1, theCases.size());
  //		
  // // cancel the last case
  // theRuntimeAPI.cancelProcessInstance(theProcessInstanceUUID);
  //		
  // // reload 1st page
  // theCaseFilter.setStartingIndex(0);
  // theCases = theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile,
  // myRequest).getCases();
  // // Check that 1 cases are built.
  // assertEquals(theInitialInstanceCount*2, theCases.size());
  //		
  // // reload 2nd page
  // theCaseFilter.setStartingIndex(20);
  // theCases = theCaseDataStore.getAllCases(theCaseFilter, adminUserProfile,
  // myRequest).getCases();
  // // Check that 1 cases are built.
  // assertEquals(1, theCases.size());
  //		
  // Utils.logout();
  // }

  @Test
  public void testRenameLabelWithCases() throws Exception {

    Utils.login(testUsername, "bpm");

    ProcessDefinition theProcess = ProcessBuilder.createProcess("testRenameLabelWithCases", "rc1").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

    AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(theProcess));

    final ProcessDefinitionUUID processUUID = theProcess.getUUID();

    // Start a case.
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);

    // Create a label.
    LabelModel theLabel;
    final String A_NAME = "a name";
    final String A_NEW_NAME = "a new name";

    LabelDataStore.getInstance().createNewLabel(testUserProfile, A_NAME);
    theLabel = LabelDataStore.getInstance().getLabel(new UserUUID(testUsername), A_NAME);

    LabelDataStore.getInstance().getAllLabels(testUsername);

    // Add label to case.
    LabelDataStore.getInstance().updateLabels(testUserProfile, new HashSet<LabelUUID>(Arrays.asList(theLabel.getUUID())), new HashSet<LabelUUID>(),
        new HashSet<CaseUUID>(Arrays.asList(new CaseUUID(theProcessInstanceUUID.getValue()))));

    // List cases tied to the label
    ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
    thefilterLabels.add(theLabel.getUUID());
    CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
    Collection<CaseItem> labelCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, testUserProfile, myRequest).getCases();
    assertEquals(1, labelCases.size());

    // rename the label
    LabelDataStore.getInstance().renameLabel(testUserProfile, theLabel.getUUID(), A_NEW_NAME);

    // Check that the old name cannot be found anymore
    theLabel = LabelDataStore.getInstance().getLabel(new UserUUID(testUsername), A_NAME);
    assertNull("The label '" + A_NAME + "' should not exist anymore. But seems to still be in the DB.", theLabel);
    // Check that the new name is there.
    theLabel = LabelDataStore.getInstance().getLabel(new UserUUID(testUsername), A_NEW_NAME);
    assertNotNull("The label '" + A_NEW_NAME + "' should exist. But seems NOT to be in the DB.", theLabel);

    // List cases tied to the old label name
    labelCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, testUserProfile, myRequest).getCases();
    assertEquals(0, labelCases.size());

    // List cases tied to the new label name
    thefilterLabels.clear();
    thefilterLabels.add(theLabel.getUUID());
    theCaseFilter.setLabels(thefilterLabels);
    labelCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, testUserProfile, myRequest).getCases();
    assertEquals(1, labelCases.size());

    LabelDataStore.getInstance().getAllLabels(testUsername);

    Utils.logout();
  }

  @Test
  public void testXorGates() throws Exception {

    Utils.login(adminUsername, "bpm");
    final String human = adminUsername;
    final String stepToExecuteName = "branch1Step1";
    final String stepToBecancelledName = "branch2Step1";
    final String stepAfterJoinName = "step2";
    ProcessDefinition process = ProcessBuilder.createProcess("xorCancellingBranchWithMultipleOutgoingTransitions", "1.0").addHuman(human).addSystemTask("start").addSystemTask("end").addDecisionNode(
        "xor").addHumanTask(stepToExecuteName, human).addHumanTask(stepToBecancelledName, human).addHumanTask(stepAfterJoinName, human).addTransition("start", stepToExecuteName).addTransition(
        stepToExecuteName, "xor").addTransition("start", stepToBecancelledName).addTransition(stepToBecancelledName, "end").addTransition(stepToBecancelledName, "xor").addTransition("xor",
        stepAfterJoinName).addTransition(stepAfterJoinName, "end").done();

    final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
    managementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(process));
    final ProcessDefinitionUUID processUUID = process.getUUID();

    // Start a case.
    final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
    final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
    ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
    assertEquals(1, queryRuntimeAPI.getProcessInstances().size());

    Collection<LightTaskInstance> tasks = queryRuntimeAPI.getLightTaskList(theProcessInstanceUUID, ActivityState.READY);
    assertEquals(2, tasks.size());

    Iterator<LightTaskInstance> iter = tasks.iterator();
    LightTaskInstance task = iter.next();
    if (!stepToExecuteName.equals(task.getActivityName())) {
      task = iter.next();
    }
    assertEquals(stepToExecuteName, task.getActivityName());

    runtimeAPI.executeTask(task.getUUID(), true);

    tasks = queryRuntimeAPI.getLightTaskList(theProcessInstanceUUID, ActivityState.READY);
    assertEquals(1, tasks.size());

    iter = tasks.iterator();
    task = iter.next();
    assertEquals(stepAfterJoinName, task.getActivityName());

    // Check the number of steps.
    managementAPI.deleteProcess(processUUID);
    Utils.logout();
  }

  @Test
  public void testGetConfiguration() throws Exception {

    Utils.login(adminUsername, "bpm");
    PreferencesDataStore.getInstance().removePreference(CASE_LIST_LAYOUT_KEY);
    final CasesConfiguration theConfiguration = CaseDataStore.getInstance().getConfiguration();
    assertNotNull(theConfiguration);
    int i = 0;
    final StringBuffer theCSV = new StringBuffer();
    for (Columns theColumn : Columns.values()) {
      assertEquals(theConfiguration.getColumnIndex(theColumn), i);
      theCSV.append(theColumn.name()).append(",");
      i++;
    }
    assertEquals(theConfiguration.getColumnLayout() + ",", theCSV.toString());
    PreferencesDataStore.getInstance().removePreference(CASE_LIST_LAYOUT_KEY);
    Utils.logout();
  }

  @Test
  public void testUpdateConfiguration() throws Exception {

    Utils.login(adminUsername, "bpm");
    PreferencesDataStore.getInstance().removePreference(CASE_LIST_LAYOUT_KEY);
    final CasesConfiguration theInitialConfiguration = CaseDataStore.getInstance().getConfiguration();
    assertNotNull(theInitialConfiguration);
    for (Columns theColumn : Columns.values()) {
      assertTrue(theInitialConfiguration.getColumnIndex(theColumn) >= 0);
    }
    final CasesConfiguration theNewConfiguration = new CasesConfiguration();
    for (Columns theColumn : Columns.values()) {
      theNewConfiguration.setColumnIndex(theColumn, -1);
    }

    CaseDataStore.getInstance().updateConfiguration(adminUserProfile, theNewConfiguration);
    CasesConfiguration theUpdatedConfiguration = CaseDataStore.getInstance().getConfiguration();
    assertNotNull(theUpdatedConfiguration);
    for (Columns theColumn : Columns.values()) {
      assertTrue(theUpdatedConfiguration.getColumnIndex(theColumn) == -1);
    }

    theNewConfiguration.setColumnIndex(Columns.SELECT_COLUMN, 0);
    theNewConfiguration.setColumnIndex(Columns.APPLICATION_COLUMN, 3);
    theUpdatedConfiguration = CaseDataStore.getInstance().updateConfiguration(adminUserProfile, theNewConfiguration);
    for (Columns theColumn : Columns.values()) {
      if (theColumn == Columns.SELECT_COLUMN) {
        assertTrue(theUpdatedConfiguration.getColumnIndex(theColumn) == 0);
      } else if (theColumn == Columns.APPLICATION_COLUMN) {
        assertTrue(theUpdatedConfiguration.getColumnIndex(theColumn) == 3);
      } else {
        assertTrue(theUpdatedConfiguration.getColumnIndex(theColumn) == -1);
      }
    }

    PreferencesDataStore.getInstance().removePreference(CASE_LIST_LAYOUT_KEY);
    Utils.logout();
  }
  
  @Test
  public void testUpdateConfigurationRemovesPreviousMapping() throws Exception {

    Utils.login(adminUsername, "bpm");
    PreferencesDataStore.getInstance().removePreference(CASE_LIST_LAYOUT_KEY);
    final CasesConfiguration theInitialConfiguration = CaseDataStore.getInstance().getConfiguration();
    assertNotNull(theInitialConfiguration);
    assertTrue(theInitialConfiguration.getColumnLayout().matches(".*,STAR_COLUMN,.*,.*,.*,.*,.*,APPLICATION_COLUMN"));
    final CasesConfiguration theNewConfiguration = new CasesConfiguration();
    theNewConfiguration.update(theInitialConfiguration);
    theNewConfiguration.setColumnIndex(Columns.STAR_COLUMN, Columns.values().length -1);

    CasesConfiguration theUpdatedConfiguration = CaseDataStore.getInstance().updateConfiguration(adminUserProfile, theNewConfiguration);
    assertNotNull(theUpdatedConfiguration);
    assertTrue(theUpdatedConfiguration.getColumnLayout(), theUpdatedConfiguration.getColumnLayout().matches(".*,,.*,.*,.*,.*,.*,STAR_COLUMN"));

    theNewConfiguration.setColumnIndex(Columns.STAR_COLUMN, 0);
    theUpdatedConfiguration = CaseDataStore.getInstance().updateConfiguration(adminUserProfile, theNewConfiguration);
    assertTrue(theUpdatedConfiguration.getColumnLayout().matches("STAR_COLUMN,,.*,.*,.*,.*,.*,"));

    PreferencesDataStore.getInstance().removePreference(CASE_LIST_LAYOUT_KEY);
    Utils.logout();
  }
  
  @Test
  public void testListInboxWithInstancesHavingEventSubProcess() throws Exception {
      Utils.login(adminUsername, "bpm");

      ProcessDefinition eventSubProcess =
          ProcessBuilder.createProcess("ESB", "1.0")
          .setEventSubProcess()
          .addTimerTask("Wait_4_seconds", "2000")
          .addHuman(adminUsername)
          .addHumanTask("event", adminUsername)
          .addTransition("Wait_4_seconds", "event")
          .done();

        ProcessDefinition process =
          ProcessBuilder.createProcess("process", "1.0")
          .addHuman(adminUsername)
          .addHumanTask("wait", adminUsername)
          .addEventSubProcess("ESB", "1.0")
          .done();

        final ManagementAPI theManagementAPI =  AccessorUtil.getManagementAPI();
        theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(eventSubProcess));
        theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(process));

        

      final ProcessDefinitionUUID processUUID = process.getUUID();
      final RuntimeAPI theRuntimeAPI =  AccessorUtil.getRuntimeAPI();
      final QueryRuntimeAPI theQueryRuntimeAPI =  AccessorUtil.getQueryRuntimeAPI();
      // Start a case.
      ProcessInstanceUUID theProcessInstanceUUID = theRuntimeAPI.instantiateProcess(processUUID);
      Thread.sleep(3000);
      assertEquals(2, theQueryRuntimeAPI.getProcessInstances().size());

      // List inbox.
      ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
      thefilterLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
      CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
      Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
      // Check only one case is built.
      assertEquals(1, theCases.size());
      // Check that the UUID is the one of the parent.
      assertEquals(theProcessInstanceUUID.getValue(), theCases.iterator().next().getUUID().getValue());
      // Check the number of steps.
      assertEquals(1, theCases.iterator().next().getSteps().size());
      Utils.logout();
  }
  
  @Test
  public void testGetCaseHavingEventSubProcess() throws Exception {
      Utils.login(adminUsername, "bpm");

      ProcessDefinition eventSubProcess =
          ProcessBuilder.createProcess("ESB", "1.0")
          .setEventSubProcess()
          .addTimerTask("Wait_4_seconds", "2000")
          .addHuman(adminUsername)
          .addHumanTask("event", adminUsername)
          .addTransition("Wait_4_seconds", "event")
          .done();

        ProcessDefinition process =
          ProcessBuilder.createProcess("process", "1.0")
          .addHuman(adminUsername)
          .addHumanTask("wait", adminUsername)
          .addEventSubProcess("ESB", "1.0")
          .done();

        final ManagementAPI theManagementAPI =  AccessorUtil.getManagementAPI();
        theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(eventSubProcess));
        theManagementAPI.deploy(BusinessArchiveFactory.getBusinessArchive(process));

        

      final ProcessDefinitionUUID processUUID = process.getUUID();
      final RuntimeAPI theRuntimeAPI =  AccessorUtil.getRuntimeAPI();
      final QueryRuntimeAPI theQueryRuntimeAPI =  AccessorUtil.getQueryRuntimeAPI();
      // Start a case.
      ProcessInstanceUUID theProcessInstanceUUID = theRuntimeAPI.instantiateProcess(processUUID);
      Thread.sleep(3000);
      assertEquals(2, theQueryRuntimeAPI.getProcessInstances().size());

      
      // Get a case of the inbox.
      ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
      thefilterLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
      CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, 20);
      
      CaseItem theCaseItem = CaseDataStore.getInstance().getCase(new CaseUUID(theProcessInstanceUUID.getValue()), adminUserProfile, theCaseFilter, myRequest);
      assertNotNull(theCaseItem);
      // Check the number of steps.
      assertEquals(2, theCaseItem.getSteps().size());
      boolean found = false;
      for (StepItem theSepItem : theCaseItem.getSteps()) {
          if("event".equals(theSepItem.getName())) {
              found = true;
          }
      }
      assert(found);
      Utils.logout();
  }

  private void filledUpWithSubProcesses(int caseNumber, long maxExpectedTime) throws Exception {
    fillInDBWithSubProcesses(caseNumber);
    System.err.println("\n");
    getInstancesFromWebAndcheckExecTime(maxExpectedTime, caseNumber);
    System.err.println("\n");
  }

  private void filledUpWithoutSubProcesses(int caseNumber, long maxExpectedTime) throws Exception {
    fillInDBWithoutSubProcesses(caseNumber);
    System.err.println("\nfillInDBWithoutSubProcesses ended.\n");
    getInstancesFromWebAndcheckExecTime(maxExpectedTime, caseNumber);
    System.err.println("\ngetInstancesFromWebAndcheckExecTime ended.\n");
  }

  private void getInstancesFromWebAndcheckExecTime(long maxExpected, int caseNumber) throws Exception {
    Utils.login(adminUsername, "bpm");
    int pageSize = 20;
    if (pageSize > caseNumber) {
      pageSize = caseNumber;
    }
    try {
      // List inbox.
      ArrayList<LabelUUID> thefilterLabels = new ArrayList<LabelUUID>();
      thefilterLabels.add(new LabelUUID(LabelModel.INBOX_LABEL.getUUID().getValue(), new UserUUID(adminUsername)));
      CaseFilter theCaseFilter = new CaseFilter(thefilterLabels, 0, pageSize);

      final long before = System.currentTimeMillis();
      Collection<CaseItem> theCases = CaseDataStore.getInstance().getAllCases(theCaseFilter, adminUserProfile, myRequest).getCases();
      final long execTime = System.currentTimeMillis() - before;

      // Check only one case is built.
      assertEquals(pageSize, theCases.size());
      System.err.println("Test with " + caseNumber + " took " + execTime + "ms");
      assertTrue("Test took much than " + maxExpected + "(" + execTime + ")", (execTime <= maxExpected));

    } finally {
      Utils.logout();
    }
  }

  private void fillInDB(int caseNumber, long maxExpectedTime) throws Exception {
    fillInDBWithSubProcesses(caseNumber);
    getInstancesFromRuntimeAndcheckExecTime(maxExpectedTime, caseNumber);
  }

  private void getInstancesFromRuntimeAndcheckExecTime(long maxExpected, int caseNumber) throws Exception {
    Utils.login(adminUsername, "bpm");
    try {
      final long before = System.currentTimeMillis();
      final List<LightProcessInstance> instances = AccessorUtil.getQueryRuntimeAPI().getLightProcessInstances(0, 20);
      final long execTime = System.currentTimeMillis() - before;
      assertEquals(20, instances.size());
      System.err.println("Test with " + caseNumber + " took " + execTime + "ms");
      assertTrue("Test took much than " + maxExpected + "(" + execTime + ")", (execTime <= maxExpected));
    } finally {
      Utils.logout();
    }
  }

  private void fillInDBWithSubProcesses(int caseNumber) throws Exception {
    Utils.login(adminUsername, "bpm");
    try {
      // assertEquals(0,
      // AccessorUtil.getQueryRuntimeAPI().getLightProcessInstances().size());
      ProcessDefinition parentProcess = ProcessBuilder.createProcess("parentProcess", "1.0").addHuman(adminUsername).addSystemTask("start").addSubProcess("aSubProcess", "aSubProcess").addHumanTask(
          "t", adminUsername).addTransition("start", "aSubProcess").addTransition("start", "t").done();

      ProcessDefinition subProcess = ProcessBuilder.createProcess("aSubProcess", "1.1").addHuman(testUsername).addSystemTask("start").addSubProcess("zSubSubProcess", "zSubSubProcess").addHumanTask(
          "o", testUsername).addTransition("start", "zSubSubProcess").addTransition("start", "o").done();

      ProcessDefinition subSubProcess = ProcessBuilder.createProcess("zSubSubProcess", "rc1").addHuman(adminUsername).addHumanTask("t", adminUsername).done();

      AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subSubProcess));
      AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(subProcess));
      AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(parentProcess));

      final ProcessDefinitionUUID processUUID = parentProcess.getUUID();

      // Start a case.
      for (int i = 0; i < caseNumber; i++) {
        AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
      }
    } finally {
      Utils.logout();
    }
  }

  private void fillInDBWithoutSubProcesses(int caseNumber) throws Exception {
    Utils.login(adminUsername, "bpm");
    try {
      // assertEquals(0,
      // AccessorUtil.getQueryRuntimeAPI().getLightProcessInstances().size());
      ProcessDefinition process;

      ProcessDefinitionUUID processUUID;

      // Start a case.
      for (int i = 0; i < caseNumber; i++) {
        process = ProcessBuilder.createProcess(("process" + System.currentTimeMillis()), "1.0").addHuman(adminUsername).addHumanTask("overdue" + i, adminUsername).addActivityExecutingTime(1000)
            .addHumanTask("onTrack" + i, adminUsername).addActivityExecutingTime(999999999).addHumanTask("atRisk" + i, adminUsername).addActivityExecutingTime(10000).done();
        AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(process));
        processUUID = process.getUUID();
        AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
      }
    } finally {
      Utils.logout();
    }
  }

}
