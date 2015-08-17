package com.mycj.healthy.fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mycj.healthy.BaseApp;
import com.mycj.healthy.R;
import com.mycj.healthy.entity.HeartRateData;
import com.mycj.healthy.entity.HistoryData;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.ui.HeartRateCountActivity;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.DataUtil;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.util.TimeUtil;
import com.mycj.healthy.view.ColorSeekBar;
import com.mycj.healthy.view.DistanceView;
import com.mycj.healthy.view.GreyCircle;
import com.mycj.healthy.view.HeartRatePathView;
import com.mycj.healthy.view.SimpleHeartRateView;
import com.mycj.healthy.view.StepCircle;
import com.mycj.healthy.view.SimpleHeartRateView.OnDataChangeListener;
import com.mycj.healthy.view.SimpleHeartRateView.OnScrollChangeListener;

public class InformationFragment extends Fragment implements OnClickListener {
	private ImageView imgStep, imgDistance, imgCal, imgHeart, imgSleep, imgMiddleSleep, imgRest;
	/**
	 * 当前页面
	 */
	private TextView tvStepViewStep;
	private SimpleHeartRateView hrv;
	private HeartRatePathView pathView;
	private FrameLayout frame;
	private View stepView;
	private View distanceView;
	private StepCircle stepCircle;
	private DistanceView disView;
	private TextView tvDistanceViewGoal;
	private TextView tvDistanceViewComplete;
	private View kcalView;
	private TextView tvCalViewGoal;
	private TextView tvCalViewCompleteInfo;
	private ColorSeekBar seekBarCal, seekBarHeartRate;
	private View hrView;
	private View sleepView;
	private View middleSleepView;
	private View restView;
	private ImageView imgHr;
	private TextView tvStepInfo, tvDistanceInfo, tvCalInfo, tvHeartRateInfo, tvSleepInfo, tvMiddleInfo, tvRestInfo;

	private final float STEP_TO_DISTANCE = 4000f / (10000 * 1000f);
	private final float STEP_TO_KCAL = 1000f / (7000f * 1000);
	private final int STEP_DEFAUL = 20000;

	private TextView tvHeartRateViewValue;
	private TextView tvStepViewModle;
	private TextView tvSleepAdviser;
	private TextView tvSleepValue;
	private TextView tvMidSleepAdviser;
	private TextView tvMidSleepValue;
	private TextView tvRestAdviser;
	private TextView tvRestValue;
	private TextView tvHeartRateViewAdviser;
	private Resources resource;

	/** ------------- **/
	private int goalStep;
	private int completeStep;
	private int currentHr;
	private float sleepHour;
	private float middleHour;
	private float restHour;
	/** ------------- **/

	private Handler mHandler = new Handler() {

	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED)) {
			} else if (action.equals(LiteBlueService.LITE_GATT_DISCONNECTED)) {
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_HEART_RATE)) {
				Log.e("", "^^^^^^^^^^^^^^^");
				final HeartRateData mHeartRateData = intent.getExtras().getParcelable(LiteBlueService.EXTRA_DATA_HEART_RATE);
				Log.e("", "^^^^^^^^^^^^^^^mHeartRateData :" + mHeartRateData);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateHeartRate(mHeartRateData.getHeartRate());
						// hrv.setData(hr);
						// pathView.setData(hr);
					}
				});
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_SLEEP)) {

				final int[] data = intent.getExtras().getIntArray(LiteBlueService.EXTRA_DATA_SLEEP);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						updateSleep(data[0], data[1], data[3]);
					}
				});
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_STEP)) {
				final int step = intent.getExtras().getInt(LiteBlueService.EXTRA_DATA_STEP);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {

						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (step >= getGoalStep()) {
									SharedPreferenceUtil.put(getActivity(), Constant.SHARE_STEP_ON_OFF, false);
								}
								updateStep(step);
							}
						});

					}
				});
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_GOAL_STEP)) {
				Log.e("", "-------------------<<<<<<<<<>>>>>>>>>>>>>>>>>>-----------");
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateStep(completeStep);
					}
				});
			}
		}
	};
	private LiteBlueService mLiteBlueService;
	private RelativeLayout rlHeart;
	private TextView tvCalViewComplete;
	private ObjectAnimator animator1;
	private ObjectAnimator animator2;
	private GreyCircle sleepCircle;
	private GreyCircle middleSleepCircle;
	private GreyCircle restCircle;

	@Override
	public void onStart() {
		Log.v("InformationFragment", "Fragment生命周期之onStart()");
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v("InformationFragment", "Fragment生命周期之onCreateView()");
		// 加载布局文件
		View view = inflater.inflate(R.layout.fragment_infomation, container, false);
		resource = getResources();
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

		// boolean isModel = getStepModel();

		// 计步
		stepView = inflater.inflate(R.layout.view_infomation_step, container, false);
		stepCircle = (StepCircle) stepView.findViewById(R.id.step_circle);
		tvStepViewStep = (TextView) stepView.findViewById(R.id.tv_step);
		tvStepViewModle = (TextView) stepView.findViewById(R.id.tv_step_model);
		// initStep(isModel);

		// 距离
		distanceView = inflater.inflate(R.layout.view_infomation_distance, container, false);
		disView = (DistanceView) distanceView.findViewById(R.id.view_distance);
		tvDistanceViewGoal = (TextView) distanceView.findViewById(R.id.tv_dis_pedo);
		tvDistanceViewComplete = (TextView) distanceView.findViewById(R.id.tv_distance);
		// initDistance(isModel);

		// 卡洛里
		kcalView = inflater.inflate(R.layout.view_infomation_cal, container, false);
		tvCalViewGoal = (TextView) kcalView.findViewById(R.id.tv_cal_goal);
		tvCalViewCompleteInfo = (TextView) kcalView.findViewById(R.id.tv_cal_complete_info);
		tvCalViewComplete = (TextView) kcalView.findViewById(R.id.tv_cal_complete);
		setTextFace(tvCalViewComplete);
		setTextFace(tvCalViewGoal);
		seekBarCal = (ColorSeekBar) kcalView.findViewById(R.id.progress_cal);
		seekBarCal.setEnabled(false);
		// initCal(isModel);

		// 心率
		hrView = inflater.inflate(R.layout.view_infomation_hr, container, false);
		imgHr = (ImageView) hrView.findViewById(R.id.img_hr);
		seekBarHeartRate = (ColorSeekBar) hrView.findViewById(R.id.progress_hr);
		tvHeartRateViewValue = (TextView) hrView.findViewById(R.id.tv_hr_value);
		tvHeartRateViewAdviser = (TextView) hrView.findViewById(R.id.tv_hr_info);
		rlHeart = (RelativeLayout) hrView.findViewById(R.id.rl_hr);
		seekBarHeartRate.setEnabled(false);

		// //心率统计
		// hrCountView = inflater.inflate(R.layout.activity_heart_rate_count,
		// container, false);
		// hrv = (SimpleHeartRateView) hrCountView.findViewById(R.id.hrv);
		// pathView = (HeartRatePathView)
		// hrCountView.findViewById(R.id.path_view);

		// 睡眠
		sleepView = inflater.inflate(R.layout.view_infomation_sleep_1, container, false);
		tvSleepAdviser = (TextView) sleepView.findViewById(R.id.tv_sleep_adv);
		tvSleepValue = (TextView) sleepView.findViewById(R.id.tv_sleep_info);
		sleepCircle = (GreyCircle) sleepView.findViewById(R.id.sleep_circle);
		float maxSleep = getMaxSleep();// 单位：分钟
		if (maxSleep != 0) {
			sleepCircle.setMax(maxSleep / (1000 * 60));
		}
//		sleepCircle.setProgress(120);

		// 午休
		middleSleepView = inflater.inflate(R.layout.view_infomation_middle_sleep, container, false);
		tvMidSleepAdviser = (TextView) middleSleepView.findViewById(R.id.tv_sleep_middle_adv);
		tvMidSleepValue = (TextView) middleSleepView.findViewById(R.id.tv_sleep_middle_info);
		middleSleepCircle = (GreyCircle) middleSleepView.findViewById(R.id.middle_sleep_circle);
		float maxMiddleSleep = getMaxMiddleSleep();
		if (maxMiddleSleep != 0) {
			middleSleepCircle.setMax(maxMiddleSleep / (1000 * 60));
		}
//		middleSleepCircle.setProgress(0.1f);
		// 小憩
		restView = inflater.inflate(R.layout.view_infomation_rest, container, false);
		tvRestAdviser = (TextView) restView.findViewById(R.id.tv_sleep_rest_adv);
		tvRestValue = (TextView) restView.findViewById(R.id.tv_sleep_rest_info);
		restCircle = (GreyCircle) restView.findViewById(R.id.rest_circle);
		restCircle.setMax(60*6);//小憩最大值为60*12分钟
		// initSleep();
		// 初始化
		frame.addView(stepView);

		setListener();
		return view;
	}

	private float getMaxMiddleSleep() {
		int middleStartHourValue = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_MIDDLE_SLEEP_START_HOUR, 12);
		int middleStartMinValue = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_MIDDLE_SLEEP_START_MIN, 00);
		int middleEndHourValue = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_MIDDLE_SLEEP_END_HOUR, 12);
		int middleEndMinValue = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_MIDDLE_SLEEP_END_MIN, 00);
		long diff = getLong(middleStartHourValue, middleStartMinValue, middleEndHourValue, middleEndMinValue);
		Log.v("", "午睡" + "时间差：" + diff);
		return diff;
	}

	/**
	 * 根据设定的睡眠/午休 时间 返回之间的时间差
	 * 
	 * @param startHourValue
	 * @param startMinValue
	 * @param endHourValue
	 * @param endMinValue
	 * @return
	 */
	private long getLong(int startHourValue, int startMinValue, int endHourValue, int endMinValue) {
		Log.v("", " 开始时刻 ：" + startHourValue + ":" + startMinValue);
		Log.v("", " 结束时刻 ：" + endHourValue + ":" + endMinValue);
		Calendar startCalendar = Calendar.getInstance();
		Date date = new Date();
		Log.v("", " 现在时间 ：" + TimeUtil.dateToString(date, "yyyy-MM-dd HH:mm:ss"));
		startCalendar.setTime(date);
		startCalendar.set(Calendar.HOUR_OF_DAY, startHourValue);
		startCalendar.set(Calendar.MINUTE, startMinValue);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(date);
		endCalendar.set(Calendar.HOUR_OF_DAY, endHourValue);
		endCalendar.set(Calendar.MINUTE, endMinValue);
		
		if (endCalendar.get(Calendar.HOUR_OF_DAY) < startCalendar.get(Calendar.HOUR_OF_DAY)) {
			Log.v("", "结束小时小于开始");
			endCalendar.add(Calendar.DAY_OF_YEAR, 1);
		} else if (endCalendar.get(Calendar.HOUR_OF_DAY) == startCalendar.get(Calendar.HOUR_OF_DAY)) {
			if (endCalendar.get(Calendar.MINUTE) <= startCalendar.get(Calendar.MINUTE)) {
				Log.v("", "结束分钟小于开始");
				endCalendar.add(Calendar.DAY_OF_YEAR, 1);
			}
		}
		
		Log.v("", " 开始时间 ：" + TimeUtil.dateToString(startCalendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
		Log.v("", " 结束时间 ：" + TimeUtil.dateToString(endCalendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
		return endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
	}

	private long getMaxSleep() {
		int sleepStartHourValue = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_START_HOUR, 12);
		int sleepStartMinValue = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_START_MIN, 00);
		int sleepEndHourValue = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_END_HOUR, 12);
		int sleepEndMinValue = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_END_MIN, 00);
		long diff = getLong(sleepStartHourValue, sleepStartMinValue, sleepEndHourValue, sleepEndMinValue);
		Log.v("", "睡眠时间差：" + diff);
		return diff;
	}

	@Override
	public void onResume() {
		boolean isModel = getStepModel();
		initStep(isModel);
		initDistance(isModel);
		initCal(isModel);
		initSleep();
		initHeartRate();
		updateSleep();
		Log.v("InformationFragment", "Fragment生命周期之onResume()");
		super.onResume();
		startHeartRateAnimation();
		mLiteBlueService = ((BaseApp) getActivity().getApplication()).getLiteBlueService();
		getActivity().registerReceiver(mReceiver, LiteBlueService.getIntentFilter());
		// ((BaseApp)(getActivity().getApplication())).setOnStepChangListener(this);
		// ((BaseApp)(getActivity().getApplication())).setOnHeartRateChangListener(this);
		Log.e("InformationFragment", "mLiteBlueService" + mLiteBlueService);
	}

	@Override
	public void onPause() {
		Log.v("InformationFragment", "Fragment生命周期之onPause()");
		super.onPause();
		getActivity().unregisterReceiver(mReceiver);
		stopHeartRateAnimation();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_sport_pedo:
			clearSelected();
			frame.removeAllViews();
			frame.addView(stepView);
			imgStep.setImageResource(R.drawable.sport_monitor_pedomete_icon_on);

			break;
		case R.id.img_sport_distance:
			clearSelected();
			frame.removeAllViews();
			frame.addView(distanceView);
			imgDistance.setImageResource(R.drawable.sport_monitor_distance_icon_on);

			break;
		case R.id.img_sport_cal:
			clearSelected();
			frame.removeAllViews();
			frame.addView(kcalView);
			imgCal.setImageResource(R.drawable.sport_monitor_cal_icon_on);
			break;
		case R.id.img_sport_hr:
			clearSelected();
			startHeartRateAnimation();
			frame.removeAllViews();
			frame.addView(hrView);
			startHeartRateAnimation();
			imgHeart.setImageResource(R.drawable.sport_monitor_hr_icon_on);
			break;
		case R.id.img_sleep:
			clearSelected();
			frame.removeAllViews();
			frame.addView(sleepView);
			imgSleep.setImageResource(R.drawable.sleep_tracking_sleep_icon_on);
	
			break;
		case R.id.img_sleep_mid:
			clearSelected();
			frame.removeAllViews();
			frame.addView(middleSleepView);
			imgMiddleSleep.setImageResource(R.drawable.sleep_tracking_midday_rest_icon_on);
			break;
		case R.id.img_sleep_rest:
			clearSelected();
			frame.removeAllViews();
			frame.addView(restView);
			imgRest.setImageResource(R.drawable.sleep_tracking_rest_icon_on);
			break;
		case R.id.rl_hr:
			// clearSelected();
			// frame.removeAllViews();
			// frame.addView(hrCountView);
			// isShow = true;
			startActivity(new Intent(getActivity(), HeartRateCountActivity.class));
			break;

		default:
			break;
		}
	}

	/**
	 * 初始化 步数 2种模式：计步模式 & 随意模式
	 * 
	 * @param isModel
	 */
	private void initStep(boolean isModel) {
		String goldStepStr = resource.getString(R.string.gold_step);
		String unbendingStr = resource.getString(R.string.unbinding_model);
		String completeStr = resource.getString(R.string.complete_step);
		String stepUnit = resource.getString(R.string.step);

		if (isModel) {
			goalStep = getGoalStep();
			Log.e("InformationFragment", "___goalStep : " + goalStep);
			stepCircle.setMaxProgress(goalStep);
			stepCircle.setProgress(completeStep);
			tvStepViewModle.setText(goldStepStr + goalStep);
		} else {
			tvStepViewModle.setText(unbendingStr);
			stepCircle.setProgress(0);
		}
		tvStepViewStep.setText(completeStr + String.valueOf(completeStep) + stepUnit);
		tvStepInfo.setText(String.valueOf(completeStep) + stepUnit);
	}

	/**
	 * 初始化 距离
	 * 
	 * @param isModel
	 */
	private void initDistance(boolean isModel) {
		String targetStr = resource.getString(R.string.target_distance);
		String completeStr = resource.getString(R.string.complete_distance);
		float goal;
		if (isModel) {
			goal = stepToDistance(getGoalStep());
			disView.setMaxDistance(goal);
		} else {
			goal = stepToDistance(20000);
			disView.setMaxDistance(goal);
		}
		tvDistanceViewGoal.setText(targetStr + DataUtil.format(goal) + "km");
		String completeDistance = DataUtil.format(stepToDistance(completeStep));
		tvDistanceViewComplete.setText(completeStr + completeDistance + "km");
		tvDistanceInfo.setText(completeDistance + "km");
	}

	/**
	 * 初始化 卡洛里
	 * 
	 * @param isModel
	 */
	private void initCal(boolean isModel) {
		String targetStr = resource.getString(R.string.target_cal);
		String completeStr = resource.getString(R.string.complete_cal);

		float goal;
		if (isModel) {
			goal = stepToCal(getGoalStep());
			seekBarCal.setMax((int) goal);
		} else {
			goal = stepToCal(20000);
			seekBarCal.setMax((int) goal);
		}
		tvCalViewGoal.setText(targetStr + DataUtil.format(goal) + "kcal");
		String completeCal = DataUtil.format(stepToCal(completeStep));
		tvCalViewComplete.setText(completeCal);
		tvCalViewCompleteInfo.setText(completeStr + completeCal + "kcal");
		tvCalInfo.setText(completeCal + "kcal");
		seekBarCal.setProgress((int) stepToCal(completeStep));
		checkCalInfo(stepToCal(completeStep), goal);
	}

	/**
	 * 初始化 心率
	 */
	private void initHeartRate() {
		tvHeartRateViewValue.setText("" + currentHr);
		seekBarHeartRate.setMax(180);
		seekBarHeartRate.setProgress(currentHr);
		tvHeartRateViewAdviser.setText("");
		tvHeartRateInfo.setText(currentHr + " bpm");
		checkHeartRateInfo(currentHr);
	}

	/**
	 * 初始化睡眠
	 */
	private void initSleep() {
		String hourStr = resource.getString(R.string.hour);
		String sleepStr = resource.getString(R.string.sleep_tips);
		String middleStr = resource.getString(R.string.siesta_tips);
		String restStr = resource.getString(R.string.nap_tips);

		tvSleepInfo.setText("" + DataUtil.format(sleepHour/60f) + hourStr);
		checkSleepAdviser(sleepHour);
		tvSleepValue.setText(sleepStr + DataUtil.format(sleepHour/60f) + hourStr);

		tvMiddleInfo.setText("" + DataUtil.format(middleHour/60f) + hourStr);
		checkMidSleepAdviser(middleHour);
		tvMidSleepValue.setText(middleStr + DataUtil.format(middleHour/60f) + hourStr);

		tvRestInfo.setText("" + DataUtil.format(restHour/60f) + hourStr);
		checkRestAdviser(restHour);
		tvRestValue.setText(restStr + DataUtil.format(restHour/60f) + hourStr);
	}

	/**
	 * 手表 -->手机 数据更新 更新步数
	 * 
	 * @param step
	 */
	private void updateStep(int step) {
		completeStep = step;
		// String completeStr = resource.getString(R.string.complete_step);
		// String stepUnitStr = resource.getString(R.string.step);
		// stepCircle.setProgress(step);
		// tvStepViewStep.setText(completeStr + step +stepUnitStr);
		// tvStepInfo.setText("" + step + stepUnitStr);
		boolean isModel = getStepModel();
		Log.e("", "````````````````````````````````````isModel : " + isModel);
		initStep(isModel);
		updateDistance(step);
		updateCal(step);
	}

	/**
	 * 手表 -->手机 数据更新
	 * 
	 * @param step
	 */
	private void updateDistance(int step) {
		// String completeStr = resource.getString(R.string.complete_distance);
		// tvDistanceViewComplete.setText(completeStr +
		// DataUtil.format(stepToDistance(step)) + "km");
		// disView.setCurrentDistance(stepToDistance(step));
		// tvDistanceInfo.setText(DataUtil.format(stepToDistance(step)) + "km");
		boolean isModel = getStepModel();
		initDistance(isModel);
	}

	private void updateCal(int step) {
		// tvCalInfo.setText(DataUtil.format(stepToCal(step)) + " kcal");
		// // tvCalViewCompleteInfo.setText("完成卡洛里："+stepToCal(step));
		// seekBarCal.setProgress((int) stepToCal(step));
		// tvCalViewComplete.setText(DataUtil.format(stepToCal(step)) + "");
		// checkCalInfo(stepToCal(step), stepToCal(getGoalStep()));
		boolean isModel = getStepModel();
		initCal(isModel);
	}

	private void updateHeartRate(int hr) {
		currentHr = hr;
		// tvHeartRateViewValue.setText(String.valueOf(hr));
		// seekBarHeartRate.setProgress(hr);
		// checkHeartRateInfo(hr);
		// tvHeartRateInfo.setText(hr + " bpm");
		initHeartRate();
	}

	private void updateSleep() {
		String dateStr1=  TimeUtil.dateToString(new Date());
		List<HistoryData> historyDatas = DataSupport.where("historyDate=?",dateStr1).find(HistoryData.class);
		Log.v("", "____________________historyDatas : " + historyDatas.size());
		if (historyDatas!=null&&historyDatas.size()!=0) {
			Log.v("",  historyDatas.get(0).toString());
			HistoryData data = historyDatas.get(0);
			sleepCircle.setProgress(data.getSleep());
			middleSleepCircle.setProgress(data.getMiddle());
			restCircle.setProgress(data.getRest());
			sleepHour = data.getSleep();
			middleHour = data.getMiddle();
			restHour = data.getRest();
		}
//		List<HistoryData> historyDatasAll = DataSupport.findAll(HistoryData.class);
//		Log.v("", "______________________________________________________");
//		for(HistoryData data : historyDatasAll){
//			Log.v("", data.toString());
//		}
//		Log.v("", "______________________________________________________");
		
	
		//
		initSleep();
	}

	private float stepToDistance(int step) {
		return step * STEP_TO_DISTANCE;
	}

	private float stepToCal(int step) {
		return step * STEP_TO_KCAL;
	}

	private boolean getStepModel() {
		return (boolean) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_STEP_ON_OFF, false);
	}

	private int getGoalStep() {
		return (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_STEP_GOAL, STEP_DEFAUL);
	}

	/**
	 * 从assert中获取有资源，获得app的assert，采用getAserts()，通过给出在assert/下面的相对路径。在实际使用中，
	 * 字体库可能存在于SD卡上，可以采用createFromFile()来替代createFromAsset。
	 * 
	 * @param tv
	 */
	private void setTextFace(TextView tv) {

		Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "font/DIGITAL-Regular.ttf");
		tv.setTypeface(face);
	}

	private void startHeartRateAnimation() {
		animator1 = ObjectAnimator.ofFloat(imgHr, "scaleX", 0.8f, 1.8f, 0.8f);
		animator2 = ObjectAnimator.ofFloat(imgHr, "scaleY", 0.8f, 1.8f, 0.8f);
		animator1.setDuration(1200);
		animator1.setRepeatCount(-1); // 动画循环播放的次数
		animator2.setDuration(1200);
		animator2.setRepeatCount(-1); // 动画循环播放的次数
		animator1.start();
		animator2.start();
	}

	private void stopHeartRateAnimation() {
		if (animator1 != null) {
			animator1.cancel();
			animator2.cancel();
		}
	}

	private void checkCalInfo(float progress, float max) {
		String completeStr = resource.getString(R.string.complete_cal);
		String lowStr = resource.getString(R.string.low);
		String normalStr = resource.getString(R.string.nomal);
		String sportStr = resource.getString(R.string.sport);
		String highStr = resource.getString(R.string.high);

		StringBuffer sb = new StringBuffer();
		sb.append(completeStr);
		sb.append(DataUtil.format(progress));
		sb.append("Kcal,");
		float deff = progress / max;
		if (deff >= 0 && deff < 0.5f) {
			sb.append(lowStr);
		} else if (deff >= 0.5f && progress < 1f) {
			sb.append(normalStr);
		} else if (deff >= 1f && deff < 1.5f) {
			sb.append(sportStr);
		} else if (deff >= 2f) {
			sb.append(highStr);
		}
		tvCalViewCompleteInfo.setText(sb.toString());
	}

	private void checkHeartRateInfo(int value) {
		String currentHr = resource.getString(R.string.hr_value);
		String lowStr = resource.getString(R.string.low);
		String normalStr = resource.getString(R.string.nomal);
		String sportStr = resource.getString(R.string.sport);
		String highStr = resource.getString(R.string.high);
		StringBuffer sb = new StringBuffer();
		Log.v("", "value : " + value);
		sb.append(currentHr);
		if (value >= 0 && value < 40) {
			sb.append(lowStr);
		} else if (value >= 40 && value < 120) {
			sb.append(normalStr);
		} else if (value >= 120 && value < 160) {
			sb.append(sportStr);
		} else if (value >= 120 && value < 200) {
			sb.append(highStr);
		}
		tvHeartRateViewAdviser.setText(sb.toString());
	}

	private void checkSleepAdviser(float value) {
		String sleepStr = resource.getString(R.string.sleep_eval);
		String lowStr = resource.getString(R.string.eval_sleep_leak);
		String normalStr = resource.getString(R.string.eval_sleep_normal);
		String noData = resource.getString(R.string.no_data);
		String highStr = resource.getString(R.string.eval_sleep_high);
		StringBuffer sb = new StringBuffer();
		sb.append(sleepStr);
		if (value > 0 && value < 6) {
			sb.append(lowStr);
		} else if (value >= 6 && value < 9) {
			sb.append(normalStr);
		} else if (value == 0) {
			sb.append(noData);
		} else {
			sb.append(highStr);
		}
		tvSleepAdviser.setText(sb.toString());
	}

	private void checkMidSleepAdviser(float value) {
		String siestaStr = resource.getString(R.string.siesta_eval);
		String lowStr = resource.getString(R.string.eval_sleep_leak);
		String normalStr = resource.getString(R.string.eval_sleep_normal);
		String highStr = resource.getString(R.string.eval_sleep_high);
		String noData = resource.getString(R.string.no_data);
		StringBuffer sb = new StringBuffer();
		sb.append(siestaStr);
		if (value > 0 && value < 0.5) {
			sb.append(lowStr);
		} else if (value == 0) {
			sb.append(noData);
		} else if (value >= 0.5 && value < 2) {
			sb.append(normalStr);
		} else {
			sb.append(highStr);
		}
		tvMidSleepAdviser.setText(sb.toString());
	}

	private void checkRestAdviser(float value) {
		String noData = resource.getString(R.string.no_data);
		String napStr = resource.getString(R.string.nap_eval);
		String lowStr = resource.getString(R.string.eval_sleep_leak);
		String normalStr = resource.getString(R.string.eval_sleep_normal);
		String highStr = resource.getString(R.string.eval_sleep_high);
		StringBuffer sb = new StringBuffer();
		sb.append(napStr);

		if (value > 0 && value < 1) {
			sb.append(lowStr);
		} else if (value >= 1 && value < 6) {
			sb.append(normalStr);
		} else if (value == 0) {
			sb.append(noData);
		} else {
			sb.append(highStr);
		}
		tvRestAdviser.setText(sb.toString());
	}

	private void clearSelected() {
		stopHeartRateAnimation();
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
		rlHeart.setOnClickListener(this);
		imgDistance.setOnClickListener(this);
		imgCal.setOnClickListener(this);
		imgHeart.setOnClickListener(this);
		imgSleep.setOnClickListener(this);
		imgMiddleSleep.setOnClickListener(this);
		imgRest.setOnClickListener(this);
	}

}
