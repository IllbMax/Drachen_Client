package com.vsis.drachen.sensor.data;

/**
 * Defines the minimal interface for SensorData. For each
 * {@link com.vsis.drachen.sensor.SensorType} you have define a class containing
 * the specific data.
 * 
 */

public interface ISensorData {

	/**
	 * Timestamp of that time receiving the data from sensor. In implementation
	 * it will mostly use System.currentTimeMillis(). You can use this timestamp
	 * to create a {@link System.util.Date} of that time.
	 * 
	 * @return Milliseconds since 1.1. 1970 (in UTC)
	 * 
	 * @see System.currentTimeMillis()
	 */
	long getUnixMillis();

	/**
	 * Nanoseconds starting from a arbitrary but fixed point in time.
	 * 
	 * Use this data to calculate time differences of two ISensorData.
	 * 
	 * @return Nanoseconds since arbitrary but fixed point in time
	 * 
	 * @see System.nanoTime();
	 */
	long getNanoTime();
}