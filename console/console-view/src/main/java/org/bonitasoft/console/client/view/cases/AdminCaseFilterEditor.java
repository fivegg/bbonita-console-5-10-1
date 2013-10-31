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
package org.bonitasoft.console.client.view.cases;

import org.bonitasoft.console.client.cases.CaseFilter;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class AdminCaseFilterEditor extends CaseFilterEditor {

  /**
   * Default constructor.
   * 
   * @param aFilter
   * @param aMessageDataSource
   * @param aLabelDataSource 
   */
  public AdminCaseFilterEditor(MessageDataSource aMessageDataSource, CaseFilter aFilter, LabelDataSource aLabelDataSource) {
    super(aMessageDataSource, aFilter,aLabelDataSource);
    myProcessDataSource.getItemFilter().setWithAdminRights(true);
  }

  protected void initContent() {
    super.initContent();
  }
}
