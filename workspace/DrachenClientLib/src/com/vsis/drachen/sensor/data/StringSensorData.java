package com.vsis.drachen.sensor.data;

/**
 * Sensordata from string sensors (code scanner, speech input etc)
 * 
 */
public class StringSensorData implements ISensorData {
	private String[] data;

	private long millis, nanos;

	public StringSensorData(long millis, long nanos, String data) {
		this.data = new String[] { data };

		this.millis = millis;
		this.nanos = nanos;
	}

	public StringSensorData(long millis, long nanos, String[] data) {
		this.data = data;
		this.millis = millis;
		this.nanos = nanos;
	}

	/**
	 * the string data of the {@link ISensorData} if a single string is the
	 * source. If an array with possibilities were given, the first value will
	 * be returned and if it is empty it returns null
	 * 
	 * @return the single string data or the first possibility or null
	 */
	public String getFirstString() {
		if (this.data.length > 0)
			return data[0];
		else
			return null;
	}

	/**
	 * determines if the is more than one possibility
	 * 
	 * @return true if the array is longer than one
	 */
	public boolean hasMultipeStrings() {
		return this.data.length > 1;
	}

	/**
	 * 
	 * @return array with all possibilities
	 */
	public String[] getMultipleString() {
		return data;
	}

	@Override
	public String toString() {
		String result = "empty";
		if (data.length > 0)
			result = "'" + join(data, "', '") + "'";
		return "String: " + result;
	}

	private static String join(String r[], String d) {
		if (r.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		int i;
		for (i = 0; i < r.length - 1; i++)
			sb.append(r[i] + d);
		return sb.toString() + r[i];
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
