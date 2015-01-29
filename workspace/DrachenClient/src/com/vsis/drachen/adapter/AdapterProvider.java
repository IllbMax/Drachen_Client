package com.vsis.drachen.adapter;

import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapter;
import com.vsis.drachen.exception.CredentialException;
import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.IdNotFoundException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.InvalidParameterException;
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachen.exception.ObjectRestrictionException;
import com.vsis.drachen.exception.ParameterException;
import com.vsis.drachen.exception.QuestException;
import com.vsis.drachen.exception.QuestFinishedException;
import com.vsis.drachen.exception.QuestStartException;
import com.vsis.drachen.exception.QuestTargetException;
import com.vsis.drachen.exception.RestrictionException;
import com.vsis.drachen.model.objects.ObjectEffect;
import com.vsis.drachen.model.objects.ObjectEffectBreak;
import com.vsis.drachen.model.objects.ObjectEffectTakeDrop;
import com.vsis.drachen.model.objects.ObjectUseListener;
import com.vsis.drachen.model.objects.ShakeObjectUseListener;
import com.vsis.drachen.model.quest.AccelerationQuestTarget;
import com.vsis.drachen.model.quest.GPSQuestTarget;
import com.vsis.drachen.model.quest.IQuestTargetUpdateState;
import com.vsis.drachen.model.quest.LocationQuestTarget;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachen.model.quest.QuestTargetDefaultUpdateState;
import com.vsis.drachen.model.quest.StringCompareQuestTarget;

public class AdapterProvider {

	public static RuntimeTypeAdapter<QuestTarget> getQuestTargetAdapter() {
		RuntimeTypeAdapter<QuestTarget> adapter = RuntimeTypeAdapter.create(
				QuestTarget.class, "type");
		adapter.registerSubtype(GPSQuestTarget.class);
		adapter.registerSubtype(AccelerationQuestTarget.class);
		adapter.registerSubtype(LocationQuestTarget.class);
		adapter.registerSubtype(StringCompareQuestTarget.class);
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

	public static RuntimeTypeAdapter<ObjectUseListener> getObjectUseListenerAdapter() {
		RuntimeTypeAdapter<ObjectUseListener> adapter = RuntimeTypeAdapter
				.create(ObjectUseListener.class, "type");
		adapter.registerSubtype(ShakeObjectUseListener.class);
		// .registerSubtype(MoreObjectUseListener.class)

		return adapter;// .create();
	}

	public static RuntimeTypeAdapter<ObjectEffect> getObjectEffectAdapter() {
		RuntimeTypeAdapter<ObjectEffect> adapter = RuntimeTypeAdapter.create(
				ObjectEffect.class, "type");
		adapter.registerSubtype(ObjectEffectBreak.class);
		adapter.registerSubtype(ObjectEffectTakeDrop.class);
		// .registerSubtype(MoreObjectEffect.class)

		return adapter;// .create();
	}

	public static RuntimeTypeAdapter<DrachenBaseException> getDrachenExceptionAdapter() {
		RuntimeTypeAdapter<DrachenBaseException> adapter = RuntimeTypeAdapter
				.create(DrachenBaseException.class, "type");
		adapter.registerSubtype(InternalProcessException.class);
		adapter.registerSubtype(CredentialException.class);
		adapter.registerSubtype(ObjectRestrictionException.class);
		adapter.registerSubtype(RestrictionException.class);
		// parameter exceptions:
		adapter.registerSubtype(ParameterException.class);
		adapter.registerSubtype(InvalidParameterException.class);
		adapter.registerSubtype(MissingParameterException.class);
		adapter.registerSubtype(IdNotFoundException.class);
		// quest exceptions
		adapter.registerSubtype(QuestException.class);
		adapter.registerSubtype(QuestStartException.class);
		adapter.registerSubtype(QuestFinishedException.class);
		adapter.registerSubtype(QuestTargetException.class);

		return adapter;
	}

	public static GsonBuilder installAllAdapter(GsonBuilder builder) {

		return builder
				.registerTypeAdapter(QuestTarget.class, getQuestTargetAdapter())
				.registerTypeAdapter(Quest.class, getQuestAdapter())
				.registerTypeAdapter(IQuestTargetUpdateState.class,
						getQuestTargetUpdateStateAdapter())
				.registerTypeAdapter(DrachenBaseException.class,
						getDrachenExceptionAdapter())
				.registerTypeAdapter(ObjectUseListener.class,
						getObjectUseListenerAdapter())
				.registerTypeAdapter(ObjectEffect.class,
						getObjectEffectAdapter())
		// .registerTypeAdapter(QuestTarget.class, getQuestTargetAdapter())
		// .registerTypeAdapter(QuestTarget.class, getQuestTargetAdapter())

		;
	}

	private static QuestTypeAdapter getQuestAdapter() {
		return new QuestTypeAdapter();
	}
}