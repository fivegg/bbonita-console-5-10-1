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
package org.bonitasoft.console.security.server.api.impl;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.bonitasoft.console.security.server.accessor.SecurityProperties;
import org.bonitasoft.console.security.server.api.ICredentialsEncryptionAPI;
import org.ow2.bonita.identity.auth.DomainOwner;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.Base64;
import org.ow2.bonita.util.BonitaConstants;

/**
 * @author Anthony Birembaut
 * 
 */
public class CredentialsEncryptionAPIImpl implements ICredentialsEncryptionAPI {

    /**
     *  8-byte Salt
     */
    private final static byte[] encryptionSalt = { (byte) 0xB9, (byte) 0x8A, (byte) 0xB7, (byte) 0x21, (byte) 0x68, (byte) 0xB2, (byte) 0xE8, (byte) 0x15 };

    /**
     *  Iteration count
     */
    private final static int encryptionIterationCount = 20;

    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(CredentialsEncryptionAPIImpl.class.getName());

    /**
     * Instance attribute
     */
    private static CredentialsEncryptionAPIImpl INSTANCE = null;

    /**
     * @return the CredentialsEncryptionAPIImpl instance
     */
    public static synchronized CredentialsEncryptionAPIImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CredentialsEncryptionAPIImpl();
        }
        return INSTANCE;
    }

    /**
     * Private contructor to prevent instantiation
     */
    private CredentialsEncryptionAPIImpl() {
    }

    /**
     * {@inheritDoc}
     */
    public String encryptCredential(final String credential) throws GeneralSecurityException, IOException {

        String encodedEncryptedCredential = null;

        final String key = SecurityProperties.getInstance().getLoginKey();

        if (key != null) {
            // generate the key
            final KeySpec keySpec = new PBEKeySpec(key.toCharArray(), encryptionSalt, encryptionIterationCount);
            final SecretKey secretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
    
            // Prepare the parameter for the cipher
            final AlgorithmParameterSpec paramSpec = new PBEParameterSpec(encryptionSalt, encryptionIterationCount);
    
            // Create the cipher
            final Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
    
            // Encode bytes to base64 to get a string
            final String encryptedCredential = Base64.encodeBytes(cipher.doFinal(credential.getBytes("UTF-8")));
    
            encodedEncryptedCredential = URLEncoder.encode(encryptedCredential, "UTF-8");
        } else {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Missing encryption key parameter in the config properties file");
            }
        }
        
        return encodedEncryptedCredential;
    }

    /**
     * {@inheritDoc}
     */
    public String decryptCredential(final String encryptedCredential) throws GeneralSecurityException, IOException {

        String decryptedCredential = null;

        final String decodedEncryptedCredential = URLDecoder.decode(encryptedCredential, "UTF-8");

        final String key = SecurityProperties.getInstance().getLoginKey();

        if (key != null) {
            // generate the key
            final KeySpec keySpec = new PBEKeySpec(key.toCharArray(), encryptionSalt, encryptionIterationCount);
            final SecretKey secretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
    
            // Prepare the parameter for the cipher
            final AlgorithmParameterSpec paramSpec = new PBEParameterSpec(encryptionSalt, encryptionIterationCount);
    
            // Create the cipher
            final Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
    
            // Encode bytes to base64 to get a string
            final byte[] encryptedCredentialBytes = Base64.decode(decodedEncryptedCredential);
    
            decryptedCredential = new String(cipher.doFinal(encryptedCredentialBytes), "UTF-8");
        } else {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Missing encryption key parameter in the config properties file");
            }
        }
        
        return decryptedCredential;
    }

    /**
     * {@inheritDoc}
     */
    public String generateTemporaryToken(final String encryptedCredential) {
        return AccessorUtil.getWebAPI().generateTemporaryToken(encryptedCredential);
    }

    /**
     * {@inheritDoc}
     */
    public String getCredentialsFromToken(final String temporaryToken) {
        try {
            final String domain = DomainOwner.getDomain();
            if (domain == null) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "The domain has not been set. Using the default domain");
                }
                DomainOwner.setDomain(BonitaConstants.DEFAULT_DOMAIN);
            }
        } catch (final Exception e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "The domain has not been set. Using the default domain");
            }
            DomainOwner.setDomain(BonitaConstants.DEFAULT_DOMAIN);
        }
        return AccessorUtil.getWebAPI().getIdentityKeyFromTemporaryToken(temporaryToken);
    }
}
