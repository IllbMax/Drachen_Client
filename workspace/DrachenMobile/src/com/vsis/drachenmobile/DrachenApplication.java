package com.vsis.drachenmobile;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vsis.drachenmobile.service.LocationLocalService;

public class DrachenApplication extends Application {

	// constants : broadcast events
	public final static String EVENT_LOCATION_CHANGED = "com.vsis.drachen.locationChanged";
	public final static String EVENT_GPSPOSITION_CHANGED = "com.vsis.drachen.gpsPositionChanged";
	public static final String EVENT_QUESTTARGET_CHANGED = "com.vsis.drachen.questTargetChanged";
	// constants : (bundle) extra names
	public static final String EXTRA_LOCATION_NEW = "location.new";
	public static final String EXTRA_LOCATION_OLD = "location.old";
	public static final String EXTRA_QUEST_ID = "quest.id";
	public static final String EXTRA_QUESTTARGET_ID = "questTarget.id";

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
		bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
		// Intent intent = new Intent(this, StartServicesReciver.class);
		// sendBroadcast(intent);
	}

	public boolean logout() {
		if (appdata.logout()) {

			Intent service = new Intent(this, LocationLocalService.class);
			stopService(service);
			return true;
		}
		return false;
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
