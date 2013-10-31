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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bonitasoft.console.common.application.ApplicationResourcesUtils;
import org.bonitasoft.forms.client.model.ApplicationConfig;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.HtmlTemplate;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.server.FormsTestCase;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormDefinitionAPI;
import org.bonitasoft.forms.server.builder.IFormBuilder;
import org.bonitasoft.forms.server.builder.impl.FormBuilderImpl;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.ProcessBuilder;
import org.ow2.bonita.util.SimpleCallbackHandler;
import org.w3c.dom.Document;

/**
 * Unit test for the implementation of the form definition API
 * 
 * @author Anthony Birembaut, Haojie Yuan
 * 
 */
public class TestFormDefinitionAPIImpl extends FormsTestCase {

    private ProcessDefinition bonitaProcess;

    private Date deployementDate;

    private Map<String, Object> context = new HashMap<String, Object>();

    private LoginContext loginContext;

    private IFormBuilder formBuilder;

    private File complexProcessDefinitionFile;

    private FormDocument formDocument;

    private String formID = "processName--1.0$entry";

    private String pageID = "processPage1";

    @Before
    public void setUp() throws Exception {

        login("john", "bpm");

        formBuilder = FormBuilderImpl.getInstance();

        complexProcessDefinitionFile = buildComplexFormXML();
        InputStream inputStream = new FileInputStream(complexProcessDefinitionFile);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(inputStream);
        inputStream.close();
        formDocument = new FormDocument(document);
        
        Map<String, Object> urlContext = new HashMap<String, Object>();
        urlContext.put(FormServiceProviderUtil.PROCESS_UUID, "processName--1.0");
        urlContext.put(FormServiceProviderUtil.IS_EDIT_MODE, true);
        urlContext.put(FormServiceProviderUtil.DOCUMENT, document);
        urlContext.put(FormServiceProviderUtil.FORM_ID, formID);
        urlContext.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);
        urlContext.put(FormServiceProviderUtil.APPLICATION_DEPLOYMENT_DATE, deployementDate);
        urlContext.put(FormServiceProviderUtil.MODE, "form");
        urlContext.put(FormServiceProviderUtil.TRANSIENT_DATA_CONTEXT, context);
        context.put(FormServiceProviderUtil.URL_CONTEXT, urlContext);
        context.put(FormServiceProviderUtil.LOCALE, Locale.ENGLISH);

        ProcessDefinition iterationProcess = ProcessBuilder.createProcess("processName", "1.0").addObjectData("groupValuesList", List.class.getName()).addHuman("john").addHumanTask("task1", "john").done();

        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(iterationProcess);
        bonitaProcess = AccessorUtil.getManagementAPI().deploy(businessArchive);
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
    public void testGetProductVersion() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, null, Locale.ENGLISH.toString());
        String result = api.getProductVersion();
        Assert.assertNotNull(result);
        Assert.assertEquals("5.10", result);
    }

    @Test
    public void testGetApplicationPermissions() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, null, Locale.ENGLISH.toString());
        String result = api.getApplicationPermissions(formID, context);
        Assert.assertNotNull(result);
        Assert.assertEquals("application#test", result);
    }
    
    @Test
    public void testGetMigrationProductVersion() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, null, Locale.ENGLISH.toString());
        String result = api.getMigrationProductVersion(formID, context);
        Assert.assertNotNull(result);
        Assert.assertEquals("5.10", result);
    }

    @Test
    public void testGetExternalWelcomePage() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, null, null);
        String result = api.getExternalWelcomePage();
        Assert.assertNotNull(result);
        Assert.assertEquals("external-welcome-page", result);
    }

    @Test
    public void testGetWelcomePage() throws Exception {
        File welcomePageTemplate = null;
        try {
            ApplicationResourcesUtils.retrieveApplicationFiles(bonitaProcess.getUUID(), deployementDate);
            welcomePageTemplate = new File(ApplicationResourcesUtils.getApplicationResourceDir(bonitaProcess.getUUID(), deployementDate), "welcome-page");
            welcomePageTemplate.createNewFile();
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("html/request.html");        
            InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
            FileOutputStream fileOutputStream = new FileOutputStream(welcomePageTemplate);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            try {
                int character = streamReader.read();
                while (character != -1) {
                    streamWriter.write(character);
                    character = streamReader.read();
                }
            } finally {
                streamWriter.close();
                inputStream.close();
                streamReader.close();
                fileOutputStream.close();
            }  
            IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, deployementDate, Locale.ENGLISH.toString());
            HtmlTemplate result = api.getWelcomePage(new HashMap<String, Object>());
            Assert.assertNotNull(result);
        } finally {
            if (welcomePageTemplate != null && welcomePageTemplate.exists()) {
                welcomePageTemplate.delete();
            }
        }
    }

    @Test
    public void testGetFormFirstPage() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, deployementDate, Locale.ENGLISH.toString());
        String result = api.getFormFirstPage(formID, context);
        Assert.assertNotNull(result);
        Assert.assertEquals("processName--1.0--1", result);
    }

    @Test
    public void testGetFormPage() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, deployementDate, Locale.ENGLISH.toString());
        FormPage result = api.getFormPage(formID, pageID, context);
        Assert.assertNotNull(result);
        Assert.assertEquals("processPage1", result.getPageId());
    }

    @Test
    public void testGetApplicationConfig() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, deployementDate, Locale.ENGLISH.toString());
        ApplicationConfig result = api.getApplicationConfig(context, formID, false);
        Assert.assertNotNull(result);
        Assert.assertEquals("mandatory-label", result.getMandatoryLabel());
    }

    @Test
    public void testGetFormTransientData() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, deployementDate, Locale.ENGLISH.toString());
        List<TransientData> result = api.getFormTransientData(formID, context);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetFormActions() throws Exception {

        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, deployementDate, Locale.ENGLISH.toString());
        List<String> pageIds = new ArrayList<String>();
        pageIds.add(pageID);
        List<FormAction> result = api.getFormActions(formID, pageIds, context);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetFormConfirmationLayout() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, deployementDate, Locale.ENGLISH.toString());
        HtmlTemplate result = api.getFormConfirmationLayout(formID, context);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTransientDataContext() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, deployementDate, Locale.ENGLISH.toString());
        final List<TransientData> transientData = api.getFormTransientData(formID, context);
        Map<String, Object> result = api.getTransientDataContext(transientData, Locale.ENGLISH, context);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetApplicationErrorLayout() throws Exception {
        IFormDefinitionAPI api = FormAPIFactory.getFormDefinitionAPI(formDocument, deployementDate, Locale.ENGLISH.toString());
        HtmlTemplate result = api.getApplicationErrorLayout(context);
        Assert.assertNotNull(result);
    }

    
    private void login(String userName, String password) throws LoginException {
        loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(userName, password));
        loginContext.login();
    }

    private File buildComplexFormXML() throws Exception {
        formBuilder.createFormDefinition();
        formBuilder.addWelcomePage("welcome-page");
        formBuilder.addExternalWelcomePage("external-welcome-page");
        formBuilder.addMigrationProductVersion("5.6");
        formBuilder.addApplication("processName", "1.0");
        formBuilder.addLabel("process label with accents éèà");
        formBuilder.addLayout("/process-template.html");
        formBuilder.addPermissions("application#test");

        formBuilder.addEntryForm("processName--1.0$entry");
        formBuilder.addFirstPageId("processName--1.0--1");
        formBuilder.addPermissions("process#test1");
        formBuilder.addPage("processPage1");
        formBuilder.addLabel("page1 label");
        formBuilder.addLayout("/process-page1-template.html");
        formBuilder.addMandatoryLabel("mandatory-label");

        return formBuilder.done();
    }
}
