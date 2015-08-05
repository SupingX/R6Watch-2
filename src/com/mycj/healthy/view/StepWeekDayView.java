package com.mycj.healthy.view;

import java.util.Random;

import com.mycj.healthy.R;

import android.content.Context;
import android.util.AttributeSet;

public class StepWeekDayView extends AbstractWeekdayCountView{

	public StepWeekDayView(Context context) {
		super(context);
		stepInit();
	}

	public StepWeekDayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		stepInit();
	}

	public StepWeekDayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		stepInit();

	}
	
	private void stepInit(){
		MAX = 70;
	}
	
	@Override
	public int getData(int day) {
		return new Random().nextInt(MAX);
	}

	@Override
	public void initColor() {
		setColor(getResources().getColor(R.color.count_step));
		
	}
	

}
