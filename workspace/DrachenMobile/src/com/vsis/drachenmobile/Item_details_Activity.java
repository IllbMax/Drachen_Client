package com.vsis.drachenmobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;
import com.vsis.drachen.ItemService;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.RestrictionException;
import com.vsis.drachen.model.objects.Item;
import com.vsis.drachen.model.objects.ObjectAction;
import com.vsis.drachen.model.objects.ObjectUseListener;
import com.vsis.drachen.model.objects.ObjectUseListener.IOnActionEventLister;
import com.vsis.drachenmobile.helper.Helper;

public class Item_details_Activity extends Activity {

	public static final String EXTRA_ITEMID = "itemId";

	int _itemId;

	private ArrayList<ObjectAction> activatorAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itemdetails);

	}

	@Override
	protected void onResume() {
		super.onResume();

		_itemId = getIntent().getExtras().getInt(EXTRA_ITEMID);

		displayItem();

	}

	@Override
	protected void onStop() {
		super.onStop();
		untrackListeners();
	}

	private void untrackListeners() {
		DrachenApplication app = (DrachenApplication) getApplication();
		MyDataSet appdata = app.getAppData();

		SensorService sensorService = appdata.getSensorService();
		for (ObjectAction a : activatorAction) {
			sensorService.untrackSensorReceiver(a.getActivator());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt(EXTRA_ITEMID, _itemId);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		_itemId = savedInstanceState.getInt(EXTRA_ITEMID);
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void displayItem() {
		DrachenApplication app = (DrachenApplication) getApplication();
		ItemService itemService = app.getAppData().getItemService();
		Item item = itemService.getItemFromId(_itemId);

		displayItem(item);
	}

	private void displayItem(final Item item) {

		if (item == null) {
			// TODO: close this activity
		} else {
			MyDataSet appdata = ((DrachenApplication) getApplication())
					.getAppData();

			TextView textView_name = (TextView) findViewById(R.id.textview1);
			TextView textView_desc = (TextView) findViewById(R.id.textview2);
			SVGImageView itemImage = (SVGImageView) findViewById(R.id.imageview1);
			ListView listView_actions = (ListView) findViewById(R.id.listview1);

			textView_name.setText(item.getName());
			textView_desc.setText(item.getDescription());

			Helper.setImage(itemImage, appdata.getResourceService(),
					item.getImageKey(), true);
			itemImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showBiggerImage(item);
				}
			});

			List<ObjectActionFormatter> actions = new ArrayList<ObjectActionFormatter>();
			activatorAction = new ArrayList<ObjectAction>();
			for (ObjectAction a : item.getObjectAction()) {
				boolean active = false;
				if (a.isNeedHoldByUser())
					active = item.getLocationId() <= 0;
				else if (a.isNeedHoldByLocation())
					active = item.getLocationId() > 0;
				else {// no constraint
					active = true;
				}
				if (active)
					if (a.getActivator() == null) // so use the button/list
					{
						actions.add(new ObjectActionFormatter(a));
					} else { // activated by sensors
						setActionListener(a, item);
					}
			}
			SensorService sensorService = appdata.getSensorService();
			for (ObjectAction a : activatorAction) {
				sensorService.trackSensorReceiver(a.getActivator());
			}
			listView_actions
					.setAdapter(new ArrayAdapter<ObjectActionFormatter>(this,
							android.R.layout.simple_list_item_1, actions));
			listView_actions.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ObjectActionFormatter af = (ObjectActionFormatter) parent
							.getAdapter().getItem(position);
					ObjectAction action = af.getAction();

					performAction(action, item);
				}
			});
		}
	}

	private void setActionListener(final ObjectAction a, final Item item) {
		activatorAction.add(a);
		a.getActivator().setOnActionEventListener(new IOnActionEventLister() {

			@Override
			public void onActionRecognized(ObjectUseListener action,
					String Identifier) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						synchronized (item) {
							untrackListeners();
							performAction(a, item);
						}
					}
				});
			}
		});
	}

	private void showBiggerImage(Item item) {
		Intent intent = new Intent(this, Image_Activity.class);
		intent.putExtra(Image_Activity.EXTRA_IMAGEKEY, item.getImageKey());
		intent.putExtra(Image_Activity.EXTRA_TITLE, item.getName());
		startActivity(intent);
	}

	private void performAction(ObjectAction action, Item item) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(action.getName());
		// builder.setMessage(action.getActionDescription());
		// builder.show();
		ItemPerformActionTask task = new ItemPerformActionTask(action, item);
		task.execute();
	}

	private class ItemPerformActionTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog ringProgressDialog;
		private DrachenBaseException _exception = null;
		private ObjectAction action;
		private Item item;

		public ItemPerformActionTask(ObjectAction action, Item item) {
			this.action = action;
			this.item = item;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Context ctx = Item_details_Activity.this;
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
		protected Boolean doInBackground(Void... params) {
			DrachenApplication app = (DrachenApplication) getApplication();
			MyDataSet appData = app.getAppData();
			try {
				return appData.getItemService().performObjectAction(action,
						item);

			} catch (DrachenBaseException e) {
				_exception = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result != null && result) {
				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Item_details_Activity.this);
				builder.setTitle(action.getName());
				builder.setMessage(action.getActionDescription());

				builder.show();

				setResult(RESULT_OK);
				finish();
			} else {
				String message = getErrorString();

				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Item_details_Activity.this);
				builder.setTitle(R.string.logout_failed);
				builder.setMessage(message);

				builder.show();
			}

		}

		private String getErrorString() {
			Context ctx = Item_details_Activity.this;
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

	}

	/**
	 * Formatter for ObjectItems in the Listview
	 */
	private static class ObjectActionFormatter {

		private ObjectAction action;

		public ObjectActionFormatter(ObjectAction action) {
			setAction(action);
		}

		public ObjectAction getAction() {
			return action;
		}

		public void setAction(ObjectAction action) {
			this.action = action;
		}

		@Override
		public String toString() {
			return action.getName();
		}
	}
}
