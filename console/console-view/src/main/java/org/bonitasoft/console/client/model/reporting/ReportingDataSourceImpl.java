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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.model.DefaultFilteredDataSourceImpl;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.SimpleSelection;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.ReportType;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.reporting.ReportingConfiguration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;

/**
 * @author Nicolas Chabanoles, Christophe Leroy
 * 
 */
public class ReportingDataSourceImpl extends DefaultFilteredDataSourceImpl<ReportUUID, ReportItem, ReportFilter> implements ReportingDataSource {

    protected List<String> myReportList;
    
    private AsyncHandler<Void> mySetDesignToDisplayInMonitoringViewHandler;
    
    private static final String REPORT_SEPARATOR = "##!##";
    private static final String PARAMS_SEPARATOR = ";;!;;";
    private static final String VALUE_SEPARATOR = "::!::";
    private static final String REPORT_PARAM_SEPARATOR = "==!==";
    private static final String COOKIE_NAME = "bonita_report";
    private List<HashMap<String, String>> reportsParameters;
    
    protected ReportingDataSourceImpl(MessageDataSource aMessageDataSource, ReportingData aReportingData) {
        super(aReportingData, new SimpleSelection<ReportUUID>(), aMessageDataSource);
        setItemFilter(new ReportFilter(0, 20));
        this.readCookie();
    }

    public ReportingDataSourceImpl(MessageDataSource aMessageDataSource) {
        this(aMessageDataSource, new ReportingData());
        this.readCookie();
    }

    @SuppressWarnings("unchecked")
    public void getReportingConfiguration(final AsyncHandler<ReportingConfiguration> aHandler) {
        ((ReportingData) myRPCItemData).getReportingConfiguration(new AsyncHandler<ReportingConfiguration>() {
            public void handleFailure(Throwable aT) {
                if (aT instanceof SessionTimeOutException) {
                    myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                } else if (aT instanceof ConsoleSecurityException) {
                    myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                }
                if (aT instanceof ConsoleException) {
                    if (myMessageDataSource != null) {
                        myMessageDataSource.addErrorMessage((ConsoleException) aT);
                    }
                }
                myMessageDataSource.addErrorMessage(messages.unableToUpdateConfiguration());
            }

            public void handleSuccess(ReportingConfiguration aResult) {
                try {
                    myChanges.fireModelChange(REPORTING_CONFIGURATION_PROPERTY, null, aResult);
                } catch (Exception e) {
                    GWT.log("Unable to fire reporting configuration changes", e);
                }
            }
        }, aHandler);
    }

    @SuppressWarnings("unchecked")
    public void updateReportingConfiguration(final ReportingConfiguration aReportingConfiguration, AsyncHandler<Void> aAsyncHandler) {
        ((ReportingData) myRPCItemData).updateReportingConfiguration(aReportingConfiguration, new AsyncHandler<Void>() {
            public void handleFailure(Throwable aT) {
                if (aT instanceof SessionTimeOutException) {
                    myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                } else if (aT instanceof ConsoleSecurityException) {
                    myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                }
                if (aT instanceof ConsoleException) {
                    if (myMessageDataSource != null) {
                        myMessageDataSource.addErrorMessage((ConsoleException) aT);
                    }
                }
                myMessageDataSource.addErrorMessage(messages.unableToUpdateConfiguration());
            }

            public void handleSuccess(Void anResult) {
                myMessageDataSource.addInfoMessage(messages.configurationUpdated());
                myChanges.fireModelChange(REPORTING_CONFIGURATION_PROPERTY, null, aReportingConfiguration);
            }
        }, aAsyncHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.bonitasoft.console.client.model.reporting.ReportingDataSource#
     * buildReportURL(org.bonitasoft.console.client.reporting.ReportItem,
     * org.bonitasoft.console.client.reporting.ReportScope)
     */
    public String buildReportURL(ReportItem aReportItem, ReportScope aScope) {
        final StringBuffer theURL = new StringBuffer(GWT.getModuleBaseURL()).append("bam/run?");
        if (aReportItem == null) {
            if (ReportScope.ADMIN == aScope) {
                theURL.append("ReportId=").append(ConsoleConstants.DEFAULT_ADMIN_REPORT_UUID.getValue());
            } else {
                theURL.append("ReportId=").append(ConsoleConstants.DEFAULT_REPORT_UUID.getValue());
            }
            theURL.append("&ReportType=").append(ReportType.BIRT.name());
            theURL.append("&ReportScope=").append(ReportScope.USER.name());
            theURL.append("&ReportCustom=").append(false);
        } else {
            theURL.append("ReportId=").append(aReportItem.getUUID());
            theURL.append("&ReportType=").append(aReportItem.getType().name());
            theURL.append("&ReportScope=").append(aReportItem.getScope().name());
            theURL.append("&ReportCustom=").append(aReportItem.isCustom());
        }
        theURL.append("&date=").append(new Date().getTime());

        return theURL.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.reporting.ReportingDataSourceExt#
     * listDesignToDisplayInMonitoringView
     * (org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @SuppressWarnings("unchecked")
    public void listDesignToDisplayInMonitoringView(AsyncHandler<List<ReportItem>> aHandler) {
        if (aHandler != null) {
            ((ReportingData) myRPCItemData).listDesignToDisplayInMonitoringView(myFilter, aHandler);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.reporting.ReportingDataSourceExt#
     * setDesignToDisplayInMonitoringView(java.util.List,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @SuppressWarnings("unchecked")
    public void setDesignToDisplayInMonitoringView(final List<ReportUUID> aNewList, final AsyncHandler<Void> aHandler) {
        if (mySetDesignToDisplayInMonitoringViewHandler == null) {

            mySetDesignToDisplayInMonitoringViewHandler = new AsyncHandler<Void>() {
                public void handleFailure(Throwable t) {
                    if (aHandler != null) {
                        aHandler.handleFailure(t);
                    }

                }

                public void handleSuccess(Void result) {
                    myChanges.fireModelChange(MONITORING_VIEW_CONFIGURATION_PROPERTY, null, aNewList);

                }
            };
        }
        ((ReportingData) myRPCItemData).setDesignToDisplayInMonitoringView(aNewList, mySetDesignToDisplayInMonitoringViewHandler, aHandler);

    }

    /* (non-Javadoc)
     * @see org.bonitasoft.console.client.model.reporting.ReportingDataSource#getParameters(java.lang.Integer)
     */
    @Override
    public HashMap<String, String> getParameters(Integer reportIndex) {
        if (reportIndex >= reportsParameters.size()) {
            return null;
        } else {
            return reportsParameters.get(reportIndex);
        }
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.console.client.model.reporting.ReportingDataSource#setParameters(java.lang.Integer, java.util.HashMap)
     */
    @Override
    public void setParameters(Integer reportIndex, HashMap<String, String> parameters) {
        if (reportIndex.intValue() >= reportsParameters.size()) {
            reportsParameters.add(parameters);
        } else {
            reportsParameters.remove(reportIndex.intValue());
            reportsParameters.add(reportIndex.intValue(), parameters);
        }
        updateCookie();
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.console.client.model.reporting.ReportingDataSource#removeReports(java.util.List)
     */
    @Override
    public void removeReports(List<Integer> reportIndexes) {
        for (Integer reportIndex : reportIndexes) {
            if (reportIndex.intValue() < reportsParameters.size()) {
                reportsParameters.remove(reportIndex.intValue());
            }
        }
        updateCookie();        
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.console.client.model.reporting.ReportingDataSource#swapReportParameters(int, int)
     */
    @Override
    public void swapReportParameters(int index1, int index2) {
        if (reportsParameters.get(index1) == null || reportsParameters.get(index2) == null) {
            GWT.log("bug in moveItemUp or in moveItemDown in MonitoringViewConfigurationWidget");
        } else {
            Collections.swap(reportsParameters, index1, index2);
        }        
    }
    
    /**
     * Serialize parameters of reports The format in the cookie is
     * reportId=paramName:value;paramName:value;...;paramName:value##reportId=...
     */
    private void updateCookie() {
        StringBuffer cookieValue = new StringBuffer();
        for (int i = 0; i < reportsParameters.size(); i++) {
            cookieValue.append(i);
            cookieValue.append(REPORT_PARAM_SEPARATOR);
            HashMap<String, String> parameters = reportsParameters.get(i);
            for (String paramName : parameters.keySet()) {
                cookieValue.append(paramName);
                cookieValue.append(VALUE_SEPARATOR);
                cookieValue.append(parameters.get(paramName));
                cookieValue.append(PARAMS_SEPARATOR);
            }
            cookieValue.append(REPORT_SEPARATOR);
        }
        String cookieStr = cookieValue.toString();
        Cookies.removeCookie(COOKIE_NAME);
        if (reportsParameters.size() > 0) {
//            Calendar cal = Calendar.getInstance();
//            cal.set(Calendar.YEAR, 9999);
            Date exp = new Date();
            exp.setYear(9999);
            Cookies.setCookie(COOKIE_NAME, cookieStr.substring(0, cookieStr.length() - REPORT_SEPARATOR.length()), exp);
        }
        GWT.log("updateCookie - cookieValue:" + cookieStr);
    }

    private void readCookie() {
        reportsParameters = new ArrayList<HashMap<String, String>>();
        String cookieValue = Cookies.getCookie(COOKIE_NAME);
        GWT.log("readCookie - cookieValue:" + cookieValue);
        if (cookieValue != null) {
            String[] reports = cookieValue.split(REPORT_SEPARATOR);
            for (String r : reports) {
                GWT.log("r:" + r);
                String[] report = r.split(REPORT_PARAM_SEPARATOR);
                HashMap<String, String> parameters = new HashMap<String, String>();
                if (report.length == 2) {
                    String[] params = report[1].split(PARAMS_SEPARATOR);
                    for (String p : params) {
                        String[] param = p.split(VALUE_SEPARATOR);
                        parameters.put(param[0], param[1]);
                    }
                }
                reportsParameters.add(Integer.valueOf(report[0]).intValue(), parameters);
            }
        }
    }    
}
