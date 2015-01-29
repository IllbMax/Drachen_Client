package com.vsis.drachen.model.objects;

import java.util.ArrayList;
import java.util.List;

public class ObjectEffectParameter {

	/**
	 * List of newly created items (at the server, so they must added to the
	 * ItemService)
	 */
	private List<Item> createdItems;
	/**
	 * list of deleted Item IDs (at the server, so they must be removed from the
	 * ItemService)
	 */
	private int[] deletedItemIds;

	/**
	 * list must created by ItemService out of the id array
	 */
	private List<Item> deletedItems = new ArrayList<Item>();

	public List<Item> getCreatedItems() {
		return createdItems;
	}

	public List<Item> getDeletedItems() {
		return deletedItems;
	}

	public int[] getDeletedItemIds() {
		return deletedItemIds;
	}

}