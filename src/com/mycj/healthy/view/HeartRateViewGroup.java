package com.mycj.healthy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class HeartRateViewGroup extends FrameLayout {
	private HeartRateBackgroudView backView;
	private HeartRatePathView pathView;

	public HeartRateViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HeartRateViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public HeartRateViewGroup(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		backView = new HeartRateBackgroudView(context);
		pathView = new HeartRatePathView(context);
		addView(backView);
		addView(pathView);
	}

}
