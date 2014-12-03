package com.vsis.drachenmobile.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.visis.drachen.sensor.SensorType;
import com.vsis.drachen.LocationService;
import com.vsis.drachen.LocationService.LocationChanged;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.SensorService.OnQuestTargetChangedListener;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachen.model.world.Location;
import com.vsis.drachen.model.world.Point;
import com.vsis.drachenmobile.DrachenApplication;
import com.vsis.drachenmobile.sensor.AccelarationSensor;
import com.vsis.drachenmobile.sensor.GPSSensor;
import com.vsis.drachenmobile.sensor.LocationSensor;

public class LocationLocalService extends Service {

	public class MyBinder extends Binder {
		LocationLocalService getService() {
			return LocationLocalService.this;
		}
	}

	private final IBinder _binder = new MyBinder();
	private LocationService locationService;
	private LocationListener locationListener;
	private LocationChanged drachenLocationListener;
	private OnQuestTargetChangedListener questTargetListener;

	@Override
	public void onCreate() {
		super.onCreate();
		// initialize();
	}

	public void initialize() {
		DrachenApplication app = (DrachenApplication) getApplication();
		locationService = app.getAppData().getLocationService();

		installDrachenLocationListener();
		installAndroidLocationListener();
		startLoctionListener();

		SensorService sensorService = app.getAppData().getSensorService();
		questTargetListener = new OnQuestTargetChangedListener() {

			@Override
			public void onQuestTargetChanged(QuestTarget qt) {

			}
		};
		sensorService.registerQuestTargetChangedListener(questTargetListener);

		GPSSensor gpsSensor = new GPSSensor("GPS Sensor", this);
		LocationSensor locationSensor = new LocationSensor("GPS Sensor", this);
		AccelarationSensor accelSensor = new AccelarationSensor(
				"Accelaration Sensor", this);

		sensorService.registerSensor(SensorType.Position, gpsSensor);
		sensorService.registerSensor(SensorType.Location, locationSensor);
		sensorService.registerSensor(SensorType.Accelaration, accelSensor);

		sensorService.setDefaultSensor(SensorType.Position, gpsSensor);
		sensorService.setDefaultSensor(SensorType.Location, locationSensor);
		sensorService.setDefaultSensor(SensorType.Accelaration, accelSensor);

		gpsSensor.start();
		locationSensor.start();
		accelSensor.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initialize();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return _binder;
	}

	public void startLoctionListener() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		boolean prov = false;

		String provider = prov ? LocationManager.NETWORK_PROVIDER
				: LocationManager.GPS_PROVIDER;

		locationManager
				.requestLocationUpdates(provider, 0, 0, locationListener);
	}

	private void installDrachenLocationListener() {
		drachenLocationListener = new LocationChanged() {

			@Override
			public void Changed(Location oldname, Location newname) {
				broadcastLocationChange(oldname, newname);
			}
		};
		locationService.RegisterListener(drachenLocationListener);
	}

	public void installAndroidLocationListener() {

		locationListener = new LocationListener() {
			public void onLocationChanged(android.location.Location location) {
				// Called when a new location is found by the network location
				makeUseOfNewLocation(location);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Toast.makeText(
						LocationLocalService.this,
						"S:" + provider + status + "  " + extras == null ? ""
								: extras.toString(), Toast.LENGTH_LONG).show();
			}

			public void onProviderEnabled(String provider) {
				Toast.makeText(LocationLocalService.this, "E:" + provider,
						Toast.LENGTH_LONG).show();
			}

			public void onProviderDisabled(String provider) {
				Toast.makeText(LocationLocalService.this, "D:" + provider,
						Toast.LENGTH_LONG).show();
			}
		};
	}

	protected void makeUseOfNewLocation(android.location.Location location) {

		String data = String.format("Lat: %f, Lon: %f, Acc: %f \nAlt: %f",
				location.getLatitude(), location.getLongitude(),
				location.getAccuracy(), location.getAltitude());
		Toast.makeText(this, data, Toast.LENGTH_SHORT).show();

		broadcastGPSChange(location);

		Point p = new Point(location.getLatitude(), location.getLongitude());
		com.vsis.drachen.model.world.Location loc = locationService
				.getLoationFromPoint(p);

		if (loc == null) {
			Toast.makeText(this, "no location", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "location:" + loc.getName(),
					Toast.LENGTH_SHORT).show();
			AsyncTask<Location, Void, Boolean> task = new AsyncTask<Location, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Location... params) {
					Location loc = params[0];
					boolean success = locationService.SetRegion(loc);
					return success;
				}

			};
			task.execute(loc);

		}
	}

	/**
	 * Starts a local broadcast to notify the QuestTargetChangeEvent
	 * 
	 * @param qt
	 *            QuestTarget that has changed
	 */
	private void broadcastLocationChange(QuestTarget qt) {
		Intent intent = new Intent(DrachenApplication.EVENT_QUESTTARGET_CHANGED);

		intent.putExtra(DrachenApplication.EXTRA_QUEST_ID, qt.getQuest()
				.getId());
		intent.putExtra(DrachenApplication.EXTRA_QUESTTARGET_ID, qt.getId());

		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	/**
	 * Starts a local broadcast to notify the LocationChangeEvent
	 * 
	 * @param old
	 *            the old Location (or null)
	 * @param now
	 *            the new Location (or null)
	 */
	private void broadcastLocationChange(Location old, Location now) {
		Intent intent = new Intent(DrachenApplication.EVENT_LOCATION_CHANGED);

		intent.putExtra(DrachenApplication.EXTRA_LOCATION_OLD,
				old != null ? old.getId() : -1);
		intent.putExtra(DrachenApplication.EXTRA_LOCATION_NEW,
				now != null ? now.getId() : -1);

		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	/**
	 * Starts a local broadcast to notify the GPS change
	 * 
	 * @param location
	 *            new GPS location
	 */
	private void broadcastGPSChange(android.location.Location location) {
		Intent intent = new Intent(DrachenApplication.EVENT_GPSPOSITION_CHANGED);

		intent.putExtra(DrachenApplication.EXTRA_LOCATION_NEW, location);

		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

}
