package com.vsis.drachen.exception;

public abstract class DrachenBaseException extends Exception {

	/**
	 * renew the detailMessage, needed for gson.
	 */
	protected String detailMessage;

	@Override
	public String getMessage() {
		return detailMessage;
	}

	public DrachenBaseException() {
		detailMessage = "";
	}

	public DrachenBaseException(String message) {
		super(message);
		detailMessage = message;
	}

	public DrachenBaseException(Throwable cause) {
		super(cause);
		detailMessage = cause.getMessage();
	}

	public DrachenBaseException(String message, Throwable cause) {
		super(message, cause);
		detailMessage = message;
	}

	public DrachenBaseException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		detailMessage = message;
	}

}
