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
package org.bonitasoft.console.client.view;

import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.model.MessageDataSource;

import com.google.gwt.user.client.ui.HTML;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class SimpleFilterEditor<F extends ItemFilter> extends ItemFilterEditor<F> {

  

  private HTML myTooltip;

  /**
   * Default constructor.
   * 
   * @param aFilter
   */
  public SimpleFilterEditor(MessageDataSource aMessageDataSource, F aFilter, String aNaturalSearchToolTip) {
    super(aMessageDataSource, aFilter);
    if(aNaturalSearchToolTip != null && aNaturalSearchToolTip.length() >0) {
      myTooltip = new HTML(aNaturalSearchToolTip);
    } else {
      myTooltip = null;
    }
    initContent();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.view.ItemFilterEditor#initContent()
   */
  @Override
  protected void initContent() {
    myFilterContentPanel.add(createNaturalSearchElement(myTooltip));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.events.ModelChangeListener#modelChange(org
   * .bonitasoft.console.client.events.ModelChangeEvent)
   */
  public void modelChange(ModelChangeEvent aEvt) {
    // TODO Auto-generated method stub

  }

}
