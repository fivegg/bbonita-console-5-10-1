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
package org.bonitasoft.migration.forms;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.bonitasoft.migration.forms.exception.FormAlreadyMigratedException;
import org.xml.sax.SAXException;

/**
 * @author Qixiang Zhang
 * @version 2.0
 */
public interface TransformFormXML {

    /**
     * transform the forms.xml which is in the .bar file (5.5-5.6)
     * 
     * @param barFile *.bar files
     * @return returns transformed forms.xml file
     * @throws IOException 
     * @throws TransformerException
     */
    File transform(File barFile) throws IOException, TransformerException;
    
    /**
     * transform the forms.xml which is in the BusinessArchives file
     * 
     * @param oldFormsXMLContent  old bytes of forms.xml
     * @return new bytes of forms.xml
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException 
     * @throws FormAlreadyMigratedException 
     */
    byte[] transform(byte[] oldFormsXMLContent) throws TransformerException, IOException, SAXException, FormAlreadyMigratedException;
}
