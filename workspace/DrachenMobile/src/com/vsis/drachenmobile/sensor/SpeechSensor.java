package com.vsis.drachenmobile.sensor;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;

import com.vsis.drachen.sensor.AbstractSensor;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.data.StringSensorData;
import com.vsis.drachenmobile.util.StartForResult_Activity;
import com.vsis.drachenmobile.util.StartForResult_Activity.IOnResultListener;

public class SpeechSensor extends AbstractSensor implements ISensor {

	private Context _ctx;
	private IOnResultListener callback = new IOnResultListener() {
		@Override
		public void onActivityResult(int resultCode, Intent data) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				useData(matches);
			}
		}
	};

	public SpeechSensor(String name, Context ctx) {
		super(name);
		_ctx = ctx;
	}

	@Override
	public void start() {
		resume();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		startIntent();
	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}

	private void startIntent() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

		StartForResult_Activity.startForResult(_ctx, intent, null, callback);
	}

	protected void useData(ArrayList<String> data) {
		long millis = System.currentTimeMillis();
		long nanos = System.nanoTime();
		callListener(new StringSensorData(millis, nanos,
				data.toArray(new String[0])));
	}

}
