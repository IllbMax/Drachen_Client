package com.visis.drachen.sensor;

import com.visis.drachen.sensor.data.ISensorData;

public interface SensorListener {
	/**
	 * Called if the Sensor receives new data
	 * 
	 * @param data
	 *            new data of the sensor
	 */
	void newSensorData(ISensorData data);
}
