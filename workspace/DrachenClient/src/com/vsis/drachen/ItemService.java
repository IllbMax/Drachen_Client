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
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachen.exception.ObjectRestrictionException;
import com.vsis.drachen.exception.RestrictionException;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.objects.IItemExtension;
import com.vsis.drachen.model.objects.Item;
import com.vsis.drachen.model.objects.ObjectAction;
import com.vsis.drachen.model.objects.ObjectEffectParameter;

public class ItemService {

	private class LocationItem {

		public List<Item> presentItems;
		public int locationId;
		public java.util.Date lastUpdate;

		public boolean isLoaded() {
			return lastUpdate != null;
		}

		public boolean needsReload() {
			System.out.print("seconds passed: ");
			System.out.println((System.currentTimeMillis() - lastUpdate
					.getTime()) + "/" + millisecondsTillItemsReload);
			return !isLoaded()
					|| (System.currentTimeMillis() - lastUpdate.getTime()) > millisecondsTillItemsReload;
		}
	}

	BlubClient client;

	Map<Integer, Item> itemIdMap;

	Map<Integer, LocationItem> locationIdItemMap;
	int millisecondsTillItemsReload = 2 * 60 * 1000;
	User user;

	/**
	 * defines methods for low level item manipulations
	 */
	private IItemExtension itemextension = new IItemExtension() {

		@Override
		public boolean removeItemFromLocation(Item item) {
			int locationId = item.getLocationId();
			if (locationId <= 0)
				return false;
			else {
				LocationItem loc = locationIdItemMap.get(locationId);

				boolean suc = loc != null && loc.presentItems.remove(item);
				if (suc)
					item.setLocationId(-1);
				return suc;
			}

		}

		@Override
		public boolean addItemToLocation(int locationId, Item item) {
			LocationItem loc = locationIdItemMap.get(locationId);
			item.setLocationId(locationId);
			return loc != null && loc.presentItems.add(item);
		}

		@Override
		public boolean addItemToUser(Item item) {
			user.addItem(item);
			return true;
		}

		@Override
		public boolean removeItemFromUser(Item item) {
			user.removeItem(item);
			return true;
		}

		@Override
		public boolean userOwnsItem(Item item) {
			return user.getItems().contains(item);
		}

		@Override
		public boolean locationContainsItem(int locationId, Item item) {
			LocationItem loc = locationIdItemMap.get(locationId);
			return loc != null && loc.presentItems.contains(item);
		}
	};

	public ItemService(BlubClient client) {
		itemIdMap = new HashMap<Integer, Item>();
		locationIdItemMap = new HashMap<Integer, LocationItem>();
		setClient(client);
	}

	private void setClient(BlubClient client) {
		this.client = client;
	}

	public void setUser(User user) {
		this.user = user;
		if (user != null) {
			for (Item item : user.getItems())
				addItemToMap(item);
		}
	}

	public synchronized List<Item> getPresentItemsForLocation(int locationId,
			boolean forceReload) throws InternalProcessException,
			RestrictionException, IdNotFoundException, DrachenBaseException {
		LocationItem locationQuests;
		if (!forceReload
				&& locationIdItemMap.containsKey(locationId)
				&& !(locationQuests = locationIdItemMap.get(locationId))
						.needsReload()) {
			// do nothing, everything is cached
		} else // load data
		{
			List<Item> items = client.ItemsForLocation(locationId);
			Collections.sort(items, new Comparator<Item>() {

				@Override
				public int compare(Item o1, Item o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			locationQuests = new LocationItem();
			locationQuests.presentItems = items;
			locationQuests.locationId = locationId;
			locationQuests.lastUpdate = new Date();
			locationIdItemMap.put(locationId, locationQuests);
			for (Item item : items) {
				item.setLocationId(locationId);
				addItemToMap(item);
			}
		}
		return locationQuests.presentItems;
	}

	public synchronized List<Item> getPresentItemsForLocation(int locationId)
			throws InternalProcessException, RestrictionException,
			IdNotFoundException, DrachenBaseException {
		return getPresentItemsForLocation(locationId, false);
	}

	public Item getItemFromId(int itemId) {
		return itemIdMap.get(itemId);
	}

	private void addItemToMap(Item item) {
		itemIdMap.put(item.getId(), item);
	}

	private Item removeItemFromMap(int itemId) {
		return itemIdMap.remove(itemId);
	}

	public boolean performObjectAction(ObjectAction action, Item item)
			throws MissingParameterException, IdNotFoundException,
			InternalProcessException, RestrictionException,
			ObjectRestrictionException, DrachenBaseException {
		// perform effect at the server
		ObjectEffectParameter param = client.performObjectAction(item.getId(),
				action.getId());
		if (param != null) {
			// prepare the parameter object for the effect
			for (Item i : param.getCreatedItems())
				addItemToMap(i);
			for (int itemId : param.getDeletedItemIds())
				param.getDeletedItems().add(getItemFromId(itemId));
			// perform the effect locally
			action.getEffect().perform(itemextension, param, user, item);
			// remove the deleted items from service
			for (Item i : param.getDeletedItems())
				removeItemFromMap(i.getId());
			return true;
		} else
			return false;
	}

	public void dispose() {
		// no resources, all done

	}

}
