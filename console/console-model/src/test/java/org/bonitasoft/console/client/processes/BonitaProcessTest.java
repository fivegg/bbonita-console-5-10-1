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
package org.bonitasoft.console.client.processes;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bonitasoft.console.client.processes.BonitaProcess.BonitaProcessState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class BonitaProcessTest {

	List<BonitaProcess> myProcesses = new ArrayList<BonitaProcess>();

	BonitaProcess myFirstProcess = new BonitaProcess("id1", "aA", "1 process", "This process does nothing.", BonitaProcessState.ENABLED.name(), "2.1.1", true);
	BonitaProcess mySecondProcess = new BonitaProcess("id2", "bB", "2 process", "This process does nothing.", BonitaProcessState.ENABLED.name(), "2.1.2", true);
	BonitaProcess myThirdProcess = new BonitaProcess("id3", "zZ", "3 process", "This process does nothing.", BonitaProcessState.DISABLED.name(), "2.1.3", true);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		myProcesses.add(myThirdProcess);
		myProcesses.add(myFirstProcess);
		myProcesses.add(mySecondProcess);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		myProcesses.clear();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Test
	public void compareToTest() throws Exception {
		// Sort the list.
		Collections.sort(myProcesses);
		assertEquals("The first element is not the first in alphabetic order.",myFirstProcess, myProcesses.get(0));
		assertEquals("The second element is not the second in alphabetic order.",mySecondProcess, myProcesses.get(1));
		assertEquals("The third element is not the third in alphabetic order.",myThirdProcess, myProcesses.get(2));

		// Reverse the list
		Collections.reverse(myProcesses);
		assertEquals("The first element is not the first in reverse alphabetic order.",myThirdProcess, myProcesses.get(0));
		assertEquals("The second element is not the second in reverse alphabetic order.",mySecondProcess, myProcesses.get(1));
		assertEquals("The third element is not the third in reverse alphabetic order.",myFirstProcess, myProcesses.get(2));

	}
}
