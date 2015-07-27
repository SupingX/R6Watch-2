package com.mycj.healthy.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycj.healthy.R;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.DataUtil;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.view.CalProgressBar;
import com.mycj.healthy.view.ColorSeekBar;
import com.mycj.healthy.view.DistanceView;
import com.mycj.healthy.view.StepCircle;

public class InformationFragment extends Fragment implements OnClickListener {
	private ImageView imgStep, imgDistance, imgCal, imgHeart, imgSleep, imgMiddleSleep, imgRest;
	// private StepFragment stepFragment;
	// private DistanceFragment distanceFragment;
	// private Fragment kcalFragment, heartFragment, sleepFragment,
	// middleFragment, restFragment;
	/**
	 * 当前页面
	 */
	private View calV;
	private TextView tvStep;
	private View distanceV;
	private FragmentManager fm;
	private FragmentTransaction beginTransaction;

	private int currentFragment;
	private FrameLayout frame;
	private View stepView;
	private View distanceView;
	private StepCircle stepCircle;
	private DistanceView disView;
	private TextView tvDistanceGoal;
	private TextView tvDistanceComplete;
	private View kcalView;
	private TextView tvCalGoal;
	private TextView tvCalComplete;
	private ColorSeekBar seekBarCal, seekBarHeartRate;
	private View hrView;
	private View sleepView;
	private View middleSleepView;
	private View restView;
	private ImageView imgHr;
	private TextView tvStepInfo, tvDistanceInfo, tvCalInfo, tvHeartRateInfo, tvSleepInfo, tvMiddleInfo, tvRestInfo;
	private TextView tvCalCompleteInfo;
	private int step;
	private int maxstep;

	private final int STEP_TO_DISTANCE = 10000 / 4000;
	private final int STEP_TO_KCAL = 7000;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED)) {
				byte[] data = intent.getByteArrayExtra(LiteBlueService.EXTRA_VALUE);
				
				Log.e("InformationFragment", "____________________characteristic changed________________________" + DataUtil.getStringByBytes(data));
				if (1==1) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							setHrearRateValue(100);
						}
					});
				}else if(1==2){
					
				}
			}
		}

	};
	private TextView tvHeartRateValue;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		IntentFilter filter = LiteBlueService.getIntentFilter();
		getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Bundle b = getArguments();
		// 加载布局文件
		View view = inflater.inflate(R.layout.fragment_infomation, container, false);
		// View view = inflater.inflate(b.getInt("layout"), container, false);
		// 初始化views
		imgStep = (ImageView) view.findViewById(R.id.img_sport_pedo);
		imgDistance = (ImageView) view.findViewById(R.id.img_sport_distance);
		imgCal = (ImageView) view.findViewById(R.id.img_sport_cal);
		imgHeart = (ImageView) view.findViewById(R.id.img_sport_hr);
		imgSleep = (ImageView) view.findViewById(R.id.img_sleep);
		imgMiddleSleep = (ImageView) view.findViewById(R.id.img_sleep_mid);
		imgRest = (ImageView) view.findViewById(R.id.img_sleep_rest);
		tvStepInfo = (TextView) view.findViewById(R.id.tv_infomation_step_value);
		tvDistanceInfo = (TextView) view.findViewById(R.id.tv_infomation_distance_value);
		tvCalInfo = (TextView) view.findViewById(R.id.tv_infomation_cal_value);
		tvHeartRateInfo = (TextView) view.findViewById(R.id.tv_infomation_hr_value);
		tvSleepInfo = (TextView) view.findViewById(R.id.tv_infomation_sleep_value);
		tvMiddleInfo = (TextView) view.findViewById(R.id.tv_infomation_middle_value);
		tvRestInfo = (TextView) view.findViewById(R.id.tv_infomation_rest_value);

		frame = (FrameLayout) view.findViewById(R.id.vp_top);
		// 计步
		stepView = inflater.inflate(R.layout.view_infomation_step, null);
		stepCircle = (StepCircle) stepView.findViewById(R.id.step_circle);
		tvStep = (TextView) stepView.findViewById(R.id.tv_step);

		// 距离
		distanceView = inflater.inflate(R.layout.view_infomation_distance, null);
		disView = (DistanceView) distanceView.findViewById(R.id.view_distance);
		tvDistanceGoal = (TextView) distanceView.findViewById(R.id.tv_dis_pedo);
		tvDistanceComplete = (TextView) distanceView.findViewById(R.id.tv_distance);
		// 卡洛里
		kcalView = inflater.inflate(R.layout.view_infomation_cal, container, false);
		tvCalGoal = (TextView) kcalView.findViewById(R.id.tv_cal_value);
		setTextFace(tvCalGoal);
		tvCalComplete = (TextView) kcalView.findViewById(R.id.tv_cal_complete);
		tvCalCompleteInfo = (TextView) kcalView.findViewById(R.id.tv_cal_info);
		setTextFace(tvCalCompleteInfo);

		seekBarCal = (ColorSeekBar) kcalView.findViewById(R.id.progress_cal);
		seekBarCal.setEnabled(false);
		// 心率
		hrView = inflater.inflate(R.layout.view_infomation_hr, container, false);
		imgHr = (ImageView) hrView.findViewById(R.id.img_hr);
		seekBarHeartRate = (ColorSeekBar) hrView.findViewById(R.id.progress_hr);
		tvHeartRateValue = (TextView) hrView.findViewById(R.id.tv_hr_value);
		
		startHeartRateAnimation();

		// 睡眠
		sleepView = inflater.inflate(R.layout.view_infomation_sleep, container, false);
		// 午休
		middleSleepView = inflater.inflate(R.layout.view_infomation_middle_sleep, container, false);
		// 小憩
		restView = inflater.inflate(R.layout.view_infomation_rest, container, false);
		// 初始化
		frame.addView(stepView);

		setListener();
		// currentFragment = 0;
		// selected();
		return view;
	}

	private void setTextFace(TextView tv) {
		// 从assert中获取有资源，获得app的assert，采用getAserts()，通过给出在assert/下面的相对路径。在实际使用中，字体库可能存在于SD卡上，可以采用createFromFile()来替代createFromAsset。
		Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "font/DIGITAL-Regular.ttf");
		tv.setTypeface(face);
	}

	private void startHeartRateAnimation() {
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(imgHr, "scaleX", 1f, 3f, 1f);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(imgHr, "scaleY", 1f, 3f, 1f);
		animator1.setDuration(200);
		animator1.setRepeatCount(-1); // 动画循环播放的次数
		animator2.setDuration(200);
		animator2.setRepeatCount(-1); // 动画循环播放的次数
		animator1.start();
		animator2.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_sport_pedo:
			clearSelected();
			frame.removeAllViews();
			frame.addView(stepView);
			imgStep.setImageResource(R.drawable.sport_monitor_pedomete_icon_on);
			maxstep = 1000000;
			step = (int) (Math.random() * maxstep);
			setStepValue(step, maxstep);

			// 模拟
			float max = maxstep / STEP_TO_DISTANCE;
			// float dis = (float) (Math.random() * max);
			float dis = step / STEP_TO_DISTANCE;
			setDistanceValue(dis, max);

			// 模拟
			float maxCal = maxstep / STEP_TO_KCAL;
			float progressCal = step / STEP_TO_KCAL;
			setCalValue(maxCal, progressCal);

			break;
		case R.id.img_sport_distance:
			clearSelected();
			frame.removeAllViews();
			frame.addView(distanceView);
			imgDistance.setImageResource(R.drawable.sport_monitor_distance_icon_on);

			// currentFragment = 1;
			// selected();

			break;
		case R.id.img_sport_cal:
			clearSelected();
			frame.removeAllViews();
			frame.addView(kcalView);
			imgCal.setImageResource(R.drawable.sport_monitor_cal_icon_on);

			// currentFragment = 2;
			// selected();
			break;
		case R.id.img_sport_hr:
			clearSelected();
			frame.removeAllViews();
			frame.addView(hrView);
			imgHeart.setImageResource(R.drawable.sport_monitor_hr_icon_on);
			// currentFragment = 3;
			// selected();
			break;
		case R.id.img_sleep:
			clearSelected();
			frame.removeAllViews();
			frame.addView(sleepView);
			imgSleep.setImageResource(R.drawable.sleep_tracking_sleep_icon_on);
			// currentFragment = 4;
			// selected();
			break;
		case R.id.img_sleep_mid:
			clearSelected();
			frame.removeAllViews();
			frame.addView(middleSleepView);
			imgMiddleSleep.setImageResource(R.drawable.sleep_tracking_midday_rest_icon_on);
			// currentFragment = 5;
			// selected();
			break;
		case R.id.img_sleep_rest:
			clearSelected();
			frame.removeAllViews();
			frame.addView(restView);
			imgRest.setImageResource(R.drawable.sleep_tracking_rest_icon_on);
			// currentFragment = 6;
			// selected();
			break;

		default:
			break;
		}
	}

	// private void selected() {
	// clearSelected();
	// fm = getActivity().getSupportFragmentManager();
	// beginTransaction = fm.beginTransaction();
	// switch (currentFragment) {
	// case 0:
	// imgStep.setImageResource(R.drawable.sport_monitor_pedomete_icon_on);
	// if (stepFragment == null) {
	//
	// }
	// beginTransaction.replace(R.id.vp_top, stepFragment);
	// break;
	// case 1:
	// imgDistance.setImageResource(R.drawable.sport_monitor_distance_icon_on);
	// if (distanceFragment == null) {
	//
	// }
	// beginTransaction.replace(R.id.vp_top, distanceFragment);
	// break;
	// case 2:
	// imgCal.setImageResource(R.drawable.sport_monitor_cal_icon_on);
	// if (kcalFragment == null) {
	// kcalFragment = new KcalFragment();
	// }
	// beginTransaction.replace(R.id.vp_top, kcalFragment);
	// break;
	// case 3:
	// imgHeart.setImageResource(R.drawable.sport_monitor_hr_icon_on);
	// if (heartFragment == null) {
	// heartFragment = new HeartRateFragment();
	// }
	// beginTransaction.replace(R.id.vp_top, heartFragment);
	// break;
	// case 4:
	// imgSleep.setImageResource(R.drawable.sleep_tracking_sleep_icon_on);
	// if (sleepFragment == null) {
	// sleepFragment = new SleepFragment();
	// }
	// beginTransaction.replace(R.id.vp_top, sleepFragment);
	// break;
	// case 5:
	// imgMiddleSleep.setImageResource(R.drawable.sleep_tracking_midday_rest_icon_on);
	// if (middleFragment == null) {
	// middleFragment = new SleepMiddleFragment();
	// }
	// beginTransaction.replace(R.id.vp_top, middleFragment);
	// break;
	// case 6:
	// imgRest.setImageResource(R.drawable.sleep_tracking_rest_icon_on);
	// if (restFragment == null) {
	// restFragment = new RestFragment();
	// }
	// beginTransaction.replace(R.id.vp_top, restFragment);
	// break;
	//
	// default:
	// break;
	// }
	// beginTransaction.commit();
	// }

	private void setCalValue(float max, float progress) {
		seekBarCal.setMax((int) max);
		seekBarCal.setProgress((int) progress);
		String proStr = DataUtil.format(progress);
		tvCalGoal.setText(proStr);
		tvCalCompleteInfo.setText("目标" + max + "Kcal");
		updateCalInfo(progress);
		tvCalInfo.setText(proStr + "千卡");
	}

	private void updateCalInfo(float progress) {
		StringBuffer sb = new StringBuffer();
		sb.append("已消耗");
		sb.append(DataUtil.format(progress));
		if (progress < 10) {
			sb.append("过低");
		} else if (progress > 10 && progress < 100) {
			sb.append("适中");
		} else if (progress > 100) {
			sb.append("偏高");
		}
		tvCalComplete.setText(sb.toString());
		tvCalInfo.setText(sb.toString());
	}

	private void setDistanceValue(float completeDistance, float maxDistance) {
		disView.setMaxDistance(maxDistance);
		tvDistanceGoal.setText("目标距离	:" + maxDistance + "(km)");
		String value = DataUtil.format(completeDistance);
		tvDistanceComplete.setText("完成" + value + "千米");
		tvDistanceInfo.setText(value + "千米");
		disView.setCurrentDistance(completeDistance);

	}

	private void setStepValue(int step, int max) {
		Log.e("", "step : " + step);
		stepCircle.setMaxProgress(max);
		stepCircle.setProgress(step);
		tvStep.setText("已走步数 ：" + step);
		tvStepInfo.setText("已走步数 ：" + step);

	}

	private void setHrearRateValue(int value) {
		seekBarHeartRate.setProgress(value);
		tvHeartRateValue.setText(value);
		updateHeartRateInfo(value);
		tvDistanceInfo.setText(value+"");
	}

	private void updateHeartRateInfo(int value) {
		StringBuffer sb = new StringBuffer();
		sb.append("当前心率 ：");
		if (value < 10) {
			sb.append("过低");
		} else if (value > 10 && value < 100) {
			sb.append("适中");
		} else if (value > 100) {
			sb.append("偏高");
		}
		tvDistanceInfo.setText(sb.toString());
	}

	private void clearSelected() {
		imgStep.setImageResource(R.drawable.sport_monitor_pedomete_icon);
		imgDistance.setImageResource(R.drawable.sport_monitor_distance_icon);
		imgCal.setImageResource(R.drawable.sport_monitor_cal_icon);
		imgHeart.setImageResource(R.drawable.sport_monitor_hr_icon);
		imgSleep.setImageResource(R.drawable.sleep_tracking_sleep_icon);
		imgMiddleSleep.setImageResource(R.drawable.sleep_tracking_midday_rest_icon);
		imgRest.setImageResource(R.drawable.sleep_tracking_rest_icon);
	}

	private void setListener() {
		imgStep.setOnClickListener(this);
		imgDistance.setOnClickListener(this);
		imgCal.setOnClickListener(this);
		imgHeart.setOnClickListener(this);
		imgSleep.setOnClickListener(this);
		imgMiddleSleep.setOnClickListener(this);
		imgRest.setOnClickListener(this);
	}

}
