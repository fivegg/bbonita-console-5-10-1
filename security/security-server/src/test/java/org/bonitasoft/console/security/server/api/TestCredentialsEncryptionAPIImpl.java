package org.bonitasoft.console.security.server.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bonitasoft.console.security.server.constants.WebBonitaConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCredentialsEncryptionAPIImpl {

	static final ICredentialsEncryptionAPI crypt = SecurityAPIFactory.getCredentialsEncryptionAPI();
	static final String adminReadableCredentials = "admin" + ":" + "bpm";
	private String myAdminEncryptedCredentials;
	private String myAdminDecryptedCredentials;
	static final String johnReadableCredentials = "john" + ":" + "bpm";
	private String myJohnEncryptedCredentials;
	private String myJohnDecryptedCredentials;
	static final String jackReadableCredentials = "jack" + ":" + "bpm";
	private String myJackEncryptedCredentials;
	private String myJackDecryptedCredentials;
	
	static {
        final String bonitaHome = System.getProperty(WebBonitaConstants.BONITA_HOME);
        if (bonitaHome == null) {
            System.err.println("\n\n*** Forcing " + WebBonitaConstants.BONITA_HOME + " to target/bonita \n\n\n");
            System.setProperty(WebBonitaConstants.BONITA_HOME, "target/bonita");
        } else {
            System.err.println("\n\n*** "+WebBonitaConstants.BONITA_HOME + " already set to: " + bonitaHome + " \n\n\n");
        }
    }
	
	@Before
	public void setUp() throws Exception {
		myAdminEncryptedCredentials = crypt.encryptCredential(adminReadableCredentials );
		myAdminDecryptedCredentials =crypt.decryptCredential(myAdminEncryptedCredentials);
		myJohnEncryptedCredentials = crypt.encryptCredential(johnReadableCredentials );
		myJohnDecryptedCredentials =crypt.decryptCredential(myJohnEncryptedCredentials);
		myJackEncryptedCredentials = crypt.encryptCredential(jackReadableCredentials );
		myJackDecryptedCredentials =crypt.decryptCredential(myJackEncryptedCredentials);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncryptCredential() {
		
		try {
			assertFalse("Result of encryption is equals to the string not encrypted.", adminReadableCredentials.equals(myAdminEncryptedCredentials));
			System.out.println(myAdminEncryptedCredentials);
			assertFalse("Result of encryption is equals to the string not encrypted.", johnReadableCredentials.equals(myJohnEncryptedCredentials));
			System.out.println(myJohnEncryptedCredentials);
			assertFalse("Result of encryption is equals to the string not encrypted.", jackReadableCredentials.equals(myJackEncryptedCredentials));
			System.out.println(myJackEncryptedCredentials);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDecryptCredential() {
		try {
			assertTrue("Result of de-cryption is not equals to the string not encrypted.", adminReadableCredentials.equals(myAdminDecryptedCredentials));
			System.out.println(myAdminDecryptedCredentials);
			assertTrue("Result of de-cryption is not equals to the string not encrypted.", johnReadableCredentials.equals(myJohnDecryptedCredentials));
			System.out.println(myJohnDecryptedCredentials);
			assertTrue("Result of de-cryption is not equals to the string not encrypted.", jackReadableCredentials.equals(myJackDecryptedCredentials));
			System.out.println(myJackDecryptedCredentials);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
