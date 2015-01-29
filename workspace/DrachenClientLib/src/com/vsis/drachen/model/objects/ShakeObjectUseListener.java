package com.vsis.drachen.model.objects;

import java.util.EnumSet;
import java.util.Set;

import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.AccelarationSensorData;
import com.vsis.drachen.sensor.data.ISensorData;

public class ShakeObjectUseListener extends ObjectUseListener {

	private static final Set<SensorType> sensors = EnumSet
			.of(SensorType.Accelaration);

	public ShakeObjectUseListener() {
		super();
	}

	public ShakeObjectUseListener(int shakeCount, float shakeMinAmplitude,
			long shakeIntervalMillis) {
		super();
		this.shakeCount = shakeCount;
		this.shakeMinAmplitude = shakeMinAmplitude;
		this.shakeIntervalMillis = shakeIntervalMillis;
	}

	private int shakeCount;
	private float shakeMinAmplitude;
	private long shakeIntervalMillis;

	public int getShakeCount() {
		return shakeCount;
	}

	public void setShakeCount(int shakeCount) {
		this.shakeCount = shakeCount;
	}

	public float getShakeMinAmplitude() {
		return shakeMinAmplitude;
	}

	public void setShakeMinAmplitude(float shakeMinAmplitude) {
		this.shakeMinAmplitude = shakeMinAmplitude;
	}

	public long getShakeIntervalMillis() {
		return shakeIntervalMillis;
	}

	public void setShakeIntervalMillis(long shakeIntervalMillis) {
		this.shakeIntervalMillis = shakeIntervalMillis;
	}

	@Override
	public boolean needsNewSensordata(SensorType type) {
		return true;
	}

	@Override
	public Set<SensorType> requiredSensors() {
		return sensors;
	}

	@Override
	public boolean receiveSensordata_internal(SensorType type, ISensorData data) {

		assert (type == SensorType.Position);

		// boolean onlyOnce = true;
		// if (isFulfilled() && onlyOnce)
		// return false;

		AccelarationSensorData accelData = (AccelarationSensorData) data;

		double ax = accelData.getAx();
		double ay = accelData.getAy();
		double az = accelData.getAz();

		double sum = ax * ax + ay * ay + az * az;
		boolean success = 15 * 15 <= sum && sum <= 1000 * 1000;
		if (success)
			return true;
		return success;
	}
}
