package com.vsis.drachen.model.objects;

import java.util.ArrayList;
import java.util.List;

import com.vsis.drachen.model.IdObject;

public class ItemPrototype extends IdObject {

	public ItemPrototype() {
		setObjectAction(new ArrayList<ObjectAction>());
	}

	public ItemPrototype(String name, String description, boolean takeable) {
		setObjectAction(new ArrayList<ObjectAction>());

		setName(name);
		setDescription(description);
		setTakeable(takeable);
	}

	private String name;

	private String description;

	private String imageKey;

	private boolean takeable;

	private List<ObjectAction> objectActions;

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ObjectAction> getObjectAction() {
		return objectActions;
	}

	public void setObjectAction(List<ObjectAction> objectAction) {
		this.objectActions = objectAction;
	}

	public void addObjectAction(ObjectAction objectAction) {
		this.objectActions.add(objectAction);
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public boolean isTakeable() {
		return takeable;
	}

	public void setTakeable(boolean takeable) {
		this.takeable = takeable;
	}

	public ObjectAction findActionWithId(int actionId) {
		for (ObjectAction a : objectActions) {
			if (a != null && a.getId() == actionId)
				return a;
		}
		return null;
	}

}