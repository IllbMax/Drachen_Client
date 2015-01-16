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

	private String hint1;
	private String hint2;
	private String hint3;

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

	public String getHint1() {
		return hint1;
	}

	public void setHint1(String hint1) {
		this.hint1 = hint1;
	}

	public String getHint2() {
		return hint2;
	}

	public void setHint2(String hint2) {
		this.hint2 = hint2;
	}

	public String getHint3() {
		return hint3;
	}

	public void setHint3(String hint3) {
		this.hint3 = hint3;
	}

}