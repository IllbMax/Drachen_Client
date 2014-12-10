package com.vsis.drachen.exception;

import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestPrototype;

/**
 * Representing error with Quests:
 */
public abstract class QuestException extends DrachenBaseException {

	/**
	 * id of the quest/questprototype
	 */
	private int questId;
	/**
	 * true if the id belongs to a {@link QuestPrototype}
	 */
	private boolean proto;

	/**
	 * The id of the {@link Quest}/{@link QuestPrototype}
	 * 
	 * @return the id
	 */
	public int getQuestId() {
		return questId;
	}

	/**
	 * Determine if the id points to a {@link QuestPrototype}
	 * 
	 * @return true if the id belongs to a {@link QuestPrototype}
	 */
	public boolean isProto() {
		return proto;
	}

	public QuestException(int questId, boolean proto) {
		this.questId = questId;
		this.proto = proto;
	}

	public QuestException(int questId, boolean proto, String message) {
		super(message);
		this.questId = questId;
		this.proto = proto;
	}

	public QuestException(int questId, boolean proto, Throwable cause) {
		super(cause);
		this.questId = questId;
		this.proto = proto;
	}

	public QuestException(int questId, boolean proto, String message,
			Throwable cause) {
		super(message, cause);
		this.questId = questId;
		this.proto = proto;
	}

}
