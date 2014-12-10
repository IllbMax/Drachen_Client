package com.vsis.drachenmobile.sensor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;

import com.vsis.drachen.sensor.AbstractSensor;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.data.GPSSensorData;
import com.vsis.drachenmobile.DrachenApplication;

public class GPSSensor extends AbstractSensor implements ISensor {

	// private long _lastLocationReciev;
	private Service _context;
	private boolean _running;

	public GPSSensor(String name, Service ctx) {
		super(name);
		// _lastLocationReciev = 0; // nano seconds since system boot
		_context = ctx;
		_running = false;
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
		LocalBroadcastManager.getInstance(_context).unregisterReceiver(
				locationGPSChangedReceiver);
		// _lastLocationReciev = ;
	}

	@Override
	public void resume() {

		LocalBroadcastManager.getInstance(_context).registerReceiver(
				locationGPSChangedReceiver,
				new IntentFilter(DrachenApplication.EVENT_GPSPOSITION_CHANGED));

		LocationManager locationManager = (LocationManager) _context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			useData(location);
		}

		_running = true;
	}

	private BroadcastReceiver locationGPSChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Location loc = intent
					.getParcelableExtra(DrachenApplication.EXTRA_LOCATION_NEW);
			useData(loc);
		}
	};

	@Override
	public boolean isAvailable() {
		return true;
	}

	protected void useData(Location location) {
		callListener(new GPSSensorData(location.getLatitude(),
				location.getLongitude()));
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
