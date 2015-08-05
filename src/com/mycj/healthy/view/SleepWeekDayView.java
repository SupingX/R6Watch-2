package com.mycj.healthy.view;

import java.util.Random;

import com.mycj.healthy.R;

import android.content.Context;
import android.util.AttributeSet;

public class SleepWeekDayView extends AbstractWeekdayCountView {

	public SleepWeekDayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		sleepInit();
	}

	public SleepWeekDayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sleepInit();
	}

	public SleepWeekDayView(Context context) {
		super(context);
		sleepInit();
	}
	private void sleepInit(){
		MAX = 168;
	}
	
	@Override
	public int getData(int day) {
		return new Random().nextInt(MAX);
	}

	@Override
	public void initColor() {
		setColor(getResources().getColor(R.color.count_bg_selected));
	}

}
