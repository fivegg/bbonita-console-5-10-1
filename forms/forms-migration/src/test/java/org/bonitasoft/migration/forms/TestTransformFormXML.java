/**
 * Copyright (C) 2011 BonitaSoft S.A.
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
package org.bonitasoft.migration.forms;

import org.bonitasoft.migration.forms.constants.XMLForms;
import org.bonitasoft.migration.forms.utils.XPathUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Qixiang Zhang
 * @version 1.0
 */
public class TestTransformFormXML extends XPathUtil {

    private final static String FORMS_XSD = "forms.xsd";

    static {
        final String bonitaHome = System.getProperty("BONITA_HOME");
        if (bonitaHome == null) {
            System.err.println("\n\n*** Forcing " + "BONITA_HOME" + " to target/bonita \n\n\n");
            System.setProperty("BONITA_HOME", "target/bonita");
        } else {
            System.err.println("\n\n*** " + "BONITA_HOME" + " already set to: " + bonitaHome + " \n\n\n");
        }
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testTransformWebPurchaseFormXML() throws Exception {

        super.parse("Web_Purchase--1.5.bar");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.MIGRATION_PRODUCT_VERSION), "5.10");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.NAME), "Web_Purchase");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.VERSION), "1.5");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION_LABEL), "Web Purchase");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION_LAYOUT), "html/Web_Purchase_process_template.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_SYMBOL), "*");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_LABEL), "mandatory");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/" + XMLForms.PERMISSIONS), "process#Web_Purchase--1.5");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/@" + XMLForms.ID), "Web_Purchase--1.5$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PERMISSIONS), "process#Web_Purchase--1.5");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "Web Purchase");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Web_Purchase_process_Web_Purchase.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/@" + XMLForms.ID), "customerEmail");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/@" + XMLForms.TYPE), "TEXTBOX");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.INITIAL_VALUE + "/" + XMLForms.EXPRESSION),
                "${customerEmail}");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.ALLOW_HTML_IN_FIELD), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.MANDATORY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.READ_ONLY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.LABEL), "Email address");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.VARIABLE_BOUND), "${customerEmail}");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/@"
                + XMLForms.ID), "customerEmailValidator1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.LABEL), "this does not appear to be a valid email address");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.CLASSNAME), "org.bonitasoft.forms.server.validator.MailValidator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.POSITION), "BOTTOM");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[4]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.VALUES_LIST
                + "/" + XMLForms.AVAILABLE_VALUE + "[1]/" + XMLForms.LABEL), "TV");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[4]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.VALUES_LIST
                + "/" + XMLForms.AVAILABLE_VALUE + "[1]/" + XMLForms.VALUE), "TV");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[5]/@" + XMLForms.ID), "Submit1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[5]/@" + XMLForms.TYPE), "BUTTON_SUBMIT");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[2]/@" + XMLForms.TYPE), "SET_VARIABLE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[2]/" + XMLForms.VARIABLE_TYPE), "PROCESS_VARIABLE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[2]/" + XMLForms.VARIABLE), "customerName");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[2]/" + XMLForms.EXPRESSION), "field_customerName");

        super.validate(FORMS_XSD);
    }

    @Test
    public void testTransformArrivalOfANewEmployeeFormXML() throws Exception {

        super.parse("Arrival_of_a_New_Employee--4.1.bar");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.MIGRATION_PRODUCT_VERSION), "5.10");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.NAME), "Arrival_of_a_New_Employee");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.VERSION), "4.1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION_LABEL), "Arrival of a New Employee");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_SYMBOL), "*");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_LABEL), "mandatory");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/" + XMLForms.PERMISSIONS), "process#Arrival_of_a_New_Employee--4.1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/@" + XMLForms.ID), "Arrival_of_a_New_Employee--4.1--Prepare_payroll_and_benefits_information$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PERMISSIONS), "activity#Arrival_of_a_New_Employee--4.1--Prepare_payroll_and_benefits_information");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/@" + XMLForms.FIRST_PAGE), "Enter_into_payroll_system");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Prepare_payroll_and_benefits_information_Enter_into_payroll_system.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/@" + XMLForms.ID), "Date_of_first_check");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/@" + XMLForms.TYPE), "DATE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.INITIAL_VALUE + "/" + XMLForms.EXPRESSION),
                "${data_of_first_check}");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.DISPLAY_FORMAT), "");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.MANDATORY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.READ_ONLY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.LABEL),
                "date of first check (add 2 weeks to start date)");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/@"
                + XMLForms.ID), "Date_of_first_check_default_validator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.LABEL), "#dateFieldValidatorLabel");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.CLASSNAME), "org.bonitasoft.forms.server.validator.DateFieldValidator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.POSITION), "BOTTOM");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.STYLE), "");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[8]/@" + XMLForms.ID), "Submit");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[8]/@" + XMLForms.TYPE), "BUTTON_SUBMIT");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[8]/" + XMLForms.LABEL_BUTTON), "false");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[7]/" + XMLForms.DISPLAY_FORMAT), "dd MMM yyyy");

        // test skip form
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/@" + XMLForms.ID), "Arrival_of_a_New_Employee--4.1--Notify_hiring_manager$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.PERMISSIONS), "activity#Arrival_of_a_New_Employee--4.1--Notify_hiring_manager");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[20]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/@" + XMLForms.TYPE), "SET_VARIABLE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[20]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/" + XMLForms.VARIABLE_TYPE), "ACTIVITY_VARIABLE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[20]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/" + XMLForms.VARIABLE), "Phone_extension");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[20]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/" + XMLForms.EXPRESSION), "field_Phone_extension");

        super.validate(FORMS_XSD);
    }

    @Test
    public void testTransformBuyAMiniFormXML() throws Exception {

        super.parse("Buy_a_MINI--3.1.bar");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.MIGRATION_PRODUCT_VERSION), "5.10");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.NAME), "Buy_a_MINI");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.VERSION), "3.1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION_LABEL), "Buy a MINI");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_SYMBOL), "*");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_LABEL), "mandatory");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/" + XMLForms.PERMISSIONS), "process#Buy_a_MINI--3.1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/@" + XMLForms.ID), "Buy_a_MINI--3.1--Model_choice$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PERMISSIONS), "activity#Buy_a_MINI--3.1--Model_choice");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/@" + XMLForms.FIRST_PAGE), "Model_choice");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "Model choice");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Model_choice_Model_choice.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.ID), "Models");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.TYPE), "RADIOBUTTON_GROUP");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.INITIAL_VALUE + "/" + XMLForms.EXPRESSION),
                "${Models}");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.MANDATORY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.READ_ONLY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.LABEL), "Choose a Model");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_FIELD), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.VARIABLE_BOUND), "${Models}");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.VALUES_LIST
                + "/" + XMLForms.AVAILABLE_VALUE + "[1]/" + "/" + XMLForms.LABEL), "One");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.VALUES_LIST
                + "/" + XMLForms.AVAILABLE_VALUE + "[1]/" + "/" + XMLForms.VALUE), "One");

        // test BUTTON_NEXT widget
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/@" + XMLForms.ID), "Go_to_other_part_colors");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/@" + XMLForms.TYPE), "BUTTON_NEXT");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.LABEL_BUTTON), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.LABEL), "Go to other part colors");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");

        // test BUTTON_PREVIOUS widget
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[5]/@" + XMLForms.ID), "Back_to_body_color");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[5]/@" + XMLForms.TYPE), "BUTTON_PREVIOUS");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[5]/" + XMLForms.LABEL_BUTTON), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[5]/" + XMLForms.LABEL), "Back to body color");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[5]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/@" + XMLForms.ID), "Go_to_accessories");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/@" + XMLForms.TYPE), "BUTTON_SUBMIT");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[6]/" + XMLForms.LABEL_BUTTON), "false");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/@" + XMLForms.TYPE), "SET_VARIABLE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/" + XMLForms.VARIABLE_TYPE), "PROCESS_VARIABLE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/" + XMLForms.VARIABLE), "Stripes");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[2]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/" + XMLForms.EXPRESSION), "field_Stripes");

        super.validate(FORMS_XSD);

    }

    @Test
    public void testTransformCreditRequestFormXML() throws Exception {

        super.parse("Request_For_Advance_Payment--1.2.bar");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.MIGRATION_PRODUCT_VERSION), "5.10");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.NAME), "Request_For_Advance_Payment");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.VERSION), "1.2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION_LABEL), "Request For Advance Payment");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_SYMBOL), "*");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_LABEL), "mandatory");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/" + XMLForms.PERMISSIONS), "process#Request_For_Advance_Payment--1.2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/@" + XMLForms.ID), "Request_For_Advance_Payment--1.2$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PERMISSIONS), "process#Request_For_Advance_Payment--1.2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/@" + XMLForms.FIRST_PAGE), "Request_For_Advance_Payment_Form");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "Request For Advance Payment Form");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Request_For_Advance_Payment_process_Request_For_Advance_Payment_Form.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/@" + XMLForms.ID), "Amount_Requested");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/@" + XMLForms.TYPE), "TEXTBOX");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.INITIAL_VALUE + "/" + XMLForms.EXPRESSION),
                "${Amount_Requested}");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.MANDATORY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.READ_ONLY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.LABEL), "Amount Requested");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.ALLOW_HTML_IN_FIELD), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.VARIABLE_BOUND), "${Amount_Requested}");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/@"
                + XMLForms.ID), "Amount_Requested_default_validator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.LABEL), "#numericDoubleFieldValidatorLabel");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.CLASSNAME), "org.bonitasoft.forms.server.validator.NumericDoubleFieldValidator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.POSITION), "BOTTOM");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[3]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/"
                + XMLForms.STYLE), "");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[7]/@" + XMLForms.ID), "Submit");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[7]/@" + XMLForms.TYPE), "BUTTON_SUBMIT");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[7]/" + XMLForms.LABEL_BUTTON), "false");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/@" + XMLForms.TYPE), "SET_VARIABLE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/" + XMLForms.VARIABLE_TYPE), "ACTIVITY_VARIABLE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/" + XMLForms.VARIABLE), "Approve");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ACTIONS + "/" + XMLForms.ACTION + "[1]/" + XMLForms.EXPRESSION), "field_Approve");

        super.validate(FORMS_XSD);
    }

    @Test
    public void testTransform1FormXML() throws Exception {

        super.parse("testTransform1--1.0.bar");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.MIGRATION_PRODUCT_VERSION), "5.10");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.NAME), "testTransform1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.VERSION), "1.0");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION_LABEL), "testTransform1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_SYMBOL), "*");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.ERROR_TEMPLATE), "html/testTransform1_error_template.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/" + XMLForms.PERMISSIONS), "process#testTransform1--1.0");

        // test transform process recap form
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/@" + XMLForms.ID), "testTransform1--1.0$view");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.FORM_TYPE), "view");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PERMISSIONS), "process#testTransform1--1.0");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/@" + XMLForms.FIRST_PAGE), "view_pageflow");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "view-pageflow");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/testTransform1_process_consultation_view_pageflow.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.ID), "Text1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.TYPE), "TEXT");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.INITIAL_VALUE + "/" + XMLForms.EXPRESSION),
                "asdasd");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.LABEL), "Text1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_FIELD), "false");

        // test transform process view form
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/@" + XMLForms.ID), "testTransform1--1.0$recap");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.FORM_TYPE), "view");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PERMISSIONS), "process#testTransform1--1.0");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/@" + XMLForms.FIRST_PAGE), "testTransform1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "testTransform1");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/testTransform1_process_recap_testTransform1.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.ID), "testRecap");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.TYPE), "TEXT");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.INITIAL_VALUE + "/" + XMLForms.EXPRESSION),
                "testRecap");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.LABEL), "testRecap");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_FIELD), "false");

        // test skip process form
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/@" + XMLForms.ID), "testTransform1--1.0$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PERMISSIONS), "process#testTransform1--1.0");

        // test transform step view form
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/@" + XMLForms.ID), "testTransform1--1.0--Step2$view");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.FORM_TYPE), "view");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.PERMISSIONS), "activity#testTransform1--1.0--Step2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/@" + XMLForms.FIRST_PAGE), "Step2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "Step2");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Step2_consultation_Step2.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.ID), "Message1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.TYPE), "MESSAGE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.INITIAL_VALUE + "/" + XMLForms.EXPRESSION),
                "asdasd");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[5]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_FIELD), "false");

        // test connectors
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/@" + XMLForms.ID), "testTransform1--1.0--Step5$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PERMISSIONS), "activity#testTransform1--1.0--Step5");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/@" + XMLForms.FIRST_PAGE), "Step5");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "Step5");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Step5_Step5.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.ID), "Select1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.TYPE), "LISTBOX_SIMPLE");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.MANDATORY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.READ_ONLY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.LABEL), "Select1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.CONNECTORS
                + "/" + XMLForms.CONNECTOR + "/" + XMLForms.CLASSNAME), "org.bonitasoft.connectors.bonita.GetUser");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.CONNECTORS
                + "/" + XMLForms.CONNECTOR + "/" + XMLForms.THROW_EXCEPTION), "true");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.CONNECTORS
                + "/" + XMLForms.CONNECTOR + "/" + XMLForms.INPUT_PARAMETERS + "/" + XMLForms.INPUT_PARAMETER + "/@" + XMLForms.NAME), "setUsername");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.CONNECTORS
                + "/" + XMLForms.CONNECTOR + "/" + XMLForms.INPUT_PARAMETERS + "/" + XMLForms.INPUT_PARAMETER + "/" + XMLForms.VALUE), "rO0ABXQADSR7bG9nZ2VkVXNlcn0=");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.CONNECTORS
                + "/" + XMLForms.CONNECTOR + "/" + XMLForms.OUTPUT_PARAMETERS + "/" + XMLForms.OUTPUT_PARAMETER + "/@" + XMLForms.NAME), "field_Select1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.AVAILABLE_VALUES + "/" + XMLForms.CONNECTORS
                + "/" + XMLForms.CONNECTOR + "/" + XMLForms.OUTPUT_PARAMETERS + "/" + XMLForms.OUTPUT_PARAMETER + "/" + XMLForms.VALUE), "${user}");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/@" + XMLForms.ID), "Submit1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/@" + XMLForms.TYPE), "BUTTON_SUBMIT");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[8]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.LABEL_BUTTON), "false");

        // test redirection url
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[9]/@" + XMLForms.FIRST_PAGE), "");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[9]/@" + XMLForms.ID), "testTransform1--1.0--Step6$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[9]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[9]/" + XMLForms.PERMISSIONS), "activity#testTransform1--1.0--Step6");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[9]/" + XMLForms.REDIRECTION_URL + "/" + XMLForms.URL), "http://www.google.com");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[9]/" + XMLForms.REDIRECTION_URL + "/" + XMLForms.TRANSMIT_SUBMIT_URL), "true");

        super.validate(FORMS_XSD);
    }

    @Test
    public void testTransform2FormXML() throws Exception {

        super.parse("testTransform2--1.0.bar");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.MIGRATION_PRODUCT_VERSION), "5.10");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.NAME), "testTransform2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.VERSION), "1.0");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION_LABEL), "testTransform2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_SYMBOL), "*");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.ERROR_TEMPLATE), "html/testTransform2_error_template.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/" + XMLForms.PERMISSIONS), "process#testTransform2--1.0");

        // test EDITABLE_GRID widget
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/@" + XMLForms.ID), "testTransform2--1.0--Step1$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PERMISSIONS), "activity#testTransform2--1.0--Step1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/@" + XMLForms.FIRST_PAGE), "Step1");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "Step1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Step1_Step1.html");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.ID), "Editable_grid1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.TYPE), "EDITABLE_GRID");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.VARIABLE_ROWS), "true");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.VARIABLE_COLUMNS), "true");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.MANDATORY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.READ_ONLY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.INITIAL_VALUE + "/" + XMLForms.EXPRESSION),
                "${[[\"b2\", ], ]}");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.VERTICAL_HEADER), "${[\"a2\", ]}");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.HORIZONTAL_HEADER), "${[\"a1\", \"b1\", ]}");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.HORIZONTAL_HEADER), "${[\"a1\", \"b1\", ]}");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_FIELD), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.HEADINGS_POSITIONS + "/"
                + XMLForms.LEFT_HEADINGS), "true");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.HEADINGS_POSITIONS + "/"
                + XMLForms.TOP_HEADINGS), "true");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.HEADINGS_POSITIONS + "/"
                + XMLForms.RIGHT_HEADINGS), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.HEADINGS_POSITIONS + "/"
                + XMLForms.BOTTOM_HEADINGS), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.LABEL), "Editable grid1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");

        // test transient-data
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/@" + XMLForms.ID), "testTransform2--1.0--Step2$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PERMISSIONS), "activity#testTransform2--1.0--Step2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/@" + XMLForms.FIRST_PAGE), "Step2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "Step2");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Step2_Step2.html");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.TRANSIENT_DATA + "/" + XMLForms.DATA + "/@" + XMLForms.NAME), "bb");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[2]/" + XMLForms.TRANSIENT_DATA + "/" + XMLForms.DATA + "/" + XMLForms.CLASSNAME), "java.lang.String");

        // test page-validators
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/@" + XMLForms.FIRST_PAGE), "Step3");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/@" + XMLForms.ID), "testTransform2--1.0--Step3$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PERMISSIONS), "activity#testTransform2--1.0--Step3");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "Step3");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Step3_Step3.html");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_VALIDATORS + "/" + XMLForms.VALIDATOR + "/@" + XMLForms.ID), "Step3Validator1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_VALIDATORS + "/" + XMLForms.VALIDATOR + "/" + XMLForms.LABEL), "*");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_VALIDATORS + "/" + XMLForms.VALIDATOR + "/" + XMLForms.CLASSNAME),
                "org.bonitasoft.forms.server.validator.GroovyPageValidator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_VALIDATORS + "/" + XMLForms.VALIDATOR + "/" + XMLForms.PARAMETER), "sss");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[3]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_VALIDATORS + "/" + XMLForms.VALIDATOR + "/" + XMLForms.POSITION), "BOTTOM");

        super.validate(FORMS_XSD);
    }
    
    @Test
    public void testRegexValidator() throws Exception {

        super.parse("RegexValidator--migration1.0.bar");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.MIGRATION_PRODUCT_VERSION), "5.10");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.NAME), "RegexValidator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/@" + XMLForms.VERSION), "migration1.0");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION_LABEL), "RegexValidator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_SYMBOL), "*");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.MANDATORY_LABEL), "mandatory");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.APPLICATION + "/" + XMLForms.PERMISSIONS), "process#RegexValidator--migration1.0");

        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/@" + XMLForms.ID), "RegexValidator--migration1.0--Step1_with_a_regex_validator$entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.FORM_TYPE), "entry");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PERMISSIONS), "activity#RegexValidator--migration1.0--Step1_with_a_regex_validator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/@" + XMLForms.FIRST_PAGE), "Step1");
        
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/@" + XMLForms.ID), "Step1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LABEL), "Step1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.PAGE_LAYOUT), "html/Step1_with_a_regex_validator_Step1.html");
        
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.ID), "Text_field1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/@" + XMLForms.TYPE), "TEXTBOX");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.LABEL), "Enter aa.* string");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.MANDATORY), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.READ_ONLY), "false");
        
        //test validator in widgets
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/@" + XMLForms.ID), "Text_field1 RegExp Validator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/" + XMLForms.LABEL), "please enter a string starting with aa");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/" + XMLForms.CLASSNAME),
                "org.bonitasoft.forms.server.validator.RegexFieldValidator");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/" + XMLForms.PARAMETER), "^aa.*");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[1]/" + XMLForms.VALIDATORS + "/" + XMLForms.VALIDATOR + "/" + XMLForms.POSITION), "BOTTOM");
        
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/@" + XMLForms.ID), "Submit1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/@" + XMLForms.TYPE), "BUTTON_SUBMIT");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.LABEL), "Submit1");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.ALLOW_HTML_IN_LABEL), "false");
        Assert.assertEquals(getStringByXpath("//" + XMLForms.FORMS + "/" + XMLForms.FORM + "[1]/" + XMLForms.PAGES + "/" + XMLForms.PAGE + "[1]/" + XMLForms.WIDGETS + "/" + XMLForms.WIDGET + "[2]/" + XMLForms.LABEL_BUTTON), "false");

        super.validate(FORMS_XSD);
    }

    @After
    public void tearDown() {
    }
}
