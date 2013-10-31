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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CustomDialogBox extends DialogBox {

	protected KeyPressHandler myKeyPressHandler = new KeyPressHandler() {
		public void onKeyPress(KeyPressEvent anEvent) {

			char theChar = anEvent.getCharCode();
			if (KeyCodes.KEY_ESCAPE == theChar) {
				escape();
			}

		};
	};

	public CustomDialogBox() {
		super();
		initCustomDialogBox();
	}

	public CustomDialogBox(boolean autoHide, boolean isModal) {
	    super(autoHide, isModal);
        initCustomDialogBox();
	}

	public CustomDialogBox(boolean anAutoHide) {
		super(anAutoHide);
        initCustomDialogBox();
	}

	private void initCustomDialogBox(){
        this.setGlassEnabled(true);
        this.setAnimationEnabled(false);
        addDomHandler(myKeyPressHandler, KeyPressEvent.getType());
	}
	
	protected void escape(){
		hide();
	}
}
