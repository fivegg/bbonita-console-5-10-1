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
package org.bonitasoft.console.security.server.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ruiheng.Fan
 *
 */
public class WebBonitaConstantsFactory {
    
    private static Map<String, WebBonitaConstants> constantsMap = new HashMap<String, WebBonitaConstants>();
    private static final String DEFAULT_DOMAIN_NAME = "default";
    private static final  String NULL = "null";
    

    public synchronized static WebBonitaConstants getWebBonitaConstants(String domain){
        if (domain == null || domain.equals(NULL) || domain.trim().length() == 0) {
            domain = DEFAULT_DOMAIN_NAME;
        } 
        if(!constantsMap.containsKey(domain)){
            constantsMap.put(domain, new WebBonitaConstantsImpl(domain));
        }
        return constantsMap.get(domain);
    }
}
