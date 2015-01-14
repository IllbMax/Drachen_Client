package com.vsis.drachenmobile;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vsis.drachen.SensorService;
import com.vsis.drachen.sensor.ISensor;

public class SensorQuickSelect_Activity extends Activity {

	public static interface IOnActivityResult {
		void onActivityResult(int requestCode, int resultCode, Intent data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.setFinishOnTouchOutside(false);
		setContentView(R.layout.dialog_selectsensor);

		ListView sensorListView = (ListView) findViewById(R.id.listView1);
		DrachenApplication application = (DrachenApplication) getApplication();
		SensorService sensorService = application.getAppData()
				.getSensorService();

		List<ISensor> sensors = sensorService.getQuickAccessSensors();

		ArrayAdapter<ISensor> sensorAdpater = new ArrayAdapter<ISensor>(this,
				android.R.layout.simple_list_item_1, sensors);
		sensorListView.setAdapter(sensorAdpater);
		sensorListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ISensor sensor = (ISensor) parent.getItemAtPosition(position);
				sensor.start();
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: send the sensors the signal
		super.onActivityResult(requestCode, resultCode, data);
	}
}
