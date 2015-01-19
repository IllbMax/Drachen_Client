package com.vsis.drachen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.IdNotFoundException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachen.exception.ObjectRestrictionException;
import com.vsis.drachen.exception.QuestFinishedException;
import com.vsis.drachen.exception.QuestStartException;
import com.vsis.drachen.exception.QuestTargetException;
import com.vsis.drachen.exception.RestrictionException;
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
			int locationId, boolean forceReload)
			throws InternalProcessException, RestrictionException,
			IdNotFoundException, DrachenBaseException {
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
			int locationId) throws InternalProcessException,
			RestrictionException, IdNotFoundException, DrachenBaseException {
		return getAvailableQuestForLocation(locationId, false);
	}

	public Collection<QuestPrototype> removeStartedQuests(
			Collection<QuestPrototype> questPrototypes) {
		List<QuestPrototype> result = new ArrayList<QuestPrototype>(
				questPrototypes.size());
		Set<Integer> protoIds = new HashSet<Integer>();
		Collection<Quest> quests = getUserQuests();
		for (Quest q : quests)
			protoIds.add(q.getPrototype().getId());
		for (QuestPrototype q : questPrototypes)
			if (!protoIds.contains(q.getId()))
				result.add(q);
		return result;
	}

	public Quest startQuest(int questPrototypeId)
			throws MissingParameterException, IdNotFoundException,
			QuestStartException, InternalProcessException,
			RestrictionException, DrachenBaseException {

		Quest quest = client.StartQuest(questPrototypeId);

		// TODO: remove Prototype Quest form Location
		if (quest != null) {
			user.startQuest(quest);
			addQuestToMap(quest);
		}
		return quest;
	}

	public boolean abortQuest(int questId) throws MissingParameterException,
			IdNotFoundException, ObjectRestrictionException,
			QuestFinishedException, InternalProcessException,
			RestrictionException, DrachenBaseException {

		Quest quest = removeQuestFromMap(questId);
		if (quest == null) // if quest is not listed assume that quest doesn't
							// exists
			throw new IdNotFoundException("questid");
		Boolean success = client.AbortQuest(questId);

		if (success != null && success) {
			user.abortQuest(quest);
			return true;
		}
		return false;
	}

	public boolean finishQuest(int questId) throws MissingParameterException,
			IdNotFoundException, QuestTargetException, QuestFinishedException,
			InternalProcessException, RestrictionException,
			ObjectRestrictionException, DrachenBaseException {

		Quest quest = getQuestFromId(questId);
		if (quest == null) // if quest is not listed assume that quest doesn't
							// exists
			throw new IdNotFoundException("questid");
		Boolean success = client.FinishQuest(questId);
		if (success != null && success) {
			quest.finishQuest();

		}
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
