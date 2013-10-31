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

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.xml.transform.TransformerException;

import org.bonitasoft.migration.forms.impl.FormMigrationImpl;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.SimpleCallbackHandler;
import org.xml.sax.SAXException;

/**
 * @author Qixiang Zhang
 * @version 1.0
 */
public class FormMigrationManager {

    /**
     * Logger
     */
    protected static Logger LOGGER = Logger.getLogger(FormMigrationManager.class.getName());

    /**
     * Login Context
     */
    private static LoginContext loginContext;

    /**
     * migrate all process
     */
    private static final String MIGRATION_ALL_FORMS = "ALL";

    protected static void logout() throws LoginException {
        loginContext.logout();
    }

    protected static void login(final String domain) throws LoginException {
        String loginContextName;
        if (domain == null || domain.trim().length() == 0) {
            loginContextName = "BonitaStore";
        } else {
            loginContextName = "BonitaStore-" + domain;
        }
        loginContext = new LoginContext(loginContextName, new SimpleCallbackHandler("admin", "bpm"));
        loginContext.login();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("you need to input at least one of ProcessDefinitionUUID!");
        }
        final String domain = System.getProperty("MIGRATE_DOMAIN");
        login(domain);
        try {
            if (MIGRATION_ALL_FORMS.equalsIgnoreCase((args[0]))) {
                final QueryDefinitionAPI queryDefinitionAPI = AccessorUtil.getQueryDefinitionAPI();
                Set<ProcessDefinition> processDefinitions = queryDefinitionAPI.getProcesses();
                for (ProcessDefinition processDefinition : processDefinitions) {
                    formMigration(processDefinition.getUUID().getValue(), domain);
                }
            } else {
                for (String arg : args) {
                    formMigration(arg, domain);
                }
            }
        } catch (final ProcessNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Process to migrate not found", e);
            throw new Exception(e);
        } catch (final TransformerException e) {
            LOGGER.log(Level.SEVERE, "Error while transforming the forms.xml ", e);
            throw new TransformerException(e);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading the forms.xml ", e);
            throw new IOException(e);
        } catch (final SAXException e) {
            LOGGER.log(Level.SEVERE, "Error while transforming the forms.xml ", e);
            throw new SAXException(e);
        } catch (final Throwable t) {
            LOGGER.log(Level.SEVERE, "Error during the migration. " + t.getMessage());
            throw new Exception(t);
        } finally {
            logout();
            System.exit(1);
        }
        
    }

    /**
     * Start forms migration
     * 
     * @param processDefinitionUUID
     * @throws SAXException 
     * @throws IOException 
     * @throws TransformerException 
     * @throws ProcessNotFoundException 
     */
    protected static void formMigration(final String processDefinitionUUID, final String domain) throws ProcessNotFoundException, TransformerException, IOException, SAXException {

        final FormMigration formMigration = new FormMigrationImpl();
        LOGGER.info("********************Starting... to migrate " + processDefinitionUUID + "**************************************");
        formMigration.replaceFormsXMLFile(processDefinitionUUID);
        LOGGER.info("********************End of migration " + processDefinitionUUID + "**********************************************");
    }


}
