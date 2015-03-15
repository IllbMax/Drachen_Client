package com.vsis.drachenmobile.sensor;

import android.content.Context;
import android.content.Intent;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vsis.drachen.sensor.AbstractSensor;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.data.StringSensorData;
import com.vsis.drachenmobile.util.StartForResult_Activity;
import com.vsis.drachenmobile.util.StartForResult_Activity.IOnResultListener;

/**
 * {@link ISensor} that receives Strings from barcodes via the zxing code
 * scanner and converts them to {@link StringSensorData}.
 * 
 */
public class ZXingScannerSensor extends AbstractSensor implements ISensor {

	private Context _ctx;
	private IOnResultListener callback = new IOnResultListener() {
		@Override
		public void onActivityResult(int resultCode, Intent data) {
			// you can use the constant because the check of the right
			// resultCode was checked in the activity
			IntentResult scanResult = IntentIntegrator.parseActivityResult(
					IntentIntegrator.REQUEST_CODE, resultCode, data);
			if (scanResult != null) {
				useData(scanResult.getContents());
			}

		}
	};

	public ZXingScannerSensor(String name, Context ctx) {
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

	/**
	 * start the zxing intent
	 */
	private void startIntent() {
		StartForResult_Activity.startForXZingResult(_ctx, callback);
	}

	/**
	 * Convert the {@link String} data from barcode to {@link StringSensorData}
	 * and calls the listener.
	 * 
	 * @param data
	 *            string from barcode
	 * 
	 */
	protected void useData(String data) {
		long millis = System.currentTimeMillis();
		long nanos = System.nanoTime();
		callListener(new StringSensorData(millis, nanos, data));
	}

}
