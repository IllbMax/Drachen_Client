package com.vsis.drachen.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vsis.drachen.model.objects.Item;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.world.Location;

public class User extends IdObject implements IUser {

	private Location location;

	public User() {

	}

	private String displayName;

	private Set<Quest> quests = new HashSet<Quest>(0);
	private List<Item> items = new ArrayList<Item>();
	private boolean logedIn;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.IUser#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.IUser#getLocation()
	 */
	@Override
	public Location getLocation() {
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.IUser#IsLogedIn()
	 */
	@Override
	public boolean IsLogedIn() {
		return logedIn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.IUser#setDisplayName(java.lang.String)
	 */
	@Override
	public void setDisplayName(String newDisplayName) {
		displayName = newDisplayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vsis.drachen.model.IUser#setLocation(com.vsis.drachen.model.world
	 * .Location)
	 */
	@Override
	public void setLocation(Location newLocation) {
		location = newLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.IUser#setLogedIn(boolean)
	 */
	@Override
	public void setLogedIn(boolean newStatus) {
		logedIn = newStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.IUser#getQuests()
	 */
	@Override
	public Set<Quest> getQuests() {
		return quests;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vsis.drachen.model.IUser#abortQuest(com.vsis.drachen.model.quest.
	 * Quest)
	 */
	@Override
	public void abortQuest(Quest quest) {
		quests.remove(quest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vsis.drachen.model.IUser#startQuest(com.vsis.drachen.model.quest.
	 * Quest)
	 */
	@Override
	public void startQuest(Quest quest) {
		quests.add(quest);
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public void addItem(Item item) {
		this.items.add(item);
	}

	public void removeItem(Item item) {
		this.items.remove(item);
	}
}
