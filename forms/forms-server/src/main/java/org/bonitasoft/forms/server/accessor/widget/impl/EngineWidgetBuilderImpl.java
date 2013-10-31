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
package org.bonitasoft.forms.server.accessor.widget.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.forms.client.model.FormFieldAvailableValue;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.FormWidget.ItemPosition;
import org.bonitasoft.forms.client.model.WidgetType;
import org.bonitasoft.forms.server.accessor.DefaultFormsProperties;
import org.bonitasoft.forms.server.accessor.DefaultFormsPropertiesFactory;
import org.bonitasoft.forms.server.accessor.widget.IEngineWidgetBuilder;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.bonitasoft.forms.server.exception.NotHandledTypeException;
import org.bonitasoft.forms.server.validator.CharFieldValidator;
import org.bonitasoft.forms.server.validator.DateFieldValidator;
import org.bonitasoft.forms.server.validator.NumericDoubleFieldValidator;
import org.bonitasoft.forms.server.validator.NumericFloatFieldValidator;
import org.bonitasoft.forms.server.validator.NumericIntegerFieldValidator;
import org.bonitasoft.forms.server.validator.NumericLongFieldValidator;
import org.bonitasoft.forms.server.validator.NumericShortFieldValidator;
import org.ow2.bonita.facade.def.element.AttachmentDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;

/**
 * @author Anthony Birembaut
 *
 */
public class EngineWidgetBuilderImpl implements IEngineWidgetBuilder {

    /**
     * validator id suffix
     */
    protected static final String VALIDATOR_ID_SUFFIX = "_validator";
    
    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(EngineWidgetBuilderImpl.class.getName());
    
    /**
     * Protected contructor to prevent instantiation
     * @param domain Domain name
     */
    protected EngineWidgetBuilderImpl() {
    }
    
    /**
     * {@inheritDoc}
     */
    public FormWidget createWidget(final DataFieldDefinition dataFieldDefinition, final String widgetIdPrefix, final boolean isEditMode) throws NotHandledTypeException {
        
        final String dataFieldLabel = dataFieldDefinition.getLabel();
        final String dataFieldName = dataFieldDefinition.getName();
        final String dataFieldDescription = dataFieldDefinition.getDescription();
        final String dataFieldClassName = dataFieldDefinition.getDataTypeClassName();
        
        final String id = widgetIdPrefix + "_" + dataFieldName;
        String label = null;
        if (dataFieldLabel != null && dataFieldLabel.length() != 0) {
            label = dataFieldLabel;
        } else {
            label = dataFieldName;
        }
        String title = null;
        if (dataFieldDescription != null && dataFieldDescription.length() != 0)
            title = dataFieldDescription;
        else {
            title = label;
        }
        WidgetType type = null;
        final List<FormValidator> fieldValidators = new ArrayList<FormValidator>();
        final List<FormFieldAvailableValue> formFieldAvailableValues = new ArrayList<FormFieldAvailableValue>();
        if (dataFieldDefinition.isEnumeration()) {
            type = WidgetType.LISTBOX_SIMPLE;
            final Set<String> enumerationValues = dataFieldDefinition.getEnumerationValues();
            for (final String enumerationValue : enumerationValues) {
                formFieldAvailableValues.add(new FormFieldAvailableValue(enumerationValue, enumerationValue));
            }
        } else {
            if (Date.class.getName().equals(dataFieldClassName)) {
                type = WidgetType.DATE;
                final String dateFieldValidatorDefaultLabel = "#dateFieldValidatorLabel";
                fieldValidators.add(new FormValidator(id + VALIDATOR_ID_SUFFIX, DateFieldValidator.class.getName(), dateFieldValidatorDefaultLabel, null, null));
            } else if (Boolean.class.getName().equals(dataFieldClassName)) {
                type = WidgetType.CHECKBOX;
            } else if (Long.class.getName().equals(dataFieldClassName)) {
                type = WidgetType.TEXTBOX;
                final String longFieldValidatorDefaultLabel = "#numericLongFieldValidatorLabel";
                fieldValidators.add(new FormValidator(id + VALIDATOR_ID_SUFFIX, NumericLongFieldValidator.class.getName(), longFieldValidatorDefaultLabel, null, null));
            } else if (Double.class.getName().equals(dataFieldClassName)) {
                type = WidgetType.TEXTBOX;
                final String doubleFieldValidatorDefaultLabel = "#numericDoubleFieldValidatorLabel";
                fieldValidators.add(new FormValidator(id + VALIDATOR_ID_SUFFIX, NumericDoubleFieldValidator.class.getName(), doubleFieldValidatorDefaultLabel, null, null));
            } else if (Integer.class.getName().equals(dataFieldClassName)) {
                type = WidgetType.TEXTBOX;
                final String integerFieldValidatorDefaultLabel = "#numericIntegerFieldValidatorLabel";
                fieldValidators.add(new FormValidator(id + VALIDATOR_ID_SUFFIX, NumericIntegerFieldValidator.class.getName(), integerFieldValidatorDefaultLabel, null, null));
            } else if (Float.class.getName().equals(dataFieldClassName)) {
                type = WidgetType.TEXTBOX;
                final String floatFieldValidatorDefaultLabel = "#numericFloatFieldValidatorLabel";
                fieldValidators.add(new FormValidator(id + VALIDATOR_ID_SUFFIX, NumericFloatFieldValidator.class.getName(), floatFieldValidatorDefaultLabel, null, null));
            } else if (Short.class.getName().equals(dataFieldClassName)) {
                type = WidgetType.TEXTBOX;
                final String shortFieldValidatorDefaultLabel = "#numericShortFieldValidatorLabel";
                fieldValidators.add(new FormValidator(id + VALIDATOR_ID_SUFFIX, NumericShortFieldValidator.class.getName(), shortFieldValidatorDefaultLabel, null, null));
            } else if (Character.class.getName().equals(dataFieldClassName)) {
                type = WidgetType.TEXTBOX;
                final String charFieldValidatorDefaultLabel = "#charFieldValidatorLabel";
                fieldValidators.add(new FormValidator(id + VALIDATOR_ID_SUFFIX, CharFieldValidator.class.getName(), charFieldValidatorDefaultLabel, null, null));
            } else if (String.class.getName().equals(dataFieldClassName)) {
                type = WidgetType.TEXTBOX;
            } else {
                final String message = "Type " + dataFieldClassName + " is not handled by the automatic form generation.";
                throw new NotHandledTypeException(message);
            }
        }

        final FormWidget formWidget = new FormWidget(id, label + " :", 0, 0, type, null, null, null, ItemPosition.LEFT, title, false);
        formWidget.setVariableBound("${" + dataFieldName + "}");
        formWidget.setInitialValueExpression("${" + dataFieldName + "}");
        
        formWidget.setValidators(fieldValidators);
        if (WidgetType.LISTBOX_SIMPLE.compareTo(type) == 0) {
            formWidget.setAvailableValues(formFieldAvailableValues);
        }
        
        if (!isEditMode) {
            formWidget.setReadOnly(true);
        } else {
            formWidget.setReadOnly(false);
        }
        return formWidget;
    }
    

    /**
     * {@inheritDoc}
     */
    public FormWidget createWidget(final AttachmentDefinition attachment, final String widgetIdPrefix, final boolean isEditMode) {
        
        final String attachmentLabel = attachment.getLabel();
        final String attachmentName = attachment.getName();
        final String attachmentDescription = attachment.getDescription();
        
        final String id = widgetIdPrefix + "_" + attachmentName;
        String label = null;
        if (attachmentLabel != null && attachmentLabel.length() != 0) {
            label = attachmentLabel;
        } else {
            label = attachmentName;
        }
        String title = null;
        if (attachmentDescription != null && attachmentDescription.length() != 0)
            title = attachmentDescription;
        else {
            title = label;
        }
        WidgetType widgetType = null;
        if (!isEditMode) {
            widgetType = WidgetType.FILEDOWNLOAD;
        } else {
            widgetType = WidgetType.FILEUPLOAD;
        }
        final FormWidget formWidget = new FormWidget(id, label + " :", 0, 0, widgetType, null, null, null, ItemPosition.LEFT, title, false);
        formWidget.setVariableBound(attachmentName);
        formWidget.setInitialValueExpression(attachmentName);
        
        return formWidget;
    }

    /**
     * {@inheritDoc}
     */
    public List<FormWidget> getPageWidgets(final String pageId, final int nbOfPages, final List<FormWidget> widgets, final String widgetIdPrefix, final boolean isEditMode) throws InvalidFormDefinitionException {
        final List<FormWidget> pageWidgets = new ArrayList<FormWidget>();
        final int pageIndex = Integer.parseInt(pageId);
        final int lastPageIndex = nbOfPages - 1;
        if (pageIndex >= 0 && pageIndex <= nbOfPages) {
            final DefaultFormsProperties defaultProperties = DefaultFormsPropertiesFactory.getDefaultFormProperties();
            final int startWidgetIndex = pageIndex * defaultProperties.getMaxWigdetPerPage();
            for (int widgetIndex = startWidgetIndex; widgetIndex < startWidgetIndex + defaultProperties.getMaxWigdetPerPage(); widgetIndex++) {
                try {
                    final FormWidget formWidget = widgets.get(widgetIndex);
                    pageWidgets.add(formWidget);
                } catch (final IndexOutOfBoundsException e) {
                    //do nothing
                }
            }
            String id = null;
            String label = null;
            String title = null;
            if (pageIndex > 0) {
                id = widgetIdPrefix + "_" + pageId + "_previous";
                label = "#previousPageButtonLabel";
                title = "#previousPageButtonTitle";
                final FormWidget previousButton = new FormWidget(id, label, 0, 0, WidgetType.BUTTON_PREVIOUS, null, null, null, null, title, false);
                pageWidgets.add(previousButton);
            }
            if (pageIndex < lastPageIndex) {
                id = widgetIdPrefix + "_" + pageId + "_next";
                label = "#nextPageButtonLabel";
                title = "#nextPageButtonTitle";
                final FormWidget nextButton = new FormWidget(id, label, 0, 0, WidgetType.BUTTON_NEXT, null, null, null, null, title, false);
                pageWidgets.add(nextButton);
            }
            if (pageIndex == lastPageIndex){
                id = widgetIdPrefix + "_submit";
                label = "#submitButtonLabel";
                title = "#submitButtonTitle";
                final FormWidget submitButton = new FormWidget(id, label, 0, 0, WidgetType.BUTTON_SUBMIT, null, null, null, null, title, false);
                pageWidgets.add(submitButton);
            }
        } else {
            final String errorMessage = "Page " + pageId + " not found.";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Page " + pageId + " not found.");
            }
            throw new InvalidFormDefinitionException(errorMessage);
        }
        return pageWidgets;
    }

}
