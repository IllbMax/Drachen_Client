package com.vsis.drachen.model.quest;

/**
 * Status of QuestTargets and Quests
 * 
 */
public enum QuestProgressStatus {
	/**
	 * Once a QuestTarget is Failed it stays Failed. <br>
	 * A Quest is Failed if at least on Target is Failed.
	 */
	Failed,
	/**
	 * The QuestTarget is fulfilled (this does not mean that the QuestTarget is
	 * finished. <br>
	 * A Quest is Succeeded if all QuestTargets are Succeeded.
	 */
	Succeeded,
	/**
	 * The QuestTarget is neither Failed nor Succeeded (it can't be finished). <br>
	 * A Quest is OnGoing if no QuestTarget is Failed and at least one
	 * QuestTarget is not Succeeded.
	 */
	OnGoing
}
