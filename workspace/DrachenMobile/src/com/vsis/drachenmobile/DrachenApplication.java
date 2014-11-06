package com.vsis.drachenmobile;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vsis.drachenmobile.service.LocationLocalService;

public class DrachenApplication extends Application {

	public final static String EVENT_LOCATION_CHANGED = "com.vsis.drachen.locationChanged";
	public final static String EVENT_GPSPOSITION_CHANGED = "com.vsis.drachen.gpsPositionChanged";

	private static MyDataSet appdata;

	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.pref_connection, true);

		appdata = new MyDataSet(this);
	}

	public MyDataSet getAppData() {
		return appdata;
	}

	/**
	 * starts all Services needed after login
	 */
	public void startDrachenServices() {

		Intent service = new Intent(this, LocationLocalService.class);
		startService(service);
		// bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
		// Intent intent = new Intent(this, StartServicesReciver.class);
		// sendBroadcast(intent);
	}

	ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d("Drachen.BindService conncected", name.flattenToShortString());
		}
	};

}
