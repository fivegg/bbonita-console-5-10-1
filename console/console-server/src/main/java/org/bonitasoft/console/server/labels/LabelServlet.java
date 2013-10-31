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
package org.bonitasoft.console.server.labels;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;

import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelService;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelUpdates;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.server.login.SessionManager;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelServlet extends RemoteServiceServlet implements LabelService {

  /**
   * The ID used for serialization.
   */
  private static final long serialVersionUID = -1078488364853453974L;

  private static final Logger LOGGER = Logger.getLogger(LabelServlet.class.getName());

  /**
   * Default constructor.
   */
  public LabelServlet() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.LabelService#createNewLabel(java.lang.String
   * )
   */
  public LabelModel createNewLabel(String aLabelName) throws ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(this.getThreadLocalRequest());
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Try to create new label '" + aLabelName + "' for user '" + theUserProfile.getUsername() + "'.");
      }
      LabelModel theResult = LabelDataStore.getInstance().createNewLabel(theUserProfile, aLabelName);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Label '" + aLabelName + "' for user '" + theUserProfile.getUsername() + "' was created successfully.");
      }
      return theResult;
    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      throw new ConsoleException("Unable to create label: " + aLabelName, e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }

  }

  public void removeLabels(List<LabelUUID> aLabelUUIDList) throws ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    try {
      theLoginContext = SessionManager.login(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Trying to remove labels.");
      }
      LabelDataStore.getInstance().removeCustomLabels(SessionManager.getUserProfile(theRequest), aLabelUUIDList);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Labels were removed.");
      }
    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (Exception e) {
      String theErrorMessage = "Unable to remove labels.";
      LOGGER.log(Level.SEVERE, theErrorMessage, e);
      throw new ConsoleException(theErrorMessage, e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.LabelService#updateLabelCSSStyle(org.bonitasoft
   * .console.client.LabelUUID, java.lang.String, java.lang.String,
   * java.lang.String)
   */
  public void updateLabelCSSStyle(LabelUUID aLabelUUID, String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle) throws SessionTimeOutException, ConsoleException {
    LoginContext theLoginContext = null;
    try {
      HttpServletRequest theRequest = this.getThreadLocalRequest();
      theLoginContext = SessionManager.login(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Trying to update css style of label '" + aLabelUUID.getValue() + "' of user '" + aLabelUUID.getOwner().getValue() + "'.");
      }
      LabelDataStore.getInstance().updateLabelCSSStyle(aLabelUUID, aEditableCSSStyle, aPreviewCSSStyle, aReadOnlyCSSStyle);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Label '" + aLabelUUID.getValue() + "' of user '" + aLabelUUID.getOwner().getValue() + "' was updated.");
      }
    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (Exception e) {
      String theErrorMessage = "Unable to update css style of label.";
      LOGGER.log(Level.SEVERE, theErrorMessage, e);
      throw new ConsoleException(theErrorMessage, e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }

  }

  public void updateLabelsVisibility(Set<LabelUUID> aLabelUUIDSelection, boolean isVisible) throws SessionTimeOutException {
    LoginContext theLoginContext = null;
    try {
      HttpServletRequest theRequest = this.getThreadLocalRequest();
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      LabelDataStore.getInstance().updateLabelsVisibility(theUserProfile, aLabelUUIDSelection, isVisible);

    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (Exception e) {
      String theErrorMessage = "Unexpected error.";
      LOGGER.log(Level.SEVERE, theErrorMessage, e);
      throw new RuntimeException(theErrorMessage);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }

  }

  public void renameLabel(LabelUUID aLabelUUID, String aNewName) throws ConsoleException, SessionTimeOutException {
    LoginContext theLoginContext = null;
    try {
      HttpServletRequest theRequest = this.getThreadLocalRequest();
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Renaming label '" + aLabelUUID.getValue() + "' of user '" + aLabelUUID.getOwner().getValue() + "'.");
      }
      LabelDataStore.getInstance().renameLabel(theUserProfile, aLabelUUID, aNewName);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Label '" + aLabelUUID.getValue() + "' of user '" + aLabelUUID.getOwner().getValue() + "' was updated.");
      }
    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (InstanceNotFoundException e) {
      e.printStackTrace();
      throw new ConsoleException("Unable to rename the label: " + aLabelUUID.getValue(), e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.LabelService#updateLabels(java.util.Set,
   * java.util.Set, java.util.Set)
   */
  public void updateLabels(Set<LabelUUID> aSetOfLabelUUIDToAdd, Set<LabelUUID> aSetOfLabelUUIDToRemove, Set<CaseUUID> aSetOfCaseUUID) throws ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Updating labels <--> cases relationship.");
      }
      LabelDataStore.getInstance().updateLabels(theUserProfile, aSetOfLabelUUIDToAdd, aSetOfLabelUUIDToRemove, aSetOfCaseUUID);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "labels <--> cases relationship updated.");
      }
    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (Exception e) {
      String theErrorMessage = "Unable to update labels of cases.";
      LOGGER.log(Level.SEVERE, theErrorMessage, e);
      throw new ConsoleException(theErrorMessage, e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
  }

  public LabelUpdates getLabelUpdates(final LabelUUID aLabelUUID, final boolean searchInHistory) throws ConsoleException, SessionTimeOutException {

    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      return LabelDataStore.getInstance().getLabelUpdates(theUserProfile, aLabelUUID, searchInHistory);
    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (Exception e) {
      String theErrorMessage = "Unable to get the total number of cases.";
      LOGGER.log(Level.SEVERE, theErrorMessage, e);
      throw new ConsoleException(theErrorMessage, e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
  }

  public List<LabelModel> getAllLabels() throws ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    try {
      theLoginContext = SessionManager.login(theRequest);
      UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      if (theUserProfile.getUser() != null) {
        return LabelDataStore.getInstance().getAllLabels(theUserProfile.getUser());
      } else {
        return LabelDataStore.getInstance().getAllLabels(theUserProfile.getUsername());
      }
    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (Exception e) {
      String theErrorMessage = "Unable to get the total number of cases.";
      LOGGER.log(Level.SEVERE, theErrorMessage, e);
      throw new ConsoleException(theErrorMessage, e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
  }

  public LabelsConfiguration getConfiguration() throws ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    try {
      theLoginContext = SessionManager.login(theRequest);
      return LabelDataStore.getInstance().getConfiguration();
    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (Exception e) {
      String theErrorMessage = "Unable to get the label configuration.";
      LOGGER.log(Level.SEVERE, theErrorMessage, e);
      throw new ConsoleException(theErrorMessage, e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
  }

  public void updateConfiguration(final LabelsConfiguration aConfiguration) throws ConsoleException, SessionTimeOutException {
    HttpServletRequest theRequest = this.getThreadLocalRequest();
    LoginContext theLoginContext = null;
    try {
      theLoginContext = SessionManager.login(theRequest);
      final UserProfile theUserProfile = SessionManager.getUserProfile(theRequest);
      LabelDataStore.getInstance().updateConfiguration(theUserProfile, aConfiguration);
    } catch (SessionTimeOutException e) {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, e.getMessage());
      }
      throw e;
    } catch (Exception e) {
      String theErrorMessage = "Unable to update the label configuration.";
      LOGGER.log(Level.SEVERE, theErrorMessage, e);
      throw new ConsoleException(theErrorMessage, e);
    } finally {
      if (theLoginContext != null) {
        SessionManager.logout(theLoginContext);
      }
    }
  }
}
