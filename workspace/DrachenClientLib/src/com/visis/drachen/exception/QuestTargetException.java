package com.visis.drachen.exception;

import com.vsis.drachen.model.quest.QuestTarget;

/**
 * Representing error with unfinished Quests:
 * 
 * If user tries to abort a quest, but the user is not on this quest (eg.
 * finished)
 */
public class QuestTargetNotFinishedException extends QuestException {

	/**
	 * id of (the first) not completed {@link QuestTarget}
	 */
	private int questTargetId;

	/**
	 * id of (the first) not completed {@link QuestTarget}
	 * 
	 * @return id of the questTarget
	 */
	public int getQuestTargetId() {
		return questTargetId;
	}

	public QuestTargetNotFinishedException(int questId, int questTargetId) {
		super(questId, false);
		this.questTargetId = questTargetId;
	}

	public QuestTargetNotFinishedException(int questId, int questTargetId,
			String message) {
		super(questId, false, message);
		this.questTargetId = questTargetId;
	}

	public QuestTargetNotFinishedException(int questId, int questTargetId,
			Throwable cause) {
		super(questId, false, cause);
		this.questTargetId = questTargetId;
	}

	public QuestTargetNotFinishedException(int questId, int questTargetId,
			String message, Throwable cause) {
		super(questId, false, message, cause);
		this.questTargetId = questTargetId;
	}

}
