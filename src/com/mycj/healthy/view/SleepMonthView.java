package com.mycj.healthy.view;

import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;

import com.mycj.healthy.R;

public class SleepMonthView extends AbstractMonthCountView{
	public SleepMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		MAX = 744;
	}

	public SleepMonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
		MAX = 744;
	}

	public SleepMonthView(Context context) {
		super(context);
		MAX = 744;
	}

	@Override
	public void initColor() {
		setColor(getResources().getColor(R.color.count_bg_selected));
	}

	@Override
	public int getData(int month, int diff) {
		return new Random().nextInt(MAX);
	}
}
