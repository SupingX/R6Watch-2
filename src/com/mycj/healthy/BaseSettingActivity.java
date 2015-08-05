package com.mycj.healthy;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseSettingActivity extends BaseActivity {

	public ImageView imgBack;
	public ImageView imgConfirm;
	public TextView tvTitle;

	public void initTitle() {
		imgBack = (ImageView) findViewById(R.id.img_back);
		imgConfirm = (ImageView) findViewById(R.id.img_confirm);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		setTitle();
		setTitleListener();
	}

	private void setTitleListener() {
		imgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imgBack();
			}
		});
		imgConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgConfirm();
			}
		});
	}

	public abstract void imgConfirm();

	public abstract void imgBack();

	public abstract void setTitle();

}
