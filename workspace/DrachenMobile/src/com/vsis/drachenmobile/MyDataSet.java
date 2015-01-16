package com.vsis.drachenmobile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.vsis.drachen.BlubClient;
import com.vsis.drachen.LocationService;
import com.vsis.drachen.QuestService;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.exception.CredentialException;
import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.InvalidParameterException;
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachen.exception.RestrictionException;
import com.vsis.drachen.model.IMiniGame;
import com.vsis.drachen.model.ISensorSensitive;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestProgressStatus;
import com.vsis.drachen.model.quest.QuestPrototype;
import com.vsis.drachen.model.quest.StringCompareQuestTarget;
import com.vsis.drachenmobile.service.AndroidDrachenResourceService;
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

	private IMiniGame currentMinigame;

	private AndroidDrachenResourceService resourceService;
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

	/**
	 * try to login the user using the username and password for authentication
	 * 
	 * @param username
	 *            username for login
	 * @param password
	 *            password for login
	 * @return true if login was successful
	 * 
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws MissingParameterException
	 *             if a parameter (username, password, etc.) was missing
	 * @throws InvalidParameterException
	 *             if a parameter of other requests were invalid (no login
	 *             exception)
	 * @throws RestrictionException
	 *             if the login signals true, but server denies access to other
	 *             requests (no login exception)
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 */
	public boolean login(String username, String password)
			throws InternalProcessException, MissingParameterException,
			InvalidParameterException, RestrictionException,
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
			createDrachenResourceService();
			locationService = new LocationService(client);
			questService = new QuestService(client);
			sensorService = new SensorService(client);

			setUser(user);

			try {
				locationService.loadLocations();
			} catch (DrachenBaseException e) {
				// some thing strange happened:
				// the user successful logged in but has no rights to access the
				// locations
				if (e instanceof RestrictionException)
					System.err
							.println("Error: no access to locations after login!");
				e.printStackTrace();
				try {
					if (!logout())
						disposeComponents();
				} catch (Exception ex) {
					disposeComponents();
				}
				throw e;
			}

			for (Quest q : questService.getUserQuests())
				sensorService.trackQuest(q);
			return true;
		} else {
			// eg wrong credentials
			return false;
		}
	}

	public boolean dummyLogin() {
		user = new User();
		user.setDisplayName("Dummy");
		user.setId(0);

		createDrachenResourceService();
		locationService = new LocationService(client);
		questService = new QuestService(client);
		sensorService = new SensorService(client);

		QuestPrototype proto = new QuestPrototype("talk", "palaber");
		proto.setHint1("try this and that");
		proto.setHint2("lorem ipsum dolor sit ...");
		proto.setHint3("ok go to this website");
		Quest quest = new Quest();
		quest.setPrototype(proto);

		List<String> choice = new ArrayList<String>();
		choice.add("Speak after me");
		choice.add("bananarama");
		StringCompareQuestTarget qt = new StringCompareQuestTarget(
				"compare me", choice, 0);
		qt.setProgress(QuestProgressStatus.OnGoing);
		quest.addQuestTarget(qt);

		user.startQuest(quest);
		setUser(user);
		for (Quest q : questService.getUserQuests())
			sensorService.trackQuest(q);
		return true;
	}

	public boolean logout() throws InternalProcessException,
			RestrictionException, DrachenBaseException {
		boolean success = client.Logout();

		if (success) {
			disposeComponents();
		}
		return success;
	}

	private void createDrachenResourceService() {
		String filename = loadSD();
		resourceService = new AndroidDrachenResourceService(ctx);
		try {
			resourceService.loadZip(filename);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private String loadSD() {
		String state = Environment.getExternalStorageState();

		if (!(state.equals(Environment.MEDIA_MOUNTED))) {
			// Toast.makeText(ctx, "There is no any sd card",
			// Toast.LENGTH_LONG).show();
			return "";

		} else {
			// Toast.makeText(ctx, "Sd card available",
			// Toast.LENGTH_LONG).show();
			File file = Environment.getExternalStorageDirectory();
			File zipFile = new File(file, "data.zip");
			return zipFile.getAbsolutePath();
		}
	}

	public void disposeComponents() {
		locationService.dispose();
		sensorService.dispose();
		questService.dispose();
		resourceService.dispose();
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

	public AndroidDrachenResourceService getResourceService() {
		return resourceService;
	}

	public IMiniGame getCurrentMinigame() {
		return currentMinigame;
	}

	public void setCurrentMinigame(IMiniGame currentMinigame) {
		if (this.currentMinigame != null)
			unregisterMinigame();
		if (currentMinigame != null)
			registerMinigame(currentMinigame);
		this.currentMinigame = currentMinigame;
	}

	private void registerMinigame(IMiniGame minigame) {
		if (minigame != null)
			for (ISensorSensitive ss : minigame.getSensorReceiver())
				sensorService.trackSensorReceiver(ss);
	}

	private void unregisterMinigame() {
		if (currentMinigame != null)
			for (ISensorSensitive ss : currentMinigame.getSensorReceiver())
				sensorService.untrackSensorReceiver(ss);
	}

}
