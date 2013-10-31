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
package org.bonitasoft.console.server.bam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportType;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.reporting.exceptions.ReportNotFoundException;
import org.bonitasoft.console.server.BonitaTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ReportingDataStoreTest extends BonitaTestCase {

    private static final ReportingDataStore dataStore = ReportingDataStore.getInstance();
    private static final String adminReportFileName = "admin.rptdesign";

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link org.bonitasoft.console.server.bam.ReportingDataStore#getItem(org.bonitasoft.console.client.users.UserProfile, org.bonitasoft.console.client.reporting.ReportUUID, org.bonitasoft.console.client.SimpleFilter)}
     * .
     */
    @Test
    public void testGetItemNominal() throws Exception {
        final ReportUUID theReportUUID = new ReportUUID(adminReportFileName);
        final SimpleFilter theReportFilter = new SimpleFilter(0, 20);
        final ReportItem theReport = dataStore.getItem(adminUserProfile, theReportUUID, theReportFilter, myServletContext);
        assertNotNull(theReport);
        assertEquals(adminReportFileName, theReport.getFileName());
        assertEquals(ReportType.BIRT, theReport.getType());
    }

    /**
     * Test method for
     * {@link org.bonitasoft.console.server.bam.ReportingDataStore#getItem(org.bonitasoft.console.client.users.UserProfile, org.bonitasoft.console.client.reporting.ReportUUID, org.bonitasoft.console.client.SimpleFilter)}
     * .
     */
    @Test(expected = ReportNotFoundException.class)
    public void testGetItemNotFound() throws Exception {
        final String theFileName = "non-existing-file";
        final ReportUUID theReportUUID = new ReportUUID(theFileName);
        final SimpleFilter theReportFilter = new SimpleFilter(0, 20);
        dataStore.getItem(adminUserProfile, theReportUUID, theReportFilter, myServletContext);
    }

    /**
     * Test method for
     * {@link org.bonitasoft.console.server.bam.ReportingDataStore#getItems(org.bonitasoft.console.client.users.UserProfile, org.bonitasoft.console.client.SimpleFilter)}
     * .
     * 
     * @throws ConsoleException
     */
    @Test
    public void testGetItemsUserProfileSimpleFilter() throws ConsoleException {
        final ReportFilter theReportFilter = new ReportFilter(0, 20);
        theReportFilter.setWithAdminRights(true);
        ItemUpdates<ReportItem> theReports = dataStore.getItems(adminUserProfile, theReportFilter, myServletContext);
        assertNotNull(theReports);
        assertTrue(theReports.getNbOfItems() > 0);
        boolean found = false;
        for (ReportItem theReport : theReports.getItems()) {
            if (adminReportFileName.equals(theReport.getFileName())) {
                found = true;
            }
        }
        assert (found);
    }

    /**
     * Test method for
     * {@link org.bonitasoft.console.server.bam.ReportingDataStore#getItems(org.bonitasoft.console.client.users.UserProfile, java.util.List, org.bonitasoft.console.client.SimpleFilter)}
     * .
     * @throws ReportNotFoundException 
     * @throws ConsoleException 
     * 
     * @throws ConsoleException
     */
    @Test
    public void testGetItemsUserProfileListOfReportUUIDSimpleFilter() throws ConsoleException, ReportNotFoundException {
        final SimpleFilter theReportFilter = new SimpleFilter(0, 20);
        final List<ReportUUID> theReportsUUIDs = Arrays.asList(new ReportUUID(adminReportFileName));
        List<ReportItem> theReports = dataStore.getItems(adminUserProfile, theReportsUUIDs, theReportFilter, myServletContext);
        assertNotNull(theReports);
        assertEquals(1, theReports.size());
        ReportItem theReport = theReports.get(0);
        assertEquals(adminReportFileName, theReport.getFileName());
    }

}
