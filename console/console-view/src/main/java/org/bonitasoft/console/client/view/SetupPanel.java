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

import org.bonitasoft.console.client.model.MessageDataSource;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * @author Nicolas Chabanoles
 *
 */
public abstract class SetupPanel extends BonitaPanel {
	protected static final String NBSP = "&nbsp;";
	protected static final String DEFAULT_CSS_STYLE = "bos_setupPanel";
	protected final MessageDataSource myMessageDataSource;
	protected boolean myUIHasBeenBuilt;
	
	
	public SetupPanel(MessageDataSource aMessageDataSource) {
		super();
		myMessageDataSource = aMessageDataSource;
		myUIHasBeenBuilt = false;
	}
	
	/**
	 * Fill the table to ensure a minimal size.
	 * 
	 * @param aStartingRow
	 */
	protected void fillWithEmptyRows(final FlexTable aTable, int aStartingRow, int aNbColumn) {
		// Fill any remaining slots with empty cells.
		for (; aStartingRow < constants.defaultMinDisplayedItems(); ++aStartingRow) {
			for (int i = 0; i < aNbColumn; i++) {
				aTable.setHTML(aStartingRow, i, NBSP);
			}
			// Set CSS style.
			aTable.getRowFormatter().setStyleName(aStartingRow, "item_list_empty_row");
		}
	}
	
	protected abstract void buildContent();
	
	protected abstract void updateContent();
	
	public void update(){
		if(!myUIHasBeenBuilt){
			buildContent();
			myUIHasBeenBuilt = true;
		}
		updateContent();
	}
}
