package com.mycj.healthy.ui;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.BaseApp;
import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.R.layout;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.Telephony.MmsSms;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SettingPhoneIncomingActivity extends BaseSettingActivity implements OnClickListener {

	// private CheckBox cbVibration;
	// private CheckBox cbSound;
	// private RelativeLayout rlViberation;
	// private RelativeLayout rlSound;
	private CheckBox cbIncoming;
	private LiteBlueService liteBlueService;
	private boolean isChecked;
	private boolean isVibrationChecked;
	private boolean isSoundChecked;
	private int callCount;
	private int mNewSmsCount;

	// private static int lastetState = TelephonyManager.CALL_STATE_IDLE; //
	// 最后的状态
	// private PhoneStateListener phoneStateListener = new PhoneStateListener(){
	// @Override
	// public void onCallStateChanged(int state, String incomingNumber) {
	// // 如果当前状态为空闲,上次状态为响铃中的话,则认为是未接来电
	// if (lastetState == TelephonyManager.CALL_STATE_RINGING && state ==
	// TelephonyManager.CALL_STATE_IDLE) {
	// sendSmgWhenMissedCall(incomingNumber);
	// }
	// lastetState =state;
	// }
	// };
	//
	// private BroadcastReceiver mCallReceiver = new BroadcastReceiver(){
	// public void onReceive(Context context, Intent intent) {
	// Log.i("sms", "CallReceiver Start...");
	// TelephonyManager telephony = (TelephonyManager)
	// context.getSystemService(Context.TELEPHONY_SERVICE);
	// // final CallListener customPhoneListener = new CallListener(context);
	// telephony.listen(phoneStateListener,
	// PhoneStateListener.LISTEN_CALL_STATE);
	// // telephony.listen(customPhoneListener,
	// PhoneStateListener.LISTEN_CALL_STATE);
	// // Bundle bundle = intent.getExtras();
	// // String phoneNr = bundle.getString("incoming_number");
	// // Log.i("sms", "CallReceiver Phone Number : " + phoneNr);
	//
	// }
	// };

	private Handler mHandler = new Handler() {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_phone_incoming);
		liteBlueService = getLiteBlueService();
		initTitle();
		initViews();
		setListener();

		// 注册Observer
		// registerObserver();
		// register();
		// registerReceiver(mCallReceiver, new
		// IntentFilter("android.intent.action.PHONE_STATE"));
	}

	@Override
	protected void onDestroy() {

		// unregisterObserver();

		super.onDestroy();
	}

	@Override
	public void imgConfirm() {
		SharedPreferenceUtil.put(SettingPhoneIncomingActivity.this, Constant.SHARE_INCOMING_ON_OFF, isChecked);
		SharedPreferenceUtil.put(SettingPhoneIncomingActivity.this, Constant.SHARE_INCOMING_VIBRATION_ON_OFF, isVibrationChecked);
		SharedPreferenceUtil.put(SettingPhoneIncomingActivity.this, Constant.SHARE_INCOMING_SOUND_ON_OFF, isSoundChecked);
		// 确认时，更新未读信息
		((BaseApp) getApplication()).doWriteToWatch();

		// if (isConnected(liteBlueService)) {

		// callCount = readMissCall();
		// mNewSmsCount = getNewSmsCount() + getNewMmsCount();
		// Log.e("SettingPhoneIncomingActivity", "callCount : " + callCount);
		// Log.e("SettingPhoneIncomingActivity", "mNewSmsCount : " +
		// mNewSmsCount);
		// liteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForPhoneMessageIncomingProtocl(callCount,
		// mNewSmsCount));
		// } else {
		// toastNotConnectted();
		// }

		finish();
	}

	@Override
	public void imgBack() {
		finish();

	}

	@Override
	public void setTitle() {
		tvTitle.setText(getResources().getString(R.string.call_msg_reminder));

	}

	@Override
	public void initViews() {
		// cbVibration = (CheckBox) findViewById(R.id.cb_vibration);
		// cbSound = (CheckBox) findViewById(R.id.cb_sound);
		cbIncoming = (CheckBox) findViewById(R.id.cb_incoming);
		//
		// rlViberation = (RelativeLayout)
		// findViewById(R.id.rl_incoming_vibration);
		// rlSound = (RelativeLayout) findViewById(R.id.rl_incoming_sound);

		isChecked = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_INCOMING_ON_OFF, false);
		isVibrationChecked = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_INCOMING_VIBRATION_ON_OFF, false);
		isSoundChecked = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_INCOMING_SOUND_ON_OFF, false);
		// 当同时不为选中中，默认选中振动
		if (!isVibrationChecked && !isSoundChecked) {
			isVibrationChecked = true;
		}

		cbIncoming.setChecked(isChecked);
		// cbVibration.setChecked(isVibrationChecked);
		// cbSound.setChecked(isSoundChecked);
	}

	@Override
	public void setListener() {
		// cbVibration.setOnClickListener(this);
		// cbSound.setOnClickListener(this);
		cbIncoming.setOnClickListener(this);
		// rlViberation.setOnClickListener(this);
		// rlSound.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.cb_vibration:
		// case R.id.rl_incoming_vibration:
		// //当另外一个为不选中，则不能改变为位选中。即不能同时为不选中
		// Log.e("", "isSoundChecked : "+ isSoundChecked
		// +",isVibrationChecked :" +isVibrationChecked);
		// if(!isSoundChecked&&isVibrationChecked){
		// Log.e("","1111111111111");
		// cbVibration.setChecked(true);
		// return ;
		// }else {
		// isVibrationChecked=!isVibrationChecked;
		// cbVibration.setChecked(isVibrationChecked);
		// }
		//
		// break;
		// case R.id.cb_sound:
		// case R.id.rl_incoming_sound:
		// //当另外一个为不选中，则不能改变为位选中。即不能同时为不选中
		// Log.e("", "isSoundChecked : "+ isSoundChecked
		// +",isVibrationChecked :" +isVibrationChecked);
		// if(!isVibrationChecked&&isSoundChecked){
		// Log.e("","2222222222222222");
		// cbSound.setChecked(true);
		// return ;
		// }else{
		// isSoundChecked=!isSoundChecked;
		// cbSound.setChecked(isSoundChecked);
		// }
		//
		// break;
		case R.id.cb_incoming:
			isChecked = !isChecked;
			cbIncoming.setChecked(isChecked);

			break;

		default:
			break;
		}

	}

	private void sendSmgWhenMissedCall(String incomingNumber) {
		runOnUiThread(new Runnable() {
			public void run() {
				doWriteToWatch();
			}
		});
	}

	private void doWriteToWatch() {
		// liteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForPhoneMessageIncomingProtocl(readMissCall(),
		// getNewSmsCount()+getNewMmsCount()));
	}
}
