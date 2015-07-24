package com.mycj.healthy.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.mycj.healthy.R;

public class SleepCountView extends View {
	private Paint mXYPaint;
	private Paint mLinePaint;
	private Paint mPointPaint;
	private float other = 25;
	/**
	 * 小点的半径
	 */
	private float radius =6f;

	/**
	 * X轴最小区间
	 */
	private float shortX;
	/**
	 * Y轴最小区间
	 */
	private float shortY;
	private String clocks[];
	private String sleeps[];
	private Paint paintTextY;

	public SleepCountView(Context context) {
		super(context);
		init();
	}

	public SleepCountView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SleepCountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	private void init(){
		Resources r = getResources();
		mXYPaint = new Paint();
		mXYPaint.setAntiAlias(true);
		mXYPaint.setStrokeWidth(4);
		mXYPaint.setColor(r.getColor(R.color.blue));

		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setAlpha(200);

		mPointPaint = new Paint();
		mPointPaint.setAntiAlias(true);
		mPointPaint.setStyle(Paint.Style.FILL);
		mPointPaint.setStrokeWidth(10);
		mPointPaint.setColor(r.getColor(R.color.blue));

		paintTextY = new Paint();
		paintTextY.setAntiAlias(true);
		
		clocks = new String[] { "22", "23", "0", "1", "2", "3", "4", "5", "6", "7" };
		sleeps = new String[] { "清醒", "浅睡", "深睡" };
	}
	
	@Override
	protected void onDraw(Canvas canvas) {

		shortX = getWidth() / 10;
		shortY = (getHeight() - 50) / 2;
		
	
		
		

		for (int i = 0; i < 10; i++) {
			// X轴上的点
//			canvas.drawCircle(shortX / 2 + shortX * i, getHeight() - other, radius, mPointPaint);
			canvas.drawCircle(shortX * 2 / 3 + shortX * i, getHeight() - other, radius, mPointPaint);
			// 竖线
			if (i != 0) {
				canvas.drawLine(shortX * 2 / 3 + shortX * i, getHeight() - 25-radius, shortX * 2 / 3 + shortX * i, 25, mLinePaint);
			}
			// X轴坐标数字
			canvas.drawText(clocks[i], shortX * 2 / 3 + shortX * i, getHeight(), mLinePaint);
		}
		// Y轴上的点
		canvas.drawCircle(shortX * 2 / 3 , shortY + other,radius, mPointPaint);
		canvas.drawCircle(shortX * 2 / 3 , other, radius, mPointPaint);
		// Y轴上的坐标名称
		for (int i = 0; i < sleeps.length; i++) {
			Rect rectTextY = new Rect();
			paintTextY.getTextBounds(sleeps[i], 0, sleeps[i].length(), rectTextY);
			
			canvas.drawText(sleeps[0], 0, getHeight() - other+rectTextY.height()/2-i*shortY, mLinePaint);
//			canvas.drawText(sleeps[1], 0, shortY + 25, mLinePaint);
//			canvas.drawText(sleeps[2], 0, 25, mLinePaint);
		}
		// 横线
		canvas.drawLine(shortX * 2 / 3+radius , shortY + other, getWidth(), shortY + other, mLinePaint);
		canvas.drawLine(shortX * 2 / 3+radius , other, getWidth(), other, mLinePaint);
		// Y轴
		canvas.drawLine(shortX * 2 / 3 , 0, shortX * 2 / 3 , getHeight() - other, mXYPaint);
		// X轴
		canvas.drawLine(shortX * 2 / 3, getHeight() - other, getWidth(), getHeight() - other, mXYPaint);
		super.onDraw(canvas);
	}
}
