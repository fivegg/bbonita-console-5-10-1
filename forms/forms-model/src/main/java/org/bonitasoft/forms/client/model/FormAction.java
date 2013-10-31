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
package org.bonitasoft.forms.client.model;

import java.io.Serializable;

/**
 * An action to execute at form submission
 * 
 * @author Anthony Birembaut
 */
public class FormAction implements Serializable {
    
    /**
     * UID
     */
    private static final long serialVersionUID = 1265444634263925221L;

    /**
     * type of action
     */
    private ActionType type;
    
    /**
     * variable type
     */
    private String variableType;
    
    /**
     * variable ID
     */
    private String variableId;
    
    /**
     * attachment name for SET_ATTACHMENT actions
     */
    private String attachmentName;
    
    /**
     * expression
     */
    private String expression;
    
    /**
     * if not null, the action has to be executed only if the button with the corresponding ID has been pressed
     */
    private String submitButtonId;

    /**
     * connectors to execute for EXECUTE_CONNECTOR actions
     */
    private Connector connector;

    /**
     * Constructor
     * @param type
     * @param variableId
     * @param variableType
     * @param expression
     * @param submitButtonId
     * @param attachmentName
     */
    public FormAction(final ActionType type, final String variableId, final String variableType, final String expression, final String submitButtonId, final String attachmentName) {
        this.type = type;
        this.variableId = variableId;
        this.variableType = variableType;
        this.attachmentName = attachmentName;
        this.expression = expression;
        this.submitButtonId = submitButtonId;
    }
    
    /**
     * Constructor
     * @param type
     * @param variableId
     * @param variableType
     * @param expression
     * @param submitButtonId
     */
    public FormAction(final ActionType type, final String variableId, final String variableType, final String expression, final String submitButtonId) {
        this.type = type;
        this.variableId = variableId;
        this.variableType = variableType;
        this.expression = expression;
        this.submitButtonId = submitButtonId;
    }
    
    /**
     * Default constructor
     */
    public FormAction() {
        super();
       // Mandatory for serialization
    }

    public ActionType getType() {
        return type;
    }

    public void setType(final ActionType type) {
        this.type = type;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(final String variableId) {
        this.variableId = variableId;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setVariableType(final String variableType) {
        this.variableType = variableType;
    }

    public String getSubmitButtonId() {
        return submitButtonId;
    }

    public void setSubmitButtonId(final String submitButtonId) {
        this.submitButtonId = submitButtonId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(final String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(final Connector connector) {
        this.connector = connector;
    }

}
