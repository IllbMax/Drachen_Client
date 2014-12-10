package com.vsis.drachen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.visis.drachen.exception.CredentialException;
import com.visis.drachen.exception.DrachenBaseException;
import com.visis.drachen.exception.IdNotFoundException;
import com.visis.drachen.exception.InternalProcessException;
import com.visis.drachen.exception.InvalidParameterException;
import com.visis.drachen.exception.MissingParameterException;
import com.visis.drachen.exception.ObjectRestrictionException;
import com.visis.drachen.exception.QuestAbortException;
import com.visis.drachen.exception.QuestStartException;
import com.visis.drachen.exception.QuestTargetNotFinishedException;
import com.visis.drachen.exception.RestrictionException;
import com.visis.drachen.exception.client.ConnectionException;
import com.visis.drachen.exception.client.InvalidResultException;
import com.vsis.drachen.adapter.AdapterProvider;
import com.vsis.drachen.model.ResultWrapper;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.quest.IQuestTargetUpdateState;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestPrototype;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachen.model.world.Location;
import com.vsis.net.CookieManager;

public class BlubClient {
	private CookieManager cookieManager;
	private URL _base;

	/**
	 * Try to register new user
	 * 
	 * @param username
	 *            name for login
	 * @param password
	 *            password for login
	 * @param displayName
	 *            ingame name (that is shown to other user)
	 * @return true if register was successful
	 * 
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws MissingParameterException
	 *             if a parameter is missing (should not happen)
	 * @throws InvalidParameterException
	 *             if username or displayName had a wrong format (eg. too short)
	 */
	public boolean registerUser(String username, String password,
			String displayName) throws DrachenBaseException,
			InternalProcessException, MissingParameterException,
			InvalidParameterException {

		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("username", username);
			param.put("password", password);
			param.put("displayName", displayName);

			ResultWrapper<User> output = loadFormGson("createUser", param,
					new TypeToken<ResultWrapper<User>>() {
					}.getType());
			if (output == null)
				throw new InternalProcessException("Empty Result");
			else if (output.success)
				return true;
			else if (output.expection == null)
				return false;
			else // there is an exception provided by the server
			{
				DrachenBaseException e = output.expection;
				e.fillInStackTrace();
				throw e;
			}

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Try to login the user
	 * 
	 * @param username
	 *            login name
	 * @param password
	 *            login password
	 * @return the User object if login was successful (or null)
	 * 
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws MissingParameterException
	 *             if a parameter is missing (should not happen)
	 * @throws CredentialException
	 *             if wrong username/password were used
	 */
	public User Login(String username, String password)
			throws DrachenBaseException, InternalProcessException,
			MissingParameterException, CredentialException {

		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("username", username);
			param.put("password", password);

			ResultWrapper<User> output = loadFormGson("login", param,
					new TypeToken<ResultWrapper<User>>() {
					}.getType());
			if (output == null)
				throw new InternalProcessException("Empty Result");
			else if (output.success)
				return output.resultObject;
			else if (output.expection == null)
				throw new CredentialException("wrong password");
			else // there is an exception provided by the server
			{
				DrachenBaseException e = output.expection;
				e.fillInStackTrace();
				throw e;
			}

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Try to logout the current logged in user
	 * 
	 * @return true if logout was successful
	 * 
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws RestrictionException
	 *             if the user wasn't logged in
	 */
	public boolean Logout() throws DrachenBaseException,
			InternalProcessException, RestrictionException {
		try {

			Map<String, Object> param = new LinkedHashMap<>();

			ResultWrapper<Boolean> output = loadFormGson("logout", param,
					new TypeToken<ResultWrapper<Boolean>>() {
					}.getType());
			if (output == null)
				throw new InternalProcessException("Empty Result");
			else if (output.success)
				return output.resultObject;
			else if (output.expection == null)
				return false;
			else // there is an exception provided by the server
			{
				DrachenBaseException e = output.expection;
				e.fillInStackTrace();
				throw e;
			}

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Load the available Quests for the location with the locationId
	 * 
	 * @param locationId
	 *            Id of the location with the desired QuestPrototypes
	 * @return available Quests for the location
	 * 
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws RestrictionException
	 *             if the user wasn't logged in
	 * @throws IdNotFoundException
	 *             if there is no location with locationId
	 */
	public List<QuestPrototype> QuestsForLocation(int locationId)
			throws DrachenBaseException, InternalProcessException,
			RestrictionException, IdNotFoundException {

		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("locationId", locationId);

			ResultWrapper<List<QuestPrototype>> output = loadFormGson(
					"showQuestsForLocation", param,
					new TypeToken<ResultWrapper<List<QuestPrototype>>>() {
					}.getType());
			if (output == null)
				throw new InternalProcessException("Empty Result");
			else if (output.success)
				return output.resultObject;
			else if (output.expection == null)
				return null;
			else // there is an exception provided by the server
			{
				DrachenBaseException e = output.expection;
				e.fillInStackTrace();
				throw e;
			}

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Load location object for specific id
	 * 
	 * @param locationId
	 *            id of the requesterd location
	 * @return location object with id
	 * 
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws RestrictionException
	 *             if the user wasn't logged in
	 * @throws IdNotFoundException
	 *             if there is no location with locationId
	 * @throws MissingParameterException
	 *             if the location parameter is missing (should not happen)
	 */
	public Location locationForId(int locationId) throws DrachenBaseException,
			InternalProcessException, RestrictionException,
			MissingParameterException, IdNotFoundException {

		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("locationId", locationId);

			ResultWrapper<Location> output = loadFormGson("locationtree",
					param, new TypeToken<ResultWrapper<Location>>() {
					}.getType());
			if (output == null)
				throw new InternalProcessException("Empty Result");
			else if (output.success)
				return output.resultObject;
			else if (output.expection == null)
				return null;
			else // there is an exception provided by the server
			{
				DrachenBaseException e = output.expection;
				e.fillInStackTrace();
				throw e;
			}

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Load the whole location-map (list of trees => forest)
	 * 
	 * @return List of all locations
	 * 
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws RestrictionException
	 *             if the user wasn't logged in
	 * @throws MissingParameterException
	 *             if the location parameter is missing (should not happen)
	 * @throws InvalidParameterException
	 *             if the (internal) locationId parameter has the wrong format
	 */
	public List<Location> allLocationForest() throws DrachenBaseException,
			InternalProcessException, RestrictionException,
			MissingParameterException, InvalidParameterException {

		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("locationId", "all");

			ResultWrapper<List<Location>> output = loadFormGson("locationtree",
					param, new TypeToken<ResultWrapper<List<Location>>>() {
					}.getType());
			if (output == null)
				throw new InternalProcessException("Empty Result");
			else if (output.success)
				return output.resultObject;
			else if (output.expection == null)
				return null;
			else // there is an exception provided by the server
			{
				DrachenBaseException e = output.expection;
				e.fillInStackTrace();
				throw e;
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Start the {@link QuestPrototype} with the id questPrototypeId
	 * 
	 * @param questPrototypeId
	 *            if of the {@link QuestPrototype} which you want to start
	 * @return the started {@link Quest} from the prototype
	 * 
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws RestrictionException
	 *             if the user wasn't logged in
	 * @throws MissingParameterException
	 *             if the questPrototypeId parameter is missing (should not
	 *             happen)
	 * @throws IdNotFoundException
	 *             if there is no {@link QuestPrototype} with the id
	 *             questPrototypeId
	 * @throws QuestStartException
	 *             if the user cannot start the Quest
	 */
	public Quest StartQuest(int questPrototypeId) throws DrachenBaseException,
			InternalProcessException, RestrictionException,
			MissingParameterException, IdNotFoundException, QuestStartException {
		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("questPrototypeId", questPrototypeId);

			ResultWrapper<Quest> output = loadFormGson("startQuest", param,
					new TypeToken<ResultWrapper<Quest>>() {
					}.getType());
			if (output == null)
				throw new InternalProcessException("Empty Result");
			else if (output.success)
				return output.resultObject;
			else if (output.expection == null)
				return null;
			else // there is an exception provided by the server
			{
				DrachenBaseException e = output.expection;
				e.fillInStackTrace();
				throw e;
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Try to abort the {@link Quest} with the id questId
	 * 
	 * @param questId
	 *            id of the Quest, which should be aborted
	 * @return true if abortion was successful
	 * 
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws RestrictionException
	 *             if the user wasn't logged in
	 * @throws MissingParameterException
	 *             if the questId parameter is missing (should not happen)
	 * @throws IdNotFoundException
	 *             if there is no {@link Quest} with the id questId
	 * @throws QuestAbortException
	 *             if the user isn't on this quest
	 * @throws ObjectRestrictionException
	 *             if the quest belongs to an other user
	 */
	public Boolean AbortQuest(int questId) throws DrachenBaseException,
			InternalProcessException, RestrictionException,
			MissingParameterException, IdNotFoundException,
			ObjectRestrictionException, QuestAbortException {
		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("questId", questId);

			ResultWrapper<Boolean> output = loadFormGson("abortQuest", param,
					new TypeToken<ResultWrapper<Boolean>>() {
					}.getType());
			if (output == null)
				throw new InternalProcessException("Empty Result");
			else if (output.success)
				return output.resultObject;
			else if (output.expection == null)
				return null;
			else // there is an exception provided by the server
			{
				DrachenBaseException e = output.expection;
				e.fillInStackTrace();
				throw e;
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * Finish the {@link Quest} with the id questId if all {@link QuestTarget}
	 * are succeeded.
	 * 
	 * @param questId
	 *            id of the quest you want to finish
	 * @return true if it was successful finished
	 * 
	 * @throws DrachenBaseException
	 *             if other exceptions occurred
	 * @throws InternalProcessException
	 *             if something went wrong at the server
	 * @throws RestrictionException
	 *             if the user wasn't logged in
	 * @throws MissingParameterException
	 *             if the questId parameter is missing (should not happen)
	 * @throws IdNotFoundException
	 *             if there is no {@link Quest} with the id questId
	 * @throws QuestTargetNotFinishedException
	 *             if not all {@link QuestTarget}s are succeeded (contains the
	 *             first target that isn't succeeded)
	 * @throws ObjectRestrictionException
	 *             if the quest belongs to an other user
	 * 
	 */
	public Boolean FinishQuest(int questId) throws DrachenBaseException,
			InternalProcessException, RestrictionException,
			MissingParameterException, IdNotFoundException,
			ObjectRestrictionException, QuestTargetNotFinishedException {
		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("questId", questId);

			ResultWrapper<Boolean> output = loadFormGson("finishQuest", param,
					Boolean.class);
			if (output == null)
				throw new InternalProcessException("Empty Result");
			else if (output.success)
				return output.resultObject;
			else if (output.expection == null)
				return null;
			else // there is an exception provided by the server
			{
				DrachenBaseException e = output.expection;
				e.fillInStackTrace();
				throw e;
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public Boolean UpdateQuestTarget(QuestTarget questTarget) {
		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("questId", questTarget.getQuest().getId());
			param.put("questTargetId", questTarget.getId());
			param.put("data", getGson().toJson(questTarget.getUpdateState(), //
					IQuestTargetUpdateState.class));

			return loadFormGson("updateQuestTarget", param, Boolean.class);
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 
	 * You can use {@code new TypeToken<MyClass<YourClass>>().getType()} to get
	 * the Type for Generics and {@code MyClass.class} for non-generic Classes
	 * 
	 * @param scriptname
	 *            name of the script at the server
	 * @param param
	 *            Map with name=value for parameter
	 * @param resultType
	 *            work around for java's - let's call it 'bad' - implementation
	 *            of Generics, use
	 *            {@code new TypeToken<MyClass<YourClass>>().getType()} for
	 *            generic classes
	 * @return instance of T parsed with Gson from server response
	 * @throws InvalidResultException
	 *             if the result string from server is not a json string
	 * @throws ConnectionException
	 *             if something happens to the connection
	 * @throws MalformedURLException
	 *             if the {@link URL} generated from base, scriptname and param
	 * @throws UnsupportedEncodingException
	 *             if UTF-8 is not supported (now we are all DOOMED, DOOMED)
	 * @throws IOException
	 *             if something happens with the connection
	 * @throws ProtocolException
	 *             if something went wrong within the protocol (eg. use wrong
	 *             protocol)
	 */
	private <T> T loadFormGson(String scriptname, Map<String, Object> param,
			Type resultType) throws ConnectionException, InvalidResultException {

		URL url;
		String result;

		try {
			url = getURL(scriptname, param);
			result = connect(url, "GET", "");
		} catch (IOException e) // includes MalformedURLException |
								// UnsupportedEncodingException |
								// ProtocollException
		{
			e.printStackTrace();
			throw new ConnectionException(e.getMessage(), e);
		}

		Gson gson = getGson();

		System.out.print(resultType);
		try {
			return (T) gson.fromJson(result, resultType);
		} catch (Throwable cause) {
			cause.printStackTrace();
			throw new InvalidResultException(result, cause.getMessage(), cause);
		}

	}

	/**
	 * generates the URL for a script name and get parameter
	 * 
	 * @param scriptname
	 *            name of the script at the server
	 * @param queryGET
	 *            string with get parameters
	 * @return {@link URL} generated from name and parameter
	 * @throws MalformedURLException
	 *             if the base, scriptname or queryGET are malformated
	 */
	private URL getURL(CharSequence scriptname, CharSequence queryGET)
			throws MalformedURLException {
		String paramString = queryGET.length() == 0 ? "" : ("?" + queryGET);
		return new URL(_base, scriptname + paramString);
		// new URL(_protocol, _host, _port, "/" + scriptname + paramString);
	}

	/**
	 * generates the URL for a script name and get parameter {@link Map}
	 * 
	 * @param scriptname
	 *            name of the script at the server
	 * @param requestParams
	 *            map with name=value of the parameter
	 * @return {@link URL} generated from name and parameter
	 * @throws MalformedURLException
	 *             if the base or scriptname are malformated
	 */
	private URL getURL(CharSequence scriptname,
			Map<String, Object> requestParams) throws MalformedURLException,
			UnsupportedEncodingException {
		return getURL(scriptname, MapToUrlParamString(requestParams));
	}

	/**
	 * return a {@link Gson} instance prepared for drachen!!! usage
	 * 
	 * @return
	 */
	private Gson getGson() {
		return AdapterProvider.installAllAdapter(new GsonBuilder()).create();
	}

	/**
	 * Loads the {@link String} result from a webrequest
	 * 
	 * @param url
	 *            target {@link URL}
	 * @param requestMethod
	 *            'POST' or 'GET'
	 * @param requestParams
	 *            Map with name=value of the request data for the defined method
	 * @return the string result (UTF-8) of the response
	 * 
	 * @throws IOException
	 *             if something happens with the connection
	 * @throws ProtocolException
	 *             if something went wrong within the protocol
	 */
	private String connect(URL url, String requestMethod,
			Map<String, Object> requestParams) throws IOException,
			ProtocolException, InterruptedException {

		CharSequence postData = MapToUrlParamString(requestParams);
		return connect(url, requestMethod, postData);
	}

	/**
	 * Converts the map to a urlencode query string
	 * 
	 * @param requestParams
	 *            Map to convert, the objects are converted to Strings by
	 *            String.valueOf
	 * @return String for urls
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private static StringBuilder MapToUrlParamString(
			Map<String, Object> requestParams)
			throws UnsupportedEncodingException {
		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String, Object> param : requestParams.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()),
					"UTF-8"));
		}
		return postData;
	}

	/**
	 * Loads the {@link String} result from a webrequest
	 * 
	 * @param url
	 *            target {@link URL}
	 * @param requestMethod
	 *            'POST' or 'GET'
	 * @param requestParamString
	 *            String with request data for the defined method
	 * @return the string result (UTF-8) of the response
	 * @throws IOException
	 *             if something happens with the connection
	 * @throws ProtocolException
	 *             if something went wrong within the protocol
	 */
	private String connect(URL url, String requestMethod,
			CharSequence requestParamString) throws IOException,
			ProtocolException {

		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();

		System.out.println(url.toString());

		if (this.cookieManager != null)
			this.cookieManager.setCookies(urlConnection);

		if (requestParamString.length() > 0) {
			byte[] postDataBytes = requestParamString.toString().getBytes(
					"UTF-8");

			urlConnection.setRequestMethod(requestMethod);
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Content-Length",
					String.valueOf(postDataBytes.length));
			urlConnection.setDoOutput(true);
			urlConnection.getOutputStream().write(postDataBytes);
		}
		// urlConnection.setRequestProperty(
		// "Cookie","JSESSIONID=" + your_session_id);
		urlConnection.connect();
		try {
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());

			StringBuilder sb = new StringBuilder();
			InputStreamReader isr = new InputStreamReader(in, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			String read;
			while ((read = br.readLine()) != null) {
				sb.append(read);
			}

			if (this.cookieManager != null)
				this.cookieManager.storeCookies(urlConnection);

			String result = sb.toString();
			System.out.println(result);
			return result;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			urlConnection.disconnect();
		}

	}

	private String connect_withAbort(URL url, String requestMethod,
			CharSequence requestParamString) throws IOException,
			ProtocolException {

		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();

		System.out.println(url.toString());

		if (this.cookieManager != null)
			this.cookieManager.setCookies(urlConnection);

		if (requestParamString.length() > 0) {
			byte[] postDataBytes = requestParamString.toString().getBytes(
					"UTF-8");

			urlConnection.setRequestMethod(requestMethod);
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Content-Length",
					String.valueOf(postDataBytes.length));
			urlConnection.setDoOutput(true);
			urlConnection.getOutputStream().write(postDataBytes);
		}
		// urlConnection.setRequestProperty(
		// "Cookie","JSESSIONID=" + your_session_id);
		urlConnection.connect();
		try {
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());

			final StringBuilder sb = new StringBuilder();
			InputStreamReader isr = new InputStreamReader(in, "UTF-8");
			final BufferedReader br = new BufferedReader(isr);

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String read;
						while ((read = br.readLine()) != null) {
							sb.append(read);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});

			if (this.cookieManager != null)
				this.cookieManager.storeCookies(urlConnection);

			// TODO: make isCanceled accessible
			boolean isCanceled = false;
			final long sleepTime = 100; // 100ms

			t.start();

			// check every <sleepTime> ms if this should be canceled
			while (!isCanceled && t.isAlive())
				Thread.sleep(sleepTime);

			if (isCanceled) {
				// TODO: handle cancel result, maybe throw exception ...
				return null;
			} else {
				String result = sb.toString();
				System.out.println(result);
				return result;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		} finally {
			urlConnection.disconnect();
		}

	}

	/**
	 * Initializes the client (enables cookies und sets the base url)
	 * 
	 * @param base
	 *            url to the server (eg. "https://example.org" or
	 *            "http://sub.example.com:80/path/to/dir/")
	 * @throws Exception
	 */
	public void initConnection(URL base) throws Exception {

		if (base == null)
			throw new NullPointerException("base");
		// _protocol = "https";
		// _host = "rzssh1";
		// _host = "localhost";
		// _host = "10.0.2.2";
		// _port = 8443;
		// _host = host;
		// _port = port;
		_base = base;

		java.net.CookieManager def = new java.net.CookieManager();
		java.net.CookieHandler.setDefault(def);
		def.setCookiePolicy(java.net.CookiePolicy.ACCEPT_ALL);

		this.cookieManager = null;// new CookieManager();

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

}
