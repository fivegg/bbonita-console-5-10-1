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
package org.bonitasoft.console.client.view;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.ItemSelection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Nicolas Chabanoles
 * @param <E> 
 * 
 */
public class ItemSelectionWidget<E extends BonitaUUID> extends Composite implements
		ModelChangeListener {

	private E myItem;
	private ItemSelection<E> myItemSelection;
	final CheckBox myCheckBox = new CheckBox();

	/**
	 * 
	 * Default constructor.
	 * @param anItemSelection 
	 * @param anItem 
	 * @param aCaseSelection
	 * @param aCase
	 */
	public ItemSelectionWidget(final ItemSelection<E> anItemSelection,
			final E anItem) {
		super();
		this.myItem = anItem;
		myItemSelection = anItemSelection;
		myItemSelection.addModelChangeListener(ItemSelection.ITEM_SELECTION_PROPERTY, this);
		FlowPanel theOuterPanel = new FlowPanel();

		myCheckBox.addClickHandler(new ClickHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
			 * .gwt.event.dom.client.ClickEvent)
			 */
			public void onClick(ClickEvent aEvent) {
				if (myCheckBox.getValue()) {
					myItemSelection.addItemToSelection(myItem);
				} else {
					myItemSelection.removeItemFromSelection(myItem);
				}
			}
		});
		// Finally layout widgets.
		theOuterPanel.add(myCheckBox);
		this.initWidget(theOuterPanel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.beans.ModelChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	public void modelChange(ModelChangeEvent aEvt) {
		// Propagate changes to the check box if necessary.
		myCheckBox
				.setValue(myItemSelection.getSelectedItems().contains(myItem));

	}
}
