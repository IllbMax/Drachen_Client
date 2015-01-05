package com.vsis.drachenmobile.helper;

import android.content.Context;

import com.vsis.drachen.exception.InvalidParameterException;
import com.vsis.drachen.exception.InvalidParameterException.InvalidType;
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

	/**
	 * Returns for an {@link InvalidParameterException} a human readable string
	 * 
	 * @param ctx
	 *            the context
	 * @param e
	 *            the Exception
	 * @return localized string describing the exception
	 */
	public static String getErrorStringForInvalidParameter(Context ctx,
			InvalidParameterException e) {
		String message = "";
		Integer number = null;
		if (e.getType() == InvalidType.TooLong
				|| e.getType() == InvalidType.TooShort)
			try {
				number = Integer.parseInt(e.getExtraInfo());
			} catch (Exception e2) {
			}

		switch (e.getType()) {
		case TooLong:
			if (number == null)
				message = ctx.getString(R.string.param_s_too_long,
						e.getParameter());
			else
				message = ctx.getString(R.string.param_s_too_long_max_char,
						e.getParameter(), number);
			break;
		case TooShort:
			if (number == null)
				message = ctx.getString(R.string.param_s_too_short,
						e.getParameter());
			else
				message = ctx.getString(R.string.param_s_too_short_min_char,
						e.getParameter(), number);

			break;
		case WrongFormat:
			message = ctx.getString(R.string.param_s_wrongformat,
					e.getParameter());
			break;
		case NotUnique:
			message = ctx.getString(R.string.param_s_not_unique,
					e.getParameter());
			break;

		default:
			message = e.getMessage();
			break;

		}
		return message;
	}

	/**
	 * returns true if the string is null or empty
	 * 
	 * @param string
	 *            tested string
	 * @return true if the string is null or empty
	 */
	public static boolean nullOrEmpty(String string) {
		return string == null || string.equals("");
	}

	/**
	 * returns true if the string is null or empty or whitespace
	 * 
	 * @param string
	 *            tested string
	 * @return true if the string is null or empty or whitespace
	 */
	public static boolean nullOrEmptyOrWS(String string) {
		return string == null || string.trim().equals("");
	}
}
