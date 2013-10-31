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
package org.bonitasoft.migration.forms;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.xml.sax.SAXException;

/**
 * @author Qixiang Zhang
 * @version 1.0
 */
public interface FormMigration {
    
    /**
     * replace the forms.xml in the business archive
     * 
     * @param processDefinitionUUID the process definition UUID
     * @throws ProcessNotFoundException 
     * @throws IOException 
     * @throws TransformerException 
     * @throws SAXException 
     */
    void replaceFormsXMLFile(String processDefinitionUUID) throws ProcessNotFoundException, TransformerException, IOException, SAXException;

}
