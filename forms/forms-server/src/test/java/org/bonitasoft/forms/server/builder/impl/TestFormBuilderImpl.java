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
package org.bonitasoft.forms.server.builder.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bonitasoft.forms.client.model.ActionType;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormValidator;
import org.bonitasoft.forms.client.model.FormWidget;
import org.bonitasoft.forms.client.model.FormWidget.ItemPosition;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.client.model.WidgetType;
import org.bonitasoft.forms.server.FormsTestCase;
import org.bonitasoft.forms.server.accessor.impl.EngineApplicationFormDefAccessorImpl;
import org.bonitasoft.forms.server.accessor.impl.XMLApplicationConfigDefAccessorImpl;
import org.bonitasoft.forms.server.accessor.impl.XMLApplicationFormDefAccessorImpl;
import org.bonitasoft.forms.server.accessor.impl.util.FormDocument;
import org.bonitasoft.forms.server.builder.IFormBuilder;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test for the implementation of the form builder
 * 
 * @author Anthony Birembaut
 * 
 */
public class TestFormBuilderImpl extends FormsTestCase {

    private IFormBuilder formBuilder;

    @Before
    public void setUp() throws Exception {
        formBuilder = FormBuilderImpl.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGenerateSimpleFormXML() throws Exception {
        formBuilder.createFormDefinition();
        formBuilder.addApplication("processName", "1.0");
        formBuilder.addLabel("process label");
        formBuilder.addLayout("/process-template.html");
        Assert.assertNotNull(formBuilder.done());
    }

    private File buildComplexFormXML() throws Exception {
        formBuilder.createFormDefinition();
        formBuilder.addWelcomePage("resources/application/welcome.html");
        formBuilder.addHomePage("resources/application/BonitaApplication.html");
        formBuilder.addExternalWelcomePage("resources/application/external-welcome.html");
        formBuilder.addMigrationProductVersion("5.6");
        formBuilder.addApplication("processName", "1.0");
        formBuilder.addLabel("process label with accents éèà");
        formBuilder.addLayout("/process-template.html");
        formBuilder.addPermissions("application#test");

        formBuilder.addEntryForm("processName--1.0$entry");
        formBuilder.addPermissions("process#processName--1.0");
        formBuilder.addPage("processPage1");
        formBuilder.addLabel("page1 label");
        formBuilder.addLayout("/process-page1-template.html");
        formBuilder.addWidget("processpage1widget1", WidgetType.TEXTBOX);
        formBuilder.addAllowHTMLInFieldBehavior(true);
        formBuilder.addLabel("page1 widget1 label");
        formBuilder.addAllowHTMLInLabelBehavior(true);
        formBuilder.addHTMLAttribute("readonly", "readonly");
        formBuilder.addLabelPosition(ItemPosition.RIGHT);
        formBuilder.addWidget("processpage1widget2", WidgetType.CHECKBOX);
        formBuilder.addLabel("page1 widget2 label");

        formBuilder.addPage("processPage2");
        formBuilder.addLabel("page2 label");
        formBuilder.addLayout("/process-page2-template.html");
        formBuilder.addValidator("processpage2validator1", "invalid page fields", "classname", null, null, null);
        formBuilder.addValidator("processpage2validator2", "invalid page fields", "classname", null, null, null);

        formBuilder.addWidget("processpage2widget1", WidgetType.MESSAGE);
        formBuilder.addLabel("page2 widget1 label");
        formBuilder.addWidget("processpage2widget2", WidgetType.BUTTON_SUBMIT);
        formBuilder.addLabel("page2 widget2 label");
        formBuilder.addWidget("processpage2widget3", WidgetType.FILEUPLOAD);
        formBuilder.addLabel("page2 widget3 label");
        formBuilder.addAttachmentImageBehavior(true);
        formBuilder.addWidget("processpage2widget4", WidgetType.TABLE);
        formBuilder.addCellsStyle("table-cellStyle");
        formBuilder.addHeadingsStyle("table-headings-cellStyle", true, true, false, false);

        formBuilder.addAction(ActionType.SET_VARIABLE, "variable", EngineApplicationFormDefAccessorImpl.PROCESS_VARIABLE, "field_processpage1widget1");
        formBuilder.addAction(ActionType.EXECUTE_SCRIPT, null, "", "expression");

        formBuilder.addAttachmentAction("attachment", "field_processpage1widget3");

        formBuilder.addConfirmationLayout("/process-confirmation-template.html");
        formBuilder.addConfirmationMessage("${\"confirmation message\"}");

        formBuilder.addEntryForm("processName--1.0--1--request$entry");

        formBuilder.addEntryForm("processName--1.0--1--validate$entry");
        formBuilder.addTransientData("transientData", Boolean.class.getName(), "true");
        formBuilder.addPage("activitypage1");
        formBuilder.addLabel("activitypage1 label");
        formBuilder.addLayout("/activity-page1-template.html");
        formBuilder.addWidget("activitypage1widget1", WidgetType.LISTBOX_SIMPLE);
        formBuilder.addAvailableValue("available value 1", "availablevalue1");
        formBuilder.addAvailableValue("available value 2", "availablevalue2");
        formBuilder.addItemsStyle("items_class_names");
        formBuilder.addWidget("activitypage1widget2", WidgetType.PASSWORD);
        formBuilder.addValidator("activitywidget2validator1", "activity widget2 validator1", "classname", null, null, null);
        formBuilder.addValidator("activitywidget2validator2", "activity widget2 validator2", "classname", null, null, null);

        formBuilder.addPage("activitypage2");
        formBuilder.addLabel("activitypage2 label");
        formBuilder.addLayout("/activity-page2-template.html");
        formBuilder.addWidget("activitypage2widget1", WidgetType.TEXTAREA);
        formBuilder.addLabel("activity page2 widget1");
        formBuilder.addMaxLength(30);
        formBuilder.addMaxHeight(3);
        formBuilder.addMandatoryBehavior(true);
        formBuilder.addStyle("css_class");
        formBuilder.addTitle("title");
        formBuilder.addInitialValue("initial\nvalue");
        formBuilder.addVariableBound("testvar");
        formBuilder.addLabelStyle("label_css_class");
        formBuilder.addWidget("activitypage2widget2", WidgetType.DATE);
        formBuilder.addInitialValue("10-01-2009");
        formBuilder.addDisplayFormat("mm/dd/yyyy");

        formBuilder.addConfirmationLayout("/activity-confirmation-template.html");
        formBuilder.addConfirmationMessage("${\"confirmation message\"}");
        formBuilder.addErrorTemplate("/process-error-template.html");

        formBuilder.addMandatorySymbol("!");
        formBuilder.addMandatoryLabel("must be set");
        formBuilder.addMandatoryStyle("mandatory_style");

        return formBuilder.done();
    }

    @Test
    public void testGenerateComplexFormXML() throws Exception {
        Assert.assertNotNull(buildComplexFormXML());
    }

    @Test
    public void testGenerateAndInterpreteComplexFormXML() throws Exception {
        File complexProcessDefinition = buildComplexFormXML();
        InputStream inputStream = new FileInputStream(complexProcessDefinition);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(inputStream);
        inputStream.close();
        FormDocument formDocument = new FormDocument(document);

        XMLApplicationConfigDefAccessorImpl applicationConfigDefAccessor = new XMLApplicationConfigDefAccessorImpl(formDocument);
        Assert.assertEquals("resources/application/welcome.html", applicationConfigDefAccessor.getWelcomePage());
        Assert.assertEquals("resources/application/BonitaApplication.html", applicationConfigDefAccessor.getHomePage());
        Assert.assertEquals("resources/application/external-welcome.html", applicationConfigDefAccessor.getExternalWelcomePage());
        Assert.assertEquals("5.10", applicationConfigDefAccessor.getMigrationProductVersion());
        Assert.assertEquals("process label with accents éèà", applicationConfigDefAccessor.getApplicationLabel());
        Assert.assertEquals("/process-template.html", applicationConfigDefAccessor.getApplicationLayout());
        Assert.assertEquals("/process-error-template.html", applicationConfigDefAccessor.getApplicationErrorTemplate());
        Assert.assertEquals("!", applicationConfigDefAccessor.getApplicationMandatorySymbol());
        Assert.assertEquals("must be set", applicationConfigDefAccessor.getApplicationMandatoryLabel());
        Assert.assertEquals("mandatory_style", applicationConfigDefAccessor.getApplicationMandatorySymbolStyle());
        Assert.assertEquals("application#test", applicationConfigDefAccessor.getApplicationPermissions());

        XMLApplicationFormDefAccessorImpl applicationFormDefAccessor = new XMLApplicationFormDefAccessorImpl("processName--1.0$entry", formDocument, null, null);
        Assert.assertEquals("process#processName--1.0", applicationFormDefAccessor.getFormPermissions());
        Assert.assertEquals("page1 label", applicationFormDefAccessor.getPageLabel("processPage1"));
        Assert.assertEquals("/process-page1-template.html", applicationFormDefAccessor.getFormPageLayout("processPage1"));
        Assert.assertEquals(2, applicationFormDefAccessor.getPageWidgets("processPage1").size());
        FormWidget textBoxWidget = applicationFormDefAccessor.getPageWidgets("processPage1").get(0);
        Assert.assertTrue(textBoxWidget.allowHTMLInLabel());
        Assert.assertTrue(textBoxWidget.allowHTMLInField());
        Assert.assertEquals(ItemPosition.RIGHT, textBoxWidget.getLabelPosition());
        Assert.assertEquals(3, applicationFormDefAccessor.getActions("processPage2").size());
        FormAction action = applicationFormDefAccessor.getActions("processPage2").get(0);
        Assert.assertEquals(ActionType.SET_VARIABLE, action.getType());
        Assert.assertEquals("variable", action.getVariableId());
        Assert.assertEquals(EngineApplicationFormDefAccessorImpl.PROCESS_VARIABLE, action.getVariableType());
        Assert.assertEquals("field_processpage1widget1", action.getExpression());
        Assert.assertEquals(2, applicationFormDefAccessor.getPageValidators("processPage2").size());
        FormValidator validator = applicationFormDefAccessor.getPageValidators("processPage2").get(0);
        Assert.assertEquals("processpage2validator1", validator.getId());
        Assert.assertEquals("invalid page fields", validator.getLabel());
        Assert.assertEquals("classname", validator.getValidatorClass());
        Assert.assertEquals("", validator.getStyle());
        FormWidget uploadWidget = applicationFormDefAccessor.getPageWidgets("processPage2").get(2);
        Assert.assertTrue(uploadWidget.isDisplayAttachmentImage());
        FormWidget tableWidget = applicationFormDefAccessor.getPageWidgets("processPage2").get(3);
        Assert.assertEquals("table-cellStyle", tableWidget.getCellsStyle());
        Assert.assertEquals("table-headings-cellStyle", tableWidget.getHeadingsStyle());
        Assert.assertTrue(tableWidget.hasLeftHeadings());
        Assert.assertTrue(tableWidget.hasTopHeadings());
        Assert.assertFalse(tableWidget.hasRightHeadings());
        Assert.assertFalse(tableWidget.hasBottomHeadings());

        applicationFormDefAccessor = new XMLApplicationFormDefAccessorImpl("processName--1.0--1--validate$entry", formDocument, null, null);
        List<TransientData> transientData = applicationFormDefAccessor.getTransientData();
        TransientData transientData1 = transientData.get(0);
        Assert.assertEquals(Boolean.class.getName(), transientData1.getClassname());
        Assert.assertEquals("true", transientData1.getValue());
        Assert.assertEquals("transientData", transientData1.getName());
    }

    @Test(expected = InvalidFormDefinitionException.class)
    public void testWrongHierarchyFormDefinition() throws Exception {
        formBuilder.createFormDefinition();
        formBuilder.addApplication("processName", "1.0");
        formBuilder.addLabel("process label");
        formBuilder.addLayout("/process-template.html");
        formBuilder.addWidget("widget1", WidgetType.BUTTON_SUBMIT);
    }

    @Test(expected = InvalidFormDefinitionException.class)
    public void testNullValuesFormDefinition() throws Exception {
        formBuilder.createFormDefinition();
        formBuilder.addApplication("processName", "1.0");
        formBuilder.addLabel("process label");
        formBuilder.addLayout(null);
    }

    @Test(expected = InvalidFormDefinitionException.class)
    public void testEmptyValuesFormDefinition() throws Exception {
        formBuilder.createFormDefinition();
        formBuilder.addApplication("processName", "1.0");
        formBuilder.addLabel("process label");
        formBuilder.addLayout("");
    }
}
