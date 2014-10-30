package com.vsis.drachenmobile.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartServicesReciver extends BroadcastReceiver {
	public StartServicesReciver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("receiver", "Got message: " + "start services");
		Intent service = new Intent(context, LocationLocalService.class);
		context.startService(service);
		// TODO: start other services
	}
}
