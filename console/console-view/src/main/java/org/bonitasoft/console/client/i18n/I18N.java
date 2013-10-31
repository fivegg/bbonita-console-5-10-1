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
package org.bonitasoft.console.client.i18n;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 */
public interface I18N {

  public static final ConsoleConstants constants = (ConsoleConstants) GWT.create(ConsoleConstants.class);
  public static final ConsoleMessages messages = (ConsoleMessages) GWT.create(ConsoleMessages.class);
  public static final ConsolePatterns patterns = (ConsolePatterns) GWT.create(ConsolePatterns.class);
  
}
