package com.vsis.drachen.model;

import com.google.gson.annotations.Expose;

public class ResultWrapper<T> {
	@Expose
	public boolean success;
	@Expose
	public T resultObject;

}
