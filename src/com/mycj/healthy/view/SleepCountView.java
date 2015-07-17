package com.mycj.healthy.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mycj.healthy.R;

public class SleepCountView extends View {
	private Paint mXYPaint;
	private Paint mLinePaint;
	private Paint mPointPaint;
	/**
	 * 小点的半径
	 */
	private float radus =6f;

	/**
	 * X轴最小区间
	 */
	private float shortX;
	/**
	 * Y轴最小区间
	 */
	private float shortY;

	public SleepCountView(Context context) {
		super(context);
	}

	public SleepCountView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public SleepCountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		shortX = getWidth() / 10;
		shortY = (getHeight() - 50) / 2;

		mXYPaint = new Paint();
		mXYPaint.setAntiAlias(true);
		mXYPaint.setStrokeWidth(4);
		mXYPaint.setColor(getResources().getColor(R.color.blue));

		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);

		mPointPaint = new Paint();
		mPointPaint.setAntiAlias(true);
		mPointPaint.setStyle(Paint.Style.FILL);
		mPointPaint.setStrokeWidth(10);
		mPointPaint.setColor(getResources().getColor(R.color.blue));

		String clocks[] = new String[] { "22", "23", "0", "1", "2", "3", "4", "5", "6", "7" };
		String sleeps[] = new String[] { "清醒", "浅睡", "深睡" };

		for (int i = 0; i < 10; i++) {
			// X轴上的点
			canvas.drawCircle(shortX / 2 + shortX * i, getHeight() - 25, radus, mPointPaint);
			// 竖线
			if (i != 0) {
				canvas.drawLine(shortX / 2 + shortX * i, getHeight() - 25-radus, shortX / 2 + shortX * i, 25, mLinePaint);
			}
			// X轴坐标数字
			canvas.drawText(clocks[i], shortX / 2 + shortX * i, getHeight(), mLinePaint);
		}
		// Y轴上的点
		canvas.drawCircle(25 , shortY + 25,radus, mPointPaint);
		canvas.drawCircle(25 , 25, radus, mPointPaint);
		// Y轴上的坐标名称
		canvas.drawText(sleeps[0], 0, getHeight() - 25, mLinePaint);
		canvas.drawText(sleeps[1], 0, shortY + 25, mLinePaint);
		canvas.drawText(sleeps[2], 0, 25, mLinePaint);

		// 横线
		canvas.drawLine(25+radus , shortY + 25, getWidth(), shortY + 25, mLinePaint);
		canvas.drawLine(25+radus , 25, getWidth(), 25, mLinePaint);
		// Y轴
		canvas.drawLine(25 , 0, 25 , getHeight() - 25, mXYPaint);
		// X轴
		canvas.drawLine(25, getHeight() - 25, getWidth(), getHeight() - 25, mXYPaint);
		super.onDraw(canvas);
	}
}
