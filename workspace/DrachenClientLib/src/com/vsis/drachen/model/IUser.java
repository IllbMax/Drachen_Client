package com.vsis.drachen.model;

import java.util.Set;

import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.world.Location;

public interface IUser {

	public abstract String getDisplayName();

	public abstract Location getLocation();

	public abstract boolean IsLogedIn();

	public abstract void setDisplayName(String newDisplayName);

	public abstract void setLocation(Location newLocation);

	public abstract void setLogedIn(boolean newStatus);

	public abstract Set<Quest> getQuests();

	public abstract void abortQuest(Quest quest);

	public abstract void startQuest(Quest quest);

}