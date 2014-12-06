package com.visis.drachen.exception.client;

/**
 * Representing errors with the connection
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
