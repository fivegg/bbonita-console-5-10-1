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
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.server.FormsTestCase;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormAdministrationAPI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * Unit test for the implementation of the administration definition API
 * @author Anthony Birembaut
 *
 */
public class TestFormAdministrationAPIImpl extends FormsTestCase {
    
    private ProcessDefinition bonitaProcess;

    private ProcessInstanceUUID processInstanceUUID;
    
    private Date deployementDate;

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
        deployementDate = bonitaProcess.getDeployedDate();
    }

    @After
    public void tearDown() throws Exception {

        AccessorUtil.getRuntimeAPI().deleteAllProcessInstances(bonitaProcess.getUUID());
        AccessorUtil.getManagementAPI().disable(bonitaProcess.getUUID());
        AccessorUtil.getManagementAPI().deleteProcess(bonitaProcess.getUUID());

        loginContext.logout();
    }
    
    @Test
    public void testGetProcessPageList() throws Exception {
        IFormAdministrationAPI api = FormAPIFactory.getFormAdministrationAPI(bonitaProcess.getUUID(), deployementDate);
        List<String> result = api.getPageList();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("0", result.get(0));
    }
    
    @Test
    public void testGetActivityPageList() throws Exception {
        IFormAdministrationAPI api = FormAPIFactory.getFormAdministrationAPI(bonitaProcess.getUUID(), deployementDate);
        List<String> result = api.getPageList("Approval");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("0", result.get(0));
    }
    
    @Test
    public void testGetProcessFormPage() throws Exception {
        IFormAdministrationAPI api = FormAPIFactory.getFormAdministrationAPI(bonitaProcess.getUUID(), deployementDate);
        FormPage result = api.getProcessFormPage("0");
        Assert.assertNotNull(result.getFormWidgets());
        Assert.assertEquals("ApprovalWorkflow_Applications", result.getFormWidgets().get(0).getId());
        Assert.assertNotNull(result.getPageValidators());
    }
    
    @Test
    public void testGetActivityFormPage() throws Exception {
        IFormAdministrationAPI api = FormAPIFactory.getFormAdministrationAPI(bonitaProcess.getUUID(), deployementDate);
        FormPage result = api.getFormPage("Approval", "0");
        Assert.assertNotNull(result.getFormWidgets());
        Assert.assertEquals("Approval_isGranted", result.getFormWidgets().get(0).getId());
        Assert.assertNotNull(result.getPageValidators());
    }
    
    @Test
    public void testGetProcessActions() throws Exception {
        IFormAdministrationAPI api = FormAPIFactory.getFormAdministrationAPI(bonitaProcess.getUUID(), deployementDate);
        List<FormAction> result = api.getProcessActions();
        Assert.assertEquals(1, result.size());
    }
    
    @Test
    public void testGetActivityActions() throws Exception {
        IFormAdministrationAPI api = FormAPIFactory.getFormAdministrationAPI(bonitaProcess.getUUID(), deployementDate);
        List<FormAction> result = api.getActions("Request");
        Assert.assertEquals(0, result.size());
        result = api.getActions("Approval");
        Assert.assertEquals(1, result.size());
    }
    
    @Test
    public void testGetFieldValue() throws Exception {
        QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
        RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
        Collection<TaskInstance> tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        for (TaskInstance activityInstance : tasks) {
            ActivityInstanceUUID uuid = activityInstance.getUUID();
            AccessorUtil.getRuntimeAPI().setProcessInstanceVariable(processInstanceUUID, "Applications", "Excel");
            AccessorUtil.getRuntimeAPI().executeTask(uuid, true);
        }
        ActivityInstanceUUID activityInstanceUUID = null;
        tasks = queryRuntimeAPI.getTaskList(processInstanceUUID, ActivityState.READY);
        for (TaskInstance activityInstance : tasks) {
            activityInstanceUUID = activityInstance.getUUID();
        }
        Assert.assertNotNull(activityInstanceUUID);
        IFormAdministrationAPI api = FormAPIFactory.getFormAdministrationAPI(bonitaProcess.getUUID(), deployementDate);
        FormPage page = api.getFormPage("Approval", "0");
        for(FormWidget formWidget: page.getFormWidgets()) {
            if (formWidget.getInitialValueExpression() != null) {
                Object value = runtimeAPI.evaluateGroovyExpression(formWidget.getInitialValueExpression(), activityInstanceUUID, true, true);
                FormFieldValue fieldValue = api.getFieldValue(value, formWidget, Locale.ENGLISH);
                Assert.assertEquals(value, fieldValue.getValue());
            }
        }
    }
    
    private void login(String userName, String password) throws LoginException {
        loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(userName, password));
        loginContext.login();
    }

}
