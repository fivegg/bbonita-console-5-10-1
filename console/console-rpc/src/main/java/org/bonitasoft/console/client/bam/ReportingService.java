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
package org.bonitasoft.console.client.bam;

import java.util.List;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.reporting.ReportingConfiguration;
import org.bonitasoft.console.client.reporting.exceptions.ReportNotFoundException;

import com.google.gwt.user.client.rpc.RemoteService;

public interface ReportingService extends RemoteService {

	public ReportingConfiguration getReportingConfiguration() throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

	public void updateReportingConfiguration(ReportingConfiguration aConfiguration) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

	public ItemUpdates<ReportItem> listReports(ReportFilter anItemFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException;

    public ReportItem getReport(ReportUUID anItemUUID, ReportFilter aFilter) throws SessionTimeOutException, ConsoleSecurityException, ConsoleException, ReportNotFoundException;

    public List<ReportItem> listReports(List<ReportUUID> anItemSelection, ReportFilter aFilter) throws ReportNotFoundException, SessionTimeOutException, ConsoleSecurityException, ConsoleException;
    
    // Reporting Settings
    public List<ReportItem> listDesignToDisplayInMonitoringView(ReportFilter aFilter) throws SessionTimeOutException, ConsoleException, ConsoleSecurityException;

    public void setDesignToDisplayInMonitoringView(List<ReportUUID> aNewList) throws SessionTimeOutException, ConsoleException, ConsoleSecurityException;

}
