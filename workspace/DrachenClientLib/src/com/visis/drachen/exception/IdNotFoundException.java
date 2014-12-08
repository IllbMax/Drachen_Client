package com.visis.drachen.exception;

/**
 * Representing error with an invalid parameter id:
 * 
 * If there is no object matching the id
 * 
 */
public class IdNotFoundException extends DrachenBaseException {
	/**
	 * Name/Identifier for the parameter
	 */
	private String parameter;

	public String getParameter() {
		return parameter;
	}

	public IdNotFoundException(String parameter) {
		this.parameter = parameter;
	}

	public IdNotFoundException(String parameter, String message) {
		super(message);
		this.parameter = parameter;
	}

	public IdNotFoundException(String parameter, String message, Throwable cause) {
		super(message, cause);
		this.parameter = parameter;
	}

}
