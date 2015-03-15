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
import com.vsis.drachen.model.quest.QuestProgressStatus;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.SensorListener;
import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.ISensorData;

/**
 * 
 * Service managing the {@link ISensor}s. Distributes the received
 * {@link ISensorData} to the {@link QuestTarget}s and other
 * {@link ISensorSensitive} objects.
 */
public class SensorService {

	public interface OnQuestTargetChangedListener {
		void onQuestTargetChanged(QuestTarget qt);
	}

	/**
	 * Representing a Item in the Sensor list, managing all sensors (and data)
	 * of a specific {@link SensorType}.
	 * 
	 * There can only be one active (default) sensor for each {@link SensorType}
	 * .
	 * 
	 * It implements the {@link SensorListener} interface to receive the
	 * {@link ISensorData} from the default sensor.
	 * 
	 */
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

		/**
		 * Determines if the default Sensor is running
		 * 
		 * @return
		 */
		public boolean isRunning() {
			return getDefaultSensor() != null && getDefaultSensor().isRunning();
		}

		/**
		 * Determines if the default Sensor is available
		 * 
		 * @return
		 */
		public boolean isAvailable() {
			return getDefaultSensor() != null && getDefaultSensor().isRunning();
		}

		/**
		 * Start the default sensor
		 */
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

		/**
		 * Add a sensor to sensors associated the the {@link SensorType}
		 * 
		 * @param sensor
		 *            new sensor for the {@link SensorType}
		 */
		public void registerSensor(ISensor sensor) {

			_registeredSensors.add(sensor);
		}

		/**
		 * Removes the sensor from the list of associated sensors
		 * 
		 * @param sensor
		 *            the to be removed sensor
		 */
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
						sensor.stop(); // maybe ISensor should get a dispose
										// method
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

		/**
		 * Register a lister which will receive data from the sensor
		 * 
		 * @param ss
		 *            new listener
		 */
		public void addSensorReceiver(ISensorSensitive ss) {
			_sensorDataReveiver.add(ss);
		}

		/**
		 * Removes the listener from, so it will receive no more sensor data
		 * 
		 * @param ss
		 *            the old listener
		 * @return true if the listener was removed successful
		 */
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

	/**
	 * Register listener for the QuestTargetChanged (if
	 * {@link QuestProgressStatus} was changed) Event.
	 * 
	 * @param lst
	 *            the new listener
	 */
	public void registerQuestTargetChangedListener(
			OnQuestTargetChangedListener lst) {
		_questTargetListeners.add(lst);
	}

	/**
	 * removes the listener from the QuestTargetChaged event.
	 * 
	 * @param lst
	 *            the old listener
	 */
	public void unregisterQuestTargetChangedListener(
			OnQuestTargetChangedListener lst) {
		_questTargetListeners.remove(lst);
	}

	/**
	 * Calls all listener of the QuestTagetChanged event with the parameter qt
	 * 
	 * @param qt
	 *            parameter of the listener function
	 */
	private void CallOnQuestTargetChangedListerns(QuestTarget qt) {
		for (OnQuestTargetChangedListener lst : _questTargetListeners)
			lst.onQuestTargetChanged(qt);
	}

	/**
	 * Registers a sensor for the {@link SensorType} type
	 * 
	 * @param type
	 *            datatype of the sensor
	 * @param sensor
	 *            new sensor
	 */
	public void registerSensor(SensorType type, ISensor sensor) {
		_map.get(type).registerSensor(sensor);
	}

	/**
	 * Unregisters a sensor for the {@link SensorType} type
	 * 
	 * @param type
	 *            datatype of the sensor
	 * @param sensor
	 *            old sensor
	 */
	public void unregisterSensor(SensorType type, ISensor sensor) {
		_map.get(type).unregisterSensor(sensor);
	}

	/**
	 * Registers a sensor for the {@link SensorType} type (if its not
	 * registered) and set the sensor as default.
	 * 
	 * @param type
	 *            datatype of the sensor
	 * @param sensor
	 *            new sensor
	 */
	public void setDefaultSensor(SensorType type, ISensor sensor) {
		_map.get(type).setDefaultSensor(sensor);
	}

	/**
	 * Return the default sensor for the specific type
	 * 
	 * @param type
	 *            Type of the sensor data
	 * @return the default sensor or null
	 */
	public ISensor getDefaultSensor(SensorType type) {
		return _map.get(type).getDefaultSensor();
	}

	/**
	 * Determines if the target should receive new sensordata
	 * 
	 * @param questTarget
	 *            the target to be checked
	 * @return true if the {@link QuestTarget} should get new sensordata
	 */
	public boolean isTargetTracked(QuestTarget questTarget) {
		return questTarget.getTrackTarget();
		// return _trackedQuests.contains(questTarget);
	}

	/**
	 * Add the {@link QuestTarget} to the list so that the target can receive
	 * new sensordata
	 * 
	 * @param questTarget
	 *            Target that should receive sensor data
	 */
	public void trackQuestTarget(QuestTarget questTarget) {
		// TODO: create own method for next 2 lines
		System.out.println(questTarget.getName());
		for (SensorType st : questTarget.requiredSensors())
			_map.get(st).addSensorReceiver(questTarget);
		questTarget.setTrackTarget(true);
		_trackedQuests.add(questTarget);
	}

	/**
	 * Removes the target from the list so that no more sensor data is send to
	 * the target.
	 * 
	 * @param questTarget
	 *            target that will receive no more sensor data
	 */
	public void untrackQuestTarget(QuestTarget questTarget) {
		questTarget.setTrackTarget(false);
		_trackedQuests.remove(questTarget);
		for (SensorType st : questTarget.requiredSensors())
			_map.get(st).removeSensorReceiver(questTarget);

	}

	/**
	 * Tracks all target of the Quest
	 * 
	 * @see SensorService#trackQuestTarget(QuestTarget)
	 * @param quest
	 *            quest with the targets
	 */
	public void trackQuest(Quest quest) {
		for (QuestTarget questTarget : quest.getQuestTargets())
			trackQuestTarget(questTarget);
	}

	/**
	 * Untracks all target of the Quest
	 * 
	 * @see SensorService#untrackQuestTarget(QuestTarget)
	 * @param quest
	 *            quest with the targets
	 */
	public void untrackQuest(Quest quest) {
		for (QuestTarget questTarget : quest.getQuestTargets())
			untrackQuestTarget(questTarget);
	}

	/**
	 * Add a {@link ISensorSensitive} to the receiver list.
	 * 
	 * @param ss
	 *            the new listener
	 */
	public void trackSensorReceiver(ISensorSensitive ss) {
		if (ss instanceof QuestTarget)
			trackQuestTarget((QuestTarget) ss);
		for (SensorType st : ss.requiredSensors())
			_map.get(st).addSensorReceiver(ss);
	}

	/**
	 * Removes the {@link ISensorSensitive} from the tracking list
	 * 
	 * @param ss
	 *            The {@link ISensorSensitive} that will receive no more data.
	 */
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

	/**
	 * Determines if the required sensor is running
	 * 
	 * @param st
	 *            SensorTypes which should checked
	 * @return true if the default sensor of {@link SensorType} st is running
	 */
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

	/**
	 * Determines if the required sensor is available
	 * 
	 * @param st
	 *            SensorTypes which should checked
	 * @return true if the default sensor of {@link SensorType} st is available
	 */
	public boolean sensorAvailable(SensorType st) {
		return _map.get(st).isAvailable();

	}

	/**
	 * Update the {@link QuestTarget}s with the new sensor data and updates the
	 * changed status (if needed) at the server.
	 * 
	 * you can enter here asynchrony process if needed.
	 * 
	 * 
	 * @param questTargets
	 *            target with will get the new sensordata
	 * @param type
	 *            type of the data
	 * @param data
	 *            the new sensor data
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

	/**
	 * Updates all {@link ISensorSensitive} with the new data.
	 * 
	 * @param dataReceiver
	 *            list of data {@link ISensorSensitive} which will get the new
	 *            sensor data
	 * 
	 * @param type
	 *            type of the data
	 * @param data
	 *            the new sensor data
	 */
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
	 * to be activated to get data once.
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
