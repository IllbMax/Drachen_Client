package com.vsis.drachen.model;

import java.util.List;

/**
 * defines a 'second mode' beside questing.
 * 
 * After staring minigame mode all questing is on pause and only this minigame
 * receives sensordata (except non QuestTarget ISensorSensitve objects)
 * 
 * after ending a minigame a sensor event spread the outcome to eg. QuestTargets
 * 
 * fffffffffffffffffffffffffffffffffffffffffffgggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggga
 * asdasdasdasdasdaas
 */
public interface IMiniGame {

	public void begin();

	public void end();

	public void abort();

	public List<ISensorSensitive> getSensorReceiver();

}
