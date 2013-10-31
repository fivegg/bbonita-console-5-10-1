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
package org.bonitasoft.forms.server.accessor;

import java.util.List;

import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;

/**
 * Accessor API allowing to retrieve a Form definition for an activity or a process instantiation
 * 
 * @deprecated
 * @author Anthony Birembaut
 *
 */
public interface IFormDefAccessor {
    
    /**
     * @return a list of ids of the pages of the page flow 
     */
    List<String> getPages();

    /**
     * @param pageId the page ID
     * @return the path of the form page template in the classpath
     * @throws InvalidFormDefinitionException 
     */
    String getFormPageTemplate(String pageId) throws InvalidFormDefinitionException;

    /**
     * @return the path of the page flow confirmation page in the classpath
     */
    String getConfirmationTemplate();
    
    /**
     * @return the message for the confirmation template
     */
    String getConfirmationMessage();

    /**
     * @param pageId the page ID
     * @return the page label
     * @throws InvalidFormDefinitionException if the page or its label cannot be found
     */
    String getPageLabel(String pageId) throws InvalidFormDefinitionException;

    /**
     * @param pageId the page ID
     * @return true if HTML is allowed in the page label, false otherwise
     * @throws InvalidFormDefinitionException 
     */
    boolean isHTMLAllowedInLabel(String pageId) throws InvalidFormDefinitionException;
    
    /**
     * @param pageId the page ID
     * @return a list of {@link FormWidget} composing the page
     * @throws InvalidFormDefinitionException 
     */
    List<FormWidget> getPageWidgets(String pageId) throws InvalidFormDefinitionException;

    /**
     * Retrieve the list of actions associated with the required page
     * @param pageId the pages from which the actions are required 
     * @return a list of {@link FormAction}
     * @throws InvalidFormDefinitionException if the activity/process or its actions cannot be found
     */
    List<FormAction> getActions(String pageId) throws InvalidFormDefinitionException;

    /**
     * Retrieve the page validators
     * @param pageId the page ID
     * @return a List of {@link FormValidator}
     * @throws InvalidFormDefinitionException
     */
    List<FormValidator> getPageValidators(String pageId) throws InvalidFormDefinitionException;
    
    /**
     * Retrieve the list of transient data for the page flow
     * @return a List of {@link TransientData}
     * @throws InvalidFormDefinitionException 
     */
    List<TransientData> getTransientData() throws InvalidFormDefinitionException;

    /**
     * Retrieve the expression to evaluate to display the first page of a page flow
     * @return an expression to evaluate
     * @throws InvalidFormDefinitionException
     */
    String getFirstPageExpression() throws InvalidFormDefinitionException;
    
    /**
     * Retrieve the expression to evaluate to get the page after the current page
     * @param pageId current page
     * @return an expression to evaluate
     * @throws InvalidFormDefinitionException
     */
    String getNextPageExpression(String pageId) throws InvalidFormDefinitionException;
    
}