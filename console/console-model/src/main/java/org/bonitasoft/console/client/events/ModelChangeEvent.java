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
package org.bonitasoft.console.client.events;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ModelChangeEvent implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4714302961999878386L;

	/**
	 * The object on which the Event initially occurred.
	 */
	protected transient Object source;

	/**
	 * name of the property that changed. May be null, if not known.
	 * 
	 * @serial
	 */
	private String propertyName;

	/**
	 * New value for property. May be null if not known.
	 * 
	 * @serial
	 */
	private Object newValue;

	/**
	 * Previous value for property. May be null if not known.
	 * 
	 * @serial
	 */
	private Object oldValue;

	/**
	 * Constructs a new <code>ModelChangeEvent</code>.
	 * 
	 * @param source
	 *            The model that fired the event.
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	public ModelChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
		if (source == null)
			throw new IllegalArgumentException("null source");

		this.source = source;
		this.propertyName = propertyName;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	/**
	 * The object on which the Event initially occurred.
	 * 
	 * @return The object on which the Event initially occurred.
	 */
	public Object getSource() {
		return this.source;
	}

	/**
	 * Gets the programmatic name of the property that was changed.
	 * 
	 * @return The programmatic name of the property that was changed. May be null if multiple properties have changed.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Sets the new value for the property, expressed as an Object.
	 * 
	 * @return The new value for the property, expressed as an Object. May be null if multiple properties have changed.
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * Gets the old value for the property, expressed as an Object.
	 * 
	 * @return The old value for the property, expressed as an Object. May be null if multiple properties have changed.
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * Returns a String representation of this ModelChangeEvent.
	 * 
	 * @return a String representation of this ModelChangeEvent.
	 */
	public String toString() {
		return getClass().getName() + "[source=" + this.source + "]";
	}
}
