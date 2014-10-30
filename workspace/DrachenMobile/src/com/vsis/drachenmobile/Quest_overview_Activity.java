package com.vsis.drachenmobile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vsis.drachen.QuestService;
import com.vsis.drachen.model.quest.Quest;
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
							Quest quest = (Quest) obj;
							showQuestdetails(quest);
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

	private void abortQuest(Quest quest) {

		QuestAbortTask task = new QuestAbortTask(quest);
		task.execute();

		Toast.makeText(this, "clicked " + quest.getId(), Toast.LENGTH_SHORT)
				.show();
	}

	private void showQuestdetails(Quest quest) {
		// Toast.makeText(this, "show detail", Toast.LENGTH_LONG).show();

		Intent intent = new Intent(Quest_overview_Activity.this,
				Quest_details_Activity.class);
		intent.putExtra(Quest_details_Activity.EXTRA_QUESTID, quest.getId());
		startActivity(intent);
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
			Button buttonAbort = (Button) convertView
					.findViewById(R.id.listview_abortButton);
			CheckBox sensorOn = (CheckBox) convertView
					.findViewById(R.id.listview_Quest_checkSonsorOn);

			textViewName.setText(quest.getName());
			buttonAbort.setFocusable(false);
			buttonAbort.setVisibility(View.VISIBLE);
			sensorOn.setFocusable(false);
			sensorOn.setVisibility(View.INVISIBLE);

			buttonAbort.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					abortQuest(quest);
				}

			});

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

	private class QuestAbortTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog ringProgressDialog;
		private Quest quest;

		public QuestAbortTask(Quest quest) {
			this.quest = quest;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Context ctx = Quest_overview_Activity.this;
			ringProgressDialog = ProgressDialog.show(
					ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.aborting_quest) + ": "
							+ quest.getName(), true);
			ringProgressDialog.setCancelable(true);

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			MyDataSet appData = ((DrachenApplication) getApplication())
					.getAppData();
			QuestService questService = appData.getQuestService();

			boolean result = questService.abortQuest(quest.getId());

			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result != null) {
				_questAdapter.removeGroup(quest);
				_questAdapter.notifyDataSetChanged();

			} else {
				// TODO: error: no Quest aborted
			}

			ringProgressDialog.dismiss();
		}
	}

}
