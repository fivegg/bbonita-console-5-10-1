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

import static org.junit.Assert.fail;

import org.bonitasoft.console.client.processes.BonitaProcess.BonitaProcessState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class BonitaProcessStateTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 */
	@Test
	public void synchroWithProcessState() {

		try {
			for (BonitaProcessState theState : BonitaProcessState.values()) {
				ProcessState.valueOf(theState.name());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("The Bonita Process State enum is out of sync with the enum defined in the engine.");
			
		}

	}
}
