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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;
import com.vsis.drachen.ItemService;
import com.vsis.drachen.LocationService;
import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.IdNotFoundException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.objects.Item;
import com.vsis.drachen.model.world.Location;
import com.vsis.drachenmobile.helper.Helper;

public class ItemList_Activity extends Activity {

	public static final String EXTRA_INVENTRORY = "isInv";

	ListView _itemListView;
	ItemArrayAdapter _itemAdapter;
	private Date _lastLocationReciev;
	private boolean isInventory = false;

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
			refreshItems();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itemslist);

		_lastLocationReciev = new Date();
		_lastLocationReciev.setTime(0); // set Time to point in past

		_itemAdapter = new ItemArrayAdapter(this, new ArrayList<Item>());
		isInventory = getIntent().getBooleanExtra(EXTRA_INVENTRORY, false);

		_itemListView = (ListView) findViewById(R.id.listView1);
		_itemListView.setAdapter(_itemAdapter);

	}

	@Override
	public void onResume() {
		super.onResume();
		_itemListView = (ListView) findViewById(R.id.listView1);
		_itemAdapter = (ItemArrayAdapter) _itemListView.getAdapter();
		isInventory = getIntent().getBooleanExtra(EXTRA_INVENTRORY, false);
		_itemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Item item = _itemAdapter.getItem(position);
				showDetails(item);
			}
		});

		LocalBroadcastManager.getInstance(this).registerReceiver(
				locationChangedReceiver,
				new IntentFilter(DrachenApplication.EVENT_LOCATION_CHANGED));

		MyDataSet appData = ((DrachenApplication) getApplication())
				.getAppData();
		LocationService locationService = appData.getLocationService();

		ActionBar actionBar = getActionBar();

		if (isInventory) {
			setInventory();
			actionBar.setSubtitle(getString(R.string.item_in_inventory, appData
					.getUser().getDisplayName()));
		} else {
			setDisplayLocation(locationService.getCurrentLocation());
			actionBar.setSubtitle(getString(R.string.item_at_location));
		}
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

	private void showDetails(Item item) {
		Intent intent = new Intent(this, Item_details_Activity.class);
		intent.putExtra(Item_details_Activity.EXTRA_ITEMID, item.getId());
		startActivityForResult(intent, 0);
	}

	private void setDisplayLocation(Location location) {
		setDisplayLocation(location, false);
	}

	private void setDisplayLocation(Location location, boolean forceReload) {
		TextView preTextView = (TextView) findViewById(R.id.textView1);
		TextView locationView = (TextView) findViewById(R.id.textView_yourLocation);

		preTextView.setText(R.string.item_at_location);
		locationView.setVisibility(View.VISIBLE);
		locationView.setText(Helper.getLocationDisplay(ItemList_Activity.this,
				location));

		if (location != null) {
			ItemLoadTask task = new ItemLoadTask(forceReload);
			task.execute(location.getId());
		} else {
			_itemAdapter.clear();
			_itemAdapter.notifyDataSetChanged();
		}
	}

	private void setInventory() {
		TextView preTextView = (TextView) findViewById(R.id.textView1);
		TextView locationView = (TextView) findViewById(R.id.textView_yourLocation);

		DrachenApplication app = (DrachenApplication) getApplication();
		User user = app.getAppData().getUser();

		preTextView.setText(getString(R.string.item_in_inventory,
				user.getDisplayName()));
		locationView.setVisibility(View.GONE);

		_itemAdapter.clear();
		_itemAdapter.addAll(user.getItems());
		_itemAdapter.notifyDataSetChanged();
		// ItemLoadTask task = new ItemLoadTask(forceReload);
		// task.execute(location.getId());
	}

	private void refreshItems() {
		DrachenApplication app = (DrachenApplication) getApplication();
		User user = app.getAppData().getUser();

		if (isInventory)
			setInventory();
		else
			setDisplayLocation(user.getLocation(), true);
	}

	private class ItemArrayAdapter extends ArrayAdapter<Item> {

		HashMap<Item, Integer> mIdMap = new HashMap<Item, Integer>();

		public ItemArrayAdapter(Context context, List<Item> objects) {
			super(context, R.layout.listviewitem_imagetext, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
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

			Item item = getItem(position);

			name.setText(item.getName());

			String key = item != null ? item.getImageKey() : null;

			MyDataSet appdata = ((DrachenApplication) getApplication())
					.getAppData();
			Helper.setImage(avatar, appdata.getResourceService(), key, true);

			return convertView;
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

	private class ItemLoadTask extends
			AsyncTask<Integer, Void, Collection<Item>> {

		private ProgressDialog ringProgressDialog;
		private boolean forceReload = false;
		private DrachenBaseException _exception;

		public ItemLoadTask(boolean forceReload) {
			this.forceReload = forceReload;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// _prototypeListView available
			_itemAdapter.clear();
			Context ctx = ItemList_Activity.this;
			ringProgressDialog = ProgressDialog.show(ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.load_QuestPrototype), true);
			ringProgressDialog.setCancelable(true);

		}

		@Override
		protected Collection<Item> doInBackground(Integer... params) {
			MyDataSet appData = ((DrachenApplication) getApplication())
					.getAppData();
			ItemService itemService = appData.getItemService();
			int locationId = params[0];

			try {
				Collection<Item> result = itemService
						.getPresentItemsForLocation(locationId, forceReload);
				return result;

			} catch (DrachenBaseException e) {
				_exception = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(Collection<Item> result) {
			super.onPostExecute(result);
			if (result != null) {
				_itemAdapter.addAll(result);
				_itemAdapter.notifyDataSetChanged();
				ringProgressDialog.dismiss();
			} else {
				String message = getErrorString();

				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ItemList_Activity.this);
				builder.setTitle(R.string.register_failed);
				builder.setMessage(message);
				builder.show();
			}

		}

		private String getErrorString() {
			Context ctx = ItemList_Activity.this;
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
}
