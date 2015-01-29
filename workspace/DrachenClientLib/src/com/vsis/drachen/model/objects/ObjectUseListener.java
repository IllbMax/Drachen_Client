package com.vsis.drachen.model.objects;

import java.util.Set;

import com.vsis.drachen.model.ISensorSensitive;
import com.vsis.drachen.model.IdObject;
import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.ISensorData;

public abstract class ObjectUseListener extends IdObject implements
		ISensorSensitive {

	public static interface IOnActionEventLister {
		void onActionRecognized(ObjectUseListener action, String Identifier);
	}

	public ObjectUseListener() {

	}

	public ObjectUseListener(ObjectEffect effect) {
		this.effect = effect;
	}

	private ObjectEffect effect;
	private IOnActionEventLister listener;

	public void setEffect(ObjectEffect effect) {
		this.effect = effect;
	}

	public ObjectEffect getEffect() {
		return effect;
	}

	@Override
	public abstract boolean needsNewSensordata(SensorType type);

	@Override
	public abstract Set<SensorType> requiredSensors();

	@Override
	public final boolean receiveSensordata(SensorType type, ISensorData data) {
		boolean result = receiveSensordata_internal(type, data);
		if (result)
			callListener("");
		return result;
	}

	protected abstract boolean receiveSensordata_internal(SensorType type,
			ISensorData data);

	private void callListener(String idetifier) {
		if (listener != null)
			listener.onActionRecognized(this, idetifier);
	}

	public void setOnActionEventListener(IOnActionEventLister listener) {
		this.listener = listener;
	}
}