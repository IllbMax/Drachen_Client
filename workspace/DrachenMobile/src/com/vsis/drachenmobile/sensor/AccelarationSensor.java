package com.vsis.drachenmobile.sensor;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.vsis.drachen.sensor.AbstractSensor;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.data.AccelarationSensorData;

/**
 * {@link ISensor} that reads the acceleration data from the Android
 * {@link Sensor} ({@link Sensor#TYPE_ACCELEROMETER}) and converts them to
 * {@link AccelarationSensorData}
 */
public class AccelarationSensor extends AbstractSensor implements ISensor {

	private Service _context;
	private boolean _running;

	private final SensorManager sensorManager;

	// last values and timestamps received by the sensor
	private float[] values;
	private long millis;
	private long nanos;

	public AccelarationSensor(String name, Service ctx) {
		super(name);
		// _lastLocationReciev = 0; // nano seconds since system boot
		_context = ctx;
		_running = false;

		sensorManager = (SensorManager) ctx
				.getSystemService(Context.SENSOR_SERVICE);

	}

	@Override
	public void start() {
		resume();
	}

	@Override
	public void stop() {
		pause();
	}

	@Override
	public boolean isRunning() {
		return _running;
	}

	@Override
	public void pause() {
		_running = false;
		sensorManager.unregisterListener(accelSensorEventListener);
	}

	@Override
	public void resume() {
		Sensor sensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		if (sensor != null) {
			sensorManager.registerListener(accelSensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
			_running = true;
			if (values != null) {
				useDataOld();
			}

		} else {
			_running = false;
		}

		_running = true;
	}

	private SensorEventListener accelSensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			useData(event);
		}
	};

	@Override
	public boolean isAvailable() {
		return true;
	}

	/**
	 * Convert the acceleration data from {@link SensorEvent} event to
	 * {@link AccelarationSensorData} and calls the listener.
	 * 
	 * @param event
	 *            acceleration event data from {@link Sensor}
	 */
	protected void useData(SensorEvent event) {
		values = event.values;
		millis = System.currentTimeMillis();
		nanos = event.timestamp;
		float x = values[0];
		float y = values[1];
		float z = values[2];

		callListener(new AccelarationSensorData(millis, nanos, x, y, z));
	}

	/**
	 * Calls the listener with the last event data.
	 * 
	 */
	protected void useDataOld() {
		float x = values[0];
		float y = values[1];
		float z = values[2];

		callListener(new AccelarationSensorData(millis, nanos, x, y, z));
	}

	@Override
	public boolean isPaused() {
		return !_running;
	}

	@Override
	public boolean isStopped() {
		return false;
	}

}
