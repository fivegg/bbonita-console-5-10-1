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
package org.bonitasoft.console.server.bam;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.client.reporting.ReportType;
import org.bonitasoft.console.server.bam.birt.BirtReportGenerator;

public class WebReport extends HttpServlet {

	protected static final long serialVersionUID = 1L;

	protected static final Logger LOGGER = Logger.getLogger(WebReport.class
			.getName());

	protected static final String REPORT_UUID_PARAMETER = "ReportId";
	protected static final String REPORT_TYPE_PARAMETER = "ReportType";
	protected static final String REPORT_SCOPE_PARAMETER = "ReportScope";
	protected static final String REPORT_CUSTOM_PARAMETER = "ReportCustom";

	public WebReport() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy();
		BirtReportGenerator.getInstance().stopBirt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		final String theReportId = req.getParameter(REPORT_UUID_PARAMETER);
		final String theReportType = req.getParameter(REPORT_TYPE_PARAMETER);
//		final String theReportScope = req.getParameter(REPORT_UUID_PARAMETER);
//		final String theReportSource = req.getParameter(REPORT_UUID_PARAMETER);
		if (isBirtReport(theReportType)) {
			BirtReportGenerator.getInstance().generateReport(req, resp,
			        theReportId,
					Thread.currentThread().getContextClassLoader());
		} else {
			if (theReportId == null) {
			    LOGGER.log(Level.SEVERE, "No report name specified.");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"No report name specified.");
			} else if (theReportType == null) {
                LOGGER.log(Level.SEVERE, "No report type specified.");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "No report type specified: expected BIRT.");
            } else {
			    LOGGER.log(Level.SEVERE, "Not supported file type: " + theReportType);
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Unsupported file type.");
			}
		}
	}

	protected boolean isBirtReport(String aReportType) {
		return (aReportType != null)
				&& (ReportType.valueOf(aReportType) == ReportType.BIRT);
	}

}
