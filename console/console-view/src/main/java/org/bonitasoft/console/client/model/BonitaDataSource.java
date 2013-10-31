/**
 * Copyright (C) 2009 BonitaSoft S.A.
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
package org.bonitasoft.console.client.model;

import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.i18n.ConsoleMessages;
import org.bonitasoft.console.client.i18n.ConsolePatterns;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface BonitaDataSource<U extends BonitaUUID> {
	
	static final ConsoleConstants constants = (ConsoleConstants) GWT.create(ConsoleConstants.class);
	static final ConsoleMessages messages = (ConsoleMessages) GWT.create(ConsoleMessages.class);
	static final ConsolePatterns patterns = (ConsolePatterns) GWT.create(ConsolePatterns.class);

	public static final String TIMEOUT_PROPERTY = "session timeout";
	public static final String MISSING_PRIVILEGES_PROPERTY = "session privileges missing";
	public static final String ITEM_LIST_PROPERTY = "item list property";
	public static final String ITEM_CREATED_PROPERTY = "item created property";
	public static final String ITEM_DELETED_PROPERTY = "item deleted property";
	public static final String ITEM_UPDATED_PROPERTY = "item updated property";
	
	
	/**
	 * Add a listener.
	 * @param aPropertyName
	 * @param aListener
	 */
	public abstract void addModelChangeListener(String aPropertyName, ModelChangeListener aListener);
	
	/**
	 * Remove a listener.
	 * @param aPropertyName
	 * @param aListener
	 */
	public abstract void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener);

	public List<U> getVisibleItems();

}