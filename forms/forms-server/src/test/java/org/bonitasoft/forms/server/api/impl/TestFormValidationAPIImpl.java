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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.server.FormsTestCase;
import org.bonitasoft.forms.server.api.FormAPIFactory;
import org.bonitasoft.forms.server.api.IFormValidationAPI;
import org.bonitasoft.forms.server.validator.CharFieldValidator;
import org.bonitasoft.forms.server.validator.DateOrderTestPageValidator;
import org.bonitasoft.forms.server.validator.InstanceUUIDTestFieldValidator;
import org.bonitasoft.forms.server.validator.RegexFieldValidator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * Unit test for the implementation of the form validation API
 * @author Anthony Birembaut
 *
 */
public class TestFormValidationAPIImpl extends FormsTestCase {

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
    public void testValidateField() throws Exception {
        IFormValidationAPI api = FormAPIFactory.getFormValidationAPI();
        List<FormValidator> validators = new ArrayList<FormValidator>();
        validators.add(new FormValidator("validatorId", CharFieldValidator.class.getName(), "validator label", ""));
        FormFieldValue value = new FormFieldValue("a", String.class.getName());
        Assert.assertEquals(0, api.validateField(processInstanceUUID, validators, "field", value, null, Locale.ENGLISH, new HashMap<String, Object>()).size());
    }
    
    @Test
    public void testValidateFieldUsingInstanceUUID() throws Exception {
        IFormValidationAPI api = FormAPIFactory.getFormValidationAPI();
        List<FormValidator> validators = new ArrayList<FormValidator>();
        validators.add(new FormValidator("validatorId", InstanceUUIDTestFieldValidator.class.getName(), "validator label", ""));
        FormFieldValue value = new FormFieldValue(processInstanceUUID.getValue(), String.class.getName());
        Assert.assertEquals(0, api.validateField(processInstanceUUID, validators, "field", value, null, Locale.ENGLISH, new HashMap<String, Object>()).size());
    }
    
    @Test
    public void testValidateFieldWithRegex() throws Exception {
        IFormValidationAPI api = FormAPIFactory.getFormValidationAPI();
        List<FormValidator> validators = new ArrayList<FormValidator>();
        validators.add(new FormValidator("validatorId", RegexFieldValidator.class.getName(), "validator label", null, "[a-z0-9_]*"));
        FormFieldValue value = new FormFieldValue("abc123_def", null);
        Assert.assertEquals(0, api.validateField(processInstanceUUID, validators, "field", value, null, Locale.ENGLISH, new HashMap<String, Object>()).size());
        validators = new ArrayList<FormValidator>();
        validators.add(new FormValidator("validatorId", RegexFieldValidator.class.getName(), "validator label", null, "[a-z_]*"));
        Assert.assertEquals(1, api.validateField(processInstanceUUID, validators, "field", value, null, Locale.ENGLISH, new HashMap<String, Object>()).size());
    }
    
    @Test
    public void testValidatePage() throws Exception {
        IFormValidationAPI api = FormAPIFactory.getFormValidationAPI();
        List<FormValidator> validators = new ArrayList<FormValidator>();
        validators.add(new FormValidator("validatorId", DateOrderTestPageValidator.class.getName(), "validator label", ""));
        Map<String, FormFieldValue> fieldValues = new HashMap<String, FormFieldValue>();
        FormFieldValue value1 = new FormFieldValue("07-15-2009", Date.class.getName(), "mm-dd-yyyy");
        fieldValues.put("fieldId1", value1);
        FormFieldValue value2 = new FormFieldValue("06-10-2010", Date.class.getName(), "mm-dd-yyyy");
        fieldValues.put("fieldId2", value2);
        Assert.assertEquals(0, api.validatePage(processInstanceUUID, validators, fieldValues, null, Locale.ENGLISH, new HashMap<String, Object>()).size());
    }

    private void login(String userName, String password) throws LoginException {
        loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(userName, password));
        loginContext.login();
    }
}
