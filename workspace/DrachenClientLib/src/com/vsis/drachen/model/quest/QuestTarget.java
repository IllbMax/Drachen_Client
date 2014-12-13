package com.vsis.drachen.model.quest;

import java.util.Set;

import com.vsis.drachen.model.ISensorSensitive;
import com.vsis.drachen.model.IdObject;
import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.ISensorData;

public abstract class QuestTarget extends IdObject implements ISensorSensitive {

	private String name;
	private Quest quest;
	private boolean trackTarget = false;

	private QuestProgressStatus progress;
	private boolean finished;

	public QuestTarget(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public QuestProgressStatus getProgress() {
		return progress;
	}

	public void setProgress(QuestProgressStatus progress) {
		this.progress = progress;
		if (progress == QuestProgressStatus.Failed)
			setFinished(true);
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Quest getQuest() {
		return quest;
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
	}

	public boolean isFailed() {
		return getProgress() == QuestProgressStatus.Failed;
	}

	public boolean isOnGoing() {
		return getProgress() == QuestProgressStatus.OnGoing;
	}

	public boolean isFulfilled() {
		return getProgress() == QuestProgressStatus.Succeeded;
	}

	/**
	 * Determines if the Target should be tracked
	 * 
	 * @return
	 */
	public boolean getTrackTarget() {
		return trackTarget;
	}

	public void setTrackTarget(boolean trackTarget) {
		this.trackTarget = trackTarget;
	}

	/**
	 * The State of the QuestTarget containing the update for the server Do NOT
	 * use this for (sensor) data streaming, a questTarget should not change the
	 * state too often (frequently)
	 * 
	 * @return
	 */
	public IQuestTargetUpdateState getUpdateState() {
		return getDefaultUpdateState();
	}

	/**
	 * returns the default update State containing the progress and the finished
	 * status
	 * 
	 * @return
	 */
	protected final IQuestTargetUpdateState getDefaultUpdateState() {
		return new QuestTargetDefaultUpdateState(progress, finished);
	}

	public void loadQuestTargetUpdateState(IQuestTargetUpdateState state) {
		loadQuestTargetDefaultUpdateState((QuestTargetDefaultUpdateState) state);
	}

	protected final void loadQuestTargetDefaultUpdateState(
			QuestTargetDefaultUpdateState state) {
		this.progress = state.getProgress();
		this.finished = state.isFinished();
	}

	/* (non-Javadoc)
	 * @see com.vsis.drachen.model.quest.ISensorSensitve#needsNewSensordata(com.vsis.drachen.sensor.SensorType)
	 */
	@Override
	public abstract boolean needsNewSensordata(SensorType type);

	/* (non-Javadoc)
	 * @see com.vsis.drachen.model.quest.ISensorSensitve#requiredSensors()
	 */
	@Override
	public abstract Set<SensorType> requiredSensors();

	/* (non-Javadoc)
	 * @see com.vsis.drachen.model.quest.ISensorSensitve#receiveSensordata(com.vsis.drachen.sensor.SensorType, com.vsis.drachen.sensor.data.ISensorData)
	 */
	@Override
	public abstract boolean receiveSensordata(SensorType type, ISensorData data);

}
