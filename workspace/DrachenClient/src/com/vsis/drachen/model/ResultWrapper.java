package com.vsis.drachen.model;

import com.google.gson.annotations.Expose;
import com.visis.drachen.exception.DrachenBaseException;

public class ResultWrapper<T> {
	@Expose
	public boolean success;
	@Expose
	public T resultObject;
	@Expose
	public DrachenBaseException expection;
}
