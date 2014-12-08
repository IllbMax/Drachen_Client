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
import com.visis.drachen.exception.InternalProcessException;
import com.visis.drachen.exception.InvalidParameterException;
import com.visis.drachen.exception.MissingParameterException;
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

	public Boolean Logout() {
		try {

			Map<String, Object> param = new LinkedHashMap<>();

			Boolean output = loadFormGson("logout", param, Boolean.class);
			return output;

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public List<QuestPrototype> QuestsForLocation(int locationId) {

		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("locationId", locationId);

			return loadFormGson("showQuestsForLocation", param,
					new TypeToken<List<QuestPrototype>>() {
					}.getType());

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Location LocationForId(int locationId) {

		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("locationId", locationId);

			return loadFormGson("locationtree", param, Location.class);

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public List<Location> allLocationForest() {

		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("locationId", "all");

			return loadFormGson("locationtree", param,
					new TypeToken<List<Location>>() {
					}.getType());

		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Quest StartQuest(int questPrototypeId) {
		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("questPrototypeId", questPrototypeId);

			return loadFormGson("startQuest", param, Quest.class);
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Boolean AbortQuest(int questId) {
		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("questId", questId);

			return loadFormGson("abortQuest", param, Boolean.class);
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	public Boolean FinishQuest(int questId) {
		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("questId", questId);

			return loadFormGson("finishQuest", param, Boolean.class);
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
	 * @param param
	 * @param resultType
	 *            work around for java's - let's call it 'bad' - implementation
	 *            of Generics
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ProtocolException
	 * @throws InvalidResultException
	 * @throws InterruptedException
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

	private URL getURL(CharSequence scriptname, CharSequence queryPOST)
			throws MalformedURLException {
		String paramString = queryPOST.length() == 0 ? "" : ("?" + queryPOST);
		return new URL(_base, scriptname + paramString);
		// new URL(_protocol, _host, _port, "/" + scriptname + paramString);
	}

	private URL getURL(CharSequence scriptname,
			Map<String, Object> requestParams) throws MalformedURLException,
			UnsupportedEncodingException {
		return getURL(scriptname, MapToUrlParamString(requestParams));
	}

	private Gson getGson() {
		return AdapterProvider.installAllAdapter(new GsonBuilder()).create();
	}

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
