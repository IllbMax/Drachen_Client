package com.vsis.drachenmobile.task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.vsis.drachen.QuestService;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.IdNotFoundException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachen.exception.QuestStartException;
import com.vsis.drachen.exception.RestrictionException;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestPrototype;
import com.vsis.drachenmobile.MyDataSet;
import com.vsis.drachenmobile.R;

/**
 * defines an AsyncTask for stating quests . it'll create a
 * {@link ProgressDialog} for the execution
 * 
 * you need to override onPostExecute and call the super method, you can call
 * showAlertExceptionDialog to show an {@link AlertDialog} for details of the
 * exception
 * 
 */

public class QuestStartTaskTemplate extends AsyncTask<Void, Void, Quest> {

	protected ProgressDialog ringProgressDialog;
	protected QuestPrototype questPrototype;
	protected Context ctx;
	protected MyDataSet appData;
	protected DrachenBaseException _exception;

	public QuestStartTaskTemplate(Context ctx, QuestPrototype questPrototype,
			MyDataSet appData) {
		this.questPrototype = questPrototype;
		this.ctx = ctx;
		this.appData = appData;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		ringProgressDialog = ProgressDialog.show(
				ctx,
				ctx.getString(R.string.please_wait_),
				ctx.getString(R.string.starting_quest) + ": "
						+ questPrototype.getName(), true);
		ringProgressDialog.setCancelable(true);

	}

	@Override
	protected Quest doInBackground(Void... params) {

		QuestService questService = appData.getQuestService();
		SensorService sensorService = appData.getSensorService();

		try {
			Quest result = questService.startQuest(questPrototype.getId());
			sensorService.trackQuest(result);
			return result;

		} catch (DrachenBaseException e) {
			_exception = e;
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Quest result) {
		super.onPostExecute(result);

		ringProgressDialog.dismiss();
	}

	/**
	 * shows an {@link AlertDialog} with information of the
	 * {@link DrachenBaseException}
	 */
	protected void showAlertExceptionDialog() {
		String message = getErrorString();

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(R.string.start_quest_failed);
		builder.setMessage(message);
		builder.show();
	}

	private String getErrorString() {
		String message = ctx.getString(R.string.please_try_again);

		if (_exception != null) {

			if (_exception instanceof QuestStartException) {
				QuestStartException e = (QuestStartException) _exception;
				switch (e.getType()) {
				case NotQualified:
					message = ctx.getString(R.string.quest_not_qualified);
					break;
				case NotRepeatable:
					message = ctx.getString(R.string.quest_cannot_repeated);
					break;
				case StillOnGoing:
					message = ctx.getString(R.string.quest_still_ongoing);
					break;
				default:
					break;

				}
			} else if (_exception instanceof MissingParameterException) {
				MissingParameterException e = (MissingParameterException) _exception;
				message = ctx.getString(R.string.missing_parameter_s,
						e.getParameter());
			} else if (_exception instanceof IdNotFoundException) {
				IdNotFoundException e = (IdNotFoundException) _exception;
				message = ctx.getString(R.string.id_not_found_parameter_s,
						e.getParameter());
			} else if (_exception instanceof RestrictionException) {
				// RestrictionException e = (RestrictionException)
				// _exception;
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