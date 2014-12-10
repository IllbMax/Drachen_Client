package com.vsis.drachen.sensor;

public interface ISensor {

	/**
	 * Identifier/Name for the Sensor
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Starts the Sensor
	 */
	void start();

	/**
	 * Stops the Sensor
	 */
	void stop();

	void pause();

	void resume();

	/**
	 * Returns true if the Sensor is available (is running or able to
	 * start/resume)
	 * 
	 * @return
	 */
	boolean isAvailable();

	/**
	 * Returns true if the Sensor is running
	 * 
	 * @return
	 */
	boolean isRunning();

	/**
	 * Returns true if the Sensor is paused (or stopped)
	 * 
	 * @return
	 */
	boolean isPaused();

	/**
	 * Returns true if the Sensor is stopped
	 * 
	 * @return
	 */
	boolean isStopped();

	/**
	 * Adds a SensorListener to
	 * 
	 * @param sensorListener
	 *            new SensorListerner
	 */
	void addListerner(SensorListener sensorListener);

	/**
	 * Removes a SensorListern from the list
	 * 
	 * @param sensorListener
	 *            SensorListener which should removed
	 */
	void removeListerner(SensorListener sensorListener);

}
