package com.vsis.drachen.sensor.data;

import com.vsis.drachen.model.world.Location;

/**
 * Sensordata from DrachenLocation Sensor
 * 
 */
public class LocationSensorData implements ISensorData {
	private Location location;
	private long millis, nanos;

	public LocationSensorData(long millis, long nanos, Location location) {
		this.location = location;

		this.millis = millis;
		this.nanos = nanos;
	}

	/**
	 * The (drachen!!!) {@link Location}
	 * 
	 * @return
	 */
	public Location getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return String.format("Location: %s",
				location != null ? location.getName() : "None");
	}

	@Override
	public long getUnixMillis() {
		return millis;
	}

	@Override
	public long getNanoTime() {
		return nanos;
	}
}
