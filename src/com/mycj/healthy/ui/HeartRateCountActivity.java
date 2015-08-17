package com.mycj.healthy.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.litepal.crud.DataSupport;

import com.mycj.healthy.BaseActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.entity.HeartRateData;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.DataUtil;
import com.mycj.healthy.util.TimeUtil;
import com.mycj.healthy.view.HeartRateFrameLayout;
import com.mycj.healthy.view.HeartRateFrameLayout.OnHeartRateChangeListener;
import com.mycj.healthy.view.HeartRatePathView;
import com.mycj.healthy.view.SimpleHeartRateView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class HeartRateCountActivity extends BaseActivity implements OnClickListener{
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
//				final int hr = intent.getExtras().getInt(LiteBlueService.EXTRA_DATA_HEART_RATE);
//				final Date date = intent.getExtras().getParcelable(LiteBlueService.EXTRA_DATA_HEART_RATE_DATE);
				final HeartRateData mHeartRateData = intent.getExtras().getParcelable(LiteBlueService.EXTRA_DATA_HEART_RATE);
				Log.e("", "^^^^^^^^^^^^^^^mHeartRateData :" + mHeartRateData);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						HeartRateData data = new HeartRateData();
//						data.setHeartRate( hr );
//						data.setDate(date);
						frameHr.setData(mHeartRateData);
						if(!datas.contains(mHeartRateData)){
							datas.add(mHeartRateData);
						}
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
//			Random r = new Random();
//			int f = r.nextInt(200);
//			HeartRateData data = new HeartRateData();
//			data.setHeartRate(f);
//			data.setDate(new Date());
//			frameHr.setData(data);
//			mHandler.sendEmptyMessage(1);
//			HeartRateData hrData = new HeartRateData(new Date(), f);
//			new MyAsyncTask().execute(hrData);
		}
	};
	private ListView lv;
	private List<HeartRateData> datas = new ArrayList<>();


	private int sum;
	private TextView tvToday;
	private ImageView imgReduce;
	private ImageView imgIncrease;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heart_rate_count);
		initViews();
		setListener();

	}
	
	public void initViews(){
	frameHr = (HeartRateFrameLayout) findViewById(R.id.frame_heartrate);
		
		tvToday = (TextView) findViewById(R.id.tv_hr_now);
		tvMaxHr = (TextView) findViewById(R.id.tv_hr_max);
		tvMinHr = (TextView) findViewById(R.id.tv_hr_min);
		tvAvgHr = (TextView) findViewById(R.id.tv_hr_avg);
		currentDate = new Date();
		setDateTextView();
		imgReduce = (ImageView) findViewById(R.id.img_reduce_hr);
		imgIncrease = (ImageView) findViewById(R.id.img_increase_hr);
//		lv = (ListView) findViewById(R.id.lv);
		imgBack = (ImageView) findViewById(R.id.img_back);
	}
	
	private void setDateTextView() {
		tvToday.setText(TimeUtil.dateToString(currentDate, "yyyy-MM-dd"));
	}
	
	
	public void setListener(){
		frameHr.setOnHeartRateChangeListener(new OnHeartRateChangeListener() {
			@Override
			public void onChange(List<HeartRateData> heartRates) {
				Log.e("HeartRateFragment", "心率数据增加中,size : " +heartRates.size());
				
			}
		});
		

		imgBack.setOnClickListener(this);
		imgReduce.setOnClickListener(this);
		imgIncrease.setOnClickListener(this);
		tvToday.setOnClickListener(this);
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.img_increase_hr:
			currentDate = TimeUtil.getDateOfDiffDay(currentDate, 1);
			showHeartRate();
			setDateTextView();
			break;
		case R.id.img_reduce_hr:
			currentDate = TimeUtil.getDateOfDiffDay(currentDate, -1);
			showHeartRate();
			setDateTextView();
			break;
		case R.id.tv_hr_now:
			currentDate = new Date();
			showHeartRate();
			setDateTextView();
			break;

		default:
			break;
		}
	}
	
	private Date currentDate;
	private void showHeartRate() {
		frameHr.reset();
		String dateStr = TimeUtil.dateToString(currentDate);
//		List<HeartRateData> list = DataSupport.where("date=?",dateStr).limit(MAX).find(HeartRateData.class);
		datas = DataSupport.where("date=?",dateStr).find(HeartRateData.class);
		Integer max = DataSupport.where("date=?",dateStr).max(HeartRateData.class, "heartRate", Integer.class);
		Integer min = DataSupport.where("date=?",dateStr).min(HeartRateData.class, "heartRate", Integer.class);
		double avg = DataSupport.where("date=?",dateStr).average(HeartRateData.class, "heartRate");
		tvMaxHr.setText(""+DataUtil.format(max)+" bpm");
		tvMinHr.setText(""+DataUtil.format(min)+" bpm");
		tvAvgHr.setText(""+DataUtil.format((float)avg)+" bpm");
		if(datas!=null){
			Log.v("", "==================datas : "+datas.size());
			for(HeartRateData hr :datas){
				Log.v("", hr.getDate()+"<-------------->"+hr.getHeartRate());
				frameHr.setData(hr);
			}
		}
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

	//保存？
	public void save(View v) {
//		if (run != null) {
//			mHandler.post(run);
//		}
//		datas = frameHr.getData();
		if(datas.size()>0){
			Log.v("", "===========开始保存============" + datas.size());
			for (HeartRateData data :datas) {
				new HeartRateTask().execute(data);
			}
		}else{
			Toast.makeText(this, "没有新的心率数据...", Toast.LENGTH_SHORT).show();;
		}
		
		
		
	}
	private List<HeartRateData> list;
	private TextView tvMaxHr;
	private TextView tvMinHr;
	private TextView tvAvgHr;
	public void select(View v) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				frameHr.reset();
				list = DataSupport.findAll(HeartRateData.class);
				if(list!=null){
					Log.v("", "==================list : "+list.size());
					for(HeartRateData hr :list){
						Log.v("", hr.getDate()+"<-------------->"+hr.getHeartRate());
						frameHr.setData(hr);
					}
				}
			}
		});
	
//		lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, list));
	}
	
	//充值？
	public void reset(View v){
		mHandler.removeCallbacks(run);
		frameHr.reset();
		datas.clear();
	}
	
	
	
	private class MyAsyncTask extends AsyncTask<HeartRateData, Void, Boolean> {
		@Override
		protected Boolean doInBackground(HeartRateData... params) {
			return params[0].save();//Litepal 存储
		}
	}

	private class HeartRateTask extends  AsyncTask<HeartRateData, Integer, Boolean>  {
	
		@Override
		protected Boolean doInBackground(HeartRateData... params) {
			if (params[0].save()) {
				sum++;
				Log.v("", "===========frameHr.getData().size()============"+frameHr.getData().size());
				if(sum>=frameHr.getData().size()){
					Log.v("", "===========保存完成============"+sum);
					return true;
				}else{
					Log.v("", "===========保存中============"+sum);
					return false;
				}
			}else{
				Log.v("", "===========保存失败============"+sum);
				return false;//Litepal 存储
			}
		}
		
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				sum=0;
				frameHr.reset();
				datas.clear();
				toast();
			}
			super.onPostExecute(result);
		}
		
	};
	
	private void toast(){
		Toast.makeText(this, "保存完成", Toast.LENGTH_SHORT).show();
	}

}
