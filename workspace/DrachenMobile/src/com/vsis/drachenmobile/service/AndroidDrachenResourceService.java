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

public class AndroidDrachenResourceService extends ResourceService {

	private Context _ctx;

	public AndroidDrachenResourceService(Context ctx) {
		this._ctx = ctx;
	}

	public Bitmap getBitmap(String id) throws IOException {
		ResourceStream resStream = null;
		try {
			resStream = getMediaResourceStream(ZIP_DIR_IMG + id);

			Bitmap myBitmap = BitmapFactory.decodeStream(resStream
					.getInputStream());

			return myBitmap;
			// ImageView imageView = (ImageView) findViewById(R.id.imageView);
			// imageView.setImageBitmap(myBitmap);
		} catch (FileNotFoundException e) {
			return BitmapFactory.decodeResource(_ctx.getResources(),
					R.drawable.imagenotfound);
		} finally {
			if (resStream != null)
				try {
					resStream.close();
				} catch (Throwable t) {
				}
		}
	}

	public Bitmap getBitmapOrNotFound(String id) {
		try {
			return getBitmap(id);
		} catch (IOException e) {
			e.printStackTrace();
			return BitmapFactory.decodeResource(_ctx.getResources(),
					R.drawable.imagenotfound);
		}
	}

	public SVG getSVG(String id) throws IOException, SVGParseException {
		ResourceStream resStream = null;
		try {
			resStream = getMediaResourceStream(ZIP_DIR_IMG + id);

			SVG mySVG = SVG.getFromInputStream(resStream.getInputStream());
			return mySVG;

		} catch (FileNotFoundException e) {
			return SVG.getFromAsset(_ctx.getAssets(), "imagenotfound.svg");
		} finally {
			if (resStream != null)
				try {
					resStream.close();
				} catch (Throwable t) {
				}
		}

	}

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

	private SVG getNotFoundSVG() {
		try {
			return SVG.getFromAsset(_ctx.getAssets(), "imagenotfound.svg");
		} catch (SVGParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
