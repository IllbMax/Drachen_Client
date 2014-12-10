package com.vsis.drachen.exception;

/**
 * Representing error with an invalid parameter id:
 * 
 * If there is no object matching the id
 * 
 */
public class IdNotFoundException extends ParameterException {

	public IdNotFoundException(String parameter) {
		super(parameter);
	}

	public IdNotFoundException(String parameter, String message) {
		super(parameter, message);
	}

	public IdNotFoundException(String parameter, String message, Throwable cause) {
		super(parameter, message, cause);
	}

}
