package com.apv.accelerate.miloAR.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.apv.accelerate.miloAR.R;

public class SplashScreen extends Activity {
	private static final int STOPSPLASH = 0;
	private static final long SPLASHTIME = 5000;

	private RelativeLayout layout;
	private ImageView imageView;
	private Intent intent;

	Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STOPSPLASH:
				layout.setVisibility(View.GONE);
				intent = new Intent();
				intent.setClass(getApplicationContext(), SearchPage.class);
				startActivity(intent);
				finish();
				break;
			}
			super.handleMessage(msg);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		imageView = new ImageView(getApplicationContext());
		imageView.setBackgroundResource(R.drawable.milo_logo);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout = new RelativeLayout(this);
		layout.setGravity(Gravity.CENTER);
		layout.setBackgroundColor(Color.WHITE);
		layout.addView(imageView, lp);

		setContentView(layout);
		
		Message message = new Message();
		message.what = STOPSPLASH;
		messageHandler.sendMessageDelayed(message, SPLASHTIME);

	}
}