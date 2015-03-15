package com.vsis.drachenmobile.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.vsis.drachen.ResourceService;
import com.vsis.drachenmobile.R;

/**
 * 
 * Class that manages the Resources specific for Android systems. Uses
 * {@link Bitmap} and {@link SVG} as Media formats.
 */
public class AndroidDrachenResourceService extends ResourceService {

	private static final String IMAGENOTFOUND_SVG = "imagenotfound.svg";
	private Context _ctx;

	public AndroidDrachenResourceService(Context ctx) {
		this._ctx = ctx;
	}

	/**
	 * Get the Bitmap resource from the id.
	 * 
	 * @param id
	 *            Id of the resource
	 * @return the {@link Bitmap} that can be created from the resource
	 * @throws IOException
	 *             if eg. the file/resource was not found
	 */
	public Bitmap getBitmap(String id) throws IOException {
		ResourceStream resStream = null;
		try {
			resStream = getMediaResourceStream(ZIP_DIR_IMG + id);

			Bitmap myBitmap = BitmapFactory.decodeStream(resStream
					.getInputStream());

			return myBitmap;
		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			if (resStream != null)
				try {
					resStream.close();
				} catch (Throwable t) {
				}
		}
	}

	/**
	 * Get the Bitmap resource from the id or in exception case the not found
	 * image.
	 * 
	 * @param id
	 *            Id of the resource
	 * @return the {@link Bitmap} that can be created from the resource or the
	 *         image not found image
	 * 
	 */
	public Bitmap getBitmapOrNotFound(String id) {
		try {
			return getBitmap(id);
		} catch (IOException e) {
			e.printStackTrace();
			return BitmapFactory.decodeResource(_ctx.getResources(),
					R.drawable.imagenotfound);
		}
	}

	/**
	 * Get the Bitmap resource from the id or in exception case null image.
	 * 
	 * @param id
	 *            Id of the resource
	 * @return the {@link Bitmap} that can be created from the resource or null
	 * 
	 */
	public Bitmap getBitmapOrNull(String id) {
		try {
			return getBitmap(id);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the {@link SVG} resource from the id.
	 * 
	 * @param id
	 *            Id of the resource
	 * @return the {@link SVG} that can be created from the resource
	 * 
	 */
	public SVG getSVG(String id) throws IOException, SVGParseException {
		ResourceStream resStream = null;
		try {
			resStream = getMediaResourceStream(ZIP_DIR_IMG + id);

			SVG mySVG = SVG.getFromInputStream(resStream.getInputStream());
			return mySVG;

		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			if (resStream != null)
				try {
					resStream.close();
				} catch (Throwable t) {
				}
		}

	}

	/**
	 * Get the {@link SVG} resource from the id or in exception case the image
	 * not svg {@link AndroidDrachenResourceService#getNotFoundSVG()}.
	 * 
	 * @param id
	 *            Id of the resource
	 * @return the {@link SVG} that can be created from the resource or the not
	 *         found svg
	 * 
	 */
	public SVG getSVGOrNotFound(String id) {
		try {
			return getSVG(id);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SVGParseException e) {
			e.printStackTrace();
		}
		return getNotFoundSVG();
	}

	/**
	 * Get the {@link SVG} resource from the id or in exception case null
	 * 
	 * @param id
	 *            Id of the resource
	 * @return the {@link SVG} that can be created from the resource or null
	 * 
	 */
	public SVG getSVGOrNull(String id) {
		try {
			return getSVG(id);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SVGParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get the image not found SVG
	 * 
	 * @return the image not found SVG
	 */
	public SVG getNotFoundSVG() {
		try {
			return SVG.getFromAsset(_ctx.getAssets(), IMAGENOTFOUND_SVG);
		} catch (SVGParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}

}
