package com.vsis.drachen.sensor.data;

/**
 * Sensordata from acceleration sensors.
 * 
 */
public class AccelarationSensorData implements ISensorData {
	private float ax, ay, az;
	private long millis, nanos;

	public AccelarationSensorData(long millis, long nanos, float ax, float ay,
			float az) {
		this.ax = ax;
		this.ay = ay;
		this.az = az;

		this.millis = millis;
		this.nanos = nanos;
	}

	/**
	 * Acceleration in x direction Unit: [m/s^2]
	 * 
	 * @return Acceleration in x direction
	 */
	public float getAx() {
		return ax;
	}

	/**
	 * Acceleration in y direction Unit: [m/s^2]
	 * 
	 * @return Acceleration in y direction
	 */
	public float getAy() {
		return ay;
	}

	/**
	 * Acceleration in z direction Unit: [m/s^2]
	 * 
	 * @return Acceleration in z direction
	 */
	public float getAz() {
		return az;
	}

	@Override
	public String toString() {
		return String.format("Accelaration: (%s; %s; %s)", ax, ay, az);
	}

	@Override
	public long getUnixMillis() {
		return millis;
	}

	@Override
	public long getNanoTime() {
		return nanos;
	}

}
