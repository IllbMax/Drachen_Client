package com.visis.drachen.exception;

/**
 * Representing error with aborting Quests:
 * 
 * If user tries to abort a quest, but the user is not on this quest (eg.
 * finished)
 */
public class QuestAbortException extends QuestException {

	public QuestAbortException(int questId) {
		super(questId, false);
	}

	public QuestAbortException(int questId, String message) {
		super(questId, false, message);
	}

	public QuestAbortException(int questId, Throwable cause) {
		super(questId, false, cause);
	}

	public QuestAbortException(int questId, String message, Throwable cause) {
		super(questId, false, message, cause);
	}

}
