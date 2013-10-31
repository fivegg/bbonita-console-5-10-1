/**
 * Copyright (C) 2010 BonitaSoft S.A.
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
package org.bonitasoft.forms.server;

import org.bonitasoft.console.security.server.constants.WebBonitaConstants;
import org.bonitasoft.console.security.server.threadlocal.ThreadLocalManager;
import org.junit.Assert;
import org.junit.Test;
import org.ow2.bonita.util.BonitaConstants;

/**
 * @author Nicolas Chabanoles
 */
public class FormsTestCase {
    private static String domain = null;
    static {
        ThreadLocalManager.setDomain(domain);
        // Initialize BONITA_HOME
        final String bonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (bonitaHome == null) {
            System.err.println("\n\n*** Forcing " + WebBonitaConstants.BONITA_HOME + " to target/bonita \n\n\n");
            System.setProperty(WebBonitaConstants.BONITA_HOME, "target/bonita");
        } else {
            System.err.println("\n\n*** " + WebBonitaConstants.BONITA_HOME + " already set to: " + bonitaHome + " \n\n\n");
        }

        // Initialize the Jaas login configuration with a default value
        final String defaultLoginFile = "src/test/resources/jaas-standard.cfg";
        final String loginFile = System.getProperty(BonitaConstants.JAAS_PROPERTY, defaultLoginFile);
        if (loginFile.equals(defaultLoginFile)) {
            System.setProperty(BonitaConstants.JAAS_PROPERTY, defaultLoginFile);
        }
    }
    
    
    @Test
    public void testIsBonitaHomeSet() throws Exception {
        final String bonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        Assert.assertNotNull("BONITA_HOME not set!", bonitaHome);
    }
    
    @Test 
    public void testIsDomainSet() throws Exception{
        final String domain = ThreadLocalManager.getDomain();
        Assert.assertEquals(this.domain, domain);
    }
    
}
