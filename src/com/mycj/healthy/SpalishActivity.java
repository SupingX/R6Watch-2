package com.mycj.healthy;

import com.mycj.healthy.service.LiteBlueService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SpalishActivity extends Activity {

	private LiteBlueService mLiteBlueService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spalish);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SpalishActivity.this, MainActivity.class));
				finish();
			}
		}, 1000);

	}
}
