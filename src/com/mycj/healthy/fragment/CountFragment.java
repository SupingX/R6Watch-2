package com.mycj.healthy.fragment;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycj.healthy.R;
import com.mycj.healthy.view.AbstractDayCountView;
import com.mycj.healthy.view.AbstractWeekdayCountView.OnDateChangeListener;
import com.mycj.healthy.view.SleepDayView;
import com.mycj.healthy.view.SleepMonthView;
import com.mycj.healthy.view.SleepWeekDayView;
import com.mycj.healthy.view.StepDayView;
import com.mycj.healthy.view.StepMonthView;
import com.mycj.healthy.view.StepWeekDayView;

public class CountFragment extends Fragment implements OnClickListener {
	private FrameLayout frame;
	private View viewWeekday;
	private View viewMonth;
	private TextView tvWeekday;
	private TextView tvMonth;
	private StepDayView stepWeekDayView;
	private ImageView imgIncreaseStepWeekday;
	private ImageView imgReduceStepWeekday;
	private ImageView imgIncreaseSleepWeekday;
	private ImageView imgReduceSleepWeekday;
	private SleepDayView sleepWeekDayView;
	private AbstractDayCountView.OnDateChangeListener mOnDateChangeListenerForStep = new AbstractDayCountView.OnDateChangeListener() {

		@Override
		public void onPrevious(Date date) {
			Log.e("", "上移一天");
//			updateText(date,0);
		}

		@Override
		public void onNext(Date date) {
			Log.e("", "下移一天");
//			updateText(date,0);
		}

		@Override
		public void onNow(Date date) {
			
		}

	};

	private AbstractDayCountView.OnDateChangeListener mOnDateChangeListenerForSleep = new AbstractDayCountView.OnDateChangeListener() {

		@Override
		public void onPrevious(Date date) {
//			updateText(date,1);
		}

		@Override
		public void onNext(Date date) {
//			updateText(date,1);
		}

		@Override
		public void onNow(Date date) {
			
		}

	};
//	private TextView tvStepWeekdayInfo1;
//	private TextView tvStepWeekdayInfo2;
//	private TextView tvSleepWeekdayInfo1;
//	private TextView tvSleepWeekdayInfo2;
	private StepMonthView stepMonthView;
	private ImageView imgIncreaseStepMonth;
	private ImageView imgReduceStepMonth;
	private SleepMonthView sleepMonthView;
	private ImageView imgIncreaseSleepMonth;
	private ImageView imgReduceSleepMonth;
	private TextView tvStepNowDay;
	private TextView tvSleepNowDay;
	private TextView tvStepNowMonth;
	private TextView tvSleepNowMonth;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_count, container, false);
		frame = (FrameLayout) view.findViewById(R.id.frame_count);
		tvWeekday = (TextView) view.findViewById(R.id.tv_weekday);
		tvMonth = (TextView) view.findViewById(R.id.tv_month);
		tvWeekday.setOnClickListener(this);
		tvMonth.setOnClickListener(this);
	

		// 周
		viewWeekday = inflater.inflate(R.layout.view_count_weekday, container, false);
			//		1〉计步
		stepWeekDayView = (StepDayView) viewWeekday.findViewById(R.id.smcv);
		imgIncreaseStepWeekday = (ImageView) viewWeekday.findViewById(R.id.img_increase_step_weekday);
		imgReduceStepWeekday = (ImageView) viewWeekday.findViewById(R.id.img_reduce_step_weekday);
//		tvStepWeekdayInfo1 = (TextView) viewWeekday.findViewById(R.id.tv_step_weekday_1);
//		tvStepWeekdayInfo2 = (TextView) viewWeekday.findViewById(R.id.tv_step_weekday_2);
		imgIncreaseStepWeekday.setOnClickListener(this);
		imgReduceStepWeekday.setOnClickListener(this);
		tvStepNowDay = (TextView) viewWeekday.findViewById(R.id.tv_step_count_now_day);
		tvSleepNowDay = (TextView) viewWeekday.findViewById(R.id.tv_sleep_count_now_day);
		tvStepNowDay.setOnClickListener(this);
		tvSleepNowDay.setOnClickListener(this);
		stepWeekDayView.setOnDateChangeListener(mOnDateChangeListenerForStep);
//		updateText(stepWeekDayView.getCurrentDate(),0);

			//		2〉睡眠
		
		sleepWeekDayView = (SleepDayView) viewWeekday.findViewById(R.id.sleep_weekday);
		imgIncreaseSleepWeekday = (ImageView) viewWeekday.findViewById(R.id.img_increase_sleep_weekday);
		imgReduceSleepWeekday = (ImageView) viewWeekday.findViewById(R.id.img_reduce_sleep_weekday);
//		tvSleepWeekdayInfo1 = (TextView) viewWeekday.findViewById(R.id.tv_sleep_weekday_1);
//		tvSleepWeekdayInfo2 = (TextView) viewWeekday.findViewById(R.id.tv_sleep_weekday_2);
		
		imgIncreaseSleepWeekday.setOnClickListener(this);
		imgReduceSleepWeekday.setOnClickListener(this);
		sleepWeekDayView.setOnDateChangeListener(mOnDateChangeListenerForSleep);
//		updateText(sleepWeekDayView.getCurrentDate(),1);

		// 月
		viewMonth = inflater.inflate(R.layout.view_count_month, container, false);
		tvStepNowMonth = (TextView) viewMonth.findViewById(R.id.tv_step_now_month);
		tvSleepNowMonth = (TextView) viewMonth.findViewById(R.id.tv_sleep_now_month);
		tvStepNowMonth.setOnClickListener(this);
		tvSleepNowMonth.setOnClickListener(this);
		//		1〉计步
		stepMonthView = (StepMonthView) viewMonth.findViewById(R.id.step_month);
		imgIncreaseStepMonth = (ImageView) viewMonth.findViewById(R.id.img_increase_step_month);
		imgReduceStepMonth = (ImageView) viewMonth.findViewById(R.id.img_reduce_step_month);
		imgIncreaseStepMonth.setOnClickListener(this);
		imgReduceStepMonth.setOnClickListener(this);
		//		2〉睡眠
		sleepMonthView = (SleepMonthView) viewMonth.findViewById(R.id.sleep_month);
		imgIncreaseSleepMonth = (ImageView) viewMonth.findViewById(R.id.img_increase_sleep_month);
		imgReduceSleepMonth = (ImageView) viewMonth.findViewById(R.id.img_reduce_sleep_month);
		imgIncreaseSleepMonth.setOnClickListener(this);
		imgReduceSleepMonth.setOnClickListener(this);
		
		frame.addView(viewWeekday);
		tvWeekday.setBackgroundColor(getResources().getColor(R.color.count_bg_selected));

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_weekday:
			changeTab(0);
			break;
		case R.id.tv_month:
			changeTab(1);
			break;
		case R.id.img_increase_step_weekday:
			stepWeekDayView.next();
			break;
		case R.id.img_reduce_step_weekday:
			stepWeekDayView.previous();
			break;
		case R.id.img_increase_sleep_weekday:
			sleepWeekDayView.next();
			break;
		case R.id.img_reduce_sleep_weekday:
			sleepWeekDayView.previous();
			break;
		case R.id.img_increase_step_month:
			stepMonthView.next();
			break;
		case R.id.img_reduce_step_month:
			stepMonthView.previous();
			break;
		case R.id.img_increase_sleep_month:
			sleepMonthView.next();
			break;
		case R.id.img_reduce_sleep_month:
			sleepMonthView.previous();
			break;
		case R.id.tv_step_count_now_day:
			stepWeekDayView.now();
			break;
		case R.id.tv_sleep_count_now_day:
			sleepWeekDayView.now();
			break;
		case R.id.tv_step_now_month:
			stepMonthView.now();
			break;
		case R.id.tv_sleep_now_month:
			sleepMonthView.now();
			break;

		default:
			break;
		}
	}

	private void changeTab(int i) {
		frame.removeAllViews();
		if (i == 0) {
			frame.addView(viewWeekday);
			tvWeekday.setBackgroundColor(getResources().getColor(R.color.count_bg_selected));
			tvMonth.setBackgroundColor(getResources().getColor(R.color.count_bg_unselected));
		} else if (i == 1) {
			frame.addView(viewMonth);
			tvWeekday.setBackgroundColor(getResources().getColor(R.color.count_bg_unselected));
			tvMonth.setBackgroundColor(getResources().getColor(R.color.count_bg_selected));
		}

	}
	/**
	 * 取得当前日期的月份
	 * @param d
	 * @return
	 */
	private int getMonth(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int month = c.get(Calendar.MONTH) + 1;
		return month;
	}

//	private void updateText(Date date,int flag){
//		int month = getMonth(date);
//		String str1;
//		String str2;
//		String str3;
//		
//		if(month==2){
//			str1 = "12";
//			str2 = String.valueOf(month-1);
//			str3 = String.valueOf(month);
//		}else if(month==1){
//			str1="11";
//			str2="12";
//			str3 = String.valueOf(month);
//		}else{
//			str1 = String.valueOf(month-2);
//			str2 = String.valueOf(month-1);
//			str3 = String.valueOf(month);
//		}
//		if(flag==0){
//			tvStepWeekdayInfo1.setText("←" + str1 + "月|" + str2 + "月→");
//			tvStepWeekdayInfo2.setText("←" + str2 + "月|" + str3 + "月→");
//		}else{
//			tvSleepWeekdayInfo1.setText("←" + str1 + "月|" + str2 + "月→");
//			tvSleepWeekdayInfo2.setText("←" + str2 + "月|" + str3 + "月→");
//		}
//	}

}
