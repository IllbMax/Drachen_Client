package com.visis.drachen.sensor.data;

import com.vsis.drachen.model.world.Location;

/**
 * Sensordata for GPS Quests
 * 
 */
public class LocationSensorData implements ISensorData {
	private Location location;

	public LocationSensorData(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return String.format("Location: %s",
				location != null ? location.getName() : "None");
	}

}
