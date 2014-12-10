package com.vsis.drachen.exception.client;

/**
 * Representing errors with the connection, or no access to the resource etc.
 * 
 */
public class ConnectionException extends Exception {

	public ConnectionException() {
	}

	public ConnectionException(String message) {
		super(message);
	}

	public ConnectionException(Throwable cause) {
		super(cause);
	}

	public ConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

}
