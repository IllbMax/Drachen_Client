package com.vsis.drachenmobile;

import android.app.Activity;
import android.os.Bundle;

import com.visis.drachen.sensor.ISensor;
import com.visis.drachen.sensor.SensorType;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.model.quest.Quest;

public class Sensor_details_Activity extends Activity {

	public static final String EXTRA_SENSORTYPE = "SensorType";

	SensorType _sensorType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensordetail);

		int typeOrdinal = getIntent().getExtras().getInt(EXTRA_SENSORTYPE);
		_sensorType = SensorType.values()[typeOrdinal];

		displaySensor();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("questId", _sensorType.ordinal());
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		_sensorType = SensorType.values()[savedInstanceState.getInt("questId")];
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void displaySensor() {
		DrachenApplication app = (DrachenApplication) getApplication();
		SensorService sensorService = app.getAppData().getSensorService();
		ISensor sensor = sensorService.getDefaultSensor(_sensorType);
		displaySensor(sensor);
	}

	private void displaySensor(final ISensor sensor) {

		if (sensor == null) {
			// TODO: find new sensor
		} else {
			// TODO: display something
		}
	}

	protected void trackQuest(Quest quest) {
		DrachenApplication app = (DrachenApplication) getApplication();
		app.getAppData().getSensorService().trackQuest(quest);

	}

}
