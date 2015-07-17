package com.mycj.healthy.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollPagerView extends ViewPager{

	public NoScrollPagerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public NoScrollPagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}
}
