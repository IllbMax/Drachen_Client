package com.visis.drachen.exception;

/**
 * Representing error with missing parameters:
 * 
 * If a required parameter is not given
 * 
 */
public class MissingParameterException extends DrachenBaseException {
	/**
	 * Name/Identifier for the parameter
	 */
	private String parameter;

	public String getParameter() {
		return parameter;
	}

	public MissingParameterException(String parameter) {
		this.parameter = parameter;
	}

	public MissingParameterException(String parameter, String message) {
		super(message);
		this.parameter = parameter;
	}

	public MissingParameterException(String parameter, String message,
			Throwable cause) {
		super(message, cause);
		this.parameter = parameter;
	}

}
