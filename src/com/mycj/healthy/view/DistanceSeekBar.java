package com.mycj.healthy.view;

import com.mycj.healthy.R;
import com.mycj.healthy.util.DensityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;
// TODO Auto-generated method stub
//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//int width= (int) DensityUtils.px2dp(getResources(), bitmap.getWidth());
//int height= (int) DensityUtils.px2dp(getResources(), bitmap.getHeight());
//setMeasuredDimension(width, height);

public class DistanceSeekBar extends SeekBar {
	private Bitmap backgroundBitmap;

	public DistanceSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DistanceSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public DistanceSeekBar(Context context) {
		super(context);
		init();
	}

	private void init() {
		backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.setting_camera_alarmer_rail_2);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d("", "bitmap :" + backgroundBitmap.getWidth() + "," + backgroundBitmap.getHeight());
		setMeasuredDimension(backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
	}
}
