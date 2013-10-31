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
package org.bonitasoft.console.client.model.reporting;

import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.reporting.ReportingConfiguration;

/**
 * @author Nicolas Chabanoles, Christophe Leroy
 * 
 */
public interface ReportingDataSource extends BonitaFilteredDataSource<ReportUUID, ReportItem, ReportFilter> {

    public static final String REPORTING_CONFIGURATION_PROPERTY = "reporting configuration";
    public static final String MONITORING_VIEW_CONFIGURATION_PROPERTY = "monitoring view configuration";
    
    public void getReportingConfiguration(final AsyncHandler<ReportingConfiguration> aHandler);

    public void updateReportingConfiguration(ReportingConfiguration aReportingConfiguration, AsyncHandler<Void> aAsyncHandler);

    public String buildReportURL(ReportItem aReportItem, ReportScope aScope);

    /**
     * List reports to display in monitoring view.
     */
    public void listDesignToDisplayInMonitoringView(final AsyncHandler<List<ReportItem>> aHandler);
    
    /**
     * Update the configuration of the monitoring view.
     */
    public void setDesignToDisplayInMonitoringView(final List<ReportUUID> aNewList, final AsyncHandler<Void> aHandler);
    
    /**
     * Get parameters of the report from a cookie
     * @param reportIndex position of the report in the MonitoringView page
     */
    public HashMap<String, String> getParameters(Integer reportIndex);
    
    /**
     * Set parameters to a cookie
     * @param reportIndex position of the report in the MonitoringView page
     * @param parameters
     */
    public void setParameters(Integer reportIndex, HashMap<String, String> parameters);
    
    /**
     * Remove reports reference in the cookie
     * @param reportIndexes  position of reports in the MonitoringView page to remove
     */
    public void removeReports(List<Integer> reportIndexes);
    
    /**
     * Swap the place of parameters in the cookie
     * @param index1
     * @param index2
     */
    public void swapReportParameters(int index1, int index2);
}
