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
package org.bonitasoft.migration.forms;

import java.io.File;
import java.net.URL;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.bonitasoft.migration.forms.impl.FormMigrationImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * @author Qixiang Zhang
 * @version 
 */
public class TestFormMigrationImpl {
    
    private LoginContext loginContext;
    
    private ProcessDefinition bonitaProcess;
    
    static {
        final String bonitaHome = System.getProperty("BONITA_HOME");
        if (bonitaHome == null) {
            System.err.println("\n\n*** Forcing " + "BONITA_HOME" + " to target/bonita \n\n\n");
            System.setProperty("BONITA_HOME", "target/bonita");
        } else {
            System.err.println("\n\n*** " + "BONITA_HOME" + " already set to: " + bonitaHome + " \n\n\n");
        }
        
        // Initialize the Jaas login configuration with a default value
        final String defaultLoginFile = "src/test/resources/jaas-standard.cfg";
        final String loginFile = System.getProperty("java.security.auth.login.config", defaultLoginFile);
        if (loginFile.equals(defaultLoginFile)) {
            System.setProperty("java.security.auth.login.config", defaultLoginFile);
        }
    }

    
    @Before
    public void setUp() throws LoginException {
        login("john", "bpm");
    }
    
    @Test
    public void testReplaceFormsXMLFile() throws Exception {
        
        final URL testBarURL = getClass().getResource("/Web_Purchase--1.5.bar");
        if (testBarURL == null) {
            throw new RuntimeException("File Web_Purchase--1.5.bar was not found.");
        }
        final BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(new File(testBarURL.toURI()));
        bonitaProcess = AccessorUtil.getManagementAPI().deploy(businessArchive);
        final FormMigration formMigration = new FormMigrationImpl();
        
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        final String resource = "forms/forms.xml";
        final ProcessDefinitionUUID definitionUUID = new ProcessDefinitionUUID(bonitaProcess.getUUID().getValue());
        final byte[] oldFormsXMLContent = queryDefinitionAPI.getResource(definitionUUID, resource);
        
        formMigration.replaceFormsXMLFile(bonitaProcess.getUUID().getValue());
        
        final byte[] newFormsXMLContent = queryDefinitionAPI.getResource(definitionUUID, resource);
        //File tempFile = new File("forms.xml");
        //TransformFormXMLUtil.getFile(tempFile, newFormsXMLContent);
        Assert.assertFalse(oldFormsXMLContent.equals(newFormsXMLContent));
    }
    
    @After
    public void tearDown() throws Exception {

        AccessorUtil.getRuntimeAPI().deleteAllProcessInstances(bonitaProcess.getUUID());
        AccessorUtil.getManagementAPI().disable(bonitaProcess.getUUID());
        AccessorUtil.getManagementAPI().deleteProcess(bonitaProcess.getUUID());

        loginContext.logout();
    }
    
    private void login(String userName, String password) throws LoginException {
        loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(userName, password));
        loginContext.login();
    }
}
