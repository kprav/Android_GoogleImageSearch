package com.codepath.googleimagesearch.touchimageview;

import android.app.Activity;
import android.os.Bundle;

import com.codepath.googleimagesearch.R;
import com.codepath.googleimagesearch.touchimageview.TouchImageView.OnTouchImageViewListener;

public class MirroringExampleActivity extends Activity {
	
	com.codepath.googleimagesearch.touchimageview.TouchImageView topImage;
	com.codepath.googleimagesearch.touchimageview.TouchImageView bottomImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mirroring_example);
		topImage = (com.codepath.googleimagesearch.touchimageview.TouchImageView) findViewById(R.id.topImage);
		bottomImage = (com.codepath.googleimagesearch.touchimageview.TouchImageView) findViewById(R.id.bottomImage);
		
		//
		// Each image has an OnTouchImageViewListener which uses its own TouchImageView
		// to set the other TIV with the same zoom variables.
		//
		topImage.setOnTouchImageViewListener(new OnTouchImageViewListener() {
			
			@Override
			public void onMove() {
				bottomImage.setZoom(topImage);
			}
		});
		
		bottomImage.setOnTouchImageViewListener(new OnTouchImageViewListener() {
			
			@Override
			public void onMove() {
				topImage.setZoom(bottomImage);
			}
		});
	}
}
