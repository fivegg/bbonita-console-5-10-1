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
import java.util.List;

/**
 * Form page data
 * 
 * @author Anthony Birembaut
 */
public class FormPage implements Serializable{

    /**
     * UID
     */
    private static final long serialVersionUID = -3003458547469536846L;

    /**
     * the page ID
     */
    private String pageId;
    
    /**
     * the page label
     */
    private String pageLabel;
    
    /**
     * Indicates if the page is in edit mode or not
     */
    private FormType formType;
    
    /**
     * if true indicates that HTML is allowed in the label
     */
    private boolean allowHTMLInLabel;
    
    /**
     * the page template
     */
    private HtmlTemplate pageTemplate;
    
    /**
     * the page widgets
     */
    private List<FormWidget> formWidgets;
    
    /**
     * the page validators
     */
    private List<FormValidator> pageValidators;
    
    /**
     * The expression to evaluate for the next page of the page flow
     */
    private String nextPageExpression;
    
    /**
     * Constructor
     * @param pageId
     * @param pageLabel
     * @param pageTemplate
     * @param formWidgets
     * @param pageValidators
     * @param formType
     */
    public FormPage(final String pageId, final String pageLabel, final HtmlTemplate pageTemplate, final List<FormWidget> formWidgets, final List<FormValidator> pageValidators, final FormType formType) {
    	this.formType = formType;
        this.pageTemplate = pageTemplate;
        this.formWidgets = formWidgets;
        this.pageValidators = pageValidators;
        this.pageId = pageId;
        this.pageLabel = pageLabel;
    }
    
    /**
     * Constructor
     * @param pageId
     * @param pageLabel
     * @param pageTemplate
     * @param formWidgets
     * @param pageValidators
     * @param formType
     * @param allowHTMLInLabel
     */
    public FormPage(final String pageId, final String pageLabel, final HtmlTemplate pageTemplate, final List<FormWidget> formWidgets, final List<FormValidator> pageValidators, final FormType formType, final boolean allowHTMLInLabel) {
        this(pageId, pageLabel, pageTemplate, formWidgets, pageValidators, formType);
        this.allowHTMLInLabel = allowHTMLInLabel;
    }
    
    /**
     * Default Constructor
     */
    public FormPage() {
        super();
        // Mandatory for serialization
    }
    
    public String getPageId() {
        return pageId;
    }

    public void setPageId(final String pageId) {
        this.pageId = pageId;
    }
    
    public HtmlTemplate getPageTemplate() {
        return pageTemplate;
    }

    public void setPageTemplate(final HtmlTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    public List<FormWidget> getFormWidgets() {
        return formWidgets;
    }

    public void setFormWidgets(final List<FormWidget> formWidgets) {
        this.formWidgets = formWidgets;
    }

    public List<FormValidator> getPageValidators() {
        return pageValidators;
    }

    public void setPageValidators(final List<FormValidator> pageValidators) {
        this.pageValidators = pageValidators;
    }

	public String getPageLabel() {
        return pageLabel;
    }

    public void setPageLabel(final String pageLabel) {
        this.pageLabel = pageLabel;
    }

    public boolean allowHTMLInLabel() {
        return allowHTMLInLabel;
    }

    public void setAllowHTMLInLabel(final boolean allowHTMLInLabel) {
        this.allowHTMLInLabel = allowHTMLInLabel;
    }

    public String getNextPageExpression() {
        return nextPageExpression;
    }

    public void setNextPageExpression(final String nextPageExpression) {
        this.nextPageExpression = nextPageExpression;
    }

    public FormType getFormType() {
        return formType;
    }

    public void setFormType(FormType formType) {
        this.formType = formType;
    }
}
