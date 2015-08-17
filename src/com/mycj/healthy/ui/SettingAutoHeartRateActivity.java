package com.mycj.healthy.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;

public class SettingAutoHeartRateActivity extends BaseSettingActivity implements OnClickListener {
	private CheckBox cbAutoHeartRate;
	private LiteBlueService liteBlueService;
	private boolean isChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_auto_heart_rate);
		liteBlueService = getLiteBlueService();
		initTitle();
		initViews();
		setListener();

	}

	@Override
	public void imgConfirm() {
		SharedPreferenceUtil.put(SettingAutoHeartRateActivity.this, Constant.SHARE_AUTO_HEART_RATE_ON_OFF, isChecked);
		if (liteBlueService.isConnetted()) {
			liteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForAutoHeartRateProtocl(isChecked ? 1 : 0));
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

		tvTitle.setText(getResources().getString(R.string.auto_hr_detection));

	}

	@Override
	public void initViews() {
		cbAutoHeartRate = (CheckBox) findViewById(R.id.cb_auto_hr);
		isChecked = (boolean) (SharedPreferenceUtil.get(this, Constant.SHARE_AUTO_HEART_RATE_ON_OFF, false));
		cbAutoHeartRate.setChecked(isChecked);
	}

	@Override
	public void setListener() {
		cbAutoHeartRate.setOnClickListener(this);
		// cbAutoHeartRate.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// SharedPreferenceUtil.put(SettingAutoHeartRateActivity.this,
		// Constant.SHARE_AUTO_HEART_RATE_ON_OFF, isChecked);
		// liteBlueService.writeCharactics(ProtoclData.toByteForAutoHeartRateProtocl(
		// isChecked?1:0));
		// }
		// });
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cb_auto_hr:
			isChecked = !isChecked;
			cbAutoHeartRate.setChecked(isChecked);
			break;

		default:
			break;
		}
	}
}
