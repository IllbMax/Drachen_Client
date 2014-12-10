package com.vsis.drachen.exception.client;

/**
 * Representing errors if the server result cannot be read, has an invalid
 * format, or doesn't make sense
 * 
 */
public class InvalidResultException extends Exception {

	private String resultString;

	public String getResultString() {
		return resultString;
	}

	public InvalidResultException(String resultString) {
		this.resultString = resultString;
	}

	public InvalidResultException(String resultString, String message) {
		super(message);
		this.resultString = resultString;
	}

	public InvalidResultException(String resultString, Throwable cause) {
		super(cause);
		this.resultString = resultString;
	}

	public InvalidResultException(String resultString, String message,
			Throwable cause) {
		super(message, cause);
		this.resultString = resultString;
	}

}
