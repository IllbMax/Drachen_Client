package com.vsis.drachenmobile;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton.OnValueChangedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.visis.drachen.sensor.ISensor;
import com.visis.drachen.sensor.SensorType;
import com.vsis.drachen.SensorService;

public class SensorManager_Activity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sensormenu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.action_refresh:
			Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT).show();
			break;
		// action with ID action_settings was selected
		case R.id.action_settings:
			Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}

		return true;
	}

	ListView listView;

	SensorTypeArrayAdapter arrayAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensormanager);

		listView = (ListView) findViewById(R.id.lstview_sensorTypes);
		ArrayList<SensorType> arrayList = new ArrayList<SensorType>();
		for (SensorType t : SensorType.values())
			arrayList.add(t);
		arrayAdapter = new SensorTypeArrayAdapter(getApplicationContext(),
				arrayList);
		listView.setAdapter(arrayAdapter);

		// LETS HIGHLIGHT SELECTED ITEMS

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long itemId) {

				/*
				 * when we click on item on list view we can get it catch item
				 * here. so view is the item clicked in list view and position
				 * is the position of that item in list view which was clicked.
				 * 
				 * Now that we know which item is click we can easily change the
				 * color of text but when we click on next item we we have to
				 * deselect the old selected item means recolor it back to
				 * default , and then hight the new selected item by coloring it
				 * .
				 * 
				 * So here's the code of doing it.
				 */

				CheckedTextView textView = (CheckedTextView) view;
				for (int i = 0; i < listView.getCount(); i++) {
					textView = (CheckedTextView) listView.getChildAt(i);
					if (textView != null) {
						textView.setTextColor(Color.WHITE);
					}

				}
				listView.invalidate();
				textView = (CheckedTextView) view;
				if (textView != null) {
					textView.setTextColor(Color.BLUE);
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SensorManager_Activity.this);

				builder.setTitle("title").setItems(
						new String[] { "ab", "cd", "qwe", "kkl" },
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// The 'which' argument contains the index
								// position
								// of the selected item

							}
						});
				// return builder.create();
				// AlertDialog alert = new
				// AlertDialog(SensorManager_Activity.this);
				AlertDialog alert = builder.create();
				alert.show();

				// if (click) {
				// popUp.showAtLocation(listView, Gravity.BOTTOM, 10, 10);
				//
				// popUp.update(50, 50, 300, 80);
				// click = false;
				// } else {
				// popUp.dismiss();
				// click = true;
				// }

			}
		});
		setPopUp();
	}

	PopupWindow popUp;
	LinearLayout layout;
	TextView tv;
	LayoutParams params;
	// LinearLayout mainLayout;
	Button but;
	boolean click = true;

	private void setPopUp() {
		popUp = new PopupWindow(this);
		layout = new LinearLayout(this);
		// mainLayout = new LinearLayout(this);
		tv = new TextView(this);
		but = new Button(this);
		but.setText("Click Me");
		but.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (click) {
					popUp.showAtLocation(listView, Gravity.BOTTOM, 10, 10);
					popUp.update(50, 50, 300, 80);
					click = false;
				} else {
					popUp.dismiss();
					click = true;
				}
			}

		});
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		tv.setText("Hi this is a sample text for popup window");
		layout.addView(tv, params);
		popUp.setContentView(layout);
		// popUp.showAtLocation(layout, Gravity.BOTTOM, 10, 10);
		// mainLayout.addView(but, params);
		// setContentView(mainLayout);
	}

	private void showDetails(SensorType item) {
		Intent intent = new Intent(this, Sensor_details_Activity.class);
		intent.putExtra(Sensor_details_Activity.EXTRA_SENSORTYPE,
				item.ordinal());
		startActivity(intent);
	}

	private class SensorTypeArrayAdapter extends ArrayAdapter<SensorType> {

		Map<SensorType, Integer> mIdMap = new EnumMap<SensorType, Integer>(
				SensorType.class);

		public SensorTypeArrayAdapter(Context context, List<SensorType> objects) {
			super(context, R.layout.listviewitem_sensortype, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) SensorManager_Activity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.listviewitem_sensortype, parent, false);
			}
			TextView textViewSensorType = (TextView) convertView
					.findViewById(R.id.textView_SensorType);
			TextView textViewSensorName = (TextView) convertView
					.findViewById(R.id.textView_SelectedSensor);
			final TextView textViewStatus = (TextView) convertView
					.findViewById(R.id.textView_SensorStatus);
			Button butttonDetails = (Button) convertView
					.findViewById(R.id.button_details);
			final MultiStateToggleButton stateButton = (MultiStateToggleButton) convertView
					.findViewById(R.id.mstb_multi_id);

			SensorService sensorService = ((DrachenApplication) SensorManager_Activity.this
					.getApplication()).getAppData().getSensorService();
			SensorType type = getItem(position);
			final ISensor sensor = sensorService.getDefaultSensor(type);

			textViewSensorType.setText(type.toString());
			if (sensor == null) {
				// DO something
				textViewSensorName.setText(" --- ");

			} else {
				textViewSensorName.setText(sensor.getName());

			}
			showStatus(sensor, stateButton, textViewStatus);
			butttonDetails.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDetails(getItem(position));
				}

			});
			stateButton.setOnValueChangedListener(new OnValueChangedListener() {
				boolean dontset = false;

				@Override
				public void onValueChanged(int value) {
					if (sensor == null || !sensor.isAvailable())
						return;
					if (dontset) {

						dontset = false;
					} else {
						if (value == 0) // stop
							sensor.stop();
						else if (value == 1) // pause
							sensor.pause();
						else if (value == 2) // start
							sensor.start();

						dontset = true;
						showStatus(sensor, stateButton, textViewStatus);
						dontset = false;
					}

					// TODO: if start()/stope() gets async do something

				}
			});
			return convertView;
		}

		private void showStatus(ISensor sensor,
				MultiStateToggleButton stateButton, TextView textViewStatus) {
			if (sensor == null) {
				textViewStatus.setText(" --- ");
				stateButton.setVisibility(View.GONE);

			} else {
				stateButton.setVisibility(sensor.isAvailable() ? View.VISIBLE
						: View.INVISIBLE);
				// TODO: use resources for Enum (SensorType) naming
				// TODO: define resource for SensorStatus
				String status = "";
				if (!sensor.isAvailable()) {
					status = "not available";
				} else if (sensor.isStopped()) {
					status = "stopped";
					stateButton.setValue(0);
				} else if (sensor.isPaused()) {
					status = "paused";
					stateButton.setValue(1);
				} else {
					status = "running";
					stateButton.setValue(2);
				}
				textViewStatus.setText(status);
			}
		}

		public long getItemId(int position) {
			SensorType item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

}
