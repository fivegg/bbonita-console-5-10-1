/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.forms.server.accessor.impl.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**
 * Form document
 * 
 * @author Anthony Birembaut
 */
public class FormDocument {

    /**
     * the form document
     */
    protected Document document;
    
    /**
     * the XPath evaluator
     */
    protected XPath xpathEvaluator;
    
    /**
     * Constructor
     * @param document
     */
    public FormDocument(final Document document) {
        this.document = document;
        xpathEvaluator = XPathFactory.newInstance().newXPath();
    }

    public Document getDocument() {
        return document;
    }

    public XPath getXpathEvaluator() {
        return xpathEvaluator;
    }

}
