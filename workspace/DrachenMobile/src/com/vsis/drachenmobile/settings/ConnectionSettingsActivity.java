package com.vsis.drachenmobile.settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.vsis.drachenmobile.R;

public class ConnectionSettingsActivity extends Activity {

	public static final String KEY_PREF_BASE_URL = "pref_base_url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}

	public static class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.pref_connection);
		}

	}

}
