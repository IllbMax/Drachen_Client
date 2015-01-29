package com.vsis.drachen;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.model.ISensorSensitive;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.SensorListener;
import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.ISensorData;

public class SensorService {

	public interface OnQuestTargetChangedListener {
		void onQuestTargetChanged(QuestTarget qt);
	}

	class SensorMapItem implements SensorListener {
		List<ISensor> _registeredSensors;
		private ISensor _defaultSensor;

		List<ISensorSensitive> _sensorDataReveiver;
		SensorType _type;
		ISensorData _lastSensorData;

		public SensorMapItem(SensorType type) {
			_registeredSensors = new ArrayList<ISensor>();
			_sensorDataReveiver = new ArrayList<ISensorSensitive>();
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

		public void dispose() {
			unregisterDefaultListener();

			for (ISensor sensor : _registeredSensors) {
				if (sensor != null) {
					try {
						sensor.stop();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
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
			if (_defaultSensor != null)
				_defaultSensor.addListerner(this);
		}

		private void unregisterDefaultListener() {
			if (_defaultSensor != null)
				_defaultSensor.removeListerner(this);
		}

		@Override
		public void newSensorData(ISensorData data) {
			System.out
					.println("recv new newSensorData: " + _type + ", " + data);
			List<QuestTarget> updateTargets = new LinkedList<QuestTarget>();
			List<ISensorSensitive> other = new LinkedList<ISensorSensitive>();
			for (ISensorSensitive ss : _sensorDataReveiver)
				if (ss.needsNewSensordata(_type)
				// && sensorsRunning(ss.requiredSensors())) {
				) {
					if (ss instanceof QuestTarget) {
						QuestTarget qt = (QuestTarget) ss;
						if (isTargetTracked(qt))
							updateTargets.add(qt);

					} else {
						other.add(ss);
					}
				}

			_lastSensorData = data;

			sendSensorData_Other(other, _type, data);
			sendSensorData_QuestTargets(updateTargets, _type, data);
		}

		public void addSensorReceiver(ISensorSensitive ss) {
			_sensorDataReveiver.add(ss);
		}

		public boolean removeSensorReceiver(ISensorSensitive ss) {
			return _sensorDataReveiver.remove(ss);
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
			_map.get(st).addSensorReceiver(questTarget);
		questTarget.setTrackTarget(true);
		_trackedQuests.add(questTarget);
	}

	public void untrackQuestTarget(QuestTarget questTarget) {
		questTarget.setTrackTarget(false);
		_trackedQuests.remove(questTarget);
		for (SensorType st : questTarget.requiredSensors())
			_map.get(st).removeSensorReceiver(questTarget);

	}

	public void trackQuest(Quest quest) {
		for (QuestTarget questTarget : quest.getQuestTargets())
			trackQuestTarget(questTarget);
	}

	public void untrackQuest(Quest quest) {
		for (QuestTarget questTarget : quest.getQuestTargets())
			untrackQuestTarget(questTarget);
	}

	public void trackSensorReceiver(ISensorSensitive ss) {
		if (ss instanceof QuestTarget)
			trackQuestTarget((QuestTarget) ss);
		for (SensorType st : ss.requiredSensors())
			_map.get(st).addSensorReceiver(ss);
	}

	public void untrackSensorReceiver(ISensorSensitive ss) {
		if (ss instanceof QuestTarget)
			untrackQuestTarget((QuestTarget) ss);
		for (SensorType st : ss.requiredSensors())
			_map.get(st).removeSensorReceiver(ss);
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
	protected void sendSensorData_QuestTargets(List<QuestTarget> questTargets,
			SensorType type, ISensorData data) {

		for (QuestTarget qt : questTargets) {
			synchronized (qt) {
				boolean update = qt.receiveSensordata(type, data);
				System.out.println(qt.getName() + ": " + update);
				if (update) {
					Boolean su;
					try {
						su = _client.UpdateQuestTarget(qt);
					} catch (DrachenBaseException e) {
						// TODO: handle the other exceptions
						e.printStackTrace();
						su = false;
					}
					// TODO: if false return qt to previous state or try to send
					// the change later, depending on the exception (eg.
					// QuestFinishedException -> update the local quest/target)
					System.out.println(qt.getName() + ", Server: " + su);

					// TODO: if true notify System that there is a change
					if (su != null && su)
						CallOnQuestTargetChangedListerns(qt);
				}
			}

		}
	}

	protected void sendSensorData_Other(List<ISensorSensitive> dataReceiver,
			SensorType type, ISensorData data) {

		for (ISensorSensitive ss : dataReceiver) {
			synchronized (ss) {
				ss.receiveSensordata(type, data);
			}

		}
	}

	public void dispose() {
		// stop all sensors
		for (SensorMapItem item : _map.values()) {
			item.dispose();
		}

	}

	/**
	 * lists all available Sensor for non background observation. So these needs
	 * to be activated to get data once
	 * 
	 * @return list of such sensors
	 */
	public List<ISensor> getQuickAccessSensors() {
		ArrayList<ISensor> result = new ArrayList<ISensor>();
		for (SensorType type : _map.keySet()) {
			if (type.isBackgroundSensor())
				continue;
			SensorMapItem item = _map.get(type);
			ISensor sensor = item.getDefaultSensor();
			if (sensor != null && sensor.isAvailable())
				result.add(sensor);
		}

		return result;
	}

}
