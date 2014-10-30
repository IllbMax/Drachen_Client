package com.vsis.drachen.model.quest;

public class QuestTargetDefaultUpdateState implements IQuestTargetUpdateState {
	private QuestProgressStatus progress;
	private boolean finished;

	public QuestTargetDefaultUpdateState(QuestProgressStatus progress, boolean finished) {
		this.setProgress(progress);
		this.setFinished(finished);
	}

	public QuestProgressStatus getProgress() {
		return progress;
	}

	public void setProgress(QuestProgressStatus progress) {
		this.progress = progress;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

}
