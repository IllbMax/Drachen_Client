package com.vsis.drachen.sensor;

import com.vsis.drachen.sensor.data.ISensorData;

public interface SensorListener {
	/**
	 * Called if the Sensor receives new data
	 * 
	 * @param data
	 *            new data of the sensor
	 */
	void newSensorData(ISensorData data);
}
