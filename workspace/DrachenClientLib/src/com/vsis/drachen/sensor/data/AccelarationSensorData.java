package com.vsis.drachen.sensor.data;

/**
 * Sensordata for GPS Quests
 * 
 */
public class AccelarationSensorData implements ISensorData {
	private double ax, ay, az;

	public AccelarationSensorData(double ax, double ay, double az) {
		this.ax = ax;
		this.ay = ay;
		this.az = az;
	}

	public double getAx() {
		return ax;
	}

	public double getAy() {
		return ay;
	}

	public double getAz() {
		return az;
	}

	@Override
	public String toString() {
		return String.format("Accelaration: (%s; %s; %s)", ax, ay, az);
	}

}
