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
package org.bonitasoft.console.client.cases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.bonitasoft.console.client.attachments.Attachment;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.steps.StepUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class TestCaseItem extends TestCase {

	List<CaseItem> caseList = new ArrayList<CaseItem>();
	CaseItem[] caseTable = new CaseItem[5];
	int[] deltas = new int[] { 16, 12, 19, 4, 25 };

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		CaseUUID caseUUID = new CaseUUID("first");
		BonitaProcessUUID theProcessUUID = new BonitaProcessUUID("first-v1.0", "first");

		List<Attachment> attachmentList = new ArrayList<Attachment>();
		StepUUID stepUUID = new StepUUID("step","step");
		Set<String> candidates = new TreeSet<String>();

		// First Case creation
		// The first case created is older than all the other

		for (int i = 0; i < 5; i++) {
			Date theCaseDate = new Date(1247639400000L + (deltas[i] * 60000));
			Date stepDate = theCaseDate;
			StepItem step = new StepItem(stepUUID, "step name", "step label", StepState.READY, candidates, "step description", "an author", stepDate, true,stepDate);
			List<StepItem> stepList = new ArrayList<StepItem>();
			stepList.add(step);
			CaseItem caseItem = new CaseItem(caseUUID, String.valueOf(i), theProcessUUID, stepList, attachmentList, theCaseDate, "john", theCaseDate);
			caseList.add(caseItem);

		}
		caseTable[0] = caseList.get(4);
		caseTable[1] = caseList.get(2);
		caseTable[2] = caseList.get(0);
		caseTable[3] = caseList.get(1);
		caseTable[4] = caseList.get(3);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.bonitasoft.console.client.cases.CaseItem#compareTo(org.bonitasoft.console.client.cases.CaseItem)}.
	 */
	public void testCompareTo() {
		System.out.println("Before the call to the sort method:");
		for (Iterator<CaseItem> iterator = caseList.iterator(); iterator.hasNext();) {
			CaseItem theCase = iterator.next();
			System.out.println(theCase.getLastUpdateDate());
		}
		System.out.println("-----");
		for (int i = 0; i < caseList.size(); i++) {
			CaseItem theCase = caseList.get(i);
			System.out.println(theCase.getLastUpdateDate());
		}

		Collections.sort(caseList);
		Collections.reverse(caseList);
		System.out.println("After sort and reverse:");

		for (int i = 0; i < caseList.size(); i++) {
			CaseItem theCase = caseList.get(i);
			assertEquals(caseTable[i].getLastUpdateDate(), theCase.getLastUpdateDate());
			System.out.println(theCase.getLastUpdateDate());
		}
	}

}
