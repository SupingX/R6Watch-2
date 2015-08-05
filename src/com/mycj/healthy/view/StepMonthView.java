package com.mycj.healthy.view;

import java.util.Random;

import com.mycj.healthy.R;

import android.content.Context;
import android.util.AttributeSet;

public class StepMonthView extends AbstractMonthCountView {

	public StepMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		MAX = 310;
	}

	public StepMonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
		MAX = 310;
	}

	public StepMonthView(Context context) {
		super(context);
		MAX = 310;
	}

	@Override
	public void initColor() {
		setColor(getResources().getColor(R.color.count_step));
	}

	@Override
	public int getData(int month, int diff) {
		return new Random().nextInt(MAX);
	}

}
