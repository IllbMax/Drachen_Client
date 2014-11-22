package com.vsis.drachen.model.quest;

import java.util.ArrayList;
import java.util.List;

import com.vsis.drachen.model.IdObject;

public class Quest extends IdObject {

	public Quest() {
		// questTargets
		finished = false;
	}

	private QuestPrototype prototype;

	private boolean finished;

	// private User user;

	// private String name;

	// private String description;

	private List<QuestTarget> questTargets = new ArrayList<QuestTarget>();

	public boolean isFailed() {
		return getProgress() == QuestProgressStatus.Failed;
	}

	public boolean isOnGoing() {
		return getProgress() == QuestProgressStatus.OnGoing;
	}

	public boolean isFulfilled() {
		return getProgress() == QuestProgressStatus.Succeeded;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String getName() {
		return prototype.getName();
		// return name;
	}

	// public int getPrototypeId() {
	// return prototypeId;
	// }
	//
	// public void setPrototypeId(int prototypeId) {
	// this.prototypeId = prototypeId;
	// }
	public QuestPrototype getPrototype() {
		return prototype;
	}

	public void setPrototype(QuestPrototype prototype) {
		this.prototype = prototype;
	}

	// public User getUser() {
	// return user;
	// }
	//
	// public void setUser(User user) {
	// this.user = user;
	// }

	// public void setName(String newName) {
	// this.name = newName;
	// }

	public String getDescription() {
		// return description;
		return prototype.getDescription();
	}

	// public void setDescription(String description) {
	// this.description = description;
	// }

	public void addQuestTarget(QuestTarget newQuestTarget) {
		questTargets.add(newQuestTarget);
		newQuestTarget.setQuest(this);
	}

	public QuestTarget getQuestTarget(int i) {
		return questTargets.get(i);
	}

	public List<QuestTarget> getQuestTargets() {
		return new ArrayList<QuestTarget>(questTargets);
	}

	public QuestProgressStatus getProgress() {
		boolean succeeded = true;
		for (QuestTarget qt : questTargets) {
			if (qt.getProgress() == QuestProgressStatus.Failed)
				return QuestProgressStatus.Failed;
			else if (qt.getProgress() != QuestProgressStatus.Succeeded)
				succeeded = false;
		}
		if (succeeded)
			return QuestProgressStatus.Succeeded;
		else
			return QuestProgressStatus.OnGoing;
	}

	public void updateQuestReference() {
		for (QuestTarget qt : questTargets) {
			qt.setQuest(this);
		}
	}

}