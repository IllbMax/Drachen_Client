package com.vsis.drachenmobile.task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.visis.drachen.exception.DrachenBaseException;
import com.visis.drachen.exception.IdNotFoundException;
import com.visis.drachen.exception.InternalProcessException;
import com.visis.drachen.exception.MissingParameterException;
import com.visis.drachen.exception.ObjectRestrictionException;
import com.visis.drachen.exception.QuestAbortException;
import com.visis.drachen.exception.RestrictionException;
import com.vsis.drachen.QuestService;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachenmobile.MyDataSet;
import com.vsis.drachenmobile.R;

/**
 * defines an AsyncTask for quest abortion. it'll create a
 * {@link ProgressDialog} for the execution
 * 
 * you need to override onPostExecute and call the super method, you can call
 * showAlertExceptionDialog to show an {@link AlertDialog} for details of the
 * exception
 * 
 */
public class QuestAbortTaskTemplate extends AsyncTask<Void, Void, Boolean> {

	protected ProgressDialog ringProgressDialog;
	protected Quest quest;
	protected Context ctx;
	protected MyDataSet appData;
	protected DrachenBaseException _exception;

	public QuestAbortTaskTemplate(Context ctx, Quest quest, MyDataSet appData) {
		this.quest = quest;
		this.ctx = ctx;
		this.appData = appData;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		ringProgressDialog = ProgressDialog
				.show(ctx,
						ctx.getString(R.string.please_wait_),
						ctx.getString(R.string.aborting_quest) + ": "
								+ quest.getName(), true);
		ringProgressDialog.setCancelable(true);

	}

	@Override
	protected Boolean doInBackground(Void... params) {

		QuestService questService = appData.getQuestService();
		SensorService sensorService = appData.getSensorService();

		try {
			boolean result = questService.abortQuest(quest.getId());
			if (result)
				sensorService.untrackQuest(quest);
			return result;
		} catch (DrachenBaseException e) {
			_exception = e;
			e.printStackTrace();
			return false;
		}

	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		ringProgressDialog.dismiss();
	}

	/**
	 * shows an {@link AlertDialog} with information of the
	 * {@link DrachenBaseException}
	 */
	protected void showAlertExceptionDialog() {
		String message = getErrorString();

		ringProgressDialog.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(R.string.quest_abortion_failed);
		builder.setMessage(message);
		builder.show();
	}

	private String getErrorString() {
		String message = ctx.getString(R.string.please_try_again);

		if (_exception != null) {

			if (_exception instanceof QuestAbortException) {
				// QuestAbortException e = (QuestAbortException) _exception;
				message = ctx.getString(R.string.quest_already_finished);
			} else if (_exception instanceof ObjectRestrictionException) {
				// ObjectRestrictionException e = (ObjectRestrictionException)
				// _exception;
				message = ctx.getString(R.string.quest_not_yours);
			} else if (_exception instanceof IdNotFoundException) {
			} else if (_exception instanceof MissingParameterException) {
				MissingParameterException e = (MissingParameterException) _exception;
				message = ctx.getString(R.string.missing_parameter_s,
						e.getParameter());
			} else if (_exception instanceof IdNotFoundException) {
				IdNotFoundException e = (IdNotFoundException) _exception;
				message = ctx.getString(R.string.id_not_found_parameter_s,
						e.getParameter());
			} else if (_exception instanceof RestrictionException) {
				InternalProcessException e = (InternalProcessException) _exception;
				message = ctx.getString(R.string.access_denied);
			} else if (_exception instanceof InternalProcessException) {
				InternalProcessException e = (InternalProcessException) _exception;
				message = ctx.getString(R.string.internal_process_error,
						e.getMessage());
			}
		}
		return message;
	}

}
