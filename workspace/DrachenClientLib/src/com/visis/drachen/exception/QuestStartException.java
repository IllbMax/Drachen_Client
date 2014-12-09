package com.visis.drachen.exception;

/**
 * Representing error with starting Quests:
 * 
 * If user tries to start a quest, but isn't allowed to start it (eg. missing
 * precondition (not qualified), already finished, ...)
 */
public class QuestStartException extends QuestException {

	public static enum StartDenyingType {
		/**
		 * if the user doesn't fulfill all preconditions
		 */
		NotQualified,
		/**
		 * if the quest is already started
		 */
		StillOnGoing,
		/**
		 * if the quest is finished but not repeatable
		 */
		NotRepeatable
	}

	StartDenyingType dtype;

	/**
	 * detailed cause of denying the start of the quest
	 * 
	 * @return the type of error prohibiting the starting
	 */
	public StartDenyingType getType() {
		return dtype;
	}

	public QuestStartException(int questId, StartDenyingType type) {
		super(questId, true);
		this.dtype = type;
	}

	public QuestStartException(int questId, StartDenyingType type,
			String message) {
		super(questId, true, message);
		this.dtype = type;
	}

	public QuestStartException(int questId, StartDenyingType type,
			Throwable cause) {
		super(questId, true, cause);
		this.dtype = type;
	}

	public QuestStartException(int questId, StartDenyingType type,
			String message, Throwable cause) {
		super(questId, true, message, cause);
		this.dtype = type;
	}

}
