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
package org.bonitasoft.forms.server.api;

import java.io.IOException;
import java.util.Date;

import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.api.impl.FormAdministrationAPIImpl;
import org.bonitasoft.forms.server.api.impl.FormDefinitionAPIImpl;
import org.bonitasoft.forms.server.api.impl.FormExpressionsAPIImpl;
import org.bonitasoft.forms.server.api.impl.FormValidationAPIImpl;
import org.bonitasoft.forms.server.api.impl.FormWorkflowAPIImpl;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;

/**
 * @author Anthony Birembaut
 *
 */
public class FormAPIFactory {
    
    public static IFormExpressionsAPI getFormExpressionsAPI() {
        return new FormExpressionsAPIImpl();
    }
    
    public static IFormWorkflowAPI getFormWorkflowAPI() {
        return new FormWorkflowAPIImpl();
    }
    
    public static IFormValidationAPI getFormValidationAPI() {
        return new FormValidationAPIImpl();
    }
    
    /**
     * @param processDefinitionUUID
     * @param processDeployementDate
     * @param domain
     * @return
     * @throws ProcessNotFoundException
     * @throws IOException
     */
    public static IFormAdministrationAPI getFormAdministrationAPI(final ProcessDefinitionUUID processDefinitionUUID, final Date processDeployementDate) throws ProcessNotFoundException, IOException {
        return FormAdministrationAPIImpl.getInstance(processDefinitionUUID, processDeployementDate);
    }
    
    /**
     * @param document
     * @param processDeployementDate
     * @param locale
     * @param domain
     * @return
     * @throws InvalidFormDefinitionException
     */
    public static IFormDefinitionAPI getFormDefinitionAPI(final FormDocument document, final Date processDeployementDate, final String locale) throws InvalidFormDefinitionException{
        return new FormDefinitionAPIImpl(document, processDeployementDate, locale);
    }
}