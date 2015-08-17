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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker;

public class SettingClockSetActivity extends BaseSettingActivity implements OnClickListener {

	private NumberPicker npClockHour, npClockMin;
	private LiteBlueService liteBlueService;
	private CheckBox cbClock;
	private boolean isCheked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_clock_set);
		initTitle();
		initViews();
		setListener();
		liteBlueService = getLiteBlueService();
	}

	public void initViews() {
		npClockHour = (NumberPicker) findViewById(R.id.np_clock_hour);
		npClockMin = (NumberPicker) findViewById(R.id.np_clock_min);

		npClockHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npClockHour.setMaxValue(23);
		npClockHour.setMinimumHeight(0);
		npClockHour.setValue((int) SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_HOUR, 12));

		npClockMin.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npClockMin.setMaxValue(59);
		npClockMin.setMinimumHeight(0);
		npClockMin.setValue((int) SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_MIN, 0));

		cbClock = (CheckBox) findViewById(R.id.cb_clock);
		isCheked = (boolean) (SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_ON_OFF, false));
		cbClock.setChecked(isCheked);

	}

	@Override
	public void imgConfirm() {
		SharedPreferenceUtil.put(this, Constant.SHARE_CLOCK_HOUR, npClockHour.getValue());
		SharedPreferenceUtil.put(this, Constant.SHARE_CLOCK_MIN, npClockMin.getValue());
		SharedPreferenceUtil.put(this, Constant.SHARE_CLOCK_ON_OFF, isCheked);
		if (liteBlueService.isConnetted()) {
			//00闹钟关闭 01闹钟开启 
			Log.e("SettingClockActivity", isCheked?"闹钟：开！":"闹钟：关！");
			liteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForClockProtocl(npClockHour.getValue(), npClockMin.getValue(), isCheked ? 0 : 1));
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
		tvTitle.setText(getResources().getString(R.string.watch_alarm));
	}

	@Override
	public void setListener() {
		cbClock.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cb_clock:
			isCheked = !isCheked;
			cbClock.setChecked(isCheked);
			break;

		default:
			break;
		}
	}

}
