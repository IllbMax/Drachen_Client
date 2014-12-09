package com.visis.drachen.exception;

/**
 * Representing error with aborting Quests:
 * 
 * If user tries to abort a quest, but the user is not on this quest (eg.
 * finished, or not own quest)
 */
public class QuestAbortException extends QuestException {

	public static enum AbortDenyingType {
		/**
		 * if the user is not on this quest
		 */
		NotRunning,
		/**
		 * if it isn't the quest of the user
		 */
		NotOwn
	}

	AbortDenyingType dtype;

	/**
	 * detailed cause of denying the abortion of the quest
	 * 
	 * @return the type of error prohibiting the abortion
	 */
	public AbortDenyingType getType() {
		return dtype;
	}

	public QuestAbortException(int questId, AbortDenyingType type) {
		super(questId, true);
		this.dtype = type;
	}

	public QuestAbortException(int questId, AbortDenyingType type,
			String message) {
		super(questId, true, message);
		this.dtype = type;
	}

	public QuestAbortException(int questId, AbortDenyingType type,
			Throwable cause) {
		super(questId, true, cause);
		this.dtype = type;
	}

	public QuestAbortException(int questId, AbortDenyingType type,
			String message, Throwable cause) {
		super(questId, true, message, cause);
		this.dtype = type;
	}

}
