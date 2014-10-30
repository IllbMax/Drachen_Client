package com.vsis.drachen.model;

public abstract class IdObject {

	private int id;

	public IdObject() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int newId) {
		id = newId;
	}

	@Override
	public int hashCode() {
		return id;
	}

}