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
package org.bonitasoft.forms.server.accessor.widget;

import java.util.List;

import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.bonitasoft.forms.server.exception.NotHandledTypeException;
import org.ow2.bonita.facade.def.element.AttachmentDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;

/**
 * @author Anthony Birembaut
 *
 */
public interface IEngineWidgetBuilder {

    /**
     * create a widget data object
     * @param dataFieldDefinition the {@link DataFieldDefinition}
     * @param widgetIdPrefix a prefix for the id of the widget
     * @param isEditMode 
     * @return a {@link FormWidget} object
     * @throws NotHandledTypeException 
     */
    public FormWidget createWidget(DataFieldDefinition dataFieldDefinition, String widgetIdPrefix, boolean isEditMode) throws NotHandledTypeException;

    /**
     * @param attachment dataFieldDefinition the {@link AttachmentDefinition}
     * @param widgetIdPrefix a prefix for the id of the widget
     * @return a {@link FormWidget} object
     */
    public FormWidget createWidget(AttachmentDefinition attachment, String widgetIdPrefix, boolean isEditMode);

    /**
     * @param pageId the page Id
     * @param nbOfPages the total nb of pages for the page flow
     * @param widgets the full {@link List} of {@link FormWidget} for the page flow (except for the buttons)
     * @param widgetIdPrefix a prefix for the id of the widget
     * @param isEditMode true if the edit page is required, false if it's the view page
     * @param isEditMode 
     * @return the list of form widgets to diplay in the page
     * @throws InvalidFormDefinitionException 
     */
    public List<FormWidget> getPageWidgets(String pageId, int nbOfPages, List<FormWidget> widgets, String widgetIdPrefix, boolean isEditMode) throws InvalidFormDefinitionException;
    
}
