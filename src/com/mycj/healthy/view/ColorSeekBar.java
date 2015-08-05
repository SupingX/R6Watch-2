package com.mycj.healthy.view;

import com.mycj.healthy.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class ColorSeekBar extends SeekBar {
	private Bitmap bitmap;

	public ColorSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ColorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ColorSeekBar(Context context) {
		super(context);
		init();
	}

	private void init() {
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.setting_walk_goal_appraisal_background);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(bitmap.getWidth(), bitmap.getHeight());
	}
}
