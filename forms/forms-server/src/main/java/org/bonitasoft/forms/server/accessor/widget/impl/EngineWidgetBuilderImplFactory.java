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
package org.bonitasoft.forms.server.accessor.widget.impl;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.console.security.server.threadlocal.ThreadLocalManager;

/**
 * @author Ruiheng.Fan
 *
 */
public class EngineWidgetBuilderImplFactory {

    private static Map<String, EngineWidgetBuilderImpl> map = new HashMap<String, EngineWidgetBuilderImpl>();
    
    /**
     * Get Domain EngineWidgetBuilderImpl
     * @param domain
     * @return EngineWidgetBuilderImpl
     */
    public synchronized static EngineWidgetBuilderImpl getEngineWidgetBuilderImpl() {
        final String domain = ThreadLocalManager.getDomain();
        if (!map.containsKey(domain)) {
            map.put(domain, new EngineWidgetBuilderImpl());
        }
        return map.get(domain);
    }
}
