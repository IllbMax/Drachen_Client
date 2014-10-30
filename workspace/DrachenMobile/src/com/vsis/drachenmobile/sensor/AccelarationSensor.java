package com.vsis.drachenmobile.sensor;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.visis.drachen.sensor.AbstractSensor;
import com.visis.drachen.sensor.ISensor;

public class AccelarationSensor extends AbstractSensor implements ISensor {

	// private long _lastLocationReciev;
	private Service _context;
	private boolean _running;
	private final SensorManager sensorService;
	private float[] values;

	public AccelarationSensor(String name, Service ctx) {
		super(name);
		// _lastLocationReciev = 0; // nano seconds since system boot
		_context = ctx;
		_running = false;

		sensorService = (SensorManager) ctx
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
		sensorService.unregisterListener(accelSensorEventListener);
	}

	@Override
	public void resume() {
		Sensor sensor = sensorService
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		if (sensor != null) {
			sensorService.registerListener(accelSensorEventListener, sensor,
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

	protected void useData(SensorEvent event) {
		values = event.values;
		float x = values[0];
		float y = values[1];
		float z = values[2];

		callListener(x, y, z);
	}

	protected void useDataOld() {
		float x = values[0];
		float y = values[1];
		float z = values[2];

		callListener(x, y, z);
	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}

}
