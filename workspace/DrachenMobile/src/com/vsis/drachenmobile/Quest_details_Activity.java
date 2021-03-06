package com.vsis.drachenmobile;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vsis.drachen.QuestService;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.util.StringFunction;
import com.vsis.drachenmobile.helper.Helper;
import com.vsis.drachenmobile.task.QuestAbortTaskTemplate;
import com.vsis.drachenmobile.util.ArrayDetailsExpandableListAdapter;

public class Quest_details_Activity extends Activity {

	public static final String EXTRA_QUESTID = "questId";

	int _questId;

	/**
	 * goes from 0-3: 0 no hint, 3 all 3 hints
	 */
	private int hintLevel = 0;
	private boolean[] hasHint = new boolean[4];
	private int[] hintLevelButtonId = new int[4];

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
			TextView textView_stared = (TextView) findViewById(R.id.textview_Quest_Started);
			Button buttonAbort = (Button) findViewById(R.id.listview_QuestDetails_AbortButton);

			ExpandableListView listView_QuestTargets = (ExpandableListView) findViewById(R.id.explistView_Quest_Questtargets);

			textView_name.setText(quest.getName());
			textView_desc.setText(quest.getDescription());
			java.text.DateFormat dateFormat = DateFormat.getDateFormat(this);
			java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);
			Date date = new Date(quest.getStartTimeMillis());
			Calendar c = new GregorianCalendar();
			c.setTime(date);

			textView_stared.setText(getString(R.string.started_date_time,
					dateFormat.format(date), timeFormat.format(date)));

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

			// the hints
			Button buttonHint1 = (Button) findViewById(R.id.button1);
			Button buttonHint2 = (Button) findViewById(R.id.button2);
			Button buttonHint3 = (Button) findViewById(R.id.button3);

			TextView textViewHint1 = (TextView) findViewById(R.id.textView1);
			TextView textViewHint2 = (TextView) findViewById(R.id.textView2);
			TextView textViewHint3 = (TextView) findViewById(R.id.textView3);

			hasHint[0] = true;
			hintLevelButtonId[0] = 0;
			hintLevelButtonId[1] = R.id.button1;
			hintLevelButtonId[2] = R.id.button2;
			hintLevelButtonId[3] = R.id.button3;
			setHintButton(buttonHint1, textViewHint1, quest.getHint1(), 1);
			setHintButton(buttonHint2, textViewHint2, quest.getHint2(), 2);
			setHintButton(buttonHint3, textViewHint3, quest.getHint3(), 3);

			setHintLevel(0);
		}
	}

	private void setHintButton(Button buttonHint, final TextView textViewHint,
			String hint, final int level) {
		if (StringFunction.nullOrWhiteSpace(hint)) {
			hasHint[level] = false;
			buttonHint.setVisibility(View.GONE);
			textViewHint.setVisibility(View.GONE);
			textViewHint.setText("");
		} else {
			hasHint[level] = true;
			buttonHint.setVisibility(View.GONE);
			textViewHint.setVisibility(View.GONE);
			textViewHint.setText(hint);

			buttonHint.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setHintLevel(level);
					if (textViewHint.getVisibility() == View.VISIBLE)

						textViewHint.setVisibility(View.GONE);
					else
						textViewHint.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	private void setHintLevel(int level) {
		// TODO: maybe save hint level in quest
		this.hintLevel = Math.max(hintLevel, level);
		int maxIndex = Math.min(hintLevel, 2) + 1;

		for (int i = 1; i <= maxIndex; i++) {
			if (hasHint[i])
				((Button) findViewById(hintLevelButtonId[i]))
						.setVisibility(View.VISIBLE);
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
				convertView = infalInflater.inflate(_layouts[0], null);
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
			final TextView textViewStatus = (TextView) convertView
					.findViewById(R.id.textview_status);

			// Button buttonAbort = (Button) convertView
			// .findViewById(R.id.listview_abortButton);
			// CheckBox sensorOn = (CheckBox) convertView
			// .findViewById(R.id.listview_Quest_checkSonsorOn);

			textViewName.setText(questTarget.getName());
			textViewStatus.setText(Helper.getProgressDisplayText(
					Quest_details_Activity.this, questTarget.isFinished(),
					questTarget.getProgress()));
			textViewStatus.setTextColor(Helper.getProgressDisplayColor(
					Quest_details_Activity.this, questTarget.isFinished(),
					questTarget.getProgress()));
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
		protected Object getChild(QuestTarget group, int childPosition) {
			int i = 0;
			SensorType sensor = null;
			for (SensorType s : group.requiredSensors())
				if (i++ == childPosition) {
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

	private class QuestAbortTask extends QuestAbortTaskTemplate {

		public QuestAbortTask(Quest quest) {
			super(Quest_details_Activity.this, quest,
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
				// close this activity
				Quest_details_Activity.this.onBackPressed();

			} else {
				this.showAlertExceptionDialog();
			}
		}
	}

	private class HintSystem {
		int buttonId, textViewId;

	}
}
