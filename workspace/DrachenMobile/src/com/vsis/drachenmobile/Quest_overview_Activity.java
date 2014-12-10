package com.vsis.drachenmobile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.visis.drachen.exception.DrachenBaseException;
import com.visis.drachen.exception.IdNotFoundException;
import com.visis.drachen.exception.InternalProcessException;
import com.visis.drachen.exception.MissingParameterException;
import com.visis.drachen.exception.ObjectRestrictionException;
import com.visis.drachen.exception.QuestTargetNotFinishedException;
import com.visis.drachen.exception.RestrictionException;
import com.vsis.drachen.QuestService;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachenmobile.helper.Helper;
import com.vsis.drachenmobile.task.QuestAbortTaskTemplate;
import com.vsis.drachenmobile.util.ArrayDetailsExpandableListAdapter;

public class Quest_overview_Activity extends Activity {

	ExpandableListView _questListView;
	QuestExpListAdapter _questAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questoverview);

		_questListView = (ExpandableListView) findViewById(R.id.explistView_Quest_overviewQuests);

		_questListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// showQuestdetails(null);
						Object obj = arg0.getItemAtPosition(arg2);
						if (obj instanceof Quest) {
							questMenu((Quest) obj);
						} else {
							// no quest (only detail)
						}
						return true;
					}
				});

		_questAdapter = new QuestExpListAdapter(this,
		// R.layout.listviewitem_questprototype,
				new ArrayList<Quest>());
		_questListView.setAdapter(_questAdapter);

		QuestLoadTask task = new QuestLoadTask();
		task.execute();

	}

	@Override
	protected void onResume() {
		super.onResume();
		_questListView = (ExpandableListView) findViewById(R.id.explistView_Quest_overviewQuests);
		_questAdapter = (QuestExpListAdapter) _questListView
				.getExpandableListAdapter();
	}

	private void finishQuest(Quest quest) {

		QuestFinishTask task = new QuestFinishTask(quest);
		task.execute();

	}

	private void abortQuest(Quest quest) {

		QuestAbortTask task = new QuestAbortTask(quest);
		task.execute();

	}

	private void showQuestdetails(Quest quest) {
		// Toast.makeText(this, "show detail", Toast.LENGTH_LONG).show();

		Intent intent = new Intent(Quest_overview_Activity.this,
				Quest_details_Activity.class);
		intent.putExtra(Quest_details_Activity.EXTRA_QUESTID, quest.getId());
		startActivity(intent);
	}

	private void questMenu(final Quest quest) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		final List<String> items = new ArrayList<String>();
		// action as work around for java's 1.6 lack of function points
		final List<Integer> action = new ArrayList<Integer>();
		if (!quest.isFinished()) {
			if (quest.isFulfilled()) {
				items.add(this.getResources().getString(R.string.finshQuest));
				action.add(0);
			}

			items.add(this.getResources().getString(R.string.abortQuest));
			action.add(1);
		}

		items.add(this.getResources().getString(R.string.details));
		action.add(2);
		builder.setTitle(quest.getName()).setItems(
				items.toArray(new String[] {}),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (action.get(which)) {
						case 0:
							finishQuest(quest);
							break;
						case 1:
							abortQuest(quest);
							break;
						case 2:
							showQuestdetails(quest);
							break;
						}

					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	private class QuestExpListAdapter extends
			ArrayDetailsExpandableListAdapter<Quest> {

		int[] _layouts = { android.R.layout.simple_expandable_list_item_1 };

		public QuestExpListAdapter(Context context, List<Quest> listDataHeader) {
			super(context, listDataHeader);

		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final Quest questPrototype = getGroup(groupPosition);
			// getChild(groupPosition, childPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(_layouts[childPosition],
						null);
			}

			if (childPosition == 0) {

				TextView txtListChild = (TextView) convertView
						.findViewById(android.R.id.text1);
				txtListChild.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
				txtListChild.setText(questPrototype.getDescription());
				return convertView;
			}
			return convertView;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			final Quest quest = getGroup(groupPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.expandlistviewitem_quest_overview, null);
			}
			final TextView textViewName = (TextView) convertView
					.findViewById(R.id.listview_label_Questname);
			final TextView textViewStatus = (TextView) convertView
					.findViewById(R.id.textview_status);

			// Button buttonAbort = (Button) convertView
			// .findViewById(R.id.listview_abortButton);
			// CheckBox sensorOn = (CheckBox) convertView
			// .findViewById(R.id.listview_Quest_checkSonsorOn);

			textViewName.setText(quest.getName());
			textViewStatus.setText(Helper.getProgressDisplayText(
					Quest_overview_Activity.this, quest.isFinished(),
					quest.getProgress()));
			textViewStatus.setTextColor(Helper.getProgressDisplayColor(
					Quest_overview_Activity.this, quest.isFinished(),
					quest.getProgress()));
			// buttonAbort.setFocusable(false);
			// buttonAbort.setVisibility(View.VISIBLE);
			// sensorOn.setFocusable(false);
			// sensorOn.setVisibility(View.INVISIBLE);
			//
			// buttonAbort.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// abortQuest(quest);
			// }
			//
			// });

			return convertView;
		}

		@Override
		protected Object getChild(Quest group, int childPosition) {
			return group.getDescription();
		}

		@Override
		protected int getChildrenCount(Quest group) {
			return 1;
		}
	}

	private class QuestLoadTask extends
			AsyncTask<Void, Void, Collection<Quest>> {

		private ProgressDialog ringProgressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// _prototypeListView available
			_questAdapter.clear();
			Context ctx = Quest_overview_Activity.this;
			ringProgressDialog = ProgressDialog.show(ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.load_Quests), true);
			ringProgressDialog.setCancelable(true);

		}

		@Override
		protected Collection<Quest> doInBackground(Void... params) {
			MyDataSet appData = ((DrachenApplication) getApplication())
					.getAppData();
			QuestService questService = appData.getQuestService();

			Collection<Quest> result = questService.getUserQuests();
			return result;
		}

		@Override
		protected void onPostExecute(Collection<Quest> result) {
			super.onPostExecute(result);
			_questAdapter.addAll(result);
			_questAdapter.notifyDataSetChanged();
			ringProgressDialog.dismiss();

		}
	}

	private class QuestAbortTask extends QuestAbortTaskTemplate {

		public QuestAbortTask(Quest quest) {
			super(Quest_overview_Activity.this, quest,
					((DrachenApplication) getApplication()).getAppData());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// nothing more to do
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result != null) {
				_questAdapter.removeGroup(quest);
				_questAdapter.notifyDataSetChanged();

			} else {
				this.showAlertExceptionDialog();
			}
		}
	}

	private class QuestFinishTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog ringProgressDialog;
		private Quest quest;
		private DrachenBaseException _exception;

		public QuestFinishTask(Quest quest) {
			this.quest = quest;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Context ctx = Quest_overview_Activity.this;
			ringProgressDialog = ProgressDialog.show(
					ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.finishing_quest) + ": "
							+ quest.getName(), true);
			ringProgressDialog.setCancelable(true);

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			MyDataSet appData = ((DrachenApplication) getApplication())
					.getAppData();
			QuestService questService = appData.getQuestService();

			try {
				boolean result = questService.finishQuest(quest.getId());
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
			if (result) {
				_questAdapter.removeGroup(quest);
				_questAdapter.notifyDataSetChanged();

			} else {
				showAlertExceptionDialog();
			}
		}

		/**
		 * shows an {@link AlertDialog} with information of the
		 * {@link DrachenBaseException}
		 */
		protected void showAlertExceptionDialog() {
			Context ctx = Quest_overview_Activity.this;
			String message = getErrorString();

			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setTitle(R.string.quest_finishing_failed);
			builder.setMessage(message);
			builder.show();
		}

		private String getErrorString() {
			Context ctx = Quest_overview_Activity.this;
			String message = ctx.getString(R.string.please_try_again);

			if (_exception != null) {

				if (_exception instanceof QuestTargetNotFinishedException) {
					QuestTargetNotFinishedException e = (QuestTargetNotFinishedException) _exception;
					MyDataSet appData = ((DrachenApplication) getApplication())
							.getAppData();
					QuestService questService = appData.getQuestService();
					Quest quest = questService.getQuestFromId(e.getQuestId());
					QuestTarget questTarget = null;
					if (quest != null)
						questTarget = quest.getQuestTargetFromId(e
								.getQuestTargetId());
					if (quest != null && questTarget != null)
						message = ctx
								.getString(R.string.questtarget_s_not_success);
					else {
						message = ctx.getString(R.string.grats_bug);
						message += "\n";
						message = ctx
								.getString(R.string.questtarget_d_not_suc_not_found);
					}
				} else if (_exception instanceof ObjectRestrictionException) {
					// ObjectRestrictionException e =
					// (ObjectRestrictionException) _exception;
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

}
