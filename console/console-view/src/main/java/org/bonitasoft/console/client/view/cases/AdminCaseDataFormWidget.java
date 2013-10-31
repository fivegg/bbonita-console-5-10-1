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
package org.bonitasoft.console.client.view.cases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.steps.StepStateWidget;
import org.bonitasoft.forms.client.i18n.FormsResourceBundle;
import org.bonitasoft.forms.client.model.FormFieldValue;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.WidgetType;
import org.bonitasoft.forms.client.model.exception.FileTooBigException;
import org.bonitasoft.forms.client.model.exception.SessionTimeOutException;
import org.bonitasoft.forms.client.rpc.FormsServiceAsync;
import org.bonitasoft.forms.client.view.common.DOMUtils;
import org.bonitasoft.forms.client.view.common.RpcFormsServices;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.widget.FormButtonWidget;
import org.bonitasoft.forms.client.view.widget.FormFieldWidget;
import org.bonitasoft.forms.client.view.widget.FormValidationMessageWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Widget allowing to diplay an admin form for variables modification
 * 
 * @author Anthony Birembaut
 *
 */
public class AdminCaseDataFormWidget extends BonitaPanel implements ModelChangeListener {

    /**
     * class of the element of the page template in witch the title has to be
     * injected
     */
    protected static final String PAGE_LABEL_CLASSNAME = "bonita_form_page_label";
    
    /**
     * class of the element of the page template in witch a message has to be
     * injected
     */
    protected static final String PAGE_MESSAGE_CLASSNAME = "bonita_form_message";
    
    /**
     * action type after a form validation
     */
    protected static enum ACTION_TYPE {
        PREVIOUS, NEXT, SUBMIT
    };

    /**
     * Utility Class form DOM manipulation
     */
    protected DOMUtils domUtils = DOMUtils.getInstance();

    /**
     * index of the currently displayed page in the list of pages ids
     */
    protected int currentPageIndex = 0;
    
    /**
     * the pressed submit button
     */
    protected Button pressedSubmitButton;
    
    /**
     * indicates whether the current page is valid or not
     */
    protected boolean isCurrentPageValid;

    /**
     * list of page ids
     */
    protected List<String> pageIdList;
    
    /**
     * Maintained list of already displayed pages (allow to diplay the previous
     * page without reloading everything)
     */
    protected List<FormPage> formPages = new ArrayList<FormPage>();

    /**
     * Map of form fields already displayed in the page flow
     */
    protected Map<String, FormFieldWidget> fieldWidgets = new HashMap<String, FormFieldWidget>();

    /**
     * Map of form buttons already displayed in the page flow
     */
    protected Map<String, FormButtonWidget> buttonWidgets = new HashMap<String, FormButtonWidget>();
    
    /**
     * Map of field validation widgets
     */
    protected Map<String, FormValidationMessageWidget> fieldValidationWidgets = new HashMap<String, FormValidationMessageWidget>();
    
    /**
     * Map of the flow's widget values
     */
    protected Map<String, FormFieldValue> widgetValues = new HashMap<String, FormFieldValue>();
    
    /**
     * Handler allowing to display a page after the RPC call retrieving its
     * definition
     */
    protected FormPageHandler formsPageHandler = new FormPageHandler();
    
    /**
     * Handler allowing to confirm the variables modification
     */
    protected VariablesModificationHandler variablesModificationHandler = new VariablesModificationHandler();
    
    /**
     * Click Handler dealing with form submission
     */
    protected SubmitClickHandler submitClickHandler = new SubmitClickHandler();
    
    /**
     * Forms RPC services
     */
    protected final FormsServiceAsync formsService = RpcFormsServices.getFormsService();
    
    /**
     * the widget table panel
     */
    protected final FlexTable outerPanel = new FlexTable();
    
    /**
     * the data update panel
     */
    protected final FlowPanel dataUpdatePanel = new FlowPanel();
    
    /**
     * the case whose data has to be displayed
     */
    protected CaseItem caseItem;
    
    /**
     * the step whose data has to be displayed
     */
    protected StepItem stepItem;

    /**
     * the datasource to display messages
     */
    protected MessageDataSource messageDataSource;
    
    /**
     * Constructor
     * @param caseItem
     * @param pageIdList
     */
    public AdminCaseDataFormWidget(CaseItem caseItem, List<String> pageIdList, MessageDataSource messageDataSource) {
        this(caseItem, null, pageIdList, messageDataSource);
    }
    
    /**
     * Constructor
     * @param stepItem
     * @param pageIdList
     */
    public AdminCaseDataFormWidget(StepItem stepItem, List<String> pageIdList, MessageDataSource messageDataSource) {
        this(null, stepItem, pageIdList, messageDataSource);
    }
    
    /**
     * Constructor
     * @param caseItem
     * @param stepItem
     * @param pageIdList
     */
    private AdminCaseDataFormWidget(CaseItem caseItem, StepItem stepItem, List<String> pageIdList, MessageDataSource messageDataSource) {
        super();
        this.caseItem = caseItem;
        this.stepItem = stepItem;
        this.pageIdList = pageIdList;
        this.messageDataSource = messageDataSource;
        initTable();
        initWidget(outerPanel);
    }
    
    /**
     * Build the static part of the UI.
     */
    private void initTable() {
        final String firstRowStle = "admin_case_editor_first_row_column";
        Label topLeft = new Label();
        topLeft.setStyleName("topLeftInner");
        Label topCenter = new Label();
        topCenter.setStyleName("topCenterInner");
        Label topRight = new Label();
        topRight.setStyleName("topRightInner");
        Label bottomLeft = new Label();
        bottomLeft.setStyleName("bottomLeftInner");
        Label bottomRight = new Label();
        bottomRight.setStyleName("bottomRightInner");
        Label bottomCenter = new Label();
        bottomCenter.setStyleName("bottomCenterInner");
        Label middleLeft = new Label();
        middleLeft.setStyleName("middleLeftInner");
        Label middleRight = new Label();
        middleRight.setStyleName("middleRightInner");
        // Build top border.
        outerPanel.getCellFormatter().addStyleName(0, 0, "topLeft");
        outerPanel.getCellFormatter().addStyleName(0, 1, "topCenter");
        outerPanel.getCellFormatter().addStyleName(0, 2, "topRight");
        outerPanel.setWidget(0, 0,topLeft);
        outerPanel.setWidget(0, 1, topCenter);
        outerPanel.setWidget(0, 2, topRight);
        outerPanel.getFlexCellFormatter().setColSpan(0, 1, 5);

        outerPanel.getCellFormatter().addStyleName(1, 0, "middleLeft");
        outerPanel.setWidget(1, 0, middleLeft);
        outerPanel.getCellFormatter().addStyleName(1, 0, "middleLeft");
        outerPanel.getFlexCellFormatter().setStyleName(1, 1, "step_editor_state");
        outerPanel.getFlexCellFormatter().addStyleName(1, 1, firstRowStle);
        outerPanel.getFlexCellFormatter().setStyleName(1, 2, "step_editor_step_name");
        outerPanel.getFlexCellFormatter().addStyleName(1, 2, firstRowStle);
        // the cell (1,3) is filled in by the update method.
        outerPanel.getFlexCellFormatter().setStyleName(1, 3, "step_editor_step_assign");
        outerPanel.getFlexCellFormatter().addStyleName(1, 3, firstRowStle);
        outerPanel.getFlexCellFormatter().setStyleName(1, 4, "step_editor_step_description");
        outerPanel.getFlexCellFormatter().addStyleName(1, 4, firstRowStle);
        outerPanel.getFlexCellFormatter().setStyleName(1, 5, "step_editor_date");
        outerPanel.getFlexCellFormatter().addStyleName(1, 5, firstRowStle);
        outerPanel.getCellFormatter().addStyleName(1, 6, "middleRight");
        outerPanel.setWidget(1, 6, middleRight);
        outerPanel.getFlexCellFormatter().addStyleName(1, 6, firstRowStle);
        if (stepItem != null) {
            outerPanel.setWidget(1, 1, new StepStateWidget(stepItem));
            outerPanel.setWidget(1, 2, new Label(stepItem.getLabel()));
            // the cell (1,3) is filled in by the update method.
            final String theStepDescription = stepItem.getDesc();
            String theShortDesc;
            if (theStepDescription.length() > 50) {
                theShortDesc = theStepDescription.substring(0, 47) + "...";
            } else {
                theShortDesc = theStepDescription;
            }
            outerPanel.setWidget(1, 4, new Label(theShortDesc));
            outerPanel.setWidget(1, 5, new Label(DateTimeFormat.getFormat(constants.dateShortFormat()).format(stepItem.getLastUpdateDate())));
        } else {
            outerPanel.setWidget(1, 2, new Label(caseItem.getProcessUUID().getLabel()));
            // the cell (1,3) is filled in by the update method.
            outerPanel.setWidget(1, 5, new Label(DateTimeFormat.getFormat(constants.dateShortFormat()).format(caseItem.getStartedDate())));
        }
        outerPanel.getCellFormatter().addStyleName(2, 0, "middleLeft");
        outerPanel.setWidget(2, 1, dataUpdatePanel);
        //the content of the data update panel is set by the update method
        outerPanel.getCellFormatter().addStyleName(2, 2, "middleRight");
        outerPanel.getFlexCellFormatter().setColSpan(2, 1, 5);

        outerPanel.getCellFormatter().addStyleName(3, 0, "bottomLeft");
        outerPanel.getCellFormatter().addStyleName(3, 1, "bottomCenter");
        outerPanel.getCellFormatter().addStyleName(3, 2, "bottomRight");
        outerPanel.setWidget(3, 0, bottomLeft);
        outerPanel.setWidget(3, 1, bottomCenter);
        outerPanel.setWidget(3, 2, bottomRight);
        outerPanel.getFlexCellFormatter().setColSpan(3, 1, 5);
        
        outerPanel.setStylePrimaryName("step_editor");
        outerPanel.addStyleName(CSSClassManager.ROUNDED_PANEL);
        outerPanel.setCellPadding(0);
        outerPanel.setCellSpacing(0);
        update();
    }
    
    /**
     * Update the UI.
     */
    protected void update() {
        
        String theAssignValue = null;
        if (stepItem != null) {
            String theCandidates = "";
            Set<UserUUID> theSetOfCandidates = stepItem.getAssign();
            for (Iterator<UserUUID> theIterator = theSetOfCandidates.iterator(); theIterator.hasNext();) {
                UserUUID theCandidate = theIterator.next();
                theCandidates = theCandidates + "," + theCandidate.toString();
            }
            if (theCandidates.length() > 1) {
                // Remove the extra comma at the beginning of the string.
                theCandidates = theCandidates.substring(1);
            }
            theAssignValue = "(" + theCandidates + ")";
        } else {
            theAssignValue = "(" + constants.caseStartedBy() + caseItem.getStartedBy().getValue() + ")";
        }
        // Finally layout widgets.
        outerPanel.setWidget(1, 3, new Label(theAssignValue));
        displayPage(currentPageIndex);
    }

    /**
     * Display the page at the given index
     * 
     * @param index
     *            index of the page in the page list
     */
    private void displayPage(int index) throws IndexOutOfBoundsException {

        if (index >= 0 && index < pageIdList.size()) {
            currentPageIndex = index;
            try {
                FormPage formPage = formPages.get(index);
                buildPage(formPage, true);
            } catch (IndexOutOfBoundsException e) {
                if (caseItem != null) {
                    formsService.getProcessInstanceAdminFormPage(caseItem.getUUID().getValue(), pageIdList.get(index), formsPageHandler);
                } else {
                    formsService.getTaskAdminFormPage(stepItem.getUUID().getValue(), pageIdList.get(index), formsPageHandler);
                }
            }
        } else {
            throw new IndexOutOfBoundsException("No page was found for index " + index);
        }
    }

    /**
     * Build the page (template + form fields)
     * 
     * @param formPage
     *            the page definition
     * @param hasAlreadyBeenDisplayed
     *            indicates whether the page has already been displayed or not
     */
    private void buildPage(FormPage formPage, boolean hasAlreadyBeenDisplayed) {
        dataUpdatePanel.clear();
        Label pageLabel = new Label();
        if (formPage.getFormWidgets().size() <= 1) {
            pageLabel.setText(constants.noVariablesForStep());
            pageLabel.setStyleName(PAGE_MESSAGE_CLASSNAME);
            dataUpdatePanel.add(pageLabel);
        } else {
            pageLabel.setText(formPage.getPageLabel());
            pageLabel.setStyleName(PAGE_LABEL_CLASSNAME);
            dataUpdatePanel.add(pageLabel);
            buildform(formPage, hasAlreadyBeenDisplayed);
        }
    }

    /**
     * Insert the widgets in the page for the edit mode
     * 
     * @param formPage
     *            the page definition
     * @param hasAlreadyBeenDisplayed
     *            indicates whether the page has already been displayed or not
     */
    /**
     * @param formPage
     * @param hasAlreadyBeenDisplayed
     */
    /**
     * @param formPage
     * @param hasAlreadyBeenDisplayed
     */
    private void buildform(FormPage formPage, boolean hasAlreadyBeenDisplayed) {
        
        List<FormWidget> formWidgets = formPage.getFormWidgets();
        FlowPanel buttonsPanel = new FlowPanel();
        for (FormWidget formWidgetData : formWidgets) {
            String widgetId = formWidgetData.getId();
            // for buttons
            if (formWidgetData.getType().name().startsWith("BUTTON")) {
                FormButtonWidget formButtonWidget = null;
                if (hasAlreadyBeenDisplayed) {
                    formButtonWidget = buttonWidgets.get(widgetId);
                } else {
                    setButtonLabel(formWidgetData);
                    formButtonWidget = new FormButtonWidget(formWidgetData);
                    addClickListener(formButtonWidget);
                    buttonWidgets.put(widgetId, formButtonWidget);
                }
                formButtonWidget.addStyleName("bonita_form_button_entry");
                if (buttonsPanel.getWidgetCount() == 0) {
                    dataUpdatePanel.add(buttonsPanel);
                    buttonsPanel.setStyleName("bonita_form_button_container");
                }
                buttonsPanel.add(formButtonWidget);
            // for form fields (Widgets other that buttons and text)
            } else {
                FormFieldWidget formFieldWidget = null;
                if (hasAlreadyBeenDisplayed) {
                    formFieldWidget = fieldWidgets.get(widgetId);
                } else {
                    Map<String, Object> urlContext = new HashMap<String, Object>();
                    if (stepItem != null) {
                        urlContext.put(URLUtils.TASK_ID_PARAM, stepItem.getUUID().getValue());
                        formFieldWidget = new FormFieldWidget(formWidgetData, urlContext, null, null);
                    } else {
                        urlContext.put(URLUtils.INSTANCE_ID_PARAM, caseItem.getUUID().getValue());
                        formFieldWidget = new FormFieldWidget(formWidgetData, urlContext, null, null);
                    }
                    fieldWidgets.put(widgetId, formFieldWidget);
                }
                formFieldWidget.addStyleName("bonita_form_entry");
                dataUpdatePanel.add(formFieldWidget);
            }
        }
    }
    
    /**
     * Set a button's label and title
     * @param formWidgetData
     */
    private void setButtonLabel(FormWidget formWidgetData) {
        
        if (formWidgetData.getType() == WidgetType.BUTTON_PREVIOUS) {
            String previousButtonLabel = constants.previousPageButtonLabel();
            formWidgetData.setLabel(previousButtonLabel);
            String previousButtonTitle = constants.previousPageButtonTitle();
            formWidgetData.setTitle(previousButtonTitle);
        } else if (formWidgetData.getType() == WidgetType.BUTTON_NEXT) {
            String nextButtonLabel = constants.nextPageButtonLabel();
            formWidgetData.setLabel(nextButtonLabel);
            String nextButtonTitle = constants.nextPageButtonTitle();
            formWidgetData.setTitle(nextButtonTitle);
        } else if (formWidgetData.getType() == WidgetType.BUTTON_SUBMIT) {
            String submitButtonLabel = constants.submitButtonLabel();
            formWidgetData.setLabel(submitButtonLabel);
            String submitButtonTitle = constants.submitButtonTitle();
            formWidgetData.setTitle(submitButtonTitle);
        }
    }

    /**
     * Associate a button with the correct click handler
     * 
     * @param formButtonWidget
     */
    private void addClickListener(FormButtonWidget formButtonWidget) {

        WidgetType buttonType = formButtonWidget.getWidgetType();
        switch (buttonType) {
        case BUTTON_PREVIOUS:
            formButtonWidget.addClickHandler(new PreviousPageClickHandler());
            break;
        case BUTTON_NEXT:
            formButtonWidget.addClickHandler(new NextPageClickHandler());
            break;
        case BUTTON_SUBMIT:
            formButtonWidget.addClickHandler(submitClickHandler);
            break;
        }
    }

    /**
     * Handler allowing to display the form
     */
    class FormPageHandler implements AsyncCallback<FormPage> {

        /**
         * {@inheritDoc}
         */
        public void onFailure(final Throwable t) {
            
            if (t instanceof SessionTimeOutException) {
                Window.Location.reload();
            } else {
                String errorMessage = FormsResourceBundle.getErrors().pageRetrievalError();
                messageDataSource.addErrorMessage(errorMessage);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onSuccess(final FormPage formPage) {
            formPages.add(formPage);
            buildPage(formPage, false);
        }
    }

    /**
     * Handler for the next page button
     */
    class NextPageClickHandler implements ClickHandler {

        /**
         * hide the current page and display the next page
         */
        public void onClick(ClickEvent event) {

            validatePage(ACTION_TYPE.NEXT);
        }
    }

    /**
     * Handler for the previous page button
     */
    class PreviousPageClickHandler implements ClickHandler {

        /**
         * hide the current page and display the previous page
         */
        public void onClick(ClickEvent event) {
            
        	validatePage(ACTION_TYPE.PREVIOUS);
        }
    }

    /**
     * Handler for the submission of the form
     */
    class SubmitClickHandler implements ClickHandler {

        /**
         * submit the form
         */
        public void onClick(ClickEvent event) {
            
            pressedSubmitButton = (Button)event.getSource();
            pressedSubmitButton.setEnabled(false);
            validatePage(ACTION_TYPE.SUBMIT);
        }
    }

    /**
     * Validate a page and its fields
     * 
     * @param actionAfterValidation
     *            type of action to execute after the validation step
     */
    private void validatePage(ACTION_TYPE actionAfterValidation) {

        isCurrentPageValid = true;

        FormPage formPage = formPages.get(currentPageIndex);

        List<FormWidget> formWidgetsToValidate = new ArrayList<FormWidget>();

        // evaluates the number of widget validations to performs and store the
        // widgets to validate
        for (FormWidget formWidget : formPage.getFormWidgets()) {
            String widgetId = formWidget.getId();
            if (fieldWidgets.containsKey(widgetId)) {
                FormFieldValue widgetValue = fieldWidgets.get(widgetId).getValue();
                widgetValues.put(widgetId, widgetValue);
                if (!formWidget.getValidators().isEmpty()) {
                    formWidgetsToValidate.add(formWidget);
                }
            }
        }

        // fields validation
        if (!formWidgetsToValidate.isEmpty()) {
        	Map<String, List<FormValidator>> validators = new HashMap<String, List<FormValidator>>();
            for (FormWidget formWidget : formWidgetsToValidate) {
            	cleanValidatorsMessages(formWidget.getValidators());
            	validators.put(formWidget.getId(), formWidget.getValidators());
            }
            if (caseItem != null) {
            	formsService.validateInstanceFieldsAdmin(caseItem.getUUID().getValue(), validators, widgetValues, new FormFieldValidatorHandler(actionAfterValidation));
            } else {
            	formsService.validateTaskFieldsAdmin(stepItem.getUUID().getValue(), validators, widgetValues, new FormFieldValidatorHandler(actionAfterValidation));
            }
        } else {
            executeActions(actionAfterValidation);
        }
    }

    /**
     * Remove the validation messages of the given validators from the page
     * 
     * @param validators
     */
    private void cleanValidatorsMessages(List<FormValidator> validators) {
        
        for (FormValidator formValidator : validators) {
            if(fieldValidationWidgets.get(formValidator.getId()) != null) {
                dataUpdatePanel.remove(fieldValidationWidgets.get(formValidator.getId()));
            }
        }
    }

    /**
     * Handler allowing to validate a form field
     */
    class FormFieldValidatorHandler implements AsyncCallback<Map<String, List<FormValidator>>> {
    	
        private ACTION_TYPE actionAfterValidation;

        public FormFieldValidatorHandler(ACTION_TYPE actionAfterValidation) {

            this.actionAfterValidation = actionAfterValidation;
        }

        /**
         * {@inheritDoc}
         */
        public void onFailure(final Throwable t) {
            
            if (t instanceof SessionTimeOutException) {
                Window.Location.reload();
            } else {
                String errorMessage = FormsResourceBundle.getErrors().fieldValidationError();
                messageDataSource.addErrorMessage(errorMessage);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onSuccess(final Map<String, List<FormValidator>> fieldValidatorsMap) {

            if (!fieldValidatorsMap.isEmpty()) {
            	for (Entry<String, List<FormValidator>> fieldValidatorsEntry : fieldValidatorsMap.entrySet()) {
                    // set the focus on the first invalid form field
            		FormFieldWidget fieldWidget = fieldWidgets.get(fieldValidatorsEntry.getKey());
                    if (isCurrentPageValid) {
                        if (fieldWidget != null) {
                            fieldWidget.setFocusOn();
                        }
                        isCurrentPageValid = false;
                    }
                    for (FormValidator fieldValidator : fieldValidatorsEntry.getValue()) {
                        FormValidationMessageWidget formValidationMessageWidget = new FormValidationMessageWidget(fieldValidator, false);
                        fieldValidationWidgets.put(fieldValidator.getId(), formValidationMessageWidget);
                        int fieldWidgetIndex = dataUpdatePanel.getWidgetIndex(fieldWidget);
                        dataUpdatePanel.insert(formValidationMessageWidget, fieldWidgetIndex + 1);
                    }
				}
            }
            // action
            if (isCurrentPageValid) {
                executeActions(actionAfterValidation);
            } else {
                if (pressedSubmitButton != null && actionAfterValidation.compareTo(ACTION_TYPE.SUBMIT) == 0) {
                    pressedSubmitButton.setEnabled(true);
                }
            }
        }
    }
    
    private void executeActions(ACTION_TYPE actionAfterValidation) {
        
        if (actionAfterValidation.compareTo(ACTION_TYPE.SUBMIT) == 0) {
            if (caseItem != null) {
                formsService.executeProcessAdminActions(caseItem.getUUID().getValue(), widgetValues, variablesModificationHandler);
            } else {
                formsService.executeTaskAdminActions(stepItem.getUUID().getValue(), widgetValues, variablesModificationHandler);
            }
        } else {
            int newIndex = currentPageIndex;
            if (actionAfterValidation.compareTo(ACTION_TYPE.PREVIOUS) == 0) {
                newIndex--;
            } else if (actionAfterValidation.compareTo(ACTION_TYPE.NEXT) == 0) {
                newIndex++;
            }
            try {
                displayPage(newIndex);
            } catch (IndexOutOfBoundsException e) {
                String errorMessage = FormsResourceBundle.getErrors().pageIndexError(newIndex);
                messageDataSource.addErrorMessage(errorMessage);
            }
        }
    }
    
    class VariablesModificationHandler implements AsyncCallback<Void> {

        /**
         * {@inheritDoc}
         */
        public void onFailure(Throwable t) {
            
            if (t instanceof FileTooBigException) {
                final String fileName = ((FileTooBigException)t).getFileName();
                final String maxSize =  ((FileTooBigException)t).getMaxSize();
                if (fileName != null) {
                    Window.alert(FormsResourceBundle.getErrors().fileTooBigErrorWithNameAndSize(fileName, maxSize));
                } else {
                    Window.alert(FormsResourceBundle.getErrors().fileTooBigError());
                }
            } else if (t instanceof SessionTimeOutException) {
                Window.Location.reload();
            } else {
                String errorMessage = FormsResourceBundle.getErrors().formSubmissionError();
                messageDataSource.addErrorMessage(errorMessage);
            }
            if (pressedSubmitButton != null) {
                pressedSubmitButton.setEnabled(true);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onSuccess(Void event) {
            
            if (pressedSubmitButton != null) {
                pressedSubmitButton.setEnabled(true);
            }
            if (currentPageIndex > 0) {
                displayPage(0);
            }
            messageDataSource.addInfoMessage(messages.caseVariablesModified());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void modelChange(ModelChangeEvent anEvent) {
        update();
    }
}
