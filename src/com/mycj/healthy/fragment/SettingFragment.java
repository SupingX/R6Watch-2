package com.mycj.healthy.fragment;

import java.util.Date;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.BaseApp;
import com.mycj.healthy.R;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.ui.SettingAutoHeartRateActivity;
import com.mycj.healthy.ui.SettingBindedDeviceActivity;
import com.mycj.healthy.ui.SettingClockSetActivity;
import com.mycj.healthy.ui.SettingHeartRateSetActivity;
import com.mycj.healthy.ui.SettingIntroduceAppActivity;
import com.mycj.healthy.ui.SettingPhoneIncomingActivity;
import com.mycj.healthy.ui.SettingSleepTimeSetActivity;
import com.mycj.healthy.ui.SettingStepGoalActivity;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.view.MyAlertDialog;

public class SettingFragment extends Fragment implements OnClickListener {
	private RelativeLayout rlStepGoalSet, rlHeartRateSet, rlSleepTimeSet, rlClockSet, rlAutoHeartRateSet, rlBindedDevice, rlPhoneIncomingSet, rlSyncTime, rlInstoduction, rlShutdown, rlUpdate;
	private TextView tvStepGoal, tvHeartRateMax, tvClock, tvAutoHrTest, tvBindDevice, tvIncoming;
	private LiteBlueService mLiteBlueService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_setting, container, false);

		rlStepGoalSet = (RelativeLayout) view.findViewById(R.id.rl_goal_step);
		rlHeartRateSet = (RelativeLayout) view.findViewById(R.id.rl_max_heart);
		rlSleepTimeSet = (RelativeLayout) view.findViewById(R.id.rl_sleep_time);
		rlClockSet = (RelativeLayout) view.findViewById(R.id.rl_clock);
		rlAutoHeartRateSet = (RelativeLayout) view.findViewById(R.id.rl_auto_hr);
		rlBindedDevice = (RelativeLayout) view.findViewById(R.id.rl_binding);
		rlUpdate = (RelativeLayout) view.findViewById(R.id.rl_update);
		rlPhoneIncomingSet = (RelativeLayout) view.findViewById(R.id.rl_phone_incoming);
		rlSyncTime = (RelativeLayout) view.findViewById(R.id.rl_sync_time);
		rlInstoduction = (RelativeLayout) view.findViewById(R.id.rl_app_introduction);
		rlShutdown = (RelativeLayout) view.findViewById(R.id.rl_shutdown);

		// 各种初始状态
		tvStepGoal = (TextView) view.findViewById(R.id.tv_goal_value);
		tvHeartRateMax = (TextView) view.findViewById(R.id.tv_max_heart);
		tvClock = (TextView) view.findViewById(R.id.tv_clock_value);
		tvAutoHrTest = (TextView) view.findViewById(R.id.tv_auto_hr_value);
		tvBindDevice = (TextView) view.findViewById(R.id.tv_binding_value);
		tvIncoming = (TextView) view.findViewById(R.id.tv_phone_incoming_value);

		setListener();
		Log.e("", "SettingFragment -- mLiteBlueService : " + mLiteBlueService);
		return view;
	}

	@Override
	public void onResume() {
		mLiteBlueService = ((BaseApp) getActivity().getApplication()).getLiteBlueService();
		setClockValue();
		setStep();
		setHeartRate();
		setAutoHeartRate();
		setBluetooth();
		setIncoming();
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_goal_step:
			startActivity(new Intent(getActivity(), SettingStepGoalActivity.class));
			break;
		case R.id.rl_max_heart:
			startActivity(new Intent(getActivity(), SettingHeartRateSetActivity.class));
			break;
		case R.id.rl_sleep_time:
			startActivity(new Intent(getActivity(), SettingSleepTimeSetActivity.class));
			break;
		case R.id.rl_clock:
			startActivity(new Intent(getActivity(), SettingClockSetActivity.class));
			break;
		case R.id.rl_auto_hr:
			startActivity(new Intent(getActivity(), SettingAutoHeartRateActivity.class));
			break;
		case R.id.rl_binding:
			startActivity(new Intent(getActivity(), SettingBindedDeviceActivity.class));
			break;
		case R.id.rl_update:
			if (mLiteBlueService.getCurrentState() != null && mLiteBlueService.getCurrentState() == ConnectState.Connected) {
				mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForAutoHeartRateProtocl());
				Toast.makeText(getActivity(), "开始同步...", Toast.LENGTH_SHORT).show();
				;
			} else {
				Toast.makeText(getActivity(), "未连接手环...", Toast.LENGTH_SHORT).show();
				;
			}
			break;
		case R.id.rl_phone_incoming:
			startActivity(new Intent(getActivity(), SettingPhoneIncomingActivity.class));
			break;
		case R.id.rl_sync_time:
			if (mLiteBlueService.getCurrentState() != null && mLiteBlueService.getCurrentState() == ConnectState.Connected) {
				mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForDateProtocl(new Date(), getActivity()));
			} else {
				Toast.makeText(getActivity(), "未连接手环...", Toast.LENGTH_SHORT).show();
				;
			}
			break;
		case R.id.rl_app_introduction:
			startActivity(new Intent(getActivity(), SettingIntroduceAppActivity.class));
			break;
		case R.id.rl_shutdown:
			showdialog();
			break;

		default:
			break;
		}
	}

	private void setClockValue() {
		int hour = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_CLOCK_HOUR, 0);
		int min = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_CLOCK_MIN, 0);
		boolean isOn = (boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_CLOCK_ON_OFF, false);
		tvClock.setText((hour < 10 ? ("0" + hour) : (hour + "")) + ":" + (min < 10 ? ("0" + min) : (min + "")) + "/" + (isOn ? "已开启" : "未开启"));
	}
	private void setStep() {
		tvStepGoal.setText("" + (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_STEP_GOAL, 0));
	}
	private void setHeartRate() {
		tvHeartRateMax.setText("" + (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_HEART_RATE_MAX, 0));
	}
	private void setAutoHeartRate() {
		tvAutoHrTest.setText((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_AUTO_HEART_RATE_ON_OFF, false) ? "已开启" : "未开启");
		tvAutoHrTest.setText((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_AUTO_HEART_RATE_ON_OFF, false) ? "已开启" : "未开启");
	}
	private void setBluetooth() {
		BluetoothDevice device = mLiteBlueService.getCurrentBluetoothDevice();
		Log.e("SettingFragment", "currentDevice : " + device);
		tvBindDevice.setText(device != null ? device.getName() : "未绑定");
	}

	private void setIncoming() {
		tvIncoming.setText((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_INCOMING_ON_OFF, false) ? "已开启" : "未开启");
	}
	private void showdialog() {
		new MyAlertDialog(getActivity()).builder().setTitle("确定推出？").setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLiteBlueService.closeAll();
				mLiteBlueService.setCurrentState(null);
				getActivity().finish();
			}
		}).setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		}).show();
	}


	private void setListener() {
		rlStepGoalSet.setOnClickListener(this);
		rlHeartRateSet.setOnClickListener(this);
		rlSleepTimeSet.setOnClickListener(this);
		rlClockSet.setOnClickListener(this);
		rlAutoHeartRateSet.setOnClickListener(this);
		rlBindedDevice.setOnClickListener(this);
		rlPhoneIncomingSet.setOnClickListener(this);
		rlSyncTime.setOnClickListener(this);
		rlInstoduction.setOnClickListener(this);
		rlShutdown.setOnClickListener(this);
		rlUpdate.setOnClickListener(this);
	}

}
