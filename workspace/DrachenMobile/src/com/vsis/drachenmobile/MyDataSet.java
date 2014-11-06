package com.vsis.drachenmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vsis.drachen.BlubClient;
import com.vsis.drachen.LocationService;
import com.vsis.drachen.QuestService;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.model.User;
import com.vsis.drachenmobile.settings.ConnectionSettingsActivity;

public class MyDataSet {
	/**
	 * Login User
	 */
	private User user;
	/**
	 * Client for server interaction
	 */
	private BlubClient client;
	private LocationService locationService;
	private QuestService questService;
	private SensorService sensorService;

	Context ctx;

	public MyDataSet(Context ctx) {
		// to enable all the cookies
		client = new BlubClient();

		this.ctx = ctx;

	}

	private void initClient() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String host = pref.getString(ConnectionSettingsActivity.KEY_PREF_HOST_NAME, "");
		int port = Integer.parseInt(pref.getString(
				ConnectionSettingsActivity.KEY_PREF_HOST_PORT, "0"));
		// String host = ctx.getResources().getString(R.string.server_host);
		// int port = ctx.getResources().getInteger(R.integer.server_port);

		try {
			client.initConnection(host, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean login(String username, String password) {
		initClient();

		// TODO: fix wrong username/password
		User user = client.Login(username, password);
		locationService = new LocationService(client);
		questService = new QuestService(client);
		sensorService = new SensorService(client);

		setUser(user);

		locationService.loadLocations();

		return true;
	}

	public User getUser() {
		return user;
	}

	private void setUser(User user) {
		this.user = user;
		locationService.setUser(user);
		questService.setUser(user);
	}

	public BlubClient getClient() {
		return client;
	}

	// public void setClient(BlubClient client) {
	// this.client = client;
	// }

	public LocationService getLocationService() {
		return locationService;
	}

	public QuestService getQuestService() {
		return questService;
	}

	public SensorService getSensorService() {
		return sensorService;
	}

}
