package com.vsis.drachenmobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vsis.drachen.model.User;

public class Login_Activity extends Activity {

	private Button btnSignin;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// TODO Auto-generated method stub

		btnLogin = (Button) findViewById(R.id.btnLogin);

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				login();
			}
		});

		btnSignin = (Button) findViewById(R.id.ButtonSignIn);

		btnSignin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				signin();
			}
		});

	}

	LocationManager locationManager;
	LocationListener locationListener;

	// PolyListe polyList;
	// CurrentRegion region;

	Button btnLogin;
	boolean prov = true;

	boolean start = true;

	private void login() {

		String username = ((EditText) findViewById(R.id.editTextUsername))
				.getText().toString();
		String password = ((EditText) findViewById(R.id.editTextPassword))
				.getText().toString();

		Toast.makeText(this, username + "/" + password, Toast.LENGTH_LONG)
				.show();

		LoginTask task = new LoginTask();
		task.execute(username, password);

	}

	class LoginTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog ringProgressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Context ctx = Login_Activity.this;
			ringProgressDialog = ProgressDialog.show(ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.logging_in), true);
			ringProgressDialog.setCancelable(true);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String username = params[0];
			String password = params[1];

			DrachenApplication app = (DrachenApplication) getApplication();
			MyDataSet client = app.getAppData();

			boolean success = client.login(username, password);
			return success;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {

				DrachenApplication app = (DrachenApplication) getApplication();
				User user = app.getAppData().getUser();

				app.startDrachenServices();

				Intent intent = new Intent(Login_Activity.this,
						Main_Activity.class);

				startActivity(intent);
			}
			ringProgressDialog.dismiss();
		}
	};

	protected void signin() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (locationManager != null && locationListener != null)
			locationManager.removeUpdates(locationListener);
	}

}
