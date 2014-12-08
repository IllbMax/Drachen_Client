package com.vsis.drachen.adapter;

import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapter;
import com.visis.drachen.exception.CredentialException;
import com.visis.drachen.exception.DrachenBaseException;
import com.visis.drachen.exception.IdNotFoundException;
import com.visis.drachen.exception.InternalProcessException;
import com.visis.drachen.exception.InvalidParameterException;
import com.visis.drachen.exception.MissingParameterException;
import com.visis.drachen.exception.ParameterException;
import com.visis.drachen.exception.RestrictionException;
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

	public static RuntimeTypeAdapter<DrachenBaseException> getDrachenExceptionAdapter() {
		RuntimeTypeAdapter<DrachenBaseException> adapter = RuntimeTypeAdapter
				.create(DrachenBaseException.class, "type");
		adapter.registerSubtype(InternalProcessException.class);
		adapter.registerSubtype(InvalidParameterException.class);
		adapter.registerSubtype(CredentialException.class);
		// parameter exceptions:
		adapter.registerSubtype(ParameterException.class);
		adapter.registerSubtype(RestrictionException.class);
		adapter.registerSubtype(MissingParameterException.class);
		adapter.registerSubtype(IdNotFoundException.class);

		return adapter;
	}

	public static GsonBuilder installAllAdapter(GsonBuilder builder) {

		return builder
				.registerTypeAdapter(QuestTarget.class, getQuestTargetAdapter())
				//
				.registerTypeAdapter(Quest.class, getQuestAdapter())
				.registerTypeAdapter(IQuestTargetUpdateState.class,
						getQuestTargetUpdateStateAdapter()) //
				.registerTypeAdapter(DrachenBaseException.class,
						getDrachenExceptionAdapter()) //
		// .registerTypeAdapter(QuestTarget.class, getQuestTargetAdapter())
		// .registerTypeAdapter(QuestTarget.class, getQuestTargetAdapter())

		;
	}

	private static QuestTypeAdapter getQuestAdapter() {
		return new QuestTypeAdapter();
	}

}