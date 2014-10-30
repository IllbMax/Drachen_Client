package com.vsis.drachen.model.quest;

import java.util.EnumSet;
import java.util.Set;

import com.visis.drachen.sensor.SensorType;
import com.vsis.drachen.model.world.Location;

public class LocationQuestTarget extends QuestTarget {

	private int locationId;

	EnumSet<SensorType> sensors = EnumSet.of(SensorType.Location);

	public LocationQuestTarget(String name, int locationId) {
		super(name);
		this.locationId = locationId;

	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	@Override
	public Set<SensorType> requiredSensors() {

		return sensors;
	}

	@Override
	public boolean receiveSensordata(SensorType type, Object... data) {
		assert (type == SensorType.Location);

		Location loc = (Location) data[0];
		Location p = loc;
		boolean success = false;
		do {
			if (p.getId() == locationId) {
				success = true;
				break;
			}
		} while ((p = loc.getParentLocation()) != null);

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
		// TODO: maybe only check once
		return this.requiredSensors().contains(type) && !isFailed()
				&& !isFinished();
	}
}