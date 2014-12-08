package com.visis.drachen.exception;

/**
 * Exception caused by parameters...
 * 
 */
public abstract class ParameterException extends DrachenBaseException {

	/**
	 * Name/Identifier for the parameter
	 */
	private String parameter;

	/**
	 * Name/Identifier for the parameter
	 * 
	 * @return the parameter name
	 */
	public String getParameter() {
		return parameter;
	}

	public ParameterException(String parameter) {
		super();
		this.parameter = parameter;
	}

	public ParameterException(String parameter, String message) {
		super(message);
		this.parameter = parameter;
	}

	public ParameterException(String parameter, String message, Throwable cause) {
		super(message, cause);
		this.parameter = parameter;
	}

}