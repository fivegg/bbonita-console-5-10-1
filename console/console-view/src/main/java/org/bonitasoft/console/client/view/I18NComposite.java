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
package org.bonitasoft.console.client.view;

import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.i18n.ConsoleMessages;
import org.bonitasoft.console.client.i18n.ConsolePatterns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author Nicolas Chabanoles
 */
public class I18NComposite extends Composite {

	protected static final ConsoleConstants constants = (ConsoleConstants) GWT.create(ConsoleConstants.class);
	protected static final ConsoleMessages messages = (ConsoleMessages) GWT.create(ConsoleMessages.class);
	protected static final ConsolePatterns patterns = (ConsolePatterns) GWT.create(ConsolePatterns.class);
	protected static final String PICTURE_PLACE_HOLDER = ConsoleConstants.PICTURE_PLACEHOLDER;
	
	public static ConsoleConstants getConstants(){
	    return constants;
	}

}
