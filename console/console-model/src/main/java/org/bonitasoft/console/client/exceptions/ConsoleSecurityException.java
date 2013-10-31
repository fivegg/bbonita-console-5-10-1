package org.bonitasoft.console.client.exceptions;

public class ConsoleSecurityException extends ConsoleException {

	private static final long serialVersionUID = 5975789540185642527L;

	protected ConsoleSecurityException() {
		super();
		// mandatory for serialization.
	}
	
	public ConsoleSecurityException(String aUserName, String aResourceName) {
		super("User " + aUserName + " tried to access protected resource " + aResourceName +"!",null);
	}
}
