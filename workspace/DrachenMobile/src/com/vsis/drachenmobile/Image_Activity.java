package com.vsis.drachenmobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.caverock.androidsvg.SVGImageView;
import com.vsis.drachenmobile.helper.Helper;

public class Image_Activity extends Activity {

	public static final String EXTRA_IMAGEKEY = "imgkey";
	public static final String EXTRA_TITLE = "title";

	String _imageKey;
	String _title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

	}

	@Override
	protected void onResume() {
		super.onResume();
		_imageKey = getIntent().getExtras().getString(EXTRA_IMAGEKEY);
		_title = getIntent().getExtras().getString(EXTRA_TITLE, "");

		displayImage();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void displayImage() {
		MyDataSet appdata = ((DrachenApplication) getApplication())
				.getAppData();
		TextView textView_title = (TextView) findViewById(R.id.textview1);
		SVGImageView itemImage = (SVGImageView) findViewById(R.id.imageview1);

		Helper.setImage(itemImage, appdata.getResourceService(), _imageKey,
				true);
		textView_title.setText(_title);

	}
}
