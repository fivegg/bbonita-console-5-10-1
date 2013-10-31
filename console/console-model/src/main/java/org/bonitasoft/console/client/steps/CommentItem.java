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
package org.bonitasoft.console.client.steps;

import java.io.Serializable;
import java.util.Date;

import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.users.UserUUID;

/**
 * @author Nicolas Chabanoles
 *
 */
public class CommentItem implements Serializable {


	private static final long serialVersionUID = 3680860662676475035L;

	protected StepUUID myStepUUID;
	protected UserUUID myUserUUID;
	protected CaseUUID myCaseUUID;
	protected Date myDate;
	protected String myContent;

	protected CommentItem() {
		// Mandatory for serialization.
	}

	public CommentItem(String aStepUUID,String aStepDefinitionUUID, String aUserId, Date aDate, String aMessage) {
		super();
		myStepUUID = new StepUUID(aStepUUID,aStepDefinitionUUID);
		myUserUUID = new UserUUID(aUserId);
		myDate = aDate;
		myContent = aMessage;
	}
	
	public CommentItem(String aCaseUUID, String aUserId, Date aDate, String aMessage) {
    super();
    myStepUUID = null;
    myCaseUUID = new CaseUUID(aCaseUUID);
    myUserUUID = new UserUUID(aUserId);
    myDate = aDate;
    myContent = aMessage;
  }

	/**
	 * @return the stepUUID
	 */
	public StepUUID getStepUUID() {
		return myStepUUID;
	}

	/**
	 * @param aStepUUID the stepUUID to set
	 */
	public void setStepUUID(StepUUID aStepUUID) {
		myStepUUID = aStepUUID;
	}

	/**
	 * @return the userUUID
	 */
	public UserUUID getUserUUID() {
		return myUserUUID;
	}

	/**
	 * @param aUserUUID the userUUID to set
	 */
	public void setUserUUID(UserUUID aUserUUID) {
		myUserUUID = aUserUUID;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return myDate;
	}

	/**
	 * @param aDate the date to set
	 */
	public void setDate(Date aDate) {
		myDate = aDate;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return myContent;
	}

	/**
	 * @param aContent the content to set
	 */
	public void setContent(String aContent) {
		myContent = aContent;
	}

}
