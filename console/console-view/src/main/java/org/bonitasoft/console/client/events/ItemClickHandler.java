package org.bonitasoft.console.client.events;

import com.google.gwt.event.dom.client.ClickEvent;

public interface ItemClickHandler<T> {

	/**
	 * Alert that an item as been clicked.
	 * 
	 * @param anItem
	 *            the item that has been clicked.
	 * @param aEvent
	 *            the click event
	 */
	void notifyItemClicked(final T anItem, final ClickEvent aEvent);
}
