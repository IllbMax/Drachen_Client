package com.vsis.drachen.model.quest;

import java.util.Set;

import com.visis.drachen.sensor.SensorType;
import com.vsis.drachen.model.IdObject;

public abstract class QuestTarget extends IdObject {

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

	/**
	 * Determines if new sensor data are required
	 * 
	 * @param type
	 *            type of possible new data
	 * 
	 * @return true if this QuestTarget should be notified if new sensor data is
	 *         available
	 */
	public abstract boolean needsNewSensordata(SensorType type);

	/**
	 * Set of required Sensors for this target
	 * 
	 * @return
	 */
	public abstract Set<SensorType> requiredSensors();

	/**
	 * 
	 * @param type
	 *            Type of the sending sensor
	 * @param data
	 *            send by the sensor
	 * @return true if there is an update in the state
	 */
	public abstract boolean receiveSensordata(SensorType type, Object... data);

}
