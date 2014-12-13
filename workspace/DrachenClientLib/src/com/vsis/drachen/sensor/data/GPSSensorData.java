package com.vsis.drachen.sensor.data;

/**
 * Sensordata from position sensor (GPS data)
 * 
 */
public class GPSSensorData implements ISensorData {
	private double latitude;
	private double longitude;

	private long millis, nanos;

	public GPSSensorData(long millis, long nanos, double latitude,
			double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;

		this.millis = millis;
		this.nanos = nanos;
	}

	/**
	 * The latitude component of GPS coordinates (in degrees)
	 * 
	 * \in [-90,90]: 0 Equator, 90 north pole
	 * 
	 * @return The latitude component of GPS coordinates
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * The longitude component of GPS coordinates (in degrees)
	 * 
	 * \in [-180,180]: 0 Greenwich, + to the east, - to the west
	 * 
	 * @return The latitude component of GPS coordinates
	 */
	public double getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return String.format("GPS: (%s; %s)", latitude, longitude);
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
