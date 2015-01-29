package com.vsis.drachenmobile.sensor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.WindowManager;
import android.widget.EditText;

import com.vsis.drachen.sensor.AbstractSensor;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.data.StringSensorData;
import com.vsis.drachenmobile.R;

public class StringInputSensor extends AbstractSensor implements ISensor {

	private Context _ctx;

	public StringInputSensor(String name, Context ctx) {
		super(name);
		this._ctx = ctx;
	}

	@Override
	public void start() {
		startIntent();
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

		final EditText input = new EditText(_ctx);
		AlertDialog.Builder builder = new AlertDialog.Builder(_ctx);

		builder.setView(input); // set textbox
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						final Editable string = input.getText();
						// call the listener in separated thread,
						// cause this could be called by UI Thread
						new Thread(new Runnable() {
							@Override
							public void run() {
								useData(string);
							}
						}).start();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

	}

	protected void useData(CharSequence data) {
		long millis = System.currentTimeMillis();
		long nanos = System.nanoTime();
		callListener(new StringSensorData(millis, nanos, data.toString()));
	}

}
