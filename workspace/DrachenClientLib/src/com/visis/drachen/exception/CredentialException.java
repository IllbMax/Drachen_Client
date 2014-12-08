package com.visis.drachen.exception;

/**
 * Representing error with login / authentication:
 * 
 * If user tries to login with wrong password
 * 
 */
public class CredentialException extends DrachenBaseException {

	public CredentialException() {
	}

	public CredentialException(String message) {
		super(message);
	}

	public CredentialException(Throwable cause) {
		super(cause);
	}

	public CredentialException(String message, Throwable cause) {
		super(message, cause);
	}

}
