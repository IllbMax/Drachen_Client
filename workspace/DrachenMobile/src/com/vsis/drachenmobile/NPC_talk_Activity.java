package com.vsis.drachenmobile;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;
import com.vsis.drachen.NPCService;
import com.vsis.drachen.model.NPC;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestPrototype;
import com.vsis.drachenmobile.helper.Helper;
import com.vsis.drachenmobile.task.QuestStartTaskTemplate;
import com.vsis.drachenmobile.util.ArrayDetailsExpandableListAdapter;

public class NPC_talk_Activity extends Activity {

	public static final String EXTRA_NPCID = "npcId";

	int _npcId;
	ExpandableListView _prototypeListView;
	QuestPrototypeExpListAdapter _prototypeAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_npctalk);

		_npcId = getIntent().getExtras().getInt(EXTRA_NPCID);

		_prototypeAdapter = new QuestPrototypeExpListAdapter(this,
				new ArrayList<QuestPrototype>());

		_prototypeListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		_prototypeListView.setAdapter(_prototypeAdapter);

		displayNPC();

	}

	@Override
	protected void onResume() {
		super.onResume();

		_prototypeListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		_prototypeAdapter = (QuestPrototypeExpListAdapter) _prototypeListView
				.getExpandableListAdapter();

		displayNPC();

		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle("available Quests");
		actionBar.setTitle("Drachen!!!");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt(EXTRA_NPCID, _npcId);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		_npcId = savedInstanceState.getInt(EXTRA_NPCID);
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void startQuest(QuestPrototype questPrototype) {
		QuestStartTask task = new QuestStartTask(this, questPrototype,
				((DrachenApplication) this.getApplication()).getAppData());
		task.execute();

	}

	private void displayNPC() {
		DrachenApplication app = (DrachenApplication) getApplication();
		NPCService npcService = app.getAppData().getNPCService();
		NPC npc = npcService.getNPCFromId(_npcId);

		displayNPC(npc);
	}

	private void displayNPC(final NPC npc) {

		if (npc == null) {
			// TODO: close this activity
		} else {
			MyDataSet appdata = ((DrachenApplication) getApplication())
					.getAppData();
			SVGImageView avatar = (SVGImageView) findViewById(R.id.imageview1);
			TextView nameTextView = (TextView) findViewById(R.id.textview1);
			TextView talkTextView = (TextView) findViewById(R.id.textview2);
			// ListView questListView = (ListView) findViewById(R.id.listview1);

			nameTextView.setText(npc.getName());
			Helper.setImage(avatar, appdata.getResourceService(),
					npc.getImageKey(), true);
			talkTextView.setText(getString(R.string.npc_talk, npc.getName(),
					npc.getTalk()));

			_prototypeAdapter.clear();
			_prototypeAdapter.addAll(appdata.getQuestService()
					.removeStartedQuests(npc.getQuestPrototypes()));
		}
	}

	private class QuestPrototypeExpListAdapter extends
			ArrayDetailsExpandableListAdapter<QuestPrototype> {

		int[] _layouts = { android.R.layout.simple_expandable_list_item_1 };

		public QuestPrototypeExpListAdapter(Context context,
				List<QuestPrototype> listDataHeader) {
			super(context, listDataHeader);

		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final QuestPrototype questPrototype = getGroup(groupPosition);
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
			final QuestPrototype questPrototype = getGroup(groupPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.expandlistviewitem_questprototype, null);
			}
			final TextView textViewName = (TextView) convertView
					.findViewById(R.id.listview_QuestPrototype_Name);
			Button butttonStart = (Button) convertView
					.findViewById(R.id.listview_QuestStartButton);

			textViewName.setText(questPrototype.getName());
			butttonStart.setText("Start");
			butttonStart.setFocusable(false);

			butttonStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startQuest(questPrototype);
				}

			});

			return convertView;
		}

		@Override
		protected Object getChild(QuestPrototype group, int childPosition) {
			return group.getDescription();
		}

		@Override
		protected int getChildrenCount(QuestPrototype group) {
			return 1;
		}
	}

	private class QuestStartTask extends QuestStartTaskTemplate {

		public QuestStartTask(Context ctx, QuestPrototype questPrototype,
				MyDataSet appData) {
			super(ctx, questPrototype, appData);
		}

		@Override
		protected void onPostExecute(Quest result) {
			super.onPostExecute(result);

			if (result != null) {
				_prototypeAdapter.removeGroup(questPrototype);
				_prototypeAdapter.notifyDataSetChanged();

			} else {
				showAlertExceptionDialog();
			}
		}
	}

}
