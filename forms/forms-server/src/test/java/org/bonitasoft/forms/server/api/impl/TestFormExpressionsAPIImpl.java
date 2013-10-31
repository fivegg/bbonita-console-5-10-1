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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.server.FormsTestCase;
import org.bonitasoft.forms.server.accessor.impl.EngineApplicationFormDefAccessorImpl;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormExpressionsAPI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.definition.InstanceInitiator;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition.JoinType;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.InitialAttachment;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.ProcessBuilder;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * Unit test for the implementation of the form expressions API
 * @author Anthony Birembaut
 *
 */
public class TestFormExpressionsAPIImpl extends FormsTestCase {
    
    private ProcessDefinition bonitaProcess;

    private ProcessInstanceUUID processInstanceUUID;

    private LoginContext loginContext;

    @Before
    public void setUp() throws Exception {

        login("john", "bpm");

        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(getAWProcess(), InstanceInitiator.class);
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
    
    protected ProcessDefinition getAWProcess() {
        Set<String> applications = new HashSet<String>();
        applications.add("Word");
        applications.add("Excel");
        applications.add("MailReader");
        applications.add("WebBrowser");

        ProcessDefinition process =
          ProcessBuilder.createProcess("ApprovalWorkflow", null)

          .addEnumData("Applications", applications, "Word")

          .addGroup("User")
          .addGroupResolver(InstanceInitiator.class.getName())
          .addHuman("john")
          
          .addSystemTask("BonitaStart")
          .addSystemTask("BonitaEnd")
            .addJoinType(JoinType.XOR)
          .addHumanTask("Request", "User")
          .addHumanTask("Approval", "john")
            .addBooleanData("isGranted", false)
          
          .addTransition("Request_Approval", "Request", "Approval")
          .addTransition("Approval_End", "Approval", "BonitaEnd")
          .addTransition("Start_Request", "BonitaStart", "Request")
          .done();
        return process;
    }
    
    @Test
    public void testExecuteSetVariableActivityAction() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        FormAction formAction = new FormAction(ActionType.SET_VARIABLE, "Applications", EngineApplicationFormDefAccessorImpl.PROCESS_VARIABLE, "field_ApprovalWorkflow_Applications", null);
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        fieldValues.put("ApprovalWorkflow_Applications", new FormFieldValue("Excel", String.class.getName()));
        api.executeAction(uuid, formAction, fieldValues, Locale.ENGLISH);
        Object variableValue = queryRuntimeAPI.getProcessInstanceVariable(processInstanceUUID, "Applications");
        Assert.assertEquals("Excel", variableValue.toString());
    }
    
    @Test
    public void testExecuteSetVariableActivityActionWithGroovy() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        FormAction formAction = new FormAction(ActionType.SET_VARIABLE, "${\"Applications\"}", EngineApplicationFormDefAccessorImpl.PROCESS_VARIABLE, "field_ApprovalWorkflow_Applications", null);
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        fieldValues.put("ApprovalWorkflow_Applications", new FormFieldValue("Excel", String.class.getName()));
        api.executeAction(uuid, formAction, fieldValues, Locale.ENGLISH);
        Object variableValue = queryRuntimeAPI.getProcessInstanceVariable(processInstanceUUID, "Applications");
        Assert.assertEquals("Excel", variableValue.toString());
    }
    
    @Test
    public void testExecuteSetVariableInstanceAction() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        FormAction formAction = new FormAction(ActionType.SET_VARIABLE, "Applications", EngineApplicationFormDefAccessorImpl.PROCESS_VARIABLE, "field_ApprovalWorkflow_Applications", null);
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        fieldValues.put("ApprovalWorkflow_Applications", new FormFieldValue("Excel", String.class.getName()));
        api.executeAction(processInstanceUUID, formAction, fieldValues, Locale.ENGLISH);
        Object variableValue = queryRuntimeAPI.getProcessInstanceVariable(processInstanceUUID, "Applications");
        Assert.assertEquals("Excel", variableValue.toString());
    }
    
    @Test
    public void testExecuteSetAttachmentActivityAction() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        FormAction formAction = new FormAction(ActionType.SET_ATTACHMENT, null, null, "field_upload", null, "attachment");
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        File attachmentFile = null;
        AttachmentInstance attachmentValue = null;
        try {
            attachmentFile = File.createTempFile("attachment-test",".txt.10");
            FileOutputStream fileOutputStream = new FileOutputStream(attachmentFile);
            fileOutputStream.write("test".getBytes("UTF-8"));
            fileOutputStream.close();
            fieldValues.put("upload", new FormFieldValue(attachmentFile.getPath(), File.class.getName()));
            api.executeAction(uuid, formAction, fieldValues, Locale.ENGLISH);
            attachmentValue = queryRuntimeAPI.getLastAttachment(processInstanceUUID, "attachment");
            String attachmentContent = new String(queryRuntimeAPI.getAttachmentValue(attachmentValue), "UTF-8");
            Assert.assertEquals("test", attachmentContent);
            Assert.assertEquals(attachmentFile.getName().substring(0, attachmentFile.getName().length() - 3), attachmentValue.getFileName());
        } finally {
            attachmentFile.delete();
            runtimeAPI.deleteDocuments(true, attachmentValue.getUUID());
        }
    }
    
    @Test
    public void testExecuteSetAttachmentActivityActionWithGroovy() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        FormAction formAction = new FormAction(ActionType.SET_ATTACHMENT, null, null, "field_upload", null, "${\"attachment\"}");
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        File attachmentFile = null;
        AttachmentInstance attachmentValue = null;
        try {
            attachmentFile = File.createTempFile("attachment-test",".txt.10");
            FileOutputStream fileOutputStream = new FileOutputStream(attachmentFile);
            fileOutputStream.write("test".getBytes("UTF-8"));
            fileOutputStream.close();
            fieldValues.put("upload", new FormFieldValue(attachmentFile.getPath(), File.class.getName()));
            api.executeAction(uuid, formAction, fieldValues, Locale.ENGLISH);
            attachmentValue = queryRuntimeAPI.getLastAttachment(processInstanceUUID, "attachment");
            String attachmentContent = new String(queryRuntimeAPI.getAttachmentValue(attachmentValue), "UTF-8");
            Assert.assertEquals("test", attachmentContent);
            Assert.assertEquals(attachmentFile.getName().substring(0, attachmentFile.getName().length() - 3), attachmentValue.getFileName());
        } finally {
            attachmentFile.delete();
            runtimeAPI.deleteDocuments(true, attachmentValue.getUUID());
        }
    }
    
    @Test
    public void testExecuteScriptActivityAction() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        FormAction formAction = new FormAction(ActionType.EXECUTE_SCRIPT, null, null, "${Applications = field_ApprovalWorkflow_Applications;}", null);
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        fieldValues.put("ApprovalWorkflow_Applications", new FormFieldValue("Excel", String.class.getName()));
        api.executeAction(uuid, formAction, fieldValues, Locale.ENGLISH);
        Object variableValue = queryRuntimeAPI.getProcessInstanceVariable(processInstanceUUID, "Applications");
        Assert.assertEquals("Excel", variableValue.toString());
    }
    
    @Test
    public void testExecuteSetVariableProcessAction() throws Exception {
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        FormAction formAction = new FormAction(ActionType.SET_VARIABLE, "Applications", EngineApplicationFormDefAccessorImpl.PROCESS_VARIABLE, "field_ApprovalWorkflow_Applications", null);
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        fieldValues.put("ApprovalWorkflow_Applications", new FormFieldValue("Excel", String.class.getName()));
        Map<String, Object> variableValues = new HashMap<String, Object>();
        Set<InitialAttachment> attachments = new HashSet<InitialAttachment>();
        api.executeAction(bonitaProcess.getUUID(), formAction, variableValues, attachments, fieldValues, Locale.ENGLISH);
        Object variableValue = variableValues.get("Applications");
        Assert.assertEquals("Excel", variableValue.toString());
    }
    
    @Test
    public void testExecuteSetAttachmentProcessAction() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        FormAction formAction = new FormAction(ActionType.SET_ATTACHMENT, null, null, "field_upload", null, "attachment");
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        File attachmentFile = null;
        AttachmentInstance attachmentValue = null;
        try {
            attachmentFile = File.createTempFile("attachment-test",".txt");
            FileOutputStream fileOutputStream = new FileOutputStream(attachmentFile);
            fileOutputStream.write("test".getBytes("UTF-8"));
            fileOutputStream.close();
            fieldValues.put("upload", new FormFieldValue(attachmentFile.getPath(), File.class.getName()));
            Map<String, Object> variableValues = new HashMap<String, Object>();
            Set<InitialAttachment> attachments = new HashSet<InitialAttachment>();
            api.executeAction(bonitaProcess.getUUID(), formAction, variableValues, attachments, fieldValues, Locale.ENGLISH);
            ProcessInstanceUUID processInstanceUUID = runtimeAPI.instantiateProcess(bonitaProcess.getUUID(), variableValues, attachments);
            attachmentValue = queryRuntimeAPI.getLastAttachment(processInstanceUUID, "attachment");
            String attachmentContent = new String(queryRuntimeAPI.getAttachmentValue(attachmentValue), "UTF-8");
            Assert.assertEquals("test", attachmentContent);
            Assert.assertEquals(attachmentFile.getName(), attachmentValue.getFileName());
        } finally {
            attachmentFile.delete();
            runtimeAPI.deleteDocuments(true, attachmentValue.getUUID());
        }
    }
    
    @Test
    public void testExecuteSetNullAttachmentProcessAction() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        FormAction formAction = new FormAction(ActionType.SET_ATTACHMENT, null, null, "field_upload", null, "attachment");
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        fieldValues.put("upload", new FormFieldValue("", File.class.getName()));
        Map<String, Object> variableValues = new HashMap<String, Object>();
        Set<InitialAttachment> attachments = new HashSet<InitialAttachment>();
        AttachmentInstance attachmentValue = null;
        try {
            api.executeAction(bonitaProcess.getUUID(), formAction, variableValues, attachments, fieldValues, Locale.ENGLISH);
            ProcessInstanceUUID processInstanceUUID = runtimeAPI.instantiateProcess(bonitaProcess.getUUID(), variableValues, attachments);
            attachmentValue = queryRuntimeAPI.getLastAttachment(processInstanceUUID, "attachment");
            byte[] attachmentContent = queryRuntimeAPI.getAttachmentValue(attachmentValue);
            Assert.assertNull(attachmentContent);
            Assert.assertNull(attachmentValue.getFileName());
        } finally {
            runtimeAPI.deleteDocuments(true, attachmentValue.getUUID());
        }
    }
    
    @Test
    public void testExecuteScriptProcessAction() throws Exception {
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        String groovyScript = "${import java.io.File;\n" 
            + "new File(\"target/\" + field_ApprovalWorkflow_Applications).createNewFile();\n"
            + "new File(\"target/\" + Applications).createNewFile();}";
        FormAction formAction = new FormAction(ActionType.EXECUTE_SCRIPT, null, null, groovyScript, null);
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        fieldValues.put("ApprovalWorkflow_Applications", new FormFieldValue("Excel", String.class.getName()));
        Map<String, Object> variableValues = new HashMap<String, Object>();
        Set<InitialAttachment> attachments = new HashSet<InitialAttachment>();
        api.executeAction(bonitaProcess.getUUID(), formAction, variableValues, attachments, fieldValues, Locale.ENGLISH);
        File fieldValueFile = new File("target/Excel");
        Assert.assertTrue(fieldValueFile.exists());
        fieldValueFile.delete();
        File variableValueFile = new File("target/Word");
        Assert.assertTrue(variableValueFile.exists());
        variableValueFile.delete();
    }
    
    @Test
    public void testEvaluateInitialExpressionOnTerminatedTask() throws Exception {
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        AccessorUtil.getRuntimeAPI().setVariable(uuid, "Applications", "Excel");
        AccessorUtil.getRuntimeAPI().executeTask(uuid, true);

        tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID newActivityUUID = null;
        for (TaskInstance activityInstance : tasks) {
            newActivityUUID = activityInstance.getUUID();
        }
        Assert.assertNotNull(newActivityUUID);
        AccessorUtil.getRuntimeAPI().setVariable(newActivityUUID, "Applications", "MailReader");
        Object result = api.evaluateInitialExpression(newActivityUUID, "${Applications}", Locale.ENGLISH, false);
        Assert.assertEquals("MailReader", result.toString());
        AccessorUtil.getRuntimeAPI().executeTask(newActivityUUID, true);

        result = api.evaluateInitialExpression(uuid, "${Applications}", Locale.ENGLISH, false);
        Assert.assertEquals("Excel", result.toString());
    }
    
    @Test
    public void testEvaluateInitialExpressionOnTerminatedInstance() throws Exception {
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
        
        ProcessDefinition simpleProcess = ProcessBuilder.createProcess("simple_process", "1.0")
        .addHuman("john")
        .addIntegerData("stringData1")
        .addHumanTask("task", "john")
        .addStringData("stringData2")
        .done();

        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(simpleProcess);
        simpleProcess = managementAPI.deploy(businessArchive);
        
        ProcessInstanceUUID instanceUUID = runtimeAPI.instantiateProcess(simpleProcess.getUUID());
        try {
	        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
	        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
	        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(instanceUUID, ActivityState.READY);
	        ActivityInstanceUUID uuid = null;
	        for (TaskInstance activityInstance : tasks) {
	            uuid = activityInstance.getUUID();
	        }
	        Assert.assertNotNull(uuid);
	        runtimeAPI.setVariable(uuid, "stringData1", "test1");
	        runtimeAPI.setVariable(uuid, "stringData2", "test2");
	        runtimeAPI.executeTask(uuid, true);
	
	        Object result = api.evaluateExpression(instanceUUID, "${stringData1}", new HashMap<String, FormFieldValue>(), Locale.ENGLISH, true, new HashMap<String, Object>());
	        Assert.assertEquals("test1", result.toString());
	
	        result = api.evaluateExpression(uuid, "${stringData2}", new HashMap<String, FormFieldValue>(), Locale.ENGLISH, true, new HashMap<String, Object>());
	        Assert.assertEquals("test2", result.toString());
	    } finally {
	        runtimeAPI.deleteProcessInstance(instanceUUID);
	        managementAPI.deleteProcess(simpleProcess.getUUID());
	    }
    }
    
    @Test
    public void testEvaluateInitialProcessExpression() throws Exception {
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        Object result = api.evaluateInitialExpression(processInstanceUUID, "${Applications}", Locale.ENGLISH, true);
        Assert.assertEquals("Word", result.toString());
    }
    
    @Test
    public void testEvaluateInitialProcessDefinitionExpression() throws Exception {
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        Object result = api.evaluateInitialExpression(bonitaProcess.getUUID(), "${Applications}", Locale.ENGLISH);
        Assert.assertEquals("Word", result.toString());
    }
    
    @Test
    public void testEvaluateInitialProcessDefinitionExpressionWithGroovyInitialisedVariable() throws Exception {
        ProcessDefinition process = ProcessBuilder.createProcess("process", "1.0")
        .addStringDataFromScript("data1", "${\"test\" + \"1\"}")
        .addHuman("john")
        .addHumanTask("task1", "john")
        .done();
        
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(process);
        process = AccessorUtil.getManagementAPI().deploy(businessArchive);
        
        try {
            IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
            Object result = api.evaluateInitialExpression(process.getUUID(), "${data1}", Locale.ENGLISH);
            Assert.assertNotNull(result);
            Assert.assertEquals("test1", result.toString());
        } finally {
            AccessorUtil.getManagementAPI().deleteProcess(process.getUUID());
        }
    }
    
    @Test
    public void testEvaluateInitialActivityExpression() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        AccessorUtil.getRuntimeAPI().executeTask(uuid, true);
        AccessorUtil.getRuntimeAPI().setVariable(uuid, "Applications", "Excel");
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        Object result = api.evaluateInitialExpression(uuid, "${Applications}", Locale.ENGLISH, false);
        Assert.assertEquals("Word", result.toString());
    }
    
    @Test
    public void testEvaluateInitialActivityExpressionCurrentValue() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        AccessorUtil.getRuntimeAPI().executeTask(uuid, true);
        AccessorUtil.getRuntimeAPI().setVariable(uuid, "Applications", "Excel");
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        Object result = api.evaluateInitialExpression(uuid, "${Applications}", Locale.ENGLISH, true);
        Assert.assertEquals("Excel", result.toString());
    }
    
    @Test
    public void testEvaluateInitialProcessValuesExpression() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        ActivityInstanceUUID uuid = null;
        for (TaskInstance activityInstance : tasks) {
            uuid = activityInstance.getUUID();
        }
        Assert.assertNotNull(uuid);
        AccessorUtil.getRuntimeAPI().setVariable(uuid, "Applications", "Excel");
        AccessorUtil.getRuntimeAPI().executeTask(uuid, true);
        
        IFormExpressionsAPI api = FormAPIFactory.getFormExpressionsAPI();
        Object result = api.evaluateInitialExpression(processInstanceUUID, "${Applications}", Locale.ENGLISH, false);
        Assert.assertEquals("Word", result.toString());
    }
    
    private void login(String userName, String password) throws LoginException {
        loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(userName, password));
        loginContext.login();
    }
}
