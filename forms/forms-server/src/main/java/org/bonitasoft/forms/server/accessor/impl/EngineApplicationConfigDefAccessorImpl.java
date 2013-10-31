/**
 * Copyright (C) 2011 BonitaSoft S.A.
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
package org.bonitasoft.forms.server.accessor.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.accessor.IApplicationConfigDefAccessor;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.bonitasoft.forms.server.provider.impl.util.FormServiceProviderUtil;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Haojie Yuan
 * 
 */
public class EngineApplicationConfigDefAccessorImpl implements IApplicationConfigDefAccessor {

    /**
     * The process definition UUID of the process to which this instance is associated
     */
    private final ProcessDefinitionUUID processDefinitionUUID;

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(EngineApplicationConfigDefAccessorImpl.class.getName());

    /**
     * @param processDefinitionUUID The process definition UUID of the process to which this instance is associated. This
     *            parameter is allowed be null because an instance of this class should be available to retrieve the default
     *            process template to display error pages
     */
    public EngineApplicationConfigDefAccessorImpl(final ProcessDefinitionUUID processDefinitionUUID) {
        this.processDefinitionUUID = processDefinitionUUID;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationErrorTemplate() {
        return DefaultFormsPropertiesFactory.getDefaultFormProperties().getPageErrorTemplate();
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationLabel() throws InvalidFormDefinitionException {
        final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
        try {
            String label = null;
            if (processDefinitionUUID != null) {
                final ProcessDefinition processDefinition = queryDefinitionAPI.getProcess(processDefinitionUUID);
                label = processDefinition.getLabel();
                if (label == null || label.length() == 0) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, "application definition label : " + label + " - application name : " + processDefinition.getName());
                    }
                    label = processDefinition.getName();
                }
            }
            return label;
        } catch (final ProcessNotFoundException e) {
            final String message = "application " + processDefinitionUUID + " not found.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, message, e);
            }
            throw new InvalidFormDefinitionException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationMandatoryLabel() {
        return DefaultFormsPropertiesFactory.getDefaultFormProperties().getApplicationMandatoryLabel();
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationMandatorySymbol() {
        return DefaultFormsPropertiesFactory.getDefaultFormProperties().getApplicationMandatorySymbol();
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationMandatorySymbolStyle() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationName() throws InvalidFormDefinitionException {
        return processDefinitionUUID.getProcessName();
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationVersion() throws InvalidFormDefinitionException {
        return processDefinitionUUID.getProcessVersion();
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationLayout() {
        return DefaultFormsPropertiesFactory.getDefaultFormProperties().getApplicationLayout();
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationPermissions() {
        if (processDefinitionUUID != null) {
            return FormServiceProviderUtil.PROCESS_UUID + "#" + processDefinitionUUID.getValue();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getMigrationProductVersion() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getExternalWelcomePage() {

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getWelcomePage() {

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getHomePage() {

        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getProductVersion() {

        return null;
    }

}
