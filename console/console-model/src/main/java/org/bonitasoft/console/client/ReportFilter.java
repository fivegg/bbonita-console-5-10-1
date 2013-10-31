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
package org.bonitasoft.console.client;

import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.ReportType;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ReportFilter extends SimpleFilter {

    private static final long serialVersionUID = -8630217120087808359L;
    protected ReportScope myScope;
    protected ReportType myType;
    protected boolean customReportsOnly;

    /**
     * Default constructor.
     */
    public ReportFilter() {
        super();
    }

    public ReportFilter(final int aStartingIndex, final int aPageSize) {
        super(aStartingIndex, aPageSize);
        myScope = ReportScope.ADMIN;
        myType = ReportType.BIRT;
        customReportsOnly = true;
    }

    @Override
    public ItemFilter createFilter() {
        ReportFilter theResult = new ReportFilter();
        theResult.updateFilter(this);
        return theResult;
    }

    public void updateFilter(ReportFilter aFilter) {
        super.updateFilter(aFilter);
        myScope = aFilter.getScope();
        myType = aFilter.getType();
        customReportsOnly = aFilter.isCustomReportsOnly();
    }

    public void setScope(ReportScope scope) {
        myScope = scope;
    }

    public ReportScope getScope() {
        return myScope;
    }

    public void setType(ReportType type) {
        myType = type;
    }

    public ReportType getType() {
        return myType;
    }

    public void setCustomReportsOnly(boolean customReportsOnly) {
        this.customReportsOnly = customReportsOnly;
    }

    public boolean isCustomReportsOnly() {
        return customReportsOnly;
    }
}
