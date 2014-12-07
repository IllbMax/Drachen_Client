package com.visis.drachen.exception;

/**
 * Representing error with internal processes:
 * 
 * If an exception in eg. loading/saving occurs (=> an empty result can occur)
 * 
 */
public class InternalProcessException extends DrachenBaseException {

	public InternalProcessException() {
	}

	public InternalProcessException(String message) {
		super(message);
	}

	public InternalProcessException(Throwable cause) {
		super(cause);
	}

	public InternalProcessException(String message, Throwable cause) {
		super(message, cause);
	}

}
