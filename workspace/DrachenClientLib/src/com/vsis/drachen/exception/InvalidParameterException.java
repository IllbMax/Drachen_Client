package com.vsis.drachen.exception;

/**
 * Representing error with wrong parameters:
 * 
 * If a parameter has an invalid format
 * 
 */
public class InvalidParameterException extends ParameterException {

	/**
	 * Enum of possible reasons for invalidation
	 * 
	 */
	public static enum InvalidType {
		/**
		 * if the parameter value is too short
		 * 
		 * extraType = int
		 */
		TooShort,
		/**
		 * if the parameter value is too long
		 * 
		 * extraType = int
		 */
		TooLong,
		/**
		 * if the parameter value has the wrong format
		 * 
		 * extraType = String with format or class name
		 */
		WrongFormat,
		/**
		 * if the parameter value has to be unique but isn't
		 * 
		 * extraType is unused (so empty String or null)
		 */
		NotUnique,
		/**
		 * if the parameter is a null (equivalent) value (an it isn't allowed
		 * too use one, eg. null, "", etc.)
		 * 
		 * extraType is unused (so empty String or null)
		 */
		NullOrEmpty,
		/**
		 * if the parameter is invalid for any other reason
		 * 
		 * extraType can be a String description
		 */
		Other
	}

	/**
	 * Reason for invalid Exception
	 */
	private InvalidType invalidType;
	/**
	 * short extra information extending the type info
	 */
	private String extraInfo;

	public InvalidType getType() {
		return invalidType;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public InvalidParameterException(String parameter, InvalidType type,
			String extraInfo, String message) {
		super(parameter, message);

		this.invalidType = type;
		this.extraInfo = extraInfo;
	}

	public InvalidParameterException(String parameter, InvalidType type,
			String extraInfo, String message, Throwable cause) {
		super(parameter, message, cause);

		this.invalidType = type;
		this.extraInfo = extraInfo;
	}

}
