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

import java.util.Collection;
import java.util.List;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.reporting.ReportingConfiguration;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ReportingData implements RPCData<ReportUUID, ReportItem, ReportFilter> {

    public void updateReportingConfiguration(ReportingConfiguration aReportingConfiguration, AsyncHandler<Void>... handlers) {
        GWT.log("RPC: updateReportingConfiguration");
        RpcConsoleServices.getReportingService().updateReportingConfiguration(aReportingConfiguration, new ChainedCallback<Void>(handlers));
    }

    public void getReportingConfiguration(AsyncHandler<ReportingConfiguration>... handlers) {
        GWT.log("RPC: getReportingConfiguration");
        RpcConsoleServices.getReportingService().getReportingConfiguration(new ChainedCallback<ReportingConfiguration>(handlers));
    }

    public void getAllItems(ReportFilter anItemFilter, AsyncHandler<ItemUpdates<ReportItem>>... handlers) {
        GWT.log("RPC: list reports");
        RpcConsoleServices.getReportingService().listReports(anItemFilter, new ChainedCallback<ItemUpdates<ReportItem>>(handlers));
    }

    public void getItem(ReportUUID anItemUUID, ReportFilter aFilter, AsyncHandler<ReportItem>... handlers) {
        GWT.log("RPC: get report");
        RpcConsoleServices.getReportingService().getReport(anItemUUID, aFilter, new ChainedCallback<ReportItem>(handlers));
    }

    public void getItems(List<ReportUUID> anItemSelection, ReportFilter aFilter, AsyncHandler<List<ReportItem>>... handlers) {
        GWT.log("RPC: list reports");
        RpcConsoleServices.getReportingService().listReports(anItemSelection, aFilter, new ChainedCallback<List<ReportItem>>(handlers));
    }

    public void addItem(ReportItem anItem, ReportFilter aFilter, AsyncHandler<ItemUpdates<ReportItem>>... handlers) {
        GWT.log("RPC: addItem report not supported.");
    }

    public void deleteItems(Collection<ReportUUID> anItemSelection, ReportFilter anItemFilter, AsyncHandler<ItemUpdates<ReportItem>>... handlers) {
        GWT.log("RPC: deleteItems report not supported.");
    }

    public void updateItem(ReportUUID anItemId, ReportItem anItem, AsyncHandler<ReportItem>... handlers) {
        GWT.log("RPC: update report not supported.");
    }
    
    //Monitoring view
    public void listDesignToDisplayInMonitoringView(ReportFilter aFilter, final AsyncHandler<List<ReportItem>>... handlers) {
        GWT.log("RPC: listDesignToDisplayInMonitoringView");
        RpcConsoleServices.getReportingService().listDesignToDisplayInMonitoringView(aFilter, new ChainedCallback<List<ReportItem>>(handlers));
    }

    public void setDesignToDisplayInMonitoringView(final List<ReportUUID> aNewList, final AsyncHandler<Void>... handlers) {
        GWT.log("RPC: setDesignToDisplayInMonitoringView");
        RpcConsoleServices.getReportingService().setDesignToDisplayInMonitoringView(aNewList, new ChainedCallback<Void>(handlers));
    }
}
