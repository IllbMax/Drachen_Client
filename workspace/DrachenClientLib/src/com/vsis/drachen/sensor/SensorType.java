package com.vsis.drachen.sensor;

/**
 * Lists all possible Sensor-Types known by the Drachensystem
 * 
 */
public enum SensorType {
	Accelaration,
	MagneticField,
	Orientation,

	/**
	 * Return a Position via Lat/Lon
	 */
	Position, // eg GPS sensor
	/**
	 * Returns a {@link com.vsis.drachen.model.world.Location}
	 */
	Location,

	Temperature,
	Humidity,
	Pressure,
	LightIntensity,

	Camera,

	/**
	 * Sensor for Interaction with the touch pad: eg. draw a pattern on it
	 */
	TouchPad,
	/**
	 * Sensor for User actions: eg collect items, level up or similar
	 */
	UserProperty,

	/**
	 * Sensor for scanning BarCodes, QRCodes etc
	 */
	CodeScanner
}
