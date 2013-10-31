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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.bonitasoft.migration.forms.TransformFormXML;
import org.bonitasoft.migration.forms.constants.XMLForms;
import org.bonitasoft.migration.forms.exception.FormAlreadyMigratedException;
import org.bonitasoft.migration.forms.utils.TransformFormXMLUtil;
import org.bonitasoft.migration.forms.utils.XPathUtil;
import org.xml.sax.SAXException;

/**
 * @author Qixiang Zhang
 * @version 1.0
 */
public class TransformFormXMLImpl extends XPathUtil implements TransformFormXML {

    /**
     * forms.xsl file name
     */
    public static final String FORMS_XSL_FILE_NAME = "forms.xsl";

    /**
     * forms.xml file name
     */
    public static final String FORMS_XML_FILE_NAME = "forms.xml";
    
    /**
     * bos product version
     */
    public static final String BOS_PRODUCT_VERSION = "5.10";
    
    /**
     * bos sp product version
     */
    public static final String BOS_SP_PRODUCT_VERSION = "5.10-SP";

    /**
     * {@inheritDoc}
     */
    public File transform(final File barFile) throws IOException, TransformerException {
        final InputStream XSLFileInputStream = getClass().getResourceAsStream("/"+FORMS_XSL_FILE_NAME);
        File tempFile = new File(FORMS_XML_FILE_NAME);
        tempFile.deleteOnExit();
        byte[] fileAsBytes = TransformFormXMLUtil.getAllContentFrom(barFile);
        Map<String, byte[]> resources = TransformFormXMLUtil.getResourcesFromZip(fileAsBytes);
        for (Map.Entry<String, byte[]> resource : resources.entrySet()) {
            if (resource.getKey().endsWith(FORMS_XML_FILE_NAME)) {
                TransformFormXMLUtil.getFile(tempFile, resource.getValue());
            }

        }
        File result = transform(XSLFileInputStream, tempFile.getPath());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(final byte[] oldFormsXMLContent) throws TransformerException, IOException, SAXException, FormAlreadyMigratedException {
        
        final InputStream XSLFileInputStream = getClass().getResourceAsStream("/" + FORMS_XSL_FILE_NAME);
        final File tempFile = new File(FORMS_XML_FILE_NAME);
        tempFile.deleteOnExit();
        TransformFormXMLUtil.getFile(tempFile, oldFormsXMLContent);
        super.parse(tempFile);
        final String migrationProductVersion = getStringByXpath("//" + XMLForms.MIGRATION_PRODUCT_VERSION);
        final String currentProductVersion = getStringByXpath("//" + XMLForms.FORMS_DEFINITION + "/@" + XMLForms.PRODUCT_VERSION);
        final String applicationName = getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.NAME);
        final String applicationVersion = getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.VERSION);
        if (BOS_PRODUCT_VERSION.equals(migrationProductVersion) || BOS_PRODUCT_VERSION.equals(currentProductVersion) || BOS_SP_PRODUCT_VERSION.equals(currentProductVersion)) {
            throw new FormAlreadyMigratedException("The process with ProcessDefinitonUUID " + applicationName +"--" + applicationVersion + " does not need to be transformed or migrated!");
        }
        final File result = transform(XSLFileInputStream, tempFile.getPath());
        return TransformFormXMLUtil.getAllContentFrom(result);
    }

    
    /**
     * 
     * @param XSLFileInputStream xsl file input stream
     * @param XMLFile need to transform file
     * @return the transformed file
     * @throws TransformerException
     * @throws IOException
     */
    protected File transform(final InputStream XSLFileInputStream, final String XMLFile) throws TransformerException, IOException {

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer(new StreamSource(XSLFileInputStream));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        final String theBonitaHome = System.getProperty("BONITA_HOME");
        final File theTempFolder = new File(theBonitaHome, File.separator + "client" + File.separator + "tmp");
        if (!theTempFolder.exists()) {
            theTempFolder.mkdirs();
        }
        File tempFile = File.createTempFile("forms", ".xml", theTempFolder);
        tempFile.deleteOnExit();
        transformer.transform(new StreamSource(XMLFile), new StreamResult(new FileOutputStream(tempFile)));
        return tempFile;

    }

}
