package com.visis.drachen.sensor;

public interface SensorListener {
	/**
	 * Called if the Sensor receives new data
	 * 
	 * @param data
	 *            new data of the sensor
	 */
	void newSensorData(Object... data);
}
