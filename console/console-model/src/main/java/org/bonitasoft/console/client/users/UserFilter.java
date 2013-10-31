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
package org.bonitasoft.console.client.users;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.SimpleFilter;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserFilter extends SimpleFilter {

    private static final long serialVersionUID = -5182166718275908546L;

    protected BonitaUUID myGroupUUID;
    protected BonitaUUID myRoleUUID;

    /**
     * Default constructor.
     */
    private UserFilter() {
        // Mandatory for serialization.
    }

    public UserFilter(final int aStartingIndex, final int aPageSize) {
        super(aStartingIndex, aPageSize, false);
    }

    public BonitaUUID getGroupUUID() {
        return myGroupUUID;
    }

    public void setGroupUUID(BonitaUUID aGroupUUID) {
        myGroupUUID = aGroupUUID;
    }

    public BonitaUUID getRoleUUID() {
        return myRoleUUID;
    }

    public void setRoleUUID(BonitaUUID aRoleUUID) {
        myRoleUUID = aRoleUUID;
    }
    
    @Override
    public ItemFilter createFilter() {
        UserFilter theResult = new UserFilter();
        theResult.updateFilter(this);
        return theResult;
    }

    public void updateFilter(UserFilter aFilter) {
        super.updateFilter(aFilter);
        myGroupUUID = aFilter.getGroupUUID();
        myRoleUUID = aFilter.getRoleUUID();
    }


}
