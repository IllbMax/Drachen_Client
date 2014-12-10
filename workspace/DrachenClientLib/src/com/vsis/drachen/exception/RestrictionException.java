package com.vsis.drachen.exception;

/**
 * Representing error with access:
 * 
 * If user tries to access resources (eg. other user's (private) data)
 * 
 */
public class RestrictionException extends DrachenBaseException {

	public RestrictionException() {
	}

	public RestrictionException(String message) {
		super(message);
	}

	public RestrictionException(Throwable cause) {
		super(cause);
	}

	public RestrictionException(String message, Throwable cause) {
		super(message, cause);
	}

}
