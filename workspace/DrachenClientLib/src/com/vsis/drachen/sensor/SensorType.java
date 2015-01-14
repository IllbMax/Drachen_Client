package com.vsis.drachen.sensor;

/**
 * Lists all possible Sensor-Types known by the Drachensystem
 * 
 */
public enum SensorType {
	Accelaration, //
	MagneticField, //
	Orientation, //

	/**
	 * Return a Position via Lat/Lon
	 */
	Position, // eg GPS sensor
	/**
	 * Returns a {@link com.vsis.drachen.model.world.Location}
	 */
	Location,

	Temperature, Humidity, Pressure, LightIntensity,

	Camera(false),

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
	CodeScanner(false),
	/**
	 * text input via speech, dialog etc.
	 */
	TextInput(false);

	private boolean _background;

	SensorType(boolean background) {
		_background = background;
	}

	SensorType() {
		this(true);
	}

	/**
	 * defines if the sensor(type) monitors continuously in the background. <br>
	 * if false, the sensor needs to be activated for a single action by the
	 * user
	 * 
	 * @return true if its is a backgroundsensor
	 */
	public boolean isBackgroundSensor() {
		return _background;
	}

}
