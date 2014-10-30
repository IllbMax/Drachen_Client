package com.vsis.drachen.model.quest;

import java.util.EnumSet;
import java.util.Set;

import com.visis.drachen.sensor.SensorType;

public class AccelerationQuestTarget extends QuestTarget {

	private static final EnumSet<SensorType> sensors = EnumSet
			.of(SensorType.Accelaration);

	private float upperBound;

	private float lowerBound;

	public float getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(float lowerBound) {
		this.lowerBound = lowerBound;
	}

	public float getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(float upperBound) {
		this.upperBound = upperBound;
	}

	public AccelerationQuestTarget(String name, float upperBound,
			float lowerBound) {
		super(name);
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	@Override
	public Set<SensorType> requiredSensors() {
		// TODO Auto-generated method stub
		return sensors;
	}

	@Override
	public boolean receiveSensordata(SensorType type, Object... data) {
		assert (type == SensorType.Position);

		double ax = (double) data[0];
		double ay = (double) data[1];
		double az = (double) data[2];

		double sum = ax * ax + ay * ay + az * az;
		boolean success = lowerBound * lowerBound <= sum
				&& sum <= upperBound * upperBound;
		// TODO: use success
		if (isOnGoing() && success)
			setProgress(QuestProgressStatus.Succeeded);
		else if (isFulfilled() && !success)
			setProgress(QuestProgressStatus.OnGoing);
		else
			return false;
		return true;
	}

	@Override
	public boolean needsNewSensordata(SensorType type) {
		// TODO Auto-generated method stub
		return this.requiredSensors().contains(type);
	}

}