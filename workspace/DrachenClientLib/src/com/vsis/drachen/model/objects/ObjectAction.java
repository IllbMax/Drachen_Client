package com.vsis.drachen.model.objects;

import com.vsis.drachen.model.IdObject;

public class ObjectAction extends IdObject {

	public ObjectAction() {

	}

	public ObjectAction(String name, String action, ObjectEffect effect) {
		this(name, action, effect, null);
	}

	public ObjectAction(String name, String action, ObjectEffect effect,
			ObjectUseListener activator) {
		this(name, action, effect, activator, false, false);
	}

	public ObjectAction(String name, String action, ObjectEffect effect,
			ObjectUseListener activator, boolean needHoldByUser,
			boolean needHoldByLocation) {
		this.name = name;
		this.actionDescription = action;
		this.effect = effect;
		this.activator = activator;
		this.needHoldByUser = needHoldByUser;
		this.needHoldByLocation = needHoldByLocation;
	}

	private String name;

	private String actionDescription;

	/**
	 * if <code>== null</code>: activation via button with title {@link #name} <br/>
	 * if <code>!= null</code>: activation via sensor
	 */
	private ObjectUseListener activator;

	private ObjectEffect effect;

	private boolean needHoldByUser;
	private boolean needHoldByLocation;

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public String getActionDescription() {
		return actionDescription;
	}

	public void setActionDescription(String action) {
		this.actionDescription = action;
	}

	public ObjectUseListener getActivator() {
		return activator;
	}

	public void setActivator(ObjectUseListener activator) {
		this.activator = activator;
	}

	public ObjectEffect getEffect() {
		return effect;
	}

	public void setEffect(ObjectEffect effect) {
		this.effect = effect;
	}

	public boolean isNeedHoldByUser() {
		return needHoldByUser;
	}

	public void setNeedHoldByUser(boolean needHoldByUser) {
		this.needHoldByUser = needHoldByUser;
	}

	public boolean isNeedHoldByLocation() {
		return needHoldByLocation;
	}

	public void setNeedHoldByLocation(boolean needHoldByLocation) {
		this.needHoldByLocation = needHoldByLocation;
	}
}