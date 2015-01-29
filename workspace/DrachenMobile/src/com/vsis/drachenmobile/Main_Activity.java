package com.vsis.drachenmobile;

import java.util.Date;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;
import com.vsis.drachen.LocationService;
import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.RestrictionException;
import com.vsis.drachen.model.NPC;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.world.Location;
import com.vsis.drachenmobile.helper.Helper;
import com.vsis.drachenmobile.minigame.Skirmish_Activity;

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
		Intent intent;
		switch (item.getItemId()) {

		case R.id.action_logout:
			logout();
			break;
		case R.id.action_sensorquickaccess:
			intent = new Intent(this, SensorQuickSelect_Activity.class);
			startActivity(intent);
			break;
		case R.id.action_quests:
			intent = new Intent(this, Quest_overview_Activity.class);
			startActivity(intent);
			break;

		// case R.id.rooms_button:
		//
		// DrachenApplication app = (DrachenApplication) getApplication();
		// MyDataSet appdata = app.getAppData();
		// LocationService locationService = appdata.getLocationService();
		// MenuItem roomsButton = (MenuItem) findViewById(R.id.rooms_button);
		// if (locationService.isInRoom()) {
		// locationService.leaveRoom();
		//
		// if (roomsButton != null) {
		// roomsButton.setIcon(R.drawable.enter_room);
		// }
		// }
		//
		// // !!!!!!!!!!!!!!!!!!!barcode scanner
		//
		// String returnString = "location: 12341234";
		// String[] tokens = returnString.split(":");
		// if (tokens[0].equals("location")) {
		//
		// int locationId = Integer.parseInt(tokens[1]);
		//
		// Location room = locationService.getLocationForId(locationId);
		// if (room != null) {
		// locationService.SetRegion(room);
		// if (roomsButton != null) {
		// roomsButton.setIcon(R.drawable.leave_room);
		// }
		// }
		// }
		// break;

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
		btn = (Button) findViewById(R.id.button_main_minigameTest);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Skirmish_Activity
						.createDummy((DrachenApplication) getApplication());
				Intent intent = new Intent(Main_Activity.this,
						Skirmish_Activity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();

		DrachenApplication app = (DrachenApplication) getApplication();
		User user = app.getAppData().getUser();

		TextView textView = (TextView) findViewById(R.id.textView_Main_title);
		textView.setText(String.format(getString(R.string.greetings),
				user.getDisplayName()));

		LocalBroadcastManager.getInstance(this).registerReceiver(
				locationChangedReceiver,
				new IntentFilter(DrachenApplication.EVENT_LOCATION_CHANGED));

		LocationService locationService = ((DrachenApplication) getApplication())
				.getAppData().getLocationService();

		showLocation(locationService.getCurrentLocation());

		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle("");
		actionBar.setTitle("Drachen!!!");
	}

	private BroadcastReceiver locationChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int locId = intent.getIntExtra(
					DrachenApplication.EXTRA_LOCATION_NEW, -1);
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

		String key = newLocation != null ? newLocation.getImageKey() : null;

		MyDataSet appdata = ((DrachenApplication) getApplication())
				.getAppData();
		SVGImageView locationImage = (SVGImageView) findViewById(R.id.imageView1);
		Helper.setImage(locationImage, appdata.getResourceService(), key, true);

		if (newLocation != null) {
			LoadNPCTask task = new LoadNPCTask();
			task.execute(newLocation.getId());
		}
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
		private DrachenBaseException _exception = null;

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

			try {
				boolean success = client.logout();
				return success;
			} catch (DrachenBaseException e) {
				_exception = e;
				return false;
			}
		}

		private void cleanUp(boolean all) {
			DrachenApplication app = (DrachenApplication) getApplication();
			if (all)
				app.getAppData().disposeComponents();
			app.stopDrachenServices();
		}

		@Override
		protected void onCancelled(Boolean result) {
			super.onCancelled(result);
			// TODO evtl clean up
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result != null && result) {
				ringProgressDialog.dismiss();
				cleanUp(false);
				Main_Activity.super.onBackPressed();

			} else {
				String message = getErrorString();

				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Main_Activity.this);
				builder.setTitle(R.string.logout_failed);
				builder.setMessage(message);
				builder.setPositiveButton(R.string.force_logout,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								cleanUp(true);
								Main_Activity.super.onBackPressed();

							}
						});
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				builder.show();
			}

		}

		private String getErrorString() {
			Context ctx = Main_Activity.this;
			String message = ctx.getString(R.string.please_try_again);

			if (_exception != null) {
				if (_exception instanceof RestrictionException) {
					// RestrictionException e = (RestrictionException)
					// _exception;
					message = ctx.getString(R.string.access_denied_logged_out);
				} else if (_exception instanceof InternalProcessException) {
					InternalProcessException e = (InternalProcessException) _exception;
					message = ctx.getString(R.string.internal_process_error,
							e.getMessage());
				}
			}
			return message;
		}

	};

	class LoadNPCTask extends AsyncTask<Integer, Void, List<NPC>> {

		private ProgressDialog ringProgressDialog;
		private DrachenBaseException _exception = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Context ctx = Main_Activity.this;
			ringProgressDialog = ProgressDialog.show(ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.load_npc), true);
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
		protected List<NPC> doInBackground(Integer... params) {
			DrachenApplication app = (DrachenApplication) getApplication();
			MyDataSet client = app.getAppData();
			int locationId = params[0];
			try {
				List<NPC> result = client.getNPCService()
						.getPresentNPCForLocation(locationId);
				return result;
			} catch (DrachenBaseException e) {
				_exception = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<NPC> result) {
			super.onPostExecute(result);
			if (result != null) {
				ringProgressDialog.dismiss();

				ListView npcs = (ListView) findViewById(R.id.listView1);
				npcs.setAdapter(new NPCArrayAdapter(Main_Activity.this, result));
				npcs.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						NPC npc = (NPC) parent.getAdapter().getItem(position);
						Intent intent = new Intent(Main_Activity.this,
								NPC_talk_Activity.class);
						intent.putExtra(NPC_talk_Activity.EXTRA_NPCID,
								npc.getId());
						startActivity(intent);
					}
				});

			} else {
				String message = getErrorString();

				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Main_Activity.this);
				builder.setTitle(R.string.logout_failed);
				builder.setMessage(message);

				builder.show();
			}

		}

		private String getErrorString() {
			Context ctx = Main_Activity.this;
			String message = ctx.getString(R.string.please_try_again);

			if (_exception != null) {
				if (_exception instanceof RestrictionException) {
					// RestrictionException e = (RestrictionException)
					// _exception;
					message = ctx.getString(R.string.access_denied_logged_out);
				} else if (_exception instanceof InternalProcessException) {
					InternalProcessException e = (InternalProcessException) _exception;
					message = ctx.getString(R.string.internal_process_error,
							e.getMessage());
				}
			}
			return message;
		}

	};

	private class NPCArrayAdapter extends ArrayAdapter<NPC> {

		// Map<SensorType, Integer> mIdMap = new EnumMap<SensorType, Integer>(
		// SensorType.class);

		public NPCArrayAdapter(Context context, List<NPC> objects) {
			super(context, R.layout.listviewitem_imagetext, objects);
			// for (int i = 0; i < objects.size(); ++i) {
			// mIdMap.put(objects.get(i), i);
			// }
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.listviewitem_imagetext,
						parent, false);
			}
			SVGImageView avatar = (SVGImageView) convertView
					.findViewById(R.id.imageview1);
			TextView name = (TextView) convertView.findViewById(R.id.textview1);

			NPC npc = getItem(position);

			name.setText(npc.getName());

			String key = npc != null ? npc.getImageKey() : null;

			MyDataSet appdata = ((DrachenApplication) getApplication())
					.getAppData();
			Helper.setImage(avatar, appdata.getResourceService(), key, true);

			return convertView;
		}

	}

}
