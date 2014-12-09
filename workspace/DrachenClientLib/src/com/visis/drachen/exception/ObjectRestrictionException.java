package com.visis.drachen.exception;

/**
 * Representing error with accessing an object:
 * 
 * If user tries to access an object (with id) not of his own
 * 
 */
public class ObjectRestrictionException extends DrachenBaseException {

	/**
	 * id of the object
	 */
	private int oid;
	/**
	 * type of the object (as string)
	 */
	private String otype;

	public int getOid() {
		return oid;
	}

	public String getOtype() {
		return otype;
	}

	public ObjectRestrictionException() {
	}

	public ObjectRestrictionException(String message) {
		super(message);
	}

	public ObjectRestrictionException(Throwable cause) {
		super(cause);
	}

	public ObjectRestrictionException(String message, Throwable cause) {
		super(message, cause);
	}

}
