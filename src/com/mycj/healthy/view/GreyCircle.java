package com.mycj.healthy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.mycj.healthy.R;

public class GreyCircle extends View {

	private Paint backCirclePaint;
	private Paint mPaint;
	private float widthBack=8;
	private float progress ;
	private float max =12*60;//单位：分钟
	/** 彩环渐变颜色 **/
	private final int[] COLORS = new int[] { 0xFF22AC38, 0xFF009944, 0xFF009B6B, 0xFF009E96, 0xFF00A0C1, 0xFF00A0E9,
			0xFF0086D1, 0xFF0068B7, 0xFF00479D, 0xFF1D2088, 0xFF601986, 0xFF920783, 0xFFBE0081, 0xFFE4007F, 0xFFE5006A,
			0xFFE5004F, 0xFFE60033, 0xFFE60012, 0xFFEB6100, 0xFFF39800, 0xFFFCC800, 0xFFFFF100, 0xFFCFDB00, 0xFF8FC31F,
			0xFF22AC38 };


	public GreyCircle(Context context) {
		super(context);
		init(context);
	}



	public GreyCircle(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public GreyCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		backCirclePaint = new Paint();
		backCirclePaint.setAntiAlias(true);
		backCirclePaint.setStrokeWidth(widthBack);
		backCirclePaint.setStyle(Paint.Style.STROKE);
		backCirclePaint.setStrokeCap(Paint.Cap.ROUND);
		backCirclePaint.setColor(getResources().getColor(R.color.grey_light));
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true); //消除锯齿
		mPaint.setStyle(Paint.Style.STROKE); //绘制空心圆
//		mPaint.setStrokeWidth(mRingWidth); //设置进度条宽度
//		mPaint.setColor(Color.RED); //设置进度条颜色
		mPaint.setStrokeJoin(Paint.Join.ROUND);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// setMeasuredDimension(getWidth()-25, getHeight()-25);
	}
	
	

	@Override
	protected void onDraw(Canvas canvas) {
		float mCentX = getWidth()/2;
		float mCentY = getHeight()/2;
		float radius = getHeight()/2-widthBack/2;
		canvas.drawCircle(mCentX, mCentY, radius, backCirclePaint);
		
		float left = 3*getHeight()/16+widthBack/2;
		float top = 3*getHeight()/16+widthBack/2;
		float right = 13*getHeight()/16-widthBack/2;
		float bottom = 13*getHeight()/16-widthBack/2;
		
		SweepGradient mShader = new SweepGradient(mCentX, mCentY, COLORS, null);
		mPaint.setShader(mShader);
		RectF rectF = new RectF(left, top, right, bottom);
		float width = 3*getHeight()/8-widthBack/2;
		mPaint.setStrokeWidth(width);
		canvas.drawArc(rectF, -90, progressToAngle(progress), false, mPaint);
		super.onDraw(canvas);
	}



	private float progressToAngle(float progress) {
		return 	360f*progress/max;
	}
	
	public void setProgress(float progress){
		this.progress = progress;
	}
	public void setMax(float max){
		this.max = max;
	}

}
