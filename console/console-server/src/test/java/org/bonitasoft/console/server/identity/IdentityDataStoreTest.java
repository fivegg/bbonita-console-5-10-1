/**
 * Copyright (C) 2010 BonitaSoft S.A.
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
package org.bonitasoft.console.server.identity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.identity.User;
import org.bonitasoft.console.client.users.UserFilter;
import org.bonitasoft.console.server.BonitaTestCase;
import org.bonitasoft.console.server.Utils;
import org.junit.Test;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.exception.UserNotFoundException;
import org.ow2.bonita.util.AccessorUtil;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class IdentityDataStoreTest extends BonitaTestCase {

  @Test
  public void addUserNominal() throws Exception {
    Utils.login(adminUsername, "bpm");

    final IdentityAPI theIdentityAPI = AccessorUtil.getIdentityAPI();

    final String theUserID = "";
    final String theUsername = "user1";
    final String thePassword = "&:�*�mlkjh";
    final String theFirstName = "first";
    final String theLastName = "last";

    try {
      theIdentityAPI.getUser(theUsername);
      fail("User must no exist at this step!");
    } catch (UserNotFoundException e) {
      // Do nothing as it is the expected behaviour.
    }
    final UserFilter theUserFilter = new UserFilter(0, 20);
    User theUserToAdd = new User(theUserID, theUsername, thePassword, theFirstName, theLastName, null, null, null, null);
    IdentityDataStore.getInstance().addUser(theUserToAdd, theUserFilter);

    assertNotNull(theIdentityAPI.getUser(theUsername));

    // clean
    theIdentityAPI.removeUser(theUsername);
    Utils.logout();
  }

  @Test(expected = org.ow2.bonita.facade.exception.UserAlreadyExistsException.class)
  public void addUserAlreadyExists() throws Exception {
    Utils.login(adminUsername, "bpm");

    final IdentityAPI theIdentityAPI = AccessorUtil.getIdentityAPI();

    final String theUserID = null;
    final String theUsername = "user1";
    final String thePassword = "&:�*�mlkjh";
    final String theFirstName = "first";
    final String theLastName = "last";

    try {
      theIdentityAPI.getUser(theUsername);
      fail("User must no exist at this step!");
    } catch (UserNotFoundException e) {
      // Do nothing as it is the expected behaviour.
    }
    try {
      final UserFilter theUserFilter = new UserFilter(0, 20);
      User theUserToAdd = new User(theUserID, theUsername, thePassword, theFirstName, theLastName, null, null, null, null);
      IdentityDataStore.getInstance().addUser(theUserToAdd, theUserFilter);
      assertNotNull(theIdentityAPI.getUser(theUsername));
      IdentityDataStore.getInstance().addUser(theUserToAdd, theUserFilter);
    } catch (Exception e) {
      throw e;
    } finally {
      // clean
      theIdentityAPI.removeUser(theUsername);
      Utils.logout();
    }
  }

  @Test(expected = Exception.class)
  public void addUserInvalid() throws Exception {
    Utils.login(adminUsername, "bpm");

    final String theUserID = null;
    final String theUsername = null;
    final String thePassword = null;
    final String theFirstName = null;
    final String theLastName = null;
    try {
      final UserFilter theUserFilter = new UserFilter(0, 20);
      User theUserToAdd = new User(theUserID, theUsername, thePassword, theFirstName, theLastName, null, null, null, null);
      IdentityDataStore.getInstance().addUser(theUserToAdd, theUserFilter);
    } catch (Exception e) {
      throw e;
    } finally {
      Utils.logout();
    }
  }

  @Test(expected = Exception.class)
  public void addUserNull() throws Exception {
    Utils.login(adminUsername, "bpm");
    try {
      IdentityDataStore.getInstance().addUser(null, null);

    } catch (Exception e) {
      throw e;
    } finally {
      Utils.logout();
    }
  }

  @Test
  public void updateUserEmailNominal() throws Exception {
    Utils.login(adminUsername, "bpm");

    final IdentityAPI theIdentityAPI = AccessorUtil.getIdentityAPI();

    final String theUserID = null;
    final String theUsername = "user1";
    final String thePassword = "&:�*�mlkjh";
    final String theFirstName = "first";
    final String theLastName = "last";

    final String theNewEmail = "new.email@address.com";
    org.ow2.bonita.facade.identity.User theAddedUser = theIdentityAPI.addUser(theUsername, thePassword, theFirstName, theLastName,null,null,null,null);
    assertNull(theAddedUser.getProfessionalContactInfo());

    User theUserToUpdate = new User(theUserID, theUsername, thePassword, theFirstName, theLastName, null, null, null, null);
    Map<String, String> theProfessionalContactInfo = new HashMap<String, String>();
    theProfessionalContactInfo.put(User.EMAIL_KEY, theNewEmail);
    theUserToUpdate.setProfessionalContactInfo(theProfessionalContactInfo );
    IdentityDataStore.getInstance().updateUser(new BonitaUUID(theAddedUser.getUUID()), theUserToUpdate);
    org.ow2.bonita.facade.identity.User theUpdatedUser = theIdentityAPI.getUserByUUID(theAddedUser.getUUID());
    assertEquals(theNewEmail, theUpdatedUser.getProfessionalContactInfo().getEmail());
    // clean
    theIdentityAPI.removeUserByUUID(theAddedUser.getUUID());
    Utils.logout();
  }

  @Test
  public void updateUsernameNominal() throws Exception {
    Utils.login(adminUsername, "bpm");

    final IdentityAPI theIdentityAPI = AccessorUtil.getIdentityAPI();

    final String theUserID = null;
    final String theUsername = "user1";
    final String theNewUsername = "user 1";
    final String thePassword = "&:�*�mlkjh";
    final String theFirstName = "first";
    final String theLastName = "last";
    final String theEmail = null;

    org.ow2.bonita.facade.identity.User theAddedUser = theIdentityAPI.addUser(theUsername, thePassword, theFirstName, theLastName, theEmail);

    User theUpdatedUser = new User(theUserID, theNewUsername, thePassword, theFirstName, theLastName, null, null, null, null);
    IdentityDataStore.getInstance().updateUser(new BonitaUUID(theAddedUser.getUUID()), theUpdatedUser);
    try {
      theIdentityAPI.getUser(theUsername);
      fail("The given username should not be valid anymore!");
    } catch (UserNotFoundException e) {
      // Do nothing as it is the expected behavior.
    }

    assertNotNull(theIdentityAPI.getUser(theNewUsername));

    // clean
    theIdentityAPI.removeUser(theNewUsername);
    Utils.logout();
  }
}
