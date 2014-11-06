package com.vsis.drachenmobile.settings;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class NumberEditTextPreference extends EditTextPreference {

	public NumberEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// public NumberEditTextPreference(Context context, AttributeSet attrs,
	// int defStyle) {
	// super(context, attrs, defStyle);
	// }

	// @Override
	// protected void onDialogClosed(boolean positiveResult) {
	// // TODO Auto-generated method stub
	// super.onDialogClosed(positiveResult);
	// String number = getPersistedString("");
	// if (number.isEmpty())
	// number = "0";
	// int n = Integer.parseInt(number);
	// persistInt(n);
	// }

	@Override
	protected boolean persistInt(int value) {
		// TODO Auto-generated method stub
		return super.persistString(String.valueOf(value));
	}

	@Override
	protected int getPersistedInt(int defaultReturnValue) {
		String n = super.getPersistedString(String.valueOf(defaultReturnValue));
		n = n.trim();
		if (n.isEmpty())
			n = String.valueOf(defaultReturnValue);
		return Integer.parseInt(n);
	}
}
