package com.mycj.healthy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.mycj.healthy.R;

public class CalProgressBar extends ProgressBar{
	private Bitmap background;
	private Bitmap thumb;                      
	
	public CalProgressBar(Context context) {
		super(context);
		background = BitmapFactory.decodeResource(getResources(),R.drawable.setting_walk_goal_appraisal_background);
		thumb = BitmapFactory.decodeResource(getResources(),R.drawable.setting_walk_goal_appraisal_coordinate);
	}
	
	
	public CalProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		background = BitmapFactory.decodeResource(getResources(),R.drawable.setting_walk_goal_appraisal_background);
		thumb = BitmapFactory.decodeResource(getResources(),R.drawable.setting_walk_goal_appraisal_coordinate);
	}


	public CalProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		background = BitmapFactory.decodeResource(getResources(),R.drawable.setting_walk_goal_appraisal_background);
		thumb = BitmapFactory.decodeResource(getResources(),R.drawable.setting_walk_goal_appraisal_coordinate);
	}


	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(background.getWidth(), background.getHeight());
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint p = new Paint();
		p.setAntiAlias(true);
		int x = getWidth();
		int dx = x * getProgress()/getMax();
		canvas.drawBitmap(thumb, dx-thumb.getWidth()/2, 0, p);
	}
}
