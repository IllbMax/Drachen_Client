package com.vsis.drachen.model.objects;

import java.util.List;

import com.vsis.drachen.model.IdObject;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.world.Location;

public class Item extends IdObject {

	public Item() {
	}

	public Item(ItemPrototype prototype) {
		setPrototype(prototype);
		setTakeable(prototype.isTakeable());
	}

	private ItemPrototype prototype;

	private boolean takeable;

	private User user;

	private Location location;
	private int locationId;

	public String getName() {
		// return name;
		return prototype.getName();
	}

	public ItemPrototype getPrototype() {
		return prototype;
	}

	public void setPrototype(ItemPrototype prototype) {
		this.prototype = prototype;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// public void setName(String newName){
	// this.name = newName;
	// }

	public String getDescription() {
		// return description;
		return prototype.getDescription();
	}

	public String getImageKey() {
		return prototype.getImageKey();
	}

	public List<ObjectAction> getObjectAction() {
		// return objectUseListeners;
		return prototype.getObjectAction();
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isTakeable() {
		return takeable;
	}

	public void setTakeable(boolean takeable) {
		this.takeable = takeable;
	}

	public ObjectAction findActionWithId(int actionId) {
		return this.prototype.findActionWithId(actionId);
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public int getLocationId() {
		return locationId;
	}
}