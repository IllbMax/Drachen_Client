package com.vsis.drachen.adapter;

import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapter;
import com.vsis.drachen.model.quest.AccelerationQuestTarget;
import com.vsis.drachen.model.quest.GPSQuestTarget;
import com.vsis.drachen.model.quest.IQuestTargetUpdateState;
import com.vsis.drachen.model.quest.LocationQuestTarget;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachen.model.quest.QuestTargetDefaultUpdateState;

public class AdapterProvider {

	public static RuntimeTypeAdapter<QuestTarget> getQuestTargetAdapter() {
		RuntimeTypeAdapter<QuestTarget> adapter = RuntimeTypeAdapter.create(
				QuestTarget.class, "type");
		adapter.registerSubtype(GPSQuestTarget.class);
		adapter.registerSubtype(AccelerationQuestTarget.class);
		adapter.registerSubtype(LocationQuestTarget.class);
		// .registerSubtype(MoreQuestTarget.class)

		return adapter;
	}

	public static RuntimeTypeAdapter<IQuestTargetUpdateState> getQuestTargetUpdateStateAdapter() {
		RuntimeTypeAdapter<IQuestTargetUpdateState> adapter = RuntimeTypeAdapter
				.create(IQuestTargetUpdateState.class, "type");
		adapter.registerSubtype(QuestTargetDefaultUpdateState.class);
		// adapter.registerSubtype(AccelerationQuestTarget.class);
		// .registerSubtype(MoreQuestTarget.class)

		return adapter;
	}

	public static GsonBuilder installAllAdapter(GsonBuilder builder) {

		return builder
				.registerTypeAdapter(QuestTarget.class, getQuestTargetAdapter())
				//
				.registerTypeAdapter(Quest.class, getQuestAdapter())
				.registerTypeAdapter(IQuestTargetUpdateState.class,
						getQuestTargetUpdateStateAdapter()) //
		// .registerTypeAdapter(QuestTarget.class, getQuestTargetAdapter())
		// .registerTypeAdapter(QuestTarget.class, getQuestTargetAdapter())

		;
	}

	private static QuestTypeAdapter getQuestAdapter() {
		return new QuestTypeAdapter();
	}

}