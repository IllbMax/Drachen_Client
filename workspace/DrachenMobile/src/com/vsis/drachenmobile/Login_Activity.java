package com.vsis.drachenmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.visis.drachen.exception.DrachenBaseException;
import com.visis.drachen.exception.InternalProcessException;
import com.visis.drachen.exception.MissingParameterException;
import com.vsis.drachen.model.User;
import com.vsis.drachenmobile.settings.ConnectionSettingsActivity;

public class Login_Activity extends Activity {

	private Button btnSignin;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.loginmenu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.action_pref:
			Intent intent = new Intent(this, ConnectionSettingsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

		return true;
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

		LoginTask task = new LoginTask();
		task.execute(username, password);
	}

	private void signin() {
		Intent intent = new Intent(this, Register_Activity.class);

		startActivityForResult(intent, 1);
	}

	class LoginTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog ringProgressDialog;
		private DrachenBaseException _exception = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Context ctx = Login_Activity.this;
			ringProgressDialog = ProgressDialog.show(ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.logging_in), true);
			ringProgressDialog.setCancelable(true);
			ringProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// actually could set running = false; right here, but I'll
					// stick to contract.
					boolean success = cancel(true);
				}
			});
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String username = params[0];
			String password = params[1];

			DrachenApplication app = (DrachenApplication) getApplication();
			MyDataSet client = app.getAppData();

			try {
				boolean success = client.login(username, password);
				return success;
			} catch (DrachenBaseException e) {
				_exception = e;
				return null;
			}

		}

		@Override
		protected void onCancelled(Boolean result) {
			super.onCancelled(result);
			// TODO evtl clean up
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result != null && result) {

				DrachenApplication app = (DrachenApplication) getApplication();
				User user = app.getAppData().getUser();

				app.startDrachenServices();

				Intent intent = new Intent(Login_Activity.this,
						Main_Activity.class);

				startActivity(intent);
				ringProgressDialog.dismiss();
			} else {

				String message = getErrorString();

				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Login_Activity.this);
				builder.setTitle(R.string.login_failed);
				builder.setMessage(message);
				builder.show();
			}
		}

		private String getErrorString() {
			Context ctx = Login_Activity.this;
			String message = ctx.getString(R.string.wrong_password);

			if (_exception != null) {
				if (_exception instanceof MissingParameterException) {
					MissingParameterException e = (MissingParameterException) _exception;
					message = ctx.getString(R.string.missing_parameter_s,
							e.getParameter());
				} else if (_exception instanceof InternalProcessException) {
					InternalProcessException e = (InternalProcessException) _exception;
					message = ctx.getString(R.string.internal_process_error,
							e.getMessage());
				}
			}
			return message;
		}

	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (locationManager != null && locationListener != null)
			locationManager.removeUpdates(locationListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				String username = data.getStringExtra("username");
				String password = data.getStringExtra("password");

				((EditText) findViewById(R.id.editTextUsername))
						.setText(username);
				((EditText) findViewById(R.id.editTextPassword))
						.setText(password);
			}
			if (resultCode == RESULT_CANCELED) {

			}
		}
	}
}
