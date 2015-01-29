package com.vsis.drachen.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vsis.drachen.model.quest.QuestPrototype;

public class Item extends IdObject {

	private String name;

	private Set<QuestPrototype> quests = new HashSet<QuestPrototype>(0);

	private String talk;

	private String imageKey;

	public Item() {

	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public String getTalk() {
		return talk;
	}

	public void setTalk(String talk) {
		this.talk = talk;
	}

	public void setAvailableQuests(Collection<QuestPrototype> availableQuests) {
		quests.clear();
		quests.addAll(availableQuests);
	}

	public Set<QuestPrototype> getQuestPrototypes() {
		return quests;
	}

	public void take() {
	}

	public void use() {
	}

	public void combine() {
	}

	public void lookat() {
	}
}
