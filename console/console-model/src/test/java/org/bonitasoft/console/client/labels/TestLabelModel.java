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
package org.bonitasoft.console.client.labels;

import java.util.Set;
import java.util.TreeSet;

import org.bonitasoft.console.client.users.UserUUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class TestLabelModel {

	Set<LabelModel> systemLabelSet = new TreeSet<LabelModel>();
	Set<LabelModel> customLabelSet = new TreeSet<LabelModel>();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		UserUUID userUUID = new UserUUID("Nicolas");

		LabelModel label = new LabelModel(LabelModel.INBOX_LABEL, userUUID);
		label.setDisplayOrder(0);
		systemLabelSet.add(label);
		label = new LabelModel(LabelModel.STAR_LABEL, userUUID);
		label.setDisplayOrder(1);
		systemLabelSet.add(label);
		label = new LabelModel(LabelModel.MY_CASES_LABEL, userUUID);
		label.setDisplayOrder(2);
		systemLabelSet.add(label);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.bonitasoft.console.client.labels.LabelModel#compareTo(org.bonitasoft.console.client.labels.LabelModel)}.
	 */
	@Test
	public void testCompareTo() {

	}

}
