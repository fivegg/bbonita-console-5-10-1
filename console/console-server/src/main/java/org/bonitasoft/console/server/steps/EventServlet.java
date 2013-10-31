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
package org.bonitasoft.console.server.steps;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.steps.EventFilter;
import org.bonitasoft.console.client.steps.EventItem;
import org.bonitasoft.console.client.steps.EventService;
import org.bonitasoft.console.client.steps.EventUUID;
import org.bonitasoft.console.client.steps.exceptions.EventNotFoundException;
import org.bonitasoft.console.server.login.SessionManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Nicolas Chabanoles
 */
public class EventServlet extends RemoteServiceServlet implements EventService {

    private static final long serialVersionUID = -3612543506785628592L;
    private static final Logger LOGGER = Logger.getLogger(EventServlet.class.getName());

    /**
     * Default constructor.
     */
    public EventServlet() {
        super();
    }

    public ItemUpdates<EventItem> getAllEvents(EventFilter anItemFilter) throws SessionTimeOutException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = SessionManager.login(theRequest);
        try {
            return EventDataStore.getInstance().getAllEvents(anItemFilter);
        } catch (ConsoleException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            throw e;
        } catch (Throwable e) {
            String errorMessage = "Unable to list Events!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new ConsoleException(errorMessage, e);
        } finally {
            SessionManager.logout(theLoginContext);
        }
    }

    public ItemUpdates<EventItem> deleteItems(Collection<EventUUID> anItemSelection, EventFilter anItemFilter) throws ConsoleException, SessionTimeOutException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = SessionManager.login(theRequest);
        try {
            return EventDataStore.getInstance().deleteEvents(anItemSelection, anItemFilter);
        } catch (ConsoleException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            throw e;
        } catch (Throwable e) {
            String errorMessage = "Unable to delete Events!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new ConsoleException(errorMessage, e);
        } finally {
            SessionManager.logout(theLoginContext);
        }
    }

    public EventItem updateItem(EventUUID anItemId, EventItem anItem)  throws ConsoleException, SessionTimeOutException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = SessionManager.login(theRequest);
        try {
            return EventDataStore.getInstance().updateEvent(anItemId, anItem);
        } catch (ConsoleException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            throw e;
        } catch (Throwable e) {
            String errorMessage = "Unable to update Event!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new ConsoleException(errorMessage, e);
        } finally {
            SessionManager.logout(theLoginContext);
        }
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.console.client.steps.EventService#executeEvent(org.bonitasoft.console.client.steps.EventUUID, org.bonitasoft.console.client.steps.EventFilter)
     */
    public ItemUpdates<EventItem> executeEvent(EventUUID anItemId, EventFilter aFilter) throws SessionTimeOutException, EventNotFoundException, ConsoleException {
        HttpServletRequest theRequest = this.getThreadLocalRequest();
        LoginContext theLoginContext = SessionManager.login(theRequest);
        try {
            return EventDataStore.getInstance().executeEvent(anItemId, aFilter);
        } catch (ConsoleException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            throw e;
        } catch (Throwable e) {
            String errorMessage = "Unable to update Event!";
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, errorMessage, e);
            }
            throw new ConsoleException(errorMessage, e);
        } finally {
            SessionManager.logout(theLoginContext);
        }
    }

}
