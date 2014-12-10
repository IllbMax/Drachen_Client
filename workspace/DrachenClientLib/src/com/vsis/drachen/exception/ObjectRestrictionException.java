package com.vsis.drachen.exception;

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

	public ObjectRestrictionException(int id, String type) {
		oid = id;
		otype = type;
	}

	public ObjectRestrictionException(int id, String type, String message) {
		super(message);
		oid = id;
		otype = type;
	}

	public ObjectRestrictionException(int id, String type, Throwable cause) {
		super(cause);
		oid = id;
		otype = type;
	}

	public ObjectRestrictionException(int id, String type, String message,
			Throwable cause) {
		super(message, cause);
		oid = id;
		otype = type;
	}

}
