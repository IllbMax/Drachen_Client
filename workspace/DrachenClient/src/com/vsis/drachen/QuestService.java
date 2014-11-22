package com.vsis.drachen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vsis.drachen.model.User;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestPrototype;
import com.vsis.drachen.model.world.Location;

public class QuestService {

	public static interface LocationChanged {
		void Changed(Location oldname, Location newname);
	}

	private class LocationQuests {

		public List<QuestPrototype> availableQuests;
		public int locationId;
		public java.util.Date lastUpdate;

		public boolean isLoaded() {
			return lastUpdate != null;
		}

		public boolean needsReload() {
			System.out.print("seconds passed: ");
			System.out.println((new java.util.Date().getTime() - lastUpdate
					.getTime()) + "/" + millisecondsTillQuestReload);
			return !isLoaded()
					|| (new java.util.Date().getTime() - lastUpdate.getTime()) > millisecondsTillQuestReload;
		}
	}

	private class QuestPrototypWrapper {
		public QuestPrototype questPrototype;
	}

	BlubClient client;

	// SparseArray<Location> locationIdMap;
	Map<Integer, Quest> questIdMap;

	Map<Integer, LocationQuests> locationIdPrototypeMap;
	Map<Integer, QuestPrototypWrapper> questPrototypeIdMap;
	int millisecondsTillQuestReload = 10 * 60 * 1000;
	User user;

	List<LocationChanged> listener;

	public QuestService(BlubClient client) {
		listener = new ArrayList<QuestService.LocationChanged>();
		// locationIdMap = new SparseArray<Location>();
		questIdMap = new HashMap<Integer, Quest>();
		locationIdPrototypeMap = new HashMap<Integer, LocationQuests>();
		questPrototypeIdMap = new HashMap<Integer, QuestService.QuestPrototypWrapper>();
		setClient(client);
	}

	private void setClient(BlubClient client) {
		this.client = client;
	}

	public void setUser(User user) {
		this.user = user;
		questIdMap.clear();
		for (Quest q : user.getQuests()) {
			addQuestToMap(q);
		}
	}

	public synchronized Collection<QuestPrototype> getAvailableQuestForLocation(
			int locationId, boolean forceReload) {
		LocationQuests locationQuests;
		if (!forceReload
				&& locationIdPrototypeMap.containsKey(locationId)
				&& !(locationQuests = locationIdPrototypeMap.get(locationId))
						.needsReload()) {
			// do nothing, everything is cached
		} else // load data
		{
			List<QuestPrototype> prototypes = client
					.QuestsForLocation(locationId);
			locationQuests = new LocationQuests();
			locationQuests.availableQuests = prototypes;
			locationQuests.locationId = locationId;
			locationQuests.lastUpdate = new Date();
			locationIdPrototypeMap.put(locationId, locationQuests);
		}
		return locationQuests.availableQuests;
	}

	public synchronized Collection<QuestPrototype> getAvailableQuestForLocation(
			int locationId) {
		return getAvailableQuestForLocation(locationId, false);
	}

	public Quest startQuest(int questPrototypeId) {
		Quest quest = client.StartQuest(questPrototypeId);
		// remove Prototype Quest form Location
		// TODO: check for success
		user.startQuest(quest);
		addQuestToMap(quest);
		return quest;
	}

	public boolean abortQuest(int questId) {
		Boolean success = client.AbortQuest(questId);
		// TODO: check for success
		Quest quest = removeQuestFromMap(questId);
		user.abortQuest(quest);
		return success;
	}

	public boolean finishQuest(int questId) {
		Boolean success = client.FinishQuest(questId);
		// TODO: check for success
		// Quest quest = removeQuestFromMap(questId);
		// user.finishQuest(quest);
		return success;
	}

	public Quest getQuestFromId(int questId) {
		return questIdMap.get(questId);
	}

	private void addQuestToMap(Quest quest) {
		questIdMap.put(quest.getId(), quest);
	}

	private Quest removeQuestFromMap(int questId) {
		return questIdMap.remove(questId);
	}

	public Collection<Quest> getUserQuests() {
		// TODO maybe refresh quests of the user
		return user.getQuests();
	}

	public void dispose() {
		// no resources, all done

	}

}
