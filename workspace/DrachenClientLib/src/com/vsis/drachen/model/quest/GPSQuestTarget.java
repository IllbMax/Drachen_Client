package com.vsis.drachen.model.quest;

import java.util.EnumSet;
import java.util.Set;

import com.vsis.drachen.model.world.Point;
import com.vsis.drachen.model.world.Polygon;
import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.GPSSensorData;
import com.vsis.drachen.sensor.data.ISensorData;

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
	public boolean receiveSensordata(SensorType type, ISensorData data) {
		assert (type == SensorType.Position);

		GPSSensorData gpsdata = (GPSSensorData) data;

		double lat = gpsdata.getLatitude();
		double lon = gpsdata.getLongitude();

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