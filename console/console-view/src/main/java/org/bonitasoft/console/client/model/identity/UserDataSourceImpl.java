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
package org.bonitasoft.console.client.model.identity;

import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.identity.IdentityConfiguration;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.model.DefaultFilteredDataSourceImpl;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.SimpleSelection;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.users.UserFilter;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class UserDataSourceImpl extends DefaultFilteredDataSourceImpl<UserUUID, User, UserFilter> implements UserDataSource {

  protected IdentityConfiguration myIdentityConfiguration;

  public UserDataSourceImpl(MessageDataSource aMessageDataSource) {
    super(new UserData(), new SimpleSelection<UserUUID>(), aMessageDataSource);
    setItemFilter(new UserFilter(0, 20));
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.bonitasoft.console.client.model.UserDataSource#
   * updatePreferredStatReportName
   * (org.bonitasoft.console.client.users.UserProfile, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public void updateDefaultReportUUID(final UserProfile aUserProfile, final ReportUUID aReportUUID, AsyncHandler<Void> anAsyncHandler) {
    ((UserData) myRPCItemData).updatePreferredStatReport(aUserProfile, aReportUUID, new AsyncHandler<Void>() {
      public void handleFailure(Throwable aT) {
          if (aT instanceof SessionTimeOutException) {
              myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
          } else if (aT instanceof ConsoleSecurityException) {
              myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
          }
          if (aT instanceof ConsoleException) {
              if (myMessageDataSource != null) {
                  myMessageDataSource.addErrorMessage((ConsoleException) aT);
              }
          }
          myMessageDataSource.addErrorMessage(messages.unableToUpdateUserProfile());
      }

      public void handleSuccess(Void aResult) {
        aUserProfile.setDefaultReportUUID(aReportUUID);
        myMessageDataSource.addInfoMessage(messages.userProfileUpdated());
      }
    }, anAsyncHandler);
  }


  @SuppressWarnings("unchecked")
  public void updateConfiguration(final IdentityConfiguration aNewConfiguration, AsyncHandler<Void> aHandler) {
    if (aNewConfiguration == null) {
      throw new IllegalArgumentException("Configuration must be not null.");
    }
    if (!aNewConfiguration.equals(myIdentityConfiguration)) {
      ((UserData) myRPCItemData).updateConfiguration(aNewConfiguration, new AsyncHandler<Void>() {
        public void handleFailure(Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            myMessageDataSource.addErrorMessage(messages.unableToUpdateUserProfile());
        }

        public void handleSuccess(Void aResult) {
          myMessageDataSource.addInfoMessage(messages.configurationUpdated());
          IdentityConfiguration theOldValue = myIdentityConfiguration;
          myIdentityConfiguration = new IdentityConfiguration(aNewConfiguration);
          myChanges.fireModelChange(IDENTITY_CONFIGURATION_PROPERTY, theOldValue, myIdentityConfiguration);
        }
      }, aHandler);
    } else {
      GWT.log("Skipping configuration update as the new configuration is the same as the current", new NullPointerException());
    }
  }

  @SuppressWarnings("unchecked")
  public void getConfiguration(final AsyncHandler<IdentityConfiguration> aHandler) {
    if (myIdentityConfiguration != null) {
      // the configuration has already been loaded.
      if (aHandler != null) {
        aHandler.handleSuccess(new IdentityConfiguration(myIdentityConfiguration));
      }
    } else {
      ((UserData) myRPCItemData).getIdentityConfiguration(new AsyncHandler<IdentityConfiguration>() {
        public void handleFailure(Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            if (aHandler != null) {
              aHandler.handleFailure(aT);
            }
            myMessageDataSource.addErrorMessage(messages.unableToUpdateUserProfile());
        }

        public void handleSuccess(IdentityConfiguration aResult) {
          myIdentityConfiguration = new IdentityConfiguration(aResult);
          if (aHandler != null) {
            aHandler.handleSuccess(aResult);
          }
        }
      });

    }
  }
}
