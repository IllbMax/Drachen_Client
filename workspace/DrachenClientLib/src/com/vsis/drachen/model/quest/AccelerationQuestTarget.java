package com.vsis.drachen.model.quest;

import java.util.EnumSet;
import java.util.Set;

import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.AccelarationSensorData;
import com.vsis.drachen.sensor.data.ISensorData;

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
	public boolean receiveSensordata(SensorType type, ISensorData data) {
		assert (type == SensorType.Position);

		boolean onlyOnce = true;
		if (isFulfilled() && onlyOnce)
			return false;

		AccelarationSensorData accelData = (AccelarationSensorData) data;

		double ax = accelData.getAx();
		double ay = accelData.getAy();
		double az = accelData.getAz();

		double sum = ax * ax + ay * ay + az * az;
		boolean success = lowerBound * lowerBound <= sum
				&& sum <= upperBound * upperBound;

		if (isOnGoing() && success) {
			setProgress(QuestProgressStatus.Succeeded);
			if (onlyOnce)
				setFinished(true);
		} else if (isFulfilled() && !success) {
			setProgress(QuestProgressStatus.OnGoing);

		} else
			return false;
		return true;
	}

	@Override
	public boolean needsNewSensordata(SensorType type) {
		// TODO Auto-generated method stub
		return !isFinished() && this.requiredSensors().contains(type);
	}

}