package com.android.CustomGallery;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		int[] images = { R.drawable.photo1, R.drawable.photo2,
				R.drawable.photo3, R.drawable.photo4, R.drawable.photo5,
				R.drawable.photo6, R.drawable.photo7, R.drawable.photo8, };

		ImageAdapter adapter = new ImageAdapter(this, images);
		adapter.createReflectedImages();

		GalleryFlow galleryFlow = (GalleryFlow) findViewById(R.id.gallery_flow);
		galleryFlow.setAdapter(adapter);

	}
}