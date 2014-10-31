package com.visis.drachen.sensor;

import java.util.LinkedList;
import java.util.List;

import com.visis.drachen.sensor.data.ISensorData;

public abstract class AbstractSensor implements ISensor {

	protected List<SensorListener> _listerners;
	private String _name;

	public String getName() {
		return _name;
	}

	protected AbstractSensor(String name) {
		_name = name;
		_listerners = new LinkedList<>();
	}

	@Override
	public void addListerner(SensorListener sensorListener) {
		_listerners.add(sensorListener);
	}

	@Override
	public void removeListerner(SensorListener sensorListener) {
		_listerners.remove(sensorListener);
	}

	/**
	 * pushes the Sensor data to the listener
	 * 
	 * @param data
	 *            Objects with the data
	 */
	protected void callListener(final ISensorData data) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				for (SensorListener sl : _listerners) {
					sl.newSensorData(data);
				}
			}
		});
		thread.start();
	}
}
