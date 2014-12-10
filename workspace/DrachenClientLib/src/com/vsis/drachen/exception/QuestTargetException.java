package com.vsis.drachen.exception;

import com.vsis.drachen.model.quest.QuestTarget;

/**
 * Representing error with {@link QuestTarget}:
 * 
 * If user tries to finish a quest but the targets are not succeeded or if the
 * user tries to change a quest target that is finished.
 */
public class QuestTargetException extends QuestException {

	/**
	 * id of (the first) not completed/or finished {@link QuestTarget}
	 */
	private int questTargetId;

	/**
	 * id of (the first) not completed/or finished {@link QuestTarget}
	 * 
	 * @return id of the questTarget
	 */
	public int getQuestTargetId() {
		return questTargetId;
	}

	public QuestTargetException(int questId, int questTargetId) {
		super(questId, false);
		this.questTargetId = questTargetId;
	}

	public QuestTargetException(int questId, int questTargetId, String message) {
		super(questId, false, message);
		this.questTargetId = questTargetId;
	}

	public QuestTargetException(int questId, int questTargetId, Throwable cause) {
		super(questId, false, cause);
		this.questTargetId = questTargetId;
	}

	public QuestTargetException(int questId, int questTargetId, String message,
			Throwable cause) {
		super(questId, false, message, cause);
		this.questTargetId = questTargetId;
	}

}
