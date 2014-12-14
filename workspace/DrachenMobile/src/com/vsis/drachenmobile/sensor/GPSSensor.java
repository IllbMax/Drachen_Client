package com.vsis.drachenmobile.sensor;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.vsis.drachen.sensor.AbstractSensor;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.data.GPSSensorData;

public class GPSSensor extends AbstractSensor implements ISensor {

	// private long _lastLocationReciev;
	private Service _context;
	private boolean _running;
	/**
	 * {@link LocationListener} that receives the GPS position.
	 */
	private LocationListener locationListener;
	/**
	 * true if the locationListener is
	 */
	private boolean _isRegistered;

	public GPSSensor(String name, Service ctx) {
		super(name);
		// _lastLocationReciev = 0; // nano seconds since system boot
		_context = ctx;
		_running = false;
		_isRegistered = false;

		initLocationListener();
	}

	@Override
	public boolean isRunning() {
		return _isRegistered && _running;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public boolean isPaused() {
		return _isRegistered && !_running;
	}

	@Override
	public boolean isStopped() {
		return !_isRegistered;
	}

	@Override
	public void start() {
		startLoctionListener();
	}

	@Override
	public void stop() {
		unregisterLocationListener();
	}

	@Override
	public void pause() {
		_running = false;

	}

	@Override
	public void resume() {

		LocationManager locationManager = (LocationManager) _context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// // if the listener is already register this does nothing
		startLoctionListener();
		if (location != null) {
			useData(location);
		}

		_running = true;
	}

	protected void useData(Location location) {
		callListener(new GPSSensorData(location.getTime(), getNanos(location),
				location.getLatitude(), location.getLongitude()));
	}

	private long getNanos(Location location) {
		if (Build.VERSION.SDK_INT >= 17)
			return getNanos_o17(location);
		else
			return getNanos_u17(location);

	}

	@TargetApi(17)
	private long getNanos_o17(Location location) {
		return location.getElapsedRealtimeNanos();
	}

	private long getNanos_u17(Location location) {
		return System.nanoTime();
	}

	private synchronized void unregisterLocationListener() {
		if (_isRegistered) {
			LocationManager locationManager = (LocationManager) _context
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(locationListener);
			_isRegistered = false;
		}
	}

	private synchronized void startLoctionListener() {
		if (!_isRegistered) {
			LocationManager locationManager = (LocationManager) _context
					.getSystemService(Context.LOCATION_SERVICE);

			boolean prov = false;

			String provider = prov ? LocationManager.NETWORK_PROVIDER
					: LocationManager.GPS_PROVIDER;

			locationManager.requestLocationUpdates(provider, 0, 0,
					locationListener);
			_isRegistered = true;
			_running = true;
		}
	}

	private void initLocationListener() {
		locationListener = new LocationListener() {
			public void onLocationChanged(android.location.Location location) {
				// Called when a new location is found by the network location
				useData(location);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Toast.makeText(
						_context,
						"S:" + provider + status + "  " + extras == null ? ""
								: extras.toString(), Toast.LENGTH_LONG).show();
			}

			public void onProviderEnabled(String provider) {
				Toast.makeText(_context, "E:" + provider, Toast.LENGTH_LONG)
						.show();
			}

			public void onProviderDisabled(String provider) {
				Toast.makeText(_context, "D:" + provider, Toast.LENGTH_LONG)
						.show();
			}
		};
	}

}
