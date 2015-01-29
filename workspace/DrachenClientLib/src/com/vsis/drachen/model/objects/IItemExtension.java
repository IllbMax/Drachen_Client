package com.vsis.drachen.model.objects;

public interface IItemExtension {

	/**
	 * Adds the item to the location
	 * 
	 * @param locationId
	 *            target location
	 * @param item
	 *            target item
	 */
	boolean addItemToLocation(int locationId, Item item);

	/**
	 * Removes the item from the location
	 * 
	 * @param item
	 *            target item
	 */
	boolean removeItemFromLocation(Item item);

	/**
	 * Adds the item to the user
	 * 
	 * @param item
	 *            target item
	 */
	boolean addItemToUser(Item item);

	/**
	 * Removes the item from the user
	 * 
	 * @param item
	 *            target item
	 */
	boolean removeItemFromUser(Item item);

	/**
	 * Determines if the user has the item in the inventory
	 * 
	 * @param item
	 *            item you are looking for
	 * @return true if the user contains the item
	 */
	boolean userOwnsItem(Item item);

	/**
	 * Determines if the location has the item inside
	 * 
	 * @param locationId
	 *            id of the tested location
	 * @param item
	 *            item you are looking for
	 * @return true if the location contains the item
	 */
	boolean locationContainsItem(int locationId, Item item);

}
