package com.vsis.drachenmobile.helper;

import android.content.Context;
import android.graphics.Bitmap;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.vsis.drachen.exception.InvalidParameterException;
import com.vsis.drachen.exception.InvalidParameterException.InvalidType;
import com.vsis.drachen.model.quest.QuestProgressStatus;
import com.vsis.drachen.model.world.Location;
import com.vsis.drachen.util.StringFunction;
import com.vsis.drachenmobile.R;
import com.vsis.drachenmobile.service.AndroidDrachenResourceService;

/**
 * 
 * Defines static methods for utility.
 */
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
	 *            Progress which will be described
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
	 *            Progress which will be colored.
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
	 * Set the Image (SVG or Bitmap) of the {@link SVGImageView} to the resource
	 * with imageKey. if no image is found an error image or no image is shown.
	 * 
	 * @param imageView
	 *            target {@link SVGImageView}.
	 * @param resourceService
	 *            resource service.
	 * @param imageKey
	 *            key of the image. (it can point to svg or bitmap)
	 * @param noImage
	 *            if true no image will be shown; if false the error image will
	 *            be shown.
	 */
	public static void setImage(SVGImageView imageView,
			AndroidDrachenResourceService resourceService, String imageKey,
			boolean noImage) {

		SVG svg = null;
		Bitmap bitmap = null;
		if (!StringFunction.nullOrWhiteSpace(imageKey)) {
			if (imageKey.endsWith(".svg"))
				svg = resourceService.getSVGOrNull(imageKey);
			else
				bitmap = resourceService.getBitmapOrNotFound(imageKey);
		}

		if (svg != null)
			imageView.setSVG(svg);
		else if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else if (noImage)
			imageView.setImageBitmap(null);
		else
			imageView.setSVG(resourceService.getNotFoundSVG());
	}
}
