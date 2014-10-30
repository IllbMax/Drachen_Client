package com.vsis.drachen.model.quest;

import com.vsis.drachen.model.IdObject;

public class QuestPrototype extends IdObject {

	public QuestPrototype() {
	}

	public QuestPrototype(String name, String description) {

		setName(name);
		setDescription(description);
	}

	private String name;

	private String description;

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

}