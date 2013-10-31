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
package org.bonitasoft.console.security.server.api;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author Anthony Birembaut
 *
 */
public interface ICredentialsEncryptionAPI {

    /**
     * parameter for the encrypted user credentials
     */
    String USER_CREDENTIALS_SEPARATOR = ":";
    
    /**
     * Encrypt and URL encode a credential character string
     * @param credential
     * @return the encrypted and encoded String
     * @throws GeneralSecurityException
     * @throws IOException
     */
    String encryptCredential(String credential) throws GeneralSecurityException, IOException;
    
    /**
     * URL decode and decrypt an encrypted ans encoded credential String
     * @param encryptedCredential
     * @return the decrypted and decoded String
     * @throws GeneralSecurityException
     * @throws IOException
     */
    String decryptCredential(String encryptedCredential) throws GeneralSecurityException, IOException;
    
    /**
     * generate a temporary token
     * @param encryptedCredential
     * @return a temporary token
     */
    String generateTemporaryToken(String encryptedCredential);
    
    /**
     * Retrieve the encrypted credentials from the token
     * @param temporaryToken
     * @return the encrypted credentials
     */
    String getCredentialsFromToken(String temporaryToken);
}
