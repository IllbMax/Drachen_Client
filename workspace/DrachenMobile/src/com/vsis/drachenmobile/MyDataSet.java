package com.vsis.drachenmobile;

import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.visis.drachen.exception.CredentialException;
import com.visis.drachen.exception.DrachenBaseException;
import com.visis.drachen.exception.InternalProcessException;
import com.visis.drachen.exception.InvalidParameterException;
import com.visis.drachen.exception.MissingParameterException;
import com.visis.drachen.exception.RestrictionException;
import com.vsis.drachen.BlubClient;
import com.vsis.drachen.LocationService;
import com.vsis.drachen.QuestService;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.quest.Quest;
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

		String baseStr = pref.getString(
				ConnectionSettingsActivity.KEY_PREF_BASE_URL, ctx
						.getResources().getString(R.string.server_baseURL));
		baseStr = baseStr.trim();

		try {
			URL baseUrl = new URL(baseStr);

			client.initConnection(baseUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean registerUser(String username, String password,
			String displayName) throws InternalProcessException,
			MissingParameterException, InvalidParameterException,
			DrachenBaseException {
		initClient();

		boolean success = client.registerUser(username, password, displayName);

		return success;
	}

	public boolean login(String username, String password)
			throws InternalProcessException, MissingParameterException,
			DrachenBaseException {
		initClient();

		User user = null;
		try {
			user = client.Login(username, password);
		} catch (CredentialException e) {
			// if wrong username then result = false
			e.printStackTrace();
		}
		if (user != null) {
			locationService = new LocationService(client);
			questService = new QuestService(client);
			sensorService = new SensorService(client);

			setUser(user);

			locationService.loadLocations();

			for (Quest q : questService.getUserQuests())
				sensorService.trackQuest(q);
			return true;
		} else {
			// eg wrong credentials
			return false;
		}
	}

	public boolean logout() throws InternalProcessException,
			RestrictionException, DrachenBaseException {
		boolean success = client.Logout();

		if (success) {
			disposeComponents();
		}
		return success;
	}

	public void disposeComponents() {
		locationService.dispose();
		sensorService.dispose();
		questService.dispose();

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
