package com.mycj.healthy.fragment;

import java.util.Date;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.mycj.healthy.view.ActionSheetDialog;
import com.mycj.healthy.view.MyAlertDialog;
import com.mycj.healthy.view.ActionSheetDialog.OnCancelClickListener;
import com.mycj.healthy.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.healthy.view.ActionSheetDialog.SheetItemColor;

public class SettingFragment extends Fragment implements OnClickListener {
	private RelativeLayout rlStepGoalSet, rlHeartRateSet, rlSleepTimeSet, rlClockSet, rlAutoHeartRateSet, rlBindedDevice, rlPhoneIncomingSet, rlSyncTime, rlInstoduction, rlShutdown, rlUpdate;
	private TextView tvStepGoal, tvHeartRateMax, tvClock, tvAutoHrTest, tvBindDevice, tvIncoming;
	private LiteBlueService mLiteBlueService;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED)) {
			} else if (action.equals(LiteBlueService.LITE_GATT_DISCONNECTED)) {
				setBluetooth();
				if (animation != null) {
					animation.cancel();
				}
			} else if (action.equals(LiteBlueService.LITE_BLUETOOTH_NOT_OPEN)) {
				setBluetooth();
			} else if (action.equals(LiteBlueService.LITE_SERVICE_DISCOVERED)) {
				setBluetooth();
			} else if (action.equals(LiteBlueService.LITE_SYNC_SUCCESS)) {
				if (pDialog != null) {
					pDialog.dismiss();
				}
				if (animation != null) {
					animation.cancel();
				}
				isSyncAll = false;
				Toast.makeText(getActivity(), getStringById(R.string.sync_ok), Toast.LENGTH_SHORT).show();

			} else if (action.equals(LiteBlueService.LITE_SYNC_FAIL)) {
				if (pDialog != null) {
					pDialog.dismiss();
				}
				if (animation != null) {
					animation.cancel();
				}
				isSyncAll = false;
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_GOAL_STEP)) {
				setStep();
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_MAX_HEART_RATE)) {
				setHeartRate();
			}
		}
	};

	private void startAnimation() {
		animation = ObjectAnimator.ofFloat(imgSyncData, "rotation", 0f, 360f, 0f);
		animation.setDuration(2000);

		animation.setRepeatMode(ValueAnimator.RESTART);
		animation.setRepeatCount(-1);
		animation.start();
	}

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

		imgSyncData = (ImageView) view.findViewById(R.id.right_update);
		setListener();
		Log.e("", "SettingFragment -- mLiteBlueService : " + mLiteBlueService);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		mLiteBlueService = ((BaseApp) getActivity().getApplication()).getLiteBlueService();
		getActivity().registerReceiver(mReceiver, LiteBlueService.getIntentFilter());
		setBluetooth();
		setClockValue();
		setStep();
		setHeartRate();
		setAutoHeartRate();
		setIncoming();
		super.onResume();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mReceiver);
		if (animation != null) {
			animation.cancel();
		}

		super.onPause();
	}

	private void checkBlue() {
		if (mLiteBlueService != null) {
			if (!mLiteBlueService.isEnable()) {
				showCheckBlueDialog();
			} else {
				startActivity(new Intent(getActivity(), SettingBindedDeviceActivity.class));
			}
		}
	}

	private ActionSheetDialog dialog;
	private static Handler mHandler = new Handler() {

	};

	private void showCheckBlueDialog() {
		dialog = new ActionSheetDialog(getActivity()).builder();
		dialog.setTitle(getResources().getString(R.string.open_blue));
		dialog.setOnCancelClickListener(new OnCancelClickListener() {

			@Override
			public void onClick() {
			}
		});
		dialog.addSheetItem(getResources().getString(R.string.confirm), SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {

				showProgressdialog();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (mLiteBlueService.isEnable()) {
							// 蓝牙打开
							startActivity(new Intent(getActivity(), SettingBindedDeviceActivity.class));

						} else {
							// 未打开
						}
						pDialog.dismiss();
					}
				}, 5000);
				mLiteBlueService.enable();
			}
		}).show();
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
			checkBlue();

			break;
		case R.id.rl_update:
			if (mLiteBlueService.getCurrentState() != null && mLiteBlueService.getCurrentState() == ConnectState.Connected) {
				mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteUpdateAllProtocl());
				if (!isSyncAll) {
					// Toast.makeText(getActivity(), "开始同步...",
					// Toast.LENGTH_SHORT).show();
					// showProgressdialog();
					startAnimation();
					isSyncAll = true;
				}
			} else {
				toastDisconnectDevice();
			}
			break;
		case R.id.rl_phone_incoming:
			startActivity(new Intent(getActivity(), SettingPhoneIncomingActivity.class));
			break;
		case R.id.rl_sync_time:
			if (mLiteBlueService.getCurrentState() != null && mLiteBlueService.getCurrentState() == ConnectState.Connected) {
				mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForDateProtocl(new Date(), getActivity()));
			} else {
				toastDisconnectDevice();
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

	private void toastDisconnectDevice() {
		String msg = getStringById(R.string.disconnect_device);
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	private String getStringById(int id) {
		return getResources().getString(id);
	}

	private void setClockValue() {

		int hour = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_CLOCK_HOUR, 12);
		int min = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_CLOCK_MIN, 0);
		boolean isOn = (boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_CLOCK_ON_OFF, false);
		tvClock.setText((hour < 10 ? ("0" + hour) : (hour + "")) + ":" + (min < 10 ? ("0" + min) : (min + "")) + "/" + (isOn ? getStringById(R.string.on) : getStringById(R.string.off)));
	}

	private void setStep() {
		tvStepGoal.setText("" + (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_STEP_GOAL, 500));
	}

	private void setHeartRate() {
		tvHeartRateMax.setText("" + (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_HEART_RATE_MAX, 100));
	}

	private void setAutoHeartRate() {
		tvAutoHrTest.setText((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_AUTO_HEART_RATE_ON_OFF, false) ? getStringById(R.string.on) : getStringById(R.string.off));
		tvAutoHrTest.setText((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_AUTO_HEART_RATE_ON_OFF, false) ? getStringById(R.string.on) : getStringById(R.string.off));
	}

	public boolean isBinded() {
		String name = (String) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_BINDING_DEVICE_NAME, "");
		String address = (String) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_BINDING_DEVICE_ADRESS, "");
		Log.e("", " [ name : " + name + ",address : " + address + " ] ");
		if (!name.equals("") && !address.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * 设置当前绑定的设备
	 */
	private String getCurrentDiviceName() {
		String name = (String) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_BINDING_DEVICE_NAME, "");
		return name;
	}
	
	private void setBluetooth() {
		if (mLiteBlueService != null) {
			if (!mLiteBlueService.isEnable()) {
				tvBindDevice.setText(getStringById(R.string.disconnect));
				return;
			}
			if (isBinded()) {
				getCurrentDiviceName();
				if (mLiteBlueService.isConnetted()) {
					tvBindDevice.setText(getCurrentDiviceName());
				} else {
					tvBindDevice.setText(getCurrentDiviceName()+getStringById(R.string.disconnect));
				}
			} else {
				tvBindDevice.setText(getCurrentDiviceName()+getStringById(R.string.disconnect));
			}

		}
	}

	private void setIncoming() {
		tvIncoming.setText((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_INCOMING_ON_OFF, false) ? getStringById(R.string.on) : getStringById(R.string.off));
	}

	private void showdialog() {
		new MyAlertDialog(getActivity()).builder().setTitle(getStringById(R.string.confirm_exit)).setPositiveButton(getStringById(R.string.confirm), new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLiteBlueService.closeAll();
				mLiteBlueService.setCurrentState(null);
				getActivity().finish();
			}
		}).setNegativeButton(getStringById(R.string.cancel), new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		}).show();
	}

	private ProgressDialog pDialog;
	private ImageView imgSyncData;
	private ObjectAnimator animation;
	private boolean isSyncAll;

	private void showProgressdialog() {
		pDialog = new ProgressDialog(getActivity());
		// pDialog.setCancelable(false);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.setMessage(getString(R.string.open_blue_ing));
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.show();
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
