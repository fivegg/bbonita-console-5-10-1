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
package org.bonitasoft.forms.server.accessor.widget;

import java.util.Date;

import javax.xml.xpath.XPath;

import org.bonitasoft.forms.server.accessor.widget.impl.EngineWidgetBuilderImplFactory;
import org.bonitasoft.forms.server.accessor.widget.impl.XMLWidgetBuilderImpl;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;

/**
 * @author Anthony Birembaut
 *
 */
public class WidgetBuilderFactory {

    public static IXMLWidgetBuilder getXMLWidgetBuilder(final XPath xpathEvaluator, final ProcessDefinitionUUID processDefinitionUUID, final String locale, final Date processDeployementDate) {
        return new XMLWidgetBuilderImpl(xpathEvaluator);
    }
    
    /**
     * @return
     */
    public static IEngineWidgetBuilder getEngineWidgetBuilder() {
        return EngineWidgetBuilderImplFactory.getEngineWidgetBuilderImpl();
    }
}
