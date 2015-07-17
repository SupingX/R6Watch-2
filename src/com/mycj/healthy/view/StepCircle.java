package com.mycj.healthy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.mycj.healthy.R;

public class StepCircle extends View {
	private float width = 12;
	private Paint paintCicle;
	private Paint paintArc;
	private int maxProgress=100; // 最大进度(总步数)
	private int progress;// 当前进度（当前步数）
	private RectF arcRect;// 圆环的区域
	private float startAngle = -90;
	private float sweepAngle;
	private float centerX;
	private float centerY;
	private float radius;

	public StepCircle(Context context) {
		super(context);
		init();
	}

	public StepCircle(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public StepCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	

	/**
	 * 根据进度得到角度
	 * @param progress
	 * @return
	 */
	private float toAngle(int progress) {
		float angle = 0;
		angle = 360 * progress / maxProgress;
		return angle;
	}
	
	private void init() {
		paintCicle = new Paint();
		paintCicle.setAntiAlias(true);
		paintCicle.setStrokeWidth(width);
		paintCicle.setStyle(Paint.Style.STROKE);
		paintCicle.setColor(getResources().getColor(R.color.grey_light));

		paintArc = new Paint();
		paintArc.setAntiAlias(true);
		paintArc.setStrokeWidth(width);
		paintArc.setStyle(Paint.Style.STROKE);
		paintArc.setColor(getResources().getColor(R.color.blue));

	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawCircle(canvas);
		drawArc(canvas);
		super.onDraw(canvas);
	}

	private void initArcRect() {
		float left = centerX-radius;
		float top = width/2;
		float right = centerX+radius;
		float bottom = centerY*2-width/2;
		arcRect = new RectF(left, top, right, bottom);
	}

	private void drawCircle(Canvas canvas) {
		centerX = getWidth() / 2;
		centerY = getHeight() / 2;
		radius = Math.min(centerX, centerY) - width / 2;
		canvas.drawCircle(centerX, centerY, radius, paintCicle);
	}

	public void drawArc(Canvas canvas) {
		initArcRect();
		canvas.drawArc(arcRect, startAngle, toAngle(progress), false, paintArc);
	}
	
	public void setProgress(int step){
		if(step<0){
			step=0;
		}
		if(step>maxProgress){
			step=maxProgress;
		}
		
		this.progress = step;
		if(mOnProgressChange!=null){
			mOnProgressChange.onChange(progress);
		}
		invalidate();
	}
	public void setMaxProgress(int maxStep){
		this.maxProgress = maxStep;
		invalidate();
	}
	
	public interface OnProgressChange {
		public void onChange(int progress);
	}
	
	private OnProgressChange mOnProgressChange;
	public void setOnProgressChange(OnProgressChange l){
		this.mOnProgressChange = l;
	}
	
}
