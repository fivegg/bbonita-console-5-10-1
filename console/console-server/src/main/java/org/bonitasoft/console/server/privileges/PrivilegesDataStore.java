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
package org.bonitasoft.console.server.privileges;

import java.util.logging.Logger;

import org.bonitasoft.console.client.privileges.RuleItem;
import org.bonitasoft.console.security.client.privileges.PrivilegePolicy;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class PrivilegesDataStore {

  private static Logger LOGGER = Logger.getLogger(PrivilegesDataStore.class.getName());

  private static final PrivilegesDataStore INSTANCE = new PrivilegesDataStore();

  protected PrivilegesDataStore() {
    super();
  }

  public static PrivilegesDataStore getInstance() {
    return INSTANCE;
  }

  public PrivilegePolicy getRuleTypePolicy(RuleType aRuleType) {
    final ManagementAPI theManagementAPI = AccessorUtil.getManagementAPI();
    org.ow2.bonita.facade.privilege.PrivilegePolicy theStoredPolicy = theManagementAPI.getRuleTypePolicy(org.ow2.bonita.facade.privilege.Rule.RuleType.valueOf(aRuleType.name()));

    return PrivilegePolicy.valueOf(theStoredPolicy.name());
  }
  
  protected RuleItem buildRuleItem(org.ow2.bonita.facade.privilege.Rule aRule) {
    RuleItem theRuleItem = new RuleItem(aRule.getUUID(), aRule.getName(), aRule.getLabel(), aRule.getDescription(), RuleType.valueOf(aRule.getType().name()));
    theRuleItem.setUsers(aRule.getUsers());
    theRuleItem.setGroups(aRule.getGroups());
    theRuleItem.setRoles(aRule.getRoles());
    theRuleItem.setMemberships(aRule.getMemberships());
    theRuleItem.setEntities(aRule.getEntities());
    theRuleItem.setExceptions(aRule.getItems());
    return theRuleItem;
  }
  
}
