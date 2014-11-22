package com.vsis.drachenmobile;

import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vsis.drachen.LocationService;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.world.Location;
import com.vsis.drachenmobile.helper.Helper;
import com.vsis.drachenmobile.service.LocationLocalService;

public class Main_Activity extends Activity {

	private Date _lastLocationReciev;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.action_logout:
			logout();
			break;
		}

		return true;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// TODO Auto-generated method stub
		_lastLocationReciev = new Date();
		_lastLocationReciev.setTime(0); // set Time to point in past

		Button btn = (Button) findViewById(R.id.button_main_QuestsPrototypes);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Main_Activity.this,
						QuestPrototype_Activity.class);
				startActivity(intent);
			}
		});
		btn = (Button) findViewById(R.id.button_main_Quests);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Main_Activity.this,
						Quest_overview_Activity.class);
				startActivity(intent);
			}
		});

		btn = (Button) findViewById(R.id.button_main_SensorManager);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Main_Activity.this,
						SensorManager_Activity.class);
				startActivity(intent);
			}
		});

		DrachenApplication app = (DrachenApplication) getApplication();
		User user = app.getAppData().getUser();

		TextView textView = (TextView) findViewById(R.id.textView_yourLocation);
		textView = (TextView) findViewById(R.id.textView_Main_title);
		textView.setText(String.format(getString(R.string.greetings),
				user.getDisplayName()));
	}

	@Override
	public void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(this).registerReceiver(
				locationChangedReceiver,
				new IntentFilter(DrachenApplication.EVENT_LOCATION_CHANGED));

		LocationService locationService = ((DrachenApplication) getApplication())
				.getAppData().getLocationService();

		if (_lastLocationReciev.before(locationService
				.getLastCurrentLocationSetTime())) {

			showLocation(locationService.getCurrentLocation());
		}
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle("");
		actionBar.setTitle("Drachen!!!");
	}

	private BroadcastReceiver locationChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int locId = intent.getIntExtra(
					LocationLocalService.EXTRA_LOCATION_NEW, -1);
			final Location newLocation = ((DrachenApplication) getApplication())
					.getAppData().getLocationService().getLocationForId(locId);
			runOnUiThread(new Runnable() {
				public void run() {
					showLocation(newLocation);
				}
			});
		}
	};

	@Override
	protected void onPause() {
		// Unregister since the activity is not visible
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				locationChangedReceiver);
		_lastLocationReciev = ((DrachenApplication) getApplication())
				.getAppData().getLocationService()
				.getLastCurrentLocationSetTime();
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("lastRecieve", _lastLocationReciev.getTime());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		_lastLocationReciev = new Date();
		_lastLocationReciev.setTime(savedInstanceState.getLong("lastRecieve"));
	}

	private void showLocation(Location newLocation) {
		TextView locationView = (TextView) findViewById(R.id.textView_yourLocation);
		String name = Helper
				.getLocationDisplay(Main_Activity.this, newLocation);
		locationView.setText(name);

	}

	@Override
	public void onBackPressed() {
		logout();
		// the super call is in the logout task
		// super.onBackPressed();
	}

	private void logout() {
		LogoutTask task = new LogoutTask();
		task.execute();
	}

	class LogoutTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog ringProgressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Context ctx = Main_Activity.this;
			ringProgressDialog = ProgressDialog.show(ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.logging_out), true);
			ringProgressDialog.setCancelable(true);
			ringProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// actually could set running = false; right here, but I'll
					// stick to contract.
					boolean success = cancel(true);
				}
			});
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DrachenApplication app = (DrachenApplication) getApplication();
			MyDataSet client = app.getAppData();

			boolean success = client.logout();
			return success;
		}

		@Override
		protected void onCancelled(Boolean result) {
			super.onCancelled(result);
			// TODO evtl clean up
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				ringProgressDialog.dismiss();

				// Main_Activity.this.finish();
				Main_Activity.super.onBackPressed();

			} else {
				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Main_Activity.this);
				builder.setTitle("Logout failed.");
				builder.setMessage("Please try again.");
				builder.show();
			}

		}
	};

}
