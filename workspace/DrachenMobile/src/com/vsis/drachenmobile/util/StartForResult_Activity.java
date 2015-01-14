package com.vsis.drachenmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import com.vsis.drachenmobile.R;

public class StartForResult_Activity extends Activity {

	private static final String EXTRA_INTENT = "startForResult.intent";
	private static final String EXTRA_BUNDLE = "startForResult.options";
	private static final String EXTRA_CALLBACK = "startForResult.callback";
	private static final int RESULTCODE = 0;
	private static int newId = 0;

	private static SparseArray<IOnResultListener> callback_map = new SparseArray<IOnResultListener>();

	public static interface IOnResultListener {
		void onActivityResult(int resultCode, Intent data);
	}

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

	private IOnResultListener listener;
	private int listener_id;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forresult);

		Intent intent = getIntent().getExtras().getParcelable(EXTRA_INTENT);
		Bundle bundle = getIntent().getExtras().getBundle(EXTRA_BUNDLE);
		listener_id = getIntent().getExtras().getInt(EXTRA_CALLBACK);
		listener = callback_map.get(listener_id);
		startActivityForResult(intent, RESULTCODE, bundle);

	}

	@Override
	public void onDestroy() {
		if (isFinishing())
			callback_map.remove(listener_id);
		super.onDestroy();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULTCODE) {
			listener.onActivityResult(resultCode, data);
		}
		finish();
	}
}
