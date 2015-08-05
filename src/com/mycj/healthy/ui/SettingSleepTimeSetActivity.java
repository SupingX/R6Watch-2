package com.mycj.healthy.ui;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.R.id;
import com.mycj.healthy.R.layout;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.view.SelectAlertDialog;
import com.mycj.healthy.view.SelectAlertDialog.OnNumberPickerChange;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public class SettingSleepTimeSetActivity extends BaseSettingActivity implements OnClickListener {

	private static final String SHARE_MIDDLE_SLEEP_START_HOUR = null;
	// private NumberPicker npMiddleStartHour, npMiddleStartMin,
	// npMiddleEndhour, npMiddleEndMin, npSleepStartHour, npSleepStartMin,
	// npSleepEndHour, npSleepEndMin;
	private LiteBlueService liteBlueService;
	private TextView tvMiddleStart, tvMiddleEnd, tvSleepStart, tvSleepEnd;
	private SelectAlertDialog startMiddleDilog;
	private SelectAlertDialog endMiddleDilog;
	private SelectAlertDialog startSleepDialog;
	private SelectAlertDialog endSleepDialog;
	private int middleStartHourValue;
	private int middleStartMinValue;
	private int middleEndHourValue;
	private int middleEndMinValue;
	private int sleepStartHourValue;
	private int sleepStartMinValue;
	private int sleepEndHourValue;
	private int sleepEndMinValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_sleep_time_set);
		initTitle();
		initViews();
		setListener();

		liteBlueService = getLiteBlueService();
	}

	public void initViews() {

		tvMiddleStart = (TextView) findViewById(R.id.tv_mid_start_value);
		tvMiddleEnd = (TextView) findViewById(R.id.tv_mid_end_value);
		tvSleepStart = (TextView) findViewById(R.id.tv_sleep_start_value);
		tvSleepEnd = (TextView) findViewById(R.id.tv_sleep_end_value);

		middleStartHourValue = (int) SharedPreferenceUtil.get(this, Constant.SHARE_MIDDLE_SLEEP_START_HOUR, 12);
		middleStartMinValue = (int) SharedPreferenceUtil.get(this, Constant.SHARE_MIDDLE_SLEEP_START_MIN, 00);
		middleEndHourValue = (int) SharedPreferenceUtil.get(this, Constant.SHARE_MIDDLE_SLEEP_END_HOUR, 12);
		middleEndMinValue = (int) SharedPreferenceUtil.get(this, Constant.SHARE_MIDDLE_SLEEP_END_MIN, 00);
		sleepStartHourValue = (int) SharedPreferenceUtil.get(this, Constant.SHARE_SLEEP_START_HOUR, 12);
		sleepStartMinValue = (int) SharedPreferenceUtil.get(this, Constant.SHARE_SLEEP_START_MIN, 00);
		sleepEndHourValue = (int) SharedPreferenceUtil.get(this, Constant.SHARE_SLEEP_END_HOUR, 12);
		sleepEndMinValue = (int) SharedPreferenceUtil.get(this, Constant.SHARE_SLEEP_END_MIN, 00);

		tvMiddleStart.setText(format(middleStartHourValue) + " : " + format(middleStartMinValue));
		tvMiddleEnd.setText(format(middleEndHourValue) + " : " + format(middleEndMinValue));
		tvSleepStart.setText(format(sleepStartHourValue) + " : " + format(sleepStartMinValue));
		tvSleepEnd.setText(format(sleepEndHourValue) + " : " + format(sleepEndMinValue));

		startMiddleDilog = new SelectAlertDialog(this, middleStartHourValue, middleStartMinValue).builder();
		endMiddleDilog = new SelectAlertDialog(this, middleEndHourValue, middleEndMinValue).builder();
		startSleepDialog = new SelectAlertDialog(this, sleepStartHourValue, sleepStartMinValue).builder();
		endSleepDialog = new SelectAlertDialog(this, sleepEndHourValue, sleepEndMinValue).builder();
	}

	@Override
	public void imgConfirm() {

		SharedPreferenceUtil.put(this, Constant.SHARE_MIDDLE_SLEEP_START_HOUR, middleStartHourValue);
		SharedPreferenceUtil.put(this, Constant.SHARE_MIDDLE_SLEEP_START_MIN, middleStartMinValue);
		SharedPreferenceUtil.put(this, Constant.SHARE_MIDDLE_SLEEP_END_HOUR, middleEndHourValue);
		SharedPreferenceUtil.put(this, Constant.SHARE_MIDDLE_SLEEP_END_MIN, middleEndMinValue);
		SharedPreferenceUtil.put(this, Constant.SHARE_SLEEP_START_HOUR, sleepStartHourValue);
		SharedPreferenceUtil.put(this, Constant.SHARE_SLEEP_START_MIN, sleepStartMinValue);
		SharedPreferenceUtil.put(this, Constant.SHARE_SLEEP_END_HOUR, sleepEndHourValue);
		SharedPreferenceUtil.put(this, Constant.SHARE_SLEEP_END_MIN, sleepEndMinValue);
		int[] sleep = new int[] { middleStartHourValue, middleStartMinValue, middleEndHourValue, middleEndMinValue, sleepStartHourValue, sleepStartMinValue, sleepEndHourValue, sleepEndMinValue };
		// if (liteBlueService.getCurrentState() != null &&
		// liteBlueService.getCurrentState()==ConnectState.Connected) {
		if (isConnected(liteBlueService)) {
			liteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForSleepProtocl(sleep));
		} else {
			toastNotConnectted();
		}
		finish();
	}

	@Override
	public void imgBack() {
		finish();
	}

	@Override
	public void setTitle() {
		tvTitle.setText(getResources().getString(R.string.sleep));
	}

	@Override
	public void setListener() {
		tvMiddleStart.setOnClickListener(this);
		tvMiddleEnd.setOnClickListener(this);
		tvSleepStart.setOnClickListener(this);
		tvSleepEnd.setOnClickListener(this);
	}

	private String format(int value) {
		if (value < 10) {
			return "0" + value;
		} else {
			return String.valueOf(value);
		}
	}

	private String getDefaultValue(String shareKey, int defaultValue) {
		return format((int) SharedPreferenceUtil.get(this, shareKey, defaultValue));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_mid_start_value:

			startMiddleDilog.setTitle("午休开始时间");
			Log.e("SettingSleepTimeSetActivity", "____________middleStartHourValue : middleStartMinValue ----" + middleStartHourValue + " : " + middleStartMinValue);
			startMiddleDilog.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(View v) {
					middleStartHourValue = startMiddleDilog.getHourValue();
					middleStartMinValue = startMiddleDilog.getMinValue();
					Log.e("SettingSleepTimeSetActivity", "____________middleStartHourValue : middleStartMinValue ----" + middleStartHourValue + " : " + middleStartMinValue);
					String value = format(middleStartHourValue) + " : " + format(middleStartMinValue);
					toast(value);
					tvMiddleStart.setText(value);
				}
			});
			startMiddleDilog.show();
			break;
		case R.id.tv_mid_end_value:

			endMiddleDilog.setTitle("午休结束时间");
			endMiddleDilog.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(View v) {
					int hour = endMiddleDilog.getHourValue();
					int min = endMiddleDilog.getMinValue();
					
					if(hour < middleStartHourValue){
						toastLong("午休结束时间要晚于开始时间");
						return ;
					}else if(hour==middleStartHourValue){
						if(min<=middleStartMinValue){
							toastLong("午休结束时间要晚于开始时间");
							return;
						}
					}
					middleEndHourValue = hour;
					middleEndMinValue = min;
					String value = format(middleEndHourValue) + " : " + format(middleEndMinValue);
					toast(value);
					tvMiddleEnd.setText(value);
				}
			});
			endMiddleDilog.show();
			break;
		case R.id.tv_sleep_start_value:

			startSleepDialog.setTitle("睡眠开始时间");
			startSleepDialog.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(View v) {
					sleepStartHourValue = startSleepDialog.getHourValue();
					sleepStartMinValue = startSleepDialog.getMinValue();
					String value = format(sleepStartHourValue) + " : " + format(sleepStartMinValue);
					toast(value);
					tvSleepStart.setText(value);
				}
			});
			startSleepDialog.show();
			break;
		case R.id.tv_sleep_end_value:

			endSleepDialog.setTitle("睡眠结束时间");
			endSleepDialog.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View v) {
					int hour = endSleepDialog.getHourValue();
					int min = endSleepDialog.getMinValue();
//					if(hour < sleepStartHourValue){
//						toastLong("睡眠结束时间要晚于开始时间");
//						
//						return ;
//					}else if(hour==sleepStartHourValue){
//						if(min<=sleepStartMinValue){
//							toastLong("睡眠结束时间要晚于开始时间");
//							return;
//						}
//					}
					String value = format(sleepEndHourValue) + " : " + format(sleepEndMinValue);
					toast(value);
					tvSleepEnd.setText(value);
					sleepEndHourValue = hour;
					sleepEndMinValue = min;
				}
			});
			endSleepDialog.show();
			break;

		default:
			break;
		}
	}
}
