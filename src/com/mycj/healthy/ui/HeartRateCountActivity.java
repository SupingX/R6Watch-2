package com.mycj.healthy.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.mycj.healthy.R;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.view.HeartRateFrameLayout;
import com.mycj.healthy.view.HeartRateFrameLayout.OnHeartRateChangeListener;
import com.mycj.healthy.view.HeartRatePathView;
import com.mycj.healthy.view.SimpleHeartRateView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HeartRateCountActivity extends Activity {
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				mHandler.postDelayed(run, 1000);
				break;

			default:
				break;
			}
		};
	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED)) {
			} else if (action.equals(LiteBlueService.LITE_GATT_DISCONNECTED)) {
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_HEART_RATE)) {
				final int hr = intent.getExtras().getInt(LiteBlueService.EXTRA_DATA_HEART_RATE);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						frameHr.setData(hr);
					}
				});
			}
		}
	};
	private ImageView imgBack;
	private HeartRateFrameLayout frameHr;
	
	private Runnable run = new Runnable() {
		@Override
		public void run() {
			Random r = new Random();
			int f = r.nextInt(200);
			frameHr.setData(f);
			mHandler.sendEmptyMessage(1);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heart_rate_count);
		frameHr = (HeartRateFrameLayout) findViewById(R.id.frame_heartrate);
		frameHr.setOnHeartRateChangeListener(new OnHeartRateChangeListener() {
			@Override
			public void onChange(List<Integer> heartRates) {
				Log.e("HeartRateFragment", "心率数据增加中,size : " +heartRates.size());
				
			}
		});
		
		imgBack = (ImageView) findViewById(R.id.img_back);
		imgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	protected void onResume() {
		registerReceiver(mReceiver, LiteBlueService.getIntentFilter());
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		mHandler.removeCallbacks(run);
		super.onDestroy();
	}

	public void add(View v) {
		if (run != null) {
			mHandler.post(run);
		}
		
	}

}
