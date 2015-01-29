package com.vsis.drachenmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.InvalidParameterException;
import com.vsis.drachen.exception.InvalidParameterException.InvalidType;
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachenmobile.helper.Helper;

public class Register_Activity extends Activity {

	private Button btnSignin;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		btnSignin = (Button) findViewById(R.id.ButtonSignIn);

		btnSignin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				signin();
			}
		});

	}

	protected void signin() {
		String username = ((EditText) findViewById(R.id.editTextUsername))
				.getText().toString();
		String password = ((EditText) findViewById(R.id.editTextPassword))
				.getText().toString();
		String displayName = ((EditText) findViewById(R.id.editTextDisplayName))
				.getText().toString();

		if (username.length() < 3 || password.length() < 3
				|| displayName.length() < 3) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					Register_Activity.this);
			builder.setTitle(R.string.register_failed);
			builder.setMessage("You need to insert at least 3 characters.");
			builder.show();
		} else {
			RegisterTask task = new RegisterTask();
			task.execute(username, password, displayName);
		}
	}

	public void registerSuccess() {
		String username = ((EditText) findViewById(R.id.editTextUsername))
				.getText().toString();
		String password = ((EditText) findViewById(R.id.editTextPassword))
				.getText().toString();

		Intent returnIntent = new Intent();
		returnIntent.putExtra("username", username);
		returnIntent.putExtra("password", password);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	class RegisterTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog ringProgressDialog;
		private DrachenBaseException _exception = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Context ctx = Register_Activity.this;
			ringProgressDialog = ProgressDialog.show(ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.perform_register), true);
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
			String displayName = params[2];

			DrachenApplication app = (DrachenApplication) getApplication();
			MyDataSet client = app.getAppData();

			try {
				boolean success = client.registerUser(username, password,
						displayName);
				return success;
			} catch (DrachenBaseException e) {
				_exception = e;
			}
			return null;
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

				ringProgressDialog.dismiss();

				registerSuccess();

			} else {
				String message = getErrorString();

				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Register_Activity.this);
				builder.setTitle(R.string.register_failed);
				builder.setMessage(message);
				builder.show();
			}

		}

		private String getErrorString() {
			Context ctx = Register_Activity.this;
			String message = ctx.getString(R.string.please_try_again);

			if (_exception != null) {
				if (_exception instanceof MissingParameterException) {
					MissingParameterException e = (MissingParameterException) _exception;
					message = ctx.getString(R.string.missing_parameter_s,
							e.getParameter());
				} else if (_exception instanceof InvalidParameterException) {
					message = getErrorStringForInvalidParameter(ctx);
				} else if (_exception instanceof InternalProcessException) {
					InternalProcessException e = (InternalProcessException) _exception;
					message = ctx.getString(R.string.internal_process_error,
							e.getMessage());
				}
			}
			return message;
		}

		private String getErrorStringForInvalidParameter(Context ctx) {
			InvalidParameterException e = (InvalidParameterException) _exception;
			if (e.getType() == InvalidType.NotUnique)
				return ctx.getString(R.string.username_already_used);
			else
				return Helper.getErrorStringForInvalidParameter(ctx, e);

		}

	};

}
