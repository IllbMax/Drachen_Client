package com.vsis.drachen;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.InvalidParameterException;
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachen.exception.RestrictionException;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.world.Location;
import com.vsis.drachen.model.world.Point;

/**
 * Service to holding all locations with automated reload and defines methods to
 * change the user's location.
 */
public class LocationService {

	public static interface LocationChanged {
		void Changed(Location oldLocation, Location newLocation);
	}

	BlubClient client;
	private boolean inRoom;

	// SparseArray<Location> locationIdMap;
	/**
	 * Map: locationId -> location
	 */
	Map<Integer, Location> locationIdMap;
	/**
	 * Map: scannerkey -> location
	 */
	Map<String, Location> locationKeyMap;
	/**
	 * List of top level locations. (the locations itself build the tree with
	 * all locations)
	 */
	List<Location> locationHierarchy;

	User user;

	List<LocationChanged> listener;

	private Date lastCurrentLocationSetTime = new Date();

	public LocationService(BlubClient client) {
		listener = new ArrayList<LocationService.LocationChanged>();
		locationHierarchy = new ArrayList<Location>();
		// locationIdMap = new SparseArray<Location>();
		locationIdMap = new HashMap<Integer, Location>();
		locationKeyMap = new HashMap<String, Location>();

		setClient(client);
	}

	public void setUser(User user) {
		this.user = user;
	}

	private void setClient(BlubClient client) {
		this.client = client;
	}

	/**
	 * Load all locations from the server and builds the map for positioning
	 * system.
	 * 
	 * @throws DrachenBaseException
	 * @throws RestrictionException
	 * @throws InternalProcessException
	 * @throws InvalidParameterException
	 * @throws MissingParameterException
	 */
	public void loadLocations() throws MissingParameterException,
			InvalidParameterException, InternalProcessException,
			RestrictionException, DrachenBaseException {

		List<Location> locs = client.allLocationForest();
		if (locs == null)
			throw new InternalProcessException(new NullPointerException());
		locationHierarchy.clear();
		locationIdMap.clear();
		locationKeyMap.clear();
		for (Location loc : locs) {
			loc.updateReferences();

			locationHierarchy.add(loc);
			addLocationToMap(loc);
		}
		if (user.getLocation() != null
				&& locationIdMap.containsKey(user.getLocation().getId())) {
			user.setLocation(locationIdMap.get(user.getLocation().getId()));
		}
	}

	private void addLocationToMap(Location loc) {
		locationIdMap.put(loc.getId(), loc);
		if (loc.getScannerKey() != null)
			locationKeyMap.put(loc.getScannerKey(), loc);
		for (Location l : loc.getChildLocations())
			addLocationToMap(l);
	}

	/**
	 * Register a listener for the LocationChanged event
	 * 
	 * @param lst
	 *            listener
	 */
	public void RegisterListener(LocationChanged lst) {
		listener.add(lst);
	}

	/**
	 * Unregister the listener for the LocationChanged event
	 * 
	 * @param lst
	 *            listener
	 */
	public void UnregisterListener(LocationChanged lst) {
		listener.remove(lst);
	}

	private void CallListener(Location oldLocation, Location newLocation) {
		for (LocationChanged lst : listener)
			lst.Changed(oldLocation, newLocation);
	}

	/**
	 * Return the current location of the user
	 * 
	 * @return location of the user
	 */
	public Location getCurrentLocation() {
		return user.getLocation();
	}

	private void setCurrentLocation(Location currentLocation) {
		this.user.setLocation(currentLocation);
		this.lastCurrentLocationSetTime = new Date();
	}

	/**
	 * returns the Time (in Dateformat) of the last CurrentLocation-setting
	 * 
	 * @return
	 */
	public Date getLastCurrentLocationSetTime() {
		return this.lastCurrentLocationSetTime;
	}

	/**
	 * Search through the location hierarchy to find the smallest (lowest in the
	 * hierarchy-tree), which contains the {@link Point} p
	 * 
	 * @param p
	 *            Point which should be contained by the location
	 * @return the (smallest) location containing the point or null if no
	 *         location contains the point
	 */
	public Location getLoationFromPoint(Point p) {
		System.out.println("getLocationFromPoint called:" + p.getX());
		for (Location loc : locationHierarchy) {
			System.out.println("getLocationFromPoint called:checking location");
			Location l = loc.findSublocation(p);
			if (l != null)
				return l;
		}
		System.out.println("getLocationFromPoint called:no location found");
		return null;
	}

	/**
	 * Search for the location with name locationName
	 * 
	 * @param locationName
	 *            name of the desired location
	 * @return Location with the name or null if no location found
	 */
	public Location getLocationFromName(String locationName) {

		for (Location loc : locationHierarchy) {
			System.out.println("getLocationFromPoint called:checking location");
			Location l = loc.findSublocation(locationName);
			if (l != null)
				return l;
		}
		System.out.println("getLocationFromName called:no location found");
		return null;
	}

	/**
	 * returns the location with the scannerkey key
	 * 
	 * @param key
	 *            key for the location
	 * @return location with the key or null if no such location exists
	 */
	public Location getLocationFromScannerKey(String key) {
		return locationKeyMap.get(key);
	}

	/**
	 * Sets the the new location with the Id locationId.
	 * 
	 * @see LocationService#SetRegion(Location)
	 * @param locationId
	 *            id of the location
	 * @return true if the setting was successful
	 */
	synchronized public boolean SetRegion(int locationId) {
		if (getCurrentLocation() == null
				|| locationId != getCurrentLocation().getId()) {
			Location loc = getLocationForId(locationId);
			return SetRegion(loc);
		}
		return false;
	}

	/**
	 * Returns the Location for the id if no id found it returns null
	 * 
	 * @param locationId
	 * @return Location or null
	 */
	public Location getLocationForId(int locationId) {
		Location loc = locationIdMap.get(locationId);
		return loc;
	}

	/**
	 * Sets the the new location loc. the server will update the user's location
	 * to loc (if it is valid).
	 * 
	 * @param loc
	 *            new location
	 * @return true if the setting was successful
	 */
	synchronized public boolean SetRegion(Location loc) {
		Location old = getCurrentLocation();
		if (loc != null && (old == null || loc.getId() != old.getId())) {
			if (notifyServer(loc)) {
				setCurrentLocation(loc);
				CallListener(old, loc);
				return true;
			}
		}
		return false;
	}

	/**
	 * Notifies the server, that the location has changed
	 * 
	 * @param newLocaion
	 * @return true if change was successful
	 */
	private boolean notifyServer(Location newLocation) {
		boolean success = false;
		try {
			success = client.updateLocation(newLocation);
		} catch (DrachenBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return success;
	}

	public void dispose() {
		// no resources, so all done
	}

	public void setDummyLocation() {

	}

	// the Room functions aren't currently in use
	public boolean isInRoom() {
		return inRoom;
	}

	public void enterRoom(Location room) {
		this.inRoom = true;

	}

	public void leaveRoom() {
		this.inRoom = false;
	}

}
