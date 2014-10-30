package com.vsis.drachen.model;

import java.util.HashSet;
import java.util.Set;

import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.world.Location;

public class User extends IdObject {

	private Location location;

	public User() {

	}

	private String displayName;

	private Set<Quest> quests = new HashSet<Quest>(0);

	private boolean logedIn;

	public String getDisplayName() {
		return displayName;
	}

	public Location getLocation() {
		return location;
	}

	public boolean IsLogedIn() {
		return logedIn;
	}

	public void setDisplayName(String newDisplayName) {
		displayName = newDisplayName;
	}

	public void setLocation(Location newLocation) {
		location = newLocation;
	}

	public void setLogedIn(boolean newStatus) {
		logedIn = newStatus;
	}

	public Set<Quest> getQuests() {
		return quests;
	}

	public void abortQuest(Quest quest) {
		quests.remove(quest);
	}

	public void startQuest(Quest quest) {
		quests.add(quest);
	}

}
