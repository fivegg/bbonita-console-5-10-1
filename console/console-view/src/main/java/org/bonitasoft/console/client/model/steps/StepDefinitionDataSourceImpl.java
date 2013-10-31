/**
 * Copyright (C) 2010 BonitaSoft S.A.
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
package org.bonitasoft.console.client.model.steps;

import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.model.DefaultFilteredDataSourceImpl;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.SimpleSelection;
import org.bonitasoft.console.client.steps.StepDefinition;
import org.bonitasoft.console.client.steps.StepUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepDefinitionDataSourceImpl extends DefaultFilteredDataSourceImpl<StepUUID, StepDefinition, StepFilter> implements StepDefinitionDataSource {


	public StepDefinitionDataSourceImpl(MessageDataSource aMessageDataSource) {
		super(new StepDefinitionData(),new SimpleSelection<StepUUID>(), aMessageDataSource);
		setItemFilter(new StepFilter(null,0, 20));
	}

}
