package com.vsis.drachenmobile.sensor;

import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.vsis.drachen.LocationService;
import com.vsis.drachen.model.world.Location;
import com.vsis.drachen.sensor.AbstractSensor;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.data.LocationSensorData;
import com.vsis.drachenmobile.DrachenApplication;
import com.vsis.drachenmobile.service.LocationLocalService;

/**
 * {@link ISensor} that receives {@link Location} change events from a
 * {@link BroadcastReceiver} and converts them to {@link LocationSensorData}.
 * 
 * Compared to the {@link GPSSensor} this sensor will fire events less often
 * caused it isn't based on a coordinate System but on the specific internal
 * application {@link Location}.
 */
public class LocationSensor extends AbstractSensor implements ISensor {

	private Date _lastLocationReciev;
	private Service _context;
	private boolean _running;

	public LocationSensor(String name, Service ctx) {
		super(name);
		_lastLocationReciev = new Date();
		_lastLocationReciev.setTime(0); // set Time to point in past
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
				locationChangedReceiver);
		_lastLocationReciev = ((DrachenApplication) _context.getApplication())
				.getAppData().getLocationService()
				.getLastCurrentLocationSetTime();
	}

	@Override
	public void resume() {

		LocalBroadcastManager.getInstance(_context).registerReceiver(
				locationChangedReceiver,
				new IntentFilter(DrachenApplication.EVENT_LOCATION_CHANGED));

		LocationService locationService = ((DrachenApplication) _context
				.getApplication()).getAppData().getLocationService();

		if (_lastLocationReciev.before(locationService
				.getLastCurrentLocationSetTime())) {

			useData(locationService.getCurrentLocation());
		}
		_running = true;
	}

	private BroadcastReceiver locationChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int locId = intent.getIntExtra(
					DrachenApplication.EXTRA_LOCATION_NEW, -1);
			Location newLocation = ((DrachenApplication) _context
					.getApplication()).getAppData().getLocationService()
					.getLocationForId(locId);
			useData(newLocation);
		}
	};

	@Override
	public boolean isAvailable() {
		return true;
	}

	/**
	 * Convert the {@link Location} data to {@link LocationSensorData} and calls
	 * the listener.
	 * 
	 * @param location
	 *            location from broadcast (by {@link LocationLocalService})
	 */
	protected void useData(Location location) {
		long millis = System.currentTimeMillis();
		long nanos = System.nanoTime();
		callListener(new LocationSensorData(millis, nanos, location));
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
