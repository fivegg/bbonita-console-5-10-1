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
package org.bonitasoft.forms.server.validator;

import java.util.Map;

import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 * Abstract class for field and page validators
 * 
 * @author Anthony Birembaut
 *
 */
public abstract class AbstractFormValidator {
    
    public static final String CLICKED_BUTTON_VARNAME = "clickedButton";
    
    private ProcessDefinitionUUID processDefinitionUUID;
    
    private ProcessInstanceUUID processInstanceUUID;
    
    private ActivityInstanceUUID activityInstanceUUID;
    
    private String parameter;
    
    private String submitButtonId;
    
    private Map<String, Object> transientDataContext;

    public ProcessDefinitionUUID getProcessDefinitionUUID() {
        return processDefinitionUUID;
    }

    public ProcessInstanceUUID getProcessInstanceUUID() {
        return processInstanceUUID;
    }

    public ActivityInstanceUUID getActivityInstanceUUID() {
        return activityInstanceUUID;
    }

	public String getParameter() {
		return parameter;
	}

    public void setProcessDefinitionUUID(final ProcessDefinitionUUID processDefinitionUUID) {
        this.processDefinitionUUID = processDefinitionUUID;
    }

    public void setProcessInstanceUUID(final ProcessInstanceUUID processInstanceUUID) {
        this.processInstanceUUID = processInstanceUUID;
    }

    public void setActivityInstanceUUID(final ActivityInstanceUUID activityInstanceUUID) {
        this.activityInstanceUUID = activityInstanceUUID;
    }

	public void setParameter(final String parameter) {
		this.parameter = parameter;
	}

	public Map<String, Object> getTransientDataContext() {
		return transientDataContext;
	}

	public void setTransientDataContext(final Map<String, Object> transientDataContext) {
		this.transientDataContext = transientDataContext;
	}

    public void setSubmitButtonId(String submitButtonId) {
        this.submitButtonId = submitButtonId;
    }

    public String getSubmitButtonId() {
        return submitButtonId;
    }
	
}
