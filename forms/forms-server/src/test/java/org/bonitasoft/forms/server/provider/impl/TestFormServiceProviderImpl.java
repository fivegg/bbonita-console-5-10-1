/**
 * Copyright (C) 2011 BonitaSoft S.A.
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
package org.bonitasoft.forms.server.provider.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bonitasoft.console.common.exception.NoCredentialsInSessionException;
import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.console.security.client.users.UserRights;
import org.bonitasoft.console.security.server.SimpleCallbackHandler;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.bonitasoft.forms.server.FormsTestCase;
import org.bonitasoft.forms.server.accessor.IApplicationFormDefAccessor;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.provider.FormServiceProvider;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderFactory;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.w3c.dom.Document;

/**
 * @author QiXiang Zhang
 * 
 */
public class TestFormServiceProviderImpl extends FormsTestCase {

    private ProcessDefinition bonitaProcess;

    private ProcessInstanceUUID processInstanceUUID;

    private ActivityInstanceUUID activityInstanceUUID;

    private LoginContext loginContext;

    @Before
    public void setUp() throws Exception {
        login("john", "bpm");
        ProcessDefinition myProcess = org.ow2.bonita.util.ProcessBuilder.createProcess("myProcess", "1.0").addHuman("john").addHumanTask("task1", "john").addHumanTask("task2", "john").addHumanTask("task3", "john")
                .addTransition("transition", "task1", "task2").addTransition("transition1", "task2", "task3").done();
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(myProcess);
        bonitaProcess = AccessorUtil.getManagementAPI().deploy(businessArchive);
        processInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(bonitaProcess.getUUID());
        activityInstanceUUID = AccessorUtil.getQueryRuntimeAPI().getOneTask(processInstanceUUID, ActivityState.READY);
    }

    private void login(String userName, String password) throws LoginException {
        loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(userName, password));
        loginContext.login();
    }

    @After
    public void tearDown() throws Exception {
        AccessorUtil.getRuntimeAPI().deleteAllProcessInstances(bonitaProcess.getUUID());
        AccessorUtil.getManagementAPI().disable(bonitaProcess.getUUID());
        AccessorUtil.getManagementAPI().deleteProcess(bonitaProcess.getUUID());

        loginContext.logout();

    }

    @Test
    public void testGetFormDefinitionDocument() throws Exception {
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.INSTANCE_UUID, processInstanceUUID.getValue());
        urlContext.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        FormDocument document = formServiceProvider.getFormDefinitionDocument(context);
        Assert.assertNotNull(document.getDocument());
    }

    @Test(expected = NoCredentialsInSessionException.class)
    public void testIsAllowed() throws Exception {
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.INSTANCE_UUID, processInstanceUUID.getValue());
        final boolean isUserAdmin = SecurityAPIFactory.getPreferencesAPI().isUserAdmin("john");
        final UserRights userRights = SecurityAPIFactory.getPrivilegesAPI().getUserRights("john");
        boolean useCredentialsTransmission = SecurityAPIFactory.getPreferencesAPI().getUseCredentialTransmissionPreference();
        User user = new User("john", isUserAdmin, Locale.ENGLISH.toString(), userRights, useCredentialsTransmission);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.USER, user);
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        boolean isAllowed1 = formServiceProvider.isAllowed(bonitaProcess.getUUID() + FormServiceProviderUtil.FORM_ID_SEPARATOR + FormServiceProviderUtil.RECAP_FORM_TYPE, FormServiceProviderUtil.INSTANCE_UUID + "#" + processInstanceUUID, "5.10", "5.10",
                context, true);
        boolean isAllowed2 = formServiceProvider.isAllowed(bonitaProcess.getUUID() + FormServiceProviderUtil.FORM_ID_SEPARATOR + FormServiceProviderUtil.RECAP_FORM_TYPE,
                FormServiceProviderUtil.PROCESS_UUID + "#" + processInstanceUUID.getProcessDefinitionUUID(), "5.10", "5.10", context, true);
        Assert.assertEquals(true, isAllowed1);
        Assert.assertEquals(true, isAllowed2);
        context.remove(FormServiceProviderUtil.USER);
        //expected = NoCredentialsInSessionException.class
        formServiceProvider.isAllowed(bonitaProcess.getUUID() + FormServiceProviderUtil.FORM_ID_SEPARATOR + FormServiceProviderUtil.RECAP_FORM_TYPE, FormServiceProviderUtil.PROCESS_UUID + "#" + processInstanceUUID.getProcessDefinitionUUID(), "5.7", "5.7", context, true);
    }

    @Test
    public void testResolveExpression() throws Exception {
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.INSTANCE_UUID, processInstanceUUID.getValue());
        urlContext.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        urlContext.put(FormServiceProviderUtil.IS_CURRENT_VALUE, true);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        Object result = formServiceProvider.resolveExpression("${return \"good\"}", context);
        Assert.assertEquals("good", result);
    }
    
    @Test
    public void testResolveExpressions() throws Exception {
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.INSTANCE_UUID, processInstanceUUID.getValue());
        urlContext.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        urlContext.put(FormServiceProviderUtil.IS_CURRENT_VALUE, true);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        Map<String, String> expressions = new HashMap<String, String>();
        expressions.put("1", "${return \"good\"}");
        expressions.put("2", "${return \"bad\"}");
        Map<String, Object> result = formServiceProvider.resolveExpressions(expressions, context);
        Assert.assertEquals("good", result.get("1"));
        Assert.assertEquals("bad", result.get("2"));
    }

    @Test
    public void testGetDeployementDate() throws Exception {
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.INSTANCE_UUID, processInstanceUUID.getValue());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        Date date = formServiceProvider.getDeployementDate(context);
        Assert.assertNotNull(date);
    }

    @Test
    public void testGetAttributesToInsert() throws Exception {
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.TASK_UUID, activityInstanceUUID);
        urlContext.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        Map<String, String> attributes = formServiceProvider.getAttributesToInsert(context);
        Assert.assertNotNull(attributes);

    }

    @Test
    public void testSkipForm() throws Exception {
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.TASK_UUID, activityInstanceUUID.toString());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        Map<String, Object> result = formServiceProvider.skipForm("myProcess--1.0--task1$entry", context);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetApplicationFormDefinitionFromXML() throws Exception {
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.TASK_UUID, activityInstanceUUID.toString());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("forms.xml");
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(inputStream);
        inputStream.close();
        FormDocument formDocument = new FormDocument(document);
        context.put(FormServiceProviderUtil.APPLICATION_DEPLOYMENT_DATE, bonitaProcess.getDeployedDate());
        context.put(FormServiceProviderUtil.IS_EDIT_MODE, true);
        IApplicationFormDefAccessor applicationFormDefAccessor = formServiceProvider.getApplicationFormDefinition("myProcess--1.0--task1$entry", formDocument, context);
        Assert.assertNull("the first page expression should be null because the entry form is empty in the forms.xml", applicationFormDefAccessor.getFirstPageExpression());
    }

    @Test
    public void testGetApplicationFormDefinitionFromEngine() throws Exception {
        AccessorUtil.getRuntimeAPI().executeTask(activityInstanceUUID, true);
        activityInstanceUUID = AccessorUtil.getQueryRuntimeAPI().getOneTask(processInstanceUUID, ActivityState.READY);
        final FormServiceProvider formServiceProvider = FormServiceProviderFactory.getFormServiceProvider();
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.TASK_UUID, activityInstanceUUID.toString());
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("forms.xml");
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(inputStream);
        inputStream.close();
        FormDocument formDocument = new FormDocument(document);
        context.put(FormServiceProviderUtil.APPLICATION_DEPLOYMENT_DATE, bonitaProcess.getDeployedDate());
        context.put(FormServiceProviderUtil.IS_EDIT_MODE, true);
        IApplicationFormDefAccessor applicationFormDefAccessor = formServiceProvider.getApplicationFormDefinition("myProcess--1.0--task2$entry", formDocument, context);
        Assert.assertNotNull("the first page expression should not be null because the entry form is not in the forms.xml, and so it should be generated from the engine", applicationFormDefAccessor.getFirstPageExpression());
    }

}
