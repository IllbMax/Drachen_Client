package com.vsis.drachen;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.visis.drachen.sensor.ISensor;
import com.visis.drachen.sensor.SensorListener;
import com.visis.drachen.sensor.SensorType;
import com.visis.drachen.sensor.data.ISensorData;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestTarget;

public class SensorService {

	public interface OnQuestTargetChangedListener {
		void onQuestTargetChanged(QuestTarget qt);
	}

	class SensorMapItem implements SensorListener {
		List<ISensor> _registeredSensors;
		private ISensor _defaultSensor;

		List<QuestTarget> _questTargets;
		SensorType _type;
		ISensorData _lastSensorData;

		public SensorMapItem(SensorType type) {
			_registeredSensors = new ArrayList<ISensor>();
			_questTargets = new ArrayList<QuestTarget>();
			_type = type;
		}

		public boolean isRunning() {
			return getDefaultSensor() != null && getDefaultSensor().isRunning();
		}

		public boolean isAvailable() {
			return getDefaultSensor() != null && getDefaultSensor().isRunning();
		}

		public void startDefault() {
			ISensor s = getDefaultSensor();
			if (s.isRunning())
				return;
			if (s.isAvailable()) {
				if (s.isStopped())
					s.start();
				else if (s.isPaused())
					s.resume();
				else {
					// TODO: Sensor invalid State
				}
			} else {
				// TODO: throw exception sensor not available
			}
		}

		public void registerSensor(ISensor sensor) {

			_registeredSensors.add(sensor);
		}

		public void unregisterSensor(ISensor sensor) {
			sensor.stop();
			if (sensor.equals(_defaultSensor))
				unregisterDefaultListener();
			_registeredSensors.remove(sensor);
		}

		public ISensor getDefaultSensor() {
			return _defaultSensor;
		}

		public void setDefaultSensor(ISensor sensor) {
			if (_defaultSensor == sensor)
				return;
			if (_defaultSensor != null)
				unregisterDefaultListener();

			this._defaultSensor = sensor;
			if (sensor != null) {
				if (!_registeredSensors.contains(sensor))
					_registeredSensors.add(sensor);
				registerDefaultListener();
			}
		}

		private void registerDefaultListener() {
			_defaultSensor.addListerner(this);
		}

		private void unregisterDefaultListener() {
			_defaultSensor.removeListerner(this);
		}

		@Override
		public void newSensorData(ISensorData data) {
			System.out
					.println("recv new newSensorData: " + _type + ", " + data);
			List<QuestTarget> updateTargets = new LinkedList<QuestTarget>();
			for (QuestTarget qt : _questTargets)
				if (isTargetTracked(qt) && qt.needsNewSensordata(_type)
						&& sensorsRunning(qt.requiredSensors()))
					updateTargets.add(qt);

			_lastSensorData = data;

			sendSensorData(updateTargets, _type, data);
		}

		public void addQuestTarget(QuestTarget questTarget) {
			_questTargets.add(questTarget);
		}

	}

	private BlubClient _client;
	Map<SensorType, SensorMapItem> _map = new EnumMap<SensorType, SensorService.SensorMapItem>(
			SensorType.class);

	Set<QuestTarget> _trackedQuests = new HashSet<>();
	List<OnQuestTargetChangedListener> _questTargetListeners = new ArrayList<>();

	public SensorService(BlubClient client) {
		_client = client;
		initSensorTypeMap();
	}

	/**
	 * Initializes the Map
	 */
	private void initSensorTypeMap() {
		for (SensorType st : SensorType.values()) {
			_map.put(st, new SensorMapItem(st));
		}
	}

	public void registerQuestTargetChangedListener(
			OnQuestTargetChangedListener lst) {
		_questTargetListeners.add(lst);
	}

	public void unregisterQuestTargetChangedListener(
			OnQuestTargetChangedListener lst) {
		_questTargetListeners.remove(lst);
	}

	private void CallOnQuestTargetChangedListerns(QuestTarget qt) {
		for (OnQuestTargetChangedListener lst : _questTargetListeners)
			lst.onQuestTargetChanged(qt);
	}

	public void registerSensor(SensorType type, ISensor sensor) {
		_map.get(type).registerSensor(sensor);
	}

	public void unregisterSensor(SensorType type, ISensor sensor) {
		_map.get(type).unregisterSensor(sensor);
	}

	public void setDefaultSensor(SensorType type, ISensor sensor) {
		_map.get(type).setDefaultSensor(sensor);
	}

	public ISensor getDefaultSensor(SensorType type) {
		return _map.get(type).getDefaultSensor();
	}

	public boolean isTargetTracked(QuestTarget questTarget) {
		return questTarget.getTrackTarget();
		// return _trackedQuests.contains(questTarget);
	}

	public void trackQuestTarget(QuestTarget questTarget) {
		// TODO: create own method for next 2 lines
		System.out.println(questTarget.getName());
		for (SensorType st : questTarget.requiredSensors())
			_map.get(st).addQuestTarget(questTarget);
		questTarget.setTrackTarget(true);
		_trackedQuests.add(questTarget);
	}

	public void untrackQuestTarget(QuestTarget questTarget) {
		questTarget.setTrackTarget(false);
		_trackedQuests.remove(questTarget);
	}

	public void trackQuest(Quest quest) {
		for (QuestTarget questTarget : quest.getQuestTargets())
			trackQuestTarget(questTarget);
	}

	public void untrackQuest(Quest quest) {
		for (QuestTarget questTarget : quest.getQuestTargets())
			untrackQuestTarget(questTarget);
	}

	/**
	 * Determines if the required sensors are running
	 * 
	 * @param requiredSensors
	 *            Set of SensorTypes which should checked
	 * @return true if all requiredSensors are running
	 */
	public boolean sensorsRunning(Set<SensorType> requiredSensors) {
		for (SensorType st : requiredSensors)
			if (!sensorRunning(st))
				return false;

		return true;
	}

	public boolean sensorRunning(SensorType st) {
		return _map.get(st).isRunning();

	}

	/**
	 * Determines if the required sensors are available
	 * 
	 * @param requiredSensors
	 *            Set of SensorTypes which should checked
	 * @return true if all requiredSensors are available
	 */
	public boolean sensorsAvailable(Set<SensorType> requiredSensors) {
		for (SensorType st : requiredSensors)
			if (!sensorAvailable(st))
				return false;

		return true;
	}

	public boolean sensorAvailable(SensorType st) {
		return _map.get(st).isAvailable();

	}

	/**
	 * you can enter here asynchrony process if needed
	 * 
	 * @param questTargets
	 * @param type
	 * @param data
	 */
	protected void sendSensorData(List<QuestTarget> questTargets,
			SensorType type, ISensorData data) {

		for (QuestTarget qt : questTargets) {
			synchronized (qt) {
				boolean update = qt.receiveSensordata(type, data);
				System.out.println(qt.getName() + ": " + update);
				if (update) {
					Boolean su = _client.UpdateQuestTarget(qt);
					// TODO: if false return qt to previous state
					System.out.println(qt.getName() + ", Server: " + su);

					// TODO: if true notify System that there is a change
					if (su)
						CallOnQuestTargetChangedListerns(qt);
				}
			}

		}
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

}
