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

import org.bonitasoft.migration.forms.impl.TransformFormXMLImpl;

/**
 * @author Qixiang Zhang
 * @version 
 */
public class TransformFormXMLFactory {
    
    
    /**
     * Instance attribute
     */
    private static TransformFormXMLFactory INSTANCE = null;
    
    private TransformFormXMLFactory(){
    }
    
    /**
     * @return the TransformFormXMLFactory instance
     */
    public static synchronized TransformFormXMLFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TransformFormXMLFactory();
        }
        
        return INSTANCE;
    }

    /**
     * create a instance TransformFormXML object;
     * 
     * @return return TransformFormXML
     */
    public TransformFormXML newTransformer() {
        return new TransformFormXMLImpl();
    }
    
    
    

}
