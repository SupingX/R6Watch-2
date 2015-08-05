package com.mycj.healthy.ui;

import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.R.layout;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SettingHeartRateSetActivity extends BaseSettingActivity {
	private EditText etHeartRateMax;
	private final int MAX = 180;
	private final int MIN = 40;
	private int lastHeartRate;
	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			// int value =
			// Integer.valueOf(etHeartRateMax.getText().toString().trim());
			// if (value < MIN) {
			// toast("最低40");
			// etHeartRateMax.setText(String.valueOf(MIN));
			// } else if (value > MAX) {
			// etHeartRateMax.setText(String.valueOf(MAX));
			// toast("最高180");
			// }
		}
	};

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};
	private LiteBlueService liteBlueService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_heart_rate_set);
		initTitle();
		initViews();
		setListener();
		liteBlueService = getLiteBlueService();
	}

	public void initViews() {
		etHeartRateMax = (EditText) findViewById(R.id.ed_hr_value);
		lastHeartRate = (int) SharedPreferenceUtil.get(this, Constant.SHARE_HEART_RATE_MAX, 100);
		etHeartRateMax.setText(lastHeartRate + "");
	}

	public void setListener() {
		etHeartRateMax.addTextChangedListener(mTextWatcher);
	}

	@Override
	public void imgConfirm() {
		int value = Integer.valueOf(etHeartRateMax.getText().toString().trim());
		if (value < MIN) {
			toast("最低40");
			return;
		} else if (value > MAX) {
			toast("最高180");
			return;
		}
		SharedPreferenceUtil.put(this, Constant.SHARE_HEART_RATE_MAX, value);
		if (isConnected(liteBlueService)) {
			// liteBlueService.writeCharactics(ProtoclData.toByteForHeartRateMaxProtocl(value));
			liteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForHeartRateMaxProtocl(value));
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
		tvTitle.setText(getResources().getString(R.string.max_heart));
	}
}
