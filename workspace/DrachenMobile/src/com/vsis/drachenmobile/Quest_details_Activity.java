package com.vsis.drachenmobile;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.visis.drachen.sensor.SensorType;
import com.vsis.drachen.QuestService;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachenmobile.util.ArrayDetailsExpandableListAdapter;

public class Quest_details_Activity extends Activity {

	public static final String EXTRA_QUESTID = "questId";

	int _questId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questdetails);

		_questId = getIntent().getExtras().getInt(EXTRA_QUESTID);

		displayQuest();

	}

	@Override
	protected void onResume() {
		super.onResume();

		// _prototypeListView = (ListView) findViewById(R.id.listview);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("questId", _questId);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		_questId = savedInstanceState.getInt("questId");
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void displayQuest() {
		DrachenApplication app = (DrachenApplication) getApplication();
		QuestService questService = app.getAppData().getQuestService();
		Quest quest = questService.getQuestFromId(_questId);

		displayQuest(quest);
	}

	private void displayQuest(final Quest quest) {

		if (quest == null) {
			// TODO: close this activity
		} else {
			TextView textView_name = (TextView) findViewById(R.id.textview_Quest_Name);
			TextView textView_desc = (TextView) findViewById(R.id.textview_Quest_Description);
			Button buttonAbort = (Button) findViewById(R.id.listview_QuestDetails_AbortButton);

			ExpandableListView listView_QuestTargets = (ExpandableListView) findViewById(R.id.explistView_Quest_Questtargets);

			textView_name.setText(quest.getName());
			textView_desc.setText(quest.getDescription());
			buttonAbort.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO: mach es besser
					// abortQuest(quest);
					trackQuest(quest);
				}
			});

			QuestTargetExpListAdapter adapter = new QuestTargetExpListAdapter(
					this, quest.getQuestTargets());
			listView_QuestTargets.setAdapter(adapter);

		}
	}

	protected void trackQuest(Quest quest) {
		DrachenApplication app = (DrachenApplication) getApplication();
		app.getAppData().getSensorService().trackQuest(quest);

	}

	private void abortQuest(Quest quest) {

		QuestAbortTask task = new QuestAbortTask(quest);
		task.execute();
	}

	private class QuestTargetExpListAdapter extends
			ArrayDetailsExpandableListAdapter<QuestTarget> {

		int[] _layouts = { android.R.layout.simple_expandable_list_item_1 };

		public QuestTargetExpListAdapter(Context context,
				List<QuestTarget> listDataHeader) {
			super(context, listDataHeader);

		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final QuestTarget questTarget = getGroup(groupPosition);
			// getChild(groupPosition, childPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(_layouts[childPosition],
						null);
			}

			SensorType sensor = (SensorType) getChild(questTarget,
					childPosition);
			if (sensor != null) {

				TextView txtListChild = (TextView) convertView
						.findViewById(android.R.id.text1);
				txtListChild.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
				txtListChild.setText(sensor.name());

				SensorService sensorService = ((DrachenApplication) getApplication())
						.getAppData().getSensorService();
				boolean available = sensorService.sensorAvailable(sensor);
				boolean running = sensorService.sensorRunning(sensor);
				if (!available)

					txtListChild.setTextColor(getResources().getColor(
							android.R.color.holo_orange_dark));
				else if (running)
					txtListChild.setTextColor(getResources().getColor(
							android.R.color.holo_green_dark));
				else
					txtListChild.setTextColor(getResources().getColor(
							android.R.color.holo_blue_light));

				return convertView;
			}
			return convertView;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			final QuestTarget questTarget = getGroup(groupPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.expandlistviewitem_quest_overview, null);
			}
			final TextView textViewName = (TextView) convertView
					.findViewById(R.id.listview_label_Questname);
			Button butttonAbort = (Button) convertView
					.findViewById(R.id.listview_abortButton);
			CheckBox checkBox = (CheckBox) convertView
					.findViewById(R.id.listview_Quest_checkSonsorOn);

			textViewName.setText(questTarget.getName());
			butttonAbort.setFocusable(false);
			checkBox.setFocusable(false);
			checkBox.setChecked(questTarget.isFulfilled());
			butttonAbort.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO: start sensors
				}

			});

			return convertView;
		}

		@Override
		protected Object getChild(QuestTarget group, int childPosition) {
			int i = 0;
			SensorType sensor = null;
			for (SensorType s : group.requiredSensors())
				if (i == childPosition) {
					sensor = s;
					break;
				}
			return sensor;
			// TODO: get Realname not Enumname
		}

		@Override
		protected int getChildrenCount(QuestTarget group) {
			return group.requiredSensors().size();
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

			Context ctx = Quest_details_Activity.this;
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
				// TODO: close this activity after quest abortion

			} else {
				// TODO: error: no Quest aborted
			}

			ringProgressDialog.dismiss();
		}
	}

}