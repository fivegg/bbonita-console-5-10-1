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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ModelChangeSupport {

	PropertyChangeSupport delegate;
	private HashMap<ModelChangeListener, PropertyChangeListener> myListeners;

	/**
	 * Constructs a <code>ModelChangeSupport</code> object.
	 * 
	 * @param sourceModel
	 *            The model to be given as the source for any events.
	 */

	public ModelChangeSupport(Object sourceModel) {
		delegate = new PropertyChangeSupport(sourceModel);
		myListeners = new HashMap<ModelChangeListener, PropertyChangeListener>();
	}

	/**
	 * Add a ModelChangeListener for a specific property.<br>
	 * The listener will be invoked only when a call on fireModelChange names that specific property. The same listener object may be added more than once. For each property, the listener will be
	 * invoked the number of times it was added for that property. If <code>propertyName</code> or <code>listener</code> is null, no exception is thrown and no action is taken.
	 * 
	 * @param propertyName
	 *            The name of the property to listen on.
	 * @param listener
	 *            The ModelChangeListener to be added
	 */

	public synchronized void addModelChangeListener(final String propertyName, final ModelChangeListener listener) {
		if (listener == null || propertyName == null) {
			return;
		}
		PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent aEvt) {
				listener.modelChange(new ModelChangeEvent(aEvt.getSource(), aEvt.getPropertyName(), aEvt.getOldValue(), aEvt.getNewValue()));
			}
		};
		myListeners.put(listener, propertyChangeListener);
		delegate.addPropertyChangeListener(propertyName, propertyChangeListener);
	}

	/**
	 * Add a ModelChangeListener to the listener list. The listener is registered for all properties. The same listener object may be added more than once, and will be called as many times as it is
	 * added. If <code>listener</code> is null, no exception is thrown and no action is taken.
	 * 
	 * @param listener
	 *            The ModelChangeListener to be added
	 */
	public synchronized void addModelChangeListener(final ModelChangeListener listener) {
		if (listener == null) {
			return;
		}
		PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent aEvt) {
				listener.modelChange(new ModelChangeEvent(aEvt.getSource(), aEvt.getPropertyName(), aEvt.getOldValue(), aEvt.getNewValue()));
			}
		};
		myListeners.put(listener, propertyChangeListener);
		delegate.addPropertyChangeListener(propertyChangeListener);
	}

	/**
	 * Report a bound property update to any registered listeners. No event is fired if old and new are equal and non-null.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	public void fireModelChange(String propertyName, Object oldValue, Object newValue) {
	  if(oldValue == null && newValue == null) {
	    return;
	  }
		delegate.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Fire an existing ModelChangeEvent to any registered listeners. No event is fired if the given event's old and new values are equal and non-null.
	 * 
	 * @param evt
	 *            The ModelChangeEvent object.
	 */
	public void fireModelChange(ModelChangeEvent evt) {
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		if(oldValue == null && newValue == null) {
      return;
    }
		String propertyName = evt.getPropertyName();
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		delegate.firePropertyChange(new PropertyChangeEvent(evt.getSource(), propertyName, oldValue, newValue));
	}

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a PropertyChangeListener that was registered for all properties. If <code>listener</code> was added more than once to the
	 * same event source, it will be notified one less time after being removed. If <code>listener</code> is null, or was never added, no exception is thrown and no action is taken.
	 * 
	 * @param listener
	 *            The PropertyChangeListener to be removed
	 */
	public synchronized void removeModelChangeListener(ModelChangeListener listener) {
		if (listener == null || !myListeners.containsKey(listener)) {
			return;
		}
		PropertyChangeListener thePropertyChangeListenerDelegate = myListeners.remove(listener);
		delegate.removePropertyChangeListener(thePropertyChangeListenerDelegate);

	}

	/**
	 * Remove a ModelChangeListener for a specific property. If <code>listener</code> was added more than once to the same event source for the specified property, it will be notified one less time
	 * after being removed. If <code>propertyName</code> is null, no exception is thrown and no action is taken. If <code>listener</code> is null, or was never added for the specified property, no
	 * exception is thrown and no action is taken.
	 * 
	 * @param propertyName
	 *            The name of the property that was listened on.
	 * @param listener
	 *            The ModelChangeListener to be removed
	 */

	public synchronized void removeModelChangeListener(String propertyName, ModelChangeListener listener) {
		if (myListeners==null || listener == null || propertyName == null) {
			return;
		}
		PropertyChangeListener thePropertyChangeListenerDelegate = myListeners.remove(listener);
		delegate.removePropertyChangeListener(propertyName, thePropertyChangeListenerDelegate);
	}
}
