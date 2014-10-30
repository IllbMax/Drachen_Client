package com.vsis.drachen.model.quest;

import java.util.EnumSet;
import java.util.Set;

import com.visis.drachen.sensor.SensorType;
import com.vsis.drachen.model.world.Point;
import com.vsis.drachen.model.world.Polygon;

public class GPSQuestTarget extends QuestTarget {

	private Polygon targetArea;
	private static EnumSet<SensorType> sensors = EnumSet
			.of(SensorType.Position);

	public GPSQuestTarget(String name, Polygon targetArea) {
		super(name);
		this.targetArea = targetArea;

	}

	public Polygon getTargetArea() {
		return targetArea;
	}

	public void setTargetArea(Polygon targetArea) {
		this.targetArea = targetArea;
	}

	@Override
	public Set<SensorType> requiredSensors() {
		System.out.println("require");
		System.out.println("set: " + sensors.size());
		return sensors;
	}

	@Override
	public boolean receiveSensordata(SensorType type, Object... data) {
		assert (type == SensorType.Position);

		double lat = (double) data[0];
		double lon = (double) data[1];

		boolean contains = targetArea.Contains(new Point(lat, lon));
		// TODO: user contains

		if (isOnGoing() && contains)
			setProgress(QuestProgressStatus.Succeeded);
		else if (isFulfilled() && !contains)
			setProgress(QuestProgressStatus.OnGoing);
		else
			return false;
		return true;
	}

	@Override
	public boolean needsNewSensordata(SensorType type) {
		// TODO: maybe only check once
		return this.requiredSensors().contains(type) && !isFailed()
				&& !isFinished();
	}
}