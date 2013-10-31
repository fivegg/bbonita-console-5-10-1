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
package org.bonitasoft.migration.forms.impl;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;

import org.bonitasoft.migration.forms.FormMigration;
import org.bonitasoft.migration.forms.TransformFormXML;
import org.bonitasoft.migration.forms.TransformFormXMLFactory;
import org.bonitasoft.migration.forms.exception.FormAlreadyMigratedException;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.xml.sax.SAXException;

/**
 * @author Qixiang Zhang
 * @version 1.0
 */
public class FormMigrationImpl implements FormMigration {

    /**
     * Logger
     */
    protected static Logger LOGGER = Logger.getLogger(FormMigrationImpl.class.getName());
    
    /**
     * {@inheritDoc}
     */
    public void replaceFormsXMLFile(final String processDefinitionUUID) throws ProcessNotFoundException, TransformerException, IOException, SAXException {
        try {
            final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
            final String resource = "forms/forms.xml";
            final ProcessDefinitionUUID definitionUUID = new ProcessDefinitionUUID(processDefinitionUUID);
            final byte[] oldFormsXMLContent = queryDefinitionAPI.getResource(definitionUUID, resource);
            if (oldFormsXMLContent == null) {
                throw new FormAlreadyMigratedException("there is no forms definition file in the process " + processDefinitionUUID);
            }
            final TransformFormXML transformer = TransformFormXMLFactory.getInstance().newTransformer();
            final byte[] newFormsXMLContent = transformer.transform(oldFormsXMLContent);
            final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
            managementAPI.setResource(definitionUUID, resource, newFormsXMLContent);
            final Date migrationDate = new Date();
            managementAPI.updateMigrationDate(definitionUUID, migrationDate);
        } catch (FormAlreadyMigratedException e) {
            LOGGER.log(Level.WARNING, "there is nothing to migrate in the process " + processDefinitionUUID, e);
        }
    }

}
