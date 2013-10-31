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
package org.bonitasoft.console.security.server.accessor;

import org.bonitasoft.console.security.server.constants.WebBonitaConstants;
import org.junit.Assert;
import org.junit.Test;


public class TestPreferencesProperties {
   
    static {
        final String bonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (bonitaHome == null) {
            System.err.println("\n\n*** Forcing " + WebBonitaConstants.BONITA_HOME + " to target/bonita \n\n\n");
            System.setProperty(WebBonitaConstants.BONITA_HOME, "target/bonita");
        } else {
            System.err.println("\n\n*** "+WebBonitaConstants.BONITA_HOME + " already set to: " + bonitaHome + " \n\n\n");
        }
    }
    
    @Test
    public void testSetProperty() throws Exception {
        PropertiesFactory.getPlatformProperties().setProperty("propertyName", "propertyValue");
        Assert.assertEquals("propertyValue", PropertiesFactory.getPlatformProperties().getProperty("propertyName"));
        PropertiesFactory.getPlatformProperties().setProperty("propertyName", "propertyValue2");
        Assert.assertEquals("propertyValue2", PropertiesFactory.getPlatformProperties().getProperty("propertyName"));
    }
    
    @Test
    public void testRemoveProperty() throws Exception {
        PropertiesFactory.getPlatformProperties().setProperty("propertyName", "propertyValue");
        Assert.assertEquals("propertyValue", PropertiesFactory.getPlatformProperties().getProperty("propertyName"));
        PropertiesFactory.getPlatformProperties().removeProperty("propertyName");
        Assert.assertNull(PropertiesFactory.getPlatformProperties().getProperty("propertyName"));
    }
}
