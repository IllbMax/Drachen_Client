package com.vsis.drachenmobile.helper;

import android.content.Context;

import com.vsis.drachen.model.quest.QuestProgressStatus;
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

	/**
	 * Returns text describing the (Quest)status
	 * 
	 * @param finished
	 * 
	 * @param ctx
	 *            The Context
	 * @param progress
	 * @return string for the status
	 */
	public static String getProgressDisplayText(Context ctx, boolean finished,
			QuestProgressStatus progress) {
		int id;
		switch (progress) {
		case OnGoing:
			id = R.string.status_ongoing;
			break;
		case Failed:
			id = R.string.status_failed;
			break;
		case Succeeded:
			id = finished ? R.string.status_succeededFin
					: R.string.status_succeeded;
			break;
		default:
			throw new Error("something is strange");

		}
		return ctx.getResources().getString(id);
	}

	/**
	 * Returns color describing the (Quest)status
	 * 
	 * @param finished
	 * 
	 * @param ctx
	 *            The Context
	 * @param progress
	 * @return color for the status
	 */
	public static int getProgressDisplayColor(Context ctx, boolean finished,
			QuestProgressStatus progress) {
		int id;
		switch (progress) {
		case OnGoing:
			id = R.color.status_ongoing;
			break;
		case Failed:
			id = R.color.status_failed;
			break;
		case Succeeded:
			id = finished ? R.color.status_succeededFin
					: R.color.status_succeeded;
			break;
		default:
			throw new Error("something is strange");

		}
		return ctx.getResources().getColor(id);
	}
}
