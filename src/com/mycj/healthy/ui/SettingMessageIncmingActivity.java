package com.mycj.healthy.ui;

import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.R.layout;

import android.os.Bundle;

/**
 * 弃用
 * 
 * @author Administrator
 *
 */
public class SettingMessageIncmingActivity extends BaseSettingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_message_incming);
		initTitle();
	}

	@Override
	public void imgConfirm() {
		finish();
	}

	@Override
	public void imgBack() {
		finish();

	}

	@Override
	public void setTitle() {
		tvTitle.setText("新信息提示");

	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub

	}
}
