package com.vsis.drachen.sensor.data;

/**
 * Sensordata for GPS Quests
 * 
 */
public class GPSSensorData implements ISensorData {
	private double latitude;
	private double longitude;

	public GPSSensorData(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return String.format("GPS: (%s; %s)", latitude, longitude);
	}

}
