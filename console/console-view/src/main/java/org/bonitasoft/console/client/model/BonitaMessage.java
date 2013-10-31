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

/**
 * @author Nicolas Chabanoles
 *
 */
public class BonitaMessage {

	/**
	 * A message severity.
	 * @author Nicolas Chabanoles
	 *
	 */
	public enum BonitaMessageSeverity {
		info, warn, error;
	}
	
	private BonitaMessageSeverity mySeverity;
	private String myMessage;

	/**
	 * Default constructor.
	 * @param aSeverity
	 * @param aMessage
	 */
	public BonitaMessage(BonitaMessageSeverity aSeverity, String aMessage) {
		mySeverity = aSeverity;
		myMessage = aMessage;
	}

	
	/**
	 * Get the severity of the message.
	 * @return the severity
	 */
	public BonitaMessageSeverity getSeverity() {
		return mySeverity;
	}

	/**
	 * Get the textual representation of the message.
	 * @return the message
	 */
	public String getMessage() {
		return myMessage;
	}
	
	@Override
	public String toString() {
		return getSeverity() + ": " + getMessage();
	}
}
