package com.mycj.healthy.ui;

import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 弃用
 * 
 * @author Administrator
 *
 */
public class SettingDisconnectActivity extends BaseSettingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_disconnect);
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
		tvTitle.setText("断连防丢");
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