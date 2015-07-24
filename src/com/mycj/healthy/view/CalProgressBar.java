package com.mycj.healthy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.RecoverySystem.ProgressListener;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

import com.mycj.healthy.R;
import com.mycj.healthy.view.StepCircle.OnProgressChange;

public class CalProgressBar extends ProgressBar implements OnProgressChange{
	private Bitmap background;
	private Bitmap thumb;
	private Paint paintThumb;                      
	
	public CalProgressBar(Context context) {
		super(context);
		init();
	}
	
	
	public CalProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}


	public CalProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		background = BitmapFactory.decodeResource(getResources(),R.drawable.setting_walk_goal_appraisal_background);
		thumb = BitmapFactory.decodeResource(getResources(),R.drawable.setting_walk_goal_appraisal_coordinate);
		paintThumb = new Paint();
		paintThumb.setAntiAlias(true);
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
		int x = getWidth();
		int dx = x * getProgress()/getMax();
		Log.e("_---", "dx : " +dx);
		canvas.drawBitmap(thumb, dx, 0, paintThumb);
	}


	@Override
	public void onChange(int progress) {
		
	}
}
