package com.visis.drachen.exception;

/**
 * Representing error with a finished Quest:
 * 
 * If user tries to change a quest (or a questTarget of the quest) but the quest
 * is already finished.
 */
public class QuestFinishedException extends QuestException {

	public QuestFinishedException(int questId) {
		super(questId, false);
	}

	public QuestFinishedException(int questId, String message) {
		super(questId, false, message);
	}

	public QuestFinishedException(int questId, Throwable cause) {
		super(questId, false, cause);
	}

	public QuestFinishedException(int questId, String message, Throwable cause) {
		super(questId, false, message, cause);
	}

}
