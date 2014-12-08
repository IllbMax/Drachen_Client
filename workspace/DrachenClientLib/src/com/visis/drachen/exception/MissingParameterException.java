package com.visis.drachen.exception;

/**
 * Representing error with missing parameters:
 * 
 * If a required parameter is not given
 * 
 */
public class MissingParameterException extends ParameterException {

	public MissingParameterException(String parameter) {
		super(parameter);
	}

	public MissingParameterException(String parameter, String message) {
		super(parameter, message);
	}

	public MissingParameterException(String parameter, String message,
			Throwable cause) {
		super(parameter, message, cause);
	}

}
