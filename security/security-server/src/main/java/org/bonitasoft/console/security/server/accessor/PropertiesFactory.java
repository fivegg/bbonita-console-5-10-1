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
package org.bonitasoft.console.security.server.accessor;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.console.security.server.threadlocal.ThreadLocalManager;


/**
 * @author Ruiheng.Fan
 *
 */
public class PropertiesFactory {
    private static final TenancyProperties bosPreferencesProperties = new TenancyProperties();
    private static final Map<String, TenancyProperties> map = new HashMap<String, TenancyProperties>();

    public static PlatformProperties getPlatformProperties() {
        return PlatformProperties.getInstance();
    }

    public static synchronized TenancyProperties getTenancyProperties() {
        final String domain = ThreadLocalManager.getDomain();
        if (domain == null) {
            return bosPreferencesProperties;
        } else {
            if (!map.containsKey(domain)) {
                map.put(domain, new TenancyProperties(domain));
            }
            return map.get(domain);
        }
    }

}
