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
package org.bonitasoft.console.security.client.users;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.security.client.privileges.PrivilegePolicy;
import org.bonitasoft.console.security.client.privileges.RuleType;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserRights implements Serializable {

    /**
     * ID used for serialization.
     */
    private static final long serialVersionUID = -5860989010476128351L;

    private Map<RuleType, PrivilegePolicy> myPolicies = new HashMap<RuleType, PrivilegePolicy>();
    private Map<RuleType, HashSet<String>> myRuleTypeExceptions = new HashMap<RuleType, HashSet<String>>();

    /**
     * 
     * Default constructor.
     */
    public UserRights() {
        super();
        // Mandatory for serialization.
    }

    public void setPolicy(final RuleType aRuleType, final PrivilegePolicy aCurrentPolicy) {
        myPolicies.put(aRuleType, aCurrentPolicy);
    }

    public PrivilegePolicy getPolicy(final RuleType aRuleType) {
        return myPolicies.get(aRuleType);
    }

    public void setRights(final RuleType aRuleType, final Set<String> anExceptionSet) {
        final HashSet<String> theExceptions = new HashSet<String>();
        if (anExceptionSet != null) {
            theExceptions.addAll(anExceptionSet);
        }
        myRuleTypeExceptions.put(aRuleType, theExceptions);
    }

    public boolean isAllowed(final RuleType aRuleType, final String anItem) {
        if (aRuleType == null || anItem == null) {
            throw new IllegalArgumentException();
        }
        final PrivilegePolicy theCurrentPolicy = myPolicies.get(aRuleType);
        if (theCurrentPolicy == null) {
            return false;
        }
        final HashSet<String> theExceptions = myRuleTypeExceptions.get(aRuleType);

        switch (theCurrentPolicy) {
        case ALLOW_BY_DEFAULT:
            if (theExceptions != null) {
                return !theExceptions.contains(anItem);
            }
            return true;
        case DENY_BY_DEFAULT:
            if (theExceptions != null) {
                return theExceptions.contains(anItem);
            }
            return false;
        default:
            throw new IllegalStateException("Policy not supported: " + theCurrentPolicy);
        }
    }

    public boolean isAllowed(final RuleType aRuleType) {
        if (aRuleType == null) {
            throw new IllegalArgumentException();
        }
        switch (aRuleType) {
        case PROCESS_INSTALL:
        case REPORT_INSTALL:
        case LOGOUT:
        case DELEGEE_UPDATE:
        case PASSWORD_UPDATE:

            final PrivilegePolicy theCurrentPolicy = myPolicies.get(aRuleType);
            if (theCurrentPolicy == null) {
                return false;
            }
            final HashSet<String> theExceptions = myRuleTypeExceptions.get(aRuleType);

            switch (theCurrentPolicy) {
            case ALLOW_BY_DEFAULT:
                return (theExceptions == null);
            case DENY_BY_DEFAULT:
                return (theExceptions != null);
            default:
                throw new IllegalStateException("Policy not supported: " + theCurrentPolicy);
            }

        default:
            throw new IllegalArgumentException();
        }

    }

    public boolean hasPrivileges(final RuleType aRuleType) {
        if (aRuleType == null) {
            throw new IllegalArgumentException();
        }
        final PrivilegePolicy theCurrentPolicy = myPolicies.get(aRuleType);
        if (theCurrentPolicy == null) {
            return false;
        }
        final HashSet<String> theExceptions = myRuleTypeExceptions.get(aRuleType);

        switch (theCurrentPolicy) {
        case ALLOW_BY_DEFAULT:
            return true;
        case DENY_BY_DEFAULT:
            return (theExceptions != null && !theExceptions.isEmpty());
        default:
            throw new IllegalStateException("Policy not supported: " + theCurrentPolicy);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Policies: " + myPolicies + "\n Exceptions: " + myRuleTypeExceptions;
    }
}
