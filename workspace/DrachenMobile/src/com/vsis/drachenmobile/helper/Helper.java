package com.vsis.drachenmobile.helper;

import android.content.Context;

import com.vsis.drachen.model.world.Location;
import com.vsis.drachenmobile.R;

public final class Helper {
	private Helper() {
	}

	/**
	 * Returns the display name of the location
	 * 
	 * @param ctx
	 *            The Context
	 * @param location
	 *            Location for the name
	 * @return unknown if location is null
	 */
	public static String getLocationDisplay(Context ctx, Location location) {
		return location != null ? location.getName() : ctx.getResources()
				.getString(R.string.unknown);

	}
}
