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
package org.bonitasoft.forms.server.api.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.bonitasoft.connectors.bonita.SetVarConnector;
import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.ActivityAttribute;
import org.bonitasoft.forms.client.model.ActivityEditState;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.server.FormsTestCase;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormWorkflowAPI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.element.HookDefinition.Event;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.runtime.command.WebDeleteDocumentsOfProcessCommand;
import org.ow2.bonita.facade.runtime.command.WebDeleteProcessCommand;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.ProcessBuilder;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * Unit test for the implementation of the form workflow API
 * @author Anthony Birembaut
 * 
 */
public class TestFormWorkflowAPIImpl extends FormsTestCase {

    private ProcessDefinition bonitaProcess;

    private ProcessInstanceUUID processInstanceUUID;

    private LoginContext loginContext;

    @Before
    public void setUp() throws Exception {

        login("john", "bpm");

        URL testBarURL = getClass().getResource("/approvalWorkflow.bar");
        if (testBarURL == null) {
            throw new RuntimeException("File approvalWorkflow.bar was not found.");
        }
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(new File(testBarURL.toURI()));
        bonitaProcess = AccessorUtil.getManagementAPI().deploy(businessArchive);
        processInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(bonitaProcess.getUUID());
    }

    @After
    public void tearDown() throws Exception {

        AccessorUtil.getRuntimeAPI().deleteAllProcessInstances(bonitaProcess.getUUID());
        AccessorUtil.getManagementAPI().disable(bonitaProcess.getUUID());
        AccessorUtil.getManagementAPI().deleteProcess(bonitaProcess.getUUID());

        loginContext.logout();
    }

    @Test
    public void testGetTaskFieldValue() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        Object result = null;
        for (TaskInstance activityInstance : tasks) {
            ActivityInstanceUUID uuid = activityInstance.getUUID();
            result = api.getFieldValue(uuid, "${Applications}", Locale.ENGLISH, true);
        }
        Assert.assertNotNull(result);
        Assert.assertEquals("Word", result);
    }
    
    @Test
    public void testGetInstanceFieldValue() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        Object result = null;
        result = api.getFieldValue(processInstanceUUID, "${Applications}", Locale.ENGLISH, true);
        Assert.assertEquals("Word", result);
    }
    
    @Test
    public void testGetDefinitionFieldValue() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        Object result = null;
        result = api.getFieldValue(bonitaProcess.getUUID(), "${Applications}", Locale.ENGLISH);
        Assert.assertEquals("Word", result);
    }

    @Test
    public void testExecuteProcessActions() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        FormFieldValue value1 = new FormFieldValue("Excel", String.class.getName());
        fieldValues.put("fieldId1", value1);
        List<FormAction> formActions = new ArrayList<FormAction>();
        formActions.add(new FormAction(ActionType.SET_VARIABLE, "Applications", "APPLICATION", "field_fieldId1", "submitButtonId"));
        api.executeActions(processInstanceUUID, fieldValues, formActions, Locale.ENGLISH);

        Object variableValue = queryRuntimeAPI.getProcessInstanceVariable(processInstanceUUID, "Applications");
        Assert.assertEquals("Excel", variableValue.toString());
    }
    
    @Test
    public void testExecuteTaskActions() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        for (TaskInstance activityInstance : tasks) {
            ActivityInstanceUUID uuid = activityInstance.getUUID();
            Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
            FormFieldValue value1 = new FormFieldValue("Excel", String.class.getName());
            fieldValues.put("fieldId1", value1);
            List<FormAction> formActions = new ArrayList<FormAction>();
            formActions.add(new FormAction(ActionType.SET_VARIABLE, "Applications", "APPLICATION", "field_fieldId1", "submitButtonId"));
            api.executeActions(uuid, fieldValues, formActions, Locale.ENGLISH);
        }
        Object variableValue = queryRuntimeAPI.getProcessInstanceVariable(processInstanceUUID, "Applications");
        Assert.assertEquals("Excel", variableValue.toString());
    }
    
    @Test
    public void testExecuteActionsAndTerminate() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        for (TaskInstance activityInstance : tasks) {
            ActivityInstanceUUID uuid = activityInstance.getUUID();
            Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
            FormFieldValue value1 = new FormFieldValue("Excel", String.class.getName());
            fieldValues.put("fieldId1", value1);
            List<FormAction> formActions = new ArrayList<FormAction>();
            formActions.add(new FormAction(ActionType.SET_VARIABLE, "Applications", "APPLICATION", "field_fieldId1", "submitButtonId"));
            api.executeActionsAndTerminate(uuid, fieldValues, formActions, Locale.ENGLISH, "submitButtonId", new HashMap<String, Object>());
        }
        String activityId = null;
        tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        for (TaskInstance activityInstance : tasks) {
            activityId = activityInstance.getActivityName();
        }
        Assert.assertNotNull(activityId);
        Assert.assertEquals("Approval", activityId);
        Object variableValue = queryRuntimeAPI.getProcessInstanceVariable(processInstanceUUID, "Applications");
        Assert.assertEquals("Excel", variableValue.toString());
    }
    
    @Test
    public void testExecuteActionsAndTerminateWithAPIAccessor() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        for (TaskInstance activityInstance : tasks) {
            ActivityInstanceUUID uuid = activityInstance.getUUID();
            Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
            List<FormAction> formActions = new ArrayList<FormAction>();
            formActions.add(new FormAction(ActionType.EXECUTE_SCRIPT, "Applications", "APPLICATION", "${" + BonitaConstants.API_ACCESSOR + ".getManagementAPI()}", "submitButtonId"));
            //api.executeActionsAndTerminate(uuid, fieldValues, formActions, Locale.ENGLISH, "submitButtonId", new HashMap<String, Object>());
        }
    }
    
    @Test
    public void testExecuteActionsAndStartInstance() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        FormFieldValue value1 = new FormFieldValue("Excel", String.class.getName());
        fieldValues.put("fieldId1", value1);
        List<FormAction> formActions = new ArrayList<FormAction>();
        formActions.add(new FormAction(ActionType.SET_VARIABLE, "Applications", "APPLICATION", "field_fieldId1", "submitButtonId"));
        ProcessInstanceUUID newProcessInstanceUUID = api.executeActionsAndStartInstance(bonitaProcess.getUUID(), fieldValues, formActions, Locale.ENGLISH, "submitButtonId", new HashMap<String, Object>());
        Assert.assertNotNull(newProcessInstanceUUID);
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Object variableValue = queryRuntimeAPI.getProcessInstanceVariable(newProcessInstanceUUID, "Applications");
        Assert.assertEquals("Excel", variableValue.toString());
        AccessorUtil.getRuntimeAPI().deleteProcessInstance(newProcessInstanceUUID);
    }
    
    @Test
    public void testGetProcessInstanceNextTask() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        runtimeAPI.executeTask(uuid, true);
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        ActivityInstanceUUID activityInstanceUUID = api.getProcessInstanceNextTask(uuid.getProcessInstanceUUID());
        Assert.assertEquals("Approval", activityInstanceUUID.getActivityName());
    }
    
    @Test
    public void testGetProcessInstanceNextTaskAfterInstantiation() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        ActivityInstanceUUID activityInstanceUUID = api.getProcessInstanceNextTask(processInstanceUUID);
        Assert.assertEquals("Request", activityInstanceUUID.getActivityName());
    }
    
    @Test
    public void testGetSubprocessNextTask() throws Exception {
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
        
        ProcessDefinition childProcess = ProcessBuilder.createProcess("child_process", "1.0")
        .addHuman("john")
        .addHumanTask("childTask", "john")
        .done();
        
        ProcessDefinition parentProcess = ProcessBuilder.createProcess("parent_process", "1.0")
        .addHuman("john")
        .addSubProcess("subTask", "child_process")
        .addHumanTask("parentTask", "john")
        .addTransition("transition", "subTask", "parentTask")
        .done();

        BusinessArchive childBusinessArchive = BusinessArchiveFactory.getBusinessArchive(childProcess);
        childProcess = managementAPI.deploy(childBusinessArchive);
        
        BusinessArchive parentBusinessArchive = BusinessArchiveFactory.getBusinessArchive(parentProcess);
        parentProcess = managementAPI.deploy(parentBusinessArchive);

        ProcessInstanceUUID instanceUUID = runtimeAPI.instantiateProcess(parentProcess.getUUID());
        
        try {
            IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
            ActivityInstanceUUID activityInstanceUUID = api.getProcessInstanceNextTask(instanceUUID);
            Assert.assertNull(activityInstanceUUID);
            activityInstanceUUID = api.getRelatedProcessesNextTask(instanceUUID);
            Assert.assertEquals("childTask", activityInstanceUUID.getActivityName());
    
            runtimeAPI.executeTask(activityInstanceUUID, true);
            activityInstanceUUID = api.getRelatedProcessesNextTask(activityInstanceUUID.getProcessInstanceUUID());
            Assert.assertEquals("parentTask", activityInstanceUUID.getActivityName());
        } finally {
            runtimeAPI.deleteProcessInstance(instanceUUID);
            
            managementAPI.deleteProcess(parentProcess.getUUID());
            managementAPI.deleteProcess(childProcess.getUUID());
        }
    }
    
    @Test
    public void testGetSubprocessLoopNextTask() throws Exception {
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        
        ProcessDefinition childProcess = ProcessBuilder.createProcess("sub_process", "1.0")
        .addIntegerData("counter")
        .addHuman("john")
        .addHumanTask("task1", "john")
        .addHumanTask("task2", "john")
          .addConnector(Event.taskOnFinish, SetVarConnector.class.getName(), true)
          .addInputParameter("variableName", "counter")
          .addInputParameter("value", "${++counter}")
        .addTransition("transition1", "task1", "task2")
        .done();
        
        ProcessDefinition processWithSubProcess = ProcessBuilder.createProcess("parent_process", "1.0")
        .addHuman("john")
        .addIntegerData("counter", 0)
        .addSubProcess("subprocess", "sub_process")
          .addSubProcessInParameter("counter", "counter")
          .addSubProcessOutParameter("counter", "counter")
          .addLoop("counter < 10", false)
        .addHumanTask("task3", "john")
        .addTransition("transition2", "subprocess", "task3")
        .done();

        BusinessArchive childBusinessArchive = BusinessArchiveFactory.getBusinessArchive(childProcess);
        childProcess = managementAPI.deploy(childBusinessArchive);
        
        BusinessArchive parentBusinessArchive = BusinessArchiveFactory.getBusinessArchive(processWithSubProcess);
        processWithSubProcess = managementAPI.deploy(parentBusinessArchive);

        ProcessInstanceUUID processInstanceUUID = runtimeAPI.instantiateProcess(processWithSubProcess.getUUID());
        
        try {
            for (int i = 0; i < 10; i++) {
              Set<ActivityInstance> activityInstances = queryRuntimeAPI.getActivityInstances(processInstanceUUID);
              Assert.assertEquals(i+1, activityInstances.size());
              for (ActivityInstance activityInstance : activityInstances) {
                  Assert.assertEquals("subprocess", activityInstance.getActivityName());
              }
              
              Set<ProcessInstanceUUID> childrenInstanceUUID = queryRuntimeAPI.getChildrenInstanceUUIDsOfProcessInstance(processInstanceUUID);
              for (ProcessInstanceUUID childInstanceUUID : childrenInstanceUUID) {
                LightProcessInstance childInstance = queryRuntimeAPI.getLightProcessInstance(childInstanceUUID);
                if (InstanceState.STARTED.equals(childInstance.getInstanceState())) {
                  Collection<TaskInstance> taskInstances = queryRuntimeAPI.getTaskList(childInstanceUUID, ActivityState.READY);
                  Assert.assertEquals(1, taskInstances.size());
                  TaskInstance childTaskInstance = taskInstances.iterator().next();
                  Assert.assertEquals("task1", childTaskInstance.getActivityName());
                  Assert.assertEquals(ActivityState.READY, childTaskInstance.getState());
                  runtimeAPI.executeTask(childTaskInstance.getUUID(), true);
                  
                  taskInstances = queryRuntimeAPI.getTaskList(childInstanceUUID, ActivityState.READY);
                  Assert.assertEquals(1, taskInstances.size());
                  childTaskInstance = taskInstances.iterator().next();
                  Assert.assertEquals("task2", childTaskInstance.getActivityName());
                  Assert.assertEquals(ActivityState.READY, childTaskInstance.getState());
                  runtimeAPI.executeTask(childTaskInstance.getUUID(), true);
                  
                  if (i < 9) {
                      ActivityInstanceUUID activityInstanceUUID = api.getRelatedProcessesNextTask(childInstance.getUUID());
                      Assert.assertEquals("task1", activityInstanceUUID.getActivityName());
                  }
              
                  childInstance = queryRuntimeAPI.getLightProcessInstance(childInstanceUUID);
                  Assert.assertEquals(InstanceState.FINISHED, childInstance.getInstanceState());
                }
              }
            }
            
            Collection<TaskInstance> taskInstances = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
            Assert.assertEquals(1, taskInstances.size());
            TaskInstance taskInstance = taskInstances.iterator().next();
            Assert.assertEquals("task3", taskInstance.getActivityName());
            Assert.assertEquals(ActivityState.READY, taskInstance.getState());
            runtimeAPI.executeTask(taskInstance.getUUID(), true);
        } finally {
            managementAPI.deleteProcess(processWithSubProcess.getUUID());
            managementAPI.deleteProcess(childProcess.getUUID());
        }
    }
    
    private ProcessDefinition buildIterationProcess() throws Exception {
        ProcessDefinition iterationProcess = ProcessBuilder.createProcess("complex_iteration_process", "1.0")
        .addBooleanData("terminateit", false)
        .addHuman("john")
        .addHumanTask("task1", "john")
        .addSubProcess("subprocess", "sub_process")
        .addSubProcessInParameter("terminateit", "terminateit")
        .addSubProcessOutParameter("terminateit", "terminateit")
        .addHumanTask("task2", "john")
        .addTransition("transition1", "task1", "subprocess")
        .addTransition("transition2", "subprocess", "subprocess")
        .addCondition("!terminateit")
        .addTransition("transition3", "subprocess", "task2")
        .addCondition("terminateit")
        .done();
        
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(iterationProcess);
        return AccessorUtil.getManagementAPI().deploy(businessArchive);
      }
      
      private ProcessDefinition buildSubProcess() throws Exception {
        ProcessDefinition subProcess = ProcessBuilder.createProcess("sub_process", "1.0")
        .addBooleanData("terminateit", false)
        .addHuman("john")
        .addHumanTask("task1sub", "john")
        .addSystemTask("activity2sub")
        .addTransition("transition1", "task1sub", "activity2sub")
        .done();
        
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(subProcess);
        return AccessorUtil.getManagementAPI().deploy(businessArchive);
      }
      
      @Test
      public void testIterateOnSubProcess() throws Exception {
        ProcessDefinition subProcess = buildSubProcess();
        ProcessDefinition processWithSubProcess = buildIterationProcess();
        
        ProcessInstanceUUID processInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processWithSubProcess.getUUID());
        
        try {
            Collection<TaskInstance> taskInstances = AccessorUtil.getQueryRuntimeAPI().getTaskList(processInstanceUUID, ActivityState.READY);
            Assert.assertEquals(1, taskInstances.size());
            AccessorUtil.getRuntimeAPI().executeTask(taskInstances.iterator().next().getUUID(), true);
            
            IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
            
            TaskInstance subprocessTaskInstance = null;
            
            taskInstances = AccessorUtil.getQueryRuntimeAPI().getTaskList(ActivityState.READY);
            for (TaskInstance taskInstance : taskInstances) {
                if (subProcess.getUUID().equals(taskInstance.getProcessDefinitionUUID())) {
                    subprocessTaskInstance = taskInstance;
                }
            }
            Assert.assertNotNull(subprocessTaskInstance);
            Assert.assertEquals("task1sub", subprocessTaskInstance.getActivityName());
            AccessorUtil.getRuntimeAPI().executeTask(subprocessTaskInstance.getUUID(), true);
            Assert.assertNotNull(api.getRelatedProcessesNextTask(subprocessTaskInstance.getProcessInstanceUUID()));
            
            subprocessTaskInstance = null;
            
            taskInstances = AccessorUtil.getQueryRuntimeAPI().getTaskList(ActivityState.READY);
            for (TaskInstance taskInstance : taskInstances) {
                if (subProcess.getUUID().equals(taskInstance.getProcessDefinitionUUID())) {
                    subprocessTaskInstance = taskInstance;
                }
            }
            Assert.assertNotNull(subprocessTaskInstance);
            Assert.assertEquals("task1sub", subprocessTaskInstance.getActivityName());
            AccessorUtil.getRuntimeAPI().executeTask(subprocessTaskInstance.getUUID(), true);
            Assert.assertNotNull(api.getRelatedProcessesNextTask(subprocessTaskInstance.getProcessInstanceUUID()));
    
            subprocessTaskInstance = null;
            
            taskInstances = AccessorUtil.getQueryRuntimeAPI().getTaskList(ActivityState.READY);
            for (TaskInstance taskInstance : taskInstances) {
                if (subProcess.getUUID().equals(taskInstance.getProcessDefinitionUUID())) {
                    subprocessTaskInstance = taskInstance;
                }
            }
            Assert.assertNotNull(subprocessTaskInstance);
            Assert.assertEquals("task1sub", subprocessTaskInstance.getActivityName());
            AccessorUtil.getRuntimeAPI().setVariable(subprocessTaskInstance.getUUID(), "terminateit", true);
            AccessorUtil.getRuntimeAPI().executeTask(subprocessTaskInstance.getUUID(), true);
            Assert.assertNotNull(api.getRelatedProcessesNextTask(subprocessTaskInstance.getProcessInstanceUUID()));
            
            TaskInstance processTaskInstance = null;
            
            taskInstances = AccessorUtil.getQueryRuntimeAPI().getTaskList(ActivityState.READY);
            for (TaskInstance taskInstance : taskInstances) {
                if (processWithSubProcess.getUUID().equals(taskInstance.getProcessDefinitionUUID())) {
                    processTaskInstance = taskInstance;
                }
            }
            Assert.assertNotNull(processTaskInstance);
            Assert.assertEquals("task2", processTaskInstance.getActivityName());
            AccessorUtil.getRuntimeAPI().executeTask(processTaskInstance.getUUID(), true);
        } finally {
            AccessorUtil.getRuntimeAPI().deleteAllProcessInstances(processWithSubProcess.getUUID());
            AccessorUtil.getRuntimeAPI().deleteAllProcessInstances(subProcess.getUUID());
            AccessorUtil.getManagementAPI().deleteProcess(processWithSubProcess.getUUID());
            AccessorUtil.getManagementAPI().deleteProcess(subProcess.getUUID());
        }
      }
    
    @Test
    public void testGetActivityAttachmentFileName() throws Exception {
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        
        File attachmentFile = File.createTempFile("attachment-test",".txt");
        FileOutputStream fileOutputStream = new FileOutputStream(attachmentFile);
        fileOutputStream.write("test".getBytes("UTF-8"));
        fileOutputStream.close();
        
        ProcessDefinition attachmentProcess = ProcessBuilder.createProcess("attachment_process", "1.0")
        .addAttachment("attachment", attachmentFile.getPath(), attachmentFile.getName())
        .addHuman("john")
        .addHumanTask("task1", "john")
        .addHumanTask("task2", "john")
        .addTransition("transition", "task1", "task2")
        .done();
        
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(attachmentProcess);
        attachmentProcess = managementAPI.deploy(businessArchive);

        ProcessInstanceUUID instanceUUID = runtimeAPI.instantiateProcess(attachmentProcess.getUUID());
        
        try {
            IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
            ActivityInstanceUUID activityInstanceUUID = null;
            Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(instanceUUID, ActivityState.READY);
            for (TaskInstance activityInstance : tasks) {
                activityInstanceUUID = activityInstance.getUUID();
            }
            Assert.assertNotNull(activityInstanceUUID);
            String fileName = api.getAttachmentFileName(activityInstanceUUID, "${attachment}", true);
            Assert.assertEquals(attachmentFile.getName(), fileName);
            
            runtimeAPI.executeTask(activityInstanceUUID, true);
            fileName = api.getAttachmentFileName(activityInstanceUUID, "${attachment}", true);
            Assert.assertEquals(attachmentFile.getName(), fileName);
            
            File attachmentFile2 = File.createTempFile("new-attachment-test",".txt");
            tasks = queryRuntimeAPI.getTaskList(instanceUUID, ActivityState.READY);
            for (TaskInstance activityInstance : tasks) {
                activityInstanceUUID = activityInstance.getUUID();
            }
            Assert.assertNotNull(activityInstanceUUID);
            runtimeAPI.addAttachment(instanceUUID, "attachment", attachmentFile2.getName(), "test".getBytes("UTF-8"));
            fileName = api.getAttachmentFileName(activityInstanceUUID, "${attachment}", true);
            Assert.assertEquals(attachmentFile2.getName(), fileName);
        } finally {
            AccessorUtil.getCommandAPI().execute(new WebDeleteDocumentsOfProcessCommand(attachmentProcess.getUUID(), true));
            AccessorUtil.getRuntimeAPI().deleteProcessInstance(instanceUUID);
            AccessorUtil.getCommandAPI().execute(new WebDeleteProcessCommand(attachmentProcess.getUUID()));
        }
    }
    
    @Test
    public void testGetInstanceAttachmentFileName() throws Exception {
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        
        File attachmentFile = File.createTempFile("attachment-test",".txt");
        FileOutputStream fileOutputStream = new FileOutputStream(attachmentFile);
        fileOutputStream.write("test".getBytes("UTF-8"));
        fileOutputStream.close();
        
        ProcessDefinition attachmentProcess = ProcessBuilder.createProcess("attachment_process", "1.0")
        .addAttachment("attachment", attachmentFile.getPath(), attachmentFile.getName())
        .addHuman("john")
        .addHumanTask("task1", "john")
        .addHumanTask("task2", "john")
        .addTransition("transition", "task1", "task2")
        .done();
        
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(attachmentProcess);
        attachmentProcess = managementAPI.deploy(businessArchive);

        ProcessInstanceUUID instanceUUID = runtimeAPI.instantiateProcess(attachmentProcess.getUUID());
        
        try {
            ActivityInstanceUUID activityInstanceUUID = null;
            Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(instanceUUID, ActivityState.READY);
            for (TaskInstance activityInstance : tasks) {
                activityInstanceUUID = activityInstance.getUUID();
            }
            Assert.assertNotNull(activityInstanceUUID);
            //In case xcmis is deployed on another server and they don't have exactly the  same time
            Thread.sleep(1000);
            File attachmentFile2 = File.createTempFile("new-attachment-test",".txt");
            runtimeAPI.addAttachment(instanceUUID, "attachment", attachmentFile2.getName(), "test".getBytes("UTF-8"));
            runtimeAPI.executeTask(activityInstanceUUID, true);
            
            IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
            String fileName = api.getAttachmentFileName(instanceUUID, "${attachment}", false);
            Assert.assertEquals(attachmentFile.getName(), fileName);
        } finally {
            AccessorUtil.getCommandAPI().execute(new WebDeleteDocumentsOfProcessCommand(attachmentProcess.getUUID(), true));
            AccessorUtil.getRuntimeAPI().deleteProcessInstance(instanceUUID);
            AccessorUtil.getCommandAPI().execute(new WebDeleteProcessCommand(attachmentProcess.getUUID()));
        }
    }
    
    @Test
    public void testGetDefinitionAttachmentFileName() throws Exception {
        ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
        
        File attachmentFile = File.createTempFile("attachment-test",".txt");
        FileOutputStream fileOutputStream = new FileOutputStream(attachmentFile);
        fileOutputStream.write("test".getBytes("UTF-8"));
        fileOutputStream.close();
        
        ProcessDefinition attachmentProcess = ProcessBuilder.createProcess("attachment_process", "1.0")
        .addAttachment("attachment", attachmentFile.getPath(), attachmentFile.getName())
        .addHuman("john")
        .addHumanTask("task1", "john")
        .addHumanTask("task2", "john")
        .addTransition("transition", "task1", "task2")
        .done();
        
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(attachmentProcess);
        attachmentProcess = managementAPI.deploy(businessArchive);
        
        try {
            IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
            String fileName = api.getAttachmentFileName(attachmentProcess.getUUID(), "${attachment}");
            Assert.assertEquals(attachmentFile.getName(), fileName);
        } finally {
            AccessorUtil.getCommandAPI().execute(new WebDeleteDocumentsOfProcessCommand(attachmentProcess.getUUID()));
            AccessorUtil.getCommandAPI().execute(new WebDeleteProcessCommand(attachmentProcess.getUUID()));
        }
    }
    
    @Test
    public void testGetAnyTodoListTaskForProcessDefinition() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        ActivityInstanceUUID activityInstanceUUID = api.getAnyTodoListTaskForProcessDefinition(bonitaProcess.getUUID());
        Assert.assertEquals("Request", activityInstanceUUID.getActivityName());
    }
    
    @Test
    public void testGetAnyTodoListTaskForProcessInstance() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        ActivityInstanceUUID activityInstanceUUID = api.getAnyTodoListTaskForProcessInstance(processInstanceUUID);
        Assert.assertEquals("Request", activityInstanceUUID.getActivityName());
    }
    
    @Test
    public void testGetProcessDefinitionDate() throws Exception {
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        Assert.assertEquals(bonitaProcess.getDeployedDate(), api.getProcessDefinitionDate(processInstanceUUID.getProcessDefinitionUUID()));
    }
    
    @Test
    public void testIsTaskOver() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        runtimeAPI.executeTask(uuid, true);
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        Assert.assertTrue(api.isTaskOver(uuid));
    }
    
    @Test
    public void testGetTaskEditState() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        Assert.assertEquals(ActivityEditState.EDITABLE, api.getTaskEditState(uuid));
        runtimeAPI.suspendTask(uuid, true);
        Assert.assertEquals(ActivityEditState.SUSPENDED, api.getTaskEditState(uuid));
        runtimeAPI.resumeTask(uuid, true);
        runtimeAPI.executeTask(uuid, true);
        Assert.assertEquals(ActivityEditState.NOT_EDITABLE, api.getTaskEditState(uuid));
        tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        runtimeAPI.cancelProcessInstance(processInstanceUUID);
        Assert.assertEquals(ActivityEditState.CANCELED, api.getTaskEditState(uuid));
    }
    
    @Test
    public void testGetActivityAttributesCandidates() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        runtimeAPI.executeTask(uuid, true);
        tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);

        IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
        String candidates = (String)api.getAttributes(uuid, Locale.ENGLISH).get(ActivityAttribute.candidates.name());
        Assert.assertTrue(candidates.contains("john"));
        Assert.assertTrue(candidates.contains("jack"));
    }
    
    @Test
    public void testGetActivityAttributesRemainingTime() throws Exception {
        ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
        
        ProcessDefinition simpleProcess = ProcessBuilder.createProcess("simple_process", "1.0")
        .addHuman("john")
        .addHumanTask("task1", "john")
        .addActivityExecutingTime(100000000L)
        .done();
        
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(simpleProcess);
        simpleProcess = managementAPI.deploy(businessArchive);
        
        ProcessInstanceUUID processInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(simpleProcess.getUUID());
        
        try {
            Collection<TaskInstance> tasks = AccessorUtil.getQueryRuntimeAPI().getTaskList(processInstanceUUID, ActivityState.READY);
            ActivityInstanceUUID uuid = null;
            for (TaskInstance activityInstance : tasks) {
                uuid = activityInstance.getUUID();
            }
            Assert.assertNotNull(uuid);

            IFormWorkflowAPI api = FormAPIFactory.getFormWorkflowAPI();
            String remainingTime = (String)api.getAttributes(uuid, Locale.ENGLISH).get(ActivityAttribute.remainingTime.name());
            Assert.assertTrue(remainingTime.contains("1day 3hours"));
        } finally {
            AccessorUtil.getManagementAPI().deleteProcess(simpleProcess.getUUID());
        }
    }

    private void login(String userName, String password) throws LoginException {
        loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(userName, password));
        loginContext.login();
    }
}
