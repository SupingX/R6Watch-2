package com.mycj.healthy.fragment;

import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.BaseActivity;
import com.mycj.healthy.BaseApp;
import com.mycj.healthy.R;
import com.mycj.healthy.UpdateAsyncTask;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.ui.SettingAutoHeartRateActivity;
import com.mycj.healthy.ui.SettingBindedDeviceActivity;
import com.mycj.healthy.ui.SettingCameraAndSearchActivity;
import com.mycj.healthy.ui.SettingClockSetActivity;
import com.mycj.healthy.ui.SettingDisconnectActivity;
import com.mycj.healthy.ui.SettingHeartRateSetActivity;
import com.mycj.healthy.ui.SettingIntroduceAppActivity;
import com.mycj.healthy.ui.SettingMessageIncmingActivity;
import com.mycj.healthy.ui.SettingPhoneIncomingActivity;
import com.mycj.healthy.ui.SettingRemindActivity;
import com.mycj.healthy.ui.SettingRemindTypeActivity;
import com.mycj.healthy.ui.SettingSleepTimeSetActivity;
import com.mycj.healthy.ui.SettingStepGoalActivity;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.DataUtil;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.view.MyAlertDialog;
import com.mycj.healthy.view.SleepCountView;

public class SettingFragment extends Fragment implements OnClickListener {
	private RelativeLayout rlStepGoalSet, rlHeartRateSet, rlSleepTimeSet, rlClockSet, /*
																					 * rlCameraAndSearchSet
																					 * ,
																					 */rlAutoHeartRateSet, rlBindedDevice, /*
																															 * rlMore
																															 * ,
																															 */rlPhoneIncomingSet, /*
																																					 * rlMessageIncomingSet
																																					 * ,
																																					 */
	/* rlRemindSet, rlRemindTypeSet, */rlSyncTime, /* rldisconnect, */rlInstoduction, rlShutdown, rlUpdate;
	// private LinearLayout llMore;
	private ScrollView svSetting;
	private ImageView imgMore;
	private boolean isExpan = false;
	private TextView tvStepGoal, tvHeartRateMax, tvClock, tvAutoHrTest, tvBindDevice, tvIncoming;
	private LiteBlueService mLiteBlueService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_setting, container, false);

		rlStepGoalSet = (RelativeLayout) view.findViewById(R.id.rl_goal_step);
		rlHeartRateSet = (RelativeLayout) view.findViewById(R.id.rl_max_heart);
		rlSleepTimeSet = (RelativeLayout) view.findViewById(R.id.rl_sleep_time);
		rlClockSet = (RelativeLayout) view.findViewById(R.id.rl_clock);
		// rlCameraAndSearchSet = (RelativeLayout)
		// view.findViewById(R.id.rl_camera_search);
		rlAutoHeartRateSet = (RelativeLayout) view.findViewById(R.id.rl_auto_hr);
		rlBindedDevice = (RelativeLayout) view.findViewById(R.id.rl_binding);
		rlUpdate = (RelativeLayout) view.findViewById(R.id.rl_update);
		// rlMore = (RelativeLayout) view.findViewById(R.id.rl_more);
		rlPhoneIncomingSet = (RelativeLayout) view.findViewById(R.id.rl_phone_incoming);
		// rlMessageIncomingSet = (RelativeLayout)
		// view.findViewById(R.id.rl_message);
		// rlRemindSet = (RelativeLayout) view.findViewById(R.id.rl_remind);
		// rlRemindTypeSet = (RelativeLayout)
		// view.findViewById(R.id.rl_remind_type);
		rlSyncTime = (RelativeLayout) view.findViewById(R.id.rl_sync_time);
		// rldisconnect = (RelativeLayout)
		// view.findViewById(R.id.rl_disconnect);
		rlInstoduction = (RelativeLayout) view.findViewById(R.id.rl_app_introduction);
		rlShutdown = (RelativeLayout) view.findViewById(R.id.rl_shutdown);

		svSetting = (ScrollView) view.findViewById(R.id.sc_setting);
		// llMore = (LinearLayout) view.findViewById(R.id.ll_more);
		// imgMore = (ImageView) view.findViewById(R.id.img_more);

		// 各种初始状态
		tvStepGoal = (TextView) view.findViewById(R.id.tv_goal_value);
		tvHeartRateMax = (TextView) view.findViewById(R.id.tv_max_heart);
		tvClock = (TextView) view.findViewById(R.id.tv_clock_value);
		tvAutoHrTest = (TextView) view.findViewById(R.id.tv_auto_hr_value);
		tvBindDevice = (TextView) view.findViewById(R.id.tv_binding_value);
		tvIncoming = (TextView) view.findViewById(R.id.tv_phone_incoming_value);

		setListener();
		mLiteBlueService = ((BaseApp) getActivity().getApplication()).getLiteBlueService();
		Log.e("", "SettingFragment -- mLiteBlueService : " + mLiteBlueService);
		return view;
	}

	@Override
	public void onResume() {
		tvStepGoal.setText("" + (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_STEP_GOAL, 0));
		tvHeartRateMax.setText("" + (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_HEART_RATE_MAX, 0));
		tvClock.setText("" + (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_CLOCK_HOUR, 0) + ":" + (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_CLOCK_MIN, 0) + "/"
				+ ((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_CLOCK_ON_OFF, false) ? "已开启" : "未开启"));
		tvAutoHrTest.setText((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_AUTO_HEART_RATE_ON_OFF, false) ? "已开启" : "未开启");
		tvAutoHrTest.setText((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_AUTO_HEART_RATE_ON_OFF, false) ? "已开启" : "未开启");
		BluetoothDevice device = mLiteBlueService.getCurrentBluetoothDevice();
		Log.e("SettingFragment", "currentDevice : " + device);
		tvBindDevice.setText(device != null ? device.getName() : "未绑定");
		tvIncoming.setText((boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_INCOMING_ON_OFF, false) ? "已开启" : "未开启");
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
		// case R.id.rl_camera_search:
		// startActivity(new Intent(getActivity(),
		// SettingCameraAndSearchActivity.class));
		// break;
		case R.id.rl_auto_hr:
			startActivity(new Intent(getActivity(), SettingAutoHeartRateActivity.class));
			break;
		case R.id.rl_binding:
			startActivity(new Intent(getActivity(), SettingBindedDeviceActivity.class));
			break;
		case R.id.rl_update:
			if (mLiteBlueService.getCurrentState() != null && mLiteBlueService.getCurrentState() == ConnectState.Connected) {
				mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForAutoHeartRateProtocl());
				UpdateAsyncTask task = new UpdateAsyncTask();
				// task.execute(null);
				Toast.makeText(getActivity(), "开始同步...", Toast.LENGTH_SHORT).show();
				;
			} else {
				Toast.makeText(getActivity(), "未连接手环...", Toast.LENGTH_SHORT).show();
				;
			}
			break;
		// case R.id.rl_more:
		// isExpan = !isExpan;
		// updateMore();
		// break;
		case R.id.rl_phone_incoming:
			startActivity(new Intent(getActivity(), SettingPhoneIncomingActivity.class));
			break;
		// case R.id.rl_message:
		// startActivity(new Intent(getActivity(),
		// SettingMessageIncmingActivity.class));
		// break;
		// case R.id.rl_remind:
		// startActivity(new Intent(getActivity(),
		// SettingRemindActivity.class));
		// break;
		// case R.id.rl_remind_type:
		// startActivity(new Intent(getActivity(),
		// SettingRemindTypeActivity.class));
		// break;
		case R.id.rl_sync_time:
			// if(mLiteBlueService.getCurrentState()!=null&&mLiteBlueService.getCurrentState()==ConnectState.Connected){
			if (mLiteBlueService.getCurrentState() != null && mLiteBlueService.getCurrentState() == ConnectState.Connected) {
				mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForDateProtocl(new Date(), getActivity()));
			} else {
				Toast.makeText(getActivity(), "未连接手环...", Toast.LENGTH_SHORT).show();
				;
			}
			break;
		// case R.id.rl_disconnect:
		// startActivity(new Intent(getActivity(),
		// SettingDisconnectActivity.class));
		// break;
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

	private void showdialog() {
		new MyAlertDialog(getActivity()).builder().setTitle("确定推出？").setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
				
				mLiteBlueService.closeAll();
				mLiteBlueService.setCurrentState(null);
				mLiteBlueService.setCurrentBluetoothDevice(null);
				mLiteBlueService=null;
			}
		}).setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		}).show();
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//		builder.setTitle("退出？").setPositiveButton("确定", null).setNegativeButton("取消", null).setIcon(R.drawable.ic_launcher).create().show();
	}

	private void updateMore() {
		if (isExpan) {
			// llMore.setVisibility(View.VISIBLE);
			imgMore.setImageResource(R.drawable.setting_advanced_option_icon);
			svSetting.post(new Runnable() {
				@Override
				public void run() {
					svSetting.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});

		} else {
			// llMore.setVisibility(View.GONE);
			imgMore.setImageResource(R.drawable.setting_advanced_option_icon_open);
			// svSetting.fullScroll(ScrollView.FOCUS_UP);//滚动到顶部
		}
	}

	private void setListener() {
		rlStepGoalSet.setOnClickListener(this);
		rlHeartRateSet.setOnClickListener(this);
		rlSleepTimeSet.setOnClickListener(this);
		rlClockSet.setOnClickListener(this);
		// rlCameraAndSearchSet.setOnClickListener(this);
		rlAutoHeartRateSet.setOnClickListener(this);
		rlBindedDevice.setOnClickListener(this);
		// rlMore.setOnClickListener(this);
		rlPhoneIncomingSet.setOnClickListener(this);
		// rlMessageIncomingSet.setOnClickListener(this);
		// rlRemindSet.setOnClickListener(this);
		// rlRemindTypeSet.setOnClickListener(this);
		rlSyncTime.setOnClickListener(this);
		// rldisconnect.setOnClickListener(this);
		rlInstoduction.setOnClickListener(this);
		rlShutdown.setOnClickListener(this);
		rlUpdate.setOnClickListener(this);
	}

}
