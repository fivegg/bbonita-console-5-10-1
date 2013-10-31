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
package org.bonitasoft.forms.server.accessor.impl.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginContext;

import org.bonitasoft.console.common.application.ApplicationResourcesUtils;
import org.bonitasoft.console.security.server.threadlocal.ThreadLocalManager;
import org.bonitasoft.forms.server.FormsTestCase;
import org.bonitasoft.forms.server.accessor.IApplicationFormDefAccessor;
import org.bonitasoft.forms.server.accessor.impl.XMLApplicationFormDefAccessorImpl;
import org.junit.Assert;
import org.junit.Test;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.ow2.bonita.util.ProcessBuilder;
import org.ow2.bonita.util.SimpleCallbackHandler;

/**
 * Test for the implementation of the form document builder
 * 
 * @author Anthony Birembaut
 * 
 */
public class TestFormDocumentBuilder extends FormsTestCase {

    @Test
    public void testInterpreteFormXMLWithI18n() throws Exception {
        FormDocument document = FormDocumentBuilderFactory.getFormDocumentBuilder(null, "fr", null).getDocument();

        IApplicationFormDefAccessor formDefAccessor = new XMLApplicationFormDefAccessorImpl("activity$Request", document, null, null);

        Assert.assertEquals("Commentaire", formDefAccessor.getPageWidgets("0").get(0).getTitle());
    }

    @Test
    public void testInterpreteFormXMLWithI18nDefault() throws Exception {
        FormDocument document = FormDocumentBuilderFactory.getFormDocumentBuilder(null, null, null).getDocument();

        IApplicationFormDefAccessor formDefAccessor = new XMLApplicationFormDefAccessorImpl("activity$Request", document, null, null);

        Assert.assertEquals("Comment", formDefAccessor.getPageWidgets("0").get(0).getTitle());
    }

    @Test
    public void testInterpreteFormXMLWithI18nNotExisting() throws Exception {
        FormDocument document = FormDocumentBuilderFactory.getFormDocumentBuilder(null, "de", null).getDocument();

        IApplicationFormDefAccessor formDefAccessor = new XMLApplicationFormDefAccessorImpl("activity$Request", document, null, null);

        Assert.assertEquals("Comment", formDefAccessor.getPageWidgets("0").get(0).getTitle());
    }

    @Test
    public void testExtractResourcesFromBar() throws Exception {

        LoginContext loginContext = new LoginContext("Bonita", new SimpleCallbackHandler("john", "bpm"));
        loginContext.login();

        ProcessDefinition simpleProcess = ProcessBuilder.createProcess("simple_process", "1.0").addHuman("john").addHumanTask("task1", "john").addActivityExecutingTime(100000000L).done();

        Map<String, byte[]> resources = new HashMap<String, byte[]>();

        InputStream inputStream = getClass().getResourceAsStream("/" + FormDocumentBuilder.FORM_DEFINITION_DEFAULT_FILE_NAME);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] content;
        try {
            int b = inputStream.read();
            while (b >= 0) {
                byteArrayOutputStream.write(b);
                b = inputStream.read();
            }
            byteArrayOutputStream.flush();
            content = byteArrayOutputStream.toByteArray();
        } finally {
            inputStream.close();
            byteArrayOutputStream.close();
        }
        resources.put(ApplicationResourcesUtils.FORMS_DIRECTORY_IN_BAR + "/" + FormDocumentBuilder.FORM_DEFINITION_DEFAULT_FILE_NAME, content);
        BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(simpleProcess, resources);
        ProcessDefinition bonitaProcess = AccessorUtil.getManagementAPI().deploy(businessArchive);

        try {
            final String domain = ThreadLocalManager.getDomain();
            FormDocumentBuilder.getInstance(bonitaProcess.getUUID(), "en", bonitaProcess.getDeployedDate(), domain, true).getDocument();

            File resourcesDir = ApplicationResourcesUtils.getApplicationResourceDir(bonitaProcess.getUUID(), bonitaProcess.getDeployedDate());
            File formsFile = new File(resourcesDir, FormDocumentBuilder.FORM_DEFINITION_DEFAULT_FILE_NAME);
            if (!formsFile.exists()) {
                Assert.fail();
            }

            ApplicationResourcesUtils.removeApplicationFiles(bonitaProcess.getUUID());
            if (formsFile.exists()) {
                Assert.fail();
            }
        } finally {
            AccessorUtil.getManagementAPI().deleteProcess(bonitaProcess.getUUID());
        }
        loginContext.logout();
    }
}
