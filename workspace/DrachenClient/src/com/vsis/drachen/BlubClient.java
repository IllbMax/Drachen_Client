package com.vsis.drachen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
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
	private String _protocol;
	private String _host;
	private int _port;

	public User Login(String username, String password) {

		try {

			Map<String, Object> param = new LinkedHashMap<>();
			param.put("username", username);
			param.put("password", password);

			ResultWrapper<User> output = loadFormGson("login", param,
					new TypeToken<ResultWrapper<User>>() {
					}.getType());
			return output.resultObject;

		} catch (InterruptedException e) {
			return null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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

		} catch (InterruptedException e) {
			return null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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

		} catch (InterruptedException e) {
			return null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
		} catch (InterruptedException e) {
			return null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
		} catch (InterruptedException e) {
			return false;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
		} catch (InterruptedException e) {
			return false;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
	 *            work around for java's f***ing implementation of Generics
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ProtocolException
	 * @throws InterruptedException
	 */
	private <T> T loadFormGson(String scriptname, Map<String, Object> param,
			Type resultType) throws MalformedURLException,
			UnsupportedEncodingException, IOException, ProtocolException,
			InterruptedException {
		URL url = getURL(scriptname, param);

		String result = connect(url, "GET", "");

		Gson gson = getGson();

		System.out.print(resultType);
		return (T) gson.fromJson(result, resultType);
	}

	private URL getURL(CharSequence scriptname, CharSequence queryPOST)
			throws MalformedURLException {
		String paramString = queryPOST.length() == 0 ? "" : ("?" + queryPOST);
		return new URL(_protocol, _host, _port, "/" + scriptname + paramString);
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
			ProtocolException, InterruptedException {

		HttpsURLConnection urlConnection = (HttpsURLConnection) url
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

			InputStreamReader isr = new InputStreamReader(in, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			String read;
			StringBuilder sb = new StringBuilder();
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
			return "";
		} finally {
			urlConnection.disconnect();
		}

	}

	public void initConnection() throws Exception {
		String host
		// = "rzssh1";
		= "localhost";
		int port = 8443;
		initConnection(host, port);
	}

	public void initConnection(String host, int port) throws Exception {

		_protocol = "https";
		// _host = "rzssh1";
		// _host = "localhost";
		// _host = "10.0.2.2";
		// _port = 8443;
		_host = host;
		_port = port;

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
