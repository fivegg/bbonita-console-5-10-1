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
package org.bonitasoft.console.security.server.api.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.console.security.client.privileges.PrivilegePolicy;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.security.client.users.UserRights;
import org.bonitasoft.console.security.server.accessor.SecurityProperties;
import org.bonitasoft.console.security.server.api.IPrivilegesAPI;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.exception.UserNotFoundException;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.User;
import org.ow2.bonita.facade.privilege.Rule;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Anthony Birembaut
 *
 */
public class PrivilegesAPIImpl implements IPrivilegesAPI {

    /**
     * Instance attribute
     */
    private static PrivilegesAPIImpl INSTANCE = null;
    
    /**
     * instance uuid 
     */
    public static final String INSTANCE_UUID = "instance";
    
    /**
     * task uuid
     */
    public static final String TASK_UUID = "task";
    
    /**
     * process definition uuid
     */
    public static final String PROCESS_UUID = "process";

    /**
     * @return the PrivilegesAPIImpl instance
     */
    public static synchronized PrivilegesAPIImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrivilegesAPIImpl();
        }
        return INSTANCE;
    }

    /**
     * Private contructor to prevent instantiation
     */
    protected PrivilegesAPIImpl() {
    }

    /**
     * {@inheritDoc}
     */
    public UserRights getUserRights(final String username) {
        final IdentityAPI identityAPI = AccessorUtil.getIdentityAPI();
        final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
        User user = null;
        List<Rule> rules = null;
        try {
            user = identityAPI.findUserByUserName(username);
            final Set<String> roleUUIDs = new HashSet<String>();
            final Set<String> groupUUIDs = new HashSet<String>();
            final Set<String> membershipUUIDs = new HashSet<String>();
            final Set<Membership> userMemberships = user.getMemberships();
            for (final Membership membership : userMemberships) {
                membershipUUIDs.add(membership.getUUID());
                roleUUIDs.add(membership.getRole().getUUID());
                groupUUIDs.add(membership.getGroup().getUUID());
            }
            rules = managementAPI.getAllApplicableRules(user.getUUID(), roleUUIDs, groupUUIDs, membershipUUIDs, username);
        } catch (final UserNotFoundException e) {
            rules = managementAPI.getAllApplicableRules(null, null, null, null, username);
        }
        final UserRights userRights = new UserRights();
        for (final org.ow2.bonita.facade.privilege.Rule.RuleType ruleType : org.ow2.bonita.facade.privilege.Rule.RuleType.values()) {
            final PrivilegePolicy privilegePolicy = PrivilegePolicy.valueOf(managementAPI.getRuleTypePolicy(ruleType).name());
            userRights.setPolicy(RuleType.valueOf(ruleType.name()), privilegePolicy);
        }
        final Map<RuleType, Set<String>> ruleTypeExceptions = new HashMap<RuleType, Set<String>>();
        for (final Rule rule : rules) {
            final RuleType ruleType = RuleType.valueOf(rule.getType().name());
            if (!ruleTypeExceptions.containsKey(ruleType)) {
                ruleTypeExceptions.put(ruleType, new HashSet<String>());
            }
            if (rule.getItems() != null) {
                ruleTypeExceptions.get(ruleType).addAll(rule.getItems());
            }
        }
        for (final Map.Entry<RuleType, Set<String>> ruleTypeEntry : ruleTypeExceptions.entrySet()) {
            userRights.setRights(ruleTypeEntry.getKey(), ruleTypeEntry.getValue());
        }
        return userRights;
    }

    /**
     * {@inheritDoc}
     */
    public SecurityProperties getSecurityProperties(final Map<String, Object> urlContext) {
        final ProcessDefinitionUUID processDefinitionUUID = getProcessDefinitionUUID(urlContext);
        return SecurityProperties.getInstance(processDefinitionUUID);
    }
    
    /**
     * Retrieve the ProcessDefinitionUUID
     * @param context
     * @return processDefinitionUUID
     */
    protected ProcessDefinitionUUID getProcessDefinitionUUID(final Map<String, Object> context) {
        ProcessDefinitionUUID processDefinitionUUID = null;
        final String processUUIDStr = (String) context.get(PROCESS_UUID);
        final String instanceUUIDStr = (String) context.get(INSTANCE_UUID);
        final String taskUUIDStr = (String) context.get(TASK_UUID);
        if (processUUIDStr != null && !"".equals(processUUIDStr.trim())) {
            processDefinitionUUID = new ProcessDefinitionUUID(processUUIDStr);
        } else if (instanceUUIDStr != null && !"".equals(instanceUUIDStr.trim())) {
            final ProcessInstanceUUID processInstanceUUID = new ProcessInstanceUUID(instanceUUIDStr);
            processDefinitionUUID = processInstanceUUID.getProcessDefinitionUUID();
        } else if (taskUUIDStr != null && !"".equals(taskUUIDStr.trim())) {
            final ActivityInstanceUUID activityInstanceUUID = new ActivityInstanceUUID(taskUUIDStr);
            processDefinitionUUID = activityInstanceUUID.getProcessInstanceUUID().getProcessDefinitionUUID();
        }
        return processDefinitionUUID;
    }

}
