package com.vsis.drachenmobile.service;

import java.util.EnumSet;
import java.util.Set;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.vsis.drachen.LocationService;
import com.vsis.drachen.LocationService.LocationChanged;
import com.vsis.drachen.SensorService;
import com.vsis.drachen.SensorService.OnQuestTargetChangedListener;
import com.vsis.drachen.model.ISensorSensitive;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachen.model.world.Location;
import com.vsis.drachen.model.world.Point;
import com.vsis.drachen.sensor.ISensor;
import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.GPSSensorData;
import com.vsis.drachen.sensor.data.ISensorData;
import com.vsis.drachen.sensor.data.StringSensorData;
import com.vsis.drachenmobile.DrachenApplication;
import com.vsis.drachenmobile.sensor.AccelarationSensor;
import com.vsis.drachenmobile.sensor.GPSSensor;
import com.vsis.drachenmobile.sensor.LocationSensor;
import com.vsis.drachenmobile.sensor.SpeechSensor;
import com.vsis.drachenmobile.sensor.StringInputSensor;
import com.vsis.drachenmobile.sensor.ZXingScannerSensor;

/**
 * Local {@link Service} that coordinates primely {@link Location} related
 * operations in the background.
 */
public class LocationLocalService extends Service {

	public class MyBinder extends Binder {
		LocationLocalService getService() {
			return LocationLocalService.this;
		}
	}

	private final IBinder _binder = new MyBinder();
	private LocationService locationService;
	private ISensorSensitive positionListener;
	private LocationChanged drachenLocationListener;
	private OnQuestTargetChangedListener questTargetListener;

	@Override
	public void onCreate() {
		super.onCreate();
		// initialize();
	}

	/**
	 * Initialize the {@link ISensor}s, {@link OnQuestTargetChangedListener},
	 * GPS sensor listener to update current user location and Code-Scanner
	 * sensor to enter rooms.
	 */
	public void initialize() {
		DrachenApplication app = (DrachenApplication) getApplication();
		locationService = app.getAppData().getLocationService();
		SensorService sensorService = app.getAppData().getSensorService();

		installPositionListener();
		sensorService.trackSensorReceiver(positionListener);
		installDrachenLocationListener();

		questTargetListener = new OnQuestTargetChangedListener() {

			@Override
			public void onQuestTargetChanged(QuestTarget qt) {
				broadcastQuestTargetChanged(qt);
			}
		};
		sensorService.registerQuestTargetChangedListener(questTargetListener);

		GPSSensor gpsSensor = new GPSSensor("GPS Sensor", this);
		LocationSensor locationSensor = new LocationSensor("Location Sensor",
				this);
		AccelarationSensor accelSensor = new AccelarationSensor(
				"Accelaration Sensor", this);
		boolean speech = true;
		ISensor stringSensor = speech ? new SpeechSensor("Speech input",
				getApplicationContext()) : new StringInputSensor("Inputdialog",
				getApplicationContext());
		ISensor codeSensor = new ZXingScannerSensor("ZXing Codescanner",
				getApplicationContext());

		sensorService.registerSensor(SensorType.Position, gpsSensor);
		sensorService.registerSensor(SensorType.Location, locationSensor);
		sensorService.registerSensor(SensorType.Accelaration, accelSensor);
		sensorService.registerSensor(SensorType.TextInput, stringSensor);
		sensorService.registerSensor(SensorType.CodeScanner, codeSensor);

		sensorService.setDefaultSensor(SensorType.Position, gpsSensor);
		sensorService.setDefaultSensor(SensorType.Location, locationSensor);
		sensorService.setDefaultSensor(SensorType.Accelaration, accelSensor);
		sensorService.setDefaultSensor(SensorType.TextInput, stringSensor);
		sensorService.setDefaultSensor(SensorType.CodeScanner, codeSensor);

		gpsSensor.start();
		locationSensor.start();
		accelSensor.start();

		ISensorSensitive listener = new ISensorSensitive() {

			@Override
			public Set<SensorType> requiredSensors() {
				return EnumSet.of(SensorType.CodeScanner, SensorType.TextInput);
			}

			@Override
			public boolean receiveSensordata(SensorType type, ISensorData data) {

				StringSensorData sdata = (StringSensorData) data;

				String input = sdata.getFirstString();

				// showToastInThread(input);
				// Toast.makeText(getApplicationContext(), input,
				// Toast.LENGTH_SHORT).show();

				String[] tokens = input.split(":");
				if (tokens[0].equals("location") && tokens.length >= 2) {

					// int locationId = Integer.parseInt(tokens[1]);
					String scannerKey = tokens[1].trim();

					// Location room = locationService
					// .getLocationForId(locationId);
					Location room = locationService
							.getLocationFromScannerKey(scannerKey);
					if (room != null) {
						locationService.SetRegion(room);
					}
				}

				return false;
			}

			@Override
			public boolean needsNewSensordata(SensorType type) {
				// TODO Auto-generated method stub
				return true;
			}
		};
		sensorService.trackSensorReceiver(listener);
	}

	/**
	 * Initialize the GPS sensor to update the current {@link Location}
	 */
	private void installPositionListener() {
		positionListener = new ISensorSensitive() {
			private final EnumSet<SensorType> sensors = EnumSet
					.of(SensorType.Position);

			@Override
			public Set<SensorType> requiredSensors() {
				return sensors;
			}

			@Override
			public boolean receiveSensordata(SensorType type, ISensorData data) {
				// assert type == SensorType.Position;
				GPSSensorData gpsdata = (GPSSensorData) data;
				makeUseOfNewLocation(gpsdata);
				// this return is not important
				return false;
			}

			@Override
			public boolean needsNewSensordata(SensorType type) {
				// Always needs the data (if Location change is provided by
				// other input than maybe not...)
				return sensors.contains(type);
			}
		};

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initialize();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return _binder;
	}

	/**
	 * Initialize the {@link BroadcastReceiver} for {@link Location} changes
	 */
	private void installDrachenLocationListener() {
		drachenLocationListener = new LocationChanged() {

			@Override
			public void Changed(Location oldname, Location newname) {
				broadcastLocationChange(oldname, newname);
			}
		};
		locationService.RegisterListener(drachenLocationListener);
	}

	/**
	 * determines the current {@link Location} from the {@link GPSSensorData}
	 * gps and calls the {@link LocationService} to update the {@link Location}.
	 * 
	 * @param gps
	 */
	protected void makeUseOfNewLocation(final GPSSensorData gps) {
		if (!locationService.isInRoom()) {
			Point p = new Point(gps.getLatitude(), gps.getLongitude());
			final com.vsis.drachen.model.world.Location loc = locationService
					.getLoationFromPoint(p);

			Handler handler = new Handler(getMainLooper());
			handler.post(new Runnable() {

				@Override
				public void run() {
					// String data = String.format("Lat: %f, Lon: %f",
					// gps.getLatitude(), gps.getLongitude());
					// Toast.makeText(LocationLocalService.this, data,
					// Toast.LENGTH_SHORT).show();

					if (loc == null) {
						// Toast.makeText(LocationLocalService.this,
						// "no location",
						// Toast.LENGTH_SHORT).show();
					} else {
						AsyncTask<Location, Void, Boolean> task = new AsyncTask<Location, Void, Boolean>() {

							@Override
							protected Boolean doInBackground(Location... params) {
								Location loc = params[0];
								boolean success = locationService
										.SetRegion(loc);
								return success;
							}

							@Override
							protected void onPostExecute(Boolean result) {
								if (result != null && result) {
									Toast.makeText(LocationLocalService.this,
											"location:" + loc.getName(),
											Toast.LENGTH_SHORT).show();
								}
							};

						};
						task.execute(loc);

					}
				}
			});
		}

	}

	/**
	 * Starts a local broadcast to notify the QuestTargetChangeEvent
	 * 
	 * @param qt
	 *            QuestTarget that has changed
	 */
	private void broadcastQuestTargetChanged(QuestTarget qt) {
		Intent intent = new Intent(DrachenApplication.EVENT_QUESTTARGET_CHANGED);

		intent.putExtra(DrachenApplication.EXTRA_QUEST_ID, qt.getQuest()
				.getId());
		intent.putExtra(DrachenApplication.EXTRA_QUESTTARGET_ID, qt.getId());

		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	/**
	 * Starts a local broadcast to notify the LocationChangeEvent
	 * 
	 * @param old
	 *            the old Location (or null)
	 * @param now
	 *            the new Location (or null)
	 */
	private void broadcastLocationChange(Location old, Location now) {
		Intent intent = new Intent(DrachenApplication.EVENT_LOCATION_CHANGED);

		intent.putExtra(DrachenApplication.EXTRA_LOCATION_OLD,
				old != null ? old.getId() : -1);
		intent.putExtra(DrachenApplication.EXTRA_LOCATION_NEW,
				now != null ? now.getId() : -1);

		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	public void showToastInThread(final String str) {

		Looper.prepare();
		MessageQueue queue = Looper.myQueue();
		queue.addIdleHandler(new IdleHandler() {
			int mReqCount = 0;

			@Override
			public boolean queueIdle() {
				if (++mReqCount == 2) {
					Looper.myLooper().quit();
					return false;
				} else
					return true;
			}
		});
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		Looper.loop();
	}

}
