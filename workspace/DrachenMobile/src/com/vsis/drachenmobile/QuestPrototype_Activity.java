package com.vsis.drachenmobile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vsis.drachen.LocationService;
import com.vsis.drachen.QuestService;
import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.IdNotFoundException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestPrototype;
import com.vsis.drachen.model.world.Location;
import com.vsis.drachenmobile.helper.Helper;
import com.vsis.drachenmobile.task.QuestStartTaskTemplate;
import com.vsis.drachenmobile.util.ArrayDetailsExpandableListAdapter;

public class QuestPrototype_Activity extends Activity {

	ExpandableListView _prototypeListView;
	QuestPrototypeExpListAdapter _prototypeAdapter;
	private Date _lastLocationReciev;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.refreshmenu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.action_refresh:
			refreshQuests();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questprototype);

		_lastLocationReciev = new Date();
		_lastLocationReciev.setTime(0); // set Time to point in past

		// _prototypeAdapter2 = new QuestPrototypeExpListAdapter2(this,
		// new ArrayList<QuestPrototype>(),
		// new HashMap<QuestPrototype, List<QuestPrototype>>());
		_prototypeAdapter = new QuestPrototypeExpListAdapter(this,
				new ArrayList<QuestPrototype>());

		_prototypeListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		_prototypeListView.setAdapter(_prototypeAdapter);

	}

	@Override
	public void onResume() {
		super.onResume();
		_prototypeListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		_prototypeAdapter = (QuestPrototypeExpListAdapter) _prototypeListView
				.getExpandableListAdapter();

		LocalBroadcastManager.getInstance(this).registerReceiver(
				locationChangedReceiver,
				new IntentFilter(DrachenApplication.EVENT_LOCATION_CHANGED));

		LocationService locationService = ((DrachenApplication) getApplication())
				.getAppData().getLocationService();

		setDisplayLocation(locationService.getCurrentLocation());

		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle("available Quests");
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
					setDisplayLocation(newLocation);
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

	private void startQuest(QuestPrototype questPrototype) {
		QuestStartTask task = new QuestStartTask(this, questPrototype,
				((DrachenApplication) this.getApplication()).getAppData());
		task.execute();

	}

	private void setDisplayLocation(Location location) {
		setDisplayLocation(location, false);
	}

	private void setDisplayLocation(Location location, boolean forceReload) {
		TextView locationView = (TextView) findViewById(R.id.textView_yourLocation);
		locationView.setText(Helper.getLocationDisplay(
				QuestPrototype_Activity.this, location));
		if (location != null) {
			QuestPrototypeLoadTask task = new QuestPrototypeLoadTask(
					forceReload);
			task.execute(location.getId());
		} else {
			_prototypeAdapter.clear();
			_prototypeAdapter.notifyDataSetChanged();
		}
	}

	private void refreshQuests() {
		DrachenApplication app = (DrachenApplication) getApplication();
		User user = app.getAppData().getUser();

		setDisplayLocation(user.getLocation(), true);
	}

	private class QuestPrototypeExpListAdapter extends
			ArrayDetailsExpandableListAdapter<QuestPrototype> {

		int[] _layouts = { android.R.layout.simple_expandable_list_item_1 };

		public QuestPrototypeExpListAdapter(Context context,
				List<QuestPrototype> listDataHeader) {
			super(context, listDataHeader);

		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final QuestPrototype questPrototype = getGroup(groupPosition);
			// getChild(groupPosition, childPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(_layouts[childPosition],
						null);
			}

			if (childPosition == 0) {

				TextView txtListChild = (TextView) convertView
						.findViewById(android.R.id.text1);
				txtListChild.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
				txtListChild.setText(questPrototype.getDescription());
				return convertView;
			}
			return convertView;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			final QuestPrototype questPrototype = getGroup(groupPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.expandlistviewitem_questprototype, null);
			}
			final TextView textViewName = (TextView) convertView
					.findViewById(R.id.listview_QuestPrototype_Name);
			Button butttonStart = (Button) convertView
					.findViewById(R.id.listview_QuestStartButton);

			textViewName.setText(questPrototype.getName());
			butttonStart.setText("Start");
			butttonStart.setFocusable(false);

			butttonStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startQuest(questPrototype);
				}

			});

			return convertView;
		}

		@Override
		protected Object getChild(QuestPrototype group, int childPosition) {
			return group.getDescription();
		}

		@Override
		protected int getChildrenCount(QuestPrototype group) {
			return 1;
		}
	}

	private class QuestPrototypeArrayAdapter extends
			ArrayAdapter<QuestPrototype> {

		HashMap<QuestPrototype, Integer> mIdMap = new HashMap<QuestPrototype, Integer>();

		public QuestPrototypeArrayAdapter(Context context,
				List<QuestPrototype> objects) {
			super(context, R.layout.listviewitem_questprototype, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) QuestPrototype_Activity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(
					R.layout.listviewitem_questprototype, parent, false);
			TextView textViewName = (TextView) rowView
					.findViewById(R.id.listview_QuestPrototype_Name);
			final TextView textViewDesc = (TextView) rowView
					.findViewById(R.id.listview_QuestPrototypeDescription);
			Button butttonStart = (Button) rowView
					.findViewById(R.id.listview_QuestStartButton);

			QuestPrototype prototye = getItem(position);
			textViewName.setText(prototye.getName());
			textViewDesc.setText(prototye.getDescription());

			textViewName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int visib = textViewDesc.getVisibility() == View.VISIBLE ? View.GONE
							: View.VISIBLE;
					textViewDesc.setVisibility(visib);
				}
			});
			butttonStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startQuest(getItem(position));
				}

			});

			return rowView;
		}

		// @Override
		// public long getItemId(int position) {
		// QuestPrototype item = getItem(position);
		// return mIdMap.get(item);
		// }

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	private class QuestPrototypeLoadTask extends
			AsyncTask<Integer, Void, Collection<QuestPrototype>> {

		private ProgressDialog ringProgressDialog;
		private boolean forceReload = false;
		private DrachenBaseException _exception;

		public QuestPrototypeLoadTask(boolean forceReload) {
			this.forceReload = forceReload;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// _prototypeListView available
			_prototypeAdapter.clear();
			Context ctx = QuestPrototype_Activity.this;
			ringProgressDialog = ProgressDialog.show(ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.load_QuestPrototype), true);
			ringProgressDialog.setCancelable(true);

		}

		@Override
		protected Collection<QuestPrototype> doInBackground(Integer... params) {
			MyDataSet appData = ((DrachenApplication) getApplication())
					.getAppData();
			QuestService questService = appData.getQuestService();
			int locationId = params[0];

			try {
				Collection<QuestPrototype> result = questService
						.getAvailableQuestForLocation(locationId, forceReload);
				return questService.removeStartedQuests(result);

			} catch (DrachenBaseException e) {
				_exception = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(Collection<QuestPrototype> result) {
			super.onPostExecute(result);
			if (result != null) {
				_prototypeAdapter.addAll(result);
				_prototypeAdapter.notifyDataSetChanged();
				ringProgressDialog.dismiss();
			} else {
				String message = getErrorString();

				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						QuestPrototype_Activity.this);
				builder.setTitle(R.string.register_failed);
				builder.setMessage(message);
				builder.show();
			}

		}

		private String getErrorString() {
			Context ctx = QuestPrototype_Activity.this;
			String message = ctx.getString(R.string.please_try_again);

			if (_exception != null) {
				if (_exception instanceof MissingParameterException) {
					MissingParameterException e = (MissingParameterException) _exception;
					message = ctx.getString(R.string.missing_parameter_s,
							e.getParameter());
				} else if (_exception instanceof IdNotFoundException) {
					IdNotFoundException e = (IdNotFoundException) _exception;
					message = ctx.getString(R.string.id_not_found_parameter_s,
							e.getParameter());

				} else if (_exception instanceof InternalProcessException) {
					InternalProcessException e = (InternalProcessException) _exception;
					message = ctx.getString(R.string.internal_process_error,
							e.getMessage());
				}
			}
			return message;
		}

	}

	private class QuestStartTask extends QuestStartTaskTemplate {

		public QuestStartTask(Context ctx, QuestPrototype questPrototype,
				MyDataSet appData) {
			super(ctx, questPrototype, appData);
		}

		@Override
		protected void onPostExecute(Quest result) {
			super.onPostExecute(result);

			if (result != null) {
				_prototypeAdapter.removeGroup(questPrototype);
				_prototypeAdapter.notifyDataSetChanged();

			} else {
				showAlertExceptionDialog();
			}
		}
	}

}
