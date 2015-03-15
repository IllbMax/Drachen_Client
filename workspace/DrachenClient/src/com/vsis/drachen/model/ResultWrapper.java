package com.vsis.drachen.model;

import com.google.gson.annotations.Expose;
import com.vsis.drachen.exception.DrachenBaseException;

/**
 * 
 * Wraps the result of server calls
 * 
 * @param <T>
 *            parameter of the result object
 */
public class ResultWrapper<T> {
	/**
	 * true if the operation was successful
	 */
	@Expose
	public boolean success;
	/**
	 * result object of the operation
	 */
	@Expose
	public T resultObject;
	/**
	 * if no success: the exception describing the problem with the operation,
	 * otherwise null
	 */
	@Expose
	public DrachenBaseException exception;
}
