package com.vsis.drachen.model.quest;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.ISensorData;
import com.vsis.drachen.sensor.data.StringSensorData;
import com.vsis.drachen.util.StringFunction;

public class StringCompareQuestTarget extends QuestTarget {

	private List<String> choice;
	/**
	 * the threshold (0 for all 1 for only equality match)
	 */
	private float matchingThreshold;
	private static EnumSet<SensorType> sensors = EnumSet.of(
			SensorType.TextInput, SensorType.CodeScanner);

	public StringCompareQuestTarget(String name, List<String> choice,
			float threshold) {
		super(name);
		this.choice = choice;
		matchingThreshold = threshold;

	}

	public List<String> getWordChoice() {
		return choice;
	}

	public void setWordChoice(List<String> choice) {
		this.choice = choice;
	}

	@Override
	public Set<SensorType> requiredSensors() {
		return sensors;
	}

	@Override
	public boolean receiveSensordata(SensorType type, ISensorData data) {
		assert (type == SensorType.TextInput || type == SensorType.CodeScanner);

		StringSensorData stringdata = (StringSensorData) data;

		long start = System.nanoTime();

		float bestRating = 0;
		String bestMatch = null;
		String desiredString = null;

		loop: for (String p : stringdata.getMultipleString()) {
			if (StringFunction.nullOrWhiteSpace(p))
				continue;
			String possible = p.toLowerCase();
			for (String c : choice) {
				if (StringFunction.nullOrWhiteSpace(c))
					continue;
				String choiceString = c.toLowerCase();
				float rating;
				if (matchingThreshold == 1) {
					rating = possible.equals(choiceString) ? 1 : 0;
				} else {
					int norm = StringFunction.leventsteinNorm(possible,
							choiceString);
					// 1 == 100% match 0 == 0% match
					rating = 1 - (norm / (float) choiceString.length());
				}
				if (rating >= matchingThreshold && rating > bestRating) {
					bestRating = rating;
					bestMatch = possible;
					desiredString = choiceString;
					if (rating == 1)
						break loop;
				}
			}
		}

		boolean success = false;
		if (bestMatch != null && desiredString != null) // match found
		{
			System.out.print("Time: " + (System.nanoTime() - start) / 1000000
					+ "ms");
			success = true;
			System.out.print(String.format(
					"rate=%.2f Matched: '%s', input: '%s'", bestRating,
					desiredString, bestMatch));
		}
		if (isOnGoing() && success) {
			setProgress(QuestProgressStatus.Succeeded);
			setFinished(true);
		} else if (isFulfilled() && !success)
			setProgress(QuestProgressStatus.OnGoing);
		else
			return false;
		return true;
	}

	@Override
	public boolean needsNewSensordata(SensorType type) {
		return this.requiredSensors().contains(type) && !isFailed()
				&& !isFinished();
	}
}