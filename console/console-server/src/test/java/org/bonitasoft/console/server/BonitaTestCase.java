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
package org.bonitasoft.console.server;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.security.server.accessor.PropertiesFactory;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.bonitasoft.console.security.server.constants.WebBonitaConstants;
import org.bonitasoft.console.security.server.threadlocal.ThreadLocalManager;
import org.bonitasoft.console.server.users.UserDataStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.DbTool;
import org.ow2.bonita.util.ProcessBuilder;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class BonitaTestCase {

    protected static final String testUsername = "james";
    protected static final String adminUsername = "admin";
    protected static UserProfile testUserProfile;
    protected static UserProfile adminUserProfile;
    
    /**
     *Domain Name Value 
     */
    private static String DOMAINNAME = null;

    protected HttpServletRequest myRequest = new MockHttpServletRequest();

    protected ServletContext myServletContext = new MockServletContext();
    

    static {
        final String bonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        ThreadLocalManager.setDomain(DOMAINNAME);
        if (bonitaHome == null) {
            System.err.println("\n\n*** Forcing " + WebBonitaConstants.BONITA_HOME + " to target/bonita \n\n\n");
            System.setProperty(WebBonitaConstants.BONITA_HOME, "target/bonita");
        } else {
            System.err.println("\n\n*** " + WebBonitaConstants.BONITA_HOME + " already set to: " + bonitaHome + " \n\n\n");
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DbTool.recreateDb(BonitaConstants.DEFAULT_DOMAIN, "hibernate-configuration:core");
        DbTool.recreateDb(BonitaConstants.DEFAULT_DOMAIN, "hibernate-configuration:history");

        // Ensure the Console DB is in correct initial state for the users,
        // i.e., build the default user labels.
        Utils.login(testUsername, "bpm");
        testUserProfile = UserDataStore.getUserProfile(testUsername, false, "en", SecurityAPIFactory.getPrivilegesAPI().getUserRights(testUsername), false);
        Utils.logout();
        Utils.login(adminUsername, "bpm");
        adminUserProfile = UserDataStore.getUserProfile(adminUsername, true, "en", SecurityAPIFactory.getPrivilegesAPI().getUserRights(adminUsername), false);
        Utils.logout();
        // After this point I can log in and out to be in the same state as the
        // servlets.
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        if (Utils.isLogin()) {
            Utils.logout();
        }
    }

    @Test
    public void checkBonitaTest() {
        // just validate the setup teardown.
    }

    public LightProcessInstance deployAProcessAndStartAnInstance() throws Exception {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        final String theProcessName = sdf.format(new Date());

        ProcessDefinition theDateNamedProcess = ProcessBuilder.createProcess("nc" + theProcessName, "1.0").addHuman(adminUsername).addHumanTask("t", adminUsername).done();
        AccessorUtil.getManagementAPI().deploy(BusinessArchiveFactory.getBusinessArchive(theDateNamedProcess));

        final ProcessDefinitionUUID processUUID = theDateNamedProcess.getUUID();

        // Start a case.
        ProcessInstanceUUID theProcessInstanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(processUUID);
        return AccessorUtil.getQueryRuntimeAPI().getLightProcessInstance(theProcessInstanceUUID);
    }

    /**
     * Create a temporary text file with content.
     * 
     * @return
     */
    public File createTempFile(String aFileName) throws Exception {
        final File tmpFolder = PropertiesFactory.getTenancyProperties().getDomainXPTempFolder();
        final File theFile = new File(tmpFolder, aFileName);
        theFile.createNewFile();
        theFile.deleteOnExit();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(theFile);
            fos.write("Nicolas Chabanoles".getBytes());
        } finally {
            if (fos != null) {
                fos.close();
            }

        }
        return theFile;
    }

}
