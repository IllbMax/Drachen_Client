package com.vsis.drachen.model;

import java.util.Set;

import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.ISensorData;

/**
 * Indicates that this can receive data from sensors.
 * 
 */
public interface ISensorSensitive {

	/**
	 * Determines if new sensor data are required
	 * 
	 * @param type
	 *            type of possible new data
	 * 
	 * @return true if this ISensorSensitive should be notified if new sensor
	 *         data is available
	 */
	public abstract boolean needsNewSensordata(SensorType type);

	/**
	 * Set of required Sensors for this ISensorSensitive
	 * 
	 * @return set of {@link SensorType}s that are necessary
	 */
	public abstract Set<SensorType> requiredSensors();

	/**
	 * let this object know the new SensorData.
	 * 
	 * if you implement this method you should declare it synchronized
	 * 
	 * @param type
	 *            Type of the sending sensor
	 * @param data
	 *            A ISensorData object containing the data matching the
	 *            SensorType type
	 * @return true if there is an update in the state
	 */
	public abstract boolean receiveSensordata(SensorType type, ISensorData data);

}