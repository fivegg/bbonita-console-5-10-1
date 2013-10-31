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
package org.bonitasoft.console.client.view.steps;

import org.bonitasoft.forms.client.view.common.DOMUtils;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;

/**
 * This class is being used to solve the focus problem of Internet Explorer.
 * @author Zhiheng Yang
 * 
 */
public class FormPageFrame extends Frame {
    
    @Override
    protected void onUnload(){
        if (DOMUtils.getInstance().isInternetExplorer()) {
            killFrame(this.getElement());
        }
    }
       
    private native void killFrame( Element pElement) /*-{
       var element = pElement;
       element.onreadystatechange=function() {
         if (element.readyState=='complete') {
            element.onreadystatechange=null;
            element.outerHTML='';
            element = null;
         }
       }
       element.src='javascript:\'\'';
    }-*/;
}
