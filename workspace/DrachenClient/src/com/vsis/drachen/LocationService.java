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

public class LocationService {

	public static interface LocationChanged {
		void Changed(Location oldLocation, Location newLocation);
	}

	BlubClient client;

	// SparseArray<Location> locationIdMap;
	Map<Integer, Location> locationIdMap;
	List<Location> locationHierachy;

	User user;

	List<LocationChanged> listener;

	private Date lastCurrentLocationSetTime = new Date();

	public LocationService(BlubClient client) {
		listener = new ArrayList<LocationService.LocationChanged>();
		locationHierachy = new ArrayList<Location>();
		// locationIdMap = new SparseArray<Location>();
		locationIdMap = new HashMap<Integer, Location>();

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
		locationHierachy.clear();
		locationIdMap.clear();
		for (Location loc : locs) {
			loc.updateReferences();

			locationHierachy.add(loc);
			addLocationToMap(loc);
		}
		if (user.getLocation() != null
				&& locationIdMap.containsKey(user.getLocation().getId())) {
			user.setLocation(locationIdMap.get(user.getLocation().getId()));
		}
	}

	private void addLocationToMap(Location loc) {
		locationIdMap.put(loc.getId(), loc);
		for (Location l : loc.getChildLocations())
			addLocationToMap(l);
	}

	public void RegisterListener(LocationChanged lst) {
		listener.add(lst);
	}

	public void UnregisterListener(LocationChanged lst) {
		listener.remove(lst);
	}

	private void CallListener(Location oldLocation, Location newLocation) {
		for (LocationChanged lst : listener)
			lst.Changed(oldLocation, newLocation);
	}

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

	public Location getLoationFromPoint(Point p) {
		for (Location loc : locationHierachy) {
			Location l = loc.findSublocation(p);
			if (l != null)
				return l;
		}
		return null;
	}

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
	 * 
	 * @param loc
	 * @return
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
	private boolean notifyServer(Location newLocaion) {
		// TODO: call client.changeLocation(bla)
		return true;
	}

	public void dispose() {
		// no resources, so all done

	}

	public void setDummyLocation() {

	}
}
