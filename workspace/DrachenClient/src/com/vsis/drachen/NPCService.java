package com.vsis.drachen;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.IdNotFoundException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.RestrictionException;
import com.vsis.drachen.model.NPC;
import com.vsis.drachen.model.User;

/**
 * Service to holding all NPCs with automated reload.
 */
public class NPCService {

	private class LocationNPC {

		public List<NPC> presentNPC;
		public int locationId;
		public java.util.Date lastUpdate;

		public boolean isLoaded() {
			return lastUpdate != null;
		}

		public boolean needsReload() {
			System.out.print("seconds passed: ");
			System.out.println((new java.util.Date().getTime() - lastUpdate
					.getTime()) + "/" + millisecondsTillNPCReload);
			return !isLoaded()
					|| (System.currentTimeMillis() - lastUpdate.getTime()) > millisecondsTillNPCReload;
		}
	}

	BlubClient client;

	Map<Integer, NPC> npcIdMap;

	Map<Integer, LocationNPC> locationIdNPCMap;
	int millisecondsTillNPCReload = 10 * 60 * 1000;
	User user;

	public NPCService(BlubClient client) {
		npcIdMap = new HashMap<Integer, NPC>();
		locationIdNPCMap = new HashMap<Integer, LocationNPC>();
		setClient(client);
	}

	private void setClient(BlubClient client) {
		this.client = client;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns a list of all {@link NPC} at the location
	 * 
	 * @param locationId
	 *            id of the location
	 * @param forceReload
	 *            reloads the items from server even if no reload is necessary
	 * @return List of NPCs at that location
	 * 
	 * @throws InternalProcessException
	 * @throws RestrictionException
	 * @throws IdNotFoundException
	 * @throws DrachenBaseException
	 */
	public synchronized List<NPC> getPresentNPCForLocation(int locationId,
			boolean forceReload) throws InternalProcessException,
			RestrictionException, IdNotFoundException, DrachenBaseException {
		LocationNPC locationQuests;
		if (!forceReload
				&& locationIdNPCMap.containsKey(locationId)
				&& !(locationQuests = locationIdNPCMap.get(locationId))
						.needsReload()) {
			// do nothing, everything is cached
		} else // load data
		{
			List<NPC> npcs = client.NPCsForLocation(locationId);
			Collections.sort(npcs, new Comparator<NPC>() {

				@Override
				public int compare(NPC o1, NPC o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			locationQuests = new LocationNPC();
			locationQuests.presentNPC = npcs;
			locationQuests.locationId = locationId;
			locationQuests.lastUpdate = new Date();
			locationIdNPCMap.put(locationId, locationQuests);
			for (NPC npc : npcs) {
				addNPCToMap(npc);
			}
		}
		return locationQuests.presentNPC;
	}

	/**
	 * Returns a list of all {@link NPC} at the location
	 * 
	 * @param locationId
	 *            id of the location
	 * @return List of NPCs at that location
	 * 
	 * @throws InternalProcessException
	 * @throws RestrictionException
	 * @throws IdNotFoundException
	 * @throws DrachenBaseException
	 */
	public synchronized List<NPC> getPresentNPCForLocation(int locationId)
			throws InternalProcessException, RestrictionException,
			IdNotFoundException, DrachenBaseException {
		return getPresentNPCForLocation(locationId, false);
	}

	/**
	 * Get the NPC for id npcId
	 * 
	 * @param npcId
	 *            the id
	 * @return The NPC or null
	 */
	public NPC getNPCFromId(int npcId) {
		return npcIdMap.get(npcId);
	}

	private void addNPCToMap(NPC npc) {
		npcIdMap.put(npc.getId(), npc);
	}

	private NPC removeNPCFromMap(int npcId) {
		return npcIdMap.remove(npcId);
	}

	public void dispose() {
		// no resources, all done
	}

}
