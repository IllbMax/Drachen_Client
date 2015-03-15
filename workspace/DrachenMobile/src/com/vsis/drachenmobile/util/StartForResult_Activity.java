package com.vsis.drachenmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import com.google.zxing.integration.android.IntentIntegrator;
import com.vsis.drachenmobile.R;

/**
 * 
 * Hack class to start a {@link Activity} for a result. It simulates the work of
 * {@link Activity#startActivityForResult(Intent, int, Bundle)} but callable
 * from other classes than Activity (with a valid {@link Context})
 */
public class StartForResult_Activity extends Activity {

	private static final String EXTRA_INTENT = "startForResult.intent";
	private static final String EXTRA_BUNDLE = "startForResult.options";
	private static final String EXTRA_CALLBACK = "startForResult.callback";
	private static final String EXTRA_ZXING = "startForResult.zxing";
	private static final int RESULTCODE = 0;
	private static int newId = 0;

	private static SparseArray<IOnResultListener> callback_map = new SparseArray<IOnResultListener>();

	public static interface IOnResultListener {
		void onActivityResult(int resultCode, Intent data);
	}

	/**
	 * Start the Activity defined in intent for result and calls callback
	 * 
	 * @param ctx
	 *            Context in which the activity will be started
	 * @param intent
	 *            intent of the desired activity
	 * @param options
	 *            options for the activity call
	 * @param callback
	 *            {@link IOnResultListener} that will be called with the result
	 *            of the intent's {@link Activity}
	 * 
	 * @see Activity#startActivityForResult(Intent, int, Bundle)
	 */
	public static synchronized void startForResult(Context ctx, Intent intent,
			Bundle options, IOnResultListener callback) {
		Intent hack = new Intent(ctx, StartForResult_Activity.class);

		hack.putExtra(EXTRA_INTENT, intent);
		hack.putExtra(EXTRA_BUNDLE, options);
		hack.putExtra(EXTRA_CALLBACK, newId);
		hack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		callback_map.put(newId, callback);
		newId++;

		ctx.startActivity(hack);
	}

	/**
	 * Starts the XZing Scanner-Activity for result
	 * 
	 * @param ctx
	 *            Context in which the scanner be be started
	 * @param callback
	 *            {@link IOnResultListener} that will be called with the result
	 *            of the scanner
	 */
	public static synchronized void startForXZingResult(Context ctx,
			IOnResultListener callback) {
		Intent hack = new Intent(ctx, StartForResult_Activity.class);

		hack.putExtra(EXTRA_ZXING, true);
		hack.putExtra(EXTRA_CALLBACK, newId);
		hack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		callback_map.put(newId, callback);
		newId++;

		ctx.startActivity(hack);
	}

	private IOnResultListener listener;
	private int listener_id;
	private boolean zxing = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forresult);
		// layout: invisible Activity

		zxing = getIntent().getExtras().getBoolean(EXTRA_ZXING, false);
		listener_id = getIntent().getExtras().getInt(EXTRA_CALLBACK);
		listener = callback_map.get(listener_id);

		// do a specific task for zxing scanner
		if (zxing) {
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();

		} else {
			Intent intent = getIntent().getExtras().getParcelable(EXTRA_INTENT);
			Bundle bundle = getIntent().getExtras().getBundle(EXTRA_BUNDLE);
			startActivityForResult(intent, RESULTCODE, bundle);
		}
	}

	@Override
	public void onDestroy() {
		if (isFinishing())
			callback_map.remove(listener_id);
		super.onDestroy();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (zxing) {
			if (requestCode == IntentIntegrator.REQUEST_CODE) {
				listener.onActivityResult(resultCode, data);
			}
		} else if (requestCode == RESULTCODE) {
			listener.onActivityResult(resultCode, data);
		}
		finish(); // make sure to end the activity after getting the (maybe bad
					// result)
	}
}
